package cn.pioneeruniverse.dev.entity;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_system_automatic_test_config")
public class TblSystemAutomaticTestConfig extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private Long systemId;

	private Integer environmentType;

	private Long systemModuleId;

	private String testScene;

	private Integer testType;

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
	public Integer getTestType() {
		return testType;
	}

	public void setTestType(Integer testType) {
		this.testType = testType;
	}

}