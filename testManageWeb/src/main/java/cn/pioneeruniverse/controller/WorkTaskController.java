package cn.pioneeruniverse.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;

@RestController
@RequestMapping("workTask")
public class WorkTaskController {
	
	@RequestMapping("toWorktask")
	public ModelAndView  toConstruction(HttpServletRequest request){
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("userId", CommonUtil.getCurrentUserId(request));
		view.addObject("userName", CommonUtil.getCurrentUserName(request));
		view.addObject("url",Constants.TEST_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("workTask/workTaskManage");
		return view;
	}
}
