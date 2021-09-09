package hs.industry.ailab.entity.modle.controlmodle;

import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.config.AlgprithmApiConfig;
import hs.industry.ailab.constant.ModelRunStatusEnum;
import hs.industry.ailab.constant.ModelTypeEnum;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.dto.PinDataDto;
import hs.industry.ailab.entity.dto.request.customize.PythonAdapter;
import hs.industry.ailab.entity.dto.request.pid.PidBaseModleParam;
import hs.industry.ailab.entity.dto.request.pid.PidInputproperty;
import hs.industry.ailab.entity.dto.request.pid.PidModleAdapter;
import hs.industry.ailab.entity.dto.request.pid.PidOutPutproperty;
import hs.industry.ailab.entity.dto.response.customize.CPythonDataDto;
import hs.industry.ailab.entity.dto.response.customize.CPythonResponse4PlantDto;
import hs.industry.ailab.entity.dto.response.fpid.PidData4PlantDto;
import hs.industry.ailab.entity.dto.response.fpid.PidResponse4PlantDto;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.entity.modle.customizemodle.CUSTOMIZEModle;
import hs.industry.ailab.entity.modle.filtermodle.FilterModle;
import hs.industry.ailab.entity.modle.iomodle.INModle;
import hs.industry.ailab.entity.modle.iomodle.OUTModle;
import hs.industry.ailab.entity.modle.modlerproerty.MPCModleProperty;
import hs.industry.ailab.service.HttpClientService;
import hs.industry.ailab.utils.help.Tool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/9 11:17
 */
@Data
@Slf4j
public class PIDModle extends BaseModleImp {

    /**
     * memery
     */
    private String datasource;
    private String pyproxyexecute;
    private String port;
    private String pidscript;

    @Override
    public void destory() {
        HttpClientService httpClientService = getHttpClientService();
        AlgprithmApiConfig algprithmApiConfig = httpClientService.getAlgprithmApiConfig();
        httpClientService.PostParam(algprithmApiConfig.getUrl() + algprithmApiConfig.getStop() + getModleId(), null);
    }


    //模型短路
    private void modleshortcircuit() {
        BaseModlePropertyImp mvinputpin = Tool.selectmodleProperyByPinname(ModleProperty.TYPE_PIN_MV, propertyImpList, ModleProperty.PINDIRINPUT);
        BaseModlePropertyImp mvoutputpin = Tool.selectmodleProperyByPinname(ModleProperty.TYPE_PIN_MV, propertyImpList, ModleProperty.PINDIROUTPUT);
        if ((mvinputpin != null) && (mvoutputpin != null)) {
            JSONObject fakecomputedata = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("value", mvinputpin.getValue());
            data.put("partkp", 0f);
            data.put("partki", 0f);
            data.put("partkd", 0f);
            JSONObject pindata = new JSONObject();
            pindata.put(mvoutputpin.getModlePinName(), data);
            fakecomputedata.put("data", pindata);
            computresulteprocess(null, fakecomputedata);
            outprocess(null, null);
            setAutovalue(0);
        }

    }

    @Override
    public void docomputeprocess() {
        setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_RUNNING);

        HttpClientService httpClientService = getHttpClientService();
        AlgprithmApiConfig algprithmApiConfig = httpClientService.getAlgprithmApiConfig();


        PidModleAdapter pidModleAdapter = new PidModleAdapter();

//        private PidBaseModleParam basemodelparam;
//        private PidInputproperty inputparam;
//        private List<PidOutPutproperty> outputparam;

        PidBaseModleParam pidBaseModleParam = new PidBaseModleParam();
        pidBaseModleParam.setModelid(getModleId());
        pidBaseModleParam.setModeltype(ModelTypeEnum.MODEL_TYPE_PID.getCode());
        pidBaseModleParam.setModelname(getModleName());
        pidModleAdapter.setBasemodelparam(pidBaseModleParam);


        PidInputproperty pidInputproperty = new PidInputproperty();
        List<PidOutPutproperty> pidOutPutproperties = new ArrayList<>();
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;

            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {

                if (baseModlePropertyImp.getModlePinName().equals(ModleProperty.TYPE_PIN_MODLE_AUTO)) {
                    pidInputproperty.setAuto(baseModlePropertyImp.getValue());
                } else if (baseModlePropertyImp.getModlePinName().equals(ModleProperty.TYPE_PIN_KP)) {
                    pidInputproperty.setKp(baseModlePropertyImp.getValue());
                } else if (baseModlePropertyImp.getModlePinName().equals(ModleProperty.TYPE_PIN_KI)) {
                    pidInputproperty.setKi(baseModlePropertyImp.getValue());
                } else if (baseModlePropertyImp.getModlePinName().equals(ModleProperty.TYPE_PIN_KD)) {
                    pidInputproperty.setKd(baseModlePropertyImp.getValue());
                } else if (baseModlePropertyImp.getModlePinName().equals(ModleProperty.TYPE_PIN_PV)) {
                    pidInputproperty.setPv(baseModlePropertyImp.getValue());
                } else if (baseModlePropertyImp.getModlePinName().equals(ModleProperty.TYPE_PIN_SP)) {
                    pidInputproperty.setSp(baseModlePropertyImp.getValue());
                } else if (baseModlePropertyImp.getModlePinName().equals(ModleProperty.TYPE_PIN_MV)) {
                    pidInputproperty.setMv(baseModlePropertyImp.getValue());
                    pidInputproperty.setDmvHigh(((MPCModleProperty) baseModlePropertyImp).getDmvHigh());
                    pidInputproperty.setDmvLow(((MPCModleProperty) baseModlePropertyImp).getDmvLow());
                } else if (baseModlePropertyImp.getModlePinName().equals(ModleProperty.TYPE_PIN_FF)) {
                    pidInputproperty.setFf(baseModlePropertyImp.getValue());
                } else if (baseModlePropertyImp.getModlePinName().equals(ModleProperty.TYPE_PIN_KF)) {
                    pidInputproperty.setKf(baseModlePropertyImp.getValue());
                } else if (baseModlePropertyImp.getModlePinName().equals("deadZone")) {
                    pidInputproperty.setDeadZone(baseModlePropertyImp.getValue());
                } else if (baseModlePropertyImp.getModlePinName().equals("mvup")) {
                    pidInputproperty.setMvuppinvalue(baseModlePropertyImp.getValue());
                } else if (baseModlePropertyImp.getModlePinName().equals("mvdown")) {
                    pidInputproperty.setMvdownpinvalue(baseModlePropertyImp.getValue());
                }

            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                PidOutPutproperty pidOutPutproperty = new PidOutPutproperty();
                pidOutPutproperty.setOutputpinname(baseModlePropertyImp.getModlePinName());
                pidOutPutproperties.add(pidOutPutproperty);
            }
        }

        pidModleAdapter.setInputparam(pidInputproperty);
        pidModleAdapter.setOutputparam(pidOutPutproperties);

        String pidResult = httpClientService.postForEntity(algprithmApiConfig.getUrl() + algprithmApiConfig.getPid(), pidModleAdapter, String.class);

        if (pidResult == null) {
            setErrormsg("http request error");
            setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
        }
        if (!StringUtils.isEmpty(pidResult)) {
            PidResponse4PlantDto pidResponse4PlantDto = JSONObject.parseObject(pidResult, PidResponse4PlantDto.class);
            if (pidResponse4PlantDto.getStatus() ==200) {
                PidData4PlantDto cPythonDataDto = pidResponse4PlantDto.getData();
                if (!ObjectUtils.isEmpty(cPythonDataDto)) {
                    List<PinDataDto> pinDataDtos = cPythonDataDto.getMvData();
                    if (!CollectionUtils.isEmpty(pinDataDtos)) {
                        Map<String, List<PinDataDto>> pinDataMaps = pinDataDtos.stream().collect(Collectors.groupingBy(PinDataDto::getPinname));

                        for (ModleProperty modleProperty : propertyImpList) {
                            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                if(pinDataMaps.containsKey(baseModlePropertyImp.getModlePinName())){
                                    baseModlePropertyImp.setValue(pinDataMaps.get(baseModlePropertyImp.getModlePinName()).get(0).getValue());
                                }else{
                                    setErrormsg("http request error");
                                    setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
                                    return;
                                }
                            }

                        }
                    }

                    setErrormsg("partkp:" + Tool.getSpecalScale(4, cPythonDataDto.getPartkp()) + "\n"
                            + "partki:" + Tool.getSpecalScale(4, cPythonDataDto.getPartki()) + "\n"
                            + "partkd:" + Tool.getSpecalScale(4, cPythonDataDto.getPartkd())
                    );

                    setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE);
                }else{
                    setErrormsg("http request error");
                    setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
                }

//                setErrormsg(pidResponse4PlantDto.getMessage());
            } else {
                setErrormsg(pidResponse4PlantDto.getMessage());
                setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
            }


        }else{
            setErrormsg("http request error");
            setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
        }
    }



    @Override
    public JSONObject inprocess(Project project) {
        setBeginruntime(Instant.now());

        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp pidinproperty = (BaseModlePropertyImp) modleProperty;
            if (pidinproperty.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                if (pidinproperty.getResource().getString("resource").equals(ModleProperty.SOURCE_TYPE_CONSTANT)) {
                    pidinproperty.setValue(pidinproperty.getResource().getDouble("value"));
                } else if (pidinproperty.getResource().getString("resource").equals(ModleProperty.SOURCE_TYPE_MODLE)) {
                    int modleId = pidinproperty.getResource().getInteger("modleId");
                    int modlepinsId = pidinproperty.getResource().getInteger("modlepinsId");

                    Modle modle = project.getIndexmodles().get(modleId);
                    if (modle != null) {
                        if (modle instanceof MPCModle) {
                            MPCModle mpcModle = (MPCModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = mpcModle.getIndexproperties().get(modlepinsId);
                            pidinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof PIDModle) {
                            PIDModle pidModle = (PIDModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = pidModle.getIndexproperties().get(modlepinsId);
                            pidinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof CUSTOMIZEModle) {
                            CUSTOMIZEModle customizeModle = (CUSTOMIZEModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = customizeModle.getIndexproperties().get(modlepinsId);
                            pidinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof FilterModle) {
                            FilterModle filterModle = (FilterModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = filterModle.getIndexproperties().get(modlepinsId);
                            pidinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof INModle) {
                            INModle inModle = (INModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = inModle.getIndexproperties().get(modlepinsId);
                            pidinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof OUTModle) {
                            OUTModle outModle = (OUTModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = outModle.getIndexproperties().get(modlepinsId);
                            pidinproperty.setValue(baseModlePropertyImp.getValue());
                        }

                    }

                }

            }
        }
        return null;
    }

    //解析计算输出结果，并且赋值给输出引脚
    @Override
    public JSONObject computresulteprocess(Project project, JSONObject computedata) {
        //do nothing
        return null;
    }

    @Override
    public void outprocess(Project project, JSONObject outdata) {
        setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE);
        setActivetime(Instant.now());
    }


    @Override
    public void init() {
        setIndexproperties(new HashMap<>());
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
            getIndexproperties().put(baseModlePropertyImp.getModlepinsId(), baseModlePropertyImp);
        }
    }


    /******db****/

    private List<ModleProperty> propertyImpList;
}
