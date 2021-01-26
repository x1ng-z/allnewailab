package hs.industry.ailab.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.entity.modle.controlmodle.MPCModle;
import hs.industry.ailab.entity.modle.controlmodle.PIDModle;
import hs.industry.ailab.entity.modle.customizemodle.CUSTOMIZEModle;
import hs.industry.ailab.entity.modle.filtermodle.FilterModle;
import hs.industry.ailab.entity.modle.iomodle.INModle;
import hs.industry.ailab.entity.modle.iomodle.OUTModle;
import hs.industry.ailab.pydriver.pyproxyserve.IOServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/8 17:09
 */
public class Project implements Runnable {
    private Logger logger = LoggerFactory.getLogger(Project.class);

    /**
     * memory
     */
    private Map<Integer, Modle> indexmodles;
    private boolean projectrun = true;

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && projectrun) {

            for (Modle modle : modleList) {
                if (modle instanceof MPCModle) {
                    MPCModle mpcModle = (MPCModle) modle;
                    JSONArray parents = mpcModle.getModleSight().getParents();
                    boolean isneedrun = true;
                    for (int index = 0; index < parents.size(); index++) {
                        JSONObject parent = parents.getJSONObject(index);
                        int parentid = parent.getInteger("id");
                        BaseModleImp parentmodle = (BaseModleImp) indexmodles.get(parentid);
                        isneedrun = isneedrun && (parentmodle.getModlerunlevel() == BaseModleImp.RUNLEVEL_RUNCOMPLET);
                    }
                    if (isneedrun && (mpcModle.getModlerunlevel() == BaseModleImp.RUNLEVEL_INITE) && (mpcModle.getSimulatControlModle().getModlerunlevel() == BaseModleImp.RUNLEVEL_INITE)) {
                        mpcModle.inprocess(this);
                        mpcModle.docomputeprocess();
                    }
                    continue;
                } else if (modle instanceof PIDModle) {
                    PIDModle pidModle = (PIDModle) modle;
                    JSONArray parents = pidModle.getModleSight().getParents();
                    boolean isneedrun = true;
                    for (int index = 0; index < parents.size(); index++) {
                        JSONObject parent = parents.getJSONObject(index);
                        int parentid = parent.getInteger("id");
                        BaseModleImp parentmodle = (BaseModleImp) indexmodles.get(parentid);
                        isneedrun = isneedrun && (parentmodle.getModlerunlevel() == BaseModleImp.RUNLEVEL_RUNCOMPLET);
                    }
                    if (isneedrun && (pidModle.getModlerunlevel() == BaseModleImp.RUNLEVEL_INITE)) {
                        pidModle.inprocess(this);
                        pidModle.docomputeprocess();
                    }
                    continue;
                } else if (modle instanceof CUSTOMIZEModle) {
                    CUSTOMIZEModle customizeModle = (CUSTOMIZEModle) modle;
                    JSONArray parents = customizeModle.getModleSight().getParents();
                    boolean isneedrun = true;
                    for (int index = 0; index < parents.size(); index++) {
                        JSONObject parent = parents.getJSONObject(index);
                        int parentid = parent.getInteger("id");
                        BaseModleImp parentmodle = (BaseModleImp) indexmodles.get(parentid);
                        isneedrun = isneedrun && (parentmodle.getModlerunlevel() == BaseModleImp.RUNLEVEL_RUNCOMPLET);
                    }
                    if (isneedrun && (customizeModle.getModlerunlevel() == BaseModleImp.RUNLEVEL_INITE)) {
                        customizeModle.inprocess(this);
                        customizeModle.docomputeprocess();
                    }
                    continue;
                } else if (modle instanceof FilterModle) {
                    FilterModle filterModle = (FilterModle) modle;
                    JSONArray parents = filterModle.getModleSight().getParents();
                    boolean isneedrun = true;
                    for (int index = 0; index < parents.size(); index++) {
                        JSONObject parent = parents.getJSONObject(index);
                        int parentid = parent.getInteger("id");
                        BaseModleImp parentmodle = (BaseModleImp) indexmodles.get(parentid);
                        isneedrun = isneedrun && (parentmodle.getModlerunlevel() == BaseModleImp.RUNLEVEL_RUNCOMPLET);
                    }
//                    System.out.println("projectid="+filterModle.getModleName()+"  isneedrun="+isneedrun+"  "+ filterModle.getModlerunlevel()+"");

                    if (isneedrun && (filterModle.getModlerunlevel() == BaseModleImp.RUNLEVEL_INITE)) {
                        filterModle.inprocess(this);
                        filterModle.docomputeprocess();
                    }
                    continue;
                } else if (modle instanceof INModle) {
                    INModle inModle = (INModle) modle;
                    JSONArray parents = inModle.getModleSight().getParents();
                    boolean isneedrun = true;
                    for (int index = 0; index < parents.size(); index++) {
                        JSONObject parent = parents.getJSONObject(index);
                        int parentid = parent.getInteger("id");
                        BaseModleImp parentmodle = (BaseModleImp) indexmodles.get(parentid);
                        isneedrun = isneedrun && (parentmodle.getModlerunlevel() == BaseModleImp.RUNLEVEL_RUNCOMPLET);
                    }
                    System.out.println("projectid=" + inModle.getModleName() + "  isneedrun=" + isneedrun + "  " + inModle.getModlerunlevel() + "");

                    if (isneedrun && (inModle.getModlerunlevel() == BaseModleImp.RUNLEVEL_INITE)) {
                        JSONObject inputdata = inModle.inprocess(this);
//                        System.out.println("inModle "+inputdata.toJSONString());
                        inModle.docomputeprocess();
                        inModle.outprocess(this, inputdata);
                    }
                    continue;
                } else if (modle instanceof OUTModle) {

                    OUTModle outModle = (OUTModle) modle;
                    JSONArray parents = outModle.getModleSight().getParents();
                    boolean isneedrun = true;
                    for (int index = 0; index < parents.size(); index++) {
                        JSONObject parent = parents.getJSONObject(index);
                        int parentid = parent.getInteger("id");
                        BaseModleImp parentmodle = (BaseModleImp) indexmodles.get(parentid);
                        isneedrun = isneedrun && (parentmodle.getModlerunlevel() == BaseModleImp.RUNLEVEL_RUNCOMPLET);
                    }
                    System.out.println("projectid=" + outModle.getModleName() + "  isneedrun=" + isneedrun + "  " + outModle.getModlerunlevel() + "");

                    if (isneedrun && (outModle.getModlerunlevel() == BaseModleImp.RUNLEVEL_INITE)) {
                        outModle.inprocess(this);
//                        System.out.println("outModle ");
                        outModle.docomputeprocess();
                        outModle.outprocess(this, null);
                    }
                    continue;
                }

            }

            boolean allcomplete = true;
            for (Modle modle : modleList) {
                BaseModleImp baseModleImp = (BaseModleImp) modle;
                allcomplete = allcomplete && (baseModleImp.getModlerunlevel() == BaseModleImp.RUNLEVEL_RUNCOMPLET);
            }

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
            if (allcomplete) {
                try {
                    for (Modle modle : modleList) {
                        BaseModleImp baseModleImp = (BaseModleImp) modle;
                        if (baseModleImp instanceof MPCModle) {
                            MPCModle mpcModle = (MPCModle) baseModleImp;
                            mpcModle.getSimulatControlModle().setModlerunlevel(BaseModleImp.RUNLEVEL_INITE);
                        }
                        baseModleImp.setModlerunlevel(BaseModleImp.RUNLEVEL_INITE);
                    }
                    TimeUnit.MILLISECONDS.sleep(Double.valueOf(getRunperiod() * 1000).intValue());
                } catch (InterruptedException e) {
                    return;
                }
            }

        }

    }

    public void init() {
        indexmodles = new HashMap<>();
        for (Modle modle : modleList) {
            BaseModleImp baseModleImp = (BaseModleImp) modle;
            indexmodles.put(baseModleImp.getModleId(), baseModleImp);
        }
    }


    /**
     * db
     */
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

    public boolean isProjectrun() {
        return projectrun;
    }

    public void setProjectrun(boolean projectrun) {
        this.projectrun = projectrun;
    }
}
