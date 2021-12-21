package cn.pioneeruniverse.project.controller;

import cn.pioneeruniverse.common.utils.CommonUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 * Author:liushan
 * Date: 2019/5/14 下午 2:12
 */
@RestController
@RequestMapping("systemTree")
public class SystemTreeController {

    @RequestMapping(value = "toSystem",method = RequestMethod.GET)
    public ModelAndView toSystem(HttpServletRequest request){
        ModelAndView view = new ModelAndView();
        view.addObject("token", CommonUtil.getToken(request));
        view.addObject("userId",CommonUtil.getCurrentUserId(request));
        view.addObject("userName", CommonUtil.getCurrentUserName(request));
        view.setViewName("systemTree/AssetSystemTree");
        return view;
    }

    @RequestMapping(value = "toSystemTree",method = RequestMethod.GET)
    public ModelAndView toSystemTree(HttpServletRequest request){
        ModelAndView view = new ModelAndView();
        view.addObject("token", CommonUtil.getToken(request));
        view.addObject("userId",CommonUtil.getCurrentUserId(request));
        view.addObject("userName", CommonUtil.getCurrentUserName(request));
        view.setViewName("systemTree/systemTree");
        return view;
    }
}
