package hs.industry.ailab.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import hs.industry.ailab.dao.mysql.service.ProjectOperaterImp;
import hs.industry.ailab.entity.ModleSight;
import hs.industry.ailab.entity.Project;
import hs.industry.ailab.entity.ProjectManager;
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
import hs.industry.ailab.utils.help.Tool;
import hs.industry.ailab.utils.httpclient.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/10 15:36
 */
@Controller
@RequestMapping("/projectedit")
public class ProjectEdit {
    private Logger logger = LoggerFactory.getLogger(ProjectEdit.class);
    private static Pattern pvpattern = Pattern.compile("(^pv(\\d+)$)");
    private static Pattern ffpattern = Pattern.compile("(^ff(\\d+)$)");
    private static Pattern mvpattern = Pattern.compile("(^mv(\\d+)$)");

    @Value("${mpcpinnumber}")
    private int mpcpinnumber;

    @Value("${oceandir}")
    private String oceandir;

    @Autowired
    private ProjectOperaterImp projectOperaterImp;

    @Autowired
    private ProjectManager projectManager;

    @RequestMapping("/savenewproject")
    @ResponseBody
    public String savenewproject(String projectinfo) {
        JSONObject result = new JSONObject();

        try {
            JSONObject jsonproject = JSONObject.parseObject(projectinfo);

            Project project= new Project();
            project.setName(jsonproject.getString("name"));
            project.setRunperiod(15);
            projectOperaterImp.insertProject(project);
            result.put("msg", "success");
            result.put("name",jsonproject.getString("name"));
            result.put("projectid",project.getProjectid()+"");
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            result.put("msg", "error");
        }

        return result.toJSONString();
    }

    @RequestMapping("/loadallproject")
    @ResponseBody
    public String loadallproject() {
        JSONObject result = new JSONObject();
        List<Project> projectList = projectOperaterImp.findAllProject();
        result.put("contrlmodles", projectList);
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
                                inputjson.put("value", ((MPCModleProperty) modleProperty).getValue());
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", mpcModleProperty.getModlePinName());
                                outputjson.put("name", mpcModleProperty.getOpcTagName());
                                outputjson.put("value", ((MPCModleProperty) modleProperty).getValue());
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        } else if (modleProperty instanceof BaseModlePropertyImp) {
                            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                inputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                inputjson.put("value", baseModlePropertyImp.getValue());
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                outputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                outputjson.put("value", baseModlePropertyImp.getValue());
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


    @RequestMapping("/getprojectrunstatus")
    @ResponseBody
    public String getprojectrunstatus(@RequestParam("projectid") int projectid) {
        JSONObject result = new JSONObject();

        try {
//            Modle modle =projectOperaterImp.findModleByid(140);

            Project project = projectManager.getProjectPool().get(projectid);//projectOperaterImp.findProjectById(projectid);
            if(project==null){
                result.put("msg", "error");
                return result.toJSONString();
            }
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
                                inputjson.put("value", Tool.getSpecalScale(4,((MPCModleProperty) modleProperty).getValue()));
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", mpcModleProperty.getModlePinName());
                                outputjson.put("name", mpcModleProperty.getOpcTagName());
                                outputjson.put("value", Tool.getSpecalScale(4,((MPCModleProperty) modleProperty).getValue()));
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        } else if (modleProperty instanceof BaseModlePropertyImp) {
                            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                inputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                inputjson.put("value", Tool.getSpecalScale(4,baseModlePropertyImp.getValue()));
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                outputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                outputjson.put("value", Tool.getSpecalScale(4,baseModlePropertyImp.getValue()));
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        }


                    }

                    jsonmodule.put("top", mpcModle.getModleSight().getPositiontop());
                    jsonmodule.put("left", mpcModle.getModleSight().getPositionleft());
                    jsonmodule.put("errormsg",mpcModle.getErrormsg());

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
                                inputjson.put("value", Tool.getSpecalScale(4,mpcModleProperty.getValue()));
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", mpcModleProperty.getModlePinName());
                                outputjson.put("name", mpcModleProperty.getOpcTagName());
                                outputjson.put("value", Tool.getSpecalScale(4,mpcModleProperty.getValue()));
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        } else if (modleProperty instanceof BaseModlePropertyImp) {
                            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                inputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                inputjson.put("value",Tool.getSpecalScale(4,baseModlePropertyImp.getValue()));
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                outputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                outputjson.put("value", Tool.getSpecalScale(4,baseModlePropertyImp.getValue()));
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        }


                    }
                    jsonmodule.put("top", pidModle.getModleSight().getPositiontop());
                    jsonmodule.put("left", pidModle.getModleSight().getPositionleft());
                    jsonmodule.put("errormsg",pidModle.getErrormsg());
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
                                inputjson.put("value", Tool.getSpecalScale(4,mpcModleProperty.getValue()));
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", mpcModleProperty.getModlePinName());
                                outputjson.put("name", mpcModleProperty.getOpcTagName());
                                outputjson.put("value", Tool.getSpecalScale(4,mpcModleProperty.getValue()));
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        } else if (modleProperty instanceof BaseModlePropertyImp) {
                            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                inputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                inputjson.put("value", Tool.getSpecalScale(4,baseModlePropertyImp.getValue()));
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                outputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                outputjson.put("value", Tool.getSpecalScale(4,baseModlePropertyImp.getValue()));
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        }


                    }
                    jsonmodule.put("top", customizeModle.getModleSight().getPositiontop());
                    jsonmodule.put("left", customizeModle.getModleSight().getPositionleft());
                    jsonmodule.put("errormsg",customizeModle.getErrormsg());
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
                                inputjson.put("value",Tool.getSpecalScale(4,mpcModleProperty.getValue()) );
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", mpcModleProperty.getModlePinName());
                                outputjson.put("name", mpcModleProperty.getOpcTagName());
                                outputjson.put("value", Tool.getSpecalScale(4,mpcModleProperty.getValue()));
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        } else if (modleProperty instanceof BaseModlePropertyImp) {
                            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                inputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                inputjson.put("value", Tool.getSpecalScale(4,baseModlePropertyImp.getValue()));
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                outputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                outputjson.put("value", Tool.getSpecalScale(4,baseModlePropertyImp.getValue()));
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        }


                    }
                    jsonmodule.put("top", filterModle.getModleSight().getPositiontop());
                    jsonmodule.put("left", filterModle.getModleSight().getPositionleft());
                    jsonmodule.put("errormsg",filterModle.getErrormsg());

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
                                inputjson.put("value", Tool.getSpecalScale(4,((MPCModleProperty) modleProperty).getValue()));
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", mpcModleProperty.getModlePinName());
                                outputjson.put("name", mpcModleProperty.getOpcTagName());
                                outputjson.put("value",Tool.getSpecalScale(4,((MPCModleProperty) modleProperty).getValue()) );
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        } else if (modleProperty instanceof BaseModlePropertyImp) {
                            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                inputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                inputjson.put("value", Tool.getSpecalScale(4,baseModlePropertyImp.getValue()));
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                outputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                outputjson.put("value", Tool.getSpecalScale(4,baseModlePropertyImp.getValue()));
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        }


                    }
                    jsonmodule.put("top", inModle.getModleSight().getPositiontop());
                    jsonmodule.put("left", inModle.getModleSight().getPositionleft());
                    jsonmodule.put("errormsg",inModle.getErrormsg());
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
                                inputjson.put("value", Tool.getSpecalScale(4,mpcModleProperty.getValue()));
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (mpcModleProperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", mpcModleProperty.getModlePinName());
                                outputjson.put("name", mpcModleProperty.getOpcTagName());
                                outputjson.put("value",Tool.getSpecalScale(4,mpcModleProperty.getValue()) );
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        } else if (modleProperty instanceof BaseModlePropertyImp) {
                            BaseModlePropertyImp baseModlePropertyImp = (BaseModlePropertyImp) modleProperty;
                            if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                                JSONObject inputjson = new JSONObject();
                                inputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                inputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                inputjson.put("value", Tool.getSpecalScale(4,baseModlePropertyImp.getValue()));
                                jsonmodule.getJSONArray("inputproperty").add(inputjson);
                            } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                                JSONObject outputjson = new JSONObject();
                                outputjson.put("pin", baseModlePropertyImp.getModlePinName());
                                outputjson.put("name", baseModlePropertyImp.getOpcTagName());
                                outputjson.put("value", Tool.getSpecalScale(4,baseModlePropertyImp.getValue()));
                                jsonmodule.getJSONArray("outputproperty").add(outputjson);
                            }
                        }


                    }
                    jsonmodule.put("top", outModle.getModleSight().getPositiontop());
                    jsonmodule.put("left", outModle.getModleSight().getPositionleft());
                    jsonmodule.put("errormsg",outModle.getErrormsg());
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




    @RequestMapping("/updateproject")
    @ResponseBody
    public String updateProject(@RequestParam("projectifno") String projectifno) {
        JSONObject result = new JSONObject();

        try {
            JSONObject jsonproject = JSONObject.parseObject(projectifno);
//            Modle modle =projectOperaterImp.findModleByid(140);
            Project project = projectOperaterImp.findProjectById(jsonproject.getInteger("projectid"));
            project.setName(jsonproject.getString("name"));
            project.setRunperiod(jsonproject.getDouble("runperiod"));

            projectOperaterImp.updateProject(project);
            result.put("msg", "success");

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
        }
        return result.toJSONString();
    }


    @RequestMapping("/stopproject")
    @ResponseBody
    public String stopProject(@RequestParam("projectid") int projectid) {
        JSONObject result = null;
        try {
            result = new JSONObject();

            Project project = projectManager.getProjectPool().get(projectid);
            if (project != null) {
                project.setProjectrun(false);
                for (Modle modle : project.getModleList()) {
                    modle.destory();
                }
            }
            result.put("msg", "success");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
        }
        return result.toJSONString();
    }


    @RequestMapping("startproject")
    @ResponseBody
    public String startProject(@RequestParam("projectid") int projectid) {
        JSONObject result = null;
        try {
            result = new JSONObject();
            Project oldproject=projectManager.getProjectPool().get(projectid);
            if(oldproject!=null){
                oldproject.setProjectrun(false);
                for (Modle modle : oldproject.getModleList()) {
                    modle.destory();
                }
            }
            projectManager.getProjectPool().remove(projectid);
            Project project = projectOperaterImp.findProjectById(projectid);
            projectManager.activeProject(project);
            result.put("msg", "success");
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
                    BaseModleImp baseModleImp = new FilterModle();
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
                    BaseModleImp baseModleImp = new INModle();
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
                    BaseModleImp baseModleImp = new MPCModle();
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
                    BaseModleImp baseModleImp = new OUTModle();
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
                    BaseModleImp baseModleImp = new PIDModle();
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

    @RequestMapping("/getmpcmodeldetail/{projectid}/{modleid}")
    @ResponseBody
    public String getmpcmodeldetail(@PathVariable("projectid") String projectid, @PathVariable("modleid") String modleid) {
        JSONObject result = new JSONObject();
        try {
            Project project = projectManager.getProjectPool().get(Integer.valueOf(projectid.trim()));
            Modle modle = project.getIndexmodles().get(Integer.valueOf(modleid.trim()));
            MPCModle controlModle = null;
            if (modle != null && modle instanceof MPCModle) {
                controlModle = (MPCModle) modle;
            }

            if (null == controlModle) {

                result.put("msg", "error");

                return result.toJSONString();
            }

            result.put("outSetp", controlModle.getControlAPCOutCycle());
            /****曲线***/
            if (controlModle.getRunstyle().equals(MPCModle.RUNSTYLEBYAUTO)) {

                result.put("funelUp", controlModle.getBackPVFunelUp());
                result.put("funelDwon", controlModle.getBackPVFunelDown());
                result.put("predict", controlModle.getBackPVPrediction());
            } else {
                result.put("funelUp", controlModle.getSimulatControlModle().getBacksimulatorPVFunelUp());
                result.put("funelDwon", controlModle.getSimulatControlModle().getBacksimulatorPVFunelDown());
                result.put("predict", controlModle.getSimulatControlModle().getBacksimulatorPVPrediction());
            }

            result.put("funneltype", controlModle.getFunneltype());

            int[] xaxis = new int[controlModle.getTimeserise_N()];
            for (int i = 0; i < controlModle.getTimeserise_N(); i++) {
                xaxis[i] = i;
            }
            result.put("xaxis", xaxis);

            String[] pvcurveNames = new String[controlModle.getNumOfRunnablePVPins_pp()];
            String[] funelUpcurveNames = new String[controlModle.getNumOfRunnablePVPins_pp()];
            String[] funelDowncurveNames = new String[controlModle.getNumOfRunnablePVPins_pp()];

            int indexEnablepv = 0;
            List<MPCModleProperty> pvrunablepins = controlModle.getRunablePins(controlModle.getCategoryPVmodletag(), controlModle.getMaskisRunnablePVMatrix());
            for (MPCModleProperty runpvpin : pvrunablepins) {
                pvcurveNames[indexEnablepv] = runpvpin.getModlePinName();
                funelUpcurveNames[indexEnablepv] = "funelUp";
                funelDowncurveNames[indexEnablepv] = "funelDown";
                indexEnablepv++;
            }

            result.put("curveNames4funelUp", funelUpcurveNames);
            result.put("curveNames4pv", pvcurveNames);
            result.put("curveNames4funelDown", funelDowncurveNames);


            /**表格内容*/


            List<MPCModleProperty> pvpinsrunable = controlModle.getRunablePins(controlModle.getCategoryPVmodletag(), controlModle.getMaskisRunnablePVMatrix());

            List<MPCModleProperty> sppinsrunable = controlModle.getRunablePins(controlModle.getCategorySPmodletag(), controlModle.getMaskisRunnablePVMatrix());

            List<MPCModleProperty> mvpinsrunable = controlModle.getRunablePins(controlModle.getCategoryMVmodletag(), controlModle.getMaskisRunnableMVMatrix());

            List<MPCModleProperty> ffpinsrunable = controlModle.getRunablePins(controlModle.getCategoryFFmodletag(), controlModle.getMaskisRunnableFFMatrix());


            int pvnum = pvpinsrunable.size();
            int mvnum = mvpinsrunable.size();
            int maxrownum = Math.max(pvnum, mvnum);

            JSONArray modlereadData = new JSONArray();
            JSONArray sdmvData = new JSONArray();
            JSONArray ffData = new JSONArray();

            int indexEnableMV = 0;
            int indexEnablePV = 0;
            for (int loop = 0; loop < maxrownum; loop++) {
                JSONObject modlereadDatarowcontext = new JSONObject();

                String mainrowpinname = "";
                if (loop < pvnum) {
                    /*pv*/
                    MPCModleProperty pv = pvpinsrunable.get(loop);
                    MPCModleProperty sp = sppinsrunable.get(loop);

                    modlereadDatarowcontext.put("pvValue", Tool.getSpecalScale(3, pv.getValue()));
                    modlereadDatarowcontext.put("spValue", Tool.getSpecalScale(3, sp.getValue()));
                    if (controlModle.getRunstyle().equals(MPCModle.RUNSTYLEBYAUTO)) {
                        modlereadDatarowcontext.put("e", Tool.getSpecalScale(3, controlModle.getBackPVPredictionError()[indexEnablePV]));
                    } else {
                        modlereadDatarowcontext.put("e", Tool.getSpecalScale(3, controlModle.getSimulatControlModle().getBacksimulatorPVPredictionError()[indexEnablePV]));

                    }
                    ++indexEnablePV;

                    modlereadDatarowcontext.put("shockpv", "");

                    mainrowpinname += pv.getModlePinName();
                }


                if (loop < mvnum) {
                    /*mv*/
                    MPCModleProperty mv = mvpinsrunable.get(loop);
                    MPCModleProperty mvDownLmt = mv.getDownLmt();
                    MPCModleProperty mvUpLmt = mv.getUpLmt();
                    MPCModleProperty mvFeedBack = mv.getFeedBack();
                    modlereadDatarowcontext.put("mvvalue", Tool.getSpecalScale(3, mv.getValue()));
                    modlereadDatarowcontext.put("mvDownLmt", Tool.getSpecalScale(3, mvDownLmt.getValue()));
                    modlereadDatarowcontext.put("mvUpLmt", Tool.getSpecalScale(3, mvUpLmt.getValue()));
                    modlereadDatarowcontext.put("mvFeedBack", Tool.getSpecalScale(3, mvFeedBack.getValue()));

                    modlereadDatarowcontext.put("dmv", Tool.getSpecalScale(3, controlModle.getBackrawDmv()[indexEnableMV]) + "|" + Tool.getSpecalScale(3, controlModle.getSimulatControlModle().getBacksimulatorrawDmv()[indexEnableMV]));
                    ++indexEnableMV;

                    modlereadDatarowcontext.put("shockmv", "");

                    mainrowpinname += (mainrowpinname.equals("") ? mv.getModlePinName() : "|" + mv.getModlePinName());
                }

                modlereadDatarowcontext.put("pinName", mainrowpinname);
                modlereadData.add(modlereadDatarowcontext);

            }

            /***仿真数据，ff数据展示*/
            indexEnablePV = 0;
            for (MPCModleProperty pvpin : pvpinsrunable) {
                JSONObject sdmvrowcontext = new JSONObject();
                JSONObject ffrowcontext = new JSONObject();


                sdmvrowcontext.put("pinName", pvpin.getModlePinName());
                ffrowcontext.put("pinName", pvpin.getModlePinName());
                indexEnableMV = 0;
                /**仿真dmv*/
                for (MPCModleProperty mvpin : mvpinsrunable) {
                    /*是否有映射关系*/
                    if (controlModle.getMaskMatrixRunnablePVUseMV()[indexEnablePV][indexEnableMV] == 1) {
                        sdmvrowcontext.put(mvpin.getModlePinName(), Tool.getSpecalScale(3, controlModle.getSimulatControlModle().getBacksimulatorDmvWrite()[indexEnablePV][indexEnableMV]));
                    }
                    ++indexEnableMV;
                }

                int indexEnableFF = 0;
                /**ff*/
                if (controlModle.getBasefeedforwardpoints_v() > 0) {
                    for (MPCModleProperty ffpin : ffpinsrunable) {
                        if (controlModle.getMaskMatrixRunnablePVUseFF()[indexEnablePV][indexEnableFF] == 1) {
                            /**dff*/
                            if (controlModle.getRunstyle().equals(MPCModle.RUNSTYLEBYAUTO)) {
                                ffrowcontext.put("d" + ffpin.getModlePinName(), Tool.getSpecalScale(3, controlModle.getBackDff()[indexEnablePV][indexEnableFF]));
                            } else {
                                ffrowcontext.put("d" + ffpin.getModlePinName(), Tool.getSpecalScale(3, controlModle.getSimulatControlModle().getBackDff()[indexEnablePV][indexEnableFF]));
                            }
                            /**ff值*/
                            ffrowcontext.put(ffpin.getModlePinName(), Tool.getSpecalScale(3, ffpin.getValue()));
                        }

                        ++indexEnableFF;
                    }
                }

                if (!sdmvrowcontext.equals("")) {
                    sdmvData.add(sdmvrowcontext);
                }

                ffData.add(ffrowcontext);
                ++indexEnablePV;
            }

            result.put("modleRealData", modlereadData);
            result.put("modlestatus", 1);
            result.put("sdmvData", sdmvData);

            if (controlModle.getBasefeedforwardpoints_v() > 0) {
                result.put("ffData", ffData);
            }
            result.put("msg", "success");

            return result.toJSONString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }
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
                    int modleId = jsonmodeinfo.getInteger("modleId");
                    String modleName = jsonmodeinfo.getString("modleName");
                    String predicttime_P = jsonmodeinfo.getString("predicttime_P");
                    String timeserise_N = jsonmodeinfo.getString("timeserise_N");
                    String controltime_M = jsonmodeinfo.getString("controltime_M");
                    String runstyle = jsonmodeinfo.getString("runstyle");

                    try {
                        MPCModle mpcmodle = (MPCModle) projectOperaterImp.findModleByid(modleId);
                        mpcmodle.setModleName(modleName);
                        mpcmodle.setPredicttime_P(Integer.parseInt(predicttime_P));
                        mpcmodle.setTimeserise_N(Integer.parseInt(timeserise_N));
                        mpcmodle.setControltime_M(Integer.parseInt(controltime_M));
                        mpcmodle.setRunstyle(Integer.parseInt(runstyle));
//                    count += projectOperaterImp.updateMPCModle(mpcmodle);

                        String auto = jsonmodeinfo.getString("auto");
                        String autoid = jsonmodeinfo.getString("autoid");
                        String autoopcTagName = jsonmodeinfo.getString("autoopcTagName");
                        String autoresourcemodleId = jsonmodeinfo.getString("autoresourcemodleId");
                        String autoresourcemodlepinsId = jsonmodeinfo.getString("autoresourcemodlepinsId");
                        String automodleOpcTag = jsonmodeinfo.getString("automodleOpcTag");

                        MPCModleProperty initmpcautoproperty = initpidplusproperty(
                                automodleOpcTag,
                                autoopcTagName,
                                MPCModleProperty.TYPE_PIN_MODLE_AUTO,
                                modleId,
                                autoresourcemodleId,
                                autoresourcemodlepinsId,
                                auto,
                                autoid,0,0,0);
                        initmpcautoproperty.setPintype(MPCModleProperty.TYPE_PIN_MODLE_AUTO);

                        count+=projectOperaterImp.updatempcmodlebusiness(mpcmodle,initmpcautoproperty);
                        result.put("msg", "success");
                    } catch (NumberFormatException e) {
                        logger.error(e.getMessage(),e);
                        result.put("msg", "error");
                    }catch (Exception e){
                        logger.error(e.getMessage(),e);
                        result.put("msg", "error");
                    }
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
                    double deadZone = jsonmodeinfo.getDouble("deadZone");
                    BaseModlePropertyImp pvbasemodleproperty = initpidplusproperty(
                            pvmodleOpcTag,
                            pvopcTagName,
                            MPCModleProperty.TYPE_PIN_PV,
                            modleId,
                            pvresourcemodleId,
                            pvresourcemodlepinsId,
                            pv, pvid,0,0,deadZone);//new BaseModlePropertyImp();
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
                            MPCModleProperty.TYPE_PIN_SP,
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
                    double dmvHigh = jsonmodeinfo.getDouble("dmvHigh");
                    double dmvLow = jsonmodeinfo.getDouble("dmvLow");
                    BaseModlePropertyImp initpidmvproperty = initpidplusproperty(
                            mvmodleOpcTag,
                            mvopcTagName,
                            MPCModleProperty.TYPE_PIN_MV,
                            modleId,
                            mvresourcemodleId,
                            mvresourcemodlepinsId,
                            mv, mvid, dmvHigh, dmvLow,0f);//new BaseModlePropertyImp();
                    pidproperties.add(initpidmvproperty);


                    String mvup = jsonmodeinfo.getString("mvuppincontantvalue");
                    String mvupid = jsonmodeinfo.getString("mvuppinid");
                    String mvupopcTagName = jsonmodeinfo.getString("mvupmodleOpcTag");
                    String mvupresourcemodleId = jsonmodeinfo.getString("mvupresourcemodleId");
                    String mvupresourcemodlepinsId = jsonmodeinfo.getString("mvupresourcemodlepinsId");
                    String mvupmodleOpcTag = jsonmodeinfo.getString("mvupmodleOpcTag");

                    BaseModlePropertyImp initpidmvupproperty = initpidproperty(
                            mvupmodleOpcTag,
                            mvupopcTagName,
                            MPCModleProperty.TYPE_PIN_MVUP,
                            modleId,
                            mvupresourcemodleId,
                            mvupresourcemodlepinsId,
                            mvup, mvupid);
                    pidproperties.add(initpidmvupproperty);

                    String mvdown = jsonmodeinfo.getString("mvdownpincontantvalue");
                    String mvdownid = jsonmodeinfo.getString("mvdownpinid");
                    String mvdownopcTagName = jsonmodeinfo.getString("mvdownmodleOpcTag");
                    String mvdownresourcemodleId = jsonmodeinfo.getString("mvdownresourcemodleId");
                    String mvdownresourcemodlepinsId = jsonmodeinfo.getString("mvdownresourcemodlepinsId");
                    String mvdownmodleOpcTag = jsonmodeinfo.getString("mvdownmodleOpcTag");

                    BaseModlePropertyImp initpidmvdownproperty = initpidproperty(
                            mvdownmodleOpcTag,
                            mvdownopcTagName,
                            MPCModleProperty.TYPE_PIN_MVDOWN,
                            modleId,
                            mvdownresourcemodleId,
                            mvdownresourcemodlepinsId,
                            mvdown, mvdownid);
                    pidproperties.add(initpidmvdownproperty);


                    String ff = jsonmodeinfo.getString("ff");
                    String ffid = jsonmodeinfo.getString("ffid");
                    String ffopcTagName = jsonmodeinfo.getString("ffopcTagName");
                    String ffresourcemodleId = jsonmodeinfo.getString("ffresourcemodleId");
                    String ffresourcemodlepinsId = jsonmodeinfo.getString("ffresourcemodlepinsId");
                    String ffmodleOpcTag = jsonmodeinfo.getString("ffmodleOpcTag");
                    if(Tool.isNoneString(ff)&&(Tool.isNoneString(ffresourcemodleId) && Tool.isNoneString(ffresourcemodlepinsId))){
                        //ff 没有就不要存储了
                    }else {
                        BaseModlePropertyImp ffbasemodleproperty = initpidproperty(
                                ffmodleOpcTag,
                                ffopcTagName,
                                MPCModleProperty.TYPE_PIN_FF,
                                modleId,
                                ffresourcemodleId,
                                ffresourcemodlepinsId,
                                ff, ffid);//new BaseModlePropertyImp();
                        pidproperties.add(ffbasemodleproperty);
                    }


                    String auto = jsonmodeinfo.getString("auto");
                    String autoid = jsonmodeinfo.getString("autoid");
                    String autoopcTagName = jsonmodeinfo.getString("autoopcTagName");
                    String autoresourcemodleId = jsonmodeinfo.getString("autoresourcemodleId");
                    String autoresourcemodlepinsId = jsonmodeinfo.getString("autoresourcemodlepinsId");
                    String automodleOpcTag = jsonmodeinfo.getString("automodleOpcTag");

                    BaseModlePropertyImp initpidautoproperty = initpidproperty(
                            automodleOpcTag,
                            autoopcTagName,
                            MPCModleProperty.TYPE_PIN_MODLE_AUTO,
                            modleId,
                            autoresourcemodleId,
                            autoresourcemodlepinsId,
                            auto,
                            autoid);
                    pidproperties.add(initpidautoproperty);



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
    public String getbasemodleproperties(@RequestParam("modleid") int modleid, @RequestParam("pindir") String pindir) {
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
                    if (baseModlePropertyImp.getResource().getString("outputpinmappingtagname") != null) {
                        property.put("outpotopcTagName", baseModlePropertyImp.getResource().getString("outputpinmappingtagname"));
                    }
                    modleproperties.add(property);
                }

            }
            return Tool.sendLayuiPage(baseModlePropertyImps.size(), modleproperties).toJSONString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @RequestMapping("/getmpcmodleproperties")
    @ResponseBody
    public String getmpcmodleproperties(@RequestParam("modleid") int modleid, @RequestParam("pintype") String pintype) {
        try {
            List<BaseModlePropertyImp> baseModlePropertyImps = projectOperaterImp.findBaseModlePropertyByModleid(modleid);
            JSONArray modleproperties = new JSONArray();
            for (BaseModlePropertyImp basemodleproperty : baseModlePropertyImps) {
                MPCModleProperty mpcmodleproperty = (MPCModleProperty) basemodleproperty;
                if (mpcmodleproperty.getPintype()!=null&&mpcmodleproperty.getPintype().equals(pintype)) {
                    JSONObject property = new JSONObject();
                    property.put("modlepinsId", mpcmodleproperty.getModlepinsId());
                    property.put("refmodleId", mpcmodleproperty.getRefmodleId());
                    property.put("modlePinName", mpcmodleproperty.getModlePinName());
                    property.put("modleOpcTag", mpcmodleproperty.getModleOpcTag());
                    property.put("opcTagName", mpcmodleproperty.getOpcTagName());
                    property.put("Q", mpcmodleproperty.getQ());
                    property.put("R", mpcmodleproperty.getR());
                    modleproperties.add(property);
                }

            }
            return Tool.sendLayuiPage(baseModlePropertyImps.size(), modleproperties).toJSONString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    @RequestMapping("/getmpcmodlerespon")
    @ResponseBody
    public String getmpcmodlerespon(@RequestParam("modleId") int modleId) {
//        JSONObject result=new JSONObject();
        try {
            List<ResponTimeSerise> resps = projectOperaterImp.findResponTimeSeriseByModleid(modleId);


            JSONArray datas = new JSONArray();

            for (ResponTimeSerise resp : resps) {
                JSONObject jsonresp = resp.getStepRespJson();//{k:1,t:180,tao:1}
                JSONObject pincontext = new JSONObject();
                pincontext.put("refrencemodleId", resp.getRefrencemodleId());
                pincontext.put("modleresponId", resp.getModleresponId());
                pincontext.put("input", resp.getInputPins());
                pincontext.put("output", resp.getOutputPins());
                pincontext.put("K", jsonresp.getFloat("k"));
                pincontext.put("T", jsonresp.getFloat("t"));
                pincontext.put("Tau", jsonresp.getFloat("tao"));
                pincontext.put("effectRatio", resp.getEffectRatio());
                datas.add(pincontext);
            }
            return Tool.sendLayuiPage(resps.size(), datas).toJSONString();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }


    @RequestMapping("/updatemodleproperties")
    @ResponseBody
    public String updatebasemodleproperties(@RequestParam("modlepropertyinfo") String modlepropertyinfo) {
        JSONObject result = new JSONObject();
        try {
            JSONObject jsonmodlepropertyinfo = JSONObject.parseObject(modlepropertyinfo);
            int count = 0;
            switch (jsonmodlepropertyinfo.getString("modletype")) {

                case Modle.MODLETYPE_MPC: {
                    if (jsonmodlepropertyinfo.getString("pindir").equals(ModleProperty.PINDIROUTPUT)) {
                        MPCModleProperty propertyImp = projectOperaterImp.findMPCModlePropertyByid(jsonmodlepropertyinfo.getInteger("modlepinsId"));
                        propertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modlePinName"));
                        propertyImp.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                        propertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MEMORY);
                        propertyImp.setResource(resource);

//                    mpcModleProperty.setPinEnable(jsonmodlepropertyinfo.getInteger("pinEnable"));
                        propertyImp.setPindir(jsonmodlepropertyinfo.getString("pindir"));
                        propertyImp.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                        count = projectOperaterImp.updateMPCModleProperty(propertyImp);

                    } else {
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

                    }
                    break;
                }
                case Modle.MODLETYPE_INPUT: {
                    BaseModlePropertyImp propertyImp = projectOperaterImp.findBaseModlePropertyByid(jsonmodlepropertyinfo.getInteger("modlepinsId"));
                    propertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modleOpcTag"));
                    propertyImp.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    propertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));
                    JSONObject resource = new JSONObject();
                    resource.put("resource", ModleProperty.SOURCE_TYPE_OPC);
                    resource.put("inmappingtag", jsonmodlepropertyinfo.getString("modleOpcTag"));
                    propertyImp.setResource(resource);
                    count = projectOperaterImp.updateBaseModleProperty(propertyImp);
                    break;
                }
                case Modle.MODLETYPE_FILTER: {
                    BaseModlePropertyImp propertyImp = projectOperaterImp.findBaseModlePropertyByid(jsonmodlepropertyinfo.getInteger("modlepinsId"));
                    propertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modleOpcTag"));
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
                        String refmodleId = jsonmodlepropertyinfo.getString("resourcemodleId");
                        String refmodlepinsId = jsonmodlepropertyinfo.getString("resourcemodlepinsId");
                        if (Tool.isNoneString(refmodleId) || Tool.isNoneString(refmodlepinsId)) {
                            throw new RuntimeException("pin data resource is none!");
                        }
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MEMORY);
                        resource.put("modleId", Integer.parseInt(refmodleId));
                        resource.put("modlepinsId", Integer.parseInt(refmodlepinsId));
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
                case Modle.MODLETYPE_OUTPUT: {

                    /**输入引脚*/
                    BaseModlePropertyImp inputpropertyImp = projectOperaterImp.findBaseModlePropertyByid(jsonmodlepropertyinfo.getInteger("inmodlepinsId"));
                    inputpropertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modleOpcTag"));
                    inputpropertyImp.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    inputpropertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));

                    String inrefmodleId = jsonmodlepropertyinfo.getString("resourcemodleId");
                    String inrefmodlepinsId = jsonmodlepropertyinfo.getString("resourcemodlepinsId");
                    if (Tool.isNoneString(inrefmodleId) || Tool.isNoneString(inrefmodlepinsId)) {
                        throw new RuntimeException("pin data resource is none!");
                    }
                    JSONObject inresource = new JSONObject();

                    inresource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                    inresource.put("modleId", Integer.parseInt(inrefmodleId));
                    inresource.put("modlepinsId", Integer.parseInt(inrefmodlepinsId));
                    inresource.put("outputpinmappingtagname", jsonmodlepropertyinfo.getString("outputpinmappingtagname"));
                    inputpropertyImp.setResource(inresource);


                    /**输出引脚*/
                    BaseModlePropertyImp outputpropertyImp = projectOperaterImp.findBaseModlePropertyByid(jsonmodlepropertyinfo.getInteger("outmodlepinsId"));
                    outputpropertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("outpinmappingtag"));
                    outputpropertyImp.setModlePinName(jsonmodlepropertyinfo.getString("outpinmappingtag"));
                    outputpropertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("outputpinmappingtagname"));
                    if (Tool.isNoneString(jsonmodlepropertyinfo.getString("outpinmappingtag"))) {
                        throw new RuntimeException("out mapping is none");
                    }
                    JSONObject outresource = new JSONObject();
                    outresource.put("resource", ModleProperty.SOURCE_TYPE_OPC);
                    outresource.put("outmappingtag", jsonmodlepropertyinfo.getString("outpinmappingtag"));
                    outresource.put("modlepinsId", inputpropertyImp.getModlepinsId());//输出模块的输入引脚id
                    outputpropertyImp.setResource(outresource);

                    count = projectOperaterImp.updateoutmodlepropertybusiness(inputpropertyImp, outputpropertyImp);
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


    @RequestMapping("/updatempcmodleproperties")
    @ResponseBody
    public String updatempcmodleproperties(@RequestParam("modlepropertyinfo") String modlepropertyinfo) {

        JSONObject result = new JSONObject();
        try {
            JSONObject jsonmodlepropertyinfo = JSONObject.parseObject(modlepropertyinfo);
            int count = 0;
            switch (jsonmodlepropertyinfo.getString("pintype")) {

                case ModleProperty.TYPE_PIN_PV: {

                    MPCModleProperty pvpinmpcModleProperty = projectOperaterImp.findMPCModlePropertyByid(jsonmodlepropertyinfo.getInteger("modlepinsId"));
//                    MPCModleProperty pvpinmpcModleProperty = new MPCModleProperty();
//                    pvpinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    pvpinmpcModleProperty.setModlePinName(jsonmodlepropertyinfo.getString("pvmodlePinName"));
                    pvpinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    pvpinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_PV);
                    pvpinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    pvpinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("pvmodleOpcTagName"));//spmodleOpcTag
                    pvpinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("pvmodleOpcTag"));
                    String pvresourcemodleId = jsonmodlepropertyinfo.getString("pvresourcemodleId");
                    String pvresourcemodlepinsId = jsonmodlepropertyinfo.getString("pvresourcemodlepinsId");
                    String pvpincontantvalue = jsonmodlepropertyinfo.getString("pvpincontantvalue");
                    if (Tool.isNoneString(pvresourcemodleId) || Tool.isNoneString(pvresourcemodlepinsId)) {
                        if (Tool.isNoneString(pvpincontantvalue)) {
                            throw new RuntimeException("pv pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(pvpincontantvalue));
                            pvpinmpcModleProperty.setResource(resource);
                            pvpinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();

                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(pvresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(pvresourcemodlepinsId));
                        pvpinmpcModleProperty.setResource(resource);

                    }

                    pvpinmpcModleProperty.setDeadZone(jsonmodlepropertyinfo.getDouble("deadZone"));
                    pvpinmpcModleProperty.setFunelinitValue(jsonmodlepropertyinfo.getDouble("funelinitValue"));

                    pvpinmpcModleProperty.setFunneltype(jsonmodlepropertyinfo.getString("funneltype"));
                    pvpinmpcModleProperty.setQ(jsonmodlepropertyinfo.getDouble("Q"));
                    pvpinmpcModleProperty.setReferTrajectoryCoef(jsonmodlepropertyinfo.getDouble("referTrajectoryCoef"));
                    pvpinmpcModleProperty.setTracoefmethod(jsonmodlepropertyinfo.getString("tracoefmethod"));


                    int pinorder = 0;
                    Matcher pvmatch = pvpattern.matcher(jsonmodlepropertyinfo.getString("pvmodlePinName"));
                    if (pvmatch.find()) {
                        pinorder = Integer.parseInt(pvmatch.group(2));
                    } else {
                        throw new RuntimeException("can't match pin order");
                    }


                    MPCModleProperty pvuppinmpcModleProperty = projectOperaterImp.findMPCModlePropertyByid(jsonmodlepropertyinfo.getInteger("pvuppinid"));//new MPCModleProperty();//sppinid
                    pvuppinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
//                    pvuppinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    pvuppinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_PVUP + pinorder);
                    pvuppinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_PVUP);
                    pvuppinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);

                    pvuppinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("pvupmodleOpcTagName"));//spmodleOpcTag
                    pvuppinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("pvupmodleOpcTag"));

                    String pvupresourcemodleId = jsonmodlepropertyinfo.getString("pvupresourcemodleId");
                    String pvupresourcemodlepinsId = jsonmodlepropertyinfo.getString("pvupresourcemodlepinsId");
                    String pvuppincontantvalue = jsonmodlepropertyinfo.getString("pvuppincontantvalue");
                    if (Tool.isNoneString(pvupresourcemodleId) || Tool.isNoneString(pvupresourcemodlepinsId)) {
                        if (Tool.isNoneString(pvuppincontantvalue)) {
                            throw new RuntimeException("pvup pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(pvuppincontantvalue));
                            pvuppinmpcModleProperty.setResource(resource);
                            pvuppinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(pvupresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(pvupresourcemodlepinsId));
                        pvuppinmpcModleProperty.setResource(resource);
                    }


                    MPCModleProperty pvdownpinmpcModleProperty = projectOperaterImp.findMPCModlePropertyByid(jsonmodlepropertyinfo.getInteger("pvdownpinid"));//new MPCModleProperty();
                    pvdownpinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    pvdownpinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    pvdownpinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_PVDOWN + pinorder);
                    pvdownpinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_PVDOWN);
                    pvdownpinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    pvdownpinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("pvdownmodleOpcTagName"));//spmodleOpcTag
                    pvdownpinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("pvdownmodleOpcTag"));

                    String pvdownresourcemodleId = jsonmodlepropertyinfo.getString("pvdownresourcemodleId");
                    String pvdownresourcemodlepinsId = jsonmodlepropertyinfo.getString("pvdownresourcemodlepinsId");
                    String pvdownpincontantvalue = jsonmodlepropertyinfo.getString("pvdownpincontantvalue");
                    if (Tool.isNoneString(pvdownresourcemodleId) || Tool.isNoneString(pvdownresourcemodlepinsId)) {
                        if (Tool.isNoneString(pvdownpincontantvalue)) {
                            throw new RuntimeException("pvdown pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(pvdownpincontantvalue));
                            pvdownpinmpcModleProperty.setResource(resource);
                            pvdownpinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(pvdownresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(pvdownresourcemodlepinsId));
                        pvdownpinmpcModleProperty.setResource(resource);
                    }


                    MPCModleProperty sppinmpcModleProperty = projectOperaterImp.findMPCModlePropertyByid(jsonmodlepropertyinfo.getInteger("sppinid"));//new MPCModleProperty();
                    sppinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
//                    sppinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    sppinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_SP + pinorder);
                    sppinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_SP);
                    sppinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    sppinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("spmodleOpcTagName"));//spmodleOpcTag
                    sppinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("spmodleOpcTag"));

                    String spresourcemodleId = jsonmodlepropertyinfo.getString("spresourcemodleId");
                    String spresourcemodlepinsId = jsonmodlepropertyinfo.getString("spresourcemodlepinsId");
                    String sppincontantvalue = jsonmodlepropertyinfo.getString("sppincontantvalue");
                    if (Tool.isNoneString(spresourcemodleId) || Tool.isNoneString(spresourcemodlepinsId)) {
                        if (Tool.isNoneString(sppincontantvalue)) {
                            throw new RuntimeException("pvdown pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(sppincontantvalue));
                            sppinmpcModleProperty.setResource(resource);
                            sppinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(spresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(spresourcemodlepinsId));
                        sppinmpcModleProperty.setResource(resource);
                    }
                    count = projectOperaterImp.updatempcmodlepvrelationpropertybusiness(pvpinmpcModleProperty, pvuppinmpcModleProperty, pvdownpinmpcModleProperty, sppinmpcModleProperty);
                    break;
                }
                case ModleProperty.TYPE_PIN_MV: {

                    MPCModleProperty mvpinmpcModleProperty = projectOperaterImp.findMPCModlePropertyByid(jsonmodlepropertyinfo.getInteger("mvpinid"));//new MPCModleProperty();
//                    mvpinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    mvpinmpcModleProperty.setModlePinName(jsonmodlepropertyinfo.getString("mvmodlePinName"));
                    mvpinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    mvpinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_MV);
                    mvpinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    mvpinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("mvmodleOpcTagName"));
                    mvpinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("mvmodleOpcTag"));
                    String mvresourcemodleId = jsonmodlepropertyinfo.getString("mvresourcemodleId");
                    String mvresourcemodlepinsId = jsonmodlepropertyinfo.getString("mvresourcemodlepinsId");
                    String mvpincontantvalue = jsonmodlepropertyinfo.getString("mvpincontantvalue");
                    if (Tool.isNoneString(mvresourcemodleId) || Tool.isNoneString(mvresourcemodlepinsId)) {
                        if (Tool.isNoneString(mvpincontantvalue)) {
                            throw new RuntimeException("mv pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(mvpincontantvalue));
                            mvpinmpcModleProperty.setResource(resource);
                            mvpinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();

                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(mvresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(mvresourcemodlepinsId));
                        mvpinmpcModleProperty.setResource(resource);
                    }

                    mvpinmpcModleProperty.setR(jsonmodlepropertyinfo.getDouble("R"));
                    mvpinmpcModleProperty.setDmvHigh(jsonmodlepropertyinfo.getDouble("dmvHigh"));

                    mvpinmpcModleProperty.setDmvLow(jsonmodlepropertyinfo.getDouble("dmvLow"));


                    int pinorder = 0;
                    Matcher pvmatch = mvpattern.matcher(jsonmodlepropertyinfo.getString("mvmodlePinName"));
                    if (pvmatch.find()) {
                        pinorder = Integer.parseInt(pvmatch.group(2));
                    } else {
                        throw new RuntimeException("can't match pin order");
                    }


                    MPCModleProperty mvuppinmpcModleProperty = projectOperaterImp.findMPCModlePropertyByid(jsonmodlepropertyinfo.getInteger("mvuppinid"));//new MPCModleProperty();
                    mvuppinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
//                    mvuppinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    mvuppinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_MVUP + pinorder);
                    mvuppinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_MVUP);
                    mvuppinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);

                    mvuppinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("mvupmodleOpcTagName"));//spmodleOpcTag
                    mvuppinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("mvupmodleOpcTag"));

                    String mvupresourcemodleId = jsonmodlepropertyinfo.getString("mvupresourcemodleId");
                    String mvupresourcemodlepinsId = jsonmodlepropertyinfo.getString("mvupresourcemodlepinsId");
                    String mvuppincontantvalue = jsonmodlepropertyinfo.getString("mvuppincontantvalue");
                    if (Tool.isNoneString(mvupresourcemodleId) || Tool.isNoneString(mvupresourcemodlepinsId)) {
                        if (Tool.isNoneString(mvuppincontantvalue)) {
                            throw new RuntimeException("mvup pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(mvuppincontantvalue));
                            mvuppinmpcModleProperty.setResource(resource);
                            mvuppinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(mvupresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(mvupresourcemodlepinsId));
                        mvuppinmpcModleProperty.setResource(resource);
                    }


                    MPCModleProperty mvdownpinmpcModleProperty = projectOperaterImp.findMPCModlePropertyByid(jsonmodlepropertyinfo.getInteger("mvdownpinid"));//new MPCModleProperty();
                    mvdownpinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
//                    mvdownpinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    mvdownpinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_MVDOWN + pinorder);
                    mvdownpinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_MVDOWN);
                    mvdownpinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    mvdownpinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("mvdownmodleOpcTagName"));//spmodleOpcTag
                    mvdownpinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("mvdownmodleOpcTag"));

                    String mvdownresourcemodleId = jsonmodlepropertyinfo.getString("mvdownresourcemodleId");
                    String mvdownresourcemodlepinsId = jsonmodlepropertyinfo.getString("mvdownresourcemodlepinsId");
                    String mvdownpincontantvalue = jsonmodlepropertyinfo.getString("mvdownpincontantvalue");
                    if (Tool.isNoneString(mvdownresourcemodleId) || Tool.isNoneString(mvdownresourcemodlepinsId)) {
                        if (Tool.isNoneString(mvdownpincontantvalue)) {
                            throw new RuntimeException("mvdown pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(mvdownpincontantvalue));
                            mvdownpinmpcModleProperty.setResource(resource);
                            mvdownpinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(mvdownresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(mvdownresourcemodlepinsId));
                        mvdownpinmpcModleProperty.setResource(resource);
                    }


                    MPCModleProperty mvfbpinmpcModleProperty = projectOperaterImp.findMPCModlePropertyByid(jsonmodlepropertyinfo.getInteger("mvfbpinid"));//new MPCModleProperty();
                    mvfbpinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
//                    mvfbpinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    mvfbpinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_MVFB + pinorder);
                    mvfbpinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_MVFB);
                    mvfbpinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    mvfbpinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("mvfbmodleOpcTagName"));//mvfbmodleOpcTag
                    mvfbpinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("mvfbmodleOpcTag"));

                    String mvfbresourcemodleId = jsonmodlepropertyinfo.getString("mvfbresourcemodleId");
                    String mvfbresourcemodlepinsId = jsonmodlepropertyinfo.getString("mvfbresourcemodlepinsId");
                    String mvfbpincontantvalue = jsonmodlepropertyinfo.getString("mvfbpincontantvalue");
                    if (Tool.isNoneString(mvfbresourcemodleId) || Tool.isNoneString(mvfbresourcemodlepinsId)) {
                        if (Tool.isNoneString(mvfbpincontantvalue)) {
                            throw new RuntimeException("mvfbdown pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(mvfbpincontantvalue));
                            mvfbpinmpcModleProperty.setResource(resource);
                            mvfbpinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(mvfbresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(mvfbresourcemodlepinsId));
                        mvfbpinmpcModleProperty.setResource(resource);
                    }
                    count = projectOperaterImp.updatempcmodlepvrelationpropertybusiness(mvpinmpcModleProperty, mvuppinmpcModleProperty, mvdownpinmpcModleProperty, mvfbpinmpcModleProperty);
                    break;
                }
                case ModleProperty.TYPE_PIN_FF: {
                    MPCModleProperty ffpinmpcModleProperty = projectOperaterImp.findMPCModlePropertyByid(jsonmodlepropertyinfo.getInteger("ffpinid"));//= new MPCModleProperty();
//                    ffpinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    ffpinmpcModleProperty.setModlePinName(jsonmodlepropertyinfo.getString("ffmodlePinName"));
                    ffpinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    ffpinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_FF);
                    ffpinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    ffpinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("ffmodleOpcTagName"));
                    ffpinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("ffmodleOpcTag"));
                    String ffresourcemodleId = jsonmodlepropertyinfo.getString("ffresourcemodleId");
                    String ffresourcemodlepinsId = jsonmodlepropertyinfo.getString("ffresourcemodlepinsId");
                    String ffpincontantvalue = jsonmodlepropertyinfo.getString("ffpincontantvalue");
                    if (Tool.isNoneString(ffresourcemodleId) || Tool.isNoneString(ffresourcemodlepinsId)) {
                        if (Tool.isNoneString(ffpincontantvalue)) {
                            throw new RuntimeException("ff pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(ffpincontantvalue));
                            ffpinmpcModleProperty.setResource(resource);
                            ffpinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();

                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(ffresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(ffresourcemodlepinsId));
                        ffpinmpcModleProperty.setResource(resource);
                    }


                    int pinorder = 0;
                    Matcher pvmatch = ffpattern.matcher(jsonmodlepropertyinfo.getString("ffmodlePinName"));
                    if (pvmatch.find()) {
                        pinorder = Integer.parseInt(pvmatch.group(2));
                    } else {
                        throw new RuntimeException("can't match pin order");
                    }


                    MPCModleProperty ffuppinmpcModleProperty = projectOperaterImp.findMPCModlePropertyByid(jsonmodlepropertyinfo.getInteger("ffuppinid"));//new MPCModleProperty();
                    ffuppinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
//                    ffuppinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    ffuppinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_FFUP + pinorder);
                    ffuppinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_FFUP);
                    ffuppinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);

                    ffuppinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("ffupmodleOpcTagName"));//spmodleOpcTag
                    ffuppinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("ffupmodleOpcTag"));

                    String mvupresourcemodleId = jsonmodlepropertyinfo.getString("ffupresourcemodleId");
                    String mvupresourcemodlepinsId = jsonmodlepropertyinfo.getString("ffupresourcemodlepinsId");
                    String mvuppincontantvalue = jsonmodlepropertyinfo.getString("ffuppincontantvalue");
                    if (Tool.isNoneString(mvupresourcemodleId) || Tool.isNoneString(mvupresourcemodlepinsId)) {
                        if (Tool.isNoneString(mvuppincontantvalue)) {
                            throw new RuntimeException("ffup pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(mvuppincontantvalue));
                            ffuppinmpcModleProperty.setResource(resource);
                            ffuppinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(mvupresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(mvupresourcemodlepinsId));
                        ffuppinmpcModleProperty.setResource(resource);
                    }


                    MPCModleProperty ffdownpinmpcModleProperty = projectOperaterImp.findMPCModlePropertyByid(jsonmodlepropertyinfo.getInteger("ffdownpinid"));//new MPCModleProperty();
                    ffdownpinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
//                    ffdownpinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    ffdownpinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_FFDOWN + pinorder);
                    ffdownpinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_FFDOWN);
                    ffdownpinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    ffdownpinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("ffdownmodleOpcTagName"));//spmodleOpcTag
                    ffdownpinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("ffdownmodleOpcTag"));

                    String mvdownresourcemodleId = jsonmodlepropertyinfo.getString("ffdownresourcemodleId");
                    String mvdownresourcemodlepinsId = jsonmodlepropertyinfo.getString("ffdownresourcemodlepinsId");
                    String mvdownpincontantvalue = jsonmodlepropertyinfo.getString("ffdownpincontantvalue");
                    if (Tool.isNoneString(mvdownresourcemodleId) || Tool.isNoneString(mvdownresourcemodlepinsId)) {
                        if (Tool.isNoneString(mvdownpincontantvalue)) {
                            throw new RuntimeException("ffdown pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(mvdownpincontantvalue));
                            ffdownpinmpcModleProperty.setResource(resource);
                            ffdownpinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(mvdownresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(mvdownresourcemodlepinsId));
                        ffdownpinmpcModleProperty.setResource(resource);
                    }
                    count = projectOperaterImp.updatempcmodlepvrelationpropertybusiness(ffpinmpcModleProperty, ffuppinmpcModleProperty, ffdownpinmpcModleProperty);
                    break;
                }
                default: {

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


    @RequestMapping("/updatempcmodlerespon")
    @ResponseBody
    public String updatempcmodlerespon(@RequestParam("responcontext") String responcontext) {
        JSONObject modlejsonObject = JSONObject.parseObject(responcontext);
        JSONObject result = new JSONObject();

        int modleid;
        String responid;
        String inputpinName;
        String outputpinName;
        float K;
        float T;
        float Tau;
        float effectRatio;
        ResponTimeSerise respontimeserise;
        JSONObject jsonres;

        try {
//            modleid = modlejsonObject.getInteger("refrencemodleId");
//            responid = modlejsonObject.getString("responid").trim();
            inputpinName = modlejsonObject.getString("inputpinName").trim();
            outputpinName = modlejsonObject.getString("outputpinName").trim();
            K = modlejsonObject.getFloat("K");
            T = modlejsonObject.getFloat("T");
            Tau = modlejsonObject.getFloat("Tau");
            effectRatio = ((modlejsonObject.getString("effectRatio").equals("")) || (modlejsonObject.getString("effectRatio") == null)) ? 1f : modlejsonObject.getFloat("effectRatio");
            respontimeserise = projectOperaterImp.findResponTimeSeriseByid(modlejsonObject.getInteger("responid"));//new ResponTimeSerise();

            respontimeserise.setInputPins(inputpinName);
            respontimeserise.setOutputPins(outputpinName);
//            respontimeserise.setRefrencemodleId(modleid);
//            respontimeserise.setModletagId(responid.equals("") ? -1 : Integer.valueOf(responid));
            jsonres = new JSONObject();
            jsonres.put("k", K);
            jsonres.put("t", T);
            jsonres.put("tao", Tau);
            respontimeserise.setStepRespJson(jsonres);
            respontimeserise.setEffectRatio(effectRatio);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        try {

            projectOperaterImp.updateResponTimeSerise(respontimeserise);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        result.put("msg", "success");
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


    @RequestMapping("/deletempcmodlerespon")
    @ResponseBody
    public String deletempcmodlerespon(@RequestParam("modleresponId") int modleresponId) {
        JSONObject result = new JSONObject();
        try {
            int count = projectOperaterImp.deleteResponTimeSeriseByid(modleresponId);
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
    public String createbasemodleproperties(@RequestParam("modlepropertyinfo") String modlepropertyinfo) {

        JSONObject result = new JSONObject();
        try {
            JSONObject jsonmodlepropertyinfo = JSONObject.parseObject(modlepropertyinfo);
            int count = 0;
            switch (jsonmodlepropertyinfo.getString("modletype")) {

                case Modle.MODLETYPE_MPC: {


                    MPCModleProperty propertyImp = new MPCModleProperty();
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
                    propertyImp.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    count = projectOperaterImp.insertMPCModleProperty(propertyImp);

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
                    resource.put("inmappingtag", jsonmodlepropertyinfo.getString("modleOpcTag"));
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
                    propertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modleOpcTag"));
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
                        String refmodleId = jsonmodlepropertyinfo.getString("resourcemodleId");
                        String refmodlepinsId = jsonmodlepropertyinfo.getString("resourcemodlepinsId");
                        if (Tool.isNoneString(refmodleId) || Tool.isNoneString(refmodlepinsId)) {
                            throw new RuntimeException("pin data resource is none!");
                        }
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MEMORY);
                        resource.put("modleId", Integer.parseInt(refmodleId));
                        resource.put("modlepinsId", Integer.parseInt(refmodlepinsId));
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

                case Modle.MODLETYPE_OUTPUT: {

                    /**输入引脚*/
                    BaseModlePropertyImp inputpropertyImp = new BaseModlePropertyImp();
                    inputpropertyImp.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    inputpropertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("modleOpcTag"));
                    inputpropertyImp.setModlePinName(jsonmodlepropertyinfo.getString("modlePinName"));
                    inputpropertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("opcTagName"));

                    String inrefmodleId = jsonmodlepropertyinfo.getString("resourcemodleId");
                    String inrefmodlepinsId = jsonmodlepropertyinfo.getString("resourcemodlepinsId");
                    if (Tool.isNoneString(inrefmodleId) || Tool.isNoneString(inrefmodlepinsId)) {
                        throw new RuntimeException("pin data resource is none!");
                    }
                    JSONObject inresource = new JSONObject();

                    inresource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                    inresource.put("modleId", Integer.parseInt(inrefmodleId));
                    inresource.put("modlepinsId", Integer.parseInt(inrefmodlepinsId));
                    inresource.put("outputpinmappingtagname", jsonmodlepropertyinfo.getString("outputpinmappingtagname"));
                    inputpropertyImp.setResource(inresource);
                    inputpropertyImp.setPindir(ModleProperty.PINDIRINPUT);
                    inputpropertyImp.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_BASE);


                    /**输出引脚*/
                    BaseModlePropertyImp outputpropertyImp = new BaseModlePropertyImp();
                    outputpropertyImp.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    outputpropertyImp.setModleOpcTag(jsonmodlepropertyinfo.getString("outpinmappingtag"));
                    outputpropertyImp.setModlePinName(jsonmodlepropertyinfo.getString("outpinmappingtag"));
                    outputpropertyImp.setOpcTagName(jsonmodlepropertyinfo.getString("outputpinmappingtagname"));
                    if (Tool.isNoneString(jsonmodlepropertyinfo.getString("outpinmappingtag"))) {
                        throw new RuntimeException("out mapping is none");
                    }
                    JSONObject outresource = new JSONObject();
                    outresource.put("resource", ModleProperty.SOURCE_TYPE_OPC);
                    outresource.put("outmappingtag", jsonmodlepropertyinfo.getString("outpinmappingtag"));
                    outresource.put("modlepinsId", -1);//输出模块的输入引脚id
                    outputpropertyImp.setResource(outresource);
                    outputpropertyImp.setPindir(ModleProperty.PINDIROUTPUT);
                    outputpropertyImp.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_BASE);
                    count = projectOperaterImp.insertoutmodlepropertybusiness(inputpropertyImp, outputpropertyImp);
                    break;
                }
                default: {
                    throw new RuntimeException("unknow type");
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


    @RequestMapping("/creatempcmodleproperties")
    @ResponseBody
    public String creatempcmodleproperties(@RequestParam("modlepropertyinfo") String modlepropertyinfo) {

        JSONObject result = new JSONObject();
        try {
            JSONObject jsonmodlepropertyinfo = JSONObject.parseObject(modlepropertyinfo);
            int count = 0;
            switch (jsonmodlepropertyinfo.getString("pintype")) {

                case ModleProperty.TYPE_PIN_PV: {
                    MPCModleProperty pvpinmpcModleProperty = new MPCModleProperty();
                    pvpinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    pvpinmpcModleProperty.setModlePinName(jsonmodlepropertyinfo.getString("pvmodlePinName"));
                    pvpinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    pvpinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_PV);
                    pvpinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    pvpinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("pvmodleOpcTagName"));//spmodleOpcTag
                    pvpinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("pvmodleOpcTag"));
                    String pvresourcemodleId = jsonmodlepropertyinfo.getString("pvresourcemodleId");
                    String pvresourcemodlepinsId = jsonmodlepropertyinfo.getString("pvresourcemodlepinsId");
                    String pvpincontantvalue = jsonmodlepropertyinfo.getString("pvpincontantvalue");
                    if (Tool.isNoneString(pvresourcemodleId) || Tool.isNoneString(pvresourcemodlepinsId)) {
                        if (Tool.isNoneString(pvpincontantvalue)) {
                            throw new RuntimeException("pv pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(pvpincontantvalue));
                            pvpinmpcModleProperty.setResource(resource);
                            pvpinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();

                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(pvresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(pvresourcemodlepinsId));
                        pvpinmpcModleProperty.setResource(resource);

                    }

                    pvpinmpcModleProperty.setDeadZone(jsonmodlepropertyinfo.getDouble("deadZone"));
                    pvpinmpcModleProperty.setFunelinitValue(jsonmodlepropertyinfo.getDouble("funelinitValue"));

                    pvpinmpcModleProperty.setFunneltype(jsonmodlepropertyinfo.getString("funneltype"));
                    pvpinmpcModleProperty.setQ(jsonmodlepropertyinfo.getDouble("Q"));
                    pvpinmpcModleProperty.setReferTrajectoryCoef(jsonmodlepropertyinfo.getDouble("referTrajectoryCoef"));
                    pvpinmpcModleProperty.setTracoefmethod(jsonmodlepropertyinfo.getString("tracoefmethod"));


                    int pinorder = 0;
                    Matcher pvmatch = pvpattern.matcher(jsonmodlepropertyinfo.getString("pvmodlePinName"));
                    if (pvmatch.find()) {
                        pinorder = Integer.parseInt(pvmatch.group(2));
                    } else {
                        throw new RuntimeException("can't match pin order");
                    }


                    MPCModleProperty pvuppinmpcModleProperty = new MPCModleProperty();
                    pvuppinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    pvuppinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    pvuppinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_PVUP + pinorder);
                    pvuppinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_PVUP);
                    pvuppinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);

                    pvuppinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("pvupmodleOpcTagName"));//spmodleOpcTag
                    pvuppinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("pvupmodleOpcTag"));

                    String pvupresourcemodleId = jsonmodlepropertyinfo.getString("pvupresourcemodleId");
                    String pvupresourcemodlepinsId = jsonmodlepropertyinfo.getString("pvupresourcemodlepinsId");
                    String pvuppincontantvalue = jsonmodlepropertyinfo.getString("pvuppincontantvalue");
                    if (Tool.isNoneString(pvupresourcemodleId) || Tool.isNoneString(pvupresourcemodlepinsId)) {
                        if (Tool.isNoneString(pvuppincontantvalue)) {
                            throw new RuntimeException("pvup pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(pvuppincontantvalue));
                            pvuppinmpcModleProperty.setResource(resource);
                            pvuppinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(pvupresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(pvupresourcemodlepinsId));
                        pvuppinmpcModleProperty.setResource(resource);
                    }


                    MPCModleProperty pvdownpinmpcModleProperty = new MPCModleProperty();
                    pvdownpinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    pvdownpinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    pvdownpinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_PVDOWN + pinorder);
                    pvdownpinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_PVDOWN);
                    pvdownpinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    pvdownpinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("pvdownmodleOpcTagName"));//spmodleOpcTag
                    pvdownpinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("pvdownmodleOpcTag"));

                    String pvdownresourcemodleId = jsonmodlepropertyinfo.getString("pvdownresourcemodleId");
                    String pvdownresourcemodlepinsId = jsonmodlepropertyinfo.getString("pvdownresourcemodlepinsId");
                    String pvdownpincontantvalue = jsonmodlepropertyinfo.getString("pvdownpincontantvalue");
                    if (Tool.isNoneString(pvdownresourcemodleId) || Tool.isNoneString(pvdownresourcemodlepinsId)) {
                        if (Tool.isNoneString(pvdownpincontantvalue)) {
                            throw new RuntimeException("pvdown pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(pvdownpincontantvalue));
                            pvdownpinmpcModleProperty.setResource(resource);
                            pvdownpinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(pvdownresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(pvdownresourcemodlepinsId));
                        pvdownpinmpcModleProperty.setResource(resource);
                    }


                    MPCModleProperty sppinmpcModleProperty = new MPCModleProperty();
                    sppinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    sppinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    sppinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_SP + pinorder);
                    sppinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_SP);
                    sppinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    sppinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("spmodleOpcTagName"));//spmodleOpcTag
                    sppinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("spmodleOpcTag"));

                    String spresourcemodleId = jsonmodlepropertyinfo.getString("spresourcemodleId");
                    String spresourcemodlepinsId = jsonmodlepropertyinfo.getString("spresourcemodlepinsId");
                    String sppincontantvalue = jsonmodlepropertyinfo.getString("sppincontantvalue");
                    if (Tool.isNoneString(spresourcemodleId) || Tool.isNoneString(spresourcemodlepinsId)) {
                        if (Tool.isNoneString(sppincontantvalue)) {
                            throw new RuntimeException("pvdown pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(sppincontantvalue));
                            sppinmpcModleProperty.setResource(resource);
                            sppinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(spresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(spresourcemodlepinsId));
                        sppinmpcModleProperty.setResource(resource);
                    }
                    count = projectOperaterImp.insertmpcmodlepvrelationpropertybusiness(pvpinmpcModleProperty, pvuppinmpcModleProperty, pvdownpinmpcModleProperty, sppinmpcModleProperty);
                    break;
                }
                case ModleProperty.TYPE_PIN_MV: {

                    MPCModleProperty mvpinmpcModleProperty = new MPCModleProperty();
                    mvpinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    mvpinmpcModleProperty.setModlePinName(jsonmodlepropertyinfo.getString("mvmodlePinName"));
                    mvpinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    mvpinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_MV);
                    mvpinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    mvpinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("mvmodleOpcTagName"));
                    mvpinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("mvmodleOpcTag"));
                    String mvresourcemodleId = jsonmodlepropertyinfo.getString("mvresourcemodleId");
                    String mvresourcemodlepinsId = jsonmodlepropertyinfo.getString("mvresourcemodlepinsId");
                    String mvpincontantvalue = jsonmodlepropertyinfo.getString("mvpincontantvalue");
                    if (Tool.isNoneString(mvresourcemodleId) || Tool.isNoneString(mvresourcemodlepinsId)) {
                        if (Tool.isNoneString(mvpincontantvalue)) {
                            throw new RuntimeException("mv pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(mvpincontantvalue));
                            mvpinmpcModleProperty.setResource(resource);
                            mvpinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();

                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(mvresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(mvresourcemodlepinsId));
                        mvpinmpcModleProperty.setResource(resource);
                    }

                    mvpinmpcModleProperty.setR(jsonmodlepropertyinfo.getDouble("R"));
                    mvpinmpcModleProperty.setDmvHigh(jsonmodlepropertyinfo.getDouble("dmvHigh"));

                    mvpinmpcModleProperty.setDmvLow(jsonmodlepropertyinfo.getDouble("dmvLow"));


                    int pinorder = 0;
                    Matcher pvmatch = mvpattern.matcher(jsonmodlepropertyinfo.getString("mvmodlePinName"));
                    if (pvmatch.find()) {
                        pinorder = Integer.parseInt(pvmatch.group(2));
                    } else {
                        throw new RuntimeException("can't match pin order");
                    }


                    MPCModleProperty mvuppinmpcModleProperty = new MPCModleProperty();
                    mvuppinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    mvuppinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    mvuppinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_MVUP + pinorder);
                    mvuppinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_MVUP);
                    mvuppinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);

                    mvuppinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("mvupmodleOpcTagName"));//spmodleOpcTag
                    mvuppinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("mvupmodleOpcTag"));

                    String mvupresourcemodleId = jsonmodlepropertyinfo.getString("mvupresourcemodleId");
                    String mvupresourcemodlepinsId = jsonmodlepropertyinfo.getString("mvupresourcemodlepinsId");
                    String mvuppincontantvalue = jsonmodlepropertyinfo.getString("mvuppincontantvalue");
                    if (Tool.isNoneString(mvupresourcemodleId) || Tool.isNoneString(mvupresourcemodlepinsId)) {
                        if (Tool.isNoneString(mvuppincontantvalue)) {
                            throw new RuntimeException("mvup pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(mvuppincontantvalue));
                            mvuppinmpcModleProperty.setResource(resource);
                            mvuppinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(mvupresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(mvupresourcemodlepinsId));
                        mvuppinmpcModleProperty.setResource(resource);
                    }


                    MPCModleProperty mvdownpinmpcModleProperty = new MPCModleProperty();
                    mvdownpinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    mvdownpinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    mvdownpinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_MVDOWN + pinorder);
                    mvdownpinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_MVDOWN);
                    mvdownpinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    mvdownpinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("mvdownmodleOpcTagName"));//spmodleOpcTag
                    mvdownpinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("mvdownmodleOpcTag"));

                    String mvdownresourcemodleId = jsonmodlepropertyinfo.getString("mvdownresourcemodleId");
                    String mvdownresourcemodlepinsId = jsonmodlepropertyinfo.getString("mvdownresourcemodlepinsId");
                    String mvdownpincontantvalue = jsonmodlepropertyinfo.getString("mvdownpincontantvalue");
                    if (Tool.isNoneString(mvdownresourcemodleId) || Tool.isNoneString(mvdownresourcemodlepinsId)) {
                        if (Tool.isNoneString(mvdownpincontantvalue)) {
                            throw new RuntimeException("mvdown pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(mvdownpincontantvalue));
                            mvdownpinmpcModleProperty.setResource(resource);
                            mvdownpinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(mvdownresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(mvdownresourcemodlepinsId));
                        mvdownpinmpcModleProperty.setResource(resource);
                    }


                    MPCModleProperty mvfbpinmpcModleProperty = new MPCModleProperty();
                    mvfbpinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    mvfbpinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    mvfbpinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_MVFB + pinorder);
                    mvfbpinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_MVFB);
                    mvfbpinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    mvfbpinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("mvfbmodleOpcTagName"));//mvfbmodleOpcTag
                    mvfbpinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("mvfbmodleOpcTag"));

                    String mvfbresourcemodleId = jsonmodlepropertyinfo.getString("mvfbresourcemodleId");
                    String mvfbresourcemodlepinsId = jsonmodlepropertyinfo.getString("mvfbresourcemodlepinsId");
                    String mvfbpincontantvalue = jsonmodlepropertyinfo.getString("mvfbpincontantvalue");
                    if (Tool.isNoneString(mvfbresourcemodleId) || Tool.isNoneString(mvfbresourcemodlepinsId)) {
                        if (Tool.isNoneString(mvfbpincontantvalue)) {
                            throw new RuntimeException("mvfbdown pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(mvfbpincontantvalue));
                            mvfbpinmpcModleProperty.setResource(resource);
                            mvfbpinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(mvfbresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(mvfbresourcemodlepinsId));
                        mvfbpinmpcModleProperty.setResource(resource);
                    }
                    count = projectOperaterImp.insertmpcmodlepvrelationpropertybusiness(mvpinmpcModleProperty, mvuppinmpcModleProperty, mvdownpinmpcModleProperty, mvfbpinmpcModleProperty);
                    break;
                }
                case ModleProperty.TYPE_PIN_FF: {

                    MPCModleProperty ffpinmpcModleProperty = new MPCModleProperty();
                    ffpinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    ffpinmpcModleProperty.setModlePinName(jsonmodlepropertyinfo.getString("ffmodlePinName"));
                    ffpinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    ffpinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_FF);
                    ffpinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    ffpinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("ffmodleOpcTagName"));
                    ffpinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("ffmodleOpcTag"));
                    String ffresourcemodleId = jsonmodlepropertyinfo.getString("ffresourcemodleId");
                    String ffresourcemodlepinsId = jsonmodlepropertyinfo.getString("ffresourcemodlepinsId");
                    String ffpincontantvalue = jsonmodlepropertyinfo.getString("ffpincontantvalue");
                    if (Tool.isNoneString(ffresourcemodleId) || Tool.isNoneString(ffresourcemodlepinsId)) {
                        if (Tool.isNoneString(ffpincontantvalue)) {
                            throw new RuntimeException("ff pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(ffpincontantvalue));
                            ffpinmpcModleProperty.setResource(resource);
                            ffpinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();

                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(ffresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(ffresourcemodlepinsId));
                        ffpinmpcModleProperty.setResource(resource);
                    }


                    int pinorder = 0;
                    Matcher pvmatch = ffpattern.matcher(jsonmodlepropertyinfo.getString("ffmodlePinName"));
                    if (pvmatch.find()) {
                        pinorder = Integer.parseInt(pvmatch.group(2));
                    } else {
                        throw new RuntimeException("can't match pin order");
                    }


                    MPCModleProperty ffuppinmpcModleProperty = new MPCModleProperty();
                    ffuppinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    ffuppinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    ffuppinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_FFUP + pinorder);
                    ffuppinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_FFUP);
                    ffuppinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);

                    ffuppinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("ffupmodleOpcTagName"));//spmodleOpcTag
                    ffuppinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("ffupmodleOpcTag"));

                    String mvupresourcemodleId = jsonmodlepropertyinfo.getString("ffupresourcemodleId");
                    String mvupresourcemodlepinsId = jsonmodlepropertyinfo.getString("ffupresourcemodlepinsId");
                    String mvuppincontantvalue = jsonmodlepropertyinfo.getString("ffuppincontantvalue");
                    if (Tool.isNoneString(mvupresourcemodleId) || Tool.isNoneString(mvupresourcemodlepinsId)) {
                        if (Tool.isNoneString(mvuppincontantvalue)) {
                            throw new RuntimeException("ffup pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(mvuppincontantvalue));
                            ffuppinmpcModleProperty.setResource(resource);
                            ffuppinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(mvupresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(mvupresourcemodlepinsId));
                        ffuppinmpcModleProperty.setResource(resource);
                    }


                    MPCModleProperty ffdownpinmpcModleProperty = new MPCModleProperty();
                    ffdownpinmpcModleProperty.setPindir(ModleProperty.PINDIRINPUT);
                    ffdownpinmpcModleProperty.setRefmodleId(jsonmodlepropertyinfo.getInteger("refmodleId"));
                    ffdownpinmpcModleProperty.setModlePinName(ModleProperty.TYPE_PIN_FFDOWN + pinorder);
                    ffdownpinmpcModleProperty.setPintype(ModleProperty.TYPE_PIN_FFDOWN);
                    ffdownpinmpcModleProperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
                    ffdownpinmpcModleProperty.setOpcTagName(jsonmodlepropertyinfo.getString("ffdownmodleOpcTagName"));//spmodleOpcTag
                    ffdownpinmpcModleProperty.setModleOpcTag(jsonmodlepropertyinfo.getString("ffdownmodleOpcTag"));

                    String mvdownresourcemodleId = jsonmodlepropertyinfo.getString("ffdownresourcemodleId");
                    String mvdownresourcemodlepinsId = jsonmodlepropertyinfo.getString("ffdownresourcemodlepinsId");
                    String mvdownpincontantvalue = jsonmodlepropertyinfo.getString("ffdownpincontantvalue");
                    if (Tool.isNoneString(mvdownresourcemodleId) || Tool.isNoneString(mvdownresourcemodlepinsId)) {
                        if (Tool.isNoneString(mvdownpincontantvalue)) {
                            throw new RuntimeException("ffdown pin data resource is none!");
                        } else {
                            JSONObject resource = new JSONObject();
                            resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                            resource.put("value", Double.parseDouble(mvdownpincontantvalue));
                            ffdownpinmpcModleProperty.setResource(resource);
                            ffdownpinmpcModleProperty.setOpcTagName("");
                        }
                    } else {
                        JSONObject resource = new JSONObject();
                        resource.put("resource", ModleProperty.SOURCE_TYPE_MODLE);
                        resource.put("modleId", Integer.parseInt(mvdownresourcemodleId));
                        resource.put("modlepinsId", Integer.parseInt(mvdownresourcemodlepinsId));
                        ffdownpinmpcModleProperty.setResource(resource);
                    }
                    count = projectOperaterImp.insertmpcmodlepvrelationpropertybusiness(ffpinmpcModleProperty, ffuppinmpcModleProperty, ffdownpinmpcModleProperty);
                    break;
                }
                default: {
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


    @RequestMapping("/creatempcmodlerespon")
    @ResponseBody
    public String creatempcmodlerespon(@RequestParam("responcontext") String responcontext) {
        JSONObject modlejsonObject = JSONObject.parseObject(responcontext);
        JSONObject result = new JSONObject();

        int modleid;
        String responid;
        String inputpinName;
        String outputpinName;
        float K;
        float T;
        float Tau;
        float effectRatio;
        ResponTimeSerise respontimeserise;
        JSONObject jsonres;

        try {
            modleid = modlejsonObject.getInteger("refrencemodleId");
//            responid = modlejsonObject.getString("responid").trim();
            inputpinName = modlejsonObject.getString("inputpinName").trim();
            outputpinName = modlejsonObject.getString("outputpinName").trim();
            K = modlejsonObject.getFloat("K");
            T = modlejsonObject.getFloat("T");
            Tau = modlejsonObject.getFloat("Tau");
            effectRatio = ((modlejsonObject.getString("effectRatio").equals("")) || (modlejsonObject.getString("effectRatio") == null)) ? 1f : modlejsonObject.getFloat("effectRatio");
            respontimeserise = new ResponTimeSerise();

            respontimeserise.setInputPins(inputpinName);
            respontimeserise.setOutputPins(outputpinName);
            respontimeserise.setRefrencemodleId(modleid);
//            respontimeserise.setModletagId(responid.equals("") ? -1 : Integer.valueOf(responid));
            jsonres = new JSONObject();
            jsonres.put("k", K);
            jsonres.put("t", T);
            jsonres.put("tao", Tau);
            respontimeserise.setStepRespJson(jsonres);
            respontimeserise.setEffectRatio(effectRatio);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        try {

            projectOperaterImp.insertResponTimeSerise(respontimeserise);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result.put("msg", "error");
            return result.toJSONString();
        }

        result.put("msg", "success");
        return result.toJSONString();
    }


    /***view***/

    @RequestMapping("/newproject")
    public ModelAndView viewnewproject() {
        ModelAndView modelAndView = new ModelAndView();
        try {
            modelAndView.setViewName("newproject");
            return modelAndView;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

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


    @RequestMapping("/vieweditprojectparam")
    public ModelAndView vieweditprojectparam(@RequestParam("projectid") int projectid) {
        ModelAndView modelAndView = new ModelAndView();
        try {
            Project project = projectOperaterImp.findProjectById(projectid);
            modelAndView.setViewName("projectparamsedit");
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
                    modelAndView.setViewName("outputmodleedit");
                    modelAndView.addObject("outputmodle", (OUTModle) modle);
                    return modelAndView;
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
                    MPCModle mpcModle = (MPCModle) modle;
                    modelAndView.setViewName("mpcmodleedit");
                    modelAndView.addObject("mpcmodle", mpcModle);
                    Map<String, List<BaseModlePropertyImp>> points = projectOperaterImp.findparentmodleboutputpinsbusiness(mpcModle.getModleId());
                    modelAndView.addObject("points", points);
                    MPCModleProperty auto=(MPCModleProperty)Tool.selectmodleProperyByPinname(MPCModleProperty.TYPE_PIN_MODLE_AUTO,mpcModle.getPropertyImpList(),MPCModleProperty.PINDIRINPUT);
                    modelAndView.addObject("auto",auto);
                    return modelAndView;
                }
                case Modle.MODLETYPE_PID: {
                    PIDModle pidModle = (PIDModle) modle;
                    Map<String, List<BaseModlePropertyImp>> points = projectOperaterImp.findparentmodleboutputpinsbusiness(pidModle.getModleId());
                    modelAndView.setViewName("pidmodleedit");
                    modelAndView.addObject("pidmodle", pidModle);
                    modelAndView.addObject("kp", Tool.selectmodleProperyByPinname("kp", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));
                    modelAndView.addObject("ki", Tool.selectmodleProperyByPinname("ki", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));
                    modelAndView.addObject("kd", Tool.selectmodleProperyByPinname("kd", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));

                    modelAndView.addObject("pv", (MPCModleProperty)Tool.selectmodleProperyByPinname("pv", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));

                    modelAndView.addObject("sp", Tool.selectmodleProperyByPinname("sp", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));

                    modelAndView.addObject("mv", (MPCModleProperty) Tool.selectmodleProperyByPinname("mv", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));
                    modelAndView.addObject("mvup", Tool.selectmodleProperyByPinname("mvup", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));
                    modelAndView.addObject("mvdown", Tool.selectmodleProperyByPinname("mvdown", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));
                    modelAndView.addObject("ff", Tool.selectmodleProperyByPinname("ff", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));
                    modelAndView.addObject("auto", Tool.selectmodleProperyByPinname("auto", pidModle.getPropertyImpList(), ModleProperty.PINDIRINPUT));

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

    @RequestMapping("/viewempcmodlerundetail")
    public ModelAndView viewmodledetail(@RequestParam("projectid") int projectid, @RequestParam("modleId") int modleId) {
        Project project = projectManager.getProjectPool().get(projectid);
        Modle modle = project.getIndexmodles().get(modleId);

        MPCModle controlModle = (MPCModle) modle;//modleConstainer.getRunnableModulepool().get(Integer.valueOf(modleid.trim()));
        ModelAndView mv = new ModelAndView();
        mv.setViewName("mpcmodlerundetail");
        if (null != controlModle) {
            mv.addObject("isrunable", true);
            List<MPCModleProperty> runnablepvpins = controlModle.getRunablePins(controlModle.getCategoryPVmodletag(), controlModle.getMaskisRunnablePVMatrix());

            mv.addObject("enablePVPins", runnablepvpins);

            List<MPCModleProperty> runnableMVPins = controlModle.getRunablePins(controlModle.getCategoryMVmodletag(), controlModle.getMaskisRunnableMVMatrix());

            mv.addObject("enableMVPins", runnableMVPins);

            List<MPCModleProperty> runnableFFPins = controlModle.getRunablePins(controlModle.getCategoryFFmodletag(), controlModle.getMaskisRunnableFFMatrix());

            mv.addObject("enableFFPins", runnableFFPins);
        } else {
            mv.addObject("isrunable", false);
            controlModle = (MPCModle) projectOperaterImp.findModleByid(modleId);//modleDBServe.getModle();
        }
        mv.addObject("modle", controlModle);
        return mv;
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
                            basemodleproperty.setOpcTagName(Tool.isNoneString(jsonpoints.getJSONArray("data").getJSONObject(index).getString("tagname")) ? jsonpoints.getJSONArray("data").getJSONObject(index).getString("tag") : jsonpoints.getJSONArray("data").getJSONObject(index).getString("tagname"));
                            baseModlePropertyImpList.add(basemodleproperty);
                        }
                    }
                    modelAndView.addObject("points", baseModlePropertyImpList);
                    return modelAndView;
                }

                case Modle.MODLETYPE_OUTPUT: {


                    modelAndView.setViewName("outmodlepropertyaddinput");
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
                            //todo 这里要判断筛选下只有输出属性的点位
                            BaseModlePropertyImp basemodleproperty = new BaseModlePropertyImp();
                            basemodleproperty.setModleOpcTag(jsonpoints.getJSONArray("data").getJSONObject(index).getString("tag"));
                            basemodleproperty.setOpcTagName(Tool.isNoneString(jsonpoints.getJSONArray("data").getJSONObject(index).getString("tagname")) ? jsonpoints.getJSONArray("data").getJSONObject(index).getString("tag") : jsonpoints.getJSONArray("data").getJSONObject(index).getString("tagname"));
                            baseModlePropertyImpList.add(basemodleproperty);
                        }
                    }
                    modelAndView.addObject("outpinmappings", baseModlePropertyImpList);


                    Map<String,List<BaseModlePropertyImp>> points = projectOperaterImp.findparentmodleboutputpinsbusiness(modleId);

                    modelAndView.addObject("points", points);

                    return modelAndView;
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
                        Map<String,List<BaseModlePropertyImp>> points = projectOperaterImp.findparentmodleboutputpinsbusiness(modleId);
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
                        Map<String,List<BaseModlePropertyImp>> points = projectOperaterImp.findparentmodleboutputpinsbusiness(modleId);
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

                case Modle.MODLETYPE_PID: {
                    if (pindir.equals(ModleProperty.PINDIROUTPUT)) {
                        modelAndView.setViewName("pidmodlepropertyaddoutput");
                        modelAndView.addObject("modleId", modleId);
                        modelAndView.addObject("modletype", modletype);
                        modelAndView.addObject("pindir", pindir);
                        return modelAndView;
                    }
                }
                case Modle.MODLETYPE_MPC: {
                    if (pindir.equals(ModleProperty.PINDIROUTPUT)) {
                        modelAndView.setViewName("mpcmodlepropertyaddoutput");
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


    @RequestMapping("/viewaddmpcmodleproperty")
    public ModelAndView viewaddmpcmodleproperty(@RequestParam("modleId") int modleId, @RequestParam("pintype") String pintype) {
        ModelAndView modelAndView = new ModelAndView();
        try {
//            JSONObject jsonproperyinfo = JSONObject.parseObject(properyinfo);
            switch (pintype) {
                case ModleProperty.TYPE_PIN_PV: {
                    modelAndView.setViewName("mpcmodleaddpvpin");
                    modelAndView.addObject("modleId", modleId);
                    modelAndView.addObject("pintype", pintype);

                    //未使用的pv引脚名称
                    List<MPCModleProperty> mpcModlePropertyList = projectOperaterImp.findMPCModlePropertyByModleid(modleId);
                    List<MPCModleProperty> usedpvpin = Tool.getspecialpintypeBympc(ModleProperty.TYPE_PIN_PV, mpcModlePropertyList);
                    modelAndView.addObject("unuserpinscope", Tool.getunUserPinScope(pvpattern, usedpvpin, mpcpinnumber));
                    ;

                    //pv数据来源
                    Map<String,List<BaseModlePropertyImp>> points = projectOperaterImp.findparentmodleboutputpinsbusiness(modleId);
                    modelAndView.addObject("points", points);
                    return modelAndView;
                }
                case ModleProperty.TYPE_PIN_MV: {
                    modelAndView.setViewName("mpcmodleaddmvpin");
                    modelAndView.addObject("modleId", modleId);
                    modelAndView.addObject("pintype", pintype);

                    //未使用的pv引脚名称
                    List<MPCModleProperty> mpcModlePropertyList = projectOperaterImp.findMPCModlePropertyByModleid(modleId);
                    List<MPCModleProperty> usedpvpin = Tool.getspecialpintypeBympc(ModleProperty.TYPE_PIN_MV, mpcModlePropertyList);
                    modelAndView.addObject("unuserpinscope", Tool.getunUserPinScope(mvpattern, usedpvpin, mpcpinnumber));
                    ;

                    //pv数据来源
                    Map<String,List<BaseModlePropertyImp>> points = projectOperaterImp.findparentmodleboutputpinsbusiness(modleId);
                    modelAndView.addObject("points", points);
                    return modelAndView;
                }

                case ModleProperty.TYPE_PIN_FF: {
                    modelAndView.setViewName("mpcmodleaddffpin");
                    modelAndView.addObject("modleId", modleId);
                    modelAndView.addObject("pintype", pintype);

                    //未使用的pv引脚名称
                    List<MPCModleProperty> mpcModlePropertyList = projectOperaterImp.findMPCModlePropertyByModleid(modleId);
                    List<MPCModleProperty> usedpvpin = Tool.getspecialpintypeBympc(ModleProperty.TYPE_PIN_FF, mpcModlePropertyList);
                    modelAndView.addObject("unuserpinscope", Tool.getunUserPinScope(ffpattern, usedpvpin, mpcpinnumber));
                    ;

                    //pv数据来源
                    Map<String,List<BaseModlePropertyImp>> points = projectOperaterImp.findparentmodleboutputpinsbusiness(modleId);
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


    @RequestMapping("/viewaddmpcmodlerespon")
    public ModelAndView viewaddmpcmodlerespon(@RequestParam("modleId") int modleId) {

        /**
         * 1、选择出已经使用mv ff引脚
         * 2、
         * */
        List<MPCModleProperty> userinputpinscope = new ArrayList<>();

        List<MPCModleProperty> usemvpinscope = new ArrayList<>();
        List<MPCModleProperty> useffpinscope = new ArrayList<>();
        List<MPCModleProperty> usepvpinscope = new ArrayList<>();


        List<MPCModleProperty> useroutputpinscope = new ArrayList<>();


        try {

            List<MPCModleProperty> mpcModlePropertyList = projectOperaterImp.findMPCModlePropertyByModleid(modleId);

            for (MPCModleProperty mpcModleProperty : mpcModlePropertyList) {
                if(!Tool.isNoneString(mpcModleProperty.getPintype())){
                    switch (mpcModleProperty.getPintype()) {
                        case ModleProperty.TYPE_PIN_FF: {
                            useffpinscope.add(mpcModleProperty);
                            break;
                        }
                        case ModleProperty.TYPE_PIN_MV: {
                            usemvpinscope.add(mpcModleProperty);
                            break;
                        }
                        case ModleProperty.TYPE_PIN_PV: {
                            usepvpinscope.add(mpcModleProperty);
                            break;
                        }
                    }
                }

            }

            userinputpinscope.addAll(usemvpinscope);
            userinputpinscope.addAll(useffpinscope);

            useroutputpinscope = usepvpinscope;// modleDBServe.pinsbypintype(modleid, ModlePin.TYPE_PIN_PV);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }


        ModelAndView view = new ModelAndView();
        view.setViewName("mpcmodleaddrespon");
        view.addObject("modleId", modleId);

        view.addObject("userinputpinscope", userinputpinscope);
        view.addObject("useroutputpinscope", useroutputpinscope);
        return view;

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
                            String tag=jsonpoints.getJSONArray("data").getJSONObject(index).getString("tag");
                            basemodleproperty.setModleOpcTag(tag);
                            String tagname=jsonpoints.getJSONArray("data").getJSONObject(index).getString("tagname");
                            basemodleproperty.setOpcTagName(Tool.isNoneString(tagname)?tag:tagname);
                            baseModlePropertyImpList.add(basemodleproperty);
                        }
                    }
                    modelAndView.addObject("points", baseModlePropertyImpList);
                    return modelAndView;
                }

                case Modle.MODLETYPE_OUTPUT: {
                    BaseModlePropertyImp inputbaseModlePropertyImp = projectOperaterImp.findBaseModlePropertyByid(modlepinsId);
                    List<BaseModlePropertyImp> outputmodlepropertyImpList = projectOperaterImp.findBaseModlePropertyByModleid(inputbaseModlePropertyImp.getRefmodleId());
                    BaseModlePropertyImp outputbaseModlePropertyImp = null;
                    for (BaseModlePropertyImp baseModleProperty : outputmodlepropertyImpList) {
                        if (baseModleProperty.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                            if (baseModleProperty.getResource().getInteger("modlepinsId") == inputbaseModlePropertyImp.getModlepinsId()) {
                                outputbaseModlePropertyImp = baseModleProperty;
                                break;
                            }

                        }
                    }
                    modelAndView.setViewName("outmodlepropertyupdateinput");
                    modelAndView.addObject("modletype", modletype);
                    modelAndView.addObject("inputproperty", inputbaseModlePropertyImp);
                    modelAndView.addObject("outproperty", outputbaseModlePropertyImp);
                    String pointinfo = HttpUtils.PostData(oceandir + "/pointoperate/getalpoints", null);
                    JSONObject jsonpoints = JSONObject.parseObject(pointinfo);
                    List<BaseModlePropertyImp> baseModlePropertyImpList = new ArrayList<>();
                    if (jsonpoints.getString("msg").equals("success")) {
                        for (int index = 0; index < jsonpoints.getJSONArray("data").size(); index++) {
                            BaseModlePropertyImp basemodleproperty = new BaseModlePropertyImp();
                            basemodleproperty.setModleOpcTag(jsonpoints.getJSONArray("data").getJSONObject(index).getString("tag"));
                            basemodleproperty.setOpcTagName(Tool.isNoneString(jsonpoints.getJSONArray("data").getJSONObject(index).getString("tagname")) ? jsonpoints.getJSONArray("data").getJSONObject(index).getString("tag") : jsonpoints.getJSONArray("data").getJSONObject(index).getString("tagname"));
                            baseModlePropertyImpList.add(basemodleproperty);
                        }
                    }
                    modelAndView.addObject("outpinmappings", baseModlePropertyImpList);


                    Map<String,List<BaseModlePropertyImp>> points = projectOperaterImp.findparentmodleboutputpinsbusiness(inputbaseModlePropertyImp.getRefmodleId());

                    modelAndView.addObject("points", points);
                    return modelAndView;
                }
                case Modle.MODLETYPE_FILTER: {
                    BaseModlePropertyImp baseModlePropertyImp = projectOperaterImp.findBaseModlePropertyByid(modlepinsId);
                    if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIRINPUT)) {
                        modelAndView.setViewName("filtermodlepropertyupdateinput");
                        Map<String,List<BaseModlePropertyImp>> points = projectOperaterImp.findparentmodleboutputpinsbusiness(baseModlePropertyImp.getRefmodleId());
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
                        Map<String,List<BaseModlePropertyImp>> points = projectOperaterImp.findparentmodleboutputpinsbusiness(baseModlePropertyImp.getRefmodleId());
                        modelAndView.addObject("points", points);

                    } else if (baseModlePropertyImp.getPindir().equals(ModleProperty.PINDIROUTPUT)) {
                        modelAndView.setViewName("customizmodlepropertyupdateoutput");
                    }
                    modelAndView.addObject("modletype", modletype);
                    modelAndView.addObject("customizmodle", baseModlePropertyImp);
                    return modelAndView;

                }

                case Modle.MODLETYPE_PID: {
                    BaseModlePropertyImp baseModlePropertyImp = projectOperaterImp.findBaseModlePropertyByid(modlepinsId);
                    modelAndView.setViewName("pidmodlepropertyupdateoutput");
                    modelAndView.addObject("modletype", modletype);
                    modelAndView.addObject("pidmodle", baseModlePropertyImp);
                    return modelAndView;
                }
                case Modle.MODLETYPE_MPC: {
                    BaseModlePropertyImp baseModlePropertyImp = projectOperaterImp.findBaseModlePropertyByid(modlepinsId);
                    modelAndView.setViewName("mpcmodlepropertyupdateoutput");
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

    @RequestMapping("/viewupdatempcmodleproperty")
    public ModelAndView viewupdatempcmodleproperty(@RequestParam("modlepinsId") int modlepinsId, @RequestParam("pintype") String pintype) {
        ModelAndView modelAndView = new ModelAndView();
        try {
//            JSONObject jsonproperyinfo = JSONObject.parseObject(properyinfo);
            switch (pintype) {
                case ModleProperty.TYPE_PIN_PV: {
                    modelAndView.setViewName("mpcmodleupdatepvpin");

                    MPCModleProperty pv = projectOperaterImp.findMPCModlePropertyByid(modlepinsId);
                    MPCModle mpcModle = (MPCModle) projectOperaterImp.findModleByid(pv.getRefmodleId());
                    MPCModleProperty pvup = null;
                    MPCModleProperty pvdown = null;
                    MPCModleProperty sp = null;

                    int pinorder = 0;
                    Matcher pvmatch = pvpattern.matcher(pv.getModlePinName());
                    if (pvmatch.find()) {
                        pinorder = Integer.parseInt(pvmatch.group(2));
                    } else {
                        throw new RuntimeException("can't match pin order");
                    }

                    for (ModleProperty modleProperty : mpcModle.getPropertyImpList()) {
                        MPCModleProperty mpcproperty = (MPCModleProperty) modleProperty;
                        if (mpcproperty.getModlePinName().equals(ModleProperty.TYPE_PIN_PVUP + pinorder)) {
                            pvup = mpcproperty;
                            continue;
                        }

                        if (mpcproperty.getModlePinName().equals(ModleProperty.TYPE_PIN_PVDOWN + pinorder)) {

                            pvdown = mpcproperty;
                            continue;
                        }

                        if (mpcproperty.getModlePinName().equals(ModleProperty.TYPE_PIN_SP + pinorder)) {
                            sp = mpcproperty;
                            continue;
                        }

                    }

                    modelAndView.addObject("pv", pv);
                    modelAndView.addObject("pvup", pvup);
                    modelAndView.addObject("pvdown", pvdown);
                    modelAndView.addObject("sp", sp);

                    modelAndView.addObject("pintype", pintype);

                    //未使用的pv引脚名称
                    List<MPCModleProperty> mpcModlePropertyList = projectOperaterImp.findMPCModlePropertyByModleid(pv.getRefmodleId());
                    List<MPCModleProperty> usedpvpin = Tool.getspecialpintypeBympc(ModleProperty.TYPE_PIN_PV, mpcModlePropertyList);
                    List<Integer> unuserpinscope = Tool.getunUserPinScope(pvpattern, usedpvpin, mpcpinnumber);
                    unuserpinscope.add(0, pinorder);
                    modelAndView.addObject("unuserpinscope", unuserpinscope);
                    modelAndView.addObject("pinorder", pinorder);
                    //pv数据来源
                    Map<String,List<BaseModlePropertyImp>> points = projectOperaterImp.findparentmodleboutputpinsbusiness(pv.getRefmodleId());
                    modelAndView.addObject("points", points);
                    return modelAndView;
                }
                case ModleProperty.TYPE_PIN_MV: {
                    modelAndView.setViewName("mpcmodleupdatemvpin");
                    MPCModleProperty mv = projectOperaterImp.findMPCModlePropertyByid(modlepinsId);
                    MPCModle mpcModle = (MPCModle) projectOperaterImp.findModleByid(mv.getRefmodleId());
                    MPCModleProperty mvup = null;
                    MPCModleProperty mvdown = null;
                    MPCModleProperty mvfb = null;

                    int pinorder = 0;
                    Matcher pvmatch = mvpattern.matcher(mv.getModlePinName());
                    if (pvmatch.find()) {
                        pinorder = Integer.parseInt(pvmatch.group(2));
                    } else {
                        throw new RuntimeException("can't match pin order");
                    }

                    for (ModleProperty modleProperty : mpcModle.getPropertyImpList()) {
                        MPCModleProperty mpcproperty = (MPCModleProperty) modleProperty;
                        if (mpcproperty.getModlePinName().equals(ModleProperty.TYPE_PIN_MVUP + pinorder)) {
                            mvup = mpcproperty;
                            continue;
                        }

                        if (mpcproperty.getModlePinName().equals(ModleProperty.TYPE_PIN_MVDOWN + pinorder)) {

                            mvdown = mpcproperty;
                            continue;
                        }

                        if (mpcproperty.getModlePinName().equals(ModleProperty.TYPE_PIN_MVFB + pinorder)) {
                            mvfb = mpcproperty;
                            continue;
                        }

                    }

                    modelAndView.addObject("mv", mv);
                    modelAndView.addObject("mvup", mvup);
                    modelAndView.addObject("mvdown", mvdown);
                    modelAndView.addObject("mvfb", mvfb);

                    modelAndView.addObject("pintype", pintype);

                    //未使用的pv引脚名称
                    List<MPCModleProperty> mpcModlePropertyList = projectOperaterImp.findMPCModlePropertyByModleid(mv.getRefmodleId());
                    List<MPCModleProperty> usedmvpin = Tool.getspecialpintypeBympc(ModleProperty.TYPE_PIN_MV, mpcModlePropertyList);
                    List<Integer> unuserpinscope = Tool.getunUserPinScope(mvpattern, usedmvpin, mpcpinnumber);
                    unuserpinscope.add(0, pinorder);
                    modelAndView.addObject("unuserpinscope", unuserpinscope);
                    modelAndView.addObject("pinorder", pinorder);
                    //pv数据来源
                    Map<String,List<BaseModlePropertyImp>> points = projectOperaterImp.findparentmodleboutputpinsbusiness(mv.getRefmodleId());
                    modelAndView.addObject("points", points);
                    return modelAndView;
                }
                case ModleProperty.TYPE_PIN_FF: {
                    modelAndView.setViewName("mpcmodleupdateffpin");
                    MPCModleProperty ff = projectOperaterImp.findMPCModlePropertyByid(modlepinsId);
                    MPCModle mpcModle = (MPCModle) projectOperaterImp.findModleByid(ff.getRefmodleId());
                    MPCModleProperty ffup = null;
                    MPCModleProperty ffdown = null;
//                    MPCModleProperty fb=null;

                    int pinorder = 0;
                    Matcher pvmatch = ffpattern.matcher(ff.getModlePinName());
                    if (pvmatch.find()) {
                        pinorder = Integer.parseInt(pvmatch.group(2));
                    } else {
                        throw new RuntimeException("can't match pin order");
                    }

                    for (ModleProperty modleProperty : mpcModle.getPropertyImpList()) {
                        MPCModleProperty mpcproperty = (MPCModleProperty) modleProperty;
                        if (mpcproperty.getModlePinName().equals(ModleProperty.TYPE_PIN_FFUP + pinorder)) {
                            ffup = mpcproperty;
                            continue;
                        }

                        if (mpcproperty.getModlePinName().equals(ModleProperty.TYPE_PIN_FFDOWN + pinorder)) {

                            ffdown = mpcproperty;
                            continue;
                        }

                    }

                    modelAndView.addObject("ff", ff);
                    modelAndView.addObject("ffup", ffup);
                    modelAndView.addObject("ffdown", ffdown);

                    modelAndView.addObject("pintype", pintype);

                    //未使用的pv引脚名称
                    List<MPCModleProperty> mpcModlePropertyList = projectOperaterImp.findMPCModlePropertyByModleid(ff.getRefmodleId());
                    List<MPCModleProperty> usedmvpin = Tool.getspecialpintypeBympc(ModleProperty.TYPE_PIN_FF, mpcModlePropertyList);
                    List<Integer> unuserpinscope = Tool.getunUserPinScope(ffpattern, usedmvpin, mpcpinnumber);
                    unuserpinscope.add(0, pinorder);
                    modelAndView.addObject("unuserpinscope", unuserpinscope);
                    modelAndView.addObject("pinorder", pinorder);
                    //pv数据来源
                    Map<String,List<BaseModlePropertyImp>> points = projectOperaterImp.findparentmodleboutputpinsbusiness(ff.getRefmodleId());
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

    @RequestMapping("/viewupdatempcmodlerespon")
    public ModelAndView viewupdatempcmodlerespon(@RequestParam("modleresponId") int modleresponId) {
        try {
            ResponTimeSerise respontimeserise = projectOperaterImp.findResponTimeSeriseByid(modleresponId);

            List<MPCModleProperty> userinputpinscope = new ArrayList<>();

            List<MPCModleProperty> usemvpinscope = new ArrayList<>();
            List<MPCModleProperty> useffpinscope = new ArrayList<>();
            List<MPCModleProperty> usepvpinscope = new ArrayList<>();


            List<MPCModleProperty> useroutputpinscope = new ArrayList<>();


//            =projectOperaterImp.findResponTimeSeriseByid(modleresponId);
            List<MPCModleProperty> mpcModlePropertyList = projectOperaterImp.findMPCModlePropertyByModleid(respontimeserise.getRefrencemodleId());

            for (MPCModleProperty mpcModleProperty : mpcModlePropertyList) {
                if(!Tool.isNoneString(mpcModleProperty.getPintype())){

                    switch (mpcModleProperty.getPintype()) {
                        case ModleProperty.TYPE_PIN_FF: {
                            useffpinscope.add(mpcModleProperty);
                            break;
                        }
                        case ModleProperty.TYPE_PIN_MV: {
                            usemvpinscope.add(mpcModleProperty);
                            break;
                        }
                        case ModleProperty.TYPE_PIN_PV: {
                            usepvpinscope.add(mpcModleProperty);
                            break;
                        }
                    }

                }

            }

            userinputpinscope.addAll(usemvpinscope);
            userinputpinscope.addAll(useffpinscope);

            useroutputpinscope = usepvpinscope;// modleDBServe.pinsbypintype(modleid, ModlePin.TYPE_PIN_PV);


            ModelAndView view = new ModelAndView();
            view.setViewName("mpcmodleupdaterespon");
            view.addObject("modleid", respontimeserise.getRefrencemodleId());

            view.addObject("userinputpinscope", userinputpinscope);
            view.addObject("useroutputpinscope", useroutputpinscope);
            view.addObject("respontimeserise", respontimeserise);
            JSONObject jsonrespm = respontimeserise.getStepRespJson();
            view.addObject("K", jsonrespm.get("k"));
            view.addObject("T", jsonrespm.get("t"));
            view.addObject("Tau", jsonrespm.get("tao"));

            return view;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;

    }
    /**modle run datail**/


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
                kpbasemodleproperty.setOpcTagName(opcTagName.equals("请选择") ? "" : opcTagName);
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

    private MPCModleProperty initpidplusproperty(String modleOpcTag, String opcTagName, String pinname, int modleId, String resourcemodleId, String resourcemodlepinsId, String properyconstant, String propertyid, double dmvhight, double dmvlow,double deadZone) {
        MPCModleProperty kpbasemodleproperty = new MPCModleProperty();
        kpbasemodleproperty.setModlePinName(pinname);
        kpbasemodleproperty.setOpcTagName(opcTagName);
        kpbasemodleproperty.setModlepropertyclazz(ModleProperty.MODLEPROPERTYCLAZZ_MPC);
        kpbasemodleproperty.setPindir(ModleProperty.PINDIRINPUT);
        kpbasemodleproperty.setRefmodleId(modleId);
        kpbasemodleproperty.setPinEnable(1);
        kpbasemodleproperty.setDmvLow(dmvlow);
        kpbasemodleproperty.setDmvHigh(dmvhight);
        kpbasemodleproperty.setDeadZone(deadZone);

        if (Tool.isNoneString(resourcemodleId) || Tool.isNoneString(resourcemodlepinsId)) {
            if (Tool.isNoneString(properyconstant)) {
                throw new RuntimeException(pinname + "resource or constant value is none");
            } else {
                JSONObject resource = new JSONObject();
                resource.put("resource", ModleProperty.SOURCE_TYPE_CONSTANT);
                resource.put("value", Double.parseDouble(properyconstant));
                kpbasemodleproperty.setResource(resource);
                kpbasemodleproperty.setModleOpcTag("" + Double.parseDouble(properyconstant));
                kpbasemodleproperty.setOpcTagName(opcTagName.equals("请选择") ? "" : opcTagName);
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
