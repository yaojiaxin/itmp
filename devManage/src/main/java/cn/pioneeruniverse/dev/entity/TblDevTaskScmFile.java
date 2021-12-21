package cn.pioneeruniverse.dev.entity;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 15:10 2019/3/8
 * @Modified By:
 */
@TableName("tbl_dev_task_scm_file")
public class TblDevTaskScmFile extends BaseEntity {

    private static final long serialVersionUID = -7247498989696700169L;

    private Long devTaskId;
    private Long devTaskScmId;
    private String scmUrl;
    private String commitNumber;
    private String lastCommitNumber;
    private String commitFile;
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

    public String getScmUrl() {
        return scmUrl;
    }

    public void setScmUrl(String scmUrl) {
        this.scmUrl = scmUrl;
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
