package cn.pioneeruniverse.project.common;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.project.vo.SynRequirementFeature;
import cn.pioneeruniverse.project.dao.mybatis.SystemInfoMapper;
import cn.pioneeruniverse.project.entity.TblRequirementFeature;
import cn.pioneeruniverse.project.service.dept.DeptInfoService;
import cn.pioneeruniverse.project.service.user.UserService;

@Component
public class SynRequirementFeatureUtil {
	
	@Autowired
	private UserService userService;
	@Autowired
	private DeptInfoService deptInfoService;	
	@Autowired
	private SystemInfoMapper systemInfoMapper;
	
	private static SynRequirementFeatureUtil synRequirementFeatureUtil;
	
	@PostConstruct  
	public void init() {       
		synRequirementFeatureUtil = this; 
		synRequirementFeatureUtil.userService = this.userService;
		synRequirementFeatureUtil.deptInfoService = this.deptInfoService;
		synRequirementFeatureUtil.systemInfoMapper = this.systemInfoMapper;
	}
	
	public static TblRequirementFeature SynRequirementFeatures(SynRequirementFeature srf) {
		TblRequirementFeature trf = new TblRequirementFeature();
		if(srf!=null) {
			trf.setTaskType(srf.getTaskType());
			trf.setFeatureName(srf.getTaskName());				
			trf.setFeatureCode(srf.getTbltaskId().toString());
			trf.setTaskId(srf.getTbltaskId());
			trf.setFeatureOverview(srf.getTaskDescription());
			trf.setSystemId(getSystemId(srf.getApplicationCode()));
			trf.setCreateType((long) 2);
			trf.setRequirementId(srf.getReqId());
			trf.setRequirementFeatureStatus(srf.getTaskStatus());
			trf.setManageUserId(getUserId(srf.getTaskMnger()));
			trf.setExecuteUserId(getUserId(srf.getTaskExer()));
			trf.setTemporaryStatus(getNumber(srf.getIsTasktemp()));
			trf.setDeptId(getDeptId(srf.getTaskDepartment()));
			trf.setHandleSuggestion(srf.getTaskResult());
			trf.setPlanStartDate(srf.getTaskPrestartdate());
			trf.setPlanEndDate(srf.getTaskPreenddate());
			if(srf.getTaskWorkReckon()!=null) {
				trf.setEstimateWorkload(srf.getTaskWorkReckon());
			}
			trf.setActualStartDate(srf.getTaskStartdate());
			trf.setActualEndDate(srf.getTaskEnddate());
			if(srf.getTaskWorkReckon()!=null) {
				trf.setActualWorkload(srf.getTaskWorkload());
			}
			trf.setStatus(1);
			//trf.setCreateBy(getUserId(srf.getOptCode()));
			trf.setCreateBy((long) 1);
			trf.setLastUpdateBy((long) 1);
			
			if(srf.getCreateDate()==null||srf.getCreateDate().toString().equals("")) {
				trf.setCreateDate(DateToTimestamp(new Date()));				
			}else {
				trf.setCreateDate(DateToTimestamp(srf.getCreateDate()));				
			}
			if(srf.getUpdateDate()!=null||srf.getUpdateDate().toString().equals("")) {
				trf.setLastUpdateDate(DateToTimestamp(new Date()));
			}else {				
				trf.setLastUpdateDate(DateToTimestamp(srf.getUpdateDate()));
			}
		}
		return trf;		
	}
	
	public static Long getUserId(String userCode) {
		 Long userId = synRequirementFeatureUtil.userService.findIdByUserAccount(userCode);
		 if(userId!=null&&userId.longValue()>0) {
			 return userId;
		 }else {
			 return null;
		 }
	}
	
	public static Long getDeptId(String deptCode) {
		 Long deptId = synRequirementFeatureUtil.deptInfoService.findIdByDeptNumber(deptCode);
		 if(deptId!=null&&deptId.longValue()>0) {
			 return deptId;
		 }else {
			 return null;
		 }
	}
	
	public static Long getSystemId(String systemCode) {
		Long systemId = synRequirementFeatureUtil.systemInfoMapper.findSystemIdBySystemCode(systemCode);
		 if(systemId!=null&&systemId.longValue()>0) {
			 return systemId;
		 }else {
			 return null;
		 }
	}
	public static Timestamp DateToTimestamp(Date date) {
		Timestamp ts = new Timestamp(System.currentTimeMillis());  
		String dateStr = "";  
        //format的格式可以任意           
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        try {  
            dateStr = sdf.format(date);  
            ts = Timestamp.valueOf(dateStr);
        } catch (Exception e) {  
            e.printStackTrace();  
        }
		return ts;  
	}
	
	public static Long getNumber(String taskId) {		
		Long id = null;
	    if(taskId!=null&&taskId.equals("00")) {
	    	id=(long) 2;
	    }else if(taskId!=null&&taskId.equals("01")) {
	    	id=(long) 1;
	    }
		return id;
	}
}
