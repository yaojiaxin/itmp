package cn.pioneeruniverse.dev.entity;

import java.util.Date;

import cn.pioneeruniverse.common.entity.BaseEntity;

public class TblTestSetCaseStep extends BaseEntity{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4746892603803783133L;

	private Long testSetCaseId;

    private Integer stepOrder;

    private String stepDescription;

    private String stepExpectedResult;

	public Long getTestSetCaseId() {
		return testSetCaseId;
	}

	public void setTestSetCaseId(Long testSetCaseId) {
		this.testSetCaseId = testSetCaseId;
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
    
    

}