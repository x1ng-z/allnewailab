package hs.industry.ailab.entity;

import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.entity.modle.ModleProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/8 17:09
 */
public class Project {

    private Map<Integer,Modle> indexmodles;





    public void init(){
        indexmodles=new HashMap<>();
        for (Modle modle : modleList) {
            BaseModleImp baseModleImp = (BaseModleImp) modle;
            indexmodles.put( baseModleImp.getModleId(),baseModleImp);
        }
    }


    /**db*/
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

    public Map<Integer, Modle> getIndexmodles() {
        return indexmodles;
    }

    public void setIndexmodles(Map<Integer, Modle> indexmodles) {
        this.indexmodles = indexmodles;
    }
}
