package cn.pioneeruniverse.dev.notify;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import cn.pioneeruniverse.common.nexus.NexusUtil;
import cn.pioneeruniverse.common.nexus.NexusAssetBO;
import cn.pioneeruniverse.common.nexus.NexusSearchVO;
import cn.pioneeruniverse.dev.dao.mybatis.*;
import cn.pioneeruniverse.dev.entity.*;
import cn.pioneeruniverse.dev.feignInterface.DevManageToSystemInterface;
import cn.pioneeruniverse.dev.service.AutomatTest.AutomatTestService;
import cn.pioneeruniverse.dev.service.deploy.IDeployService;
import cn.pioneeruniverse.dev.service.devtask.DevTaskService;
import cn.pioneeruniverse.dev.service.packages.PackageService;
import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.StringUtil;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.sonar.bean.SonarQubeException;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.dev.controller.DeployController;
import cn.pioneeruniverse.dev.controller.DevTaskController;
import cn.pioneeruniverse.dev.service.build.IJenkinsBuildService;
import cn.pioneeruniverse.dev.service.structure.IStructureService;

@RestController
@RequestMapping("notify")
public class StructureNotify {
	private final static Logger callbackLog = LoggerFactory.getLogger(StructureNotify.class);
	@Autowired
	private IStructureService iStructureService;
	@Autowired
	private IJenkinsBuildService jenkinsBuildService;
	@Autowired
	private DevTaskController devTaskController;
	@Autowired
	private DevTaskService devTaskService;
	@Autowired
	private DeployController deployController;
	@Autowired
	private IDeployService deployService;
	@Autowired
	private AutomatTestService automatTestService;
	@Autowired
	private PackageService packageService;
    @Autowired
	private DevManageToSystemInterface devManageToSystemInterface;
    @Autowired
	private TblProjectInfoMapper tblProjectInfoMapper;
    @Autowired
	private TblRequirementFeatureMapper tblRequirementFeatureMapper;
    @Autowired
	private TblUserInfoMapper tblUserInfoMapper;
    @Autowired
	private TblSprintInfoMapper tblSprintInfoMapper;
    @Autowired
	private TblDevTaskMapper tblDevTaskMapper;
    @Autowired
	private TblDevTaskScmGitFileMapper tblDevTaskScmGitFileMapper;
    @Autowired
	private TblDevTaskScmFileMapper tblDevTaskScmFileMapper;
    @Autowired
	private TblCommissioningWindowMapper tblCommissioningWindowMapper;
    @Autowired
    private TblSystemModuleJenkinsJobRunMapper tblSystemModuleJenkinsJobRunMapper;


    private static ExecutorService threadPool;
    {

        threadPool = Executors.newFixedThreadPool(20);

    }

	@RequestMapping(value = "callBackJenkinsLog", method = RequestMethod.POST)
	@Transactional
	public void callBackJenkins(String jsonMap) throws IOException, InterruptedException, SonarQubeException {

		try {
			Map<String, Object> backMap = JSON.parseObject(jsonMap, Map.class);
			Timestamp startTime=formateDate(backMap);
			Timestamp endTime=formateEndDate(backMap);
			String runId = backMap.get("runId").toString();
			String systemJenkinsId = backMap.get("systemJenkinsId").toString();
			String moduleRunJson = backMap.get("moduleRunJson").toString();
			String jenkinsToolId = backMap.get("jenkinsToolId").toString();
			String scheduled = null;
			if (backMap.get("scheduled") != null) {
				scheduled = backMap.get("scheduled").toString();
			}
			List<Integer> moduleRunIds = new ArrayList<>();
			if (backMap.get("moduleRunIds") != null) {
				moduleRunIds = JSON.parseObject(backMap.get("moduleRunIds").toString(),List.class);
			}
			String systemScmId = backMap.get("systemScmId").toString();
			String systemId = backMap.get("systemId").toString();
			// 首先判断是否是定时任务
			TblSystemJenkinsJobRun jorRun = iStructureService.selectBuildMessageById(Long.parseLong(runId));
			String envflag = "";
			if (jorRun.getEnvironmentType() != null) {
				envflag = String.valueOf(jorRun.getEnvironmentType());
			}
			List<TblSystemModule> moduleLog=iStructureService.selectSystemModule(Long.parseLong(systemId));
			TblSystemJenkins tblSystemJenkinsLog = iStructureService.selectSystemJenkinsById(systemJenkinsId);
			TblToolInfo jenkinsTool = iStructureService.geTblToolInfo(Integer.parseInt(jenkinsToolId));
			String nowJobNumber=backMap.get("nowJobNumber").toString();
			String jobNumberTemp=backMap.get("jobNumber").toString();
			if (scheduled != null && scheduled.equals("true")
					 && !jobNumberTemp.equals(nowJobNumber)) {
            	// 定时任务（创建历史表）
				TblSystemJenkinsJobRun ttr = new TblSystemJenkinsJobRun();
				ttr.setSystemJenkinsId(jorRun.getSystemJenkinsId());
				ttr.setSystemId(jorRun.getSystemId());
				ttr.setJobName(jorRun.getJobName());
				ttr.setRootPom(".");
				ttr.setBuildStatus(1);// 正常
				ttr.setStatus(1);
				ttr.setEndDate(endTime);
				ttr.setCreateDate(startTime);
				ttr.setStartDate(startTime);
				ttr.setLastUpdateDate(endTime);
				ttr.setEnvironmentType(jorRun.getEnvironmentType());
				ttr.setGoalsOptions("mvn clean install -Dmaven.test.skip=true");
				ttr.setCreateType(1);
				ttr.setJobType(1);
				ttr.setStartDate(startTime);
				Thread.sleep(8000);
				String jobName = jorRun.getJobName();

//				Map<String, String> result = jenkinsBuildService.getLastBuildLog(jenkinsTool, tblSystemJenkinsLog,
//						jobName);

                Map<String, String> result =
						jenkinsBuildService.getBuildLogByNumber(jenkinsTool,tblSystemJenkinsLog,jobName,Integer.parseInt(nowJobNumber),moduleLog);
				String timeStatus = result.get("status");
				String timeLog = result.get("log");
				ttr.setBuildLogs(timeLog);
				ttr.setBuildStatus(Integer.parseInt(timeStatus));
				long jobid = iStructureService.insertJenkinsJobRun(ttr);
				String moduleJson=backMap.get("moduleJson").toString();
				if (envflag.equals(Constants.PRDIN) || envflag.equals(Constants.PRDOUT)) {
					iStructureService.insertModuleRunCallBack(moduleRunIds, jobid, endTime,"2",startTime,moduleJson);
				} else {
					if(backMap.get("sonarToolId")!=null && timeStatus.equals("2")) {
					 String sonarToolId = backMap.get("sonarToolId").toString();
					 iStructureService.sonarDetail(moduleRunJson, endTime, sonarToolId, "2", jobid,startTime,moduleJson);// 2为定时任务
					}else{ //没有soanr扫描或失败时调用
						iStructureService.insertModuleRunCallBack(moduleRunIds, jobid, endTime,"2",startTime,moduleJson);
					}
				}
				callbackLog.info("定时自动构建完成");

			} else {
				TblSystemJenkinsJobRun tblSystemJenkinsJobRun = new TblSystemJenkinsJobRun();
				// 更新scm表数据
				if (scheduled != null && scheduled.equals("true")) {
					tblSystemJenkinsJobRun.setCreateDate(startTime);
				} else {

					TblSystemScm tblSystemScm = iStructureService.getTblsystemScmById(Long.parseLong(systemScmId));
					tblSystemScm.setBuildStatus(1);
					tblSystemScm.setId(Long.parseLong(systemScmId));
					iStructureService.updateSystemScmBuildStatus(tblSystemScm);// 1空闲 2构建中

				}
				String status = "";
				// 获取jenkinsTool 和jobName
				Thread.sleep(8000);
				String jobName = jorRun.getJobName();

				Map<String, String> result=new HashMap<>();
				if(backMap.get("jobNumber")!=null){
					int jobNumber=Integer.parseInt(backMap.get("jobNumber").toString());
					result=jenkinsBuildService.getBuildLogByNumber(jenkinsTool,tblSystemJenkinsLog,jobName,jobNumber,moduleLog);
				}else{
					result = jenkinsBuildService.getLastBuildLog(jenkinsTool, tblSystemJenkinsLog, jobName,moduleLog);
				}


				status = result.get("status");
				String log = result.get("log");
				log=iStructureService.detailByteBylog(log,Constants.MAX_ALLOW_PACKET);
				tblSystemJenkinsJobRun.setBuildStatus(Integer.parseInt(status));
				tblSystemJenkinsJobRun.setSystemId((long) Integer.parseInt(systemId));
				tblSystemJenkinsJobRun.setBuildLogs(log);
				tblSystemJenkinsJobRun.setStartDate(startTime);
				tblSystemJenkinsJobRun.setEndDate(endTime);
				tblSystemJenkinsJobRun.setSystemJenkinsId(Long.parseLong(systemJenkinsId));
				tblSystemJenkinsJobRun.setId(Long.parseLong(runId));
				tblSystemJenkinsJobRun.setLastUpdateDate(endTime);
				if (scheduled != null && scheduled.equals("true")) {
					tblSystemJenkinsJobRun.setCreateDate(startTime);
				} else {
					if (backMap.get("moduleList") != null && status.equals("2")) {
						List<String> moduleList = (List<String>) backMap.get("moduleList");
						updateModuleInfoFristCompile(moduleList);
					}
				}

				// 根据id更新记录
				iStructureService.updateJobRun(tblSystemJenkinsJobRun);
                String moduleJson=backMap.get("moduleJson").toString();
				if ((backMap.get("sonarflag") != null && backMap.get("sonarflag").toString().equals("1")) || (scheduled != null && scheduled.equals("true")) ) {
					if(backMap.get("sonarToolId")!=null && status.equals("2")) {

					String sonarToolId = backMap.get("sonarToolId").toString();
					iStructureService.sonarDetail(moduleRunJson, endTime, sonarToolId, "1",
							tblSystemJenkinsJobRun.getId(),startTime,moduleJson);
					}else{
						//sonar失败情况下更新module数据
						iStructureService.insertModuleRunCallBack(moduleRunIds, tblSystemJenkinsJobRun.getId(), endTime,"1",startTime,moduleJson);
					}


				} else {
					iStructureService.insertModuleRunCallBack(moduleRunIds, tblSystemJenkinsJobRun.getId(), endTime,"1",startTime,moduleJson);
				}
				if (status.equals("2")) {
					detailPackage(backMap, runId, systemId);
				}
				//发送消息
				sendMessage(tblSystemJenkinsJobRun,backMap,"1");
				callbackLog.info("自动构建完成");
			}
		} catch (Exception e) {
			this.handleException(e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}

	}



	private Timestamp getStartDate(TblToolInfo jenkinsTool, String jobName) throws Exception {
		return jenkinsBuildService.getJobStartDate(jenkinsTool, jobName);


	}

	@RequestMapping(value = "callBackManualJenkins", method = RequestMethod.POST)
	@Transactional
	public void callBackManualJenkins(String jsonMap) throws Exception {

		try {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Map<String, Object> backMap = JSON.parseObject(jsonMap, Map.class);
			long jobRunId = Long.parseLong(backMap.get("jobRunId").toString());
			TblSystemJenkinsJobRun tJobRun = iStructureService.selectBuildMessageById(jobRunId);
			String moduleJobRunId = backMap.get("moduleJobRunId").toString();
			// 更新tblsystemjenkins表数据
			Map<String, Object> sjmap = new HashMap<>();
			sjmap.put("id", backMap.get("systemJenkinsId"));
			List<TblSystemModule> moduleLog=new ArrayList<>();
			TblSystemJenkins tblSystemJenkins = iStructureService.getSystemJenkinsByMap(sjmap).get(0);
			String sonarName = "";
			if (tJobRun.getEndDate() != null) {
				// 定时任务
				// 插入历史表数据
				TblSystemJenkinsJobRun ttr = new TblSystemJenkinsJobRun();
				ttr.setSystemJenkinsId(tJobRun.getSystemJenkinsId());
				ttr.setSystemId(tJobRun.getSystemId());
				ttr.setJobName(tJobRun.getJobName());
				ttr.setRootPom(".");
				ttr.setBuildStatus(1);// 正常
				ttr.setStartDate(getBefortime());
				ttr.setStatus(1);
				ttr.setCreateDate(timestamp);
				ttr.setEndDate(timestamp);
				ttr.setLastUpdateDate(timestamp);
				ttr.setEnvironmentType(tJobRun.getEnvironmentType());
				ttr.setGoalsOptions("mvn clean install -Dmaven.test.skip=true");
				ttr.setCreateType(2);
				ttr.setJobType(1);
				Thread.sleep(8000);
				String jobName = tJobRun.getJobName();
				TblToolInfo jenkinsTool = iStructureService
						.geTblToolInfo(Long.parseLong(backMap.get("jenkinsToolId").toString()));
				//Timestamp startTime = getStartDate(jenkinsTool, jobName);
				//ttr.setStartDate(startTime);
				Map<String, String> result = jenkinsBuildService.getLastBuildLog(jenkinsTool, tblSystemJenkins,
						jobName,moduleLog);
				String timeStatus = result.get("status");
				String timeLog = result.get("log");
				timeLog=iStructureService.detailByteBylog(timeLog,Constants.MAX_ALLOW_PACKET);
				ttr.setBuildLogs(timeLog);
				ttr.setBuildStatus(Integer.parseInt(timeStatus));

				long jobid = iStructureService.insertJenkinsJobRun(ttr);
				if (backMap.get("sonarName") == null) {
				} else {
					sonarName = backMap.get("sonarName").toString();
					// 更新sonar相关信息
					String tblSystemSonar = backMap.get("tblSystemSonar").toString();
					iStructureService.sonarDetailManual(jobName, tblSystemSonar, timestamp, sonarName, moduleJobRunId,
							"2", jobid);
				}

				callbackLog.info("定时手动构建完成");
			} else {
				String scheduled = null;
				if (backMap.get("scheduled") == null) {

				} else {
					scheduled = backMap.get("scheduled").toString();
				}

				String status = "";

				tblSystemJenkins.setLastUpdateDate(timestamp);
				if (scheduled != null && scheduled.equals("true")) {

				} else {
					tblSystemJenkins.setBuildStatus(1);
				}
				Thread.sleep(8000);
				iStructureService.updateJenkins(tblSystemJenkins);
				TblToolInfo jenkinsTool = iStructureService.geTblToolInfo(tblSystemJenkins.getToolId());
				TblSystemJenkinsJobRun tblSystemJenkinsJobRun = new TblSystemJenkinsJobRun();
				String log = "";
				Timestamp startTime= null;
				Timestamp endTime= null;
				if (backMap.get("timeout") != null && backMap.get("timeout").toString().equals("true")) {
					status = backMap.get("status").toString();
					log = backMap.get("log").toString();
				} else {
					Map<String, String> result=new HashMap<>();

					if(backMap.get("jobNumber")!=null){
						int jobNumber=Integer.parseInt(backMap.get("jobNumber").toString());
						result=jenkinsBuildService.getBuildLogByNumber(jenkinsTool,tblSystemJenkins,tblSystemJenkins.getJobName(),jobNumber,moduleLog);

						 startTime=jenkinsBuildService.getJobStartDate(jenkinsTool,tblSystemJenkins,tblSystemJenkins.getJobName(),jobNumber);
						 endTime=jenkinsBuildService.getJobEndDate(jenkinsTool,tblSystemJenkins,tblSystemJenkins.getJobName(),jobNumber);

					}else {
						result = jenkinsBuildService.getLastBuildLog(jenkinsTool, tblSystemJenkins,
								tblSystemJenkins.getJobName(),moduleLog);
					}
					status = result.get("status");
					log = result.get("log");
				}
				log=iStructureService.detailByteBylog(log,Constants.MAX_ALLOW_PACKET);
				tblSystemJenkinsJobRun.setBuildStatus(Integer.parseInt(status));
				tblSystemJenkinsJobRun.setSystemId(tblSystemJenkins.getSystemId());
				tblSystemJenkinsJobRun.setBuildLogs(log);
				tblSystemJenkinsJobRun.setEndDate(endTime);
				tblSystemJenkinsJobRun.setStartDate(startTime);
				tblSystemJenkinsJobRun.setSystemJenkinsId(tblSystemJenkins.getId());
				tblSystemJenkinsJobRun.setId(jobRunId);
				tblSystemJenkinsJobRun.setLastUpdateDate(endTime);
				// 根据id更新记录
				iStructureService.updateJobRun(tblSystemJenkinsJobRun);
				if (backMap.get("sonarName") == null) {
				} else {
					sonarName = backMap.get("sonarName").toString();
					// 更新sonar相关信息
					String tblSystemSonar = backMap.get("tblSystemSonar").toString();
					iStructureService.sonarDetailManual(tblSystemJenkins.getJobName(), tblSystemSonar, timestamp,
							sonarName, moduleJobRunId, "1", tblSystemJenkinsJobRun.getId());
				}

				//发送消息
				sendMessage(tblSystemJenkinsJobRun,backMap,"1");
				callbackLog.info("手动构建完成");
			}


		} catch (Exception e) {
			this.handleException(e);
		}

	}

	@RequestMapping(value = "callBackManualDepolyJenkins", method = RequestMethod.POST)
	@Transactional
	public void callBackManualDepolyJenkins(String jsonMap) throws Exception {

		try {
			Map<String, Object> backMap = JSONObject.parseObject(jsonMap, Map.class);
			String moduleJobRunId = backMap.get("moduleJobRunId").toString();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String status = "";
			long jobRunId = Long.parseLong(backMap.get("jobRunId").toString());
			TblSystemJenkinsJobRun tJobRun = iStructureService.selectBuildMessageById(jobRunId);
			Map<String, Object> sjmap = new HashMap<>();
			sjmap.put("id", backMap.get("systemJenkinsId"));
			List<TblSystemModule> moduleLog=new ArrayList<>();
			TblSystemJenkins tblSystemJenkins = iStructureService.getSystemJenkinsByMap(sjmap).get(0);
			if (tJobRun.getEndDate() != null) {
				// 定时任务
				// 插入历史表数据
				TblSystemJenkinsJobRun ttr = new TblSystemJenkinsJobRun();
				ttr = tJobRun;
				ttr.setCreateType(2);
				ttr.setJobType(2);
				ttr.setStartDate(getBefortime());
				ttr.setStatus(1);
				ttr.setCreateDate(timestamp);
				ttr.setEndDate(timestamp);
				ttr.setLastUpdateDate(timestamp);
				ttr.setId(null);
				Thread.sleep(10000);
				String jobName = tJobRun.getJobName();
				TblToolInfo jenkinsTool = iStructureService
						.geTblToolInfo(Long.parseLong(backMap.get("jenkinsToolId").toString()));
				Timestamp startTime = getStartDate(jenkinsTool, jobName);
				ttr.setStartDate(startTime);
				Map<String, String> result = jenkinsBuildService.getLastBuildLog(jenkinsTool, tblSystemJenkins,
						jobName,moduleLog);

				String timeStatus = result.get("status");
				String timeLog = result.get("log");
				timeLog=iStructureService.detailByteBylog(timeLog,Constants.MAX_ALLOW_PACKET);
				ttr.setBuildLogs(timeLog);
				ttr.setBuildStatus(Integer.parseInt(timeStatus));
				long jobid = iStructureService.insertJenkinsJobRun(ttr);
				// 插入子任务表
				TblSystemModuleJenkinsJobRun moduleJobrun = iStructureService
						.selectByModuleRunId(Long.parseLong(moduleJobRunId));
				moduleJobrun.setLastUpdateDate(timestamp);
				moduleJobrun.setId(null);
				moduleJobrun.setSystemJenkinsJobRun(jobid);
				moduleJobrun.setCreateType(2);
				moduleJobrun.setJobType(2);
                moduleJobrun.setBuildStatus(Integer.parseInt(timeStatus));
				// moduleJobrun.getCreateBy();
				iStructureService.insertJenkinsModuleJobRun(moduleJobrun);
				callbackLog.info("定时手动部署完成");

			} else {
				// 更新tblsystemjenkins表数据
				String scheduled = null;
				if (backMap.get("scheduled") == null) {

				} else {
					scheduled = backMap.get("scheduled").toString();
				}

				tblSystemJenkins.setLastUpdateDate(timestamp);
				if (scheduled != null && scheduled.equals("true")) {

				} else {
					tblSystemJenkins.setDeployStatus(1);
				}
				Timestamp startTime= null;
				Timestamp endTime= null;
				iStructureService.updateJenkins(tblSystemJenkins);
				Thread.sleep(8000);
				TblToolInfo jenkinsTool = iStructureService.geTblToolInfo(tblSystemJenkins.getToolId());

				Map<String, String> result =new HashMap<>();
				if(backMap.get("jobNumber")!=null){
					int jobNumber=Integer.parseInt(backMap.get("jobNumber").toString());
					result=jenkinsBuildService.getBuildLogByNumber(jenkinsTool,tblSystemJenkins,tblSystemJenkins.getJobName(),jobNumber,moduleLog);
					startTime=jenkinsBuildService.getJobStartDate(jenkinsTool,tblSystemJenkins,tblSystemJenkins.getJobName(),jobNumber);
					endTime=jenkinsBuildService.getJobEndDate(jenkinsTool,tblSystemJenkins,tblSystemJenkins.getJobName(),jobNumber);
				}else{
					result = jenkinsBuildService.getLastBuildLog(jenkinsTool, tblSystemJenkins,
							tblSystemJenkins.getJobName(),moduleLog);
				}



				TblSystemJenkinsJobRun tblSystemJenkinsJobRun = new TblSystemJenkinsJobRun();
				status = result.get("status");
				String log = result.get("log");
				log=iStructureService.detailByteBylog(log,Constants.MAX_ALLOW_PACKET);
				tblSystemJenkinsJobRun.setBuildStatus(Integer.parseInt(status));
				tblSystemJenkinsJobRun.setSystemId(tblSystemJenkins.getSystemId());
				tblSystemJenkinsJobRun.setBuildLogs(log);
				tblSystemJenkinsJobRun.setStartDate(startTime);
				tblSystemJenkinsJobRun.setEndDate(endTime);
				tblSystemJenkinsJobRun.setSystemJenkinsId(tblSystemJenkins.getId());
				tblSystemJenkinsJobRun.setId(jobRunId);
				tblSystemJenkinsJobRun.setLastUpdateDate(endTime);
				// 根据id更新记录
				iStructureService.updateJobRun(tblSystemJenkinsJobRun);
				// 更新modulejobrun表
				TblSystemModuleJenkinsJobRun tmjr = new TblSystemModuleJenkinsJobRun();
				tmjr.setLastUpdateDate(timestamp);
				tmjr.setId(Long.parseLong(moduleJobRunId));
				tmjr.setBuildStatus(Integer.parseInt(status));
                //String moduleJson=backMap.get("moduleJson").toString();
				iStructureService.updateModuleJonRun(tmjr);
				// 开发任务

					if (backMap.get("reqFeatureqIds") != null && !backMap.get("reqFeatureqIds").equals("") &&  backMap.get("taskEnvType")!=null 
							&& !backMap.get("taskEnvType").equals("")) {
						String reqFeatureqIds = backMap.get("reqFeatureqIds").toString();
						String taskEnvType = backMap.get("taskEnvType").toString();
						String userId = backMap.get("userId").toString();
						String userName = backMap.get("userName").toString();
						String userAccount = backMap.get("userAccount").toString();
						Map<String, Object> sendMap = new HashMap<>();
						sendMap.put("userId", userId);
						sendMap.put("userAccount", userAccount);
						sendMap.put("userName", userName);
						String sendTask = JSON.toJSONString(sendMap);
						devTaskController.updateDeployStatus(reqFeatureqIds, taskEnvType, sendTask, Integer.parseInt(status));
						Map<String, Object> remap = new HashMap<>();
						remap.put("reqFeatureIds", reqFeatureqIds);
						remap.put("requirementFeatureFirstTestDeployTime", new Timestamp(new Date().getTime()));
						String json = JsonUtil.toJson(remap);
						devTaskService.updateReqFeatureTimeTraceForDeploy(json);

					}
				//发送消息
				sendMessage(tblSystemJenkinsJobRun,backMap,"2");
				callbackLog.info("手动部署完成");

			}
		} catch (Exception e) {
			this.handleException(e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
		}

	}

	@RequestMapping(value = "callBackAutoDepolyJenkins", method = RequestMethod.POST)
	@Transactional
	public void callBackAutoDepolyJenkins(String jsonMap) throws IOException, InterruptedException, SonarQubeException {

		try {
			//判断是否直接调用message
			boolean messageflag=true;
		    Map<String, Object> backMap = JSON.parseObject(jsonMap, Map.class);
			Timestamp startTime=formateDate(backMap);
			Timestamp endTime=formateEndDate(backMap);
			String jobNumber=backMap.get("jobNumber").toString();
			String runId = backMap.get("runId").toString();
			String systemJenkinsId = backMap.get("systemJenkinsId").toString();
			String jenkinsToolId = backMap.get("jenkinsToolId").toString();
			String envType = null;
			if (backMap.get("envType") != null) {
				envType = backMap.get("envType").toString();
			}
			List<Integer> moduleRunIds = new ArrayList<>();
			if (backMap.get("moduleRunIds") != null) {
				moduleRunIds = JSON.parseObject(backMap.get("moduleRunIds").toString(),List.class);
			}
			String moduleRunJson = backMap.get("moduleRunJson").toString();
			String systemScmId = backMap.get("systemScmId").toString();
			String reqFeatureqIds = null;
			if (backMap.get("reqFeatureqIds") != null) {
				reqFeatureqIds = backMap.get("reqFeatureqIds").toString();
			}
			String systemId = backMap.get("systemId").toString();
			List<TblSystemModule> moduleLog=iStructureService.selectSystemModule(Long.parseLong(systemId));
			String scheduled = null;
			if (backMap.get("scheduled") != null) {
				scheduled = backMap.get("scheduled").toString();
			}
			// 首先判断是否是定时任务
			TblSystemJenkinsJobRun jorRun = iStructureService.selectBuildMessageById(Long.parseLong(runId));
			// 获取日志
			TblSystemJenkins tblSystemJenkinsLog = iStructureService.selectSystemJenkinsById(systemJenkinsId);
			//自动化测试数据
			List<String> moduleTestList=new ArrayList<>();
			if(backMap.get("moduleList")!=null) {
				moduleTestList = (List<String>) backMap.get("moduleList");

			}
			//定时需要modulelist和envtype也需要
             //api 自动化测试
			List<Map<String,Object>> testConfig=deployService.getTestConfig( Long.parseLong(systemId), moduleTestList,  envType,"1");
			//ui自动化测试
			List<Map<String,Object>> uiTestConfig=deployService.getTestConfig( Long.parseLong(systemId), moduleTestList,  envType,"2");
			Map<String,Object> autoMap=new HashMap<>();
			autoMap.put("systemId",systemId);
			autoMap.put("testConfig",testConfig);
			autoMap.put("uiTestConfig",uiTestConfig);
			autoMap.put("deployType","1");//源码部署
			autoMap.put("scmId",systemScmId);
			autoMap.put("jobRunId",runId);
			autoMap.put("environnmentType",envType);
			autoMap.put("jenkinsId",systemJenkinsId);
			autoMap.put("taskMapFlag",1);//默认不修改任务状态 1 不需要 2需要
			autoMap.put("autoTest","false");
			autoMap.put("scheduled","false");
			if(backMap.get("userId")!=null) {
                autoMap.put("userId", backMap.get("userId").toString());
            }
			if(backMap.get("envName")!=null) {
                autoMap.put("envName", backMap.get("envName").toString());
            }

			TblToolInfo jenkinsTool = iStructureService.geTblToolInfo(Integer.parseInt(jenkinsToolId));
			String nowJobNumber=backMap.get("nowJobNumber").toString();
		    if (scheduled != null && scheduled.equals("true")
				&& !jobNumber.equals(nowJobNumber)) {
				// 定时任务（创建历史表）
				// Timestamp time = Timestamp.valueOf(startDate);
				TblSystemJenkinsJobRun ttr = new TblSystemJenkinsJobRun();
				ttr.setSystemJenkinsId(jorRun.getSystemJenkinsId());
				ttr.setSystemId(jorRun.getSystemId());
				ttr.setJobName(jorRun.getJobName());
				ttr.setRootPom(".");
				ttr.setBuildStatus(1);// 正常
				ttr.setStatus(1);
				ttr.setCreateDate(startTime);
				ttr.setEndDate(endTime);
				ttr.setLastUpdateDate(endTime);
				ttr.setEnvironmentType(jorRun.getEnvironmentType());
				ttr.setGoalsOptions("mvn clean install -Dmaven.test.skip=true");
				ttr.setCreateType(1);
				ttr.setJobType(2);
				Thread.sleep(8000);
				String jobName = jorRun.getJobName();

				ttr.setStartDate(startTime);
				Map<String, String> result = jenkinsBuildService.getLastBuildLog(jenkinsTool, tblSystemJenkinsLog,
						jobName,moduleLog);
				String timeStatus = result.get("status");
				String timeLog = result.get("log");
				timeLog=iStructureService.detailByteBylog(timeLog,Constants.MAX_ALLOW_PACKET);
				ttr.setBuildLogs(timeLog);
				ttr.setBuildStatus(Integer.parseInt(timeStatus));


				if((testConfig.size()>0 || uiTestConfig.size()>0) && timeStatus.equals("2")){
					String testLog=addTestTime(timeLog);
					ttr.setBuildLogs(testLog);
					autoMap.put("scheduled","true");
					autoMap.put("autoTest","true");
				}else {
					ttr.setLastUpdateDate(startTime);
					ttr.setEndDate(endTime);
				}
				long jobid = iStructureService.insertJenkinsJobRun(ttr);
				autoMap.put("jobRunId",jobid);
				callbackLog.info("定时jorunid="+jobid+"-"+ttr.getId());

				for(Integer modulejobRunId:moduleRunIds) {
					String moduleJson=backMap.get("moduleJson").toString();
					TblSystemModuleJenkinsJobRun moduleJobrun=iStructureService.selectByModuleRunId(modulejobRunId);
					if((testConfig.size()>0 || uiTestConfig.size()>0)  && timeStatus.equals("2")) {

					}else{
						moduleJobrun.setLastUpdateDate(endTime);
                        moduleJobrun.setBuildStatus(Integer.parseInt(timeStatus));
					}
					moduleJobrun.setCreateDate(startTime);
					// 插入数据
					moduleJobrun.setSystemJenkinsJobRun(jobid);
					moduleJobrun.setId(null);
					updateModuleStatus(moduleJson,4,startTime,endTime);

					long id=iStructureService.insertJenkinsModuleJobRun(moduleJobrun);
					moduleJobrun.setId(id);
					updateModuleRunInfoSchedule(moduleJson,moduleJobrun,String.valueOf(modulejobRunId));


				}


				if(testConfig.size()>0 && timeStatus.equals("2")) {
					messageflag=false;
					for (Map<String, Object> map : testConfig) {
						autoMap.put("testConfig", map);
						autoMap.put("testType","1");
						Map<String,Object> paramMap = new HashMap<String,Object>(){{
							putAll(autoMap);
						}};
						threadPool.submit(new MyRun(paramMap));
					}
				}

				if (uiTestConfig.size()>0 && timeStatus.equals("2") ){//ui自动化测试
					messageflag=false;
					for(Map<String,Object> map:uiTestConfig) {
						autoMap.put("testConfig",map);
						autoMap.put("testType","2");
						Map<String,Object> paramMap = new HashMap<String,Object>(){{
							putAll(autoMap);
						}};
						threadPool.submit(new MyRun(paramMap));
					}

				}
				callbackLog.info("定时自动部署完成");

			} else {

 				TblSystemJenkinsJobRun tblSystemJenkinsJobRun = new TblSystemJenkinsJobRun();
				String status = "";
				// 获取jenkinsTool 和jobName
				Thread.sleep(8000);
				String jobName = jorRun.getJobName();
				Map<String, String> result = jenkinsBuildService.getLastBuildLog(jenkinsTool, tblSystemJenkinsLog,
						jobName,moduleLog);
				status = result.get("status");
				String log = result.get("log");
				log=iStructureService.detailByteBylog(log,Constants.MAX_ALLOW_PACKET);
				// 更新scm表数据
				if (scheduled != null && scheduled.equals("true")) {

				} else {
					//修改是否第一次编译
					if(backMap.get("moduleList")!=null && status.equals("2")) {
						List<String> moduleList = (List<String>) backMap.get("moduleList");
						updateModuleInfoFristCompile(moduleList);
					}
					TblSystemScm tblSystemScm = iStructureService.getTblsystemScmById(Long.parseLong(systemScmId));
					tblSystemScm.setDeployStatus(1);
					tblSystemScm.setId(Long.parseLong(systemScmId));
					if ((testConfig.size()>0 || uiTestConfig.size()>0)&& status.equals("2")){

						//自动化测试中
						tblSystemScm.setDeployStatus(3);
						autoMap.put("autoTest","true");
					}else{
						//空闲
						tblSystemScm.setDeployStatus(1);
					}
					iStructureService.updateSystemScmBuildStatus(tblSystemScm);
				}


				callbackLog.info("部署开始:envtype"+envType+"scheduled"+scheduled+"reqFeatureqIds"+reqFeatureqIds+"status"+status);
				tblSystemJenkinsJobRun.setSystemId((long) Integer.parseInt(systemId));
				tblSystemJenkinsJobRun.setBuildLogs(log);
				tblSystemJenkinsJobRun.setSystemJenkinsId(Long.parseLong(systemJenkinsId));
				tblSystemJenkinsJobRun.setId(Long.parseLong(runId));
				tblSystemJenkinsJobRun.setStartDate(startTime);
				if((testConfig.size()>0 || uiTestConfig.size()>0) && status.equals("2")){ //只有部署成功才会测试
                    String moduleJson=backMap.get("moduleJson").toString();
					String testLog=addTestTime(log);
					tblSystemJenkinsJobRun.setBuildLogs(testLog);
					tblSystemJenkinsJobRun.setBuildStatus(4);//自动化测试中 只更新日志
                    //更新module状态
                    updateModuleStatus(moduleJson,4,startTime,endTime);


				}else {
                    String moduleJson=backMap.get("moduleJson").toString();
					tblSystemJenkinsJobRun.setBuildStatus(Integer.parseInt(status));
					tblSystemJenkinsJobRun.setEndDate(endTime);
					tblSystemJenkinsJobRun.setLastUpdateDate(endTime);
					iStructureService.insertModuleRunCallBack(moduleRunIds, tblSystemJenkinsJobRun.getId(), endTime,"1",startTime,moduleJson);
				}
				// 根据id更新记录
				iStructureService.updateJobRun(tblSystemJenkinsJobRun);
 			    // 任务状态修改
				if (reqFeatureqIds != null && !reqFeatureqIds.equals("") ) {
                    autoMap.put("reqFeatureqIdsEmail",reqFeatureqIds);
					Map<String, Object> sendMap = new HashMap<>();
					String userId = backMap.get("userId").toString();
					String userAccount = backMap.get("userAccount").toString();
					String userName = backMap.get("userName").toString();
					sendMap.put("userId", userId);
					sendMap.put("status", status);
					sendMap.put("envType", envType);
					sendMap.put("userAccount", userAccount);
					sendMap.put("userName", userName);
					String sendTask = JSON.toJSONString(sendMap);

					Map<String, Object> remap = new HashMap<>();
					remap.put("reqFeatureIds", reqFeatureqIds);
					remap.put("requirementFeatureFirstTestDeployTime", new Timestamp(new Date().getTime()));

					if((testConfig.size()==0 && uiTestConfig.size()==0) || status.equals("3")) {

						String json = JsonUtil.toJson(remap);
						devTaskController.updateDeployStatus(reqFeatureqIds, envType, sendTask, Integer.parseInt(status));
						devTaskService.updateReqFeatureTimeTraceForDeploy(json);
					}else{
						sendMap.put("reqFeatureIds", reqFeatureqIds);
						sendMap.put("requirementFeatureFirstTestDeployTime", new Timestamp(new Date().getTime()));
						autoMap.put("taskMapFlag",2);
						autoMap.put("taskMap",sendMap);
					}

				}

				// 更新状态
				if (status.equals(Constants.DEPLOY_SUCCESS)) {
					if (scheduled != null && scheduled.equals("true")) {
						// 定时任务不需要调用自动化运维平台接口 需要调用自动化测试接口
						autoMap.put("scheduled","true");

						if (testConfig.size()>0 ){//api自动化测试
							messageflag=false;
							autoMap.put("autoTest","true");
							for(Map<String,Object> map:testConfig) {
								autoMap.put("testConfig",map);
								autoMap.put("testType","1");
								Map<String,Object> paramMap = new HashMap<String,Object>(){{
									putAll(autoMap);
								}};
								threadPool.submit(new MyRun(paramMap));
							}

						}


						if (uiTestConfig.size()>0 ){//ui自动化测试
							messageflag=false;
							autoMap.put("autoTest","true");
							for(Map<String,Object> map:uiTestConfig) {
								autoMap.put("testConfig",map);
								autoMap.put("testType","2");
								Map<String,Object> paramMap = new HashMap<String,Object>(){{
									putAll(autoMap);
								}};
								threadPool.submit(new MyRun(paramMap));
							}

						}
					} else {

						if (envType.equals(Constants.PRDIN) || envType.equals(Constants.PRDOUT)) {
							List<Map<String,Object>> microList=new ArrayList<>();
							String systemPackageName = detailAutoPlant(microList,systemId,backMap);
							// 调用自动化运维平台接口
							String versionInfo = backMap.get("versionInfo").toString();
							String proDuctionDeploytype = backMap.get("proDuctionDeploytype").toString();
							callbackLog.info("proDuctionDeploytype"+proDuctionDeploytype);
							if(proDuctionDeploytype.equals("2")) {
								messageflag = false;
							}
							autoPlantForm(Long.parseLong(systemId), envType, reqFeatureqIds, versionInfo,
										proDuctionDeploytype,systemPackageName,microList,runId,autoMap);



						}else{
							if (testConfig.size()>0){
								messageflag=false;
								for(Map<String,Object> map:testConfig) {
									autoMap.put("testConfig",map);
									autoMap.put("testType","1");
									Map<String,Object> paramMap = new HashMap<String,Object>(){{
										putAll(autoMap);
									}};
									threadPool.submit(new MyRun(paramMap));
								}
							}


							if (uiTestConfig.size()>0 ){//ui自动化测试
								messageflag=false;
								for(Map<String,Object> map:uiTestConfig) {
									autoMap.put("testConfig",map);
									autoMap.put("testType","2");
									Map<String,Object> paramMap = new HashMap<String,Object>(){{
										putAll(autoMap);
									}};
									threadPool.submit(new MyRun(paramMap));
								}

							}

						}
					}
				}
			//修改测试任务部署时间
                if(status.equals("2")) {
                    detailEnvDate(reqFeatureqIds, envType, endTime);
                }
            //发送消息
			if(messageflag) {
				sendMessage(tblSystemJenkinsJobRun, backMap, "2");
			}
			callbackLog.info("自动部署完成");
			}
		} catch (Exception e) {
			this.handleException(e);
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

		}

	}

	 class MyRun implements Runnable{
        private Map<String,Object> data ;

        public MyRun(Map<String,Object>data){
        	this.data = data;
		}
		 @Override
		 public void run() {
			 automatTestService.toAutomatTest(this.data);
		 }
	 }

	private void updateModuleInfoFristCompile(List<String> moduleList) {


    		iStructureService.updateModuleInfoFristCompile(moduleList);


	}

    private void updateModuleStatus(String moduleJson,Integer buildStatus,Timestamp startTime,Timestamp endTime){
        Map<String,Object> moduleList = (Map<String,Object>) JSONArray.parse(moduleJson);
        for(String key:moduleList.keySet()){
            String value = moduleList.get(key).toString();
            Map<String,Object> map = (Map<String,Object>) JSONArray.parse(value);
            SimpleDateFormat time = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
            Integer modulejobRunId=Integer.parseInt(map.get("moduleRunId").toString());
            TblSystemModuleJenkinsJobRun moduleJobrun = tblSystemModuleJenkinsJobRunMapper
                    .selectById(modulejobRunId);

			if(map.get("endDate")==null){
				moduleJobrun.setLastUpdateDate(endTime);
			}else {
				moduleJobrun.setLastUpdateDate(Timestamp.valueOf(map.get("endDate").toString()));
			}
			if(map.get("startDate")==null){
				moduleJobrun.setCreateDate(startTime);
			}else{
				moduleJobrun.setCreateDate( Timestamp.valueOf(map.get("startDate").toString()));
			}

            moduleJobrun.setBuildStatus(buildStatus);
            tblSystemModuleJenkinsJobRunMapper.updateByPrimaryKeySelective(moduleJobrun);


        }

    }

	private String addTestTime(String log){
		return  log;
	}


	private void detailEnvDate(String reqFeatureqIds,String envType,Timestamp timestamp){

		deployService.detailEnvDate(reqFeatureqIds, envType, timestamp);

	}

	private String detailAutoPlant(List<Map<String,Object>> microList,String systemId,Map<String,Object> backMap){
		String systemPackageName = "";
		TblSystemInfo tblSystemInfo=iStructureService.geTblSystemInfo(Long.parseLong(systemId));
		if(tblSystemInfo.getArchitectureType().toString().equals(Constants.SERVER_MICRO_TYPE)){
			String subSystemPackageName=backMap.get("subSystemPackageName").toString();
			String subsystemCode=backMap.get("subSystemCode").toString();
			String[] subSystemPackageNameArray=subSystemPackageName.split(",");
			String[] subSystemCodeArray=subsystemCode.split(",");
			if(subSystemPackageNameArray.length==subSystemCodeArray.length) {
				for (int i = 0; i < subSystemPackageNameArray.length; i++) {
					//微服务
					Map<String,Object> micMap=new HashMap<>();
					micMap.put("subSystemCode",subSystemCodeArray[i]);
					micMap.put("subSystemPackageName",subSystemPackageNameArray[i]);
					microList.add(micMap);
				}
			}
		}else{
			 systemPackageName=backMap.get("systemPackageName").toString();

		}
		return systemPackageName;
	}

	private void autoPlantForm(Long systemId, String envType, String reqFeatureqIds, String versionInfo,
			String proDuctionDeploytype,String systemPackageName,List<Map<String,Object>> microList,String runId,Map<String,Object> autoMap) {

		if (envType != null && proDuctionDeploytype != null && versionInfo != null) {

			if (proDuctionDeploytype.equals("2")
					&& (envType.equals(Constants.PRDIN) || envType.equals(Constants.PRDOUT))) {
				Long[] featureIds = null;
				VersionInfo ver = JSON.parseObject(versionInfo, VersionInfo.class);
				if (reqFeatureqIds != null && !reqFeatureqIds.equals("")) {
					String[] req = reqFeatureqIds.split(",");
					featureIds = (Long[]) ConvertUtils.convert(req, Long.class);
				}

				List<Map<String,Object>> testConfig= (List<Map<String, Object>>) autoMap.get("testConfig");

				deployController.versionToOperation(systemId, featureIds, ver, systemPackageName, microList, runId,autoMap);



			}
		}
	}

	private Timestamp getBefortime() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		Date beforDate = new Date(now.getTime() - 300000);
		return Timestamp.valueOf(sdf.format(beforDate));

	}

	@RequestMapping(value = "callBackPackageDepolyJenkins", method = RequestMethod.POST)
	@Transactional
	public void callBackPackageDepolyJenkins(String jsonMap) throws Exception {

		try {
			//判断是否直接调用message
			boolean messageflag=true;
			Map<String, Object> backMap = JSON.parseObject(jsonMap, Map.class);
			Timestamp startTime=formateDate(backMap);
			Timestamp endTime=formateEndDate(backMap);
			String runId = backMap.get("runId").toString();
			String systemJenkinsId = backMap.get("systemJenkinsId").toString();
			String jenkinsToolId = backMap.get("jenkinsToolId").toString();
			String moduleRunList = backMap.get("moduleRunList").toString();
			String scheduled = null;
			if (backMap.get("scheduled") != null) {
				scheduled = backMap.get("scheduled").toString();
			}
			String reqFeatureqIds = null;
			if (backMap.get("reqFeatureqIds") != null) {
				reqFeatureqIds = backMap.get("reqFeatureqIds").toString();
			}
			String envType = null;
			if (backMap.get("envType") != null) {
				envType = backMap.get("envType").toString();
			}
			String systemId = backMap.get("systemId").toString();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String status = "";
			long jobRunId = Long.parseLong(runId);
			TblSystemJenkinsJobRun tJobRun = iStructureService.selectBuildMessageById(jobRunId);
			Map<String, Object> symap = new HashMap<>();
			symap.put("id", systemJenkinsId);
			TblSystemJenkins tblSystemJenkins = iStructureService.getSystemJenkinsByMap(symap).get(0);
			List<String> moduleTestList=new ArrayList<>();
			if(backMap.get("moduleList")!=null) {
				moduleTestList = (List<String>) backMap.get("moduleList");

			}
			List<TblSystemModule> moduleLog=iStructureService.selectSystemModule(Long.parseLong(systemId));
			//api 自动化测试
			List<Map<String,Object>> testConfig=deployService.getTestConfig( Long.parseLong(systemId), moduleTestList,  envType,"1");
			//ui 自动化测试
			List<Map<String,Object>> uiTestConfig=deployService.getTestConfig( Long.parseLong(systemId), moduleTestList,  envType,"2");
			Map<String,Object> autoMap=new HashMap<>();

			autoMap.put("systemId",systemId);
			autoMap.put("uiTestConfig",uiTestConfig);
			autoMap.put("testConfig",testConfig);
			autoMap.put("deployType","2");//制品部署
			autoMap.put("jenkinsId",tblSystemJenkins.getId());
			autoMap.put("jobRunId",jobRunId);
			autoMap.put("environnmentType",envType);
			autoMap.put("taskMapFlag",1);//默认不修改任务状态 1 不需要 2需要
			autoMap.put("autoTest","false");
			autoMap.put("scheduled","false");
            if(backMap.get("userId")!=null) {
                autoMap.put("userId", backMap.get("userId").toString());
            }
            if(backMap.get("envName")!=null) {
                autoMap.put("envName", backMap.get("envName").toString());
            }

				TblToolInfo jenkinsTool = iStructureService.geTblToolInfo(tblSystemJenkins.getToolId());
				Thread.sleep(8000);
				Map<String, String> result = jenkinsBuildService.getLastBuildLog(jenkinsTool, tblSystemJenkins,
						tblSystemJenkins.getJobName(),moduleLog);
				TblSystemJenkinsJobRun tblSystemJenkinsJobRun = new TblSystemJenkinsJobRun();
				status = result.get("status");
				String log = result.get("log");
 			    log=iStructureService.detailByteBylog(log,Constants.MAX_ALLOW_PACKET);
				tblSystemJenkinsJobRun.setBuildStatus(Integer.parseInt(status));
				tblSystemJenkinsJobRun.setSystemId(tblSystemJenkins.getSystemId());
				tblSystemJenkinsJobRun.setBuildLogs(log);
				tblSystemJenkinsJobRun.setLastUpdateDate(endTime);
				tblSystemJenkinsJobRun.setSystemJenkinsId(tblSystemJenkins.getId());
				tblSystemJenkinsJobRun.setId(jobRunId);
				tblSystemJenkinsJobRun.setStartDate(startTime);
				tblSystemJenkins.setLastUpdateDate(timestamp);
				if((testConfig.size()>0 || uiTestConfig.size()>0) && status.equals("2")){
					tblSystemJenkins.setDeployStatus(3);//自动化测试中
					tblSystemJenkinsJobRun.setBuildStatus(4);//自动化测试中
					autoMap.put("autoTest","true");
				}else {
					tblSystemJenkinsJobRun.setLastUpdateDate(timestamp);
					tblSystemJenkinsJobRun.setEndDate(endTime);
					tblSystemJenkins.setDeployStatus(1);
				}
				iStructureService.updateJenkins(tblSystemJenkins);
				iStructureService.updateJobRun(tblSystemJenkinsJobRun);
			    String moduleJson=backMap.get("moduleJson").toString();
				// 更新modulejobrun表
				List<TblSystemModuleJenkinsJobRun> object = (List<TblSystemModuleJenkinsJobRun>) JSONArray
						.parseArray(moduleRunList, TblSystemModuleJenkinsJobRun.class);
				for (TblSystemModuleJenkinsJobRun tmjr : object) {
					if((testConfig.size()>0  || uiTestConfig.size()>0)&& status.equals("2")){
                        tmjr.setBuildStatus(4);
					}else{

                        tmjr.setBuildStatus(Integer.parseInt(status));

					}
					tmjr.setId(tmjr.getId());
					tmjr.setCreateDate(startTime);
					tmjr.setLastUpdateDate(endTime);
					//更新module状态
					updateModuleRunInfo(moduleJson,tmjr);
					iStructureService.updateModuleJonRun(tmjr);
				}
				// 任务状态修改
				if (reqFeatureqIds != null && !reqFeatureqIds.equals("")) {
                    autoMap.put("reqFeatureqIdsEmail",reqFeatureqIds);
					Map<String, Object> sendMap = new HashMap<>();
					String userId = backMap.get("userId").toString();
					String userAccount = backMap.get("userAccount").toString();
					String userName = backMap.get("userName").toString();
					sendMap.put("userId", userId);
					sendMap.put("userAccount", userAccount);
					sendMap.put("userName", userName);
					String sendTask = JSON.toJSONString(sendMap);

					Map<String, Object> remap = new HashMap<>();
					remap.put("reqFeatureIds", reqFeatureqIds);
					remap.put("requirementFeatureFirstTestDeployTime", new Timestamp(new Date().getTime()));

					if((testConfig.size()==0  && uiTestConfig.size()==0 )|| status.equals("3")) {
						String json = JsonUtil.toJson(remap);
						devTaskController.updateDeployStatus(reqFeatureqIds, envType, sendTask, Integer.parseInt(status));
						devTaskService.updateReqFeatureTimeTraceForDeploy(json);
					}else{
					     sendMap .put("reqFeatureIds", reqFeatureqIds);
						 sendMap .put("envType", envType);
						 sendMap .put("status", status);
					     sendMap.put("requirementFeatureFirstTestDeployTime", new Timestamp(new Date().getTime()));
						 autoMap.put("taskMap",sendMap);
						 autoMap.put("taskMapFlag","2");


					}

				}

				if (status.equals(Constants.DEPLOY_SUCCESS)) {
					if (envType.equals(Constants.PRDIN) || envType.equals(Constants.PRDOUT)) {
						// 调用自动化平台
						//Map<String,Object> trdMap=new HashMap<>();
						List<Map<String,Object>> microList=new ArrayList<>();
						String systemPackageName = detailAutoPlant(microList,systemId,backMap);
						String versionInfo = backMap.get("versionInfo").toString();
						String proDuctionDeploytype = backMap.get("proDuctionDeploytype").toString();
						if(proDuctionDeploytype.equals("2")){
							messageflag=false;
						}
						autoPlantForm(Long.parseLong(systemId), envType, reqFeatureqIds, versionInfo,
								proDuctionDeploytype,systemPackageName,microList,runId,autoMap);
					}else {
                        if (testConfig.size() > 0) {
							messageflag=false;
							for(Map<String,Object> map:testConfig) {
								autoMap.put("testConfig", map);
								autoMap.put("testType","1");
								Map<String,Object> paramMap = new HashMap<String,Object>(){{
									putAll(autoMap);
								}};
								threadPool.submit(new MyRun(paramMap));
							}
                        }

						if (uiTestConfig.size()>0 ){//ui自动化测试
							messageflag=false;
							for(Map<String,Object> map:uiTestConfig) {
								autoMap.put("testConfig",map);
								autoMap.put("testType","2");
								Map<String,Object> paramMap = new HashMap<String,Object>(){{
									putAll(autoMap);
								}};
								threadPool.submit(new MyRun(paramMap));
							}

						}
                    }



				}
			//发送消息
			if(messageflag) {
				sendMessage(tblSystemJenkinsJobRun, backMap, "2");
			}
			callbackLog.info("制品部署完成");

		} catch (Exception e) {
			this.handleException(e);

		}

	}




	private  Timestamp formateDate(Map<String,Object> backMap){
		String startDate=backMap.get("startDate").toString();
		SimpleDateFormat time = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
		return Timestamp.valueOf(startDate);

	}

	private  Timestamp formateEndDate(Map<String,Object> backMap){
		String startDate=backMap.get("endDate").toString();
		SimpleDateFormat time = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
		return Timestamp.valueOf(startDate);

	}

	private void detailPackage(Map<String,Object> backMap, String runId, String systemId) throws Exception{
		TblSystemInfo tblSystemInfo = iStructureService.geTblSystemInfo(Long.parseLong(systemId));
    	TblSystemJenkinsJobRun tblSystemJenkinsJobRun=iStructureService.selectBuildMessageById(Long.parseLong(runId));
    	String artType = (String)backMap.get("artType");
    	String version = (String)backMap.get("version");
    	if(StringUtil.isNotEmpty(artType) && StringUtil.isNotEmpty(version)){
			List<String> moduleList = (List<String>) backMap.get("moduleList");
			Map<String, Object> param = new HashMap<>();
			param.put("status", 1);
			param.put("tool_type", 6);
			List<TblToolInfo> list = iStructureService.getTblToolInfo(param);
			NexusUtil nexusUtil = new NexusUtil(list.get(0));
			NexusAssetBO tagNexusAssetBO=new NexusAssetBO();
			if (moduleList == null || moduleList.size() <= 0) {//单模块
				String artifactId = tblSystemInfo.getArtifactId();
				String groupId = tblSystemInfo.getGroupId();
				String snapShotName = tblSystemInfo.getSnapshotRepositoryName();
				String releaseName = tblSystemInfo.getReleaseRepositoryName();
				tagNexusAssetBO = getNexusAssetBO(nexusUtil, artifactId, groupId, snapShotName, releaseName, artType, version);
				addArtifactData(systemId, tblSystemJenkinsJobRun, artType, tagNexusAssetBO, null);
			} else {
				for (String moduleId : moduleList) {
					String artifactId = tblSystemInfo.getArtifactId();
					String groupId = tblSystemInfo.getGroupId();
					String snapShotName = tblSystemInfo.getSnapshotRepositoryName();
					String releaseName = tblSystemInfo.getReleaseRepositoryName();
					TblSystemModule tblSystemModule = iStructureService.getTblsystemModule(Long.parseLong(moduleId));
					if (tblSystemModule != null) {
						if (StringUtil.isNotEmpty(tblSystemModule.getArtifactId())) {
							artifactId = tblSystemModule.getArtifactId();
						}
						if (StringUtil.isNotEmpty(tblSystemModule.getGroupId())) {
							groupId = tblSystemModule.getGroupId();
						}
						if (StringUtil.isNotEmpty(tblSystemModule.getSnapshotRepositoryName())) {
							snapShotName = tblSystemModule.getSnapshotRepositoryName();
						}
						if (StringUtil.isNotEmpty(tblSystemModule.getReleaseRepositoryName())) {
							releaseName = tblSystemModule.getReleaseRepositoryName();
						}
					}
					
					tagNexusAssetBO = getNexusAssetBO(nexusUtil, artifactId, groupId, snapShotName, releaseName, artType, version);
					addArtifactData(systemId, tblSystemJenkinsJobRun, artType, tagNexusAssetBO, Long.parseLong(moduleId));
				}
			}

		}
	}

	private void addArtifactData(String systemId, TblSystemJenkinsJobRun tblSystemJenkinsJobRun, String artType,
			NexusAssetBO tagNexusAssetBO, Long moduleId) {
		if (tagNexusAssetBO != null) {
			TblArtifactInfo tblArtifactInfo=new TblArtifactInfo();
			tblArtifactInfo.setSystemId(Long.parseLong(systemId));
			tblArtifactInfo.setSystemModuleId(moduleId);
			tblArtifactInfo.setRepository(tagNexusAssetBO.getRepository());
			tblArtifactInfo.setGroupId(tagNexusAssetBO.getGroup());
			tblArtifactInfo.setArtifactId(tagNexusAssetBO.getArtifactId());
			tblArtifactInfo.setVersion(tagNexusAssetBO.getVersion());
			tblArtifactInfo.setNexusPath(tagNexusAssetBO.getPath());
			tblArtifactInfo.setRemark("");
			long userId=tblSystemJenkinsJobRun.getCreateBy();
			if(tblSystemJenkinsJobRun.getCreateBy() == null){
				userId=1;
			}
			packageService.addArtifactFrature(tblArtifactInfo,String.valueOf(tblSystemJenkinsJobRun.getEnvironmentType()),userId,artType);
		}
	}

	private NexusAssetBO getNexusAssetBO(NexusUtil nexusUtil, String artifactId, String groupId,
			String snapShotName, String releaseName, String artType, String version ) throws Exception {
		NexusAssetBO tagNexusAssetBO = null;
		List<NexusAssetBO> listBo;
		NexusSearchVO nexusSearchVO = new NexusSearchVO();
		nexusSearchVO.setGroupId(groupId);
		nexusSearchVO.setArtifactId(artifactId);
		if (artType.equals("1")) {//快照
			nexusSearchVO.setRepository(snapShotName);
			nexusSearchVO.setBaseVersion(version+"-SNAPSHOT");
			listBo = nexusUtil.searchAssetList(nexusSearchVO);
			Collections.sort(listBo, new Comparator<NexusAssetBO>() {
				@Override
				public int compare(NexusAssetBO o1, NexusAssetBO o2) {
					return o2.getCreateTime().compareTo(o1.getCreateTime());
				}
			});
			
		} else {//release
			nexusSearchVO.setRepository(releaseName);
			nexusSearchVO.setBaseVersion(version);
			listBo = nexusUtil.searchAssetList(nexusSearchVO);
			
		}
		
		//取出最新一条数据
		if(listBo.size()>0){
			tagNexusAssetBO=listBo.get(0);
		}
		return tagNexusAssetBO;
	}

	public void handleException(Exception e) {
		e.printStackTrace();
		callbackLog.error(e.getMessage(), e);

	}



	private void sendMessage(TblSystemJenkinsJobRun tblSystemJenkinsJobRun,Map<String,Object> backMap ,String jobType){
		//TblSystemInfo tblSystemInfo=iStructureService.geTblSystemInfo(tblSystemJenkinsJobRun.getSystemId());
		/* 改造项目和系统关系 liushan  */
		TblSystemInfo tblSystemInfo = iStructureService.getTblSystemInfoById(tblSystemJenkinsJobRun.getSystemId());
        String messageTitle="";
        String messageContent="";
		if(tblSystemInfo.getProjectIds() != null && !tblSystemInfo.getProjectIds().equals("") && backMap.get("userId")!=null && backMap.get("envName")!=null ) {
			String creatUserId = backMap.get("userId").toString();
			String envName = backMap.get("envName").toString();
			Map<String, Object> map = new HashMap<>();
			String stauts = "";

            if(jobType.equals("1")){
                messageTitle="["+tblSystemInfo.getSystemName()+"]" + "-" +  (tblSystemJenkinsJobRun.getBuildStatus() == 2 ? "构建成功" : "构建失败");
//                if(tblSystemJenkinsJobRun.getBuildStatus() == 3){
//					messageContent = tblSystemInfo.getSystemName() + "-" + envName + "-" + "构建失败"+","+detailBuildCode(tblSystemInfo,messageContent);
//
//				}else {
//					messageContent = tblSystemInfo.getSystemName() + "-" + envName + "-" + (tblSystemJenkinsJobRun.getBuildStatus() == 2 ? "构建成功" : "构建失败") + "开始时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tblSystemJenkinsJobRun.getStartDate()) + "-结束时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tblSystemJenkinsJobRun.getEndDate());
//				}
				messageContent = tblSystemInfo.getSystemName() + "-" + envName + "-" + (tblSystemJenkinsJobRun.getBuildStatus() == 2 ? "构建成功" : "构建失败") + "开始时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tblSystemJenkinsJobRun.getStartDate()) + "-结束时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tblSystemJenkinsJobRun.getEndDate());
            }else {
                messageTitle = "[" + tblSystemInfo.getSystemName() + "]" + "-" + (tblSystemJenkinsJobRun.getBuildStatus() == 2 ? "部署成功" : "部署失败");
                messageContent = tblSystemInfo.getSystemName() + "-" + envName + "-" + (tblSystemJenkinsJobRun.getBuildStatus() == 2 ? "部署成功" : "部署失败") + "开始时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tblSystemJenkinsJobRun.getStartDate()) + "-结束时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tblSystemJenkinsJobRun.getEndDate());

            }
//
            map.put("messageContent",messageContent);
            map.put("messageTitle",messageTitle);
			map.put("messageReceiverScope", 2);
			Map<String, Object> paramMap = new HashMap<>();

			String[] projects = tblSystemInfo.getProjectIds().split(",");
			String userIds = "";
			for(String project:projects){
				paramMap.put("projectId",project);

				List<Map<String, Object>> list = tblProjectInfoMapper.getUserIdByProjectId(paramMap);
				for (Map<String, Object> userMap : list) {
					userIds = userIds + userMap.get("userId").toString() + ",";
				}


			}
			if (!userIds.equals("")) {
				userIds = userIds.substring(0, userIds.length() - 1);
			}


			//获取该系统下项目
			map.put("messageReceiver", userIds);
			map.put("createBy", creatUserId);
			//发送系统内消息
			devManageToSystemInterface.insertMessage(JSON.toJSONString(map));
			//发送邮件邮箱
			Map<String, Object> emWeMap = new HashMap<>();
            emWeMap.put("sendMethod","3");
            emWeMap.put("messageContent",messageContent);
            emWeMap.put("messageTitle",messageTitle);
			Map<String,Object> param=new HashMap<>();
			if(jobType.equals("1") && tblSystemJenkinsJobRun.getBuildStatus() == 3){//构建失败暂且不发送邮件


			}else {
			//查询此次 项目管理岗、开发管理岗、配置管理员
			param.put("roleName", "项目管理岗");
			param.put("userIds", Arrays.asList(userIds.split(",")));
			param.put("projectIds", Arrays.asList(tblSystemInfo.getProjectIds().split(",")));
			List<String> pList = tblUserInfoMapper.findRoleByUserIds(param);
			param.put("roleName", "开发管理岗");
			List<String> devList = tblUserInfoMapper.findRoleByUserIds(param);
			param.put("roleName", "配置管理");
			List<String> configList = tblUserInfoMapper.findRoleByUserIds(param);
			pList.add(creatUserId);
			pList.addAll(devList);
			pList.addAll(configList);
			HashSet h = new HashSet(pList);
			pList.clear();
			pList.addAll(h);
			emWeMap.put("messageReceiver",String.join(",",pList));
			devManageToSystemInterface.sendMessage(JSON.toJSONString(emWeMap));
			}



		}




	}
    @RequestMapping(value = "detailModuleStatus", method = RequestMethod.POST)
    @Transactional
	private void detailModuleStatus(Map<String,Object> map){

    }

	private String detailBuildCode(TblSystemInfo tblSystemInfo,String messageContent){
    	String workTaskCode="";
    	String code="";
		List<String> devIds=new ArrayList<>();
         if( tblSystemInfo.getDevelopmentMode()!=null){
         	String type=tblSystemInfo.getDevelopmentMode().toString();
         	if(type.equals("1")){//敏态
         		List<TblSprintInfo> list=tblSprintInfoMapper.findSprintBySystemIdDate(tblSystemInfo.getId());
         		if(list!=null &&list.size()>0){
					TblSprintInfo tblSprintInfo=list.get(0);
					devIds=tblDevTaskMapper.selectIdBySprintId(tblSprintInfo.getId());
				}
			}else{
				List<TblCommissioningWindow> afterTime= tblCommissioningWindowMapper.selectAfterTimeLimit();
				if(afterTime!=null && afterTime.size()>0){
					Map<String, Object> param = new HashMap<>();
					param.put("systemId", tblSystemInfo.getId());
					param.put("windowId", afterTime.get(0).getId());
					devIds=tblDevTaskMapper.selectIdByWindowId(param);

				}

			}
			 if(devIds!=null && devIds.size()>0) {
				 Map<String, Object> param = new HashMap<>();
				 param.put("devTaskIds", devIds);
				 List<Map<String, Object>> svnMaps=tblDevTaskScmFileMapper.getSvnFilesByDevTaskIds(param);
				 List<Map<String, Object>> gitMaps=tblDevTaskScmGitFileMapper.getGitFilesByDevTaskIds(param);
				 svnMaps.addAll(gitMaps);
				 for(Map<String, Object> map:svnMaps){
					 workTaskCode=workTaskCode+map.get("DEV_TASK_CODE").toString()+",";
					 code=code+map.get("COMMIT_FILE").toString()+",";
				 }
			 }

			 workTaskCode=workTaskCode.substring(0,workTaskCode.length()-1);
			 code=code.substring(0,code.length()-1);
			 messageContent=messageContent+"涉及开发工作任务如下:"+workTaskCode+","+"涉及代码文件如下:"+code;
		 }
             return messageContent;
	}


	private void updateModuleRunInfo(String moduleJson,TblSystemModuleJenkinsJobRun tblSystemModuleJenkinsJobRun){
		Map<String,Object> moduleList = (Map<String,Object>) JSONArray.parse(moduleJson);
		for(String key:moduleList.keySet()){
			String value = moduleList.get(key).toString();
			Map<String,Object> map = (Map<String,Object>) JSONArray.parse(value);
			SimpleDateFormat time = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
			Long modulejobRunId=Long.parseLong(map.get("moduleRunId").toString());

			if(String.valueOf(modulejobRunId).equals(tblSystemModuleJenkinsJobRun.getId().toString())) {
				Integer buildStatus=Integer.parseInt(map.get("buildStatus").toString());
				if(map.get("endDate")==null){

				}else {
					tblSystemModuleJenkinsJobRun.setLastUpdateDate(Timestamp.valueOf(map.get("endDate").toString()));
				}
				if(map.get("startDate")==null){

				}else{
					tblSystemModuleJenkinsJobRun.setCreateDate( Timestamp.valueOf(map.get("startDate").toString()));
				}

				tblSystemModuleJenkinsJobRun.setBuildStatus(buildStatus);
				break;
			}



		}

	}




	private void updateModuleRunInfoSchedule(String moduleJson,TblSystemModuleJenkinsJobRun tblSystemModuleJenkinsJobRun,String mId){
		Map<String,Object> moduleList = (Map<String,Object>) JSONArray.parse(moduleJson);
		for(String key:moduleList.keySet()){
			String value = moduleList.get(key).toString();
			Map<String,Object> map = (Map<String,Object>) JSONArray.parse(value);
			SimpleDateFormat time = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
			Long modulejobRunId=Long.parseLong(map.get("moduleRunId").toString());

			if(String.valueOf(modulejobRunId).equals(mId)) {
				Integer buildStatus=Integer.parseInt(map.get("buildStatus").toString());
				if(map.get("endDate")==null){

				}else {
					tblSystemModuleJenkinsJobRun.setLastUpdateDate(Timestamp.valueOf(map.get("endDate").toString()));
				}
				if(map.get("startDate")==null){

				}else{
					tblSystemModuleJenkinsJobRun.setCreateDate( Timestamp.valueOf(map.get("startDate").toString()));
				}

				tblSystemModuleJenkinsJobRun.setBuildStatus(buildStatus);
				tblSystemModuleJenkinsJobRunMapper.updateById(tblSystemModuleJenkinsJobRun);
				break;
			}



		}

	}
}