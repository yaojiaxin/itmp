package cn.pioneeruniverse.dev.entity;

import java.util.Date;

import org.springframework.data.annotation.Transient;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_sprint_info")
public class TblSprintInfo extends BaseEntity{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long systemId;
	
    private String sprintName;

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date sprintStartDate;
    
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    private Date sprintEndDate;
    
    private Integer validStatus;

    private Long projectPlanId;

    @Transient
    private String systemName;
    @Transient
    private String projectPlanName;
    @Transient
    private Long projectIds;    //项目id
    @Transient
    private String systemIdList;    //系统id项目集
    private String sprintIdList;    //冲刺Id结果集
    @Transient
    private String systemList;

    public Long getSystemId() {
        return systemId;
    }

    public void setSystemId(Long systemId) {
        this.systemId = systemId;
    }

    public String getSprintName() {
        return sprintName;
    }

    public void setSprintName(String sprintName) {
        this.sprintName = sprintName == null ? null : sprintName.trim();
    }

    public Date getSprintStartDate() {
        return sprintStartDate;
    }

    public void setSprintStartDate(Date sprintStartDate) {
        this.sprintStartDate = sprintStartDate;
    }

    public Date getSprintEndDate() {
        return sprintEndDate;
    }

    public void setSprintEndDate(Date sprintEndDate) {
        this.sprintEndDate = sprintEndDate;
    }
    
	public Integer getValidStatus() {
		return validStatus;
	}

	public void setValidStatus(Integer validStatus) {
		this.validStatus = validStatus;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

    public Long getProjectPlanId() {
        return projectPlanId;
    }
    public void setProjectPlanId(Long projectPlanId) {
        this.projectPlanId = projectPlanId;
    }

    public String getProjectPlanName() {
        return projectPlanName;
    }
    public void setProjectPlanName(String projectPlanName) {
        this.projectPlanName = projectPlanName;
    }

    public Long getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(Long projectIds) {
        this.projectIds = projectIds;
    }

    public String getSystemList() {
        return systemList;
    }

    public void setSystemList(String systemList) {
        this.systemList = systemList;
    }

    public String getSystemIdList() {
        return systemIdList;
    }

    public void setSystemIdList(String systemIdList) {
        this.systemIdList = systemIdList;
    }

    public String getSprintIdList() {
        return sprintIdList;
    }

    public void setSprintIdList(String sprintIdList) {
        this.sprintIdList = sprintIdList;
    }

    @Override
    public String toString() {
        return "TblSprintInfo{" +
                "systemId=" + systemId +
                ", sprintName='" + sprintName + '\'' +
                ", sprintStartDate=" + sprintStartDate +
                ", sprintEndDate=" + sprintEndDate +
                ", validStatus=" + validStatus +
                ", projectPlanId=" + projectPlanId +
                ", systemName='" + systemName + '\'' +
                ", projectPlanName='" + projectPlanName + '\'' +
                ", projectIds=" + projectIds +
                ", systemIdList='" + systemIdList + '\'' +
                ", sprintIdList='" + sprintIdList + '\'' +
                ", systemList='" + systemList + '\'' +
                '}';
    }
}