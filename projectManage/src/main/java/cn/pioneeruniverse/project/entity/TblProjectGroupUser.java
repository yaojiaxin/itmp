package cn.pioneeruniverse.project.entity;

import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_project_group_user")
public class TblProjectGroupUser extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	private Long projectGroupId;
	
	private Long userId;
	
	private Integer userPost;
	
	private Date planStartDate;
	
	private Date planEndDate;
	
	private Double allocateWorkload; //分配工时

	@TableField(exist = false)
	private List<Long> userIds;

	@TableField(exist = false)
	private Integer isAll;
	@TableField(exist = false)
	private Long projectId;


	public Long getProjectGroupId() {
		return projectGroupId;
	}

	public void setProjectGroupId(Long projectGroupId) {
		this.projectGroupId = projectGroupId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Integer getUserPost() {
		return userPost;
	}

	public void setUserPost(Integer userPost) {
		this.userPost = userPost;
	}

	public Date getPlanStartDate() {
		return planStartDate;
	}

	public void setPlanStartDate(Date planStartDate) {
		this.planStartDate = planStartDate;
	}

	public Date getPlanEndDate() {
		return planEndDate;
	}

	public void setPlanEndDate(Date planEndDate) {
		this.planEndDate = planEndDate;
	}

	public Double getAllocateWorkload() {
		return allocateWorkload;
	}

	public void setAllocateWorkload(Double allocateWorkload) {
		this.allocateWorkload = allocateWorkload;
	}

	public List<Long> getUserIds() {
		return userIds;
	}
	public void setUserIds(List<Long> userIds) {
		this.userIds = userIds;
	}

	public Integer getIsAll() {
		return isAll;
	}
	public void setIsAll(Integer isAll) {
		this.isAll = isAll;
	}

	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	@Override
	public String toString() {
		return "TblProjectGroupUser{" +
				"projectGroupId=" + projectGroupId +
				", userId=" + userId +
				", userPost=" + userPost +
				", planStartDate=" + planStartDate +
				", planEndDate=" + planEndDate +
				", allocateWorkload=" + allocateWorkload +
				", userIds=" + userIds +
				", isAll=" + isAll +
				", projectId=" + projectId +
				'}';
	}
}
