package cn.pioneeruniverse.project.dao.mybatis;

import java.util.HashMap;
import java.util.List;

import cn.pioneeruniverse.project.entity.TblProjectGroupUser;

public interface ProjectGroupUserMapper {

	List<Long> findProjectGroupIdsByUserId(Long userId);

	List<Long> findProjectGroupIdsByUserIdAndUserPost(HashMap<String, Object> map);

	List<Long> findUserId(HashMap<String, Object> map);

	List<TblProjectGroupUser> selectProjectGroupUserByProjectGroupId(Long projectGroupId);

	void updateProjectGroupUser(HashMap<String, Object> map3);

	List<Long> selectIdByProjectGroupId(Long projectGroupId);

	void insertProjectGroupUser(TblProjectGroupUser tblProjectGroupUser);

	void delProjectGroupUser(List<Long> ls);

	void deleteProjectGroupUser(HashMap<String, Object> map);
	

}
