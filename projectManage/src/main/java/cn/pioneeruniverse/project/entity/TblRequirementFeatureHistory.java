package cn.pioneeruniverse.project.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_requirement_feature_history")
public class TblRequirementFeatureHistory {
    private Long id;

    private Long requirementFeatureId;

    private Long assetSystemTreeId;

    private Double estimateRemainWorkload;

    private Long commissioningWindowId;

    private Long sprintId;

    private Long projectPlanId;

    private String requirementFeatureStatus;

    private Integer status;

    private Date createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRequirementFeatureId() {
        return requirementFeatureId;
    }

    public void setRequirementFeatureId(Long requirementFeatureId) {
        this.requirementFeatureId = requirementFeatureId;
    }

    public Long getAssetSystemTreeId() {
        return assetSystemTreeId;
    }

    public void setAssetSystemTreeId(Long assetSystemTreeId) {
        this.assetSystemTreeId = assetSystemTreeId;
    }

    public Double getEstimateRemainWorkload() {
        return estimateRemainWorkload;
    }

    public void setEstimateRemainWorkload(Double estimateRemainWorkload) {
        this.estimateRemainWorkload = estimateRemainWorkload;
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

    public Long getProjectPlanId() {
        return projectPlanId;
    }

    public void setProjectPlanId(Long projectPlanId) {
        this.projectPlanId = projectPlanId;
    }

    public String getRequirementFeatureStatus() {
        return requirementFeatureStatus;
    }

    public void setRequirementFeatureStatus(String requirementFeatureStatus) {
        this.requirementFeatureStatus = requirementFeatureStatus == null ? null : requirementFeatureStatus.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}