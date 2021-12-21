package cn.pioneeruniverse.dev.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.utils.CommonUtil;

@RestController
@RequestMapping("deploy")
public class DeploymentController {

	/**
	 * 跳转部署页面
	 * @return
	 */
	@RequestMapping(value = "toDeploy")
	public ModelAndView toStructureManage(HttpServletRequest request) {
		ModelAndView view = new ModelAndView();
		 view.addObject("currentUserId", CommonUtil.getCurrentUserId(request));
		 view.addObject("token", CommonUtil.getToken(request));
		view.setViewName("deployment/deployment");
		return view;
	}
}
