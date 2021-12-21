package cn.pioneeruniverse.dev.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.utils.CommonUtil;

@RestController
@RequestMapping("serverinfo")
public class ServerInfoController {
	@RequestMapping("toServerInfo")
	public ModelAndView  toServerInfo(HttpServletRequest request){
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.setViewName("serverinfo/serverinfo");
		return view;
	}
}
