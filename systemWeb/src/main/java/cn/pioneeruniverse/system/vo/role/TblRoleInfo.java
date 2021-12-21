package cn.pioneeruniverse.system.vo.role;

import cn.pioneeruniverse.system.vo.BaseVo;

public class TblRoleInfo  extends BaseVo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3636108394306466797L;

    private String roleName;
    
    private String roleCode;
    
    private String menuIds;

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public String getMenuIds() {
		return menuIds;
	}

	public void setMenuIds(String menuIds) {
		this.menuIds = menuIds;
	}

}
