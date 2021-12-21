package cn.pioneeruniverse.system.vo.user;

import java.util.Date;
import java.util.List;

import cn.pioneeruniverse.system.vo.BaseVo;
import cn.pioneeruniverse.system.vo.role.TblRoleInfo;

public class TblUserInfo extends BaseVo {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 720187800927749405L;

	private String userAccount;
	
	private String userPassword;
	
	private String userName;
	
	private String  employeeNumber;
	
	private Integer userStatus;
	private Integer userType;
	private Long companyId;
	private String companyName;
	
	private Long deptId;
	private String deptName;
	
	private Integer isAllowed;
	
	private String userRfid;
	
	private List<TblRoleInfo> userRoles;
	private String roleName;
	private List<Long> userIds;
	private Date entryDate;
	private Date leaveDate;
	

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

	public Integer getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(Integer userStatus) {
		this.userStatus = userStatus;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public Integer getIsAllowed() {
		return isAllowed;
	}

	public void setIsAllowed(Integer isAllowed) {
		this.isAllowed = isAllowed;
	}

	public String getUserRfid() {
		return userRfid;
	}

	public void setUserRfid(String userRfid) {
		this.userRfid = userRfid;
	}



	public List<TblRoleInfo> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<TblRoleInfo> userRoles) {
		this.userRoles = userRoles;
	}

	public List<Long> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
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

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Object get(String string) {
		// TODO Auto-generated method stub
		return null;
	}


}
