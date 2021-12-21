package cn.pioneeruniverse.system.controller.configuration;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("configuration")
public class ConfigurationController {
	@RequestMapping("toSystemInformation")
	public ModelAndView  toSystemInformation(){
		ModelAndView view = new ModelAndView();
		view.setViewName("configurationManagement/systemInformationConfiguration");
		return view;
	}
	@RequestMapping("toToolInformation")
	public ModelAndView  toToolInformation(){
		ModelAndView view = new ModelAndView();
		view.setViewName("configurationManagement/toolInformationConfiguration");
		return view;
	}
	
}
