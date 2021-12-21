package cn.pioneeruniverse.project.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.DateUtil;
import cn.pioneeruniverse.project.dao.mybatis.RequirementFeatureMapper;
import cn.pioneeruniverse.project.dao.mybatis.RequirementMapper;
import cn.pioneeruniverse.project.dao.mybatis.TblDevTaskMapper;
import cn.pioneeruniverse.project.entity.TblCommissioningWindow;
import cn.pioneeruniverse.project.entity.TblDevTask;
import cn.pioneeruniverse.project.entity.TblRequirementFeature;
import cn.pioneeruniverse.project.entity.TblRequirementInfo;
import cn.pioneeruniverse.project.entity.TblSystemInfo;
import cn.pioneeruniverse.project.service.commissioningWindow.CommissioningWindowService;

@RestController
@RequestMapping("commissioningWindow")
public class CommissioningWindowController extends BaseController {

	
	@Autowired
	private CommissioningWindowService commissioningWindowService;
	
	@Autowired
	private RequirementMapper requirementMapper;

	@Autowired
	private RequirementFeatureMapper requirementFeatureMapper;
	
	@Autowired
	private TblDevTaskMapper tblDevTaskMapper;
	
	/**
	 * 投产窗口条件查询带分页
	 * @param tblCommissioningWindow
	 * @return
	 */
	@RequestMapping(value="selectCommissioningWindows",method=RequestMethod.POST)
	public Map<String, Object> selectCommissioningWindows(TblCommissioningWindow tblCommissioningWindow, String year, Integer page,Integer rows){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblCommissioningWindow> list = commissioningWindowService.selectCommissioningWindows(tblCommissioningWindow,year,page,rows);
			List<TblCommissioningWindow> list2 = commissioningWindowService.selectCommissioningWindows(tblCommissioningWindow,year,1,Integer.MAX_VALUE);
			double total = Math.ceil(list2.size()*1.0/rows);    //总页数
			map.put("records", list2.size());    //查询的总条目数
			map.put("page", page);					//当前页
			map.put("total", total); 
			map.put("data", list);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 投产窗口条件查询带分页 bootstrap
	 * @param tblCommissioningWindow
	 * @return
	 */
	@RequestMapping(value="selectCommissioningWindows2",method=RequestMethod.POST)
	public Map<String, Object> selectCommissioningWindows2(TblCommissioningWindow tblCommissioningWindow, Integer pageNumber,Integer pageSize){
		HashMap<String, Object> map = new HashMap<>();
		try {
			List<TblCommissioningWindow> list = commissioningWindowService.selectAllWindows(tblCommissioningWindow,pageNumber,pageSize);
			List<TblCommissioningWindow> list2 = commissioningWindowService.selectAllWindows(tblCommissioningWindow,1,Integer.MAX_VALUE);
			map.put("total", list2.size());    //查询的总条目数
			map.put("rows", list);
			
		} catch (Exception e) {
			return super.handleException(e, "查询投产创建失败！");
		}
		return map;
	}
	
	/**
	 * 窗口类型查询下拉框的显示
	 * @return
	 */
	@RequestMapping(value="selectWindowType",method=RequestMethod.POST)
	public Map<String, Object> selectWindowType(){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<String> list = commissioningWindowService.selectWindowType();
			map.put("data", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 *    新增常规投产窗口
	 * @param tblCommissioningWindow
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="insertRoutineCommissioningWindow",method=RequestMethod.POST)
	public Map<String, Object> insertRoutineCommissioningWindow(@RequestBody String tblCommissioningWindow,HttpServletRequest request) throws Exception{
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {			
			Gson gson = new Gson();
			TblCommissioningWindow commissioningWindow = gson.fromJson(tblCommissioningWindow, TblCommissioningWindow.class);
			List<TblCommissioningWindow> cw=commissioningWindowService.
					findCommissioningByWindowDate(commissioningWindow);
			if(cw==null||cw.size()<=0) {
				commissioningWindowService.insertCommissioningWindowItmp(commissioningWindow,request);
			}else {
				map.put("status", Constants.ITMP_RETURN_FAILURE);
			}
		} catch (Exception e) {
			map.put("status", "3");
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 *    新增紧急投产窗口
	 * @param tblCommissioningWindow
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="insertUrgentCommissioningWindow",method=RequestMethod.POST)
	public Map<String, Object> insertUrgentCommissioningWindow(@RequestBody String tblCommissioningWindow,HttpServletRequest request) throws Exception{
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Gson gson = new Gson();
			TblCommissioningWindow commissioningWindow = gson.fromJson(tblCommissioningWindow, TblCommissioningWindow.class);
			List<TblCommissioningWindow> cw=commissioningWindowService.
					findCommissioningByWindowDate(commissioningWindow);
			if(cw==null||cw.size()<=0) {
				commissioningWindowService.insertCommissioningWindowItmp(commissioningWindow,request);
			}else {
				map.put("status", Constants.ITMP_RETURN_FAILURE);
			}
		} catch (Exception e) {
			map.put("status", "3");
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * （修改状态）
	 * @param id
	 * @return
	 */
	@RequestMapping(value="delectCommissioningWindow",method=RequestMethod.POST)
	public Map<String, Object> delectCommissioningWindow(Long id,Long status, HttpServletRequest request){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			HashMap<String, Object> result =commissioningWindowService.delectCommissioningWindowItmp(id,status, request);
			commissioningWindowService.delectCommissioningWindowTmp(result);			
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 编辑页面的数据回显（根据id查询窗口）
	 * @param id
	 * @return
	 */
	@RequestMapping(value="selectCommissioningWindowById",method=RequestMethod.POST)
	public Map<String, Object> selectCommissioningWindowById( Long id){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			TblCommissioningWindow tblCommissioningWindow = commissioningWindowService.selectCommissioningWindowById(id);
			map.put("data", tblCommissioningWindow);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 编辑窗口
	 * @param tblCommissioningWindow
	 * @param request
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value="editCommissioningWindow",method=RequestMethod.POST)
	public Map<String, Object> editCommissioningWindow(@RequestBody String tblCommissioningWindow, HttpServletRequest request) throws Exception{
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Gson gson = new Gson();
			TblCommissioningWindow commissioningWindow = gson.fromJson(tblCommissioningWindow, TblCommissioningWindow.class);
			List<TblCommissioningWindow> cw=commissioningWindowService.
					findCommissioningByWindowDate(commissioningWindow);
			if(cw==null||cw.size()<=0) {
				commissioningWindow=commissioningWindowService.editCommissioningWindowItmp(commissioningWindow,request);
				commissioningWindowService.editCommissioningWindowTmp(commissioningWindow);
			}else {
				map.put("status", Constants.ITMP_RETURN_FAILURE);
			}
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 获取关联任务弹窗搜索栏的任务状态列表
	 * @return
	 * wjdz
	 * 2019年1月2日
	 * 下午6:06:53
	 */
	@RequestMapping(value="selectFeatureType",method=RequestMethod.POST)
	public Map<String, Object> selectFeatureType(){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<String> list = commissioningWindowService.selectFeatureType();
			map.put("data", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 关联开发任务弹窗的分页搜索
	 * @param tblRequirementFeature
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * wjdz
	 * 2019年1月2日
	 * 下午6:57:51
	 */
	@RequestMapping(value="selectRequirement",method=RequestMethod.POST)
	public Map<String, Object> selectRequirement(TblRequirementFeature tblRequirementFeature, Integer pageNumber, Integer pageSize){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblRequirementFeature> list = commissioningWindowService.selectRequirement(tblRequirementFeature,pageNumber,pageSize);
			List<TblRequirementFeature> list2 = commissioningWindowService.selectRequirement(tblRequirementFeature,1,Integer.MAX_VALUE);
			map.put("total", list2.size());
			map.put("rows", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 关联开发任务
	 * @param windowId
	 * @param ids
	 * @return
	 * wjdz
	 * 2019年1月4日
	 * 上午10:49:40
	 */
	@RequestMapping(value="relationRequirement",method=RequestMethod.POST)
	public Map<String, Object> relationRequirement(Long windowId, @RequestParam(value="ids[]")Long[] ids, HttpServletRequest request){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			commissioningWindowService.relationRequirement(windowId, ids, request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	
	/**
	 * 根据投产窗口id通过开发任务查找相关系统
	 * @param windowId
	 * @return
	 */
	@RequestMapping(value="getSystemsByWindowId",method=RequestMethod.POST)
	public Map<String,Object> getSystemsByWindowId(Long windowId){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblSystemInfo> list = commissioningWindowService.getSystemsByWindowId(windowId);
			map.put("data", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "获取相关系统失败！");
		}
		return map;
	}
	
	
	//导出
	@RequestMapping("export")
	public void export(Long windowId, String systemIds, HttpServletRequest request, HttpServletResponse response) throws Exception {
//		List<TblRequirementInfo> list = commissioningWindowService.getExportRequirements(windowId, systemIds);
		Map<String,Object> map = new HashMap<>();
		map.put("windowId", windowId);
		map.put("systemIds", systemIds);
		List<TblRequirementFeature> features = requirementFeatureMapper.getRequirementId(map);
		List<Long> ids = new ArrayList<>();
		List<TblRequirementFeature> feaList = new ArrayList<>();
		for (TblRequirementFeature tblRequirementFeature : features) {
			if(tblRequirementFeature.getRequirementId() != null) {
				ids.add(tblRequirementFeature.getRequirementId());
			}else {
				feaList.add(tblRequirementFeature);
			}
		}
		List<TblRequirementInfo> list = new ArrayList<>();
		if(ids != null && ids.size() > 0) {
			list = requirementMapper.getRequirementsByIds(ids);  //需求
			for (TblRequirementInfo tblRequirementInfo : list) {
				Map<String,Object> map2 = new HashMap<>();
				map2.put("windowId", windowId);
				map2.put("systemIds", systemIds);
				map2.put("requirementId", tblRequirementInfo.getId());
				List<TblRequirementFeature> reqfeaList = requirementFeatureMapper.getRequirementFeatures(map2);
				tblRequirementInfo.setReqfeaList(reqfeaList);   //开发任务
				for (TblRequirementFeature tblRequirementFeature : reqfeaList) {
					List<TblDevTask> devTaskList = tblDevTaskMapper.getDevTasks(tblRequirementFeature.getId());
					tblRequirementFeature.setDevTaskList(devTaskList);  //工作任务
				}
			}
		}
		for (TblRequirementFeature tblRequirementFeature : feaList) {
			List<TblDevTask> devTaskList = tblDevTaskMapper.getDevTasks(tblRequirementFeature.getId());
			tblRequirementFeature.setDevTaskList(devTaskList);  //工作任务
		}
		commissioningWindowService.export(list, feaList, request, response);
	}
	
}
