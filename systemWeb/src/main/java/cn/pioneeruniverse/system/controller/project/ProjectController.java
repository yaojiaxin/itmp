package cn.pioneeruniverse.system.controller.project;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("project")
public class ProjectController {
	@RequestMapping("toProjectManage")
	public ModelAndView  toDeptManage(){
		ModelAndView view = new ModelAndView();
		view.setViewName("projectManagement/projectManagement");
		return view;
	}
	
	@RequestMapping("toDemand")
	public ModelAndView  todemand(){
		ModelAndView view = new ModelAndView();
		view.setViewName("projectManagement/demandManagement");
		return view;
	}
	
	@RequestMapping("toScheduling")
	public ModelAndView  toScheduling(){
		ModelAndView view = new ModelAndView();
		view.setViewName("projectManagement/schedulingManagement");
		return view;
	}
	
	
}
