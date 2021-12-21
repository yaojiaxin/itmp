package cn.pioneeruniverse.project.vo;

import java.io.Serializable;

public class ProjectPlanVO implements Serializable {
    private static final long serialVersionUID = -6496067455862633155L;
    private Long planId;
    private String PlanName;
    private Long sprintId;

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return PlanName;
    }

    public void setPlanName(String planName) {
        PlanName = planName;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }
}
