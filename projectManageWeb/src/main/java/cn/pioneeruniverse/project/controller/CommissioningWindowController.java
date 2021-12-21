package cn.pioneeruniverse.project.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;

@RestController
@RequestMapping("commissioningWindow")
public class CommissioningWindowController {

	@RequestMapping("toCommissioningWindow")
	public ModelAndView toOamProject(HttpServletRequest request) {
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.PROJECT_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("project/commissioningWindow");
		return view;
	}
	
}
