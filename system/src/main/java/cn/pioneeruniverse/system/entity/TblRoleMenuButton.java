package cn.pioneeruniverse.system.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

@TableName("tbl_role_menu_button")
public class TblRoleMenuButton extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3371077281191777872L;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long roleId;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long menuButtonId;
	

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getMenuButtonId() {
		return menuButtonId;
	}

	public void setMenuButtonId(Long menuButtonId) {
		this.menuButtonId = menuButtonId;
	}

}
