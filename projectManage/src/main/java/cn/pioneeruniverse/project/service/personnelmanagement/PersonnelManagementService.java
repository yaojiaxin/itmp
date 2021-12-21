package cn.pioneeruniverse.project.service.personnelmanagement;

import cn.pioneeruniverse.project.entity.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

public interface PersonnelManagementService {

    List<Company> selectCompanyVague(String companyName);

    List<TblDeptInfo> selectDeptVague(String deptName);

    List<TblProjectInfo> selectProjectVague(String projectName,String projectCode);

    List<TblUserInfo> selectUserInfoVague(TblUserInfo userInfo);

    Integer getAllUserProjectCount(Map<String,Object> map);

    List<TblUserInfo> getAllUserProject(Map<String,Object> map);

    String getProjectInfoByUser(Long userId);

    List<TblProjectInfo> getAllProject();

    List<TblProjectGroup> getProjectGroupByProjectId(Long projectId);

    void insertItmpProjectGroupUser(List<TblProjectGroupUser> groupUserList, HttpServletRequest request)  throws Exception ;

    void insertTmpProjectGroupUser(List<TblProjectGroupUser> tblProjectGroupUsers);

    List<TblProjectGroup> previewProject(Long projectId);

}
