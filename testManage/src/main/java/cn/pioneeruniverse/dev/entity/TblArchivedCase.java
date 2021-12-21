package cn.pioneeruniverse.dev.entity;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.annotation.Transient;

import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_archived_case")
public class TblArchivedCase extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	private Integer systemId;
	
	private String caseName; //案例名称
	
	private String caseNumber; //案例编号
	
	private String casePrecondition; //前置条件
	
	private Integer caseType; //案例类型（1.正面，2.负面）

	@Transient
	private String userName;
	
	@Transient
	private String systemName; //系统名称
	
	@Transient
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
	private Timestamp createTime;//创建时间
	
	@Transient
	private String lastUpdateUser; //最后修改者
	
	@Transient
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", locale = "zh", timezone = "GMT+8")
	private Timestamp lastUpdateTime; //最后修改时间
	
	private String type;//类型

	@Transient
	private List<TblArchivedCaseStep> caseSteps;
	
	@Transient
	private Long[] uIds;
	
	@Transient
	private Long[] systemIds;

	public Integer getSystemId() {
		return systemId;
	}

	public void setSystemId(Integer systemId) {
		this.systemId = systemId;
	}

	public String getCaseName() {
		return caseName;
	}

	public void setCaseName(String caseName) {
		this.caseName = caseName;
	}

	public String getCaseNumber() {
		return caseNumber;
	}

	public void setCaseNumber(String caseNumber) {
		this.caseNumber = caseNumber;
	}

	public String getCasePrecondition() {
		return casePrecondition;
	}

	public void setCasePrecondition(String casePrecondition) {
		this.casePrecondition = casePrecondition;
	}

	public Integer getCaseType() {
		return caseType;
	}

	public void setCaseType(Integer caseType) {
		this.caseType = caseType;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getLastUpdateUser() {
		return lastUpdateUser;
	}

	public void setLastUpdateUser(String lastUpdateUser) {
		this.lastUpdateUser = lastUpdateUser;
	}

	public Timestamp getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Timestamp lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public List<TblArchivedCaseStep> getCaseSteps() {
		return caseSteps;
	}

	public void setCaseSteps(List<TblArchivedCaseStep> caseSteps) {
		this.caseSteps = caseSteps;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long[] getuIds() {
		return uIds;
	}

	public void setuIds(Long[] uIds) {
		this.uIds = uIds;
	}

	public Long[] getSystemIds() {
		return systemIds;
	}

	public void setSystemIds(Long[] systemIds) {
		this.systemIds = systemIds;
	}

	private String expectResult;	//预期结果
	private String inputData;		//输入数据
	private String testPoint;		//测试项
	private String moduleName;		//模块
	private String businessType;	//业务类型
	private String caseDescription; //案例描述

	public String getExpectResult() {
		return expectResult;
	}
	public void setExpectResult(String expectResult) {
		this.expectResult = expectResult;
	}

	public String getInputData() {
		return inputData;
	}
	public void setInputData(String inputData) {
		this.inputData = inputData;
	}

	public String getTestPoint() {
		return testPoint;
	}
	public void setTestPoint(String testPoint) {
		this.testPoint = testPoint;
	}

	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getCaseDescription() {
		return caseDescription;
	}

	public void setCaseDescription(String caseDescription) {
		this.caseDescription = caseDescription;
	}
	
}
