package cn.pioneeruniverse.dev.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pioneeruniverse.dev.dao.mybatis.TblRequirementFeatureDeployStatusMapper;
import cn.pioneeruniverse.dev.entity.*;
import cn.pioneeruniverse.dev.service.CustomFieldTemplate.ICustomFieldTemplateService;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.common.utils.BrowserUtil;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.DateUtil;
import cn.pioneeruniverse.common.utils.ExportExcel;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.common.utils.S3Util;
import cn.pioneeruniverse.dev.common.DicConstants;
import cn.pioneeruniverse.dev.dao.mybatis.TblRequirementFeatureMapper;
import cn.pioneeruniverse.dev.feignInterface.TestManageToDevManageInterface;
import cn.pioneeruniverse.dev.service.testtask.TestTaskService;
import cn.pioneeruniverse.dev.service.workTask.impl.WorkTaskServiceImpl;
import cn.pioneeruniverse.dev.vo.TestTaskVo;

/**
 * 测试任务controller
 * 
 * @author:tingting
 * @version:2018年12月5日 下午3:13:08
 */

@RestController
@RequestMapping("testtask")
public class TestTaskController extends BaseController {

	@Autowired
	private TestTaskService testTaskService;
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private WorkTaskServiceImpl workTaskServiceImpl;
	@Autowired
	private S3Util s3Util;
	@Autowired
	private TblRequirementFeatureMapper requirementFeatureMapper;
	@Autowired
	private TestManageToDevManageInterface testManageToDevManageInterface;
	@Autowired
	private ICustomFieldTemplateService iCustomFieldTemplateService;
	@Autowired
	private TblRequirementFeatureDeployStatusMapper deployStatusMapper;

	@RequestMapping(value = "getAllTestTask", method = RequestMethod.POST)
	public Map<String, Object> getAllTestTask(TblRequirementFeature requirementFeature, Integer page, Integer rows,HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		Long uid = CommonUtil.getCurrentUserId(request);
	//	List<Long> systemIds = testManageToDevManageInterface.findSystemIdByUserId(uid);
		List<TestTaskVo> reqFeatures = testTaskService.getAllReqFeature(requirementFeature,uid,null,page, rows);
		for (TestTaskVo testTaskVo2 : reqFeatures) {
			testTaskVo2.setUid(uid);
			Long id = testTaskVo2.getId();
			int defectNum = requirementFeatureMapper.findDefectNum(id, 1)+requirementFeatureMapper.findDefectNum(id, 2);
			testTaskVo2.setDefectNum(defectNum);
			int testCaseNum= requirementFeatureMapper.findTestCaseNum(id,1)+requirementFeatureMapper.findTestCaseNum(id,2);
			testTaskVo2.setTestCaseNum(testCaseNum);
		}
		List<TestTaskVo> total = testTaskService.getAllReqFeature(requirementFeature,uid,null,1, Integer.MAX_VALUE);
		map.put("rows", reqFeatures);
		map.put("records", total.size());
		map.put("total", total.size()%rows == 0 ? total.size()/rows:total.size()/rows+1 );
		map.put("page", page);
		return map;
	}
	@RequestMapping(value = "getAllTestTask2", method = RequestMethod.POST)
	public JqGridPage<TestTaskVo> getAllTestTask(TestTaskVo testTaskVo, HttpServletRequest request,
			HttpServletResponse response) {
		Long uid = CommonUtil.getCurrentUserId(request);
		testTaskVo.setUid(uid);

		LinkedHashMap map = (LinkedHashMap) redisUtils.get(CommonUtil.getToken(request));
		List<String> roleCodes = (List<String>) map.get("roles");

		JqGridPage<TestTaskVo> jqGridPage = testTaskService.selectAll(new JqGridPage<TestTaskVo>(request, response),
				testTaskVo,roleCodes);
		return jqGridPage;
	}

	@RequestMapping(value = "detailEnvDate", method = RequestMethod.POST)
	public void detailEnvDate(String list,String envName,String timestamp
							 ) {
		Gson gson = new Gson();
		List<Map<String,Object>> arrList = gson.fromJson(list, new TypeToken<List<Map<String, Object>>>() {}.getType());


	   Timestamp timestampDate=	Timestamp.valueOf(timestamp);
		testTaskService.detailEnvDate(arrList,envName,timestampDate);

	}


	@RequestMapping(value = "test1", method = RequestMethod.POST)
	public void test1(String envName
	) {

		System.out.println("fsdfsd");

	}


	@RequestMapping(value = "findDeployByReqFeatureId", method = RequestMethod.POST)
	public Map<String , Object> findDeployByReqFeatureId(Long featureId) {
		Map<String , Object> map = new HashMap<>();
		try {
			String deployName = testTaskService.findDeployByReqFeatureId(featureId);
			map.put("deployName", deployName);
		} catch (Exception e) {
			return super.handleException(e, "获取数据失败！");
		}
		return map;
	}

    @RequestMapping(value = "findDeployByReqFeatureId1", method = RequestMethod.POST)
    public Map<String , Object> findDeployByReqFeatureId1(Long featureId) {
        Map<String , Object> map = new HashMap<>();
        try {
            String deployArr = testTaskService.findDeployByReqFeatureId1(featureId);
            map.put("deployArr", deployArr);
        } catch (Exception e) {
            return super.handleException(e, "获取数据失败！");
        }
        return map;
    }

	// 获取一条测试任务信息及附件
	@RequestMapping(value = "getOneTestTask", method = RequestMethod.POST)
	public Map<String, Object> getOneTestTask(Long id) {
		Map<String, Object> map = testTaskService.getOneTestTask(id);
		// 查询附件
		List<TblRequirementFeatureAttachement> attachements = testTaskService.findAtt(id);
		map.put("attachements", attachements);
		List<Map<String, Object>> workTasks = testTaskService.findByReqFeature(id);
		map.put("list", workTasks);
		//查询系测/版测缺陷数
		int sitDefectNum = testTaskService.findDefectNum(id,1);
		int pptDefectNum = testTaskService.findDefectNum(id,2);
		map.put("sitDefectNum",sitDefectNum );
		map.put("pptDefectNum", pptDefectNum);
		//查询系测版测案例数
		int sitTestCaseNum= requirementFeatureMapper.findTestCaseNum(id,1);
		int pptTestCaseNum= requirementFeatureMapper.findTestCaseNum(id,2);
		map.put("sitTestCaseNum", sitTestCaseNum);
		map.put("pptTestCaseNum", pptTestCaseNum);
		List<ExtendedField> extendedFields=iCustomFieldTemplateService.findFieldByReqFeature(id);
		map.put("field", extendedFields);
		return map;
	}
	// 获取一条测试任务信息及附件
	@RequestMapping(value = "getOneTestTask3", method = RequestMethod.POST)
	public Map<String, Object> getOneTestTask3(Long id) {
		Map<String, Object> map = new HashMap<>();
		try {
			map= testTaskService.getOneTestTask(id);
			map.putAll(toAddData());
			// 查询附件
			List<TblRequirementFeatureAttachement> attachements = testTaskService.findAtt(id);
			map.put("attachements", attachements);
			List<Map<String, Object>> workTasks = testTaskService.findByReqFeature(id);
			map.put("list", workTasks);
			//查询系测/版测缺陷数
			int sitDefectNum = testTaskService.findDefectNum(id,1);
			int pptDefectNum = testTaskService.findDefectNum(id,2);
			map.put("sitDefectNum",sitDefectNum );
			map.put("pptDefectNum", pptDefectNum);
			//查询系测版测案例数
			int sitTestCaseNum= requirementFeatureMapper.findTestCaseNum(id,1);
			int pptTestCaseNum= requirementFeatureMapper.findTestCaseNum(id,2);
			map.put("sitTestCaseNum", sitTestCaseNum);
			map.put("pptTestCaseNum", pptTestCaseNum);
			//扩展字段
			List<ExtendedField> extendedFields=iCustomFieldTemplateService.findFieldByReqFeature(id);
			map.put("field", extendedFields);
		} catch (Exception e) {
			return super.handleException(e, "获取数据失败！");
		}
		
		return map;
	}

	@RequestMapping(value = "getOneTestTask2", method = RequestMethod.POST)
	public Map<String, Object> getOneTestTask2(Long id) {
		Map<String, Object> map = getOneTestTask(id);
		// 查询兄弟任务
		if (map.containsKey("requirementId")) {
			Long requirementId = (Long) map.get("requirementId");
			List<Map<String, Object>> brotherReqFeatures = testTaskService.findBrother(requirementId, id);
			map.put("brother", brotherReqFeatures);
		}
		// 查询备注及备注附件
		List<TblRequirementFeatureRemark> remarks = testTaskService.findRemark(id);
		map.put("remarks", remarks);
		// 查询处理日志及附件
		List<TblRequirementFeatureLog> logs = testTaskService.findLog(id);
		List<ExtendedField> extendedFields=iCustomFieldTemplateService.findFieldByReqFeature(id);
		map.put("field", extendedFields);
		map.put("logs", logs);
		//查询同系统同需求的开发任务
		Long systemId = (Long)map.get("systemId");
		Long requirementId = (Long)map.get("requirementId");
		if(systemId!=null && requirementId!=null) {
			Map<String, Object> result = testManageToDevManageInterface.getDevTaskBySystemAndRequirement(systemId, requirementId);
			if(result!=null) {
				map.put("requirementList",result);
			}
		}
		return map;
	}

	@RequestMapping(value = "toAddData", method = RequestMethod.POST)
	public Map<String, Object> toAddData() {
		Map<String, Object> map = new HashMap<>();
		try {
			//List<TblCommissioningWindow> windows = testTaskService.getWindows();
			//map.put("cmmitWindows", windows);
			/*List<TblSystemInfo> systemInfos = testTaskService.findAll();
			List<TblRequirementInfo> requirementInfos = testTaskService.getAllReq();
			map.put("systemInfos", systemInfos);
			map.put("requirementInfos", requirementInfos);*/
			String termCode = "TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_SOURCE";
			List<TblDataDic> dataDics = getDataFromRedis(termCode);
			map.put("dics", dataDics);
			String termCode2 = "TBL_REQUIREMENT_INFO_REQUIREMENT_TYPE";
			List<TblDataDic> dataDics2 = getDataFromRedis(termCode2);
			map.put("reqTypes", dataDics2);
			String termCode3 = "TBL_REQUIREMENT_INFO_REQUIREMENT_STATUS";
			List<TblDataDic> dataDics3 = getDataFromRedis(termCode3);
			map.put("reqStatus", dataDics3);
		} catch (Exception e) {
			return super.handleException(e, "获取数据失败！");
		}
		
		return map;
	}

	@RequestMapping(value = "addTestTask", method = RequestMethod.POST)
	public Map<String, Object> addTestTask(HttpServletRequest request, TblRequirementFeature requirementFeature,
			String startDate, String endDate, String pptstartWork, String pptendWork, String attachFiles, String developmentDeptNumber) {
		Map<String, Object> map = new HashMap<>();
		/*List<TblRequirementFeature> list =  testTaskService.findByName(requirementFeature);
		if (!list.isEmpty()) {
			map.put("status", "repeat");
			return map;
		}*/
		List<TblRequirementFeatureAttachement> files = JsonUtil.fromJson(attachFiles,
				JsonUtil.createCollectionType(ArrayList.class, TblRequirementFeatureAttachement.class));

		try {
			if (startDate != null && !"".equals(startDate)) {
				requirementFeature.setPlanSitStartDate(DateUtil.getDate(startDate, null));
			}
			if (endDate != null && !"".equals(endDate)) {
				requirementFeature.setPlanSitEndDate(DateUtil.getDate(endDate,null));
			}
			if (!StringUtils.isBlank(pptstartWork)) {
				requirementFeature.setPlanPptStartDate(DateUtil.getDate(pptstartWork,null));
			}
			if (!StringUtils.isBlank(pptendWork)) {
				requirementFeature.setPlanPptEndDate(DateUtil.getDate(pptendWork,null));
			}
			requirementFeature.setFeatureCode(getFeatureCode());
			requirementFeature.setCreateType(1);// 创建方式 自建
			requirementFeature.setRequirementFeatureStatus("11");// 任务状态新建
			requirementFeature.setCreateBy(CommonUtil.getCurrentUserId(request));
			requirementFeature.setCreateDate(new Timestamp(new Date().getTime()));
			testTaskService.addTestTask(requirementFeature, files, request, developmentDeptNumber);
			map.put("status", "success");
		} catch (Exception e) {
			return super.handleException(e, "新增测试任务失败");
		}
		return map;
	}

	@RequestMapping(value = "updateTestTask", method = RequestMethod.POST)
	public Map<String, Object> updateTestTask(HttpServletRequest request, TblRequirementFeature requirementFeature,
			String attachFiles, String pstartDate, String pendDate, String aendDate, String astartDate, Long[] tUserIds, String developmentDeptNumber) {
		Map<String, Object> map = new HashMap<>();
		TblRequirementFeature requirementFeature2 = requirementFeatureMapper.selectByPrimaryKey(requirementFeature.getId());
		Long uid = CommonUtil.getCurrentUserId(request);
		
		if (uid!=null&&requirementFeature2.getManageUserId()!=null&&requirementFeature2.getExecuteUserId()!=null&&uid.longValue()!=requirementFeature2.getManageUserId().longValue() && uid.longValue()!=requirementFeature2.getExecuteUserId().longValue()
				&& tUserIds != null && !Arrays.asList(tUserIds).contains(uid) 
				&& requirementFeature2.getCreateBy()!=null && uid.longValue()!=requirementFeature2.getCreateBy().longValue()) {
			map.put("status", "noPermission");
			return map;
		}
		/*List<TblRequirementFeature> list =  testTaskService.findByName(requirementFeature);
		if (!list.isEmpty()) {
			map.put("status", "repeat");
			return map;
		}*/
		List<TblRequirementFeatureAttachement> files = JsonUtil.fromJson(attachFiles,
				JsonUtil.createCollectionType(ArrayList.class, TblRequirementFeatureAttachement.class));
		try {
			if (pstartDate != null && !"".equals(pstartDate)) {
				requirementFeature.setActualSitStartDate(DateUtil.getDate(pstartDate,null));
			}
			if (pendDate != null && !"".equals(pendDate)) {
				requirementFeature.setActualSitEndDate(DateUtil.getDate(pendDate,null));
			}
			if (astartDate != null && !"".equals(astartDate)) {
				requirementFeature.setActualPptStartDate(DateUtil.getDate(astartDate,null));
			}
			if (aendDate != null && !"".equals(aendDate)) {
				requirementFeature.setActualPptEndDate(DateUtil.getDate(aendDate,null));
			}
			requirementFeature.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
			requirementFeature.setLastUpdateDate(new Timestamp(new Date().getTime()));
			
			testTaskService.updateTestTask(requirementFeature, files, request, developmentDeptNumber);
			map.put("status", "success");
			
			
		} catch (Exception e) {
			return super.handleException(e, "修改测试任务失败");
		}

		return map;
	}

	// 拆分测试任务(新建工作任务并关联当前测试任务)
	@RequestMapping(value = "splitTestTask", method = RequestMethod.POST)
	public Map<String, Object> splitTestTask(HttpServletRequest request, TblTestTask testTask, Long id,
			String startDate, String endDate, String requirementFeatureStatus, Long[] tUserIds) {
		Map<String, Object> map = new HashMap<>();
		TblRequirementFeature requirementFeature = requirementFeatureMapper.selectByPrimaryKey(id);
		Long uid = CommonUtil.getCurrentUserId(request);
		if (uid!=null&&requirementFeature.getManageUserId()!=null&&requirementFeature.getExecuteUserId()!=null&&uid.longValue()!=requirementFeature.getManageUserId().longValue()&& uid.longValue()!=requirementFeature.getExecuteUserId().longValue()
				&& tUserIds != null && !Arrays.asList(tUserIds).contains(uid) && requirementFeature.getCreateBy()!=null && uid.longValue()!=requirementFeature.getCreateBy().longValue()) {
			map.put("status", "noPermission");
			return map;
		}
		try {

			if (startDate != null && !"".equals(startDate)) {
				testTask.setPlanStartDate(DateUtil.getDate(startDate, null));
			}
			if (endDate != null && !"".equals(endDate)) {
				testTask.setPlanEndDate(DateUtil.getDate(endDate,null));
			}
			testTask.setTestTaskCode(workTaskServiceImpl.getTestCode());
			testTaskService.addWorkTask(id,testTask,request);
			// 根据id修改测试任务的状态为实施中
//			if (!"02".equals(requirementFeatureStatus)&&!"06".equals(requirementFeatureStatus)) {
//				testTaskService.updateStatus(id,testTask.getTestStage(), request);
//			}
			map.put("status", "success");
		} catch (Exception e) {
			return super.handleException(e, "拆分测试任务失败");
		}
		return map;
	}

	// 查询拆分弹框的符合条件的user(当前测试任务所属的系统（已知） 所属的项目 的项目小组下的成员)
	@RequestMapping(value = "tosplit", method = RequestMethod.POST)
	public Map<String, Object> tosplit(Long id, Long systemId) {
		Map<String, Object> map = new HashMap<>();
		Map<String, Object> testTask = testTaskService.getOneTestTask(id);
		List<ExtendedField> extendedFields=iCustomFieldTemplateService.findFieldByReqFeature(id);
		map.putAll(testTask);
		map.put("field", extendedFields);
		List<TblDataDic> dataDics = getDataFromRedis("TBL_TEST_TASK_TEST_STAGE");
		map.put("dataDics", dataDics);
		return map;

	}

	// 处理测试任务(修改)
	@RequestMapping(value = "handleTestTask", method = RequestMethod.POST)
	public Map<String, Object> handleTestTask(HttpServletRequest request, TblRequirementFeature requirementFeature,
			String startDate, String endDate, String pptstartDate, String pptendDate, String attachFiles, Long[] tUserIds ) {
		Map<String, Object> map = new HashMap<>();
		TblRequirementFeature requirementFeature2 = requirementFeatureMapper.selectByPrimaryKey(requirementFeature.getId());
		Long uid = CommonUtil.getCurrentUserId(request);
		if (uid!=null&&requirementFeature2.getManageUserId()!=null&&requirementFeature2.getExecuteUserId()!=null&&uid.longValue()!=requirementFeature2.getManageUserId().longValue()&& uid.longValue()!=requirementFeature2.getExecuteUserId().longValue()
				&& tUserIds != null && !Arrays.asList(tUserIds).contains(uid) && requirementFeature2.getCreateBy()!=null && uid.longValue()!=requirementFeature2.getCreateBy().longValue()) {
			map.put("status", "noPermission");
			return map;
		}
		
		List<TblRequirementFeatureAttachement> files = JsonUtil.fromJson(attachFiles,
				JsonUtil.createCollectionType(ArrayList.class, TblRequirementFeatureAttachement.class));
		try {
			if (startDate != null && !"".equals(startDate)) {
				requirementFeature.setActualSitStartDate(DateUtil.getDate(startDate,null));
			}
			if (endDate != null && !"".equals(endDate)) {
				requirementFeature.setActualSitEndDate(DateUtil.getDate(endDate,null));
			}
			if (pptstartDate != null && !"".equals(pptstartDate)) {
				requirementFeature.setActualPptStartDate(DateUtil.getDate(pptstartDate,null));
			}
			if (!StringUtils.isBlank(pptendDate)) {
				requirementFeature.setActualPptEndDate(DateUtil.getDate(pptendDate,null));
			}
			// 修改状态为实施完成
			requirementFeature.setRequirementFeatureStatus("03");
			requirementFeature.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
			requirementFeature.setLastUpdateDate(new Timestamp(new Date().getTime()));
			testTaskService.handleTestTask(requirementFeature, files, request);
			map.put("status", "success");

		} catch (Exception e) {
			return super.handleException(e, "处理测试任务失败");
		}

		return map;
	}

	// 转派
	@RequestMapping(value = "transfer")
	public Map<String, Object> transfer(TblRequirementFeature requirementFeature,String transferRemark,HttpServletRequest request, Long[] tUserIds) {
		Map<String, Object> map = new HashMap<>();
		TblRequirementFeature requirementFeature2 = requirementFeatureMapper.selectByPrimaryKey(requirementFeature.getId());
		Long uid = CommonUtil.getCurrentUserId(request);
		if (uid!=null&&requirementFeature2.getManageUserId()!=null&&requirementFeature2.getExecuteUserId()!=null&&uid.longValue()!=requirementFeature2.getManageUserId().longValue() && uid.longValue()!=requirementFeature2.getExecuteUserId().longValue()
				&& tUserIds != null && !Arrays.asList(tUserIds).contains(uid) && requirementFeature2.getCreateBy()!=null && uid.longValue()!=requirementFeature2.getCreateBy().longValue()) {
			map.put("status", "noPermission");
			return map;
		}
		try {
			if (requirementFeature.getExecuteUserId()!=null) {
				testTaskService.updateTransfer(requirementFeature,transferRemark, request);
			}
			map.put("status", "success");
		} catch (Exception e) {
			return super.handleException(e, "转派测试任务失败");
		}

		return map;
	}

	// 合并任务(同步过来的任务合并到自建任务)
	/*@RequestMapping("merge")
	public Map<String, Object> mergeSynDevTask(TblRequirementFeature requirementFeature, Long synId,
			HttpServletRequest request, String roleCodes, @RequestParam("tUserIds[]")Long[] tUserIds) {
		Map<String, Object> map = new HashMap<>();
		TblRequirementFeature requirementFeature2 = requirementFeatureMapper.selectByPrimaryKey(requirementFeature.getId());
		Long uid = CommonUtil.getCurrentUserId(request);
		if (uid!=null&&requirementFeature2.getManageUserId()!=null&&requirementFeature2.getExecuteUserId()!=null&&uid.longValue()!=requirementFeature2.getManageUserId().longValue() && uid.longValue()!=requirementFeature2.getExecuteUserId().longValue()
				&&roleCodes != null && roleCodes.indexOf("CESHIZUZHANG")==-1&& roleCodes.indexOf("CESHIGUANLIGANG")==-1 && tUserIds.length != 0 && !Arrays.asList(tUserIds).contains(uid)) {
			map.put("status", "noPermission");
			return map;
		}
		try {
			// 把同步的taskId给自建的taskId 只有同步过来的任务有taskId
			testTaskService.mergeDevTask(requirementFeature, synId, request);
			map.put("status", "success");
		} catch (Exception e) {
			return super.handleException(e, "合并测试任务失败");
		}
		return map;
	}*/

	// 新增备注
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
			testTaskService.addRemark(remark, files, request);
			map.put("status", "success");
		} catch (Exception e) {
			return super.handleException(e, "新增测试任务备注失败");
		}
		return map;
	}
	
	//导出
	@RequestMapping("exportExcel")
	public Map<String, Object> exportExcel(String reqFeatue,HttpServletResponse response,HttpServletRequest request){
		Map<String, Object> map = new HashMap<String, Object>();
		String fileName = "测试任务导出数据.xlsx";
		TestTaskVo testTaskVo = JsonUtil.fromJson(reqFeatue, TestTaskVo.class);
		testTaskVo.setUid(CommonUtil.getCurrentUserId(request));
		LinkedHashMap map2 = (LinkedHashMap) redisUtils.get(CommonUtil.getToken(request));
		List<String> roleCodes = (List<String>) map2.get("roles"); 
		List<TestTaskVo> list = new ArrayList<>();
		if (roleCodes!=null && roleCodes.contains("XITONGGUANLIYUAN")) {//当前登录用户有角色是系统管理员
			list = requirementFeatureMapper.getAll(testTaskVo);
		}else {
			list = requirementFeatureMapper.getAllCondition(testTaskVo);
		}
		for (TestTaskVo testTaskVo2 : list) {
			Long id = testTaskVo2.getId();
			int defectNum = requirementFeatureMapper.findDefectNum(id, 1)+requirementFeatureMapper.findDefectNum(id, 2);
			testTaskVo2.setAllDefectAmount(defectNum);
//			int testCaseNum= requirementFeatureMapper.findTestCaseNum(id,1)+requirementFeatureMapper.findTestCaseNum(id,2);
			int testCaseNum = requirementFeatureMapper.getTestCaseNum(id);
			testTaskVo2.setTestCaseNum(testCaseNum);
			testTaskVo2.setActualSitStartDate(requirementFeatureMapper.getActualStartDate(id,1));
			testTaskVo2.setActualSitEndDate(requirementFeatureMapper.getActualEndDate(id,1));
			testTaskVo2.setActualSitWorkload(requirementFeatureMapper.getActualWorkoad(id,1));
			
			testTaskVo2.setActualPptStartDate(requirementFeatureMapper.getActualStartDate(id,2));
			testTaskVo2.setActualPptEndDate(requirementFeatureMapper.getActualEndDate(id,2));
			testTaskVo2.setActualPptWorkload(requirementFeatureMapper.getActualWorkoad(id,2));
			String isSupervisionReq="";
			if(StringUtils.isNotBlank(testTaskVo2.getRequirmentType())) {
				if(testTaskVo2.getRequirmentType().equals("1")) {
					isSupervisionReq="是";
				}else if (testTaskVo2.getRequirmentType().equals("2")){
					isSupervisionReq="否";
				}
			}
			testTaskVo2.setRequirmentType(isSupervisionReq);
			List<TblDataDic> dataDics = getDataFromRedis("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS_2");
			for (TblDataDic dataDic : dataDics) {
				if (StringUtils.isNotBlank(dataDic.getValueCode())&&StringUtils.isNotBlank(testTaskVo2.getStatusName())&&dataDic.getValueCode().equals(testTaskVo2.getStatusName())) {
					testTaskVo2.setStatusName(dataDic.getValueName());
				}
			}

			Integer featureSource = testTaskVo2.getFeatureSource();
			if(featureSource != null) {
				String string = redisUtils.get("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_SOURCE_2").toString();
				JSONObject object = JSON.parseObject(string);
				String source = object.get((featureSource).toString()).toString();
				testTaskVo2.setSourceName(source);
			}

		}
		try {
			new ExportExcel("", TestTaskVo.class).setDataList(list).write(response, fileName).dispose();
		} catch (IOException e) {
			return super.handleException(e, "导出开发任务失败");
		}
		return map;
	} 

	// 查询与该自建任务需求和系统都一致的同步任务
	@RequestMapping("findSynTestTask")
	public List<TblRequirementFeature> findSynTestTask(TblRequirementFeature requirementFeature) {
		List<TblRequirementFeature> synTestTasks = null;
		try {
			synTestTasks = testTaskService.findSynDevTask(requirementFeature);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("mes:" + e.getMessage(), e);
		}
		return synTestTasks;
	}


	//查询当前登录用户所在项目下的系统
	@RequestMapping(value="selectAllSystemInfo")
	public Map<String, Object> selectAllSystemInfo(TblSystemInfo systemInfo,Integer pageNumber,Integer pageSize,HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			Long uid = CommonUtil.getCurrentUserId(request);
			List<Map<String, Object>> list = testTaskService.getAllSystemInfo(systemInfo,uid, pageNumber, pageSize);
			List<Map<String, Object>> list2 = testTaskService.getAllSystemInfo(systemInfo, uid,1, Integer.MAX_VALUE);
			
			map.put("rows",list );
			map.put("total", list2.size());

		} catch (Exception e) {
			return super.handleException(e, "查询系统信息失败");
		}
		return map;
	}
	//查询所有系统
	@RequestMapping(value="selectAllSystemInfo2")
	public Map<String, Object> selectAllSystemInfo2(TblSystemInfo systemInfo,Integer pageNumber,Integer pageSize,HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			//Long uid = CommonUtil.getCurrentUserId(request);
			List<Map<String, Object>> list = testTaskService.getAllSystemInfo(systemInfo,null, pageNumber, pageSize);
			List<Map<String, Object>> list2 = testTaskService.getAllSystemInfo(systemInfo, null,1, Integer.MAX_VALUE);
			
			map.put("rows",list );
			map.put("total", list2.size());

		} catch (Exception e) {
			return super.handleException(e, "查询系统信息失败");
		}
		return map;
	}

	// 需求bootstrap
	@RequestMapping(value = "getAllReq")
	public Map<String, Object> getAllRequirement(HttpServletRequest request, TblRequirementInfo requirement, Integer pageSize, Integer pageNumber) {
		Map<String, Object> map = new HashMap<>();
		try {
			System.out.println(requirement.toString());
			int count= testTaskService.getAllRequirementCount(requirement);
			List<Map<String, Object>> list = testTaskService.getAllRequirement(requirement, pageNumber, pageSize);
			map.put("total", count);
			map.put("rows", list);
		} catch (Exception e) {
			 e.printStackTrace();
			 logger.error("mes:" + e.getMessage(), e);
		}
		return map;		
	}

	@RequestMapping(value = "getDataDicList", method = RequestMethod.POST)
	public List<TblDataDic> getDataDicList(String datadictype) {
		String termCode = "";
		List<TblDataDic> resultList = new ArrayList<TblDataDic>();
		if (datadictype != null && datadictype.equals("reqStatus")) {
			termCode = DicConstants.req_status;
		}
		if (datadictype != null && datadictype.equals("reqSource")) {
			termCode = DicConstants.req_source;
		}
		if (datadictype != null && datadictype.equals("reqType")) {
			termCode = DicConstants.req_type;
		}
		String result = redisUtils.get(termCode).toString();
		if (!StringUtils.isBlank(result)) {
			Map maps = (Map) JSON.parse(result);
			for (Object map : maps.entrySet()) {
				TblDataDic tdd = new TblDataDic();
				tdd.setValueCode(((Map.Entry) map).getKey().toString());
				tdd.setValueName(((Map.Entry) map).getValue().toString());
				resultList.add(tdd);
			}
		}
		return resultList;
	}

	// 文件上传 
	@RequestMapping(value = "uploadFile")
	public List<Map<String, Object>> uploadFile(@RequestParam("files") MultipartFile[] files,HttpServletRequest request) {
		List<Map<String, Object>> attinfos = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			if (files.length > 0 && files != null) {
				for (MultipartFile file : files) {
					if (!file.isEmpty()) {
						map = new HashMap<String, Object>();
						InputStream inputStream = file.getInputStream();
						String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);// 后缀名
						//String newFileName = UUID.randomUUID().toString().replace("-", "");
						//String url = fileUploadPath + newFileName + "." + extension;
						String fileNameOld = file.getOriginalFilename();
						if (BrowserUtil.isMSBrowser(request)) {
							fileNameOld = fileNameOld.substring(fileNameOld.lastIndexOf("\\")+1);
						}
						Random random = new Random();
						String i = String.valueOf(random.nextInt());
						String keyname = s3Util.putObject(s3Util.getTestTaskBucket(),i , inputStream);
						map.put("fileS3Key", keyname);
						map.put("fileS3Bucket", s3Util.getTestTaskBucket());

						//map.put("fileNameNew", newFileName);
						//map.put("filePath", url);
						map.put("fileNameOld",fileNameOld);
						map.put("fileType", extension);
						attinfos.add(map);
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
	
	/**
	 * 根据测试集获取所有测试任务
	 * @param nameOrNumber
	 * @param createBy
	 * @param testTaskId
	 * @return
	 */
	@RequestMapping(value = "getAllTestTaskByTestSet",method=RequestMethod.POST)
	public Map<String, Object> getAllTestTaskByTestSet(String nameOrNumber,String createBy,String testTaskId,Long taskId){
		Map<String, Object> map = new HashMap<>();
		try {
			List<Map<String, Object>> list = testTaskService.selectTaskByTestSetCon(nameOrNumber.equals("")?null:nameOrNumber, createBy, testTaskId,taskId);
			map.put("rows", list);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
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

	// 获取测试编号
	private String getFeatureCode() {
		String featureCode = "";
		int codeInt = 0;
		Object object = redisUtils.get("TBL_REQUIREMENT_FEATURE_FEATURE_CODE_TEST");
		if (object != null && !"".equals(object)) {// redis有直接从redis中取
			String code = object.toString();
			// codeInt
			// =Integer.parseInt(code.substring(Constants.ITMP_DEV_TASK_CODE.length()+1))+1;
			codeInt = Integer.parseInt(code) + 1;
		} else {// redis中没有查询数据库中最大的任务编号
			int length = Constants.ITMP_TEST_TASK_CODE.length() + 1;
			String cod = testTaskService.findMaxCode(length);
			if (!StringUtils.isBlank(cod)) {
				codeInt = Integer.parseInt(cod) + 1;
			} else {
				codeInt = 0;
			}
		}
		DecimalFormat df = new DecimalFormat("00000000");
		String codeString = df.format(codeInt);

		featureCode = Constants.ITMP_TEST_TASK_CODE + codeString;
		redisUtils.set("TBL_REQUIREMENT_FEATURE_FEATURE_CODE_TEST", codeString);
		return featureCode;
	}
	
	//测试任务取消状态 当关联的需求变成取消 该测试任务以及下属的工作任务也变成取消状态
	@RequestMapping("cancelStatus")
	public Map<String,Object> changeCancelStatus(Long requirementId){
		Map<String, Object> map =new HashMap<>();
		try {
			testTaskService.changeCancelStatus(requirementId);
		} catch (Exception e) {
			return super.handleException(e, "修改开发任务及下属工作任务的状态为取消");
		}
		return map;
	}
	//取消状态 根据id该开发任务及下属工作任务都变为取消状态
		@RequestMapping("cancelStatusReqFeature")
		public Map<String,Object> cancelStatusReqFeature(Long reqFeatureId){
			Map<String, Object> map =new HashMap<>();
			try {
				testTaskService.cancelStatusReqFeature(reqFeatureId);
			} catch (Exception e) {
				return super.handleException(e, "修改开发任务及下属工作任务的状态为取消");
			}
			return map;
		}
	
	//部署确认
	@RequestMapping("sureDeploy")
	public Map<String, Object> sureDeploy(Long reqFeatureId,String deployStatus,HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		try {
			if (reqFeatureId!=null) {
				List<TblRequirementFeatureDeployStatus> oldDeployStatus = deployStatusMapper.findByReqFeatureId(reqFeatureId);
				testTaskService.updateDeployStatus(reqFeatureId,deployStatus,request);
				List<TblRequirementFeatureDeployStatus> newDeployStatus = deployStatusMapper.findByReqFeatureId(reqFeatureId);
				testTaskService.insertFeatureLog(reqFeatureId,oldDeployStatus,request);
				map.put("status", "success");
				//修改后把该任务的部署状态同步给开发任务
				TblRequirementFeature feature = requirementFeatureMapper.selectByPrimaryKey(reqFeatureId);
				TblRequirementFeatureLog log = new TblRequirementFeatureLog();
				log.setCreateBy(CommonUtil.getCurrentUserId(request));
				log.setCreateDate(new Timestamp(new Date().getTime()));
				log.setUserId(CommonUtil.getCurrentUserId(request));
				log.setUserAccount(CommonUtil.getCurrentUserAccount(request));
				log.setUserName(CommonUtil.getCurrentUserName(request));
				String loginfo = JsonUtil.toJson(log);
				String DeployStatusString = JSONObject.toJSONString(newDeployStatus);
				Map<String, Object> result = testManageToDevManageInterface.
						synReqFeatureDeployStatus(feature.getRequirementId(),feature.getSystemId(),DeployStatusString,loginfo);
			}
		} catch (Exception e) {
			super.handleException(e, "修改任务的部署状态失败");
		}
		return  map;
	}
		
	
	/**同步开发任务的部署状态
	 * @param
	 * */
	@RequestMapping(value="synReqFeatureDeployStatus")
	public Map<String, Object> synReqFeatureDeployStatus(Long requirementId,Long systemId,
														 String deployStatus,String loginfo){
		Map<String, Object> result = new HashMap<>();
		try {
			if(requirementId!=null && systemId!=null) {
				testTaskService.synReqFeatureDeployStatus(requirementId,systemId,deployStatus,loginfo);
			}
		} catch (Exception e) {
			return super.handleException(e, "同步开发任务部署状态失败");
		}
		return result;
	}
	
	//同步投产窗口
	@RequestMapping(value="synReqFeaturewindow")
	public Map<String, Object> synReqFeaturewindow(Long requirementId, Long systemId, Long commissioningWindowId, String loginfo, String beforeName, String afterName) {
		Map<String, Object> result = new HashMap<>();
		try {
			if(requirementId!=null && systemId!=null && commissioningWindowId!=null) {
				testTaskService.synReqFeaturewindow(requirementId,systemId,commissioningWindowId,loginfo,beforeName,afterName);
			}
		} catch (Exception e) {
			return super.handleException(e, "同步开发任务投产窗口失败");
		}
		return result;
	}
	
	//同步所属处室
	@RequestMapping(value="synReqFeatureDept")
	public Map<String, Object> synReqFeatureDept(Long requirementId, Long systemId, Integer deptId, String loginfo, String deptBeforeName,
			String deptAfterName){
		Map<String, Object> result = new HashMap<>();
		try {
			if(requirementId!=null && systemId!=null && deptId!=null) {
				testTaskService.synReqFeaturewindow(requirementId,systemId,deptId,loginfo,deptBeforeName,deptAfterName);
			}
		} catch (Exception e) {
			return super.handleException(e, "同步开发任务投产窗口失败");
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
	 * 获取测试任务树
	 * @param userId
	 * @param taskName
	 * @param testSetName
	 * @param requirementFeatureStatus
	 * @return
	 */
	@RequestMapping(value="getTaskTree")
	public Map<String, Object> getTaskTree(Long userId,String taskName,String testSetName,Integer requirementFeatureStatus,HttpServletRequest request){
		Map<String, Object> result = new HashMap<>();
		try {
			if(userId == null) {
				userId = CommonUtil.getCurrentUserId(request);
			}
			List<TblRequirementFeature> list = testTaskService.getTaskTree(userId, taskName.equals("")?null:taskName, testSetName.equals("")?null:testSetName, requirementFeatureStatus);
			result.put("rows", list);
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			result = super.handleException(e, e.getMessage());
		}
		return result;
	}
	
	//合并
	@RequestMapping(value="mergeTestTask",method=RequestMethod.POST)
	public Map<String, Object> mergeTestTask(Long requirementId, Long systemId, HttpServletRequest request){
		Map<String,Object> map = new HashMap<>();
		try {
			testTaskService.mergeTestTask(requirementId, systemId, request);
			map.put("status", "success");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map = super.handleException(e, e.getMessage());
		}
		return map;
		
	}
	
	//选择需求查询需求中部门number
	@RequestMapping(value="findDeptNumber",method=RequestMethod.POST)
	public Map<String, Object> findDeptNumber(Long id){
		Map<String,Object> map = new HashMap<>();
		try {
			TblDeptInfo tblDeptInfo = testTaskService.findDeptNumber(id);
			map.put("status", "success");
			map.put("data", tblDeptInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}
	
}
