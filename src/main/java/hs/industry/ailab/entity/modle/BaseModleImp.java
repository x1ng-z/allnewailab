package hs.industry.ailab.entity.modle;

import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.entity.ModleSight;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.modle.Modle;

import java.time.Instant;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/8 16:46
 */
public abstract class BaseModleImp implements Modle {
    public static final int RUNLEVEL_RUNNING=1;//正在运行中
    public static final int RUNLEVEL_RUNCOMPLET=2;//运行完成
    public static final int RUNLEVEL_INITE=3;//初始状态
//    public static final int RUNLEVEL_ENABLE=6;//dcs自动
//    public static final int RUNLEVEL_DISENABLE=7;//dcs手动
    public static final int RUNLEVEL_JAVAMODLEBUILDCOMPLET=5;//java模型构建完成状态
    public static final int RUNLEVEL_PYTHONMODLEBUILDCOMPLET=4;//python模型构建状态


    /**memory*/
    private int modlerunlevel=RUNLEVEL_INITE;
    private String errormsg="";
    private long errortimestamp;
    private int autovalue=1;
    private Instant beginruntime;//模型开始运行时间


    /***db***/
    private int modleId;//模型id主键
    private String modleName;//模型名称
    private int modleEnable=1;//模块使能，用于设置算法是否运行，算法是否运行
    private String modletype;
    private int refprojectid;
    /*****/

    private ModleSight modleSight;//模型视图

    @Override
    public abstract void connect();
    @Override
    public abstract void reconnect();
    @Override
    public abstract void destory() ;
    @Override
    public abstract void docomputeprocess() ;

    @Override
    public abstract JSONObject inprocess(Project project) ;

    @Override
    public abstract JSONObject computresulteprocess(Project project,JSONObject computedata) ;

    @Override
    public abstract  void outprocess(Project project,JSONObject outdata) ;

    @Override
    public abstract void init();

    public int getModleId() {
        return modleId;
    }

    public void setModleId(int modleId) {
        this.modleId = modleId;
    }

    public String getModleName() {
        return modleName;
    }

    public void setModleName(String modleName) {
        this.modleName = modleName;
    }

    public int getModleEnable() {
        return modleEnable;
    }

    public void setModleEnable(int modleEnable) {
        this.modleEnable = modleEnable;
    }

    public String getModletype() {
        return modletype;
    }

    public void setModletype(String modletype) {
        this.modletype = modletype;
    }

    public int getRefprojectid() {
        return refprojectid;
    }

    public void setRefprojectid(int refprojectid) {
        this.refprojectid = refprojectid;
    }

    public ModleSight getModleSight() {
        return modleSight;
    }

    public void setModleSight(ModleSight modleSight) {
        this.modleSight = modleSight;
    }

    public int getModlerunlevel() {
        return modlerunlevel;
    }

    public void setModlerunlevel(int modlerunlevel) {
        this.modlerunlevel = modlerunlevel;
    }

    public String getErrormsg() {
        return errormsg;
    }

    public void setErrormsg(String errormsg) {
        this.errormsg = errormsg;
    }

    public long getErrortimestamp() {
        return errortimestamp;
    }

    public void setErrortimestamp(long errortimestamp) {
        this.errortimestamp = errortimestamp;
    }

    public int getAutovalue() {
        return autovalue;
    }

    public void setAutovalue(int autovalue) {
        this.autovalue = autovalue;
    }

    public Instant getBeginruntime() {
        return beginruntime;
    }

    public void setBeginruntime(Instant beginruntime) {
        this.beginruntime = beginruntime;
    }
}
