package cn.pioneeruniverse.project.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.project.entity.TblProjectInfo;

public interface NewProjectMapper {

	List<TblProjectInfo> getAllNewProject(Map<String, Object> map);

	List<TblProjectInfo> getNewProjectByUid(Map<String, Object> map);

	TblProjectInfo getNewProjectById(Long id);

	Long getCountNewProject();

	void insertNewProject(TblProjectInfo projectInfo);

	List<TblProjectInfo> getNewProjectByPage(HashMap<String, Object> map);

	void updateNewProject(TblProjectInfo projectInfo);

	String getProjectCodeById(Long id);

}
