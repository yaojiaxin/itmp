package cn.pioneeruniverse.dev.entity;

import java.util.Date;

import cn.pioneeruniverse.common.entity.BaseEntity;

public class TblTestSetCaseStepExecute extends BaseEntity{


	private Integer testSetCaseExecuteId;

    private Integer stepOrder;

    private String stepDescription;

    private String stepExpectedResult;

    private String stepActualResult;
    
    private Integer stepExecuteResult;
	
	public Integer getTestSetCaseExecuteId() {
		return testSetCaseExecuteId;
	}

	public void setTestSetCaseExecuteId(Integer testSetCaseExecuteId) {
		this.testSetCaseExecuteId = testSetCaseExecuteId;
	}

	public Integer getStepOrder() {
		return stepOrder;
	}

	public void setStepOrder(Integer stepOrder) {
		this.stepOrder = stepOrder;
	}

	public String getStepDescription() {
		return stepDescription;
	}

	public void setStepDescription(String stepDescription) {
		this.stepDescription = stepDescription;
	}

	public String getStepExpectedResult() {
		return stepExpectedResult;
	}

	public void setStepExpectedResult(String stepExpectedResult) {
		this.stepExpectedResult = stepExpectedResult;
	}

	public String getStepActualResult() {
		return stepActualResult;
	}

	public void setStepActualResult(String stepActualResult) {
		this.stepActualResult = stepActualResult;
	}

	public Integer getStepExecuteResult() {
		return stepExecuteResult;
	}

	public void setStepExecuteResult(Integer stepExecuteResult) {
		this.stepExecuteResult = stepExecuteResult;
	}
    
    

}