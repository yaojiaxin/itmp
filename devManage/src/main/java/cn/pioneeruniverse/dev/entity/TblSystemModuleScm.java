package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_system_module_scm")
public class TblSystemModuleScm extends BaseEntity {

    private static final long serialVersionUID = 1L;

    //private Integer environmentType;

    private Long systemId;

    private Long systemModuleId;
    private Long systemScmId;

    private Integer environmentType;
    private Integer scmType;

    private Long toolId;
    private String scmUrl;
    private String scmBranch;

    private Long systemRepositoryId;
    //仓库名称
    private String scmRepositoryName;

    @TableField(exist = false)
    private String [] ids;

    @TableField(exist = false)
    private String [] moduleIds;

    @TableField(exist = false)
    private String idsString;

    @TableField(exist = false)
    private String moduleIdsString;

    public Long getSystemScmId() {
        return systemScmId;
    }

    public void setSystemScmId(Long systemScmId) {
        this.systemScmId = systemScmId;
    }

    private String dependencySystemModuleIds;

    private Integer sourceCodeUpdateStatus;

	/*public Integer getEnvironmentType() {
        return environmentType;
	}

	public void setEnvironmentType(Integer environmentType) {
		this.environmentType = environmentType;
	}*/

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public Long getSystemModuleId() {
        return systemModuleId;
    }

    public void setSystemModuleId(Long systemModuleId) {
        this.systemModuleId = systemModuleId;
    }

    public String getDependencySystemModuleIds() {
        return dependencySystemModuleIds;
    }

    public void setDependencySystemModuleIds(String dependencySystemModuleIds) {
        this.dependencySystemModuleIds = dependencySystemModuleIds == null ? null : dependencySystemModuleIds.trim();
    }

    public Integer getSourceCodeUpdateStatus() {
        return sourceCodeUpdateStatus;
    }

    public void setSourceCodeUpdateStatus(Integer sourceCodeUpdateStatus) {
        this.sourceCodeUpdateStatus = sourceCodeUpdateStatus;
    }

    public Integer getEnvironmentType() {
        return environmentType;
    }
    public void setEnvironmentType(Integer environmentType) {
        this.environmentType = environmentType;
    }

    public Integer getScmType() {
        return scmType;
    }
    public void setScmType(Integer scmType) {
        this.scmType = scmType;
    }

    public Long getToolId() {
        return toolId;
    }
    public void setToolId(Long toolId) {
        this.toolId = toolId;
    }

    public String getScmUrl() {
        return scmUrl;
    }
    public void setScmUrl(String scmUrl) {
        this.scmUrl = scmUrl;
    }

    public String getScmBranch() {
        return scmBranch;
    }
    public void setScmBranch(String scmBranch) {
        this.scmBranch = scmBranch;
    }

    @Override
    public String toString() {
        return "TblSystemModuleScm [systemId=" + systemId + ", systemModuleId=" + systemModuleId + ", systemScmId="
                + systemScmId + ", dependencySystemModuleIds=" + dependencySystemModuleIds + ", sourceCodeUpdateStatus="
                + sourceCodeUpdateStatus + "]";
    }

    public Long getSystemRepositoryId() {
        return systemRepositoryId;
    }
    public void setSystemRepositoryId(Long systemRepositoryId) {
        this.systemRepositoryId = systemRepositoryId;
    }

    public String getScmRepositoryName() {
        return scmRepositoryName;
    }
    public void setScmRepositoryName(String scmRepositoryName) {
        this.scmRepositoryName = scmRepositoryName;
    }

    public String[] getIds() {
        return ids;
    }
    public void setIds(String[] ids) {
        this.ids = ids;
    }

    public String[] getModuleIds() {
        return moduleIds;
    }
    public void setModuleIds(String[] moduleIds) {
        this.moduleIds = moduleIds;
    }

    public String getIdsString() {
        return idsString;
    }
    public void setIdsString(String idsString) {
        this.idsString = idsString;
    }

    public String getModuleIdsString() {
        return moduleIdsString;
    }
    public void setModuleIdsString(String moduleIdsString) {
        this.moduleIdsString = moduleIdsString;
    }
}