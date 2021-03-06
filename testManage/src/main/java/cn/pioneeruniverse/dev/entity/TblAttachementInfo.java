package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_attachement_info")
public class TblAttachementInfo extends BaseEntity {
    private String fileNameNew;

    private String fileNameOld;

    private String fileType;

    private String filePath;

    private String saveMationIp;

    private String fromMationIp;

    public String getFileNameNew() {
        return fileNameNew;
    }

    public void setFileNameNew(String fileNameNew) {
        this.fileNameNew = fileNameNew == null ? null : fileNameNew.trim();
    }

    public String getFileNameOld() {
        return fileNameOld;
    }

    public void setFileNameOld(String fileNameOld) {
        this.fileNameOld = fileNameOld == null ? null : fileNameOld.trim();
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType == null ? null : fileType.trim();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath == null ? null : filePath.trim();
    }

    public String getSaveMationIp() {
        return saveMationIp;
    }

    public void setSaveMationIp(String saveMationIp) {
        this.saveMationIp = saveMationIp == null ? null : saveMationIp.trim();
    }

    public String getFromMationIp() {
        return fromMationIp;
    }

    public void setFromMationIp(String fromMationIp) {
        this.fromMationIp = fromMationIp == null ? null : fromMationIp.trim();
    }
}