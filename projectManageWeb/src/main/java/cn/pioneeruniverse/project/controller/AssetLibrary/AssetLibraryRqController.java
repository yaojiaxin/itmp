package cn.pioneeruniverse.project.controller.AssetLibrary;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
/**
 * @Author: weiji
 * @Description:
 * @Modified By:
 */
@RestController
@RequestMapping("assetRq")
public class AssetLibraryRqController {

	
	@RequestMapping("toRequirement")
	public ModelAndView toPersonnelManagement(HttpServletRequest request) {
		ModelAndView view = new ModelAndView();
		view.addObject("currentUserId", CommonUtil.getCurrentUserId(request));
		view.addObject("token", CommonUtil.getToken(request));
		//view.setViewName("assetsLibirary/assetsLibraryNeeds/assetsLibraryNeeds");
        view.setViewName("assetsLibrary/assetsLibraryNeeds/assetsLibraryNeeds");
		return view;
	}
	@RequestMapping(value = "/toAssociatedDemand", method = RequestMethod.GET)
	public ModelAndView toAssociatedDemand(HttpServletRequest request) {
		ModelAndView view = new ModelAndView();
		view.addObject("currentUserId", CommonUtil.getCurrentUserId(request));
		view.addObject("token", CommonUtil.getToken(request));
		view.setViewName("assetsLibrary/associatedDemand");
		return view;
	}

	@RequestMapping(value = "/toDevelopmentTask", method = RequestMethod.GET)
	public ModelAndView toDevelopmentTask(HttpServletRequest request) {
		ModelAndView view = new ModelAndView();
		view.addObject("currentUserId", CommonUtil.getCurrentUserId(request));
		view.addObject("token", CommonUtil.getToken(request));
		view.setViewName("assetsLibrary/developmentTask");
		return view;
	}

}
