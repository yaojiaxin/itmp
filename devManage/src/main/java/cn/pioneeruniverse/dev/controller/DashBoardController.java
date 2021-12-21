package cn.pioneeruniverse.dev.controller;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.common.velocity.tag.VelocityDataDict;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.dev.service.dashboard.DashBoardService;

@RestController
@RequestMapping("dashBoard")
public class DashBoardController extends BaseController{
	
	@Autowired
	private DashBoardService dashBoardService;
		
	private static Logger log = LoggerFactory.getLogger(DashBoardController.class);
	
	@RequestMapping(value= "getUserDesk")
	public Map<String, Object> getUserDesk(HttpServletRequest request) {
		Map<String,Object> map = new HashMap<>();
		try {
			Long id=CommonUtil.getCurrentUserId(request);				
			map=dashBoardService.getUserDesk(id);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return map;
	}
	@RequestMapping(value= "getAllProject")
	public Map<String, Object> getAllProject(HttpServletRequest request) {
		Long id = CommonUtil.getCurrentUserId(request);
		Map<String, Object> map = new HashMap<>();
		try {
			map = dashBoardService.getAllProject(id);
		}catch (Exception e) {
			log.error(e.getMessage(), e);		
		}
		return map;
	}

	 /**
	 * @param systemId
	 * @param startDate
	 * @param endDate
	 * */
	@RequestMapping(value= "getDashBoard")
	public Map<String, Object> getDashBoard(Long systemId,String startDate,String endDate) {
		Map<String,Object> map = new HashMap<>();
		try {			
			map=dashBoardService.getDashBoard(systemId,startDate,endDate);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return map;
	}

	/**
	 * @param systemId
	 * @param startDate
	 * @param endDate
	 * */
	@RequestMapping(value= "getTheCumulative")
	public Map<String, Object> getTheCumulative(Long systemId,String startDate,String endDate) {
		Map<String,Object> map = new HashMap<>();
		try {			
			map=dashBoardService.getTheCumulative(systemId,startDate,endDate);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return map;
	}
	/**
	 * @param timeTraceId
	 *
	 * */
	@RequestMapping(value= "valueStreamMapping")
	public Map<String, Object> valueStreamMapping(Long timeTraceId){
		Map<String,Object> map = new HashMap<>();
		try {
			map=dashBoardService.valueStreamMapping(timeTraceId);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return map;
	}
	/** 获取投产窗口关联开发任务状态
	 * @param windowId
	 * @param systemId
	 *
	 * */
	@RequestMapping(value= "getFratureStatus")
	public Map<String, Object> getFratureStatus(@RequestParam(value="windowId")Long windowId,
												@RequestParam(value="systemId")Long systemId) {
		Map<String, Object> map = new HashMap<>();
		try {
			map=dashBoardService.getFratureStatus(windowId,systemId);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return map;
	}

	/** 获取子系统代码情况
	 * @param systemModuleId
	 *
	 * */
	@RequestMapping(value= "getSonarByModuleId")
	public Map<String, Object> getSonarByModuleId(Long systemModuleId) {
		Map<String, Object> map = new HashMap<>();
		try {
			map=dashBoardService.getSonarByModuleId(systemModuleId);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return map;
	}

	@RequestMapping(value= "getEnvironmentType")
	public String getEnvironmentType(Long environmentType) {
		JSONObject jsonObj = new JSONObject();
		VelocityDataDict dict= new VelocityDataDict();
		try {
			Map<String, String> result=dict.getDictMap("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
			if(environmentType!=null) {
				for(Map.Entry<String, String> entry:result.entrySet()){
					if(Long.valueOf(entry.getKey())==environmentType) {
						jsonObj.put("typeValue",entry.getValue());
						break;
					}
				}
			}
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return jsonObj.toJSONString();
	}
}
