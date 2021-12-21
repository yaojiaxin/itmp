package cn.pioneeruniverse.common.dto;

import cn.pioneeruniverse.common.entity.BaseEntity;

/**
 * Description:
 * Author:liushan
 * Date: 2018/12/24 下午 7:11
 */
public class TblAttachementInfoDTO extends BaseEntity {

    private Long associatedId;// 关联外键表的id

    private String fileNameOld;

    private String fileType;

    private String filePath;

    private String fileS3Bucket;

    private String fileS3Key;

    public TblAttachementInfoDTO(){
        this.setFilePath("C:\\itmpFile\\");
    }

    public Long getAssociatedId() {
        return associatedId;
    }

    public void setAssociatedId(Long associatedId) {
        this.associatedId = associatedId;
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

    @Override
    public String toString() {
        return "TblAttachementInfoDTO{" +
                "associatedId=" + associatedId +
                ", fileNameOld='" + fileNameOld + '\'' +
                ", fileType='" + fileType + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileS3Bucket='" + fileS3Bucket + '\'' +
                ", fileS3Key='" + fileS3Key + '\'' +
                '}';
    }
}
