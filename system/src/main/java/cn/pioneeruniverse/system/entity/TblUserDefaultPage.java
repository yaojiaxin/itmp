package cn.pioneeruniverse.system.entity;

import org.springframework.data.annotation.Transient;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_user_default_page")
public class TblUserDefaultPage {

	private Long id;
	
	private Long userId;
	
	private Long menuButtonId;
	
	private Integer menuOrder;
	
	private Integer status;
	
	@Transient
	private String url;
	
	@Transient
	private String menuButtonName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getMenuButtonId() {
		return menuButtonId;
	}

	public void setMenuButtonId(Long menuButtonId) {
		this.menuButtonId = menuButtonId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getMenuOrder() {
		return menuOrder;
	}

	public void setMenuOrder(Integer menuOrder) {
		this.menuOrder = menuOrder;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMenuButtonName() {
		return menuButtonName;
	}

	public void setMenuButtonName(String menuButtonName) {
		this.menuButtonName = menuButtonName;
	}


	
	
	
	
}
