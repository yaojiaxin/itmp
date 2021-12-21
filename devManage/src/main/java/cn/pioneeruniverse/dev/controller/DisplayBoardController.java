package cn.pioneeruniverse.dev.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.dao.mybatis.TblDevTaskMapper;
import cn.pioneeruniverse.dev.entity.DevTaskVo;
import cn.pioneeruniverse.dev.entity.TblDataDic;
import cn.pioneeruniverse.dev.entity.TblDevTask;
import cn.pioneeruniverse.dev.entity.TblProjectInfo;
import cn.pioneeruniverse.dev.entity.TblSprintInfo;
import cn.pioneeruniverse.dev.entity.TblSystemInfo;
import cn.pioneeruniverse.dev.service.displayboard.DisplayBoardService;
import cn.pioneeruniverse.dev.service.worktask.WorkTaskService;

/**
*  看板后台controller 
* @author:tingting
* @version:2019年3月20日 上午11:45:16 
*/

@RestController
@RequestMapping("displayboard")
public class DisplayBoardController extends BaseController{
	@Autowired
	private DisplayBoardService displayBoardService;
	@Autowired
	private WorkTaskService workTaskService;
	@Autowired
	private TblDevTaskMapper devTaskMapper;
	@Autowired
	private RedisUtils redisUtils;
	
	
	/**
	 * 根据冲刺查询 开发任务以及下面的工作任务
	 * */
	@RequestMapping("getDevTaskBySprintId")
	public Map<String, Object> getDevTaskBySprintId(Long sprintId){
		Map<String, Object> map = new HashMap<>();
		try {
			List<DevTaskVo> devTaskVos = displayBoardService.getDevTaskBySprintId(sprintId);
			for (DevTaskVo devTaskVo : devTaskVos) {
				List<TblDevTask> devTasks = displayBoardService.getDevTaskByReqFeatureId(devTaskVo.getId());
				devTaskVo.setDevTasks(devTasks);
				List<Map<String, Object>> statusCount = displayBoardService.getWorkTaskStatusCount(devTaskVo.getId());
				devTaskVo.setWorkStatusCount(statusCount);
			}
			
			List<TblDataDic> status = displayBoardService.getReqFeatureStatus();
					//getDataFromRedis("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS");
			
			Collections.sort(status, new Comparator<TblDataDic>(){
				@Override
				public int compare(TblDataDic o1, TblDataDic o2) {
					return o1.getValueSeq() - o2.getValueSeq();
				}});

			List<TblDataDic> workTaskstatus = getDataFromRedis("TBL_DEV_TASK_DEV_TASK_STATUS");
			
			map.put("status", status);
			map.put("workTaskstatus", workTaskstatus);
			map.put("reqFeatures", devTaskVos);
		} catch (Exception e) {
			return super.handleException(e, "查询开发任务失败");
		}
		return map;
	}
	
	/**
	 *移动泳道  修改开发任务的状态 
	 * */
	@RequestMapping("updateDevTaskStatus")
	public Map<String, Object> updateDevTaskStatus(Long reqFeatureId,String status,HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			if (reqFeatureId!=null && !StringUtils.isBlank(status)) {
				displayBoardService.updateDevTaskStatus(reqFeatureId,status,request);
				map.put("status", Constants.ITMP_RETURN_SUCCESS);
			}
		} catch (Exception e) {
			return super.handleException(e, "修改开发任务状态失败");
		}
		return map;
	}
	
	/**
	 * 根据冲刺查询 工作任务
	 * */
	@RequestMapping("getWorkTaskBySprintId")
	public Map<String, Object> getWorkTaskBySprintId(Long sprintId){
		Map<String, Object> map = new HashMap<>();
		try {
			List<TblDevTask> devTasks = displayBoardService.getWorkTaskBySprintId(sprintId);
			List<TblDataDic> status = displayBoardService.getWorTaskStatus();
			Collections.sort(status, new Comparator<TblDataDic>(){
				@Override
				public int compare(TblDataDic o1, TblDataDic o2) {
					return o1.getValueSeq() - o2.getValueSeq();
				}});
			map.put("status", status);
			map.put("devTasks", devTasks);
		} catch (Exception e) {
			return super.handleException(e, "查询工作任务失败");
		}
		return map;
	}
	
	/**
	 *移动泳道  修改工作任务的状态 
	 * */
	@RequestMapping("updateWorkTaskStatus")
	public Map<String, Object> updateWorkTaskStatus(Long devTaskId,Integer status,HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			if (devTaskId!=null && status!=null) {
				TblDevTask OlddevTask = devTaskMapper.getDevOld(devTaskId);//旧数据
				displayBoardService.updateWorkTaskStatus(devTaskId,status,request);
				workTaskService.logAll(devTaskId.toString(), "", OlddevTask, "编辑工作任务", null, request);
				map.put("status", Constants.ITMP_RETURN_SUCCESS);
			}
		} catch (Exception e) {
			return super.handleException(e, "修改工作任务状态失败");
		}
		return map;
	}
	
	
	/**
	 * 当前登录用户所在项目 除结项状态之外
	 * */
	@RequestMapping("getAllProject")
	public Map<String, Object> getAllProject(HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			Long uid = CommonUtil.getCurrentUserId(request);
			List<TblProjectInfo> projectInfos = displayBoardService.getAllProjectByUid(uid);
			map.put("projects", projectInfos);
		} catch (Exception e) {
			return super.handleException(e, "查询项目失败");
		}
		return map;
				
	}
	
	/**
	 * 根据项目id 查询 system  
	 * */
	@RequestMapping("getSystemByPId")
	public Map<String, Object> getSystemByPId(Long projectId){
		Map<String, Object> map = new HashMap<>();
		try {
			if (projectId!=null) {
				List<TblSystemInfo> systemInfos = displayBoardService.getSystemByPId(projectId);
				map.put("systemInfos", systemInfos);
			}
		} catch (Exception e) {
			return super.handleException(e, "查询系统失败");
		}
		
		return map;
	}
	
	/**
	 * 根据系统id 查询冲刺
	 * */
	@RequestMapping("getSprintBySystemId")
	public Map<String, Object> getSprintBySystemId(Long systemId){
		Map<String, Object> map = new HashMap<>();
		try {
			if (systemId!=null) {
				List<TblSprintInfo> sprintInfos = displayBoardService.getSprintBySystemId(systemId);
				map.put("sprintInfos", sprintInfos);
			}
		} catch (Exception e) {
			return super.handleException(e, "查询冲刺失败");
		}
		return map;
	}
	
	public List<TblDataDic> getDataFromRedis(String termCode) {
		String result = redisUtils.get(termCode).toString();
		List<TblDataDic> dics = new ArrayList<>();
		if (!StringUtils.isBlank(result)) {
			// dics = JsonUtil.fromJson(result, JsonUtil.createCollectionType(ArrayList.class,TblDataDic.class));
			Map<String, Object> maps = (Map<String, Object>) JSON.parse(result);
			for (Entry<String, Object> entry : maps.entrySet()) {
				TblDataDic dic = new TblDataDic();
				dic.setValueCode(entry.getKey());
				dic.setValueName(entry.getValue().toString());
				dics.add(dic);
			}
		}
		return dics;
	}




	//根据项目获取项目小组
	@RequestMapping(value="getProjectGroupByProjectId")
	public Map<String, Object> getProjectGroupBySystemId(HttpServletRequest request ,long projectId){
		Map<String, Object> result = new HashMap<>();
		try {

			String projectGroup=displayBoardService.getProjectGroupByProjectId(projectId);
			result.put("zNodes",projectGroup);



		} catch (Exception e) {
			return super.handleException(e, "获取小组出错");
		}
		return result;
	}


	
}
