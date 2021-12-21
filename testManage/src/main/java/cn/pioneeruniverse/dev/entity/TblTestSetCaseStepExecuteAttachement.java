package cn.pioneeruniverse.dev.entity;

import java.util.Date;

import cn.pioneeruniverse.common.entity.BaseEntity;

public class TblTestSetCaseStepExecuteAttachement extends BaseEntity{

    /**
	 * 
	 */
	private static final long serialVersionUID = 89433790705187290L;

	private Long testSetCaseStepExecuteId;

    private String fileNameNew;

    private String fileNameOld;

    private String fileType;

    private String filePath;

    private String fileS3Bucket;

    private String fileS3Key;

	public Long getTestSetCaseStepExecuteId() {
		return testSetCaseStepExecuteId;
	}

	public void setTestSetCaseStepExecuteId(Long testSetCaseStepExecuteId) {
		this.testSetCaseStepExecuteId = testSetCaseStepExecuteId;
	}

	public String getFileNameNew() {
		return fileNameNew;
	}

	public void setFileNameNew(String fileNameNew) {
		this.fileNameNew = fileNameNew;
	}

	public String getFileNameOld() {
		return fileNameOld;
	}

	public void setFileNameOld(String fileNameOld) {
		this.fileNameOld = fileNameOld;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileS3Bucket() {
		return fileS3Bucket;
	}

	public void setFileS3Bucket(String fileS3Bucket) {
		this.fileS3Bucket = fileS3Bucket;
	}

	public String getFileS3Key() {
		return fileS3Key;
	}

	public void setFileS3Key(String fileS3Key) {
		this.fileS3Key = fileS3Key;
	}

   
}