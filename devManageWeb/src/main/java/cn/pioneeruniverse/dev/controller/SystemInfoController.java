package cn.pioneeruniverse.dev.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.dev.entity.TblSystemInfo;
import cn.pioneeruniverse.dev.feignInterface.systeminfo.SystemInfoInterface;

/**
*  系统前端controller 
* @author:tingting
* @version:2018年10月30日 下午2:35:44 
*/

@RestController
@RequestMapping("systeminfo")
public class SystemInfoController extends BaseController{
	@RequestMapping(value = "toSystem")
	public ModelAndView toSystemPage(HttpServletRequest request) {
		ModelAndView view  = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("systeminfo/systeminfoList");
		return view;
	}

	@RequestMapping(value = "toSystemScm")
	public ModelAndView toSystemScm(HttpServletRequest request) {
		ModelAndView view  = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("systeminfo/systemScmList");
		return view;
	}

	@RequestMapping(value = "toSystemDeploy")
	public ModelAndView toSystemDeploy(HttpServletRequest request) {
		ModelAndView view  = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("systeminfo/systemDeploy");
		return view;
	}

	@RequestMapping(value = "toVersionManagement")
	public ModelAndView toVersionManagement(HttpServletRequest request) {
		ModelAndView view  = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("systeminfo/versionManagement");
		return view;
	}

	@RequestMapping(value = "toAddSystemModel")
	public ModelAndView toAddSystemModel(HttpServletRequest request) {
		ModelAndView view  = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("systeminfo/addSystemModel");
		return view;
	}

	@RequestMapping(value = "toDeploymentConfig")
	public ModelAndView toDeploymentConfig(HttpServletRequest request) {
		ModelAndView view  = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("systeminfo/deploymentConfig");
		return view;
	}

	@RequestMapping(value = "toAutomatedTesting")
	public ModelAndView toAutomatedTesting(HttpServletRequest request) {
		ModelAndView view  = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("systeminfo/automatedTest");
		return view;
	}

	@RequestMapping(value = "toEnvironmentConfig")
	public ModelAndView toEnvironmentConfig(HttpServletRequest request) {
		ModelAndView view  = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("systeminfo/environmentConfig");
		return view;
	}

}
