package hs.industry.ailab.entity.modle.controlmodle;

import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.ResponTimeSerise;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.entity.modle.iomodle.INModle;
import hs.industry.ailab.entity.modle.modlerproerty.MPCModleProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/8 16:50
 */
public class MPCModle extends BaseModleImp {
    private Logger logger = LoggerFactory.getLogger(MPCModle.class);


    /**
     * memery
     */
    private boolean iscomplete = false;
    private String datasource;
    private Map<Integer, BaseModlePropertyImp> indexproperties;//key=modleid






    /****db****/
    private Integer predicttime_P;//预测时域
    private Integer controltime_M;//单一控制输入未来控制M步增量(控制域)
    private Integer timeserise_N;//响应序列长度
    private Integer controlAPCOutCycle;//控制周期
    private Integer runstyle=0;//运行方式0-自动分配模式 1-手动分配模式
    /**************/

    private List<ModleProperty> propertyImpList;
    private List<ResponTimeSerise> responTimeSeriseList;



    public Integer getPredicttime_P() {
        return predicttime_P;
    }

    public void setPredicttime_P(Integer predicttime_P) {
        this.predicttime_P = predicttime_P;
    }

    public Integer getControltime_M() {
        return controltime_M;
    }

    public void setControltime_M(Integer controltime_M) {
        this.controltime_M = controltime_M;
    }

    public Integer getTimeserise_N() {
        return timeserise_N;
    }

    public void setTimeserise_N(Integer timeserise_N) {
        this.timeserise_N = timeserise_N;
    }

    public Integer getControlAPCOutCycle() {
        return controlAPCOutCycle;
    }

    public void setControlAPCOutCycle(Integer controlAPCOutCycle) {
        this.controlAPCOutCycle = controlAPCOutCycle;
    }

    public Integer getRunstyle() {
        return runstyle;
    }

    public void setRunstyle(Integer runstyle) {
        this.runstyle = runstyle;
    }

    public List<ModleProperty> getPropertyImpList() {
        return propertyImpList;
    }

    public void setPropertyImpList(List<ModleProperty> propertyImpList) {
        this.propertyImpList = propertyImpList;
    }

    public List<ResponTimeSerise> getResponTimeSeriseList() {
        return responTimeSeriseList;
    }

    public void setResponTimeSeriseList(List<ResponTimeSerise> responTimeSeriseList) {
        this.responTimeSeriseList = responTimeSeriseList;
    }

    @Override
    public JSONObject inprocess(Project project) {
        return null;
    }

    @Override
    public JSONObject computeprocess(Project project) {
        return null;
    }

    @Override
    public void outprocess(Project project, JSONObject outdata) {

    }

    @Override
    public void init() {
        indexproperties=new HashMap<>();
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
            indexproperties.put( baseModlePropertyImp.getModlepinsId(),baseModlePropertyImp);
        }
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


    /*********/

}
