package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 14:59 2019/9/2
 * @Modified By:
 */
@TableName("tbl_dev_task_scm_git_file")
public class TblDevTaskScmGitFile extends BaseEntity {

    private static final long serialVersionUID = 4684670363799936583L;

    private Long devTaskId;
    private Long devTaskScmId;
    private Long toolId;
    private Long gitRepositoryId;//项目id
    private String gitBranch;
    private String commitNumber;
    private String lastCommitNumber;
    private String commitFile;//TODO (文件夹weburl前缀/tree/分支名/；文件weburl前缀/blob/分支名)
    private String beforeRenameFile;
    private String operateType;

    @TableField(exist = false)
    private String fileContentCharset;

    public Long getDevTaskId() {
        return devTaskId;
    }

    public void setDevTaskId(Long devTaskId) {
        this.devTaskId = devTaskId;
    }

    public Long getDevTaskScmId() {
        return devTaskScmId;
    }

    public void setDevTaskScmId(Long devTaskScmId) {
        this.devTaskScmId = devTaskScmId;
    }

    public Long getToolId() {
        return toolId;
    }

    public void setToolId(Long toolId) {
        this.toolId = toolId;
    }

    public Long getGitRepositoryId() {
        return gitRepositoryId;
    }

    public void setGitRepositoryId(Long gitRepositoryId) {
        this.gitRepositoryId = gitRepositoryId;
    }

    public String getGitBranch() {
        return gitBranch;
    }

    public void setGitBranch(String gitBranch) {
        this.gitBranch = gitBranch;
    }

    public String getCommitNumber() {
        return commitNumber;
    }

    public void setCommitNumber(String commitNumber) {
        this.commitNumber = commitNumber;
    }

    public String getLastCommitNumber() {
        return lastCommitNumber;
    }

    public void setLastCommitNumber(String lastCommitNumber) {
        this.lastCommitNumber = lastCommitNumber;
    }

    public String getCommitFile() {
        return commitFile;
    }

    public void setCommitFile(String commitFile) {
        this.commitFile = commitFile;
    }

    public String getBeforeRenameFile() {
        return beforeRenameFile;
    }

    public void setBeforeRenameFile(String beforeRenameFile) {
        this.beforeRenameFile = beforeRenameFile;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getFileContentCharset() {
        return fileContentCharset;
    }

    public void setFileContentCharset(String fileContentCharset) {
        this.fileContentCharset = fileContentCharset;
    }
}
