package cn.pioneeruniverse.project.entity;

public class User {

	private Long id;
	
	private String valueName;   //岗位名称
	
	private String userName;     //项目人员
	
	private String deptOrCompany;   //属于部门或公司
	
	private Integer userType;			//人员类型

	private Integer userPost;
	
	private Long peojectGroupUserId;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getUserPost() {
		return userPost;
	}

	public void setUserPost(Integer userPost) {
		this.userPost = userPost;
	}

	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getDeptOrCompany() {
		return deptOrCompany;
	}

	public void setDeptOrCompany(String deptOrCompany) {
		this.deptOrCompany = deptOrCompany;
	}

	public Integer getUserType() {
		return userType;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public Long getPeojectGroupUserId() {
		return peojectGroupUserId;
	}

	public void setPeojectGroupUserId(Long peojectGroupUserId) {
		this.peojectGroupUserId = peojectGroupUserId;
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", valueName='" + valueName + '\'' +
				", userName='" + userName + '\'' +
				", deptOrCompany='" + deptOrCompany + '\'' +
				", userType=" + userType +
				", userPost=" + userPost +
				", peojectGroupUserId=" + peojectGroupUserId +
				'}';
	}
}
