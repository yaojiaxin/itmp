package cn.pioneeruniverse.project.service.oamproject;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.project.entity.TblProjectGroup;
import cn.pioneeruniverse.project.entity.TblProjectInfo;
import cn.pioneeruniverse.project.entity.TblSystemInfo;
import cn.pioneeruniverse.project.entity.TblUserInfo;
import cn.pioneeruniverse.project.entity.User;

public interface OamProjectService {

	List<TblProjectInfo> selectOamProject(String projectName, String projectStatusName, String projectManageName,
			String developManageName, Long uid, List<String> roleCodes, Integer page, Integer rows) throws Exception;


	TblProjectInfo selectProject(Long id) throws Exception;


	void insertOamProject(TblProjectInfo projectInfo,HttpServletRequest request) throws Exception;


	List<TblUserInfo> selectUser(String userName, String employeeNumber, String deptName, String companyName,
			Integer pageNumber, Integer pageSize) throws Exception;


	List<String> selectDeptName() throws Exception;


	List<String> selectCompanyName() throws Exception;


	void editProject(TblProjectInfo tblProjectInfo, HttpServletRequest request) throws Exception;


	List<TblProjectGroup> selectProjectGroup(Long id) throws Exception;


	List<User> selectUsers(Long[] ids) throws Exception;


	void endProject(Long id, HttpServletRequest request) throws Exception;


	List<TblSystemInfo> selectSystemInfo(TblSystemInfo tblSystemInfo, Integer pageNumber, Integer pageSize, Long[] systemIds) throws Exception;


	void deletePeojectGroup(Long projectGroupId, HttpServletRequest request) throws Exception;


	void editProjectGroup(TblProjectGroup tblProjectGroup, HttpServletRequest request) throws Exception;


	void saveProjectGroup(TblProjectGroup tblProjectGroup, HttpServletRequest request) throws Exception;


	void insertTmpProject(TblProjectInfo projectInfo, HttpServletRequest request, Long id) throws Exception;


	void editTmpProject(TblProjectInfo projectInfo, HttpServletRequest request) throws Exception;


	void endTmpProject(Long id, HttpServletRequest request) throws Exception;


	void deleteTmpPeojectGroup(Long projectGroupId, HttpServletRequest request) throws Exception;


	void editTmpProjectGroup(TblProjectGroup tblProjectGroup, HttpServletRequest request) throws Exception;


	void saveTmpProjectGroup(TblProjectGroup tblProjectGroup, HttpServletRequest request, Long id) throws Exception;




}
