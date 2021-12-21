package cn.pioneeruniverse.dev.service.workTask.impl;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;

import cn.pioneeruniverse.common.bean.PropertyInfo;
import cn.pioneeruniverse.common.bean.ReflectFieledType;
import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.CommonUtils;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.common.utils.PageWithBootStrap;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.dao.mybatis.AllDevRequirementMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblDefectInfoMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblProjectGroupUserMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblRequirementFeatureLogMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblRequirementFeatureMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblRequirementInfoMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblSystemInfoMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestSetCaseExecuteMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestSetCaseMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestTaskAttachementMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestTaskLogAttachementMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestTaskLogMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblTestTaskMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblUserInfoMapper;
import cn.pioneeruniverse.dev.entity.TblDataDic;
import cn.pioneeruniverse.dev.entity.TblDefectInfo;
import cn.pioneeruniverse.dev.entity.TblDefectLog;
import cn.pioneeruniverse.dev.entity.TblRequirementFeature;
import cn.pioneeruniverse.dev.entity.TblRequirementFeatureLog;
import cn.pioneeruniverse.dev.entity.TblRequirementInfo;
import cn.pioneeruniverse.dev.entity.TblSystemInfo;
import cn.pioneeruniverse.dev.entity.TblTestTask;
import cn.pioneeruniverse.dev.entity.TblTestTaskAttachement;
import cn.pioneeruniverse.dev.entity.TblTestTaskLog;
import cn.pioneeruniverse.dev.entity.TblTestTaskLogAttachement;
import cn.pioneeruniverse.dev.entity.TblUserInfo;
import cn.pioneeruniverse.dev.service.defect.DefectService;
import cn.pioneeruniverse.dev.service.workTask.workTaskService;
import cn.pioneeruniverse.dev.vo.DevDetailVo;
@Service("workTaskService")
public class WorkTaskServiceImpl implements workTaskService {
	public static final String SOURCE = "TBL_REQUIREMENT_INFO_REQUIREMENT_SOURCE";
	 
	 public static final String TYPE = "TBL_REQUIREMENT_INFO_REQUIREMENT_TYPE";  
	    
	 public static final String PRIORITY = "TBL_REQUIREMENT_INFO_REQUIREMENT_PRIORITY";  
	 public static final String RETYPE= "TBL_REQUIREMENT_INFO_REQUIREMENT_TYPE";
	 public static final String PLAN = "TBL_REQUIREMENT_INFO_REQUIREMENT_PLAN";
	 public static final String STATUS = "TBL_REQUIREMENT_INFO_REQUIREMENT_STATUS"; 
	 //存放需要添加日志的字段类型
	  private final List<String> listType =ReflectFieledType.listType;
	
		@Autowired
	    private TblTestTaskMapper tblTestTaskMapper;
		@Autowired
	    private RedisUtils redisUtils;
		@Autowired
		private TblRequirementInfoMapper requirementInfoMapper;
		@Autowired
	    private TblSystemInfoMapper SystemInfoMapper;
		@Autowired
		private AllDevRequirementMapper allDevRequirementMapper;
		@Autowired
		private TblTestTaskLogMapper tblTestTaskLogMapper;
		@Autowired
		private TblTestTaskLogAttachementMapper tblTestTaskLogAttachementMapper;
		@Autowired
		private TblTestTaskAttachementMapper tblTestTaskAttachementMapper;
		@Autowired
		private  TblRequirementFeatureMapper tblRequirementFeatureMapper;
		@Autowired
		private TblUserInfoMapper tblUserInfoMapper; 
		@Autowired
		private TblDefectInfoMapper tblDefectInfoMapper;
		@Autowired
		private TblTestSetCaseMapper tblTestSetCaseMapper;
		@Autowired
		private DefectService defectService;
		@Autowired
		private TblTestSetCaseExecuteMapper tblTestSetCaseExecuteMapper;
		@Autowired
		private TblProjectGroupUserMapper tblProjectGroupUserMapper;
		@Autowired
		private TblRequirementFeatureLogMapper requirementFeatureLogMapper;
		@Override
		@Transactional(readOnly=true)
		public Map getTestTask(TblTestTask tblTestTask,String filters, Integer page, Integer rows,HttpServletRequest request) {
			 List<TblTestTask> list =  new ArrayList<>();
		        Map<String, Object> result = new HashMap<String, Object>();
		        Map<String, Object> parm = new HashMap<String, Object>();
		        Map<String, Object> filterMap = new HashMap<String, Object>();
		        List<Integer> Array=new ArrayList();
		        if (page != null && rows != null) {
		        	if(filters != null&&!filters.equals("")) {
		        		 Map<String, Object> filter = JSON.parseObject(filters, Map.class);
						List<Map<String, Object>> ruleList = JSON.parseObject(filter.get("rules").toString(), List.class);
						for (Map<String, Object> map2 : ruleList) {
							String key = map2.get("field").toString();
							String value = map2.get("data").toString().trim();
							if(key.equals("testTaskStatus")) {
								value=value.substring(1);
							}
							if(key.equals("testStage")&&value.equals("0")) {
								value = "";
							}
							filterMap.put(key, value);
						}
					}
		            PageHelper.startPage(page, rows);
		            if(tblTestTask.getTestTaskStatus()!=null) {
		            	String devStatus=tblTestTask.getTestTaskStatus().toString();
		                if(devStatus.length()==1) {
		                	Array.add(Integer.parseInt(devStatus));
		                	tblTestTask.setTestStatusList(Array);
		                }else {
		                	for(int i=0;i<devStatus.length();i++) {
		                		String status=devStatus.substring(i,i+1);
		                		Array.add(Integer.parseInt(status));
		                	}
		                	tblTestTask.setTestStatusList(Array);
		                }
		            }
		            LinkedHashMap map = (LinkedHashMap) redisUtils.get(CommonUtil.getToken(request));
		    		List<String> roleCodes = (List<String>) map.get("roles"); 
		            parm.put("test", tblTestTask);
		            parm.put("filterMap", filterMap);
		            Boolean flag = new CommonUtils().currentUserWithAdmin(request);
		            if (flag) {
		                list = tblTestTaskMapper.getTestTaskAll(parm);
		            }else {
		            	 list = tblTestTaskMapper.getTestTask(parm);
		            }
		            for (TblTestTask testTask : list) {
//		            	Long caseNum = tblTestSetCaseMapper.findCaseCount(testTask.getId());
		            	Long caseNum = tblTestTaskMapper.getCaseNum(testTask.getId());
		            	testTask.setCaseNum(caseNum);
		            	List<Long> uIds = tblProjectGroupUserMapper.findUserIdBySystemId(testTask.getSystemId());
		            	testTask.settUserIds(uIds);
					}
		           
		            PageInfo<TblTestTask> pageInfo = new PageInfo<TblTestTask>(list);
			            result.put("rows", list);
			          
			            result.put("records", pageInfo.getTotal());
			            result.put("total", pageInfo.getPages());
			            result.put("page", page < pageInfo.getPages()?page : pageInfo.getPages());
		            return result;
		        }else {
		        	parm.put("test", tblTestTask);
		        	result.put("rows", tblTestTaskMapper.getTestTask(parm));
				        return result;	
		        }
		      
		}
		
		
		@Override
		@Transactional(readOnly=true)
		public Map<String, Object> getTestTask2(TblTestTask TblTestTask, Integer page, Integer rows) {
			Map<String, Object> result = new HashMap<String, Object>();
			PageHelper.startPage(page, rows);
			List<TblTestTask> list = tblTestTaskMapper.selectTestTaskByCon(TblTestTask);
			PageInfo<TblTestTask> pageInfo = new PageInfo<TblTestTask>(list);
			Map<String,Object> statusMap = JsonUtil.fromJson((String) redisUtils.get("TBL_TEST_TASK_TEST_TASK_STATUS"), Map.class);
        	for (TblTestTask testTask : list) {
        		String status = (String)statusMap.get(testTask.getTestTaskStatus().toString());
				testTask.setWorkTaskStatus(status);
			}
        	result.put("rows", list);
        	result.put("total", pageInfo.getTotal());
        	return result;
		}
		
		@Override
		@Transactional(readOnly=true)
		public List<Map<String,Object>> getAllFeature(TblTestTask TblTestTask ,Long userdId,Integer pageNumber, Integer pageSize,HttpServletRequest request) {
			 Map<String, Object> map = new HashMap<>();
			 map = PageWithBootStrap.setPageNumberSize(map,pageNumber,pageSize);
			 map.put("TblTestTask", TblTestTask);
			 map.put("userId", userdId);
			 Boolean flag = new CommonUtils().currentUserWithAdmin(request);
			 if (flag) {//当前登录用户有角色是系统管理员
				 return tblRequirementFeatureMapper.getAllRequirementFeature(map);
				}else {
				return	 tblRequirementFeatureMapper.getRequirementFeature(map);
				}
		}

		@Override
		@Transactional(readOnly=true)
		public List<TblTestTask> getAllTestUser(Long testUserId) {
			 List<TblTestTask> list = tblTestTaskMapper.getAllTestUser(testUserId);
		        return list;
		}

		 @Override
		 @Transactional(readOnly=true)
		 public Map getAllRequirt(TblRequirementInfo RequirementInfo,Integer page,Integer rows)  {
			 List<TblRequirementInfo> list =  new ArrayList<>();
			 Map<String, Object> result = new HashMap<String, Object>();
			 List<String> Array=new ArrayList();
			 if (page != null && rows != null) {
				 PageHelper.startPage(page, rows);
				 if(RequirementInfo.getRequirementType()!=null) {
					 String RequirementType=RequirementInfo.getRequirementType();
		               
					 Array= Arrays.asList(RequirementType.split(","));
					 RequirementInfo.setRequirementTypeList(Array);
				 }
				 list = requirementInfoMapper.getRequirement(RequirementInfo);
		            	
				 Object object = redisUtils.get(RETYPE);
				 Map<String, Object> mapsource= new HashMap<String, Object>();
				 if (object != null &&!"".equals( object)) {//redis有直接从redis中取
					 mapsource = JSON.parseObject(object.toString());
				 }
				 if(list.size()>0) {
					 for(int i=0;i<list.size(); i++) {
						 for(String key:mapsource.keySet()) {
							 if(key.equals(list.get(i).getRequirementType())) {
								 list.get(i).setRequirementType(mapsource.get(key).toString());
							 }
						 }
					 }
				 }
		    		
		    		
				 PageInfo<TblRequirementInfo> pageInfo = new PageInfo<TblRequirementInfo>(list);
				 result.put("rows", list);
				 result.put("records", pageInfo.getTotal());
				 result.put("total", pageInfo.getPages());
				 result.put("page", page < pageInfo.getPages()?page : pageInfo.getPages());
				 return result;
			 }else {
				 result.put("rows",  requirementInfoMapper.getRequirement(RequirementInfo));
			 }
			 return result;
		 }

		@Override
		@Transactional(readOnly=true)
	    public Map getAllsystem(TblSystemInfo systemInfo,Integer page,Integer rows) {
	    	  List<TblSystemInfo> list=new ArrayList();
	    	  List<String> Array=new ArrayList();
	    	  Map<String, Object> result = new HashMap<String, Object>();
	    	if (page != null && rows != null) {
	    		
	            PageHelper.startPage(page, rows);
	            if(systemInfo.getSystemType()!=null) {
	            	String SystemType=systemInfo.getSystemType();
	               
	                		Array= Arrays.asList(SystemType.split(","));
	                		systemInfo.setSystemTypeList(Array);
	            }
	            list = SystemInfoMapper.getAllsystem(systemInfo);
	            PageInfo<TblSystemInfo> pageInfo = new PageInfo<TblSystemInfo>(list);
	            
	            Object object = redisUtils.get("TBL_SYSTEM_INFO_SYSTEM_TYPE");
				 Map<String, Object> mapsource=new HashMap<String, Object>();
				 if (object != null &&!"".equals( object)) {//redis有直接从redis中取
					 mapsource = JSON.parseObject(object.toString());
				 }
				 if(list.size()>0) {
					 for(int i=0;i<list.size(); i++) {
						 for(String key:mapsource.keySet()) {
							 if(key.equals(list.get(i).getSystemType())) {
								 list.get(i).setSystemType(mapsource.get(key).toString());
							 }
						 }
					 }
				 }
	            result.put("rows", list);
	            result.put("records", pageInfo.getTotal());
	            result.put("total", pageInfo.getPages());
	            result.put("page", page < pageInfo.getPages()?page : pageInfo.getPages());
	            return result;
	        }else {
	        	result.put("rows",  SystemInfoMapper.getAllsystem(systemInfo));
	        }
	    	
	    	  
	        return result;
	    }

		@Override
		@Transactional(readOnly=true)
		public List<TblTestTask> getUserName() {
			  List<TblTestTask> list = tblTestTaskMapper.getUseName();
		        return list;
		}

		@Override
		@Transactional(readOnly=true)
		public Map<String, Object> getSeeDetail(String id) {
			Map<String,Object> result = new HashMap<String,Object>();
  			DevDetailVo dev=new DevDetailVo();
  			Long devID=Long.parseLong(id);
  			dev= allDevRequirementMapper.AlldevReuirement(devID);
  			dev.setDevuserName(allDevRequirementMapper.getdevName(dev.getDevuserID()));
  			dev.setCreateName(allDevRequirementMapper.getdevName(dev.getCreateBy()));
  			dev.setManageUserName(allDevRequirementMapper.getdevName(dev.getManageUserId()));
  			dev.setSystemName(allDevRequirementMapper.getSystemName(dev.getSystemId()));
  			dev.setExecuteUserName(allDevRequirementMapper.getdevName(dev.getExecuteUserId()));
  			dev.setApplyUserName(allDevRequirementMapper.getdevName(dev.getApplyUserId()));
  			dev.setApplyDeptName(allDevRequirementMapper.getdeptName(dev.getApplyDeptId()));
  			Object source = redisUtils.get(SOURCE);
  			Object type=redisUtils.get(TYPE);
  			Object priority=redisUtils.get(PRIORITY);
  			Object plan=redisUtils.get(PLAN);
  			Object status=redisUtils.get(STATUS);
  			Object testStage=redisUtils.get("TBL_TEST_TASK_TEST_STAGE");//测试阶段
  			Object devStatus = redisUtils.get("TBL_TEST_TASK_TEST_TASK_STATUS");//工作任务状态
  			Map<String, Object> maptestStage = JSON.parseObject(testStage.toString());//测试阶段
  			Map<String, Object> mapdevStatus = JSON.parseObject(devStatus.toString());
  			Map<String, Object> mapsource = JSON.parseObject(source.toString());
  			Map<String, Object> maptype = JSON.parseObject(type.toString());
  			Map<String, Object> mappriority = JSON.parseObject(priority.toString());
  			Map<String, Object>  mapplan= JSON.parseObject(plan.toString());
  			Map<String, Object>  mapstatus= JSON.parseObject(status.toString());
  			for (String key : mapsource.keySet()) {
  	        	String reqiureSorce=dev.getRequirementSource();
  	        	if(reqiureSorce!=null) {
  	            if (key.equals(reqiureSorce.toUpperCase(Locale.ENGLISH))) {
  	                dev.setRequirementSource(mapsource.get(key).toString());
  	            }
  	        	}
  	        }  
  		  for (String key : maptype.keySet()) {
          	if(dev.getRequirementType()!=null) {
          		if (key.equals(dev.getRequirementType().toUpperCase(Locale.ENGLISH))) {
                      dev.setRequirementType(maptype.get(key).toString());
                  }
          	}
              
          }
  		 for (String key : mappriority.keySet()) {
         	if(dev.getRequirementPriority()!=null) {
         		if (key.equals(dev.getRequirementPriority().toUpperCase(Locale.ENGLISH))) {
                     dev.setRequirementPriority(mappriority.get(key).toString());
                 }
         	}
             
         }
  		  for (String key : mapplan.keySet()) {
          	if(dev.getRequirementPanl()!=null) {
          		 if (key.equals(dev.getRequirementPanl().toUpperCase(Locale.ENGLISH))) {
                       dev.setRequirementPanl(mapplan.get(key).toString());
                   }
          	}
             
          }
  		for (String key : mapstatus.keySet()) {
        	if(dev.getRequirementStatus()!=null) {
        		 if (key.equals(dev.getRequirementStatus().toUpperCase(Locale.ENGLISH))) {
                     dev.setRequirementStatus(mapstatus.get(key).toString());
                 }
        	}
           
        }
  		 for (String key : mapdevStatus.keySet()) {
             if (key.equals(dev.getDevTaskStatus().toString().toUpperCase(Locale.ENGLISH))) {
                 dev.setTestDevTaskStatus(mapdevStatus.get(key).toString());
             }
         }
  		 for (String key : maptestStage.keySet()) {
			 if(dev.getTestStage()!=null){
             if (key.equals(dev.getTestStage().toUpperCase(Locale.ENGLISH))) {
                 dev.setTestStage(maptestStage.get(key).toString());
             }
			 }
         }
  		 
  		 	//解析关联任务状态
  		 	String status2 = dev.getRequirementFeatureStatus();
  		 	if(status2 != null) {
	  		 	String Str = redisUtils.get("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS_2").toString();
	  		 	JSONObject object = JSONObject.parseObject(Str);
	  		 	String statusName = object.get(status2).toString();
	  		 	dev.setRequirementFeatureStatusName(statusName);
  		 	}
  		 	
  		 	List<String> users = tblTestSetCaseExecuteMapper.findExecuteUser(devID);
  		 	dev.setExecuteUser(users);
  			dev.setId(Long.parseLong(id));
  			
  			TblRequirementFeature requirementFeature = tblRequirementFeatureMapper.findRequirement(devID);
  			String string = null;
  			if(requirementFeature.getRequirementFeatureSource() != null) {
  			String str = redisUtils.get("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_SOURCE_2").toString();
  			JSONObject jsonObject = JSON.parseObject(str);
  			string = jsonObject.get(requirementFeature.getRequirementFeatureSource().toString()).toString();
  			}
  			dev.setRequirementFeatureSource(string);
  			dev.setPptDeployTime(requirementFeature.getPptDeployTime());
  			dev.setRequirementChangeNumber(requirementFeature.getRequirementChangeNumber());
  			dev.setImportantRequirementType(requirementFeature.getImportantRequirementType());
  			dev.setSubmitTestTime(requirementFeature.getSubmitTestTime());
  			
  			Long num = tblTestTaskMapper.getDefectNum(devID);
  			dev.setDefectNum(num);
  			
  			Long featureId = tblTestTaskMapper.getFeatureIdByTaskId(devID);
  			dev.setFeatureId(featureId);
  			result.put("dev",dev);
  			return result;
		}
		//获取编辑信息
		@Override
		public Map<String, Object> getEditTestTask(String id) {
	  		List<TblTestTask> list=new ArrayList<>();
	  		Map<String,Object> result = new HashMap<String,Object>();
	  		Long devid=Long.parseLong(id);
	  		list=tblTestTaskMapper.getEditTestTask(devid);
	  		TblRequirementFeature tblRequirementFeature=tblRequirementFeatureMapper.getPlanDate(devid);
	  		result.put("tblRequirementFeature",tblRequirementFeature);
	  		result.put("dev",list);
	  		return result;
		}
		//添加任务信息
		@Transactional(readOnly=false)
		@Override
		public void addtestTask(String obj,String attachFiles,Long Userid,HttpServletRequest request,String UserAccount) {
			TblTestTask devTask=JSON.parseObject(obj,TblTestTask.class);
  			devTask.setCreateBy(Userid);
  			devTask.setTestTaskCode(getTestCode());
  			
  			
  			Date date=new Date();
  			Timestamp time2 = new Timestamp(date.getTime());  
  			devTask.setCreateDate(time2);
  			Integer Status=devTask.getFeatureStatus();
  			if(Status!=null&&Status==1||Status==3) {
  				Long featureId=devTask.getRequirementFeatureId();
  				tblTestTaskMapper.editFeature(featureId);
  			}
  			tblTestTaskMapper.addTestTask(devTask);
  			Long testId=devTask.getId();
  			TblTestTaskLog tblTestTaskLog=new TblTestTaskLog();
	  		tblTestTaskLog.setTestTaskId(testId);
	  		tblTestTaskLog.setLogType("新增工作任务");
	  		Long logId =this.insertDefectLog(tblTestTaskLog,request);
	  		if(!attachFiles.equals("")&&!attachFiles.equals(null)) {
	  			List<TblTestTaskLogAttachement> files = JsonUtil.fromJson(attachFiles, JsonUtil.createCollectionType(ArrayList.class,TblTestTaskLogAttachement.class));
	  			for(int i=0;i<files.size();i++) {
	  				files.get(i).setTestTaskLogId(logId);
	  				files.get(i).setCreateBy(Userid);
	  			}
	  			tblTestTaskLogAttachementMapper.addLogAttachement(files);
	  		}
	  		if(!attachFiles.equals("")&&!attachFiles.equals(null)&&!attachFiles.equals("[]")) {
	  			List<TblTestTaskAttachement> fileTask = JsonUtil.fromJson(attachFiles, JsonUtil.createCollectionType(ArrayList.class,TblTestTaskAttachement.class));
	  			for(int i=0;i<fileTask.size();i++) {
	  				fileTask.get(i).setTestTaskId(testId);
	  				fileTask.get(i).setCreateBy(CommonUtil.getCurrentUserId(request));
	  			}
	  			tblTestTaskAttachementMapper.addAttachement(fileTask);
	  		}
	  		
	  		Long id = devTask.getId();
	  		updateReqFea(id, request);
  			
		}
		//编辑任务信息
		@Transactional(readOnly=false)
		@Override
		public void updateTestTask(String obj,String attachFiles, String deleteFiles,Long Userid,HttpServletRequest request) throws Exception {
			TblTestTask TestTask=JSON.parseObject(obj,TblTestTask.class);
			TblDefectInfo tblDefectInfo=new TblDefectInfo();
			tblDefectInfo.setTestTaskId(TestTask.getId());
			tblDefectInfo.setCommissioningWindowId(TestTask.getCommissioningWindowId());
	  		defectService.updateTmpDefectByWorkId(tblDefectInfo);
			TestTask.setLastUpdateBy(Userid);
	  		Date date=new Date();
	  		TblTestTask oldTestTask=tblTestTaskMapper.getTestOld(TestTask.getId());//旧数据
	  		Integer Status=TestTask.getFeatureStatus();
	  		if(Status!=null&&Status==1||Status==3) {
	  			Long featureId=TestTask.getRequirementFeatureId();
	  			tblTestTaskMapper.editFeature(featureId);
	  		}
	  		
	  		Timestamp time = new Timestamp(date.getTime()); 
	  		TestTask.setLastUpdateDate(time);
	  		tblTestTaskMapper.updateTestTask(TestTask);
	  		
	  		logAlls( TestTask.getId().toString(), attachFiles,deleteFiles,oldTestTask,"编辑工作任务",new HashMap<>(), request);
	  		
	  		Long id = TestTask.getId();
	  		if(TestTask.getTestTaskStatus()!=0) {
	  			updateReqFea(id, request);
	  		}
	  		
		}
		
		@Transactional(readOnly=false)
		@Override
		public void Handle(String handle,String HattachFiles,String deleteFiles,HttpServletRequest request) {
			TblTestTask TestTask=new TblTestTask();
			TestTask=JSON.parseObject(handle, TblTestTask.class);
			TblTestTask oldTestTask=tblTestTaskMapper.getTestOld(TestTask.getId());
			TestTask.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
  			tblTestTaskMapper.Handle(TestTask);
  			logAlls( TestTask.getId().toString(), HattachFiles,deleteFiles,oldTestTask,"处理工作任务",new HashMap<>(), request);
  			updateReqFea(TestTask.getId(), request);
		}
		/**
		 * 待处理
		 */
		@Transactional(readOnly=false)
		@Override
		public void DHandle(String handle,String DHattachFiles,String deleteFiles,HttpServletRequest request) {
			TblTestTask tblTestTask=new TblTestTask();
			tblTestTask=JSON.parseObject(handle, TblTestTask.class);
			TblTestTask oldTestTask=tblTestTaskMapper.getTestOld(tblTestTask.getId());
			tblTestTask.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
			tblTestTaskMapper.DHandle(tblTestTask);
			logAlls( tblTestTask.getId().toString(), DHattachFiles,deleteFiles,oldTestTask,"处理工作任务",new HashMap<>(), request);
			updateReqFea(tblTestTask.getId(), request);
	}
		/**
		 * 待评审
		 */
		@Override
		public void examineHandle(String handle, String DHattachFiles, String deleteFiles, HttpServletRequest request) {
			TblTestTask tblTestTask=new TblTestTask();
			tblTestTask=JSON.parseObject(handle, TblTestTask.class);
			TblTestTask oldTestTask=tblTestTaskMapper.getTestOld(tblTestTask.getId());
			tblTestTask.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
			tblTestTaskMapper.examineHandle(tblTestTask);
			logAlls( tblTestTask.getId().toString(), DHattachFiles,deleteFiles,oldTestTask,"处理工作任务",new HashMap<>(), request);
			updateReqFea(tblTestTask.getId(), request);
		}
		@Transactional(readOnly=false)
		@Override
		public void assigTest(String assig,String Remark,HttpServletRequest request) {
			TblTestTask tblTestTask=new TblTestTask();
			tblTestTask=JSON.parseObject(assig, TblTestTask.class);
			Long id=tblTestTask.getId();//任务id
  	  		Long DevUser=tblTestTask.getTestUserId();//新用户ID
  	  		TblTestTask oldTestTask=tblTestTaskMapper.getTestOld(id);//旧数据
  	  		Map<String,Object> remarkMap = new HashMap<>();
	  		if(oldTestTask!=null) {
  			Long OleUser= oldTestTask.getTestUserId();
  			if(OleUser==null) {
  				
  				if(!Remark.equals("")&&Remark!=null) {
  					remarkMap.put("remark",Remark);
  				}
  				tblTestTaskMapper.assigTest(tblTestTask);
  	  	  		logAll( id.toString(), "",oldTestTask,"工作任务处理",remarkMap, request);
  				
  			}else if(OleUser!=DevUser||!Remark.equals("")) {
  				tblTestTaskMapper.assigTest(tblTestTask);
  				if(!Remark.equals("")&&Remark!=null) {
  					remarkMap.put("remark",Remark);
  				}
  				logAll(id.toString(), "",oldTestTask,"工作任务转派",remarkMap, request);
  			
  			}
  			
  		}
  						
		}
		@Transactional(readOnly=true)
		@Override
		public String DevfindMaxCode(int length) {
			return tblTestTaskMapper.DevfindMaxCode(length);
		}
		@Transactional(readOnly=true)
		@Override
		public List<TblTestTask> getExcelAllWork(TblTestTask tblDevTask,HttpServletRequest request) {
			List<TblTestTask> list = new ArrayList<>();
			 Map<String, Object> result = new HashMap<String, Object>();
			 result.put("test", tblDevTask);
			 Boolean flag = new CommonUtils().currentUserWithAdmin(request);
			 if (flag) {
				 list = tblTestTaskMapper.getTestTaskAll(result);
			 }else {
	            	list = tblTestTaskMapper.getTestTask(result);
			 }
            //list = tblTestTaskMapper.getTestTask(result);
            Object object=redisUtils.get("TBL_TEST_TASK_TEST_TASK_STATUS");
            Map<String, Object> testStage = JSON.parseObject(redisUtils.get("TBL_TEST_TASK_TEST_STAGE").toString(), Map.class);
            Map<String, Object> mapsource=new HashMap<String, Object>();
    		if (object != null &&!"".equals( object)) {//redis有直接从redis中取
    			mapsource = JSON.parseObject(object.toString());
    		}
    		 if(list.size()>0) {
				 for(int i=0;i<list.size(); i++) {
					 for(String key:mapsource.keySet()) {
						 if(key.equals(list.get(i).getTestTaskStatus().toString())) {
							 list.get(i).setWorkTaskStatus(mapsource.get(key).toString());
						 }
					 }
					 list.get(i).setTestStageName(testStage.get(list.get(i).getTestStage().toString()).toString());
				 }
			 }
            return list;
		}
		
		
		 public String getTestCode() {
				String featureCode = "";
				int codeInt =0;
				Object object = redisUtils.get("TBL_TEST_TASK_TEST_TASK_CODE");
				if (object != null &&!"".equals( object)) {//redis有直接从redis中取
					String code = object.toString();
					if (!StringUtils.isBlank(code)) {
						codeInt =Integer.parseInt(code)+1;
					}
				}else {//redis中没有查询数据库中最大的任务编号
					int length = Constants.ITMP_TESTWORK_TASK_CODE.length()+1;
					String cod = DevfindMaxCode(length);
					if (!StringUtils.isBlank(cod)) {
						codeInt = Integer.parseInt(cod)+1;
					}else {
						codeInt =1;
					}
					
				}
				DecimalFormat mat=new DecimalFormat("00000000");
				String codeString=mat.format(codeInt);
			
				featureCode = Constants.ITMP_TESTWORK_TASK_CODE+codeString;
				redisUtils.set("TBL_TEST_TASK_TEST_TASK_CODE",codeString );	
				return featureCode;
			}
		 
			@Override
			public Map ReqSystem() {
				Object object = redisUtils.get("TBL_SYSTEM_INFO_SYSTEM_TYPE");
				Map<String, Object> mapsource= new HashMap<>();
				if (object != null &&!"".equals( object)) {//redis有直接从redis中取
					mapsource = JSON.parseObject(object.toString());
					}
				return mapsource;
			}

			@Override
			public Map ReqStatus() {
				Object object = redisUtils.get(RETYPE);
				Map<String, Object> mapsource= new HashMap<>();
				if (object != null &&!"".equals( object)) {//redis有直接从redis中取
					mapsource = JSON.parseObject(object.toString());
					}
				return mapsource;
			}

	public Long insertDefectLog(TblTestTaskLog defectLog,HttpServletRequest request){
			        defectLog = this.setDefectLog(defectLog,request);
			        defectLog.setCreateBy(CommonUtil.getCurrentUserId(request));
			        defectLog.setCreateDate(new Timestamp(new Date().getTime()));
			        tblTestTaskLogMapper.insertDefectLog(defectLog);
			        return defectLog.getId();
			  }
		 private TblTestTaskLog setDefectLog(TblTestTaskLog defectLog,HttpServletRequest request){
		        defectLog.setUserId(CommonUtil.getCurrentUserId(request));
		        defectLog.setUserName(CommonUtil.getCurrentUserName(request));
		        defectLog.setUserAccount(CommonUtil.getCurrentUserAccount(request));
		        return defectLog;
		    }
			  /**
		     *  添加日志详情 一个字段
		     *  更新字段
		     *  @param oldObject
		     *  @param newObject
		     *  @param logId
		     *
		     */
		   
		    public void updateFieledsReflect(Object oldObject,Object newObject,Long logId,Map<String,Object> remarkMap) throws  IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException, ClassNotFoundException{
		        /*Class<?> oldDemo = null;
		        Class<?> newDemo = null;*/
		        List<Map<String,Object>> list = new ArrayList<>();
		        if (!remarkMap.isEmpty()){
		            list.add(remarkMap);
		        }
		        Class<?>   oldDemo = Class.forName(oldObject.getClass().getName());
		        Class<?>   newDemo = Class.forName(newObject.getClass().getName());

		        Field[] oldFields = oldDemo.getDeclaredFields();
		       // if(!oldObject.equals("Infinity")) { 
		        for(Field oldField : oldFields){

		            oldField.setAccessible(true);
		            String oldDclareName = oldField.getName();
		            Object oldDeclareValue = oldField.get(oldObject);
		            PropertyInfo propertyInfo = oldField.getAnnotation(PropertyInfo.class);

		            Field newField = newDemo.getDeclaredField(oldDclareName);
		            newField.setAccessible(true);
		            Object newDeclareValue = newField.get(newObject);

		            // 测试工作任务名称
		            if (oldDclareName.equals("requirementFeatureId") && oldDeclareValue != null){
		                oldDeclareValue = tblTestTaskMapper.getFeatureNameById((Long)oldDeclareValue);
		            }
		            if (oldDclareName.equals("requirementFeatureId") && newDeclareValue != null){
		                newDeclareValue = tblTestTaskMapper.getFeatureNameById((Long)newDeclareValue);
		            }
		            // 测试工作任务名称
		            Object object = redisUtils.get("TBL_TEST_TASK_TEST_TASK_STATUS");
		            Map<String, Object> mapsource= new HashMap<>();
		            if (object != null &&!"".equals( object)) {//redis有直接从redis中取
	         			mapsource = JSON.parseObject(object.toString());
	         		}
		            if (oldDclareName.equals("testTaskStatus") && oldDeclareValue != null){
		            	for(String key:mapsource.keySet()) {
	        				if(key.equals(oldDeclareValue.toString())) {
	        					oldDeclareValue=mapsource.get(key).toString();
	        				}
	        			}
		            }
		            if (oldDclareName.equals("testTaskStatus") && newDeclareValue != null){
		            	for(String key:mapsource.keySet()) {
	        				if(key.equals(newDeclareValue.toString())) {
	        					newDeclareValue=mapsource.get(key).toString();
	        				}
	        			}
		            }
		            
		            if (oldDclareName.equals("testUserId") && newDeclareValue != null){
		            	continue;
		            }
		            if (oldDclareName.equals("commissioningWindowId") && newDeclareValue != null){
		            	continue;
		            }
		            if (oldDclareName.equals("featureName") && newDeclareValue != null){
		            	continue;
		            }
		            if (oldDclareName.equals("featureStatus") && newDeclareValue != null){
		            	continue;
		            }
					if (oldDclareName.equals("systemInfoList") && newDeclareValue != null){
						continue;
					}
		           
		            if (oldDclareName.equals("testStage") && oldDeclareValue != null){
		            	if(newDeclareValue.toString().equals("1")) {
		            		oldDeclareValue="系测";
		            	}else {
		            		oldDeclareValue="版测";
		            	}
		            	
		            }
		            if (oldDclareName.equals("testStage") && newDeclareValue != null){
		            	if(newDeclareValue.toString().equals("1")) {
		            		newDeclareValue="系测";
		            	}else {
		            		newDeclareValue="版测";
		            	}
		            }
		          
		            String[] fieldTypeName = newField.getType().getName().split("\\.");
		            if(!listType.contains(fieldTypeName[fieldTypeName.length-1])){
		                updateFieledsReflect(oldDeclareValue, newDeclareValue, logId,remarkMap);
		                continue;
		            }
		            
		            if(oldDeclareValue == null || newDeclareValue == null){
		                if(oldDeclareValue == null && newDeclareValue == null){
		                    continue;
		                } else {
		                    if(oldDeclareValue == null){
		                        if(oldField.getType().equals(java.util.Date.class)){
		                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		                            newDeclareValue = simpleDateFormat.format(newDeclareValue);
		                        }
		                        // 新增日志操作
		                        Map<String,Object> newMap = new HashMap<>();
		                        newMap.put("fieldName",propertyInfo.name());
		                        newMap.put("oldValue","");
		                        newMap.put("newValue",newDeclareValue.toString());
		                        list.add(newMap);
		                    }else{
		                        // 删除日志操作
		                        if(oldField.getType().equals(java.sql.Timestamp.class)){
		                            continue;
		                        } else {
		                            if(oldField.getType().equals(java.util.Date.class)){
		                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		                                oldDeclareValue = simpleDateFormat.format(oldDeclareValue);
		                            
		                            }
		                           /*String name= propertyInfo.name();
		                            Map<String,Object> delMap = new HashMap<>();
		                            delMap.put("fieldName",name);
		                            delMap.put("oldValue",oldDeclareValue.toString());
		                            delMap.put("newValue","已删除");
		                            list.add(delMap);*/
		                        }

		                    }
		                    continue;
		                }
		            }
		            // 修改日志操作
		            if(!oldDeclareValue.toString().equals(newDeclareValue.toString())){
		                if(oldField.getType().equals(java.sql.Timestamp.class)){
		                    continue;
		                } else {
		                    if(oldField.getType().equals(java.util.Date.class)){
		                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		                        oldDeclareValue = simpleDateFormat.format(oldDeclareValue);
		                        newDeclareValue=simpleDateFormat.format(newDeclareValue);
		                    }
		                    String name=propertyInfo.name();
		                    Map<String,Object> updateMap = new HashMap<>();
		                    updateMap.put("fieldName",name);
		                    updateMap.put("oldValue",oldDeclareValue.toString());
		                    updateMap.put("newValue",newDeclareValue.toString());
		                    list.add(updateMap);
		                }
		            }

		       // }
		    }
		        Gson gson = new Gson();
		        String logDetail = gson.toJson(list);
		        TblDefectLog log = new TblDefectLog();
		        log.setLogDetail(logDetail);
		        log.setId(logId);
		        // 保存到数据库
		        tblTestTaskLogMapper.updateLogById(log);
		    }

	@Override
	@Transactional(readOnly=true)
	public TblTestTask getTestTaskById(Long id) {
		return  tblTestTaskMapper.getTestOld(id);
	}
	
	 /**
	  * 添加日志附件
	  * @param id
	  * @param adoptFiles
	  * @param oldDevTask
	  * @param request
	  */
	 public void logAll(String id,String adoptFiles,Object oldDevTask,String LogType,Map<String,Object> remarkMap,HttpServletRequest request) {
			TblTestTaskLog tblTestTaskLog=new TblTestTaskLog();
			tblTestTaskLog.setTestTaskId(Long.parseLong(id));
			tblTestTaskLog.setLogType(LogType);
	  		Long logId =this.insertDefectLog(tblTestTaskLog,request);
	  		TblTestTask newTestTask=tblTestTaskMapper.getTestOld(Long.parseLong(id));
	  		if(!adoptFiles.equals("")&&!adoptFiles.equals(null)&&!adoptFiles.equals("[]")) {
	  			List<TblTestTaskLogAttachement> files = JsonUtil.fromJson(adoptFiles, JsonUtil.createCollectionType(ArrayList.class,TblTestTaskLogAttachement.class));
	  			for(int i=0;i<files.size();i++) {
	  				files.get(i).setTestTaskLogId(logId);
	  				files.get(i).setCreateBy(CommonUtil.getCurrentUserId(request));
					files.get(i).setStatus(1);
	  			}
	  			tblTestTaskLogAttachementMapper.addLogAttachement(files);
	  		}
	  	
	  		if(!adoptFiles.equals("")&&!adoptFiles.equals(null)&&!adoptFiles.equals("[]")) {
	  			List<TblTestTaskAttachement> fileTask = JsonUtil.fromJson(adoptFiles, JsonUtil.createCollectionType(ArrayList.class,TblTestTaskAttachement.class));
	  			for(int i=0;i<fileTask.size();i++) {
	  				fileTask.get(i).setTestTaskId(Long.parseLong(id));
	  				fileTask.get(i).setCreateBy(CommonUtil.getCurrentUserId(request));
	  			}
	  			tblTestTaskAttachementMapper.addAttachement(fileTask);
	  		}
	  		try{
	            updateFieledsReflect(oldDevTask, newTestTask, logId,remarkMap);
	        }catch (Exception e){
	        	
	        }
	 }

	public void logAlls(String id,String adoptFiles,String deleteAttaches,Object oldDevTask,String LogType,Map<String,Object> remarkMap,HttpServletRequest request) {
		TblTestTaskLog tblTestTaskLog=new TblTestTaskLog();
		tblTestTaskLog.setTestTaskId(Long.parseLong(id));
		tblTestTaskLog.setLogType(LogType);
		Long logId =this.insertDefectLog(tblTestTaskLog,request);
		TblTestTask newTestTask=tblTestTaskMapper.getTestOld(Long.parseLong(id));
		if(!adoptFiles.equals("")&&!adoptFiles.equals(null)&&!adoptFiles.equals("[]")) {
			List<TblTestTaskLogAttachement> files = JsonUtil.fromJson(adoptFiles, JsonUtil.createCollectionType(ArrayList.class,TblTestTaskLogAttachement.class));
			for(int i=0;i<files.size();i++) {
				files.get(i).setTestTaskLogId(logId);
				files.get(i).setCreateBy(CommonUtil.getCurrentUserId(request));
				files.get(i).setStatus(1);
			}
			tblTestTaskLogAttachementMapper.addLogAttachement(files);
		}

		if(!adoptFiles.equals("")&&!adoptFiles.equals(null)&&!adoptFiles.equals("[]")) {
			List<TblTestTaskAttachement> fileTask = JsonUtil.fromJson(adoptFiles, JsonUtil.createCollectionType(ArrayList.class,TblTestTaskAttachement.class));
			for(int i=0;i<fileTask.size();i++) {
				fileTask.get(i).setTestTaskId(Long.parseLong(id));
				fileTask.get(i).setCreateBy(CommonUtil.getCurrentUserId(request));
			}
			tblTestTaskAttachementMapper.addAttachement(fileTask);
		}

		// 添加删除附件日志
		if(!deleteAttaches.equals("")&&!deleteAttaches.equals(null)&&!deleteAttaches.equals("[]")){
			List<TblTestTaskLogAttachement> deleteFileTask = JsonUtil.fromJson(deleteAttaches, JsonUtil.createCollectionType(ArrayList.class,TblTestTaskLogAttachement.class));
			for(int i=0;i<deleteFileTask.size();i++) {
				deleteFileTask.get(i).setTestTaskLogId(logId);
				deleteFileTask.get(i).setCreateBy(CommonUtil.getCurrentUserId(request));
				deleteFileTask.get(i).setStatus(2);
			}
			tblTestTaskLogAttachementMapper.addLogAttachement(deleteFileTask);
		}
		try{
			updateFieledsReflect(oldDevTask, newTestTask, logId,remarkMap);
		}catch (Exception e){

		}
	}

	@Override
	public List<Map<String, Object>> getAllFeatureTask(TblTestTask tblTestTask, Integer pageNumber, Integer pageSize) {
		 Map<String, Object> map = new HashMap<>();
		 map = PageWithBootStrap.setPageNumberSize(map,pageNumber,pageSize);
		 map.put("TblTestTask", tblTestTask);
		 return tblTestTaskMapper.getAllFeature(map);
	}

	@Override
	public List<Map<String, Object>> getAllTestUser(TblUserInfo tblUserInfo, Integer notWithUserID, Integer devID,
			Integer pageNumber, Integer pageSize) {
		Map<String, Object> map = new HashMap<>();
		 map = PageWithBootStrap.setPageNumberSize(map,pageNumber,pageSize);
		 map.put("tblUserInfo", tblUserInfo);
		 map.put("notWithUserID", notWithUserID);
		 map.put("devID", devID);
		 return   tblUserInfoMapper.getAllTestUser(map);
	}

	/**
	 * 根据测试任务id查询工作任务
	 */
	@Override
	public List<TblTestTask> selectTestTaskByRequirementFeatureId(Long requirementFeatureId,String nameOrNumber, String createBy, String testTaskId) {
		List<Long> userIds = JSON.parseArray(createBy, Long.class);
		List<Long> testTaskIds = JSON.parseArray(testTaskId,Long.class);
		return tblTestTaskMapper.selectTestTaskByRequirementFeatureId(requirementFeatureId,nameOrNumber,userIds,testTaskIds);
	}

	//根据测试工作任务的状态修改测试任务状态
	public void updateReqFea(Long id, HttpServletRequest request) {
		TblRequirementFeature requirementFeature = tblRequirementFeatureMapper.getReqFeaByTaskId(id);
		String status = "";
		String afterName = "";
		List<TblDataDic> dataDics = getDataFromRedis("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS_2");
		for (TblDataDic tblDataDic : dataDics) {
			if (requirementFeature.getRequirementFeatureStatus()!=null) {
				if (tblDataDic.getValueCode().equals(requirementFeature.getRequirementFeatureStatus())) {
					afterName = tblDataDic.getValueName();
				}
			}
		}
		Map<Object,Object> map = new HashMap<>();
  		map.put("id", id);
  		List<Integer> testStages = tblTestTaskMapper.selectTestStageById(id);//查出同一测试任务下的工作任务的测试阶段(排除撤销的工作任务)
  		if(testStages.size() == 1) { //长度为1，全部都是版测或系测
			if(testStages.get(0)==1) {//测试任务下的工作任务都为系测
				List<Integer> testTaskStatus = tblTestTaskMapper.selectTestTaskStatusById(id);
				if(testTaskStatus.size()==1 && testTaskStatus.get(0)==3) {//工作任务都是测试完成
					map.put("status", "03");
					tblTestTaskMapper.updateReqFeaStatus(map);//系测完成
					if(!requirementFeature.getRequirementFeatureStatus().equals("03")) {
						status = "&nbsp;&nbsp;“<b>"+afterName+"</b>”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“<b>系测完成</b>”&nbsp;&nbsp;；";
						insertLog(requirementFeature, status, request);
					}
				}else {
					map.put("status", "02");
					tblTestTaskMapper.updateReqFeaStatus(map);//系测中
					if(!requirementFeature.getRequirementFeatureStatus().equals("02")) {
						status = "&nbsp;&nbsp;“<b>"+afterName+"</b>”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“<b>系测中</b>”&nbsp;&nbsp;；";
						insertLog(requirementFeature, status, request);
					}
				}
			}else if(testStages.get(0)==2) {//测试任务下的工作任务都为版测
				List<Integer> testTaskStatus = tblTestTaskMapper.selectTestTaskStatusById(id);
				if(testTaskStatus.size()==1 && testTaskStatus.get(0)==3) {//工作任务都是测试完成
					map.put("status", "08");
					tblTestTaskMapper.updateReqFeaStatus(map);//版测完成
					if(!requirementFeature.getRequirementFeatureStatus().equals("08")) {
						status = "&nbsp;&nbsp;“<b>"+afterName+"</b>”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“<b>版测完成</b>”&nbsp;&nbsp;；";
						insertLog(requirementFeature, status, request);
					}
				}else {
					map.put("status", "06");
					tblTestTaskMapper.updateReqFeaStatus(map);//版测中
					if(!requirementFeature.getRequirementFeatureStatus().equals("06")) {
						status = "&nbsp;&nbsp;“<b>"+afterName+"</b>”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“<b>版测中</b>”&nbsp;&nbsp;；";
						insertLog(requirementFeature, status, request);
					}
				}
			}
		}else if(testStages.size() == 2){ //长度不为1(为2)，有版测也有系测
			List<Integer> testTaskStatus = tblTestTaskMapper.selectTestTaskStatusById2(id);
			if(testTaskStatus.size()==1 && testTaskStatus.get(0)==3) {//工作任务都是测试完成
				map.put("status", "08");
				tblTestTaskMapper.updateReqFeaStatus(map);//版测完成
				if(!requirementFeature.getRequirementFeatureStatus().equals("08")) {
					status = "&nbsp;&nbsp;“<b>"+afterName+"</b>”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“<b>版测完成</b>”&nbsp;&nbsp;；";
					insertLog(requirementFeature, status, request);
				}
			}else {
				map.put("status", "06");
				tblTestTaskMapper.updateReqFeaStatus(map);//版测中
				if(!requirementFeature.getRequirementFeatureStatus().equals("06")) {
					status = "&nbsp;&nbsp;“<b>"+afterName+"</b>”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“<b>版测中</b>”&nbsp;&nbsp;；";
					insertLog(requirementFeature, status, request);
				}
			}
		}
//  		TblRequirementFeatureLog log = new TblRequirementFeatureLog();
//		log.setRequirementFeatureId(requirementFeature.getId());
//		log.setLogType("修改测试任务");
//		log.setLogDetail(status);
//		insertLog(log, request);
	}
	
	@Transactional(readOnly = false)
	public void insertLog(TblRequirementFeature requirementFeature, String status, HttpServletRequest request) {
		TblRequirementFeatureLog log = new TblRequirementFeatureLog();
		log.setRequirementFeatureId(requirementFeature.getId());
		log.setLogType("修改测试任务");
		log.setLogDetail(status);
		log.setCreateBy(CommonUtil.getCurrentUserId(request));
		log.setCreateDate(new Timestamp(new Date().getTime()));
		log.setUserId(CommonUtil.getCurrentUserId(request));
		log.setUserAccount(CommonUtil.getCurrentUserAccount(request));
		log.setUserName(CommonUtil.getCurrentUserName(request));
		requirementFeatureLogMapper.insert(log);
	}
	
	private List<TblDataDic> getDataFromRedis(String termCode) {
		String result = redisUtils.get(termCode).toString();
		List<TblDataDic> dics = new ArrayList<>();
		if (!StringUtils.isBlank(result)) {
			Map<String, Object> maps = (Map<String, Object>) JSON.parse(result);
			for (Entry<String, Object> entry : maps.entrySet()) {
				TblDataDic dic = new TblDataDic();
				dic.setValueCode(entry.getKey());
				dic.setValueName(entry.getValue().toString());
				dics.add(dic);
			}
		}
		return dics;
	}

	//根据工作任务id查询该任务关联系统下的项目组的测试组长和测试管理岗的人员
	@Override
	public List<Long> selectUserIdsById(Long id) {
		// TODO Auto-generated method stub
		Long systemId = tblTestTaskMapper.getSystemIdByTaskId(id);
		List<Long> uIds = tblProjectGroupUserMapper.findUserIdBySystemId(systemId);
		return uIds;
	}
	

		    
}
