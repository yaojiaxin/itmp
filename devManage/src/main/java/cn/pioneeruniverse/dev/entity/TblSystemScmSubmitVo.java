package cn.pioneeruniverse.dev.entity;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 16:09 2019/10/21
 * @Modified By:
 */
public class TblSystemScmSubmitVo implements Serializable {
    private static final long serialVersionUID = -2170766907615342811L;

    private List<TblSystemScmSubmit> tblSystemScmSubmitList;

    private List<TblSystemScmSubmitRegex> tblSystemScmSubmitRegexList;

    public List<TblSystemScmSubmit> getTblSystemScmSubmitList() {
        return tblSystemScmSubmitList;
    }

    public void setTblSystemScmSubmitList(List<TblSystemScmSubmit> tblSystemScmSubmitList) {
        this.tblSystemScmSubmitList = tblSystemScmSubmitList;
    }

    public List<TblSystemScmSubmitRegex> getTblSystemScmSubmitRegexList() {
        return tblSystemScmSubmitRegexList;
    }

    public void setTblSystemScmSubmitRegexList(List<TblSystemScmSubmitRegex> tblSystemScmSubmitRegexList) {
        this.tblSystemScmSubmitRegexList = tblSystemScmSubmitRegexList;
    }
}
