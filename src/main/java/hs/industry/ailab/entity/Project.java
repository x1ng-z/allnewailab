package hs.industry.ailab.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.constant.ModelRunStatusEnum;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.entity.modle.controlmodle.MPCModle;
import hs.industry.ailab.entity.modle.controlmodle.PIDModle;
import hs.industry.ailab.entity.modle.customizemodle.CUSTOMIZEModle;
import hs.industry.ailab.entity.modle.filtermodle.FilterModle;
import hs.industry.ailab.entity.modle.iomodle.INModle;
import hs.industry.ailab.entity.modle.iomodle.OUTModle;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/8 17:09
 */
@Slf4j
@Data
public class Project implements Runnable {

    /**
     * memory
     */
    private Map<Integer, Modle> indexmodles;
    private boolean projectrun = true;
    AtomicLong success = new AtomicLong(0);
    private AtomicBoolean isProjectStop= new AtomicBoolean(false);
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && projectrun) {
            if (modleList.size() == 0) {
                isProjectStop.set(true);
                log.warn("hava no model. id={} name={}",getProjectid(),name);
                return;
            }
            try {

                for (Modle modle : modleList) {
                    //here request api style
                    if(!projectrun){
                        break;
                    }
                    BaseModleImp baseModleImp = (BaseModleImp) modle;

                    log.debug("############ success={}", success.get());
                    modleList.forEach(m -> {
                        BaseModleImp baseModleImp_1 = (BaseModleImp) m;
                        log.debug(baseModleImp_1.getModleName() + baseModleImp_1.getModlerunlevel().getDesc());
                    });
                    log.debug("############");


                    JSONArray parents = baseModleImp.getModleSight().getParents();
                    boolean isneedrun = true;
                    for (int index = 0; index < parents.size(); index++) {
                        JSONObject parent = parents.getJSONObject(index);
                        int parentid = parent.getInteger("id");
                        BaseModleImp parentmodle = (BaseModleImp) indexmodles.get(parentid);
                        isneedrun = isneedrun && parentmodle.getModlerunlevel().equals(ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE);
                        if (!isneedrun) {
                            break;
                        }
                    }

                    if (isneedrun && baseModleImp.getModlerunlevel().equals(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE)) {
                        baseModleImp.inprocess(this);
                        baseModleImp.docomputeprocess();
                        if(baseModleImp.getModlerunlevel().equals(ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE)){
                            success.addAndGet(1);
                        }

                    }

                }

                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }

                if (success.get() != modleList.size()) {
                    continue;
                }


                log.debug("********** success={}", success.get());
                modleList.forEach(m -> {
                    BaseModleImp baseModleImp_1 = (BaseModleImp) m;
                    log.debug(baseModleImp_1.getModleName() + baseModleImp_1.getModlerunlevel().getDesc());
                });
                log.debug("*********");


                Instant earliestNode = null;
                boolean reSetAll = false;
                while (!reSetAll) {
                    try {
                        for (Modle modle : modleList) {
                            BaseModleImp leafmodle = (BaseModleImp) modle;
                            //是否为叶节点
                            if (leafmodle.getModleSight().getChilds().size() == 0) {
                                log.debug("leafnode={}", leafmodle.getModleName());
                                if (leafmodle.getModlerunlevel() == ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE) {
                                    //已经完成状态的叶节点
                                    List<BaseModleImp> rootmodles = new ArrayList<>();
                                    //查找到所有的根节点
                                    findRootModle(rootmodles, leafmodle.getModleId());
                                    Instant earliest = Instant.now();
                                    //找到运行完成最早的根节点
                                    if (rootmodles.size() != 0) {
                                        for (BaseModleImp rootmodle : rootmodles) {
                                            if (rootmodle.getBeginruntime() != null) {
                                                if (rootmodle.getBeginruntime().isBefore(earliest)) {
                                                    earliest = rootmodle.getBeginruntime();
                                                }
                                            }
                                        }
                                    }
                                    earliestNode = earliest;
                                    //最早的根节点开始运行时间是不是和现在相差一个调度时间了

                                    log.debug("runtimeend={},now={}", earliest.plusMillis(Double.valueOf(getRunperiod() * 1000).longValue()), Instant.now());
                                    if (earliest.plusMillis(Double.valueOf(getRunperiod() * 1000).longValue()).isBefore(Instant.now())) {
                                        //根据叶子模块是的运行完成，去重置输出模块父节点上的运行状态,采用深度优先遍历
                                        JSONArray outparents = leafmodle.getModleSight().getParents();
                                        for (int index = 0; index < outparents.size(); index++) {
                                            reSetParentsModleRunLevel(outparents.getJSONObject(index).getInteger("id"));
                                        }
                                        leafmodle.setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);

                                    }

                                }


                            }
                        }
                    } catch (Exception e) {
                        return;
                    }

                   // log.debug("********befor sleep,end={},now={},interval={}", earliestNode.plusMillis(Double.valueOf(getRunperiod() * 1000).longValue()), Instant.now(), earliestNode.plusMillis(Double.valueOf(getRunperiod() * 1000).longValue()).isAfter(Instant.now()));
                    if (earliestNode != null && earliestNode.plusMillis(Double.valueOf(getRunperiod() * 1000).longValue()).isAfter(Instant.now())) {

                        Duration duration = Duration.between(Instant.now(), earliestNode.plusMillis(Double.valueOf(getRunperiod() * 1000).longValue()));
                        log.debug("********sleep,end={},now={},interval={}", earliestNode.plusMillis(Double.valueOf(getRunperiod() * 1000).longValue()), Instant.now(), duration.toMillis());

                        TimeUnit.MILLISECONDS.sleep(duration.toMillis());
                    }


                    reSetAll = true;
                    for (Modle m : modleList) {
                        BaseModleImp baseModleImp_1 = (BaseModleImp) m;
                        reSetAll = reSetAll && baseModleImp_1.getModlerunlevel().equals(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
                        if (!reSetAll) {
                            break;
                        }
                    }


                }

                if (success.get() == modleList.size()) {
                    success.set(0);
                }


            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }
        log.info("try to stop projectid={},name={}",projectid,name);
        for (Modle modle : modleList) {
            modle.destory();
        }
        log.info("stop projectid={},name={} complete",projectid,name);
        isProjectStop.set(true);
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

    //深度优先遍历
    private void reSetParentsModleRunLevel(int parentmodleid) {
        BaseModleImp baseModleImp = (BaseModleImp) indexmodles.get(parentmodleid);
        if (baseModleImp == null) {
            return;
        }
        //根节点运行完成
        if (baseModleImp.getModlerunlevel() == ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE) {
            log.debug("reset model status={}", baseModleImp.getModleName());
            baseModleImp.setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
            JSONArray parents = baseModleImp.getModleSight().getParents();
            for (int index = 0; index < parents.size(); index++) {
                this.reSetParentsModleRunLevel(parents.getJSONObject(index).getInteger("id"));
            }

        }

    }

    //深度优先遍历根节点模型
    private void findRootModle(List<BaseModleImp> rootmodle, int parentmodleid) {
        BaseModleImp baseModleImp = (BaseModleImp) indexmodles.get(parentmodleid);
        if (baseModleImp == null) {
            return;
        }
        JSONArray parents = baseModleImp.getModleSight().getParents();
        if (parents.size() == 0) {
            rootmodle.add(baseModleImp);
            return;
        } else {
            for (int index = 0; index < parents.size(); index++) {
                this.findRootModle(rootmodle, parents.getJSONObject(index).getInteger("id"));
            }
        }

    }

}
