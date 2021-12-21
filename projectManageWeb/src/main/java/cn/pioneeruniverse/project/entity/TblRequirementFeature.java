package cn.pioneeruniverse.project.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;


public class TblRequirementFeature extends BaseEntity {

	private static final long serialVersionUID = 1L;

    private String featureName;
    private String featureOverview;
    private Long projectId;
    private Long systemId;
    private Long requirementId;
    private Long commissioningWindowId;   

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName == null ? null : featureName.trim();
    }

    public String getFeatureOverview() {
        return featureOverview;
    }

    public void setFeatureOverview(String featureOverview) {
        this.featureOverview = featureOverview == null ? null : featureOverview.trim();
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public Long getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(Long requirementId) {
        this.requirementId = requirementId;
    }

    public Long getCommissioningWindowId() {
        return commissioningWindowId;
    }

    public void setCommissioningWindowId(Long commissioningWindowId) {
        this.commissioningWindowId = commissioningWindowId;
    }	

}