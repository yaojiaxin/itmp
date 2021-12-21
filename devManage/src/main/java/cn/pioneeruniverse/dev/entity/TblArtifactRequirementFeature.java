package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.util.Date;

@TableName("tbl_artifact_requirement_feature")
public class TblArtifactRequirementFeature extends BaseEntity{

	private static final long serialVersionUID = -2641141539727484292L;
	private Long artifactId;
	private Long requirementFeatureId;
	public Long getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(Long artifactId) {
		this.artifactId = artifactId;
	}
	public Long getRequirementFeatureId() {
		return requirementFeatureId;
	}
	public void setRequirementFeatureId(Long requirementFeatureId) {
		this.requirementFeatureId = requirementFeatureId;
	}

	
}
