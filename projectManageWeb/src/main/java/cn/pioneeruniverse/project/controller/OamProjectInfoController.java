package cn.pioneeruniverse.project.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;

@RestController
@RequestMapping("oamproject")
public class OamProjectInfoController {
	

	@RequestMapping("toOamProject")
	public ModelAndView toOamProject(HttpServletRequest request) {
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.PROJECT_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("project/oamProjectManage");
		return view;
	}
	
	//跳转到详情或者编辑页面带详情数据（同一个页面）
	@RequestMapping("toEditProject")
	public ModelAndView toEditProject(Long id,String type) {
		ModelAndView view = new ModelAndView();
		view.addObject("id", id);
		view.addObject("type", type);
		view.setViewName("project/editProject");
		return view;
	}
	
}
