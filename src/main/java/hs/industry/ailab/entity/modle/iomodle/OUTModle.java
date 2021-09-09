package hs.industry.ailab.entity.modle.iomodle;

import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.constant.ModelRunStatusEnum;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.entity.modle.controlmodle.MPCModle;
import hs.industry.ailab.entity.modle.controlmodle.PIDModle;
import hs.industry.ailab.entity.modle.customizemodle.CUSTOMIZEModle;
import hs.industry.ailab.entity.modle.filtermodle.FilterModle;
import hs.industry.ailab.service.HttpClientService;
import hs.industry.ailab.utils.httpclient.HttpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/9 11:20
 */
@Slf4j
@Data
public class OUTModle extends BaseModleImp {

    /**
     * memory
     */
    private String datasource;


    public void toBeRealModle(String datasource) {
        this.datasource = datasource;
    }


    @Override
    public void destory() {

    }

    @Override
    public void docomputeprocess() {
        outprocess(null, null);
        setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE);
    }


    /***
     * 从上一个模块引脚输出的数据赋值给模块的输入引脚
     * */
    @Override
    public JSONObject inprocess(Project project) {
        setBeginruntime(Instant.now());

        setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_RUNNING);
        for (ModleProperty property : propertyImpList) {
            BaseModlePropertyImp outmodlepin = (BaseModlePropertyImp) property;
            if (outmodlepin.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                //get model id
                int modleId = outmodlepin.getResource().getInteger("modleId");
                //get modelpin id
                int modlepinsId = outmodlepin.getResource().getInteger("modlepinsId");

                Modle modle = project.getIndexmodles().get(modleId);
                if (modle != null) {
                    BaseModleImp mpcModle = (BaseModleImp) modle;
                    BaseModlePropertyImp baseModlePropertyImp = mpcModle.getIndexproperties().get(modlepinsId);
                    outmodlepin.setValue(baseModlePropertyImp.getValue());
                }
            }

        }

        return null;
    }

    @Override
    public JSONObject computresulteprocess(Project project, JSONObject computedata) {
        return null;
    }


    /**
     * 将本模块的输入引脚输出给本模块的输出引脚，并且将输出数据提交给ocean
     */
    @Override
    public void outprocess(Project project, JSONObject outdata) {

        try {
            JSONObject writecontext = new JSONObject();
            for (ModleProperty property : propertyImpList) {
                BaseModlePropertyImp outmodlepin = (BaseModlePropertyImp) property;
                if (outmodlepin.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                    int modlepinsId = outmodlepin.getResource().getInteger("modlepinsId");
                    String outmappingtag = outmodlepin.getResource().getString("outmappingtag");
                    writecontext.put(outmappingtag, getIndexproperties().get(modlepinsId).getValue());
                    outmodlepin.setValue(getIndexproperties().get(modlepinsId).getValue());
                }
            }

            Map<String, String> postdata = new HashMap<>();
            postdata.put("tagvalue", writecontext.toJSONString());
            HttpClientService httpClientService = getHttpClientService();
            String inputdata = httpClientService.PostParam(httpClientService.getDcsApiConfig().getOceandir() + httpClientService.getDcsApiConfig().getDcswrite(), postdata);//HttpUtils.PostData(datasource + "/opc/write", postdata);
            log.info("modleid=" + getModleId() + " write info" + inputdata);
            setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE);
            setActivetime(Instant.now());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public void init() {
        setIndexproperties(new HashMap<>());
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
            getIndexproperties().put(baseModlePropertyImp.getModlepinsId(), baseModlePropertyImp);
        }
    }

    /**
     * db
     */
    private List<ModleProperty> propertyImpList;
}
