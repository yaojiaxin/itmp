package cn.pioneeruniverse.dev.entity;

import java.sql.Timestamp;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableName;
import java.util.Date;

@TableName("tbl_artifact_tag")
public class TblArtifactTag extends BaseEntity{
	private static final long serialVersionUID = -3768946118855786989L;
	private Long artifactId;
	private Integer environmentType;
	private Long tagUserId;
	private Date tagTime;
	public Long getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(Long artifactId) {
		this.artifactId = artifactId;
	}
	public Integer getEnvironmentType() {
		return environmentType;
	}
	public void setEnvironmentType(Integer environmentType) {
		this.environmentType = environmentType;
	}
	public Long getTagUserId() {
		return tagUserId;
	}
	public void setTagUserId(Long tagUserId) {
		this.tagUserId = tagUserId;
	}
	public Date getTagTime() {
		return tagTime;
	}
	public void setTagTime(Date tagTime) {
		this.tagTime = tagTime;
	}

}
