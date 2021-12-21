package cn.pioneeruniverse.project.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.project.dto.ProjectHomeDTO;
import cn.pioneeruniverse.project.service.projectHome.ProjectHomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.project.entity.TblProjectGroup;
import cn.pioneeruniverse.project.entity.TblProjectInfo;
import cn.pioneeruniverse.project.entity.TblSystemInfo;
import cn.pioneeruniverse.project.entity.TblUserInfo;
import cn.pioneeruniverse.project.entity.User;
import cn.pioneeruniverse.project.service.oamproject.OamProjectService;

@RestController
@RequestMapping("oamproject")
public class OamProjectInfoController extends BaseController{
	
	@Autowired
	private OamProjectService oamProjectService;

	@Autowired
	private RedisUtils redisUtils;


	@Autowired
	private ProjectHomeService projectHomeService;

	/**
	 * 运维项目管理的搜索框的状态下拉框
	 * @param termCode
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="selectStatusName",method=RequestMethod.POST)
	public Map<String, Object> selectStatusName(@RequestParam("termCode")String termCode) {
		HashMap<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			ArrayList<String> list = new ArrayList<>();
			String redisStr = redisUtils.get(termCode).toString();
			JSONObject jsonObj = JSON.parseObject(redisStr);
//			for (String key : jsonObj.keySet()) {
//				String ValueName = jsonObj.get(key).toString();
//				list.add(ValueName);
//			}
			String value1 = jsonObj.get("2").toString();
			String value2 = jsonObj.get("4").toString();
			list.add(value1);
			list.add(value2);
			map.put("data", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 运维项目管理的分页和条件查询
	 * @param
	 * @param page
	 * @param rows
	 * @param
	 * @return
	 */
	@RequestMapping(value="selectOamProject",method=RequestMethod.POST)
	public Map<String, Object> selectOamProject(String projectName,String projectStatusName,
			String projectManageName,String developManageName,
			Integer page,Integer rows,HttpServletRequest request){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Long uid  = CommonUtil.getCurrentUserId(request);
			LinkedHashMap codeMap = (LinkedHashMap) redisUtils.get(CommonUtil.getToken(request));
			List<String> roleCodes = (List<String>) codeMap.get("roles");
			List<TblProjectInfo> list = oamProjectService.selectOamProject(projectName,projectStatusName,projectManageName,developManageName,uid,roleCodes,page,rows);
			List<TblProjectInfo> list2 = oamProjectService.selectOamProject(projectName,projectStatusName,projectManageName,developManageName,uid,roleCodes,1,Integer.MAX_VALUE);
			double total = Math.ceil(list2.size()*1.0/rows);
			map.put("records", list2.size());    //查询的总条目数
			map.put("page", page);					//当前页
			map.put("total", total);             //总页数
			map.put("data", list);				//每页数据
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		
		return map;
		
	}
	

	/**
	 * 新增项目和编辑项目的关联系统页面的分页显示(查询的是未绑定和未选中的系统)
	 * @param tblSystemInfo
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="selectSystemInfo",method=RequestMethod.POST)
	public Map<String, Object> selectSystemInfo(TblSystemInfo tblSystemInfo,
			@RequestParam(value="pageNumber",defaultValue = "1", required = true)Integer pageNumber,                                  
			@RequestParam(value="pageSize",defaultValue = "10",required = true)Integer pageSize,
			@RequestParam(value="systemIds[]") Long[] systemIds
			){
			HashMap<String, Object> map = new HashMap<>();
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblSystemInfo> list = oamProjectService.selectSystemInfo(tblSystemInfo,pageNumber,pageSize,systemIds);
			List<TblSystemInfo> list2 = oamProjectService.selectSystemInfo(tblSystemInfo,1,Integer.MAX_VALUE,systemIds);
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
	 * 新增运维项目
	 * @param tblProjectInfo
	 */
	@RequestMapping(value="insertOamProject",method=RequestMethod.POST)
	public Map<String,Object> insertOamProject(@RequestBody String tblProjectInfo,HttpServletRequest request) {
		Map<String,Object> map = new HashMap<>();
		try {
			Gson gson = new Gson();
			TblProjectInfo projectInfo = gson.fromJson(tblProjectInfo, TblProjectInfo.class);
			oamProjectService.insertOamProject(projectInfo,request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}
	
	/**
	 * 结束项目
	 * @param id
	 * @return
	 */
	@RequestMapping(value="endProject",method=RequestMethod.POST)
	public Map<String, Object> endProject(Long id, HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			oamProjectService.endProject(id,request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}
	
	/**
	 * 根据项目id查询项目详情and项目组成员（项目详情页面和编辑页面的数据回显使用同一个请求）
	 * @param id
	 * @return
	 */
	@RequestMapping(value="selectProjectAndUserById",method=RequestMethod.POST)
	public Map<String, Object> selectProjectAndUserById(Long id,Integer homeType){
		HashMap<String,Object> map = new HashMap<>();
		if (homeType == 1){  //项目主页获者项目群主页过来
//			//基本信息
//			ProjectHomeDTO projectHome = projectHomeService.selectSystemInfoByID(homeId);
//			//项目关联系统
//			List<ProjectHomeDTO> interactedSystem = projectHomeService.projectSystemList(homeId);
//			logger.info("interactedSystem:"+interactedSystem);
//			map.put("projectHome",projectHome);
//			map.put("interactedSystem",interactedSystem);
			try {
				//查询项目详情及人员详情
				TblProjectInfo tblProjectInfo = oamProjectService.selectProject(id);
				map.put("data", tblProjectInfo);
				map.put("type", 1);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("mes:" + e.getMessage(), e);
			}
			logger.info("团队成员:"+map);
			return  map;
		}else{
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
			try {
				//查询项目详情及人员详情
				TblProjectInfo tblProjectInfo = oamProjectService.selectProject(id);
				map.put("data", tblProjectInfo);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("mes:" + e.getMessage(), e);
			}
			return map;
		}
	}
	
	/**
	 * 查询所有岗位(显示在项目成员岗位)
	 * @return
	 */
	@RequestMapping(value="selectUserPost",method=RequestMethod.POST)
	public Map<String, Object> selectUserPost(){
		HashMap<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			String redisStr = redisUtils.get("TBL_PROJECT_GROUP_USER_USER_POST").toString();
			JSONObject jsonObject = JSON.parseObject(redisStr);
			
//			ArrayList<String> list = new ArrayList<>();
//			for (String key : jsonObject.keySet()) {
//				String post = jsonObject.get(key).toString();
//				list.add(post);
//			}
			map.put("data", jsonObject);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 编辑页面的关联人员的弹窗
	 * @param userName
	 * @param employeeNumber
	 * @param
	 * @param
	 * @return
	 */
	@RequestMapping(value="selectUser",method=RequestMethod.POST)
	public Map<String, Object> selectUser(String userName,String employeeNumber,String deptName,String companyName,Integer pageNumber,Integer pageSize){
		HashMap<String, Object> map = new HashMap<>();
		map.put("statsu", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblUserInfo> list = oamProjectService.selectUser(userName,employeeNumber,deptName,companyName,pageNumber,pageSize);
			List<TblUserInfo> list2 = oamProjectService.selectUser(userName,employeeNumber,deptName,companyName,1,Integer.MAX_VALUE);
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
	 * 搜索部门名称显示在关联人员的搜索下拉框
	 * @return
	 */
	@RequestMapping(value="selectDeptName",method=RequestMethod.POST)
	public Map<String, Object> selectDeptName(){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<String> list = oamProjectService.selectDeptName();
			map.put("data", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 查询公司名称显示在关联人员搜索的下拉框
	 * @return
	 */
	@RequestMapping(value="selectCompanyName",method=RequestMethod.POST)
	public Map<String, Object> selectCompanyName(){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<String> list = oamProjectService.selectCompanyName();
			map.put("data", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 编辑项目
	 * @param tblProjectInfo
	 * @return
	 */
	@RequestMapping(value="editProject",method=RequestMethod.POST)
	public Map<String, Object> editProject(@RequestBody String tblProjectInfo,HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			Gson gson = new Gson();
			TblProjectInfo projectInfo = gson.fromJson(tblProjectInfo, TblProjectInfo.class);
			oamProjectService.editProject(projectInfo,request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}
	
	/**
	 * 维护项目组织的项目小组数据显示(根据项目id查询该项目对应的项目小组《上下级》)
	 * @param id
	 * @return
	 */
	@RequestMapping(value="selectProjectGroup",method=RequestMethod.POST)
	public Map<String, Object> selectProjectGroup(Long id){
		HashMap<String,Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblProjectGroup> list = oamProjectService.selectProjectGroup(id);
			int size = list.size();
			map.put("records", size);
			map.put("data", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 根据人员弹窗选中的人员的id，去查询封装显示在编辑页面
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="selectUsers",method=RequestMethod.POST)
	public Map<String, Object> selectUsers(Long[] ids){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<User> list = oamProjectService.selectUsers(ids);
			map.put("data", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return map;
	}
	
	/**
	 * 维护项目组织的删除项目组
	 * @return
	 */
	@RequestMapping(value="deletePeojectGroup",method=RequestMethod.POST)
	public Map<String, Object> deletePeojectGroup(Long projectGroupId,HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			oamProjectService.deletePeojectGroup(projectGroupId,request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}
	
	/**
	 * 维护项目组织的编辑项目组
	 * @param tblProjectGroup
	 * @param request
	 * @return
	 */
	@RequestMapping(value="editProjectGroup",method=RequestMethod.POST)
	public Map<String, Object> editProjectGroup(TblProjectGroup tblProjectGroup, HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			oamProjectService.editProjectGroup(tblProjectGroup,request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}
	
	/**
	 * 维护项目组织的新增项目组
	 * @param tblProjectGroup
	 * @param request
	 * @return
	 */
	@RequestMapping(value="saveProjectGroup",method=RequestMethod.POST)
	public Map<String, Object> saveProjectGroup(TblProjectGroup tblProjectGroup, HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			oamProjectService.saveProjectGroup(tblProjectGroup, request);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}
	
	
	//接口
	@RequestMapping(value="selectOamProject2",method=RequestMethod.POST)
	public Map<String, Object> selectOamProject2(String projectName,String projectStatusName,
			String projectManageName,String developManageName,
			Integer page,Integer rows){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblProjectInfo> list = oamProjectService.selectOamProject(projectName,projectStatusName,projectManageName,developManageName,null,null,page,rows);
			List<TblProjectInfo> list2 = oamProjectService.selectOamProject(projectName,projectStatusName,projectManageName,developManageName,null,null,1,Integer.MAX_VALUE);
			map.put("total", list2.size());    //查询的总条目数
			map.put("rows", list);				//每页数据
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		
		return map;
		
	}
	
}
