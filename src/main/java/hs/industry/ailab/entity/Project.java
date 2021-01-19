package hs.industry.ailab.entity;

import hs.industry.ailab.entity.modle.Modle;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/8 17:09
 */
public class Project {
    private int projectid;
    private String name;
    private double runperiod;

    private List<Modle> modleList;

    public int getProjectid() {
        return projectid;
    }

    public void setProjectid(int projectid) {
        this.projectid = projectid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRunperiod() {
        return runperiod;
    }

    public void setRunperiod(double runperiod) {
        this.runperiod = runperiod;
    }

    public List<Modle> getModleList() {
        return modleList;
    }

    public void setModleList(List<Modle> modleList) {
        this.modleList = modleList;
    }
}
