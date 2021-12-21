package cn.pioneeruniverse.dev.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_user_info")
public class TblUserInfo extends BaseEntity  {

	  private static final long serialVersionUID = -1421203092465310527L;
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
	    private String userScmPassword;
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
		public String getUserScmPassword() {
			return userScmPassword;
		}
		public void setUserScmPassword(String userScmPassword) {
			this.userScmPassword = userScmPassword;
		}
	    
	    
}
