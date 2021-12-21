package cn.pioneeruniverse.dev.entity;


import cn.pioneeruniverse.common.gitlab.entity.Commit;
import cn.pioneeruniverse.common.gitlab.entity.Project;
import cn.pioneeruniverse.common.gitlab.entity.User;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 14:48 2019/7/16
 * @Modified By:
 */
public class GitLabPreReceiveHookParam implements Serializable {
    private static final long serialVersionUID = 9047234488501032052L;

    private String oldCommitId;
    private String nowCommitId;
    private String refName;
    private String commitMessage;
    private String committerEmail;
    private Integer projectId;
    private String ip;
    private String port;
    private String protocol;
    private Project relatedProject;
    private TblSystemScmSubmit relatedSystemScmSubmit;
    private TblToolInfo relatedToolInfo;//提交代码对应仓库关联的工具
    private TblDevTask relatedWorkTask;//提交代码所关联的工作任务
    private TblRequirementFeature relatedDevTask;//提交代码所关联的开发任务
    private User commitUser;

    public String getOldCommitId() {
        return oldCommitId;
    }

    public void setOldCommitId(String oldCommitId) {
        this.oldCommitId = oldCommitId;
    }

    public String getNowCommitId() {
        return nowCommitId;
    }

    public void setNowCommitId(String nowCommitId) {
        this.nowCommitId = nowCommitId;
    }

    public String getRefName() {
        return refName;
    }

    public void setRefName(String refName) {
        this.refName = refName.split("/", 3)[2];
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Project getRelatedProject() {
        return relatedProject;
    }

    public void setRelatedProject(Project relatedProject) {
        this.relatedProject = relatedProject;
    }

    public TblSystemScmSubmit getRelatedSystemScmSubmit() {
        return relatedSystemScmSubmit;
    }

    public void setRelatedSystemScmSubmit(TblSystemScmSubmit relatedSystemScmSubmit) {
        this.relatedSystemScmSubmit = relatedSystemScmSubmit;
    }

    public TblToolInfo getRelatedToolInfo() {
        return relatedToolInfo;
    }

    public void setRelatedToolInfo(TblToolInfo relatedToolInfo) {
        this.relatedToolInfo = relatedToolInfo;
    }

    public User getCommitUser() {
        return commitUser;
    }

    public void setCommitUser(User commitUser) {
        this.commitUser = commitUser;
    }

    public TblDevTask getRelatedWorkTask() {
        return relatedWorkTask;
    }

    public void setRelatedWorkTask(TblDevTask relatedWorkTask) {
        this.relatedWorkTask = relatedWorkTask;
    }

    public TblRequirementFeature getRelatedDevTask() {
        return relatedDevTask;
    }

    public void setRelatedDevTask(TblRequirementFeature relatedDevTask) {
        this.relatedDevTask = relatedDevTask;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    //TODO 脚本传递过来的message都并行在一行
    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public String getCommitterEmail() {
        return committerEmail;
    }

    public void setCommitterEmail(String committerEmail) {
        if (StringUtils.isNotEmpty(committerEmail)) {
            committerEmail = committerEmail.split(" ")[2];
            this.committerEmail = committerEmail.substring(1, committerEmail.length() - 1);
        }
    }

}
