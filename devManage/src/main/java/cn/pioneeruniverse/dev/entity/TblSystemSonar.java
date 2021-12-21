package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_system_sonar")
public class TblSystemSonar extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long systemId;

    private Long systemScmId;

    private Long systemModuleId;

    private Long toolId;

    private String sonarProjectKey;

    private String sonarProjectName;

    private String sonarSources;

    private String sonarJavaBinaries;
    private Long systemJenkinsId;

    public Long getSystemJenkinsId() {
        return systemJenkinsId;
    }

    public void setSystemJenkinsId(Long systemJenkinsId) {
        this.systemJenkinsId = systemJenkinsId;
    }

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public Long getSystemScmId() {
        return systemScmId;
    }

    public void setSystemScmId(Long systemScmId) {
        this.systemScmId = systemScmId;
    }

    public Long getSystemModuleId() {
        return systemModuleId;
    }

    public void setSystemModuleId(Long systemModuleId) {
        this.systemModuleId = systemModuleId;
    }

    public String getSonarProjectKey() {
        return sonarProjectKey;
    }

    public void setSonarProjectKey(String sonarProjectKey) {
        this.sonarProjectKey = sonarProjectKey == null ? null : sonarProjectKey.trim();
    }

    public String getSonarProjectName() {
        return sonarProjectName;
    }

    public void setSonarProjectName(String sonarProjectName) {
        this.sonarProjectName = sonarProjectName == null ? null : sonarProjectName.trim();
    }

    public String getSonarSources() {
        return sonarSources;
    }

    public void setSonarSources(String sonarSources) {
        this.sonarSources = sonarSources == null ? null : sonarSources.trim();
    }

    public String getSonarJavaBinaries() {
        return sonarJavaBinaries;
    }

    public void setSonarJavaBinaries(String sonarJavaBinaries) {
        this.sonarJavaBinaries = sonarJavaBinaries == null ? null : sonarJavaBinaries.trim();
    }

    public Long getToolId() {
        return toolId;
    }

    public void setToolId(Long toolId) {
        this.toolId = toolId;
    }

}