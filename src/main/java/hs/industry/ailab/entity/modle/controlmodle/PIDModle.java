package hs.industry.ailab.entity.modle.controlmodle;

import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.entity.Project;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/9 11:17
 */
public class PIDModle extends BaseModleImp {
    private Logger logger = LoggerFactory.getLogger(PIDModle.class);

    /**
     * memery
     */
//    private boolean javabuildcomplet = false;//java控制模型是构建完成？
//    private boolean pythonbuildcomplet = false;//python的控制模型是否构建完成
//    private boolean iscomputecomplete = false;//运算是否完成
    private String datasource;
    private Map<Integer, BaseModlePropertyImp> indexproperties;//key=modleid
    private PySessionManager pySessionManager;
    private ExecutePythonBridge executepythonbridge;
    private String pyproxyexecute;
    private String port;
    private String pidscript;

    public void toBeRealModle(PySessionManager pySessionManager, String pidscript, String nettyport, String pyproxyexecute) {
        this.pidscript = pidscript;
        this.port = nettyport;
        this.pyproxyexecute = pyproxyexecute;
        this.pySessionManager = pySessionManager;

    }

    @Override
    public void connect() {
        executepythonbridge.execute();
        PySession pidpySession = pySessionManager.getSpecialSession(getModleId(), pidscript);
//        while (pidpySession == null) {
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

    @Override
    public void reconnect() {
        PySession mpcpySession = pySessionManager.getSpecialSession(getModleId(), pidscript);
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
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(),e);
        }

        executepythonbridge.stop();
        setModlerunlevel(BaseModleImp.RUNLEVEL_INITE);
        executepythonbridge.execute();
    }

    @Override
    public void destory() {
        PySession pySession = pySessionManager.getSpecialSession(getModleId(), pidscript);
        if (pySession != null) {
            JSONObject json = new JSONObject();
            json.put("msg", "stop");
            try {
                pySession.getCtx().writeAndFlush(CommandImp.STOP.build(json.toJSONString().getBytes("utf-8"), getModleId()));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }
        executepythonbridge.stop();
    }


    //模型短路
    private void modleshortcircuit() {
        BaseModlePropertyImp mvinputpin = Tool.selectmodleProperyByPinname(ModleProperty.TYPE_PIN_MV, propertyImpList, ModleProperty.PINDIRINPUT);
        BaseModlePropertyImp mvoutputpin = Tool.selectmodleProperyByPinname(ModleProperty.TYPE_PIN_MV, propertyImpList, ModleProperty.PINDIROUTPUT);
        if ((mvinputpin != null) && (mvoutputpin != null)) {
            JSONObject fakecomputedata = new JSONObject();
            JSONObject data = new JSONObject();
            data.put("value", mvinputpin.getValue());
            data.put("partkp", 0f);
            data.put("partki", 0f);
            data.put("partkd", 0f);
            JSONObject pindata = new JSONObject();
            pindata.put(mvoutputpin.getModlePinName(), data);
            fakecomputedata.put("data", pindata);
            computresulteprocess(null, fakecomputedata);
            outprocess(null, null);
            setAutovalue(0);
        }

    }

    @Override
    public void docomputeprocess() {

//            BaseModlePropertyImp baseModlePropertyImp=(BaseModlePropertyImp)modleProperty;
        BaseModlePropertyImp autopin = Tool.selectmodleProperyByPinname(ModleProperty.TYPE_PIN_MODLE_AUTO, propertyImpList, ModleProperty.PINDIRINPUT);

        if ((autopin != null)) {
            if (autopin.getValue() == 0) {
                //把输入的mv直接丢给输出mv
                modleshortcircuit();
                return;
            } else if ((autopin.getValue() != 0) && (getAutovalue() == 0)) {
                setAutovalue(1);
                reconnect();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage(), e);
                }
            }

        }

        PySession pySession = pySessionManager.getSpecialSession(getModleId(), pidscript);
        if (pySession != null) {
            JSONObject scriptinputcontext = new JSONObject();

            JSONObject INjson = new JSONObject();
            scriptinputcontext.put("IN1", INjson);

            JSONObject OUTjson = new JSONObject();
            scriptinputcontext.put("OUT1", OUTjson);
            for (ModleProperty modleProperty : propertyImpList) {
                BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;

                if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                    JSONObject invalue = new JSONObject();
                    invalue.put("value", baseModlePropertyImp.getValue());
                    INjson.put(baseModlePropertyImp.getModlePinName(), invalue);
//                    if (baseModlePropertyImp.getModlePinName().equals(ModleProperty.TYPE_PIN_PV)) {
//                        JSONObject inpvdeadZonealue = new JSONObject();
//                        inpvdeadZonealue.put("value", ((MPCModleProperty) baseModlePropertyImp).getDeadZone());
//                        INjson.put("deadZone", inpvdeadZonealue);
//                    } else

                        if (baseModlePropertyImp.getModlePinName().equals(ModleProperty.TYPE_PIN_MV)) {
                        JSONObject inmvdmvHighnealue = new JSONObject();
                        inmvdmvHighnealue.put("value", ((MPCModleProperty) baseModlePropertyImp).getDmvHigh());
                        INjson.put("dmvHigh", inmvdmvHighnealue);

                        JSONObject inmvdmvLownealue = new JSONObject();
                        inmvdmvLownealue.put("value", ((MPCModleProperty) baseModlePropertyImp).getDmvLow());
                        INjson.put("dmvLow", inmvdmvLownealue);
                    }

                } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                    JSONObject outvalue = new JSONObject();
                    OUTjson.put(baseModlePropertyImp.getModlePinName(), outvalue);
                    outvalue.put("pinName", baseModlePropertyImp.getModlePinName());
                }
            }
            try {
                setModlerunlevel(BaseModleImp.RUNLEVEL_RUNNING);
                pySession.getCtx().writeAndFlush(CommandImp.PARAM.build(scriptinputcontext.toJSONString().getBytes("utf-8"), getModleId()));
            } catch (UnsupportedEncodingException e) {
                logger.error(e.getMessage(), e);
            }
        }


    }

    @Override
    public JSONObject inprocess(Project project) {
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp pidinproperty = (BaseModlePropertyImp) modleProperty;
            if (pidinproperty.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                if (pidinproperty.getResource().getString("resource").equals(ModleProperty.SOURCE_TYPE_CONSTANT)) {
                    pidinproperty.setValue(pidinproperty.getResource().getDouble("value"));
                } else if (pidinproperty.getResource().getString("resource").equals(ModleProperty.SOURCE_TYPE_MODLE)) {
                    int modleId = pidinproperty.getResource().getInteger("modleId");
                    int modlepinsId = pidinproperty.getResource().getInteger("modlepinsId");

                    Modle modle = project.getIndexmodles().get(modleId);
                    if (modle != null) {
                        if (modle instanceof MPCModle) {
                            MPCModle mpcModle = (MPCModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = mpcModle.getIndexproperties().get(modlepinsId);
                            pidinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof PIDModle) {
                            PIDModle pidModle = (PIDModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = pidModle.getIndexproperties().get(modlepinsId);
                            pidinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof CUSTOMIZEModle) {
                            CUSTOMIZEModle customizeModle = (CUSTOMIZEModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = customizeModle.getIndexproperties().get(modlepinsId);
                            pidinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof FilterModle) {
                            FilterModle filterModle = (FilterModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = filterModle.getIndexproperties().get(modlepinsId);
                            pidinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof INModle) {
                            INModle inModle = (INModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = inModle.getIndexproperties().get(modlepinsId);
                            pidinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof OUTModle) {
                            OUTModle outModle = (OUTModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = outModle.getIndexproperties().get(modlepinsId);
                            pidinproperty.setValue(baseModlePropertyImp.getValue());
                        }

                    }

                }

            }
        }
        return null;
    }

    //解析计算输出结果，并且赋值给输出引脚
    @Override
    public JSONObject computresulteprocess(Project project, JSONObject computedata) {
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp pidinproperty = (BaseModlePropertyImp) modleProperty;
            if (pidinproperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                JSONObject outpinjsoncontext = computedata.getJSONObject("data").getJSONObject(pidinproperty.getModlePinName());
                if (outpinjsoncontext != null) {
                    pidinproperty.setValue(outpinjsoncontext.getDouble("value"));
                    if (outpinjsoncontext.containsKey("partkp") && outpinjsoncontext.containsKey("partki") && outpinjsoncontext.containsKey("partkd")) {
                        setErrormsg("partkp:" + Tool.getSpecalScale(4, outpinjsoncontext.getDouble("partkp")) + "\n"
                                + "partki:" + Tool.getSpecalScale(4, outpinjsoncontext.getDouble("partki")) + "\n"
                                + "partkd:" + Tool.getSpecalScale(4, outpinjsoncontext.getDouble("partkd"))
                        );
                    }

                }
            }
        }

        return null;
    }

    @Override
    public void outprocess(Project project, JSONObject outdata) {
        setModlerunlevel(BaseModleImp.RUNLEVEL_RUNCOMPLET);
        setActivetime(Instant.now());
    }


    @Override
    public void init() {
        indexproperties = new HashMap<>();
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
            indexproperties.put(baseModlePropertyImp.getModlepinsId(), baseModlePropertyImp);
        }


        String filterpath = System.getProperty("user.dir") + "\\" + pyproxyexecute;
        executepythonbridge = new ExecutePythonBridge(filterpath, "127.0.0.1", port, pidscript, getModleId() + "");
    }


    /******db****/

    private List<ModleProperty> propertyImpList;


    public List<ModleProperty> getPropertyImpList() {
        return propertyImpList;
    }

    public void setPropertyImpList(List<ModleProperty> propertyImpList) {
        this.propertyImpList = propertyImpList;
    }


    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public Map<Integer, BaseModlePropertyImp> getIndexproperties() {
        return indexproperties;
    }

    public void setIndexproperties(Map<Integer, BaseModlePropertyImp> indexproperties) {
        this.indexproperties = indexproperties;
    }

    public PySessionManager getPySessionManager() {
        return pySessionManager;
    }

    public void setPySessionManager(PySessionManager pySessionManager) {
        this.pySessionManager = pySessionManager;
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

    public String getPidscript() {
        return pidscript;
    }

    public void setPidscript(String pidscript) {
        this.pidscript = pidscript;
    }

//    public boolean isIscomputecomplete() {
//        return iscomputecomplete;
//    }
//
//    public void setIscomputecomplete(boolean iscomputecomplete) {
//        this.iscomputecomplete = iscomputecomplete;
//    }
}
