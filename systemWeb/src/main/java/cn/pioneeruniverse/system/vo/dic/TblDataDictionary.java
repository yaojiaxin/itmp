package cn.pioneeruniverse.system.vo.dic;

import cn.pioneeruniverse.system.vo.BaseVo;

public class TblDataDictionary extends BaseVo{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1782134754937933244L;
	
	private String termCode;
	
	private Integer valueCode;
	
    private String termName;
    
    private String valueName;
    
    

	private Integer valueSeq;

	public String getTermCode() {
		return termCode;
	}

	public void setTermCode(String termCode) {
		this.termCode = termCode;
	}

	public Integer getValueCode() {
		return valueCode;
	}

	public void setValueCode(Integer valueCode) {
		this.valueCode = valueCode;
	}

	public String getTermName() {
		return termName;
	}

	public void setTermName(String termName) {
		this.termName = termName;
	}

	public String getValueName() {
		return valueName;
	}

	public void setValueName(String valueName) {
		this.valueName = valueName;
	}

	public Integer getValueSeq() {
		return valueSeq;
	}

	public void setValueSeq(Integer valueSeq) {
		this.valueSeq = valueSeq;
	}
}
