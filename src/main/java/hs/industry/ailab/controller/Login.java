package hs.industry.ailab.controller;

import hs.industry.ailab.dao.mysql.service.ProjectOperaterImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;

/**
 * @author zzx
 * @version 1.0
 * @date 2021/1/22 13:51
 */
@Controller

public class Login {
    private Logger logger = LoggerFactory.getLogger(ProjectEdit.class);

    @Value("${companyname}")
    private String companyname;

    @Autowired
    private ProjectOperaterImp projectOperaterImp;

    @RequestMapping("/")
    public ModelAndView userlogin() {
        ModelAndView mv=new ModelAndView();
        mv.setViewName("viewbusiness/login");
        try {
            mv.addObject("companyName", new String(companyname.getBytes("iso-8859-1"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(),e);
        }
        return mv;
    }

    @RequestMapping("/index")
    public ModelAndView projectindex(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("viewbusiness/index");
        try {
            mv.addObject("companyName", new String(companyname.getBytes("iso-8859-1"), "UTF-8"));
            mv.addObject("contrlmodles",projectOperaterImp.findAllProject());

        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(),e);
        }
        return mv;
    }

    @RequestMapping("/home")
    public ModelAndView home(){
        ModelAndView mv=new ModelAndView();
        mv.setViewName("viewbusiness/home");
        try {
            mv.addObject("companyName", new String(companyname.getBytes("iso-8859-1"), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(),e);
        }
        return mv;
    }

}
