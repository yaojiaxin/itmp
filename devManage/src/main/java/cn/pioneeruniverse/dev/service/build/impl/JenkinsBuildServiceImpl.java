package cn.pioneeruniverse.dev.service.build.impl;

import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.StringUtil;
import com.offbytwo.jenkins.model.JobWithDetails;

import cn.pioneeruniverse.common.jenkins.JenkinsUtil;
import cn.pioneeruniverse.common.nexus.NexusUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.entity.FtpS3Vo;
import cn.pioneeruniverse.dev.entity.TblArtifactInfo;
import cn.pioneeruniverse.dev.entity.TblServerInfo;
import cn.pioneeruniverse.dev.entity.TblSystemDbConfig;
import cn.pioneeruniverse.dev.entity.TblSystemDeploy;
import cn.pioneeruniverse.dev.entity.TblSystemDeployScript;
import cn.pioneeruniverse.dev.entity.TblSystemInfo;
import cn.pioneeruniverse.dev.entity.TblSystemJenkins;
import cn.pioneeruniverse.dev.entity.TblSystemModule;
import cn.pioneeruniverse.dev.entity.TblSystemModuleJenkinsJobRun;
import cn.pioneeruniverse.dev.entity.TblSystemModuleScm;
import cn.pioneeruniverse.dev.entity.TblSystemScm;
import cn.pioneeruniverse.dev.entity.TblSystemSonar;
import cn.pioneeruniverse.dev.entity.TblToolInfo;
import cn.pioneeruniverse.dev.service.build.IJenkinsBuildService;

@Service("iJenkinsBuildService")
public class JenkinsBuildServiceImpl extends JenkinsBase implements IJenkinsBuildService {
	private final static Logger log = LoggerFactory.getLogger(JenkinsBuildServiceImpl.class);
	@Value("${jenkins.callback.url}")
	private String jenkinsCallbackUrl;
	
	@Autowired
	RedisUtils redisUtils;
	@Autowired
	JenkinsDeployExcept jenkinsDeployExcept;
	
	@Value("${s3.ftpBucket}")
	private String ftpBucket;
	
	private String defaultMavenBuild = "mvn clean package";
	private String defaultMavenBuildMust = "mvn clean install";
	private String parentMavenBuild = "mvn clean install -N -Dmaven.test.skip=true";
	private String deployNexusReleasesId = "releases";
	private String deployNexusSnapshotsId = "snapshots";
	private String rootNodeKey = "rootNodeKey";
	
	private Pattern sonarNamePattern = Pattern.compile("withSonarQubeEnv\\((.+?)\\)");
	private Pattern sonarProjectKeyPattern = Pattern.compile("sonar\\.projectKey\\s*?=\\s*?(\\S+?)\\s+?");  
	private Pattern sonarProjectNamePattern = Pattern.compile("sonar\\.projectName\\s*?=\\s*?(\\S+?)\\s+?");  
	private Pattern sonarProjectVersionPattern = Pattern.compile("sonar\\.projectVersion\\s*?=\\s*?(\\S+?)\\s+?");  
	private Pattern sonarSourcesPattern = Pattern.compile("sonar\\.sources\\s*?=\\s*?(\\S+?)\\s+?");  
	private Pattern sonarBinariesPattern = Pattern.compile("sonar\\.java\\.binaries\\s*?=\\s*?(\\S+?)\\s+?");  
	
	private Pattern validateCronPattern = Pattern.compile("width=1>([\\s\\S]+?)</div>");
	//?????????????????????????????????Map????????????script
	private Map<String, Map<String, List<StringBuilder>>> nodeParallelMap = new HashMap<String, Map<String, List<StringBuilder>>>();
	
	private Map<String, Object> jenkinsParameterMap = new HashMap<String, Object>();//??????????????????????????????????????????????????????subSystemCode???systemPackageName??????
	
	private String systemCode = "systemCode";
	private String systemPackageName = "systemPackageName";
	private String subSystemPackageName = "subSystemPackageName";
	private String subSystemCode = "subSystemCode";
	private String packageName = "package.tar";
	private String startDate = "startDate";
	private String endDate = "endDate";
	private String moduleJson = "moduleJson";
	
	enum MethodType {//???????????????????????????
		buildMicroAutoJob,buildMicroAutoDeployJob,buildPackageAutoDeployJob,buildPROAutoDeployJob
	}

	private JenkinsUtil getJenkinsUtil(TblToolInfo bean) throws URISyntaxException {
		String url = "http://" + bean.getIp() + ":" + bean.getPort();// http://localhost:8087
		JenkinsUtil jenkinsUtil = new JenkinsUtil(url, bean.getUserName(), bean.getPassword());
//		if (jenkinsUtil == null || !jenkinsUtil.hasConnected(bean, url)) {
//			jenkinsUtilMap.put(jenkinsKey, jenkinsUtil);
//		}
		log.info("Jenkins?????????url:" + url + ",username:" + bean.getUserName() + ",password:" + bean.getPassword());
		return jenkinsUtil;
	}
	
	/**
	 * ???????????????Jenkins??????
	 * @param
	 * @return
	 */
	@Override
	public void buildGeneralAutoJob(Map<String, Object> paramMap) throws Exception {
		TblSystemModuleJenkinsJobRun moduleRun = (TblSystemModuleJenkinsJobRun)paramMap.get("resultModuleRun");
		TblSystemSonar tblSystemSonar = (TblSystemSonar)paramMap.get("tblSystemSonar");
		TblToolInfo tblSonarTool = (TblToolInfo)paramMap.get("tblSonarTool");
		
		List<TblSystemModuleJenkinsJobRun> moduleRunJobList = new ArrayList<TblSystemModuleJenkinsJobRun>();
		moduleRunJobList.add(moduleRun);
		List<TblSystemSonar> tblSystemSonarList = new ArrayList<TblSystemSonar>();
		tblSystemSonarList.add(tblSystemSonar);
		List<TblToolInfo> tblSonarToolList = new ArrayList<TblToolInfo>();
		tblSonarToolList.add(tblSonarTool);
		
		paramMap.put("tblSystemSonarList", tblSystemSonarList);
		paramMap.put("moduleRunJobList", moduleRunJobList);
		paramMap.put("tblSonarToolList", tblSonarToolList);
		
		buildMicroAutoJob(paramMap);
	}

	/**
	 * ??????????????????????????????Jenkins???????????????Pipeline??????
	 * @param
	 * @return
	 */
	@Override
	public void buildMicroAutoJob(Map<String, Object> paramMap) throws Exception {
		TblSystemInfo tblSystemInfo = (TblSystemInfo)paramMap.get("tSystemInfo");
		TblSystemScm tblSystemScm = (TblSystemScm)paramMap.get("tblSystemScm");
		List<TblSystemModule> tblSystemModuleList = (List<TblSystemModule>)paramMap.get("tblSystemModuleList");
		List<TblSystemModuleScm> tblSystemModuleScmList = (List<TblSystemModuleScm>)paramMap.get("tblSystemModuleScmList");
		TblSystemJenkins tblSystemJenkins = (TblSystemJenkins)paramMap.get("tblSystemJenkins");
		TblToolInfo jenkinsTool = (TblToolInfo)paramMap.get("jenkinsTool");
		TblToolInfo sourceTool = (TblToolInfo)paramMap.get("sourceTool");
		List<TblToolInfo> sourceToolList = (List<TblToolInfo>)paramMap.get("sourceToolList");
		TblToolInfo nexusToolInfo = (TblToolInfo)paramMap.get("nexusToolInfo");
		List<TblToolInfo> tblSonarToolList = (List<TblToolInfo>)paramMap.get("tblSonarToolList");
		List<TblSystemSonar> tblSystemSonarList = (List<TblSystemSonar>)paramMap.get("tblSystemSonarList");
		String jobrun = (String)paramMap.get("jobrun");
		List<TblSystemModuleJenkinsJobRun> moduleRunJobList = (List<TblSystemModuleJenkinsJobRun>)paramMap.get("moduleRunJobList");
		String scheduled = (String)paramMap.get("scheduled");
		String sonarflag = (String)paramMap.get("sonarflag");//1?????????2??????
		String artType = (String)paramMap.get("artType");//??????????????????1???snapshots,2???releases
		String version = (String)paramMap.get("version");//????????????
		String sonar = (String)paramMap.get("sonar");//true????????????Sonar????????????????????????
		
		paramMap.put("methodType", MethodType.buildMicroAutoJob);
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			Element root = jenkinsUtil.getSAXElementByXml(1);
			String configXml = root.asXML();
			
			/** Properties????????????*/
			Element propertiesElement = root.element("properties");
			if (propertiesElement != null) {
				//????????????????????????
				assembleRemoveOldData(-1, 3, -1, -1, propertiesElement);
				//???????????????????????????????????????
				assembleTimerTrigger(tblSystemJenkins, scheduled, configXml, root);
				if (StringUtil.isNotEmpty(scheduled) && scheduled.equals("true") && StringUtil.isNotEmpty(tblSystemJenkins.getJobCron())) {//????????????
					sonarflag = "1";
					paramMap.put("sonarflag", sonarflag);
				}
			}
			if (StringUtil.isNotEmpty(sonar)) {
				sonarflag = "1";
				paramMap.put("sonarflag", sonarflag);
			}
			
			/** pipeline????????????*/
			Element scriptElement = root.element("definition").element("script");
			StringBuilder scriptSB = new StringBuilder();
			int blankCount = 1;
			scriptSB.append(assebmleImportClass(paramMap, 0));
			scriptSB.append("\nnode").append(getRootNode(tblSystemInfo)).append("{\n");
			scriptSB.append(assebmleExtData(paramMap, tblSystemInfo, tblSystemModuleList, moduleRunJobList, blankCount));
			scriptSB.append(getPreBlank(blankCount)).append("try{\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("echo '" + scriptInsertMsg + "'\n");

			nodeParallelMap.clear();
			jenkinsParameterMap.clear();
			jenkinsParameterMap.put("tblSystemModuleScmList", tblSystemModuleScmList);
			// pipeline ???Shell???????????????????????????
			assembleParameter(paramMap, tblSystemInfo, scriptSB, blankCount+1);
			// Git,SVN?????????
			assembleSourceSCM(paramMap, tblSystemInfo, tblSystemModuleList, tblSystemScm, tblSystemModuleScmList, sourceTool, sourceToolList, scriptSB, blankCount+1);
			// Maven,Gradle,ant??????
			if (StringUtil.isEmpty(sonar)) {
				assembleCompile(paramMap, tblSystemInfo, tblSystemJenkins, tblSystemModuleList, nexusToolInfo, artType, version, 1, scriptSB, blankCount+1);
			}
			// sonar??????
			if (StringUtil.isNotEmpty(sonarflag) && sonarflag.equals("1")) {//???????????????????????????Sonar????????????
				assembleSonar(paramMap, tblSystemInfo, tblSystemScm, tblSystemModuleScmList, sourceTool, tblSonarToolList, tblSystemModuleList, tblSystemSonarList, scriptSB, blankCount+1);
			}
			//?????????????????????scriptSB
			assembleNodeMap2ScriptSB(paramMap, tblSystemModuleList, scriptSB, blankCount);
//			assembleRemoveWorksapce(scriptSB, blankCount+1);
			
			scriptSB.append(getPreBlank(blankCount)).append("}catch(callback_shell_ex){\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("  echo '" + scriptInsertMsg + "'\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("  echo '????????????......'\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("  throw callback_shell_ex\n");
			scriptSB.append(getPreBlank(blankCount)).append("} finally {\n");
			
			paramMap.put(BUILD_TYPE, BUILD_TYPE_AUTO);
			// ????????????????????????shell
			assembleCallbackAuto(paramMap, tblSystemInfo, tblSystemScm, tblSystemJenkins, jenkinsTool, jobrun, moduleRunJobList, 
					tblSystemSonarList, scriptSB, blankCount+1);
			
			scriptSB.append(getPreBlank(blankCount)).append("}\n");
			scriptSB.append("}\n\n");
			scriptSB.append(assebmleMethod(paramMap, 0));
			scriptElement.setText(scriptSB.toString());
			
			buildJob(scheduled, tblSystemJenkins, root, jenkinsUtil);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}

	/**
	 * ????????????Jenkins??????
	 * @param jenkinsTool
	 * @param jobName
	 * @param parameterJson
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> buildManualJob(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		JenkinsUtil jenkinsUtil = null;
		try {
			if (paramMap != null) {
				TblToolInfo jenkinsToolInfo = (TblToolInfo)paramMap.get("jenkinsToolInfo");
				String jobName = Objects.toString(paramMap.get("jobName"), "");
				String jsonParam = Objects.toString(paramMap.get("jsonParam"), "");
				String buildType = Objects.toString(paramMap.get(BUILD_TYPE), "");
				TblSystemJenkins tblSystemJenkins = (TblSystemJenkins)paramMap.get("tblSystemJenkins");
				String scheduled = Objects.toString(paramMap.get("scheduled"), "");
				paramMap.remove("jsonParam");
				paramMap.remove("jenkinsToolInfo");
				paramMap.remove("tblSystemJenkins");
//				paramMap.remove("scheduled");
				paramMap.put("jenkinsToolId", jenkinsToolInfo.getId());
				if (StringUtil.isEmpty(buildType)) {
					paramMap.put(BUILD_TYPE, BUILD_TYPE_MANUAL);
				}
				Map<String, String> parameterMap = new HashMap<String, String>();
				if (StringUtil.isNotEmpty(jsonParam)) {
					parameterMap = JSON.parseObject(jsonParam, Map.class);
				}
				jenkinsUtil = getJenkinsUtil(jenkinsToolInfo);
				String configXml = jenkinsUtil.getConfigXml(tblSystemJenkins.getJobPath(), jobName);
				returnMap.put("jobConfiguration", configXml);//??????config.xml??????
//				deleteJobCallback(jenkinsToolInfo, tblSystemJenkins, jobName);//????????????????????????????????????
				configXml = jenkinsUtil.getConfigXml(tblSystemJenkins.getJobPath(), jobName);
				if (StringUtil.isNotEmpty(configXml)) {
					// ??????config.xml??????????????????????????????sonar maven nexus...
					Map<String ,Object> pluginMap = getPluginMsgWithConfigXml(jenkinsUtil, paramMap, configXml);
					// ???config.xml???????????????????????????????????????????????????
//					configXml = insertJobCallbackXml(jenkinsUtil, paramMap, pluginMap, configXml);
					if (StringUtil.isNotEmpty(scheduled) && scheduled.equals("true")) {//??????????????????????????????
						Element root = jenkinsUtil.getSAXElementByXml(configXml);
						/** Properties????????????*/
						Element propertiesElement = root.element("properties");
						if (propertiesElement != null) {
							//???????????????????????????????????????
							assembleTimerTrigger(tblSystemJenkins, scheduled, configXml, root);
							configXml = root.asXML();
							//System.out.println(configXml);
							jenkinsUtil.updateJenkinsJob(tblSystemJenkins.getJobPath(), jobName, configXml);
						}
					} else {
						//System.out.println(configXml);
//						jenkinsUtil.updateJenkinsJob(tblSystemJenkins.getJobPath(), jobName, configXml);
						// ???????????? 
						if (configXml.indexOf("ParametersDefinitionProperty") == -1) {//??????????????????
							jenkinsUtil.buildJenkinsJob(tblSystemJenkins.getJobPath(), jobName);
						} else {
							jenkinsUtil.buildJenkinsJob(tblSystemJenkins.getJobPath(), jobName, parameterMap);
						}
					}
				}
				jenkinsUtil.closeJenkins();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
		return returnMap;
	}
	
	/**
	 * ?????????????????????Jenkins??????
	 * @param
	 * @return
	 */
	@Override
	public void buildGeneralAutoDeployJob(Map<String, Object> paramMap) throws Exception {
		TblSystemModuleJenkinsJobRun moduleRun = (TblSystemModuleJenkinsJobRun)paramMap.get("resultModuleRun");
		TblSystemSonar tblSystemSonar = (TblSystemSonar)paramMap.get("tblSystemSonar");
		
		List<TblSystemModuleJenkinsJobRun> moduleRunJobList = new ArrayList<TblSystemModuleJenkinsJobRun>();
		moduleRunJobList.add(moduleRun);
		List<TblSystemSonar> tblSystemSonarList = new ArrayList<TblSystemSonar>();
		tblSystemSonarList.add(tblSystemSonar);
		
		paramMap.put("tblSystemSonarList", tblSystemSonarList);
		paramMap.put("moduleRunJobList", moduleRunJobList);
		
		buildMicroAutoDeployJob(paramMap);
	}
	
	
	/**
	 * ?????????????????????Jenkins??????
	 * @param paramMap
	 * @throws Exception
	 */
	@Override
	public void buildMicroAutoDeployJob(Map<String, Object> paramMap) throws Exception {
		TblSystemInfo tblSystemInfo = (TblSystemInfo)paramMap.get("tSystemInfo");
		TblSystemScm tblSystemScm = (TblSystemScm)paramMap.get("tblSystemScm");
		List<TblSystemModule> tblSystemModuleList = (List<TblSystemModule>)paramMap.get("tblSystemModuleList");
		List<TblSystemModuleScm> tblSystemModuleScmList = (List<TblSystemModuleScm>)paramMap.get("tblSystemModuleScmList");
		TblSystemJenkins tblSystemJenkins = (TblSystemJenkins)paramMap.get("tblSystemJenkins");
		TblToolInfo jenkinsTool = (TblToolInfo)paramMap.get("jenkinsTool");
		TblToolInfo sourceTool = (TblToolInfo)paramMap.get("sourceTool");
		List<TblToolInfo> sourceToolList = (List<TblToolInfo>)paramMap.get("sourceToolList");
		List<TblSystemSonar> tblSystemSonarList = (List<TblSystemSonar>)paramMap.get("tblSystemSonarList");
		String jobrun = (String)paramMap.get("jobrun");
		List<TblSystemModuleJenkinsJobRun> moduleRunJobList = (List<TblSystemModuleJenkinsJobRun>)paramMap.get("moduleRunJobList");
		String scheduled = (String)paramMap.get("scheduled");
		List<TblServerInfo> tblServerInfoList = (List<TblServerInfo>)paramMap.get("tblServerInfoList");
		List<TblSystemDeploy> tblSystemDeployList = (List<TblSystemDeploy>)paramMap.get("tblSystemDeployList");
		List<TblSystemDeployScript> tblSystemDeployScriptList = (List<TblSystemDeployScript>)paramMap.get("tblSystemDeployScriptList");
		
		paramMap.put("methodType", MethodType.buildMicroAutoDeployJob);
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			Element root = jenkinsUtil.getSAXElementByXml(1);
			String configXml = root.asXML();
			
			root.element("description").setText("??????????????????");
			
			/** Properties????????????*/
			Element propertiesElement = root.element("properties");
			if (propertiesElement != null) {
				//????????????????????????
				assembleRemoveOldData(-1, 3, -1, -1, propertiesElement);
				//???????????????????????????????????????
				assembleTimerTrigger(tblSystemJenkins, scheduled, configXml, root);
			}
			
			/** pipeline????????????*/
			Element scriptElement = root.element("definition").element("script");
			StringBuilder scriptSB = new StringBuilder();
			int blankCount = 1;
			scriptSB.append(assebmleImportClass(paramMap, 0));
			scriptSB.append("\nnode").append(getRootNode(tblSystemInfo)).append("{\n");
			scriptSB.append(assebmleExtData(paramMap, tblSystemInfo, tblSystemModuleList, moduleRunJobList, blankCount));
			scriptSB.append(getPreBlank(blankCount)).append("try{\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("echo '" + scriptInsertMsg + "'\n");

			nodeParallelMap.clear();
			jenkinsParameterMap.clear();
			jenkinsParameterMap.put("tblSystemDeployList", tblSystemDeployList);
			jenkinsParameterMap.put("tblSystemModuleScmList", tblSystemModuleScmList);
			// pipeline ???Shell???????????????????????????
			assembleParameter(paramMap, tblSystemInfo, scriptSB, blankCount+1);
			// Git,SVN?????????
			assembleSourceSCM(paramMap, tblSystemInfo, tblSystemModuleList, tblSystemScm, tblSystemModuleScmList, sourceTool, sourceToolList, scriptSB, blankCount+1);
			// Maven,Gradle,ant??????
			assembleCompile(paramMap, tblSystemInfo, tblSystemJenkins, tblSystemModuleList, null, null, null, 2, scriptSB, blankCount+1);
			// sonar??????
//			assembleSonar(tblSystemInfo, tblSystemScm, sourceTool, tblSystemModuleList, tblSystemSonarList, scriptSB, blankCount+1);
			
			// ????????????
			assembleDeployData(paramMap, tblSystemInfo, jenkinsTool, tblSystemModuleList, null, 
					tblServerInfoList, tblSystemDeployList, tblSystemDeployScriptList, scriptSB, blankCount+1);
			
			//?????????????????????scriptSB
			assembleNodeMap2ScriptSB(paramMap, tblSystemModuleList, scriptSB, blankCount);
			
			scriptSB.append(getPreBlank(blankCount)).append("}catch(callback_shell_ex){\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("  echo '" + scriptInsertMsg + "'\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("  echo '????????????......'\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("  throw callback_shell_ex\n");
			scriptSB.append(getPreBlank(blankCount)).append("} finally {\n");
			
			paramMap.put(BUILD_TYPE, BUILD_TYPE_AUTO_DEPLOY);
			// ????????????????????????shell
			assembleCallbackAuto(paramMap, tblSystemInfo, tblSystemScm, tblSystemJenkins, jenkinsTool, jobrun, moduleRunJobList, 
					tblSystemSonarList, scriptSB, blankCount+1);
			
			scriptSB.append(getPreBlank(blankCount)).append("}\n");
			scriptSB.append("}\n\n");
			scriptSB.append(assebmleMethod(paramMap, 0));
			scriptElement.setText(scriptSB.toString());
			
			buildJob(scheduled, tblSystemJenkins, root, jenkinsUtil);
			jenkinsUtil.closeJenkins();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ????????????Jenkins??????
	 * @param paramMap
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> buildManualDeployJob(Map<String, Object> paramMap) throws Exception {
		paramMap.put(BUILD_TYPE, BUILD_TYPE_MANUAL_DEPLOY);
		return buildManualJob(paramMap);
	}
	
	/**
	 * ????????????Jenkins??????
	 * @param paramMap
	 * @throws Exception
	 */
	@Override
	public void buildPackageAutoDeployJob(Map<String, Object> paramMap) throws Exception {
		TblSystemInfo tblSystemInfo = (TblSystemInfo)paramMap.get("tSystemInfo");
		TblSystemScm tblSystemScm = (TblSystemScm)paramMap.get("tblSystemScm");
		List<TblSystemModule> tblSystemModuleList = (List<TblSystemModule>)paramMap.get("tblSystemModuleList");
		TblSystemJenkins tblSystemJenkins = (TblSystemJenkins)paramMap.get("tblSystemJenkins");
		TblToolInfo jenkinsTool = (TblToolInfo)paramMap.get("jenkinsTool");
		List<TblSystemSonar> tblSystemSonarList = (List<TblSystemSonar>)paramMap.get("tblSystemSonarList");
		String jobrun = (String)paramMap.get("jobrun");
		List<TblSystemModuleJenkinsJobRun> moduleRunJobList = (List<TblSystemModuleJenkinsJobRun>)paramMap.get("moduleRunJobList");
		String scheduled = (String)paramMap.get("scheduled");
		List<TblServerInfo> tblServerInfoList = (List<TblServerInfo>)paramMap.get("tblServerInfoList");
		List<TblSystemDeploy> tblSystemDeployList = (List<TblSystemDeploy>)paramMap.get("tblSystemDeployList");
		List<TblSystemDeployScript> tblSystemDeployScriptList = (List<TblSystemDeployScript>)paramMap.get("tblSystemDeployScriptList");
		
		TblToolInfo nexusTool = (TblToolInfo)paramMap.get("nexusTool");
		List<TblArtifactInfo> tblArtifactInfoList = (List<TblArtifactInfo>)paramMap.get("tblArtifactInfoList");
		List<TblSystemDbConfig> tblSystemDbConfigList = (List<TblSystemDbConfig>)paramMap.get("tblSystemDbConfigList");
		
		paramMap.put("methodType", MethodType.buildPackageAutoDeployJob);
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			Element root = jenkinsUtil.getSAXElementByXml(1);
			String configXml = root.asXML();
			
			root.element("description").setText("??????????????????");
			
			/** Properties????????????*/
			Element propertiesElement = root.element("properties");
			if (propertiesElement != null) {
				//????????????????????????
				assembleRemoveOldData(-1, 3, -1, -1, propertiesElement);
				//???????????????????????????????????????
				assembleTimerTrigger(tblSystemJenkins, scheduled, configXml, root);
			}
			
			/** pipeline????????????*/
			Element scriptElement = root.element("definition").element("script");
			StringBuilder scriptSB = new StringBuilder();
			int blankCount = 1;
			scriptSB.append(assebmleImportClass(paramMap, 0));
			scriptSB.append("\nnode").append(getRootNode(tblSystemInfo)).append("{\n");
			scriptSB.append(assebmleExtData(paramMap, tblSystemInfo, tblSystemModuleList, moduleRunJobList, blankCount));
			scriptSB.append(getPreBlank(blankCount)).append("try{\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("echo '" + scriptInsertMsg + "'\n");

			nodeParallelMap.clear();
			jenkinsParameterMap.clear();
			jenkinsParameterMap.put("tblSystemDeployList", tblSystemDeployList);
			// pipeline ???Shell???????????????????????????
			assembleParameter(paramMap, tblSystemInfo, scriptSB, blankCount+1);
			//???Nexus????????????????????????
			downloadPackage(paramMap, tblSystemInfo, nexusTool, tblSystemModuleList, tblArtifactInfoList, scriptSB, blankCount+1);
			
			//??????zip???CD????????????SQL????????????
			assembleSQLData(paramMap, tblSystemInfo, nexusTool, tblSystemModuleList, tblArtifactInfoList, tblSystemDbConfigList, scriptSB, blankCount+1);
			
			// ????????????
			assembleDeployData(paramMap, tblSystemInfo, jenkinsTool, tblSystemModuleList, tblArtifactInfoList, 
					tblServerInfoList, tblSystemDeployList, tblSystemDeployScriptList, scriptSB, blankCount+1);
			
			//?????????????????????scriptSB
			assembleNodeMap2ScriptSB(paramMap, tblSystemModuleList, scriptSB, blankCount);
			
			scriptSB.append(getPreBlank(blankCount)).append("}catch(callback_shell_ex){\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("  echo '" + scriptInsertMsg + "'\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("  echo '????????????......'\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("  throw callback_shell_ex\n");
			scriptSB.append(getPreBlank(blankCount)).append("} finally {\n");
			
			paramMap.put(BUILD_TYPE, BUILD_TYPE_PACKAGE_AUTO_DEPLOY);
			// ????????????????????????shell
			assembleCallbackAuto(paramMap, tblSystemInfo, tblSystemScm, tblSystemJenkins, jenkinsTool, jobrun, moduleRunJobList, 
					tblSystemSonarList, scriptSB, blankCount+1);
			
			scriptSB.append(getPreBlank(blankCount)).append("}\n");
			scriptSB.append("}\n\n");
			scriptSB.append(assebmleMethod(paramMap, 0));
			scriptElement.setText(scriptSB.toString());
			
			buildJob(scheduled, tblSystemJenkins, root, jenkinsUtil);
			jenkinsUtil.closeJenkins();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ?????????726??????????????????????????????????????????????????????????????????????????????
	 * @param paramMap
	 * @throws Exception
	 */
	@Override
	public void buildPROAutoDeployJob(Map<String, Object> paramMap) throws Exception {
		TblSystemInfo tblSystemInfo = (TblSystemInfo)paramMap.get("tSystemInfo");
		TblSystemScm tblSystemScm = (TblSystemScm)paramMap.get("tblSystemScm");
		List<TblSystemModule> tblSystemModuleList = (List<TblSystemModule>)paramMap.get("tblSystemModuleList");
		List<TblSystemModuleScm> tblSystemModuleScmList = (List<TblSystemModuleScm>)paramMap.get("tblSystemModuleScmList");
		TblSystemJenkins tblSystemJenkins = (TblSystemJenkins)paramMap.get("tblSystemJenkins");
		TblToolInfo jenkinsTool = (TblToolInfo)paramMap.get("jenkinsTool");
		TblToolInfo sourceTool = (TblToolInfo)paramMap.get("sourceTool");
		List<TblToolInfo> sourceToolList = (List<TblToolInfo>)paramMap.get("sourceToolList");
		String jobrun = (String)paramMap.get("jobrun");
		String scheduled = (String)paramMap.get("scheduled");
		
		TblToolInfo nexusTool = (TblToolInfo)paramMap.get("nexusTool");
		List<TblArtifactInfo> tblArtifactInfoList = (List<TblArtifactInfo>)paramMap.get("tblArtifactInfoList");
		
		TblSystemModuleJenkinsJobRun moduleRun = (TblSystemModuleJenkinsJobRun)paramMap.get("resultModuleRun");
		List<TblSystemModuleJenkinsJobRun> moduleRunJobList = new ArrayList<TblSystemModuleJenkinsJobRun>();
		if (moduleRun != null && moduleRun.getId() != null) {
			moduleRunJobList.add(moduleRun);
		} else {
			moduleRunJobList = (List<TblSystemModuleJenkinsJobRun>)paramMap.get("moduleRunJobList");
		}
		TblSystemSonar tblSystemSonar = (TblSystemSonar)paramMap.get("tblSystemSonar");
		List<TblSystemSonar> tblSystemSonarList = new ArrayList<TblSystemSonar>();
		if (tblSystemSonar != null) {
			tblSystemSonarList.add(tblSystemSonar);
		} else {
			tblSystemSonarList = (List<TblSystemSonar>)paramMap.get("tblSystemSonarList");
		}
		List<String> checkModuleList = (List<String>)paramMap.get("checkModuleList");
		
		paramMap.put("methodType", MethodType.buildPROAutoDeployJob);
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			Element root = jenkinsUtil.getSAXElementByXml(1);
			String configXml = root.asXML();
			
			if (tblSystemInfo.getDeployType() == 1) { //1:?????????????????????2:????????????
				root.element("description").setText("??????????????????");
			} else {
				root.element("description").setText("??????????????????");
			}
			
			/** Properties????????????*/
			Element propertiesElement = root.element("properties");
			if (propertiesElement != null) {
				//????????????????????????
				assembleRemoveOldData(-1, 3, -1, -1, propertiesElement);
				//???????????????????????????????????????
				assembleTimerTrigger(tblSystemJenkins, scheduled, configXml, root);
			}
			
			/** pipeline????????????*/
			Element scriptElement = root.element("definition").element("script");
			StringBuilder scriptSB = new StringBuilder();
			int blankCount = 1;
			scriptSB.append(assebmleImportClass(paramMap, 0));
			paramMap.put("isPROAutoDeploy", true);
			scriptSB.append("\nnode").append(getRootNode(tblSystemInfo)).append("{\n");
			scriptSB.append(assebmleExtData(paramMap, tblSystemInfo, tblSystemModuleList, moduleRunJobList, blankCount));
			scriptSB.append(getPreBlank(blankCount)).append("try{\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("echo '" + scriptInsertMsg + "'\n");
			
			//?????????????????????????????????????????????
			for (TblSystemModule tblSystemModule : tblSystemModuleList) {
				tblSystemModule.setSystemModuleFlag(null);
			}
			nodeParallelMap.clear();
			jenkinsParameterMap.clear();
			jenkinsParameterMap.put("tblSystemModuleScmList", tblSystemModuleScmList);
			// pipeline ???Shell???????????????????????????
			assembleParameter(paramMap, tblSystemInfo, scriptSB, blankCount+1);
			if (tblSystemInfo.getDeployType() == 1) { //1:?????????????????????2:????????????
				// Git,SVN?????????
				assembleSourceSCM(paramMap, tblSystemInfo, tblSystemModuleList, tblSystemScm, tblSystemModuleScmList, sourceTool, sourceToolList, scriptSB, blankCount+1);
				// Maven,Gradle,ant??????
				assembleCompile(paramMap, tblSystemInfo, tblSystemJenkins, tblSystemModuleList, nexusTool, null, null, 2, scriptSB, blankCount+1);
				// sonar??????
//				assembleSonar(tblSystemInfo, tblSystemScm, sourceTool, tblSystemModuleList, tblSystemSonarList, scriptSB, blankCount+1);
			} else {
				//???Nexus????????????????????????
				downloadPackage(paramMap, tblSystemInfo, nexusTool, tblSystemModuleList, tblArtifactInfoList, scriptSB, blankCount+1);
				//??????????????????????????????target?????????zip????????????????????????????????????????????????configuration/sql/package
				assemblePROZipPackage(tblSystemInfo, tblSystemModuleList, tblArtifactInfoList, scriptSB, blankCount+1);
			}
			
			//?????????????????????scriptSB
			assembleNodeMap2ScriptSB(paramMap, tblSystemModuleList, scriptSB, blankCount);
			
			// ?????????????????????????????????Jenkins??????uploadTemp????????????
			downloadPROFile(paramMap, scriptSB, blankCount+1);
			
			//???????????????sql,configuration,package?????????uploadTemp/uploadPackageTemp?????????????????????
			assemblePROCopyPackage(paramMap, tblSystemInfo, tblSystemJenkins, tblSystemModuleList, tblSystemScm, tblArtifactInfoList, checkModuleList, scriptSB, blankCount+1);
			
			// ????????????
			assemblePRODeployData(paramMap, tblSystemInfo, tblSystemModuleList, checkModuleList, scriptSB, blankCount+1);
			
			
			scriptSB.append(getPreBlank(blankCount)).append("}catch(callback_shell_ex){\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("  echo '" + scriptInsertMsg + "'\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("  echo '????????????......'\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("  throw callback_shell_ex\n");
			scriptSB.append(getPreBlank(blankCount)).append("} finally {\n");
			
			if (tblSystemInfo.getDeployType() == 1) { //1:?????????????????????2:????????????
				paramMap.put(BUILD_TYPE, BUILD_TYPE_AUTO_DEPLOY);
			} else {
				paramMap.put(BUILD_TYPE, BUILD_TYPE_PACKAGE_AUTO_DEPLOY);
			}
			// ????????????????????????shell
			assembleCallbackAuto(paramMap, tblSystemInfo, tblSystemScm, tblSystemJenkins, jenkinsTool, jobrun, moduleRunJobList, 
					tblSystemSonarList, scriptSB, blankCount+1);
			
			scriptSB.append(getPreBlank(blankCount)).append("}\n");
			scriptSB.append("}\n\n");
			scriptSB.append(assebmleMethod(paramMap, 0));
			scriptElement.setText(scriptSB.toString());
			
			buildJob(scheduled, tblSystemJenkins, root, jenkinsUtil);
			jenkinsUtil.closeJenkins();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ??????pipeline????????????????????????????????????????????????
	 * @param tblSystemInfo
	 * @return
	 */
	private String getRootNode(TblSystemInfo tblSystemInfo) {
		String node = "";
		String tag = tblSystemInfo.getSystemFlag();
		if (tag != null && StringUtil.isNotEmpty(tag.trim())) {
			node = "('" + tag.trim() + "')";
		}
		return node;
	}
	
	/**
	 * ??????pipeline????????????????????????????????????????????????
	 * @param tblSystemInfo
	 * @return
	 */
	private String getModuleNode(TblSystemModule tblSystemModule) {
		String node = "";
		String tag = tblSystemModule.getSystemModuleFlag();
		if (tag != null && StringUtil.isNotEmpty(tag.trim())) {
			node = "('" + tag.trim() + "')";
		}
		return node;
	}
	
	/**
	 * ?????????node??????script????????????????????????map???
	 * ???????????????
	 * @param tblSystemModule
	 * @param tempSB
	 * @param blankCount
	 * @return
	 */
	private int assembleNodeStart(TblSystemModule tblSystemModule, StringBuilder tempSB, int blankCount) {
		String nodeKey = tblSystemModule.getSystemModuleFlag();
		if (StringUtil.isNotEmpty(nodeKey)) {
			blankCount++;
		}
		return blankCount;
	}
	
	/**
	 * ?????????node??????script????????????????????????map???
	 * ???????????????
	 * @param tblSystemModule
	 * @param tempSB
	 * @param blankCount
	 * @return
	 */
	private int assembleNodeEnd(TblSystemModule tblSystemModule, StringBuilder tempSB, int blankCount) {
		String nodeKey = tblSystemModule.getSystemModuleFlag();
		if (StringUtil.isNotEmpty(nodeKey)) {
			blankCount--;
		} else {
			nodeKey = rootNodeKey;
		}
		//TODO
		Map<String, List<StringBuilder>> sbMap = nodeParallelMap.get(nodeKey);
		if (sbMap == null) {
			List<StringBuilder> list = new ArrayList<StringBuilder>();
			list.add(tempSB);
			sbMap = new HashMap<String, List<StringBuilder>>();
			sbMap.put(tblSystemModule.getModuleCode(), list);
			nodeParallelMap.put(nodeKey, sbMap);
		} else {
			List<StringBuilder> list = sbMap.get(tblSystemModule.getModuleCode());
			if (list == null) {
				list = new ArrayList<StringBuilder>();
				list.add(tempSB);
				sbMap.put(tblSystemModule.getModuleCode(), list);
			} else {
				list.add(tempSB);
			}
		}
		return blankCount;
	}
	
	/**
	 * ???Map?????????????????????????????????scriptSB
	 * ???????????????
	 * @param paramMap 
	 * @param tblSystemModuleList
	 * @param scriptSB
	 * @param blankCount
	 */
	private void assembleNodeMap2ScriptSB(Map<String, Object> paramMap, List<TblSystemModule> tblSystemModuleList, StringBuilder scriptSB, int blankCount) {
		List<TblSystemDeploy> tblSystemDeployList = null;
		if (jenkinsParameterMap.size() > 0) {
			tblSystemDeployList = (List<TblSystemDeploy>)jenkinsParameterMap.get("tblSystemDeployList");
		}
		if (tblSystemModuleList != null && tblSystemModuleList.size() > 0) {
			Map<String, List<Map<String, Object>>> sbSortMap = new HashMap<String, List<Map<String, Object>>>();//??????????????????
			Map<String, List<StringBuilder>> sbMap = nodeParallelMap.get(rootNodeKey);
			if (sbMap != null) {//???????????????????????????????????????
				
				for (TblSystemModule tblSystemModule : tblSystemModuleList) {//??????????????????
					List<StringBuilder> moduleSBList = sbMap.get(tblSystemModule.getModuleCode());
					if (moduleSBList != null && moduleSBList.size() > 0) {
						String sequenceKey = getSequenceKey(tblSystemModule, tblSystemDeployList);
						sortScriptMap(sbSortMap, tblSystemModule, sequenceKey, moduleSBList);
					}
				}
				
			}
			
			for (TblSystemModule tblSystemModule : tblSystemModuleList) {//??????????????????????????????
				String nodeKey = tblSystemModule.getSystemModuleFlag();
				if (StringUtil.isNotEmpty(nodeKey)) {
					sbMap = nodeParallelMap.get(nodeKey);
					if (sbMap != null) {
						List<StringBuilder> moduleSBList = sbMap.get(tblSystemModule.getModuleCode());
						if (moduleSBList != null && moduleSBList.size() > 0) {
							String sequenceKey = getSequenceKey(tblSystemModule, tblSystemDeployList);
							sortScriptMap(sbSortMap, tblSystemModule, sequenceKey, moduleSBList);
							
						}
					}
				}
			}
			
			//?????????????????????Map?????????scriptSB???
			int sequence = -1;
			Integer sequenceMax = (Integer)jenkinsParameterMap.get("sequenceMax");
			while (sequence <= sequenceMax) {
				String sequenceKey = "sequence_" + sequence;
				List<Map<String, Object>> sbSortList = sbSortMap.get(sequenceKey);
				if (sbSortList != null && sbSortList.size() > 0) {
					sortMap2Script(paramMap, tblSystemModuleList, sbSortList, sequenceKey, scriptSB, blankCount);
				}
				sequence++;
			}
		}
	}

	/**
	 * ?????????????????????Map?????????scriptSB???
	 * @param paramMap 
	 * @param tblSystemModuleList
	 * @param sbSortList
	 * @param sequenceKey
	 * @param scriptSB
	 * @param blankCount
	 */
	private void sortMap2Script(Map<String, Object> paramMap, List<TblSystemModule> tblSystemModuleList, List<Map<String, Object>> sbSortList, 
			String sequenceKey, StringBuilder scriptSB, int blankCount) {
			if (sbSortList.size() == 1) {
				List<StringBuilder> tempSBList = (List<StringBuilder>)sbSortList.get(0).get("moduleSBList");
				TblSystemModule tblSystemModule = (TblSystemModule)sbSortList.get(0).get("module");
				appendScript(paramMap, tblSystemModule, tempSBList, scriptSB, blankCount);
			} else {
				if (sequenceKey.indexOf("-1") != -1) {//?????????????????????????????????????????????
					for (int i = 0; i < sbSortList.size(); i++) {
						List<StringBuilder> tempSBList = (List<StringBuilder>)sbSortList.get(i).get("moduleSBList");
						TblSystemModule tblSystemModule = (TblSystemModule)sbSortList.get(i).get("module");
						appendScript(paramMap, tblSystemModule, tempSBList, scriptSB, blankCount);
					}
				} else { //??????????????????
					scriptSB.append("parallel").append("(\n");
					for (int i = 0; i < sbSortList.size(); i++) {
						List<StringBuilder> tempSBList = (List<StringBuilder>)sbSortList.get(i).get("moduleSBList");
						TblSystemModule tblSystemModule = (TblSystemModule)sbSortList.get(i).get("module");
						String parallelTitle = getParallelTitle(tblSystemModule);
						scriptSB.append(getPreBlank(1)).append("'").append(parallelTitle).append("':{\n");
						appendScript(paramMap, tblSystemModule, tempSBList, scriptSB, blankCount + 1);
						if (i != sbSortList.size() - 1) {
							scriptSB.append(getPreBlank(1)).append("},\n");
						} else {
							scriptSB.append(getPreBlank(1)).append("}\n");
						}
					}
					scriptSB.append(")\n");
				}
			}
	}

	/**
	 * ??????????????????
	 * @param tblSystemModule
	 * @return
	 */
	private String getParallelTitle(TblSystemModule tblSystemModule) {
		String parallelTitle = tblSystemModule.getModuleCode();
		if (StringUtil.isNotEmpty(tblSystemModule.getSystemModuleFlag())) {
			parallelTitle = tblSystemModule.getSystemModuleFlag() + " " + parallelTitle;
		}
		return parallelTitle;
	}

	/**
	 * ???????????????????????????????????????Node?????????????????????????????????scriptSB
	 * @param paramMap 
	 * @param tblSystemModule
	 * @param tempSBList
	 * @param scriptSB
	 * @param blankCount
	 */
	private void appendScript(Map<String, Object> paramMap, TblSystemModule tblSystemModule, List<StringBuilder> tempSBList,
			StringBuilder scriptSB, int blankCount) {
		String parentStr = "";
		String mustStr = "";
		List<TblSystemModuleScm> tblSystemModuleScmList = null;
		if (jenkinsParameterMap != null) {
			parentStr = (String)jenkinsParameterMap.get("parentStr");
			mustStr = (String)jenkinsParameterMap.get("mustStr");
			tblSystemModuleScmList = (List<TblSystemModuleScm>)jenkinsParameterMap.get("tblSystemModuleScmList");
		}
		String nodeKey = tblSystemModule.getSystemModuleFlag();
		if (tempSBList != null && tempSBList.size() > 0) {
			StringBuilder newSB = new StringBuilder();
			if (StringUtil.isNotEmpty(nodeKey)) {
				newSB.append(getPreBlank(blankCount)).append("node").append(getModuleNode(tblSystemModule)).append("{\n");
				newSB.append(getPreBlank(blankCount + 1)).append("echo '---------------NODE START:").append(nodeKey).append("---------------'\n");
				for (int i = 0; i < tempSBList.size(); i++) {
					StringBuilder sb = tempSBList.get(i);
					if (i == tempSBList.size() - 1) {
						sb = new StringBuilder(replaceStageLast(paramMap, sb.toString()));
					}
					if (sb.indexOf("checkout([") != -1) {
//						StringBuilder nodeKeySB = (StringBuilder)nodeParameterMap.get(nodeKey);
//						if (nodeKeySB == null) {
							newSB.append(sb);
							if (StringUtil.isNotEmpty(parentStr)) {
								newSB.append(parentStr);//??????parent pom??????
							}
							if (StringUtil.isNotEmpty(mustStr)) {
								mustStr = replaceAllStageLast(paramMap, mustStr);
								newSB.append(mustStr);//????????????????????????
							}
//							nodeParameterMap.put(nodeKey, sb);
//						} else {
//							for (TblSystemModuleScm tblSystemModuleScm : tblSystemModuleScmList) {
//								if (tblSystemModule.getId() == tblSystemModuleScm.getSystemModuleId().longValue()) {
//									String currentScmUrl = assembleScmUrl(tblSystemModuleScm.getScmUrl());
//									if (nodeKeySB.indexOf(currentScmUrl) == -1) {
//										newSB.append(sb);
//										break;
//									}
//								}
//							}
//						}
					} else {
						newSB.append(sb);
					}
				}
				newSB.append(getPreBlank(blankCount + 1)).append("echo '---------------NODE END:").append(nodeKey).append("---------------'\n");
				newSB.append(getPreBlank(blankCount)).append("}\n");
				String newSBStr = newSB.toString().replaceAll("stage\\('", "stage\\('" + nodeKey + " ");
				scriptSB.append(newSBStr);
			} else {
				for (int i = 0; i < tempSBList.size(); i++) {
					StringBuilder sb = tempSBList.get(i);
					if (i == tempSBList.size() - 1) {
						sb = new StringBuilder(replaceStageLast(paramMap, sb.toString()));
					}
					if (sb.indexOf("checkout([") != -1) {
						StringBuilder nodeKeySB = (StringBuilder)jenkinsParameterMap.get(rootNodeKey);
						if (nodeKeySB == null) {
							newSB.append(sb);
							if (StringUtil.isNotEmpty(parentStr)) {
								newSB.append(parentStr);//??????parent pom??????
							}
							if (StringUtil.isNotEmpty(mustStr)) {
								mustStr = replaceAllStageLast(paramMap, mustStr);
								newSB.append(mustStr);//????????????????????????
							}
							jenkinsParameterMap.put(rootNodeKey, sb);
						} else {
							for (TblSystemModuleScm tblSystemModuleScm : tblSystemModuleScmList) {
								if (tblSystemModule.getId() == tblSystemModuleScm.getSystemModuleId().longValue()) {
									String currentScmUrl = assembleScmUrl(tblSystemModuleScm.getScmUrl());
									if (nodeKeySB.indexOf(currentScmUrl) == -1) {
										newSB.append(sb);
										jenkinsParameterMap.put(rootNodeKey, nodeKeySB.append(sb));
										break;
									}
								}
							}
						}
						
					} else {
						newSB.append(sb);
					}
				}
				scriptSB.append(newSB);
			}
		}
	}

	private String replaceStageLast(Map<String, Object> paramMap, String sbStr) {
		int lastIndex = sbStr.lastIndexOf("stageEnd");
		if (paramMap.get("isPROAutoDeploy") == null && lastIndex != -1) {//???????????????????????????
			String subStartStr = sbStr.substring(0, lastIndex);
			String subEndStr = sbStr.substring(lastIndex);
			subEndStr = subEndStr.replaceAll("stageEnd", "dataMap.put('stageLast',true)\n      stageEnd");
			sbStr = subStartStr + subEndStr;
		}
		return sbStr;
	}
	private String replaceAllStageLast(Map<String, Object> paramMap, String sbStr) {
		int lastIndex = sbStr.lastIndexOf("stageEnd");
		if (paramMap.get("isPROAutoDeploy") == null && lastIndex != -1) {//???????????????????????????
			sbStr = sbStr.replaceAll("stageEnd", "dataMap.put('stageLast',true)\n      stageEnd");
		}
		return sbStr;
	}

	/**
	 * ??????????????????:???????????????????????????BUILD_DEPENDENCY_SEQUENCE?????????????????????????????????????????????????????????????????????DEPLOY_SEQUENCE
	 * @param tblSystemModule
	 * @param tblSystemDeployList
	 * @return
	 */
	private String getSequenceKey(TblSystemModule tblSystemModule, List<TblSystemDeploy> tblSystemDeployList) {
		Integer sequence = null;
		if (tblSystemDeployList == null || tblSystemDeployList.size() == 0) {//????????????
			sequence = tblSystemModule.getBuildDependencySequence();
			if (sequence == null) {
				sequence = -1;
			}
		} else {//????????????
			for (TblSystemDeploy tblSystemDeploy : tblSystemDeployList) {
				if (tblSystemModule.getId().equals(tblSystemDeploy.getSystemModuleId())) {
					sequence = tblSystemDeploy.getDeploySequence();
					if (sequence == null) {
						sequence = -1;
					}
					break;
				}
			}
		}
		if (sequence == null) {
			sequence = -1;
		}
		
		Integer sequenceMax = (Integer)jenkinsParameterMap.get("sequenceMax");
		if (sequenceMax == null) {
			jenkinsParameterMap.put("sequenceMax", sequence);
		} else {
			if (sequence > sequenceMax) {
				jenkinsParameterMap.put("sequenceMax", sequence);
			}
		}
		
		String key = "sequence_" + sequence;
		return key;
	}

	/**
	 * ????????????Map?????????????????????????????????
	 * @param moduleSortMap
	 * @param tblSystemModule
	 * @param tempSB
	 */
	private void sortScriptMap(Map<String, List<Map<String, Object>>> sbSortMap, TblSystemModule tblSystemModule, 
			String sequenceKey, List<StringBuilder> moduleSBList) {
		List<Map<String, Object>> sbList = sbSortMap.get(sequenceKey);
		if (sbList == null) {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("moduleSBList", moduleSBList);
			tempMap.put("module", tblSystemModule);
			sbList = new ArrayList<Map<String, Object>>();
			sbList.add(tempMap);
			sbSortMap.put(sequenceKey, sbList);
		} else {
			Map<String, Object> tempMap = new HashMap<String, Object>();
			tempMap.put("moduleSBList", moduleSBList);
			tempMap.put("module", tblSystemModule);
			sbList.add(tempMap);
		}
	}
	
	/**
	 * ??????????????????????????????????????????
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
	@Override
	public String getJobCron(TblToolInfo jenkinsToolInfo, TblSystemJenkins tblSystemJenkins, String jobName) throws Exception {
		String corn = "";
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsToolInfo);
			String configXml = jenkinsUtil.getConfigXml(tblSystemJenkins.getJobPath(), jobName);
			if (StringUtil.isNotEmpty(configXml) && configXml.indexOf("TimerTrigger") != -1) {
				Element root = jenkinsUtil.getSAXElementByXml(configXml);
				//???????????????
				if (configXml.indexOf(JenkinsUtil.JOB_TYPE_WORKFLOW) != -1
						|| configXml.indexOf(JenkinsUtil.JOB_TYPE_WORKFLOW_MULTIBRANCH) != -1) {
					corn = root.element("properties").element("org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty")
							.element("triggers").element("hudson.triggers.TimerTrigger").element("spec").getText();
				} else {
					corn = root.element("triggers").element("hudson.triggers.TimerTrigger").element("spec").getText();
				}
			}
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
		return corn;
	}
	
	/**
	 * ??????PRO???????????????????????????SNAPSHOT???????????????Release?????????????????????Nexus
	 * @param paramMap
	 * @throws Exception
	 */
	public void buildSnapshots2ReleaseJob(Map paramMap) throws Exception {
		TblToolInfo jenkinsToolInfo = (TblToolInfo)paramMap.get("jenkinsToolInfo");
		TblToolInfo nexusToolInfo = (TblToolInfo)paramMap.get("nexusToolInfo");
		TblArtifactInfo tblArtifactInfo = (TblArtifactInfo)paramMap.get("tblArtifactInfo");
		JenkinsUtil jenkinsUtil = null;
		try {
			String jobName = JenkinsUtil.JENKINS_JOB_SNAPSHOTS2RELEASE;
			String nexusShell = this.assembleSnapshots2ReleaseShell(nexusToolInfo, tblArtifactInfo);
			
			jenkinsUtil = getJenkinsUtil(jenkinsToolInfo);
			Element root = jenkinsUtil.getSAXElementByXml(2);
			
			/** Properties????????????*/
			Element propertiesElement = root.element("properties");
			if (propertiesElement != null) {
				//????????????????????????
				assembleRemoveOldData(-1, 3, -1, -1, propertiesElement);
			}
			
			//SNAPSHOT???????????????Release????????????
			Element buildersElement = root.element("builders").element(JenkinsUtil.SHELL_NAME).element(JenkinsUtil.SHELL_COMMAND);
			if (buildersElement != null) {
				buildersElement.setText(nexusShell);
			}
			TblSystemJenkins tblSystemJenkins = new TblSystemJenkins();
			tblSystemJenkins.setJobName(jobName);
			buildJob(null, tblSystemJenkins, root, jenkinsUtil);
			jenkinsUtil.closeJenkins();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ???SNAPSHOT???????????????Release?????????????????????Nexus
	 * mvn deploy:deploy-file -Dmaven.test.skip=true -DgroupId=com.zd -DartifactId=test -Dversion=0.0.1 -Dpackaging=war 
	 * -Dfile=G:\workspace\test\target\testweb-0.0.1-SNAPSHOT.war -Durl=http://192.168.1.145:8081/nexus/repository/maven-releases/ 
	 * -DrepositoryId=releases
	 * @param nexusToolInfo 
	 * 
	 * @throws Exception
	 */
	private String assembleSnapshots2ReleaseShell(TblToolInfo nexusToolInfo, TblArtifactInfo tblArtifactInfo) throws Exception {
		//???Nexus????????????????????????
		//http://192.168.1.145:8081/nexus/repository/maven-snapshots/com/zd2/test2/0.0.1-SNAPSHOT/test2-0.0.1-20181217.111011-2.war
		StringBuilder downloadSb = new StringBuilder();
		downloadSb.append("http://").append(nexusToolInfo.getIp()).append(":").append(nexusToolInfo.getPort());
		String accessUrl = nexusToolInfo.getAccessUrl();
		if (StringUtil.isNotEmpty(accessUrl)) {
			accessUrl = JenkinsUtil.addSlash(accessUrl, "/|\\\\", "/", true);
			downloadSb.append(accessUrl);
		}
//		downloadSb.append("repository/").APPEND(nexusToolInfo.getSnapshotRepositoryName())
//		.append(addSlash(tblArtifactInfo.getGroupId(), "\\.", "/"));
		
		StringBuilder shellSB = new StringBuilder();
		shellSB.append("mvn deploy:deploy-file -q -Dmaven.test.skip=true")
				.append(" -DgroupId=").append(tblArtifactInfo.getGroupId())
				.append(" -DartifactId=").append(tblArtifactInfo.getArtifactId())
				.append(" -Dversion=").append(tblArtifactInfo.getVersion())
				.append(" -Dpackaging=war")
				.append(" -Dfile=").append(tblArtifactInfo.getArtifactId())
				.append(" -Durl").append(tblArtifactInfo.getArtifactId())
				.append(" -DrepositoryId=releases").append(tblArtifactInfo.getArtifactId());
		return shellSB.toString();
	}
	
	/**
	 * ???????????????class??????import
	 * @param paramMap
	 * @param blankCount
	 * @return
	 */
	private StringBuilder assebmleImportClass(Map paramMap, int blankCount) {
		StringBuilder scriptSb = new StringBuilder();
		scriptSb.append(getPreBlank(blankCount)).append("import groovy.json.JsonOutput").append("\n");
		return scriptSb;
	}
	
	/**
	 * ????????????groovy??????method
	 * @param paramMap
	 * @param blankCount
	 * @return
	 */
	private StringBuilder assebmleMethod(Map<String, Object> paramMap, int blankCount) {
		StringBuilder scriptSb = new StringBuilder();
		//stage??????????????????
		scriptSb.append(getPreBlank(blankCount)).append("public void stageStart(Map dataMap){").append("\n");
		scriptSb.append(getPreBlank(blankCount + 1)).append("String moduleCode = dataMap.get('moduleCode')").append("\n");
		scriptSb.append(getPreBlank(blankCount + 1)).append("Map moduleMap = dataMap.get('moduleMap')").append("\n");
		scriptSb.append(getPreBlank(blankCount + 1)).append("if(moduleMap != null && moduleCode != null){").append("\n");
		scriptSb.append(getPreBlank(blankCount + 2)).append("String startDate = moduleMap.get(moduleCode).get('startDate')").append("\n");
		scriptSb.append(getPreBlank(blankCount + 2)).append("if(startDate == null){").append("\n");
		scriptSb.append(getPreBlank(blankCount + 3)).append("startDate = new Date().format('yyyy-MM-dd HH:mm:ss')").append("\n");
		scriptSb.append(getPreBlank(blankCount + 3)).append("moduleMap.get(moduleCode).put('startDate',startDate)").append("\n");
		scriptSb.append(getPreBlank(blankCount + 2)).append("}").append("\n");
		scriptSb.append(getPreBlank(blankCount + 1)).append("}").append("\n");
		scriptSb.append(getPreBlank(blankCount)).append("}").append("\n");
		//stage??????????????????
		scriptSb.append(getPreBlank(blankCount)).append("public void stageEnd(Map dataMap){").append("\n");
		scriptSb.append(getPreBlank(blankCount + 1)).append("String moduleCode = dataMap.get('moduleCode')").append("\n");
		scriptSb.append(getPreBlank(blankCount + 1)).append("Map moduleMap = dataMap.get('moduleMap')").append("\n");
		scriptSb.append(getPreBlank(blankCount + 1)).append("if(moduleMap != null && moduleCode != null){").append("\n");
		scriptSb.append(getPreBlank(blankCount + 2)).append("String endDate = new Date().format('yyyy-MM-dd HH:mm:ss')").append("\n");
		scriptSb.append(getPreBlank(blankCount + 2)).append("moduleMap.get(moduleCode).put('endDate',endDate)").append("\n");
		scriptSb.append(getPreBlank(blankCount + 2)).append("Boolean stageLast = dataMap.get('stageLast')").append("\n");
		scriptSb.append(getPreBlank(blankCount + 2)).append("if(stageLast){").append("\n");
		scriptSb.append(getPreBlank(blankCount + 3)).append("moduleMap.get(moduleCode).put('buildStatus',2)").append("\n");
		scriptSb.append(getPreBlank(blankCount + 3)).append("dataMap.put('stageLast',false)").append("\n");
		scriptSb.append(getPreBlank(blankCount + 2)).append("}").append("\n");
		scriptSb.append(getPreBlank(blankCount + 1)).append("}").append("\n");
		scriptSb.append(getPreBlank(blankCount)).append("}").append("\n");
		return scriptSb;
	}
	
	/**
	 * ??????????????????????????????????????????????????????????????????????????????
	 * @param tblSystemInfo 
	 * @param tblSystemModuleList 
	 * @param moduleRunJobList 
	 * @param blankCount 
	 * @param object
	 * @return
	 */
	private StringBuilder assebmleExtData(Map paramMap, TblSystemInfo tblSystemInfo, List<TblSystemModule> tblSystemModuleList, 
			List<TblSystemModuleJenkinsJobRun> moduleRunJobList, int blankCount) {
		//def output = new Date().format('yyyy-MM-dd HH:mm:ss')
		StringBuilder extScript = new StringBuilder();
		extScript.append(getPreBlank(blankCount)).append(this.startDate).append(" = new Date().format('yyyy-MM-dd HH:mm:ss')\n");
		extScript.append(getPreBlank(blankCount)).append("echo '?????????????????????' + ").append(this.startDate).append("\n");
		//????????????????????????
		extScript.append(getPreBlank(blankCount)).append(this.systemCode).append("=''\n");
		extScript.append(getPreBlank(blankCount)).append(this.systemPackageName).append("=''\n");
		extScript.append(getPreBlank(blankCount)).append(this.subSystemPackageName).append("=''\n");
		extScript.append(getPreBlank(blankCount)).append(this.subSystemCode).append("=''\n");
//		 Map moduleMap = ['devManage':['moduleRunId':10,'buildStatus':2],'devManageWeb':['moduleRunId':11,'buildStatus':3]]
		String moduleRunMapStr = "Map moduleMap = [";
		if (tblSystemInfo.getArchitectureType() == 1) {//1=??????????????????2=????????????
			for (TblSystemModule module : tblSystemModuleList) {
				for (TblSystemModuleJenkinsJobRun jobRun : moduleRunJobList) {
					if (module.getId().equals(jobRun.getSystemModuleId())) {
						moduleRunMapStr += "'" + module.getModuleCode() + "':['moduleRunId':" + jobRun.getId() + ",'buildStatus':3" + "],";
						break;
					}
				}
			}
			if (moduleRunMapStr.endsWith(",")) {
				moduleRunMapStr = moduleRunMapStr.substring(0, moduleRunMapStr.length() - 1);
			}
		} else {
			moduleRunMapStr += "'" + tblSystemInfo.getSystemCode() + "':['moduleRunId':" + moduleRunJobList.get(0).getId() + ",'buildStatus':3" + "]";
		}
		moduleRunMapStr += "]";
		extScript.append(getPreBlank(blankCount)).append(moduleRunMapStr).append("\n");
		extScript.append(getPreBlank(blankCount)).append("Map dataMap = new TreeMap()\n");
		extScript.append(getPreBlank(blankCount)).append("dataMap.put('moduleMap',moduleMap)\n");
		return extScript;
	}

	/**
	 * ????????????
	 * @param scheduled
	 * @param tblSystemJenkins
	 * @param root
	 * @throws Exception
	 */
	private Integer buildJob(String scheduled, TblSystemJenkins tblSystemJenkins, Element root, JenkinsUtil jenkinsUtil) throws Exception {
//		System.out.println(root.asXML());
		String currentJobName = tblSystemJenkins.getJobName();
		boolean nowBuild = true;
		if (StringUtil.isNotEmpty(scheduled) && "true".equals(scheduled)) {
			currentJobName = tblSystemJenkins.getCronJobName();
			nowBuild = false;
		}
		
		JobWithDetails job = jenkinsUtil.getJenkinsJob(tblSystemJenkins.getJobPath(), currentJobName);
		if (job == null) {// Jenkins?????????Job?????????
			jenkinsUtil.creatJenkinsJob(currentJobName, root.asXML());
			job = jenkinsUtil.getJenkinsJob(tblSystemJenkins.getJobPath(), currentJobName);
		} else {
			jenkinsUtil.updateJenkinsJob(currentJobName, root.asXML());
			job = jenkinsUtil.getJenkinsJob(tblSystemJenkins.getJobPath(), currentJobName);
		}
		if (nowBuild) {
			jenkinsUtil.buildJenkinsJob(job);
		}
		return job.getNextBuildNumber();
	}
	
	/**
	 * ??????????????????????????????Jenkins????????????
	 */
	@Override
	public int getNextBuildNumber(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins, String jobName) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		int nextBuildNumber = 1;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			nextBuildNumber = jenkinsUtil.getNextBuildNumber(tblSystemJenkins.getJobPath(), jobName);
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
		return nextBuildNumber;
	}
	
	/**
	 * ??????Jenkins????????????????????????
	 */
	@Override
	public Map<String, String> getBuildLogByNumber(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins, String jobName, int jobNumber, 
			List<TblSystemModule> tblSystemModuleList) throws Exception {
		Map<String,String> logMap = new HashMap<String,String>();
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			String log = jenkinsUtil.getJenkinsLogByNumber(tblSystemJenkins.getJobPath(), jobName, jobNumber);
			if (log.indexOf("Finished: SUCCESS") != -1) {
				logMap.put("status", "2");
			} else {
				logMap.put("status", "3");
			}
			
			log = filterLog(log);
			log = sortLog(tblSystemModuleList, log);
			log = addColorToLog(log);
			log = log + "\njobNumber:" + jobNumber;
			logMap.put("log", log);
			jenkinsUtil.closeJenkins();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
		return logMap;
	}

	/**
	 * ??????????????????????????????
	 */
	@Override
	public Map<String,String> getLastBuildLog(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins, String jobName, 
			List<TblSystemModule> tblSystemModuleList) throws Exception {
		Map<String,String> logMap = new HashMap<String,String>();
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
//			JobWithDetails job = jenkinsUtil.getJenkinsJob(tblSystemJenkins.getJobPath(), jobName);
//			BuildResult result = jenkinsUtil.getJenkinsResult(job);
//			String log = jenkinsUtil.getJenkinsLog(job);
//			switch (result) {
//			case SUCCESS:
//				logMap.put("status", "2");
//				break;
//			default:
//				logMap.put("status", "3");
//				break;
//			}
			
			String log = jenkinsUtil.getJenkinsLog(tblSystemJenkins.getJobPath(), jobName);
			if (log.indexOf("Finished: SUCCESS") != -1) {
				logMap.put("status", "2");
			} else {
				logMap.put("status", "3");
			}
			
			log = filterLog(log);
			log = sortLog(tblSystemModuleList, log);
			log = addColorToLog(log);
			logMap.put("log", log);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
		return logMap;
	}
	
	/**
	 * ??????????????????
	 * @param log
	 * @return
	 */
	private String addColorToLog(String log) {
		if (StringUtil.isNotEmpty(log)) {
			StringBuffer moduleBuffer = new StringBuffer();
			String[] logArr = log.split("\n");
			if (logArr.length > 0) {
				String colorStartGreen = "<span style='color:#00FF00;'>";
				String colorStartRed = "<span style='color:#FF0000;'>";
				String colorStartYellow = "<span style='color:#FFFF00;'>";
				String colorEnd = "</span>";
				String tempStr = "";
				int afterLine = 1000;//???????????????????????????????????????????????????
				if (logArr.length > 1000) {
					afterLine = logArr.length - 300;
				}
				for (int i=0; i<logArr.length; i++) {
					tempStr = logArr[i];
					if (tempStr.indexOf("------[") != -1) {//?????????????????????????????????
						tempStr = colorStartGreen + tempStr + colorEnd;
					} else if (i > afterLine) {
						if (tempStr.indexOf("ERROR") != -1 || tempStr.indexOf("FAILURE") != -1 || tempStr.indexOf("ABORTED") != -1) {
							tempStr = colorStartRed + tempStr + colorEnd;
						} else if (tempStr.indexOf("SUCCESS") != -1) {
							tempStr = colorStartGreen + tempStr + colorEnd;
						}
					}
					moduleBuffer.append(tempStr).append("\n");
				}
			}
		}
		return log;
	}
	
	/**
	 * ?????????????????????????????????
	 * @param tblSystemModuleList 
	 * @param log
	 * @return
	 */
	private String sortLog(List<TblSystemModule> tblSystemModuleList, String log) {
		if (tblSystemModuleList != null && tblSystemModuleList.size() > 0 && log.indexOf("[Pipeline] parallel") != -1) {
			String mainStartStr = "";
			String[] logStartArr = log.split("\\[Pipeline\\] parallel\n");
			if (logStartArr.length == 2) {
				mainStartStr = logStartArr[0] + "[Pipeline] parallel\n";
			}
			String mainEndStr = "";
			String[] logEndArr = logStartArr[1].split("\\[Pipeline\\] // parallel\n");
			if (logEndArr.length == 2) {
				mainEndStr = "[Pipeline] // parallel\n" + logEndArr[1];
			}
			
			StringBuffer moduleBuffer = new StringBuffer();//????????????????????????
			if (logStartArr.length == 2 && logEndArr.length == 2) {
				String[] logArr = logEndArr[0].split("\n");//?????????????????????
				if (logArr.length > 0) {
					//??????????????????????????????????????????
					List<String> moduleNameList = new ArrayList<String>();
					String parallelTitle = null;
					Map<String, StringBuffer> bufferMap = new HashMap<String, StringBuffer>();
					for (TblSystemModule tblSystemModule : tblSystemModuleList) {
						parallelTitle = "[" + getParallelTitle(tblSystemModule) + "]";
						if (log.indexOf(parallelTitle) != -1) {
							moduleNameList.add(parallelTitle);
							bufferMap.put(parallelTitle, new StringBuffer());
						}
					}
					//??????????????????????????????????????????
					for (String str : logArr) {
						for (String moduleName : moduleNameList) {
							if (str.startsWith(moduleName) || str.startsWith("[Pipeline] " + moduleName)) {
								bufferMap.get(moduleName).append(str).append("\n");
								break;
							}
						}
					}
					//???????????????Map??????
					for (String moduleName : moduleNameList) {
						moduleBuffer.append(bufferMap.get(moduleName));
					}
				}
			}
			
			if (StringUtil.isNotEmpty(mainStartStr) && StringUtil.isNotEmpty(mainEndStr) && moduleBuffer.length() > 0) {
				log = mainStartStr + moduleBuffer.toString() + mainEndStr;
			}
		}
		return log;
	}

	/**
	 * ???????????????????????????????????????????????????????????? 
	 * @param log
	 * @return
	 */
	public static String filterLog(String log) {
		String filterLog = "";
		int saveSubstringA = 0;
		int saveSubstringB = 0;
		int beforeProgress = log.indexOf("Progress (");
		while (beforeProgress != -1) {
			int afterProgress = log.indexOf("BUILD SUCCESS", beforeProgress);
			if (afterProgress != -1) {
				int offset = afterProgress - beforeProgress;
				if (offset > 3000) {
					offset = 500;
					saveSubstringB = beforeProgress + offset;
					filterLog += log.substring(saveSubstringA, saveSubstringB);
					filterLog += "\n......\n......\n......\n";
					saveSubstringA = afterProgress - 4 * offset;
				} else {
					saveSubstringB = afterProgress;
					filterLog += log.substring(saveSubstringA, saveSubstringB);
					saveSubstringA = afterProgress;
				}
				beforeProgress = log.indexOf("Progress (", afterProgress);
				
			} else {
				break;
			}
		}
		if (StringUtil.isEmpty(filterLog)) {
			filterLog = log;
		} else  {
			filterLog += log.substring(saveSubstringA, log.length());
		}
		return filterLog;
	}
	
	/**
	 * ????????????Build???Jenkins?????????????????????
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
//	public String getJenkinsBuildingLog(TblToolInfo jenkinsTool, String jobName) throws Exception {
//		jenkinsUtil = getJenkinsUtil(jenkinsTool);
//		return jenkinsUtil.getJenkinsLog("", jobName);
//	}
	
	/**
	 * ????????????Build???Jenkins?????????????????????
	 * @param jobPath
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
//	public String getJenkinsBuildingLog(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins, String jobName) throws Exception {
//		String buildLog = "";
//		String msg = "";
//		if (jenkinsTool == null) {
//			msg = "error:getJenkinsBuildingLog, jenkinsTool is null";
//			log.error(msg);
//			buildLog = msg;
//		} else if (tblSystemJenkins == null) {
//			msg = "error:getJenkinsBuildingLog, tblSystemJenkins is null";
//			log.error(msg);
//			buildLog = msg;
//		} else {
//			jenkinsUtil = getJenkinsUtil(jenkinsTool);
//			buildLog = jenkinsUtil.getJenkinsLog(tblSystemJenkins.getJobPath(), jobName);
//		}
//		return buildLog;
//	}
	
	/**
	 * ????????????Build???Jenkins?????????????????????
	 * @param jobPath
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
	@Override
	public Map<String, String> getJenkinsBuildingLog(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins, String jobName, String start, int jobNumber) throws Exception {
		String msg = "";
		Map<String, String> resultMap = null;
		JenkinsUtil jenkinsUtil = null;
		try {
			if (jenkinsTool == null) {
				msg = "error:getJenkinsBuildingLog, jenkinsTool is null";
				log.error(msg);
			} else if (tblSystemJenkins == null) {
				msg = "error:getJenkinsBuildingLog, tblSystemJenkins is null";
				log.error(msg);
			} else {
				jenkinsUtil = getJenkinsUtil(jenkinsTool);
				resultMap = jenkinsUtil.getJenkinsBuildingLog(tblSystemJenkins.getJobPath(), jobName, start, jobNumber);
			}
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
		return resultMap;
	}
	
	/**
	 * ?????????????????????????????????????????????
	 * job/zhoudu_itmp_1_45_packagedeploy/32/input/Interrupt1/proceedEmpty
	 * @param url
	 */
	@Override
	public void continuePipeline(TblToolInfo jenkinsTool, String subUrl) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			jenkinsUtil.continuePipeline(subUrl);
			
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ???????????????JobName?????????Job???Parameter???????????????
	 */
	@Override
	public String getJobParameter(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins, String jobName) throws Exception {
		List<Map<String, Object>> parameterList = new ArrayList<Map<String, Object>>();
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			String configXml = jenkinsUtil.getConfigXml(tblSystemJenkins.getJobPath(), jobName);
			if (StringUtil.isNotEmpty(configXml)) {
				Element root = jenkinsUtil.getSAXElementByXml(configXml);
				Element parameterDefElement = root.element("properties").element(JenkinsUtil.PARAMETER_DEFINITION_PROPERTY);
				if (parameterDefElement != null && parameterDefElement.hasContent()) {
					Element definitionsElement = parameterDefElement.element(JenkinsUtil.PARAMETER_DEFINITIONS);
					List<Element> elementList = definitionsElement.elements();
					for (Element element : elementList) {
						String elementName = element.getName();
						Map<String, Object> parameterMap = new HashMap<String, Object>();
						String paraType = elementName.substring(elementName.lastIndexOf(".") + 1).replace("ParameterDefinition", "");
						parameterMap.put(JenkinsUtil.PARA_NAME, elementName);
						parameterMap.put(JenkinsUtil.PARA_TYPE, paraType);
						parameterMap.put(JenkinsUtil.PARA_TEXT_NAME, element.elementText(JenkinsUtil.PARA_TEXT_NAME));
						parameterMap.put(JenkinsUtil.PARA_DESCRIPTION, element.elementText(JenkinsUtil.PARA_DESCRIPTION));
						if (elementName.indexOf(JenkinsUtil.PARAMETER_TYPE_STRING) != -1
								|| elementName.indexOf(JenkinsUtil.PARAMETER_TYPE_TEXT) != -1) {
							parameterMap.put(JenkinsUtil.PARA_DEFAULT_VALUE, element.elementText(JenkinsUtil.PARA_DEFAULT_VALUE));
							parameterMap.put(JenkinsUtil.PARA_TRIM, element.elementText(JenkinsUtil.PARA_TRIM));
						} else if (elementName.indexOf(JenkinsUtil.PARAMETER_TYPE_BOOLEAN) != -1
								|| elementName.indexOf(JenkinsUtil.PARAMETER_TYPE_PASSWD) != -1) {
							parameterMap.put(JenkinsUtil.PARA_DEFAULT_VALUE, element.elementText(JenkinsUtil.PARA_DEFAULT_VALUE));
						} else if (elementName.indexOf(JenkinsUtil.PARAMETER_TYPE_CHOICE) != -1) {
							List<Element> choiceElementList = element.element("choices").element("a").elements();
							List<String> choiceList = new ArrayList<String>();
							for (Element choiceElement : choiceElementList) {
								choiceList.add(choiceElement.getText());
							}
							parameterMap.put("choices", choiceList);
						}
						parameterList.add(parameterMap);
					}
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
		String parameterJson = JSON.toJSONString(parameterList);
		return parameterJson;
	}
	
	/**
	 * ????????????????????????????????????
	 * @param jenkinsTool
	 * @param jobName
	 * @throws Exception
	 */
	@Override
	public void deleteJobCallback(TblToolInfo jenkinsTool,TblSystemJenkins tblSystemJenkins, String jobName) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			String configXml = jenkinsUtil.getConfigXml(tblSystemJenkins.getJobPath(), jobName);
			if (StringUtil.isNotEmpty(configXml)) {
				// ???config.xml??????????????????
				configXml = deleteJobCallbackXml(configXml);
				jenkinsUtil.updateJenkinsJob(tblSystemJenkins.getJobPath(), jobName, configXml);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ????????????
	 * @param jenkinsTool
	 * @param jobName
	 * @throws Exception
	 */
	@Override
	public void stopBuilding(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins, String jobName) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			jenkinsUtil.stopJenkinsBuilding(tblSystemJenkins.getJobPath(), jobName);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ?????????????????????????????????
	 * @param jenkinsTool
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
//	@Override
//	public boolean isJenkinsBuilding(TblToolInfo jenkinsTool, String jobName) throws Exception {
//		return isJenkinsBuilding(jenkinsTool, null, jobName);
//	}
	
	/**
	 * ?????????????????????????????????
	 * @param jenkinsTool
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
//	@Override
//	public boolean isJenkinsBuilding(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins, String jobName) throws Exception {
//		try {
//			jenkinsUtil = getJenkinsUtil(jenkinsTool);
//			String jobPath = "";
//			if (tblSystemJenkins != null ) {
//				jobPath = tblSystemJenkins.getJobPath();
//			}
//			return jenkinsUtil.isJenkinsBuilding(jobPath, jobName);
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			throw e;
//		}
//	}
	
	/**
	 * ?????????????????????????????????
	 * @param jenkinsTool
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean isJenkinsBuilding(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins, String jobName, int jobNumber) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			String jobPath = "";
			if (tblSystemJenkins != null ) {
				jobPath = tblSystemJenkins.getJobPath();
			}
			return jenkinsUtil.isJenkinsBuilding(jobPath, jobName, jobNumber);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ??????Jenkins??????????????????
	 * @param jenkinsTool
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean existJob(TblToolInfo jenkinsTool, String jobName) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			return jenkinsUtil.existJenkinsJob(jobName);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ??????Jenkins???Cron?????????
	 * @param jenkinsTool
	 * @param jobName
	 * @throws Exception
	 */
	@Override
	public Map<String, String> validateCron(TblToolInfo jenkinsTool, String cron) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			String msg =   jenkinsUtil.validateJenkinsCron(cron);
			if (msg.indexOf("class=ok") != -1) {
				map.put("status", "1");
			} else {
				map.put("status", "0");
				map.put("message", getMatcher(validateCronPattern, msg));
			}
			
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
		return map;
	}
	
	/**
	 *  ????????????????????????
	 *  ??????tblSystemJenkins?????????????????????????????????????????????????????????
	 * @param jenkinsTool
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
	@Override
	public Timestamp getJobStartDate(TblToolInfo jenkinsTool, String jobName) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		try {
			String jobPath = "";
//			TblSystemJenkins tblSystemJenkins = null;
//			if (tblSystemJenkins != null ) {
//				jobPath = tblSystemJenkins.getJobPath();
//			}
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			return jenkinsUtil.getJenkinsJobStartDate(jobPath, jobName, 0);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	@Override
	public Timestamp getJobStartDate(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins, String jobName, int jobNumber) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		try {
			String jobPath = "";
			if (tblSystemJenkins != null ) {
				jobPath = tblSystemJenkins.getJobPath();
			}
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			return jenkinsUtil.getJenkinsJobStartDate(jobPath, jobName, jobNumber);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	@Override
	public Timestamp getJobEndDate(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins, String jobName, int jobNumber) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		try {
			String jobPath = "";
			if (tblSystemJenkins != null ) {
				jobPath = tblSystemJenkins.getJobPath();
			}
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			return jenkinsUtil.getJenkinsJobEndDate(jobPath, jobName, jobNumber);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	
	/**
	 *  ??????Jenkins Job
	 * @param jenkinsTool
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
	@Override
	public void deleteJob(TblToolInfo jenkinsTool, String jobName) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		try {
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			jenkinsUtil.deleteJenkinsJob(jobName);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ??????Jenkins???StageView????????????
	 */
	@Override
	public String getStageViewDescribeJson(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins, String jobName, int jobNumber) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		try {
			String jobPath = "";
			if (tblSystemJenkins != null ) {
				jobPath = tblSystemJenkins.getJobPath();
			}
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			return jenkinsUtil.getStageViewDescribeJson(jobPath, jobName, jobNumber);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ??????Jenkins???????????????
	 */
	@Override
	public String getNextPendingInputAction(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins, String jobName, int jobNumber) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		try {
			String jobPath = "";
			if (tblSystemJenkins != null ) {
				jobPath = tblSystemJenkins.getJobPath();
			}
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			return jenkinsUtil.getNextPendingInputAction(jobPath, jobName, jobNumber);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ??????????????????????????????
	 */
	@Override
	public void getStageViewNextPending(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins, 
			String jobName, int jobNumber, String interruptId, Integer flag) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		try {
			String jobPath = "";
			if (tblSystemJenkins != null ) {
				jobPath = tblSystemJenkins.getJobPath();
			}
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			jenkinsUtil.getStageViewNextPending(jobPath, jobName, jobNumber, interruptId, flag);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ??????StageView????????????execution??????
	 */
	@Override
	public String getStageViewDescribeExecutionJson(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins,
			String jobName, int jobNumber, int describeId) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		try {
			String jobPath = "";
			if (tblSystemJenkins != null ) {
				jobPath = tblSystemJenkins.getJobPath();
			}
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			return jenkinsUtil.getStageViewDescribeExecutionJson(jobPath, jobName, jobNumber, describeId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ??????StageView????????????execution?????????????????????
	 */
	@Override
	public String getStageViewExecutionLogJson(TblToolInfo jenkinsTool, TblSystemJenkins tblSystemJenkins,
			String jobName, int jobNumber, int executionId) throws Exception {
		JenkinsUtil jenkinsUtil = null;
		try {
			String jobPath = "";
			if (tblSystemJenkins != null ) {
				jobPath = tblSystemJenkins.getJobPath();
			}
			jenkinsUtil = getJenkinsUtil(jenkinsTool);
			return jenkinsUtil.getStageViewExecutionLogJson(jobPath, jobName, jobNumber, executionId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			if (jenkinsUtil != null) {
				jenkinsUtil.closeJenkins();
			}
		}
	}
	
	/**
	 * ???????????????Config??????????????????????????????
	 * @param paramMap 
	 * @param configXml
	 * @return
	 */
	private Map<String ,Object> getPluginMsgWithConfigXml(JenkinsUtil jenkinsUtil, Map paramMap, String configXml) throws Exception {
		Long systemSonarId = paramMap.get("systemSonarId") == null ? null : Long.parseLong(paramMap.get("systemSonarId").toString());
		Map<String ,Object> pluginMap = new HashMap<String ,Object>();
		Element root = jenkinsUtil.getSAXElementByXml(configXml);
		String valueTemp = "";
		TblSystemSonar sonarBean = null;
		String sonarName = null;
		//???????????????
		if (configXml.indexOf(JenkinsUtil.JOB_TYPE_WORKFLOW) != -1
				|| configXml.indexOf(JenkinsUtil.JOB_TYPE_WORKFLOW_MULTIBRANCH) != -1) {
			if (configXml.indexOf("definition") != -1 && configXml.indexOf("<script>") != -1) {
				String script = root.element("definition").element("script").getText();
				//Sonar
				Matcher matcher = sonarNamePattern.matcher(script); 
				while(matcher.find()) { 
					valueTemp = matcher.group(1);
					valueTemp = valueTemp.replaceAll("'|\"", "");
					sonarName = valueTemp;
					sonarBean = new TblSystemSonar();
					sonarBean.setId(systemSonarId);
					sonarBean.setSonarProjectKey(getMatcher(sonarProjectKeyPattern, script));
					sonarBean.setSonarProjectName(getMatcher(sonarProjectNamePattern, script));
					sonarBean.setSonarSources(getMatcher(sonarSourcesPattern, script));
					sonarBean.setSonarJavaBinaries(getMatcher(sonarBinariesPattern, script));
				}
			}
			
		} else {
			//Sonar
			Element buildersElement = root.element("builders");//?????????
			if (buildersElement == null) {
				buildersElement = root.element("postbuilders");//??????Maven
			}
			if (buildersElement == null) {
				buildersElement = root.element("prebuilders");//??????Maven
			}
			if (buildersElement != null) {
				Element sonarElement = buildersElement.element("hudson.plugins.sonar.SonarRunnerBuilder");
				if (sonarElement != null) {
					sonarBean = new TblSystemSonar();
					Element sonarNameElement = sonarElement.element("installationName");
					if (sonarNameElement != null) {
						sonarName = sonarNameElement.getText();
					} else {
						sonarName = "";
					}
					String  propertiesText = sonarElement.element("properties").getText();
					sonarBean.setId(systemSonarId);
					sonarBean.setSonarProjectKey(getMatcher(sonarProjectKeyPattern, propertiesText));
					sonarBean.setSonarProjectName(getMatcher(sonarProjectNamePattern, propertiesText));
					sonarBean.setSonarSources(getMatcher(sonarSourcesPattern, propertiesText));
					sonarBean.setSonarJavaBinaries(getMatcher(sonarBinariesPattern, propertiesText));
				}
			}
		}
		pluginMap.put("sonarName", sonarName);
		pluginMap.put("tblSystemSonar", sonarBean);
		return pluginMap;
	}
	
	private String getMatcher(Pattern pattern, String text) {
		String value = null;
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			value = matcher.group(1);
		}
		return value;
	}
	
	/**
	 * ????????????????????????config.xml
	 * @param configXml
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	private String insertJobCallbackXml(JenkinsUtil jenkinsUtil, Map paramMap, Map<String ,Object> pluginMap, String configXml) throws Exception {
		Element root = jenkinsUtil.getSAXElementByXml(configXml);
		String callback = getCallbackManualParameter(paramMap, pluginMap, configXml);
		//???????????????
		if (configXml.indexOf(JenkinsUtil.JOB_TYPE_WORKFLOW) != -1
				|| configXml.indexOf(JenkinsUtil.JOB_TYPE_WORKFLOW_MULTIBRANCH) != -1) {
			if (configXml.indexOf("definition") != -1 && configXml.indexOf("<script>") != -1) {
				Element scriptElement = root.element("definition").element("script");
				String script = assembleCallbackScript(scriptElement.getText(), callback);
				scriptElement.setText(script);
			}
			
		} else {//??????????????????
			Element buildersElement = root.element("builders");
			if (buildersElement == null) {
				buildersElement = root.element("postbuilders");
			}
			if (buildersElement != null) {
				buildersElement.addElement(JenkinsUtil.SHELL_NAME).addElement(JenkinsUtil.SHELL_COMMAND).addText(callback);
			}
		}
		return root.asXML();
	}

	private String assembleCallbackScript(String script, String callback) {
		callback = "sh '''" + callback + "'''";
		int offsetIndex = -1;
		if (script.indexOf("pipeline") != -1 && script.indexOf("stages") != -1) {//?????????Pieple
			//TODO ?????????????????????
			String[] arr = script.split("post[\\s\\S]*?\\{");
			if (arr.length > 1) {//??????script?????????post??????
				arr = script.split("always[\\s\\S]*?\\{");
				if (arr.length > 1) {//??????script?????????always??????
					offsetIndex = script.lastIndexOf("always");
				} else {
					offsetIndex = script.lastIndexOf("post");
					callback = " always('itmp_callback_shell'){" + callback + "}";
				}
				offsetIndex = offsetIndex + script.substring(offsetIndex).indexOf("{");
				if (offsetIndex != -1) {
					script = script.substring(0, offsetIndex + 1) + callback + script.substring(offsetIndex + 1);
				}
			} else {
				callback = "post('itmp_callback_shell'){always{" + callback + "}}";
				offsetIndex = script.lastIndexOf("}");
				if (offsetIndex != -1) {
					script = script.substring(0, offsetIndex) + callback + script.substring(offsetIndex);
				}
			}
		} else {
			String[] arr = script.split("node[\\s\\S]*?\\{");
			if (arr.length > 1) {
				offsetIndex = script.indexOf("node");
				offsetIndex = offsetIndex + script.substring(offsetIndex).indexOf("{");
				script = script.substring(0, offsetIndex + 1) + "\n\n ";
				script += "def startDateCallback = new Date().format('yyyy-MM-dd HH:mm:ss')\n";
				script += "try{\n  echo '" + scriptInsertMsg + "'\n\n" + script.substring(offsetIndex + 1);
				offsetIndex = script.lastIndexOf("}");
				String catchStr = "\n}catch(callback_shell_ex){\n";
				catchStr += "  echo '" + scriptInsertMsg + "'\n";
				catchStr += "  echo '????????????......'\n";
				catchStr += "  throw callback_shell_ex\n";
				catchStr += "} finally {" + callback + "}\n\n";
				if (offsetIndex != -1) {
					script = script.substring(0, offsetIndex) + catchStr + script.substring(offsetIndex);
				}
			}
		}
		return script;
	}
	
	private String deleteJobCallbackXml(String configXml) {
		//???????????????
		if (configXml.indexOf(JenkinsUtil.JOB_TYPE_WORKFLOW) != -1
				|| configXml.indexOf(JenkinsUtil.JOB_TYPE_WORKFLOW_MULTIBRANCH) != -1) {
			if (configXml.indexOf("pipeline") != -1 && configXml.indexOf("stages") != -1) {//?????????Pieple
				if (configXml.indexOf("post('itmp_callback_shell')") != -1) {
					configXml = configXml.replaceAll("post\\('itmp_callback_shell'\\)\\{always\\{[\\s\\S]+?\\}\\}", "");
				} else if (configXml.indexOf("always('itmp_callback_shell')") != -1) {
					configXml = configXml.replaceAll("always\\('itmp_callback_shell'\\)\\{[\\s\\S]+?\\}", "");
				} else {
					configXml = configXml.replaceAll("sh '''[\\s\\S]+?" + callBackManualJenkins + "[\\s\\S]+?'''", "");
				}
			} else {
				configXml = configXml.replaceAll("def startDateCallback[\\s\\S]+?try\\{[\\s\\S]+?echo '" + scriptInsertMsg + "'", "");
				configXml = configXml.replaceAll("\\}catch\\(callback_shell_ex\\)\\{[\\s\\S]+?\\} finally \\{sh '''[\\s\\S]+?'''\\}", "");
			}
		} else {//??????????????????
			configXml = configXml.replaceAll("<hudson.tasks.Shell>\\s*?<command>[\\s\\S]+?curl.+?" + callBackManualJenkins + "[\\s\\S]+?</hudson.tasks.Shell>", "");
		}
		//???????????????????????????????????????????????????????????????????????????
		configXml = configXml.replaceAll("[\\r\\n]{1,2}\\s*[\\r\\n]{1,2}\\s*[\\r\\n]{1,2}\\s*[\\r\\n]{1,2}", "\r\n\r\n");
		return configXml;
	}
	
	/**
	 * ???????????????Parameter???????????????Jenkins???Config.xml????????????
	 */
	public String updateJobParameterXml(JenkinsUtil jenkinsUtil, List<Map<String, Object>> parameterList, String configXml) throws Exception {
		if (StringUtil.isNotEmpty(configXml) && parameterList != null && parameterList.size() > 0) {
			Element root = jenkinsUtil.getSAXElementByXml(configXml);
			Element propertiesElement = root.element("properties");
			Element parameterDefElement = propertiesElement.element(JenkinsUtil.PARAMETER_DEFINITION_PROPERTY);
			if (parameterDefElement != null) {
				Element definitionsElement = parameterDefElement.element(JenkinsUtil.PARAMETER_DEFINITIONS);
				//??????ParametersDefinitionProperty????????????????????????
				assembleParameterProperty(definitionsElement, parameterList);
			} 
			configXml = root.asXML();
		}
		return configXml;
	}
	
	/**
	 * ??????ParametersDefinitionProperty????????????????????????
	 * @param propertiesElement
	 * @param parameterList 
	 */
	private void assembleParameterProperty(Element definitionsElement, List<Map<String, Object>> parameterList) {
		
		for (Map<String, Object> paramMap : parameterList) {
			String elementName = paramMap.get(JenkinsUtil.PARA_NAME) == null ? "" : paramMap.get(JenkinsUtil.PARA_NAME).toString();
			String name = paramMap.get(JenkinsUtil.PARA_TEXT_NAME) == null ? "" : paramMap.get(JenkinsUtil.PARA_TEXT_NAME).toString();
			String description = paramMap.get(JenkinsUtil.PARA_DESCRIPTION) == null ? "" : paramMap.get(JenkinsUtil.PARA_DESCRIPTION).toString();
			
			Element paraElement = getExistParameterElement(elementName, name, definitionsElement);
			if (paraElement != null) {
				paraElement.element(JenkinsUtil.PARA_TEXT_NAME).setText(name);
				paraElement.element(JenkinsUtil.PARA_DESCRIPTION).setText(description);
				
				if (elementName.indexOf(JenkinsUtil.PARAMETER_TYPE_STRING) != -1
						|| elementName.indexOf(JenkinsUtil.PARAMETER_TYPE_TEXT) != -1) {
					String defaultValue = paramMap.get(JenkinsUtil.PARA_DEFAULT_VALUE) == null ? "" : paramMap.get(JenkinsUtil.PARA_DEFAULT_VALUE).toString();
					String trim = paramMap.get(JenkinsUtil.PARA_TRIM) == null ? "" : paramMap.get(JenkinsUtil.PARA_TRIM).toString();
					paraElement.element(JenkinsUtil.PARA_DEFAULT_VALUE).setText(defaultValue);
					paraElement.element(JenkinsUtil.PARA_TRIM).setText(trim);
				} else if (elementName.indexOf(JenkinsUtil.PARAMETER_TYPE_BOOLEAN) != -1
						|| elementName.indexOf(JenkinsUtil.PARAMETER_TYPE_PASSWD) != -1) {
					String defaultValue = paramMap.get(JenkinsUtil.PARA_DEFAULT_VALUE) == null ? "" : paramMap.get(JenkinsUtil.PARA_DEFAULT_VALUE).toString();
					paraElement.element(JenkinsUtil.PARA_DEFAULT_VALUE).setText(defaultValue);
				} else if (elementName.indexOf(JenkinsUtil.PARAMETER_TYPE_CHOICE) != -1) {
					List<String> choiceList = paramMap.get("choices") == null ? null : (List<String>)paramMap.get("choices");
					Element aElement = paraElement.element("choices").element("a");
					for (String choice : choiceList) {
						aElement.element("string").setText(choice);
					}
				}
			}
		}
	}
	
	/**
	 * ?????????????????????????????????Job??????????????????
	 * @param elementName
	 * @param name
	 * @param definitionsElement
	 * @return
	 */
	private Element getExistParameterElement(String elementName, String name, Element definitionsElement) {
		List<Element> existElementList =  definitionsElement.elements(elementName);
		Element existElement = null;
		for (Element element : existElementList) {
			if (name.equals(element.elementText(JenkinsUtil.PARA_TEXT_NAME))) {
				existElement = element;
				break;
			}
		}
		return existElement;
	}

	/**
	 * ????????????????????????
	 * ????????????maven/pipeline/?????????????????????
	 * @param daysToKeep ?????????????????????
	 * @param numToKeep ???????????????????????????
	 * @param artifactDaysToKeep ?????????????????????
	 * @param artifactNumToKeep ?????????????????????#?????????
	 * @param propertiesElement
	 */
	private void assembleRemoveOldData(int daysToKeep, int numToKeep, int artifactDaysToKeep, int artifactNumToKeep, Element propertiesElement) {
		Element buildDiscarderElement = propertiesElement.element("jenkins.model.BuildDiscarderProperty");
		if (buildDiscarderElement != null) {
			Element strategyElement = buildDiscarderElement.element("strategy");
			if (daysToKeep > 0) {
				strategyElement.element("daysToKeep").setText(String.valueOf(daysToKeep));
			}
			if (numToKeep > 0) {
				strategyElement.element("numToKeep").setText(String.valueOf(numToKeep));
			}
			if (artifactDaysToKeep > 0) {
				strategyElement.element("artifactDaysToKeep").setText(String.valueOf(artifactDaysToKeep));
			}
			if (artifactNumToKeep > 0) {
				strategyElement.element("artifactNumToKeep").setText(String.valueOf(artifactNumToKeep));
			}
		} else {
			Element strategyElement = propertiesElement.addElement("jenkins.model.BuildDiscarderProperty").addElement("strategy");
			strategyElement.addElement("daysToKeep").setText(String.valueOf(daysToKeep));
			strategyElement.addElement("numToKeep").setText(String.valueOf(numToKeep));
			strategyElement.addElement("artifactDaysToKeep").setText(String.valueOf(artifactDaysToKeep));
			strategyElement.addElement("artifactNumToKeep").setText(String.valueOf(artifactNumToKeep));
		}
	}
	
	/**
	 * ???????????????????????????????????????
	 * @param tblSystemJenkins
	 * @param scheduled
	 * @param configXml
	 * @param root
	 */
	private void assembleTimerTrigger(TblSystemJenkins tblSystemJenkins, String scheduled, String configXml, Element root) {
		if (StringUtil.isNotEmpty(scheduled) && scheduled.equals("true") && StringUtil.isNotEmpty(tblSystemJenkins.getJobCron())) {//????????????
			//???????????????
			if (configXml.indexOf(JenkinsUtil.JOB_TYPE_WORKFLOW) != -1
					|| configXml.indexOf(JenkinsUtil.JOB_TYPE_WORKFLOW_MULTIBRANCH) != -1) {
				Element propertiesElement = root.element("properties");
				Element pipelineTriggersElement = getElementOrNew("org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty", propertiesElement);
				Element triggersElement = getElementOrNew("triggers", pipelineTriggersElement);
				Element timerTriggerElement = getElementOrNew("hudson.triggers.TimerTrigger", triggersElement);
				Element specElement = getElementOrNew("spec", timerTriggerElement);
				specElement.setText(tblSystemJenkins.getJobCron());
			} else {
				Element triggersElement = getElementOrNew("triggers", root);
				Element timerTriggerElement = getElementOrNew("hudson.triggers.TimerTrigger", triggersElement);
				Element specElement = getElementOrNew("spec", timerTriggerElement);
				specElement.setText(tblSystemJenkins.getJobCron());
			}
			
		} else {//?????????????????????????????????XML????????????????????????
			//???????????????
			if (configXml.indexOf(JenkinsUtil.JOB_TYPE_WORKFLOW) != -1
					|| configXml.indexOf(JenkinsUtil.JOB_TYPE_WORKFLOW_MULTIBRANCH) != -1) {
				Element propertiesElement = root.element("properties");
				Element pipelineTriggersElement = propertiesElement.element("org.jenkinsci.plugins.workflow.job.properties.PipelineTriggersJobProperty");
				if (pipelineTriggersElement != null) {
					Element triggersElement = pipelineTriggersElement.element("triggers");
					if (triggersElement != null) {
						removeElement("hudson.triggers.TimerTrigger", triggersElement);
					}
				}
			} else {
				Element triggersElement = root.element("triggers");
				if (triggersElement != null) {
					removeElement("hudson.triggers.TimerTrigger", triggersElement);
				}
			}
		}
	}
	
	/**
	 * ??????Element?????????????????????????????????????????????
	 * @param eName
	 * @param parentElement
	 * @return
	 */
	private Element getElementOrNew(String eName, Element parentElement) {
		Element eNameElement = parentElement.element(eName);
		if (eNameElement == null) {
			eNameElement = parentElement.addElement(eName);
		}
		return eNameElement;
	}
	private void removeElement(String eName, Element parentElement) {
		Element eNameElement = parentElement.element(eName);
		if (eNameElement != null) {
			parentElement.remove(eNameElement);
		}
	}

	/**
	 * pipeline ???Shell????????????????????????
	 * @param scriptSB
	 * @param blankCount
	 */
	private void assembleParameter(Map paramMap, TblSystemInfo tblSystemInfo, StringBuilder scriptSB, int blankCount) {
		String sonarflag = (String)paramMap.get("sonarflag");//1?????????2??????
		String envPath = "";
		String envMsg = "";
		if (StringUtil.isNotEmpty(tblSystemInfo.getJdkVersion())) {
			scriptSB.append(getPreBlank(blankCount)).append("jdk = tool '" + tblSystemInfo.getJdkVersion() + "' \n");
			envPath += "${jdk}/bin:";
			envMsg += " + jdk + '\\n'";
		}
		if (StringUtil.isNotEmpty(sonarflag) && sonarflag.equals("1")) {//???????????????????????????Sonar????????????
			scriptSB.append(getPreBlank(blankCount)).append("sonar = tool 'Sonar' \n");
			envPath += "${sonar}/bin:";
			envMsg += " + sonar + '\\n'";
		}
		
		if (tblSystemInfo.getBuildType() != null && StringUtil.isNotEmpty(tblSystemInfo.getBuildToolVersion())) {
			if (tblSystemInfo.getBuildType() == 1) { //Maven
				scriptSB.append(getPreBlank(blankCount)).append("mvnHome = tool 'Maven" + tblSystemInfo.getBuildToolVersion() + "' \n");
				envPath += "${mvnHome}/bin:";
				envMsg += " + mvnHome + '\\n'";
			} else {//???Maven????????????nexus?????????
				scriptSB.append(getPreBlank(blankCount)).append("mvnHome = tool 'maven' \n");
				envPath += "${mvnHome}/bin:";
				envMsg += " + mvnHome + '\\n'";
			}
			if (tblSystemInfo.getBuildType() == 2) { //Ant
				scriptSB.append(getPreBlank(blankCount)).append("antHome = tool 'Ant" + tblSystemInfo.getBuildToolVersion() + "' \n");
				envPath += "${antHome}/bin:";
				envMsg += " + antHome + '\\n'";
			}
			if (tblSystemInfo.getBuildType() == 3 || tblSystemInfo.getBuildType() == 4) { //NodeJs Gulp
				scriptSB.append(getPreBlank(blankCount)).append("nodeHome = '/app/node" + tblSystemInfo.getBuildToolVersion() + "' \n");
				envPath += "${nodeHome}/bin:";
				envMsg += " + nodeHome + '\\n'";
			}
		}
		scriptSB.append(getPreBlank(blankCount)).append("groovy = tool 'Groovy' \n");
		envPath += "${groovy}/bin:";
		envMsg += " + groovy + '\\n'";
		if (envPath.indexOf("${jdk}") != -1) {
			scriptSB.append(getPreBlank(blankCount)).append("env.JAVA_HOME = \"${jdk}\" \n");
			//env.CLASSPATH = ".:${jdk}/lib/tools.jar:${jdk}/lib/dt.jar"
		}
		scriptSB.append(getPreBlank(blankCount)).append("env.PATH = \"" + envPath + "${env.PATH}\" \n");
		scriptSB.append(getPreBlank(blankCount)).append("echo 'ENVIRONMENT:\\n'" + envMsg + "\n");
	}
	
	/**
	 * pipeline ???Shell????????????????????????:???????????????
	 * @param tblSystemModule
	 * @param scriptSB
	 * @param blankCount
	 */
	private void assembleModuleParameter(TblSystemModule tblSystemModule, StringBuilder scriptSB, int blankCount) {
		String envPath = "";
		if (StringUtil.isNotEmpty(tblSystemModule.getJdkVersion())) {
			scriptSB.append(getPreBlank(blankCount)).append("jdk = tool '" + tblSystemModule.getJdkVersion() + "' \n");
			envPath += "${jdk}/bin:";
		}
		
		if (tblSystemModule.getBuildType() != null && StringUtil.isNotEmpty(tblSystemModule.getBuildToolVersion())) {
			if (tblSystemModule.getBuildType() == 1) { //Maven
				scriptSB.append(getPreBlank(blankCount)).append("mvnHome = tool 'Maven" + tblSystemModule.getBuildToolVersion() + "' \n");
				envPath += "${mvnHome}/bin:";
			}
			if (tblSystemModule.getBuildType() == 2) { //Ant
				scriptSB.append(getPreBlank(blankCount)).append("antHome = tool 'Ant" + tblSystemModule.getBuildToolVersion() + "' \n");
				envPath += "${antHome}/bin:";
			}
			if (tblSystemModule.getBuildType() == 3 || tblSystemModule.getBuildType() == 4) { //NodeJs Gulp
				scriptSB.append(getPreBlank(blankCount)).append("nodeHome = '/app/node" + tblSystemModule.getBuildToolVersion() + "' \n");
				envPath += "${nodeHome}/bin:";
			}
		}
		if (StringUtil.isNotEmpty(envPath)) {
			scriptSB.append(getPreBlank(blankCount)).append("env.PATH = \"" + envPath + "${env.PATH}\" \n");
		}
	}

	/**
	 * ??????GIT/SVN???XML ??????Pipeline
	 * @param tblSystemScm
	 * @param scriptSB
	 */
	private void assembleSourceSCM(Map<String, Object> paramMap, TblSystemInfo tblSystemInfo,List<TblSystemModule> tblSystemModuleList, TblSystemScm tblSystemScm, 
			List<TblSystemModuleScm> tblSystemModuleScmList, TblToolInfo sourceTool, List<TblToolInfo> sourceToolList, StringBuilder scriptSB, int blankCount) {
		if (tblSystemInfo.getArchitectureType() == 2) { //1=??????????????????2=????????????
			StringBuilder tempSB = new StringBuilder();
			if (tblSystemScm.getScmType() == 1) {// ?????????????????????1:GIT???2:SVN???
				String branch = tblSystemScm.getScmBranch();
				if (StringUtil.isEmpty(branch)) {
					branch = "master";
				}
				String relativeDir = "";
				tempSB.append(getPreBlank(blankCount)).append("stage('checkout SCM'){\n");
				showScriptTitle(CHECKOUT_TITLE, 0, SUFFIX_TITLE_2, tempSB, blankCount + 1);
				tempSB.append(getPreBlank(blankCount + 1)).append("checkout([\n");
				tempSB.append(getPreBlank(blankCount + 2)).append("$class: 'GitSCM', branches: [[name: '*/").append(branch).append("']], \n");
				tempSB.append(getPreBlank(blankCount + 2)).append("doGenerateSubmoduleConfigurations: false, submoduleCfg: [], \n");
				tempSB.append(getPreBlank(blankCount + 2)).append("extensions: [").append(relativeDir).append("], \n");
				tempSB.append(getPreBlank(blankCount + 2)).append("userRemoteConfigs: [[\n");
				tempSB.append(getPreBlank(blankCount + 3)).append("credentialsId: '").append(sourceTool.getJenkinsCredentialsId()).append("', \n");
				tempSB.append(getPreBlank(blankCount + 3)).append("url: '").append(assembleScmUrl(tblSystemScm.getScmUrl())).append("' \n");
				tempSB.append(getPreBlank(blankCount + 2)).append("]], \n");
				tempSB.append(getPreBlank(blankCount + 1)).append("])\n");
				tempSB.append(getPreBlank(blankCount + 1)).append("echo '").append("???????????????????????? ......'\n");
				tempSB.append(getPreBlank(blankCount)).append("}\n");
			} else {
				String branch = tblSystemScm.getScmBranch();
				if (StringUtil.isEmpty(branch)) {
					branch = ".";
				}
				tempSB.append(getPreBlank(blankCount)).append("stage('checkout SCM'){\n");
				showScriptTitle(CHECKOUT_TITLE, 0, SUFFIX_TITLE_2, tempSB, blankCount + 1);
				tempSB.append(getPreBlank(blankCount + 1)).append("checkout([\n");
				tempSB.append(getPreBlank(blankCount + 2)).append("$class:'SubversionSCM',additionalCredentials:[],excludedCommitMessages:'',excludedRegions:'',excludedRevprop:'',\n");
				tempSB.append(getPreBlank(blankCount + 2)).append("excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', \n");
				tempSB.append(getPreBlank(blankCount + 2)).append("locations: [[\n");
				tempSB.append(getPreBlank(blankCount + 3)).append("cancelProcessOnExternalsFail: true, \n");
				tempSB.append(getPreBlank(blankCount + 3)).append("credentialsId: '").append(sourceTool.getJenkinsCredentialsId()).append("', \n");
				tempSB.append(getPreBlank(blankCount + 3)).append("depthOption: 'infinity', ignoreExternalsOption: true, \n");
				tempSB.append(getPreBlank(blankCount + 3)).append("local: '").append(branch).append("', \n");
				tempSB.append(getPreBlank(blankCount + 3)).append("remote: '").append(assembleScmUrl(tblSystemScm.getScmUrl())).append("@HEAD'\n");
				tempSB.append(getPreBlank(blankCount + 2)).append("]], \n");
				tempSB.append(getPreBlank(blankCount + 2)).append("quietOperation: true, workspaceUpdater: [$class: 'UpdateWithRevertUpdater']\n");
				tempSB.append(getPreBlank(blankCount + 1)).append("])\n");
				tempSB.append(getPreBlank(blankCount + 1)).append("echo '").append("???????????????????????? ......'\n");
				tempSB.append(getPreBlank(blankCount)).append("}\n");
			}
			scriptSB.append(tempSB);
		} else if (tblSystemModuleList != null) { //???????????????
//			boolean existEmptyAndNoDependency = false;//?????????????????????????????????????????????????????????????????????
			for (TblSystemModuleScm tblSystemModuleScm : tblSystemModuleScmList) {
				for (TblSystemModule tblSystemModule : tblSystemModuleList) {
					if (tblSystemModule.getId() == tblSystemModuleScm.getSystemModuleId().longValue()) {
						String currentScmUrl = assembleScmUrl(tblSystemModuleScm.getScmUrl());
						String nodeKey = tblSystemModule.getSystemModuleFlag();
						if (StringUtil.isEmpty(nodeKey)) {
//							if (tblSystemModule.getBuildDependency() == null || tblSystemModule.getBuildDependency() == 2) {//???????????????1:?????????2:????????????
//								existEmptyAndNoDependency = true;
//							}
							nodeKey = rootNodeKey;
						}
						
						if (StringUtil.isNotEmpty(currentScmUrl)) {
							for (TblToolInfo tblToolInfo : sourceToolList) {
								if (tblSystemModuleScm.getToolId() == tblToolInfo.getId().longValue()) {
									sourceTool = tblToolInfo;
									break;
								}
							}
							
							StringBuilder tempSB = new StringBuilder();
							blankCount = this.assembleNodeStart(tblSystemModule, tempSB, blankCount);//???????????????
							String branch = tblSystemModuleScm.getScmBranch();
							if (tblSystemModuleScm.getScmType() == 1) {// ?????????????????????1:GIT???2:SVN???
								if (StringUtil.isEmpty(branch)) {
									branch = "master";
								}
								String relativeDir = "[$class: 'RelativeTargetDirectory', relativeTargetDir: '" + tblSystemModuleScm.getScmRepositoryName() + "']";
								tempSB.append(getPreBlank(blankCount)).append("stage('checkout SCM'){\n");
								showScriptTitle(CHECKOUT_TITLE, 0, SUFFIX_TITLE_2, tempSB, blankCount + 1);
								tempSB.append(getPreBlank(blankCount + 1)).append("checkout([\n");
								tempSB.append(getPreBlank(blankCount + 2)).append("$class: 'GitSCM', branches: [[name: '*/").append(branch).append("']], \n");
								tempSB.append(getPreBlank(blankCount + 2)).append("doGenerateSubmoduleConfigurations: false, submoduleCfg: [], \n");
								tempSB.append(getPreBlank(blankCount + 2)).append("extensions: [").append(relativeDir).append("], \n");
								tempSB.append(getPreBlank(blankCount + 2)).append("userRemoteConfigs: [[\n");
								tempSB.append(getPreBlank(blankCount + 3)).append("credentialsId: '").append(sourceTool.getJenkinsCredentialsId()).append("', \n");
								tempSB.append(getPreBlank(blankCount + 3)).append("url: '").append(currentScmUrl).append("' \n");
								tempSB.append(getPreBlank(blankCount + 2)).append("]]\n");
								tempSB.append(getPreBlank(blankCount + 1)).append("])\n");
								tempSB.append(getPreBlank(blankCount + 1)).append("echo '").append("???????????????????????? ......'\n");
								tempSB.append(getPreBlank(blankCount)).append("}\n");
							} else {
								if (StringUtil.isEmpty(branch)) {
									branch = ".";
								}
								tempSB.append(getPreBlank(blankCount)).append("stage('checkout SCM'){\n");
								showScriptTitle(CHECKOUT_TITLE, 0, SUFFIX_TITLE_2, tempSB, blankCount + 1);
								tempSB.append(getPreBlank(blankCount + 1)).append("checkout([\n");
								tempSB.append(getPreBlank(blankCount + 2)).append("$class:'SubversionSCM',additionalCredentials:[],excludedCommitMessages:'',excludedRegions:'',excludedRevprop:'',\n");
								tempSB.append(getPreBlank(blankCount + 2)).append("excludedUsers: '', filterChangelog: false, ignoreDirPropChanges: false, includedRegions: '', \n");
								tempSB.append(getPreBlank(blankCount + 2)).append("locations: [[\n");
								tempSB.append(getPreBlank(blankCount + 3)).append("cancelProcessOnExternalsFail: true, \n");
								tempSB.append(getPreBlank(blankCount + 3)).append("credentialsId: '").append(sourceTool.getJenkinsCredentialsId()).append("', \n");
								tempSB.append(getPreBlank(blankCount + 3)).append("depthOption: 'infinity', ignoreExternalsOption: true, \n");
								tempSB.append(getPreBlank(blankCount + 3)).append("local: '").append(branch).append("', \n");
								tempSB.append(getPreBlank(blankCount + 3)).append("remote: '").append(currentScmUrl).append("@HEAD'\n");
								tempSB.append(getPreBlank(blankCount + 2)).append("]], \n");
								tempSB.append(getPreBlank(blankCount + 2)).append("quietOperation: true, workspaceUpdater: [$class: 'UpdateWithRevertUpdater']\n");
								tempSB.append(getPreBlank(blankCount + 1)).append("])\n");
								tempSB.append(getPreBlank(blankCount + 1)).append("echo '").append("???????????????????????? ......'\n");
								tempSB.append(getPreBlank(blankCount)).append("}\n");
							}
							blankCount = this.assembleNodeEnd(tblSystemModule, tempSB, blankCount);//???????????????
						}
						break;
					}
				}
			}
//			if (!existEmptyAndNoDependency) {//??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
//				nodeParallelMap.remove(rootNodeKey);
//			}
			
//			Map<String, Object> tempMap = new HashMap<String, Object>();
//			tempMap.put("tblSystemDeployList", paramMap.get("tblSystemDeployList"));
//			assebmleNodeMap2ScriptSB(tblSystemModuleList, nodeMap, tempMap, false, scriptSB, blankCount);//?????????????????????scriptSB
		}
	}

	/**
	 * ??????scm url
	 * @param scmUrl
	 * @return
	 */
	private String assembleScmUrl(String scmUrl) {
		if (StringUtil.isNotEmpty(scmUrl)) {
			if (scmUrl.indexOf(":80/") != -1) {//Jenkins svn???80???????????????????????????????????????????????????
				scmUrl = scmUrl.replaceFirst(":80", "");
			}
		}
		return scmUrl;
	}

	/**
	 * ??????Maven?????????pom.xml goal??? ??????Pipeline
	 * @param tblSystemInfo
	 * @param tblSystemJenkins
	 * @param tblSystemModuleList
	 * @param nexusToolInfo
	 * @param artType ??????????????????1???snapshots,2???releases
	 * @param version
	 * @param buildDeployType 1?????????2?????????
	 * @param sonarflag ??????sonar??????1???2???
	 * @param scriptSB
	 * @param blankCount
	 */
	private void assembleCompile(Map<String, Object> paramMap, TblSystemInfo tblSystemInfo, TblSystemJenkins tblSystemJenkins, List<TblSystemModule> tblSystemModuleList, 
			TblToolInfo nexusToolInfo, String artType, String  version, int buildDeployType, StringBuilder scriptSB, int blankCount) {
		if (tblSystemInfo.getBuildType() != null && tblSystemInfo.getArchitectureType() != null) {
			
			String command = "";
			if (tblSystemInfo.getArchitectureType() == 2) {//????????????mvn clean deploy -Dmaven.test.skip=true
				if (StringUtil.isEmpty(artType)) {//????????????
					command = StringUtil.isEmpty(tblSystemInfo.getCompileCommand()) ? defaultMavenBuild : tblSystemInfo.getCompileCommand();
				} else { //??????????????????
					command = assembleDeployNexus(paramMap, tblSystemInfo, tblSystemJenkins, null, nexusToolInfo, artType, version);
				}
				command= assembleCommand(paramMap, command, buildDeployType, tblSystemInfo.getBuildType());
				
				scriptSB.append(getPreBlank(blankCount)).append("stage('build ").append(tblSystemInfo.getSystemCode()).append("'){\n");
				showScriptTitle(tblSystemInfo.getSystemCode() + " " + BUILD_TITLE, 0, SUFFIX_TITLE_2, scriptSB, blankCount + 1);
				scriptSB.append(getPreBlank(blankCount + 1)).append("sh '''").append(command).append("'''\n");
				assembleSystemPackageName(paramMap, tblSystemInfo, null, scriptSB, blankCount + 1);
				scriptSB.append(getPreBlank(blankCount + 1)).append("echo '").append(tblSystemInfo.getSystemCode()).append(" ?????????????????? ......'\n");
				scriptSB.append(getPreBlank(blankCount)).append("}\n");
			} else if (tblSystemModuleList != null) {//???????????????
				/**????????????????????????pom***/
				StringBuilder parentSB = new StringBuilder();
				this.assembleFirstCompileModuleCommand(tblSystemModuleList, parentSB, blankCount);
				
				/**???????????????pom***/
				StringBuilder mustSB = new StringBuilder();
				for (TblSystemModule tblSystemModule : tblSystemModuleList) {
					if (tblSystemModule.getBuildDependency() == 1) {//????????????
						assembleModuleCompile(paramMap, tblSystemInfo, tblSystemModule, tblSystemJenkins, nexusToolInfo, artType, 
								version, buildDeployType, mustSB, blankCount);
					}
				}
				
				for (TblSystemModule tblSystemModule : tblSystemModuleList) {
					if (tblSystemModule.getBuildDependency() == 2) {//???????????????
						StringBuilder tempSB = new StringBuilder();
						blankCount = this.assembleNodeStart(tblSystemModule, tempSB, blankCount);//???????????????
						assembleModuleCompile(paramMap, tblSystemInfo, tblSystemModule, tblSystemJenkins, nexusToolInfo, artType,
								version, buildDeployType, tempSB, blankCount);
						blankCount = this.assembleNodeEnd(tblSystemModule, tempSB, blankCount);//???????????????
					}
				}
				jenkinsParameterMap.put("parentStr", parentSB.toString());
				jenkinsParameterMap.put("mustStr", mustSB.toString());
//				tempMap.put("tblSystemDeployList", paramMap.get("tblSystemDeployList"));
//				assebmleNodeMap2ScriptSB(tblSystemModuleList, nodeMap, tempMap, true, scriptSB, blankCount);//?????????????????????scriptSB
			}
			
		} 
	}

	private void assembleModuleCompile(Map<String, Object> paramMap, TblSystemInfo tblSystemInfo, 
			TblSystemModule tblSystemModule, TblSystemJenkins tblSystemJenkins,TblToolInfo nexusToolInfo, 
			String artType, String version, int buildDeployType, StringBuilder scriptSB, int blankCount) {
		String command = "";
		scriptSB.append(getPreBlank(blankCount)).append("stage('build ").append(tblSystemModule.getModuleCode()).append("'){\n");
		showScriptTitle(tblSystemModule.getModuleCode() + " " + BUILD_TITLE, 0, SUFFIX_TITLE_2, scriptSB, blankCount + 1);
		blankCount = this.assembleAOPStart(paramMap, tblSystemModule, scriptSB, blankCount);
		scriptSB.append(getPreBlank(blankCount + 1)).append("dir(path:'").append(tblSystemModule.getRelativePath()).append("'){\n");
		
		assembleModuleParameter(tblSystemModule, scriptSB, blankCount + 2);//????????????????????????????????????
		if (StringUtil.isEmpty(artType)) {//????????????
			String mavenBuild = defaultMavenBuild;
			if (tblSystemModule.getBuildDependency() == 1) {//???????????????1:?????????2:????????????
				mavenBuild = defaultMavenBuildMust;
			}
			if (StringUtil.isEmpty(tblSystemModule.getCompileCommand())) {
				command = StringUtil.isEmpty(tblSystemInfo.getCompileCommand()) ? mavenBuild : tblSystemInfo.getCompileCommand();
			} else {
				command = tblSystemModule.getCompileCommand();
			}
		} else { //??????????????????
			command = assembleDeployNexus(paramMap, tblSystemInfo, tblSystemJenkins, tblSystemModule, nexusToolInfo, artType, version);
		}
		int buildType = tblSystemModule.getBuildType() == null ? tblSystemInfo.getBuildType() : tblSystemModule.getBuildType();
		command= assembleCommand(paramMap, command, buildDeployType, buildType);
		scriptSB.append(getPreBlank(blankCount + 2)).append("sh '''").append(command).append("'''\n");
		
		assembleSystemPackageName(paramMap, tblSystemInfo, tblSystemModule, scriptSB, blankCount + 2);
		scriptSB.append(getPreBlank(blankCount + 2)).append("echo '").append(tblSystemModule.getModuleCode()).append(" ?????????????????? ......'\n");
		scriptSB.append(getPreBlank(blankCount + 1)).append("}\n");
		blankCount = this.assembleAOPEnd(paramMap, tblSystemModule, scriptSB, blankCount);
		scriptSB.append(getPreBlank(blankCount)).append("}\n");
	}
	
	/**
	 * ????????????pom,????????????????????????????????????mvn clean install -N
	 * @param tblSystemModuleList
	 * @param scriptTemp
	 * @param blankCount
	 */
	private void assembleFirstCompileModuleCommand(List<TblSystemModule> tblSystemModuleList, StringBuilder scriptSB, int blankCount) {
		StringBuilder scriptTemp = new StringBuilder();
		for (TblSystemModule tblSystemModule : tblSystemModuleList) {
			String path = tblSystemModule.getRelativePath();
			path = JenkinsUtil.addSlash(path, "/|\\\\", "/", false);
			String[] pathArr = path.split("/");
			if (tblSystemModule.getBuildType() == 1) {//1=MAVEN???2=ANT...
				if (pathArr.length == 1) {
					String stageNameRoot = "build root parent";
					if (scriptTemp.indexOf(stageNameRoot) == -1) {
						scriptTemp.append(getPreBlank(blankCount)).append("stage('").append(stageNameRoot).append("'){\n");
						assemblePomJudge(scriptTemp, blankCount + 1);
						scriptTemp.append(getPreBlank(blankCount + 1)).append("if(pomExist == 'pom.xml'){\n");
						assembleModuleParameter(tblSystemModule, scriptTemp, blankCount + 2);//????????????????????????????????????
						scriptTemp.append(getPreBlank(blankCount + 2)).append("sh '''").append(parentMavenBuild).append("'''\n");
						scriptTemp.append(getPreBlank(blankCount + 1)).append("}else{\n");
						scriptTemp.append(getPreBlank(blankCount + 2)).append("echo 'no parent pom.xml'\n");
						scriptTemp.append(getPreBlank(blankCount + 1)).append("}\n");
						scriptTemp.append(getPreBlank(blankCount)).append("}\n");
					}
				} else if (pathArr.length > 1) {
					String stageNameChild = path.substring(0, path.lastIndexOf("/"));
					if (scriptTemp.indexOf(stageNameChild) == -1) {
						scriptTemp.append(getPreBlank(blankCount)).append("stage('").append(stageNameChild).append("'){\n");
						scriptTemp.append(getPreBlank(blankCount + 1)).append("dir(path:'").append(stageNameChild).append("'){\n");
						assemblePomJudge(scriptTemp, blankCount + 2);
						scriptTemp.append(getPreBlank(blankCount + 2)).append("if(pomExist == 'pom.xml'){\n");
						assembleModuleParameter(tblSystemModule, scriptTemp, blankCount + 3);//????????????????????????????????????
						scriptTemp.append(getPreBlank(blankCount + 3)).append("sh '''").append(parentMavenBuild).append("'''\n");
						scriptTemp.append(getPreBlank(blankCount + 2)).append("}else{\n");
						scriptTemp.append(getPreBlank(blankCount + 3)).append("echo 'no parent pom.xml'\n");
						scriptTemp.append(getPreBlank(blankCount + 2)).append("}\n");
						scriptTemp.append(getPreBlank(blankCount + 1)).append("}\n");
						scriptTemp.append(getPreBlank(blankCount)).append("}\n");
					}
				}
			}
		}
		scriptSB.append(scriptTemp.toString());
	}
	
	/**
	 * ????????????????????????pom.xml??????
	 * @param scriptSB
	 * @param blankCount
	 */
	private void assemblePomJudge(StringBuilder scriptSB, int blankCount) {
		scriptSB.append(getPreBlank(blankCount)).append("sh 'ls|grep ^pom.xml\\$ > commandResult &'\n");
		scriptSB.append(getPreBlank(blankCount)).append("pomExist").append(" = readFile('commandResult').trim()\n");
		scriptSB.append(getPreBlank(blankCount)).append("sh 'rm -rf commandResult'\n");
	}

	/**
	 * ???Pipeline??????????????????,???????????????????????????
	 * @param tblSystemInfo
	 * @param tblSystemModule
	 * @param scriptSB
	 * @param blankCount
	 */
	private void assembleSystemPackageName(Map paramMap, TblSystemInfo tblSystemInfo, TblSystemModule tblSystemModule,
			StringBuilder scriptSB, int blankCount) {
		if (paramMap.get("isPROAutoDeploy") != null) {//???????????????
			String artifactId = tblSystemInfo.getArtifactId();
			String groupId = tblSystemInfo.getGroupId();
			String packageSuffix = tblSystemInfo.getPackageSuffix();
			if (tblSystemModule != null) {
				if (StringUtil.isNotEmpty(tblSystemModule.getArtifactId())) {
					artifactId = tblSystemModule.getArtifactId();
				}
				if (StringUtil.isNotEmpty(tblSystemModule.getGroupId())) {
					groupId = tblSystemModule.getGroupId();
				}
				if (StringUtil.isNotEmpty(tblSystemModule.getPackageSuffix())) {
					packageSuffix = tblSystemModule.getPackageSuffix();
				}
			}
//			scriptSB.append(getPreBlank(blankCount)).append("sh 'mkdir target").append(shellNoPrint).append("'\n");
			if (tblSystemInfo.getArchitectureType() == 2) {//????????????
				jenkinsParameterMap.put(this.systemCode, tblSystemInfo.getSystemCode());//systemPackageName???Pipeline?????????
				scriptSB.append(getPreBlank(blankCount)).append("sh 'ls target|grep ^").append(artifactId).append(".*").append(packageSuffix).append("\\$ > commandResult &'\n");
				scriptSB.append(getPreBlank(blankCount)).append(this.systemPackageName).append(" = readFile('commandResult').trim()\n");
			} else if (tblSystemModule != null) {
				List<String> checkModuleList = (List<String>)paramMap.get("checkModuleList");
				for (String checkModuleId : checkModuleList) {
					if (checkModuleId.equals(tblSystemModule.getId().toString())) {
						String subSystemCode = (String)jenkinsParameterMap.get(this.subSystemCode);//subSystemPackageName???Pipeline?????????
						if (StringUtil.isEmpty(subSystemCode)) {
							subSystemCode = tblSystemModule.getModuleCode();
						} else {
							subSystemCode += "," + tblSystemModule.getModuleCode();
						}
						jenkinsParameterMap.put(this.subSystemCode, subSystemCode);
						scriptSB.append(getPreBlank(blankCount)).append("sh 'ls target|grep ^").append(artifactId).append(".*").append(packageSuffix).append("\\$ > commandResult &'\n");
						scriptSB.append(getPreBlank(blankCount)).append(this.subSystemPackageName).append(" += readFile('commandResult').trim() + ','\n");
						break;
					}
				}
			}
			scriptSB.append(getPreBlank(blankCount)).append("sh 'rm -rf commandResult'\n");
		}
	}

	/**
	 * ??????????????????????????????????????????
	 * @param command
	 * @param buildDeployType 1?????????2?????????
	 * @param buildType
	 * @return
	 */
	private String assembleCommand(Map<String, Object> paramMap, String command, int buildDeployType, int buildType) {
		String sonarflag = (String)paramMap.get("sonarflag");//1?????????2??????
		if (buildType == 1) {//1=Maven???2=Ant???3=NodeJs 4=Gulp...
//			mvn clean install -Dmaven.test.skip=true
			if (buildDeployType == 1 && "1".equals(sonarflag)) {//buildDeployType 1?????????2?????????
				if (command.indexOf(" clean ") == -1) {
					command = command.replaceAll(" install", " clean org.jacoco:jacoco-maven-plugin:0.8.3:prepare-agent install -Dmaven.test.failure.ignore=true");
					command = command.replaceAll(" package", " clean org.jacoco:jacoco-maven-plugin:0.8.3:prepare-agent package -Dmaven.test.failure.ignore=true");
				} else {
					command = command.replaceAll(" install", " org.jacoco:jacoco-maven-plugin:0.8.3:prepare-agent install -Dmaven.test.failure.ignore=true");
					command = command.replaceAll(" package", " org.jacoco:jacoco-maven-plugin:0.8.3:prepare-agent package -Dmaven.test.failure.ignore=true");
				}
			}
			if (buildDeployType == 2 && command.indexOf("mvn ") != -1 && command.indexOf("-Dmaven.test.skip") == -1) {
				command = command.replaceFirst(" install", " install -Dmaven.test.skip=true");
				command = command.replaceFirst(" package", " package -Dmaven.test.skip=true");
			}
			
		} else if (buildType == 2) {
		} else if (buildType == 3) {
		} else if (buildType == 4) {
		} else {
		}
		return command;
	}

	/**
	 * mvn install -Dattr.packaging=war -Dattr.version=1.1.1 deploy:deploy-file -DgroupId=com.test -DartifactId=test -Dpackaging=war -Dversion=1.1.1-SNAPSHOT 
	 * -Dfile=target/eureka-1.1.1.war -Durl=http://192.168.1.145:8081/nexus/repository/maven-snapshots/ -DrepositoryId=snapshots
	 * @param paramMap 
	 * @param version 
	 * @param artType 
	 * @param tblSystemJenkins 
	 * @param tblSystemInfo 
	 * @param nexusToolInfo 
	 * @param i 
	 * @param tblSystemModuleList 
	 * @return
	 */
	private String assembleDeployNexus(Map<String, Object> paramMap, TblSystemInfo tblSystemInfo, TblSystemJenkins tblSystemJenkins, TblSystemModule tblSystemModule, 
			TblToolInfo nexusToolInfo, String artType, String version) {
		String artifactId = tblSystemInfo.getArtifactId();
		String groupId = tblSystemInfo.getGroupId();
		String packageSuffix = tblSystemInfo.getPackageSuffix();
		String snapshotRepositoryName = tblSystemInfo.getSnapshotRepositoryName();
		String releaseRepositoryName = tblSystemInfo.getReleaseRepositoryName();
		String compileCommand = tblSystemInfo.getCompileCommand();
		if (tblSystemModule != null) {
			if (StringUtil.isNotEmpty(tblSystemModule.getArtifactId())) {
				artifactId = tblSystemModule.getArtifactId();
			}
			if (StringUtil.isNotEmpty(tblSystemModule.getGroupId())) {
				groupId = tblSystemModule.getGroupId();
			}
			if (StringUtil.isNotEmpty(tblSystemModule.getPackageSuffix())) {
				packageSuffix = tblSystemModule.getPackageSuffix();
			}
			if (StringUtil.isNotEmpty(tblSystemModule.getSnapshotRepositoryName())) {
				snapshotRepositoryName = tblSystemModule.getSnapshotRepositoryName();
			}
			if (StringUtil.isNotEmpty(tblSystemModule.getReleaseRepositoryName())) {
				releaseRepositoryName = tblSystemModule.getReleaseRepositoryName();
			}
			if (StringUtil.isNotEmpty(tblSystemModule.getCompileCommand())) {
				compileCommand = tblSystemModule.getCompileCommand();
			}
		}
		
		StringBuffer sb = new StringBuffer();
		NexusUtil nexus = new NexusUtil(nexusToolInfo);
		if (tblSystemModule != null && tblSystemModule.getBuildDependency() == 1) {//????????????????????????????????????????????????
			List<String> checkModuleList = (List<String>)paramMap.get("checkModuleList");
			sb.append("mvn clean install -Dmaven.test.skip=true -Dattr.packaging=").append(packageSuffix).append(" -Dattr.version=").append(version).append("\n");
			for (String checkModuleId : checkModuleList) {
				if (checkModuleId.equals(tblSystemModule.getId().toString())) {
					sb.append("mvn deploy:deploy-file -q");
					sb.append(" -DgroupId=").append(groupId);
					sb.append(" -DartifactId=").append(artifactId);
					sb.append(" -Dpackaging=").append(packageSuffix);
					if ("1".equals(artType)) {//snapshots
						sb.append(" -Dversion=").append(version).append("-SNAPSHOT");
						sb.append(" -Durl=").append(nexus.getBaseUrl()).append("repository/").append(snapshotRepositoryName).append("/");
						sb.append(" -DrepositoryId=").append(deployNexusSnapshotsId);
					} else {
						sb.append(" -Dversion=").append(version);
						sb.append(" -Durl=").append(nexus.getBaseUrl()).append("repository/").append(releaseRepositoryName).append("/");
						sb.append(" -DrepositoryId=").append(deployNexusReleasesId);
					}
					sb.append(" -Dfile=target/").append(artifactId).append("-").append(version).append(".").append(packageSuffix);
					break;
				}
			}
		} else {//?????????????????????CD?????????SQL???????????????
			String zipPackage = artifactId + "-" + version + ".zip";
			if ((tblSystemModule != null && tblSystemModule.getBuildType() == 1) || 
					tblSystemModule == null && tblSystemInfo.getBuildType() == 1) { //Maven??????
				sb.append("mvn clean package -Dmaven.test.skip=true -Dattr.packaging=").append(packageSuffix).append(" -Dattr.version=").append(version).append("\n");
			} else {//???Maven??????
				sb.append(compileCommand).append("\n");
			}
			sb.append("rm -rf CD-ZIP").append("\n");
			sb.append("mkdir -p CD-ZIP/configuration CD-ZIP/sql CD-ZIP/package").append("\n");
			sb.append("if [ -d \"configuration/\" ];then ").append("cp -rf configuration CD-ZIP").append("; else echo \"\"; fi").append("\n");
			sb.append("if [ -d \"sql/\" ];then ").append("cp -rf sql CD-ZIP").append("; else echo \"\"; fi").append("\n");
			sb.append("cp -rf target/*.").append(packageSuffix).append(" ").append("CD-ZIP/package/").append("\n");
			sb.append("cd CD-ZIP").append("\n");
			sb.append("find . -type d -iname '.svn' -exec rm -rf {} \\\\; > /dev/null 2>&1 & ").append("\n");
			sb.append("sleep 2").append("\n");
			sb.append("zip -rq ").append(zipPackage).append(" * \n");
			
			sb.append("mvn deploy:deploy-file -q");
			sb.append(" -DgroupId=").append(groupId);
			sb.append(" -DartifactId=").append(artifactId);
			sb.append(" -Dpackaging=zip");
			if ("1".equals(artType)) {//snapshots
				sb.append(" -Dversion=").append(version).append("-SNAPSHOT");
				sb.append(" -Durl=").append(nexus.getBaseUrl()).append("repository/").append(snapshotRepositoryName).append("/");
				sb.append(" -DrepositoryId=").append(deployNexusSnapshotsId);
			} else {
				sb.append(" -Dversion=").append(version);
				sb.append(" -Durl=").append(nexus.getBaseUrl()).append("repository/").append(releaseRepositoryName).append("/");
				sb.append(" -DrepositoryId=").append(deployNexusReleasesId);
			}
			sb.append(" -Dfile=").append(zipPackage);
		}
		
		return sb.toString();
	}

	/**
	 * pipeline??????Sonar?????????
	 * Jenkins sonarQube Name:sonar-test
	 * Jenkins SonarQube Scanner Name:sonar Scanner-test
	 * @param tblSystemJenkins 
	 * @param
	 * @param scriptSB
	 */
	private void assembleSonar(Map<String,Object>paramMap, TblSystemInfo tblSystemInfo, TblSystemScm tblSystemScm, List<TblSystemModuleScm> tblSystemModuleScmList, TblToolInfo sourceTool, 
			List<TblToolInfo> tblSonarToolList, List<TblSystemModule> tblSystemModuleList, List<TblSystemSonar> tblSystemSonarList, StringBuilder scriptSB, int blankCount) {
		TblToolInfo sonarTool = null;
		if (tblSonarToolList != null && tblSonarToolList.size() > 0) {
			sonarTool = tblSonarToolList.get(0);
		}
		
		if (tblSystemSonarList != null) {
			if (tblSystemInfo.getArchitectureType() == 2) { //1=??????????????????2=????????????
				for (int i = 0; i < tblSystemSonarList.size(); i++) {
					TblSystemSonar tblSystemSonar = tblSystemSonarList.get(i);
					scriptSB.append(getPreBlank(blankCount)).append("stage('sonar analysis ").append(tblSystemInfo.getSystemCode()).append("'){\n");
					showScriptTitle(tblSystemInfo.getSystemCode() + " " + SONAR_TITLE, 0, SUFFIX_TITLE_2, scriptSB, blankCount + 1);
					assembleSonar(sourceTool, sonarTool, tblSystemScm, null, tblSystemSonar, scriptSB, blankCount + 1);
					scriptSB.append(getPreBlank(blankCount + 1)).append("echo '").append(tblSystemInfo.getSystemCode()).append(" sonar???????????? ......'\n");
					scriptSB.append(getPreBlank(blankCount)).append("}\n");
					break;
				}
			} else if (tblSystemModuleList != null) {//???????????????
				TblSystemModule tblSystemModule = new TblSystemModule();
				for (int i = 0; i < tblSystemSonarList.size(); i++) {
					TblSystemSonar tblSystemSonar = tblSystemSonarList.get(i);
					for (TblSystemModule moduleTemp : tblSystemModuleList) {
						if (tblSystemSonar.getSystemModuleId() == moduleTemp.getId().longValue()) {
							tblSystemModule = moduleTemp;
							break;
						}
					}
					
					for (TblSystemModuleScm tblSystemModuleScm : tblSystemModuleScmList) {
						if (tblSystemModuleScm.getSystemModuleId() == tblSystemModule.getId().longValue()) {
							StringBuilder tempSB = new StringBuilder();
							blankCount = this.assembleNodeStart(tblSystemModule, tempSB, blankCount);//???????????????
							tempSB.append(getPreBlank(blankCount)).append("stage('sonar analysis ").append(tblSystemModule.getModuleCode()).append("'){\n");
							showScriptTitle(tblSystemModule.getModuleCode() + " " + SONAR_TITLE, 0, SUFFIX_TITLE_2, scriptSB, blankCount + 1);
							blankCount = this.assembleAOPStart(paramMap, tblSystemModule, tempSB, blankCount);
							tempSB.append(getPreBlank(blankCount + 1)).append("dir(path:'").append(tblSystemModule.getRelativePath()).append("'){\n");
							assembleSonar(sourceTool, sonarTool, tblSystemScm, tblSystemModuleScm, tblSystemSonar, tempSB, blankCount + 2);
							tempSB.append(getPreBlank(blankCount + 1)).append("}\n");
							tempSB.append(getPreBlank(blankCount + 1)).append("echo '").append(tblSystemModule.getModuleCode()).append(" sonar???????????? ......'\n");
							blankCount = this.assembleAOPEnd(paramMap, tblSystemModule, tempSB, blankCount);
							tempSB.append(getPreBlank(blankCount)).append("}\n");
							blankCount = this.assembleNodeEnd(tblSystemModule, tempSB, blankCount);//???????????????
							break;
						}
					}
				}
//				Map<String, Object> tempMap = new HashMap<String, Object>();
//				tempMap.put("tblSystemDeployList", paramMap.get("tblSystemDeployList"));
//				assebmleNodeMap2ScriptSB(tblSystemModuleList, nodeMap, tempMap, false, scriptSB, blankCount);//?????????????????????scriptSB
			}
		}
	}

	/**
	 * Sonar????????????
	 * @param sourceTool
	 * @param sonarTool
	 * @param tblSystemScm
	 * @param tblSystemSonar
	 * @param scriptSB
	 * @param blankCount
	 */
	private void assembleSonar(TblToolInfo sourceTool, TblToolInfo sonarTool, TblSystemScm tblSystemScm, TblSystemModuleScm tblSystemModuleScm, TblSystemSonar tblSystemSonar, 
			StringBuilder scriptSB, int blankCount) {
		StringBuilder sonarSB = new StringBuilder();
		Integer scmType = tblSystemScm.getScmType();
		if (tblSystemModuleScm != null) {
			scmType = tblSystemModuleScm.getScmType();
		}
		
		sonarSB.append("sonar.projectKey=").append(tblSystemSonar.getSonarProjectKey()).append("\n")
		.append("sonar.projectName=").append(tblSystemSonar.getSonarProjectName()).append("\n")
		.append("sonar.projectVersion=1.0\n")
		.append("sonar.sources=.\n")
		.append("sonar.test=.\n")
		.append("sonar.test.inclusions=**/*Test*/**\n")
		.append("sonar.exclusions=**/*Test*/**\n")
		.append("sonar.java.binaries=.\n");
		if (scmType == 1) {//Git
			sonarSB.append("sonar.scm.disabled=true\n");
		} else if (scmType == 2) {//SVN:
			sonarSB.append("sonar.svn.username=").append(sourceTool.getUserName()).append("\n");
			sonarSB.append("sonar.svn.password.secured=").append(sourceTool.getPassword()).append("\n");
			sonarSB.append("sonar.scm.disabled=true\n");
		}
		
		scriptSB.append(getPreBlank(blankCount)).append("sh '''\n");//??????'???????????????????????????
		scriptSB.append("cat>sonar-project.properties<<eof\n");
		scriptSB.append(sonarSB.toString());
		scriptSB.append("eof\n");//???????????????????????????????????????
		scriptSB.append(getPreBlank(blankCount)).append("'''\n");
		
		//sonar-scanner??????????????????
		StringBuilder scannerParam = new StringBuilder();
		if (sonarTool != null) {
			if (StringUtil.isNotEmpty(sonarTool.getIp()) && StringUtil.isNotEmpty(sonarTool.getPort())
					&& StringUtil.isNotEmpty(sonarTool.getUserName()) && StringUtil.isNotEmpty(sonarTool.getPassword())) {
				String url = "http://" + sonarTool.getIp() + ":" + sonarTool.getPort();// http://localhost:8087
				scannerParam.append(" -Dsonar.host.url=").append(url);
				scannerParam.append(" -Dsonar.login=").append(sonarTool.getUserName());
				scannerParam.append(" -Dsonar.password=").append(sonarTool.getPassword());
			}
		}
		scriptSB.append(getPreBlank(blankCount)).append("sh 'sonar-scanner").append(scannerParam.toString()).append("'\n");
		scriptSB.append(getPreBlank(blankCount)).append("sh 'rm -rf expect.sh'\n");
	}
	
	/**
	 * ???????????????????????????????????????????????????
	 * @param tblSystemScm
	 * @param scriptSB
	 */
	private void assembleRemoveWorksapce(StringBuilder scriptSB, int blankCount) {
		scriptSB.append(getPreBlank(blankCount)).append("stage('remove workspace'){\n");
		scriptSB.append(getPreBlank(blankCount + 1)).append("sh 'rm -rf ${WORKSPACE}/*").append("'\n");
		scriptSB.append(getPreBlank(blankCount)).append("}\n");
	}
	
	/**
	 * ???Nexus????????????????????????????????????????????????
	 * curl --noproxy 10.1.12.38 -x 10.1.27.102:8080 -O http://10.1.12.38:8081/nexus/repository/maven-releases/cn/pioneerservice/eureka/0.0.1/eureka-0.0.1.jar
	 * wms-0.0.1-SNAPSHOT.war
	 * @param nexusTool
	 * @param tblSystemModuleList
	 * @param tblArtifactInfoList
	 * @param scriptSB
	 * @param i
	 */
	private void downloadPackage(Map<String, Object> paramMap, TblSystemInfo tblSystemInfo, TblToolInfo nexusTool, List<TblSystemModule> tblSystemModuleList,
			List<TblArtifactInfo> tblArtifactInfoList, StringBuilder scriptSB, int blankCount) {
		String url = "http://" + nexusTool.getIp() + ":" + nexusTool.getPort();
		if (tblArtifactInfoList != null && tblArtifactInfoList.size() > 0) {
			String packageSuffix = tblSystemInfo.getPackageSuffix();
			if (tblSystemInfo.getArchitectureType() == 2) { //1=??????????????????2=????????????
				TblArtifactInfo tblArtifactInfo = tblArtifactInfoList.get(0);
				String nexusPath = tblArtifactInfo.getNexusPath();
				String[] nexusPathArr = nexusPath.split("/");
				String downloadPackageName = nexusPathArr[nexusPathArr.length - 1]; 
				scriptSB.append(getPreBlank(blankCount)).append("stage('download ").append(tblSystemInfo.getSystemCode()).append("'){\n");
				showScriptTitle(tblSystemInfo.getSystemCode() + " " + DOWNLOAD_PACKAGE_TITLE, 0, SUFFIX_TITLE_2, scriptSB, blankCount + 1);
				scriptSB.append(getPreBlank(blankCount + 1)).append("sh 'rm -rf target' \n");
				scriptSB.append(getPreBlank(blankCount + 1))
				.append("sh 'curl -o target/" + downloadPackageName + " --create-dirs " + url + nexusTool.getContext() + "repository/" + tblArtifactInfo.getRepository() + "/" + nexusPath + "'\n");
				scriptSB.append(getPreBlank(blankCount + 1)).append("sleep 5 \n");
				scriptSB.append(getPreBlank(blankCount + 1)).append("echo '???????????????????????? ......'\n");
				scriptSB.append(getPreBlank(blankCount)).append("}\n");
				String deployPackageName = tblArtifactInfo.getArtifactId() + "-" + tblArtifactInfo.getVersion() + "." + packageSuffix;
				jenkinsParameterMap.put(this.systemCode, tblSystemInfo.getSystemCode());
				jenkinsParameterMap.put(this.systemPackageName, deployPackageName);
			} else if (tblSystemModuleList != null) {//???????????????
				String subSystemCode = "";
				String subSystemPackageName = "";
				for (TblSystemModule tblSystemModule : tblSystemModuleList) {
					for (TblArtifactInfo tblArtifactInfo : tblArtifactInfoList) {
						if (tblSystemModule.getId().equals(tblArtifactInfo.getSystemModuleId())) {
							if (StringUtil.isNotEmpty(tblSystemModule.getPackageSuffix())) {
								packageSuffix = tblSystemModule.getPackageSuffix();
							}
							String nexusPath = tblArtifactInfo.getNexusPath();
							String[] nexusPathArr = nexusPath.split("/");
							String downloadPackageName = nexusPathArr[nexusPathArr.length - 1]; 
							StringBuilder tempSB = new StringBuilder();
							blankCount = this.assembleNodeStart(tblSystemModule, tempSB, blankCount);//???????????????
							tempSB.append(getPreBlank(blankCount)).append("stage('download ").append(tblSystemModule.getModuleCode()).append("'){\n");
							showScriptTitle(tblSystemModule.getModuleCode() + " " + DOWNLOAD_PACKAGE_TITLE, 0, SUFFIX_TITLE_2, scriptSB, blankCount + 1);
							blankCount = this.assembleAOPStart(paramMap, tblSystemModule, tempSB, blankCount);
							tempSB.append(getPreBlank(blankCount + 1)).append("dir(path:'").append(tblSystemModule.getRelativePath()).append("'){\n");
							tempSB.append(getPreBlank(blankCount + 2)).append("sh 'rm -rf target' \n");
							tempSB.append(getPreBlank(blankCount + 2))
							.append("sh 'curl -o target/" + downloadPackageName + " --create-dirs " + url + nexusTool.getContext() + "repository/" + tblArtifactInfo.getRepository() + "/" + nexusPath + "'\n");
							tempSB.append(getPreBlank(blankCount + 1)).append("}\n");
							tempSB.append(getPreBlank(blankCount + 1)).append("sleep 5 \n");
							tempSB.append(getPreBlank(blankCount + 1)).append("echo '???????????????????????? ......'\n");
							blankCount = this.assembleAOPEnd(paramMap, tblSystemModule, tempSB, blankCount);
							tempSB.append(getPreBlank(blankCount)).append("}\n");
							blankCount = this.assembleNodeEnd(tblSystemModule, tempSB, blankCount);//???????????????
							List<String> checkModuleList = (List<String>)paramMap.get("checkModuleList");
							for (String checkModuleId : checkModuleList) {
								if (checkModuleId.equals(tblSystemModule.getId().toString())) {
									String deployPackageName = tblArtifactInfo.getArtifactId() + "-" + tblArtifactInfo.getVersion() + "." + packageSuffix;
									subSystemCode += tblSystemModule.getModuleCode() + ",";
									subSystemPackageName += deployPackageName + ",";
									break;
								}
							}
							break;
						}
					}
				}
				
//				Map<String, Object> tempMap = new HashMap<String, Object>();
//				tempMap.put("tblSystemDeployList", paramMap.get("tblSystemDeployList"));
//				assebmleNodeMap2ScriptSB(tblSystemModuleList, nodeMap, tempMap, false, scriptSB, blankCount);//?????????????????????scriptSB
				if (StringUtil.isNotEmpty(subSystemCode)) {
					subSystemCode = subSystemCode.substring(0, subSystemCode.length() - 1);
					jenkinsParameterMap.put(this.subSystemCode, subSystemCode);
				}
				if (StringUtil.isNotEmpty(subSystemPackageName)) {
					subSystemPackageName = subSystemPackageName.substring(0, subSystemPackageName.length() - 1);
					jenkinsParameterMap.put(this.subSystemPackageName, subSystemPackageName);
				}
			}
		}
	}

	/**
	 * ????????????document,???????????????sql?????????src?????????
	 * @param configFtpList
	 * @param operFtpList
	 * @param sqlFtpList
	 * @param scriptSB
	 * @param blankCount
	 */
	private void downloadPROFile(Map paramMap, StringBuilder scriptSB, int blankCount) {
		scriptSB.append(getPreBlank(blankCount)).append("stage('download file'){\n");
		scriptSB.append(getPreBlank(blankCount + 1)).append("sh '''\n");//??????'???????????????????????????
		
		scriptSB.append(getPreBlank(blankCount + 1)).append("rm -rf ").append(uploadTemp).append("\n");
		scriptSB.append(getPreBlank(blankCount + 1)).append("mkdir -p ").append(uploadConfigurationPath).append("\n");
		scriptSB.append(getPreBlank(blankCount + 1)).append("mkdir -p ").append(uploadDocumentPath).append("\n");
		scriptSB.append(getPreBlank(blankCount + 1)).append("mkdir -p ").append(uploadPackagePath).append("\n");
		scriptSB.append(getPreBlank(blankCount + 1)).append("mkdir -p ").append(uploadSqlPath).append("\n");
		
//		List<FtpS3Vo> configFtpList = (List<FtpS3Vo>)paramMap.get("configResult");
		List<FtpS3Vo> operFtpList = (List<FtpS3Vo>)paramMap.get("operFileResult");
//		List<FtpS3Vo> sqlFtpList = (List<FtpS3Vo>)paramMap.get("sqlFileResult");
		
		if (operFtpList != null) {
			for (FtpS3Vo ftpS3Vo : operFtpList) {
				ftpS3Vo.setFileType(2);
				assembleCURL(ftpS3Vo, scriptSB, blankCount);
			}
		}
		scriptSB.append(getPreBlank(blankCount + 1)).append(" '''\n");
		scriptSB.append(getPreBlank(blankCount + 1)).append("echo ' ?????????????????????????????? ......'\n");
		scriptSB.append(getPreBlank(blankCount)).append("}\n");
	}
	
	private void assembleCURL(FtpS3Vo ftpS3Vo, StringBuilder scriptSB, int blankCount) {
		StringBuilder dataSB = new StringBuilder();
		String keyName = JenkinsUtil.encode(ftpS3Vo.getKeyName());
		String fileName = JenkinsUtil.encode(ftpS3Vo.getFileName());
		dataSB.append("curl ");
		dataSB.append(jenkinsCallbackUrl).append("/devManage/devtask/downloadFile?");
		dataSB.append("fileS3Bucket=titmgr-ftp").append("\\\\&");
		dataSB.append("fileS3Key=").append(keyName).append("\\\\&");
		dataSB.append("fileNameOld=").append(fileName);
		dataSB.append(" -o " + uploadDocumentPath  + "/").append(formatStringWithPipeline(ftpS3Vo.getFileName()));
		packageContinue(dataSB.toString(), scriptSB, blankCount + 1);
	}
	
	private void assembleCheckFile(String path, StringBuilder scriptSB, int blankCount) {
//		scriptSB.append(getPreBlank(blankCount)).append("sh 'md5sum ").append(path).append("*.*").append(" > commandResult &'\n");
		scriptSB.append(getPreBlank(blankCount)).append("sh 'ls -l ").append(path).append(" > commandResult &'\n");
		scriptSB.append(getPreBlank(blankCount)).append("env.checkPROFile").append(" += readFile('commandResult').trim()\n");
		scriptSB.append(getPreBlank(blankCount)).append("sh 'rm -rf commandResult'\n");
	}
	
	/**
	 * ??????????????????????????????target?????????zip???????????????????????????????????????configuration/sql/package
	 * @param tblSystemInfo
	 * @param tblSystemModuleList
	 * @param tblArtifactInfoList
	 * @param scriptSB
	 * @param blankCount
	 */
	private void assemblePROZipPackage(TblSystemInfo tblSystemInfo, List<TblSystemModule> tblSystemModuleList, 
			List<TblArtifactInfo> tblArtifactInfoList, StringBuilder scriptSB, int blankCount) {
		if (tblArtifactInfoList != null) {
			String artifactId = tblSystemInfo.getArtifactId();
			String packageSuffix = tblSystemInfo.getPackageSuffix();
			if (tblSystemInfo.getArchitectureType() == 2) { //1=??????????????????2=????????????
				TblArtifactInfo tblArtifactInfo = tblArtifactInfoList.get(0);
				String zipPackage = artifactId + "-" + tblArtifactInfo.getVersion() + ".zip";
				String mvnPackage = "*." + packageSuffix;
				scriptSB.append(getPreBlank(blankCount)).append("stage('move zip to file ").append("'){\n");
				scriptSB.append(getPreBlank(blankCount + 1)).append("sh 'unzip -oq target/").append(zipPackage).append(" -d target/'\n");
				scriptSB.append(getPreBlank(blankCount + 1)).append("sh '''\n");
				scriptSB.append(getPreBlank(blankCount + 1)).append("rm -rf configuration sql \n");
				scriptSB.append(getPreBlank(blankCount + 1)).append("if [ -d \"target/configuration/\" ];then ").append("mv -f target/configuration .")
				.append("; else echo \"\"; fi").append("\n");
				scriptSB.append(getPreBlank(blankCount + 1)).append("if [ -d \"target/sql/\" ];then ").append("mv -f target/sql .").append("; else echo \"\"; fi").append("\n");
				scriptSB.append(getPreBlank(blankCount + 1)).append("rm -rf target/").append(mvnPackage).append("\n");
				scriptSB.append(getPreBlank(blankCount + 1)).append("mv -f target/package/").append(mvnPackage).append(" target/\n");
				scriptSB.append(getPreBlank(blankCount + 1)).append("'''\n");
//				scriptSB.append(getPreBlank(blankCount + 1)).append("sh 'rm -rf target/").append(zipPackage).append("'\n");
				scriptSB.append(getPreBlank(blankCount + 1)).append("echo '").append(tblSystemInfo.getSystemCode()).append(" move zip file???????????? ......'\n");
				scriptSB.append(getPreBlank(blankCount)).append("}\n");
			} else if (tblSystemModuleList != null) {//???????????????
				for (TblSystemModule tblSystemModule : tblSystemModuleList) {
					TblArtifactInfo tblArtifactInfo = null;
					for (TblArtifactInfo bean : tblArtifactInfoList) {
						if (tblSystemModule.getId().equals(bean.getSystemModuleId())) {
							tblArtifactInfo = bean;
							break;
						}
					}
					if (StringUtil.isNotEmpty(tblSystemModule.getArtifactId())) {
						artifactId = tblSystemModule.getArtifactId();
					}
					if (StringUtil.isNotEmpty(tblSystemModule.getPackageSuffix())) {
						packageSuffix = tblSystemModule.getPackageSuffix();
					}
					String zipPackage = artifactId + "-" + tblArtifactInfo.getVersion() + ".zip";
					String mvnPackage = "*." + packageSuffix;
					StringBuilder tempSB = new StringBuilder();
					blankCount = this.assembleNodeStart(tblSystemModule, tempSB, blankCount);//???????????????
					tempSB.append(getPreBlank(blankCount)).append("stage('move zip to file ").append("'){\n");
					tempSB.append(getPreBlank(blankCount + 1)).append("dir(path:'").append(tblSystemModule.getRelativePath()).append("'){\n");
					tempSB.append(getPreBlank(blankCount + 2)).append("sh 'unzip -oq target/").append(zipPackage).append(" -d target/'\n");
					tempSB.append(getPreBlank(blankCount + 2)).append("sh '''\n");
					tempSB.append(getPreBlank(blankCount + 2)).append("rm -rf configuration sql \n");
					tempSB.append(getPreBlank(blankCount + 2)).append("if [ -d \"target/configuration/\" ];then ").append("mv -f target/configuration .")
					.append("; else echo \"\"; fi").append("\n");
					tempSB.append(getPreBlank(blankCount + 2)).append("if [ -d \"target/sql/\" ];then ").append("mv -f target/sql .").append("; else echo \"\"; fi").append("\n");
					tempSB.append(getPreBlank(blankCount + 2)).append("rm -rf target/").append(mvnPackage).append("\n");
					tempSB.append(getPreBlank(blankCount + 2)).append("mv -f target/package/").append(mvnPackage).append(" target/\n");
					tempSB.append(getPreBlank(blankCount + 2)).append("'''\n");
//					tempSB.append(getPreBlank(blankCount + 2)).append("sh 'rm -rf target/").append(zipPackage).append("'\n");
					tempSB.append(getPreBlank(blankCount + 1)).append("}\n");
					tempSB.append(getPreBlank(blankCount + 1)).append("echo '").append(tblSystemModule.getModuleCode()).append(" move zip file???????????? ......'\n");
					tempSB.append(getPreBlank(blankCount)).append("}\n");
					blankCount = this.assembleNodeEnd(tblSystemModule, tempSB, blankCount);//???????????????
				}
			}
		}
	}

	/**
	 * ???sql,configuration,package?????????uploadTemp?????????????????????
	 * @param tblSystemInfo
	 * @param tblSystemJenkins
	 * @param tblSystemModuleList
	 * @param tblSystemModuleScmList 
	 * @param tblSystemScm 
	 * @param tblArtifactInfoList 
	 * @param scriptSB
	 * @param blankCount
	 */
	private void assemblePROCopyPackage(Map paramMap, TblSystemInfo tblSystemInfo, TblSystemJenkins tblSystemJenkins, List<TblSystemModule> tblSystemModuleList, 
			TblSystemScm tblSystemScm, List<TblArtifactInfo> tblArtifactInfoList, List<String> checkModuleList, 
			StringBuilder scriptSB, int blankCount) {
		if (tblSystemInfo.getBuildType() != null) {
			String envName = paramMap.get("envName").toString();
			scriptSB.append(getPreBlank(blankCount)).append("stage('cp package sql configuration'){\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("sh '''\n");//??????'???????????????????????????
			int moduleCount = 0;
			String localPath = "";
			String checkFilePath = "";
			String remotePath = "";
			if (tblSystemModuleList == null || tblSystemModuleList.size() == 0) {//?????????
				String remoteRelativePath = tblSystemInfo.getSystemCode();
				if (tblArtifactInfoList != null && tblArtifactInfoList.size() > 0) {
					localPath = "sql/" + tblArtifactInfoList.get(0).getVersion();
					remotePath = uploadSqlPath + "/" + remoteRelativePath;
					scriptSB.append(getPreBlank(blankCount + 1)).append("mkdir -p ").append(remotePath).append("\n");
					scriptSB.append(getPreBlank(blankCount + 1)).append("if [ -d \"").append(localPath).append("/\" ];then ")
					.append("cp -rf ").append(localPath).append("/* ").append(remotePath).append("; else echo \"\"; fi").append("\n");
				}
				localPath = "configuration/" + envName;
				remotePath = uploadConfigurationPath + "/" + remoteRelativePath;
				String configPackage = tblSystemInfo.getSystemCode() + ".tar";
				scriptSB.append(getPreBlank(blankCount + 1)).append("mkdir -p ").append(remotePath).append("\n");
				scriptSB.append(getPreBlank(blankCount + 1)).append("if [ -d \"").append(localPath).append("/\" ];then ")
				.append("cp -rf ").append(localPath).append("/* ").append(remotePath).append(";")
				.append("cd ").append(remotePath).append(";")
				.append("tar -cvf ").append(configPackage).append(" *").append(";")
				.append("mv ").append(configPackage).append(" ../").append(";")
				.append("rm -rf *").append(";")
				.append("mv ../").append(configPackage).append(" .").append(";")
				.append("cd - ").append(";")
				.append(" else echo \"\"; fi").append("\n");
				
				remotePath = uploadPackagePath + "/" + remoteRelativePath;
				scriptSB.append(getPreBlank(blankCount + 1)).append("mkdir -p ").append(remotePath).append("\n");
				scriptSB.append(getPreBlank(blankCount + 1)).append("cp target/*.").append(tblSystemInfo.getPackageSuffix()).append(" ")
				.append(remotePath ).append("\n");
				checkFilePath = remotePath + "/*.*";
				moduleCount++;
			} else {//???????????????
				for (TblSystemModule tblSystemModule : tblSystemModuleList) {
					for (String checkModuleId : checkModuleList) {
						if (checkModuleId.equals(tblSystemModule.getId().toString())) {
							String localRelativePath = JenkinsUtil.addSlash(tblSystemModule.getRelativePath(), "/|\\\\", "/", false);
							String remoteRelativePath = tblSystemModule.getModuleCode();
							if (tblArtifactInfoList != null) {
								for (TblArtifactInfo bean : tblArtifactInfoList) {
									if (tblSystemModule.getId().equals(bean.getSystemModuleId())) {
										localPath = localRelativePath + "/sql/" + bean.getVersion();
										remotePath = uploadSqlPath + "/" + remoteRelativePath;
										scriptSB.append(getPreBlank(blankCount + 1)).append("mkdir -p ").append(remotePath).append("\n");
										scriptSB.append(getPreBlank(blankCount + 1)).append("if [ -d \"").append(localPath).append("/\" ];then ")
										.append("cp -rf ").append(localPath).append("/* ").append(remotePath).append("; else echo \"\"; fi").append("\n");
										break;
									}
								}
							}
							localPath = localRelativePath + "/configuration/" + envName;
							remotePath = uploadConfigurationPath + "/" + remoteRelativePath;
							String configPackage = tblSystemModule.getModuleCode() + ".tar";
							scriptSB.append(getPreBlank(blankCount + 1)).append("mkdir -p ").append(remotePath).append("\n");
							scriptSB.append(getPreBlank(blankCount + 1)).append("if [ -d \"").append(localPath).append("/\" ];then ")
							.append("cp -rf ").append(localPath).append("/* ").append(remotePath).append(";")
							.append("cd ").append(remotePath).append(";")
							.append("tar -cvf ").append(configPackage).append(" *").append(";")
							.append("mv ").append(configPackage).append(" ../").append(";")
							.append("rm -rf *").append(";")
							.append("mv ../").append(configPackage).append(" .").append(";")
							.append("cd - ").append(";")
							.append(" else echo \"\"; fi").append("\n");
							
							String packageSuffix = tblSystemInfo.getPackageSuffix();
							if (StringUtil.isNotEmpty(tblSystemModule.getPackageSuffix())) {
								packageSuffix = tblSystemModule.getPackageSuffix();
							}
							localPath = localRelativePath + "/target";
							remotePath = uploadPackagePath + "/" + remoteRelativePath;
							scriptSB.append(getPreBlank(blankCount + 1)).append("mkdir -p ").append(remotePath).append("\n");
							scriptSB.append(getPreBlank(blankCount + 1))
							.append("cp ").append(localPath).append("/*.").append(packageSuffix).append(" ").append(remotePath).append("\n");
//							scriptSB.append(getPreBlank(blankCount + 1)).append("sleep 5 \n");
							checkFilePath += remotePath + "/*.* ";
							moduleCount++;
							break;
						}
					}
				}
			}
			scriptSB.append(getPreBlank(blankCount + 1)).append(" '''\n");
			assembleCheckFile(checkFilePath, scriptSB, blankCount + 1);
//			scriptSB.append(getPreBlank(blankCount + 1)).append("sleep 10 \n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("echo ' ??????cp package sql configuration?????? ......'\n");
			scriptSB.append(getPreBlank(blankCount)).append("}\n");
			paramMap.put("moduleCount", moduleCount);
		} 
	}
	
	/**
	 * ???packageData??????if???????????????Jenkins???????????????????????????????????????
	 * @param packageData
	 * @param scriptSB
	 * @param blankCount
	 */
	private void packageContinue(String packageData, StringBuilder scriptSB, int blankCount) {
		scriptSB.append(getPreBlank(blankCount)).append("if ").append(packageData).append(";then \n");
		scriptSB.append(getPreBlank(blankCount)).append("  echo 'end.'  else  echo 'also end.' \n");
		scriptSB.append(getPreBlank(blankCount)).append("fi \n");
	}
	
	
	/**
	 * ??????PRO??????????????????
	 * @param tblSystemInfo 
	 * @param tblSystemDeployList 
	 * @param tblSystemModuleList 
	 * @param configFtpList
	 * @param operFtpList
	 * @param sqlFtpList
	 * @param scriptSB
	 * @param blankCount
	 */
	private void assemblePRODeployData(Map paramMap, TblSystemInfo tblSystemInfo, List<TblSystemModule> tblSystemModuleList, 
			List<String> checkModuleList, StringBuilder scriptSB, int blankCount) {
		scriptSB.append(getPreBlank(blankCount)).append("stage('upload deploy'){\n");
		showScriptTitle(DEPLOY_AUTO_TITLE, 0, SUFFIX_TITLE_2, scriptSB, blankCount + 1);
		if (tblSystemModuleList != null) {
			for (TblSystemModule tblSystemModule : tblSystemModuleList) {
				blankCount = this.assembleAOPStart(paramMap, tblSystemModule, scriptSB, blankCount);
			}
		}
		scriptSB.append(getPreBlank(blankCount + 1)).append("sh '''\n");//??????'???????????????????????????
		scriptSB.append("cat>expect.sh<<eof\n");
		String expectScript = jenkinsDeployExcept.assemblePROExpectScript(paramMap, tblSystemInfo, tblSystemModuleList, checkModuleList, blankCount + 1);
		scriptSB.append(expectScript);
		scriptSB.append("eof\n");//???????????????????????????????????????
		scriptSB.append("expect expect.sh\n");
		scriptSB.append(getPreBlank(blankCount + 1)).append(" '''\n");
		scriptSB.append(getPreBlank(blankCount + 1)).append("sh 'rm -rf expect.sh'\n");
		scriptSB.append(getPreBlank(blankCount + 1)).append("echo ' ????????????????????????????????? ......'\n");
		if (tblSystemModuleList != null) {
			for (TblSystemModule tblSystemModule : tblSystemModuleList) {
				
				scriptSB.append(getPreBlank(blankCount + 1)).append("dataMap.put('stageLast',true)\n");
				blankCount = this.assembleAOPEnd(paramMap, tblSystemModule, scriptSB, blankCount);
			}
		}
		scriptSB.append(getPreBlank(blankCount)).append("}\n");
	}

	
	
	/**
	 * ????????????SQL????????????
	 * @param paramMap
	 * @param tblSystemInfo
	 * @param nexusTool 
	 * @param tblSystemModuleList 
	 * @param tblArtifactInfoList 
	 * @param scriptSB
	 * @param i
	 */
	private void assembleSQLData(Map<String, Object> paramMap, TblSystemInfo tblSystemInfo, TblToolInfo nexusTool, List<TblSystemModule> tblSystemModuleList, List<TblArtifactInfo> tblArtifactInfoList, 
			List<TblSystemDbConfig> tblSystemDbConfigList, StringBuilder scriptSB, int blankCount) {
		if (tblArtifactInfoList != null) {
			if (tblSystemInfo.getArchitectureType() == 2) { //1=??????????????????2=????????????
				TblArtifactInfo tblArtifactInfo = tblArtifactInfoList.get(0);
				scriptSB.append(getPreBlank(blankCount)).append("stage('sql groovy ").append(tblSystemInfo.getSystemCode()).append("'){\n");
				showScriptTitle(tblSystemInfo.getSystemCode() + " " + INIT_GROOVY_TITLE, 0, SUFFIX_TITLE_2, scriptSB, blankCount + 1);
				if (tblSystemDbConfigList != null && tblSystemDbConfigList.size() > 0) {
					assembleInitSQLGroovy(tblSystemInfo, null, tblArtifactInfo, tblSystemDbConfigList, scriptSB, blankCount + 1);
					assembleSQLScript(tblSystemInfo, nexusTool, null, tblArtifactInfo, tblSystemDbConfigList, scriptSB, blankCount + 1);
				}
				scriptSB.append(getPreBlank(blankCount + 1)).append("echo '").append(tblSystemInfo.getSystemCode()).append(" sql groovy???????????? ......'\n");
				scriptSB.append(getPreBlank(blankCount)).append("}\n");
			} else if (tblSystemModuleList != null) {//???????????????
				for (TblSystemModule tblSystemModule : tblSystemModuleList) {
					TblArtifactInfo tblArtifactInfo = null;
					for (TblArtifactInfo bean : tblArtifactInfoList) {
						if (tblSystemModule.getId().equals(bean.getSystemModuleId())) {
							tblArtifactInfo = bean;
							break;
						}
					}
					List<TblSystemDbConfig> DbConfigListTemp = new ArrayList<TblSystemDbConfig>();
					for (TblSystemDbConfig tblSystemDbConfig : tblSystemDbConfigList) {
						if (tblSystemModule.getId().equals(tblSystemDbConfig.getSystemModuleId())) {
							DbConfigListTemp.add(tblSystemDbConfig);
						}
					}
					StringBuilder tempSB = new StringBuilder();
					blankCount = this.assembleNodeStart(tblSystemModule, tempSB, blankCount);//???????????????
					tempSB.append(getPreBlank(blankCount)).append("stage('sql groovy ").append(tblSystemModule.getModuleCode()).append("'){\n");
					showScriptTitle(tblSystemModule.getModuleCode() + " " + INIT_GROOVY_TITLE, 0, SUFFIX_TITLE_2, scriptSB, blankCount + 1);
					blankCount = this.assembleAOPStart(paramMap, tblSystemModule, tempSB, blankCount);
					tempSB.append(getPreBlank(blankCount + 1)).append("dir(path:'").append(tblSystemModule.getRelativePath()).append("'){\n");
					if (DbConfigListTemp != null && DbConfigListTemp.size() > 0) {
						assembleInitSQLGroovy(tblSystemInfo, tblSystemModule, tblArtifactInfo, DbConfigListTemp, tempSB, blankCount + 2);
						assembleSQLScript(tblSystemInfo, nexusTool, tblSystemModule, tblArtifactInfo, DbConfigListTemp, tempSB, blankCount + 2);
					}
					tempSB.append(getPreBlank(blankCount + 1)).append("}\n");
					tempSB.append(getPreBlank(blankCount + 1)).append("echo '").append(tblSystemModule.getModuleCode()).append(" sql groovy???????????? ......'\n");
					blankCount = this.assembleAOPEnd(paramMap, tblSystemModule, tempSB, blankCount);
					tempSB.append(getPreBlank(blankCount)).append("}\n");
					blankCount = this.assembleNodeEnd(tblSystemModule, tempSB, blankCount);//???????????????
				}
			}
		}
	}
	
	/**
	 * ??????zip?????????????????????groovy??????,???SQL??????????????????????????????????????????
	 * @param tblSystemInfo
	 * @param tblSystemModule
	 * @param tblArtifactInfo
	 * @param tblSystemDbConfigList 
	 * @param tblSystemDbConfig
	 * @param scriptSB
	 * @param i
	 */
	private void assembleInitSQLGroovy(TblSystemInfo tblSystemInfo, TblSystemModule tblSystemModule,
			TblArtifactInfo tblArtifactInfo, List<TblSystemDbConfig> tblSystemDbConfigList, StringBuilder scriptSB, int blankCount) {
		String artifactId = tblSystemInfo.getArtifactId();
		if (tblSystemModule != null) {
			if (StringUtil.isNotEmpty(tblSystemModule.getArtifactId())) {
				artifactId = tblSystemModule.getArtifactId();
			}
		}
		StringBuilder grapesSB = new StringBuilder();
		for (TblSystemDbConfig tblSystemDbConfig : tblSystemDbConfigList) {
			grapesSB.append("@Grab('").append(tblSystemDbConfig.getDriverVersionName()).append("'),");
		}
		if (grapesSB.length() > 0) {//????????????????????????????????????????????????
			String grapesStr = grapesSB.substring(0, grapesSB.length() - 1);
			String zipPackage = artifactId + "-" + tblArtifactInfo.getVersion() + ".zip";
			scriptSB.append(getPreBlank(blankCount)).append("sh '''\n");
			scriptSB.append(getPreBlank(blankCount)).append("cd target\n");
			scriptSB.append(getPreBlank(blankCount)).append("unzip -oq ").append(zipPackage).append("\n");
			scriptSB.append(getPreBlank(blankCount)).append("cp -r /app/CD-SQL/*.groovy .\n");
			//?????????????????????????????????SQL??????????????????????????????????????????
			scriptSB.append(getPreBlank(blankCount)).append("groovy InitData.groovy \"").append(tblArtifactInfo.getVersion()).append("\" \"").append(grapesStr).append("\"\n");//
			scriptSB.append(getPreBlank(blankCount)).append("'''\n");
		}
		
	}

	/**
	 * ??????AeforeStop AfterStop AfterStartUp
	 * @param tblSystemInfo
	 * @param nexusTool 
	 * @param tblSystemModule
	 * @param tblArtifactInfo
	 * @param tblSystemDbConfigList
	 * @param scriptSB
	 * @param blankCount
	 */
	private void assembleSQLScript(TblSystemInfo tblSystemInfo, TblToolInfo nexusTool, TblSystemModule tblSystemModule, TblArtifactInfo tblArtifactInfo, 
			List<TblSystemDbConfig> tblSystemDbConfigList, StringBuilder scriptSB, int blankCount) {
		scriptSB.append(getPreBlank(blankCount)).append("echo '??????").append(beforeStopGroovy).append(".groovy ").append(afterStopGroovy)
			.append(".groovy ").append(afterStartUpGroovy).append(".groovy'\n");
		scriptSB.append(getPreBlank(blankCount)).append("sh '''\n");//??????'???????????????????????????
		scriptSB.append("cat>").append("target/").append(beforeStopGroovy).append(".groovy<<eof\n");
		StringBuilder beforeStopSB = assembleSQLGroovy(tblSystemInfo, tblSystemModule, tblSystemDbConfigList, 1, 0);
		scriptSB.append(beforeStopSB);
		scriptSB.append("eof\n");//???????????????????????????????????????
		scriptSB.append("cat>").append("target/").append(afterStopGroovy).append(".groovy<<eof\n");
		StringBuilder afterStopSB = assembleSQLGroovy(tblSystemInfo, tblSystemModule, tblSystemDbConfigList, 2, 0);
		scriptSB.append(afterStopSB);
		scriptSB.append("eof\n");//???????????????????????????????????????
		scriptSB.append("cat>").append("target/").append(afterStartUpGroovy).append(".groovy<<eof\n");
		StringBuilder afterStartUpSB = assembleSQLGroovy(tblSystemInfo, tblSystemModule, tblSystemDbConfigList, 3, 0);
		scriptSB.append(afterStartUpSB);
		scriptSB.append("eof\n");//???????????????????????????????????????
		scriptSB.append(getPreBlank(blankCount)).append("'''\n");
		//TODO
	}

	/**
	 * ??????Groovy??????SQL?????????
	 * @param tblSystemInfo
	 * @param tblSystemModule
	 * @param tblArtifactInfo 
	 * @param tblSystemDbConfigList
	 * @param rollbackList
	 * @param num ?????? 
	 * @param blankCount
	 * @return
	 */
	private StringBuilder assembleSQLGroovy(TblSystemInfo tblSystemInfo, TblSystemModule tblSystemModule,
			List<TblSystemDbConfig> tblSystemDbConfigList, int num, int blankCount) {
		StringBuilder groovySB = new StringBuilder();
		String className = "";
		if (num == 1) {
			className = beforeStopGroovy;
		} else if (num == 2) {
			className = afterStopGroovy;
		} else if (num == 3) {
			className = afterStartUpGroovy;
		}
		groovySB.append(getPreBlank(blankCount)).append("class ").append(className).append(" {\n");//BeforeStop
		groovySB.append(getPreBlank(blankCount + 1)).append("static void main(args) {\n");//main
		for (TblSystemDbConfig tblSystemDbConfig : tblSystemDbConfigList) {
			String url = tblSystemDbConfig.getUrl();
			String user = tblSystemDbConfig.getUserName();
			String password = tblSystemDbConfig.getPassword();
			String driver = tblSystemDbConfig.getDriverClassName();
			if (tblSystemDbConfig.getDriverClassName().indexOf("mysql") != -1) {//mysql
				String databaseName = url.substring(url.lastIndexOf("/") + 1);
				if (databaseName.indexOf("?") != -1) {
					databaseName = databaseName.substring(0, databaseName.indexOf("?"));
				}
				groovySB.append(getPreBlank(blankCount + 2)).append("SqlUtil.initSqlInstance('").append(url).append("', '").append(user)
				.append("', '").append(password).append("', '").append(driver).append("', '").append(databaseName).append("')\n");
			} else if (tblSystemDbConfig.getDriverClassName().indexOf("oracle") != -1) {//oracle
				String databaseName = tblSystemDbConfig.getUserName();
				groovySB.append(getPreBlank(blankCount + 2)).append("SqlUtil.initSqlInstance('").append(url).append("', '").append(user)
				.append("', '").append(password).append("', '").append(driver).append("', '").append(databaseName).append("')\n");
			} else if (tblSystemDbConfig.getDriverClassName().indexOf("sqlserver") != -1) {//sqlserver
				String databaseName = url.substring(url.lastIndexOf("/") + 1);
				if (databaseName.indexOf("?") != -1) {
					databaseName = databaseName.substring(0, databaseName.indexOf("?"));
				}
				groovySB.append(getPreBlank(blankCount + 2)).append("SqlUtil.initSqlInstance('").append(url).append("', '").append(user)
				.append("', '").append(password).append("', '").append(driver).append("', '").append(databaseName).append("')\n");
			}
		}
		if (num == 1) {//??????beforestop
			groovySB.append(getPreBlank(blankCount + 2)).append("InitData.cleanFileData(SqlUtil.rollbackDataPath)\n");
			groovySB.append(getPreBlank(blankCount + 2)).append("String backupExecutePath = 'beforestop/backup/execute'\n");
			groovySB.append(getPreBlank(blankCount + 2)).append("String backupRollbackPath = 'beforestop/backup/rollback'\n");
			groovySB.append(getPreBlank(blankCount + 2)).append("String updateExecutePath = 'beforestop/update/execute'\n");
			groovySB.append(getPreBlank(blankCount + 2)).append("String updateRollbackPath = 'beforestop/update/rollback'\n");
		} else if (num == 2) {//??????afterstop
			groovySB.append(getPreBlank(blankCount + 2)).append("String backupExecutePath = 'afterstop/backup/execute'\n");
			groovySB.append(getPreBlank(blankCount + 2)).append("String backupRollbackPath = 'afterstop/backup/rollback'\n");
			groovySB.append(getPreBlank(blankCount + 2)).append("String updateExecutePath = 'afterstop/update/execute'\n");
			groovySB.append(getPreBlank(blankCount + 2)).append("String updateRollbackPath = 'afterstop/update/rollback'\n");
		} else if (num == 3) {//??????afterstartup
			groovySB.append(getPreBlank(blankCount + 2)).append("String backupExecutePath = 'afterstartup/backup/execute'\n");
			groovySB.append(getPreBlank(blankCount + 2)).append("String backupRollbackPath = 'afterstartup/backup/rollback'\n");
			groovySB.append(getPreBlank(blankCount + 2)).append("String updateExecutePath = 'afterstartup/update/execute'\n");
			groovySB.append(getPreBlank(blankCount + 2)).append("String updateRollbackPath = 'afterstartup/update/rollback'\n");
		}
		groovySB.append(getPreBlank(blankCount + 2)).append("boolean success = SqlUtil.executeSqlTransaction(backupExecutePath, backupRollbackPath)\n");
		groovySB.append(getPreBlank(blankCount + 2)).append("if (!success) {\n");
		groovySB.append(getPreBlank(blankCount + 3)).append("throw new Exception('')\n");
		groovySB.append(getPreBlank(blankCount + 2)).append("}\n");
		groovySB.append(getPreBlank(blankCount + 2)).append("success = SqlUtil.executeSqlTransaction(updateExecutePath, updateRollbackPath)\n");
		groovySB.append(getPreBlank(blankCount + 2)).append("if (!success) {\n");
		groovySB.append(getPreBlank(blankCount + 3)).append("throw new Exception('')\n");
		groovySB.append(getPreBlank(blankCount + 2)).append("}\n");
		
		groovySB.append(getPreBlank(blankCount + 1)).append("}\n");//main
		groovySB.append(getPreBlank(blankCount)).append("}\n");//BeforeStop
		return groovySB;
	}

	/**
	 * ????????????????????????
	 * @param paramMap 
	 * @param tblServerInfoList
	 * @param tblSystemDeployList
	 * @param tblSystemDbConfigList 
	 * @param scriptSB
	 * @param blankCount
	 */
	private void assembleDeployData(Map<String, Object> paramMap, TblSystemInfo tblSystemInfo, TblToolInfo jenkinsTool, List<TblSystemModule> tblSystemModuleList,
			List<TblArtifactInfo> tblArtifactInfoList, List<TblServerInfo> tblServerInfoList, List<TblSystemDeploy> tblSystemDeployList,
			List<TblSystemDeployScript> tblSystemDeployScriptList, StringBuilder scriptSB, int blankCount) {
		if (tblServerInfoList != null && tblServerInfoList.size() > 0  
				&& tblSystemDeployList != null && tblSystemDeployList.size() > 0
				&& tblSystemDeployScriptList != null && tblSystemDeployScriptList.size() > 0) {
			for (TblSystemDeploy tblSystemDeploy : tblSystemDeployList) {
				
				if (tblSystemInfo.getArchitectureType() == 2) { //1=??????????????????2=????????????
					TblArtifactInfo tblArtifactInfo = null;
					if (tblArtifactInfoList != null && tblArtifactInfoList.size() > 0) {
						tblArtifactInfo = tblArtifactInfoList.get(0);
					}
					String currentCode = tblSystemInfo.getSystemCode();
					jenkinsDeployExcept.assembleExpect(paramMap, tblSystemInfo, jenkinsTool, null, tblArtifactInfo, tblServerInfoList, tblSystemDeploy, 
							tblSystemDeployScriptList, currentCode, scriptSB, blankCount);
				} else if (tblSystemModuleList != null) {
					for (TblSystemModule tblSystemModule : tblSystemModuleList) {
						if (tblSystemModule.getId().equals(tblSystemDeploy.getSystemModuleId())) {//?????????
							TblArtifactInfo tblArtifactInfo = null;
							if (tblArtifactInfoList != null) {
								for (TblArtifactInfo bean : tblArtifactInfoList) {
									if (tblSystemModule.getId().equals(bean.getSystemModuleId())) {
										tblArtifactInfo = bean;
										break;
									}
								}
							}
							StringBuilder tempSB = new StringBuilder();
							String currentCode = tblSystemModule.getModuleCode();
							blankCount = this.assembleNodeStart(tblSystemModule, tempSB, blankCount);//???????????????
							jenkinsDeployExcept.assembleExpect(paramMap, tblSystemInfo, jenkinsTool, tblSystemModule, tblArtifactInfo, tblServerInfoList, tblSystemDeploy, 
									tblSystemDeployScriptList, currentCode, tempSB, blankCount);
							blankCount = this.assembleNodeEnd(tblSystemModule, tempSB, blankCount);//???????????????
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * ????????????????????????
	 * @param tblSystemInfo
	 * @param tblSystemScm
	 * @param tblSystemJenkins
	 * @param jenkinsTool
	 * @param scriptSB
	 */
	private void assembleCallbackAuto(Map paramMap, TblSystemInfo tblSystemInfo, TblSystemScm tblSystemScm, 
			TblSystemJenkins tblSystemJenkins, TblToolInfo jenkinsTool, String runId, List<TblSystemModuleJenkinsJobRun> moduleRunList, 
			List<TblSystemSonar> tblSystemSonarList, StringBuilder scriptSB, int blankCount) throws Exception {
		
		
		scriptSB.append(getPreBlank(blankCount + 1)).append(this.endDate).append(" = new Date().format('yyyy-MM-dd HH:mm:ss')\n");
		if (tblSystemInfo.getArchitectureType() == 2) {//????????????
			scriptSB.append(getPreBlank(blankCount + 1)).append("systemObj = moduleMap.get('").append(tblSystemInfo.getSystemCode()).append("')\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("systemObj.put('buildStatus',2)").append("\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("systemObj.put('startDate',startDate)").append("\n");
			scriptSB.append(getPreBlank(blankCount + 1)).append("systemObj.put('endDate',endDate)").append("\n");
		}
		scriptSB.append(getPreBlank(blankCount + 1)).append("moduleJson = JsonOutput.toJson(moduleMap)\n");
		scriptSB.append(getPreBlank(blankCount + 1)).append("moduleJson = moduleJson.replaceAll('\"','\\\'')\n");
		scriptSB.append(getPreBlank(blankCount + 1)).append("echo '?????????????????????' + ").append(this.endDate).append("\n");
		
		String para = getCallbackAutoParameter(paramMap, tblSystemInfo, tblSystemScm, tblSystemJenkins, jenkinsTool, runId, moduleRunList, tblSystemSonarList);
		scriptSB.append(getPreBlank(blankCount + 1)).append("sh '''");//??????'???????????????????????????
		scriptSB.append(getPreBlank(blankCount + 1)).append(para).append(" '''");
//		scriptSB.append(getPreBlank(blankCount)).append("}\n");
	}
	
	/**
	 * ????????????Shell?????????
	 * curl http://192.168.1.178:8080/devManage/notify/callBackJenkinsLog -XPOST -d 
	 * "systemId=111&systemScmId=222&systemJenkinsId=333&jenkinsToolId=444&runId=555&architectureType=6666&moduleRunJson='[{...}]'"
	 * @param tblSystemInfo
	 * @param tblSystemScm
	 * @param tblSystemJenkins
	 * @param jenkinsTool
	 * @param runId
	 * @return
	 * @throws UnknownHostException
	 */
	private String getCallbackAutoParameter(Map paramMap, TblSystemInfo tblSystemInfo, TblSystemScm tblSystemScm,
			TblSystemJenkins tblSystemJenkins, TblToolInfo jenkinsTool, String runId, 
			List<TblSystemModuleJenkinsJobRun> moduleRunList, List<TblSystemSonar> tblSystemSonarList) throws Exception {
		List list = new ArrayList();
		String sonarflag = (String)paramMap.get("sonarflag");
		if (StringUtil.isNotEmpty(sonarflag) && sonarflag.equals("1")) {//???????????????????????????Sonar????????????
			if (moduleRunList != null && tblSystemSonarList != null) {
				for (TblSystemModuleJenkinsJobRun moduleJobRun : moduleRunList) {
					for (TblSystemSonar tblSystemSonar : tblSystemSonarList) {
						if (tblSystemSonar.getSystemModuleId() != null && 
								moduleJobRun.getSystemModuleId().equals(tblSystemSonar.getSystemModuleId())) {
							Map map = new HashMap();
							map.put("moduleRunId", moduleJobRun.getId());
							map.put("projectKey", tblSystemSonar.getSonarProjectKey());
							list.add(map);
							break;
						}
						
						if(tblSystemSonar.getSystemModuleId() == null) {
							//???????????????
							Map map = new HashMap();
							map.put("moduleRunId", moduleJobRun.getId());
							map.put("projectKey", tblSystemSonar.getSonarProjectKey());
							list.add(map);
							break;
							
						}
					}
				}
			}
		}
		
		String buildType = Objects.toString(paramMap.get(BUILD_TYPE), "");
		String scheduled = Objects.toString(paramMap.get("scheduled"), "");
		StringBuilder paramSB = new StringBuilder();
		paramSB.append("if ");
		paramSB.append("curl -m 2 ");
		//http://192.168.1.178:8080/devManage/notify/callBackJenkinsLog
		if (buildType.equals(BUILD_TYPE_AUTO)) {
			paramSB.append(jenkinsCallbackUrl).append("/devManage/notify/" + callBackJenkinsLog + " -XPOST -d \"");
		} else if (buildType.equals(BUILD_TYPE_AUTO_DEPLOY)) {
			paramSB.append(jenkinsCallbackUrl).append("/devManage/notify/" + callBackAutoDepolyJenkins + " -XPOST -d \"");
		}
		else if (buildType.equals(BUILD_TYPE_PACKAGE_AUTO_DEPLOY)) {
			paramSB.append(jenkinsCallbackUrl).append("/devManage/notify/" + callBackPackageDepolyJenkins + " -XPOST -d \"");
		}
		
		Map<String, Object> backMap = new HashMap<String, Object>();
		backMap.put("systemId", tblSystemInfo.getId());
		if (tblSystemScm != null) {
			backMap.put("systemScmId", tblSystemScm.getId());
		}
		if (StringUtil.isNotEmpty(sonarflag) && sonarflag.equals("1")) {//???????????????????????????Sonar????????????
			if (tblSystemSonarList != null && tblSystemSonarList.size() > 0) {
				backMap.put("sonarToolId", tblSystemSonarList.get(0).getToolId());
			}
		}
		backMap.put("systemJenkinsId", tblSystemJenkins.getId());
		backMap.put("jenkinsToolId", jenkinsTool.getId());
		backMap.put("runId", StringUtils.defaultString(runId));
		backMap.put("architectureType", tblSystemInfo.getArchitectureType());
		backMap.put("scheduled", StringUtils.defaultString(scheduled));
		backMap.put("reqFeatureqIds", paramMap.get("reqFeatureqIds"));
		backMap.put("taskEnvType", paramMap.get("taskEnvType"));
		backMap.put("userId", paramMap.get("userId"));
		backMap.put("userAccount", paramMap.get("userAccount"));
		backMap.put("userName", paramMap.get("userName"));
		backMap.put("proDuctionDeploytype", paramMap.get("proDuctionDeploytype"));
		backMap.put("envType", paramMap.get("envType"));
		backMap.put("envName", paramMap.get("envName"));
		backMap.put("versionInfo", paramMap.get("versionInfo"));
		backMap.put("sonarflag", paramMap.get("sonarflag"));
		backMap.put("moduleList", paramMap.get("moduleList"));
		backMap.put("jobNumber", paramMap.get("jobNumber"));
		backMap.put("nowJobNumber", "${BUILD_NUMBER}");
		backMap.put(this.startDate, "'''+" + this.startDate + "+'''");
		backMap.put(this.endDate, "'''+" + this.endDate + "+'''");
		backMap.put(this.moduleJson, "'''+" + this.moduleJson + "+'''");
		backMap.put("artType", paramMap.get("artType"));//??????????????????1???snapshots,2???releases
		backMap.put("version", paramMap.get("version"));//????????????
		
		backMap.put("moduleRunJson", list);
		List<Long> runList = new ArrayList<Long>();
		if (moduleRunList != null) {
			for (TblSystemModuleJenkinsJobRun obj : moduleRunList) {
				runList.add(obj.getId());
			}
		}
		backMap.put("moduleRunIds", runList);
		if (buildType.equals(BUILD_TYPE_PACKAGE_AUTO_DEPLOY)) {
			backMap.put("moduleRunList", moduleRunList);
		}
		if (paramMap.get("isPROAutoDeploy") != null) {//???????????????
			backMap.put(this.systemCode, jenkinsParameterMap.get(this.systemCode));
			backMap.put(this.subSystemCode, jenkinsParameterMap.get(this.subSystemCode));
			if (jenkinsParameterMap.get(this.systemPackageName) != null) {
				backMap.put(this.systemPackageName, jenkinsParameterMap.get(this.systemPackageName));
			} else {
				backMap.put(this.systemPackageName, "'''+" + this.systemPackageName + "+'''");
			}
			if (jenkinsParameterMap.get(this.subSystemPackageName) != null) {
				backMap.put(this.subSystemPackageName, jenkinsParameterMap.get(this.subSystemPackageName));
			} else {
				backMap.put(this.subSystemPackageName, "'''+" + this.subSystemPackageName + "+'''");
			}
		}
		
		String paramJson = JSON.toJSONString(backMap);
		paramJson = paramJson.replaceAll("\"", "\\\\\\\\\"");
		paramSB.append("jsonMap=").append(paramJson);
		
		paramSB.append("\" ;then \n      echo 'end.'  else  echo 'also end.' \n     fi");
		return paramSB.toString();
	}
	
	private String getCallbackManualParameter(Map paramMap, Map<String, Object> pluginMap, String configXml) throws Exception {
		paramMap.putAll(pluginMap);
		String buildType = Objects.toString(paramMap.get(BUILD_TYPE), "");
		StringBuilder paramSB = new StringBuilder();
		paramSB.append("if ");
		paramSB.append("curl -m 2 ");
		if (buildType.equals(BUILD_TYPE_MANUAL)) {
			paramSB.append(jenkinsCallbackUrl).append("/devManage/notify/" + callBackManualJenkins + " -XPOST -d \"");
		} else if (buildType.equals(BUILD_TYPE_MANUAL_DEPLOY)) {
			paramSB.append(jenkinsCallbackUrl).append("/devManage/notify/" + callBackManualDepolyJenkins + " -XPOST -d \"");
		}
		String paramJson = JSON.toJSONString(paramMap);
		paramJson = paramJson.replaceAll("\"", "\\\\\\\\\"");
		paramSB.append("jsonMap=").append(paramJson);
		paramSB.append("\" ;then \n  echo 'end.'  else  echo 'also end.' \n fi");
		return paramSB.toString();
	}
	
	
//	public static void main(String[] args) throws Exception {
//		JenkinsBuildServiceImpl obj = new JenkinsBuildServiceImpl();
//		TblToolInfo jenkinsTool = new TblToolInfo();
//		jenkinsTool.setIp("192.168.1.145");
//		jenkinsTool.setPort("8087");
//		jenkinsTool.setUserName("admin");
//		jenkinsTool.setPassword("admin");
		
//		Map<String, String> map = obj.validateCron(jenkinsTool, "H/5 * * * *");
//		System.out.println("OK");
//		TblSystemScm tblSystemScm = new TblSystemScm();
//		tblSystemScm.setScmType(1);
//		tblSystemScm.setScmUrl("http://218.17.169.171:18091/zhoudu222/itmp.git");
//		tblSystemScm.setScmBranch("master222");
//		TblSystemJenkins tblSystemJenkins = new TblSystemJenkins();
//		tblSystemJenkins.setJobName("superTest");
//		tblSystemJenkins.setRootPom("pom.xml");
//		tblSystemJenkins.setGoalsOptions("clean install -Dmaven.test.skip=true");
//
////		obj.buildJob(1, jenkinsTool, tblSystemScm, tblSystemJenkins);
//		try {
//			obj.getJobParameter(jenkinsTool, "parameter_pipeline");
//			String configXml = "" + 
//					"<project>\n" + 
//					"<description>test</description>\n" + 
//					"<keepDependencies>false</keepDependencies>\n" + 
//					"<properties>...</properties>\n" + 
//					"<scm class=\"hudson.scm.NullSCM\"/>\n" + 
//					"<canRoam>true</canRoam>\n" + 
//					"<disabled>false</disabled>\n" + 
//					"<blockBuildWhenDownstreamBuilding>false</blockBuildWhenDownstreamBuilding>\n" + 
//					"<blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>\n" + 
//					"<triggers/>\n" + 
//					"<concurrentBuild>false</concurrentBuild>\n" + 
//					"<builders>\n" + 
//					"<hudson.tasks.Shell>\n" + 
//					"</hudson.tasks.Shell>\n" + 
//					"<hudson.plugins.sonar.SonarRunnerBuilder plugin=\"sonar@2.8.1\">\n" + 
//					"<installationName>sonar-test-188</installationName>\n" + 
//					"<project>aaaaaaaaaa</project>\n" + 
//					"<properties>bbbbbbbbbbbbbbbb</properties>\n" + 
//					"<javaOpts/>\n" + 
//					"<additionalArguments/>\n" + 
//					"<jdk>(Inherit From Job)</jdk>\n" + 
//					"<task/>\n" + 
//					"</hudson.plugins.sonar.SonarRunnerBuilder>\n" + 
//					"</builders>\n" + 
//					"<publishers/>\n" + 
//					"<buildWrappers/>\n" + 
//					"</project>";
//			String ss = obj.deleteJobCallbackXml(configXml);
//			System.out.println(ss);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	
//	JenkinsBuildServiceImpl obj = new JenkinsBuildServiceImpl();
//	String script = " node{\n" + 
//			"\n" + 
//			"    stage('mvn'){\n" + 
//			"        echo 'abcdegf'\n" + 
//			"    }\n" + 
//			"\n" + 
//			"}\n";
//	String callback = "echo 'aaabbbccc'\n";
//	System.out.println(script);
//	script = obj.assembleCallbackScript(script, callback);
//	System.out.println("OK");
//	}

}
