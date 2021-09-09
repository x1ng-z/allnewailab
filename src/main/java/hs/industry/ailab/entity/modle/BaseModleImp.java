package hs.industry.ailab.entity.modle;

import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.constant.ModelRunStatusEnum;
import hs.industry.ailab.entity.ModleSight;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.service.HttpClientService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Map;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/8 16:46
 */
@Data
@Slf4j
public abstract class BaseModleImp implements Modle {
    public static final int RUNLEVEL_RUNNING=1;//正在运行中
    public static final int RUNLEVEL_RUNCOMPLET=2;//运行完成
    public static final int RUNLEVEL_INITE=3;//初始状态
//    public static final int RUNLEVEL_ENABLE=6;//dcs自动
//    public static final int RUNLEVEL_DISENABLE=7;//dcs手动
    public static final int RUNLEVEL_JAVAMODLEBUILDCOMPLET=5;//java模型构建完成状态
    public static final int RUNLEVEL_PYTHONMODLEBUILDCOMPLET=4;//python模型构建状态


    /**memory*/
    private ModelRunStatusEnum modlerunlevel=ModelRunStatusEnum.MODEL_RUN_STATUS_INITE;
    private String errormsg="";
    private long errortimestamp;
    private int autovalue=1;
    private Instant beginruntime;//模型开始运行时间，用于重置模型运行状态
    private Instant activetime;//用于判断模型是否已经离线

    private HttpClientService httpClientService;
    private Map<Integer, BaseModlePropertyImp> indexproperties;//key=modleid

    /***db***/
    private int modleId;//模型id主键
    private String modleName;//模型名称
    private int modleEnable=1;//模块使能，用于设置算法是否运行，算法是否运行
    private String modletype;
    private int refprojectid;
    /*****/

    private ModleSight modleSight;//模型视图

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
}
