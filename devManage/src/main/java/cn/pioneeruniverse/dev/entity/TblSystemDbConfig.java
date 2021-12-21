package cn.pioneeruniverse.dev.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_system_db_config")
public class TblSystemDbConfig extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private Long systemId;

	private Long systemModuleId;

	private Integer environmentType;

	private Integer driverVersion;

	private String driverClassName;

	private String url;

	private String userName;

	private String password;


	@TableField(exist = false)
	private String driverVersionName;

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

	public Integer getEnvironmentType() {
		return environmentType;
	}

	public void setEnvironmentType(Integer environmentType) {
		this.environmentType = environmentType;
	}

	public Integer getDriverVersion() {
		return driverVersion;
	}

	public void setDriverVersion(Integer driverVersion) {
		this.driverVersion = driverVersion;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName == null ? null : driverClassName.trim();
	}

	public String getUrl() {
		return url;
	}
	public String getDriverVersionName() {
		return driverVersionName;
	}

	public void setDriverVersionName(String driverVersionName) {
		this.driverVersionName = driverVersionName;
	}

	public void setUrl(String url) {
		this.url = url == null ? null : url.trim();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName == null ? null : userName.trim();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password == null ? null : password.trim();
	}

}