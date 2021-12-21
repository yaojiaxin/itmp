package cn.pioneeruniverse.project.entity;

import java.util.Date;

import org.springframework.data.annotation.Transient;
import org.springframework.format.annotation.DateTimeFormat;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_commissioning_window")
public class TblCommissioningWindow extends BaseEntity{

	private static final long serialVersionUID = 1L;
	
	private String windowName;
	
	@JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date windowDate;
	
	private Integer windowType;
	
	private String windowVersion;
	
	@TableField(exist = false)
	private String typeName;

	@Transient
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startDate;
	
	@Transient
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endDate;
	
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getWindowName() {
		return windowName;
	}

	public void setWindowName(String windowName) {
		this.windowName = windowName;
	}

	public Date getWindowDate() {
		return windowDate;
	}

	public void setWindowDate(Date windowDate) {
		this.windowDate = windowDate;
	}

	public Integer getWindowType() {
		return windowType;
	}

	public void setWindowType(Integer windowType) {
		this.windowType = windowType;
	}

	public String getWindowVersion() {
		return windowVersion;
	}

	public void setWindowVersion(String windowVersion) {
		this.windowVersion = windowVersion;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	@Override
	public String toString() {
		return "TblCommissioningWindow [windowName=" + windowName + ", windowDate=" + windowDate + ", windowType="
				+ windowType + ", windowVersion=" + windowVersion + ", typeName=" + typeName + "]";
	}
	
	
	
}
