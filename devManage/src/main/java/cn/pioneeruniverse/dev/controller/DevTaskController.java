package cn.pioneeruniverse.dev.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pioneeruniverse.dev.entity.*;
import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.StringUtil;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.common.utils.BrowserUtil;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.ExportExcel;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.common.utils.S3Util;
import cn.pioneeruniverse.dev.dao.mybatis.TblRequirementFeatureMapper;
import cn.pioneeruniverse.dev.feignInterface.DevManageToSystemInterface;
import cn.pioneeruniverse.dev.feignInterface.DevManageToTestManageInterface;
import cn.pioneeruniverse.dev.service.devtask.DevTaskService;
import cn.pioneeruniverse.dev.service.displayboard.DisplayBoardService;
import cn.pioneeruniverse.dev.service.systeminfo.ISystemInfoService;
import cn.pioneeruniverse.dev.service.worktask.impl.WorkTaskServiceImpl;

/**
 * ??????????????????controller
 * 
 * @author:tingting
 * @version:2018???11???12??? ??????11:12:54
 */

@RestController
@RequestMapping("devtask")
public class DevTaskController extends BaseController {

	@Autowired
	private DevTaskService devTaskService;
	@Autowired
	private ISystemInfoService systemInfoService;
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private WorkTaskServiceImpl workTaskServiceImpl;
	@Autowired
	private S3Util s3Util;
	@Autowired
	private TblRequirementFeatureMapper requirementFeatureMapper;
	@Autowired
	private DevManageToTestManageInterface devManageToTestManageInterface;
	@Autowired
	private DisplayBoardService displayBoardService;
	@Autowired
	private DevManageToSystemInterface devManageToSystemInterface;

	

	@RequestMapping(value = "getAllDevTask", method = RequestMethod.POST)
	public Map<String, Object> getAllDevTask(TblRequirementFeature requirementFeature, Integer page, Integer rows,HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Long uid = CommonUtil.getCurrentUserId(request);
		List<DevTaskVo> reqFeatures = devTaskService.getAllReqFeature(requirementFeature, uid,page, rows);
		int total = devTaskService.getAllReqFeatureCount(requirementFeature, uid);
		map.put("rows", reqFeatures);
		map.put("records", total);
		map.put("total", total%rows == 0 ? total/rows:total/rows+1 );
		map.put("page", page);
		return map;
	}


	//????????????
	//@RequestMapping(value="exportTemplet")
//	public Map<String, Object> exportTemplet(HttpServletRequest request, HttpServletResponse response){
//		HashMap<String, Object> map = new HashMap<>();
//		map.put("status", Constants.ITMP_RETURN_SUCCESS);
////		try {
////			devTaskService.exportTemplet(request, response);
////		} catch (Exception e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
////			logger.error("mes:" + e.getMessage(), e);
////		}
//
//		//?????????????????????
//		String excelPath = request.getSession().getServletContext().getRealPath("/Excel/"+"xx.xls");
//		String fileName = "xx.xls".toString(); // ????????????????????????
//		// ????????????
//		InputStream inStream = new FileInputStream(excelPath);//?????????????????????
//		// ?????????????????????
//		response.reset();
//		response.setContentType("bin");
//		response.addHeader("Content-Disposition",
//				"attachment;filename=" + URLEncoder.encode("xx.xls", "UTF-8"));
//		// ???????????????????????????
//		byte[] b = new byte[200];
//		int len;
//
//		while ((len = inStream.read(b)) > 0){
//			response.getOutputStream().write(b, 0, len);
//		}
//		inStream.close();
//
//	}



	@RequestMapping(value = "getAllDevTask2", method = RequestMethod.POST)
	public JqGridPage<DevTaskVo> getAllDevTask2(DevTaskVo devTaskVo, HttpServletRequest request,
			HttpServletResponse response) {
		Long uid = CommonUtil.getCurrentUserId(request);
		devTaskVo.setUserId(uid);
		LinkedHashMap map = (LinkedHashMap) redisUtils.get(CommonUtil.getToken(request));
		List<String> roleCodes = (List<String>) map.get("roles"); 
		
		JqGridPage<DevTaskVo> jqGridPage = devTaskService.selectAll(new JqGridPage<DevTaskVo>(request, response),
				devTaskVo,roleCodes);
		return jqGridPage;
	}

	@RequestMapping(value = "findDeployByReqFeatureId", method = RequestMethod.POST)
	public Map<String , Object> findDeployByReqFeatureId(Long featureId) {
		Map<String , Object> map = new HashMap<>();
		try {
			String deployName = devTaskService.findDeployByReqFeatureId(featureId);
			map.put("deployName", deployName);
		} catch (Exception e) {
			return super.handleException(e, "?????????????????????");
		}
		return map;
	}

	//???????????????????????????????????????
	@RequestMapping("getBrotherDiffWindow")
	public Map<String, Object> getBrotherDiffWindow(DevTaskVo devTaskVo){
		Map<String , Object> map = new HashMap<>();
		//????????????????????????????????????????????????????????????????????????????????????
		List<Long> list = requirementFeatureMapper.findWindowByReqId(devTaskVo.getRequirementId());
		List<Long> list2 = new ArrayList<>();
		for (Long windowId : list) {
			if(windowId != null) {
				list2.add(windowId);
			}
		}
		if(list2 != null && list2.size() > 0) {
			List<Map<String, Object>> brothers = requirementFeatureMapper.findBrotherDiffWindow(devTaskVo);
			map.put("brothers", brothers);
		}
		return map;
	}
	// ????????????????????????????????????????????? ?????????
	@RequestMapping(value = "getOneDevTask3", method = RequestMethod.POST)
	public Map<String, Object> getOneDevTask3(Long id) {
		Map<String, Object> map = new HashMap<>();
		try {
			map = devTaskService.getOneDevTask(id);
			map.putAll(toAddData());
			if (map.containsKey("createType")&&"1".equals(map.get("createType").toString())) {//???????????????????????????
				// ????????????
				List<TblRequirementFeatureAttachement> attachements = devTaskService.findAtt(id);
				map.put("attachements", attachements);
			}
			//?????????????????????
			List<TblDefectInfo> defectInfos = devTaskService.findDftByReqFId(id);

			//??????????????????(????????????)
			List<ExtendedField> extendedFields=devTaskService.findFieldByReqId(id);
			map.put("fields", extendedFields);

			if (map.get("systemId") != null){
				String systemId=map.get("systemId").toString();
				TblSystemInfo tblSystemInfo=systemInfoService.getOneSystemInfo(Long.parseLong(systemId));
				map.put("systemCode",tblSystemInfo.getSystemCode());
				map.put("systemName",tblSystemInfo.getSystemName());
			}

			map.put("defectInfos", defectInfos);

		} catch (Exception e) {
			return super.handleException(e, "?????????????????????");
		}
		
		return map;
	}
		
	// ?????????????????????????????? ??? ?????????????????? ??? ????????????????????? ?????????
	@RequestMapping(value = "getOneDevTask", method = RequestMethod.POST)
	public Map<String, Object> getOneDevTask(Long id) {
	//	Map<String, Object> map = getOneDevTask3(id);
		Map<String, Object> map = devTaskService.getOneDevTask(id);
		if (map.containsKey("createType")&&"1".equals(map.get("createType").toString())) {//???????????????????????????
			// ????????????
			List<TblRequirementFeatureAttachement> attachements = devTaskService.findAtt(id);
			map.put("attachements", attachements);
		}
		//?????????????????????
		List<TblDefectInfo> defectInfos = devTaskService.findDftByReqFId(id);
		map.put("defectInfos", defectInfos);
		List<Map<String, Object>> devTasks = devTaskService.findByReqFeature(id);
		map.put("list", devTasks);
		// ????????????????????????
		if (map.containsKey("requirementId")) {
			Long requirementId = (Long) map.get("requirementId");
			List<Map<String, Object>> brotherReqFeatures = devTaskService.findBrother(requirementId, id);
			map.put("brother", brotherReqFeatures);
		}
		return map;
	}
	
	// ??????????????????????????????  ??????????????????  ????????????????????? ?????? ?????? ??????
	@RequestMapping(value = "getOneDevTask2", method = RequestMethod.POST)
	public Map<String, Object> getOneDevTask2(HttpServletRequest request, Long id) {
		Map<String, Object> map = getOneDevTask(id);
		//???????????????
		if(map.get("assetSystemTreeId")!=null) {
		   map.put("systemTreeName",getTreeName(map.get("assetSystemTreeId").toString()));
		}


		// ???????????????????????????
		List<TblRequirementFeatureRemark> remarks = devTaskService.findRemark(id);
		map.put("remarks", remarks);
		// ???????????????????????????
		List<TblRequirementFeatureLog> logs = devTaskService.findLog(id);
		map.put("logs", logs);
		//??????????????????(????????????)
		List<ExtendedField> extendedFields=devTaskService.findFieldByReqId(id);
		map.put("fields", extendedFields);
		
		//??????????????????
		Long userId = CommonUtil.getCurrentUserId(request);
		TblRequirementFeatureAttention attention = new TblRequirementFeatureAttention();
		attention.setRequirementFeatureId(id);
		attention.setUserId(userId);
		attention.setStatus(1);
		List<TblRequirementFeatureAttention> attentionList = devTaskService.getAttentionList(attention);
		if (attentionList != null && attentionList.size() > 0) {
			map.put("attentionStatus", 1);
		} else {
			map.put("attentionStatus", 2);
		}

		return map;
	}
	
	//??????????????????????????????
	@RequestMapping(value = "changeAttention", method = RequestMethod.POST)
	public Map<String, Object> changeAttention(HttpServletRequest request, Long id, Integer attentionStatus) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", "success");
		try {
			if (attentionStatus != null) {
				devTaskService.changeAttention(id, attentionStatus, request);
			}
		} catch (Exception e) {
			return super.handleException(e, "??????????????????");
		}
		return map;
	}

	@RequestMapping(value = "toAddData", method = RequestMethod.POST)
	public Map<String, Object> toAddData() {
		Map<String, Object> map = new HashMap<>();
		try {
			//List<TblCommissioningWindow> windows = devTaskService.getWindows();
			//map.put("cmmitWindows", windows);
			String termCode = "TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_SOURCE";
			List<TblDataDic> dataDics = getDataFromRedis(termCode);
			map.put("dics", dataDics);
			List<TblDataDic> reqFeaturePriorityList = getDataFromRedis("TBL_REQUIREMENT_FEATURE_PRIORITY");
			map.put("reqFeaturePriorityList", reqFeaturePriorityList);

			List<ExtendedField> extendedFields=devTaskService.findFieldByReqId(null);
			map.put("fields", extendedFields);


		} catch (Exception e) {
			return super.handleException(e, "?????????????????????");
		}
		return map;
	}

	@RequestMapping(value = "addDevTask", method = RequestMethod.POST)
	public Map<String, Object> addDevTask(HttpServletRequest request, TblRequirementFeature requirementFeature,
			String startDate, String endDate, String attachFiles,String defectIds,String dftJsonString,Double dftActualWorkload,String defectRemark) {
		logger.info("??????????????????:"+requirementFeature);
		Map<String, Object> map = new HashMap<>();
		List<TblRequirementFeatureAttachement> files = JsonUtil.fromJson(attachFiles,
				JsonUtil.createCollectionType(ArrayList.class, TblRequirementFeatureAttachement.class));
		DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (startDate != null && !"".equals(startDate)) {
				Date planStartDate = dFormat.parse(startDate);
				requirementFeature.setPlanStartDate(planStartDate);
			}
			if (endDate != null && !"".equals(endDate)) {
				Date planEndDate = dFormat.parse(endDate);
				requirementFeature.setPlanEndDate(planEndDate);
			}
			requirementFeature.setFeatureCode(getFeatureCode());
			requirementFeature.setCreateType(1);// ???????????? ??????
			requirementFeature.setRequirementFeatureStatus("01");// ??????????????? ?????????
			requirementFeature.setCreateBy(CommonUtil.getCurrentUserId(request));
			requirementFeature.setCreateDate(new Timestamp(new Date().getTime()));
			devTaskService.addDevTask(requirementFeature, files,defectIds,dftActualWorkload,defectRemark,dftJsonString, request);



            sendAddMessage(request,requirementFeature);


			map.put("status", "success");
		} catch (Exception e) {
			return super.handleException(e, "????????????????????????");

		}
		return map;
	}

    private void sendAddMessage(HttpServletRequest request,TblRequirementFeature tblRequirementFeature){
		logger.info("???????????????????????????"+tblRequirementFeature);
		tblRequirementFeature.setManageUserId(1);
		Map<String,Object> map=new HashMap<>();
		map.put("messageTitle","????????????????????????????????????");
		map.put("messageContent",tblRequirementFeature.getFeatureCode()+"|"+tblRequirementFeature.getFeatureName());
		map.put("messageReceiverScope",2);
		//???????????? 1-- ??????????????????
		map.put("messageSource",1);
		map.put("messageType",1);
		map.put("systemId",tblRequirementFeature.getSystemId());
		map.put("projectId",tblRequirementFeature.getProjectId());
		if(tblRequirementFeature.getExecuteUserId()!=tblRequirementFeature.getManageUserId()) {
			map.put("messageReceiver", tblRequirementFeature.getExecuteUserId() + "," + tblRequirementFeature.getManageUserId());
		}else{
			map.put("messageReceiver", tblRequirementFeature.getExecuteUserId());
		}
		devManageToSystemInterface.insertMessage(JSON.toJSONString(map));

	}


	private void sendTrsMessage(HttpServletRequest request,TblRequirementFeature tr){


		TblRequirementFeature tblRequirementFeature=devTaskService.getOneFeature(String.valueOf(tr.getId()));
		Map<String,Object> map=new HashMap<>();
		map.put("messageTitle","????????????????????????????????????");
		map.put("messageContent",tblRequirementFeature.getFeatureCode()+"|"+tblRequirementFeature.getFeatureName());
		map.put("messageReceiverScope",2);
		map.put("messageReceiver", tr.getExecuteUserId());

		devManageToSystemInterface.insertMessage(JSON.toJSONString(map));

	}

	@RequestMapping(value = "updateDevTask", method = RequestMethod.POST)
	public Map<String, Object> updateDevTask(HttpServletRequest request, TblRequirementFeature requirementFeature,
			String attachFiles, String pstartDate, String pendDate, String aendDate, String astartDate,String defectIds) {
		Map<String, Object> map = new HashMap<>();
		//????????????id
		long id=requirementFeature.getId();
		//??????????????????????????????
		TblRequirementFeature oldTblRequirementFeature=devTaskService.getOneFeature(String.valueOf(id));






		List<TblRequirementFeatureAttachement> files = JsonUtil.fromJson(attachFiles,
				JsonUtil.createCollectionType(ArrayList.class, TblRequirementFeatureAttachement.class));


		DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (pstartDate != null && !"".equals(pstartDate)) {
				Date planStartDate = dFormat.parse(pstartDate);
				requirementFeature.setPlanStartDate(planStartDate);
			}
			if (pendDate != null && !"".equals(pendDate)) {
				Date planEndDate = dFormat.parse(pendDate);
				requirementFeature.setPlanEndDate(planEndDate);
			}
			if (astartDate != null && !"".equals(astartDate)) {
				Date actualStartDate = dFormat.parse(astartDate);
				requirementFeature.setActualStartDate(actualStartDate);
			}
			if (aendDate != null && !"".equals(aendDate)) {
				Date actualEndDate = dFormat.parse(aendDate);
				requirementFeature.setActualEndDate(actualEndDate);
			}
			requirementFeature.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
			requirementFeature.setLastUpdateDate(new Timestamp(new Date().getTime()));
			devTaskService.updateDevTask(requirementFeature, files,defectIds, request);

			//????????????????????????????????????
			TblRequirementFeatureAttention attention = new TblRequirementFeatureAttention();
			attention.setRequirementFeatureId(id);
			attention.setStatus(1);
			List<TblRequirementFeatureAttention> attentionList = devTaskService.getAttentionList(attention);
			if (attentionList != null && attentionList.size() > 0) {
				//??????????????????
				sendEditMessage(request,oldTblRequirementFeature,requirementFeature, attentionList);
			}
			map.put("status", "success");
		} catch (Exception e) {
			return super.handleException(e, "????????????????????????");
		}

		return map;
	}


	private void sendEditMessage(HttpServletRequest request,TblRequirementFeature oldFeature,TblRequirementFeature newFeature, 
			List<TblRequirementFeatureAttention> attentionList){
		String userIds = "";
		for (TblRequirementFeatureAttention attention : attentionList) {
			userIds += attention.getUserId() + ",";
		}
		if(StringUtil.isNotEmpty(userIds)){
			userIds = userIds.substring(0, userIds.length() - 1);
			Map<String,Object> map=new HashMap<>();
			map.put("messageTitle","?????????????????????????????????");
			map.put("messageContent",oldFeature.getFeatureCode()+"|"+newFeature.getFeatureName());
			map.put("messageReceiverScope",2);
			map.put("messageReceiver", userIds);
			//???????????? 4--??????????????????
			map.put("messageSource",4);
			map.put("systemId",oldFeature.getSystemId());
			devManageToSystemInterface.insertMessage(JSON.toJSONString(map));
		}

	}

	// ???????????????????????????(?????????????????????????????????????????????)
	@RequestMapping(value = "splitDevTasks", method = RequestMethod.POST)
	public Map<String, Object> splitDevTasks(HttpServletRequest request, String devTasks, Long id,
											 String requirementFeatureStatus) {
		Map<String, Object> map = new HashMap<>();
		try {        
			List<TblDevTask> list = new ArrayList<TblDevTask>();
			list = JSONObject.parseArray(devTasks, TblDevTask.class);
			for (TblDevTask devTask : list) {
				devTask.setDevTaskCode(workTaskServiceImpl.getDevCode());
				devTaskService.addWorkTask(id, devTask, request);
				// ??????id???????????????????????????????????????
				if (!"02".equals(requirementFeatureStatus)) {
					devTaskService.updateStatus(id, request);
				}
			}

			//????????????
			for(TblDevTask devTask : list){
				sendAddWorkMessage(request,devTask)	;
			}
			map.put("status", "success");
		} catch (Exception e) {
			return super.handleException(e, "????????????????????????");
		}
		return map;




	}

	private void sendAddWorkMessage(HttpServletRequest request,TblDevTask devTask){

		Map<String,Object> map=new HashMap<>();
		map.put("messageTitle","??????????????????????????????????????????");
		map.put("messageContent",devTask.getDevTaskCode()+"|"+devTask.getDevTaskName());
		map.put("messageReceiverScope",2);
		map.put("messageReceiver",devTask.getDevUserId());
		devManageToSystemInterface.insertMessage(JSON.toJSONString(map));

	}




	// ??????????????????(?????????????????????????????????????????????)
	@RequestMapping(value = "splitDevTask", method = RequestMethod.POST)
	public Map<String, Object> splitDevTask(HttpServletRequest request, TblDevTask devTask, Long id, String startDate,
			String endDate, String requirementFeatureStatus) {
		Map<String, Object> map = new HashMap<>();
		DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {

			if (startDate != null && !"".equals(startDate)) {
				Date planStartDate = dataFormat.parse(startDate);
				devTask.setPlanStartDate(planStartDate);
			}
			if (endDate != null && !"".equals(endDate)) {
				Date planEndDate = dataFormat.parse(endDate);
				devTask.setPlanEndDate(planEndDate);
			}
			devTask.setDevTaskCode(workTaskServiceImpl.getDevCode());
			devTaskService.addWorkTask(id,devTask,request);
			// ??????id???????????????????????????????????????
			if (!"02".equals(requirementFeatureStatus)) {
				devTaskService.updateStatus(id, request);
			}

			map.put("status", "success");
		} catch (Exception e) {
			return super.handleException(e, "????????????????????????");
		}
		return map;
	}

	// ??????????????????(??????)
	@RequestMapping(value = "handleDevTask", method = RequestMethod.POST)
	public Map<String, Object> handleDevTask(HttpServletRequest request, TblRequirementFeature requirementFeature,
			String startDate, String endDate, String actualWorkload,String handleRemark) {
		Map<String, Object> map = new HashMap<>();
		DateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (startDate != null && !"".equals(startDate)) {
				Date actualStartDate = dataFormat.parse(startDate);
				requirementFeature.setActualStartDate(actualStartDate);
			}
			if (endDate != null && !"".equals(endDate)) {
				Date actualEndDate = dataFormat.parse(endDate);
				requirementFeature.setActualEndDate(actualEndDate);
			}
			if (actualWorkload != null && !"".equals(actualWorkload)) {
				requirementFeature.setActualWorkload(Double.parseDouble(actualWorkload));
			}
			// ???????????????????????????
			requirementFeature.setRequirementFeatureStatus("03");
			requirementFeature.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
			requirementFeature.setLastUpdateDate(new Timestamp(new Date().getTime()));
			devTaskService.handleDevTask(requirementFeature, request,handleRemark);
			map.put("status", "success");
		} catch (Exception e) {
			return super.handleException(e, "????????????????????????");
		}
		return map;
	}

	// ??????
	@RequestMapping(value = "transfer")
	public Map<String, Object> transfer(TblRequirementFeature requirementFeature,String transferRemark, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		try {
			devTaskService.updateTransfer(requirementFeature,transferRemark, request);
			map.put("status", "success");
			sendTrsMessage(request,requirementFeature);
		} catch (Exception e) {
			return super.handleException(e, "????????????????????????");
		}
		return map;
	}

	// ????????????(??????????????????????????????????????????)
	@RequestMapping("merge")
	public Map<String, Object> mergeSynDevTask(TblRequirementFeature requirementFeature, Long synId,
			HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		try {
			// ????????????taskId??????????????????????????? ??????????????????????????????????????????????????? 
			devTaskService.mergeDevTask(requirementFeature, synId, request);
			map.put("status", "success");
		} catch (Exception e) {
			return super.handleException(e, "????????????????????????");
		}
		return map;
	}

	// ????????????
	@RequestMapping("addRemark")
	public Map<String, Object> addRemark(Long id, TblRequirementFeatureRemark remark, String attachFiles,
			HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		List<TblRequirementFeatureRemarkAttachement> files = JsonUtil.fromJson(attachFiles,
				JsonUtil.createCollectionType(ArrayList.class, TblRequirementFeatureRemarkAttachement.class));
		try {
			remark.setCreateBy(CommonUtil.getCurrentUserId(request));
			remark.setCreateDate(new Timestamp(new Date().getTime()));
			remark.setRequirementFeatureId(id);
			remark.setUserId(CommonUtil.getCurrentUserId(request));
			remark.setUserAccount(CommonUtil.getCurrentUserAccount(request));
			remark.setUserName(CommonUtil.getCurrentUserName(request));
			devTaskService.addRemark(remark, files, request);
			map.put("status", "success");
		} catch (Exception e) {
			return super.handleException(e, "??????????????????????????????");
		}
		return map;
	}
	
	//????????????  ?????????????????????????????????????????? ??????????????????????????????????????????????????????????????????
	@RequestMapping("cancelStatus")
	public Map<String,Object> changeCancelStatus(Long requirementId){
		Map<String, Object> map =new HashMap<>();
		try {
			devTaskService.changeCancelStatus(requirementId);
		} catch (Exception e) {
			return super.handleException(e, "?????????????????????????????????????????????????????????????????????");
		}
		return map;
	}
	
	//???????????? ??????????????????id???????????????????????????????????????
	@RequestMapping("cancelStatusReqFeature")
	public Map<String,Object> cancelStatusReqFeature(Long reqFeatureId){
		Map<String, Object> map =new HashMap<>();
		try {
			devTaskService.cancelStatusReqFeature(reqFeatureId);
		} catch (Exception e) {
			return super.handleException(e, "??????????????????????????????????????????????????????");
		}
		return map;
	}
	
	//????????????
	@RequestMapping("updateSprints")
	public Map<String, Object> updateSprints(String ids,Long sprintId,String devTaskStatus,Integer executeUserId,Long systemVersionId,
											 Long   executeProjectGroupId, HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			if (StringUtils.isNotBlank(ids)) {
				if(sprintId==null && (devTaskStatus==null || devTaskStatus.equals("")) && executeUserId==null){

				}else {

				 devTaskService.updateSprints(ids, sprintId, devTaskStatus, executeUserId, request);
				}


				//???????????????????????????????????????
				devTaskService.updateGroupAndVersion(ids,systemVersionId,executeProjectGroupId);
				map.put("status", "success");
			}


			
		} catch (Exception e) {
			return super.handleException(e, "??????????????????????????????????????????");
		}
		return map;
	}
	
	// ???????????????????????????????????????????????????????????????
	@RequestMapping("findSynDevTask")
	public List<TblRequirementFeature> findSynDevTask(TblRequirementFeature requirementFeature) {
		List<TblRequirementFeature> synDevTasks = null;
		try {
			synDevTasks = devTaskService.findSynDevTask(requirementFeature);
		} catch (Exception e) {
			e.printStackTrace();
		    logger.error("mes:" + e.getMessage(), e);
		}
		return synDevTasks;
	}

	// ??????????????????????????????????????????
	@RequestMapping(value = "getSearchData", method = RequestMethod.POST)
	public Map<String, Object> getSearchData() {
		Map<String, Object> map = new HashMap<>();
		List<TblCommissioningWindow> windows = devTaskService.getWindows();
		map.put("windows", windows);

		String termCode = "TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS";
		List<TblDataDic> dics = getDataFromRedis(termCode);
		map.put("dataDic", dics);
		return map;
	}
	
	//??????
	@RequestMapping(value="exportExcel")
	public Map<String, Object> exportExcel(String reqFeatue,HttpServletResponse response,HttpServletRequest request){
		Map<String, Object> map = new HashMap<String, Object>();
		String fileName = "????????????????????????.xlsx";
		DevTaskVo devTaskVo = JsonUtil.fromJson(reqFeatue, DevTaskVo.class);
		Long uid = CommonUtil.getCurrentUserId(request);
		devTaskVo.setUserId(uid);
		LinkedHashMap map2 = (LinkedHashMap) redisUtils.get(CommonUtil.getToken(request));
		List<String> roleCodes = (List<String>) map2.get("roles"); 
		List<DevTaskVo> list = new ArrayList<>();
		if (roleCodes!=null && roleCodes.contains("XITONGGUANLIYUAN")) {//?????????????????????????????????????????????
			list = requirementFeatureMapper.getAll(devTaskVo);
		}else {
			list = requirementFeatureMapper.getAllCondition(devTaskVo);
		}

		List<ExtendedField> extendedFields=devTaskService.findFieldByReqId(null);
		List<ExtendedField> extendedFieldsNew=new ArrayList<>();
		List<String> filedNames=new ArrayList<>();

		for(ExtendedField extendedField:extendedFields){
			if(extendedField.getStatus().equals("1")){
				extendedFieldsNew.add(extendedField);
				filedNames.add(extendedField.getFieldName());
			}
		}
		List<TblDataDic> dataDics = getDataFromRedis("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS");
		List<TblDataDic> dataDicsSource = getDataFromRedis("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_SOURCE");

		for (DevTaskVo devTaskVo2 : list) {
			if(devTaskVo2.getFieldTemplate()!=null) {
				JSONObject jsonObject=JSONObject.parseObject((devTaskVo2.getFieldTemplate()));
				String listTxt=jsonObject.getString("field");
				devTaskVo2.setExtendedFields( JSONArray.parseArray(listTxt, ExtendedField.class));
			}

			for (TblDataDic dataDic : dataDics) {
				if (StringUtils.isNotBlank(dataDic.getValueCode())&&StringUtils.isNotBlank(devTaskVo2.getStatusName())&&dataDic.getValueCode().equals(devTaskVo2.getStatusName())) {
					devTaskVo2.setStatusName(dataDic.getValueName());
				}

			}

			for (TblDataDic dataDic : dataDicsSource) {
				if (StringUtils.isNotBlank(dataDic.getValueCode())&&StringUtils.isNotBlank(devTaskVo2.getStatusName())&&dataDic.getValueCode().equals(devTaskVo2.getStatusName())) {
					devTaskVo2.setRequirementFeatureSourceName(dataDic.getValueName());
				}

			}
		}
		try {
			new ExportExcel("", DevTaskVo.class,extendedFieldsNew).setDataListNew(list,filedNames).write(response, fileName).dispose();
		} catch (IOException e) {
			return super.handleException(e, "????????????????????????");
		}
		return map;
	}




	// ???????????? 
	@RequestMapping(value = "uploadFile")
	public List<Map<String, Object>> uploadFile(@RequestParam("files") MultipartFile[] files,HttpServletRequest request) throws Exception {
		List<Map<String, Object>> attinfos = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (files.length > 0 && files != null) {
				for (MultipartFile file : files) {
					if (!file.isEmpty()) {
						InputStream inputStream = file.getInputStream();
						map = new HashMap<String, Object>();
						String extension = file.getOriginalFilename()
								.substring(file.getOriginalFilename().lastIndexOf(".") + 1);// ?????????
						String fileNameOld = file.getOriginalFilename();
						if (BrowserUtil.isMSBrowser(request)) {
							fileNameOld = fileNameOld.substring(fileNameOld.lastIndexOf("\\")+1);
						}
						Random random = new Random();
						String i = String.valueOf(random.nextInt());
						String keyname = s3Util.putObject(s3Util.getDevTaskBucket(), i, inputStream);
						map.put("fileS3Key", keyname);
						map.put("fileS3Bucket", s3Util.getDevTaskBucket());
						map.put("fileNameOld", fileNameOld);
						map.put("fileType", extension);
						attinfos.add(map);
					} else {
						// ??????????????????
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		    logger.error("mes:" + e.getMessage(), e);
		}
		return attinfos;
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
	//??????????????????????????????????????????????????????
	@RequestMapping(value="listDftNoReqFeature")
	public Map<String, Object> listDftNoReqFeature(TblDefectInfo defectInfo,Integer featureSource,
												   Long reqFeatureId,Integer pageNumber, Integer pageSize){
		Map<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblDefectInfo> list = devTaskService.findDftNoReqFeature(defectInfo,featureSource, reqFeatureId,pageNumber, pageSize);
			List<TblDefectInfo> list2 = devTaskService.findDftNoReqFeature(defectInfo,featureSource,reqFeatureId, 1, Integer.MAX_VALUE);
			map.put("total", list2.size());
			map.put("rows", list);
		} catch (Exception e) {
			return super.handleException(e, "????????????????????????");
		}
		return map;
	}
	//????????????????????????
	@RequestMapping("getDefectDic")
	public Map<String, Object> getDefectDic(){
		Map<String, Object> map = new HashMap<>();
		List<TblDataDic> defectSource = getDataFromRedis("TBL_DEFECT_INFO_DEFECT_SOURCE");//???????????? ??????/??????
		List<TblDataDic> defectType = getDataFromRedis("TBL_DEFECT_INFO_DEFECT_TYPE");//???????????? ??????/?????????
		List<TblDataDic> dftEmergencyLevel = getDataFromRedis("TBL_DEFECT_INFO_EMERGENCY_LEVEL");//????????????
		List<TblDataDic> dftSolveStatus = getDataFromRedis("TBL_DEFECT_INFO_SOLVE_STATUS");//????????????
		List<TblDataDic> defectStatus = getDataFromRedis("TBL_DEFECT_INFO_DEFECT_STATUS");//????????????
		List<TblDataDic> dftSeverityLevel = getDataFromRedis("TBL_DEFECT_INFO_SEVERITY_LEVEL");//????????????
		List<TblDataDic> dftRejectReson = getDataFromRedis("TBL_DEFECT_INFO_REJECT_REASON");//????????????
		map.put("defectSource", defectSource);
		map.put("dftSeverityLevel", dftSeverityLevel);
		map.put("dftRejectReson", dftRejectReson);
		map.put("defectType",defectType );
		map.put("dftEmergencyLevel", dftEmergencyLevel);
		map.put("dftSolveStatus", dftSolveStatus);
		map.put("defectStatus",defectStatus );
		return map;
	}
	

	public List<TblDataDic> getDataFromRedis(String termCode) {
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

	private String getFeatureCode() {
		String featureCode = "";
		int codeInt = 0;
		Object object = redisUtils.get("TBL_REQUIREMENT_FEATURE_FEATURE_CODE");
		if (object != null && !"".equals(object)) {// redis????????????redis??????
			String code = object.toString();
			// codeInt=Integer.parseInt(code.substring(Constants.ITMP_DEV_TASK_CODE.length()+1))+1;
			codeInt = Integer.parseInt(code) + 1;
		} else {// redis????????????????????????????????????????????????
			int length = Constants.ITMP_DEV_TASK_CODE.length() + 1;
			String cod = devTaskService.findMaxCode(length);
			if (!StringUtils.isBlank(cod)) {
				codeInt = Integer.parseInt(cod) + 1;
			} else {
				codeInt = 0;
			}
		}
		DecimalFormat df = new DecimalFormat("00000000");
		String codeString = df.format(codeInt);
		featureCode = Constants.ITMP_DEV_TASK_CODE + codeString;
		redisUtils.set("TBL_REQUIREMENT_FEATURE_FEATURE_CODE", codeString);
		return featureCode;
	}

	/**
	 * ??????????????????????????????
	 * @param windowName
	 * @param pageNumber
	 * @param pageSize
	 * @return
	 * scz
	 * 2019???1???9???
	 * ??????4:11:51
	 */
	@RequestMapping(value="selectWindows",method=RequestMethod.POST)
	public Map<String, Object> selectWindows(String windowName, Integer pageNumber, Integer pageSize){
		HashMap<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			List<TblCommissioningWindow> list = devTaskService.selectWindows(windowName, pageNumber, pageSize);
			List<TblCommissioningWindow> list2 = devTaskService.selectWindows(windowName, 1, Integer.MAX_VALUE);
			map.put("total", list2.size());
			map.put("rows", list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			this.handleException(e, "????????????????????????");
		}
		return map;
	}
	
	//bootstrap
	@RequestMapping(value="getAllFeature",method=RequestMethod.POST)
	public Map<String, Object> getAllFeature(HttpServletRequest request,TblRequirementFeature feature,Integer pageSize,Integer pageNumber){
		Map<String, Object> map = new HashMap<>();
		try {
			List<Map<String, Object>> count= devTaskService.getAllFeature(feature,1,Integer.MAX_VALUE);
			List<Map<String, Object>> list = devTaskService.getAllFeature(feature, pageNumber, pageSize);
			map.put("total", count.size());
			map.put("rows", list);
		} catch (Exception e) {
			e.printStackTrace();
			this.handleException(e, "????????????????????????");
		}
		return map;		
	}
	
	//??????systemId??????system_scm?????????systemVersionId scmBranch
	@RequestMapping(value="getSystemVersionBranch")
	public Map<String, Object> getSystemVersionBranch(Long systemId){
		Map<String, Object> map = new HashMap<>();
		try {
			List<Map<String, Object>> list = devTaskService.getSystemVersionBranch(systemId);
			map.put("systemVersionBranchs", list);
		} catch (Exception e) {
			return super.handleException(e, "?????????????????????????????????????????????");
		}
		return map;
		
	}
	
	//????????????code?????????id??????????????????
	@RequestMapping(value="getReqFeatureByReqCodeAndSystemId")
	public Map<String, Object> getReqFeatureByReqCodeAndSystemId (String requirementCode,Long systemId){
		Map<String, Object> map = new HashMap<>();
		try {
			if (!StringUtils.isBlank(requirementCode)&&systemId!=null) {
				List<TblRequirementFeature> list = devTaskService.getReqFeatureByReqCodeAndSystemId(requirementCode,systemId);
				map.put("data", list);
			}
			
		} catch (Exception e) {
			return super.handleException(e, "????????????????????????");
		}
		
		return map;
	}
	
	//?????????????????????????????????  ????????????????????????????????? 
	@RequestMapping(value="deplayReqFesture")
	public Map<String, Object> deplayReqFesture(TblRequirementFeature feature,String status,Integer pageSize,Integer pageNumber){
		Map<String, Object> map = new HashMap<>();
		status = "03,";
		try {
			String statusArr[] = null;
			if (!StringUtils.isBlank(status)) {
				statusArr = status.split(",");
			}
			List<DevTaskVo> count= devTaskService.getDeplayReqFesture(feature,statusArr,1,Integer.MAX_VALUE);
			List<DevTaskVo> list = devTaskService.getDeplayReqFesture(feature,statusArr, pageNumber, pageSize);
			
			map.put("total", count.size());
			map.put("rows", list);
		} catch (Exception e) {
			e.printStackTrace();
			this.handleException(e, "????????????????????????");
		}
		return map;
		
	}


	//?????????????????????????????????  ?????????????????????????????????
	@RequestMapping(value="deplayReqFestureByParam")
	public Map<String, Object> deplayReqFestureByParam(TblRequirementFeature feature,String status,String windowId,String sprintId, Integer pageSize,Integer pageNumber){
		Map<String, Object> map = new HashMap<>();
		status = "03,";
		try {
			String statusArr[] = null;
			if (!StringUtils.isBlank(status)) {
				statusArr = status.split(",");
			}
			List<DevTaskVo> list= devTaskService.getDeplayReqFesture(feature,statusArr,1,Integer.MAX_VALUE);
			//List<DevTaskVo> list = devTaskService.getDeplayReqFesture(feature,statusArr, pageNumber, pageSize);

			map.put("rows", list);
		} catch (Exception e) {
			e.printStackTrace();
			this.handleException(e, "????????????????????????");
		}
		return map;

	}

	
	/**?????????????????????????????????
	 * @param
	 * ids:???????????????id
	 * env:????????????
	 * jsonString:?????????????????????
	 * status 1:??????  2????????? 3????????? 4?????????
	 * 
	 * */ 
	@RequestMapping(value="updateDeployStatus")
	public Map<String, Object> updateDeployStatus(String ids,String env,String jsonString,Integer status){
		Map<String, Object> result = new HashMap<>();
		try {
			if (!StringUtils.isBlank(ids)&& !StringUtils.isBlank(env) && status!=null) {
				devTaskService.updateDeployStatusOne(ids,env,jsonString,status);
				//devTaskService.updateDeployStatus(idsArr,status);
			}
		} catch (Exception e) {
			return super.handleException(e, "????????????????????????");
		}
		return result;
	}
	
	/**?????????????????????????????????
	 * @param 
	 * 
	 * */
	@RequestMapping(value="synReqFeatureDeployStatus")
	public Map<String, Object> synReqFeatureDeployStatus(Long requirementId,Long systemId,String deployStatus,String loginfo){
		Map<String, Object> result = new HashMap<>();
		try {
			if(requirementId!=null && systemId!=null) {
				devTaskService.synReqFeatureDeployStatus(requirementId,systemId,deployStatus,loginfo);
				result.put("status", "success");
			}
		} catch (Exception e) {
			return super.handleException(e, "????????????????????????????????????");
		}
		return result;
	}
	
	@RequestMapping(value="getDeployStatus")
	public Map<String, Object> getDeployStatus(){
		Map<String, Object> map = new HashMap<>();
		List<TblDataDic> deployStatus = getDataFromRedis("TBL_REQUIREMENT_FEATURE_DEPLOY_STATUS");
		map.put("deployStatus", deployStatus);
		return map;
	}
	
	/**
	 *  ????????????????????????????????????
	 * */
	@RequestMapping("updateReqFeatureTimeTrace")
	public Map<String, Object> updateReqFeatureTimeTrace(String jsonString){
		Map<String, Object> result = new HashMap<>();
		try {
			
			TblRequirementFeatureTimeTrace timeTrace = JsonUtil.fromJson(jsonString, TblRequirementFeatureTimeTrace.class);
			List<TblRequirementFeature> requirementFeatures = requirementFeatureMapper.selectBySystemIdAndReqId1(timeTrace.getSystemId(),timeTrace.getRequirementId());
			for (TblRequirementFeature tblRequirementFeature : requirementFeatures) {
				if (tblRequirementFeature.getId()!=null) {
					Map<String, Object> map = new HashMap<>();
					map.put("requirementFeatureId",tblRequirementFeature.getId());
					if (timeTrace.getRequirementFeatureTestingTime()!=null) {
						map.put("requirementFeatureTestingTime", timeTrace.getRequirementFeatureTestingTime());
					}
					if (timeTrace.getRequirementFeatureTestCompleteTime()!=null) {
						map.put("requirementFeatureTestCompleteTime", timeTrace.getRequirementFeatureTestCompleteTime());
					}
					String jsonString2 = JsonUtil.toJson(map);
					devTaskService.updateReqFeatureTimeTrace(jsonString2);
				}
			}
		} catch (Exception e) {
			return super.handleException(e, "?????????????????????????????????????????????");
		}
		return result;
	}
	
	/**
	 * ??????????????????
	 * */
	@RequestMapping("getSprintBySystemId")
	public Map<String, Object> getSprintBySystemId(Long systemId,Long projectId){
		Map<String, Object> map = new HashMap<>();
		try {
//			List<TblSprintInfo> sprintInfos = devTaskService.getSprintBySystemId(systemId);
			List<TblSprintInfo> sprintInfos = devTaskService.getSprintInfoListBySystemIdAndProjectId(systemId,projectId);
			map.put("sprintInfos", sprintInfos);
		} catch (Exception e) {
			return super.handleException(e, "?????????????????????");
		}
		return map;
	}
	
	
	//?????????????????????????????????
	@RequestMapping(value="deplayPrdReqFesture")
	public Map<String, Object> deplayPrdReqFesture(TblRequirementFeature feature,String status,String windowId,Integer pageSize,Integer pageNumber){
		Map<String, Object> map = new HashMap<>();
		if(windowId==null || windowId.equals("")) {
			return map;
		}
		status = "03,";
		try {
			String statusArr[] = null;
			if (!StringUtils.isBlank(status)) {
				statusArr = status.split(",");
			}
			List<DevTaskVo> count= devTaskService.getPrdDeplayReqFesture(feature,statusArr,windowId,1,Integer.MAX_VALUE);
			List<DevTaskVo> list = devTaskService.getPrdDeplayReqFesture(feature,statusArr,windowId, pageNumber, pageSize);
			
			map.put("total", count.size());
			map.put("rows", list);
		} catch (Exception e) {
			e.printStackTrace();
			this.handleException(e, "????????????????????????");
		}
		return map;
		
	}




	//?????????????????????????????????
	@RequestMapping(value="deplayPrdReqFestureNoPage")
	public Map<String, Object> deplayPrdReqFestureNoPage(TblRequirementFeature feature,String status,String windowId){
		Map<String, Object> map = new HashMap<>();
		//if(windowId==null || windowId.equals("")) {
			//return map;
//		}else{
//			feature.setCommissioningWindowId(Long.parseLong(windowId));
//		}
		status = "03,";

		try {
			String statusArr[] = null;
			if (!StringUtils.isBlank(status)) {
				statusArr = status.split(",");
			}
			List<DevTaskVo> list= devTaskService.getPrdDeplayReqFesture(feature,statusArr,windowId,1,Integer.MAX_VALUE);
			map.put("rows", list);
		} catch (Exception e) {
			e.printStackTrace();
			this.handleException(e, "????????????????????????");
		}
		return map;

	}







	// ??????????????????
	  @RequestMapping(value = "getSearchWindow", method = RequestMethod.POST)
	  public Map<String, Object> getSearchWindow(Long systemId) {
	    Map<String, Object> map = new HashMap<>();
	    List<TblCommissioningWindow> windows = devTaskService.getLimitWindows(systemId);
	    map.put("windows", windows);	
	    return map;
	  }
	  
	  //???????????????????????????
	@RequestMapping(value = "getReqFeatureStatus")
	public Map<String, Object> getReqFeatureStatus() {
		Map<String, Object> map = new HashMap<>();
		try {
			List<TblDataDic> status = requirementFeatureMapper.getReqFeatureStatus();

			Collections.sort(status, new Comparator<TblDataDic>() {
				@Override
				public int compare(TblDataDic o1, TblDataDic o2) {
					return o1.getValueSeq() - o2.getValueSeq();
				}
			});
			map.put("reqFeatureStatus", status);
		} catch (Exception e) {
			super.handleException(e, "?????????????????????????????????");
		}
		return map;
	}
	 
	//??????????????????????????????
	@RequestMapping(value="synReqFeaturewindow")
	public Map<String, Object> synReqFeaturewindow(Long requirementId, Long systemId, Long commissioningWindowId, String loginfo ,String beforeName, String afterName ){
		Map<String, Object> result = new HashMap<>();
		try {
			if(requirementId!=null && systemId!=null && commissioningWindowId!=null) {
				devTaskService.synReqFeaturewindow(requirementId,systemId,commissioningWindowId,loginfo,beforeName,afterName);
			}
		} catch (Exception e) {
			return super.handleException(e, "????????????????????????????????????");
		}
		return result;
	}
	
	//??????????????????????????????
	@RequestMapping(value="synReqFeatureDept")
	public Map<String, Object> synReqFeatureDept(Long requirementId, Long systemId, Long deptId, String loginfo, String deptBeforeName,String deptAfterName){
		Map<String,Object> result = new HashMap<>();
		try {
			if(requirementId!=null && systemId!=null && deptId!=null) {
				devTaskService.synReqFeatureDept(requirementId,systemId,deptId,loginfo,deptBeforeName,deptAfterName);
			}
		} catch (Exception e) {
			return super.handleException(e, "????????????????????????????????????");
		}
		return result;
	}

	//????????????
	@RequestMapping(value="getProjectGroup")
	public Map<String, Object> getProjectGroup(HttpServletRequest request ){
		Map<String, Object> result = new HashMap<>();
		try {
			    Long uid = CommonUtil.getCurrentUserId(request);
			    String zNodes=devTaskService.getProjectGroup(uid);
               // zNodes = zNodes.replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2");
			    result.put("zNodes",zNodes);

		} catch (Exception e) {
			return super.handleException(e, "??????????????????");
		}
		return result;
	}

   /* //????????????????????????
    @RequestMapping(value="getProjectGroupBySystemId")
    public Map<String, Object> getProjectGroupBySystemId(HttpServletRequest request ,long systemId){
        Map<String, Object> result = new HashMap<>();
        try {
            Long uid = CommonUtil.getCurrentUserId(request);
           List<String> projectGroupIds=devTaskService.getProjectGroupBySystemId(systemId,uid);
           result.put("size",projectGroupIds.size());
           if(projectGroupIds.size()==1) {
               result.put("id", projectGroupIds.get(0));
           }
        } catch (Exception e) {
            return super.handleException(e, "??????????????????");
        }
        return result;
    }*/


	//??????????????????????????????   
    //???????????? ??????-??????????????????????????? ????????? ---ztt 
	@RequestMapping(value="getProjectGroupByProjectId")
	public Map<String, Object> getProjectGroupByProjectId( Long systemId){
		
		Map<String, Object> result = new HashMap<>();
		if( systemId!=null) {
			try {
				String projectGroup = devTaskService.getProjectGroupByProjectIds(systemId);
				result.put("zNodes", projectGroup);
			} catch (Exception e) {
				return super.handleException(e, "??????????????????");
			}
		}else{
			   result.put("zNodes", "");
		}
		return result;
	}

	//????????????????????????????????????
	@RequestMapping(value="getDevTaskBySystemAndRequirement")
	public Map<String, Object> getDevTaskBySystemAndRequirement(Long systemId, Long requirementId){
		Map<String,Object> result = new HashMap<>();
		try {
			List<Map<String,Object>> list = devTaskService.getDevTaskBySystemAndRequirement(systemId,requirementId);
			result.put("list", list);
		} catch (Exception e) {
			return super.handleException(e, "????????????????????????");
		}
		return result;
	}

	@RequestMapping(value = "downloadReqExcel", method = RequestMethod.GET)
	public void downloadReqExcel(HttpServletResponse response) {
		Object redisEnvType  =  redisUtils.get("FEATURE_EXCEL_S3");
		Map<String, Object> map = JSON.parseObject(redisEnvType.toString());
		s3Util.downObject(map.get("fileS3Bucket").toString(), map.get("fileS3Key").toString(), map.get("fileNameOld").toString(), response);
	}
    @RequestMapping(value = "importExcel", method = RequestMethod.POST)
    public Map<String, Object> importExcel(MultipartFile file,HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();

        map =devTaskService.importExcel(file, request);

        return map;
    }




    @RequestMapping(value = "getOneFeature", method = RequestMethod.POST)
    public Map<String, Object> getOneFeature(String id) {
        Map<String, Object> map = new HashMap<>();
       TblRequirementFeature tblRequirementFeature= devTaskService.getOneFeature(id);
        map.put("tblRequirementFeature",tblRequirementFeature);
       return map;

    }

	@RequestMapping(value = "getTreeName", method = RequestMethod.POST)
    private String getTreeName(String treeId){
		String treeName="";
		Map<String,Object> map=new HashMap<>();
		map.put("id",treeId);
		List<Map<String,Object>> maps=devTaskService.getTreeName(map);
		if(maps!=null && maps.size()>0){
			treeName=maps.get(0).get("systemTreeName").toString();
		}
		return treeName;
	}

	//bootstrap
	@RequestMapping(value="getAllProjectPlan",method=RequestMethod.POST)
	public Map<String, Object> getAllRequirement2(TblProjectPlan tblProjectPlan, Integer pageSize, Integer pageNumber){
		Map<String, Object> map = new HashMap<>();
		try {
			int count= devTaskService.getAllProjectPlanCount(tblProjectPlan);
			List<Map<String, Object>> list = devTaskService.getAllProjectPlan(tblProjectPlan, pageNumber, pageSize);
			map.put("total", count);
			map.put("rows", list);
		} catch (Exception e) {
			return super.handleException(e, "?????????????????????");
		}
		return map;
	}
}
