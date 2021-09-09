package hs.industry.ailab.entity.modle.customizemodle;

import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.config.AlgprithmApiConfig;
import hs.industry.ailab.constant.ModelRunStatusEnum;
import hs.industry.ailab.constant.ModelTypeEnum;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.dto.PinDataDto;
import hs.industry.ailab.entity.dto.request.customize.PythonAdapter;
import hs.industry.ailab.entity.dto.request.customize.PythonBaseParam;
import hs.industry.ailab.entity.dto.request.customize.PythonOutParam;
import hs.industry.ailab.entity.dto.response.customize.CPythonDataDto;
import hs.industry.ailab.entity.dto.response.customize.CPythonResponse4PlantDto;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.entity.modle.controlmodle.MPCModle;
import hs.industry.ailab.entity.modle.controlmodle.PIDModle;
import hs.industry.ailab.entity.modle.filtermodle.FilterModle;
import hs.industry.ailab.entity.modle.iomodle.INModle;
import hs.industry.ailab.entity.modle.iomodle.OUTModle;
import hs.industry.ailab.service.HttpClientService;
import hs.industry.ailab.utils.help.FileHelp;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/9 11:28
 */
@Slf4j
@Data
public class CUSTOMIZEModle extends BaseModleImp {
    public static Pattern scriptpattern = Pattern.compile("^(.*).py$");
    /**
     * memery
     */
    private String datasource;
    private String pyproxyexecute;
    private String port;



    @Override
    public void destory() {
        HttpClientService httpClientService = getHttpClientService();
        AlgprithmApiConfig algprithmApiConfig = httpClientService.getAlgprithmApiConfig();
        httpClientService.PostParam(algprithmApiConfig.getUrl() + algprithmApiConfig.getStop() + getModleId(), null);
    }

    @Override
    public void docomputeprocess() {
        setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_RUNNING);
        HttpClientService httpClientService = getHttpClientService();
        AlgprithmApiConfig algprithmApiConfig = httpClientService.getAlgprithmApiConfig();


        PythonAdapter pythonAdapter = new PythonAdapter();
//        private PythonBaseParam basemodelparam;
//        private String pythoncontext;
//        private Map<String,String> inputparam;//输出参数数据，key=参数名称,value=值
//        private List<PythonOutParam> outputparam;

        PythonBaseParam pythonBaseParam = new PythonBaseParam();
        pythonBaseParam.setModelid(getModleId());
        pythonBaseParam.setModelname(getModleName());
        pythonBaseParam.setModeltype(ModelTypeEnum.MODEL_TYPE_CUSTOMIZE.getCode());
        pythonAdapter.setBasemodelparam(pythonBaseParam);


        pythonAdapter.setPythoncontext(FileHelp.readInfoFromFile(getCustomizepyname()));

        Map<String, String> inputParams = new HashMap<>();
        List<PythonOutParam> pythonOutParams = new ArrayList<>();
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                inputParams.put(baseModlePropertyImp.getModlePinName(), baseModlePropertyImp.getValue() + "");
            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                PythonOutParam pythonOutParam = new PythonOutParam();
                pythonOutParam.setOutputpinname(baseModlePropertyImp.getModlePinName());
                pythonOutParams.add(pythonOutParam);
            }
        }
        pythonAdapter.setInputparam(inputParams);
        pythonAdapter.setOutputparam(pythonOutParams);

        String customizeResult = httpClientService.postForEntity(algprithmApiConfig.getUrl() + algprithmApiConfig.getPython(), pythonAdapter, String.class);


        if (customizeResult == null) {
            setErrormsg("http request error");
            setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
        }
        if (!StringUtils.isEmpty(customizeResult)) {
            CPythonResponse4PlantDto cPythonResponse4PlantDto = JSONObject.parseObject(customizeResult, CPythonResponse4PlantDto.class);
            if (cPythonResponse4PlantDto.getStatus() ==200) {
                CPythonDataDto cPythonDataDto = cPythonResponse4PlantDto.getData();
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
                                    setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
                                    return;
                                }

                            }

                        }
                        setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE);
                        setErrormsg(cPythonResponse4PlantDto.getMessage());
                    }
                }else{
                    setErrormsg("http request error");
                    setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
                    log.error("python request error. reason={}",customizeResult);
                }

            } else {
                setErrormsg(cPythonResponse4PlantDto.getMessage());
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
            BaseModlePropertyImp customizeinproperty = (BaseModlePropertyImp) modleProperty;
            if (customizeinproperty.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                if (customizeinproperty.getResource().getString("resource").equals(ModleProperty.SOURCE_TYPE_CONSTANT)) {
                    customizeinproperty.setValue(customizeinproperty.getResource().getDouble("value"));
                } else if (customizeinproperty.getResource().getString("resource").equals(ModleProperty.SOURCE_TYPE_MODLE)) {
                    int modleId = customizeinproperty.getResource().getInteger("modleId");
                    int modlepinsId = customizeinproperty.getResource().getInteger("modlepinsId");
                    Modle modle = project.getIndexmodles().get(modleId);
                    if (modle != null) {
                        if (modle instanceof MPCModle) {
                            MPCModle mpcModle = (MPCModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = mpcModle.getIndexproperties().get(modlepinsId);
                            customizeinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof PIDModle) {
                            PIDModle pidModle = (PIDModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = pidModle.getIndexproperties().get(modlepinsId);
                            customizeinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof CUSTOMIZEModle) {
                            CUSTOMIZEModle customizeModle = (CUSTOMIZEModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = customizeModle.getIndexproperties().get(modlepinsId);
                            customizeinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof FilterModle) {
                            FilterModle filterModle = (FilterModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = filterModle.getIndexproperties().get(modlepinsId);
                            customizeinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof INModle) {
                            INModle inModle = (INModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = inModle.getIndexproperties().get(modlepinsId);
                            customizeinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof OUTModle) {
                            OUTModle outModle = (OUTModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = outModle.getIndexproperties().get(modlepinsId);
                            customizeinproperty.setValue(baseModlePropertyImp.getValue());
                        }

                    }

                }

            }
        }
        return null;
    }

    /**
     * 解析计算后的数据，并且将数据设置到输出引脚上
     *
     * @param computedata 里面的key是引脚的名称，值是value比如{
     *                    'pintag':{'value':1.2}
     *                    }
     */
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
         setIndexproperties(new HashMap<>());;
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
            getIndexproperties().put(baseModlePropertyImp.getModlepinsId(), baseModlePropertyImp);
        }
    }


    /****db****/
    private String customizepyname;
    /*********/
    private List<ModleProperty> propertyImpList;


    public String noscripNametail() {
        Matcher matcher = scriptpattern.matcher(customizepyname);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
