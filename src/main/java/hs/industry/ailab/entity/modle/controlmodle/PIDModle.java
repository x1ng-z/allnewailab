package hs.industry.ailab.entity.modle.controlmodle;

import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.ModleProperty;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/9 11:17
 */
public class PIDModle extends BaseModleImp {
    /******db****/
//    private double kp;
//    private double ki;
//    private double kd;
    /*********/

    private List<ModleProperty> propertyImpList;



//    public double getKp() {
//        return kp;
//    }
//
//    public void setKp(double kp) {
//        this.kp = kp;
//    }
//
//    public double getKi() {
//        return ki;
//    }
//
//    public void setKi(double ki) {
//        this.ki = ki;
//    }
//
//    public double getKd() {
//        return kd;
//    }
//
//    public void setKd(double kd) {
//        this.kd = kd;
//    }

    public List<ModleProperty> getPropertyImpList() {
        return propertyImpList;
    }

    public void setPropertyImpList(List<ModleProperty> propertyImpList) {
        this.propertyImpList = propertyImpList;
    }
}
