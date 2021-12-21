package cn.pioneeruniverse.dev.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.dev.entity.TblDeptInfo;
import cn.pioneeruniverse.dev.entity.TblUserInfo;
import cn.pioneeruniverse.dev.feignInterface.devtask.DevTaskInterface;
import cn.pioneeruniverse.dev.feignInterface.devtask.UserInterface;

/**
* 开发管理前台Controller 
* @author:tingting
* @version:2018年11月12日 上午11:25:38 
*/

@RestController
@RequestMapping("devtask")
public class DevTaskController extends BaseController{
	@Autowired
	private DevTaskInterface devTaskInterface;
	@Autowired
	private UserInterface userInterface;
	@Value("${requirement.att.url}")
	private String reqAttUrl;
	
	
	@RequestMapping(value = "toDevTask")
	public ModelAndView toDevTaskPage(HttpServletRequest request, String planId, String planName, String systemId, String systemName, String systemCode) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("token", CommonUtil.getToken(request));
		modelAndView.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		modelAndView.addObject("uid",CommonUtil.getCurrentUserId(request));
		String requirementFeatureStatus = request.getParameter("requirementFeatureStatus");
		modelAndView.addObject("requirementFeatureStatus", requirementFeatureStatus);
		modelAndView.addObject("username",CommonUtil.getCurrentUserName(request));
		modelAndView.addObject("reqAttUrl",reqAttUrl);
		modelAndView.addObject("planId", planId);
		modelAndView.addObject("planName", planName);
		modelAndView.addObject("systemId",systemId);
		modelAndView.addObject("systemName",systemName);		
		modelAndView.addObject("systemCode", systemCode);
		modelAndView.setViewName("devtask/devtaskList");
		return modelAndView;
		
	}
	//详情
	@RequestMapping(value = "toInfo")
	public ModelAndView toInfo(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("token", CommonUtil.getToken(request));
		modelAndView.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		modelAndView.addObject("uid",CommonUtil.getCurrentUserId(request));
		String requirementFeatureStatus = request.getParameter("requirementFeatureStatus");
		modelAndView.addObject("requirementFeatureStatus", requirementFeatureStatus);
		modelAndView.addObject("username",CommonUtil.getCurrentUserName(request));
		modelAndView.addObject("reqAttUrl",reqAttUrl);
		modelAndView.setViewName("devtask/devtaskList_check");
		return modelAndView;
	}
	//编辑
	@RequestMapping(value = "toEdit")
	public ModelAndView toEdit(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("token", CommonUtil.getToken(request));
		modelAndView.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		modelAndView.addObject("uid",CommonUtil.getCurrentUserId(request));
		String requirementFeatureStatus = request.getParameter("requirementFeatureStatus");
		modelAndView.addObject("requirementFeatureStatus", requirementFeatureStatus);
		modelAndView.addObject("username",CommonUtil.getCurrentUserName(request));
		modelAndView.addObject("reqAttUrl",reqAttUrl);
		modelAndView.setViewName("devtask/devtaskList_edit");
		return modelAndView;
	}
	
	//拆分
	@RequestMapping(value = "toSplit")
	public ModelAndView toSplit(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("token", CommonUtil.getToken(request));
		modelAndView.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		modelAndView.addObject("uid",CommonUtil.getCurrentUserId(request));
		String requirementFeatureStatus = request.getParameter("requirementFeatureStatus");
		modelAndView.addObject("requirementFeatureStatus", requirementFeatureStatus);
		modelAndView.addObject("username",CommonUtil.getCurrentUserName(request));
		modelAndView.addObject("reqAttUrl",reqAttUrl);
		modelAndView.setViewName("devtask/devtaskList_split");
		return modelAndView;
	}
	
	
	
	@RequestMapping(value="getData",method = RequestMethod.POST)
	public Map<String, Object> getData(Long id){
		Map<String, Object> result = new HashMap<>();
		try {
			Map<String, Object> map = devTaskInterface.toAddData();
			List<TblDeptInfo> depts = userInterface.getDept();
			result.put("depts", depts);
			result.putAll(map);
			if (id!=null && !"".equals(id)) {
				Map<String, Object> devtask = devTaskInterface.getOneDevTask(id);
				result.putAll(devtask);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "fail");
		}
		
		return result;
		
	}

}
