package cn.pioneeruniverse.project.entity;

import org.springframework.data.annotation.Transient;

import com.baomidou.mybatisplus.annotations.TableName;
import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_requirement_system")
public class TblRequirementSystem  extends BaseEntity{
	
	private static final long serialVersionUID = 1L;
	
	private Long reqSystemId;    	//it全流程平台主键
	private Long requirementId;    	//需求表主键
    private Long systemId;			//系统id  
    private Long assetSystemTreeId; //模块
    private Long functionCount;		//功能点计数
    private Long mainSystemFlag;	//主系统标志 
    @Transient
    private String systemName; //系统名称
    @Transient
    private String assetSystemName; //模块名称
    @Transient
    private String systemCode; //系统code
       
	public Long getReqSystemId() {
		return reqSystemId;
	}
	public void setReqSystemId(Long reqSystemId) {
		this.reqSystemId = reqSystemId;
	}
	public Long getRequirementId() {
		return requirementId;
	}
	public void setRequirementId(Long requirementId) {
		this.requirementId = requirementId;
	}
	public Long getSystemId() {
		return systemId;
	}
	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}
	public Long getFunctionCount() {
		return functionCount;
	}
	public void setFunctionCount(Long functionCount) {
		this.functionCount = functionCount;
	}
	public Long getMainSystemFlag() {
		return mainSystemFlag;
	}
	public void setMainSystemFlag(Long mainSystemFlag) {
		this.mainSystemFlag = mainSystemFlag;
	}
	public Long getAssetSystemTreeId() {
		return assetSystemTreeId;
	}
	public void setAssetSystemTreeId(Long assetSystemTreeId) {
		this.assetSystemTreeId = assetSystemTreeId;
	}
	public String getSystemName() {
		return systemName;
	}
	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
	public String getAssetSystemName() {
		return assetSystemName;
	}
	public void setAssetSystemName(String assetSystemName) {
		this.assetSystemName = assetSystemName;
	}
	public String getSystemCode() {
		return systemCode;
	}
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
}