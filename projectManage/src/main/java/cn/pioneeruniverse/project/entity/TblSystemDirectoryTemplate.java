package cn.pioneeruniverse.project.entity;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_system_directory_template")
public class TblSystemDirectoryTemplate extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	private Integer projectType;
	
	private String dirName;
	
	private Integer orderNumber;
	
	private Integer tierNumber;
	
	private Long parentId;
	
	private String parentIds;
	
	private String documentType2s;

	public Integer getProjectType() {
		return projectType;
	}

	public void setProjectType(Integer projectType) {
		this.projectType = projectType;
	}

	public String getDirName() {
		return dirName;
	}

	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Integer getTierNumber() {
		return tierNumber;
	}

	public void setTierNumber(Integer tierNumber) {
		this.tierNumber = tierNumber;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public String getParentIds() {
		return parentIds;
	}

	public void setParentIds(String parentIds) {
		this.parentIds = parentIds;
	}

	public String getDocumentType2s() {
		return documentType2s;
	}

	public void setDocumentType2s(String documentType2s) {
		this.documentType2s = documentType2s;
	}
	
	
}
