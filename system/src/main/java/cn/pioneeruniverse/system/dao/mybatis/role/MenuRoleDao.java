package cn.pioneeruniverse.system.dao.mybatis.role;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.system.entity.TblRoleMenuButton;

public interface MenuRoleDao extends BaseMapper<TblRoleMenuButton> {

	void insertMenuRole(List<TblRoleMenuButton> list);
	
	void delMenuRole(List<Long> list);
	
	String selectMenuIdsByRoleId(Long roleId);
	
	void delMenuByRoleId(Long roleId);
	
	void delMenuByRoleIds(@Param("list")List<Long> list,@Param("lastUpdateBy")Long lastUpdateBy);
}
