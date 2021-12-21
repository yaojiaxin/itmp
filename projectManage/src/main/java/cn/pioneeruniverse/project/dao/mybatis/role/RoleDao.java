package cn.pioneeruniverse.project.dao.mybatis.role;

import cn.pioneeruniverse.project.entity.TblRoleInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface RoleDao {

	List<TblRoleInfo> selectUserPostRole(@Param(value = "projectId") Long projectId,
                                         @Param(value = "userPost") Integer userPost);

	void insertUserPostRole(TblRoleInfo roleInfo);

	List<TblRoleInfo> selectNoProjectRole();

	void deleteProjectUserRole(Long [] roleIds);

	Integer selectUserRole(@Param(value = "userId")Long userId,
						   @Param(value = "roleId")Long roleId);

	void insertRoleUser(Map<String,Object> map);
}
