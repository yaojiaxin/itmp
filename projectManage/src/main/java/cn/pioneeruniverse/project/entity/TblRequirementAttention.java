package cn.pioneeruniverse.project.entity;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_requirement_attention")
public class TblRequirementAttention extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private Long requirementId;

	private Long userId;

	public Long getRequirementId() {
		return requirementId;
	}

	public void setRequirementId(Long requirementId) {
		this.requirementId = requirementId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}