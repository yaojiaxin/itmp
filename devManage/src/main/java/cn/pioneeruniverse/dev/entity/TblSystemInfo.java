package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.*;

import java.util.List;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_system_info")
public class TblSystemInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;

	//private Long projectId;
	@TableField(exist = false)
	private String projectIds;

	@TableField(exist = false)
	private String projectName;

	private String systemName;

	private String systemCode;

	private String systemShortName;

	private Integer sonarScanStatus;

	private String jdkVersion;

	private Integer developmentMode;

	private String codeReviewUserIds;

	private String compileCommand;
	
	private String buildToolVersion;
	
	private String packageSuffix; //包件后缀jar,war...

	private String systemType;

	@TableField(exist = false)
	private List<String> systemTypeList;

	@TableField(exist = false)
	private String codeReviewUserName; // 代码评审人

	private String groupId;

	private String artifactId;

	private Integer architectureType;

	private String snapshotRepositoryName;

	private String releaseRepositoryName;

	private Integer scmStrategy;// 代码托管策略（数据字典：1=主干分支；2=多分支）

	private Integer deployType;// 部署方式（1:源码构建部署，2:制品部署）

	private Integer productionDeployType;// 生产环境部署方式（1:726部署流程，2:非726部署流程）

	private Integer codeReviewStatus;// 是否代码评审（1:是，2:否）

	private Integer buildType;

	private Integer createType;

	private String  systemFlag;		//系统标签

	private String  environmentType;

	public String getCompileCommand() {
		return compileCommand;
	}

	public void setCompileCommand(String compileCommand) {
		this.compileCommand = compileCommand;
	}

	public String getProjectIds() {
		return projectIds;
	}

	public void setProjectIds(String projectIds) {
		this.projectIds = projectIds;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

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

	public Integer getScmStrategy() {
		return scmStrategy;
	}

	public void setScmStrategy(Integer scmStrategy) {
		this.scmStrategy = scmStrategy;
	}

	public Integer getDeployType() {
		return deployType;
	}

	public void setDeployType(Integer deployType) {
		this.deployType = deployType;
	}

	public Integer getProductionDeployType() {
		return productionDeployType;
	}

	public void setProductionDeployType(Integer productionDeployType) {
		this.productionDeployType = productionDeployType;
	}

	public Integer getCodeReviewStatus() {
		return codeReviewStatus;
	}

	public void setCodeReviewStatus(Integer codeReviewStatus) {
		this.codeReviewStatus = codeReviewStatus;
	}

	public Integer getBuildType() {
		return buildType;
	}

	public void setBuildType(Integer buildType) {
		this.buildType = buildType;
	}

	public Integer getCreateType() {
		return createType;
	}

	public void setCreateType(Integer createType) {
		this.createType = createType;
	}

	public List<String> getSystemTypeList() {
		return systemTypeList;
	}

	public void setSystemTypeList(List<String> systemTypeList) {
		this.systemTypeList = systemTypeList;
	}

	public String getSnapshotRepositoryName() {
		return snapshotRepositoryName;
	}

	public void setSnapshotRepositoryName(String snapshotRepositoryName) {
		this.snapshotRepositoryName = snapshotRepositoryName;
	}

	public String getReleaseRepositoryName() {
		return releaseRepositoryName;
	}

	public void setReleaseRepositoryName(String releaseRepositoryName) {
		this.releaseRepositoryName = releaseRepositoryName;
	}

	public String getJdkVersion() {
		return jdkVersion;
	}

	public void setJdkVersion(String jdkVersion) {
		this.jdkVersion = jdkVersion;
	}

	public Integer getDevelopmentMode() {
		return developmentMode;
	}

	public void setDevelopmentMode(Integer developmentMode) {
		this.developmentMode = developmentMode;
	}

	public String getCodeReviewUserIds() {
		return codeReviewUserIds;
	}

	public void setCodeReviewUserIds(String codeReviewUserIds) {
		this.codeReviewUserIds = codeReviewUserIds;
	}

	public String getCodeReviewUserName() {
		return codeReviewUserName;
	}

	public void setCodeReviewUserName(String codeReviewUserName) {
		this.codeReviewUserName = codeReviewUserName;
	}

	public Integer getSonarScanStatus() {
		return sonarScanStatus;
	}

	public void setSonarScanStatus(Integer sonarScanStatus) {
		this.sonarScanStatus = sonarScanStatus;
	}
	public String getEnvironmentType() {
		return environmentType;
	}

	public void setEnvironmentType(String environmentType) {
		this.environmentType = environmentType;
	}
	

	public String getBuildToolVersion() {
		return buildToolVersion;
	}

	public void setBuildToolVersion(String buildToolVersion) {
		this.buildToolVersion = buildToolVersion;
	}
	
	public String getPackageSuffix() {
		return packageSuffix;
	}

	public void setPackageSuffix(String packageSuffix) {
		this.packageSuffix = packageSuffix;
	}

	public String getSystemFlag() {
		return systemFlag;
	}
	public void setSystemFlag(String systemFlag) {
		this.systemFlag = systemFlag;
	}

	@Override
	public String toString() {
		return "TblSystemInfo [systemName=" + systemName + ", systemCode=" + systemCode
				+ ", groupId=" + groupId + ", artifactId=" + artifactId + ", architectureType=" + architectureType
				+ "]";
	}



}