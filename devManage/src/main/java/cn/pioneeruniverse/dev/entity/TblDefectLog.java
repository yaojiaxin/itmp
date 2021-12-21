package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.List;

/**
 * 缺陷日志实体类
 */
@TableName("tbl_defect_log")
public class TblDefectLog extends BaseEntity {

    @JsonSerialize(using = ToStringSerializer.class)
    private Long defectId;

    private String logType;

    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    private String userName;

    private String userAccount;

    private String logDetail;

    @TableField(exist = false)
    private List<TblDefectLogAttachement> logAttachementList;

    public Long getDefectId() {
        return defectId;
    }

    public void setDefectId(Long defectId) {
        this.defectId = defectId;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType == null ? null : logType.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount == null ? null : userAccount.trim();
    }

    public String getLogDetail() {
        return logDetail;
    }

    public void setLogDetail(String logDetail) {
        this.logDetail = logDetail == null ? null : logDetail.trim();
    }

    public List<TblDefectLogAttachement> getLogAttachementList() {
        return logAttachementList;
    }

    public void setLogAttachementList(List<TblDefectLogAttachement> logAttachementList) {
        this.logAttachementList = logAttachementList;
    }
}