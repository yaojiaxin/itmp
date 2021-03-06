package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;

public class TblToolInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private Integer functionType;// 功能类别

	private Integer toolType;// 工具分类
	
	private String toolTypeName;// 工具名称

	private String homePath;// 地址

	private String toolImgPath;// 图片地址

	private String ip;

	private String port;

	private String userName;

	private String password;
	
	private String accessUrl;

	private String jenkinsApiToken;
	
	private String snapshotRepositoryName;
	
	private String releaseRepositoryName;

	public Integer getFunctionType() {
		return functionType;
	}

	public void setFunctionType(Integer functionType) {
		this.functionType = functionType;
	}

	public Integer getToolType() {
		return toolType;
	}

	public void setToolType(Integer toolType) {
		this.toolType = toolType;
	}

	public String getToolTypeName() {
		return toolTypeName;
	}

	public void setToolTypeName(String toolTypeName) {
		this.toolTypeName = toolTypeName;
	}

	public String getHomePath() {
		return homePath;
	}

	public void setHomePath(String homePath) {
		this.homePath = homePath;
	}

	public String getToolImgPath() {
		return toolImgPath;
	}

	public void setToolImgPath(String toolImgPath) {
		this.toolImgPath = toolImgPath;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getAccessUrl() {
		return accessUrl;
	}

	public void setAccessUrl(String accessUrl) {
		this.accessUrl = accessUrl;
	}

	public String getJenkinsApiToken() {
		return jenkinsApiToken;
	}

	public void setJenkinsApiToken(String jenkinsApiToken) {
		this.jenkinsApiToken = jenkinsApiToken;
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
	
	

}