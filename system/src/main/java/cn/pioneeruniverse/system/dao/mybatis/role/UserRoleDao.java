package cn.pioneeruniverse.system.dao.mybatis.role;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.system.entity.TblUserRole;

public interface UserRoleDao extends BaseMapper<TblUserRole> {

	void insertUserRole(List<TblUserRole> list);
	
	void delUserRole(List<Long> list);
	
	void delUserRoleByRoleId(@Param("list")List<Long> list,@Param("lastUpdateBy")Long lastUpdateBy);
	
	void delUserRoleByUserIds(@Param("list")List<Long> list,@Param("lastUpdateBy")Long lastUpdateBy);
	
	void delUserRoleByUserId(Long userId);

	List<Long> findRoleIdByUserId(Long id);
}
