package cn.pioneeruniverse.dev.entity;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import cn.pioneeruniverse.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

@TableName("tbl_commissioning_window")
public class TblCommissioningWindow extends BaseEntity {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonFormat(pattern = "yyyy-MM-dd", timezone="GMT+8")
	@DateTimeFormat(pattern="yyyy-MM-dd")
    private Date windowDate;

    private String windowName;

    private Byte windowType;

    private String windowVersion;
    
    @TableField(exist = false)
    private String featureStatus;	//开发任务状态

    public Date getWindowDate() {
        return windowDate;
    }

    public void setWindowDate(Date windowDate) {
        this.windowDate = windowDate;
    }

    public String getWindowName() {
        return windowName;
    }

    public void setWindowName(String windowName) {
        this.windowName = windowName == null ? null : windowName.trim();
    }

    public Byte getWindowType() {
        return windowType;
    }

    public void setWindowType(Byte windowType) {
        this.windowType = windowType;
    }

    public String getWindowVersion() {
        return windowVersion;
    }

    public void setWindowVersion(String windowVersion) {
        this.windowVersion = windowVersion == null ? null : windowVersion.trim();
    }

	public String getFeatureStatus() {
		return featureStatus;
	}

	public void setFeatureStatus(String featureStatus) {
		this.featureStatus = featureStatus;
	}
      
}