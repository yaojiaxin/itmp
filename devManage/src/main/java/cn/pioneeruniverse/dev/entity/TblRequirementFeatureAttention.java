package cn.pioneeruniverse.dev.entity;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_requirement_feature_attention")
public class TblRequirementFeatureAttention extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private Long requirementFeatureId;

	private Long userId;

	public Long getRequirementFeatureId() {
		return requirementFeatureId;
	}

	public void setRequirementFeatureId(Long requirementFeatureId) {
		this.requirementFeatureId = requirementFeatureId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}