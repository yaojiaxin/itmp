package cn.pioneeruniverse.project.controller;

import java.io.PrintWriter;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.HttpUtil;
import cn.pioneeruniverse.project.service.requirement.impl.RequirementServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ntp.TimeStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.common.utils.S3Util;
import cn.pioneeruniverse.project.common.DicConstants;
import cn.pioneeruniverse.project.common.SynRequirementFeatureUtil;
import cn.pioneeruniverse.project.dao.mybatis.RequirementMapper;
import cn.pioneeruniverse.project.dao.mybatis.RequirmentSystemMapper;
import cn.pioneeruniverse.project.entity.ExtendedField;
import cn.pioneeruniverse.project.entity.TblDataDic;
import cn.pioneeruniverse.project.entity.TblRequirementAttachement;
import cn.pioneeruniverse.project.entity.TblRequirementAttention;
import cn.pioneeruniverse.project.entity.TblRequirementFeature;
import cn.pioneeruniverse.project.entity.TblRequirementInfo;
import cn.pioneeruniverse.project.entity.TblRequirementSystem;
import cn.pioneeruniverse.project.feignInterface.DevTaskInterface;
import cn.pioneeruniverse.project.feignInterface.ProjectToSystemInterface;
import cn.pioneeruniverse.project.feignInterface.TestTaskInterface;
import cn.pioneeruniverse.project.service.requirement.RequirementFeatureService;
import cn.pioneeruniverse.project.service.requirement.RequirementService;
import cn.pioneeruniverse.project.service.requirementsystem.RequirementSystemService;
import cn.pioneeruniverse.project.vo.SynRequirementFeature;

@RestController
@RequestMapping("requirement")
public class RequirementController extends BaseController{

	@Autowired
	private DevTaskInterface taskInterface;
	@Autowired
	private TestTaskInterface testInterface;
	@Autowired
	private S3Util s3Util;	
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private RequirementService requirementService;
	@Autowired
	private RequirementFeatureService requirementFeatureService;
	@Autowired
	private RequirementSystemService requirementSystemService;
	@Autowired
	private ProjectToSystemInterface projectToSystemInterface;
	@Autowired
	private RequirmentSystemMapper tblRequirementSystemMapper;
	@Autowired
	private RequirementMapper requirementMapper;
		
	private static Logger log = LoggerFactory.getLogger(RequirementController.class);
		
	@RequestMapping(value="getAllRequirement",method=RequestMethod.POST)
	public String getAllRequirement(HttpServletRequest request,String findRequirment,Integer rows,Integer page){
		JSONObject jsonObj = new JSONObject();
		TblRequirementInfo requirement = new TblRequirementInfo();
		try {
			Long uid = CommonUtil.getCurrentUserId(request);
			LinkedHashMap map = (LinkedHashMap) redisUtils.get(CommonUtil.getToken(request));
			List<String> roleCodes = (List<String>) map.get("roles");
			if (StringUtils.isNotBlank(findRequirment)) {
				requirement = JSONObject.parseObject(findRequirment, TblRequirementInfo.class);
			}
			requirement.setUserId(uid);
			int count= requirementService.getCountRequirement(requirement,roleCodes);
			List<TblRequirementInfo> list = requirementService.getAllRequirement(requirement, page, rows,roleCodes);
			jsonObj.put("page", page);
			if(rows!=null) {
				jsonObj.put("total", (count+rows-1)/rows);
			}
			jsonObj.put("records", count);			
			jsonObj.put("rows", list);
		} catch (Exception e) {
			 e.printStackTrace();
		     log.error("mes:" + e.getMessage(), e);
		}
		return jsonObj.toJSONString();		
	}
	
	@RequestMapping(value="getFunctionCountByReqId",method=RequestMethod.POST)
	public Map<String,Object> getFunctionCountByReqId(HttpServletRequest request,Long reqId){
		Map<String, Object> result = new HashMap<String, Object>();
		try {						
			int count= requirementSystemService.getFunctionCountByReqId(reqId);
			result.put("data", count);
		} catch (Exception e) {
			 e.printStackTrace();
		     log.error("mes:" + e.getMessage(), e);
		}
		return result;
	}
	
	@RequestMapping(value = "findRequirementById", method = RequestMethod.POST)
	public Map<String, Object> findRequirementById(HttpServletRequest request, Long rIds,Long parentIds) {
		Map<String, Object> result = new HashMap<String, Object>();		
		try {
			result = requirementService.findRequirementById(rIds,parentIds);
			List<TblRequirementFeature> trf = requirementFeatureService.findFeatureByRequirementId(rIds);
			//查询关注功能
			Long userId = CommonUtil.getCurrentUserId(request);
			TblRequirementAttention attention = new TblRequirementAttention();
			attention.setRequirementId(rIds);
			attention.setUserId(userId);
			attention.setStatus(1);
			List<TblRequirementAttention> attentionList = requirementService.getAttentionList(attention);
			if (attentionList != null && attentionList.size() > 0) {
				result.put("attentionStatus", 1);
			} else {
				result.put("attentionStatus", 2);
			}
			//查询扩展字段
			List<ExtendedField> extendedFields=requirementService.findRequirementField(rIds);
			result.put("fields", extendedFields);
			
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
			result.put("trf",trf);
		} catch (Exception e) {
			return handleException(e, "获取需求失败");
		}
		return result;
	}
	
	//更新需求关注功能
	@RequestMapping(value = "changeAttention", method = RequestMethod.POST)
	public Map<String, Object> changeAttention(HttpServletRequest request, Long id, Integer attentionStatus) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", "success");
		try {
			if (attentionStatus != null) {
				requirementService.changeAttention(id, attentionStatus, request);
			}
		} catch (Exception e) {
			return super.handleException(e, "更新关注失败");
		}
		return map;
	}
		
	@RequestMapping(value = "toEditRequirementById", method = RequestMethod.POST)
	public Map<String, Object> toEditRequirementById(Long rIds) {
		Map<String, Object> result = new HashMap<String, Object>();		
		try {
			result = requirementService.toEditRequirementById(rIds);
			List<TblRequirementAttachement> attachements = requirementService.getRequirementAttachement(rIds);
			
			//获取扩展字段
			List<ExtendedField> extendedFields=requirementService.findRequirementField(rIds);
			result.put("fields", extendedFields);
			result.put("attachements", attachements);			
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			return handleException(e, "获取需求失败");
		}
		return result;
	}
	
	@RequestMapping(value = "getDataDicList", method = RequestMethod.POST)
	public List<TblDataDic> getDataDicList(String datadictype) {
		String termCode="";
		List<TblDataDic> resultList= new ArrayList<TblDataDic>();
		try {
			if(datadictype!=null&&datadictype.equals("reqStatus")) {
				termCode=DicConstants.req_status;
			}
			if(datadictype!=null&&datadictype.equals("reqSource")) {
				termCode=DicConstants.req_source;
			}
			if(datadictype!=null&&datadictype.equals("reqType")) {
				termCode=DicConstants.req_type;
			}			
			String result =redisUtils.get(termCode).toString();
			if (!StringUtils.isBlank(result)) {
				Map<String, Object> maps = JSON.parseObject(result);
		        for (Map.Entry<String, Object> entry : maps.entrySet()){  
		        	TblDataDic tdd= new TblDataDic();
		        	tdd.setValueCode(entry.getKey().toString());
		        	tdd.setValueName(entry.getValue().toString());
		        	resultList.add(tdd);  
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		    log.error("mes:" + e.getMessage(), e);
		}
		return resultList;
	}

	@RequestMapping(value = "updateRequirementData",method = RequestMethod.POST)
	public synchronized void  synReq(@RequestBody String requirementData,HttpServletRequest request,HttpServletResponse response) {
		String ip = HttpUtil.getIPAddress(request);
		logger.info("来访IP:"+ip);
		try {
			logger.info("开始同步需求信息数据"+requirementData);
			if (!StringUtils.isBlank(requirementData)) {
				Map<String, Object> data = jsonToMap(jsonToMap(requirementData).get("requestBody").toString());
				String requirementList=data.get("requirementList").toString();						
				String reqSystemList=data.get("reqSystemList").toString();
				List<TblRequirementFeature> featureList =strToFeature(data.get("taskList").toString());
				Map<String,Object> map = requirementService.updateRequirementDataItmp(requirementList);
				Integer status = Integer.parseInt(map.get("status").toString());
				if(status==0) {
					status = requirementService.updateRequirementDataTmp(map.get("reqList").toString());
					requirementSystemService.updateReqSystemData(reqSystemList,map.get("reqId").toString());
					for(TblRequirementFeature feature:featureList) {
						if(feature.getTaskType().equals("taskdevelop")) {
							if(feature.getRequirementFeatureStatus().equals("03")){
								feature.setRequirementFeatureStatus("00");
								requirementFeatureService.updateStatusByTmp(feature);
							}else{
								requirementFeatureService.updateTaskDataItmp(feature,map.get("reqId").toString());
							}
						}else if(feature.getTaskType().equals("tasktest")){
							if(feature.getRequirementFeatureStatus().equals("03")) {
								feature.setRequirementFeatureStatus("00");
								requirementFeatureService.updateStatusItmp(feature);
							}else{
								requirementFeatureService.updateTaskDataTmp(feature, map.get("reqId").toString());
							}
						}else{
							feature.setRequirementFeatureStatus("00");
							requirementFeatureService.updateStatusItmp(feature);
							requirementFeatureService.updateStatusByTmp(feature);
						}
					}

					if(map.get("reqStatus").toString().toUpperCase(Locale.ENGLISH).equals("REQ_CANCELED")) {
						taskInterface.changeCancelStatus(Long.valueOf(map.get("reqId").toString()));
						testInterface.changeCancelStatus(Long.valueOf(map.get("reqId").toString()));
					}
				}				
				if(status>0) {
					Map<String,Object> head= new HashMap<>();
		        	Map<String,Object> map1= new HashMap<>();
		        	map1.put("consumerSeqNo","itmgr");	
		        	map1.put("status",0);
		        	map1.put("seqNo","");
		        	map1.put("providerSeqNo","");
		        	map1.put("esbCode","");
		        	map1.put("esbMessage","");          
		        	map1.put("appCode","0");
		        	map1.put("appMessage","同步需求信息成功");	
	                head.put("responseHead",map1);
					PrintWriter writer = response.getWriter();				
					writer.write(new JSONObject(head).toJSONString());
				}
				
				//发送消息
				String string = map.get("reqList").toString();
//				JSONObject jsonObject = JSONObject.parseObject(string);
//				TblRequirementInfo requirementInfo = JSONObject.toJavaObject(jsonObject, TblRequirementInfo.class);
				List<TblRequirementInfo> resultList = JSONObject.parseArray(string, TblRequirementInfo.class); //把字符串转换成list
				String str = map.get("synStatus").toString();
				for(TblRequirementInfo requirementInfo : resultList) {
//				TblRequirementInfo requirementInfo = resultList.get(0);
					List<TblRequirementSystem> list = tblRequirementSystemMapper.getReqSystemByReqId(requirementInfo.getId());
					requirementInfo.setList(list);
					if(str.equals("insert")) {
						requirementService.sendAddMessage(request, requirementInfo);
						//给项目管理岗发送邮件和微信 --ztt
						Map<String,Object> emWeMap = new HashMap<String, Object>();
						emWeMap.put("messageTitle", "【IT开发测试管理系统】- 收到一个新分配的需求");
						emWeMap.put("messageContent","您收到一个新的需求：“"+ requirementInfo.getRequirementCode()+" | "+requirementInfo.getRequirementName()+"，请及时处理。”");
						String userIds = requirementMapper.getProManageUserIds(requirementInfo.getId());//获取该需求所在系统所在项目的项目管理岗 
						emWeMap.put("messageReceiver",userIds);//接收人 项目管理岗
						emWeMap.put("sendMethod", 3);//发送方式 3 邮件和微信
						projectToSystemInterface.sendMessage(JSON.toJSONString(emWeMap));
					}
					else if(str.equals("update")) {
						TblRequirementAttention attention = new TblRequirementAttention();
						attention.setRequirementId(requirementInfo.getId());
						attention.setStatus(1);
						List<TblRequirementAttention> attentionList = requirementService.getAttentionList(attention);
						if (attentionList != null && attentionList.size() > 0) {
							requirementService.sendEditMessage(request, requirementInfo, attentionList);
						}
					}
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setHeader("appCode", "500");
			response.setHeader("appMessage", e.getMessage());
			log.error("同步出错" + ":" + e.getMessage(), e);
		}
	}
	
	//新增需求
	@RequestMapping(value = "addRequirement", method = RequestMethod.POST)
	public Map<String, Object> addRequirement(HttpServletRequest request,TblRequirementInfo requirementInfo,String reqSysList,
			String startDate, String endDate,String workload1,String time) {
//		JSONObject.parseObject(reqSysList, TblRequirementSystem.class);
		List<TblRequirementSystem> array = JSONObject.parseArray(reqSysList, TblRequirementSystem.class);
		requirementInfo.setList(array);
		Map<String, Object> map = new HashMap<>();
		List<TblRequirementInfo> list = requirementService.findRequirementByName(requirementInfo);
		if (!list.isEmpty()) {
			map.put("status", "repeat");
			return map;
		}
		List<TblRequirementSystem> reqsysList = requirementInfo.getList();
		if(reqsysList != null && reqsysList.size()>1) {
			for(int i = 0; i < reqsysList.size(); i ++) {
				for(int j = i+1; j < reqsysList.size(); j ++) {
					if(reqsysList.get(i).getSystemId().equals(reqsysList.get(j).getSystemId()) && 
							reqsysList.get(i).getAssetSystemTreeId().equals(reqsysList.get(j).getAssetSystemTreeId())) {
						map.put("status", "003");
						return map;
					}
				}
			}
		}
		DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (startDate != null && !"".equals(startDate)) {
				Date planStartDate = dFormat.parse(startDate);
				requirementInfo.setPlanOnlineDate(planStartDate);
			}
			if (endDate != null && !"".equals(endDate)) {
				Date planEndDate = dFormat.parse(endDate);
				requirementInfo.setActualOnlineDate(planEndDate);
			}						
			if (workload1 != null && !"".equals(workload1)) {				
				requirementInfo.setWorkload(Double.valueOf(workload1));
			}
			if(time != null && !time.equals("")) {
				SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
				Date date = datetimeFormatter1.parse(time);
				Timestamp closeTime = new Timestamp(date.getTime());
				requirementInfo.setCloseTime(closeTime);
			}
			map=requirementService.insertRequirementItmp(requirementInfo, request);			
			requirementService.insertRequirementTmp(map.get("requirementInfo").toString());
			map.put("status", "success");
			//添加消息
			requirementService.sendAddMessage(request,requirementInfo);
		} catch (Exception e) {
			return super.handleException(e, "新增需求失败");

		}
		return map;
	}
	

	
	//编辑需求
	@RequestMapping(value = "editRequirement", method = RequestMethod.POST)
	public Map<String, Object> editRequirement(HttpServletRequest request,TblRequirementInfo requirementInfo,String reqSysList,
			String startDate, String endDate,String workload1,String time) {
		List<TblRequirementSystem> array = JSONObject.parseArray(reqSysList, TblRequirementSystem.class);
		requirementInfo.setList(array);
		Map<String, Object> map = new HashMap<>();
		List<TblRequirementSystem> reqsysList = requirementInfo.getList();
		if(reqsysList != null && reqsysList.size()>1) {
			for(int i = 0; i < reqsysList.size(); i ++) {
				for(int j = i+1; j < reqsysList.size(); j ++) {
					if(reqsysList.get(i).getSystemId().equals(reqsysList.get(j).getSystemId()) && 
							reqsysList.get(i).getAssetSystemTreeId().equals(reqsysList.get(j).getAssetSystemTreeId())) {
						map.put("status", "003");
						return map;
					}
				}
			}
		}
		DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (startDate != null && !"".equals(startDate)) {
				Date planStartDate = dFormat.parse(startDate);
				requirementInfo.setPlanOnlineDate(planStartDate);
			}
			if (endDate != null && !"".equals(endDate)) {
				Date planEndDate = dFormat.parse(endDate);
				requirementInfo.setActualOnlineDate(planEndDate);
			}						
			if (workload1 != null && !"".equals(workload1)) {				
				requirementInfo.setWorkload(Double.valueOf(workload1));
			}
			if(time != null && !time.equals("")) {
				SimpleDateFormat datetimeFormatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); 
				Date date = datetimeFormatter1.parse(time);
				Timestamp closeTime = new Timestamp(date.getTime());
				requirementInfo.setCloseTime(closeTime);
			}
			map=requirementService.updateRequirementItmp(requirementInfo, request);
			requirementService.updateRequirementTmp(map.get("requirementInfo").toString());
			map.put("status", "success");
			
			//关注的需求有更新发送消息
			TblRequirementAttention attention = new TblRequirementAttention();
			attention.setRequirementId(requirementInfo.getId());
			attention.setStatus(1);
			List<TblRequirementAttention> attentionList = requirementService.getAttentionList(attention);
			if (attentionList != null && attentionList.size() > 0) {
				//编辑发送消息
				requirementService.sendEditMessage(request, requirementInfo, attentionList);
			}
			
		} catch (Exception e) {
			return super.handleException(e, "新增需求失败");
		}
		return map;
	}
	
	/*上传附件*/
	@RequestMapping(value = "uploadFile")
	public Map<String, Object> uploadFile(@RequestParam("files") MultipartFile[] files,
			@RequestParam("reqId") Long reqId, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<>();
		result.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Map<String, Object> map = requirementService.uploadFileItmp(files, reqId, request);
			if (map.get("message").toString().equals("NULL")) {
				return handleException(new Exception(), "上传需求附件失败");
			}else {
				requirementService.uploadFileTmp(map.get("listAtta").toString());
			}
		} catch (Exception e) {
			return handleException(e, "上传需求附件失败");
		}
		return result;
	}
	 
	 /**
     * 移除附件
     * @return
     */
    @RequestMapping(value = "removeAtt",method = RequestMethod.POST)
    public Map<String, Object> removeAtt(Long[] ids,HttpServletRequest request){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        TblRequirementAttachement atta=new TblRequirementAttachement();
        try {
            if (ids == null){
                return handleException(new Exception(), "删除附件失败");
            }
            for(int i=0;i<ids.length;i++) {
            	atta.setId(ids[i]);
            	atta=requirementService.removeAttItmp(atta,request);
            	requirementService.removeAttTmp(atta);
            }           
        } catch (Exception e) {
            return handleException(e, "删除附件失败");
        }
        return result;
    }
        
    /*下载附件*/
    @RequestMapping(value= "downloadFile")
    public void downloadFile(TblRequirementAttachement atta,HttpServletResponse response) {
    	try {
        s3Util.downObject(atta.getFileS3Bucket(), atta.getFileS3Key(), atta.getFileNameOld(), response);
    	}catch (Exception e) {
			e.printStackTrace();
			log.error("mes:" + e.getMessage(), e);
		}
    }
    //导出excel
    @RequestMapping(value="getExcelRequirement",method=RequestMethod.POST)
	public List<TblRequirementInfo> getExcelRequirement(String findRequirment,Long uid,@RequestBody List<String> roleCodes){
		TblRequirementInfo requirement = new TblRequirementInfo();
		List<TblRequirementInfo> list =new ArrayList<TblRequirementInfo>();
		try {
			if (StringUtils.isNotBlank(findRequirment)) {
				requirement = JSONObject.parseObject(findRequirment, TblRequirementInfo.class);
			}
			requirement.setUserId(uid);
			list = requirementService.getAllRequirement(requirement, null, null,roleCodes);
			
		} catch (Exception e) {
			 e.printStackTrace();
		     log.error("mes:" + e.getMessage(), e);
		}
		return list;		
	}
	
	//bootstrap
	@RequestMapping(value="getAllRequirement2",method=RequestMethod.POST)
	public Map<String, Object> getAllRequirement2(HttpServletRequest request,TblRequirementInfo requirement,Integer pageSize,Integer pageNumber){
		Map<String, Object> map = new HashMap<>();
		try {
			int count= requirementService.getAllRequirementCount(requirement);
			List<Map<String, Object>> list = requirementService.getAllRequirement2(requirement, pageNumber, pageSize);
			map.put("total", count);
			map.put("rows", list);
		} catch (Exception e) {
			 e.printStackTrace();
		     log.error("mes:" + e.getMessage(), e);
		}
		return map;      
	}	
	//json转map
  	public static Map<String, Object> jsonToMap(String str) {
  		Map<String, Object> mapTypes = JSON.parseObject(str);
  		return mapTypes;  
    }
  	
  	public List<TblRequirementFeature> strToFeature(String str) {
  		List<TblRequirementFeature> featureList=new ArrayList<TblRequirementFeature>();
  		
  		List<SynRequirementFeature> resultList = JSONObject.parseArray(str, SynRequirementFeature.class);
  	    for (SynRequirementFeature synFeature : resultList) {
  	        TblRequirementFeature trf = new TblRequirementFeature();
  	        trf = SynRequirementFeatureUtil.SynRequirementFeatures(synFeature);
  	        featureList.add(trf);
  	    }
  	    return featureList;
  	}
  	
  	//获取扩展字段
  	@RequestMapping(value="getRequirementFiled")
	public  Map<String,Object> getRequirementFiled(HttpServletRequest request){
		Map<String,Object> map = new HashMap<String,Object>();

		List<ExtendedField> extendedFields=requirementService.findRequirementField(null);
		map.put("fields", extendedFields);
		return  map;

	}
  	
  	@RequestMapping(value="getRequirementFiled2")
  	public  Map<String,Object> getRequirementFiled2(Long id){
  		Map<String,Object> map = new HashMap<String,Object>();
  		
  		List<ExtendedField> extendedFields=requirementService.findRequirementField(id);
  		map.put("fields", extendedFields);
  		return  map;
  		
  	}
  	
  	@RequestMapping(value="getRequirementsByIds",method=RequestMethod.POST)
  	public List<String> getRequirementsByIds(String reqIds){
  		List<String> list = new ArrayList<>();
  		try {
  			list = requirementService.getRequirementsByIds(reqIds);
		} catch (Exception e) {
			 e.printStackTrace();
		     log.error("mes:" + e.getMessage(), e);
		}
		return list;
  	}
  	
  	@RequestMapping(value="getsystems",method=RequestMethod.POST)
  	public List<String> getsystems(Long id){
  		List<String> list = new ArrayList<>();
  		try {
  			list = requirementService.getsystems(id);
		} catch (Exception e) {
			 e.printStackTrace();
		     log.error("mes:" + e.getMessage(), e);
		}
  		return list;
  	}
  	
}
