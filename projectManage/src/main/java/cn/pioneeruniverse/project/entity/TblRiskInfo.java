package cn.pioneeruniverse.project.entity;

import org.springframework.data.annotation.Transient;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_risk_info")
public class TblRiskInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	private Long projectId;   //项目表主键
	
	private Long programId;   //项目群主键
	
	private Long responsibleUserId;    //责任方
	
	private String riskDescription;    //风险描述
	
	private Integer riskType;    //风险分类(数据字典)
	
	private Integer riskFactor;    //危险度(数据字典)
	
	private Integer riskProbability;    //发生概率(数据字典)
	
	private Integer riskPriority;    //风险优先级(数据字典)
	
	private String riskInfluence;    //风险影响
	
	private String riskTriggers;    //触发条件
	
	private Integer riskStatus;    //风险状态(数据字典)
	
	private String remark;    //备注
	
	private String copingStrategy;    //应对措施
	
	private String copingStrategyRecord;    //应对措施执行记录
	
	@Transient
	private String responsibleUser;    //责任人
	
	@Transient
	private String riskPriorityName;    //优先级
	
	@Transient
	private String statusName;    //风险状态
	
	@Transient
	private String riskTypeName;    //风险分类名称
	
	@Transient
	private String riskFactorName;   //危险度名称
	
	@Transient
	private String riskProbabilityName;  //发生概率名称
	
	@Transient
	private Long number;  //编号(排序号)
	
	

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getResponsibleUserId() {
		return responsibleUserId;
	}

	public void setResponsibleUserId(Long responsibleUserId) {
		this.responsibleUserId = responsibleUserId;
	}

	public String getRiskDescription() {
		return riskDescription;
	}

	public void setRiskDescription(String riskDescription) {
		this.riskDescription = riskDescription;
	}

	public Integer getRiskType() {
		return riskType;
	}

	public void setRiskType(Integer riskType) {
		this.riskType = riskType;
	}

	public Integer getRiskFactor() {
		return riskFactor;
	}

	public void setRiskFactor(Integer riskFactor) {
		this.riskFactor = riskFactor;
	}

	public Integer getRiskProbability() {
		return riskProbability;
	}

	public void setRiskProbability(Integer riskProbability) {
		this.riskProbability = riskProbability;
	}

	public Integer getRiskPriority() {
		return riskPriority;
	}

	public void setRiskPriority(Integer riskPriority) {
		this.riskPriority = riskPriority;
	}

	public String getRiskInfluence() {
		return riskInfluence;
	}

	public void setRiskInfluence(String riskInfluence) {
		this.riskInfluence = riskInfluence;
	}

	public String getRiskTriggers() {
		return riskTriggers;
	}

	public void setRiskTriggers(String riskTriggers) {
		this.riskTriggers = riskTriggers;
	}

	public Integer getRiskStatus() {
		return riskStatus;
	}

	public void setRiskStatus(Integer riskStatus) {
		this.riskStatus = riskStatus;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCopingStrategy() {
		return copingStrategy;
	}

	public void setCopingStrategy(String copingStrategy) {
		this.copingStrategy = copingStrategy;
	}

	public String getCopingStrategyRecord() {
		return copingStrategyRecord;
	}

	public void setCopingStrategyRecord(String copingStrategyRecord) {
		this.copingStrategyRecord = copingStrategyRecord;
	}

	public String getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(String responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	public String getRiskPriorityName() {
		return riskPriorityName;
	}

	public void setRiskPriorityName(String riskPriorityName) {
		this.riskPriorityName = riskPriorityName;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getRiskTypeName() {
		return riskTypeName;
	}

	public void setRiskTypeName(String riskTypeName) {
		this.riskTypeName = riskTypeName;
	}

	public String getRiskFactorName() {
		return riskFactorName;
	}

	public void setRiskFactorName(String riskFactorName) {
		this.riskFactorName = riskFactorName;
	}

	public String getRiskProbabilityName() {
		return riskProbabilityName;
	}

	public void setRiskProbabilityName(String riskProbabilityName) {
		this.riskProbabilityName = riskProbabilityName;
	}

	public Long getNumber() {
		return number;
	}

	public void setNumber(Long number) {
		this.number = number;
	}

	public Long getProgramId() {
		return programId;
	}

	public void setProgramId(Long programId) {
		this.programId = programId;
	}


}
