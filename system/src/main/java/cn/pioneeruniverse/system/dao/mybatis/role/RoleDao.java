package cn.pioneeruniverse.system.dao.mybatis.role;

import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.system.entity.TblUserInfo;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.system.entity.TblRoleInfo;

public interface RoleDao extends BaseMapper<TblRoleInfo> {
	
	List<TblRoleInfo> getAllRole(TblRoleInfo bean);
	
	TblRoleInfo findRoleById(Long id);
	
	List<TblRoleInfo> findUserRole(Long userId);
	
	List<TblRoleInfo> getRoleWithMenu();
	
	void insertRole(TblRoleInfo role);
	
	void updateRole(TblRoleInfo role);
	
	List<TblRoleInfo> getRoleByMenuId(Long menuId);
	
	List<TblRoleInfo> getRoleByUserId(Long userId);
	List<TblRoleInfo> getRoleByUserId1(Long userId);
	
	Long selectRoleCodeLen(String roleCode);
	
	void delRole(@Param("ids")List<Long> ids,@Param("lastUpdateBy")Long lastUpdateBy);

	int insertRoleUser(Map<String,Object> map);

    void updateRoleWithUser(Map<String,Object> map);

	TblRoleInfo getRoleUserById(Map<String,Object> map);

	void updateRoleUser(Map<String,Object> map);

    TblRoleInfo findRoleByName(TblRoleInfo role);

	void deleteRoleMenu(Map<String,Object> map);

	void insertRoleMenu(Map<String,Object> map);

	String selectMaxEmpNo();

	List<TblRoleInfo> getUserAllRole(Long userId);

    TblRoleInfo getAdminRole();

	List<TblRoleInfo> getRoles();
}
