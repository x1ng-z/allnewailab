package hs.industry.ailab.entity.modle.iomodle;

import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.utils.httpclient.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/9 11:19
 */
public class INModle extends BaseModleImp {
    private Logger logger = LoggerFactory.getLogger(INModle.class);


    /**
     * memery
     */
    private boolean iscomplete = false;
    private String datasource;
    private Map<Integer,BaseModlePropertyImp> indexproperties;//key=modleid

    @Override
    public JSONObject inprocess(Project project) {
        StringBuilder tags = new StringBuilder();
        Map<String, String> postdata = new HashMap<>();
        for (ModleProperty modleProperty : propertyImpList) {
            tags.append(((BaseModlePropertyImp) modleProperty).getResource().getString("inmappingtag") + ",");
        }
        postdata.put("tags", tags.toString().substring(0, tags.length() - 1));
        String inputdata = HttpUtils.PostData(datasource + "/realdata/read", postdata);
        JSONObject jsoninputdata = JSONObject.parseObject(inputdata);
        return jsoninputdata.getJSONObject("data");
    }

    @Override
    public JSONObject computeprocess(Project project) {
        return null;
    }

    @Override
    public void outprocess(Project project, JSONObject outdata) {
        for (ModleProperty modleProperty : propertyImpList) {
            Double tagvalue = outdata.getDouble(((BaseModlePropertyImp) modleProperty).getResource().getString("inmappingtag"));
            ((BaseModlePropertyImp) modleProperty).setValue(tagvalue);
        }
    }


    @Override
    public void init() {
        indexproperties=new HashMap<>();
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
            indexproperties.put( baseModlePropertyImp.getModlepinsId(),baseModlePropertyImp);
        }
    }

    /**
     * db
     **/
    private List<ModleProperty> propertyImpList;

    public List<ModleProperty> getPropertyImpList() {
        return propertyImpList;
    }

    public void setPropertyImpList(List<ModleProperty> propertyImpList) {
        this.propertyImpList = propertyImpList;
    }

    public boolean isIscomplete() {
        return iscomplete;
    }

    public void setIscomplete(boolean iscomplete) {
        this.iscomplete = iscomplete;
    }


    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public Map<Integer, BaseModlePropertyImp> getIndexproperties() {
        return indexproperties;
    }

    public void setIndexproperties(Map<Integer, BaseModlePropertyImp> indexproperties) {
        this.indexproperties = indexproperties;
    }
}
