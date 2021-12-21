package cn.pioneeruniverse.dev.entity;

import java.util.Date;

import cn.pioneeruniverse.common.entity.BaseEntity;

public class TblTestSetExcuteRoundUser extends BaseEntity{

    /**
	 * 
	 */
	private static final long serialVersionUID = 5715571860426103659L;

	private Long testSetId;

    private Integer excuteRound;

    private Long excuteUserId;

	public Long getTestSetId() {
		return testSetId;
	}

	public void setTestSetId(Long testSetId) {
		this.testSetId = testSetId;
	}

	public Integer getExcuteRound() {
		return excuteRound;
	}

	public void setExcuteRound(Integer excuteRound) {
		this.excuteRound = excuteRound;
	}

	public Long getExcuteUserId() {
		return excuteUserId;
	}

	public void setExcuteUserId(Long excuteUserId) {
		this.excuteUserId = excuteUserId;
	}

    
}