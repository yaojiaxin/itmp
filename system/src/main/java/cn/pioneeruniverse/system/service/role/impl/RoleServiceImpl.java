package cn.pioneeruniverse.system.service.role.impl;

import java.sql.Timestamp;
import java.util.*;

import cn.pioneeruniverse.common.utils.*;
import cn.pioneeruniverse.system.controller.RoleController;
import cn.pioneeruniverse.system.dao.mybatis.defaultPage.UserDefaultPageDao;
import cn.pioneeruniverse.system.dao.mybatis.menu.MenuDao;
import cn.pioneeruniverse.system.dao.mybatis.user.UserDao;
import cn.pioneeruniverse.system.entity.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.system.dao.mybatis.role.RoleDao;
import cn.pioneeruniverse.system.service.role.IMenuRoleService;
import cn.pioneeruniverse.system.service.role.IRoleService;
import cn.pioneeruniverse.system.service.role.IUserRoleService;

import javax.servlet.http.HttpServletRequest;


@Service("iRoleService")
@Transactional(readOnly = true)
public class RoleServiceImpl extends ServiceImpl<RoleDao, TblRoleInfo> implements IRoleService{

	public static final Integer MENU_TYPE = 1;
	public static final Integer BUTTON_TYPE = 2;

	@Autowired
	private RoleDao roleDao;
	@Autowired
	private MenuDao menuDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private IMenuRoleService iMenuRoleService;
	@Autowired
	private IUserRoleService iUserRoleService;
	@Autowired
	private UserDefaultPageDao userDefaultPageDao;

	private Long roleId = null;


	@Override
	public TblRoleInfo findRoleById(Long id) {
		return roleDao.findRoleById(id);
	}

	@Override
	public List<TblRoleInfo> findUserRole(Long userId) {
		return roleDao.findUserRole(userId);
	}

	@Transactional(readOnly = true)
	@Override
	public List<TblRoleInfo> getUserAllRole(Long userId) {
		return roleDao.getUserAllRole(userId);
	}

	@Override
	public List<TblRoleInfo> getRoleWithMenu() {
		return roleDao.getRoleWithMenu();
	}

	@Override
	@Transactional(readOnly = false)
	public Boolean insertRole(TblRoleInfo role, HttpServletRequest request) {
		String empNo = PinYinUtil.getPingYin(role.getRoleName()).toUpperCase(Locale.ENGLISH);
		role = (TblRoleInfo) CommonUtil.setBaseValue(role,request);
		role.setRoleCode(empNo);
		TblRoleInfo roleInfo = roleDao.findRoleByName(role);
		if (roleInfo == null){
			roleDao.insertRole(role);
		} else {
			return false;
		}
		String menuIds = role.getMenuIds();
		if(StringUtils.isNotBlank(menuIds)){
			List<TblRoleMenuButton> rms = new ArrayList<TblRoleMenuButton>();
			String[] mids = menuIds.split(",");
			Long roleId = role.getId();
			for(String menuId:mids){
				TblRoleMenuButton roleMenu = new TblRoleMenuButton();
				roleMenu.setCreateBy(role.getCreateBy());
				roleMenu.setLastUpdateBy(role.getCreateBy());
				roleMenu.setMenuButtonId(Long.valueOf(menuId));
				roleMenu.setRoleId(roleId);
				roleMenu.setStatus(role.getStatus());
				rms.add(roleMenu);
			}
			iMenuRoleService.insertMenuRole(rms);
		}
		return true;
	}

	private String selectMaxEmpNo() {
		return roleDao.selectMaxEmpNo();
	}

	@Override
	@Transactional(readOnly = false)
	public void updateRole(TblRoleInfo role, HttpServletRequest request) {
		role.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		roleDao.updateRole(role);
	}

	@Override
	public List<TblRoleInfo> getRoleByMenuId(Long menuId) {
		return roleDao.getRoleByMenuId(menuId);
	}

	@Override
	public PageInfo<TblRoleInfo> getAllRole(TblRoleInfo bean, Integer pageIndex,Integer pageSize) {
		if(pageIndex != null && pageSize != null)
			PageHelper.startPage(pageIndex, pageSize);
		List<TblRoleInfo> list =  roleDao.getAllRole(bean);
		roleId = list.get(0).getId();
		PageInfo<TblRoleInfo> pageInfo = new PageInfo<TblRoleInfo>(list);
		return pageInfo;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteRoles(List<Long> roleIds,Long lastUpdateBy) {
		if(roleIds != null && !roleIds.isEmpty()){
			roleDao.delRole(roleIds,lastUpdateBy);
			iMenuRoleService.delMenuByRoleIds(roleIds,lastUpdateBy);
			iUserRoleService.delUserRoleByRoleId(roleIds,lastUpdateBy);
		}
	}

	/**
	 * 单个角色的菜单按钮权限
	 * @param id 角色id
	 * @return 菜单按钮列表
	 */
	@Override
	public List<TblRoleInfo> getRoleMenu(Long id) {
		Map<String,Object> map = new HashMap<>();
		Map<String,TblMenuButtonInfo> resultMap = new LinkedHashMap<String,TblMenuButtonInfo>();
		List<TblMenuButtonInfo> button_list = new ArrayList<>();
		TblRoleInfo roleInfo = new TblRoleInfo();
		if( id == null && roleId != null){
			id = roleId;
		}

		map.put("id",id);
		map.put("menuType",MENU_TYPE);
		List<TblMenuButtonInfo> menuList = menuDao.getMenusWithTYPE(map);
		resultMap = menuList(menuList,resultMap);

		List<TblRoleInfo> menu_list = new ArrayList<TblRoleInfo>();
		for(Map.Entry<String,TblMenuButtonInfo> entry:resultMap.entrySet()){
			roleInfo.setId(entry.getValue().getId());
			roleInfo.setLoaded(true);
			roleInfo.setExpanded(false);
			roleInfo.setMenu(entry.getValue().getMenuButtonName());
			roleInfo.setParent(entry.getValue().getParentId());

			String parentIds;
			if (entry.getValue().getParentIds() == null || entry.getValue().getParentIds().equals("")){
				parentIds =  entry.getValue().getId()+",";
				roleInfo.setLevel(0);
			} else {
				parentIds = entry.getValue().getParentIds()+entry.getValue().getId()+",";
				String[] parendIdsArr = entry.getValue().getParentIds().split(",");
				roleInfo.setLevel(parendIdsArr.length);
			}
			List<TblRoleInfo> childrenList = menuDao.findRoleMenu(parentIds);
			if (childrenList != null && childrenList.size() != 0){
				roleInfo.setLeaf(false);
			}else{
				roleInfo.setLeaf(true);
			}
			map.put("id",id);
			map.put("buttonId",entry.getValue().getId());
			String dd = entry.getValue().getId().toString();
			TblMenuButtonInfo  childrenMenu= menuDao.getButtonByRid(map);
			if (childrenMenu == null ){
				roleInfo.setIsSelect(false);
			} else {
				roleInfo.setIsSelect(true);
			}
			map.put("buttonType",BUTTON_TYPE);
			map.put("parentId",entry.getValue().getId());
			List<TblMenuButtonInfo> buttonList = menuDao.getButton(map);
			if (buttonList != null && !buttonList.isEmpty()) {
				for (TblMenuButtonInfo tb : buttonList) {
					// 查询是否有权限
					map.put("buttonId", tb.getId());
					TblMenuButtonInfo button = menuDao.getButtonByRid(map);
					if (button == null) {
						tb.setIsSelect(false);
					} else {
						tb.setIsSelect(true);
					}
					button_list.add(tb);
				}
				roleInfo.setButtonList(button_list);
				button_list = new ArrayList<>();
			}
			roleInfo.setLoaded(true);
			roleInfo.setExpanded(false);
			roleInfo.setId(entry.getValue().getId());
			roleInfo.setMenu(entry.getValue().getMenuButtonName());
			roleInfo.setParent(entry.getValue().getParentId());
			menu_list.add(roleInfo);
			roleInfo = new TblRoleInfo();

		}
		return menu_list;
	}

	public Map<String,TblMenuButtonInfo> menuList(List<TblMenuButtonInfo> menuList,Map<String,TblMenuButtonInfo> resultMap){
		Map<String,Object> map = new HashMap<>();
		map.put("menuType",MENU_TYPE);
		if(menuList != null && menuList.size() != 0){
			for (TblMenuButtonInfo tblMenuButtonInfo:menuList){
				if (tblMenuButtonInfo.getParentIds() != null){
					map.put("parentId",tblMenuButtonInfo.getParentIds()+tblMenuButtonInfo.getId()+",");
				} else {
					map.put("parentId",tblMenuButtonInfo.getId()+",");
				}
				resultMap.put("menu_"+tblMenuButtonInfo.getId(),tblMenuButtonInfo);
				List<TblMenuButtonInfo> childrenMenus = menuDao.getChildrenMenus(map);
				menuList(childrenMenus,resultMap);
			}
		}
		return resultMap;
	}



	/**
	 * 单个角色获取用户
	 * @param id 角色id
	 * @param startIndex
	 * @param pageSize
	 * @return 返回查询的关联的人员
	 */
	@Override
	public List<TblUserInfo> getRoleUser(Long id, Integer startIndex, Integer pageSize) {
		Map<String,Object> map = new HashMap<String,Object>();
		map = PageWithBootStrap.setPageNumberSize(map,startIndex,pageSize);
		map.put("roleId",id);
		return userDao.getUserByRoleId(map);
	}

	/**
	 * 单个角色添加关联人员
	 * @param userId 人员的id
	 * @param id 角色id
	 * @param request
	 */
	@Override
	@Transactional(readOnly = false)
	public void insertRoleUser(Long[] userId, Long id, HttpServletRequest request) {
		Map<String,Object> map = new HashMap<String,Object>();
		for(int i = 0,len = userId.length;i < len;i++){
			map.put("useId",userId[i]);
			map.put("roleId",id);
			map.put("createBy",CommonUtil.getCurrentUserId(request));
			map.put("lastUpdateBy",CommonUtil.getCurrentUserId(request));
			TblRoleInfo role = roleDao.getRoleUserById(map);
			if (role == null){
				map.put("status",1);
				roleDao.insertRoleUser(map);
			}else{
				roleDao.updateRoleUser(map);
				role = new TblRoleInfo();
			}
		}
	}


	/**
	 * 单个角色获取为关联的人员
	 * @param roleId 角色id
	 * @param userInfo 人员条件
	 * @param pageNumber
	 * @param pageSize
	 * @return 返回查询的人员列表
	 */
	@Override
	public List<TblUserInfo> findUserWithNoRole(Long roleId, TblUserInfo userInfo, Integer pageNumber, Integer pageSize) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("roleId",roleId);
		map.put("user",userInfo);
		map = PageWithBootStrap.setPageNumberSize(map,pageNumber,pageSize);
		List<TblUserInfo> list = userDao.findUserWithNoRole(map);
		return list;
	}

	/**
	 * 修改关联的人员
	 * @param userId 用户id
	 * @param roleId 角色id
	 * @param request
	 */
	@Override
	@Transactional(readOnly = false)
	public void updateRoleWithUser(Long[] userId, Long roleId, HttpServletRequest request) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("roleId",roleId);
		map.put("userId",userId);
		map.put("lastUpdateBy",CommonUtil.getCurrentUserId(request));
		roleDao.updateRoleWithUser(map);
	}

	/**
	 *  单个角色修改菜单按钮关联关系
	 * @param id 角色id
	 * @param menuIds 菜单按钮id
	 * @param request
	 * @return true：成功 false：失败
	 */
	private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class) ;
	@Override
	@Transactional(readOnly = false)
	public void updateRoleMenu(Long id, Long[] menuIds, HttpServletRequest request) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("roleId",id);
		map.put("menuId",menuIds);
		map.put("lastUpdateBy",CommonUtil.getCurrentUserId(request));
		//先删除，再添加
		roleDao.deleteRoleMenu(map);
		if (menuIds != null ){
			roleDao.insertRoleMenu(map);
		}
		//this.authRoleMenu();
	}

	/**
	 * 初始化URL鉴权Redis
	 */
	@Override
	public void authRoleMenu() {
		List<TblRoleInfo> roleList = roleDao.getRoleWithMenu();
		if(roleList != null){
			for (TblRoleInfo tblRoleInfo : roleList) {
				String roleId = Constants.ITMP_REDIS_ROLE_MENU_PREFIX + tblRoleInfo.getId();
				StringBuilder urlSB = new StringBuilder(",");
				if (tblRoleInfo.getMenuList() != null) {
					for (TblMenuButtonInfo tblMenuButtonInfo : tblRoleInfo.getMenuList()) {
						urlSB.append(tblMenuButtonInfo.getUrl()).append(",");
					}
				}
				redisUtils.set(roleId, urlSB.toString());
			}
		}
	}
	/**
	 * 获取系统管理员角色
	 */
	@Override
	public void getAdminRole() {
		redisUtils.set("adminRole", roleDao.getAdminRole());
	}

	/**
	 * 修改角色名称
	 * @param roleInfo
	 * @param request
	 */
	@Override
	@Transactional(readOnly = false)
	public void updateRoleName(TblRoleInfo roleInfo, HttpServletRequest request) {
		String empo = PinYinUtil.getPingYin(roleInfo.getRoleName()).toUpperCase(Locale.ENGLISH);
		roleInfo.setRoleCode(empo);
		roleInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
		roleInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
		roleDao.updateRole(roleInfo);
		if(roleInfo.getId().longValue() == 1){
			this.getAdminRole();
		}
	}

	@Override
	public List<TblMenuButtonInfo> getUserMenu(HttpServletRequest request) {
		// TODO Auto-generated method stub
		List<TblMenuButtonInfo> list = menuDao.getUserMenu2(CommonUtil.getCurrentUserId(request));
		List<Long> menuList = userDefaultPageDao.getDefaultPage(CommonUtil.getCurrentUserId(request));
		for (TblMenuButtonInfo tblMenuButtonInfo : list) {
			tblMenuButtonInfo.setLoaded(true);
			tblMenuButtonInfo.setExpanded(false);
			String parentIds = tblMenuButtonInfo.getParentIds();
			if(parentIds==null || parentIds.equals("")) {
				tblMenuButtonInfo.setLevel(1);
			}else {
				tblMenuButtonInfo.setLevel(parentIds.split(",").length+1);
			}
			List<TblMenuButtonInfo> sons = menuDao.selectSonMenu(tblMenuButtonInfo.getId());
			if(sons.size()!=0 && sons!=null) {
				tblMenuButtonInfo.setLeaf(false);
			}else {
				tblMenuButtonInfo.setLeaf(true);
			}
			if(menuList.contains(tblMenuButtonInfo.getId())) {
				tblMenuButtonInfo.setIsSelect(true);
			}else {
				tblMenuButtonInfo.setIsSelect(false);
			}
			
		}
		return list;
	}

	@Override
	@Transactional(readOnly = false)
	public void saveDefaultPage(List<TblUserDefaultPage> list, Long userId) {
		// TODO Auto-generated method stub
		userDefaultPageDao.updateDefaultPage(userId);
		for (TblUserDefaultPage tblUserDefaultPage : list) {
			tblUserDefaultPage.setUserId(userId);
			tblUserDefaultPage.setStatus(1);
			userDefaultPageDao.saveDefaultPage(tblUserDefaultPage);
		}
	}

	@Override
	public List<TblUserDefaultPage> getDefaultPage(Long userId) {
		// TODO Auto-generated method stub
		List<TblUserDefaultPage> list = userDefaultPageDao.getDefaultPage2(userId);
		return list;
	}

}
