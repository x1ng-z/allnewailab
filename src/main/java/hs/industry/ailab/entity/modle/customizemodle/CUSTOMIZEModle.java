package hs.industry.ailab.entity.modle.customizemodle;

import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.modle.BaseModleImp;
import hs.industry.ailab.entity.modle.BaseModlePropertyImp;
import hs.industry.ailab.entity.modle.Modle;
import hs.industry.ailab.entity.modle.ModleProperty;
import hs.industry.ailab.entity.modle.controlmodle.MPCModle;
import hs.industry.ailab.entity.modle.controlmodle.PIDModle;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/9 11:28
 */
public class CUSTOMIZEModle extends BaseModleImp {
    private Logger logger = LoggerFactory.getLogger(CUSTOMIZEModle.class);
    private static Pattern scriptpattern = Pattern.compile("^(.*).py$");
    /**
     * memery
     */
    private boolean iscomplete = false;
    private String datasource;
    private Map<Integer, BaseModlePropertyImp> indexproperties;//key=modleid
    private PySessionManager pySessionManager;
    private ExecutePythonBridge executepythonbridge;
    private String pyproxyexecute;
    private String port;



    public void toBeRealModle(PySessionManager pySessionManager, String nettyport, String pyproxyexecute){
        this. port=nettyport;
        this.pyproxyexecute=pyproxyexecute;
        this.pySessionManager=pySessionManager;
    }



    @Override
    public void connect() {
        executepythonbridge.execute();

    }

    @Override
    public void reconnect() {
        executepythonbridge.stop();
        executepythonbridge.execute();
    }

    @Override
    public void destory() {
        PySession pySession = pySessionManager.getSpecialSession(getModleId(),customizepyname);
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
        PySession pySession = pySessionManager.getSpecialSession(getModleId(),customizepyname);
        JSONObject scriptinputcontext = new JSONObject();
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;

            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                JSONObject invalue = new JSONObject();
                invalue.put("value", baseModlePropertyImp.getValue());
                scriptinputcontext.put(baseModlePropertyImp.getModlePinName(), invalue);
            }
        }
        try {
            pySession.getCtx().writeAndFlush(CommandImp.RESULT.build(scriptinputcontext.toJSONString().getBytes("utf-8"), getModleId()));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }

    }


    @Override
    public JSONObject inprocess(Project project) {
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp customizeinproperty = (BaseModlePropertyImp) modleProperty;
            if (customizeinproperty.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                if (customizeinproperty.getResource().getString("resource").equals(ModleProperty.SOURCE_TYPE_CONSTANT)) {
                    customizeinproperty.setValue(customizeinproperty.getResource().getDouble("value"));
                } else if (customizeinproperty.getResource().getString("resource").equals(ModleProperty.SOURCE_TYPE_MODLE)) {
                    int modleId = customizeinproperty.getResource().getInteger("modleId");
                    int modlepinsId = customizeinproperty.getResource().getInteger("modlepinsId");
                    Modle modle = project.getIndexmodles().get(modleId);
                    if (modle != null) {
                        if (modle instanceof MPCModle) {
                            MPCModle mpcModle = (MPCModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = mpcModle.getIndexproperties().get(modlepinsId);
                            customizeinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof PIDModle) {
                            PIDModle pidModle = (PIDModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = pidModle.getIndexproperties().get(modlepinsId);
                            customizeinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof CUSTOMIZEModle) {
                            CUSTOMIZEModle customizeModle = (CUSTOMIZEModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = customizeModle.getIndexproperties().get(modlepinsId);
                            customizeinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof FilterModle) {
                            FilterModle filterModle = (FilterModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = filterModle.getIndexproperties().get(modlepinsId);
                            customizeinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof INModle) {
                            INModle inModle = (INModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = inModle.getIndexproperties().get(modlepinsId);
                            customizeinproperty.setValue(baseModlePropertyImp.getValue());
                        } else if (modle instanceof OUTModle) {
                            OUTModle outModle = (OUTModle) modle;
                            BaseModlePropertyImp baseModlePropertyImp = outModle.getIndexproperties().get(modlepinsId);
                            customizeinproperty.setValue(baseModlePropertyImp.getValue());
                        }

                    }

                }

            }
        }
        return null;
    }

    /**
     * 解析计算后的数据，并且将数据设置到输出引脚上
     *
     * @param computedata 里面的key是引脚的名称，值是value比如{
     *                    'pintag':{'value':1.2}
     *                    }
     */
    @Override
    public JSONObject computresulteprocess(Project project, JSONObject computedata) {

        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                JSONObject jsonObject = computedata.getJSONObject(baseModlePropertyImp.getModlePinName());
                if (jsonObject != null) {
                    baseModlePropertyImp.setValue(jsonObject.getDouble("value"));
                }
            }

        }

        return null;
    }

    @Override
    public void outprocess(Project project, JSONObject outdata) {

    }

    @Override
    public void init() {
        indexproperties = new HashMap<>();
        for (ModleProperty modleProperty : propertyImpList) {
            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
            indexproperties.put(baseModlePropertyImp.getModlepinsId(), baseModlePropertyImp);
        }

        String filterpath = System.getProperty("user.dir") + "\\" + pyproxyexecute;
        Matcher matcher = scriptpattern.matcher(customizepyname);
        if (matcher.find()) {
            executepythonbridge = new ExecutePythonBridge(filterpath, "127.0.0.1", port, matcher.group(1), getModleId() + "");
        }

    }




    /****db****/
    private String customizepyname;
    /*********/
    private List<ModleProperty> propertyImpList;


    public List<ModleProperty> getPropertyImpList() {
        return propertyImpList;
    }

    public void setPropertyImpList(List<ModleProperty> propertyImpList) {
        this.propertyImpList = propertyImpList;
    }

    public String getCustomizepyname() {
        return customizepyname;
    }

    public void setCustomizepyname(String customizepyname) {
        this.customizepyname = customizepyname;
    }




    public boolean isIscomplete() {
        return iscomplete;
    }

    public void setIscomplete(boolean iscomplete) {
        this.iscomplete = iscomplete;
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
}
