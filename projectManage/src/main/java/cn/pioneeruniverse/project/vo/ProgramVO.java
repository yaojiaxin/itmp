package cn.pioneeruniverse.project.vo;

import java.io.Serializable;
import java.util.List;

public class ProgramVO implements Serializable {
    private static final long serialVersionUID = -6496067455862633155L;
    private Long projectId;
    private String projectName;
    private Integer developmentMode;
    private Double projectCount;
    private List<SpringInfoVO> milestones;
    private Integer minus;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<SpringInfoVO> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<SpringInfoVO> milestones) {
        this.milestones = milestones;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getDevelopmentMode() {
        return developmentMode;
    }

    public void setDevelopmentMode(Integer developmentMode) {
        this.developmentMode = developmentMode;
    }

    public Double getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Double projectCount) {
        this.projectCount = projectCount;
    }

    public Integer getMinus() {
        return minus;
    }

    public void setMinus(Integer minus) {
        this.minus = minus;
    }

    @Override
    public String toString() {
        return "ProgramVO{" +
                "projectId=" + projectId +
                ", projectName='" + projectName + '\'' +
                ", developmentMode=" + developmentMode +
                ", projectCount=" + projectCount +
                ", milestones=" + milestones +
                ", minus=" + minus +
                '}';
    }
}
