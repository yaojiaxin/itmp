package cn.pioneeruniverse.dev.service.testtask.impl;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.databus.DataBusRequestHead;
import cn.pioneeruniverse.common.databus.DataBusUtil;
import cn.pioneeruniverse.common.velocity.tag.VelocityDataDict;
import cn.pioneeruniverse.dev.dao.mybatis.*;
import cn.pioneeruniverse.dev.dto.TaskFeatuDTO;
import cn.pioneeruniverse.dev.entity.*;
import cn.pioneeruniverse.dev.yiranUtil.DefectExport;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ccic.databus.client.publish.DataBusPublishClient;
import com.ccic.databus.common.exception.DataBusException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.common.utils.CollectionUtil;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.common.utils.ReflectUtils;
import cn.pioneeruniverse.dev.feignInterface.TestManageToDevManageInterface;
import cn.pioneeruniverse.dev.service.testtask.TestTaskService;
import cn.pioneeruniverse.dev.vo.TestTaskVo;

/**
 * 类说明
 * 
 * @author:tingting
 * @version:2018年12月5日 下午3:13:08
 */
@Service
@Transactional(readOnly = true)
public class TestTaskServiceImpl implements TestTaskService {


	@Autowired
	private TaskFeatuMapper taskFeatuMapper;
	@Autowired
	private TblRequirementFeatureMapper requirementFeatureMapper;
	@Autowired
	private TblRequirementInfoMapper requirementInfoMapper;
	@Autowired
	private TblTestTaskMapper testTaskMapper;// 工作任务mapper
	@Autowired
	private TblCommissioningWindowMapper commissioningWindowMapper;
	@Autowired
	private TblRequirementFeatureAttachementMapper requirementFeatureAttachementMapper;
	@Autowired
	private TblSystemInfoMapper systemInfoMapper;
	@Autowired
	private TblRequirementFeatureRemarkMapper requirementFeatureRemarkMapper;
	@Autowired
	private TblRequirementFeatureRemarkAttachementMapper requirementFeatureRemarkAttachementMapper;
	@Autowired
	private TblRequirementFeatureLogMapper requirementFeatureLogMapper;
	@Autowired
	private TblRequirementFeatureLogAttachementMapper requirementFeatureLogAttachementMapper;
	@Autowired
	private RedisUtils redisUtils;
	@Autowired
	private TestManageToDevManageInterface testManageToDevManageInterface;
	@Autowired
	private TblDataDicMapper tblDataDicMapper;
	@Autowired
	private TblUserInfoMapper tblUserInfoMapper;
	@Autowired
	private TblDefectInfoMapper defectInfoMapper;
	@Autowired
	private TblProjectGroupUserMapper tblProjectGroupUserMapper;
	@Autowired
	private TblRequirementFeatureDeployStatusMapper deployStatusMapper;
	@Value("${databuscc.name}")
	private String databusccName;
	
	public final static Logger logger = LoggerFactory.getLogger(TestTaskServiceImpl.class);
	
	private static final  String manageUserPropertyInfoName = "管理岗";
	private static final  String executeUserPropertyInfoName = "执行人";
	private static final  String deptPropertyInfoName = "所属处室";
	private static final  String systemPropertyInfoName = "涉及系统";
	private static final  String requirementPropertyInfoName = "关联需求";
	private static final  String commissioningWindowPropertyInfoName = "投产窗口";
	private static final  String sourcePropertyInfoName = "任务类型";
	private static final  String reqFeatureStatusInfoName = "任务状态";

	@Override
	public List<TestTaskVo> getAllReqFeature(TblRequirementFeature requirementFeature,Long uid,List<Long> systemIds, Integer page,
			Integer rows) {
		Map<String, Object> map = new HashMap<>();
		Integer start = (page - 1) * rows;
		map.put("start", start);
		map.put("pageSize", rows);
		map.put("reqFeature", requirementFeature);
		map.put("systemIds", systemIds);
		map.put("uid", uid);
		return requirementFeatureMapper.getAllReqFeature(map);
	}
	
	@Override
	public JqGridPage<TestTaskVo> selectAll(JqGridPage<TestTaskVo> jqGridPage,TestTaskVo testTaskVo,List<String> roleCodes) {
		try {
			jqGridPage.filtersAttrToEntityField(testTaskVo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String deployStatus = testTaskVo.getDeployStatus();
		List<Long> deploys = findDploys(deployStatus);
		testTaskVo.setDeploys(deploys);
		PageHelper.startPage(jqGridPage.getJqGridPrmNames().getPage(), jqGridPage.getJqGridPrmNames().getRows());
		Map<String, Object> map = new HashMap<>();
		List<TestTaskVo> list = new ArrayList<>();
		if (roleCodes!=null && roleCodes.contains("XITONGGUANLIYUAN")) {//当前登录用户有角色是系统管理员
			if("0".equals(testTaskVo.getRequirementFeatureStatus())) {
				testTaskVo.setRequirementFeatureStatus("");
			}
			list = requirementFeatureMapper.getAll(testTaskVo);
		}else {
			if("0".equals(testTaskVo.getRequirementFeatureStatus())) {
				testTaskVo.setRequirementFeatureStatus("");
			}
			list = requirementFeatureMapper.getAllCondition(testTaskVo);
		}
		for (TestTaskVo testTaskVo2 : list) {
			Long id = testTaskVo2.getId();
			int defectNum = requirementFeatureMapper.findDefectNum(id, 1)+requirementFeatureMapper.findDefectNum(id, 2);
			testTaskVo2.setDefectNum(defectNum);
//			int testCaseNum= requirementFeatureMapper.findTestCaseNum(id,1)+requirementFeatureMapper.findTestCaseNum(id,2);
			int testCaseNum = requirementFeatureMapper.getTestCaseNum(id);
			testTaskVo2.setTestCaseNum(testCaseNum);
			List<Long> uIds = tblProjectGroupUserMapper.findUserIdBySystemId(testTaskVo2.getSystemId());
			testTaskVo2.settUserIds(uIds);
			
			Map<String,Object> map2 = new HashMap<>();
			map2.put("systemId", testTaskVo2.getSystemId());
			map2.put("requirementId", testTaskVo2.getRequirementId());
			int count = requirementFeatureMapper.getCountBySystemIdAndRequirementId(map2);
			if(count >= 2) {
				testTaskVo2.setFlag(true);
			}else {
				testTaskVo2.setFlag(false);
			}
		}
		PageInfo<TestTaskVo> pageInfo = new PageInfo<TestTaskVo>(list);
		jqGridPage.processDataForResponse(pageInfo);
		return jqGridPage;

	}

	@Override
	public String findDeployByReqFeatureId(Long featureId){
		List<TblRequirementFeatureDeployStatus> deployStatusList=
				deployStatusMapper.findByReqFeatureId(featureId);
		String deployName = "";
		if(deployStatusList!=null&&deployStatusList.size()>0){
			for (TblRequirementFeatureDeployStatus deployStatus : deployStatusList){
				deployName = deployName + getDeployName(deployStatus.getDeployStatu())+ " | " +
						getTime(deployStatus.getDeployTime()) + "，";
			}
		}
		return deployName;
	}

    @Override
    public String findDeployByReqFeatureId1(Long featureId){
        List<TblRequirementFeatureDeployStatus> deployStatusList =
                                deployStatusMapper.findByReqFeatureId(featureId);
        String deployArr = "";
        if(deployStatusList!=null&&deployStatusList.size()>0){
            for (TblRequirementFeatureDeployStatus deployStatus : deployStatusList){
                deployArr = deployStatus.getDeployStatu()+","+deployArr;
            }
        }
        return deployArr;
    }

	public List<Long> findDploys(String deployStatus) {
		// TODO Auto-generated method stub
		String termCode = "TBL_REQUIREMENT_FEATURE_DEPLOY_STATUS";
		Map<String,Object> map = new HashMap<>();
		map.put("termCode", termCode);
		map.put("deployStatus", deployStatus);
		return tblDataDicMapper.findDploys(map);
	}

	@Override
	public Map<String, Object> getOneTestTask(Long id) {
		 Map<String, Object> oneTestTask = requirementFeatureMapper.getOneTestTask(id);

		List<TaskFeatuDTO> taskFeatu = taskFeatuMapper.selectTaskFeatuByID(id);
		Map<String, Object> stringObjectMap = DefectExport.taskFeatuDate(taskFeatu, oneTestTask);


		Object object = oneTestTask.get("requirementFeatureSource");
		 if(object != null) {
			 String str = object.toString();
			 String string = redisUtils.get("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_SOURCE_2").toString();
			 JSONObject jsonObject = JSON.parseObject(string);
			 String s = jsonObject.get(str).toString();
			 oneTestTask.put("featureSource", s);
		 }
		 //系测设计案例数
		 Long designCaseNumber = requirementFeatureMapper.getDesignCaseNumber(id);
		 oneTestTask.put("designCaseNumber", designCaseNumber);
		 //系测执行案例数
		 Long executeCaseNumber = requirementFeatureMapper.getExecuteCaseNumber(id);
		 oneTestTask.put("executeCaseNumber", executeCaseNumber);
		 //系测缺陷数
		 Long defectNum = requirementFeatureMapper.getDefectNumber(id);
		 oneTestTask.put("defectNum", defectNum);
		 //版测设计案例数
		 Long designCaseNumber2 = requirementFeatureMapper.getDesignCaseNumber2(id);
		 oneTestTask.put("designCaseNumber2", designCaseNumber2);
		 //版测执行案例数
		 Long executeCaseNumber2 = requirementFeatureMapper.getExecuteCaseNumber2(id);
		 oneTestTask.put("executeCaseNumber2", executeCaseNumber2);
		 //版测缺陷数
		 Long defectNum2 = requirementFeatureMapper.getDefectNumber2(id);
		 oneTestTask.put("defectNum2", defectNum2);
		 //系测工作量
		 Double workload = requirementFeatureMapper.getWorkload(id);
		 oneTestTask.put("workload", workload);
		 //版测工作量
		 Double workload2 = requirementFeatureMapper.getWorkload2(id);
		 oneTestTask.put("workload2", workload2);
		 return oneTestTask;
	}

	@Override
	public List<Map<String, Object>> findByReqFeature(Long id) {
		return testTaskMapper.findByReqFeature(id);
	}

	@Override
	@Transactional(readOnly = false)
	public void addTestTask(TblRequirementFeature requirementFeature, List<TblRequirementFeatureAttachement> files,
			HttpServletRequest request, String developmentDeptNumber) {
		Long deptId = requirementFeatureMapper.findDeptIdByDeptNumber(developmentDeptNumber);
		requirementFeature.setDevelopmentDeptId(deptId);
		requirementFeatureMapper.insertReqFeature(requirementFeature);
		Long reqFeatureId = requirementFeature.getId();
		if (files != null && files.size() > 0) {
			for (TblRequirementFeatureAttachement tblRequirementFeatureAttachement : files) {
				tblRequirementFeatureAttachement.setRequirementFeatureId(reqFeatureId);
				tblRequirementFeatureAttachement.setCreateBy(CommonUtil.getCurrentUserId(request));
				tblRequirementFeatureAttachement.setCreateDate(new Timestamp(new Date().getTime()));
				requirementFeatureAttachementMapper.insertAtt(tblRequirementFeatureAttachement);
			}
		}
		TblRequirementFeatureLog log = new TblRequirementFeatureLog();
		log.setRequirementFeatureId(reqFeatureId);
		log.setLogType("新增测试任务");
		insertLog(log, request);
	}

	@Override
	@Transactional(readOnly = false)
	public void addWorkTask(Long id,TblTestTask testTask,HttpServletRequest request) {
		TblRequirementFeature requirementFeature2 = requirementFeatureMapper.selectByPrimaryKey(id);
		testTask.setTestTaskStatus(1);// 新增时状态为未实施
		testTask.setCreateBy(CommonUtil.getCurrentUserId(request));
		testTask.setCreateDate(new Timestamp(new Date().getTime()));
		testTask.setRequirementFeatureId(id);
		testTaskMapper.addTestTask(testTask);
		String status = "";
		String afterName = "";
		List<TblDataDic> dataDics = getDataFromRedis("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS_2");
		for (TblDataDic tblDataDic : dataDics) {
			if (requirementFeature2.getRequirementFeatureStatus()!=null) {
				if (tblDataDic.getValueCode().equals(requirementFeature2.getRequirementFeatureStatus())) {
					afterName = tblDataDic.getValueName();
				}
			}
		}
		

		Long testId = testTask.getId();
		Map<Object,Object> map = new HashMap<>();
  		map.put("id", testId);
  		List<Integer> testStages = testTaskMapper.selectTestStageById(testId);//查出同一测试任务下的工作任务的测试阶段(排除撤销的工作任务)
  		if(testStages.size() == 1) { //长度为1，全部都是版测或系测
			if(testStages.get(0)==1) {//测试任务下的工作任务都为系测
				List<Integer> testTaskStatus = testTaskMapper.selectTestTaskStatusById(testId);
				if(testTaskStatus.size()==1 && testTaskStatus.get(0)==3) {//工作任务都是测试完成
					map.put("status", "03");
					testTaskMapper.updateReqFeaStatus(map);//系测完成
					status = "&nbsp;&nbsp;“<b>"+afterName+"</b>”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“<b>系测完成</b>”&nbsp;&nbsp;；";
				}else {
					map.put("status", "02");
					testTaskMapper.updateReqFeaStatus(map);//系测中
					status = "&nbsp;&nbsp;“<b>"+afterName+"</b>”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“<b>系测中</b>”&nbsp;&nbsp;；";
				}
			}else if(testStages.get(0)==2) {//测试任务下的工作任务都为版测
				List<Integer> testTaskStatus = testTaskMapper.selectTestTaskStatusById(testId);
				if(testTaskStatus.size()==1 && testTaskStatus.get(0)==3) {//工作任务都是测试完成
					map.put("status", "08");
					testTaskMapper.updateReqFeaStatus(map);//版测完成
					status = "&nbsp;&nbsp;“<b>"+afterName+"</b>”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“<b>版测完成</b>”&nbsp;&nbsp;；";
				}else {
					map.put("status", "06");
					testTaskMapper.updateReqFeaStatus(map);//版测中
					status = "&nbsp;&nbsp;“<b>"+afterName+"</b>”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“<b>版测中</b>”&nbsp;&nbsp;；";
				}
			}
		}else if(testStages.size() == 2){ //长度不为1(为2)，有版测也有系测
			List<Integer> testTaskStatus = testTaskMapper.selectTestTaskStatusById2(testId);
			if(testTaskStatus.size()==1 && testTaskStatus.get(0)==3) {//工作任务都是测试完成
				map.put("status", "08");
				testTaskMapper.updateReqFeaStatus(map);//版测完成
				status = "&nbsp;&nbsp;“<b>"+afterName+"</b>”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“<b>版测完成</b>”&nbsp;&nbsp;；";
			}else {
				map.put("status", "06");
				testTaskMapper.updateReqFeaStatus(map);//版测中
				status = "&nbsp;&nbsp;“<b>"+afterName+"</b>”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“<b>版测中</b>”&nbsp;&nbsp;；";
			}
		}
//		if(testTask.getTestStage()==1){
//			status = "&nbsp;&nbsp;“<b>"+afterName+"</b>”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“<b>系测中</b>”&nbsp;&nbsp;；";
//		}else{
//			status = "&nbsp;&nbsp;“<b>"+afterName+"</b>”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“<b>版测中</b>”&nbsp;&nbsp;；";
//		}

		TblRequirementFeatureLog log = new TblRequirementFeatureLog();
		log.setRequirementFeatureId(id);
		log.setLogType("拆分测试任务");
		
		log.setLogDetail(status+"拆分出工作任务："+testTask.getTestTaskCode()+" | "+testTask.getTestTaskName());
		insertLog(log, request);
	}

	@Override
	@Transactional(readOnly = false)
	public void handleTestTask(TblRequirementFeature requirementFeature, List<TblRequirementFeatureAttachement> files,
			HttpServletRequest request) {
		TblRequirementFeature requirementFeature2 = requirementFeatureMapper
				.selectByPrimaryKey(requirementFeature.getId());
		requirementFeatureMapper.updateTestTask(requirementFeature);
		//修改开发任务时间点追踪表  开发任务对应的测试任务首次变成实施完成时间 字段 
		Map<String, Object> jsonMap = new HashMap<>();
		TblRequirementFeature requirementFeature4 = requirementFeatureMapper.selectByPrimaryKey(requirementFeature.getId());
		if (requirementFeature4.getSystemId()!=null && requirementFeature4.getRequirementId()!=null) {
			jsonMap.put("requirementFeatureId", requirementFeature.getId());
			jsonMap.put("systemId", requirementFeature4.getSystemId());
			jsonMap.put("requirementId",requirementFeature4.getRequirementId() );
			jsonMap.put("requirementFeatureTestCompleteTime",new Timestamp(new Date().getTime()) );
			String jsonString = JsonUtil.toJson(jsonMap);
			testManageToDevManageInterface.updateReqFeatureTimeTrace(jsonString);
		}
		//为了找到这一次操作删除的附件 先查询出前当任务下所有的附件 和 返回的带id的附件比较 少了哪个 哪个就是删除了
		List<TblRequirementFeatureAttachement> allAtts = requirementFeatureAttachementMapper.findAttByRFId(requirementFeature.getId());
		
		getInsertAtts(requirementFeature.getId(),files, request, allAtts);
		
		TblRequirementFeatureLog log = new TblRequirementFeatureLog();
		log.setRequirementFeatureId(requirementFeature.getId());
		log.setLogType("处理开发任务");
		Map<String, String> map = new HashMap<>();
		map.put("actualSitStartDate", "actualSitStartDate");
		map.put("actualSitEndDate", "actualSitEndDate");
		map.put("actualSitWorkload", "actualSitWorkload");
		map.put("actualPptStartDate", "actualPptStartDate");
		map.put("actualPptEndDate", "actualPptEndDate");
		map.put("actualPptWorkload", "actualPptWorkload");
		String detail = ReflectUtils.packageModifyContent(requirementFeature, requirementFeature2, map);
		String status = "";
		if ("01".equals(requirementFeature2.getRequirementFeatureStatus())) {
			status = "&nbsp;&nbsp;“<b>系测待测试</b>”&nbsp;&nbsp;修改为&nbsp;&nbsp;“</b>系测完成</b>”&nbsp;&nbsp;；";
		} else if ("02".equals(requirementFeature2.getRequirementFeatureStatus())) {
			status = "&nbsp;&nbsp;“<b>系测中</b>”&nbsp;&nbsp;修改为&nbsp;&nbsp;“<b>系测完成</b>”&nbsp;&nbsp;；";
		} else if ("04".equals(requirementFeature2.getRequirementFeatureStatus())) {
			status = "&nbsp;&nbsp;“<b>系测待审核</b>”&nbsp;&nbsp;修改为&nbsp;&nbsp;“</b>系测完成</b>”&nbsp;&nbsp;；";
		}else if ("05".equals(requirementFeature2.getRequirementFeatureStatus())) {
			status = "&nbsp;&nbsp;“<b>版测待测试</b>”&nbsp;&nbsp;修改为&nbsp;&nbsp;“</b>版测完成</b>”&nbsp;&nbsp;；";
		}else if ("06".equals(requirementFeature2.getRequirementFeatureStatus())) {
			status = "&nbsp;&nbsp;“<b>版测中</b>”&nbsp;&nbsp;修改为&nbsp;&nbsp;“</b>版测完成</b>”&nbsp;&nbsp;；";
		}else if ("07".equals(requirementFeature2.getRequirementFeatureStatus())) {
			status = "&nbsp;&nbsp;“<b>版测待审核</b>”&nbsp;&nbsp;修改为&nbsp;&nbsp;“</b>版测完成</b>”&nbsp;&nbsp;；";
		}
		log.setLogDetail(status + detail);
		insertLog(log, request);
		
		getInsertLogAtts(files, request, allAtts, log);
		TblRequirementFeature requirementFeature3= requirementFeatureMapper
				.selectByPrimaryKey(requirementFeature.getId());
		if (2==requirementFeature3.getCreateType()) {
			Map<String, Object> dataResult = pushData(requirementFeature3);

			String taskId = "";
			if(requirementFeature3.getTaskId()!=null) {
				taskId = requirementFeature3.getTaskId().toString();
			}

			// 使用databus
			DataBusUtil.send(databusccName,taskId,JsonUtil.toJson(dataResult));
		}
	
	}
	public Map<String, Object> pushData(TblRequirementFeature requirementFeature) {
		Map<String, Object> mapAll = new LinkedHashMap<>();

		Map<String, Object> mapBody = new HashMap<>();
		mapBody.put("tbltaskId", requirementFeature.getTaskId());
		mapBody.put("taskResult","测试完成");

		Double actualSitWorkload=requirementFeatureMapper.getWorkload(requirementFeature.getId());
		Double actualPptWorkload=requirementFeatureMapper.getWorkload2(requirementFeature.getId());
		if(actualSitWorkload!=null&&actualSitWorkload>0) {
			mapBody.put("taskWorkload",actualSitWorkload);
		}else if(actualPptWorkload!=null&&actualPptWorkload>0){
			mapBody.put("taskWorkload",actualPptWorkload);
		}else {
			mapBody.put("taskWorkload",0.0);
		}
		mapAll.put("requestHead",DataBusRequestHead.getRequestHead());
		mapAll.put("requestBody",mapBody);
		return mapAll;
	}
	@Override
	@Transactional(readOnly = false)
	public void updateTestTask(TblRequirementFeature requirementFeature, List<TblRequirementFeatureAttachement> files,
			HttpServletRequest request, String developmentDeptNumber) {
		Long deptId = requirementFeatureMapper.findDeptIdByDeptNumber(developmentDeptNumber);
		requirementFeature.setDevelopmentDeptId(deptId);
		TblRequirementFeature requirementFeature2 = requirementFeatureMapper
				.selectByPrimaryKey(requirementFeature.getId());
		requirementFeatureMapper.updateByPrimaryKey(requirementFeature);
		Long userId = CommonUtil.getCurrentUserId(request);
		if(requirementFeature.getCreateType()==2) {
			TblUserInfo userInfo = tblUserInfoMapper.getUserById(userId);
			String beforeName = commissioningWindowMapper.getWindowName(requirementFeature2.getCommissioningWindowId());
			String afterName = commissioningWindowMapper.getWindowName(requirementFeature.getCommissioningWindowId());
			String deptBeforeName = requirementFeatureMapper.getDeptName(requirementFeature2.getDeptId());
			String deptAfterName = requirementFeatureMapper.getDeptName(requirementFeature.getDeptId());
			TblRequirementFeatureLog log2 = new TblRequirementFeatureLog();
			log2.setCreateBy(log2.getUserId());
			log2.setCreateDate(new Timestamp(new Date().getTime()));
			log2.setUserId(userId);
			log2.setUserName(userInfo.getUserName());
			log2.setUserAccount(userInfo.getUserAccount());
			String loginfo = JsonUtil.toJson(log2);
			if(requirementFeature.getCommissioningWindowId()==null) {
				requirementFeature.setCommissioningWindowId((long) 0);
			}
			if(requirementFeature.getDeptId()==null) {
				requirementFeature.setDeptId((long) 0);
			}
			if(requirementFeature2.getCommissioningWindowId() != requirementFeature.getCommissioningWindowId()) {
				testManageToDevManageInterface.synReqFeaturewindow(requirementFeature.getRequirementId(),
						requirementFeature.getSystemId(), requirementFeature.getCommissioningWindowId(), loginfo,beforeName,afterName);
			}
			if(requirementFeature2.getDeptId() != requirementFeature.getDeptId()) {
				testManageToDevManageInterface.synReqFeatureDept(requirementFeature.getRequirementId(),
						requirementFeature.getSystemId(),requirementFeature.getDeptId(),loginfo,deptBeforeName,deptAfterName);
			}
		}
		
		if ("02".equals(requirementFeature.getRequirementFeatureStatus())) {
			//修改开发任务时间点追踪表  开发任务对应的测试任务首次变成实施中时间 字段 
			Map<String, Object> jsonMap = new HashMap<>();
			TblRequirementFeature requirementFeature3 = requirementFeatureMapper.selectByPrimaryKey(requirementFeature.getId());
			if (requirementFeature3.getSystemId()!=null && requirementFeature3.getRequirementId()!=null) {
				jsonMap.put("requirementFeatureId", requirementFeature.getId());
				jsonMap.put("systemId", requirementFeature3.getSystemId());
				jsonMap.put("requirementId",requirementFeature3.getRequirementId() );
				jsonMap.put("requirementFeatureTestingTime",new Timestamp(new Date().getTime()) );
				String jsonString = JsonUtil.toJson(jsonMap);
				testManageToDevManageInterface.updateReqFeatureTimeTrace(jsonString);
			}
		}
		
		if ("03".equals(requirementFeature.getRequirementFeatureStatus())) {
			//修改开发任务时间点追踪表  开发任务对应的测试任务首次变成实施完成时间 字段 
			Map<String, Object> jsonMap = new HashMap<>();
			TblRequirementFeature requirementFeature3 = requirementFeatureMapper.selectByPrimaryKey(requirementFeature.getId());
			if (requirementFeature3.getSystemId()!=null && requirementFeature3.getRequirementId()!=null) {
				jsonMap.put("requirementFeatureId", requirementFeature.getId());
				jsonMap.put("systemId", requirementFeature3.getSystemId());
				jsonMap.put("requirementId",requirementFeature3.getRequirementId() );
				jsonMap.put("requirementFeatureTestCompleteTime",new Timestamp(new Date().getTime()) );
				String jsonString = JsonUtil.toJson(jsonMap);
				testManageToDevManageInterface.updateReqFeatureTimeTrace(jsonString);
			}
		}
		
		if("00".equals(requirementFeature.getRequirementFeatureStatus())) {
			//如果测试任务状态是取消 下属工作任务状态也变成取消
			testTaskMapper.updateStatus(requirementFeature.getId());
		}

		if((Integer.valueOf(requirementFeature.getRequirementFeatureStatus())>4&&
			Integer.valueOf(requirementFeature.getRequirementFeatureStatus())<9 )||
			requirementFeature.getRequirementFeatureStatus().equals("03")) {

			TblRequirementFeature requirementFeature3= requirementFeatureMapper
					.selectByPrimaryKey(requirementFeature.getId());
			if (2==requirementFeature3.getCreateType()) {
				Map<String, Object> dataResult = pushData(requirementFeature3);

				// 使用databus
				String taskId = "";
				if(requirementFeature3.getTaskId()!=null) {
					taskId = requirementFeature3.getTaskId().toString();
				}

				// 使用databus
				DataBusUtil.send(databusccName,taskId,JsonUtil.toJson(dataResult));

			}
		}

		// 把该开发任务下的所有工作任务的投产窗口字段都修改
		testTaskMapper.updateCommssioningWindowId(requirementFeature);
		//把缺陷的投产窗口也修改
		List<TblTestTask> tblTestTasks = testTaskMapper.findByReqFeatureId(requirementFeature.getId());
		String testTaskIds = "";
		for (TblTestTask tblTestTask : tblTestTasks) {
			testTaskIds += tblTestTask.getId()+",";
		}
		if (StringUtils.isNotBlank(testTaskIds)) {
			defectInfoMapper.updateCommssioningWindowId(testTaskIds,requirementFeature.getCommissioningWindowId());
		}


		List<TblRequirementFeatureAttachement> allAtts = requirementFeatureAttachementMapper.findAttByRFId(requirementFeature.getId());
		
		getInsertAtts(requirementFeature.getId(), files, request, allAtts);
		
		TblRequirementFeatureLog log = new TblRequirementFeatureLog();
		log.setRequirementFeatureId(requirementFeature.getId());
		log.setLogType("修改测试任务");
		Map<String, String> map = new HashMap<>();
		map.put("featureName", "featureName");
		map.put("featureOverview", "featureOverview");
		map.put("systemId", "systemId");
		map.put("requirementFeatureSource", "requirementFeatureSource");
		map.put("requirementId", "requirementId");
		//map.put("questionNumber", "questionNumber");
		map.put("manageUserId", "manageUserId");
		map.put("executeUserId", "executeUserId");
		map.put("deptId", "deptId");
		map.put("commissioningWindowId", "commissioningWindowId");
		map.put("actualSitStartDate", "actualSitStartDate");
		map.put("actualSitEndDate", "actualSitEndDate");
//		map.put("actualSitWorkload", "actualSitWorkload");
		map.put("actualPptStartDate", "actualPptStartDate");
		map.put("actualPptEndDate", "actualPptEndDate");
//		map.put("actualPptWorkload", "actualPptWorkload");
		map.put("submitTestTime", "submitTestTime");
		map.put("pptDeployTime", "pptDeployTime");
		map.put("fieldTemplate", "fieldTemplate");
		map.put("importantRequirementType","importantRequirementType");
		
	//	map.put("requirementCode", "requirementCode");
		map.put("requirementFeatureStatus", "requirementFeatureStatus");
		Map<String, String> detailMap = ReflectUtils.packageModifyContentReMap(requirementFeature, requirementFeature2,map);
		if (detailMap.containsKey(systemPropertyInfoName)) {
			String beforeName = "";
			String afterName = "";
			if (requirementFeature2.getSystemId()!=null) {
				beforeName = systemInfoMapper.getSystemName(requirementFeature2.getSystemId());
			}
			if(requirementFeature.getSystemId()!=null) {
				afterName = systemInfoMapper.getSystemName(requirementFeature.getSystemId());
			}
			String title = detailMap.get(systemPropertyInfoName).substring(0,detailMap.get(systemPropertyInfoName).indexOf("："))+"：";
			detailMap.put(systemPropertyInfoName,title+"&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;") ;
		}
		if (detailMap.containsKey(requirementPropertyInfoName)) {
			String beforeName = "";
			String afterName = "";
			if (requirementFeature2.getRequirementId()!=null) {
				beforeName = requirementInfoMapper.getReqCode(requirementFeature2.getRequirementId());
			}
			if(requirementFeature.getRequirementId()!=null) {
				afterName = requirementInfoMapper.getReqCode(requirementFeature.getRequirementId());
			}
			String title = detailMap.get(requirementPropertyInfoName).substring(0,detailMap.get(requirementPropertyInfoName).indexOf("："))+"：";
			detailMap.put(requirementPropertyInfoName,title+"&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;") ;	
		}
		if (detailMap.containsKey(manageUserPropertyInfoName)) {
			String beforeName = "";
			String afterName = "";
			if (requirementFeature2.getManageUserId()!=null) {
				beforeName = requirementFeatureMapper.getUserName(requirementFeature2.getManageUserId());
			}
			if(requirementFeature.getManageUserId()!=null) {
				afterName = requirementFeatureMapper.getUserName(requirementFeature.getManageUserId());
			}
			String title = detailMap.get(manageUserPropertyInfoName).substring(0,detailMap.get(manageUserPropertyInfoName).indexOf("："))+"：";
			detailMap.put(manageUserPropertyInfoName,title+"&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;") ;
		}
		if (detailMap.containsKey(executeUserPropertyInfoName)) {
			String beforeName = "";
			String afterName = "";
			if (requirementFeature2.getExecuteUserId()!=null) {
				beforeName = requirementFeatureMapper.getUserName(requirementFeature2.getExecuteUserId());
			}
			if(requirementFeature.getExecuteUserId()!=null) {
				afterName = requirementFeatureMapper.getUserName(requirementFeature.getExecuteUserId());
			}
			String title = detailMap.get(executeUserPropertyInfoName).substring(0,detailMap.get(executeUserPropertyInfoName).indexOf("："))+"：";
			detailMap.put(executeUserPropertyInfoName,title+"&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;") ;
		}
		if (detailMap.containsKey(deptPropertyInfoName)) {
			String beforeName = "";
			String afterName = "";
			if (requirementFeature2.getDeptId()!=null) {
				beforeName = requirementFeatureMapper.getDeptName(requirementFeature2.getDeptId());
			}
			if(requirementFeature.getDeptId()!=null) {
				afterName = requirementFeatureMapper.getDeptName(requirementFeature.getDeptId());
			}
			String title = detailMap.get(deptPropertyInfoName).substring(0,detailMap.get(deptPropertyInfoName).indexOf("："))+"：";
			detailMap.put(deptPropertyInfoName,title+"&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;") ;
		}
		if(detailMap.containsKey(commissioningWindowPropertyInfoName)) {
			
			String beforeName = "";
			String afterName = "";
			if (requirementFeature2.getCommissioningWindowId()!=null) {
				beforeName = commissioningWindowMapper.getWindowName(requirementFeature2.getCommissioningWindowId());
			}
			if(requirementFeature.getCommissioningWindowId()!=null) {
				afterName = commissioningWindowMapper.getWindowName(requirementFeature.getCommissioningWindowId());
			}
			String title = detailMap.get(commissioningWindowPropertyInfoName).substring(0,detailMap.get(commissioningWindowPropertyInfoName).indexOf("："))+"：";
			detailMap.put(commissioningWindowPropertyInfoName,title+"&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;") ;
		}
		if(detailMap.containsKey(sourcePropertyInfoName)) {
			
			String beforeName = "";
			String afterName = "";
			if (requirementFeature2.getRequirementFeatureSource()!=null) {
				String string = redisUtils.get("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_SOURCE_2").toString();
				JSONObject jsonObject = JSON.parseObject(string);
				beforeName = jsonObject.get(requirementFeature2.getRequirementFeatureSource().toString()).toString();
			}
			if(requirementFeature.getRequirementFeatureSource()!=null) {
				String string = redisUtils.get("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_SOURCE_2").toString();
				JSONObject jsonObject = JSON.parseObject(string);
				afterName = jsonObject.get(requirementFeature.getRequirementFeatureSource().toString()).toString();
			}
			String title = detailMap.get(sourcePropertyInfoName).substring(0,detailMap.get(sourcePropertyInfoName).indexOf("："))+"：";
			detailMap.put(sourcePropertyInfoName,title+"&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;") ;
		}
		/*if(detailMap.containsKey(sourcePropertyInfoName)) {
			String beforeName = "";
			String afterName = "";
			List<TblDataDic> dataDics = getDataFromRedis("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_SOURCE");
			for (TblDataDic tblDataDic : dataDics) {
				if (tblDataDic.getValueCode().equals(requirementFeature2.getRequirementFeatureSource().toString())) {
					beforeName = tblDataDic.getValueName();
				}
				if (tblDataDic.getValueCode().equals(requirementFeature.getRequirementFeatureSource().toString())) {
					afterName = tblDataDic.getValueName();
				}
			}
			String title = detailMap.get(sourcePropertyInfoName).substring(0,detailMap.get(sourcePropertyInfoName).indexOf("："))+"：";
			detailMap.put(sourcePropertyInfoName,title+beforeName+"——>"+afterName) ;
		}*/
		if(detailMap.containsKey(reqFeatureStatusInfoName)) {
			String beforeName = "";
			String afterName = "";
			List<TblDataDic> dataDics = getDataFromRedis("TBL_REQUIREMENT_FEATURE_REQUIREMENT_FEATURE_STATUS_2");
			for (TblDataDic tblDataDic : dataDics) {
				if (requirementFeature2.getRequirementFeatureStatus()!=null) {
					if (tblDataDic.getValueCode().equals(requirementFeature2.getRequirementFeatureStatus().toString())) {
						beforeName = tblDataDic.getValueName();
					}
				}
				if (tblDataDic.getValueCode().equals(requirementFeature.getRequirementFeatureStatus().toString())) {
					afterName = tblDataDic.getValueName();
				}
			}
			String title = detailMap.get(reqFeatureStatusInfoName).substring(0,detailMap.get(reqFeatureStatusInfoName).indexOf("："))+"：";
			detailMap.put(reqFeatureStatusInfoName,title+"&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;") ;
		}
		if (detailMap.containsKey("重点需求类型")) {
			String beforeName = "";
			String afterName = "";
			if (requirementFeature2.getImportantRequirementType()!=null) {
				if(requirementFeature2.getImportantRequirementType().equals("1")){
					beforeName="是";
				}else{
					beforeName="否";
				}
			}
			if(requirementFeature.getImportantRequirementType()!=null) {
				if(requirementFeature.getImportantRequirementType().equals("1")){
					afterName="是";
				}else{
					afterName="否";
				}
			}
			String title = detailMap.get("重点需求类型").substring(0,detailMap.get("重点需求类型").indexOf("："))+"：";
			detailMap.put("重点需求类型",title+"&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;") ;
		}
		String detail = detailMap.toString().replace("=", "").replace(",", "；").replace("{", "").replace("}", "");
		log.setLogDetail(detail);
		insertLog(log, request);
		
		//日志附件
		getInsertLogAtts(files, request, allAtts, log);

	}

	@Override
	public List<TblCommissioningWindow> getWindows() {
		return commissioningWindowMapper.getAll();
	}

	@Override
	public List<Map<String, Object>> getSplitUser(Long systemId) {
		return requirementFeatureMapper.getSplitUser(systemId);
	}

	@Override
	public List<TblRequirementFeature> findByName(TblRequirementFeature requirementFeature) {
		return requirementFeatureMapper.findByName(requirementFeature);
	}

	@Override
	public List<Map<String, Object>> findBrother(Long requirementId, Long id) {
		return requirementFeatureMapper.findBrother(requirementId, id);
	}

	@Override
	@Transactional(readOnly = false)
	public void updateStatus(Long id,Integer testStage, HttpServletRequest request) {
		//修改为实施中
		if(testStage==1){
			requirementFeatureMapper.updateStatus("02",id);
		}else{
			requirementFeatureMapper.updateStatus("06",id);
			TblRequirementFeature requirementFeature3= requirementFeatureMapper
					.selectByPrimaryKey(id);
			if (2==requirementFeature3.getCreateType()) {
				Map<String, Object> dataResult = pushData(requirementFeature3);
				String taskId = "";
				if (requirementFeature3.getTaskId() != null) {
					taskId = requirementFeature3.getTaskId().toString();
				}
				// 使用databus
				DataBusUtil.send(databusccName,taskId,JsonUtil.toJson(dataResult));
			}
		}

		//修改开发任务时间点追踪表  开发任务对应的测试任务首次变成实施中时间 字段 
		Map<String, Object> jsonMap = new HashMap<>();
		TblRequirementFeature requirementFeature2 = requirementFeatureMapper.selectByPrimaryKey(id);
		if (requirementFeature2.getSystemId()!=null && requirementFeature2.getRequirementId()!=null) {
			jsonMap.put("requirementFeatureId", id);
			jsonMap.put("systemId", requirementFeature2.getSystemId());
			jsonMap.put("requirementId",requirementFeature2.getRequirementId() );
			jsonMap.put("requirementFeatureTestingTime",new Timestamp(new Date().getTime()) );
			String jsonString = JsonUtil.toJson(jsonMap);
			testManageToDevManageInterface.updateReqFeatureTimeTrace(jsonString);
		}
		
	}

	@Override
	public String findMaxCode(int length) {
		return requirementFeatureMapper.findMaxCode(length);
	}

	@Override
	public List<TblRequirementFeatureAttachement> findAtt(Long id) {
		return requirementFeatureAttachementMapper.findAttByRFId(id);
	}

	@Override
	public int getCountRequirement(TblRequirementInfo record) {
		return requirementInfoMapper.getCountRequirement(record);
	}

	@Override
	public List<Map<String, Object>> getAllRequirement(TblRequirementInfo requirement, Integer pageNumber,
			Integer pageSize) {
		Map<String, Object> map = new HashMap<>();
		int start = (pageNumber - 1) * pageSize;
		map.put("start", start);
		map.put("pageSize", pageSize);
		map.put("requirement", requirement);
		return requirementInfoMapper.getAllReq2(map);
	}

	@Override
	public List<Map<String, Object>> getAllSystemInfo(TblSystemInfo systemInfo,Long uid,Integer pageNumber, Integer pageSize) {
		Map<String, Object> map = new HashMap<>();
		int start = (pageNumber - 1) * pageSize;
		map.put("start", start);
		map.put("pageSize", pageSize);
		map.put("uid",uid);
		map.put("systemInfo", systemInfo);
		return systemInfoMapper.getAllSystemInfo(map);
	}

	@Override
	public List<TblSystemInfo> findAll() {
		return systemInfoMapper.getAll();
	}

	@Override
	public List<TblRequirementInfo> getAllReq() {
		return requirementInfoMapper.getAllReq();
	}

	@Override
	@Transactional(readOnly = false)
	public void updateTransfer(TblRequirementFeature requirementFeature,String transferRemark, HttpServletRequest request) {
		TblRequirementFeature requirementFeature2 = requirementFeatureMapper
				.selectByPrimaryKey(requirementFeature.getId());
		requirementFeatureMapper.updateTransfer(requirementFeature);
		TblRequirementFeatureLog log = new TblRequirementFeatureLog();
		log.setRequirementFeatureId(requirementFeature.getId());
		log.setLogType("转派测试任务");
		Map<String, String> map = new HashMap<>();
		map.put("executeUserId", "executeUserId");
		String detail = ReflectUtils.packageModifyContent(requirementFeature, requirementFeature2, map);
		if (!StringUtils.isBlank(detail)) {
			String beforeName = "";
			String afterName = "";
			if (requirementFeature2.getExecuteUserId()!=null) {
				beforeName = requirementFeatureMapper.getUserName(requirementFeature2.getExecuteUserId());
			}
			if(requirementFeature.getExecuteUserId()!=null) {
				afterName = requirementFeatureMapper.getUserName(requirementFeature.getExecuteUserId());
			}
			//执行人：&nbsp;&nbsp;“<b>14</b>”&nbsp;&nbsp;修改为&nbsp;&nbsp;“<b>4</b>”&nbsp;&nbsp;；
			String title = detail.substring(0,detail.indexOf("：")+1);
			detail = title+"&nbsp;&nbsp;“&nbsp;<b>"+beforeName+"</b>&nbsp;”&nbsp;&nbsp;"+"修改为"+"&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;"+"；";
			
		}
		String remark = "";
		if(!StringUtils.isBlank(transferRemark)) {
			remark = "备注内容： "+transferRemark;
		}
		log.setLogDetail(detail + remark);
		insertLog(log, request);
	}

	@Override
	public List<TblRequirementFeature> findSynDevTask(TblRequirementFeature requirementFeature) {
		return requirementFeatureMapper.findSynDevTask(requirementFeature);
	}

	@Override
	@Transactional(readOnly = false)
	public void mergeDevTask(TblRequirementFeature requirementFeature, Long synId, HttpServletRequest request) {
		TblRequirementFeature requirementFeature2 = requirementFeatureMapper
				.selectByPrimaryKey(requirementFeature.getId());

		requirementFeatureMapper.updateTaskId(requirementFeature);
		// 把原来的同步任务置为status 2
		requirementFeatureMapper.updateSynStatus(synId);
		//查询出同步任务下的工作任务移到要合并的自建任务下
		List<TblTestTask> testTasks = testTaskMapper.findByReqFeatureId(synId);
		Map<String, Object> map2 = new HashMap<>();
		map2.put("testTasks",testTasks);
		map2.put("reqFeatureId", requirementFeature.getId());
		testTaskMapper.updateReqFeatureId(map2);
		
		TblRequirementFeatureLog log = new TblRequirementFeatureLog();
		log.setRequirementFeatureId(requirementFeature.getId());
		log.setLogType("合并开发任务");
		Map<String, String> map = new HashMap<>();
		map.put("taskId", "taskId");
		String detail = ReflectUtils.packageModifyContent(requirementFeature, requirementFeature2,map);
		log.setLogDetail(detail);
		insertLog(log, request);
	}

	@Override
	public TblRequirementFeatureAttachement getReqFeatureAttByUrl(String filePath) {
		return requirementFeatureAttachementMapper.getReqFeatureAttByUrl(filePath);
	}

	@Override
	@Transactional(readOnly = false)
	public void addRemark(TblRequirementFeatureRemark remark, List<TblRequirementFeatureRemarkAttachement> files,
			HttpServletRequest request) {
		requirementFeatureRemarkMapper.addRemark(remark);
		Long remarkId = remark.getId();
		if (files != null && files.size() > 0) {
			for (TblRequirementFeatureRemarkAttachement tblRequirementFeatureRemarkAttachement : files) {
				tblRequirementFeatureRemarkAttachement.setRequirementFeatureRemarkId(remarkId);
				tblRequirementFeatureRemarkAttachement.setCreateBy(CommonUtil.getCurrentUserId(request));
				tblRequirementFeatureRemarkAttachement.setCreateDate(new Timestamp(new Date().getTime()));
				requirementFeatureRemarkAttachementMapper.insertAtt(tblRequirementFeatureRemarkAttachement);
			}
		}
	}

	@Override
	public List<TblRequirementFeatureLog> findLog(Long id) {
		List<TblRequirementFeatureLog> logs = requirementFeatureLogMapper.findByReqFeatureId(id);
		for (TblRequirementFeatureLog tblRequirementFeatureLog : logs) {
			List<TblRequirementFeatureLogAttachement> logAttachements = requirementFeatureLogAttachementMapper
					.findByLogId(tblRequirementFeatureLog.getId());
			tblRequirementFeatureLog.setLogAttachements(logAttachements);
		}
		return logs;
	}

	@Override
	public List<TblRequirementFeatureRemark> findRemark(Long id) {
		List<TblRequirementFeatureRemark> remarks = requirementFeatureRemarkMapper.findRemarkByReqFeatureId(id);
		for (TblRequirementFeatureRemark tblRequirementFeatureRemark : remarks) {
			List<TblRequirementFeatureRemarkAttachement> remarkAtts = requirementFeatureRemarkAttachementMapper
					.findByRemarkId(tblRequirementFeatureRemark.getId());
			tblRequirementFeatureRemark.setRemarkAttachements(remarkAtts);
		}
		return remarks;
	}

	@Override
	public int findDefectNum(Long id, int status) {
		return requirementFeatureMapper.findDefectNum(id,status);
	}
	
	@Transactional(readOnly = false)
	public void insertLog(TblRequirementFeatureLog log, HttpServletRequest request) {
		log.setCreateBy(CommonUtil.getCurrentUserId(request));
		log.setCreateDate(new Timestamp(new Date().getTime()));
		log.setUserId(CommonUtil.getCurrentUserId(request));
		log.setUserAccount(CommonUtil.getCurrentUserAccount(request));
		log.setUserName(CommonUtil.getCurrentUserName(request));
		requirementFeatureLogMapper.insert(log);
	}
	
	@Transactional(readOnly = false)
	public void insertLogAtt(TblRequirementFeatureAttachement tblRequirementFeatureAttachement,TblRequirementFeatureLogAttachement logAttachement,Long logId,HttpServletRequest request) {
		logAttachement.setCreateBy(CommonUtil.getCurrentUserId(request));
		logAttachement.setCreateDate(new Timestamp(new Date().getTime()));
		logAttachement.setFileNameNew(tblRequirementFeatureAttachement.getFileNameNew());
		logAttachement.setFileNameOld(tblRequirementFeatureAttachement.getFileNameOld());
		logAttachement.setFilePath(tblRequirementFeatureAttachement.getFilePath());
		logAttachement.setFileS3Bucket(tblRequirementFeatureAttachement.getFileS3Bucket());
		logAttachement.setFileS3Key(tblRequirementFeatureAttachement.getFileS3Key());
		logAttachement.setFileType(tblRequirementFeatureAttachement.getFileType());
		logAttachement.setRequirementFeatureLogId(logId);
		requirementFeatureLogAttachementMapper.insert(logAttachement);
	}
	/**
	 * 新增删除的附件
	 * @param 
	 * */
	@Transactional(readOnly = false)
	public void getInsertAtts(Long reqFeatureId,List<TblRequirementFeatureAttachement> files, HttpServletRequest request,
			List<TblRequirementFeatureAttachement> allAtts) {
		List<Long> allids = new ArrayList<>();
		if (allAtts!=null&&allAtts.size()>0) {
			for (TblRequirementFeatureAttachement tblRequirementFeatureAttachement : allAtts) {
				allids.add(tblRequirementFeatureAttachement.getId());
			}
		}
		List<Long> ids = new ArrayList<>();
		if (files!=null && files.size()>0) {
			for (TblRequirementFeatureAttachement tblRequirementFeatureAttachement : files) {
				if (tblRequirementFeatureAttachement.getId()!=null) {
					ids.add(tblRequirementFeatureAttachement.getId());
				}
			}
		}
		List<Long> diffIds = (List<Long>) CollectionUtil.getDiffent(allids, ids);
		//删除的附件
		if (diffIds!=null && diffIds.size()>0) {
			requirementFeatureAttachementMapper.deleteByIds(diffIds);
			
		}
		//新增的附件
		if (files != null && files.size() > 0) {
			for (TblRequirementFeatureAttachement tblRequirementFeatureAttachement : files) {
				if (tblRequirementFeatureAttachement.getId()==null || "".equals(tblRequirementFeatureAttachement.getId())) {
					tblRequirementFeatureAttachement.setRequirementFeatureId(reqFeatureId);
					tblRequirementFeatureAttachement.setCreateBy(CommonUtil.getCurrentUserId(request));
					tblRequirementFeatureAttachement.setCreateDate(new Timestamp(new Date().getTime()));
					requirementFeatureAttachementMapper.insertAtt(tblRequirementFeatureAttachement);
				}
			}
		}
	}
	
	/**
	 * 获取日志附件
	 * @param
	 * */
	@Transactional(readOnly = false)
	public void getInsertLogAtts(List<TblRequirementFeatureAttachement> files, HttpServletRequest request,
			List<TblRequirementFeatureAttachement> allAtts, TblRequirementFeatureLog log) {
		List<Long> allids = new ArrayList<>();
		if (allAtts!=null&&allAtts.size()>0) {
			for (TblRequirementFeatureAttachement tblRequirementFeatureAttachement : allAtts) {
				allids.add(tblRequirementFeatureAttachement.getId());
			}
		}
		List<Long> ids = new ArrayList<>();
		if (files!=null && files.size()>0) {
			for (TblRequirementFeatureAttachement tblRequirementFeatureAttachement : files) {
				if (tblRequirementFeatureAttachement.getId()!=null) {
					ids.add(tblRequirementFeatureAttachement.getId());
				}
			}
		}
		List<Long> diffIds = (List<Long>) CollectionUtil.getDiffent(allids, ids);
		//日志删除的附件
		if (diffIds!=null && diffIds.size()>0) {
			for (Long id : diffIds) {
				TblRequirementFeatureAttachement attachement = requirementFeatureAttachementMapper.selectByPrimaryKey(id);
				TblRequirementFeatureLogAttachement logAttachement = new TblRequirementFeatureLogAttachement();
				logAttachement.setStatus(2);
				insertLogAtt(attachement,logAttachement,log.getId(),request);
			}
		}
		//日志新增的附件
		if (files != null && files.size() > 0) {
			for (TblRequirementFeatureAttachement tblRequirementFeatureAttachement : files) {
				if (tblRequirementFeatureAttachement.getId()==null || "".equals(tblRequirementFeatureAttachement.getId())) {
					TblRequirementFeatureLogAttachement logAttachement = new TblRequirementFeatureLogAttachement();
					logAttachement.setStatus(1);
					insertLogAtt(tblRequirementFeatureAttachement,logAttachement,log.getId(),request);
				}
			}
		}
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
	
	/**
	 * 根据测试集信息查询测试任务
	 */
	@Override
	public List<Map<String, Object>> selectTaskByTestSetCon(String nameOrNumber, String createBy, String testTaskId,Long taskId) {
		List<Long> userIds = JSON.parseArray(createBy, Long.class);
		List<Long> testTaskIds = JSON.parseArray(testTaskId,Long.class);
		return requirementFeatureMapper.selectTaskByTestSetCon(nameOrNumber, userIds, testTaskIds,taskId);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void changeCancelStatus(Long requirementId) {
		requirementFeatureMapper.changeCancelStatus(requirementId);
		//把测试任务下属工作任务置为取消状态  
		List<TblRequirementFeature> requirementFeatures = requirementFeatureMapper.findByrequirementId(requirementId);
		for (TblRequirementFeature tblRequirementFeature : requirementFeatures) {
			testTaskMapper.updateStatus(tblRequirementFeature.getId());
		}	
	}

	@Override
	@Transactional(readOnly = false)
	public void synReqFeatureDeployStatus(Long requirementId, Long systemId, String deployStatus,String loginfo) {
		//找到需要修改状态的任务 同一需求 同一系统下 日志需要
		List<TblRequirementFeatureDeployStatus> deployStatusList = JSONObject.
				parseArray(deployStatus,TblRequirementFeatureDeployStatus.class);

		List<TblRequirementFeature> requirementFeatures = requirementFeatureMapper.selectBySystemIdAndReqId1(systemId,requirementId);
		for (TblRequirementFeature feature : requirementFeatures) {
			List<TblRequirementFeatureDeployStatus> oldDeployStatusList =
					deployStatusMapper.findByReqFeatureId(feature.getId());

			deployStatusMapper.deleteByFeatureId(feature.getId());
			for (TblRequirementFeatureDeployStatus deployStatus1 : deployStatusList){
				TblRequirementFeatureDeployStatus featureDeployStatus1 =
						new TblRequirementFeatureDeployStatus();
				featureDeployStatus1.setRequirementFeatureId(feature.getId());
				featureDeployStatus1.setDeployStatu(deployStatus1.getDeployStatu());
				featureDeployStatus1.setDeployTime(deployStatus1.getDeployTime());
				featureDeployStatus1.setStatus(1);
				featureDeployStatus1.setCreateBy(deployStatus1.getCreateBy());
				featureDeployStatus1.setCreateDate(deployStatus1.getCreateDate());
				featureDeployStatus1.setLastUpdateBy(deployStatus1.getLastUpdateBy());
				featureDeployStatus1.setLastUpdateDate(deployStatus1.getLastUpdateDate());
				deployStatusMapper.insertFeatureDeployStatus(featureDeployStatus1);
			}
			insertFeatureLog1(feature.getId(),oldDeployStatusList,loginfo);
		}
	}


	private String getDeployName(Integer deployStatu){
		String deployName = "";
		VelocityDataDict dict= new VelocityDataDict();
		Map<String, String> result = dict.getDictMap("TBL_REQUIREMENT_FEATURE_DEPLOY_STATUS");
		for(Map.Entry<String, String> entry : result.entrySet()){
			if(deployStatu == Integer.valueOf(entry.getKey())){
				deployName = entry.getValue();
			}
		}
		return deployName;
	}

	@Override
	@Transactional(readOnly = false)
	public void updateDeployStatus(Long reqFeatureId, String deployStatus,HttpServletRequest request) {
		String [] strAll = deployStatus.split(",");
		List<TblRequirementFeatureDeployStatus> deployStatusList = deployStatusMapper.findByReqFeatureId(reqFeatureId);
		List<Integer> delDeployStatusList = delDeployStatus(deployStatusList,strAll);
		for(Integer delDeployStatus : delDeployStatusList){
			TblRequirementFeatureDeployStatus delDeployStatus1 =
					deployStatusMapper.findByReqFeatureIdDeployStatu(reqFeatureId,delDeployStatus);
			deployStatusMapper.deleteById(delDeployStatus1.getId());
		}
		for (String deploy : strAll){
			TblRequirementFeatureDeployStatus featureDeployStatus
					= deployStatusMapper.findByReqFeatureIdDeployStatu(reqFeatureId,Integer.valueOf(deploy));
			TblRequirementFeatureDeployStatus featureDeployStatus1= new TblRequirementFeatureDeployStatus();
			featureDeployStatus1.setRequirementFeatureId(reqFeatureId);
			featureDeployStatus1.setDeployStatu(Integer.valueOf(deploy));
			featureDeployStatus1.setStatus(1);
			featureDeployStatus1.setCreateBy(CommonUtil.getCurrentUserId(request));
			featureDeployStatus1.setCreateDate(new Timestamp(new Date().getTime()));
			featureDeployStatus1.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
			featureDeployStatus1.setLastUpdateDate(new Timestamp(new Date().getTime()));
			CommonUtil.setBaseValue(featureDeployStatus1,request);
			if(featureDeployStatus==null){
				featureDeployStatus1.setDeployTime(new Timestamp(new Date().getTime()));
				deployStatusMapper.insertFeatureDeployStatus(featureDeployStatus1);
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void insertFeatureLog(Long reqFeatureId, List<TblRequirementFeatureDeployStatus>
			oldDeployStatus,HttpServletRequest request){
		// 插入日志
		String beforeName = "";
		String afterName = "";
		List<TblRequirementFeatureDeployStatus> newDeployStatus = deployStatusMapper.findByReqFeatureId(reqFeatureId);

		if(oldDeployStatus!=null && oldDeployStatus.size()>0){
			for (TblRequirementFeatureDeployStatus deployStatus : oldDeployStatus){
				beforeName = beforeName + getDeployName(deployStatus.getDeployStatu())+ " | " +
						getTime(deployStatus.getDeployTime()) + "，";
			}
		}
		if(newDeployStatus!=null && newDeployStatus.size()>0){
			for (TblRequirementFeatureDeployStatus deployStatus : newDeployStatus){
				afterName = afterName + getDeployName(deployStatus.getDeployStatu())+ " | " +
						getTime(deployStatus.getDeployTime()) + "，";
			}
		}

		TblRequirementFeatureLog log = new TblRequirementFeatureLog();
		log.setRequirementFeatureId(reqFeatureId);
		log.setLogType("修改测试任务");
		log.setLogDetail("部署状态：" + "&nbsp;&nbsp;“&nbsp;<b>" + beforeName + "</b>&nbsp;”&nbsp;&nbsp;" + "修改为"
				+ "&nbsp;&nbsp;“&nbsp;<b>" + afterName + "</b>&nbsp;”&nbsp;&nbsp;");
		insertLog(log, request);
	}

	private String getTime(Timestamp ts){
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			if(ts!=null){
				tsStr = sdf.format(ts);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tsStr;
	}

	@Override
	@Transactional(readOnly = false)
	public void insertFeatureLog1(Long reqFeatureId, List<TblRequirementFeatureDeployStatus>
			oldDeployStatus,String loginfo){
		// 插入日志
		String beforeName = "";
		String afterName = "";
		List<TblRequirementFeatureDeployStatus> newDeployStatus = deployStatusMapper.findByReqFeatureId(reqFeatureId);

		if(oldDeployStatus!=null && oldDeployStatus.size()>0){
			for (TblRequirementFeatureDeployStatus deployStatus : oldDeployStatus){
				beforeName = beforeName + getDeployName(deployStatus.getDeployStatu())+ " | " +
						getTime(deployStatus.getDeployTime()) + "，";
			}
		}
		if(newDeployStatus!=null && newDeployStatus.size()>0){
			for (TblRequirementFeatureDeployStatus deployStatus : newDeployStatus){
				afterName = afterName + getDeployName(deployStatus.getDeployStatu())+ " | " +
						getTime(deployStatus.getDeployTime()) + "，";
			}
		}

		TblRequirementFeatureLog log = JsonUtil.fromJson(loginfo, TblRequirementFeatureLog.class);
		log.setRequirementFeatureId(reqFeatureId);
		log.setCreateBy(log.getUserId());
		log.setCreateDate(new Timestamp(new Date().getTime()));
		log.setUserId(log.getUserId());
		log.setLogType("修改开发任务");
		log.setLogDetail("部署状态：" + "&nbsp;&nbsp;“&nbsp;<b>" + beforeName + "</b>&nbsp;”&nbsp;&nbsp;" + "修改为"
				+ "&nbsp;&nbsp;“&nbsp;<b>" + afterName + "</b>&nbsp;”&nbsp;&nbsp;");
		requirementFeatureLogMapper.insert(log);
	}

	private List<Integer> delDeployStatus(List<TblRequirementFeatureDeployStatus> deployStatusList, String [] strAll) {
		List<Integer> flag = new ArrayList<>();
		List<Integer> beforFlag = new ArrayList<>();
		for (TblRequirementFeatureDeployStatus deployStatus : deployStatusList) {
			beforFlag.add(deployStatus.getDeployStatu());
		}
		for (Integer deployStatus : beforFlag) {
			for (String str : strAll) {
				if(str .equals(deployStatus.toString())){
					flag.add(Integer.valueOf(str));
				}
			}
		}
		HashSet h1 = new HashSet(flag);
		flag.clear();
		flag.addAll(h1);
		beforFlag.removeAll(flag);
		return beforFlag;
	}

	/**
	 * 获取测试任务树
	 */
	@Override
	public List<TblRequirementFeature> getTaskTree(Long userId, String taskName,String testSetName,
			Integer requirementFeatureStatus) {
		return requirementFeatureMapper.getTaskTree(userId, taskName, testSetName, requirementFeatureStatus);
	}

	/**
	 * 根据测试工作ID 查询 测试任务信息
	 * @param testTaskId
	 * @return
	 */
	@Override
	public TblRequirementFeature getFeatureByTestTaskId(Long testTaskId) {
		return requirementFeatureMapper.getFeatureByTestTaskId(testTaskId);
	}

	@Override
	public int getAllRequirementCount(TblRequirementInfo requirement) {
		return requirementInfoMapper.getAllRequirementCount(requirement);
	}
	
	@Override
	@Transactional(readOnly = false)
	public void cancelStatusReqFeature(Long reqFeatureId) {
		//把该开发任务下属工作任务置为取消状态  
		testTaskMapper.updateStatus(reqFeatureId);
	}

	@Override
	@Transactional(readOnly = false)
	public void synReqFeaturewindow(Long requirementId, Long systemId, Long commissioningWindowId, String loginfo, String beforeName, String afterName) {
		// TODO Auto-generated method stub
		List<TblRequirementFeature> requirementFeatures = requirementFeatureMapper.selectBySystemIdAndReqId(systemId,requirementId);
		for (TblRequirementFeature tblRequirementFeature : requirementFeatures) {
//			String beforeName = "";
//			String afterName = "";
			TblRequirementFeatureLog log = JsonUtil.fromJson(loginfo, TblRequirementFeatureLog.class);
			log.setRequirementFeatureId(tblRequirementFeature.getId());
			log.setLogType("修改投产窗口");
//			if (!StringUtils.isBlank(beforeName)) {
//				beforeName = beforeName.substring(0,beforeName.length()-1);
//			}
//			if (!StringUtils.isBlank(afterName)) {
//				afterName = afterName.substring(0,afterName.length()-1);
//			}
			log.setLogDetail("投产窗口：" + "&nbsp;&nbsp;“&nbsp;<b>"+beforeName +"</b>&nbsp;”&nbsp;&nbsp;"+ "修改为" + "&nbsp;&nbsp;“&nbsp;<b>"+afterName+"</b>&nbsp;”&nbsp;&nbsp;");
			requirementFeatureLogMapper.insert(log);
		}
		if(commissioningWindowId==0) {
			commissioningWindowId=null;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("requirementId", requirementId);
		map.put("systemId", systemId);
		map.put("commissioningWindowId", commissioningWindowId);
		requirementFeatureMapper.synReqFeaturewindow(map);

}

	@Override
	@Transactional(readOnly = false)
	public void synReqFeaturewindow(Long requirementId, Long systemId, Integer deptId, String loginfo,
			String deptBeforeName, String deptAfterName) {
		// TODO Auto-generated method stub
		List<TblRequirementFeature> requirementFeatures = requirementFeatureMapper.selectBySystemIdAndReqId(systemId,requirementId);
		for (TblRequirementFeature tblRequirementFeature : requirementFeatures) {
			TblRequirementFeatureLog log = JsonUtil.fromJson(loginfo, TblRequirementFeatureLog.class);
			log.setRequirementFeatureId(tblRequirementFeature.getId());
			log.setLogType("修改所属处室");
			log.setLogDetail("所属处室：" + "&nbsp;&nbsp;“&nbsp;<b>"+deptBeforeName +"</b>&nbsp;”&nbsp;&nbsp;"+ "修改为" + "&nbsp;&nbsp;“&nbsp;<b>"+deptAfterName+"</b>&nbsp;”&nbsp;&nbsp;");
			requirementFeatureLogMapper.insert(log);
		}
		if(deptId == 0) {
			deptId = null;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("requirementId", requirementId);
		map.put("systemId", systemId);
		map.put("deptId", deptId);
		requirementFeatureMapper.synReqFeatureDept(map);
	}

	//合并
	@Override
	@Transactional(readOnly = false)
	public void mergeTestTask(Long requirementId, Long systemId, HttpServletRequest request) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<>();
		map.put("requirementId", requirementId);
		map.put("systemId", systemId);
		//自建任务
		TblRequirementFeature requirementFesature = requirementFeatureMapper.findSelf(map);
		//同步任务
		TblRequirementFeature requirementFesature2 = requirementFeatureMapper.findSyn(map);
		if(requirementFesature != null && requirementFesature2 != null) {
			Map<String,Object> map2 = new HashMap<>();
			map2.put("id", requirementFesature.getId());
			map2.put("taskId",requirementFesature2.getTaskId());
			map2.put("code", requirementFesature2.getFeatureCode());
			map2.put("name", requirementFesature2.getFeatureName());
			requirementFeatureMapper.synRequirementFeature(map2);
			//把同步任务状态改为2
			requirementFeatureMapper.updateSynFeature(requirementFesature2.getId());
		}
        if((Integer.valueOf(requirementFesature.getRequirementFeatureStatus())>4&&
                Integer.valueOf(requirementFesature.getRequirementFeatureStatus())<9 )||
                requirementFesature.getRequirementFeatureStatus().equals("03")) {

            Map<String, Object> dataResult = pushData(requirementFesature);
			String taskId = requirementFesature2.getTaskId().toString();

			// 使用databus
			DataBusUtil.send(databusccName,taskId,JsonUtil.toJson(dataResult));
        }
	}

	//选择需求查询需求的部门number
	@Override
	@Transactional(readOnly = true)
	public TblDeptInfo findDeptNumber(Long id) {
		// TODO Auto-generated method stub
		return requirementFeatureMapper.findDeptNumberByRequirementId(id);
	}

	@Override
	public void detailEnvDate(List<Map<String, Object>> list,String env,Timestamp timestamp) {
		List<String> ids=new ArrayList<>();
		for(Map<String,Object> map:list){
			String  requirement=map.get("requirement").toString();
			String  systemId=map.get("systemId").toString();
			Map<String,Object> param=new HashMap<>();
			param.put("REQUIREMENT_ID",requirement);
			param.put("SYSTEM_ID",systemId);
			param.put("STATUS",1);
			List<TblRequirementFeature> requirementFeatures=requirementFeatureMapper.selectByMap(param);
			if(requirementFeatures!=null && requirementFeatures.size()>0){
				for(TblRequirementFeature requirementFeature:requirementFeatures){
					if(env.startsWith(Constants.XICE)){
						requirementFeature.setSubmitTestTime(timestamp);
					}else{
						requirementFeature.setPptDeployTime(timestamp);
					}
					requirementFeatureMapper.updateById(requirementFeature);
				}

			}

		}

	}
	
}
