package cn.pioneeruniverse.common.dto;

import cn.pioneeruniverse.common.entity.BaseEntity;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description: 数据字典DTO类
 * @Date: Created in 16:30 2018/12/13
 * @Modified By:
 */
public class TblDataDicDTO extends BaseEntity {

    private static final long serialVersionUID = 1218997333070911386L;

    private String termName;
    private String termCode;
    private String valueName;
    private String valueCode;
    private Integer valueSeq;

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

    public String getValueCode() {
        return valueCode;
    }

    public void setValueCode(String valueCode) {
        this.valueCode = valueCode;
    }

    public Integer getValueSeq() {
        return valueSeq;
    }

    public void setValueSeq(Integer valueSeq) {
        this.valueSeq = valueSeq;
    }
}
