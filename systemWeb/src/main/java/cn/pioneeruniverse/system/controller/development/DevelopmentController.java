package cn.pioneeruniverse.system.controller.development;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@RequestMapping("development")
public class DevelopmentController {
	@RequestMapping("toConstruction")
	public ModelAndView  toConstruction(){
		ModelAndView view = new ModelAndView();
		view.setViewName("developmentManagement/constructionManagement");
		return view;
	}
	
	@RequestMapping("toDevelopment")
	public ModelAndView  toDevelopment(){
		ModelAndView view = new ModelAndView();
		view.setViewName("developmentManagement/developmentTaskManagement");
		return view;
	}
	
}
