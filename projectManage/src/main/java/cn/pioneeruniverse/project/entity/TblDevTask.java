package cn.pioneeruniverse.project.entity;

import java.util.Date;

import org.springframework.data.annotation.Transient;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_dev_task")
public class TblDevTask extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	private String devTaskName;
	
	private String devTaskCode;
	
	private String devTaskOverview;
	
	private Long requirementFeatureId;
	
	private Long defectID;
	
	private Integer devTaskPriority;
	
	private Integer devTaskStatus;
	
	private Integer codeReviewStatus;
	
	private Long devUserId;
	
	private Date planStartDate;
	
	private Date planEndDate;
	
	private Double planWorkload;
	
	private Double estimateRemainWorkload;
	
	private Date actualStartDate;
	
	private Date actualEndDate;
	
	private Double actualWorkload;
	
	private Long commissioningWindowId;
	
	private Long sprintId;
	
	private Long parentId;
	
	@Transient
	private String devUserName;  //开发人员

	public String getDevTaskName() {
		return devTaskName;
	}

	public void setDevTaskName(String devTaskName) {
		this.devTaskName = devTaskName;
	}

	public String getDevTaskCode() {
		return devTaskCode;
	}

	public void setDevTaskCode(String devTaskCode) {
		this.devTaskCode = devTaskCode;
	}

	public String getDevTaskOverview() {
		return devTaskOverview;
	}

	public void setDevTaskOverview(String devTaskOverview) {
		this.devTaskOverview = devTaskOverview;
	}

	public Long getRequirementFeatureId() {
		return requirementFeatureId;
	}

	public void setRequirementFeatureId(Long requirementFeatureId) {
		this.requirementFeatureId = requirementFeatureId;
	}

	public Long getDefectID() {
		return defectID;
	}

	public void setDefectID(Long defectID) {
		this.defectID = defectID;
	}

	public Integer getDevTaskPriority() {
		return devTaskPriority;
	}

	public void setDevTaskPriority(Integer devTaskPriority) {
		this.devTaskPriority = devTaskPriority;
	}

	public Integer getDevTaskStatus() {
		return devTaskStatus;
	}

	public void setDevTaskStatus(Integer devTaskStatus) {
		this.devTaskStatus = devTaskStatus;
	}

	public Integer getCodeReviewStatus() {
		return codeReviewStatus;
	}

	public void setCodeReviewStatus(Integer codeReviewStatus) {
		this.codeReviewStatus = codeReviewStatus;
	}

	public Long getDevUserId() {
		return devUserId;
	}

	public void setDevUserId(Long devUserId) {
		this.devUserId = devUserId;
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

	public Double getPlanWorkload() {
		return planWorkload;
	}

	public void setPlanWorkload(Double planWorkload) {
		this.planWorkload = planWorkload;
	}

	public Double getEstimateRemainWorkload() {
		return estimateRemainWorkload;
	}

	public void setEstimateRemainWorkload(Double estimateRemainWorkload) {
		this.estimateRemainWorkload = estimateRemainWorkload;
	}

	public Date getActualStartDate() {
		return actualStartDate;
	}

	public void setActualStartDate(Date actualStartDate) {
		this.actualStartDate = actualStartDate;
	}

	public Date getActualEndDate() {
		return actualEndDate;
	}

	public void setActualEndDate(Date actualEndDate) {
		this.actualEndDate = actualEndDate;
	}

	public Double getActualWorkload() {
		return actualWorkload;
	}

	public void setActualWorkload(Double actualWorkload) {
		this.actualWorkload = actualWorkload;
	}

	public Long getCommissioningWindowId() {
		return commissioningWindowId;
	}

	public void setCommissioningWindowId(Long commissioningWindowId) {
		this.commissioningWindowId = commissioningWindowId;
	}

	public Long getSprintId() {
		return sprintId;
	}

	public void setSprintId(Long sprintId) {
		this.sprintId = sprintId;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getDevUserName() {
		return devUserName;
	}

	public void setDevUserName(String devUserName) {
		this.devUserName = devUserName;
	}
	
	

}
