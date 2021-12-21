package cn.pioneeruniverse.common.dto;

import cn.pioneeruniverse.common.entity.BaseEntity;

import java.util.Date;
import java.util.Set;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description: 系统用户DTO类
 * @Date: Created in 16:25 2018/12/13
 * @Modified By:
 */
public class TblUserInfoDTO extends BaseEntity {


    private static final long serialVersionUID = -3964598288460722398L;

    private String userAccount;
    private String userPassword;
    private String userName;
    private String employeeNumber;
    private String email;
    private String gender;
    private Date birthday;
    private Integer userType;
    private Long companyId;
    private Date entryDate;
    private Date leaveDate;
    private Long deptId;
    private Integer userStatus;
    private Integer isAllowed;
    private String userScmAccount;
    private String userScmPassword;


    //非数据表字段
    private String companyName;
    private String roleName;
    private String svnAuthority;
    private Integer gitLabAccessLevel;
    private String deptName;
    private String excludeUserId;
    private Boolean removeExcludeUser;
    private String projectGroupId;
    private String myProjectGroupId;
    private String myProjectGroupName;
    private String myProjectName;
    private Set<String> stringPermissions;
    private Set<String> roles;
    private Set<String> stringPermissionUrls;
    private Long projectId;
    private Integer userPost;

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getLeaveDate() {
        return leaveDate;
    }

    public void setLeaveDate(Date leaveDate) {
        this.leaveDate = leaveDate;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public Integer getIsAllowed() {
        return isAllowed;
    }

    public void setIsAllowed(Integer isAllowed) {
        this.isAllowed = isAllowed;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getSvnAuthority() {
        return svnAuthority;
    }

    public void setSvnAuthority(String svnAuthority) {
        this.svnAuthority = svnAuthority;
    }

    public String getUserScmPassword() {
        return userScmPassword;
    }

    public void setUserScmPassword(String userScmPassword) {
        this.userScmPassword = userScmPassword;
    }

    public String getExcludeUserId() {
        return excludeUserId;
    }

    public void setExcludeUserId(String excludeUserId) {
        this.excludeUserId = excludeUserId;
    }

    public String getProjectGroupId() {
        return projectGroupId;
    }

    public void setProjectGroupId(String projectGroupId) {
        this.projectGroupId = projectGroupId;
    }

    public String getUserScmAccount() {
        return userScmAccount;
    }

    public void setUserScmAccount(String userScmAccount) {
        this.userScmAccount = userScmAccount;
    }

    public String getMyProjectGroupId() {
        return myProjectGroupId;
    }

    public void setMyProjectGroupId(String myProjectGroupId) {
        this.myProjectGroupId = myProjectGroupId;
    }

    public String getMyProjectGroupName() {
        return myProjectGroupName;
    }

    public void setMyProjectGroupName(String myProjectGroupName) {
        this.myProjectGroupName = myProjectGroupName;
    }

    public Set<String> getStringPermissions() {
        return stringPermissions;
    }

    public void setStringPermissions(Set<String> stringPermissions) {
        this.stringPermissions = stringPermissions;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getStringPermissionUrls() {
        return stringPermissionUrls;
    }

    public void setStringPermissionUrls(Set<String> stringPermissionUrls) {
        this.stringPermissionUrls = stringPermissionUrls;
    }

    public Integer getGitLabAccessLevel() {
        return gitLabAccessLevel;
    }

    public void setGitLabAccessLevel(Integer gitLabAccessLevel) {
        this.gitLabAccessLevel = gitLabAccessLevel;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Boolean getRemoveExcludeUser() {
        return removeExcludeUser;
    }

    public void setRemoveExcludeUser(Boolean removeExcludeUser) {
        this.removeExcludeUser = removeExcludeUser;
    }

    public Integer getUserPost() {
        return userPost;
    }

    public void setUserPost(Integer userPost) {
        this.userPost = userPost;
    }

    public String getMyProjectName() {
        return myProjectName;
    }

    public void setMyProjectName(String myProjectName) {
        this.myProjectName = myProjectName;
    }
}
