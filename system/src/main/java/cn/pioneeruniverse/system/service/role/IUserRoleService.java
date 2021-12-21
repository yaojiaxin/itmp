package cn.pioneeruniverse.system.service.role;

import java.util.List;

import cn.pioneeruniverse.system.entity.TblUserRole;

public interface IUserRoleService {

	void insertUserRole(List<TblUserRole> list);
	
	void delUserRole(List<Long> list);
	
	void delUserRoleByRoleId(List<Long> list,Long lastUpdateBy);
    void delUserRoleByUserIds(List<Long> list,Long lastUpdateBy);
	
	void delUserRoleByUserId(Long userId);
}
