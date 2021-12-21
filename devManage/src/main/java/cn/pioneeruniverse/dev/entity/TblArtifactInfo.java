package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.sql.Timestamp;
import java.util.Date;

@TableName("tbl_artifact_info")
public class TblArtifactInfo extends BaseEntity{
	private static final long serialVersionUID = -8775645847477860159L;
	private Long systemId;
	private Long systemModuleId;
	private String repository;
	private String groupId;
	private String artifactId;
	private String version;
	private String nexusPath;
	private String remark;
	@TableField(exist = false)
	private String tags;
	public Long getSystemId() {
		return systemId;
	}
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	public Long getSystemModuleId() {
		return systemModuleId;
	}
	public void setSystemModuleId(Long systemModuleId) {
		this.systemModuleId = systemModuleId;
	}
	public String getRepository() {
		return repository;
	}
	public void setRepository(String repository) {
		this.repository = repository;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getArtifactId() {
		return artifactId;
	}
	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getNexusPath() {
		return nexusPath;
	}
	public void setNexusPath(String nexusPath) {
		this.nexusPath = nexusPath;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

    
}
