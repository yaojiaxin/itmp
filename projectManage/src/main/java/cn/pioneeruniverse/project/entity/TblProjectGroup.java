package cn.pioneeruniverse.project.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotations.TableField;
import org.springframework.data.annotation.Transient;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_project_group")
public class TblProjectGroup extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	private Long projectId;
	
	private String projectGroupName;
	
	private Long parentId;
	
	private String parentIds;
	
	private Integer orderSeq;
	
	private Integer displaySeq;

	@TableField(exist=false)
	private List<User> list;  //小组人员集合
	@TableField(exist=false)
	private String projectName;
//	@Transient
//	private List<String> sublevelProjectGroupName;   //子项目小组
//
//	
//	public List<String> getSublevelProjectGroupName() {
//		return sublevelProjectGroupName;
//	}
//
//	public void setSublevelProjectGroupName(List<String> sublevelProjectGroupName) {
//		this.sublevelProjectGroupName = sublevelProjectGroupName;
//	}
	@Transient
	private Integer level;// 级别
	
	@Transient
    private Boolean isLeaf;// boolean 是否为叶子节点
    
	@Transient
    private Long parent;// 父节点
    
	@Transient
    private Boolean loaded;// boolean 是否加载完成
    
	@Transient
    private Boolean expanded;//boolean 表示此节点是否展开
	
	

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public Long getParent() {
		return parent;
	}

	public void setParent(Long parent) {
		this.parent = parent;
	}

	public Boolean getLoaded() {
		return loaded;
	}

	public void setLoaded(Boolean loaded) {
		this.loaded = loaded;
	}

	public Boolean getExpanded() {
		return expanded;
	}

	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}

	public List<User> getList() {
		return list;
	}

	public void setList(List<User> list) {
		this.list = list;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getProjectGroupName() {
		return projectGroupName;
	}

	public void setProjectGroupName(String projectGroupName) {
		this.projectGroupName = projectGroupName;
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

	public Integer getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(Integer orderSeq) {
		this.orderSeq = orderSeq;
	}

	public Integer getDisplaySeq() {
		return displaySeq;
	}

	public void setDisplaySeq(Integer displaySeq) {
		this.displaySeq = displaySeq;
	}

	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
