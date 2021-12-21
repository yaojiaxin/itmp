package cn.pioneeruniverse.dev.entity;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_dev_task_remark")
public class TblDevTaskRemark extends BaseEntity{
	private static final long serialVersionUID = 1L;

	private Long devTaskId;
	private String devTaskRemark;
	private Long userId;
	private String userName;
	private String userAccount;
	
	public Long getDevTaskId() {
		return devTaskId;
	}
	public void setDevTaskId(Long devTaskId) {
		this.devTaskId = devTaskId;
	}
	
	public String getDevTaskRemark() {
		return devTaskRemark;
	}
	public void setDevTaskRemark(String devTaskRemark) {
		this.devTaskRemark = devTaskRemark;
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
