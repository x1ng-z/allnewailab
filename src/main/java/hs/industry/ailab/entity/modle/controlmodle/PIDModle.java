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
import hs.industry.ailab.pydriver.command.CommandImp;
import hs.industry.ailab.pydriver.session.PySession;
import hs.industry.ailab.pydriver.session.PySessionManager;
import hs.industry.ailab.utils.bridge.ExecutePythonBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
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

    public void toBeRealModle(PySessionManager pySessionManager,String pidscript, String nettyport, String pyproxyexecute){
        this.pidscript=pidscript;
        this. port=nettyport;
        this.pyproxyexecute=pyproxyexecute;
        this.pySessionManager=pySessionManager;

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
        executepythonbridge.stop();
        executepythonbridge.execute();
    }

    @Override
    public void destory() {
        PySession pySession = pySessionManager.getSpecialSession(getModleId(),pidscript);
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


    @Override
    public void docomputeprocess() {

        PySession pySession = pySessionManager.getSpecialSession(getModleId(),pidscript);
        if(pySession!=null){
            JSONObject scriptinputcontext = new JSONObject();

            JSONObject INjson=new JSONObject();
            scriptinputcontext.put("IN1",INjson);

            JSONObject OUTjson=new JSONObject();
            scriptinputcontext.put("OUT1",OUTjson);
            for (ModleProperty modleProperty : propertyImpList) {
                BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;

                if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                    JSONObject invalue = new JSONObject();
                    invalue.put("value", baseModlePropertyImp.getValue());
                    INjson.put(baseModlePropertyImp.getModlePinName(),invalue);

                }else if(baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)){
                    JSONObject outvalue = new JSONObject();
                    OUTjson.put(baseModlePropertyImp.getModlePinName(),outvalue);
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
                JSONObject outpinjsoncontext=computedata.getJSONObject("data").getJSONObject(pidinproperty.getModlePinName());
                if(outpinjsoncontext!=null){
                    pidinproperty.setValue(outpinjsoncontext.getDouble("value"));
                }
            }
        }

        return null;
    }

    @Override
    public void outprocess(Project project, JSONObject outdata) {
        setModlerunlevel(BaseModleImp.RUNLEVEL_RUNCOMPLET);
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
