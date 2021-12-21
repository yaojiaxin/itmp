package cn.pioneeruniverse.project.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;

@RestController
@RequestMapping("newProject")
public class NewProjectController {

	@RequestMapping("toNewProject")
	public ModelAndView toOamProject(HttpServletRequest request,String name) {
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.PROJECT_MANAGE_UI_URL+request.getRequestURI());
		view.addObject("name",name);
		view.setViewName("newProject/newProject");
		return view;
	}

	@RequestMapping("toNewProjectDetail")
	public ModelAndView toNewProjectDetail(HttpServletRequest request,Integer id,Integer type) {
		System.out.println("id:"+id  +"		type:"+type);
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.PROJECT_MANAGE_UI_URL+request.getRequestURI());
		view.addObject("id",id);
		view.addObject("type",type);
		view.setViewName("newProject/newProjectDetail");
		return view;
	}

	@RequestMapping("toPlanChart")
	public ModelAndView toPlanChart(HttpServletRequest request,Integer id, String name,Long requestUserId) {
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.PROJECT_MANAGE_UI_URL+request.getRequestURI());
		view.addObject("id",id);
		view.addObject("name",name);
		view.addObject("requestUserId",requestUserId);
		view.setViewName("newProject/planChart");
		return view;
	}
	@RequestMapping("toPlanManage")
	public ModelAndView toPlanManage(HttpServletRequest request,Integer id, String name,Integer version) {
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.PROJECT_MANAGE_UI_URL+request.getRequestURI());
		view.addObject("id",id);
		view.addObject("name",name);
		view.addObject("version",version);
		view.setViewName("newProject/planManage");
		return view;
	}
	
	@RequestMapping("toRiskManage")
	public ModelAndView toRiskManage(HttpServletRequest request,Integer id, Long userId, String userName, String name, Integer type) {
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.PROJECT_MANAGE_UI_URL+request.getRequestURI());
		view.addObject("id",id);
		view.addObject("userId",userId);
		view.addObject("userName",userName);
		view.addObject("name",name);
		view.addObject("type",type);
		view.setViewName("newProject/riskManage");
		return view;
	}
	
	@RequestMapping("toUpdateManage")
	public ModelAndView toUpdateManage(HttpServletRequest request,Integer id, Long userId, String userName, String name, Integer type) {
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.PROJECT_MANAGE_UI_URL+request.getRequestURI());
		view.addObject("id",id);
		view.addObject("userId",userId);
		view.addObject("userName",userName);
		view.addObject("name",name);
		view.addObject("type",type);
		view.setViewName("newProject/updateManage");
		return view;
	}
	
	@RequestMapping("toQuestionManage")
	public ModelAndView toQuestionManage(HttpServletRequest request,Integer id, Long userId, String userName, String name, Integer type) {
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.PROJECT_MANAGE_UI_URL+request.getRequestURI());
		view.addObject("id",id);
		view.addObject("userId",userId);
		view.addObject("userName",userName);
		view.addObject("name",name);
		view.addObject("type",type);
		view.setViewName("newProject/questionManage");
		return view;
	}
	


}
