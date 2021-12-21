package cn.pioneeruniverse.system.vo.company;

import cn.pioneeruniverse.system.vo.BaseVo;

public class Company extends BaseVo{
	private Long id;
	private String companyName;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	

}
