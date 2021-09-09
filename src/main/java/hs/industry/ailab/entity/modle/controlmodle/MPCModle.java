package hs.industry.ailab.entity.modle.controlmodle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.config.AlgprithmApiConfig;
import hs.industry.ailab.constant.ModelRunStatusEnum;
import hs.industry.ailab.constant.ModelTypeEnum;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.ResponTimeSerise;
import hs.industry.ailab.entity.dto.PinDataDto;
import hs.industry.ailab.entity.dto.request.dmc.*;
import hs.industry.ailab.entity.dto.response.dmc.DmcData4PlantDto;
import hs.industry.ailab.entity.dto.response.dmc.DmcDmvData4PlantDto;
import hs.industry.ailab.entity.dto.response.dmc.DmcPvPredictData4PlantDto;
import hs.industry.ailab.entity.dto.response.dmc.DmcResponse4PlantDto;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.entity.modle.customizemodle.CUSTOMIZEModle;
import hs.industry.ailab.entity.modle.filtermodle.FilterModle;
import hs.industry.ailab.entity.modle.iomodle.INModle;
import hs.industry.ailab.entity.modle.iomodle.OUTModle;
import hs.industry.ailab.entity.modle.modlerproerty.MPCModleProperty;
import hs.industry.ailab.service.HttpClientService;
import hs.industry.ailab.utils.help.Tool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/8 16:50
 */
@Slf4j
@Data
public class MPCModle extends BaseModleImp {
    private Logger logger = LoggerFactory.getLogger(MPCModle.class);
    private static Pattern pvpattern = Pattern.compile("(^pv\\d+$)");
    private static Pattern ffpattern = Pattern.compile("(^ff\\d+$)");
    public static final String MSGTYPE_BUILD = "build";
    public static final String MSGTYPE_COMPUTE = "compute";
    public static final Integer RUNSTYLEBYAUTO = 0;//运行方式0-自动分配模式 1-手动分配模式
    public static final Integer RUNSTYLEBYMANUL = 1;//1-手动分配模式

    /**
     * memery
     */
    private boolean javabuildcomplet = false;//java控制模型是构建完成？
    private boolean pythonbuildcomplet = false;//python的控制模型是否构建完成
    private String datasource;
    private String pyproxyexecute;
    private String port;
    private String mpcscript;
    private String runmsg;//脚本运行的错误消息记录


    /**
     * 模型各个类型引脚数量
     */
    private int totalPv = 8;
    private int totalFf = 8;
    private int totalMv = 8;

    /**模型真实运行状态*/
    /**
     * apc反馈的y0的预测值
     */
    private double[][] backPVPrediction;//pv的预测曲线
    /**
     * apc反馈的pv的漏斗的上边界
     */
    private double[][] backPVFunelUp;//PV的漏斗上限
    /**
     * apc反馈的pv的漏斗的下边界
     */
    private double[][] backPVFunelDown;//PV的漏斗下限
    /**
     * apc反馈的dmv增量的预测值shape=(p,m)
     */
    private double[][] backDmvWrite;
    private double[] backrawDmv;
    private double[] backrawDff;
    /**
     * apc反馈的y0与yreal的error预测值
     */
    private double[] backPVPredictionError;//预测误差

    /**
     * 模型计算时候的前馈变换值dff shape=(p,num_ff)
     */
    private double[][] backDff;


//    private OpcServicConstainer opcServicConstainer;//opcserviceconstainer
//    private BaseConf baseConf;//控制器的基本配置，在数据库中所定义，进行注入

    /**
     * 执行apc算法的桥接器
     */
//    private ExecutePythonBridge executePythonBridge;


    /**
     * 原始多目标pv输出数量
     */
    private Integer baseoutpoints_p = 0;//输出个数量
    /**
     * 可运行的的pv引脚
     */
    private Integer numOfRunnablePVPins_pp = 0;

    /**
     * 原始前馈数量
     */
    private Integer basefeedforwardpoints_v = 0;
    /**
     * 对应可运行的pv引脚的可运行ff引脚数量
     */
    private Integer numOfRunnableFFpins_vv = 0;

    /**
     * 原始可控制输入数量
     */
    private Integer baseinputpoints_m = 0;
    /**
     * 对应可运行pv引脚所用的可运行mv引脚数量
     */
    private Integer numOfRunnableMVpins_mm = 0;

    private List<MPCModleProperty> categoryPVmodletag = new ArrayList<>();//已经分类号的PV引脚
    private List<MPCModleProperty> categorySPmodletag = new ArrayList<>();//已经分类号的SP引脚
    private List<MPCModleProperty> categoryMVmodletag = new ArrayList<>();//已经分类号的MV引脚
    private List<MPCModleProperty> categoryFFmodletag = new ArrayList<>();//已经分类号的FF引脚
//    private ModlePin autoEnbalePin = null;//dcs手自动切换引脚


    private double[][][] A_RunnabletimeseriseMatrix = null;//输入响应 shape=[pv][mv][resp_N]
    private double[][][] B_RunnabletimeseriseMatrix = null;//前馈响应 shape=[pv][ff][resp_N]
    private double[][] runnableintegrationInc_mv = null;//mv的积分增量
    private double[][] runnableintegrationInc_ff = null;//ff的积分增量
    private Double[] Q = null;//误差权重矩阵
    private Double[] R = null;//控制权矩阵、正则化
    private Double[] alpheTrajectoryCoefficients = null;//参考轨迹的柔化系数


    private String[] alpheTrajectoryCoefmethod = null;//参考轨迹的柔化系数方法
    private Double[] deadZones = null;//漏斗死区
    private Double[] funelinitvalues = null;//漏斗初始值
    private double[][] funneltype;


    /**
     * 仿真模型
     */
    private SimulatControlModle simulatControlModle;

    /***仿真模型python执行器*/
//    private ExecutePythonBridge simulateexecutePythonBridge;
    /**
     * 仿真器脚本名称
     */
    private String simulatorscript;


    /**
     * DB模型配置了mv1 mv2 mv3.....mvn
     * pv1  1
     * pv2  1
     * pv3
     * pv4
     * ...
     * pvn
     * <p>
     * 指示PV用了哪几个mv
     * pvusemv矩阵shape=(num_pv,num_mv)
     * 如：pv1用了mv1,pv2用了mv1
     * 如[[1,0]，
     * [0,1]]
     */
    private int[][] maskBaseMapPvUseMvMatrix = null;

    /**
     * 基本pv对mv的基本作用比例
     **/
    private float[][] maskBaseMapPvEffectMvMatrix = null;


    /**
     * 激活的pv引脚对应的mv
     */
    private int[][] maskMatrixRunnablePVUseMV = null;


    /**
     * 基本可运行的pv对mv的基本作用比例
     **/
    private float[][] maskMatrixRunnablePvEffectMv = null;


    /**
     * DB模型配置表示Pv使用了哪些ff
     * pvuseff矩阵shape=(num_pv,num_ff)
     * 如pv1用了ff1,pv2用了ff2
     * 如[[1,0],
     * [0,1]]
     */
    private int[][] maskBaseMapPvUseFfMatrix = null;
    /**
     * 激活的pv对应的ff
     */
    private int[][] maskMatrixRunnablePVUseFF = null;


    /**
     * 本次参与控制的FF引脚的标记矩阵，在内容为1的地方就说明改引脚被引用了
     */
    int[] maskisRunnableFFMatrix = null;
    /**
     * 本次参与控制的PV引脚的标记矩阵，在内容为1的地方就说明改引脚被引用了
     */
    int[] maskisRunnablePVMatrix = null;
    /**
     * 本次参与控制的MV引脚的标记矩阵，在内容为1的地方就说明改引脚被引用了
     */
    int[] maskisRunnableMVMatrix = null;

    /**
     * apc程序位置
     */
//    String apcdir;

    private Map<String, MPCModleProperty> stringmodlePinsMap = new HashMap<>();//方便引脚索引key=pv1.mv2,sp1,ff1等 value=引脚类


    /**
     * 初始化 ControlModle的重要属性，使其成为真正的ControlModle
     */
    public void toBeRealModle(int controlAPCOutCycle, int mpcpinnumber) {
        /***
         * 可以新建的引脚的数量
         * */
        this.totalFf = mpcpinnumber;//baseConf.getFf();
        this.totalMv = mpcpinnumber;// baseConf.getMv();
        this.totalPv = mpcpinnumber;//baseConf.getPv();
        this.controlAPCOutCycle = controlAPCOutCycle<=0?1:controlAPCOutCycle;
    }


    @Override
    public void destory() {
        HttpClientService httpClientService = getHttpClientService();
        AlgprithmApiConfig algprithmApiConfig = httpClientService.getAlgprithmApiConfig();
        httpClientService.PostParam(algprithmApiConfig.getUrl() + algprithmApiConfig.getStop() + getModleId(), null);
    }


    //模型短路，直接将输入的mv赋值给输出的mv
    private void modleshortcircuit() {
        List<MPCModleProperty> modlePropertyList = new ArrayList<>();
        for (ModleProperty modleProperty : propertyImpList) {
            MPCModleProperty mpcModleProperty = (MPCModleProperty) modleProperty;
            modlePropertyList.add(mpcModleProperty);
        }
        List<MPCModleProperty> inputmvproperties = Tool.getspecialpintypeBympc(MPCModleProperty.TYPE_PIN_MV, modlePropertyList);

        //输入的mv直接给到输出的mv引脚上
        for (MPCModleProperty inputmv : inputmvproperties) {
            BaseModlePropertyImp outputmv = Tool.selectmodleProperyByPinname(inputmv.getModlePinName(), propertyImpList, MPCModleProperty.PINDIROUTPUT);
            BaseModlePropertyImp outputdmv = Tool.selectmodleProperyByPinname("d" + inputmv.getModlePinName(), propertyImpList, MPCModleProperty.PINDIROUTPUT);
            if (outputmv != null) {
                outputmv.setValue(inputmv.getValue());
            }
            if (outputdmv != null) {
                outputdmv.setValue(0d);
            }
        }
        outprocess(null, null);
//        if (simulatControlModle != null) {
//            simulatControlModle.outprocess(null, null);
//        }
    }


    @Override
    public void docomputeprocess() {
        setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_RUNNING);

        HttpClientService httpClientService = getHttpClientService();
        AlgprithmApiConfig algprithmApiConfig = httpClientService.getAlgprithmApiConfig();


        DmcModleAdapter dmcModleAdapter = new DmcModleAdapter();

        DmcBasemodleparam dmcBasemodleparam = new DmcBasemodleparam();

        BaseModlePropertyImp autopin = Tool.selectmodleProperyByPinname(ModleProperty.TYPE_PIN_MODLE_AUTO, propertyImpList, ModleProperty.PINDIRINPUT);

        dmcBasemodleparam.setModelid(getModleId());
        dmcBasemodleparam.setModelname(getModleName());
        dmcBasemodleparam.setModeltype(ModelTypeEnum.MODEL_TYPE_MPC.getCode());

        dmcBasemodleparam.setAuto(autopin != null ? autopin.getValue() : 1);
        dmcBasemodleparam.setControltime_M(getControltime_M().toString());
        dmcBasemodleparam.setPredicttime_P(getPredicttime_P().toString());
        dmcBasemodleparam.setTimeserise_N(getTimeserise_N().toString());
        dmcBasemodleparam.setRunstyle(getRunstyle());
        dmcBasemodleparam.setControlapcoutcycle(getControlAPCOutCycle());

        dmcModleAdapter.setBasemodelparam(dmcBasemodleparam);

        //model response
        if (!CollectionUtils.isEmpty(responTimeSeriseList)) {
            List<DmcResponparam> dmcResponparams = new ArrayList<>();
            responTimeSeriseList.forEach(r -> {
                DmcResponparam dmcResponparam = new DmcResponparam();
                dmcResponparam.setInputpinname(r.getInputPins());
                dmcResponparam.setOutputpinname(r.getOutputPins());
                dmcResponparam.setKi(r.getStepRespJson().containsKey("Ki") ? r.getStepRespJson().getDouble("Ki") : 0);
                dmcResponparam.setK(r.getStepRespJson().getDouble("k"));
                dmcResponparam.setT(r.getStepRespJson().getDouble("t"));
                dmcResponparam.setTau(r.getStepRespJson().getDouble("tao"));
                dmcResponparams.add(dmcResponparam);
            });
            dmcModleAdapter.setModel(dmcResponparams);

        }

        // pv,mv,ff

        stringmodlePinsMap = new ConcurrentHashMap<>();

        categoryPVmodletag = new ArrayList<>();//已经分类号的PV引脚
        categorySPmodletag = new ArrayList<>();//已经分类号的SP引脚
        categoryMVmodletag = new ArrayList<>();//已经分类号的MV引脚
        categoryFFmodletag = new ArrayList<>();//已经分类号的FF引脚
        /**将引脚进行分类*/
        if (classAndCombineRegiterPinsToMap()) {
            //pv
            if (!CollectionUtils.isEmpty(categoryPVmodletag)) {
                List<Pvparam> pvparams = new ArrayList<>();
                categoryPVmodletag.forEach(pv -> {
                    Pvparam pvparam = new Pvparam();
                    pvparam.setPvpinname(pv.getModlePinName());
                    pvparam.setPvpinvalue(pv.getValue());
                    pvparam.setDeadzone(pv.getDeadZone());
                    pvparam.setFunelinitvalue(pv.getFunelinitValue());
                    pvparam.setFunneltype(pv.getFunneltype());
                    pvparam.setQ(pv.getQ());
                    pvparam.setRefertrajectorycoef(pv.getReferTrajectoryCoef());
                    pvparam.setPvuppinvalue(pv.getUpLmt() == null ? Double.MAX_VALUE : pv.getUpLmt().getValue());
                    pvparam.setPvdownpinvalue(pv.getDownLmt() == null ? Double.MIN_VALUE : pv.getDownLmt().getValue());
                    pvparam.setSppinvalue(pv.getSpPin() == null ? 0 : pv.getSpPin().getValue());
                    pvparam.setTracoefmethod(pv.getTracoefmethod());
                    pvparams.add(pvparam);
                });

                dmcModleAdapter.setPv(pvparams);
            }

            if (!CollectionUtils.isEmpty(categoryMVmodletag)) {

                List<Mvparam> mvparams = new ArrayList<>();
                categoryMVmodletag.forEach(mv -> {
                    Mvparam mvparam = new Mvparam();

                    mvparam.setMvpinname(mv.getModlePinName());
                    mvparam.setMvpinvalue(mv.getValue());
                    mvparam.setR(mv.getR());
                    mvparam.setDmvhigh(mv.getDmvHigh());
                    mvparam.setDmvlow(mv.getDmvLow());
                    mvparam.setMvuppinvalue(mv.getUpLmt().getValue());
                    mvparam.setMvdownpinvalue(mv.getDownLmt().getValue());
                    mvparam.setMvfbpinvalue(mv.getFeedBack().getValue());
                    mvparams.add(mvparam);
                });
                dmcModleAdapter.setMv(mvparams);
            }

            if (!CollectionUtils.isEmpty(categoryFFmodletag)) {
                List<Ffparam> ffparams = new ArrayList<>();
                categoryFFmodletag.forEach(ff -> {
                    Ffparam ffparam = new Ffparam();
                    ffparam.setFfpinname(ff.getModlePinName());
                    ffparam.setFfpinvalue(ff.getValue());
                    ffparam.setFfuppinvalue(ff.getUpLmt() == null ? Double.MAX_VALUE : ff.getUpLmt().getValue());
                    ffparam.setFfdownpinvalue(ff.getUpLmt() == null ? Double.MIN_VALUE : ff.getDownLmt().getValue());
                    ffparams.add(ffparam);
                });
                dmcModleAdapter.setFf(ffparams);
            }else {
                dmcModleAdapter.setFf(new ArrayList<>());
            }

            if (!CollectionUtils.isEmpty(propertyImpList)) {
                List<DmcOutproperty> dmcOutproperties = new ArrayList<>();
                propertyImpList.forEach(p -> {
                    if (((BaseModlePropertyImp) p).getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                        DmcOutproperty dmcOutproperty = new DmcOutproperty();
                        dmcOutproperty.setOutputpinname(((BaseModlePropertyImp) p).getModlePinName());
                        dmcOutproperties.add(dmcOutproperty);
                    }
                });
                dmcModleAdapter.setOutputparam(dmcOutproperties);
            }


        } else {
            setErrormsg("modle build error:\n" + "p:" + numOfRunnablePVPins_pp + " m:" + numOfRunnableMVpins_mm + " ff:" + numOfRunnableFFpins_vv);
            setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
            return;
        }

        //call
        String mpcResult = httpClientService.postForEntity(algprithmApiConfig.getUrl() + algprithmApiConfig.getDmc(), dmcModleAdapter, String.class);

        if (mpcResult == null) {
            setErrormsg("http request error");
            setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
        }
        if (!StringUtils.isEmpty(mpcResult)) {
            DmcResponse4PlantDto dmcResponse4PlantDto = JSONObject.parseObject(mpcResult, DmcResponse4PlantDto.class);
            if (dmcResponse4PlantDto.getStatus() == 200) {
                DmcData4PlantDto dmcData4PlantDto = dmcResponse4PlantDto.getData();
                if (!ObjectUtils.isEmpty(dmcData4PlantDto)) {
                    List<PinDataDto> pinDataDtos = dmcData4PlantDto.getMvData();

                    List<DmcDmvData4PlantDto> dmcDmvData4PlantDtos = dmcData4PlantDto.getDmv();

                    List<DmcPvPredictData4PlantDto> dmcPvPredictData4PlantDtos = dmcData4PlantDto.getPvpredict();


                    int p = getNumOfRunnablePVPins_pp();
                    int m = getNumOfRunnableMVpins_mm();
                    int v = getNumOfRunnableFFpins_vv();
                    int N = getTimeserise_N();

                    double[] predictpvArray = new double[p * N];
                    double[][] funelupAnddownArray = new double[2][p * N];
                    double[] eArray = new double[p];
                    double[] dmvArray = new double[m];

                    double[] dffArray = null;
                    if (v != 0) {
                        dffArray = new double[v];
                    }


                    Map<String, List<DmcPvPredictData4PlantDto>> dmcpvPredictMaps = dmcPvPredictData4PlantDtos.stream().collect(Collectors.groupingBy(DmcPvPredictData4PlantDto::getPvpinname));

                    List<String> pvoder = new ArrayList<>();
                    int indexpv=0;
                    for (int i = 1; i <= 8; i++) {
                        if (!CollectionUtils.isEmpty(dmcpvPredictMaps.get(ModleProperty.TYPE_PIN_PV + i))) {
                            pvoder.add(ModleProperty.TYPE_PIN_PV + i);
                            DmcPvPredictData4PlantDto dmcPvPredictData4PlantDto = dmcpvPredictMaps.get(ModleProperty.TYPE_PIN_PV + i).get(0);
                            for (int j = 0; j < N; j++) {
                                predictpvArray[(indexpv) * N + j] = dmcPvPredictData4PlantDto.getPredictorder()[j];
                                funelupAnddownArray[0][(indexpv) * N + j] = dmcPvPredictData4PlantDto.getUpfunnel()[j];
                                funelupAnddownArray[1][(indexpv) * N + j] = dmcPvPredictData4PlantDto.getDownfunnel()[j];
                            }
                            eArray[indexpv] = dmcPvPredictData4PlantDto.getE();
                            indexpv++;
                        }


                    }


                    Map<String, List<DmcDmvData4PlantDto>> dmcDmvData4planteDto = dmcDmvData4PlantDtos.stream().collect(Collectors.groupingBy(DmcDmvData4PlantDto::getInputpinname));
                    int j = 0;
                    List<String> mvorder = new ArrayList<>();
                    for (int i = 1; i <= 8; i++) {
                        if (!CollectionUtils.isEmpty(dmcDmvData4planteDto.get(ModleProperty.TYPE_PIN_MV + i))) {
                            mvorder.add(ModleProperty.TYPE_PIN_MV + i);
                            List<DmcDmvData4PlantDto> dmcDmvData4PlantDtoList = dmcDmvData4planteDto.get(ModleProperty.TYPE_PIN_MV + i);
                            DmcDmvData4PlantDto plantDto = dmcDmvData4PlantDtoList.get(0);
                            dmvArray[j] = plantDto.getValue();
                            j++;
                        }


                    }

                    updateModleComputeResult(predictpvArray, funelupAnddownArray, dmvArray, eArray, dffArray);
                    //pin value update
                    if (runstyle.equals(RUNSTYLEBYAUTO)) {
                        if (!CollectionUtils.isEmpty(pinDataDtos)) {

                            Map<String, List<PinDataDto>> mvs = pinDataDtos.stream().collect(Collectors.groupingBy(PinDataDto::getPinname));
                            Map<String, List<DmcDmvData4PlantDto>> dmvs = dmcDmvData4planteDto;

                            //获取出所有的输出引脚
                            List<ModleProperty> mpcOutProperties = propertyImpList.stream().filter(pro -> {
                                return ((BaseModlePropertyImp) pro).getPindir().equals(MPCModleProperty.PINDIROUTPUT);
                            }).collect(Collectors.toList());


                            //更新输出引脚输出
                            if (!CollectionUtils.isEmpty(mpcOutProperties)) {
                                for (ModleProperty modleProperty : mpcOutProperties) {
                                    BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                                    if (!CollectionUtils.isEmpty(mvs)) {
                                        if(mvs.containsKey(baseModlePropertyImp.getModlePinName())){
                                            List<PinDataDto> mvvalues = mvs.get(baseModlePropertyImp.getModlePinName());
                                            if (!CollectionUtils.isEmpty(mvvalues)) {
                                                baseModlePropertyImp.setValue(mvvalues.get(0).getValue());
                                            }else{
                                                setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
                                                setErrormsg(dmcResponse4PlantDto.getMessage());
                                                return;
                                            }
                                        }else{
                                            setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
                                            setErrormsg(dmcResponse4PlantDto.getMessage());
                                        }
                                    }
                                }

                            }
                            setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE);
                            setErrormsg(dmcResponse4PlantDto.getMessage());


                        }else{
                            setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
                        }
                    }


                }else{
                    setErrormsg("http request error");
                    setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
                }


            }else{
                setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
                setErrormsg(dmcResponse4PlantDto.getMessage());
            }
        } else {
            setErrormsg("http request error");
            setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_INITE);
        }


    }

    @Override
    public JSONObject inprocess(Project project) {
        setBeginruntime(Instant.now());

        for (ModleProperty modleProperty : propertyImpList) {
            MPCModleProperty mpcinproperty = (MPCModleProperty) modleProperty;
            if (mpcinproperty.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                if (mpcinproperty.getResource().getString("resource").equals(ModleProperty.SOURCE_TYPE_CONSTANT)) {
                    mpcinproperty.setValue(mpcinproperty.getResource().getDouble("value"));
                } else if (mpcinproperty.getResource().getString("resource").equals(ModleProperty.SOURCE_TYPE_MODLE)) {
                    int modleId = mpcinproperty.getResource().getInteger("modleId");
                    int modlepinsId = mpcinproperty.getResource().getInteger("modlepinsId");

                    Modle modle = project.getIndexmodles().get(modleId);
                    if (modle != null) {
                        if (modle instanceof MPCModle) {
                            MPCModle mpcModle = (MPCModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = mpcModle.getIndexproperties().get(modlepinsId);
                            mpcinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof PIDModle) {
                            PIDModle pidModle = (PIDModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = pidModle.getIndexproperties().get(modlepinsId);
                            mpcinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof CUSTOMIZEModle) {
                            CUSTOMIZEModle customizeModle = (CUSTOMIZEModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = customizeModle.getIndexproperties().get(modlepinsId);
                            mpcinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof FilterModle) {
                            FilterModle filterModle = (FilterModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = filterModle.getIndexproperties().get(modlepinsId);
                            mpcinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof INModle) {
                            INModle inModle = (INModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = inModle.getIndexproperties().get(modlepinsId);
                            mpcinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof OUTModle) {
                            OUTModle outModle = (OUTModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = outModle.getIndexproperties().get(modlepinsId);
                            mpcinproperty.setValue(baseModlePropertyImp.getValue());
                        }

                    }

                }

            }
        }
        return null;
    }

    @Override
    public JSONObject computresulteprocess(Project project, JSONObject computedata) {
        return null;
    }

    @Override
    public void outprocess(Project project, JSONObject outdata) {
        setModlerunlevel(ModelRunStatusEnum.MODEL_RUN_STATUS_COMPELTE);
        //activetime
        setActivetime(Instant.now());
    }

    @Override
    public void init() {
        setIndexproperties(new HashMap<>());
        for (ModleProperty modleProperty : propertyImpList) {
            MPCModleProperty mpcModleProperty = (MPCModleProperty) modleProperty;
            getIndexproperties().put(mpcModleProperty.getModlepinsId(), mpcModleProperty);
        }
    }


    /**
     * 1、引脚注册进引脚的map中，key=pvn/mvn/spn等(n=1,2,3..8) value=pin
     * 2将引脚进行分类，按照顺序单独存储到各自类别的list，ex:categoryPVmodletag内存储pv的引脚
     *
     * @return false 对应位号不完整
     */
    private boolean classAndCombineRegiterPinsToMap() {
        /**将定义的引脚按照key=pvn/mvn/spn等(n=1,2,3..8) value=pin 放进索引*/
        if (propertyImpList.size() == 0) {
            return false;
        }
        for (ModleProperty baseModlePropertyImp : propertyImpList) {
            MPCModleProperty modlePin = (MPCModleProperty) baseModlePropertyImp;
            if (modlePin.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                stringmodlePinsMap.put(modlePin.getModlePinName(), modlePin);
            }

        }

        /**分类引脚*/
        for (int i = 1; i <= totalPv; i++) {
            MPCModleProperty pvPin = stringmodlePinsMap.get(ModleProperty.TYPE_PIN_PV + i);
            if (pvPin != null) {
                categoryPVmodletag.add(pvPin);
                MPCModleProperty dcsEnablepin = stringmodlePinsMap.get(ModleProperty.TYPE_PIN_PIN_PVENABLE + i);
                if (dcsEnablepin != null) {
                    pvPin.setDcsEnabePin(dcsEnablepin);//dcs作用用于模型是否启用,已经弃用
                }

                MPCModleProperty pvdown = stringmodlePinsMap.get(ModleProperty.TYPE_PIN_PVDOWN + i);
                MPCModleProperty pvup = stringmodlePinsMap.get(ModleProperty.TYPE_PIN_PVUP + i);
                /**没有的话也不强制退出*/
                if ((null == pvdown) || (null == pvup)) {
                    logger.warn("modleid=" + getModleId() + ",存在pv的置信区间不完整");
                }
                pvPin.setDownLmt(pvdown);//置信区间下限值位号
                pvPin.setUpLmt(pvup);//置信区间上限值位号


                MPCModleProperty spPin = stringmodlePinsMap.get(ModleProperty.TYPE_PIN_SP + i);//目标值sp位号
                if (spPin != null) {
                    pvPin.setSpPin(spPin);
                    categorySPmodletag.add(spPin);
                } else {
                    return false;
                }
            }
        }

        /**
         *ff
         * */
        for (int i = 1; i <= totalFf; ++i) {
            MPCModleProperty ffPin = stringmodlePinsMap.get(ModleProperty.TYPE_PIN_FF + i);

            if (ffPin != null) {
                MPCModleProperty dcsEnablepin = stringmodlePinsMap.get(ModleProperty.TYPE_PIN_PIN_FFENABLE + i);
                if (dcsEnablepin != null) {
                    ffPin.setDcsEnabePin(dcsEnablepin);//dcs作用用于模型是否启用
                }
                MPCModleProperty ffdown = stringmodlePinsMap.get(ModleProperty.TYPE_PIN_FFDOWN + i);
                MPCModleProperty ffup = stringmodlePinsMap.get(ModleProperty.TYPE_PIN_FFUP + i);
                ffPin.setDownLmt(ffdown);//置信区间下限值位号
                ffPin.setUpLmt(ffup);//置信区间上限值位号
                categoryFFmodletag.add(ffPin);
                if ((null == ffdown) || (null == ffup)) {
                    logger.warn("modleid=" + getModleId() + ",存在ff的置信区间不完整");
                }
            }

        }

        /**
         * mv mvfb,mvdown mvup
         * */
        for (int i = 1; i < totalMv; i++) {
            MPCModleProperty mvPin = stringmodlePinsMap.get(ModleProperty.TYPE_PIN_MV + i);

            if (mvPin != null) {

                MPCModleProperty dcsEnablepin = stringmodlePinsMap.get(ModleProperty.TYPE_PIN_PIN_MVENABLE + i);
                if (dcsEnablepin != null) {
                    mvPin.setDcsEnabePin(dcsEnablepin);//dcs作用用于模型是否启用
                }

                MPCModleProperty mvfbPin = stringmodlePinsMap.get(ModleProperty.TYPE_PIN_MVFB + i);
                MPCModleProperty mvupPin = stringmodlePinsMap.get(ModleProperty.TYPE_PIN_MVUP + i);
                MPCModleProperty mvdownPin = stringmodlePinsMap.get(ModleProperty.TYPE_PIN_MVDOWN + i);
                if (mvfbPin != null && mvupPin != null && mvdownPin != null) {
                    mvPin.setUpLmt(mvupPin);
                    mvPin.setDownLmt(mvdownPin);
                    mvPin.setFeedBack(mvfbPin);
                    categoryMVmodletag.add(mvPin);
                } else {
                    return false;
                }
            }
        }


        /**init pvusemv and pvuseff matrix
         * 数据库配配置的pv和mv/ff映射关系
         * */
        setMaskBaseMapPvUseMvMatrix(new int[getCategoryPVmodletag().size()][getCategoryMVmodletag().size()]);
        setMaskBaseMapPvUseFfMatrix(new int[getCategoryPVmodletag().size()][getCategoryFFmodletag().size()]);

        /**pv对mv的作用关系*/
        setMaskBaseMapPvEffectMvMatrix(new float[getCategoryPVmodletag().size()][getCategoryMVmodletag().size()]);

        setMaskisRunnableMVMatrix(new int[getCategoryMVmodletag().size()]);
        setMaskisRunnableFFMatrix(new int[getCategoryFFmodletag().size()]);
        setMaskisRunnablePVMatrix(new int[getCategoryPVmodletag().size()]);

        initRunnableMatrixAndBaseMapMatrix(this);

        //本次可运行的pv数量
        setNumOfRunnablePVPins_pp(0);
        //本次可运行的mv数量
        setNumOfRunnableMVpins_mm(0);
        //本次可运行的ff数量
        setNumOfRunnableFFpins_vv(0);


        initStatisticRunnablePinNum();
        //没有可以运行的引脚直接不需要进行build了
        if (getNumOfRunnablePVPins_pp() == 0 || getNumOfRunnableMVpins_mm() == 0) {
            log.error("p=" + 0 + ",m=" + 0);
            return false;
        }


        setA_RunnabletimeseriseMatrix(new double[getNumOfRunnablePVPins_pp()][getNumOfRunnableMVpins_mm()][getTimeserise_N()]);
        //mv的积分增量
         setRunnableintegrationInc_mv(new double[ getNumOfRunnablePVPins_pp()][ getNumOfRunnableMVpins_mm()]);


        //可运行的pv和mv的作用比例矩阵
        setMaskMatrixRunnablePvEffectMv(new float[getNumOfRunnablePVPins_pp()][ getNumOfRunnableMVpins_mm()]);


        /***
         *1、fill respon into A matrix
         *2、and init matrixEnablePVUseMV
         * */

        /**predict zone params*/
        setQ(new Double[ getNumOfRunnablePVPins_pp()]);//use for pv
        /**trajectry coefs*/
         setAlpheTrajectoryCoefficients(new Double[ getNumOfRunnablePVPins_pp()]);//use for pv
         setAlpheTrajectoryCoefmethod(new String[ getNumOfRunnablePVPins_pp()]);
        /**死区时间和漏洞初始值*/
         setDeadZones(new Double[ getNumOfRunnablePVPins_pp()]);//use for pv
         setFunelinitvalues(new Double[ getNumOfRunnablePVPins_pp()]);//use for pv
        /**funnel type*/
         setFunneltype(new double[ getNumOfRunnablePVPins_pp()][2]);//use for pv

        setRunnableintegrationInc_mv(new double[getNumOfRunnablePVPins_pp()][getNumOfRunnableMVpins_mm()]);

         setMaskMatrixRunnablePVUseMV(new int[ getNumOfRunnablePVPins_pp()][ getNumOfRunnableMVpins_mm()]);//recording enablepv use which mvs

        initPVparams(this);


        setB_RunnabletimeseriseMatrix(new double[getNumOfRunnablePVPins_pp()][getNumOfRunnableFFpins_vv()][getTimeserise_N()]);
        setRunnableintegrationInc_ff(new double[getNumOfRunnablePVPins_pp()][getNumOfRunnableFFpins_vv()]);//ff的积分增量


        setMaskMatrixRunnablePVUseFF(new int[getNumOfRunnablePVPins_pp()][getNumOfRunnableFFpins_vv()]);

        initFFparams(this);



        setBackPVPrediction(new double[getNumOfRunnablePVPins_pp()][getTimeserise_N()]);//pv的预测曲线

        setBackPVFunelUp(new double[getNumOfRunnablePVPins_pp()][getTimeserise_N()]);//PV的漏斗上曲线

        setBackPVFunelDown(new double[getNumOfRunnablePVPins_pp()][getTimeserise_N()]);//漏斗下曲线

        setBackDmvWrite(new double[getNumOfRunnablePVPins_pp()][getNumOfRunnableMVpins_mm()]);//MV写入值
        setBackrawDmv(new double[getNumOfRunnableMVpins_mm()]);
        setBackrawDff(new double[getNumOfRunnableFFpins_vv()]);

        setBackPVPredictionError(new double[getNumOfRunnablePVPins_pp()]);//预测误差

        setBackDff(new double[getNumOfRunnablePVPins_pp()][getNumOfRunnableFFpins_vv()]);//前馈变换值


        return true;
    }


    /**
     * 初始化本次可运行的pv、mv、ff的引脚应用情况，如果在对应位置写1则说明本次参与运行
     * 另外一个是初始化数据库中配置了pv对应了那些个mv，pv对应了哪些ff
     * maskRunnablePVMatrix  maskRunnableMVMatrix  maskRunnableFFMatrix maskBaseMapPvEffectMvMatrix
     */
    private void initRunnableMatrixAndBaseMapMatrix(MPCModle mpcModel) {
        for (int indexpv = 0; indexpv < mpcModel.getCategoryPVmodletag().size(); ++indexpv) {


            /**1\marker total pvusemv
             * 2\marker participate mv
             * */
            for (int indexmv = 0; indexmv < mpcModel.getCategoryMVmodletag().size(); ++indexmv) {
                ResponTimeSerise ismapping = isPVMappingMV(mpcModel, mpcModel.getCategoryPVmodletag().get(indexpv).getModlePinName(), mpcModel.getCategoryMVmodletag().get(indexmv).getModlePinName());
                mpcModel.getMaskBaseMapPvUseMvMatrix()[indexpv][indexmv] = (null != ismapping ? 1 : 0);
                mpcModel.getMaskBaseMapPvEffectMvMatrix()[indexpv][indexmv] = (null != ismapping ? ismapping.getEffectRatio() : 0f);


                /**pv引脚启用，并且参与本次控制*/
                if ((null != ismapping) && isThisTimeRunnablePin(mpcModel.getCategoryPVmodletag().get(indexpv))) {
                    mpcModel.getMaskisRunnablePVMatrix()[indexpv] = 1;
                }

                /**1是否有映射关系、2、pv是否启用 3mv是否启用*/
                if ((null != ismapping) && isThisTimeRunnablePin(mpcModel.getCategoryPVmodletag().get(indexpv)) && isThisTimeRunnablePin(mpcModel.getCategoryMVmodletag().get(indexmv))) {
                    mpcModel.getMaskisRunnableMVMatrix()[indexmv] = 1;
                }

            }

            /**1\marker total pvuseff
             * 2\marker participate ff
             * */
            for (int indexff = 0; indexff < mpcModel.getCategoryFFmodletag().size(); ++indexff) {
                ResponTimeSerise ismapping = isPVMappingFF(mpcModel, mpcModel.getCategoryPVmodletag().get(indexpv).getModlePinName(), mpcModel.getCategoryFFmodletag().get(indexff).getModlePinName());
                mpcModel.getMaskBaseMapPvUseFfMatrix()[indexpv][indexff] = (null != ismapping ? 1 : 0);
                if ((null != ismapping) && isThisTimeRunnablePin(mpcModel.getCategoryPVmodletag().get(indexpv)) && isThisTimeRunnablePin(mpcModel.getCategoryFFmodletag().get(indexff))) {
                    mpcModel.getMaskisRunnableFFMatrix()[indexff] = 1;
                }
            }
        }
    }

    /**
     * 1统计参与本次runnbale的pv
     * 2统计runnbale的pv的 runnbale mv的数量
     * 3统计参与runnbale的pv的runnbale ff的数量
     */
    private void initStatisticRunnablePinNum() {

        /**统计runnbale的pv的mv的数量*/
        for (int pvi : maskisRunnablePVMatrix) {
            if (1 == pvi) {
                ++numOfRunnablePVPins_pp;
            }
        }

        /**统计runnbale的pv的mv的数量*/
        for (int mvi : maskisRunnableMVMatrix) {
            if (1 == mvi) {
                ++numOfRunnableMVpins_mm;
            }
        }
        /**统计runnbale的pv的ff的数量*/
        for (int ffi : maskisRunnableFFMatrix) {
            if (1 == ffi) {
                ++numOfRunnableFFpins_vv;
            }
        }

    }


    /**
     * 初始化pv有关的参数
     * Q预测域参数
     * alpheTrajectoryCoefficients轨迹软化系统
     * deadZones死区
     * funelinitvalues漏斗初始值
     * funneltype漏斗类型
     **/


    /**
     * 累计可运行的pv和mv的映射关系数量
     */
    private void accumulativeNumOfRunnablePVMVMaping() {
        for (int p = 0; p < numOfRunnablePVPins_pp; ++p) {
            for (int m = 0; m < numOfRunnableMVpins_mm; ++m) {
                if (1 == maskMatrixRunnablePVUseMV[p][m]) {
                    simulatControlModle.addNumOfIOMappingRelation();
                }
            }

        }
    }


    public void getMVRelationData(JSONObject jsonObject) {

        /**limitU输入限制 mv上下限*/
        Double[][] limitU = new Double[numOfRunnableMVpins_mm][2];
        Double[][] limitDU = new Double[numOfRunnableMVpins_mm][2];
        /**U执行器当前给定*/
        Double[] U = new Double[numOfRunnableMVpins_mm];
        /**U执行器当前反馈**/
        Double[] UFB = new Double[numOfRunnableMVpins_mm];

        int indexEnableMV = 0;
        for (int indexmv = 0; indexmv < categoryMVmodletag.size(); ++indexmv) {
            if (maskisRunnableMVMatrix[indexmv] == 0) {
                continue;
            }
            Double[] mvminmax = new Double[2];
            MPCModleProperty mvdown = categoryMVmodletag.get(indexmv).getDownLmt();
            MPCModleProperty mvup = categoryMVmodletag.get(indexmv).getUpLmt();

            mvminmax[0] = mvdown.getValue();

            mvminmax[1] = mvup.getValue();

            //执行器限制
            limitU[indexEnableMV] = mvminmax;

            Double[] dmvminmax = new Double[2];
            dmvminmax[0] = categoryMVmodletag.get(indexmv).getDmvLow();
            dmvminmax[1] = categoryMVmodletag.get(indexmv).getDmvHigh();
            limitDU[indexEnableMV] = dmvminmax;

            //执行器给定
            U[indexEnableMV] = categoryMVmodletag.get(indexmv).getValue();
            UFB[indexEnableMV] = categoryMVmodletag.get(indexmv).getFeedBack().getValue();
            indexEnableMV++;

        }
        jsonObject.put("origionstructlimitmv", limitU);
        jsonObject.put("origionstructlimitDmv", limitDU);
        jsonObject.put("origionstructmv", U);
        jsonObject.put("originstructmvfb", UFB);
    }


    public void getPVRealData(JSONObject jsonObject) {
        Double[] pv = new Double[numOfRunnablePVPins_pp];
        int indexEnablePV = 0;
        for (int indexpv = 0; indexpv < categoryPVmodletag.size(); ++indexpv) {
            if (maskisRunnablePVMatrix[indexpv] == 0) {
                continue;
            }
            /***
             * 是否有滤波器，有则使用滤波器的值，不然就使用实时数据
             * */
            pv[indexEnablePV] = categoryPVmodletag.get(indexpv).getValue();
            ++indexEnablePV;
        }
        jsonObject.put("origiony0", pv);
    }


    /**
     * 判断引脚是否启用，并且本次参与控制
     */
    private boolean isThisTimeRunnablePin(MPCModleProperty pin) {
        /**启用，并且本次参与控制*/
        if ((1 == pin.getPinEnable() && (pin.isThisTimeParticipate()))) {
            return true;
        }
        return false;
    }

    /**
     * pv与mv是否有映射关系
     **/
    private ResponTimeSerise isPVMappingMV(MPCModle mpcModel, String pvpin, String mvpin) {

        for (ResponTimeSerise responTimeSerise : mpcModel.getResponTimeSeriseList()) {
            if (
                    responTimeSerise.getInputPins().equals(mvpin)
                            &&
                            responTimeSerise.getOutputPins().equals(pvpin)
            ) {
                return responTimeSerise;
            }
        }
        return null;
    }

    /**
     * pv与ff是否有映射关系
     */
    private ResponTimeSerise isPVMappingFF(MPCModle mpcModel, String pvpin, String ffpin) {

        for (ResponTimeSerise responTimeSerise : mpcModel.getResponTimeSeriseList()) {
            if (
                    responTimeSerise.getInputPins().equals(ffpin)
                            &&
                            responTimeSerise.getOutputPins().equals(pvpin)
            ) {
                return responTimeSerise;
            }

        }
        return null;
    }


    /**
     * 更新模型计算后的数据
     *
     * @param funelupAnddown   尺寸：2XpN
     *                         第0行存漏斗的上限；[0~N-1]第一个pv的，[N~2N-1]为第二个pv的漏斗上限
     *                         第1行存漏斗的下限；
     * @param backPVPrediction
     */
    public boolean updateModleComputeResult(double[] backPVPrediction, double[][] funelupAnddown, double[] backDmvWrite, double[] backPVPredictionError, double[] dff) {
        /**
         * 模型运算状态值
         * */
        try {
            for (int i = 0; i < numOfRunnablePVPins_pp; i++) {
                this.backPVPrediction[i] = Arrays.copyOfRange(backPVPrediction, 0 + timeserise_N * i, timeserise_N + timeserise_N * i);//pv的预测曲线
                this.backPVFunelUp[i] = Arrays.copyOfRange(funelupAnddown[0], 0 + timeserise_N * i, timeserise_N + timeserise_N * i);//PV的漏斗上限
                this.backPVFunelDown[i] = Arrays.copyOfRange(funelupAnddown[1], 0 + timeserise_N * i, timeserise_N + timeserise_N * i);//PV的漏斗下限
            }

            /**预测误差*/
            this.backPVPredictionError = backPVPredictionError;
            this.backrawDmv = backDmvWrite;
            this.backrawDff = dff;
            for (int indexpv = 0; indexpv < numOfRunnablePVPins_pp; indexpv++) {

                /**dMV写入值*/
                for (int indexmv = 0; indexmv < numOfRunnableMVpins_mm; indexmv++) {
                    if (maskMatrixRunnablePVUseMV[indexpv][indexmv] == 1) {
                        this.backDmvWrite[indexpv][indexmv] = backDmvWrite[indexmv];
                    }
                }

                /**前馈增量*/
                for (int indexff = 0; indexff < numOfRunnableFFpins_vv; indexff++) {
                    if (maskMatrixRunnablePVUseFF[indexpv][indexff] == 1) {
                        this.backDff[indexpv][indexff] = dff[indexff];
                    }
                }

            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }


    public List<MPCModleProperty> getRunablePins(List<MPCModleProperty> categorypins, int[] maskmatrix) {
        List<MPCModleProperty> result = new LinkedList<>();
        for (int indexpin = 0; indexpin < categorypins.size(); ++indexpin) {
            if (1 == maskmatrix[indexpin]) {
                result.add(categorypins.get(indexpin));
            }
        }
        return result;
    }


    private void initPVparams(MPCModle mpcModel) {

        List<MPCModleProperty> runablePVPins = getRunablePins(mpcModel.getCategoryPVmodletag(), mpcModel.getMaskisRunnablePVMatrix());
        List<MPCModleProperty> runableMVPins = getRunablePins(mpcModel.getCategoryMVmodletag(), mpcModel.getMaskisRunnableMVMatrix());

        int looppv = 0;
        for (MPCModleProperty runpvpin : runablePVPins) {
            mpcModel.getQ()[looppv] = runpvpin.getQ();
            mpcModel.getAlpheTrajectoryCoefficients()[looppv] = runpvpin.getReferTrajectoryCoef();
            mpcModel.getAlpheTrajectoryCoefmethod()[looppv] = runpvpin.getTracoefmethod();
            mpcModel.getDeadZones()[looppv] = runpvpin.getDeadZone();
            mpcModel.getFunelinitvalues()[looppv] = runpvpin.getFunelinitValue();
            double[] fnl = new double[2];



            int loopmv = 0;
            for (MPCModleProperty runmvpin : runableMVPins) {

                /**查找映射关系*/
                ResponTimeSerise responTimeSerisePVMV = isPVMappingMV(mpcModel, runpvpin.getModlePinName(), runmvpin.getModlePinName());
                if (responTimeSerisePVMV != null) {
                    mpcModel.getA_RunnabletimeseriseMatrix()[looppv][loopmv] = responTimeSerisePVMV.responOneTimeSeries(mpcModel.getTimeserise_N(), mpcModel.getControlAPCOutCycle());
                    mpcModel.getMaskMatrixRunnablePVUseMV()[looppv][loopmv] = 1;
                    mpcModel.getMaskMatrixRunnablePvEffectMv()[looppv][loopmv] = responTimeSerisePVMV.getEffectRatio();
//                    mpcModel.getRunnableintegrationInc_mv()[looppv][loopmv] = responTimeSerisePVMV.getStepRespJson().getDouble("Ki");
                }

                ++loopmv;
            }

            ++looppv;
        }

    }

    private void initFFparams(MPCModle mpcModel) {

        List<MPCModleProperty> runablePVPins = getRunablePins(mpcModel.getCategoryPVmodletag(), mpcModel.getMaskisRunnablePVMatrix());//获取可运行的pv引脚
        List<MPCModleProperty> runableFFPins = getRunablePins(mpcModel.getCategoryFFmodletag(), mpcModel.getMaskisRunnableFFMatrix());//获取可运行的ff引脚

        int looppv = 0;
        for (MPCModleProperty runpv : runablePVPins) {

            int loopff = 0;
            for (MPCModleProperty runff : runableFFPins) {

                ResponTimeSerise responTimeSerisePVFF = isPVMappingFF(mpcModel, runpv.getModlePinName(), runff.getModlePinName());

                if (responTimeSerisePVFF != null) {
                    mpcModel.getB_RunnabletimeseriseMatrix()[looppv][loopff] = responTimeSerisePVFF.responOneTimeSeries(mpcModel.getTimeserise_N(), mpcModel.getControlAPCOutCycle());
                    mpcModel.getMaskMatrixRunnablePVUseFF()[looppv][loopff] = 1;
//                    mpcModel.getRunnableintegrationInc_ff()[looppv][loopff] = responTimeSerisePVFF.getStepRespJson().getDouble("Ki");
                }
                ++loopff;
            }
            ++looppv;
        }
    }



    /****db****/
    private Integer predicttime_P;//预测时域
    private Integer controltime_M;//单一控制输入未来控制M步增量(控制域)
    private Integer timeserise_N;//响应序列长度
    private Integer controlAPCOutCycle;//控制周期
    private Integer runstyle = 0;//运行方式0-自动分配模式 1-手动分配模式
    /**************/

    private List<ModleProperty> propertyImpList;
    private List<ResponTimeSerise> responTimeSeriseList;


    /*********/


}
