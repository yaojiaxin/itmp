package cn.pioneeruniverse.project.dao.mybatis;


import cn.pioneeruniverse.project.entity.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface PersonnelManagementMapper {

    List<Company> selectCompanyVague(String companyName);

    List<TblDeptInfo> selectDeptVague(String deptName);

    List<TblProjectInfo> selectProjectVague(@Param("projectName")String projectName,
                                            @Param("projectCode")String projectCode);

    List<TblUserInfo> selectUserInfoVague(@Param("userAndAccount")String userAndAccount,
                                          @Param("ids")Long [] ids);

    Integer getAllUserProjectCount(Map<String,Object> map);

    List<TblUserInfo> getAllUserProject(Map<String,Object> map);

    String getProjectInfoByUser(Long userId);

    List<TblProjectInfo> getAllProject();

    List<TblProjectGroup> getProjectGroupByProjectId(Long projectId);

    Integer getCountByGroupUser(TblProjectGroupUser tblProjectGroupUser);

    void insertItmpProjectGroupUser(TblProjectGroupUser tblProjectGroupUser);

    void insertTmpProjectGroupUser(TblProjectGroupUser tblProjectGroupUser);

    List<TblProjectGroup> previewProject(Long projectId);

}
