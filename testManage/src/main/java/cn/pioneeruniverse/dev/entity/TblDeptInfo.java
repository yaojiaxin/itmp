package cn.pioneeruniverse.dev.entity;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_dept_info")
public class TblDeptInfo extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2537302089144628111L;
	private Long id;
	private String deptName;
	private String deptNumber;
	private String parentDeptNumber;
	private Long parentDeptId;
	private String parentDeptIds;
	
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
	public String getParentDeptNumber() {
		return parentDeptNumber;
	}
	public void setParentDeptNumber(String parentDeptNumber) {
		this.parentDeptNumber = parentDeptNumber;
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
}
