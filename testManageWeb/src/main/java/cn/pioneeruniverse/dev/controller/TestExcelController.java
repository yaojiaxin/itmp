package cn.pioneeruniverse.dev.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;

@RestController
@RequestMapping("testReport")
public class TestExcelController {
	@RequestMapping(value = "toReport",method = RequestMethod.GET)
    public ModelAndView toTestExcel(HttpServletRequest request){
        ModelAndView view = new ModelAndView();
        view.addObject("token", CommonUtil.getToken(request));
    	view.addObject("url",Constants.TEST_MANAGE_UI_URL+request.getRequestURI());
    	view.addObject("uid",CommonUtil.getCurrentUserId(request));
    	view.addObject("username",CommonUtil.getCurrentUserName(request));
        view.setViewName("exportTestExcel/exportTestExcel");
        return view;
    }
}
