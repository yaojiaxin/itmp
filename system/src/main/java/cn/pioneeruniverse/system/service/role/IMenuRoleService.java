package cn.pioneeruniverse.system.service.role;

import java.util.List;

import cn.pioneeruniverse.system.entity.TblRoleMenuButton;

public interface IMenuRoleService {
	
	void insertMenuRole(List<TblRoleMenuButton> list);
	
	void delMenuRole(List<Long> list);
	
	void delMenuByRoleId(Long roleId);
	
	void delMenuByRoleIds(List<Long> list,Long lastUpdateBy);
}
