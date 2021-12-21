package cn.pioneeruniverse.project.service.personnelmanagement.impl;

import cn.pioneeruniverse.common.annotion.DataSource;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.PinYinUtil;
import cn.pioneeruniverse.common.utils.SpringContextHolder;
import cn.pioneeruniverse.common.velocity.tag.VelocityDataDict;
import cn.pioneeruniverse.project.dao.mybatis.PersonnelManagementMapper;
import cn.pioneeruniverse.project.dao.mybatis.ProjectInfoMapper;
import cn.pioneeruniverse.project.dao.mybatis.role.RoleDao;
import cn.pioneeruniverse.project.entity.*;
import cn.pioneeruniverse.project.service.personnelmanagement.PersonnelManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service
public class PersonnelManagementServiceImpl implements PersonnelManagementService {

    @Autowired
    private PersonnelManagementMapper personnelManagementMapper;
    @Autowired
    private RoleDao roleDao;

    @Override
    public List<Company> selectCompanyVague(String companyName) {
        return personnelManagementMapper.selectCompanyVague(companyName);
    }

    @Override
    public List<TblDeptInfo> selectDeptVague(String deptName) {
        return personnelManagementMapper.selectDeptVague(deptName);
    }

    @Override
    public List<TblProjectInfo> selectProjectVague(String projectName,String projectCode){
        return personnelManagementMapper.selectProjectVague(projectName,projectCode);
    }

    @Override
    public List<TblUserInfo> selectUserInfoVague(TblUserInfo userInfo) {
        return personnelManagementMapper.selectUserInfoVague(userInfo.getUserAndAccount(),userInfo.getIds());
    }

    @Override
    public Integer getAllUserProjectCount(Map<String, Object> map) {
        return personnelManagementMapper.getAllUserProjectCount(map);
    }

    @Override
    public List<TblUserInfo> getAllUserProject(Map<String, Object> map) {
        List<TblUserInfo> user = personnelManagementMapper.getAllUserProject(map);
        return user;
    }

    @Override
    public String getProjectInfoByUser(Long userId) {
        return personnelManagementMapper.getProjectInfoByUser(userId);
    }

    @Override
    public List<TblProjectInfo> getAllProject() {
        return personnelManagementMapper.getAllProject();
    }

    @Override
    public List<TblProjectGroup> getProjectGroupByProjectId(Long projectId){
        return personnelManagementMapper.getProjectGroupByProjectId(projectId);
    }

    @Override
    @DataSource(name = "itmpDataSource")
    @Transactional(readOnly=false,rollbackFor=Exception.class,propagation = Propagation.REQUIRED)
    public void insertItmpProjectGroupUser(List<TblProjectGroupUser> groupUserList, HttpServletRequest request) throws Exception  {
        List<TblProjectGroupUser> tmpProjectGroupUser = new ArrayList();
        for(TblProjectGroupUser groupUser : groupUserList) {
            if (groupUser.getProjectId() == 0) {
                List<TblProjectInfo> projectList = personnelManagementMapper.getAllProject();
                for(TblProjectInfo project : projectList){
                    insertAllProjectRole(project.getId(),groupUser,request);
                }
            } else {
                insertAllProjectRole(groupUser.getProjectId(),groupUser,request);
            }
            //循环所选人员
            for (Long userId : groupUser.getUserIds()) {
                groupUser.setUserId(userId);
                //获取项目组的所有小组信息
                List<TblProjectGroup> projectGroupList =
                        personnelManagementMapper.getProjectGroupByProjectId(groupUser.getProjectId());
                //循环项目小组信息
                for (TblProjectGroup projectGroup : projectGroupList) {
                    groupUser.setProjectGroupId(projectGroup.getId());
                    Integer count = personnelManagementMapper.getCountByGroupUser(groupUser);
                    if (count == 0) {
                        TblProjectGroupUser groupUser1 = new TblProjectGroupUser();
                        groupUser1.setUserId(userId);
                        groupUser1.setProjectGroupId(groupUser.getProjectGroupId());
                        groupUser1.setUserPost(groupUser.getUserPost());
                        CommonUtil.setBaseValue(groupUser1, request);
                        personnelManagementMapper.insertItmpProjectGroupUser(groupUser1);
                        tmpProjectGroupUser.add(groupUser1);
                    }
                }
            }
        }
        SpringContextHolder.getBean(PersonnelManagementService.class).insertTmpProjectGroupUser(tmpProjectGroupUser);
    }

    @Override
    @DataSource(name = "tmpDataSource")
    @Transactional(readOnly=false,rollbackFor=Exception.class,propagation=Propagation.REQUIRES_NEW)
    public void insertTmpProjectGroupUser(List<TblProjectGroupUser> tblProjectGroupUsers){
        for (TblProjectGroupUser groupUser:tblProjectGroupUsers){
            personnelManagementMapper.insertTmpProjectGroupUser(groupUser);
        }
    }

    @Override
    public List<TblProjectGroup> previewProject(Long projectId) {
        return personnelManagementMapper.previewProject(projectId);
    }

    private void insertAllProjectRole(Long projectId,TblProjectGroupUser groupUser,HttpServletRequest request){
        //查询角色表是否存在对应的项目组与岗位的记录
        List<TblRoleInfo> roleInfoList = roleDao.
                selectUserPostRole(projectId, groupUser.getUserPost());
        Long roleId;
        if (roleInfoList == null || roleInfoList.size() == 0) {
            //如果不存在就新增一条岗位记录
            TblRoleInfo roleInfo = new TblRoleInfo();
            roleInfo.setProjectId(projectId);
            roleInfo.setUserPost(groupUser.getUserPost());
            VelocityDataDict dict = new VelocityDataDict();
            Map<String, String> userPostMap = dict.getDictMap("TBL_PROJECT_GROUP_USER_USER_POST");
            for (Map.Entry<String, String> entry : userPostMap.entrySet()) {
                if (entry.getKey().equals(groupUser.getUserPost().toString())) {
                    roleInfo.setRoleName(entry.getValue());
                    String empNo = projectId.toString() +
                            PinYinUtil.getPingYin(entry.getValue()).toUpperCase(Locale.ENGLISH);
                    roleInfo.setRoleCode(empNo);
                }
            }
            CommonUtil.setBaseValue(roleInfo, request);
            roleDao.insertUserPostRole(roleInfo);
            //获取新增岗位记录的id
            roleId = roleInfo.getId();
        } else {
            //如果存在就将数据的id赋值给roleId
            roleId = roleInfoList.get(0).getId();
        }
        for (Long userId : groupUser.getUserIds()) {
            //查询是否存在相对应的用户岗位
            Integer count = roleDao.selectUserRole(userId, roleId);
            if (count < 1) {
                Map<String, Object> userRole = new HashMap();
                //如果不存在相对应的用户岗位 就新增一条角色表记录
                userRole.put("roleId", roleId);
                userRole.put("useId", userId);
                userRole.put("status", 1);
                userRole.put("createBy", CommonUtil.getCurrentUserId(request));
                userRole.put("lastUpdateBy", CommonUtil.getCurrentUserId(request));
                roleDao.insertRoleUser(userRole);
            }
        }
    }
}
