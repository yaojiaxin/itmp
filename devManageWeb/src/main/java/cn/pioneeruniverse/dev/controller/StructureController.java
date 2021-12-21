package cn.pioneeruniverse.dev.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.dev.feignInterface.structure.StructureInterface;


@RestController
@RequestMapping("structure")
public class StructureController {
	@Autowired
	private StructureInterface structureInterface;

	
	@RequestMapping(value = "toStructureManage")
	public ModelAndView toStructureManage(HttpServletRequest request) {
		ModelAndView view = new ModelAndView();
		view.setViewName("structure/structureManage");
		  view.addObject("currentUserId", CommonUtil.getCurrentUserId(request));
		 view.addObject("token", CommonUtil.getToken(request));
		 view.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		 return view;
	}
	
	
/*	//暂且不需要权限
	
	@RequestMapping(value = "getAllSystemInfo", method = RequestMethod.GET)
	public Map<String, Object> getAllUserByAjax(Integer pageIndex,Integer pageSize) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			
			result=	structureInterface.getAllSystemInfo(1, 10);
			
			
			
		}catch (Exception e) {
			
			
		}
System.out.println(result);
		return result;
	}*/
	
	
	
	
	/*@RequestMapping(value = "getSystemModule", method = RequestMethod.GET)
	public Map<String, Object> getSystemModuleById(Integer id) {
		Map<String, Object> result = new HashMap<String, Object>();

		try {
			result=structureInterface.getSystemModule(1);
			
			
			
			
		}catch (Exception e) {
			
			
		}

		return result;
	}*/
	
	
/*	@RequestMapping(value = "creatJenkinsJob", method = RequestMethod.GET)
	public Map<String, Object> creatJenkinsJob(Integer systemId,String systemName) {
		Map<String, Object> result = new HashMap<String, Object>();
		//

		try {
			String[] module= {"12"};
			result=structureInterface.creatJenkinsJob(3, "最新", "1", module);
			
			
			
			
		}catch (Exception e) {
			
			
		}

		return result;
	}*/
	
	

}
