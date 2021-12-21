package cn.pioneeruniverse.project.service.newproject;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.project.entity.TblProgramInfo;
import cn.pioneeruniverse.project.entity.TblProjectInfo;

public interface NewProjectService {

	List<TblProjectInfo> getAllNewProject(TblProjectInfo projectInfo, Long uid, List<String> roleCodes, Integer page,
			Integer rows);

	TblProjectInfo getNewProjectById(Long id);

	void insertNewProject(TblProjectInfo projectInfo, HttpServletRequest request);

	void insertProgram(TblProgramInfo programInfo, HttpServletRequest request);

	List<TblProjectInfo> getNewProjectByPage(Integer pageNumber, Integer pageSize);

	void updateNewProject(TblProjectInfo projectInfo, HttpServletRequest request);

	void updateSystemDirectory(String developSystemIds, HttpServletRequest request);

	void insertTmpNewProject(TblProjectInfo projectInfo, HttpServletRequest request);

	void updateTmpNewProject(TblProjectInfo projectInfo, HttpServletRequest request);

}
