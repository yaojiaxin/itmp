package cn.pioneeruniverse.system.vo.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;

import cn.pioneeruniverse.system.vo.BaseVo;
import cn.pioneeruniverse.system.vo.role.TblRoleInfo;

public class TblMenuButtonInfo extends BaseVo{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6870185996775233451L;

	private Integer menuButtonType;
	private String menuButtonName;
	private Long parentId;
	private String parentIds;
	private String css;
	private String url;
	private Integer menuOrder;
	
	private List<TblMenuButtonInfo> childMenu;
	
	private List<TblRoleInfo> ownRoles;//拥有该菜单的所有权限

	public Integer getMenuButtonType() {
		return menuButtonType;
	}
	public void setMenuButtonType(Integer menuButtonType) {
		this.menuButtonType = menuButtonType;
	}
	public String getMenuButtonName() {
		return menuButtonName;
	}
	public void setMenuButtonName(String menuButtonName) {
		this.menuButtonName = menuButtonName;
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
	public String getCss() {
		return css;
	}
	public void setCss(String css) {
		this.css = css;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<TblMenuButtonInfo> getChildMenu() {
		return childMenu;
	}
	public void setChildMenu(List<TblMenuButtonInfo> childMenu) {
		this.childMenu = childMenu;
	}
	public List<TblRoleInfo> getOwnRoles() {
		return ownRoles;
	}
	public void setOwnRoles(List<TblRoleInfo> ownRoles) {
		this.ownRoles = ownRoles;
	}
	
	
	public Collection<ConfigAttribute> getRoleAuthorities() {
		Collection<ConfigAttribute> roleAuthorities = new ArrayList<ConfigAttribute>();
		ConfigAttribute configAttribute = null;
		for (TblRoleInfo role : this.ownRoles) {
			configAttribute = new SecurityConfig("ROLE_"+role.getRoleCode());
			roleAuthorities.add(configAttribute);
		}
		return roleAuthorities;
	}
	public Integer getMenuOrder() {
		return menuOrder;
	}
	public void setMenuOrder(Integer menuOrder) {
		this.menuOrder = menuOrder;
	}
}
