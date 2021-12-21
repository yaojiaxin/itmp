package cn.pioneeruniverse.project.entity;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_project_change_log")
public class TblProjectChangeLog extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	private Long projectChangeId;
	
	private String logType;
	
	private String logDetail;
	
	private Long userId;
	
	private String userName;
	
	private String userAccount;

	public Long getProjectChangeId() {
		return projectChangeId;
	}

	public void setProjectChangeId(Long projectChangeId) {
		this.projectChangeId = projectChangeId;
	}

	public String getLogType() {
		return logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getLogDetail() {
		return logDetail;
	}

	public void setLogDetail(String logDetail) {
		this.logDetail = logDetail;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAccount() {
		return userAccount;
	}

	public void setUserAccount(String userAccount) {
		this.userAccount = userAccount;
	}
	
	
}
