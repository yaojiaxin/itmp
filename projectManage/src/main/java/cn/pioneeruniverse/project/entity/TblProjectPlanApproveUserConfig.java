package cn.pioneeruniverse.project.entity;

public class TblProjectPlanApproveUserConfig {

    private	Long id;
    private	Long projectId;
    private	Long userId;
    private	int approveLevel;

    private	String userName;

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

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
}
