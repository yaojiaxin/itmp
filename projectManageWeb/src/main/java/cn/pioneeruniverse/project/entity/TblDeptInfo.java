package cn.pioneeruniverse.project.entity;

import java.util.List;
import cn.pioneeruniverse.common.entity.BaseEntity;

public class TblDeptInfo extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2537302089144628111L;
	private Long id;
	private String deptName;
	private String deptNumber;
	private Long parentDeptId;
	private String parentDeptIds;
	private List<TblDeptInfo> childDept;
	
	public String getDeptName() {
		return deptName;
	}
	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}
	public String getDeptNumber() {
		return deptNumber;
	}
	public void setDeptNumber(String deptNumber) {
		this.deptNumber = deptNumber;
	}
	public Long getParentDeptId() {
		return parentDeptId;
	}
	public void setParentDeptId(Long parentDeptId) {
		this.parentDeptId = parentDeptId;
	}
	public String getParentDeptIds() {
		return parentDeptIds;
	}
	public void setParentDeptIds(String parentDeptIds) {
		this.parentDeptIds = parentDeptIds;
	}
	public List<TblDeptInfo> getChildDept() {
		return childDept;
	}
	public void setChildDept(List<TblDeptInfo> childDept) {
		this.childDept = childDept;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
