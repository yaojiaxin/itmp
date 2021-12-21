
package cn.pioneeruniverse.dev.controller;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.dev.entity.*;
import org.apache.commons.lang3.StringUtils;
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
import com.alibaba.fastjson.serializer.SerializerFeature;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.service.build.IJenkinsBuildService;
import cn.pioneeruniverse.dev.service.structure.IStructureService;


@RestController
@RequestMapping("structure")
public class StructureController {
	@Autowired
	private IStructureService istructureService;
	@Autowired
	private IJenkinsBuildService iJenkinsBuildService;
	@Autowired
	RedisUtils redisUtils;
	private final static Logger log = LoggerFactory.getLogger(StructureController.class);
	private Pattern interruptPattern = Pattern.compile("<a href='#' onclick=\"new Ajax\\.Request\\('.*?(job.+?)'\\);[\\s\\S]+?Request\\('.*?(job.+?)'\\);.+?Abort</a>");//日志中断pattern
	
	/**
	 * 自动构建
	 *
	 * @param sysId
	 * @param systemName
	 * @param modules
	 * @param env
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "creatJenkinsJob", method = RequestMethod.POST)
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> creatJenkinsJob(String sysId, String systemName, String serverType, String modules,
											   String env,String sonarflag,String version,String artType, HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			Map<String, Object> result = istructureService.creatJenkinsJob(sysId, systemName, serverType, modules, env,sonarflag,
					request);
			if (result.get("status").toString().equals(Constants.ITMP_RETURN_FAILURE)) {
				return result;
			}
			result.put("sonarflag", sonarflag);
			result.put("version",version);
			result.put("artType",artType);
			TblToolInfo nexusToolInfo = new TblToolInfo();
			Map<String, Object> param = new HashMap<>();
			param.put("status", 1);
			param.put("TOOL_TYPE", 6);
			List<TblToolInfo> toolList = istructureService.getTblToolInfo(param);
			if (toolList != null && toolList.size() > 0) {
				nexusToolInfo=toolList.get(0);
			}
			result.put("nexusToolInfo",nexusToolInfo);
			if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {// 微服务

				iJenkinsBuildService.buildMicroAutoJob(result);
			} else {
				iJenkinsBuildService.buildGeneralAutoJob(result);
			}
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
			// 新增日志
			TblSystemJenkins tblSystemJenkins = (TblSystemJenkins) result.get("tblSystemJenkins");
			map.put("toolId", tblSystemJenkins.getToolId());
			map.put("jobName", tblSystemJenkins.getJobName());
			map.put("jenkinsId", tblSystemJenkins.getId());
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			map.put("status", Constants.ITMP_RETURN_FAILURE);
			map.put("message", "构建失败,请检查配置是否正确");
			this.handleException(e);
			return map;
		}
		return map;

	}




	/**
	 * 自动构建
	 *
	 * @param sysId
	 * @param systemName
	 * @param modules
	 * @param env
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "creatJenkinsSonarJob", method = RequestMethod.POST)
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> creatJenkinsSonarJob(String sysId, String systemName, String serverType, String modules,
											   String env, HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {
			String sonarflag="1";
			Map<String, Object> result = istructureService.creatJenkinsJob(sysId, systemName, serverType, modules, env,sonarflag,
					request);
			if (result.get("status").toString().equals(Constants.ITMP_RETURN_FAILURE)) {
				return result;
			}
			result.put("sonar", "true");
			if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {// 微服务

				iJenkinsBuildService.buildMicroAutoJob(result);
			} else {
				iJenkinsBuildService.buildGeneralAutoJob(result);
			}
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
			// 新增日志
			TblSystemJenkins tblSystemJenkins = (TblSystemJenkins) result.get("tblSystemJenkins");
			map.put("toolId", tblSystemJenkins.getToolId());
			map.put("jobName", tblSystemJenkins.getJobName());
			map.put("jenkinsId", tblSystemJenkins.getId());
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			map.put("status", Constants.ITMP_RETURN_FAILURE);
			map.put("message", "扫描失败,请检查配置是否正确");
			this.handleException(e);
			return map;
		}
		return map;

	}






	/**
	 * 批量构建
	 *
	 * @param env
	 * @param data
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "creatJenkinsJobBatch", method = RequestMethod.POST)
	public Map<String, Object> creatJenkinsJobBatch(String env, String data, HttpServletRequest request) {
		Map<String, Object> result;
		try {
			result = istructureService.creatJenkinsJobBatch(env, data, request);
		} catch (Exception e) {
			this.handleException(e);
			throw e;
		}
		return result;
	}

	/**
	 * 获取构建历史详细信息
	 *
	 * @param jobRunId
	 * @param createType
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "getBuildMessageById", method = RequestMethod.POST)
	public Map<String, Object> getBuildMessageById(String jobRunId, String createType) {
		Map<String, Object> result;
		try {
			result = istructureService.getBuildMessageById(jobRunId, createType);
		} catch (Exception e) {
			this.handleException(e);
			throw e;
		}
		return result;

	}
	/**
	 * 获取sonar信息
	 * @param systemId
	 * @param startDate
	 * @param endDate
	 * @return Map<String, Object>
	 */

	@RequestMapping(value = "getSonarMessage", method = RequestMethod.POST)
	public Map<String, Object> getSonarMessage(String systemId, String startDate, String endDate) {
		Map<String, Object> result;
		try {
			result = istructureService.getSonarMessage(systemId, startDate, endDate);
		} catch (Exception e) {
			this.handleException(e);
			throw e;
		}
		return result;

	}

	/**
	 * 获取微服务sonar信息
	 *
	 * @param systemId
	 * @param startDate
	 * @param endDate
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "getSonarMessageMincro", method = RequestMethod.POST)
	public Map<String, Object> getSonarMessageMincro(String systemId, String startDate, String endDate) {

		Map<String, Object> result;
		try {
			result = istructureService.getSonarMessageMincro(systemId, startDate, endDate);
		} catch (Exception e) {
			this.handleException(e);
			throw e;
		}
		return result;

	}

	/**
	 * 手动构建
	 *
	 * @param systemJenkisId
	 * @param jsonParam
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "buildJobManual", method = RequestMethod.POST)
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> buildJobManual(HttpServletRequest request, String systemJenkisId, String jsonParam) {
		Map<String, Object> result = new HashMap<>();
		try {
			Map<String, Object> paramMap = istructureService.buildJobManual(request, systemJenkisId, jsonParam);
			TblToolInfo jenkinsToolInfo = (TblToolInfo) paramMap.get("jenkinsToolInfo");
			if (paramMap.get("status") != null && paramMap.get("status").toString().equals("2")) {
				return paramMap;

			}
			Map<String, Object> configMap = iJenkinsBuildService.buildManualJob(paramMap);
			istructureService.updateConfigInfo(configMap, paramMap);
			result.put("status", Constants.ITMP_RETURN_SUCCESS);
			// 轮询
			Boolean flag = redisUtils.exists("structCallback");
			List<Map<String, Object>> itmpMaps = new ArrayList<>();
			if (flag == true) {
				itmpMaps = (List<Map<String, Object>>) redisUtils.get("structCallback");
			}
			paramMap.put("jenkinsToolInfo", jenkinsToolInfo);
			paramMap.put("userId", CommonUtil.getCurrentUserId(request));
			paramMap.put("envName","");
			itmpMaps.add(paramMap);
			redisUtils.set("structCallback", itmpMaps);
			// 新增日志
			result.put("toolId", jenkinsToolInfo.getId());
			result.put("jobName", paramMap.get("jobName").toString());
			result.put("jenkinsId", systemJenkisId);
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			this.handleException(e);
			result.put("status", Constants.ITMP_RETURN_FAILURE);
			return result;

		}
		return result;

	}

	/**
	 * 获取手动sonar信息
	 *
	 * @param systemId
	 * @param startDate
	 * @param endDate
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "getSonarMessageManual", method = RequestMethod.POST)
	public Map<String, Object> getSonarMessageManual(String systemId, String startDate, String endDate) {
		Map<String, Object> result;
		try {
			result = istructureService.getSonarMessageManual(systemId, startDate, endDate);
		} catch (Exception e) {
			this.handleException(e);
			throw e;
		}
		return result;
	}

	/**
	 * 系统条件分页查询
	 *
	 * @param systemInfo
	 * @param pageSize
	 * @param pageNum
	 * @param scmBuildStatus
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "getAllSystemInfo", method = RequestMethod.POST)
	public Map<String, Object> getAllSystemInfo(HttpServletRequest request, String systemInfo, Integer pageSize,
												Integer pageNum, String scmBuildStatus,String refreshIds, String checkIds,String checkModuleIds) {
		Map<String, Object> result;
		Long uid = CommonUtil.getCurrentUserId(request);
		try {
			String[] refreshIdsArray=null;
			String[] checkIdsArray=null;
			String[] checkModuleArray=null;
			if(refreshIds!=null && !refreshIds.equals("")) {
				refreshIdsArray=refreshIds.split(",");
			}
			if(checkIds!=null && !checkIds.equals("")) {
				checkIdsArray=checkIds.split(",");
			}

			if(checkModuleIds!=null && !checkModuleIds.equals("")) {
				checkModuleArray=checkModuleIds.split(",");
			}

			result = istructureService.getAllSystemInfo(systemInfo, pageSize, pageNum, scmBuildStatus, uid,refreshIdsArray,checkIdsArray,checkModuleArray,request);
		} catch (Exception e) {
			this.handleException(e);
			throw e;
		}
		return result;
	}

	/**
	 * 系统条件分页查询
	 *
	 * @param ids
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "getAllSystemInfoByBuilding", method = RequestMethod.GET)
	public Map<String, Object> getAllSystemInfoByBuilding(String ids) {
		Map<String, Object> result;
		try {
			result = istructureService.getAllSystemInfoByBuilding(ids);
		} catch (Exception e) {
			this.handleException(e);
			throw e;
		}
		return result;
	}

	@RequestMapping(value = "getSystemModule", method = RequestMethod.POST)
	public Map<String, Object> getSystemModule(Integer id) {
		Map<String, Object> map = new HashMap<>();
		try {
			map = istructureService.getSystemModule(id);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
		} catch (Exception e) {
			this.handleException(e);
			map.put("status", Constants.ITMP_RETURN_FAILURE);
			return map;
		}

		return map;

	}
	/**
	 * 获取所有项目
	 * @return Map<String, Object>
	 */

	@RequestMapping(value = "getAllproject", method = RequestMethod.POST)
	public Map<String, Object> getAllproject(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		try {
			List<TblProjectInfo> list = istructureService.getAllprojectByUser(request);
			map.put("list", list);
		} catch (Exception e) {
			this.handleException(e);
		}

		return map;

	}

	/**
	 * 获取系统构建历史
	 *
	 * @param systemId
	 * @param pageNumber
	 * @param pageSize
	 * @param createType
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "getAllBuildMessage", method = RequestMethod.POST)
	public Map<String, Object> getAllBuildMessage(String systemId, Integer pageNumber, Integer pageSize,
												  String createType) {
		Map<String, Object> map = new HashMap<>();
		List<Map<String, Object>> allList = istructureService.selectMessageBySystemIdAndPage(Long.parseLong(systemId),
				1, Integer.MAX_VALUE, createType);

		List<Map<String, Object>> list = istructureService.selectMessageBySystemIdAndPage(Long.parseLong(systemId),
				pageNumber, pageSize, createType);
		// 删选出手动第一条数据
		List<String> jobNameFlag = new ArrayList<>();
		List<String> idFlag = new ArrayList<>();
		if (createType.equals(Constants.CREATE_TYPE_MANUAL)) {

			for (Map<String, Object> maps : allList) {
				String jobName = maps.get("jobName").toString();
				if (!jobNameFlag.contains(jobName)) {
					// 不包含 则是第一条数据
					jobNameFlag.add(jobName);
					idFlag.add(maps.get("id").toString());
				}

			}
		}
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		for (Map<String, Object> maps : list) {
			String id = maps.get("id").toString();
			//获取该jobrunId下子模块
           String names=istructureService.selectModulesNamesByRunId(id);
           if(names!=null ){
               maps.put("moduleNames",names);
           }else{
               maps.put("moduleNames","");
           }
           //获取创建者
            if(maps.get("userName")==null || maps.get("userName").toString().equals("")
                    || (maps.get("jobName")!=null && maps.get("jobName").toString().contains("_scheduled"))){
                maps.put("userName","系统管理员");
            }
			if (createType.equals(Constants.CREATE_TYPE_MANUAL)) {
				if (idFlag.contains(id)) {
					// 手动构建jobname第一条数据
					maps.put("manualFrist", "true");

				}
			}
			maps.put("id", id);
			if (maps.get("startDate") != null) {
				maps.put("startDate", sdf.format(maps.get("startDate")));
			}

			if (maps.get("endDate") != null) {
				maps.put("endDate", sdf.format(maps.get("endDate")));
			}

			if(maps.get("buildParameter")==null){
				maps.put("buildParameter","");
			}

		}

		map.put("rows", list);
		map.put("total", allList.size());
		return map;

	}

	/**
	 * 获取项目sonar详细问题
	 *
	 * @param toolId
	 * @param dateTime
	 * @param projectKey
	 * @param type
	 * @param p
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "getSonarIssule", method = RequestMethod.POST)
	public String getSonarIssule(String toolId, String dateTime, String projectKey, String type, String p) {
		try {
			dateTime = dateTime.replace(" ", "+");
			return istructureService.getSonarIssue(toolId, dateTime, projectKey, type, p);
		} catch (Exception e) {
			this.handleException(e);
			throw e;
		}

	}

	/**
	 * 手动构建获取构建名称
	 *
	 * @param systemId
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "getJobName", method = RequestMethod.POST)
	public Map<String, Object> getJobName(String systemId) {
		Map<String, Object> map = new HashMap<>();
		map.put("system_id", systemId);
		map.put("BUILD_STATUS", 1);
		map.put("STATUS", 1);
		map.put("JOB_TYPE", 1);
		map.put("create_type", 2);
		try {
			List<TblSystemJenkins> list = istructureService.selectJenkinsByMap(map);
			for (TblSystemJenkins tt : list) {
				tt.setRootPom(tt.getId() + "");
			}
			map.put("status", "success");
			map.put("list", list);
			return map;
		} catch (Exception e) {
			this.handleException(e);
			map.put("status", "fail");
			return map;
		}

	}

	/**
	 * 手动构建获取参数
	 *
	 * @param systemJenkisId
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "getJobNameParam", method = RequestMethod.POST)
	public Map<String, Object> getJobNameParam(String systemJenkisId) {

		Map<String, Object> map = new HashMap<>();
		map.put("id", systemJenkisId);

		try {
			List<TblSystemJenkins> list = istructureService.selectJenkinsByMap(map);
			// 获取jenkinstool
			TblToolInfo tblToolInfo = istructureService.geTblToolInfo(list.get(0).getToolId());
			String paramJson = iJenkinsBuildService.getJobParameter(tblToolInfo, list.get(0), list.get(0).getJobName());
			map.put("status", "success");
			map.put("paramJson", paramJson);
			map.put("jobName", list.get(0).getJobName());
			return map;
		} catch (Exception e) {
			map.put("status", "fail");
			this.handleException(e);
			return map;
		}

	}

	@RequestMapping(value = "getEnv", method = RequestMethod.POST)
	public Map<String, Object> getEnv() {
		Object redisEnvType = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
		Map<String, Object> envMap = JSON.parseObject(redisEnvType.toString());
		istructureService.getDeleteEnvName( "TBL_SYSTEM_SCM_ENVIRONMENT_TYPE", envMap) ;

		return envMap;

	}


	@RequestMapping(value = "getEnvBySystemId", method = RequestMethod.POST)
	public Map<String, Object> getEnvBySystemId(long systemId) {
		Map<String, Object> envMap=new HashMap<>();
		envMap=istructureService.getEnvBySystemId(systemId);
		return  envMap;

	}

	/**
	 * 获取构建定时任务
	 * @param systemId
	 * @param type
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "getCorn", method = RequestMethod.POST)
	public Map<String, Object> getCorn(String type, long systemId) {
		Map<String, Object> map = new HashMap<>();
		// type 1 为自动构建定时 2为手动构建定时
		// 获取该系统下systemjenkins表数据
		Map<String, Object> param = new HashMap<>();
		param.put("SYSTEM_ID", systemId);
		param.put("STATUS", 1);
		param.put("JOB_TYPE", 1);

		if (type.equals(Constants.CREATE_TYPE_AUTO)) {

			param.put("CREATE_TYPE", 1);

		} else {

			param.put("CREATE_TYPE", 2);
		}
		List<TblSystemJenkins> jenkinsList = istructureService.selectJenkinsByMap(param);

		List<TblSystemJenkins> jenkins = new ArrayList<>();
		for (TblSystemJenkins tsj : jenkinsList) {
			if(tsj.getJobCron()==null || tsj.getJobCron().equals("") || tsj.getJobCron().equals(" ")){
				continue;
			}
			if (type.equals(Constants.CREATE_TYPE_AUTO)) {
				int statusFlag=istructureService.getTblsystemScmById(tsj.getSystemScmId()).getStatus();
				if(statusFlag==1){
					Map<String, Object> scmParam = new HashMap<>();
					scmParam.put("id", tsj.getSystemScmId());
					Integer environmentType = istructureService.selectSystemScmByMap(scmParam).get(0).getEnvironmentType();
					tsj.setEnvironmentType(environmentType);
					Object redisEnvType = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
					Map<String, Object> envMap = JSON.parseObject(redisEnvType.toString());
					String envName = envMap.get(environmentType + "").toString();
					tsj.setEnvironmentTypeName(envName);
					tsj.setStringId(tsj.getId() + "");
					//getJobRun(tsj);

					jenkins.add(tsj);
				}

			}else {
				//tsj.setIsRun(false);
				tsj.setStringId(tsj.getId() + "");
				jenkins.add(tsj);
			}

		}
		map.put("rows", jenkins);
		map.put("total", jenkins.size());
		return map;

	}

	private void getJobRun(TblSystemJenkins tblSystemJenkins){
		if(tblSystemJenkins.getToolId()!=null){
			TblToolInfo tblToolInfo=istructureService.geTblToolInfo(tblSystemJenkins.getToolId());
			String jobName=tblSystemJenkins.getCronJobName();
			//iJenkinsBuildService.set
			tblSystemJenkins.setIsRun(false);

		}
	}

	/**
	 * 修改定时任务
	 * @param jobCron
	 * @param type
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "setCornOne", method = RequestMethod.POST)
	@Transactional
	public Map<String, Object> setCornOne(String systemJenkinsId, HttpServletRequest request, String type,
										  String jobCron) {

		Map<String, Object> map = new HashMap<>();
		// type 1 为自动构建定时 2为手动构建定时
		// 更新或获取该系统下systemjenkins表数据
		try {

			Map<String, Object> jenkinsParam = new HashMap<>();
			jenkinsParam.put("ID", systemJenkinsId);
			TblSystemJenkins tblSystemJenkins = istructureService.selectJenkinsByMap(jenkinsParam).get(0);
			if (jobCron.equals("")) {
				tblSystemJenkins.setJobCron(" ");
			} else {
				// 判断jobCron 是否正确
				TblToolInfo jenkinsTool = istructureService.geTblToolInfo(tblSystemJenkins.getToolId());
				Map<String, String> valid = iJenkinsBuildService.validateCron(jenkinsTool, jobCron);
				String status = valid.get("status").toString();
				if (!status.equals("1")) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("message", valid.get("message").toString());
					return map;
				}

				tblSystemJenkins.setJobCron(jobCron);
			}

			if (type.equals(Constants.CREATE_TYPE_AUTO)) {// 自动构建定时
				long scmId = tblSystemJenkins.getSystemScmId();

				Map<String, Object> param = new HashMap<>();
				param.put("ID", scmId);
				TblSystemScm scm = istructureService.selectSystemScmByMap(param).get(0);
				long systemId = scm.getSystemId();
				Object redisEnvType = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
				Map<String, Object> envMap = JSON.parseObject(redisEnvType.toString());
				String envName = envMap.get(scm.getEnvironmentType().toString()).toString();
				TblSystemInfo tblSystemInfo = istructureService.geTblSystemInfo(systemId);
				String serverType = tblSystemInfo.getArchitectureType() + "";
				String jobName = tblSystemInfo.getSystemCode() + "_" + scm.getEnvironmentType().toString() + "_"
						+ String.valueOf(systemId) + "_" + "scheduled";
				//判断是否toolid改变
				istructureService.judgeJenkins(tblSystemJenkins,jobName);
				tblSystemJenkins.setCronJobName(jobName);
				istructureService.updateJenkins(tblSystemJenkins);
				Map<String, Object> result = istructureService.creatJenkinsJobScheduled(scm.getSystemId() + "",
						tblSystemInfo.getSystemName(), serverType, scm.getEnvironmentType() + "", request,
						tblSystemJenkins);
				if (result.get("status")!=null && result.get("status").toString().equals(Constants.ITMP_RETURN_FAILURE)) {
					TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
					return result;
				}
				result.put("scheduled", "true");
				if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {// 微服务
					iJenkinsBuildService.buildMicroAutoJob(result);
				} else {
					iJenkinsBuildService.buildGeneralAutoJob(result);
				}
			} else {
				istructureService.updateJenkins(tblSystemJenkins);
				Map<String, Object> paramMap = istructureService.buildJobManualScheduled(request, systemJenkinsId);
				paramMap.put("scheduled", "true");
				paramMap.put("tblSystemJenkins", tblSystemJenkins);
				Map<String, Object> configMap = iJenkinsBuildService.buildManualJob(paramMap);
				istructureService.updateConfigInfo(configMap, paramMap);

			}
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
			return map;

		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			map.put("status", Constants.ITMP_RETURN_FAILURE);
			this.handleException(e);
			return map;
		}

	}

	/**
	 * 插入定时任务
	 * @param tblSystemJenkins
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "insertCorn", method = RequestMethod.POST)
	@Transactional
	public Map<String, Object> insertCorn(TblSystemJenkins tblSystemJenkins, HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		try {
			String envFlag=tblSystemJenkins.getEnvironmentType().toString();
			String jobCornFlag = tblSystemJenkins.getJobCron();
			if (jobCornFlag != null && !jobCornFlag.equals("") && !jobCornFlag.equals(" ")) {
				TblToolInfo jenkinsTool = istructureService.getEnvtool(tblSystemJenkins.getEnvironmentType() + "", "4");
				Map<String, String> valid = iJenkinsBuildService.validateCron(jenkinsTool, jobCornFlag);
				String status = valid.get("status").toString();
				if (!status.equals("1")) {
					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("message", valid.get("message").toString());
					return map;
				}

			}

			Map<String, Object> scmParam = new HashMap<>();
			scmParam.put("system_id", tblSystemJenkins.getSystemId());
			scmParam.put("ENVIRONMENT_TYPE", tblSystemJenkins.getEnvironmentType());
			scmParam.put("status", 1);

			if (istructureService.selectSystemScmByMap(scmParam).size() == 0) {
				map.put("status", Constants.ITMP_RETURN_FAILURE);
				map.put("message", tblSystemJenkins.getEnvironmentTypeName() + "请配置此环境下信息");
				return map;
			}
			TblSystemScm scm = istructureService.selectSystemScmByMap(scmParam).get(0);
			// 判断该环境下是否已经存在
			Map<String, Object> jenkinsParam = new HashMap<>();
			jenkinsParam.put("system_id", tblSystemJenkins.getSystemId());
			jenkinsParam.put("SYSTEM_SCM_ID", scm.getId());
			jenkinsParam.put("status", 1);
			jenkinsParam.put("create_type", 1);
			jenkinsParam.put("job_type", 1);
			List<TblSystemJenkins> jenkinsSize = istructureService.selectJenkinsByMap(jenkinsParam);
			long systemId = scm.getSystemId();
			TblSystemInfo tblSystemInfo = istructureService.geTblSystemInfo(systemId);
			if (jenkinsSize.size() > 0) {
				if(jenkinsSize.get(0).getJobCron()==null || jenkinsSize.get(0).getJobCron().equals("") || jenkinsSize.get(0).getJobCron().equals(" ")){
					tblSystemJenkins=jenkinsSize.get(0);
					if(tblSystemJenkins.getBuildStatus()==null || tblSystemJenkins.getBuildStatus().equals("")) {
						tblSystemJenkins.setBuildStatus(1);
					}
					tblSystemJenkins.setJobCron(jobCornFlag);
//					tblSystemJenkins.setJobType(1);
//					tblSystemJenkins.setCreateType(1);
					if(tblSystemJenkins.getCronJobName()==null || tblSystemJenkins.getCronJobName().equals("")){
						String cronJobName = tblSystemInfo.getSystemCode() + "_" + envFlag + "_"
								+ tblSystemJenkins.getSystemId() + "_" + "deployScheduled";
						tblSystemJenkins.setCronJobName(cronJobName);
					}

					if(tblSystemJenkins.getToolId()==null ){
						TblToolInfo toolType = istructureService.getEnvtool(envFlag + "", "4");
						tblSystemJenkins.setToolId(toolType.getId());
					}

					istructureService.updateJenkins(tblSystemJenkins);
				}else {


					map.put("status", Constants.ITMP_RETURN_FAILURE);
					map.put("message", tblSystemJenkins.getEnvironmentTypeName() + "已存在请不要重复添加");
					return map;
				}
			}else {
				tblSystemJenkins.setSystemScmId(scm.getId());
				tblSystemJenkins.setStatus(1);
				tblSystemJenkins.setBuildStatus(1);
				tblSystemJenkins.setJobType(1);
				tblSystemJenkins.setCreateType(1);

				String cronJobName = tblSystemInfo.getSystemCode() + "_" + tblSystemJenkins.getEnvironmentType() + "_"
						+ tblSystemJenkins.getSystemId() + "_" + "scheduled";

				tblSystemJenkins.setCronJobName(cronJobName);
				TblToolInfo toolType = istructureService.getEnvtool(tblSystemJenkins.getEnvironmentType() + "", "4");
				tblSystemJenkins.setToolId(toolType.getId());
				istructureService.insertSystemJenkins(tblSystemJenkins);
			}

			String serverType = tblSystemInfo.getArchitectureType() + "";
			Map<String, Object> result = istructureService.creatJenkinsJobScheduled(scm.getSystemId() + "",
					tblSystemInfo.getSystemName(), serverType, scm.getEnvironmentType() + "", request,
					tblSystemJenkins);
			if (result.get("status").toString().equals(Constants.ITMP_RETURN_FAILURE)) {
				TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
				return result;
			}
			result.put("scheduled", "true");
			if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {// 微服务
				iJenkinsBuildService.buildMicroAutoJob(result);
			} else {
				iJenkinsBuildService.buildGeneralAutoJob(result);
			}
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
			map.put("message", "处理成功");
			return map;
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			map.put("status", Constants.ITMP_RETURN_FAILURE);
			map.put("message", "处理失败");
			this.handleException(e);
			return map;
		}

	}

	/**
	 * 获取强制结束列表
	 * @param createType
	 * @param systemId
	 * @param jobType
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "getBreakName", method = RequestMethod.POST)
	public Map<String, Object> getBreakName(String createType, long systemId, String jobType) {
		Map<String, Object> map = new HashMap<>();
		try {
			List<Map<String, Object>> list = istructureService.getBreakName(createType, systemId, jobType);
			map.put("rows", list);
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
			map.put("total", list.size());
			return map;
		} catch (Exception e) {

			this.handleException(e);
			map.put("status", Constants.ITMP_RETURN_FAILURE);
			map.put("message", "处理失败");
			return map;
		}
	}


	/**
	 * 获取强制结束列表
	 * @param createType
	 * @param systemId
	 * @param jobType
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "getLogParam", method = RequestMethod.POST)
	public Map<String, Object> getLogParam(String createType, Long systemId, String jobType,String ManualjobName,String envName,Integer flag) {
		Map<String, Object> map = new HashMap<>();
		try {
			//通过env获取jobname
			String toolId="";
			String jenkinsId="";
			String jobName="";
			if(ManualjobName!=null && !ManualjobName.equals("")) {
				jobName=ManualjobName;
			}else {
				jobName=	istructureService.getJobNameByEnv(envName,systemId,createType,jobType,flag);
			}
			List<Map<String, Object>> list = istructureService.getBreakName(createType, systemId, jobType);
			for(Map<String, Object> m:list) {
				if(m.get("jobName").toString().equals(jobName)) {
					toolId=m.get("toolId").toString();
					jenkinsId=m.get("jenkinsId").toString();
					break;
				}
			}
			map.put("jobName",jobName );
			map.put("toolId",toolId );
			map.put("jenkinsId", jenkinsId);


			return map;
		} catch (Exception e) {

			this.handleException(e);
			map.put("status", Constants.ITMP_RETURN_FAILURE);
			map.put("message", "处理失败");
			return map;
		}
	}




//	/**
//	 * 实时日志接口
//	 * @param toolId
//	 * @param jobName
//	 * @param jenkinsId
//	 * @param count
//	 * @return Map<String, Object>
//	 */
//	@RequestMapping(value = "getLogTest", method = RequestMethod.POST)
//	public Map<String, Object> getLogTest( String jobName, int count,String createType, long systemId, String jobType) {
//		Long toolId=null;
//		String jenkinsId="";
//		List<Map<String, Object>> list = istructureService.getBreakName(createType, systemId, jobType);
//		if(list!=null && list.size()>0) {
//			for(Map<String, Object> map:list) {
//				if(jobName.equals(map.get("jobName"))) {
//					toolId=(long) map.get("toolId");
//					jenkinsId= map.get("jenkinsId").toString();
//					break;
//				}
//			}
//		}
//		Map<String, Object> map = new HashMap<>();
//		try {
//			TblToolInfo tblToolInfo = istructureService.geTblToolInfo(toolId);
//			// 判断是否结束
//			Boolean flag = iJenkinsBuildService.isJenkinsBuilding(tblToolInfo, jobName);
//			// 正在构建
//			if (flag == false) {
//				map.put("building", "false");
//			} else {
//				map.put("building", "true");
//			}
//			TblSystemJenkins tblSystemJenkins = istructureService.selectSystemJenkinsById(jenkinsId);
//			//String log = iJenkinsBuildService.getJenkinsBuildingLog(tblToolInfo, tblSystemJenkins, jobName);
//			map.put("status", Constants.ITMP_RETURN_SUCCESS);
//			map.put("log", log);
//			return map;
//		} catch (Exception e) {
//			this.handleException(e);
//			map.put("status", Constants.ITMP_RETURN_FAILURE);
//			map.put("building", "false");
//			map.put("log", "接口出现问题");
//			map.put("message", "处理失败");
//			return map;
//		}
//	}
//






	/**
	 * 强制结束接口
	 * @param toolId
	 * @param jobName
	 * @param jenkinsId
	 * @param createType
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "breakJob", method = RequestMethod.POST)
	@Transactional
	public Map<String, Object> breakJob(long toolId, String jobName, String jenkinsId, String createType) throws Exception {
		Map<String, Object> map = new HashMap<>();
		try {

			TblToolInfo tblToolInfo = istructureService.geTblToolInfo(toolId);
			TblSystemJenkins tblSystemJenkins = istructureService.selectSystemJenkinsById(jenkinsId);
			//获取更新时间
			Timestamp startTime=tblSystemJenkins.getLastUpdateDate();
			Timestamp nowTime= new Timestamp(System.currentTimeMillis());
			//判断时间差
//			int sec=(int) (nowTime.getTime()-startTime.getTime())/1000;
//
//			if(sec<30) {
//				map.put("status", Constants.ITMP_RETURN_FAILURE);
//				map.put("message", "不可马上强制结束请在项目运行一分钟后重试!");
//				return map;


//			}

//			Thread.sleep(6000);

			String jobType = String.valueOf(tblSystemJenkins.getJobType());

			if (createType.equals(Constants.CREATE_TYPE_MANUAL)) {

				List<Map<String, Object>> itmpMaps = new ArrayList<>();
				// 手动
				if (jobType.equals(Constants.JOB_TYPE_BUILD)) {// 构建
					itmpMaps = (List<Map<String, Object>>) redisUtils.get("structCallback");

				} else {// 部署
					itmpMaps = (List<Map<String, Object>>) redisUtils.get("deployCallback");
				}

				if (itmpMaps != null && !itmpMaps.isEmpty()) {
					Iterator<Map<String, Object>> it = itmpMaps.iterator();
					while (it.hasNext()) {
						Map<String, Object> mapFlag = it.next();
						String jobNameFlag = mapFlag.get("jobName").toString();
						if (jobName.equals(jobNameFlag)) {
							List<Map<String, Object>> refreshMapsList = new ArrayList<>();
							refreshMapsList.addAll(itmpMaps);
							refreshMapsList.remove(mapFlag);
							if (jobType.equals("1")) {
								redisUtils.set("structCallback", refreshMapsList);
							} else {
								redisUtils.set("deployCallback", refreshMapsList);
							}

						}

					}
				}
				iJenkinsBuildService.stopBuilding(tblToolInfo,tblSystemJenkins, jobName);//为了防止轮询已经开始跑将此暂停放置为后
				istructureService.detailErrorStructure(tblSystemJenkins, jobType, "2");

			} else {
				// 自动
				TblSystemJenkinsJobRun jobRun = new TblSystemJenkinsJobRun();
				jobRun.setSystemJenkinsId(Long.parseLong(jenkinsId));
				jobRun.setSystemId(tblSystemJenkins.getSystemId());
				jobRun.setJobName(jobName);
				Integer lastJobNumber = istructureService.getJobRunLastJobNumber(jobRun);
				Boolean flag = iJenkinsBuildService.isJenkinsBuilding(tblToolInfo,tblSystemJenkins, jobName, lastJobNumber);
				if (flag == true) {
					iJenkinsBuildService.stopBuilding(tblToolInfo,tblSystemJenkins, jobName);
				} else {
					// 回调异常
					Map<String, Object> paramMap = new HashMap<>();
					paramMap.put("id", tblSystemJenkins.getSystemScmId());
					if(tblSystemJenkins.getSystemScmId()==null && tblSystemJenkins.getEnvironmentType()!=null) {
						//制品部署
						istructureService.detailArtAutoErrorStructure(tblSystemJenkins, "2");
					}else {
						List<TblSystemScm> scms = istructureService.selectScmByMap(paramMap);

						istructureService.detailAutoErrorStructure(scms.get(0), jobType, "2",
								tblSystemJenkins.getId() + "");
					}
				}
			}
			map.put("status", Constants.ITMP_RETURN_SUCCESS);
			return map;
		} catch (Exception e) {
			TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
			this.handleException(e);
			map.put("status", Constants.ITMP_RETURN_FAILURE);
			map.put("message", "处理失败");
			return map;

		}
	}

	public static long fromDateStringToLong(String inVal) {
		Date date = null;
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss:SSS");
		try {
			date = inputFormat.parse(inVal);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (date != null) {
			return date.getTime();
		}else{
			return 0;
		}

	}

	/**
	 * 实时日志接口
	 * @param toolId
	 * @param jobName
	 * @param jenkinsId
	 * @param count
	 * @return Map<String, Object>
	 */
	@RequestMapping(value = "getLog", method = RequestMethod.POST)
	public Map<String, Object> getLog(Long toolId, String jobName, String jenkinsId, Integer count,Integer line) {
		Map<String, Object> map = new HashMap<>();
		try {
			
			Map stageViewMap = getStageViewMap(null, toolId, jobName, jenkinsId);
			if (stageViewMap != null) {
				TblSystemJenkins tblSystemJenkins = (TblSystemJenkins)stageViewMap.get("tblSystemJenkins");
				TblToolInfo tblToolInfo = (TblToolInfo)stageViewMap.get("tblToolInfo");
				Integer lastJobNumber = (Integer)stageViewMap.get("lastJobNumber");
				if (lastJobNumber != null) {
					Boolean building = iJenkinsBuildService.isJenkinsBuilding(tblToolInfo,tblSystemJenkins, jobName, lastJobNumber);
					map.put("building", building);
					Map<String, String> resultMap = iJenkinsBuildService.getJenkinsBuildingLog(tblToolInfo, tblSystemJenkins, jobName,String.valueOf(line), lastJobNumber);
					map.put("line",resultMap.get("start"));
					map.put("status", Constants.ITMP_RETURN_SUCCESS);
					map.put("log", detailContinueLog(resultMap.get("log"),tblToolInfo.getId()));
					if (!"1".equals(resultMap.get("resultFlag"))) {//刚开始任务延时，返回正在执行中。
						map.put("building", true);
					}
				}
			}
			return map;
		} catch (Exception e) {
			this.handleException(e);
			map.put("status", Constants.ITMP_RETURN_FAILURE);
			map.put("building", "false");
			map.put("log", "接口出现问题");
			map.put("message", "处理失败");
			return map;
		}
	}
	
	
	/**
	 * 获取Jenkins的StageView列表内容
	 * @param request
	 * @param toolId
	 * @param jobName
	 * @param jenkinsId
	 * @return
	 */
	@RequestMapping(value = "getStageView", method = RequestMethod.POST)
	public Map<String, Object> getStageView(HttpServletRequest request, Long toolId, String jobName, String jenkinsId) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Map stageViewMap = getStageViewMap(request, toolId, jobName, jenkinsId);
			if (stageViewMap != null) {
				TblSystemJenkins tblSystemJenkins = (TblSystemJenkins)stageViewMap.get("tblSystemJenkins");
				TblToolInfo tblToolInfo = (TblToolInfo)stageViewMap.get("tblToolInfo");
				Integer lastJobNumber = (Integer)stageViewMap.get("lastJobNumber");
				if (lastJobNumber != null) {
					String json = iJenkinsBuildService.getStageViewDescribeJson(tblToolInfo, tblSystemJenkins, jobName, lastJobNumber);
					JSONObject obj = JSON.parseObject(json);
					map.put("stageView", obj);
				}
			}
		} catch (Exception e) {
			this.handleException(e);
			map.put("status", Constants.ITMP_RETURN_FAILURE);
			map.put("building", "false");
			map.put("log", "接口出现问题");
			map.put("message", "处理失败");
		}
		return map;
	}
	
	/**
	 * 获取Jenkins的中断内容
	 * @param request
	 * @param toolId
	 * @param jobName
	 * @param jenkinsId
	 * @param interruptId
	 * @param flag
	 * @return
	 */
	@RequestMapping(value = "getStageViewNextPending", method = RequestMethod.POST)
	public Map<String, Object> getStageViewNextPending(HttpServletRequest request, Long toolId, String jobName, String jenkinsId, String interruptId, Integer flag) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Map stageViewMap = getStageViewMap(request, toolId, jobName, jenkinsId);
			if (stageViewMap != null) {
				TblSystemJenkins tblSystemJenkins = (TblSystemJenkins)stageViewMap.get("tblSystemJenkins");
				TblToolInfo tblToolInfo = (TblToolInfo)stageViewMap.get("tblToolInfo");
				Integer lastJobNumber = (Integer)stageViewMap.get("lastJobNumber");
				if (lastJobNumber != null) {
					iJenkinsBuildService.getStageViewNextPending(tblToolInfo, tblSystemJenkins, jobName, lastJobNumber, interruptId, flag);
				}
			}
		} catch (Exception e) {
			this.handleException(e);
			map.put("status", Constants.ITMP_RETURN_FAILURE);
			map.put("building", "false");
			map.put("log", "接口出现问题");
			map.put("message", "处理失败");
		}
		return map;
	}
	
	/**
	 * 执行中断继续或者停止
	 * @param request
	 * @param toolId
	 * @param jobName
	 * @param jenkinsId
	 * @return
	 */
	@RequestMapping(value = "getNextPendingInputAction", method = RequestMethod.POST)
	public Map<String, Object> getNextPendingInputAction(HttpServletRequest request, Long toolId, String jobName, String jenkinsId) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Map stageViewMap = getStageViewMap(request, toolId, jobName, jenkinsId);
			if (stageViewMap != null) {
				TblSystemJenkins tblSystemJenkins = (TblSystemJenkins)stageViewMap.get("tblSystemJenkins");
				TblToolInfo tblToolInfo = (TblToolInfo)stageViewMap.get("tblToolInfo");
				Integer lastJobNumber = (Integer)stageViewMap.get("lastJobNumber");
				if (lastJobNumber != null) {
					String json = iJenkinsBuildService.getNextPendingInputAction(tblToolInfo, tblSystemJenkins, jobName, lastJobNumber);
					JSONObject obj = JSON.parseObject(json);
					map.put("nextPendingInput", obj);
				}
			}
		} catch (Exception e) {
			this.handleException(e);
			map.put("status", Constants.ITMP_RETURN_FAILURE);
			map.put("building", "false");
			map.put("log", "接口出现问题");
			map.put("message", "处理失败");
		}
		return map;
	}
	
	/**
	 * 获取StageView列表子页execution内容
	 * @param request
	 * @param toolId
	 * @param jobName
	 * @param jenkinsId
	 * @param describeId
	 * @return
	 */
	@RequestMapping(value = "getStageViewLog", method = RequestMethod.POST)
	public Map<String, Object> getStageViewLog(HttpServletRequest request, Long toolId, String jobName, String jenkinsId, Integer describeId) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Map stageViewMap = getStageViewMap(request, toolId, jobName, jenkinsId);
			if (stageViewMap != null) {
				TblSystemJenkins tblSystemJenkins = (TblSystemJenkins)stageViewMap.get("tblSystemJenkins");
				TblToolInfo tblToolInfo = (TblToolInfo)stageViewMap.get("tblToolInfo");
				Integer lastJobNumber = (Integer)stageViewMap.get("lastJobNumber");
				if (lastJobNumber != null) {
					String json = iJenkinsBuildService.getStageViewDescribeExecutionJson(tblToolInfo, tblSystemJenkins, jobName, lastJobNumber, describeId);
					JSONObject obj = JSON.parseObject(json);
					map.put("stageView", obj);
				}
			}
		} catch (Exception e) {
			this.handleException(e);
			map.put("status", Constants.ITMP_RETURN_FAILURE);
			map.put("building", "false");
			map.put("log", "接口出现问题");
			map.put("message", "处理失败");
		}
		return map;
	}
	
	/**
	 * 获取StageView列表子页execution的详细日志内容
	 * @param request
	 * @param toolId
	 * @param jobName
	 * @param jenkinsId
	 * @param executionId
	 * @return
	 */
	@RequestMapping(value = "getStageViewLogDetail", method = RequestMethod.POST)
	public Map<String, Object> getStageViewLogDetail(HttpServletRequest request, Long toolId, String jobName, String jenkinsId, Integer executionId) {
		Map<String, Object> map = new HashMap<>();
		map.put("status", Constants.ITMP_RETURN_SUCCESS);
		try {
			Map stageViewMap = getStageViewMap(request, toolId, jobName, jenkinsId);
			if (stageViewMap != null) {
				TblSystemJenkins tblSystemJenkins = (TblSystemJenkins)stageViewMap.get("tblSystemJenkins");
				TblToolInfo tblToolInfo = (TblToolInfo)stageViewMap.get("tblToolInfo");
				Integer lastJobNumber = (Integer)stageViewMap.get("lastJobNumber");
				if (lastJobNumber != null) {
					String json = iJenkinsBuildService.getStageViewExecutionLogJson(tblToolInfo, tblSystemJenkins, jobName, lastJobNumber, executionId);
					JSONObject obj = JSON.parseObject(json);
					String text = obj.getString("text");
					obj.put("text", detailContinueLog(text, tblToolInfo.getId()));
					map.put("stageView", obj);
				}
			}
		} catch (Exception e) {
			this.handleException(e);
			map.put("status", Constants.ITMP_RETURN_FAILURE);
			map.put("building", "false");
			map.put("log", "接口出现问题");
			map.put("message", "处理失败");
		}
		return map;
	}
	

	private Map<String, Object> getStageViewMap(HttpServletRequest request, Long toolId, String jobName, String jenkinsId) {
		String stageViewName = toolId + "_" + jenkinsId + "_" + jobName;
		Map<String, Object> stageViewMap = (Map<String, Object>)redisUtils.get(stageViewName);
		TblSystemJenkins tblSystemJenkins = null;
		TblToolInfo tblToolInfo = null;
		if (stageViewMap == null) {
			//修改全局缺省配置:fastjson序列化@Transient的字段比如password
			JSON.DEFAULT_GENERATE_FEATURE = SerializerFeature.config(JSON.DEFAULT_GENERATE_FEATURE,SerializerFeature.SkipTransientField, false);
			tblSystemJenkins = istructureService.selectSystemJenkinsById(jenkinsId);
			tblToolInfo = istructureService.geTblToolInfo(toolId);
			stageViewMap = new HashMap<String, Object>();
			stageViewMap.put("tblSystemJenkins", JSON.toJSONString(tblSystemJenkins));
			stageViewMap.put("tblToolInfo", JSON.toJSONString(tblToolInfo));
			redisUtils.set(stageViewName, stageViewMap);
		} else {
			tblSystemJenkins = JSON.parseObject(stageViewMap.get("tblSystemJenkins").toString(), TblSystemJenkins.class);
			tblToolInfo = JSON.parseObject(stageViewMap.get("tblToolInfo").toString(), TblToolInfo.class);
		}
		
		TblSystemJenkinsJobRun jobRun = new TblSystemJenkinsJobRun();
		jobRun.setSystemJenkinsId(Long.parseLong(jenkinsId));
		jobRun.setSystemId(tblSystemJenkins.getSystemId());
		jobRun.setJobName(jobName);
		Integer lastJobNumber = istructureService.getJobRunLastJobNumber(jobRun);
		
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("tblSystemJenkins", tblSystemJenkins);
		returnMap.put("tblToolInfo", tblToolInfo);
		returnMap.put("lastJobNumber", lastJobNumber);
		return returnMap;
	}


	@RequestMapping(value = "getTestLog", method = RequestMethod.POST)
	public Map<String, Object> gettestLog(String createType, long systemId, String jobType,String ManualjobName,String envName,int flag) {
		Map<String, Object> map = new HashMap<>();

			//通过env获取jobname
			String toolId = "";
			String jenkinsId = "";
			String jobName = "";
			String log = "";

			jobName = istructureService.getJobNameByEnv(envName, systemId, createType, jobType, flag);
		   List<TblSystemModule> moduleLog=istructureService.selectSystemModule(systemId);
			List<Map<String, Object>> list = istructureService.getBreakName(createType, systemId, jobType);
			for (Map<String, Object> m : list) {
				if (m.get("jobName").toString().equals(jobName)) {
					toolId = m.get("toolId").toString();
					jenkinsId = m.get("jenkinsId").toString();
					break;
				}
			}
			TblToolInfo tblToolInfo = istructureService.geTblToolInfo(Long.parseLong(toolId));
			TblSystemJenkins tblSystemJenkins = istructureService.selectSystemJenkinsById(jenkinsId);
			try {
				log = iJenkinsBuildService.getLastBuildLog(tblToolInfo, tblSystemJenkins, jobName,moduleLog).get("log");
				log=log+"\n自动化测试中......";
			} catch (Exception e) {
				e.printStackTrace();
			}

			map.put("log", log);
			return map;


	}


/**
 * <a href='#' onclick="new Ajax.Request('/job/wms_git_20190806_2_176_packagedeploy/3/input/Interrupt1/proceedEmpty'); return false">Continue</a> or 
 * <a href='#' onclick="new Ajax.Request('/job/wms_git_20190806_2_176_packagedeploy/3/input/Interrupt1/abort'); return false">Abort</a>
 * @param log
 * @param id
 * @return
 */
	public String detailContinueLog(String log,long id){
		String teslog=log.trim();
		if (teslog.indexOf("model-link") != -1) {//日志页面链接去掉:初始用户、节点
			log = log.replaceAll("<a.+?model-link.+?>(.+?)</a>", "$1");
		}
		if(teslog.indexOf("Ajax.Request") != -1) {
//			Pattern interruptPattern = Pattern.compile("<a href='#' onclick=\"new Ajax\\.Request\\('.*?(job.+?)'\\);[\\s\\S]+?Request\\('.*?(job.+?)'\\);.+?Abort</a>");//日志中断pattern
			Matcher m = interruptPattern.matcher(log);
			if (m.find()) {
				log = m.replaceAll("<a href='#' onclick=goContinueLog('$1','"+id+"')>点击继续部署</a>&nbsp;&nbsp;<a href='#' onclick=goContinueLog('$2','"+id+"')>停止</a>");
			}
		}
		return log;
	}

	private void subLogString(String log,int line, Map<String,Object> map) {

		String[] logArray=StringUtils.split(log,"\n");
		StringBuffer resultLog=new StringBuffer();
		for(int i=0;i<logArray.length;i++){
			if(line<=i){
				resultLog=resultLog.append(logArray[i]+"\n");
			}
		}
//		StringBuffer resultLog=new StringBuffer();
//		ArrayList<String> list=new ArrayList<>();
//		StringTokenizer st=new StringTokenizer(log,"\n");
//		while (st.hasMoreTokens()){
//			list.add(st.nextToken());
//		}
//        for(int i=0;i<list.size();i++){
//        	if(line<=i){
//				resultLog=resultLog.append(list.get(i)+"\n");
//			}
//		}




		map.put("log",resultLog);
		map.put("line",logArray.length);
	//	map.put("line",list.size());
	}


	public void handleException(Exception e) {
		e.printStackTrace();
		log.error(e.getMessage(), e);

	}


}