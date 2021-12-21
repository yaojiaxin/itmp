package cn.pioneeruniverse.project.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.project.feignInterface.ProjectInfoInterface;

@RestController
@RequestMapping("project")
public class ProjectInfoController {

	@Autowired
	private ProjectInfoInterface projectInfoInterface;
	
	@RequestMapping("toProject")
	public ModelAndView toProject() {
		ModelAndView view = new ModelAndView();
		view.setViewName("project/projectManage");
		return view;
	}
	
	@RequestMapping("toProjectDetails")
	public ModelAndView toProjectDetails(Long id) {
		ModelAndView view = new ModelAndView();
		Map<String, Object> map = projectInfoInterface.selectProjectById(id);
		view.addObject(map);
		view.setViewName("");
		return view;
	}
}
