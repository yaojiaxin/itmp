package cn.pioneeruniverse.project.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 项目群里程碑
 */
public class ProgramProjectPlanVO implements Serializable {

    private static final long serialVersionUID = -6496067455862633155L;
    private Long id;
    private String programName;
    private String devMode;
    private List<Milestones> milestones;
    private Long sprintId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getDevMode() {
        return devMode;
    }

    public void setDevMode(String devMode) {
        this.devMode = devMode;
    }

    public List<Milestones> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestones> milestones) {
        this.milestones = milestones;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    @Override
    public String toString() {
        return "ProgramProjectPlanVO{" +
                "id=" + id +
                ", programName='" + programName + '\'' +
                ", devMode='" + devMode + '\'' +
                ", milestones=" + milestones +
                ", sprintId=" + sprintId +
                '}';
    }
}
