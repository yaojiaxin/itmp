package cn.pioneeruniverse.system.controller.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("error")
public class ErrorController {

	Logger log = LoggerFactory.getLogger(ErrorController.class);
	
	@RequestMapping(value="/404")
	public ModelAndView  to404Page(){
		ModelAndView model = new ModelAndView();
		model.setViewName("error/404");
		return model; 
	}
	
	@RequestMapping(value="/500")
	public ModelAndView  to500Page(){
		log.info("ErrorController.to500Page");
		ModelAndView model = new ModelAndView();
		model.setViewName("error/500");
		return model; 
	}
	
	
	
	@RequestMapping(value="/400")
	public ModelAndView  to400Page(){
		ModelAndView model = new ModelAndView();
		model.setViewName("error/400");
		return model; 
	}
}
