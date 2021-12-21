package cn.pioneeruniverse.dev.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.utils.CommonUtil;

@RestController
@RequestMapping("sprintManageui")
public class SprintInfoController {

	@RequestMapping("toSprint")
	public ModelAndView toSprint(HttpServletRequest request, Long projectId, String systemId, String systemName) {
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("uid", CommonUtil.getCurrentUserId(request));
		view.addObject("projectId", projectId);
		view.addObject("systemId", systemId);
		view.addObject("systemName", systemName);
		view.setViewName("sprint/sprint");
		return view;
	}
}
