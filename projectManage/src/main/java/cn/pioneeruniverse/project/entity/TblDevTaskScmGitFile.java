package cn.pioneeruniverse.project.entity;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_dev_task_scm_git_file")
public class TblDevTaskScmGitFile extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	private Long devTaskId;
	
	private Long devTaskScmId;
	
	private Long toolId;
	
	private Long gitRepositoryId;
	
	private String gitBranch;
	
	private String commitNumber;
	
	private String lastCommitNumber;
	
	private String commitFile;
	
	private String beforeRenameFile;
	
	private String operateType;

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
	
	
	
}
