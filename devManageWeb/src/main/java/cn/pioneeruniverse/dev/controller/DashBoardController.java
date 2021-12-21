package cn.pioneeruniverse.dev.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.controller.BaseController;


@RestController
@RequestMapping("dashBoard")
public class DashBoardController extends BaseController{
	@Value("${requirement.att.url}")
	private String reqAttUrl;
	
	@RequestMapping(value = "toWorkDesk")
	public ModelAndView toWorkDesk() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("dashBoard/workDeskNew");
		modelAndView.addObject("reqAttUrl",reqAttUrl);
		return modelAndView;
	}
		
	@RequestMapping(value = "toDashBoard")
	public ModelAndView toDashBoard() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("dashBoard/dashBoard");
		return modelAndView;
	}
}
