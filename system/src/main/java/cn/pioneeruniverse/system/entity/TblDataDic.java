package cn.pioneeruniverse.system.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_data_dic")
public class TblDataDic extends BaseEntity implements Comparable<TblDataDic> {

    /**
     *
     */
    private static final long serialVersionUID = 4639097273797835613L;
    private String termName;  //1几名称
    private String termCode;  //2
    private String valueName;//i名称
    private String valueCode;  //code
    private Integer valueSeq;  //排序


    public String getTermName() {
        return termName;
    }

    public void setTermName(String termName) {
        this.termName = termName;
    }

    public String getTermCode() {
        return termCode;
    }

    public void setTermCode(String termCode) {
        this.termCode = termCode;
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

    public String getValueCode() {
        return valueCode;
    }

    public void setValueCode(String valueCode) {
        this.valueCode = valueCode;
    }

	@Override
	public int compareTo(TblDataDic o) {
		// TODO Auto-generated method stub
		return this.valueSeq - o.valueSeq;
	}


}
