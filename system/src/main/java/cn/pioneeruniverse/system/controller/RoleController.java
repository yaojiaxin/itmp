package cn.pioneeruniverse.system.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import cn.pioneeruniverse.system.entity.TblUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.system.entity.TblMenuButtonInfo;
import cn.pioneeruniverse.system.entity.TblRoleInfo;
import cn.pioneeruniverse.system.entity.TblUserDefaultPage;
import cn.pioneeruniverse.system.service.role.IRoleService;

/**
 * Description: role controller
 * Author:liushan
 * Date: 2018/10/29 下午 5:08
 */
@RestController
@RequestMapping("role")
public class RoleController {

	private static final Logger logger = LoggerFactory.getLogger(RoleController.class) ;
	
	@Autowired
	private IRoleService iRoleService;

	@RequestMapping(value = "findRolesByUserID",method = RequestMethod.POST)
	public Map<String,Object> findRolesByUserID(Long userId) {
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		if ( userId == null){
			return this.handleException(new Exception(), "操作失败，参数为空");
		} else {
			try{
				List<TblRoleInfo> roleInfos = iRoleService.getUserAllRole(userId);
				result.put("roles",roleInfos);
			}catch(Exception e){
				return this.handleException(e, "操作失败");
			}
		}
		return result;
	}


	@RequestMapping(value = "updateRoleName",method = RequestMethod.POST)
	public Map<String,Object> updateRoleName(TblRoleInfo roleInfo,HttpServletRequest request) {

		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		if ( roleInfo == null){
			return this.handleException(new Exception(), "修改角色名称失败，参数为空");
		} else {
			try{
				iRoleService.updateRoleName(roleInfo,request);
			}catch(Exception e){
				return this.handleException(e, "修改角色名称失败");
			}
		}

		return result;
	}

	@RequestMapping(value = "updateRoleMenu",method = RequestMethod.POST)
	public Map<String,Object> updateRoleMenu(Long id,Long[] menuIds,HttpServletRequest request){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		if ( id == null){
			return this.handleException(new Exception(), "修改角色菜单按钮失败，参数为空");
		} else {
			try{
				iRoleService.updateRoleMenu(id,menuIds,request);
			}catch(Exception e){
				return this.handleException(e, "修改角色菜单按钮失败");
			}
		}

		return result;
	}

	@RequestMapping(value = "getRoleMenu",method = RequestMethod.POST)
	public Map<String,Object> getRoleMenu(Long id){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			List<TblRoleInfo> list = iRoleService.getRoleMenu(id);
			result.put("result",list);
		}catch(Exception e){
			return this.handleException(e, "获取菜单按钮失败");
		}

		return result;
	}
	
	//查询当前登录用户有权限的菜单
	@RequestMapping(value = "getUserMenu",method = RequestMethod.POST)
	public Map<String,Object> getUserMenu(HttpServletRequest request){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			List<TblMenuButtonInfo> list = iRoleService.getUserMenu(request);
			result.put("result",list);
		}catch(Exception e){
			return this.handleException(e, "获取权限菜单失败");
		}
		
		return result;
	}

	@RequestMapping(value = "getRoleUser",method = RequestMethod.POST)
	public Map<String,Object>  getRoleUser(Integer pageNumber,Integer pageSize,Long id){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		if (id == null ){
			return this.handleException(new Exception(), "获取关联人员失败，参数为空");
		} else {
			try{
				List<TblUserInfo> list = iRoleService.getRoleUser(id,pageNumber,pageSize);
				List<TblUserInfo> list1 = iRoleService.getRoleUser(id,null,null);
				result.put("rows",list);
				result.put("total",list1.size());
			}catch(Exception e){
				return this.handleException(e, "获取关联人员失败");
			}
		}

		return result;
	}

	@RequestMapping(value = "findUserWithNoRole",method = RequestMethod.POST)
	public Map<String,Object> findUserWithNoRole(Integer pageNumber,Integer pageSize,Long roleId,TblUserInfo userInfo){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		if (roleId == null ){
			return this.handleException(new Exception(), "查询未关联人员失败，参数为空");
		} else {
			try{
				List<TblUserInfo> list = iRoleService.findUserWithNoRole(roleId,userInfo,pageNumber,pageSize);
				List<TblUserInfo> list1 = iRoleService.findUserWithNoRole(roleId,userInfo,null,null);
				result.put("rows",list);
				result.put("total",list1.size());
			}catch(Exception e){
				return this.handleException(e, "查询未关联人员失败");
			}
		}

		return result;
	}

	@RequestMapping(value = "insertRoleUser",method = RequestMethod.POST)
	public Map<String,Object> insertRoleUser(Long[] userId,Long id,HttpServletRequest request){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		if ( id == null ){
			return this.handleException(new Exception(), "添加角色关联人员失败，参数为空");
		} else {
			try{
				iRoleService.insertRoleUser(userId,id,request);
			}catch(Exception e){
				return this.handleException(e, "添加角色关联人员失败");
			}
		}


		return result;
	}

	@RequestMapping(value = "updateRoleWithUser",method = RequestMethod.POST)
	public Map<String,Object> updateRoleWithUser(Long[] userId,Long roleId,HttpServletRequest request){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		if ( roleId == null){
			return this.handleException(new Exception(), "修改角色关联人员失败，参数为空");
		} else {
			try{
				iRoleService.updateRoleWithUser(userId,roleId,request);
			}catch(Exception e){
				return this.handleException(e, "修改角色关联人员失败");
			}
		}

		return result;
	}

	@RequestMapping(value="getAllRole",method=RequestMethod.POST)
	public Map<String,Object> getAllRole(String roleJson, Integer pageIndex,Integer pageSize){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			TblRoleInfo bean = new TblRoleInfo();
			if (StringUtils.isNotBlank(roleJson)) {
				bean = JSONObject.parseObject(roleJson, TblRoleInfo.class);
			}
			PageInfo<TblRoleInfo> roles = iRoleService.getAllRole(bean, pageIndex,pageSize);
			if(pageIndex != null && pageSize != null)
				result.put("data", JSONObject.toJSONString(roles));
			else
				result.put("data", JSONObject.toJSONString(roles.getList()));
		}catch(Exception e){
			return this.handleException(e, "获取权限集失败");
		}
		
		return result;
	}
	
	
	
	@RequestMapping(value="findRoleById",method=RequestMethod.POST)
	public Map<String,Object> findRoleById(Long roleId){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		if ( roleId == null){
			return this.handleException(new Exception(), "获取权限集失败，参数为空");
		} else {
			try{
				TblRoleInfo role = iRoleService.findRoleById(roleId);
				result.put("data", JSONObject.toJSONString(role));
			}catch(Exception e){
				return this.handleException(e, "获取权限集失败");
			}
		}

		
		return result;
	}
	
	
	@RequestMapping(value="updateRole",method=RequestMethod.POST)
	public Map<String,Object> updateRole(@RequestBody TblRoleInfo role,HttpServletRequest request){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			iRoleService.updateRole(role,request);
		}catch(Exception e){
			return this.handleException(e, "添加权限失败");
		}
		
		return result;
	}
	
	
	@RequestMapping(value="insertRole",method=RequestMethod.POST)
	public Map<String,Object> insertRole(@RequestBody TblRoleInfo role,HttpServletRequest request){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		if (role == null ){
			return this.handleException(new Exception(), "添加角色，参数为空");
		} else {
			try{
				boolean flag = iRoleService.insertRole(role,request);
				result.put("result",flag);
			}catch(Exception e){
				return this.handleException(e, "添加权限失败");
			}
		}

		return result;
	}
	
	
	@RequestMapping(value="delRole",method=RequestMethod.POST)
	public Map<String,Object> delRole(String roleIds,Long lastUpdateBy){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			List<Long> ids = new ArrayList<Long>();
			if(StringUtils.isNotBlank(roleIds)){
				String[] roles = roleIds.split(",");
				for(String roleId:roles){
					ids.add(Long.valueOf(roleId));
				}
			}
			iRoleService.deleteRoles(ids,lastUpdateBy);
		}catch(Exception e){
			return this.handleException(e, "删除权限失败");
		}

		return result;
	}

	public  Map<String,Object> handleException(Exception e,String message){
		e.printStackTrace();
		logger.error(message+":"+e.getMessage(), e);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("status", Constants.ITMP_RETURN_FAILURE);
		map.put("errorMessage", message);
		return map;
	}
	
	//保存默认页面
	@RequestMapping(value="saveDefaultPage",method=RequestMethod.POST)
	public Map<String,Object> saveDefaultPage(HttpServletRequest request, String pages){
		Map<String,Object> result = new HashMap<>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblUserDefaultPage> list = JSONArray.parseArray(pages, TblUserDefaultPage.class);
			Long userId = CommonUtil.getCurrentUserId(request);
			iRoleService.saveDefaultPage(list, userId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return this.handleException(e, "保存默认页面失败");
		}
		return result;
	}
	
	@RequestMapping(value="getDefaultPage",method=RequestMethod.POST)
	public Map<String, Object> getDefaultPage(HttpServletRequest request){
		Map<String,Object> result = new HashMap<>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Long userId = CommonUtil.getCurrentUserId(request);
			List<TblUserDefaultPage> list = iRoleService.getDefaultPage(userId);
			result.put("data", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return this.handleException(e, "查询默认页面失败");
		}
		return result;
	}
}
