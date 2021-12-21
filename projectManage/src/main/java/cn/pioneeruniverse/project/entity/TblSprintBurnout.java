package cn.pioneeruniverse.project.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableName;

@TableName("tbl_sprint_burnout")
public class TblSprintBurnout {
    private Long id;

    private Double estimateRemainWorkload;

    private Long sprintId;

    private Date createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getEstimateRemainWorkload() {
        return estimateRemainWorkload;
    }

    public void setEstimateRemainWorkload(Double estimateRemainWorkload) {
        this.estimateRemainWorkload = estimateRemainWorkload;
    }

    public Long getSprintId() {
        return sprintId;
    }

    public void setSprintId(Long sprintId) {
        this.sprintId = sprintId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}