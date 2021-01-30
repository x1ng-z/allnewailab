package hs.industry.ailab.entity.modle.controlmodle;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.ResponTimeSerise;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.entity.modle.customizemodle.CUSTOMIZEModle;
import hs.industry.ailab.entity.modle.filtermodle.FilterModle;
import hs.industry.ailab.entity.modle.iomodle.INModle;
import hs.industry.ailab.entity.modle.iomodle.OUTModle;
import hs.industry.ailab.entity.modle.modlerproerty.MPCModleProperty;
import hs.industry.ailab.pydriver.command.CommandImp;
import hs.industry.ailab.pydriver.session.PySession;
import hs.industry.ailab.pydriver.session.PySessionManager;
import hs.industry.ailab.utils.bridge.ExecutePythonBridge;
import hs.industry.ailab.utils.help.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/8 16:50
 */
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
    private Map<Integer, MPCModleProperty> indexproperties;//key=modleid
    private PySessionManager pySessionManager;
    private ExecutePythonBridge mpcexecutepythonbridge;
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
    private ExecutePythonBridge simulateexecutePythonBridge;
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
    public void toBeRealModle(int controlAPCOutCycle, String mpcscript, String simulatorscript, int mpcpinnumber, PySessionManager pySessionManager, String nettyport, String pyproxyexecute) {
//        this.opcServicConstainer = opcServicConstainer;
//        this.baseConf = baseConf;
//        this.simulatorbuilddir = simulatordir;
        /***
         * 可以新建的引脚的数量
         * */
        this.totalFf = mpcpinnumber;//baseConf.getFf();
        this.totalMv = mpcpinnumber;// baseConf.getMv();
        this.totalPv = mpcpinnumber;//baseConf.getPv();
//        this.apcdir=apcdir;
        this.mpcscript = mpcscript;
        this.simulatorscript = simulatorscript;
        this.port = nettyport;
        this.pySessionManager = pySessionManager;
        this.pyproxyexecute = pyproxyexecute;
        this.controlAPCOutCycle = controlAPCOutCycle;
    }

    /**
     * 需要判断mpc/simulator是否都运行成功
     */
    public boolean ismpcmodleruncomplet() {
        /**
         * 函数思路 注意mpc javabuild和simultor的javabuild肯定是一起的
         * mpc运行成功可能是
         * 1、他就是正常运行成功
         * 2、build出错：
         *      2.1mpcjavabuild出错，导致mpc模型短路，这时候就不要去判断simulator的输出了（模型直接短路,输入的mv直接幅值给输出的mv，变成完成状态）
         *      2.2simulatorbuild出错，导致simulator为空，这时候虽然mpcjavabuild完成，由于simultor为空，模型直接短路（输入的mv直接幅值给输出的mv，变成完成状态）
         * */
        if (getModlerunlevel() == BaseModleImp.RUNLEVEL_RUNCOMPLET) {
            //判断下mpc完成状态下，simultor模型是否完成模型构建
            if (javabuildcomplet) {
                //mpc的javabuild完成，那么就要要求simulator的也javabuild完成，否则就是simulator的python数据肯定还没有计算完成
                if ((simulatControlModle != null) && (simulatControlModle.isJavabuildcomplet()) && simulatControlModle.getModlerunlevel() == BaseModleImp.RUNLEVEL_RUNCOMPLET) {
                    //simulator的javabuild成功，且运行完成
                    return true;
                } else {
                    //simulator的javabuild出错 模型整体被短路
                    return false;
                }
            } else {
                //mpc javabuild有问题，模型短路，那么就要直接判断模型已经运行成功了
                return true;
            }

        } else {
            //模型未完成运算
            return false;
        }
    }


    @Override
    public void connect() {
        mpcexecutepythonbridge.execute();
        simulateexecutePythonBridge.execute();
//        PySession mpcpySession = pySessionManager.getSpecialSession(getModleId(), mpcscript);
//        PySession simulatepySession = pySessionManager.getSpecialSession(getModleId(), simulatorscript);
//        while (mpcpySession == null || simulatepySession == null) {
////            logger.info("try");
//            try {
//                TimeUnit.MILLISECONDS.sleep(500);
//            } catch (InterruptedException e) {
//                logger.error(e.getMessage(),e);
//            }
//
//
//        }


    }

    /**
     * 确保python模型已经连接
     */
    @Override
    public void reconnect() {
        setJavabuildcomplet(false);
        pythonbuildcomplet = false;
        if (simulatControlModle != null) {
            simulatControlModle.setJavabuildcomplet(false);
            simulatControlModle.setPythonbuildcomplet(false);
        }

        PySession mpcpySession = pySessionManager.getSpecialSession(getModleId(), mpcscript);
        if (mpcpySession != null) {
            JSONObject json = new JSONObject();
            json.put("msg", "stop");
            try {
                mpcpySession.getCtx().writeAndFlush(CommandImp.STOP.build(json.toJSONString().getBytes("utf-8"), getModleId()));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
            pySessionManager.removeSessionModule(mpcpySession.getCtx()).getCtx().close();
        }

        PySession simulatepySession = pySessionManager.getSpecialSession(getModleId(), simulatorscript);
        if (simulatepySession != null) {
            JSONObject json = new JSONObject();
            json.put("msg", "stop");
            try {
                simulatepySession.getCtx().writeAndFlush(CommandImp.STOP.build(json.toJSONString().getBytes("utf-8"), getModleId()));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
            pySessionManager.removeSessionModule(simulatepySession.getCtx()).getCtx().close();
        }

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }

        mpcexecutepythonbridge.stop();
        simulateexecutePythonBridge.stop();

        mpcexecutepythonbridge.execute();
        simulateexecutePythonBridge.execute();

        mpcpySession = pySessionManager.getSpecialSession(getModleId(), mpcscript);
        simulatepySession = pySessionManager.getSpecialSession(getModleId(), simulatorscript);
        int trycheckcount=10;
        while ((trycheckcount-->0)&&(mpcpySession == null || simulatepySession == null)) {
            //等待连接上来
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
            mpcpySession = pySessionManager.getSpecialSession(getModleId(), mpcscript);
            simulatepySession = pySessionManager.getSpecialSession(getModleId(), simulatorscript);
        }


        //模型构建
        if (false && modleBuild(false)) {
            //java 模型构建
            JSONObject mpcmodlebuild = dataForpythonBuildmpc();
            JSONObject simulatemodlebuild = simulatControlModle.dataforpythonbuildsimulate();
            try {
                pythonbuildcomplet = false;
                simulatControlModle.setPythonbuildcomplet(false);

                mpcpySession.getCtx().writeAndFlush(CommandImp.PARAM.build(mpcmodlebuild.toJSONString().getBytes("utf-8"), getModleId()));

                simulatepySession.getCtx().writeAndFlush(CommandImp.PARAM.build(simulatemodlebuild.toJSONString().getBytes("utf-8"), getModleId()));

            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
            //mpc pythonmodle 构架未完成，且在线
            while (!pythonbuildcomplet && pySessionManager.getSpecialSession(getModleId(), mpcscript) != null) {
                int i = 3;
                while (i-- > 0) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                    if (pythonbuildcomplet) {
                        break;
                    }
                }
                if (!pythonbuildcomplet) {
                    mpcmodlebuild = dataForpythonBuildmpc();
                    try {
                        mpcpySession.getCtx().writeAndFlush(CommandImp.PARAM.build(mpcmodlebuild.toJSONString().getBytes("utf-8"), getModleId()));
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e.getMessage(), e);
                    }

                }
            }

            //simulatepython 模型构建完成且在线
            while (!simulatControlModle.isPythonbuildcomplet() && pySessionManager.getSpecialSession(getModleId(), simulatorscript) != null) {
                int i = 3;
                while (i-- > 0) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                    if (simulatControlModle.isPythonbuildcomplet()) {
                        break;
                    }
                }
                if (!simulatControlModle.isPythonbuildcomplet()) {
                    simulatemodlebuild = simulatControlModle.dataforpythonbuildsimulate();
                    try {
                        simulatepySession.getCtx().writeAndFlush(CommandImp.PARAM.build(simulatemodlebuild.toJSONString().getBytes("utf-8"), getModleId()));
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e.getMessage(), e);
                    }

                }
            }

        }
    }

    @Override
    public void destory() {
        PySession mpcpySession = pySessionManager.getSpecialSession(getModleId(), mpcscript);
        if (mpcpySession != null) {
            JSONObject json = new JSONObject();
            json.put("msg", "stop");
            try {
                mpcpySession.getCtx().writeAndFlush(CommandImp.STOP.build(json.toJSONString().getBytes("utf-8"), getModleId()));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
            pySessionManager.removeSessionModule(mpcpySession.getCtx()).getCtx().close();
        }
        mpcexecutepythonbridge.stop();


        PySession simulatepySession = pySessionManager.getSpecialSession(getModleId(), simulatorscript);
        if (simulatepySession != null) {
            JSONObject json = new JSONObject();
            json.put("msg", "stop");
            try {
                simulatepySession.getCtx().writeAndFlush(CommandImp.STOP.build(json.toJSONString().getBytes("utf-8"), getModleId()));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
            pySessionManager.removeSessionModule(simulatepySession.getCtx()).getCtx().close();
        }

        simulateexecutePythonBridge.stop();


    }


    /**
     * 0判断是pv、ff类型的引脚是否越界还是已经恢复
     * 1-1、如果是则设置模型引脚停运，新建一个引脚停止运行的任务
     * 2运行
     * 2-1设置引脚运行,设置引脚，并设置引脚下次正真参与控制的时间
     */
    private void checkmodlepinisinLimit(List<ModleProperty> pins) {
        /**引脚类型判断，筛选出pv或者ff引脚，这里限制了pv和ff这个范围，因为如果是mv也是有上下限的，二mv是不需要通过这个进行设置引脚运行还是停止*/
        boolean ishavebreakOrRestorepin = false;
        for (ModleProperty pin : pins) {
            MPCModleProperty mpcModleProperty = (MPCModleProperty) pin;
            boolean ispvpin = (mpcModleProperty.getPintype() != null) && (mpcModleProperty.getPintype().equals(MPCModleProperty.TYPE_PIN_PV));
            boolean isffpin = (mpcModleProperty.getPintype() != null) && (mpcModleProperty.getPintype().equals(MPCModleProperty.TYPE_PIN_FF));
            if (ispvpin || isffpin) {
                /**模型引脚停止*/
                /**
                 * 判断是否超过置信区间
                 * */
                if (mpcModleProperty.isBreakLimit()) {
                    /**突破边界*/
                    if (mpcModleProperty.isThisTimeParticipate()) {
                        /*参与控制了，停止他*/
                        mpcModleProperty.setThisTimeParticipate(false);
                        ishavebreakOrRestorepin = true;//越界了
                    }

                } else {
                    /**在边界内*/
                    if (mpcModleProperty.isThisTimeParticipate()) {
                        /*参与控制*/
                        if (null != mpcModleProperty.getRunClock()) {
                            /*闹铃时间到了吗*/
                            if (mpcModleProperty.clockAlarm()) {
                                ishavebreakOrRestorepin = true;//恢复了
                                mpcModleProperty.clearRunClock();
                            }
                        }
                    } else {
                        /*没参与控制,设立闹钟*/
                        mpcModleProperty.setThisTimeParticipate(true);
                        int checktime = 10;
                        /*如果模型的输出周期为null/0，则直接设置引脚保持在置信区间为10s*/
                        if (getControlAPCOutCycle() != null && getControlAPCOutCycle() != 0) {
                            checktime = 3 * getControlAPCOutCycle();
                        }
                        mpcModleProperty.setRunClock(Instant.now().plusSeconds(checktime));

                    }


                }

            }

        }

        if (ishavebreakOrRestorepin) {
            logger.info("some pin break limit");
            setJavabuildcomplet(false);
            pythonbuildcomplet = false;
            if (simulatControlModle != null) {
                simulatControlModle.setJavabuildcomplet(false);
                simulatControlModle.setPythonbuildcomplet(false);
            }
            reconnect();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }

    }


    //模型构造数据
    private JSONObject dataForpythonBuildmpc() {
        JSONObject jsonObject = new JSONObject();
        /**
         * base
         * */
        jsonObject.put("m", getNumOfRunnableMVpins_mm());
        jsonObject.put("p", getNumOfRunnablePVPins_pp());
        jsonObject.put("M", getControltime_M());
        jsonObject.put("P", getPredicttime_P());
        jsonObject.put("N", getTimeserise_N());
        jsonObject.put("fnum", getNumOfRunnableFFpins_vv());
        jsonObject.put("pvusemv", getMaskMatrixRunnablePVUseMV());
        jsonObject.put("APCOutCycle", getControlAPCOutCycle());
        jsonObject.put("enable", getModleEnable());
//        jsonObject.put("validekey", modle.getValidkey());
        jsonObject.put("funneltype", getFunneltype());


        /**
         *mv
         * */
        if (getNumOfRunnablePVPins_pp() != 0) {
            jsonObject.put("A", getA_RunnabletimeseriseMatrix());
        }

        /**
         *ff
         */
        if (getNumOfRunnableFFpins_vv() != 0) {
            jsonObject.put("B", getB_RunnabletimeseriseMatrix());
        }

        jsonObject.put("Q", getQ());
        jsonObject.put("R", getR());
        jsonObject.put("alphe", getAlpheTrajectoryCoefficients());
        jsonObject.put("alphemethod", getAlpheTrajectoryCoefmethod());
        jsonObject.put("msgtype", MSGTYPE_BUILD);
        return jsonObject;
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
        if (simulatControlModle != null) {
            simulatControlModle.outprocess(null, null);
        }
    }


    @Override
    public void docomputeprocess() {
        //首先判断下是否dcs打自动
        BaseModlePropertyImp autopin = Tool.selectmodleProperyByPinname(ModleProperty.TYPE_PIN_MODLE_AUTO, propertyImpList, ModleProperty.PINDIRINPUT);
        //判断是否dcs已经打了自动
        if (autopin != null) {
            if (autopin.getValue() == 0) {
                //把输入的mv直接丢给输出mv，短路模型
//                JSONObject fakecomputedata = new JSONObject();
                modleshortcircuit();//模型短路

//                computresulteprocess(null, fakecomputedata);
//                outprocess(null, null);
                setAutovalue(0);//记录下当前的dcs自动位号值
                return;
            } else if ((autopin.getValue() != 0) && (getAutovalue() == 0)) {
                //之前为0，且当前dcs位号也不为0,开始重启模型
                setAutovalue(1);
                //清除java模型构建模型和python模型构建标志位
                setJavabuildcomplet(false);
                pythonbuildcomplet = false;
                if (simulatControlModle != null) {
                    simulatControlModle.setJavabuildcomplet(false);
                    simulatControlModle.setPythonbuildcomplet(false);
                }
                reconnect();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }

        }

        //检测下引脚有没有越界
        checkmodlepinisinLimit(propertyImpList);


        PySession mpcpySession = pySessionManager.getSpecialSession(getModleId(), mpcscript);
        PySession simulatepySession = pySessionManager.getSpecialSession(getModleId(), simulatorscript);
//        JSONObject scriptinputcontext = new JSONObject();
        //modle build
        if (mpcpySession != null && simulatepySession != null) {
            if (!javabuildcomplet&&((simulatControlModle==null)||(!simulatControlModle.isJavabuildcomplet()))) {
                if (modleBuild(true)) {
                    JSONObject mpcmodlebuild = dataForpythonBuildmpc();
                    JSONObject simulatemodlebuild = simulatControlModle.dataforpythonbuildsimulate();
                    try {
                        pythonbuildcomplet = false;
                        simulatControlModle.setPythonbuildcomplet(false);

                        mpcpySession.getCtx().writeAndFlush(CommandImp.PARAM.build(mpcmodlebuild.toJSONString().getBytes("utf-8"), getModleId()));

                        simulatepySession.getCtx().writeAndFlush(CommandImp.PARAM.build(simulatemodlebuild.toJSONString().getBytes("utf-8"), getModleId()));

                    } catch (UnsupportedEncodingException e) {
                        logger.error(e.getMessage(), e);
                    }
                    //pythonmodle 构架未完成，且在线
                } else {
                    //短路模型
                    setErrormsg("modle build error:\n" + "p:" + numOfRunnablePVPins_pp + " m:" + numOfRunnableMVpins_mm + " ff:" + numOfRunnableFFpins_vv);
                    modleshortcircuit();
                    return;
                }

            }
            JSONObject mpcmodlebuild = dataForpythonBuildmpc();
            JSONObject simulatemodlebuild = simulatControlModle.dataforpythonbuildsimulate();
            while (javabuildcomplet&&!pythonbuildcomplet && pySessionManager.getSpecialSession(getModleId(), mpcscript) != null) {
                int i = 3;
                while (i-- > 0) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                    if (pythonbuildcomplet) {
                        break;
                    }
                }
                if (!pythonbuildcomplet) {
                    mpcmodlebuild = dataForpythonBuildmpc();
                    try {
                        mpcpySession.getCtx().writeAndFlush(CommandImp.PARAM.build(mpcmodlebuild.toJSONString().getBytes("utf-8"), getModleId()));
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e.getMessage(), e);
                    }

                }
            }


            while (simulatControlModle.isJavabuildcomplet()&&!simulatControlModle.isPythonbuildcomplet() && pySessionManager.getSpecialSession(getModleId(), simulatorscript) != null) {
                int i = 3;
                while (i-- > 0) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                    }
                    if (simulatControlModle.isPythonbuildcomplet()) {
                        break;
                    }
                }
                if (!simulatControlModle.isPythonbuildcomplet()) {
                    simulatemodlebuild = simulatControlModle.dataforpythonbuildsimulate();
                    try {
                        simulatepySession.getCtx().writeAndFlush(CommandImp.PARAM.build(simulatemodlebuild.toJSONString().getBytes("utf-8"), getModleId()));
                    } catch (UnsupportedEncodingException e) {
                        logger.error(e.getMessage(), e);
                    }

                }
            }

//            else {
            //modle build completed
            //send compute parame
            try {

                mpcpySession = pySessionManager.getSpecialSession(getModleId(), mpcscript);
                simulatepySession = pySessionManager.getSpecialSession(getModleId(), simulatorscript);
                if (mpcpySession != null && simulatepySession != null) {
                    if (javabuildcomplet && (simulatControlModle != null) && (simulatControlModle.isJavabuildcomplet())) {
                        setModlerunlevel(BaseModleImp.RUNLEVEL_RUNNING);
                        simulatControlModle.setModlerunlevel(BaseModleImp.RUNLEVEL_RUNNING);
                        mpcpySession.getCtx().writeAndFlush(CommandImp.PARAM.build(getRealData().toJSONString().getBytes("utf-8"), getModleId()));
                        simulatepySession.getCtx().writeAndFlush(CommandImp.PARAM.build(simulatControlModle.getRealSimulateData().toJSONString().getBytes("utf-8"), getModleId()));
                    }
                }

            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
//            }
        }

    }


    @Override
    public JSONObject inprocess(Project project) {

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

        if (computedata.getJSONObject("data").getString("msgtype").equals(MSGTYPE_BUILD)) {
            pythonbuildcomplet = true;
        } else if (computedata.getJSONObject("data").getString("msgtype").equals(MSGTYPE_COMPUTE)) {
            try {

                JSONObject modlestatus = computedata.getJSONObject("data");//JSONObject.parseObject(data);
                JSONArray predictpvJson = modlestatus.getJSONArray("predict");//
                JSONArray eJson = modlestatus.getJSONArray("e");//
                JSONArray funelupAnddownJson = modlestatus.getJSONArray("funelupAnddown");//
                JSONArray dmvJson = modlestatus.getJSONArray("dmv");//
                JSONArray dffJson = modlestatus.getJSONArray("dff");//

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
                for (int i = 0; i < p * N; i++) {
                    predictpvArray[i] = predictpvJson.getDouble(i);
                    funelupAnddownArray[0][i] = funelupAnddownJson.getJSONArray(0).getDouble(i);
                    funelupAnddownArray[1][i] = funelupAnddownJson.getJSONArray(1).getDouble(i);
                }

                for (int i = 0; i < p; i++) {
                    eArray[i] = eJson.getDouble(i);
                }

                for (int i = 0; i < m; i++) {
                    dmvArray[i] = dmvJson.getDouble(i);
                }
                for (int i = 0; i < v; ++i) {
                    dffArray[i] = dffJson.getDouble(i);
                }
                updateModleComputeResult(predictpvArray, funelupAnddownArray, dmvArray, eArray, dffArray);

                JSONArray mvJson = modlestatus.getJSONArray("mv");//
                if (runstyle.equals(RUNSTYLEBYAUTO)) {
                    int index = 0;
                    for (MPCModleProperty mpcModleProperty : categoryMVmodletag) {
                        String outputpinname = mpcModleProperty.getModlePinName();
                        double outvalue = mvJson.getDouble(index);
                        index++;
                        for (ModleProperty modleProperty : propertyImpList) {
                            MPCModleProperty outputpin = (MPCModleProperty) modleProperty;
                            if (outputpin.getPindir().equals(MPCModleProperty.PINDIROUTPUT)) {
                                if (outputpinname.equals(outputpin.getModlePinName())) {
                                    outputpin.setValue(outvalue);
                                }
                            }

                        }
                    }
                }


            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return null;
    }

    @Override
    public void outprocess(Project project, JSONObject outdata) {
        setModlerunlevel(BaseModleImp.RUNLEVEL_RUNCOMPLET);
        //activetime
        setActivetime(Instant.now());
    }

    @Override
    public void init() {
        indexproperties = new HashMap<>();
        for (ModleProperty modleProperty : propertyImpList) {
            MPCModleProperty mpcModleProperty = (MPCModleProperty) modleProperty;
            indexproperties.put(mpcModleProperty.getModlepinsId(), mpcModleProperty);
        }
        String filterpath = System.getProperty("user.dir") + "\\" + pyproxyexecute;
        mpcexecutepythonbridge = new ExecutePythonBridge(filterpath, "127.0.0.1", port, mpcscript, getModleId() + "");

        simulateexecutePythonBridge = new ExecutePythonBridge(filterpath, "127.0.0.1", port, simulatorscript, getModleId() + "");
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
        return true;
    }


    /**
     * 初始化本次可运行的pv、mv、ff的引脚应用情况，如果在对应位置写1则说明本次参与运行
     * 另外一个是初始化数据库中配置了pv对应了那些个mv，pv对应了哪些ff
     * maskRunnablePVMatrix  maskRunnableMVMatrix  maskRunnableFFMatrix maskBaseMapPvEffectMvMatrix
     */
    private void initRunnableMatrixAndBaseMapMatrix() {
        for (int indexpv = 0; indexpv < categoryPVmodletag.size(); ++indexpv) {
            /**pv引脚启用，并且参与本次控制*/
            if (isThisTimeRunnablePin(categoryPVmodletag.get(indexpv))) {
                maskisRunnablePVMatrix[indexpv] = 1;
            }

            /**1\marker total pvusemv
             * 2\marker participate mv
             * */
            for (int indexmv = 0; indexmv < categoryMVmodletag.size(); ++indexmv) {
                ResponTimeSerise ismapping = isPVMappingMV(categoryPVmodletag.get(indexpv).getModlePinName(), categoryMVmodletag.get(indexmv).getModlePinName());
                maskBaseMapPvUseMvMatrix[indexpv][indexmv] = (null != ismapping ? 1 : 0);
                maskBaseMapPvEffectMvMatrix[indexpv][indexmv] = (null != ismapping ? ismapping.getEffectRatio() : 0f);
                /**1是否有映射关系、2、pv是否启用 3mv是否启用*/
                if ((null != ismapping) && isThisTimeRunnablePin(categoryPVmodletag.get(indexpv)) && isThisTimeRunnablePin(categoryMVmodletag.get(indexmv))) {
                    maskisRunnableMVMatrix[indexmv] = 1;
                }
            }

            /**1\marker total pvuseff
             * 2\marker participate ff
             * */
            for (int indexff = 0; indexff < categoryFFmodletag.size(); ++indexff) {
                ResponTimeSerise ismapping = isPVMappingFF(categoryPVmodletag.get(indexpv).getModlePinName(), categoryFFmodletag.get(indexff).getModlePinName());
                maskBaseMapPvUseFfMatrix[indexpv][indexff] = (null != ismapping ? 1 : 0);
                if ((null != ismapping) && isThisTimeRunnablePin(categoryPVmodletag.get(indexpv)) && isThisTimeRunnablePin(categoryFFmodletag.get(indexff))) {
                    maskisRunnableFFMatrix[indexff] = 1;
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

    private void initPVparams() {

        List<MPCModleProperty> runablePVPins = getRunablePins(categoryPVmodletag, maskisRunnablePVMatrix);
        List<MPCModleProperty> runableMVPins = getRunablePins(categoryMVmodletag, maskisRunnableMVMatrix);

        int looppv = 0;
        for (MPCModleProperty runpvpin : runablePVPins) {
            Q[looppv] = runpvpin.getQ();
            alpheTrajectoryCoefficients[looppv] = runpvpin.getReferTrajectoryCoef();
            alpheTrajectoryCoefmethod[looppv] = runpvpin.getTracoefmethod();
            deadZones[looppv] = runpvpin.getDeadZone();
            funelinitvalues[looppv] = runpvpin.getFunelinitValue();
            double[] fnl = new double[2];
            if (runpvpin.getFunneltype() != null) {
                switch (runpvpin.getFunneltype()) {
                    case MPCModleProperty.TYPE_FUNNEL_FULL:
                        fnl[0] = 0d;
                        fnl[1] = 0d;
                        funneltype[looppv] = fnl;
                        break;
                    case MPCModleProperty.TYPE_FUNNEL_UP:
                        fnl[0] = 0;
                        //乘负无穷
                        fnl[1] = 1;
                        funneltype[looppv] = fnl;
                        break;
                    case MPCModleProperty.TYPE_FUNNEL_DOWN:
                        //乘正无穷
                        fnl[0] = 1;
                        fnl[1] = 0;
                        funneltype[looppv] = fnl;
                        break;
                    default:
                        fnl[0] = 0;
                        fnl[1] = 0;
                        funneltype[looppv] = fnl;
                }
            } else {
                //匹配不到就是全漏斗
                fnl[0] = 0;
                fnl[1] = 0;
                funneltype[looppv] = fnl;
            }


            int loopmv = 0;
            for (MPCModleProperty runmvpin : runableMVPins) {

                /**查找映射关系*/
                ResponTimeSerise responTimeSerisePVMV = isPVMappingMV(runpvpin.getModlePinName(), runmvpin.getModlePinName());
                if (responTimeSerisePVMV != null) {
                    A_RunnabletimeseriseMatrix[looppv][loopmv] = responTimeSerisePVMV.responOneTimeSeries(timeserise_N, controlAPCOutCycle);
                    maskMatrixRunnablePVUseMV[looppv][loopmv] = 1;
                    maskMatrixRunnablePvEffectMv[looppv][loopmv] = responTimeSerisePVMV.getEffectRatio();
                }

                ++loopmv;
            }

            ++looppv;
        }

    }


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


    private void initRMatrix() {
        int indevEnableMV = 0;
        for (MPCModleProperty runmv : getRunablePins(categoryMVmodletag, maskisRunnableMVMatrix)) {
            R[indevEnableMV] = runmv.getR();
            ++indevEnableMV;
        }
    }


    private void initFFparams() {

        List<MPCModleProperty> runablePVPins = getRunablePins(categoryPVmodletag, maskisRunnablePVMatrix);//获取可运行的pv引脚
        List<MPCModleProperty> runableFFPins = getRunablePins(categoryFFmodletag, maskisRunnableFFMatrix);//获取可运行的ff引脚

        int looppv = 0;
        for (MPCModleProperty runpv : runablePVPins) {

            int loopff = 0;
            for (MPCModleProperty runff : runableFFPins) {

                ResponTimeSerise responTimeSerisePVFF = isPVMappingFF(runpv.getModlePinName(), runff.getModlePinName());

                if (responTimeSerisePVFF != null) {
                    B_RunnabletimeseriseMatrix[looppv][loopff] = responTimeSerisePVFF.responOneTimeSeries(timeserise_N, controlAPCOutCycle);
                    maskMatrixRunnablePVUseFF[looppv][loopff] = 1;
                }
                ++loopff;
            }
            ++looppv;
        }
    }


    /**
     * 模型构建函数
     *
     * @param isfirsttime 如果是第一次构建，那么需要重新重新分类引脚并注册进opcserv
     */
    public synchronized Boolean modleBuild(boolean isfirsttime) {
        logger.info("modle id=" + getModleId() + " is building");

        try {
//            if (isfirsttime) {
            stringmodlePinsMap = new ConcurrentHashMap<>();
//            categoryPVmodletag=
            categoryPVmodletag = new ArrayList<>();//已经分类号的PV引脚
            categorySPmodletag = new ArrayList<>();//已经分类号的SP引脚
            categoryMVmodletag = new ArrayList<>();//已经分类号的MV引脚
            categoryFFmodletag = new ArrayList<>();//已经分类号的FF引脚
            /**将引脚进行分类*/
            if (!classAndCombineRegiterPinsToMap()) {
                return false;
            }
            /**第一次将点号注册进lopcserve*/
            //firstTimeRegiterPinsToOPCServe();
//            }

            /**新建仿真器
             * 吧仿真器之前运行状态记录下来
             * */
//            if(simulatControlModle!=null){
//                boolean lasttimesimulatstatus=simulatControlModle.isIssimulation();
//                simulatControlModle = new SimulatControlModle(controltime_M, predicttime_P, timeserise_N, controlAPCOutCycle, simulatorscript, getModleId());
//                simulatControlModle.setIssimulation(lasttimesimulatstatus);
//            }else {
//                simulatControlModle = new SimulatControlModle(controltime_M, predicttime_P, timeserise_N, controlAPCOutCycle, simulatorscript, getModleId());
//            }

            simulatControlModle = new SimulatControlModle(controltime_M, predicttime_P, timeserise_N, controlAPCOutCycle, simulatorscript, getModleId());


            /**init pvusemv and pvuseff matrix
             * 数据库配配置的pv和mv/ff映射关系
             * */
            maskBaseMapPvUseMvMatrix = new int[categoryPVmodletag.size()][categoryMVmodletag.size()];
            maskBaseMapPvUseFfMatrix = new int[categoryPVmodletag.size()][categoryFFmodletag.size()];

            /**pv对mv的作用关系*/
            maskBaseMapPvEffectMvMatrix = new float[categoryPVmodletag.size()][categoryMVmodletag.size()];

            maskisRunnableMVMatrix = new int[categoryMVmodletag.size()];
            maskisRunnableFFMatrix = new int[categoryFFmodletag.size()];
            maskisRunnablePVMatrix = new int[categoryPVmodletag.size()];

            initRunnableMatrixAndBaseMapMatrix();

            //本次可运行的pv数量
            numOfRunnablePVPins_pp = 0;
            //本次可运行的mv数量
            numOfRunnableMVpins_mm = 0;
            //本次可运行的ff数量
            numOfRunnableFFpins_vv = 0;
            initStatisticRunnablePinNum();
            //没有可以运行的引脚直接不需要进行build了
            if (numOfRunnablePVPins_pp == 0 || numOfRunnableMVpins_mm == 0) {
                return false;
            }
//            logger.debug("p="+numOfRunnablePVPins_pp+" ,m="+numOfRunnableMVpins_mm+" ,v="+numOfRunnableFFpins_vv);
            /***
             * 输入输出响应对应矩阵
             * init A matrix
             * */
            A_RunnabletimeseriseMatrix = new double[numOfRunnablePVPins_pp][numOfRunnableMVpins_mm][timeserise_N];


            /**可运行的pv和mv的作用比例矩阵*/
            maskMatrixRunnablePvEffectMv = new float[numOfRunnablePVPins_pp][numOfRunnableMVpins_mm];


            /***
             *1、fill respon into A matrix
             *2、and init matrixEnablePVUseMV
             * */

            /**predict zone params*/
            Q = new Double[numOfRunnablePVPins_pp];//use for pv
            /**trajectry coefs*/
            alpheTrajectoryCoefficients = new Double[numOfRunnablePVPins_pp];//use for pv
            alpheTrajectoryCoefmethod = new String[numOfRunnablePVPins_pp];
            /**死区时间和漏洞初始值*/
            deadZones = new Double[numOfRunnablePVPins_pp];//use for pv
            funelinitvalues = new Double[numOfRunnablePVPins_pp];//use for pv
            /**funnel type*/
            funneltype = new double[numOfRunnablePVPins_pp][2];//use for pv


            maskMatrixRunnablePVUseMV = new int[numOfRunnablePVPins_pp][numOfRunnableMVpins_mm];//recording enablepv use which mvs

            initPVparams();

            /**累计映射关系*/
            accumulativeNumOfRunnablePVMVMaping();


            /**init R control zone params*/
            R = new Double[numOfRunnableMVpins_mm];//use for mv
            initRMatrix();

            /***
             * 前馈输出对应矩阵
             * init B matrix
             * */
            B_RunnabletimeseriseMatrix = new double[numOfRunnablePVPins_pp][numOfRunnableFFpins_vv][timeserise_N];

            /**
             *fill respon into B matrix
             *填入前馈输出响应矩阵
             * */
            maskMatrixRunnablePVUseFF = new int[numOfRunnablePVPins_pp][numOfRunnableFFpins_vv];

            initFFparams();


            /**模型数据库配置的 pv**/
            baseoutpoints_p = categoryPVmodletag.size();

            basefeedforwardpoints_v = categoryFFmodletag.size();

            baseinputpoints_m = categoryMVmodletag.size();

            /***************acp算法回传的数据********************/
            backPVPrediction = new double[numOfRunnablePVPins_pp][timeserise_N];//pv的预测曲线

            backPVFunelUp = new double[numOfRunnablePVPins_pp][timeserise_N];//PV的漏斗上曲线

            backPVFunelDown = new double[numOfRunnablePVPins_pp][timeserise_N];//漏斗下曲线

            backDmvWrite = new double[numOfRunnablePVPins_pp][numOfRunnableMVpins_mm];//MV写入值
            backrawDmv = new double[numOfRunnableMVpins_mm];
            backrawDff = new double[numOfRunnableFFpins_vv];

            backPVPredictionError = new double[numOfRunnablePVPins_pp];//预测误差

            backDff = new double[numOfRunnablePVPins_pp][numOfRunnableFFpins_vv];//前馈变换值

            // validkey = System.currentTimeMillis();
            List<MPCModleProperty> mpcModlePropertyArrayList = new ArrayList<>();
            mpcModlePropertyArrayList.addAll(indexproperties.values());
            simulatControlModle.setModlePins(mpcModlePropertyArrayList);

            simulatControlModle.setControlModle(this);
            simulatControlModle.build();
            javabuildcomplet = true;
            return true;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return false;
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
     * 获取计算的实时数据
     */
    public JSONObject getRealData() {
        try {
            /**
             * y0(pv)
             * limitU(mv)
             * limitY()
             * FF
             * Wi(sp)
             *
             * */
            JSONObject jsonObject = new JSONObject();
            //sp
            Double[] sp = new Double[numOfRunnablePVPins_pp];
            int indexEnableSP = 0;
            for (int indexsp = 0; indexsp < categorySPmodletag.size(); ++indexsp) {
                if (maskisRunnablePVMatrix[indexsp] == 0) {
                    continue;
                }
                sp[indexEnableSP] = categorySPmodletag.get(indexsp).getValue();
                indexEnableSP++;
            }
            jsonObject.put("wi", sp);

            //pv
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
            jsonObject.put("y0", pv);

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
            jsonObject.put("limitU", limitU);
            jsonObject.put("limitDU", limitDU);
            jsonObject.put("U", U);
            jsonObject.put("UFB", UFB);

            //FF
            int indexEnableFF = 0;
            if (numOfRunnableFFpins_vv != 0) {
                Double[] ff = new Double[numOfRunnableFFpins_vv];
                Double[] fflmt = new Double[numOfRunnableFFpins_vv];
                for (int indexff = 0; indexff < categoryFFmodletag.size(); ++indexff) {
                    if (maskisRunnableFFMatrix[indexff] == 0) {
                        continue;
                    }

                    ff[indexEnableFF] = categoryFFmodletag.get(indexff).getValue();
                    MPCModleProperty ffuppin = categoryFFmodletag.get(indexff).getUpLmt();
                    MPCModleProperty ffdownpin = categoryFFmodletag.get(indexff).getDownLmt();

                    /**
                     *ff信号是否在置信区间内
                     * */
                    Double ffHigh = 0d;
                    Double ffLow = 0d;

                    ffHigh = ffuppin.getValue();
                    ffLow = ffdownpin.getValue();

                    if ((ffLow <= categoryFFmodletag.get(indexff).getValue()) && (ffHigh >= categoryFFmodletag.get(indexff).getValue())) {
                        fflmt[indexEnableFF] = 1d;
                    } else {
                        fflmt[indexEnableFF] = 0d;
                    }
                    indexEnableFF++;
                }

                jsonObject.put("FF", ff);
                jsonObject.put("FFLmt", fflmt);

            }

            jsonObject.put("enable", 1);

            /**
             *死区时间和漏斗初始值
             * */
            jsonObject.put("deadZones", deadZones);
            jsonObject.put("funelInitValues", funelinitvalues);
            jsonObject.put("msgtype", MSGTYPE_COMPUTE);

//            jsonObject.put("validekey", );
            return jsonObject;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    public boolean writeData(Double[] values) {
//        try {
////            int loop = 0;
//            int indexENableMV = 0;
//            boolean result = true;
//            for (int indexmv = 0; indexmv < categoryMVmodletag.size(); ++indexmv) {
//                if (maskisRunnableMVMatrix[indexmv] == 0) {
//                    continue;
//                }
//                MPCModleProperty mvpin = categoryMVmodletag.get(indexmv);
//                mvpin.setWriteValue(values[indexENableMV]);
//                result = opcServicConstainer.writeModlePinValue(mvpin, values[indexENableMV]) && result;
//                indexENableMV++;
//            }
//            return result;
//
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }
        return false;

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


    /**
     * 获取标记组数不为0位置上的引脚
     *
     * @param categorypins 待提取引脚
     * @param maskmatrix   标记数组
     */
    public List<MPCModleProperty> getRunablePins(List<MPCModleProperty> categorypins, int[] maskmatrix) {
        List<MPCModleProperty> result = new LinkedList<>();
        for (int indexpin = 0; indexpin < categorypins.size(); ++indexpin) {
            if (1 == maskmatrix[indexpin]) {
                result.add(categorypins.get(indexpin));
            }
        }
        return result;
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
    private ResponTimeSerise isPVMappingMV(String pvpin, String mvpin) {

        for (ResponTimeSerise responTimeSerise : responTimeSeriseList) {
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
    private ResponTimeSerise isPVMappingFF(String pvpin, String ffpin) {

        for (ResponTimeSerise responTimeSerise : responTimeSeriseList) {
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


    /****db****/
    private Integer predicttime_P;//预测时域
    private Integer controltime_M;//单一控制输入未来控制M步增量(控制域)
    private Integer timeserise_N;//响应序列长度
    private Integer controlAPCOutCycle;//控制周期
    private Integer runstyle = 0;//运行方式0-自动分配模式 1-手动分配模式
    /**************/

    private List<ModleProperty> propertyImpList;
    private List<ResponTimeSerise> responTimeSeriseList;


    public Integer getPredicttime_P() {
        return predicttime_P;
    }

    public void setPredicttime_P(Integer predicttime_P) {
        this.predicttime_P = predicttime_P;
    }

    public Integer getControltime_M() {
        return controltime_M;
    }

    public void setControltime_M(Integer controltime_M) {
        this.controltime_M = controltime_M;
    }

    public Integer getTimeserise_N() {
        return timeserise_N;
    }

    public void setTimeserise_N(Integer timeserise_N) {
        this.timeserise_N = timeserise_N;
    }

    public Integer getControlAPCOutCycle() {
        return controlAPCOutCycle;
    }

    public void setControlAPCOutCycle(Integer controlAPCOutCycle) {
        this.controlAPCOutCycle = controlAPCOutCycle;
    }

    public Integer getRunstyle() {
        return runstyle;
    }

    public void setRunstyle(Integer runstyle) {
        this.runstyle = runstyle;
    }

    public List<ModleProperty> getPropertyImpList() {
        return propertyImpList;
    }

    public void setPropertyImpList(List<ModleProperty> propertyImpList) {
        this.propertyImpList = propertyImpList;
    }

    public List<ResponTimeSerise> getResponTimeSeriseList() {
        return responTimeSeriseList;
    }

    public void setResponTimeSeriseList(List<ResponTimeSerise> responTimeSeriseList) {
        this.responTimeSeriseList = responTimeSeriseList;
    }


    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public Map<Integer, MPCModleProperty> getIndexproperties() {
        return indexproperties;
    }

    public void setIndexproperties(Map<Integer, MPCModleProperty> indexproperties) {
        this.indexproperties = indexproperties;
    }

    public PySessionManager getPySessionManager() {
        return pySessionManager;
    }

    public void setPySessionManager(PySessionManager pySessionManager) {
        this.pySessionManager = pySessionManager;
    }

    public ExecutePythonBridge getMpcexecutepythonbridge() {
        return mpcexecutepythonbridge;
    }

    public void setMpcexecutepythonbridge(ExecutePythonBridge mpcexecutepythonbridge) {
        this.mpcexecutepythonbridge = mpcexecutepythonbridge;
    }

    public String getPyproxyexecute() {
        return pyproxyexecute;
    }

    public void setPyproxyexecute(String pyproxyexecute) {
        this.pyproxyexecute = pyproxyexecute;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getMpcscript() {
        return mpcscript;
    }

    public void setMpcscript(String mpcscript) {
        this.mpcscript = mpcscript;
    }

    public List<MPCModleProperty> getCategoryPVmodletag() {
        return categoryPVmodletag;
    }

    public void setCategoryPVmodletag(List<MPCModleProperty> categoryPVmodletag) {
        this.categoryPVmodletag = categoryPVmodletag;
    }

    public List<MPCModleProperty> getCategorySPmodletag() {
        return categorySPmodletag;
    }

    public void setCategorySPmodletag(List<MPCModleProperty> categorySPmodletag) {
        this.categorySPmodletag = categorySPmodletag;
    }

    public List<MPCModleProperty> getCategoryMVmodletag() {
        return categoryMVmodletag;
    }

    public void setCategoryMVmodletag(List<MPCModleProperty> categoryMVmodletag) {
        this.categoryMVmodletag = categoryMVmodletag;
    }

    public List<MPCModleProperty> getCategoryFFmodletag() {
        return categoryFFmodletag;
    }

    public void setCategoryFFmodletag(List<MPCModleProperty> categoryFFmodletag) {
        this.categoryFFmodletag = categoryFFmodletag;
    }

    public int getTotalPv() {
        return totalPv;
    }

    public void setTotalPv(int totalPv) {
        this.totalPv = totalPv;
    }

    public int getTotalFf() {
        return totalFf;
    }

    public void setTotalFf(int totalFf) {
        this.totalFf = totalFf;
    }

    public int getTotalMv() {
        return totalMv;
    }

    public void setTotalMv(int totalMv) {
        this.totalMv = totalMv;
    }

    public double[][] getBackPVPrediction() {
        return backPVPrediction;
    }

    public void setBackPVPrediction(double[][] backPVPrediction) {
        this.backPVPrediction = backPVPrediction;
    }

    public double[][] getBackPVFunelUp() {
        return backPVFunelUp;
    }

    public void setBackPVFunelUp(double[][] backPVFunelUp) {
        this.backPVFunelUp = backPVFunelUp;
    }

    public double[][] getBackPVFunelDown() {
        return backPVFunelDown;
    }

    public void setBackPVFunelDown(double[][] backPVFunelDown) {
        this.backPVFunelDown = backPVFunelDown;
    }

    public double[][] getBackDmvWrite() {
        return backDmvWrite;
    }

    public void setBackDmvWrite(double[][] backDmvWrite) {
        this.backDmvWrite = backDmvWrite;
    }

    public double[] getBackrawDmv() {
        return backrawDmv;
    }

    public void setBackrawDmv(double[] backrawDmv) {
        this.backrawDmv = backrawDmv;
    }

    public double[] getBackrawDff() {
        return backrawDff;
    }

    public void setBackrawDff(double[] backrawDff) {
        this.backrawDff = backrawDff;
    }

    public double[] getBackPVPredictionError() {
        return backPVPredictionError;
    }

    public void setBackPVPredictionError(double[] backPVPredictionError) {
        this.backPVPredictionError = backPVPredictionError;
    }

    public double[][] getBackDff() {
        return backDff;
    }

    public void setBackDff(double[][] backDff) {
        this.backDff = backDff;
    }

    public Integer getBaseoutpoints_p() {
        return baseoutpoints_p;
    }

    public void setBaseoutpoints_p(Integer baseoutpoints_p) {
        this.baseoutpoints_p = baseoutpoints_p;
    }

    public Integer getNumOfRunnablePVPins_pp() {
        return numOfRunnablePVPins_pp;
    }

    public void setNumOfRunnablePVPins_pp(Integer numOfRunnablePVPins_pp) {
        this.numOfRunnablePVPins_pp = numOfRunnablePVPins_pp;
    }

    public Integer getBasefeedforwardpoints_v() {
        return basefeedforwardpoints_v;
    }

    public void setBasefeedforwardpoints_v(Integer basefeedforwardpoints_v) {
        this.basefeedforwardpoints_v = basefeedforwardpoints_v;
    }

    public Integer getNumOfRunnableFFpins_vv() {
        return numOfRunnableFFpins_vv;
    }

    public void setNumOfRunnableFFpins_vv(Integer numOfRunnableFFpins_vv) {
        this.numOfRunnableFFpins_vv = numOfRunnableFFpins_vv;
    }

    public Integer getBaseinputpoints_m() {
        return baseinputpoints_m;
    }

    public void setBaseinputpoints_m(Integer baseinputpoints_m) {
        this.baseinputpoints_m = baseinputpoints_m;
    }

    public Integer getNumOfRunnableMVpins_mm() {
        return numOfRunnableMVpins_mm;
    }

    public void setNumOfRunnableMVpins_mm(Integer numOfRunnableMVpins_mm) {
        this.numOfRunnableMVpins_mm = numOfRunnableMVpins_mm;
    }

    public double[][][] getA_RunnabletimeseriseMatrix() {
        return A_RunnabletimeseriseMatrix;
    }

    public void setA_RunnabletimeseriseMatrix(double[][][] a_RunnabletimeseriseMatrix) {
        A_RunnabletimeseriseMatrix = a_RunnabletimeseriseMatrix;
    }

    public double[][][] getB_RunnabletimeseriseMatrix() {
        return B_RunnabletimeseriseMatrix;
    }

    public void setB_RunnabletimeseriseMatrix(double[][][] b_RunnabletimeseriseMatrix) {
        B_RunnabletimeseriseMatrix = b_RunnabletimeseriseMatrix;
    }

    public Double[] getQ() {
        return Q;
    }

    public void setQ(Double[] q) {
        Q = q;
    }

    public Double[] getR() {
        return R;
    }

    public void setR(Double[] r) {
        R = r;
    }

    public Double[] getAlpheTrajectoryCoefficients() {
        return alpheTrajectoryCoefficients;
    }

    public void setAlpheTrajectoryCoefficients(Double[] alpheTrajectoryCoefficients) {
        this.alpheTrajectoryCoefficients = alpheTrajectoryCoefficients;
    }

    public String[] getAlpheTrajectoryCoefmethod() {
        return alpheTrajectoryCoefmethod;
    }

    public void setAlpheTrajectoryCoefmethod(String[] alpheTrajectoryCoefmethod) {
        this.alpheTrajectoryCoefmethod = alpheTrajectoryCoefmethod;
    }

    public Double[] getDeadZones() {
        return deadZones;
    }

    public void setDeadZones(Double[] deadZones) {
        this.deadZones = deadZones;
    }

    public Double[] getFunelinitvalues() {
        return funelinitvalues;
    }

    public void setFunelinitvalues(Double[] funelinitvalues) {
        this.funelinitvalues = funelinitvalues;
    }

    public double[][] getFunneltype() {
        return funneltype;
    }

    public void setFunneltype(double[][] funneltype) {
        this.funneltype = funneltype;
    }

    public SimulatControlModle getSimulatControlModle() {
        return simulatControlModle;
    }

    public void setSimulatControlModle(SimulatControlModle simulatControlModle) {
        this.simulatControlModle = simulatControlModle;
    }

    public String getSimulatorscript() {
        return simulatorscript;
    }

    public void setSimulatorscript(String simulatorscript) {
        this.simulatorscript = simulatorscript;
    }

    public int[][] getMaskBaseMapPvUseMvMatrix() {
        return maskBaseMapPvUseMvMatrix;
    }

    public void setMaskBaseMapPvUseMvMatrix(int[][] maskBaseMapPvUseMvMatrix) {
        this.maskBaseMapPvUseMvMatrix = maskBaseMapPvUseMvMatrix;
    }

    public float[][] getMaskBaseMapPvEffectMvMatrix() {
        return maskBaseMapPvEffectMvMatrix;
    }

    public void setMaskBaseMapPvEffectMvMatrix(float[][] maskBaseMapPvEffectMvMatrix) {
        this.maskBaseMapPvEffectMvMatrix = maskBaseMapPvEffectMvMatrix;
    }

    public int[][] getMaskMatrixRunnablePVUseMV() {
        return maskMatrixRunnablePVUseMV;
    }

    public void setMaskMatrixRunnablePVUseMV(int[][] maskMatrixRunnablePVUseMV) {
        this.maskMatrixRunnablePVUseMV = maskMatrixRunnablePVUseMV;
    }

    public float[][] getMaskMatrixRunnablePvEffectMv() {
        return maskMatrixRunnablePvEffectMv;
    }

    public void setMaskMatrixRunnablePvEffectMv(float[][] maskMatrixRunnablePvEffectMv) {
        this.maskMatrixRunnablePvEffectMv = maskMatrixRunnablePvEffectMv;
    }

    public int[][] getMaskBaseMapPvUseFfMatrix() {
        return maskBaseMapPvUseFfMatrix;
    }

    public void setMaskBaseMapPvUseFfMatrix(int[][] maskBaseMapPvUseFfMatrix) {
        this.maskBaseMapPvUseFfMatrix = maskBaseMapPvUseFfMatrix;
    }

    public int[][] getMaskMatrixRunnablePVUseFF() {
        return maskMatrixRunnablePVUseFF;
    }

    public void setMaskMatrixRunnablePVUseFF(int[][] maskMatrixRunnablePVUseFF) {
        this.maskMatrixRunnablePVUseFF = maskMatrixRunnablePVUseFF;
    }

    public int[] getMaskisRunnableFFMatrix() {
        return maskisRunnableFFMatrix;
    }

    public void setMaskisRunnableFFMatrix(int[] maskisRunnableFFMatrix) {
        this.maskisRunnableFFMatrix = maskisRunnableFFMatrix;
    }

    public int[] getMaskisRunnablePVMatrix() {
        return maskisRunnablePVMatrix;
    }

    public void setMaskisRunnablePVMatrix(int[] maskisRunnablePVMatrix) {
        this.maskisRunnablePVMatrix = maskisRunnablePVMatrix;
    }

    public int[] getMaskisRunnableMVMatrix() {
        return maskisRunnableMVMatrix;
    }

    public void setMaskisRunnableMVMatrix(int[] maskisRunnableMVMatrix) {
        this.maskisRunnableMVMatrix = maskisRunnableMVMatrix;
    }

    public Map<String, MPCModleProperty> getStringmodlePinsMap() {
        return stringmodlePinsMap;
    }

    public void setStringmodlePinsMap(Map<String, MPCModleProperty> stringmodlePinsMap) {
        this.stringmodlePinsMap = stringmodlePinsMap;
    }

    public boolean isJavabuildcomplet() {
        return javabuildcomplet;
    }

    public void setJavabuildcomplet(boolean javabuildcomplet) {
        this.javabuildcomplet = javabuildcomplet;
    }

    public String getRunmsg() {
        return runmsg;
    }

    public void setRunmsg(String runmsg) {
        this.runmsg = runmsg;
    }

    /*********/


}
