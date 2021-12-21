package cn.pioneeruniverse.project.vo;

import java.io.Serializable;
import java.util.List;

public class ProgramProjectVO implements Serializable {
    private static final long serialVersionUID = -6496067455862633155L;
    private String projectName;  //项目名称
    private String userName;  //项目经理
    private Integer projectStatus;  //项目状态
    private Integer projectScheduleStatus;  //项目进度状态
    private Long projectId;
    private List<Milestones> milestones;
    private Integer developmentMode;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(Integer projectStatus) {
        this.projectStatus = projectStatus;
    }

    public Integer getProjectScheduleStatus() {
        return projectScheduleStatus;
    }

    public void setProjectScheduleStatus(Integer projectScheduleStatus) {
        this.projectScheduleStatus = projectScheduleStatus;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public List<Milestones> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestones> milestones) {
        this.milestones = milestones;
    }

    public Integer getDevelopmentMode() {
        return developmentMode;
    }

    public void setDevelopmentMode(Integer developmentMode) {
        this.developmentMode = developmentMode;
    }

    @Override
    public String toString() {
        return "ProgramProjectVO{" +
                "projectName='" + projectName + '\'' +
                ", userName='" + userName + '\'' +
                ", projectStatus=" + projectStatus +
                ", projectScheduleStatus=" + projectScheduleStatus +
                ", projectId=" + projectId +
                ", milestones=" + milestones +
                ", developmentMode=" + developmentMode +
                '}';
    }

}
