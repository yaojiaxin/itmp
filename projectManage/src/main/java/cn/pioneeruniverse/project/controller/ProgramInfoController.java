package cn.pioneeruniverse.project.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
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
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.project.entity.TblProgramInfo;
import cn.pioneeruniverse.project.service.program.ProgramService;

@RestController
@RequestMapping("program")
public class ProgramInfoController extends BaseController {
	
	@Autowired
	private ProgramService programService;
	
	@Autowired
	private RedisUtils redisUtils;
	
	/**
	 * 项目群列表查询
	 * @param programInfo
	 * @param request
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping(value="getAllPrograms",method=RequestMethod.POST)
	public Map<String, Object> getAllPrograms(TblProgramInfo programInfo, Integer page,Integer rows, HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Long uid  = CommonUtil.getCurrentUserId(request);
			LinkedHashMap codeMap = (LinkedHashMap) redisUtils.get(CommonUtil.getToken(request));
			List<String> roleCodes = (List<String>) codeMap.get("roles");
			List<TblProgramInfo> list = programService.getAllPrograms(programInfo, uid, roleCodes, page, rows);
			List<TblProgramInfo> list2 = programService.getAllPrograms(programInfo, uid, roleCodes, 1, Integer.MAX_VALUE);
			double total = Math.ceil(list2.size()*1.0/rows);
			map.put("records", list2.size());    //查询的总条目数
			map.put("page", page);					//当前页
			map.put("total", total);             //总页数
			map.put("data", list);				//每页数据
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "项目群查询失败！");
		}
		return map;
	}
	
	
	/**
	 * 编辑项目群数据回显
	 * @param id
	 * @return
	 */
	@RequestMapping(value="getProgramById",method=RequestMethod.POST)
	public Map<String, Object> getProgramById(Long id){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			TblProgramInfo programInfo = programService.getProgramById(id);
			map.put("data", programInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "获取项目群详情失败！");
		}
		return map;
	}
	
	/**
	 * 编辑项目群
	 * @param programInfo
	 * @param request
	 * @return
	 */
	@RequestMapping(value="updateProgram",method=RequestMethod.POST)
	public Map<String, Object> updateProgram(@RequestBody String programInfo, HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			TblProgramInfo tblProgramInfo = JSONObject.parseObject(programInfo, TblProgramInfo.class);
			programService.updateProgram(tblProgramInfo, request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "编辑项目群失败！");
		}
		return map;
	}

}
