package cn.pioneeruniverse.dev.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;

@RestController
@RequestMapping("package")
public class PackageController {
	@RequestMapping("toPackage")
	public ModelAndView  toConstruction(HttpServletRequest request){
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("package/packageManage");
		return view;
	}
}
