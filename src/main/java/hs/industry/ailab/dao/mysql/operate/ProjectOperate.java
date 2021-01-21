package hs.industry.ailab.dao.mysql.operate;

import hs.industry.ailab.entity.ModleSight;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.ResponTimeSerise;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.Modle;
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
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/8 16:05
 */
@Repository
public interface ProjectOperate {

    int insertProject(@Param("project") Project project);
    int deleteProject(@Param("projectid")int projectid);
    int updateProject(@Param("project")Project project);
    Project findProjectById(@Param("projectid")int projectid);
    Project findAllProject();




    int insertMPCModle(@Param("modle") MPCModle modle);
    int insertCUSTOMIZEModle(@Param("modle") CUSTOMIZEModle modle);
    int insertPIDModle(@Param("modle") PIDModle modle);
    int insertFilterModle(@Param("modle") FilterModle modle);
    int insertINModle(@Param("modle") INModle modle);
    int insertOUTModle(@Param("modle") OUTModle modle);
    int insertBaseModleImp(@Param("modle") BaseModleImp modle);

    int deleteModleByid(@Param("modleid")int modleid);
    int deleteModleByprojectid(@Param("projectid")int projectid);

    int updateMPCModle(@Param("modle") MPCModle modle);
    int updateCUSTOMIZEModle(@Param("modle") CUSTOMIZEModle modle);
    int updatePIDModle(@Param("modle") PIDModle modle);
    int updateFilterModle(@Param("modle") FilterModle modle);
    int updateINModle(@Param("modle") INModle modle);
    int updateOUTModle(@Param("modle") OUTModle modle);


    Modle findModleByid(@Param("modleid")int modleid);
    Modle findModleByProjectid(@Param("projectid")int projectid);


    int insertBaseModleProperty(@Param("baseModlePropertyImp") BaseModlePropertyImp baseModlePropertyImp);
    int deleteBaseModlePropertyByid(@Param("modlepinsId") int modlepinsId);
    int deleteBaseModlePropertyByMoldeid(@Param("modleId") int modleId);

    int updateBaseModleProperty(@Param("baseModlePropertyImp") BaseModlePropertyImp baseModlePropertyImp);
    int updateBaseModlePropertyEnable(@Param("enbale") int enable,@Param("propertyid") int propertyid);
    BaseModlePropertyImp findBaseModlePropertyByid(@Param("modlepinsId") int modlepinsId);
    List<BaseModlePropertyImp> findBaseModlePropertyByModleid(@Param("refmodleId") int refmodleId);


    int insertMPCModleProperty(@Param("mpcModleProperty") MPCModleProperty mpcModleProperty);
    int deleteMPCModlePropertyByid(@Param("modlepinsId") int modlepinsId);
    int deleteMPCModlePropertyByModleid(@Param("modleId") int modleId);
    int updateMPCModleProperty(@Param("mpcModleProperty") MPCModleProperty mpcModleProperty);
    MPCModleProperty findMPCModlePropertyByid(@Param("modlepinsId") int modlepinsId);
    List<MPCModleProperty> findMPCModlePropertyByModleid(@Param("refmodleId") int refmodleId);



    int insertModleSight(@Param("modleSight") ModleSight modleSight);
    int deleteModleSightByid(@Param("modlesightid") int modlesightid);
    int deleteModleSightByModleid(@Param("modleid") int modleid);
    int updateModleSight(@Param("modleSight") ModleSight modleSight);
    ModleSight findBaseModleSightByid(@Param("modlesightid") int modlesightid);
    ModleSight findBaseModleSightByModleid(@Param("refmodleid") int refmodleid);



    int insertResponTimeSerise(@Param("responTimeSerise") ResponTimeSerise responTimeSerise);
    int deleteResponTimeSeriseByid(@Param("modleresponId") int modleresponId);
    int deleteResponTimeSeriseByModleid(@Param("refrencemodleId") int refrencemodleId);
    int updateResponTimeSerise(@Param("responTimeSerise") ResponTimeSerise responTimeSerise);
    ResponTimeSerise findResponTimeSeriseByid(@Param("modletagId") int modletagId);
    List<ResponTimeSerise> findResponTimeSeriseByModleid(@Param("refrencemodleId") int refrencemodleId);




    int insertFirstOrderLagFilter(@Param("filter") FirstOrderLagFilter filter);
    int insertMoveAverageFilter(@Param("filter") MoveAverageFilter filter);

    int deleteFilter(@Param("filterid") int filterid);
    int deleteFilterByModleid(@Param("refmodleid") int refmodleid);
    int updateFirstOrderLagFilter(@Param("filter") FirstOrderLagFilter filter);
    int updateMoveAverageFilter(@Param("filter") MoveAverageFilter filter);
    Filter findFilterByid(@Param("filterid") int filterid);
    Filter findFilterByModleid(@Param("refmodleid") int refmodleid);


}
