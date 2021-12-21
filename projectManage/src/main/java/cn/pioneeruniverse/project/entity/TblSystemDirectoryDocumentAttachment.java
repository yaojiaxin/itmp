package cn.pioneeruniverse.project.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;

import java.util.Date;

public class TblSystemDirectoryDocumentAttachment extends BaseEntity {


    private Long systemDirectoryDocumentId;

    private String attachmentNameNew;

    private String attachmentNameOld;

    private Integer attachmentType;

    private String attachmentS3Bucket;

    private String attachmentS3Key;

    private String attachmentImageUrl;



    public Long getSystemDirectoryDocumentId() {
        return systemDirectoryDocumentId;
    }

    public void setSystemDirectoryDocumentId(Long systemDirectoryDocumentId) {
        this.systemDirectoryDocumentId = systemDirectoryDocumentId;
    }

    public String getAttachmentNameNew() {
        return attachmentNameNew;
    }

    public void setAttachmentNameNew(String attachmentNameNew) {
        this.attachmentNameNew = attachmentNameNew == null ? null : attachmentNameNew.trim();
    }

    public String getAttachmentNameOld() {
        return attachmentNameOld;
    }

    public void setAttachmentNameOld(String attachmentNameOld) {
        this.attachmentNameOld = attachmentNameOld == null ? null : attachmentNameOld.trim();
    }

    public String getAttachmentS3Bucket() {
        return attachmentS3Bucket;
    }

    public void setAttachmentS3Bucket(String attachmentS3Bucket) {
        this.attachmentS3Bucket = attachmentS3Bucket == null ? null : attachmentS3Bucket.trim();
    }

    public String getAttachmentS3Key() {
        return attachmentS3Key;
    }

    public void setAttachmentS3Key(String attachmentS3Key) {
        this.attachmentS3Key = attachmentS3Key == null ? null : attachmentS3Key.trim();
    }

    public String getAttachmentImageUrl() {
        return attachmentImageUrl;
    }

    public void setAttachmentImageUrl(String attachmentImageUrl) {
        this.attachmentImageUrl = attachmentImageUrl == null ? null : attachmentImageUrl.trim();
    }


    public Integer getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(Integer attachmentType) {
        this.attachmentType = attachmentType;
    }
}