package cn.pioneeruniverse.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;

@RestController
@RequestMapping("testSet")
public class TestSetController {

	@RequestMapping("toTestSet")
	public ModelAndView  toTestSet(HttpServletRequest request){
		ModelAndView view = new ModelAndView();
		view.addObject("testTaskId", request.getParameter("testTaskId"));
		view.addObject("workTaskId", request.getParameter("workTaskId"));
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.TEST_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("testSet/testSetList");
		return view;
	}
}
