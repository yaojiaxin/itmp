package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 14:47 2019/6/12
 * @Modified By:
 */
@TableName("tbl_system_scm_submit")
public class TblSystemScmSubmit extends BaseEntity {

    private Long systemId;

    private Long systemScmRepositoryId;

    private String scmRepositoryName;

    private String scmUrl;

    private String scmBranch;

    private Integer submitStatus;

    private String submitSuperUserNames;

    private String submitUserNames;

    private Long systemVersionId;

    private Long commissioningWindowId;

    @TableField(exist = false)
    private String submitSuperUserRealNames;

    @TableField(exist = false)
    private String submitUserRealNames;

    @TableField(exist = false)
    private Set<String> submitSuperUserNamesCollection;

    @TableField(exist = false)
    private Set<String> submitUserNamesCollection;

    @TableField(exist = false)
    private String systemVersionName;//系统版本号名称

    @TableField(exist = false)
    private String commissioningWindowName;//投产窗口名称

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public Long getSystemScmRepositoryId() {
        return systemScmRepositoryId;
    }

    public void setSystemScmRepositoryId(Long systemScmRepositoryId) {
        this.systemScmRepositoryId = systemScmRepositoryId;
    }

    public String getScmRepositoryName() {
        return scmRepositoryName;
    }

    public void setScmRepositoryName(String scmRepositoryName) {
        this.scmRepositoryName = scmRepositoryName;
    }

    public String getScmUrl() {
        return scmUrl;
    }

    public void setScmUrl(String scmUrl) {
        this.scmUrl = scmUrl;
    }

    public String getScmBranch() {
        return scmBranch;
    }

    public void setScmBranch(String scmBranch) {
        this.scmBranch = scmBranch;
    }

    public Integer getSubmitStatus() {
        return submitStatus;
    }

    public void setSubmitStatus(Integer submitStatus) {
        this.submitStatus = submitStatus;
    }

    public String getSubmitSuperUserNames() {
        return submitSuperUserNames;
    }

    public void setSubmitSuperUserNames(String submitSuperUserNames) {
        this.submitSuperUserNames = submitSuperUserNames;
    }

    public String getSubmitUserNames() {
        return submitUserNames;
    }

    public void setSubmitUserNames(String submitUserNames) {
        this.submitUserNames = submitUserNames;
    }

    public Long getSystemVersionId() {
        return systemVersionId;
    }

    public void setSystemVersionId(Long systemVersionId) {
        this.systemVersionId = systemVersionId;
    }

    public Long getCommissioningWindowId() {
        return commissioningWindowId;
    }

    public void setCommissioningWindowId(Long commissioningWindowId) {
        this.commissioningWindowId = commissioningWindowId;
    }

    public String getSubmitSuperUserRealNames() {
        return submitSuperUserRealNames;
    }

    public void setSubmitSuperUserRealNames(String submitSuperUserRealNames) {
        this.submitSuperUserRealNames = submitSuperUserRealNames;
    }

    public String getSubmitUserRealNames() {
        return submitUserRealNames;
    }

    public void setSubmitUserRealNames(String submitUserRealNames) {
        this.submitUserRealNames = submitUserRealNames;
    }

    public Set<String> getSubmitSuperUserNamesCollection() {
        return submitSuperUserNamesCollection;
    }

    public void setSubmitSuperUserNamesCollection(String submitSuperUserNames) {
        Set<String> submitSuperUserNamesCollection = new HashSet<>();
        submitSuperUserNamesCollection.addAll(Arrays.asList(submitSuperUserNames.split(",|，")));
        this.submitSuperUserNamesCollection = submitSuperUserNamesCollection;
    }

    public Set<String> getSubmitUserNamesCollection() {
        return submitUserNamesCollection;
    }

    public void setSubmitUserNamesCollection(String submitUserNames) {
        Set<String> submitUserNamesCollection = new HashSet<>();
        submitUserNamesCollection.addAll(Arrays.asList(submitUserNames.split(",|，")));
        this.submitUserNamesCollection = submitUserNamesCollection;
    }

    public String getSystemVersionName() {
        return systemVersionName;
    }

    public void setSystemVersionName(String systemVersionName) {
        this.systemVersionName = systemVersionName;
    }

    public String getCommissioningWindowName() {
        return commissioningWindowName;
    }

    public void setCommissioningWindowName(String commissioningWindowName) {
        this.commissioningWindowName = commissioningWindowName;
    }
}
