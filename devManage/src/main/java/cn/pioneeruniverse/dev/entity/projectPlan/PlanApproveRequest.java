package cn.pioneeruniverse.dev.entity.projectPlan;

import java.util.Date;

public class PlanApproveRequest {

    private	Long id;
    private	Long projectId;
    private	Long requestUserId;
    private	String requestReason;
    private	int	approveStatus;

    private	String	userName;
    private	String	projectName;

    private Date commitDate;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getRequestUserId() {
        return requestUserId;
    }
    public void setRequestUserId(Long requestUserId) {
        this.requestUserId = requestUserId;
    }

    public String getRequestReason() {
        return requestReason;
    }
    public void setRequestReason(String requestReason) {
        this.requestReason = requestReason;
    }

    public int getApproveStatus() {
        return approveStatus;
    }
    public void setApproveStatus(int approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Date getCommitDate() {
        return commitDate;
    }
    public void setCommitDate(Date commitDate) {
        this.commitDate = commitDate;
    }
}
