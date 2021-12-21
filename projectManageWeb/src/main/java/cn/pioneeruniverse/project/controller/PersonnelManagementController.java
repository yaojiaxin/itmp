package cn.pioneeruniverse.project.controller;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.project.feignInterface.ProjectInfoInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("personnelManagement")
public class PersonnelManagementController {

	
	@RequestMapping("toPersonnelManagement")
	public ModelAndView toPersonnelManagement(HttpServletRequest request) {
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url", Constants.PROJECT_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("personnelManagement/personnelManagement");
		return view;
	}
}
