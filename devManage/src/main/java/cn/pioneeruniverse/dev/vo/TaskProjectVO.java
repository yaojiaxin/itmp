package cn.pioneeruniverse.dev.vo;

import java.io.Serializable;

public class TaskProjectVO implements Serializable {

    private static final long serialVersionUID = -6496067455862633155L;
    private Long id;
    private String projectName;
    private String projectCode;
    private Integer developmentMode;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public Integer getDevelopmentMode() {
        return developmentMode;
    }

    public void setDevelopmentMode(Integer developmentMode) {
        this.developmentMode = developmentMode;
    }
}
