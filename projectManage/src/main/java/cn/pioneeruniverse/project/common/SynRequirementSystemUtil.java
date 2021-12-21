package cn.pioneeruniverse.project.common;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import cn.pioneeruniverse.project.vo.SynRequirementSystem;
import cn.pioneeruniverse.project.dao.mybatis.SystemInfoMapper;
import cn.pioneeruniverse.project.entity.TblRequirementSystem;

@Component
public class SynRequirementSystemUtil {
			
	@Autowired
	private SystemInfoMapper systemInfoMapper;
	
	private static SynRequirementSystemUtil synReqSystemUtil;
	
	@PostConstruct  
	public void init() {       
		synReqSystemUtil = this; 
		synReqSystemUtil.systemInfoMapper=this.systemInfoMapper;
	}
	
	public static TblRequirementSystem SynTblRequirementSystem(SynRequirementSystem synReqSystem) {
		TblRequirementSystem trs = new TblRequirementSystem();
		if(synReqSystem!=null) {
			trs.setReqSystemId(synReqSystem.getReqsystemId());
			trs.setId(synReqSystem.getReqsystemId());
			trs.setRequirementId(synReqSystem.getReqId());
			trs.setSystemId(getSystemId(synReqSystem.getApplicationCode()));						
			trs.setFunctionCount(getFunctionCount(synReqSystem.getFunctionCount()));
			trs.setMainSystemFlag(getMainSystemFlag(synReqSystem.getIsMainsystem()));
		}
		return trs;		
	}
		
	public static Long getFunctionCount(String functionCount) {
		Long FunctionCount1=null;
		if(functionCount!= null&&!("").equals(functionCount)) {
			FunctionCount1=Long.valueOf(functionCount);
		}
		return FunctionCount1;
	}
	
	public static Long getMainSystemFlag(String isMainsystem) {
		Long mainSystemFlag=null;
		if(isMainsystem!= null&&!("").equals(isMainsystem)) {
			mainSystemFlag=Long.valueOf(isMainsystem);
		}
		return mainSystemFlag;
	}
	
	public static Long getSystemId(String systemCode) {
		Long systemId = synReqSystemUtil.systemInfoMapper.findSystemIdBySystemCode(systemCode);
		 if(systemId!=null&&systemId.longValue()>0) {
			 return systemId;
		 }else {
			 return null;
		 }
	}		
}
