package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_system_deploy_script_template")
public class TblSystemDeployScriptTemplate extends BaseEntity{

	private static final long serialVersionUID=1L;

	private Byte templateType;

    private Integer stepOrder;

    private Byte operateType;

    private Integer waitTime;

    private String script;

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



	public Integer getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(Integer waitTime) {
		this.waitTime = waitTime;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public Byte getTemplateType() {
		return templateType;
	}

	public void setTemplateType(Byte templateType) {
		this.templateType = templateType;
	}
}