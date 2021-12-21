package cn.pioneeruniverse.dev.entity;

import com.baomidou.mybatisplus.annotations.TableField;

import cn.pioneeruniverse.common.bean.PropertyInfo;
import cn.pioneeruniverse.common.entity.BaseEntity;
import cn.pioneeruniverse.common.utils.ExcelField;

public class TblTestSetCase extends BaseEntity{

	private static final long serialVersionUID = 3960801397790687762L;

	private Long testSetId;
	private Long systemId;
	//案例名称	
    private String caseName;
    //案例编号
    private String caseNumber;
    //前置条件
    private String casePrecondition;
    //案例类型（数据字典：1：正面、2：负面）
    private Integer caseType;
    //执行结果
    private Integer caseExecuteResult;
    //执行结果
    @TableField(exist = false)
    private String executeResult;
    
	public String getExecuteResult() {
		return executeResult;
	}

	public void setExecuteResult(String executeResult) {
		this.executeResult = executeResult;
	}

	public Long getTestSetId() {
		return testSetId;
	}
	
	public void setTestSetId(Long testSetId) {
		this.testSetId = testSetId;
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

	public Integer getCaseType() {
		return caseType;
	}

	public void setCaseType(Integer caseType) {
		this.caseType = caseType;
	}
	public String getCasePrecondition() {
		return casePrecondition;
	}

	public void setCasePrecondition(String casePrecondition) {
		this.casePrecondition = casePrecondition;
	}

	public Integer getCaseExecuteResult() {
		return caseExecuteResult;
	}

	public void setCaseExecuteResult(Integer caseExecuteResult) {
		this.caseExecuteResult = caseExecuteResult;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}


	private String expectResult;	//预期结果
	private String inputData;		//输入数据
	private String testPoint;		//测试项
	private String moduleName;		//模块
	private String businessType;	//业务类型

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

}