package cn.pioneeruniverse.project.entity;

public class TblProjectPlanApproveUser {

    private	Long id;
    private	Long projectPlanApproveRequestId;
    private	Long userId;
    private	int approveLevel;
    private	int	approveStatus;
    private String approveSuggest;
    private	int	approveOnOff;

    private	Long projectId;
    private int operate;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectPlanApproveRequestId() {
        return projectPlanApproveRequestId;
    }
    public void setProjectPlanApproveRequestId(Long projectPlanApproveRequestId) {
        this.projectPlanApproveRequestId = projectPlanApproveRequestId;
    }

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getApproveLevel() {
        return approveLevel;
    }
    public void setApproveLevel(int approveLevel) {
        this.approveLevel = approveLevel;
    }

    public int getApproveStatus() {
        return approveStatus;
    }
    public void setApproveStatus(int approveStatus) {
        this.approveStatus = approveStatus;
    }

    public String getApproveSuggest() {
        return approveSuggest;
    }
    public void setApproveSuggest(String approveSuggest) {
        this.approveSuggest = approveSuggest;
    }

    public Long getProjectId() {
        return projectId;
    }
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public int getOperate() {
        return operate;
    }
    public void setOperate(int operate) {
        this.operate = operate;
    }

    public int getApproveOnOff() {
        return approveOnOff;
    }
    public void setApproveOnOff(int approveOnOff) {
        this.approveOnOff = approveOnOff;
    }
}
