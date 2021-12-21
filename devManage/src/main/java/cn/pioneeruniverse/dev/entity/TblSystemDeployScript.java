package cn.pioneeruniverse.dev.entity;

import com.github.pagehelper.StringUtil;

import cn.pioneeruniverse.common.entity.BaseEntity;

public class TblSystemDeployScript extends BaseEntity{

	private static final long serialVersionUID = 2342206953167986666L;

	private Long systemDeployId;

    private Integer stepOrder;

    private Byte operateType;

    private String userName;

    private String password;

    private Integer waitTime;

    private String script;

	public Long getSystemDeployId() {
		return systemDeployId;
	}

	public void setSystemDeployId(Long systemDeployId) {
		this.systemDeployId = systemDeployId;
	}

	public Integer getStepOrder() {
		return stepOrder;
	}

	public void setStepOrder(Integer stepOrder) {
		this.stepOrder = stepOrder;
	}

	public Byte getOperateType() {
		return operateType;
	}

	public void setOperateType(Byte operateType) {
		this.operateType = operateType;
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

	public Integer getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(Integer waitTime) {
		this.waitTime = waitTime;
	}

	public String getScript() {
		String scriptTemp = script;
		if (StringUtil.isNotEmpty(script)) {
			scriptTemp = script.replaceAll("&amp;", "&").replaceAll("&lt;", "<").replaceAll("&gt;", ">");
		}
		return scriptTemp;
	}

	public void setScript(String script) {
		this.script = script;
	}
    
    
}