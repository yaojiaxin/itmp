package cn.pioneeruniverse.common.databus;

import java.util.List;

public class SubDisposeDetail {
    private String subSystemCode;
    private List<DisposeDetail> subDisposeDetails;

    public String getSubSystemCode() {
        return subSystemCode;
    }
    public void setSubSystemCode(String subSystemCode) {
        this.subSystemCode = subSystemCode;
    }

    public List<DisposeDetail> getSubDisposeDetails() {
        return subDisposeDetails;
    }
    public void setSubDisposeDetails(List<DisposeDetail> subDisposeDetails) {
        this.subDisposeDetails = subDisposeDetails;
    }
}
