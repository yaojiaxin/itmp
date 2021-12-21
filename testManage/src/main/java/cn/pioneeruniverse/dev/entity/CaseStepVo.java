package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import cn.pioneeruniverse.dev.common.ExcelField;

public class CaseStepVo extends BaseEntity{
	private Integer stepOrder;	//步骤排序号
    private String stepDescription;  //步骤描述
    private String stepExpectedResult; //步骤预期结果

	@ExcelField(title="步骤", type=1, align=1, sort=10)
	public Integer getStepOrder() {
		return stepOrder;
	}
	public void setStepOrder(Integer stepOrder) {
		this.stepOrder = stepOrder;
	}

	@ExcelField(title="步骤描述", type=1, align=1, sort=11)
	public String getStepDescription() {
		return stepDescription;
	}
	public void setStepDescription(String stepDescription) {
		this.stepDescription = stepDescription;
	}

	@ExcelField(title="步骤预期结果", type=1, align=1, sort=12)
	public String getStepExpectedResult() {
		return stepExpectedResult;
	}
	public void setStepExpectedResult(String stepExpectedResult) {
		this.stepExpectedResult = stepExpectedResult;
	}
}
