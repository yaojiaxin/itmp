package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_system_jenkins")
public class TblSystemJenkins extends BaseEntity {

	private static final long serialVersionUID = 1L;
	private Long systemId;
	private Long systemScmId;
	private Long toolId;
	private String jobName;
	private String jobPath;
	private String rootPom;
	private String cronJobName;
	private Integer createType;
	private Integer jobType;
	private Integer buildStatus;
	private String jobCron;
	private Integer deployStatus;
	private String goalsOptions;
	@TableField(exist = false)
	private String StringId;


	@TableField(exist = false)
	private Boolean isRun;
	
	private Integer environmentType;
	@TableField(exist = false)
	private String environmentTypeName;

	public void setCronJobName(String cronJobName) {
		this.cronJobName = cronJobName;
	}

	public String getCronJobName() {
		return cronJobName;
	}

	public Integer getCreateType() {
		return createType;
	}

	public void setCreateType(Integer createType) {
		this.createType = createType;
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

	public Long getToolId() {
		return toolId;
	}

	public void setToolId(Long toolId) {
		this.toolId = toolId;
	}

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName == null ? null : jobName.trim();
	}

	public String getJobPath() {
		return jobPath;
	}

	public void setJobPath(String jobPath) {
		this.jobPath = jobPath;
	}

	public String getRootPom() {
		return rootPom;
	}

	public void setRootPom(String rootPom) {
		this.rootPom = rootPom == null ? null : rootPom.trim();
	}

	public String getGoalsOptions() {
		return goalsOptions;
	}

	public void setGoalsOptions(String goalsOptions) {
		this.goalsOptions = goalsOptions == null ? null : goalsOptions.trim();
	}

	public Integer getBuildStatus() {
		return buildStatus;
	}

	public void setBuildStatus(Integer buildStatus) {
		this.buildStatus = buildStatus;
	}

	public Integer getJobType() {
		return jobType;
	}

	public void setJobType(Integer jobType) {
		this.jobType = jobType;
	}

	public String getStringId() {
		return StringId;
	}

	public void setStringId(String stringId) {
		StringId = stringId;
	}

	public String getJobCron() {
		return jobCron;
	}

	public void setJobCron(String jobCron) {
		this.jobCron = jobCron;
	}

	public Integer getEnvironmentType() {
		return environmentType;
	}

	public void setEnvironmentType(Integer environmentType) {
		this.environmentType = environmentType;
	}

	public String getEnvironmentTypeName() {
		return environmentTypeName;
	}

	public void setEnvironmentTypeName(String environmentTypeName) {
		this.environmentTypeName = environmentTypeName;
	}

	public Integer getDeployStatus() {
		return deployStatus;
	}

	public void setDeployStatus(Integer deployStatus) {
		this.deployStatus = deployStatus;
	}
	public Boolean getIsRun() {
		return isRun;
	}

	public void setIsRun(Boolean run) {
		isRun = run;
	}

}