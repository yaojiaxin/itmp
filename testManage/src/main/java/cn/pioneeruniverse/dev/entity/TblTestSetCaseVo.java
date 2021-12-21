package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import cn.pioneeruniverse.dev.common.ExcelField;

import java.util.List;

public class TblTestSetCaseVo extends BaseEntity{
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
    private String executeResult;
    //轮次
	private String excuteRound;

	private List<CaseStepVo> caseStep;

	@ExcelField(title="步骤", type=1, align=1, sort=10,isList = true)
	public List<CaseStepVo> getCaseStep() {
		return caseStep;
	}
	public void setCaseStep(List<CaseStepVo> caseStep) {
		this.caseStep = caseStep;
	}

	@ExcelField(title="执行结果", type=1, align=1, sort=9)
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
   	@ExcelField(title="案例名称", type=1, align=1, sort=2)
   	public String getCaseName() {
   		return caseName;
   	}

   	public void setCaseName(String caseName) {
   		this.caseName = caseName;
   	}
   	@ExcelField(title="案例编号", type=1, align=1, sort=1)
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
   	@ExcelField(title="前置条件", type=1, align=1, sort=3)
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
   	@ExcelField(title="测试轮次", type=1, align=1, sort=0)
	public String getExcuteRound() {
		return excuteRound;
	}

	public void setExcuteRound(String excuteRound) {
		this.excuteRound = excuteRound;
	}

	private String expectResult;	//预期结果
	private String inputData;		//输入数据
	private String testPoint;		//测试项
	private String moduleName;		//模块
	private String businessType;	//业务类型

	@ExcelField(title="预期结果", type=1, align=1, sort=8)
	public String getExpectResult() {
		return expectResult;
	}
	public void setExpectResult(String expectResult) {
		this.expectResult = expectResult;
	}

	@ExcelField(title="输入数据", type=1, align=1, sort=4)
	public String getInputData() {
		return inputData;
	}
	public void setInputData(String inputData) {
		this.inputData = inputData;
	}

	@ExcelField(title="测试项", type=1, align=1, sort=5)
	public String getTestPoint() {
		return testPoint;
	}
	public void setTestPoint(String testPoint) {
		this.testPoint = testPoint;
	}

	@ExcelField(title="模块", type=1, align=1, sort=6)
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	@ExcelField(title="业务类型", type=1, align=1, sort=7)
	public String getBusinessType() {
		return businessType;
	}
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
}
