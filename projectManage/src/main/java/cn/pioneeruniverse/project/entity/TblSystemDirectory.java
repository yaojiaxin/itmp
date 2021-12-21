package cn.pioneeruniverse.project.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 10:30 2019/11/12
 * @Modified By:
 */
@TableName("tbl_system_directory")
public class TblSystemDirectory extends BaseEntity {

    private static final long serialVersionUID = -3326783192921906798L;

    private Long projectId;

    private Integer projectType;
    
    private String documentType;

    private String dirName;

    private Integer orderNumber;

    private Integer tierNumber;

    private Long parentId;

    private String parentIds;

    private Integer createType;

    @TableField(exist = false)
    private List<TblSystemDirectory> children;

    @TableField(exist = false)
    private String documentType2s;

    @TableField(exist = false)
    private Long systemId;

    @TableField(exist = false)
    private Long systemDirectoryTemplateId;

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

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

    public Integer getCreateType() {
        return createType;
    }

    public void setCreateType(Integer createType) {
        this.createType = createType;
    }

    public List<TblSystemDirectory> getChildren() {
        return children;
    }

    public void setChildren(List<TblSystemDirectory> children) {
        this.children = children;
    }

    public Long getSystemDirectoryTemplateId() {
        return systemDirectoryTemplateId;
    }

    public void setSystemDirectoryTemplateId(Long systemDirectoryTemplateId) {
        this.systemDirectoryTemplateId = systemDirectoryTemplateId;
    }

    public String getDocumentType2s() {
        return documentType2s;
    }

    public void setDocumentType2s(String documentType2s) {
        this.documentType2s = documentType2s;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}
    
}
