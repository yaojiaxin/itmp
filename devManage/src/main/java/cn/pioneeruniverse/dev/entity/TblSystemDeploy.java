package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;

public class TblSystemDeploy extends BaseEntity{

	private static final long serialVersionUID = 7830782747538469351L;

	private Integer environmentType;

    private Long systemId;

    private Long systemModuleId;

    private String serverIds;

    private String systemDeployPath;

    private Integer timeOut;

    private Integer retryNumber;
    
    private Integer deploySequence;

    private Long toolId;

    private Integer deployStatus;
    
    private String stopSystemScript;
    
    private String startSystemScript;
    
    private String checkScript;

	private Integer templateType;
    
	public String getStopSystemScript() {
		return stopSystemScript;
	}

	public void setStopSystemScript(String stopSystemScript) {
		this.stopSystemScript = stopSystemScript;
	}

	public String getStartSystemScript() {
		return startSystemScript;
	}

	public void setStartSystemScript(String startSystemScript) {
		this.startSystemScript = startSystemScript;
	}

	public String getCheckScript() {
		return checkScript;
	}

	public void setCheckScript(String checkScript) {
		this.checkScript = checkScript;
	}

	public Integer getEnvironmentType() {
		return environmentType;
	}

	public void setEnvironmentType(Integer environmentType) {
		this.environmentType = environmentType;
	}

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

	public String getServerIds() {
		return serverIds;
	}

	public void setServerIds(String serverIds) {
		this.serverIds = serverIds;
	}

	public String getSystemDeployPath() {
		return systemDeployPath;
	}

	public void setSystemDeployPath(String systemDeployPath) {
		this.systemDeployPath = systemDeployPath;
	}

	public Integer getTimeOut() {
		return timeOut;
	}

	public void setTimeOut(Integer timeOut) {
		this.timeOut = timeOut;
	}

	public Integer getRetryNumber() {
		return retryNumber;
	}

	public void setRetryNumber(Integer retryNumber) {
		this.retryNumber = retryNumber;
	}
	
	public Integer getDeploySequence() {
		return deploySequence;
	}

	public void setDeploySequence(Integer deploySequence) {
		this.deploySequence = deploySequence;
	}

	public Long getToolId() {
		return toolId;
	}

	public void setToolId(Long toolId) {
		this.toolId = toolId;
	}

	public Integer getDeployStatus() {
		return deployStatus;
	}

	public void setDeployStatus(Integer deployStatus) {
		this.deployStatus = deployStatus;
	}
	public Integer getTemplateType() {
		return templateType;
	}

	public void setTemplateType(Integer templateType) {
		this.templateType = templateType;
	}
    
    
}