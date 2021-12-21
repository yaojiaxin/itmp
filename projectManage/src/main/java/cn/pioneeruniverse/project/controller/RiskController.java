package cn.pioneeruniverse.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.project.entity.TblRiskInfo;
import cn.pioneeruniverse.project.entity.TblRiskLog;
import cn.pioneeruniverse.project.service.risk.RiskService;

@RestController
@RequestMapping("risk")
public class RiskController extends BaseController {
	
	@Autowired
	private RiskService riskService;

	
	/**
	 * 风险列表
	 * @param projectId
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getRiskInfo",method=RequestMethod.POST)
	public Map<String, Object> getRiskInfo(Long projectId, HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblRiskInfo> list = riskService.getRiskInfo(projectId, request);
			map.put("data", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "风险列表查询失败！");
		}
		return map;
	}
	
	
	/**
	 * 新增风险
	 * @param riskInfo
	 * @param request
	 * @return
	 */
	@RequestMapping(value="insertRiskInfo",method=RequestMethod.POST)
	public Map<String, Object> insertRiskInfo(@RequestBody String riskInfo, HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			TblRiskInfo tblRiskInfo = JSONObject.parseObject(riskInfo, TblRiskInfo.class);
			riskService.insertRiskInfo(tblRiskInfo, request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "新增风险失败！");
		}
		return map;
	}
	
	
	/**
	 * 删除风险
	 * @param id
	 * @param request
	 * @return
	 */
	@RequestMapping(value="deleteRiskInfo",method=RequestMethod.POST)
	public Map<String, Object> deleteRiskInfo(Long id, HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			riskService.deleteRiskInfo(id, request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "删除风险失败！");
		}
		return map;
	}
	
	
	/**
	 * 风险详情
	 * @param id
	 * @return
	 */
	@RequestMapping(value="getRiskInfoById",method=RequestMethod.POST)
	public Map<String, Object> getRiskInfoById(Long id){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			TblRiskInfo tblRiskInfo = riskService.getRiskInfoById(id);
			//风险日志
			List<TblRiskLog> logs = riskService.getRiskLog(id);
			map.put("data", tblRiskInfo);
			map.put("logs", logs);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "风险详情获取失败！");
		}
		return map;	
	}
	
	
	/**
	 * 编辑风险
	 * @param riskInfo
	 * @param request
	 * @return
	 */
	@RequestMapping(value="updateRisk",method=RequestMethod.POST)
	public Map<String, Object> updateRisk(@RequestBody String riskInfo, HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			TblRiskInfo tblRiskInfo = JSONObject.parseObject(riskInfo, TblRiskInfo.class);
			riskService.updateRisk(tblRiskInfo, request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "风险编辑失败！");
		}
		return map;
	}
	
	
	/**
	 * 项目群管理风险列表
	 * @param programId
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getRiskInfoByProgram",method=RequestMethod.POST)
	public Map<String,Object> getRiskInfoByProgram(Long programId, HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblRiskInfo> list = riskService.getRiskInfoByProgram(programId, request);
			map.put("data", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "获取风险列表失败！");
		}
		return map;
	}
	
	
	
	
}
