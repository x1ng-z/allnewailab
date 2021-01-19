package hs.industry.ailab.entity.modle.modlerproerty;

import hs.industry.ailab.entity.modle.BaseModlePropertyImp;

import java.util.regex.Pattern;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/8 16:54
 */
public class MPCModleProperty extends BaseModlePropertyImp {
    Pattern pvenablepattern = Pattern.compile("(^pvenable\\d+$)");

    /******db*****/
    private double Q;
    private double dmvHigh;
    private double deadZone;
    private double funelinitValue;
    private double R;
    private double dmvLow;
    private double referTrajectoryCoef;
    private String funneltype;
    private String pintype;
    private String tracoefmethod;
    /*************/





    public double getQ() {
        return Q;
    }

    public void setQ(double q) {
        Q = q;
    }

    public double getDmvHigh() {
        return dmvHigh;
    }

    public void setDmvHigh(double dmvHigh) {
        this.dmvHigh = dmvHigh;
    }

    public double getDeadZone() {
        return deadZone;
    }

    public void setDeadZone(double deadZone) {
        this.deadZone = deadZone;
    }

    public double getFunelinitValue() {
        return funelinitValue;
    }

    public void setFunelinitValue(double funelinitValue) {
        this.funelinitValue = funelinitValue;
    }

    public double getR() {
        return R;
    }

    public void setR(double r) {
        R = r;
    }

    public double getDmvLow() {
        return dmvLow;
    }

    public void setDmvLow(double dmvLow) {
        this.dmvLow = dmvLow;
    }

    public double getReferTrajectoryCoef() {
        return referTrajectoryCoef;
    }

    public void setReferTrajectoryCoef(double referTrajectoryCoef) {
        this.referTrajectoryCoef = referTrajectoryCoef;
    }

    public String getFunneltype() {
        return funneltype;
    }

    public void setFunneltype(String funneltype) {
        this.funneltype = funneltype;
    }

    public String getPintype() {
        return pintype;
    }

    public void setPintype(String pintype) {
        this.pintype = pintype;
    }

    public String getTracoefmethod() {
        return tracoefmethod;
    }

    public void setTracoefmethod(String tracoefmethod) {
        this.tracoefmethod = tracoefmethod;
    }
    /*************/

}
