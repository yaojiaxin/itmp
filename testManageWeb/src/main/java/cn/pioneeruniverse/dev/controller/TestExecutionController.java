package cn.pioneeruniverse.dev.controller;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: 测试执行管理 前台
 * Author:liushan
 * Date: 2019/1/21 下午 4:15
 */
@RestController
@RequestMapping("testExecution")
public class TestExecutionController {

    /**
     * 测试案例管理主页面
     * @param request
     * @return
     */
    @RequestMapping(value = "toTestCase",method = RequestMethod.GET)
    public ModelAndView toTestCase(HttpServletRequest request){
        ModelAndView view = new ModelAndView();
        view.addObject("token", CommonUtil.getToken(request));
    	view.addObject("url",Constants.TEST_MANAGE_UI_URL+request.getRequestURI());
    	view.addObject("uid",CommonUtil.getCurrentUserId(request));
    	view.addObject("username",CommonUtil.getCurrentUserName(request));
        view.setViewName("testManagement/testCaseManagement");
        return view;
    }
    
   


    /**
     * 测试执行管理主页面
     * @param request
     * @return
     */
    @RequestMapping(value = "toTestCaseRun",method = RequestMethod.GET)
    public ModelAndView toTestCaseRun(HttpServletRequest request){
        ModelAndView view = new ModelAndView();
        view.addObject("token", CommonUtil.getToken(request));
        view.addObject("url",Constants.TEST_MANAGE_UI_URL+request.getRequestURI());
        view.setViewName("testExecutionManagement/testExecutionManagement");
        return view;
    }
}
