package cn.pioneeruniverse.dev.entity;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_system_automatic_test_result")
public class TblSystemAutomaticTestResult extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private Long systemId;

	private Integer environmentType;

	private Long systemModuleId;

	private String testScene;

	private Long systemJenkinsJobRun;

	private String testRequestNumber;

	private Integer testResult;


	private Integer testType;

	private Integer successNumber;

	private Integer failedNumber;

	private String testResultDetailUrl;

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	public Integer getEnvironmentType() {
		return environmentType;
	}

	public void setEnvironmentType(Integer environmentType) {
		this.environmentType = environmentType;
	}

	public Long getSystemModuleId() {
		return systemModuleId;
	}

	public void setSystemModuleId(Long systemModuleId) {
		this.systemModuleId = systemModuleId;
	}

	public String getTestScene() {
		return testScene;
	}

	public void setTestScene(String testScene) {
		this.testScene = testScene;
	}

	public Long getSystemJenkinsJobRun() {
		return systemJenkinsJobRun;
	}

	public void setSystemJenkinsJobRun(Long systemJenkinsJobRun) {
		this.systemJenkinsJobRun = systemJenkinsJobRun;
	}

	public String getTestRequestNumber() {
		return testRequestNumber;
	}

	public void setTestRequestNumber(String testRequestNumber) {
		this.testRequestNumber = testRequestNumber;
	}

	public Integer getTestResult() {
		return testResult;
	}

	public void setTestResult(Integer testResult) {
		this.testResult = testResult;
	}

	public Integer getSuccessNumber() {
		return successNumber;
	}

	public void setSuccessNumber(Integer successNumber) {
		this.successNumber = successNumber;
	}

	public Integer getFailedNumber() {
		return failedNumber;
	}

	public void setFailedNumber(Integer failedNumber) {
		this.failedNumber = failedNumber;
	}

	public String getTestResultDetailUrl() {
		return testResultDetailUrl;
	}

	public void setTestResultDetailUrl(String testResultDetailUrl) {
		this.testResultDetailUrl = testResultDetailUrl;
	}
	public Integer getTestType() {
		return testType;
	}

	public void setTestType(Integer testType) {
		this.testType = testType;
	}
}