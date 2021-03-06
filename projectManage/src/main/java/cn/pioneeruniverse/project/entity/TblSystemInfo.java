package cn.pioneeruniverse.project.entity;


import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_system_info")
public class TblSystemInfo  extends BaseEntity{
	
	private static final long serialVersionUID = 1L;


    private String systemName;

    private String systemCode;
    
    private String systemShortName; //系统简称
    
    private String systemType;   //系统类型
    
    private String groupId;

    private String artifactId;
    
    private String snapshotRepositoryName;
    
   

    private Integer architectureType;
    
    private Integer buildType;   //构建方式
   
    private Integer scmStrategy;   //代码托管策略

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName == null ? null : systemName.trim();
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode == null ? null : systemCode.trim();
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId == null ? null : groupId.trim();
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId == null ? null : artifactId.trim();
    }

    public Integer getArchitectureType() {
        return architectureType;
    }

    public void setArchitectureType(Integer architectureType) {
        this.architectureType = architectureType;
    }

	public String getSystemShortName() {
		return systemShortName;
	}

	public void setSystemShortName(String systemShortName) {
		this.systemShortName = systemShortName;
	}

	public String getSystemType() {
		return systemType;
	}

	public void setSystemType(String systemType) {
		this.systemType = systemType;
	}

	public Integer getBuildType() {
		return buildType;
	}

	public void setBuildType(Integer buildType) {
		this.buildType = buildType;
	}

	public Integer getScmStrategy() {
		return scmStrategy;
	}

	public void setScmStrategy(Integer scmStrategy) {
		this.scmStrategy = scmStrategy;
	}

	@Override
	public String toString() {
		return "TblSystemInfo [projectId= systemName=" + systemName + ", systemCode=" + systemCode
				+ ", systemShortName=" + systemShortName + ", systemType=" + systemType + ", groupId=" + groupId
				+ ", artifactId=" + artifactId + ", architectureType=" + architectureType + ", buildType=" + buildType
				+ ", scmStrategy=" + scmStrategy + "]";
	}

   

	

}