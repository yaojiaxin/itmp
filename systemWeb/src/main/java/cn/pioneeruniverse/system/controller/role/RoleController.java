package cn.pioneeruniverse.system.controller.role;


import java.util.HashMap;
import java.util.Map;

import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.SpellUtil;
import cn.pioneeruniverse.system.feignInterface.role.RoleInterface;
import cn.pioneeruniverse.system.vo.role.TblRoleInfo;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("role")
public class RoleController extends BaseController {

	
	@Autowired
	private RoleInterface roleInterface;
	
	
	@RequestMapping("toRoleManage")
	public ModelAndView toRoleManage(HttpServletRequest request){
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.setViewName("systemManagement/roleManagement");
		return view;
	}

	@RequestMapping("toRoleAdd")
	public ModelAndView toRoleAdd(){
		ModelAndView view = new ModelAndView();
		view.setViewName("role/roleAdd");
		return view;
	}
	
	
	@RequestMapping("toRoleUpdate")
	public ModelAndView toRoleUpdate(Long roleId){
		ModelAndView view = new ModelAndView();
		view.setViewName("role/roleUpdate");
		view.addObject("roleId", roleId);
		return view;
	}
	
	@RequestMapping(value="getAllRole",method=RequestMethod.POST)
	public Map<String,Object> getAllRole(String roleJson, Integer pageIndex,Integer pageSize){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			result = roleInterface.getAllRole(roleJson, pageIndex,pageSize);
		}catch(Exception e){
			return this.handleException(e, "获取权限集失败");
		}
		
		return result;
	}
	
	
	@RequestMapping(value="insertRole",method=RequestMethod.POST)
	public Map<String,Object> insertRole(TblRoleInfo role){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			Long userId = 1L;//SecurityUserHolder.getCurrentUser().getId()
			role.setCreateBy(userId);
			role.setLastUpdateBy(userId);
			String roleName = role.getRoleName();
			String roleCode =  SpellUtil.getUpEname(roleName);
			role.setRoleCode(roleCode.toUpperCase());
			result = roleInterface.insertRole(role);
		}catch(Exception e){
			return this.handleException(e, "添加权限失败");
		}
		
		return result;
	}
	
	
	@RequestMapping(value="findRoleById",method=RequestMethod.POST)
	public Map<String,Object> findRoleById(Long roleId){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			result = roleInterface.findRoleById(roleId);
		}catch(Exception e){
			return this.handleException(e, "获取权限详情失败");
		}
		
		return result;
	}
	
	
	
	
	@RequestMapping(value="updateRole",method=RequestMethod.POST)
	public Map<String,Object> updateRole(TblRoleInfo role){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			Long userId = 1L;//SecurityUserHolder.getCurrentUser().getId()
			role.setLastUpdateBy(userId);
			result = roleInterface.updateRole(role);
		}catch(Exception e){
			return this.handleException(e, "更新权限失败");
		}
		
		return result;
	}
	
	
	@RequestMapping(value="delRole",method=RequestMethod.POST)
	public Map<String,Object> delRole(String roleIds){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			Long lastUpdateBy = 1L;//SecurityUserHolder.getCurrentUser().getId()
			roleInterface.delRole(roleIds,lastUpdateBy);
		}catch(Exception e){
			return this.handleException(e, "删除权限失败");
		}
		return result;
	}

	/*@RequestMapping(value = "getRoleMenu",method = RequestMethod.POST)
	public Map<String,Object> getRoleMenu(Long id){
		Map<String,Object> result = new HashMap<String,Object>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try{
			result = roleInterface.getRoleMenu(id);
		}catch(Exception e){
			return this.handleException(e, "获取菜单按钮权限失败");
		}
		return result;
	}*/

}

