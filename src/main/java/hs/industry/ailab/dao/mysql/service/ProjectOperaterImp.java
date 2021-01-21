package hs.industry.ailab.dao.mysql.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.controller.ProjectEdit;
import hs.industry.ailab.dao.mysql.operate.ProjectOperate;
import hs.industry.ailab.entity.ModleSight;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.ResponTimeSerise;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.entity.modle.controlmodle.MPCModle;
import hs.industry.ailab.entity.modle.controlmodle.PIDModle;
import hs.industry.ailab.entity.modle.customizemodle.CUSTOMIZEModle;
import hs.industry.ailab.entity.modle.filter.Filter;
import hs.industry.ailab.entity.modle.filter.FirstOrderLagFilter;
import hs.industry.ailab.entity.modle.filter.MoveAverageFilter;
import hs.industry.ailab.entity.modle.filtermodle.FilterModle;
import hs.industry.ailab.entity.modle.iomodle.INModle;
import hs.industry.ailab.entity.modle.iomodle.OUTModle;
import hs.industry.ailab.entity.modle.modlerproerty.MPCModleProperty;
import hs.industry.ailab.utils.help.FileHelp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/10 8:49
 */
@Service
public class ProjectOperaterImp {
    private Logger logger = LoggerFactory.getLogger(ProjectOperaterImp.class);

    @Autowired
    private ProjectOperate projectOperate;


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertProject(Project project) {
        return projectOperate.insertProject(project);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int deleteProject(int projectid) {
        return projectOperate.deleteProject(projectid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateProject(Project project) {
        return projectOperate.updateProject(project);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public Project findProjectById(int projectid) {
        return projectOperate.findProjectById(projectid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public Project findAllProject() {
        return projectOperate.findAllProject();
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertMPCModle(MPCModle modle) {
        return projectOperate.insertMPCModle(modle);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertCUSTOMIZEModle(CUSTOMIZEModle modle) {
        return projectOperate.insertCUSTOMIZEModle(modle);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertPIDModle(PIDModle modle) {
        return projectOperate.insertPIDModle(modle);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertFilterModle(FilterModle modle) {
        return projectOperate.insertFilterModle(modle);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertINModle(INModle modle) {
        return projectOperate.insertINModle(modle);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertOUTModle(OUTModle modle) {
        return projectOperate.insertOUTModle(modle);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int deleteModleByid(int modleid) {
        return projectOperate.deleteModleByid(modleid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int deleteModleByprojectid(int projectid) {
        return projectOperate.deleteModleByprojectid(projectid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateMPCModle(MPCModle modle) {
        return projectOperate.updateMPCModle(modle);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateCUSTOMIZEModle(CUSTOMIZEModle modle) {
        return projectOperate.updateCUSTOMIZEModle(modle);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updatePIDModle(PIDModle modle) {
        return projectOperate.updatePIDModle(modle);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateFilterModle(FilterModle modle) {
        return projectOperate.updateFilterModle(modle);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateINModle(INModle modle) {
        return projectOperate.updateINModle(modle);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateOUTModle(OUTModle modle) {
        return projectOperate.updateOUTModle(modle);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public Modle findModleByid(int modleid) {
        return projectOperate.findModleByid(modleid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public Modle findModleByProjectid(int projectid) {
        return projectOperate.findModleByProjectid(projectid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertBaseModleProperty(BaseModlePropertyImp baseModlePropertyImp) {
        return projectOperate.insertBaseModleProperty(baseModlePropertyImp);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int deleteBaseModlePropertyByid(int modlepinsId) {
        return projectOperate.deleteBaseModlePropertyByid(modlepinsId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int deleteBaseModlePropertyByMoldeid(int modleId) {
        return projectOperate.deleteBaseModlePropertyByMoldeid(modleId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateBaseModleProperty(BaseModlePropertyImp baseModlePropertyImp) {
        return projectOperate.updateBaseModleProperty(baseModlePropertyImp);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateBaseModlePropertyEnable(int enable, int propertyid) {
        return projectOperate.updateBaseModlePropertyEnable(enable, propertyid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public BaseModlePropertyImp findBaseModlePropertyByid(int modlepinsId) {
        return projectOperate.findBaseModlePropertyByid(modlepinsId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public List<BaseModlePropertyImp> findBaseModlePropertyByModleid(int refmodleId) {
        return projectOperate.findBaseModlePropertyByModleid(refmodleId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertMPCModleProperty(MPCModleProperty mpcModleProperty) {
        return projectOperate.insertMPCModleProperty(mpcModleProperty);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int deleteMPCModlePropertyByid(int modlepinsId) {
        return projectOperate.deleteMPCModlePropertyByid(modlepinsId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int deleteMPCModlePropertyByModleid(int modleId) {
        return projectOperate.deleteMPCModlePropertyByModleid(modleId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateMPCModleProperty(MPCModleProperty mpcModleProperty) {
        return projectOperate.updateMPCModleProperty(mpcModleProperty);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public MPCModleProperty findMPCModlePropertyByid(int modlepinsId) {
        return projectOperate.findMPCModlePropertyByid(modlepinsId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public List<MPCModleProperty> findMPCModlePropertyByModleid(int refmodleId) {
        return projectOperate.findMPCModlePropertyByModleid(refmodleId);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertModleSight(ModleSight modleSight) {
        return projectOperate.insertModleSight(modleSight);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int deleteModleSightByid(int modlesightid) {
        return projectOperate.deleteModleSightByid(modlesightid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int deleteModleSightByModleid(int modleid) {
        return projectOperate.deleteModleSightByModleid(modleid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateModleSight(ModleSight modleSight) {
        return projectOperate.updateModleSight(modleSight);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public ModleSight findBaseModleSightByid(int modlesightid) {
        return projectOperate.findBaseModleSightByid(modlesightid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public ModleSight findBaseModleSightByModleid(int refmodleid) {
        return projectOperate.findBaseModleSightByModleid(refmodleid);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertResponTimeSerise(ResponTimeSerise responTimeSerise) {
        return projectOperate.insertResponTimeSerise(responTimeSerise);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int deleteResponTimeSeriseByid(int modleresponId) {
        return projectOperate.deleteResponTimeSeriseByid(modleresponId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int deleteResponTimeSeriseByModleid(int refrencemodleId) {
        return projectOperate.deleteResponTimeSeriseByModleid(refrencemodleId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateResponTimeSerise(ResponTimeSerise responTimeSerise) {
        return projectOperate.updateResponTimeSerise(responTimeSerise);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public ResponTimeSerise findResponTimeSeriseByid(int modletagId) {
        return projectOperate.findResponTimeSeriseByid(modletagId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public List<ResponTimeSerise> findResponTimeSeriseByModleid(int refrencemodleId) {
        return projectOperate.findResponTimeSeriseByModleid(refrencemodleId);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertFirstOrderLagFilter(FirstOrderLagFilter filter) {
        return projectOperate.insertFirstOrderLagFilter(filter);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertMoveAverageFilter(MoveAverageFilter filter) {
        return projectOperate.insertMoveAverageFilter(filter);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int deleteFilter(int filterid) {
        return projectOperate.deleteFilter(filterid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int deleteFilterByModleid(int refmodleid) {
        return projectOperate.deleteFilterByModleid(refmodleid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateFirstOrderLagFilter(FirstOrderLagFilter filter) {
        return projectOperate.updateFirstOrderLagFilter(filter);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateMoveAverageFilter(MoveAverageFilter filter) {
        return projectOperate.updateMoveAverageFilter(filter);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public Filter findFilterByid(int filterid) {
        return projectOperate.findFilterByid(filterid);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public Filter findFilterByModleid(int refmodleid) {
        return projectOperate.findFilterByModleid(refmodleid);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public void creatmodlebusiness(BaseModleImp baseModleImp) {
        projectOperate.insertBaseModleImp(baseModleImp);
        baseModleImp.getModleSight().setRefmodleid(baseModleImp.getModleId());
        projectOperate.insertModleSight(baseModleImp.getModleSight());
    }


    /**
     * @param parents 父节点信息
     */
    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public void deletemodlebusiness(int modleid, int projectid, JSONArray parents) {
        BaseModleImp baseModleImp = (BaseModleImp) projectOperate.findModleByid(modleid);
        //删除父关系
        JSONArray parentrations = baseModleImp.getModleSight().getParents();
        for (int index = 0; index < parentrations.size(); index++) {

            BaseModleImp parentbasemodle = (BaseModleImp) projectOperate.findModleByid(parentrations.getJSONObject(index).getInteger("id"));
            for (int parentindex = 0; parentindex < parentbasemodle.getModleSight().getChilds().size(); parentindex++) {
                JSONObject child = parentbasemodle.getModleSight().getChilds().getJSONObject(parentindex);
                if (child.getIntValue("id") == (modleid)) {
                    parentbasemodle.getModleSight().getChilds().remove(parentindex);
                    projectOperate.updateModleSight(parentbasemodle.getModleSight());
                    break;
                }
            }
        }


        //删除子关系
        JSONArray childsrations = baseModleImp.getModleSight().getChilds();
        for (int index = 0; index < childsrations.size(); index++) {
            BaseModleImp childbasemodle = (BaseModleImp) projectOperate.findModleByid(childsrations.getJSONObject(index).getInteger("id"));
            for (int childindex = 0; childindex < childbasemodle.getModleSight().getParents().size(); childindex++) {
                JSONObject parent = childbasemodle.getModleSight().getParents().getJSONObject(childindex);
                if (parent.getIntValue("id") == (modleid)) {
                    childbasemodle.getModleSight().getParents().remove(childindex);
                    projectOperate.updateModleSight(childbasemodle.getModleSight());
                    break;
                }
            }
        }


        projectOperate.deleteModleByid(modleid);//删除模型
        projectOperate.deleteModleSightByModleid(modleid);//删除模型组态信息
        projectOperate.deleteBaseModlePropertyByMoldeid(modleid);//删除模型属性


    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public void deletemodlechildbusiness(int modleid, int childid) {
        //子关系删除

        ModleSight parentmodleSight = projectOperate.findBaseModleSightByModleid(modleid);
        for (int index = 0; index < parentmodleSight.getChilds().size(); index++) {
            JSONObject child = parentmodleSight.getChilds().getJSONObject(index);
            if (child.getInteger("id").equals(childid)) {
                parentmodleSight.getChilds().remove(index);
                break;
            }
        }
        projectOperate.updateModleSight(parentmodleSight);

        //父关系删除
        ModleSight childmodleSight = projectOperate.findBaseModleSightByModleid(childid);

        for (int index = 0; index < childmodleSight.getParents().size(); index++) {
            JSONObject parent = childmodleSight.getParents().getJSONObject(index);
            if (parent.getInteger("id").equals(modleid)) {
                childmodleSight.getParents().remove(index);
                break;
            }
        }
        projectOperate.updateModleSight(childmodleSight);


    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public void createmodlechildbusiness(int modleid, int childid) {

        //创建子关系
        ModleSight parentmodleSight = projectOperate.findBaseModleSightByModleid(modleid);
        JSONObject jsonchild = new JSONObject();
        jsonchild.put("id", childid);
        parentmodleSight.getChilds().add(jsonchild);
        projectOperate.updateModleSight(parentmodleSight);

        //创建父关系
        ModleSight childmodleSight = projectOperate.findBaseModleSightByModleid(childid);
        JSONObject jsonparent = new JSONObject();
        jsonparent.put("id", modleid);
        childmodleSight.getParents().add(jsonparent);
        projectOperate.updateModleSight(childmodleSight);

    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public void movemodlebusiness(int modleid, double top, double left) {
        ModleSight modleSight = projectOperate.findBaseModleSightByModleid(modleid);
        modleSight.setPositiontop(top);
        modleSight.setPositionleft(left);
        projectOperate.updateModleSight(modleSight);
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateIOmodlebusiness(int modleid, String modename) {
        Modle modle = projectOperate.findModleByid(modleid);
        BaseModleImp baseModleImp = (BaseModleImp) modle;
        baseModleImp.setModleName(modename);
        int count=0;
        if(modle instanceof INModle){
            count+=projectOperate.updateINModle((INModle) baseModleImp);
        }
       if(modle instanceof OUTModle){
           count+=projectOperate.updateOUTModle((OUTModle)baseModleImp);
       }
        return count;
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateFiltermodlebusiness(FilterModle filterModle) {
        int count = 0;
        count += projectOperate.updateFilterModle(filterModle);
        if (filterModle.getFilter() instanceof FirstOrderLagFilter) {
            FirstOrderLagFilter firstOrderLagFilter = (FirstOrderLagFilter) filterModle.getFilter();
            if (firstOrderLagFilter.getFilterid() == -1) {
                count += projectOperate.insertFirstOrderLagFilter(firstOrderLagFilter);
            } else {
                count += projectOperate.updateFirstOrderLagFilter(firstOrderLagFilter);
            }

        } else if (filterModle.getFilter() instanceof MoveAverageFilter) {

            MoveAverageFilter moveAverageFilter = (MoveAverageFilter) filterModle.getFilter();
            if (moveAverageFilter.getFilterid() == -1) {
                count += projectOperate.insertMoveAverageFilter(moveAverageFilter);
            } else {
                count += projectOperate.updateMoveAverageFilter(moveAverageFilter);
            }

        }
        return count;

    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public List<BaseModlePropertyImp> findparentmodleboutputpinsbusiness(int modleid) {
        List<BaseModlePropertyImp> baseModlePropertyImps = new ArrayList<>();
        BaseModleImp baseModleImp = (BaseModleImp) projectOperate.findModleByid(modleid);
        JSONArray parentmodleids = baseModleImp.getModleSight().getParents();
        for (int indexparent = 0; indexparent < parentmodleids.size(); indexparent++) {

            Modle parentmodle = projectOperate.findModleByid(parentmodleids.getJSONObject(indexparent).getInteger("id"));
            if (parentmodle instanceof INModle) {
                INModle inModle = (INModle) parentmodle;
                for (ModleProperty modleProperty : inModle.getPropertyImpList()) {
                    if (((BaseModlePropertyImp) modleProperty).getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                        baseModlePropertyImps.add((BaseModlePropertyImp) modleProperty);
                    }

                }

            } else if (parentmodle instanceof OUTModle) {
                OUTModle outModle = (OUTModle) parentmodle;
                for (ModleProperty modleProperty : outModle.getPropertyImpList()) {
                    if (((BaseModlePropertyImp) modleProperty).getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                        baseModlePropertyImps.add((BaseModlePropertyImp) modleProperty);
                    }
                }

            } else if (parentmodle instanceof FilterModle) {
                FilterModle filterModle = (FilterModle) parentmodle;
                for (ModleProperty modleProperty : filterModle.getPropertyImpList()) {
                    if (((BaseModlePropertyImp) modleProperty).getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                        baseModlePropertyImps.add((BaseModlePropertyImp) modleProperty);
                    }
                }

            } else if (parentmodle instanceof CUSTOMIZEModle) {
                CUSTOMIZEModle customizeModle = (CUSTOMIZEModle) parentmodle;
                for (ModleProperty modleProperty : customizeModle.getPropertyImpList()) {
                    if (((BaseModlePropertyImp) modleProperty).getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                        baseModlePropertyImps.add((BaseModlePropertyImp) modleProperty);
                    }
                }

            } else if (parentmodle instanceof PIDModle) {
                PIDModle pidModle = (PIDModle) parentmodle;
                for (ModleProperty modleProperty : pidModle.getPropertyImpList()) {
                    if (((BaseModlePropertyImp) modleProperty).getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                        baseModlePropertyImps.add((BaseModlePropertyImp) modleProperty);
                    }
                }

            } else if (parentmodle instanceof MPCModle) {
                MPCModle mpcModle = (MPCModle) parentmodle;
                for (ModleProperty modleProperty : mpcModle.getPropertyImpList()) {
                    if (((BaseModlePropertyImp) modleProperty).getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                        baseModlePropertyImps.add((BaseModlePropertyImp) modleProperty);
                    }
                }
            }

        }
        return baseModlePropertyImps;

    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public List<BaseModlePropertyImp> findthismodleinputpinsbusiness(int modleid) {
        List<BaseModlePropertyImp> baseModlePropertyImps = new ArrayList<>();
        BaseModleImp baseModleImp = (BaseModleImp) projectOperate.findModleByid(modleid);

        if (baseModleImp instanceof INModle) {
            INModle inModle = (INModle) baseModleImp;
            if (inModle.getPropertyImpList() != null) {
                for (ModleProperty modleProperty : inModle.getPropertyImpList()) {
                    BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                    if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                        baseModlePropertyImps.add(baseModlePropertyImp);
                    }

                }
            }


        } else if (baseModleImp instanceof OUTModle) {
            OUTModle outModle = (OUTModle) baseModleImp;
            if (outModle.getPropertyImpList() != null) {
                for (ModleProperty modleProperty : outModle.getPropertyImpList()) {
                    BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                    if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                        baseModlePropertyImps.add(baseModlePropertyImp);
                    }

                }
            }

        } else if (baseModleImp instanceof FilterModle) {
            FilterModle filterModle = (FilterModle) baseModleImp;
            if (filterModle.getPropertyImpList() != null) {
                for (ModleProperty modleProperty : filterModle.getPropertyImpList()) {
                    BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                    if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                        baseModlePropertyImps.add(baseModlePropertyImp);
                    }

                }
            }

        } else if (baseModleImp instanceof CUSTOMIZEModle) {
            CUSTOMIZEModle customizeModle = (CUSTOMIZEModle) baseModleImp;
            if (customizeModle.getPropertyImpList() != null) {
                for (ModleProperty modleProperty : customizeModle.getPropertyImpList()) {
                    BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                    if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                        baseModlePropertyImps.add(baseModlePropertyImp);
                    }

                }
            }

        } else if (baseModleImp instanceof PIDModle) {
            PIDModle pidModle = (PIDModle) baseModleImp;
            if (pidModle.getPropertyImpList() != null) {
                for (ModleProperty modleProperty : pidModle.getPropertyImpList()) {
                    BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                    if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                        baseModlePropertyImps.add(baseModlePropertyImp);
                    }

                }
            }

        } else if (baseModleImp instanceof MPCModle) {
            MPCModle mpcModle = (MPCModle) baseModleImp;
            if (mpcModle.getPropertyImpList() != null) {
                for (ModleProperty modleProperty : mpcModle.getPropertyImpList()) {
                    BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                    if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                        baseModlePropertyImps.add(baseModlePropertyImp);
                    }

                }
            }
        }
        return baseModlePropertyImps;

    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int createCustomizeMoldebusiness(CUSTOMIZEModle customizeModle) {
        int count = 0;
        creatmodlebusiness(customizeModle);
        String pyfilename = customizeModle.getModleId() + "_" + System.currentTimeMillis() + ".py";
        try {
            FileHelp.creatpyfile(pyfilename, "customizepythontemplates");
            customizeModle.setCustomizepyname(pyfilename);
            count += projectOperate.updateCUSTOMIZEModle(customizeModle);
        } catch (IOException e) {
            throw new RuntimeException(pyfilename + " file operate error");
        }
        return count;
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateCustomizmodlebusiness(CUSTOMIZEModle customizeModle, String codecontext) {
        int count = 0;
        projectOperate.updateCUSTOMIZEModle(customizeModle);
        try {
            FileHelp.updateFile(customizeModle.getCustomizepyname(), codecontext);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException("python file operate failed");
        }
        return count;
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updatepidmodlebusiness(PIDModle pidModle, List<BaseModlePropertyImp> pidproperties) {
        int count = 0;
        count += projectOperate.updatePIDModle(pidModle);
        for (BaseModlePropertyImp baseModlePropertyImp : pidproperties) {
            if (baseModlePropertyImp.getModlepinsId() == -1) {
                count += projectOperate.insertBaseModleProperty(baseModlePropertyImp);
            } else {
                count += projectOperate.updateBaseModleProperty(baseModlePropertyImp);
            }

        }
        return count;

    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertoutmodlepropertybusiness(BaseModlePropertyImp inproperty,BaseModlePropertyImp outproperty) {
        int count = 0;
        count+=projectOperate.insertBaseModleProperty(inproperty);
        outproperty.getResource().put("modlepinsId",inproperty.getModlepinsId());
        count+=projectOperate.insertBaseModleProperty(outproperty);
        return count;
    }



    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updateoutmodlepropertybusiness(BaseModlePropertyImp inproperty,BaseModlePropertyImp outproperty) {
        int count = 0;
        count+=projectOperate.updateBaseModleProperty(inproperty);
        count+=projectOperate.updateBaseModleProperty(outproperty);
        return count;
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int insertmpcmodlepvrelationpropertybusiness(MPCModleProperty... mpcModleProperties) {
        int count = 0;
        for(MPCModleProperty mpcModleProperty:mpcModleProperties){
            count+=projectOperate.insertMPCModleProperty(mpcModleProperty);
        }
        return count;
    }


    @Transactional(isolation = Isolation.READ_COMMITTED, transactionManager = "mysqlTransactionManager")
    public int updatempcmodlepvrelationpropertybusiness(MPCModleProperty... mpcModleProperties) {
        int count = 0;
        for(MPCModleProperty mpcModleProperty:mpcModleProperties){
            count+=projectOperate.updateMPCModleProperty(mpcModleProperty);
        }
        return count;
    }


}
