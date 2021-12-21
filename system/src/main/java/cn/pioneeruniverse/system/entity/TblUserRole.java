package cn.pioneeruniverse.system.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_user_role")
public class TblUserRole  extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7593470309355719831L;

	private Long userId;
	
	private Long roleId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}
	
	
}
