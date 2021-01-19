package hs.industry.ailab.entity.modle.controlmodle;

import hs.industry.ailab.entity.ResponTimeSerise;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.entity.modle.modlerproerty.MPCModleProperty;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/8 16:50
 */
public class MPCModle extends BaseModleImp {

    /****db****/
    private Integer predicttime_P = 12;//预测时域
    private Integer controltime_M = 6;//单一控制输入未来控制M步增量(控制域)
    private Integer timeserise_N = 40;//响应序列长度
    private Integer controlAPCOutCycle = 0;//控制周期
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
    /*********/

}
