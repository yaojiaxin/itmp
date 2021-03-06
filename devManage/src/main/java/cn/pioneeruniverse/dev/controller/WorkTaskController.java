package cn.pioneeruniverse.dev.controller;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pioneeruniverse.dev.entity.*;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.StringUtil;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.ExportExcel;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.common.utils.S3Util;
import cn.pioneeruniverse.dev.feignInterface.DevManageToSystemInterface;
import cn.pioneeruniverse.dev.service.devtask.DevTaskService;
import cn.pioneeruniverse.dev.service.workRemark.workTaskRemark;
import cn.pioneeruniverse.dev.service.worktask.WorkTaskService;
@RestController
@RequestMapping("worktask")
public class WorkTaskController extends BaseController{
	@Autowired 
	private WorkTaskService workTaskService;
	@Autowired 
	private workTaskRemark workTaskRemark;
	@Autowired
	private DevTaskService devTaskService;
	@Autowired
	private S3Util s3Util;
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private DevManageToSystemInterface devManageToSystemInterface;

	/**
	 * shan-??????id????????????????????????
	 * @param id
	 * @return
	 */
	@PostMapping("getWorkTask")
	public TblDevTask getWorkTask(Long id){
		TblDevTask devTask = workTaskService.getDevTaskById(id);
		return devTask;
	}
	
	@PostMapping("getAllWorktask")
	public Map  toConstruction( String workTask, String getProjectIds,Integer rows,Integer page,HttpServletRequest request, String sidx, String sord){
		Map<String, Object> result = new HashMap<String, Object>();
		TblDevTask tblDevTask=new TblDevTask();
		try {
			 Long Userd=CommonUtil.getCurrentUserId(request);
			if (StringUtils.isNotBlank(workTask)){
				tblDevTask=JSONObject.parseObject(workTask,TblDevTask.class);
			}
			tblDevTask.setSidx(sidx);
			tblDevTask.setSord(sord);
			Long[] projectIds = null;
			if( getProjectIds != null && !getProjectIds.equals("")){
				String[] str = getProjectIds.split(",");
				projectIds = (Long[]) ConvertUtils.convert(str,Long.class);
			}
			tblDevTask.setId(Userd);
			result=workTaskService.getDevTask(tblDevTask,projectIds, page, rows,request);
		} catch (Exception e) {
			 e.printStackTrace();
		     logger.error("mes:" + e.getMessage(), e);
		}
		return result;
	}
		
		//??????????????????
		@RequestMapping(value="getAllFeature")
		public Map<String, Object> getAllFeature(TblDevTask tblDevTask,Integer pageNumber, Integer pageSize,HttpServletRequest request){
			Map<String, Object> result = new HashMap<String, Object>();
			 result.put("status", Constants.ITMP_RETURN_SUCCESS);
			 
			try {
				Long userdId =CommonUtil.getCurrentUserId(request);
				LinkedHashMap map = (LinkedHashMap) redisUtils.get(CommonUtil.getToken(request));
				List<String> roleCodes = (List<String>) map.get("roles"); 
				 List<Map<String, Object>> list = workTaskService.getAllFeature(tblDevTask,userdId,roleCodes, pageNumber, pageSize);
		         List<Map<String, Object>> list2 = workTaskService.getAllFeature(tblDevTask,userdId,roleCodes, null, null);
		         result.put("rows",list );
		         result.put("total", list2.size());
			} catch (Exception e) {
				 
				 e.printStackTrace();
			     logger.error("mes:" + e.getMessage(), e);
			     return handleException(e, "??????????????????????????????");
			}
			return result;
		}
	
	/**
	 * ??????????????????
	 * @param objStr
	 * @param attachFiles
	 * @param request
	 * @return
	 */
	@RequestMapping(value="addDevTask",method=RequestMethod.POST)
	public Map addDevTask(String objStr,String attachFiles,HttpServletRequest request) {
		Map<String, Object>  map= new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Long Userid=CommonUtil.getCurrentUserId(request);
			String  UserName=CommonUtil.getCurrentUserName(request);
			String UserAccount=CommonUtil.getCurrentUserAccount(request);
			workTaskService.addDevTask(objStr,attachFiles,Userid,request,UserAccount);
//			//????????????
//			sendAddMessage(request,objStr);
		} catch (Exception e) {
			return super.handleException(e, "????????????");
		}
		return map;
	}



	private void sendAddMessage(HttpServletRequest request,String objStr){

		TblDevTask devTask = JSON.parseObject(objStr, TblDevTask.class);
		Map<String,Object> map=new HashMap<>();
		map.put("messageTitle","??????????????????????????????????????????");
		map.put("messageContent",devTask.getDevTaskCode()+"|"+devTask.getDevTaskName());
		map.put("messageReceiverScope",2);
		map.put("messageReceiver",devTask.getDevUserId());
		devManageToSystemInterface.insertMessage(JSON.toJSONString(map));

	}

	private void sendTrsMessage(HttpServletRequest request,TblDevTask tblDevTask){

		TblDevTask devTask=workTaskService.getDevTaskById(tblDevTask.getId());
		Map<String,Object> map=new HashMap<>();
		map.put("messageTitle","??????????????????????????????????????????");
		map.put("messageContent",devTask.getDevTaskCode()+"|"+devTask.getDevTaskName());
		map.put("messageReceiverScope",2);
		map.put("messageReceiver",tblDevTask.getDevUserId());
		devManageToSystemInterface.insertMessage(JSON.toJSONString(map));

	}

	/**
	 * ???????????????????????????????????????
	 * @param objStr
	 * @param attachFiles
	 * @param request
	 * @return
	 */
		@RequestMapping(value="addDefectDevTask",method=RequestMethod.POST)
		public Map addDefectDevTask(String objStr,String attachFiles,HttpServletRequest request) {
			Map<String, Object>  map= new HashMap<>();
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
			try {
				Long Userid=CommonUtil.getCurrentUserId(request);
				String  UserName=CommonUtil.getCurrentUserName(request);
				String UserAccount=CommonUtil.getCurrentUserAccount(request);
				workTaskService.addDefectDevTask(objStr,attachFiles,Userid,request,UserAccount);
			} catch (Exception e) {
				return super.handleException(e, "????????????");
			}
			return map;
		}
	//??????
	@RequestMapping(value="updateDevTask",method=RequestMethod.POST)
	public Map updateDevTask(String obj, String attachFiles,String deleteAttaches,HttpServletRequest request) {
		//????????????
		TblDevTask newDevTask = JSON.parseObject(obj, TblDevTask.class);
		TblDevTask oldDevTask=workTaskService.getDevTaskById(newDevTask.getId());

		Map<String, Object>  map= new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblDevTaskAttachement> tblDevTaskAttachement = JsonUtil.fromJson(deleteAttaches, JsonUtil.createCollectionType(ArrayList.class, TblDevTaskAttachement.class));
			if(tblDevTaskAttachement.size()>0) {
				workTaskRemark.updateNo(tblDevTaskAttachement,request);
			}
			
			 
			Long Userid=CommonUtil.getCurrentUserId(request);
			workTaskService.updateDevTask(obj,attachFiles,deleteAttaches,Userid,request);


		} catch (Exception e) {
			return super.handleException(e, "????????????");
		}

		//????????????????????????????????????
		TblDevTaskAttention attention = new TblDevTaskAttention();
		attention.setDevTaskId(newDevTask.getId());
		attention.setStatus(1);
		List<TblDevTaskAttention> attentionList = workTaskService.getAttentionList(attention);
		if (attentionList != null && attentionList.size() > 0) {
			//??????????????????
			sendEditMessage(request,oldDevTask,newDevTask, attentionList);
		}
		return map;
	}


	private  void sendEditMessage(HttpServletRequest request,TblDevTask oldDevTask, TblDevTask newDevTask, 
			List<TblDevTaskAttention> attentionList){
		String userIds = "";
		for (TblDevTaskAttention attention : attentionList) {
			userIds += attention.getUserId() + ",";
		}
		if(StringUtil.isNotEmpty(userIds)){
			userIds = userIds.substring(0, userIds.length() - 1);
			Map<String,Object> map=new HashMap<>();
			map.put("messageTitle","???????????????????????????????????????");
			map.put("messageContent",oldDevTask.getDevTaskCode()+"|"+newDevTask.getDevTaskName());
			map.put("messageReceiverScope",2);
			map.put("messageReceiver", userIds);
			//???????????? 4--????????????????????????
			map.put("messageSource",4);
			map.put("systemId",newDevTask.getSystemId());
			devManageToSystemInterface.insertMessage(JSON.toJSONString(map));
		}
	}


	//??????????????????
	@RequestMapping(value="getEditDevTask",method=RequestMethod.POST)
	public Map<String,Object> getEditDevTask(String id) {
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			List<TblDevTaskAttachement> list=workTaskRemark.findAttachement(Long.parseLong(id));
			result=workTaskService.getEditDevTask(id);
			result.put("attachements", list);

			//??????????????????

			List<ExtendedField> extendedFields=workTaskService.findFieldByDevId(Long.parseLong(id));
			result.put("fields", extendedFields);

		} catch (Exception e) {
			result.put("status", "2");
			return super.handleException(e, "??????????????????????????????");
		}
		return result;
	}
	
	
	@RequestMapping(value="getAllDevUser")
	public List<TblDevTask> getAllDevUser(HttpServletRequest request){
		Long id=CommonUtil.getCurrentUserId(request);
		List<TblDevTask> list=workTaskService.getAllDevUser(id);
		return list;
	}



	@RequestMapping(value="getDevTaskFiled")
	public  Map<String,Object> getDevTaskFiled(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String,Object>();

		List<ExtendedField> extendedFields=workTaskService.findFieldByDevId(null);
		map.put("fields", extendedFields);
		return  map;

	}

	
	//????????????
	@RequestMapping(value="getSeeDetail",method=RequestMethod.POST)
	public Map<String,Object> getSeeDetail(HttpServletRequest request, String id,Long requirementFeatureId){
		List<TblDevTaskRemark> list= new ArrayList<>();
		List<TblDevTaskLogAttachement> listLogAttachement= new ArrayList<>();
		Map<String,Object> result = new HashMap<String,Object>();
		try {
			result=workTaskService.getSeeDetail(id);
			
			list=workTaskRemark.selectRemark(id);
			List<Long> idRemark = new ArrayList<Long>(list.size());
			List<TblDevTaskRemarkAttachement> list2= new ArrayList<>();
			for(int i=0;i<list.size();i++) {
				idRemark.add(list.get(i).getId());
			}
			if(idRemark.size()>0) {
				list2=workTaskRemark.findTaskRemarkAttchement(idRemark);
			}
			List<TblDevTaskLog>  logsList= workTaskRemark.findLogList(Long.parseLong(id));
			List<Long> idLog = new ArrayList<Long>(list.size());
			for(int j=0;j<logsList.size();j++) {
				idLog.add(logsList.get(j).getId());
			}
			if(idLog.size()>0) {
				listLogAttachement=workTaskRemark.findLogAttachement(idLog);
			}
			List<TblDevTaskAttachement> listfile=workTaskRemark.findAttachement(Long.parseLong(id));
			Map<String, Object> devTasks = devTaskService.getOneDevTask(requirementFeatureId);

			//??????????????????
			Long userId = CommonUtil.getCurrentUserId(request);
			TblDevTaskAttention attention = new TblDevTaskAttention();
			attention.setDevTaskId(Long.parseLong(id));
			attention.setUserId(userId);
			attention.setStatus(1);
			List<TblDevTaskAttention> attentionList = workTaskService.getAttentionList(attention);
			if (attentionList != null && attentionList.size() > 0) {
				result.put("attentionStatus", 1);
			} else {
				result.put("attentionStatus", 2);
			}

			//??????????????????

			List<ExtendedField> extendedFields=workTaskService.findFieldByDevId(Long.parseLong(id));
			result.put("fields", extendedFields);

			result.put("attachements", listfile);
			result.put("logs", logsList);
			result.put("Attchement", list2);
			result.put("rmark", list);
			result.put("logAttachement",listLogAttachement);
			result.put("devTasks",devTasks);

		} catch (Exception e) {
			return super.handleException(e, "??????????????????");
		}
		
		
		return result;
		
	}
	
	//??????????????????????????????
	@RequestMapping(value = "changeAttention", method = RequestMethod.POST)
	public Map<String, Object> changeAttention(HttpServletRequest request, Long id, Integer attentionStatus) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", "success");
		try {
			if (attentionStatus != null) {
				workTaskService.changeAttention(id, attentionStatus, request);
			}
		} catch (Exception e) {
			return super.handleException(e, "??????????????????");
		}
		return map;
	}
	
	//?????????
	@RequestMapping(value="DHandleDev",method=RequestMethod.POST)
	public void DHandleDev(String handle,String DHattachFiles,String deleteAttaches,HttpServletRequest request){
		List<TblDevTaskAttachement> tblDevTaskAttachement = JsonUtil.fromJson(deleteAttaches, JsonUtil.createCollectionType(ArrayList.class, TblDevTaskAttachement.class));
		if(tblDevTaskAttachement.size()>0) {
			workTaskRemark.updateNo(tblDevTaskAttachement,request);
		}
		workTaskService.DHandle(handle,DHattachFiles,deleteAttaches,request);
	}
	//?????????
	@RequestMapping(value="HandleDev",method=RequestMethod.POST)
	public void HandleDev(String handle,String HattachFiles,String deleteAttaches,HttpServletRequest request){
		List<TblDevTaskAttachement> tblDevTaskAttachement = JsonUtil.fromJson(deleteAttaches, JsonUtil.createCollectionType(ArrayList.class, TblDevTaskAttachement.class));
		if(tblDevTaskAttachement.size()>0) {
			workTaskRemark.updateNo(tblDevTaskAttachement,request);
		}
        TblDevTask devTask = JSON.parseObject(handle, TblDevTask.class);
		devTask.setDevTaskStatus(3);
		workTaskService.Handle(devTask,HattachFiles,deleteAttaches,request);
	}
	//????????????
	@RequestMapping(value="CodeHandleDev",method=RequestMethod.POST)
	public void CodeHandleDev(String handle,String HattachFiles,String deleteAttaches,HttpServletRequest request){
		List<TblDevTaskAttachement> tblDevTaskAttachement = JsonUtil.fromJson(deleteAttaches, JsonUtil.createCollectionType(ArrayList.class, TblDevTaskAttachement.class));
		if(tblDevTaskAttachement.size()>0) {
			workTaskRemark.updateNo(tblDevTaskAttachement,request);
		}
		workTaskService.CodeHandle(handle,HattachFiles,deleteAttaches, request);
	}
	//??????????????????
	@RequestMapping(value="reviewAdopt",method=RequestMethod.POST)
	public void reviewAdopt(String id,String adoptFiles,String deleteAttaches,HttpServletRequest request){
		workTaskService.reviewAdopt(id,adoptFiles,deleteAttaches,request);
	}
	//?????????????????????
	@RequestMapping(value="reviewNAdopt",method=RequestMethod.POST)
	public void reviewNAdopt(String id,String adoptFiles,String deleteAttaches,HttpServletRequest request){
		List<TblDevTaskAttachement> tblDevTaskAttachement = JsonUtil.fromJson(deleteAttaches, JsonUtil.createCollectionType(ArrayList.class, TblDevTaskAttachement.class));
		if(tblDevTaskAttachement.size()>0) {
			workTaskRemark.updateNo(tblDevTaskAttachement,request);
		}
		workTaskService.reviewNAdopt(id,adoptFiles,deleteAttaches,request);
	}
	//??????
	@RequestMapping(value="assigDev",method=RequestMethod.POST)
	public Map assigDev(String assig,String Remark,HttpServletRequest request){
		 Map<String, Object> result = new HashMap<String, Object>();
		 result.put("status", "1");
		try {
			TblDevTask devTaskSend = new TblDevTask();
			devTaskSend = JSON.parseObject(assig, TblDevTask.class);

			workTaskService.assigDev(assig,Remark,request);
			sendTrsMessage(request,devTaskSend);
		} catch (Exception e) {
			result.put("status", "2");
			e.printStackTrace();
		}
		return result;
	}
	//????????????
	@RequestMapping(value="getSystem",method=RequestMethod.POST)
	public Map<String,Object> getSystem(String workTask,Integer rows,Integer page,HttpServletRequest request){
		Long userId=CommonUtil.getCurrentUserId(request);
		  Map<String, Object> result = new HashMap<String, Object>();
		TblSystemInfo systemInfo=new TblSystemInfo();
		if (StringUtils.isNotBlank(workTask)) {
			systemInfo=JSONObject.parseObject(workTask,TblSystemInfo.class);
		}
			
			systemInfo.setId(userId);
			 result=workTaskService.getAllsystem(systemInfo, page, rows);
		return  result;
	}
	
	//????????????
	@RequestMapping(value="Requirement",method=RequestMethod.POST)
	public Map<String,Object> getRequirement(String workTask,Integer rows,Integer page,HttpServletRequest request){
		Long userId=CommonUtil.getCurrentUserId(request);
		  Map<String, Object> result = new HashMap<String, Object>();
		TblRequirementInfo rquirement=new TblRequirementInfo();
		if (StringUtils.isNotBlank(workTask)) {
			rquirement=JSONObject.parseObject(workTask,TblRequirementInfo.class);
		} 
			
			rquirement.setId(userId);
			result=workTaskService.getAllRequirt(rquirement, page, rows);
		return  result;
	}

	/**
	 * ????????????????????????????????????
	 * @param request
	 * @return
	 */
	@PostMapping("findProjectByUser")
	public Map<String, Object>  findProjectByUser( HttpServletRequest request){
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			List<TblProjectInfo> list = workTaskService.findProjectByUser(request);
			result.put("data",list);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return result;
	}

	/**
	 * ??????
	 * @param excelDate
	 * @param response
	 * @param request
	 */
	@ResponseBody
	@RequestMapping(value="getExcelAllWork")
	public void getExcelUser(String excelDate,String getProjectIds,HttpServletResponse response,HttpServletRequest request){
		List<TblDevTask> list = new ArrayList<TblDevTask>();
		TblDevTask tblDevTask=new TblDevTask();
		  List<Integer> Array=new ArrayList();
		try {
			 Long Userd=CommonUtil.getCurrentUserId(request);
			if (StringUtils.isNotBlank(excelDate)) {
				tblDevTask=JSONObject.parseObject(excelDate,TblDevTask.class);
			}

				tblDevTask.setId(Userd);
				
				   if(tblDevTask.getDevTaskStatus()!=null) {
		            	String devStatus=tblDevTask.getDevTaskStatus().toString();
		                if(devStatus.length()==1) {
		                	Array.add(Integer.parseInt(devStatus));
		                	tblDevTask.setDevStatus(Array);
		                }else {
		                	for(int i=0;i<devStatus.length();i++) {
		                		String status=devStatus.substring(i,i+1);
		                		Array.add(Integer.parseInt(status));
		                	}
		                	tblDevTask.setDevStatus(Array);
		                }
		            }
				   SimpleDateFormat sformat=new SimpleDateFormat("yyyy???MM???dd???HH???mm???ss???");
					  long now=System.currentTimeMillis();
					String day=sformat.format(now);
					String fileName = "???????????????"+day+".xls";


					Long[] projectIds = null;
					if( getProjectIds != null && !getProjectIds.equals("")){
						String[] str = getProjectIds.split(",");
						projectIds = (Long[]) ConvertUtils.convert(str,Long.class);
					}



            List<ExtendedField> extendedFields=workTaskService.findFieldByDevId(null);
            List<ExtendedField> extendedFieldsNew=new ArrayList<>();
            List<String> filedNames=new ArrayList<>();

            for(ExtendedField extendedField:extendedFields){
                if(extendedField.getStatus().equals("1")){
                    extendedFieldsNew.add(extendedField);
                    filedNames.add(extendedField.getFieldName());
                }
            }



            list=workTaskService.getExcelAllWork(tblDevTask,projectIds,request);


			//new ExportExcel("", TblDevTask.class).setDataList(list).write(response, fileName).dispose();
            new ExportExcel("", TblDevTask.class,extendedFieldsNew).setDataListNew(list,filedNames).write(response, fileName).dispose();

		} catch (Exception e) {
			e.printStackTrace();
		     logger.error("mes:" + e.getMessage(), e);
		}
	}
	
    
  //????????????
  	@RequestMapping(value="DevStatus")
  	public Map DevStatus(){
  		Map<String, Object>  map=workTaskService.devStatus();
  		return map;
  	}
  	
  	 //????????????
  	@RequestMapping(value="ReqStatus")
  	public Map ReqStatus(){
  		Map<String, Object>  map=workTaskService.ReqStatus();
  		return map;
  	}
  	//??????????????????
  	@RequestMapping(value="ReqSystem")
  	public Map ReqSystem(){
  		Map<String, Object>  map=workTaskService.ReqSystem();
  		return map;
  	}
	//??????????????????
  	@RequestMapping(value="FeatureStatus")
  	public Map FeatureStatus(){
  		Map<String, Object>  map=workTaskService.FeatureStatus();
  		return map;
  	}
  	
  	
  	@RequestMapping(value = "downloadFile")
  	public void downloadFile(HttpServletRequest request, HttpServletResponse response,String fileS3Bucket,String fileS3Key,String fileNameOld) {
  		try {
  			if(!StringUtils.isBlank(fileS3Bucket)&&!StringUtils.isBlank(fileS3Key)&&!StringUtils.isBlank(fileNameOld)) {
  				s3Util.downObject(fileS3Bucket, fileS3Key,fileNameOld, response);
  			}
  			} catch (Exception e) {
  				e.printStackTrace();
  				logger.error("mes:" + e.getMessage(), e);
  			}
  			
  		}
  	/*????????????*/
  	@PostMapping(value="addTaskRemark")
  	public void addTaskRemark(String remark,String attachFiles,HttpServletRequest request) {
  		
  		List<TblDevTaskRemarkAttachement> files = JsonUtil.fromJson(attachFiles, JsonUtil.createCollectionType(ArrayList.class,TblDevTaskRemarkAttachement.class));
  		Long Userid=CommonUtil.getCurrentUserId(request);
  		String userName=CommonUtil.getCurrentUserName(request);
  		String UserAccount=CommonUtil.getCurrentUserAccount(request);
  		TblDevTaskRemark tblTestTaskRemark=JSON.parseObject(remark, TblDevTaskRemark.class);
  		workTaskRemark.addTaskRemark(remark,Userid,userName,UserAccount,files);
  		
  	}
  	 public static String getFileNameNoEx(String filename) { 
         if ((filename != null) && (filename.length() > 0)) { 
             int dot = filename.lastIndexOf('.'); 
             if ((dot >-1) && (dot < (filename.length()))) { 
                 return filename.substring(0, dot); 
             } 
         } 
         return filename; 
     }
  	 	//???????????? 
		@RequestMapping(value="uploadFile")
		public List<Map<String,Object>> uploadFile(@RequestParam ("files") MultipartFile[] files) throws Exception {
			List<Map<String,Object>> attinfos = new ArrayList<Map<String,Object>>();
			Map<String, Object> map = new HashMap<String, Object>();
			try {
				if (files.length > 0 && files !=null) {
					for (MultipartFile file : files) {
						String OldfileName=getFileNameNoEx(file.getOriginalFilename());
						if (!file.isEmpty()) {
							InputStream inputStream = file.getInputStream();
							map = new HashMap<String, Object>();
							String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);//?????????
							String newFileName = UUID.randomUUID().toString().replace("-", "");
							
							Random random = new Random();
							String i = String.valueOf(random.nextInt());
							String keyname = s3Util.putObject(s3Util.getDevTaskBucket(),i,inputStream);
							map.put("fileS3Key", keyname);
							map.put("fileS3Bucket", s3Util.getDevTaskBucket());
							
							map.put("fileNameNew", newFileName);//newFileName
							//map.put("filePath", url);//url
							map.put("fileNameOld",file.getOriginalFilename());//filename
							//map.put("length", file.getSize());
							map.put("fileType",extension );//extension
							
							attinfos.add(map);
						}else {
							//??????????????????
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return attinfos;
		}
		/*????????????*/
		@RequestMapping(value="delFile",method=RequestMethod.POST)
		public Map delFile(String attacheId,HttpServletRequest request) {
			Map<String,Object> result = new HashMap<String,Object>();
			try {
				if(!attacheId.equals("")||!attacheId.equals(null)) {
					
					/*workTaskRemark.updateNo(Long.parseLong(attacheId),request);*/
					result.put("status", "success");
				}
			} catch (Exception e) {
				e.printStackTrace();
				result.put("status", "fail");
			}
			
			
			return result;
			
		}
		
	/**
	 * ?????????????????????systemId ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
	 * 
	 * add by ztt
	 * */
	@RequestMapping("countWorkloadBysystemId")
	public Map<String, Object> countWorkloadBysystemId(Long systemId){
		Map<String, Object> map = new HashMap<>();
		try {
			List<Map<String, Object>> countDatas =  workTaskService.countWorkloadBysystemId(systemId);
			map.put("countDatas", countDatas);
		} catch (Exception e) {
			return super.handleException(e, "???????????????????????????????????????????????????????????????");
		}
		return map;
	}
	
		
}
