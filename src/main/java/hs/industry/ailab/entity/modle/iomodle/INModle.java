package hs.industry.ailab.entity.modle.iomodle;

import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.utils.httpclient.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
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
//    private boolean javabuildcomplet = false;//java控制模型是构建完成？
//    private boolean pythonbuildcomplet = false;//python的控制模型是否构建完成
//    private boolean iscomputecomplete = false;//运算是否完成
    private String datasource;
    private Map<Integer, BaseModlePropertyImp> indexproperties;//key=modleid


    public void toBeRealModle(String datasource) {
        this.datasource = datasource;
    }

    @Override
    public void connect() {

    }

    @Override
    public void reconnect() {

    }

    @Override
    public void destory() {

    }

    @Override
    public void docomputeprocess() {
        setModlerunlevel(BaseModleImp.RUNLEVEL_RUNNING);

    }


    @Override
    public JSONObject inprocess(Project project) {
        StringBuilder tags = new StringBuilder();
        Map<String, String> postdata = new HashMap<>();
        for (ModleProperty modleProperty : propertyImpList) {
            tags.append(((BaseModlePropertyImp) modleProperty).getResource().getString("inmappingtag") + ",");
        }
        if (propertyImpList.size() > 0) {
            postdata.put("tags", tags.toString().substring(0, tags.length() - 1));
            String inputdata = HttpUtils.PostData(datasource + "/realdata/read", postdata);
            JSONObject jsoninputdata = JSONObject.parseObject(inputdata);
            return jsoninputdata.getJSONObject("data");
        }
        return new JSONObject();

    }

    @Override
    public JSONObject computresulteprocess(Project project, JSONObject computedata) {

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
        setModlerunlevel(BaseModleImp.RUNLEVEL_RUNCOMPLET);
        setActivetime(Instant.now());
    }


    @Override
    public void init() {
        indexproperties = new HashMap<>();
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
            indexproperties.put(baseModlePropertyImp.getModlepinsId(), baseModlePropertyImp);
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
//
//    public boolean isJavabuildcomplet() {
//        return javabuildcomplet;
//    }
//
//    public void setJavabuildcomplet(boolean javabuildcomplet) {
//        this.javabuildcomplet = javabuildcomplet;
//    }
//
//    public boolean isPythonbuildcomplet() {
//        return pythonbuildcomplet;
//    }
//
//    public void setPythonbuildcomplet(boolean pythonbuildcomplet) {
//        this.pythonbuildcomplet = pythonbuildcomplet;
//    }
//
//    public boolean isIscomputecomplete() {
//        return iscomputecomplete;
//    }
//
//    public void setIscomputecomplete(boolean iscomputecomplete) {
//        this.iscomputecomplete = iscomputecomplete;
//    }
}
