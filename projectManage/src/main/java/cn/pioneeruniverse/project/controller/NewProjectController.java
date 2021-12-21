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
import cn.pioneeruniverse.project.entity.TblProjectInfo;
import cn.pioneeruniverse.project.service.newproject.NewProjectService;

@RestController
@RequestMapping("newProject")
public class NewProjectController extends BaseController {

	@Autowired
	private RedisUtils redisUtils;
	
	@Autowired
	private NewProjectService newProjectService;
	
	/**
	 * 新建项目列表查询
	 * @param projectInfo
	 * @param request
	 * @return
	 */
	@RequestMapping(value="getAllNewProject",method=RequestMethod.POST)
	public Map<String,Object> getAllNewProject(TblProjectInfo projectInfo, Integer page,Integer rows, HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Long uid  = CommonUtil.getCurrentUserId(request);
			LinkedHashMap codeMap = (LinkedHashMap) redisUtils.get(CommonUtil.getToken(request));
			List<String> roleCodes = (List<String>) codeMap.get("roles");
			List<TblProjectInfo> list = newProjectService.getAllNewProject(projectInfo, uid, roleCodes, page, rows);
			List<TblProjectInfo> list2 = newProjectService.getAllNewProject(projectInfo, uid, roleCodes, 1, Integer.MAX_VALUE);
			double total = Math.ceil(list2.size()*1.0/rows);
			map.put("records", list2.size());    //查询的总条目数
			map.put("page", page);					//当前页
			map.put("total", total);             //总页数
			map.put("data", list);				//每页数据
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "新建项目列表查询失败！");
		}
		return map;
	}
	
	
	/**
	 * 详情/数据回显
	 * @param id
	 * @return
	 */
	@RequestMapping(value="getNewProjectById",method=RequestMethod.POST)
	public Map<String,Object> getNewProjectById(Long id){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			TblProjectInfo projectInfo = newProjectService.getNewProjectById(id);
			map.put("data", projectInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "获取项目详情失败！");
		}
		return map;
	}
	
	/**
	 * 新增新建项目
	 * @param project
	 * @param request
	 * @return
	 */
	@RequestMapping(value="insertNewProject",method=RequestMethod.POST)
	public Map<String,Object> insertNewProject(@RequestBody String tblProjectInfo, HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			TblProjectInfo projectInfo = JSONObject.parseObject(tblProjectInfo, TblProjectInfo.class);
			newProjectService.insertNewProject(projectInfo, request);
			if(!projectInfo.getDevelopSystemIds().equals("")) {
				updateSystemDirectory(projectInfo.getDevelopSystemIds(), request);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "新增新建项目失败！");
		}
		return map;
	}
	
	/**
	 * 新增或编辑新建项目时根据开发系统更新系统目录
	 * @param developSystemIds
	 */
	@RequestMapping(value="updateSystemDirectory",method=RequestMethod.POST)
	public void updateSystemDirectory(String developSystemIds, HttpServletRequest request) {
		newProjectService.updateSystemDirectory(developSystemIds, request);
	}


	/**
	 * 新增项目群
	 * @param programInfo
	 * @param request
	 * @return
	 */
	@RequestMapping(value="insertProgram",method=RequestMethod.POST)
	public Map<String,Object> insertProgram(@RequestBody String tblProgramInfo, HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			TblProgramInfo programInfo = JSONObject.parseObject(tblProgramInfo, TblProgramInfo.class);
			newProjectService.insertProgram(programInfo, request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "新增项目群失败！");
		}
		return map;
	}

	
	/**
	 * 新建项目群关联新建项目弹窗
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value="getNewProjectByPage",method=RequestMethod.POST)
	public Map<String,Object> getNewProjectByPage(Integer pageNumber,Integer pageSize){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblProjectInfo> list = newProjectService.getNewProjectByPage(pageNumber, pageSize);
			List<TblProjectInfo> list2 = newProjectService.getNewProjectByPage(1, Integer.MAX_VALUE);
			map.put("total", list2.size());
			map.put("rows", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "获取新建项目失败！");
		}
		return map;
	}
	
	
	/**
	 * 编辑新建项目
	 * @param tblProjectInfo
	 * @param request
	 * @return
	 */
	@RequestMapping(value="updateNewProject",method=RequestMethod.POST)
	public Map<String,Object> updateNewProject(@RequestBody String tblProjectInfo, HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			TblProjectInfo projectInfo = JSONObject.parseObject(tblProjectInfo, TblProjectInfo.class);
			newProjectService.updateNewProject(projectInfo, request);
			if(!projectInfo.getDevelopSystemIds().equals("")) {
				updateSystemDirectory(projectInfo.getDevelopSystemIds(), request);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return super.handleException(e, "编辑新建项目失败！");
		}
		return map;
	}
	
	
}
