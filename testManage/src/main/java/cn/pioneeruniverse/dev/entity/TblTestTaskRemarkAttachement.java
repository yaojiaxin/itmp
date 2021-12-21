package cn.pioneeruniverse.dev.entity;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;
@TableName("tbl_test_task_remark_attachement")
public class TblTestTaskRemarkAttachement extends BaseEntity{
	private Long  testTaskRemarkId;
	private String fileNameNew;
	private String fileNameOld;
	private String fileType;
	private String filePath;
	private String fileS3Bucket;
	private String fileS3Key;
	public Long getTestTaskRemarkId() {
		return testTaskRemarkId;
	}
	public void setTestTaskRemarkId(Long testTaskRemarkId) {
		this.testTaskRemarkId = testTaskRemarkId;
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
