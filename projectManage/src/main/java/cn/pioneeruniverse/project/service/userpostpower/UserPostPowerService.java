package cn.pioneeruniverse.project.service.userpostpower;

import cn.pioneeruniverse.project.entity.TblRoleInfo;

import java.util.List;

public interface UserPostPowerService {

	List<TblRoleInfo> getUserPostRole(Long projectId, Integer userPost);

	void insertUserPostRole(TblRoleInfo roleInfo);

	List<TblRoleInfo> getRoleMenu(Long id);

	void setRoleId(Long id);

	List<TblRoleInfo> getNoProjectRole();
}
