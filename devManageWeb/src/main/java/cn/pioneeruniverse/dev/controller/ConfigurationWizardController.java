package cn.pioneeruniverse.dev.controller;

import cn.pioneeruniverse.common.controller.BaseController;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;


@RestController
@RequestMapping("configurationWizard")
public class ConfigurationWizardController extends BaseController{

	@RequestMapping(value = "toConfigurationWizard")
	public ModelAndView toConfigurationWizard() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("configurationWizard/configurationWizard");
		return modelAndView;
	}
}
