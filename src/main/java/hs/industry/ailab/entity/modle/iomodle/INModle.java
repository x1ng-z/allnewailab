package hs.industry.ailab.entity.modle.iomodle;

import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.config.DcsApiConfig;
import hs.industry.ailab.constant.ModelRunStatusEnum;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.service.HttpClientService;
import hs.industry.ailab.utils.httpclient.HttpUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/9 11:19
 */
@Slf4j
@Data
public class INModle extends BaseModleImp {
    private String datasource;



    public void toBeRealModle(String datasource) {
        this.datasource = datasource;
    }


    @Override
    public void destory() {
        //todo nothing
    }


    @Override
    public void docomputeprocess() {

    }


    @Override
    public JSONObject inprocess(Project project) {
        setBeginruntime(Instant.now());

        setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_RUNNING);

        StringBuilder tags = new StringBuilder();
        Map<String, String> postdata = new HashMap<>();
        for (ModleProperty modleProperty : propertyImpList) {
            tags.append(((BaseModlePropertyImp) modleProperty).getResource().getString("inmappingtag") + ",");
        }
        if (propertyImpList.size() > 0) {
            postdata.put("tags", tags.toString().substring(0, tags.length() - 1));
            HttpClientService httpClientService =getHttpClientService();
            DcsApiConfig dcsApiConfig =httpClientService.getDcsApiConfig();
            String inputdata = httpClientService.PostParam(dcsApiConfig.getOceandir()+dcsApiConfig.getDcsread(),postdata);//HttpUtils.PostParam(datasource + "/realdata/read", postdata);

            if(StringUtils.isEmpty(inputdata)){
                setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
                return null;
            }


            try {
                JSONObject jsoninputdata = JSONObject.parseObject(inputdata);
                //更新到输出引脚
                JSONObject values =jsoninputdata.getJSONObject("data");
                propertyImpList.forEach(p->{
                    BaseModlePropertyImp baseModlePropertyImp=(BaseModlePropertyImp) p;
                    String tag=baseModlePropertyImp.getModlePinName();
                    if (values.containsKey(tag)){
                        baseModlePropertyImp.setValue(values.getFloat(tag));
                    }else{
                        setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
                        return;
                    }

                });
                setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE);
                return jsoninputdata.getJSONObject("data");
            } catch (Exception e) {
                log.error(e.getMessage(),e);
                setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
            }
            return null;
        }
        setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE);
        return null;

    }

    @Override
    public JSONObject computresulteprocess(Project project, JSONObject computedata) {
        //do nothing
        return null;
    }


    /***刚好inprocess处理后的数据幅值给输出引脚*/
    @Override
    public void outprocess(Project project, JSONObject outdata) {
        for (ModleProperty modleProperty : propertyImpList) {
            if (outdata.containsKey(((BaseModlePropertyImp) modleProperty).getResource().getString("inmappingtag"))) {
                Double tagvalue = outdata.getDouble(((BaseModlePropertyImp) modleProperty).getResource().getString("inmappingtag"));
                ((BaseModlePropertyImp) modleProperty).setValue(tagvalue);
            }
        }
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



    /**db*/
    private List<ModleProperty> propertyImpList;
}
