package hs.industry.ailab.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.dao.mysql.service.ProjectOperaterImp;
import hs.industry.ailab.entity.ModleSight;
import hs.industry.ailab.entity.Project;
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
import hs.industry.ailab.utils.help.Tool;
import hs.industry.ailab.utils.httpclient.HttpUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/10 15:36
 */
@Controller
@RequestMapping("/projectedit")
public class ProjectEdit {
    private Logger logger = LoggerFactory.getLogger(ProjectEdit.class);

    @Value("${oceandir}")
    private String oceandir;
    @Autowired
    private ProjectOperaterImp projectOperaterImp;

    @RequestMapping("/savenewproject")
    @ResponseBody
    public String savenewproject(String projectinfo) {
        JSONObject result = new JSONObject();

        JSONObject jsonproject = JSONObject.parseObject(projectinfo);

        result.put("msg", "success");

        return result.toJSONString();
    }


    @RequestMapping("/findproject")
    @ResponseBody
    public String findProject(@RequestParam("projectid") int projectid) {
        JSONObject result = new JSONObject();

        try {
//            Modle modle =projectOperaterImp.findModleByid(140);
            Project project = projectOperaterImp.findProjectById(projectid);

            result.put("msg", "success");

            JSONObject projectjson = new JSONObject();

            projectjson.put("projectid", project.getProjectid() + "");
            projectjson.put("projectname", project.getName());
            projectjson.put("modules", new JSONArray());

            result.put("project", projectjson);
//            JSONArray modules = new JSONArray();
            for (Modle modle : project.getModleList()) {
                if (modle instanceof MPCModle) {
                    MPCModle mpcModle = (MPCModle) modle;

                    JSONObject jsonmodule = new JSONObject();
                    projectjson.getJSONArray("modules").add(jsonmodule);
                    jsonmodule.put("id", mpcModle.getModleId() + "");
                    jsonmodule.put("type", mpcModle.getModletype());
                    jsonmodule.put("status", 0);
                    jsonmodule.put("name", mpcModle.getModleName());
                    jsonmodule.put("child", mpcModle.getModleSight().getChilds());

                    jsonmodule.put("inputproperty", new JSONArray());
                    jsonmodule.put("outputproperty", new JSONArray());
                    for (ModleProperty modleProperty : mpcModle.getPropertyImpList()) {
                        if (modleProperty instanceof MPCModleProperty) {
                            MPCModleProperty mpcModleProperty = (MPCModleProperty) modleProperty;
                            if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", mpcModleProperty.getModlePinName());
                                inputjson.put("name", mpcModleProperty.getOpcTagName());
                                inputjson.put("value", 0);
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", mpcModleProperty.getModlePinName());
                                outputjson.put("name", mpcModleProperty.getOpcTagName());
                                outputjson.put("value", 0);
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        } else if (modleProperty instanceof BaseModlePropertyImp) {
                            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                inputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                inputjson.put("value", 0);
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                outputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                outputjson.put("value", 0);
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        }


                    }
                    jsonmodule.put("top", mpcModle.getModleSight().getPositiontop());
                    jsonmodule.put("left", mpcModle.getModleSight().getPositionleft());


                } else if (modle instanceof PIDModle) {
                    PIDModle pidModle = (PIDModle) modle;

//                    MPCModle mpcModle=(MPCModle)modle;
                    JSONObject jsonmodule = new JSONObject();
                    projectjson.getJSONArray("modules").add(jsonmodule);
                    jsonmodule.put("id", pidModle.getModleId() + "");
                    jsonmodule.put("type", pidModle.getModletype());
                    jsonmodule.put("status", 0);
                    jsonmodule.put("name", pidModle.getModleName());
                    jsonmodule.put("child", pidModle.getModleSight().getChilds());

                    jsonmodule.put("inputproperty", new JSONArray());
                    jsonmodule.put("outputproperty", new JSONArray());
                    for (ModleProperty modleProperty : pidModle.getPropertyImpList()) {
                        if (modleProperty instanceof MPCModleProperty) {
                            MPCModleProperty mpcModleProperty = (MPCModleProperty) modleProperty;
                            if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", mpcModleProperty.getModlePinName());
                                inputjson.put("name", mpcModleProperty.getOpcTagName());
                                inputjson.put("value", 0);
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", mpcModleProperty.getModlePinName());
                                outputjson.put("name", mpcModleProperty.getOpcTagName());
                                outputjson.put("value", 0);
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        } else if (modleProperty instanceof BaseModlePropertyImp) {
                            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                inputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                inputjson.put("value", 0);
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                outputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                outputjson.put("value", 0);
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        }


                    }
                    jsonmodule.put("top", pidModle.getModleSight().getPositiontop());
                    jsonmodule.put("left", pidModle.getModleSight().getPositionleft());

                } else if (modle instanceof CUSTOMIZEModle) {
                    CUSTOMIZEModle customizeModle = (CUSTOMIZEModle) modle;

//                    MPCModle mpcModle=(MPCModle)modle;
                    JSONObject jsonmodule = new JSONObject();
                    projectjson.getJSONArray("modules").add(jsonmodule);
                    jsonmodule.put("id", customizeModle.getModleId() + "");
                    jsonmodule.put("type", customizeModle.getModletype());
                    jsonmodule.put("status", 0);
                    jsonmodule.put("name", customizeModle.getModleName());
                    jsonmodule.put("child", customizeModle.getModleSight().getChilds());

                    jsonmodule.put("inputproperty", new JSONArray());
                    jsonmodule.put("outputproperty", new JSONArray());
                    for (ModleProperty modleProperty : customizeModle.getPropertyImpList()) {
                        if (modleProperty instanceof MPCModleProperty) {
                            MPCModleProperty mpcModleProperty = (MPCModleProperty) modleProperty;
                            if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", mpcModleProperty.getModlePinName());
                                inputjson.put("name", mpcModleProperty.getOpcTagName());
                                inputjson.put("value", 0);
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", mpcModleProperty.getModlePinName());
                                outputjson.put("name", mpcModleProperty.getOpcTagName());
                                outputjson.put("value", 0);
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        } else if (modleProperty instanceof BaseModlePropertyImp) {
                            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                inputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                inputjson.put("value", 0);
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                outputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                outputjson.put("value", 0);
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        }


                    }
                    jsonmodule.put("top", customizeModle.getModleSight().getPositiontop());
                    jsonmodule.put("left", customizeModle.getModleSight().getPositionleft());

                } else if (modle instanceof FilterModle) {
                    FilterModle filterModle = (FilterModle) modle;
                    JSONObject jsonmodule = new JSONObject();
                    projectjson.getJSONArray("modules").add(jsonmodule);
                    jsonmodule.put("id", filterModle.getModleId() + "");
                    jsonmodule.put("type", filterModle.getModletype());
                    jsonmodule.put("status", 0);
                    jsonmodule.put("name", filterModle.getModleName());
                    jsonmodule.put("child", filterModle.getModleSight().getChilds());

                    jsonmodule.put("inputproperty", new JSONArray());
                    jsonmodule.put("outputproperty", new JSONArray());
                    for (ModleProperty modleProperty : filterModle.getPropertyImpList()) {
                        if (modleProperty instanceof MPCModleProperty) {
                            MPCModleProperty mpcModleProperty = (MPCModleProperty) modleProperty;
                            if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", mpcModleProperty.getModlePinName());
                                inputjson.put("name", mpcModleProperty.getOpcTagName());
                                inputjson.put("value", 0);
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", mpcModleProperty.getModlePinName());
                                outputjson.put("name", mpcModleProperty.getOpcTagName());
                                outputjson.put("value", 0);
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        } else if (modleProperty instanceof BaseModlePropertyImp) {
                            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                inputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                inputjson.put("value", 0);
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                outputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                outputjson.put("value", 0);
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        }


                    }
                    jsonmodule.put("top", filterModle.getModleSight().getPositiontop());
                    jsonmodule.put("left", filterModle.getModleSight().getPositionleft());


                } else if (modle instanceof INModle) {
                    INModle inModle = (INModle) modle;

//                    FilterModle filterModle=(FilterModle)modle;
                    JSONObject jsonmodule = new JSONObject();
                    projectjson.getJSONArray("modules").add(jsonmodule);
                    jsonmodule.put("id", inModle.getModleId() + "");
                    jsonmodule.put("type", inModle.getModletype());
                    jsonmodule.put("status", 0);
                    jsonmodule.put("name", inModle.getModleName());
                    jsonmodule.put("child", inModle.getModleSight().getChilds());

                    jsonmodule.put("inputproperty", new JSONArray());
                    jsonmodule.put("outputproperty", new JSONArray());
                    for (ModleProperty modleProperty : inModle.getPropertyImpList()) {
                        if (modleProperty instanceof MPCModleProperty) {
                            MPCModleProperty mpcModleProperty = (MPCModleProperty) modleProperty;
                            if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", mpcModleProperty.getModlePinName());
                                inputjson.put("name", mpcModleProperty.getOpcTagName());
                                inputjson.put("value", 0);
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", mpcModleProperty.getModlePinName());
                                outputjson.put("name", mpcModleProperty.getOpcTagName());
                                outputjson.put("value", 0);
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        } else if (modleProperty instanceof BaseModlePropertyImp) {
                            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                inputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                inputjson.put("value", 0);
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                outputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                outputjson.put("value", 0);
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        }


                    }
                    jsonmodule.put("top", inModle.getModleSight().getPositiontop());
                    jsonmodule.put("left", inModle.getModleSight().getPositionleft());
                } else if (modle instanceof OUTModle) {
                    OUTModle outModle = (OUTModle) modle;

//                    INModle inModle=(INModle)modle;

//                    FilterModle filterModle=(FilterModle)modle;
                    JSONObject jsonmodule = new JSONObject();
                    projectjson.getJSONArray("modules").add(jsonmodule);
                    jsonmodule.put("id", outModle.getModleId() + "");
                    jsonmodule.put("type", outModle.getModletype());
                    jsonmodule.put("status", 0);
                    jsonmodule.put("name", outModle.getModleName());
                    jsonmodule.put("name", outModle.getModleName());
                    jsonmodule.put("child", outModle.getModleSight().getChilds());

                    jsonmodule.put("inputproperty", new JSONArray());
                    jsonmodule.put("outputproperty", new JSONArray());
                    for (ModleProperty modleProperty : outModle.getPropertyImpList()) {
                        if (modleProperty instanceof MPCModleProperty) {
                            MPCModleProperty mpcModleProperty = (MPCModleProperty) modleProperty;
                            if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", mpcModleProperty.getModlePinName());
                                inputjson.put("name", mpcModleProperty.getOpcTagName());
                                inputjson.put("value", 0);
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", mpcModleProperty.getModlePinName());
                                outputjson.put("name", mpcModleProperty.getOpcTagName());
                                outputjson.put("value", 0);
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        } else if (modleProperty instanceof BaseModlePropertyImp) {
                            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                inputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                inputjson.put("value", 0);
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                outputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                outputjson.put("value", 0);
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        }


                    }
                    jsonmodule.put("top", outModle.getModleSight().getPositiontop());
                    jsonmodule.put("left", outModle.getModleSight().getPositionleft());

                }

            }
//            projectjson.put("modules", modules)


            /**
             * {
             *                 'id': "1",
             *                 'type': 'input',
             *                 'name': '输入点集合',
             *                 'status': '1',
             *                 'child': [{
             *                     'id': 2
             *                 }],
             *                 'inputproperty': [],
             *                 'outputproperty': [
             *                     {'pin': 'input.pv', 'name': '磨机振动', 'value': 0},
             *                     {'pin': 'input.pv', 'name': '磨机振动', 'value': 0},
             *                     {'pin': 'input.pv', 'name': '磨机振动', 'value': 0},
             *                     {'pin': 'input.pv', 'name': '磨机振动', 'value': 0},
             *                     {'pin': 'input.pv', 'name': '磨机振动', 'value': 0},
             *                     {'pin': 'input.pv', 'name': '磨机振动', 'value': 0},
             *                     {'pin': 'input.pv', 'name': '磨机振动', 'value': 0},
             *                     {'pin': 'input.pv', 'name': '磨机振动', 'value': 0}],
             *                 'top': 50,
             *                 'left': 150
             *             }
             *
             *
             *
             * */

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
        }
        return result.toJSONString();
    }


    /***组态编辑操作modle edit***/
    @RequestMapping("/creatmodle")
    @ResponseBody
    public String creatModle(@RequestParam("modleinfo") String modleinfo) {
        JSONObject result = new JSONObject();

        try {
            JSONObject jsonmodleinfo = JSONObject.parseObject(modleinfo);

            JSONObject jsonposition = jsonmodleinfo.getJSONObject("position");
            int projectid = jsonmodleinfo.getInteger("projectid");


            switch (jsonmodleinfo.getString("modletype")) {
                case Modle.MODLETYPE_CUSTOMIZE: {
                    CUSTOMIZEModle baseModleImp = new CUSTOMIZEModle();
                    baseModleImp.setModleEnable(1);
                    baseModleImp.setModleName("自定义");
                    baseModleImp.setModletype(Modle.MODLETYPE_CUSTOMIZE);
                    baseModleImp.setRefprojectid(projectid);

                    ModleSight modleSight = new ModleSight();
                    modleSight.setChilds(new JSONArray());
                    modleSight.setParents(new JSONArray());
                    modleSight.setRefmodleid(-1);
                    modleSight.setPositionleft(jsonposition.getDouble("left"));
                    modleSight.setPositiontop(jsonposition.getDouble("top"));
                    baseModleImp.setModleSight(modleSight);
                    projectOperaterImp.createCustomizeMoldebusiness(baseModleImp);

                    result.put("modleid", baseModleImp.getModleId() + "");
                    result.put("modlename", baseModleImp.getModleName());
                    break;
                }
                case Modle.MODLETYPE_FILTER: {
                    BaseModleImp baseModleImp = new BaseModleImp();
                    baseModleImp.setModleEnable(1);
                    baseModleImp.setModleName("滤波器");
                    baseModleImp.setModletype(Modle.MODLETYPE_FILTER);
                    baseModleImp.setRefprojectid(projectid);

                    ModleSight modleSight = new ModleSight();
                    modleSight.setChilds(new JSONArray());
                    modleSight.setParents(new JSONArray());
                    modleSight.setRefmodleid(-1);
                    modleSight.setPositionleft(jsonposition.getDouble("left"));
                    modleSight.setPositiontop(jsonposition.getDouble("top"));
                    baseModleImp.setModleSight(modleSight);
                    projectOperaterImp.creatmodlebusiness(baseModleImp);
                    result.put("modleid", baseModleImp.getModleId() + "");
                    result.put("modlename", baseModleImp.getModleName());
                    break;
                }
                case Modle.MODLETYPE_INPUT: {
                    BaseModleImp baseModleImp = new BaseModleImp();
                    baseModleImp.setModleEnable(1);
                    baseModleImp.setModleName("输入集合");
                    baseModleImp.setModletype(Modle.MODLETYPE_INPUT);
                    baseModleImp.setRefprojectid(projectid);

                    ModleSight modleSight = new ModleSight();
                    modleSight.setChilds(new JSONArray());
                    modleSight.setParents(new JSONArray());
                    modleSight.setRefmodleid(-1);
                    modleSight.setPositionleft(jsonposition.getDouble("left"));
                    modleSight.setPositiontop(jsonposition.getDouble("top"));
                    baseModleImp.setModleSight(modleSight);
                    projectOperaterImp.creatmodlebusiness(baseModleImp);
                    result.put("modleid", baseModleImp.getModleId() + "");
                    result.put("modlename", baseModleImp.getModleName());
                    break;
                }
                case Modle.MODLETYPE_MPC: {
                    BaseModleImp baseModleImp = new BaseModleImp();
                    baseModleImp.setModleEnable(1);
                    baseModleImp.setModleName("MPC");
                    baseModleImp.setModletype(Modle.MODLETYPE_MPC);
                    baseModleImp.setRefprojectid(projectid);

                    ModleSight modleSight = new ModleSight();
                    modleSight.setChilds(new JSONArray());
                    modleSight.setParents(new JSONArray());
                    modleSight.setRefmodleid(-1);
                    modleSight.setPositionleft(jsonposition.getDouble("left"));
                    modleSight.setPositiontop(jsonposition.getDouble("top"));
                    baseModleImp.setModleSight(modleSight);
                    projectOperaterImp.creatmodlebusiness(baseModleImp);
                    result.put("modleid", baseModleImp.getModleId() + "");
                    result.put("modlename", baseModleImp.getModleName());
                    break;
                }
                case Modle.MODLETYPE_OUTPUT: {
                    BaseModleImp baseModleImp = new BaseModleImp();
                    baseModleImp.setModleEnable(1);
                    baseModleImp.setModleName("输出集合");
                    baseModleImp.setModletype(Modle.MODLETYPE_OUTPUT);
                    baseModleImp.setRefprojectid(projectid);

                    ModleSight modleSight = new ModleSight();
                    modleSight.setChilds(new JSONArray());
                    modleSight.setParents(new JSONArray());
                    modleSight.setRefmodleid(-1);
                    modleSight.setPositionleft(jsonposition.getDouble("left"));
                    modleSight.setPositiontop(jsonposition.getDouble("top"));
                    baseModleImp.setModleSight(modleSight);
                    projectOperaterImp.creatmodlebusiness(baseModleImp);
                    result.put("modleid", baseModleImp.getModleId() + "");
                    result.put("modlename", baseModleImp.getModleName());
                    break;
                }
                case Modle.MODLETYPE_PID: {
                    BaseModleImp baseModleImp = new BaseModleImp();
                    baseModleImp.setModleEnable(1);
                    baseModleImp.setModleName("PID");
                    baseModleImp.setModletype(Modle.MODLETYPE_PID);
                    baseModleImp.setRefprojectid(projectid);

                    ModleSight modleSight = new ModleSight();
                    modleSight.setChilds(new JSONArray());
                    modleSight.setParents(new JSONArray());
                    modleSight.setRefmodleid(-1);
                    modleSight.setPositionleft(jsonposition.getDouble("left"));
                    modleSight.setPositiontop(jsonposition.getDouble("top"));
                    baseModleImp.setModleSight(modleSight);
                    projectOperaterImp.creatmodlebusiness(baseModleImp);
                    result.put("modleid", baseModleImp.getModleId() + "");
                    result.put("modlename", baseModleImp.getModleName());
                    break;
                }
                default:
                    break;
            }
            result.put("msg", "success");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
        }
        return result.toJSONString();
    }


    @RequestMapping("/deletemodle")
    @ResponseBody
    public String deleteModle(@RequestParam("modleinfo") String modleinfo) {
        JSONObject result = new JSONObject();
        try {
            JSONObject jsonmodleinfo = JSONObject.parseObject(modleinfo);

            int modleid = jsonmodleinfo.getInteger("modleid");
            int projectid = jsonmodleinfo.getInteger("projectid");
            JSONArray parents = jsonmodleinfo.getJSONArray("parent");
            projectOperaterImp.deletemodlebusiness(modleid, projectid, parents);
            result.put("msg", "success");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
        }
        return result.toJSONString();
    }


    @RequestMapping("/createmodlechild")
    @ResponseBody
    public String createModleChild(@RequestParam("modlechildinfo") String modlechildinfo) {
        JSONObject result = new JSONObject();
        try {
            JSONObject jsonmodleinfo = JSONObject.parseObject(modlechildinfo);
            int modleid = jsonmodleinfo.getInteger("modleid");
            int childid = jsonmodleinfo.getInteger("childid");
            projectOperaterImp.createmodlechildbusiness(modleid, childid);
            result.put("msg", "success");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
        }
        return result.toJSONString();
    }


    @RequestMapping("/deletemodlechild")
    @ResponseBody
    public String deleteModleChild(@RequestParam("modlechildinfo") String modlechildinfo) {
        JSONObject result = new JSONObject();
        try {
            JSONObject jsonmodleinfo = JSONObject.parseObject(modlechildinfo);
            int modleid = jsonmodleinfo.getInteger("modleid");
            int childid = jsonmodleinfo.getInteger("childid");
            projectOperaterImp.deletemodlechildbusiness(modleid, childid);
            result.put("msg", "success");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
        }
        return result.toJSONString();
    }

    @RequestMapping("/movemodle")
    @ResponseBody
    public String moveModle(@RequestParam("modleifno") String modleifno) {
        JSONObject result = new JSONObject();
        try {
            JSONObject jsonmodleinfo = JSONObject.parseObject(modleifno);
            int modleid = jsonmodleinfo.getInteger("modleid");
            JSONObject jsonposition = jsonmodleinfo.getJSONObject("position");
            projectOperaterImp.movemodlebusiness(modleid, jsonposition.getDouble("top"), jsonposition.getDouble("left"));
            result.put("msg", "success");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
        }
        return result.toJSONString();
    }


    /***模型信息编辑**/

    @RequestMapping("/updatemodle")
    @ResponseBody
    public String updatemodle(@RequestParam("modleinfo") String modleinfo) {
        JSONObject result = new JSONObject();
//        projectOperaterImp.insert
        int count = 0;
        try {
            JSONObject jsonmodeinfo = JSONObject.parseObject(modleinfo);
            switch (jsonmodeinfo.getString("modletype")) {
                case Modle.MODLETYPE_INPUT: {
                    count = projectOperaterImp.updateIOmodlebusiness(jsonmodeinfo.getInteger("modleId"), jsonmodeinfo.getString("modleName"));
                    result.put("msg", "success");
                    result.put("count", count);
                    break;
                }
                case Modle.MODLETYPE_FILTER: {
                    int modleId = jsonmodeinfo.getInteger("modleId");
                    String modleName = jsonmodeinfo.getString("modleName");
                    String filtermethod = jsonmodeinfo.getString("filtermethod");
                    int filterid = Tool.isNoneString(jsonmodeinfo.getString("filterid")) ? -1 : jsonmodeinfo.getInteger("filterid");
                    FilterModle filterModle = (FilterModle) projectOperaterImp.findModleByid(modleId);
                    filterModle.setModleName(modleName);
                    if (!Tool.isNoneString(filtermethod)) {
                        if (filtermethod.equals(Filter.FILTMETHOD_MVAV)) {
                            int filtercapacity = jsonmodeinfo.getInteger("filtercapacity");
                            MoveAverageFilter moveAverageFilter = new MoveAverageFilter();
                            moveAverageFilter.setFiltermethod(filtermethod);
                            moveAverageFilter.setFilterid(filterid);
                            moveAverageFilter.setFiltercapacity(filtercapacity);
                            moveAverageFilter.setRefmodleid(modleId);
                            filterModle.setFilter(moveAverageFilter);
                        } else {
                            double filteralphe = jsonmodeinfo.getDouble("filteralphe");
                            FirstOrderLagFilter firstOrderLagFilter = new FirstOrderLagFilter();
                            firstOrderLagFilter.setFiltermethod(filtermethod);
                            firstOrderLagFilter.setFilterid(filterid);
                            firstOrderLagFilter.setFilteralphe(filteralphe);
                            firstOrderLagFilter.setRefmodleid(modleId);
                            filterModle.setFilter(firstOrderLagFilter);
                        }
                        count = projectOperaterImp.updateFiltermodlebusiness(filterModle);
                    } else {
                        throw new RuntimeException("filtermethod is null");
                    }


                    result.put("msg", "success");
                    result.put("count", count);
                    break;
                }
                case Modle.MODLETYPE_MPC: {

                    result.put("msg", "success");
                    result.put("count", count);
                    break;
                }
                case Modle.MODLETYPE_PID: {
                    int modleId = jsonmodeinfo.getInteger("modleId");
                    String modleName = jsonmodeinfo.getString("modleName");


                    PIDModle pidmodle = (PIDModle) projectOperaterImp.findModleByid(modleId);
                    pidmodle.setModleName(modleName);

                    List<BaseModlePropertyImp> pidproperties = new ArrayList<>();

                    String kp = jsonmodeinfo.getString("kp");
                    String kpid = jsonmodeinfo.getString("kpid");
                    String kpopcTagName = jsonmodeinfo.getString("kpopcTagName");
                    String kpresourcemodleId = jsonmodeinfo.getString("kpresourcemodleId");
                    String kpresourcemodlepinsId = jsonmodeinfo.getString("kpresourcemodlepinsId");
                    String kpmodleOpcTag = jsonmodeinfo.getString("kpmodleOpcTag");

                    BaseModlePropertyImp kpbasemodleproperty = initpidproperty(kpmodleOpcTag, kpopcTagName, "kp", modleId, kpresourcemodleId, kpresourcemodlepinsId, kp, kpid);//new BaseModlePropertyImp();
                    pidproperties.add(kpbasemodleproperty);


                    String ki = jsonmodeinfo.getString("ki");
                    String kiid = jsonmodeinfo.getString("kiid");
                    String kiopcTagName = jsonmodeinfo.getString("kiopcTagName");
                    String kiresourcemodleId = jsonmodeinfo.getString("kiresourcemodleId");
                    String kiresourcemodlepinsId = jsonmodeinfo.getString("kiresourcemodlepinsId");
                    String kimodleOpcTag = jsonmodeinfo.getString("kimodleOpcTag");
                    BaseModlePropertyImp kibasemodleproperty = initpidproperty(
                            kimodleOpcTag,
                            kiopcTagName,
                            "ki",
                            modleId,
                            kiresourcemodleId,
                            kiresourcemodlepinsId,
                            ki,
                            kiid);//new BaseModlePropertyImp();
                    pidproperties.add(kibasemodleproperty);


                    String kd = jsonmodeinfo.getString("kd");
                    String kdid = jsonmodeinfo.getString("kdid");
                    String kdopcTagName = jsonmodeinfo.getString("kdopcTagName");
                    String kdresourcemodleId = jsonmodeinfo.getString("kdresourcemodleId");
                    String kdresourcemodlepinsId = jsonmodeinfo.getString("kdresourcemodlepinsId");
                    String kdmodleOpcTag = jsonmodeinfo.getString("kdmodleOpcTag");
                    BaseModlePropertyImp kdbasemodleproperty = initpidproperty(
                            kdmodleOpcTag,
                            kdopcTagName,
                            "kd",
                            modleId,
                            kdresourcemodleId,
                            kdresourcemodlepinsId,
                            kd,
                            kdid);//new BaseModlePropertyImp();
                    pidproperties.add(kdbasemodleproperty);

                    String pv = jsonmodeinfo.getString("pv");
                    String pvid = jsonmodeinfo.getString("pvid");
                    String pvopcTagName = jsonmodeinfo.getString("pvopcTagName");
                    String pvresourcemodleId = jsonmodeinfo.getString("pvresourcemodleId");
                    String pvresourcemodlepinsId = jsonmodeinfo.getString("pvresourcemodlepinsId");
                    String pvmodleOpcTag = jsonmodeinfo.getString("pvmodleOpcTag");

                    BaseModlePropertyImp pvbasemodleproperty = initpidproperty(
                            pvmodleOpcTag,
                            pvopcTagName,
                            "pv",
                            modleId,
                            pvresourcemodleId,
                            pvresourcemodlepinsId,
                            pv, pvid);//new BaseModlePropertyImp();
                    pidproperties.add(pvbasemodleproperty);

                    String sp = jsonmodeinfo.getString("sp");
                    String spid = jsonmodeinfo.getString("spid");
                    String spopcTagName = jsonmodeinfo.getString("spopcTagName");
                    String spresourcemodleId = jsonmodeinfo.getString("spresourcemodleId");
                    String spresourcemodlepinsId = jsonmodeinfo.getString("spresourcemodlepinsId");
                    String spmodleOpcTag = jsonmodeinfo.getString("spmodleOpcTag");
                    BaseModlePropertyImp spbasemodleproperty = initpidproperty(
                            spmodleOpcTag,
                            spopcTagName,
                            "sp",
                            modleId,
                            spresourcemodleId,
                            spresourcemodlepinsId,
                            sp, spid);//new BaseModlePropertyImp();
                    pidproperties.add(spbasemodleproperty);

                    String mv = jsonmodeinfo.getString("mv");
                    String mvid = jsonmodeinfo.getString("mvid");
                    String mvopcTagName = jsonmodeinfo.getString("mvopcTagName");
                    String mvresourcemodleId = jsonmodeinfo.getString("mvresourcemodleId");
                    String mvresourcemodlepinsId = jsonmodeinfo.getString("mvresourcemodlepinsId");
                    String mvmodleOpcTag = jsonmodeinfo.getString("mvmodleOpcTag");
                    BaseModlePropertyImp mvbasemodleproperty = initpidproperty(
                            mvmodleOpcTag,
                            mvopcTagName,
                            "mv",
                            modleId,
                            mvresourcemodleId,
                            mvresourcemodlepinsId,
                            mv, mvid);//new BaseModlePropertyImp();
                    pidproperties.add(mvbasemodleproperty);

                    String ff = jsonmodeinfo.getString("ff");
                    String ffid = jsonmodeinfo.getString("ffid");
                    String ffopcTagName = jsonmodeinfo.getString("ffopcTagName");
                    String ffresourcemodleId = jsonmodeinfo.getString("ffresourcemodleId");
                    String ffresourcemodlepinsId = jsonmodeinfo.getString("ffresourcemodlepinsId");
                    String ffmodleOpcTag = jsonmodeinfo.getString("ffmodleOpcTag");
                    BaseModlePropertyImp ffbasemodleproperty = initpidproperty(
                            ffmodleOpcTag,
                            ffopcTagName,
                            "ff",
                            modleId,
                            ffresourcemodleId,
                            ffresourcemodlepinsId,
                            ff, ffid);//new BaseModlePropertyImp();
                    pidproperties.add(ffbasemodleproperty);

                    count = projectOperaterImp.updatepidmodlebusiness(pidmodle, pidproperties);
                    result.put("msg", "success");
                    result.put("count", count);
                    break;
                }
                case Modle.MODLETYPE_OUTPUT: {
                    count = projectOperaterImp.updateIOmodlebusiness(jsonmodeinfo.getInteger("modleId"), jsonmodeinfo.getString("modleName"));
                    result.put("msg", "success");
                    result.put("count", count);
                    break;
                }
                case Modle.MODLETYPE_CUSTOMIZE: {
                    jsonmodeinfo.getInteger("modleId");
                    jsonmodeinfo.getString("modleName");
                    String code = jsonmodeinfo.getString("code");

                    CUSTOMIZEModle customizeModle = (CUSTOMIZEModle) projectOperaterImp.findModleByid(jsonmodeinfo.getInteger("modleId"));
                    customizeModle.setModleName(jsonmodeinfo.getString("modleName"));
                    projectOperaterImp.updateCustomizmodlebusiness(customizeModle, code);


                    result.put("msg", "success");
                    result.put("count", count);
                    break;
                }
                default: {
                    result.put("msg", "error");
                    break;
                }

            }
            return result.toJSONString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
        }
        return result.toJSONString();
    }


    @RequestMapping("/savenewmodle")
    @ResponseBody
    public String savenewmodle(String modleinfo) {
        JSONObject result = new JSONObject();
//        projectOperaterImp.insert

        JSONObject jsonmodle = JSONObject.parseObject(modleinfo);
        result.put("msg", "success");
        return result.toJSONString();
    }


    /**
     * modle property edit
     */

    @RequestMapping("/getmodleproperties")
    @ResponseBody
    public String getmodleproperties(@RequestParam("modleid") int modleid, @RequestParam("pindir") String pindir) {
        try {
            List<BaseModlePropertyImp> baseModlePropertyImps = projectOperaterImp.findBaseModlePropertyByModleid(modleid);
            JSONArray modleproperties = new JSONArray();
            for (BaseModlePropertyImp baseModlePropertyImp : baseModlePropertyImps) {

                if (baseModlePropertyImp.getPindir().equals(pindir)) {
                    JSONObject property = new JSONObject();
                    property.put("modlepinsId", baseModlePropertyImp.getModlepinsId());
                    property.put("refmodleId", baseModlePropertyImp.getRefmodleId());
                    property.put("modlePinName", baseModlePropertyImp.getModlePinName());
                    property.put("modleOpcTag", baseModlePropertyImp.getModleOpcTag());
                    property.put("opcTagName", baseModlePropertyImp.getOpcTagName());
                    modleproperties.add(property);
                }

            }
            return Tool.sendLayuiPage(baseModlePropertyImps.size(), modleproperties).toJSONString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    @RequestMapping("/updatemodleproperties")
    @ResponseBody
    public String updatemodleproperties(@RequestParam("modlepropertyinfo") String modlepropertyinfo) {
        JSONObject result = new JSONObject();
        try {
            JSONObject jsonmodlepropertyinfo = JSONObject.parseObject(modlepropertyinfo);
            int count = 0;
            switch (jsonmodlepropertyinfo.getString("modletype")) {

                case Modle.MODLETYPE_MPC: {
                    MPCModleProperty mpcModleProperty = new MPCModleProperty();
                    mpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    mpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("modleOpcTag"));
                    mpcModleProperty.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    mpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));
                    JSONObject resource = new JSONObject();
                    resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                    mpcModleProperty.setResource(resource);
                    mpcModleProperty.setQ(jsonmodlepropertyinfo.getDouble("Q"));
                    mpcModleProperty.setDmvHigh(jsonmodlepropertyinfo.getDouble("dmvHigh"));
                    mpcModleProperty.setDeadZone(jsonmodlepropertyinfo.getDouble("deadZone"));
                    mpcModleProperty.setFunelinitValue(jsonmodlepropertyinfo.getDouble("funelinitValue"));
                    mpcModleProperty.setR(jsonmodlepropertyinfo.getDouble("R"));
                    mpcModleProperty.setDmvLow(jsonmodlepropertyinfo.getDouble("dmvLow"));
                    mpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("referTrajectoryCoef"));
                    mpcModleProperty.setFunneltype(jsonmodlepropertyinfo.getString("funneltype"));
//                    mpcModleProperty.setPinEnable(jsonmodlepropertyinfo.getInteger("pinEnable"));
                    mpcModleProperty.setTracoefmethod(jsonmodlepropertyinfo.getString("tracoefmethod"));
                    mpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    mpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    count = projectOperaterImp.updateMPCModleProperty(mpcModleProperty);
                    break;
                }
                case Modle.MODLETYPE_INPUT: {
                    BaseModlePropertyImp propertyImp = projectOperaterImp.findBaseModlePropertyByid(jsonmodlepropertyinfo.getInteger("modlepinsId"));
                    propertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modleOpcTag"));
                    propertyImp.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));
                    JSONObject resource = new JSONObject();
                    resource.put("resource", ModleProperty.SOURCE_TYPE_OPC);
                    propertyImp.setResource(resource);
                    count = projectOperaterImp.updateBaseModleProperty(propertyImp);
                    break;
                }
                case Modle.MODLETYPE_FILTER: {
                    BaseModlePropertyImp propertyImp = projectOperaterImp.findBaseModlePropertyByid(jsonmodlepropertyinfo.getInteger("modlepinsId"));
                    propertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));

                    //数据源
                    if (jsonmodlepropertyinfo.getString("pindir").equals(ModleProperty.PINDIRINPUT)) {
                        String refmodleId = jsonmodlepropertyinfo.getString("resourcemodleId");
                        String refmodlepinsId = jsonmodlepropertyinfo.getString("resourcemodlepinsId");
                        if (Tool.isNoneString(refmodleId) || Tool.isNoneString(refmodlepinsId)) {
                            throw new RuntimeException("pin data resource is none!");
                        }
                        JSONObject resource = new JSONObject();

                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(refmodleId));
                        resource.put("modlepinsId", Integer.parseInt(refmodlepinsId));
                        propertyImp.setResource(resource);
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MEMORY);
                        propertyImp.setResource(resource);
                    }
//                    mpcModleProperty.setPinEnable(jsonmodlepropertyinfo.getInteger("pinEnable"));
                    propertyImp.setPindir(jsonmodlepropertyinfo.getString("pindir"));
                    propertyImp.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_BASE);
                    count = projectOperaterImp.updateBaseModleProperty(propertyImp);
                    break;
                }
                case Modle.MODLETYPE_CUSTOMIZE: {

                    BaseModlePropertyImp propertyImp = projectOperaterImp.findBaseModlePropertyByid(jsonmodlepropertyinfo.getInteger("modlepinsId"));
                    propertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modleOpcTag"));
                    propertyImp.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));

                    //数据源
                    if (jsonmodlepropertyinfo.getString("pindir").equals(ModleProperty.PINDIRINPUT)) {
                        String refmodleId = jsonmodlepropertyinfo.getString("resourcemodleId");
                        String refmodlepinsId = jsonmodlepropertyinfo.getString("resourcemodlepinsId");

                        String modlePincontantvalue = jsonmodlepropertyinfo.getString("modlePincontantvalue");

                        if (Tool.isNoneString(refmodleId) || Tool.isNoneString(refmodlepinsId)) {

                            if (Tool.isNoneString(modlePincontantvalue)) {
                                throw new RuntimeException("pin data resource or constant value is none!");
                            } else {
                                JSONObject resource = new JSONObject();
                                resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                                resource.put("value", Double.parseDouble(modlePincontantvalue));
                                propertyImp.setModleOpcTag("" + Double.parseDouble(modlePincontantvalue));
                                propertyImp.setResource(resource);

                                propertyImp.setOpcTagName("" + Double.parseDouble(modlePincontantvalue));
                            }

                        } else {
                            JSONObject resource = new JSONObject();

                            resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                            resource.put("modleId", Integer.parseInt(refmodleId));
                            resource.put("modlepinsId", Integer.parseInt(refmodlepinsId));
                            propertyImp.setResource(resource);
                        }

                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MEMORY);
                        propertyImp.setResource(resource);
                    }
//                    mpcModleProperty.setPinEnable(jsonmodlepropertyinfo.getInteger("pinEnable"));
                    propertyImp.setPindir(jsonmodlepropertyinfo.getString("pindir"));
                    propertyImp.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_BASE);
                    count = projectOperaterImp.updateBaseModleProperty(propertyImp);
                    break;
                }
                case Modle.MODLETYPE_PID: {
                    BaseModlePropertyImp propertyImp = projectOperaterImp.findBaseModlePropertyByid(jsonmodlepropertyinfo.getInteger("modlepinsId"));
                    propertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));
                    JSONObject resource = new JSONObject();
                    resource.put("resource", ModleProperty.SOURCE_TYPE_MEMORY);
                    propertyImp.setResource(resource);

//                    mpcModleProperty.setPinEnable(jsonmodlepropertyinfo.getInteger("pinEnable"));
                    propertyImp.setPindir(jsonmodlepropertyinfo.getString("pindir"));
                    propertyImp.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_BASE);
                    count = projectOperaterImp.updateBaseModleProperty(propertyImp);
                    break;
                }
                default: {
                    BaseModlePropertyImp propertyImp = new BaseModlePropertyImp();
                    propertyImp.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    propertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modleOpcTag"));
                    propertyImp.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));
                    JSONObject resource = new JSONObject();
                    resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                    propertyImp.setResource(resource);

//                    mpcModleProperty.setPinEnable(jsonmodlepropertyinfo.getInteger("pinEnable"));
                    propertyImp.setPindir(ModleProperty.PINDIRINPUT);
                    propertyImp.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_BASE);
                    count = projectOperaterImp.updateBaseModleProperty(propertyImp);
                    break;
                }

            }

            result.put("msg", "success");
            result.put("count", count);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
        }
        return result.toJSONString();
    }


    @RequestMapping("/deletemodleproperties")
    @ResponseBody
    public String deletemodleproperties(@RequestParam("modlepropertyid") int modlepropertyid) {
        JSONObject result = new JSONObject();
        try {
            int count = projectOperaterImp.deleteBaseModlePropertyByid(modlepropertyid);
            result.put("msg", "success");
            result.put("count", count);

        } catch (Exception e) {
            result.put("msg", "error");
            logger.error(e.getMessage(), e);
        }
        return result.toJSONString();
    }


    @RequestMapping("/createmodleproperties")
    @ResponseBody
    public String createmodleproperties(@RequestParam("modlepropertyinfo") String modlepropertyinfo) {

        JSONObject result = new JSONObject();
        try {
            JSONObject jsonmodlepropertyinfo = JSONObject.parseObject(modlepropertyinfo);
            int count = 0;
            switch (jsonmodlepropertyinfo.getString("modletype")) {

                case Modle.MODLETYPE_MPC: {
                    MPCModleProperty mpcModleProperty = new MPCModleProperty();
                    mpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    mpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("modleOpcTag"));
                    mpcModleProperty.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    mpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));
                    JSONObject resource = new JSONObject();
                    resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                    mpcModleProperty.setResource(resource);
                    mpcModleProperty.setQ(jsonmodlepropertyinfo.getDouble("Q"));
                    mpcModleProperty.setDmvHigh(jsonmodlepropertyinfo.getDouble("dmvHigh"));
                    mpcModleProperty.setDeadZone(jsonmodlepropertyinfo.getDouble("deadZone"));
                    mpcModleProperty.setFunelinitValue(jsonmodlepropertyinfo.getDouble("funelinitValue"));
                    mpcModleProperty.setR(jsonmodlepropertyinfo.getDouble("R"));
                    mpcModleProperty.setDmvLow(jsonmodlepropertyinfo.getDouble("dmvLow"));
                    mpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("referTrajectoryCoef"));
                    mpcModleProperty.setFunneltype(jsonmodlepropertyinfo.getString("funneltype"));
//                    mpcModleProperty.setPinEnable(jsonmodlepropertyinfo.getInteger("pinEnable"));
                    mpcModleProperty.setTracoefmethod(jsonmodlepropertyinfo.getString("tracoefmethod"));
                    mpcModleProperty.setPindir(jsonmodlepropertyinfo.getString("pindir"));
                    mpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    count = projectOperaterImp.insertMPCModleProperty(mpcModleProperty);
                    break;
                }
                case Modle.MODLETYPE_INPUT: {
                    BaseModlePropertyImp propertyImp = new BaseModlePropertyImp();
                    propertyImp.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    propertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modleOpcTag"));
                    propertyImp.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));
                    JSONObject resource = new JSONObject();
                    resource.put("resource", ModleProperty.SOURCE_TYPE_OPC);
                    propertyImp.setResource(resource);

//                    mpcModleProperty.setPinEnable(jsonmodlepropertyinfo.getInteger("pinEnable"));
                    propertyImp.setPindir(jsonmodlepropertyinfo.getString("pindir"));
                    propertyImp.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_BASE);
                    count = projectOperaterImp.insertBaseModleProperty(propertyImp);
                    break;
                }
                case Modle.MODLETYPE_FILTER: {
                    BaseModlePropertyImp propertyImp = new BaseModlePropertyImp();
                    propertyImp.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    propertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));

                    //数据源
                    if (jsonmodlepropertyinfo.getString("pindir").equals(ModleProperty.PINDIRINPUT)) {
                        String refmodleId = jsonmodlepropertyinfo.getString("resourcemodleId");
                        String refmodlepinsId = jsonmodlepropertyinfo.getString("resourcemodlepinsId");
                        if (Tool.isNoneString(refmodleId) || Tool.isNoneString(refmodlepinsId)) {
                            throw new RuntimeException("pin data resource is none!");
                        }
                        JSONObject resource = new JSONObject();

                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(refmodleId));
                        resource.put("modlepinsId", Integer.parseInt(refmodlepinsId));
                        propertyImp.setResource(resource);
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MEMORY);
                        propertyImp.setResource(resource);
                    }
//                    mpcModleProperty.setPinEnable(jsonmodlepropertyinfo.getInteger("pinEnable"));
                    propertyImp.setPindir(jsonmodlepropertyinfo.getString("pindir"));
                    propertyImp.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_BASE);
                    count = projectOperaterImp.insertBaseModleProperty(propertyImp);
                    break;
                }
                case Modle.MODLETYPE_CUSTOMIZE: {

                    BaseModlePropertyImp propertyImp = new BaseModlePropertyImp();
                    propertyImp.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    propertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modleOpcTag"));
                    propertyImp.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));

                    //数据源
                    if (jsonmodlepropertyinfo.getString("pindir").equals(ModleProperty.PINDIRINPUT)) {
                        String refmodleId = jsonmodlepropertyinfo.getString("resourcemodleId");
                        String refmodlepinsId = jsonmodlepropertyinfo.getString("resourcemodlepinsId");

                        String modlePincontantvalue = jsonmodlepropertyinfo.getString("modlePincontantvalue");

                        if (Tool.isNoneString(refmodleId) || Tool.isNoneString(refmodlepinsId)) {

                            if (Tool.isNoneString(modlePincontantvalue)) {
                                throw new RuntimeException("pin data resource or constant value is none!");
                            } else {
                                JSONObject resource = new JSONObject();
                                resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                                resource.put("value", Double.parseDouble(modlePincontantvalue));
                                propertyImp.setModleOpcTag("" + Double.parseDouble(modlePincontantvalue));
                                propertyImp.setResource(resource);
                            }
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                            resource.put("modleId", Integer.parseInt(refmodleId));
                            resource.put("modlepinsId", Integer.parseInt(refmodlepinsId));
                            propertyImp.setResource(resource);
                        }

                    } else {

                        //输出默认数据源在memory
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MEMORY);
                        propertyImp.setResource(resource);
                    }
//                    mpcModleProperty.setPinEnable(jsonmodlepropertyinfo.getInteger("pinEnable"));
                    propertyImp.setPindir(jsonmodlepropertyinfo.getString("pindir"));
                    propertyImp.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_BASE);
                    count = projectOperaterImp.insertBaseModleProperty(propertyImp);
                    break;
                }
                case Modle.MODLETYPE_PID: {


                    BaseModlePropertyImp propertyImp = new BaseModlePropertyImp();
                    propertyImp.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    propertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));

                    //数据源
                    if (jsonmodlepropertyinfo.getString("pindir").equals(ModleProperty.PINDIROUTPUT)) {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MEMORY);
                        propertyImp.setResource(resource);
                    } else {
                        throw new RuntimeException("pid add property.the  type only output");
                    }
//                    mpcModleProperty.setPinEnable(jsonmodlepropertyinfo.getInteger("pinEnable"));
                    propertyImp.setPindir(jsonmodlepropertyinfo.getString("pindir"));
                    propertyImp.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_BASE);
                    count = projectOperaterImp.insertBaseModleProperty(propertyImp);
                    break;
                }
                default: {
                    BaseModlePropertyImp propertyImp = new BaseModlePropertyImp();
                    propertyImp.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    propertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modleOpcTag"));
                    propertyImp.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));

                    JSONObject resource = new JSONObject();
                    resource.put("resource", ModleProperty.SOURCE_TYPE_OPC);
                    propertyImp.setResource(resource);

//                    mpcModleProperty.setPinEnable(jsonmodlepropertyinfo.getInteger("pinEnable"));
                    propertyImp.setPindir(ModleProperty.PINDIRINPUT);
                    propertyImp.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_BASE);
                    count = projectOperaterImp.insertBaseModleProperty(propertyImp);
                    break;
                }

            }

            result.put("msg", "success");
            result.put("count", count);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
        }
        return result.toJSONString();
    }


    /****/


    /***view***/

    @RequestMapping("/vieweditproject")
    public ModelAndView viewEditProject(@RequestParam("projectid") int projectid) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            Project project = projectOperaterImp.findProjectById(projectid);
            modelAndView.setViewName("projectedit");
            modelAndView.addObject("project", project);
            return modelAndView;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @RequestMapping("/vieweditmodle")
    public ModelAndView viewEditModle(@RequestParam("modleId") int modleId) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            BaseModleImp modle = (BaseModleImp) projectOperaterImp.findModleByid(modleId);
            switch (modle.getModletype()) {
                case Modle.MODLETYPE_INPUT: {
                    modelAndView.setViewName("inputmodleedit");
                    modelAndView.addObject("inputmodle", (INModle) modle);
                    return modelAndView;
                }

                case Modle.MODLETYPE_OUTPUT: {

                    break;
                }
                case Modle.MODLETYPE_FILTER: {

                    modelAndView.setViewName("filtermodleedit");
                    modelAndView.addObject("filtermodle", (FilterModle) modle);
                    return modelAndView;
                }
                case Modle.MODLETYPE_CUSTOMIZE: {
                    CUSTOMIZEModle customizeModle = (CUSTOMIZEModle) modle;
                    modelAndView.setViewName("customizemodleedit");
                    modelAndView.addObject("customizemodle", customizeModle);
                    modelAndView.addObject("codecontext", FileHelp.readInfoFromFile(customizeModle.getCustomizepyname()));
                    return modelAndView;
                }
                case Modle.MODLETYPE_MPC: {

                    break;
                }
                case Modle.MODLETYPE_PID: {
                    PIDModle pidModle = (PIDModle) modle;
                    List<BaseModlePropertyImp> points = projectOperaterImp.findparentmodleboutputpinsbusiness(pidModle.getModleId());
                    modelAndView.setViewName("pidmodleedit");
                    modelAndView.addObject("pidmodle", pidModle);
                    modelAndView.addObject("kp", Tool.selectmodleProperyByPinname("kp", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));
                    modelAndView.addObject("ki", Tool.selectmodleProperyByPinname("ki", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));
                    modelAndView.addObject("kd", Tool.selectmodleProperyByPinname("kd", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));

                    modelAndView.addObject("pv", Tool.selectmodleProperyByPinname("pv", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));

                    modelAndView.addObject("sp", Tool.selectmodleProperyByPinname("sp", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));

                    modelAndView.addObject("mv", Tool.selectmodleProperyByPinname("mv", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));

                    modelAndView.addObject("ff", Tool.selectmodleProperyByPinname("ff", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));

                    modelAndView.addObject("points", points);
                    return modelAndView;
                }
                default: {

                    break;
                }

            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @RequestMapping("/viewaddmodleproperty")
    public ModelAndView viewaddmodleproperty(@RequestParam("modletype") String modletype, @RequestParam("modleId") int modleId, @RequestParam("pindir") String pindir) {
        ModelAndView modelAndView = new ModelAndView();
        try {
//            JSONObject jsonproperyinfo = JSONObject.parseObject(properyinfo);
            switch (modletype) {
                case Modle.MODLETYPE_INPUT: {
                    modelAndView.setViewName("inputmodlepropertyadd");
                    modelAndView.addObject("modleId", modleId);
                    modelAndView.addObject("modletype", modletype);
                    modelAndView.addObject("pindir", pindir);
                    String pointinfo = HttpUtils.PostData(oceandir + "/pointoperate/getalpoints", null);
                    JSONObject jsonpoints = new JSONObject();
                    if (pointinfo != null) {
                        jsonpoints = JSONObject.parseObject(pointinfo);
                    }
                    List<BaseModlePropertyImp> baseModlePropertyImpList = new ArrayList<>();
                    if (jsonpoints.getString("msg").equals("success")) {
                        for (int index = 0; index < jsonpoints.getJSONArray("data").size(); index++) {
                            BaseModlePropertyImp basemodleproperty = new BaseModlePropertyImp();
                            basemodleproperty.setModleOpcTag(jsonpoints.getJSONArray("data").getJSONObject(index).getString("tag"));
                            basemodleproperty.setOpcTagName(jsonpoints.getJSONArray("data").getJSONObject(index).getString("tagname"));
                            baseModlePropertyImpList.add(basemodleproperty);
                        }
                    }
                    modelAndView.addObject("points", baseModlePropertyImpList);
                    return modelAndView;
                }

                case Modle.MODLETYPE_OUTPUT: {


                    break;
                }
                case Modle.MODLETYPE_FILTER: {

                    /***
                     * 1查找工程中的该父节点的输出位号
                     * */
                    if (pindir.equals(ModleProperty.PINDIRINPUT)) {
                        modelAndView.setViewName("filtermodlepropertyaddinput");
                        modelAndView.addObject("modleId", modleId);
                        modelAndView.addObject("modletype", modletype);
                        modelAndView.addObject("pindir", pindir);
                        List<BaseModlePropertyImp> points = projectOperaterImp.findparentmodleboutputpinsbusiness(modleId);
                        modelAndView.addObject("points", points);
                        return modelAndView;
                    } else if (pindir.equals(ModleProperty.PINDIROUTPUT)) {
                        modelAndView.setViewName("filtermodlepropertyaddoutput");
                        modelAndView.addObject("modleId", modleId);
                        modelAndView.addObject("modletype", modletype);
                        modelAndView.addObject("pindir", pindir);
                        List<BaseModlePropertyImp> points = projectOperaterImp.findthismodleinputpinsbusiness(modleId);
                        modelAndView.addObject("points", points);
                        return modelAndView;

                    }

                }
                case Modle.MODLETYPE_CUSTOMIZE: {

                    if (pindir.equals(ModleProperty.PINDIRINPUT)) {
                        modelAndView.setViewName("customizmodlepropertyaddinput");
                        modelAndView.addObject("modleId", modleId);
                        modelAndView.addObject("modletype", modletype);
                        modelAndView.addObject("pindir", pindir);
                        List<BaseModlePropertyImp> points = projectOperaterImp.findparentmodleboutputpinsbusiness(modleId);
                        modelAndView.addObject("points", points);
                        return modelAndView;
                    } else if (pindir.equals(ModleProperty.PINDIROUTPUT)) {
                        modelAndView.setViewName("customizmodlepropertyaddoutput");
                        modelAndView.addObject("modleId", modleId);
                        modelAndView.addObject("modletype", modletype);
                        modelAndView.addObject("pindir", pindir);
                        return modelAndView;

                    }

                }
                case Modle.MODLETYPE_MPC: {

                    break;
                }
                case Modle.MODLETYPE_PID: {
                    if (pindir.equals(ModleProperty.PINDIROUTPUT)) {
                        modelAndView.setViewName("pidmodlepropertyaddoutput");
                        modelAndView.addObject("modleId", modleId);
                        modelAndView.addObject("modletype", modletype);
                        modelAndView.addObject("pindir", pindir);
                        return modelAndView;
                    }
                }
                default: {

                    break;
                }

            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @RequestMapping("/viewupdatemodleproperty")
    public ModelAndView viewupdatemodleproperty(@RequestParam("modletype") String modletype, @RequestParam("modlepinsId") int modlepinsId) {
        ModelAndView modelAndView = new ModelAndView();
        try {
//            JSONObject jsonproperyinfo = JSONObject.parseObject(properyinfo);
            switch (modletype) {
                case Modle.MODLETYPE_INPUT: {

                    BaseModlePropertyImp baseModlePropertyImp = projectOperaterImp.findBaseModlePropertyByid(modlepinsId);
                    modelAndView.setViewName("inputmodlepropertyupdate");
                    modelAndView.addObject("modletype", modletype);
                    modelAndView.addObject("baseModlePropertyImp", baseModlePropertyImp);
                    String pointinfo = HttpUtils.PostData(oceandir + "/pointoperate/getalpoints", null);
                    JSONObject jsonpoints = JSONObject.parseObject(pointinfo);
                    List<BaseModlePropertyImp> baseModlePropertyImpList = new ArrayList<>();
                    if (jsonpoints.getString("msg").equals("success")) {
                        for (int index = 0; index < jsonpoints.getJSONArray("data").size(); index++) {
                            BaseModlePropertyImp basemodleproperty = new BaseModlePropertyImp();
                            basemodleproperty.setModleOpcTag(jsonpoints.getJSONArray("data").getJSONObject(index).getString("tag"));
                            basemodleproperty.setOpcTagName(jsonpoints.getJSONArray("data").getJSONObject(index).getString("tagname"));
                            baseModlePropertyImpList.add(basemodleproperty);
                        }
                    }
                    modelAndView.addObject("points", baseModlePropertyImpList);
                    return modelAndView;
                }

                case Modle.MODLETYPE_OUTPUT: {

                    break;
                }
                case Modle.MODLETYPE_FILTER: {
                    BaseModlePropertyImp baseModlePropertyImp = projectOperaterImp.findBaseModlePropertyByid(modlepinsId);
                    if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                        modelAndView.setViewName("filtermodlepropertyupdateinput");
                        List<BaseModlePropertyImp> points = projectOperaterImp.findparentmodleboutputpinsbusiness(baseModlePropertyImp.getRefmodleId());
                        modelAndView.addObject("points", points);
                    } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                        modelAndView.setViewName("filtermodlepropertyupdateoutput");
                        List<BaseModlePropertyImp> points = projectOperaterImp.findthismodleinputpinsbusiness(baseModlePropertyImp.getRefmodleId());
                        modelAndView.addObject("points", points);
                    }
                    modelAndView.addObject("modletype", modletype);
                    modelAndView.addObject("baseModlePropertyImp", baseModlePropertyImp);

                    return modelAndView;
                }
                case Modle.MODLETYPE_CUSTOMIZE: {

                    BaseModlePropertyImp baseModlePropertyImp = projectOperaterImp.findBaseModlePropertyByid(modlepinsId);
                    if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                        modelAndView.setViewName("customizmodlepropertyupdateinput");
                        List<BaseModlePropertyImp> points = projectOperaterImp.findparentmodleboutputpinsbusiness(baseModlePropertyImp.getRefmodleId());
                        modelAndView.addObject("points", points);

                    } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                        modelAndView.setViewName("customizmodlepropertyupdateoutput");
                    }
                    modelAndView.addObject("modletype", modletype);
                    modelAndView.addObject("customizmodle", baseModlePropertyImp);
                    return modelAndView;

                }
                case Modle.MODLETYPE_MPC: {

                    break;
                }
                case Modle.MODLETYPE_PID: {
                    BaseModlePropertyImp baseModlePropertyImp = projectOperaterImp.findBaseModlePropertyByid(modlepinsId);
                    modelAndView.setViewName("pidmodlepropertyupdateoutput");
                    modelAndView.addObject("modletype", modletype);
                    modelAndView.addObject("pidmodle", baseModlePropertyImp);

                    return modelAndView;
                }
                default: {

                    break;
                }

            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    /**
     * @param pinname
     */
    private BaseModlePropertyImp initpidproperty(String modleOpcTag, String opcTagName, String pinname, int modleId, String resourcemodleId, String resourcemodlepinsId, String properyconstant, String propertyid) {
        BaseModlePropertyImp kpbasemodleproperty = new BaseModlePropertyImp();
        kpbasemodleproperty.setModlePinName(pinname);
        kpbasemodleproperty.setOpcTagName(opcTagName);
        kpbasemodleproperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_BASE);
        kpbasemodleproperty.setPindir(ModleProperty.PINDIRINPUT);
        kpbasemodleproperty.setRefmodleId(modleId);
        kpbasemodleproperty.setPinEnable(1);

        if (Tool.isNoneString(resourcemodleId) || Tool.isNoneString(resourcemodlepinsId)) {
            if (Tool.isNoneString(properyconstant)) {
                throw new RuntimeException(pinname + "resource or constant value is none");
            } else {
                JSONObject resource = new JSONObject();
                resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                resource.put("value", Double.parseDouble(properyconstant));
                kpbasemodleproperty.setResource(resource);
                kpbasemodleproperty.setModleOpcTag("" + Double.parseDouble(properyconstant));
                kpbasemodleproperty.setOpcTagName(null);
            }

        } else {
            JSONObject resource = new JSONObject();
            resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
            resource.put("modlepinsId", Integer.parseInt(resourcemodlepinsId));
            resource.put("modleId", Integer.parseInt(resourcemodleId));
            kpbasemodleproperty.setResource(resource);
            kpbasemodleproperty.setModleOpcTag(modleOpcTag);
        }

        if (Tool.isNoneString(propertyid)) {
            kpbasemodleproperty.setModlepinsId(-1);
        } else {
            kpbasemodleproperty.setModlepinsId(Integer.parseInt(propertyid));
        }
        return kpbasemodleproperty;
    }
}
