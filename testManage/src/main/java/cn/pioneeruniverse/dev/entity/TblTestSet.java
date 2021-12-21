package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;

public class TblTestSet extends BaseEntity{

    /**
	 * 
	 */
	private static final long serialVersionUID = 2055248377349260724L;

	private Long testTaskId;
    
    private String testSetName; 
    
    private String testSetNumber;

    private Integer excuteRound;

	public Long getTestTaskId() {
		return testTaskId;
	}

	public void setTestTaskId(Long testTaskId) {
		this.testTaskId = testTaskId;
	}

	public String getTestSetName() {
		return testSetName;
	}

	public void setTestSetName(String testSetName) {
		this.testSetName = testSetName;
	}

	public String getTestSetNumber() {
		return testSetNumber;
	}

	public void setTestSetNumber(String testSetNumber) {
		this.testSetNumber = testSetNumber;
	}

	public Integer getExcuteRound() {
		return excuteRound;
	}

	public void setExcuteRound(Integer excuteRound) {
		this.excuteRound = excuteRound;
	}

	
    
    
}