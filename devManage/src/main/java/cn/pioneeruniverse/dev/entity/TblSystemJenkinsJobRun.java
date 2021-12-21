package cn.pioneeruniverse.dev.entity;

import java.sql.Timestamp;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_system_jenkins_job_run")
public class TblSystemJenkinsJobRun extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private Long systemJenkinsId;
	private Integer environmentType;
	private Integer jobRunNumber;
	private Long systemId;
	private String jobConfiguration;
	private String buildParameter;
	private String jobName;
	private String rootPom;
	private String goalsOptions;
	private Integer buildStatus;
	private Integer createType;
	private String buildLogs;
	private Timestamp startDate;
	private Timestamp endDate;
	private Integer jobType;
	@TableField(exist = false)
	private String systemName;// 系统名称

	@TableField(exist = false)
	private String jenkinsId;// 系统名称

	@TableField(exist = false)
	private Long inCount; // 每日构建中的总条数
	@TableField(exist = false)
	private Long successCount;// 每日构建成功的总条数
	@TableField(exist = false)
	private Long failCount; // 每日构建失败的总条数
	@TableField(exist = false)
	private Long minuteCount; // 每日构建的总时间数(分钟)

	public Integer getJobType() {
		return jobType;
	}

	public void setJobType(Integer jobType) {
		this.jobType = jobType;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

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

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName == null ? null : jobName.trim();
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

	public String getBuildLogs() {
		return buildLogs;
	}

	public void setBuildLogs(String buildLogs) {
		this.buildLogs = buildLogs == null ? null : buildLogs.trim();
	}

	public Integer getCreateType() {
		return createType;
	}

	public void setCreateType(Integer createType) {
		this.createType = createType;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public Long getInCount() {
		return inCount;
	}

	public void setInCount(Long inCount) {
		this.inCount = inCount;
	}

	public Long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Long successCount) {
		this.successCount = successCount;
	}

	public Long getFailCount() {
		return failCount;
	}

	public void setFailCount(Long failCount) {
		this.failCount = failCount;
	}

	public Long getMinuteCount() {
		return minuteCount;
	}

	public void setMinuteCount(Long minuteCount) {
		this.minuteCount = minuteCount;
	}

	public String getJenkinsId() {
		return jenkinsId;
	}

	public void setJenkinsId(String jenkinsId) {
		this.jenkinsId = jenkinsId;
	}

	public Integer getEnvironmentType() {
		return environmentType;
	}

	public void setEnvironmentType(Integer environmentType) {
		this.environmentType = environmentType;
	}

	public String getJobConfiguration() {
		return jobConfiguration;
	}

	public void setJobConfiguration(String jobConfiguration) {
		this.jobConfiguration = jobConfiguration;
	}
	public Integer getJobRunNumber() {
		return jobRunNumber;
	}

	public void setJobRunNumber(Integer jobRunNumber) {
		this.jobRunNumber = jobRunNumber;
	}
	public String getBuildParameter() {
		return buildParameter;
	}

	public void setBuildParameter(String buildParameter) {
		this.buildParameter = buildParameter;
	}

}