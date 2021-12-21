package cn.pioneeruniverse.common.jenkins;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.StringUtil;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.escape.Escaper;
import com.google.common.net.UrlEscapers;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpConnection;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.BuildWithDetails;
import com.offbytwo.jenkins.model.JobWithDetails;

import cn.pioneeruniverse.common.utils.DateUtil;
import cn.pioneeruniverse.dev.entity.TblToolInfo;

/**
 * Jenkins 基础公共类
 * @author zhoudu
 *
 */
public class JenkinsUtil {
//	private volatile static JenkinsUtil jenkinsUtil;
	private static final Logger logger = LoggerFactory.getLogger(JenkinsUtil.class);
	private JenkinsServer server;
	private String url;
	private String username;
	private String password;
	
	private static final String ENCODE = "UTF-8";
	
	public static final String JENKINS_JOB_SNAPSHOTS2RELEASE = "snapshots2Release";
	public static final String JOB_TYPE_FREE = "<project>";//自由配置
	public static final String JOB_TYPE_MAVEN = "maven2-moduleset";//基于Maven配置
	public static final String JOB_TYPE_MATRIX = "matrix-project";//多配置
	public static final String JOB_TYPE_WORKFLOW = "flow-definition";//流水线配置
	public static final String JOB_TYPE_WORKFLOW_MULTIBRANCH = "WorkflowMultiBranchProject";//多分支流水线
	
	public static final String PARAMETER_DEFINITION_PROPERTY = "hudson.model.ParametersDefinitionProperty"; //Jenkins参数化构建ParametersDefinitionProperty
	public static final String PARAMETER_DEFINITIONS = "parameterDefinitions"; //Jenkins参数化构建parameterDefinitions
	
	public static final String PARAMETER_TYPE_STRING = "hudson.model.StringParameterDefinition"; //Jenkins参数化构建String类型
	public static final String PARAMETER_TYPE_BOOLEAN = "hudson.model.BooleanParameterDefinition"; //Jenkins参数化构建Boolean类型
	public static final String PARAMETER_TYPE_TEXT = "hudson.model.TextParameterDefinition"; //Jenkins参数化构建Text类型
	public static final String PARAMETER_TYPE_FILE = "hudson.model.FileParameterDefinition"; //Jenkins参数化构建File类型
	public static final String PARAMETER_TYPE_PASSWD = "hudson.model.PasswordParameterDefinition"; //Jenkins参数化构建Password类型
	public static final String PARAMETER_TYPE_CHOICE = "hudson.model.ChoiceParameterDefinition"; //Jenkins参数化构建Choice类型
	
	public static final String PARA_NAME = "elementName";//参数化构建-类型名称
	public static final String PARA_TYPE = "type";//参数化构建-类型
	public static final String PARA_TEXT_NAME = "name";//参数化构建-参数名称
	public static final String PARA_DESCRIPTION = "description";//参数化构建-描述
	public static final String PARA_DEFAULT_VALUE = "defaultValue";//参数化构建-值
	public static final String PARA_TRIM = "trim";//参数化构建-去除Trim
	
	public static final String SHELL_NAME = "hudson.tasks.Shell";
	public static final String SHELL_COMMAND = "command";
	
	public JenkinsUtil(String url, String username, String password) throws URISyntaxException {
		server = new JenkinsServer(new URI(url), username, password);
		this.url = url;
		this.username = username;
		this.password = password; 
	}

	public boolean isRunning() {
		if (server == null) {
			return false;
		}
		return true;
	}

	public JenkinsHttpConnection getJenkinsHttpConnection() throws IOException {
		return server.getQueue().getClient();
	}
	
	public void closeJenkins() {
		if (server != null) {
			server.close();
		}
	}
	
	private void writeLog(String jobName, String jobPath) {
		logger.info("jobName:" + jobName + ",jobPath:" + jobPath);
	}
	
	public static String encode(String str) {
		try {
			if (StringUtil.isNotEmpty(str) && str.indexOf("%") == -1) {//带%表示已经经过转义
				str = URLEncoder.encode(str, JenkinsUtil.ENCODE);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(),e);
		}
		return str;
	}
	
	/**
	 * 通过地址获取基于SAX的XMLElemet
	 * @param pipelineType 1为普通流水线模板，2 用于快照转releases的Jenkins模板 
	 * @return
	 * @throws Exception
	 */
	public Element getSAXElementByXml(int pipelineType) throws Exception {
		String xml = getJenkinsJobPipelineXml(pipelineType);
		Element root = getSAXElementByXml(xml);
		return root;
	}
	
	/**
	 * 通过configXml获取基于SAX的XMLElemet
	 * @param configXml
	 * @return
	 * @throws Exception
	 */
	public Element getSAXElementByXml(String configXml) throws Exception {
		StringReader stringReader = new StringReader(configXml);  
		SAXReader reader = new SAXReader();
		Document document = reader.read(stringReader);
		Element root = document.getRootElement();
		return root;
	}
	
	/**
	 * 创建Jenkins连接其它软件的凭据用户
	 * @param username
	 * @param password
	 * @throws IOException
	 */
	public String createCredentialsUser(String username, String password) throws IOException {
		String createUserUrl = url + "/credentials/store/system/domain/_/createCredentials";
		StringBuilder jsonSB = new StringBuilder();
		jsonSB.append("{");
		jsonSB.append("\"\": \"0\",");
		jsonSB.append("\"credentials\": {");
		jsonSB.append("\"scope\": \"GLOBAL\",");
		jsonSB.append("\"id\": \"\",");
		jsonSB.append("\"username\": \"zxccvv\",");
		jsonSB.append("\"password\": \"aaaaaaaa\",");
		jsonSB.append("\"description\": \"deees\",");
		jsonSB.append("\"$class\": \"com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl\"");
		jsonSB.append("}");
		jsonSB.append("}");
		createUserUrl += "?" + jsonSB.toString();
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		client.post(createUserUrl);
		//给予Jenkins一定创建用户时间
//		try {
//			Thread.sleep(100);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		Map<String,String> userMap = getCredentialsUserByName(username);
		String idCode = null;
		if (userMap != null) {
			idCode = userMap.get("id").toString();
		}
		return idCode;
	}
	
	public List<Map<String,String>> getCredentialsUserList() throws IOException {
		String getUserUrl = url + "/credentials/store/system/domain/_/api/json?depth=1";
		JenkinsHttpConnection client = server.getQueue().getClient();
		String userJson = client.get(getUserUrl);
		JSONObject obj = JSON.parseObject(userJson);
		Object credentialsObj = obj.get("credentials");
		List<Map<String,String>> userList = null;
		if (credentialsObj != null) {
			userList = (List<Map<String,String>>)credentialsObj;
		}
		return userList;
	}
	
	public Map<String,String> getCredentialsUserByName(String username) throws IOException {
		List<Map<String,String>> list = getCredentialsUserList();
		Map<String,String> user = null;
		for (Map<String, String> userMap : list) {
			String displayName = userMap.get("displayName").toString();
			String[] displayNameArr = displayName.split("/");
			if (username.trim().equals(displayNameArr[0])) {
				user = userMap;
				break;
			}
		}
		return user;
	}
	public Map<String,String> getCredentialsUser(String idCode) throws IOException {
		List<Map<String,String>> list = getCredentialsUserList();
		Map<String,String> user = null;
		for (Map<String, String> userMap : list) {
			String id = userMap.get("id").toString();
			if (idCode.trim().equals(id)) {
				user = userMap;
				break;
			}
		}
		return user;
	}
	
	public Boolean isExistCredentialsUser(String idCode) throws IOException {
		Map<String,String> userMap = getCredentialsUser(idCode);
		Boolean exist = false;
		if (userMap != null) {
			exist = true;
		}
		return exist;
	}
	
	
	/**
	 * 判断用户有没有连接Jenkins
	 * @param username
	 * @param password
	 * @param url 
	 * @return
	 */
	public boolean hasConnected(TblToolInfo bean, String url) {
		boolean connected = true;
		if (this.username == null || this.password == null || this.url == null) {
			connected = false;
		} else if (!this.username.equals(bean.getUserName()) || !this.password.equals(bean.getPassword()) || !this.url.equals(url)) {
			connected = false;
		} else if (!isRunning()) {
			connected = false;
		}
		return connected;
	}

	/**
	 * 创建Jenkins Job
	 * http://localhost:8087/createItem/api/json?name=test_maven
	 * @param jobName
	 * @param configXml
	 * @throws IOException
	 */
	public void creatJenkinsJob(String jobName, String configXml) throws IOException {
		jobName = encode(jobName);
		server.createJob(jobName, configXml);
	}
	
	/**
	 * 更新Jenkins Job，需要完整的配置XML，直接根据XML重新生成一个Job配置。
	 * http://localhost:8087/job/itmp_maven/config.xml
	 * @param jobName
	 * @param configXml
	 * @throws IOException
	 */
	public void updateJenkinsJob(String jobName, String configXml) throws IOException {
		jobName = encode(jobName);
		updateJenkinsJob("", jobName, configXml);
	}
	public void updateJenkinsJob(String jobPath, String jobName, String configXml) throws IOException {
		jobName = encode(jobName);
		if (StringUtil.isEmpty(jobPath)) {
			server.updateJob(jobName, configXml);
		} else {
			jobPath = getValidJobPath(jobPath);
			jobPath = jobPath + "job/" + jobName + "/config.xml";
			writeLog(jobName, jobPath);
			JenkinsHttpConnection client = getJenkinsHttpConnection();
			client.post_xml(jobPath, configXml);
		}
	}
	
	/**
	 * 获取Job
	 * @param jobName
	 * @return
	 * @throws IOException
	 */
	public JobWithDetails getJenkinsJob(String jobPath, String jobName) throws IOException {
		jobName = encode(jobName);
		JobWithDetails job;
		if (StringUtil.isEmpty(jobPath)) {
			job = server.getJob(jobName);
		} else {
			jobPath = getValidJobPath(jobPath);
			jobPath = jobPath + "job/" + jobName;
			writeLog(jobName, jobPath);
			JenkinsHttpConnection client = getJenkinsHttpConnection();
			job = client.get(jobPath, JobWithDetails.class);
		}
		return job;
	}
	
	/**
	 * 查询是否存在名为jobName的job
	 * http://localhost:8087/job/itmp_maven/api/json
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
	public boolean existJenkinsJob(String jobName) throws IOException {
		jobName = encode(jobName);
		JobWithDetails job = server.getJob(jobName);
		if (job != null) {
			return true;
		}
		return false;
	}

	/**
	 * 删除Jenkins Job
	 * @param jobName
	 * @throws Exception
	 */
	public void deleteJenkinsJob(String jobName) throws IOException {
		jobName = encode(jobName);
		server.deleteJob(jobName);
	}

	/**
	 * 获取服务器的config.xml配置文件内容，用来创建Jenkins任务
	 * @param configXmlPath
	 * @return
	 */
	public String getConfigContent(String configXmlPath) throws IOException {
		StringBuilder build = new StringBuilder();
		InputStream in = null;
		InputStreamReader read = null;
		try {
			in = JenkinsUtil.class.getResourceAsStream(configXmlPath);
			read = new InputStreamReader(in);
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null; 
			while ((lineTxt = bufferedReader.readLine()) != null) {
				build.append(lineTxt);
			}
		} catch (IOException e) {
			throw e;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (read != null) {
					read.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
		return build.toString();
	}
	
	/**
	 * 通过传入的JobName，返回Job的config.xml
	 * http://192.168.1.145:8087/job/parameter_pipeline/config.xml
	 * @param jobName
	 * @return
	 */
	public String getConfigXml(String jobName) throws IOException {
		jobName = encode(jobName);
		return getConfigXml("", jobName);
	}
	
	/**
	 * 通过传入的JobName，返回Job的config.xml
	 * 带相对任务路径如：
	 * ccic_int/trunk/trunk-multibranch-micro-service-build
	 * /job/ccic_int/job/trunk/job/trunk-multibranch-micro-service-build/
	 * @param jobName
	 * @return
	 */
	public String getConfigXml(String jobPath, String jobName) throws IOException {
		jobName = encode(jobName);
		jobPath = getValidJobPath(jobPath);
		String path = jobPath + "job/" + jobName + "/config.xml";
		writeLog(jobName, path);
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		String configXml = client.get(path);
		return configXml;
	}
	
	public String getValidJobPath(String jobPath) {
		if (StringUtil.isNotEmpty(jobPath) && jobPath.length() > 1) {
			if (jobPath.indexOf("/job/") != -1) {
				jobPath = addSlash(jobPath, "/", "/", true);
				if (jobPath.endsWith("/job/")) {
					jobPath = jobPath.substring(0, jobPath.length() - 4);
				}
			} else {
				if (jobPath.startsWith("/")) {
					jobPath = jobPath.substring(1, jobPath.length());
				}
				if (jobPath.endsWith("/")) {
					jobPath = jobPath.substring(0, jobPath.length() - 1);
				}
				jobPath =  jobPath.replaceAll("/", "/job/");
				jobPath = "/job/" + jobPath + "/";
				
			}
		} else {
			jobPath = "/";
		}
		return jobPath;
	}
	
	/**
	 * 将str里面的splitSignRegex转换成slash，并且前后加上slash。
	 * 如：aaa/bbb > /aaa/bbb/
	 * "aaa":"bbb" > \"aaa\":\"bbb\"
	 * @param  str
	 * @param splitSign
	 * @param slash
	 * @param trimSlash 是:前后加入替换的slash字符。否：前后去除替换的slash字符
	 * @return
	 */
	public static String addSlash(String str, String splitSignRegex, String slash, boolean trimSlash) {
		if (StringUtil.isNotEmpty(str)) {
			str =  str.replaceAll(splitSignRegex, slash);
//			if (trimSlash && ! str.startsWith(slash)) {
//				str = slash +  str;
//			}
//			if (trimSlash && ! str.endsWith(slash)) {
//				str =  str + slash;
//			}
			
			if (trimSlash) {
				if (! str.startsWith(slash)) {
					str = slash +  str;
				}
				if (! str.endsWith(slash)) {
					str =  str + slash;
				}
			} else {
				if (str.startsWith(slash)) {
					str = str.substring(1, str.length());
				}
				if (str.endsWith(slash)) {
					str = str.substring(0, str.length() - 1);
				}
			}
		}
		return  str;
	}

	/**
	 * 终止Jenkins Job构建
	 * http://localhost:8087/job/itmp_maven/disable/api/json
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
	public void disableJenkinsJob(String jobName) throws IOException {
		jobName = encode(jobName);
		server.disableJob(jobName);
	}
	
	/**
	 * 启用Jenkins Job构建
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
	public void enableJenkinsJob(String jobName) throws IOException {
		jobName = encode(jobName);
		server.enableJob(jobName);
	}
	
	/**
	 *构建任务
	 *http://localhost:8087/job/itmp_maven/build/api/json
	 * @param job
	 * @throws IOException
	 */
	public void buildJenkinsJob(String jobPath, String jobName) throws IOException {
		jobName = encode(jobName);
		jobPath = getValidJobPath(jobPath);
		String path = jobPath + "job/" + jobName + "/build";
		writeLog(jobName, path);
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		client.post(path);
	}
	public void buildJenkinsJob(JobWithDetails job) throws IOException {
		job.build();
	}
	public void buildJenkinsJob(String jobPath, String jobName, Map paraMap) throws IOException {
		jobName = encode(jobName);
		String paraStr = "";
		if (paraMap != null && paraMap.size() > 0) {
			paraStr = StringUtils.join(Collections2.transform(paraMap.entrySet(), new MapEntryToQueryStringPair()), "&");
			paraStr = "?" + paraStr;
		}
		jobPath = getValidJobPath(jobPath);
		String path = jobPath + "job/" + jobName + "/buildWithParameters" + paraStr;
		writeLog(jobName, path);
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		client.post(path);
	}
	
	/**
	 * 获取下一次构建生成的Jenkins任务编号
	 * @param jobPath
	 * @param jobName
	 * @return
	 * @throws IOException
	 */
	public int getNextBuildNumber(String jobPath, String jobName) throws IOException {
		JobWithDetails job = getJenkinsJob(jobPath, jobName);
		int nextBuildNumber = 1;
		if (job != null) {
			nextBuildNumber = job.getNextBuildNumber();
		}
		return nextBuildNumber;
	}
	
	/**
	 * 通过jenkins任务编号获取日志
	 * @param jobPath
	 * @param jobName
	 * @param jobNumber
	 * @return
	 * @throws IOException
	 */
	public String getJenkinsLogByNumber(String jobPath, String jobName, int jobNumber) throws IOException {
		jobName = encode(jobName);
		jobPath = getValidJobPath(jobPath);
		String path = jobPath + "job/" + jobName;
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		path = path + "/" + jobNumber + "/consoleText";
		writeLog(jobName, path);
		String log = client.get(path);
		return log;
	}
	
	/**
	 * 获取Jenkins日志（全量）
	 * @param jobPath
	 * @param jobName
	 * @return
	 * @throws IOException
	 */
	public String getJenkinsLog(String jobPath, String jobName) throws IOException {
		jobName = encode(jobName);
		jobPath = getValidJobPath(jobPath);
		String path = jobPath + "job/" + jobName;
		writeLog(jobName, path);
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		JobWithDetails job = client.get(path, JobWithDetails.class);
		int lastNumber = job.getLastBuild().getNumber();
		
		path = path + "/" + lastNumber + "/consoleText";
		writeLog(jobName, path);
		String log = client.get(path);
		return log;
	}
	/**
	 * 获取Jenkins日志（增量）
	 * @param jobPath
	 * @param jobName
	 * @return
	 * @throws IOException
	 */
	public Map<String, String> getJenkinsBuildingLog(String jobPath, String jobName, String start, int jobNumber) throws Exception {
		Map<String, String> resultMap = new HashMap<String, String>();
		resultMap.put("start", "0");
		resultMap.put("log", "");
		jobName = encode(jobName);
		jobPath = getValidJobPath(jobPath);
		String path = jobPath + "job/" + jobName;
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		
		if (jobNumber <= 0) {
			JobWithDetails job = client.get(path, JobWithDetails.class);
			jobNumber = job.getLastBuild().getNumber();
		}
		
		path = path + "/" + jobNumber + "/logText/progressiveHtml?start=" + start;
		writeLog(jobName, path);
		HttpResponse response = null;
		try {
			List<NameValuePair> params=new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("start", start));
			response = client.post_form_with_result(path, params, false);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		if (response != null) {
			int statusCode = response.getStatusLine().getStatusCode();
			String resultStr = "";
			if (statusCode == 200) {
				Header sizeHeader = response.getFirstHeader("X-Text-Size");
				if (sizeHeader != null) {
					resultMap.put("start", sizeHeader.getValue());
				}
				resultStr = EntityUtils.toString(response.getEntity());
				resultMap.put("resultFlag", "1");//成功
			}
			resultMap.put("log", resultStr);
		}
		return resultMap;
	}
	/**
	 * 获取正在Build的Jenkins任务日志输出。
	 * http://192.168.1.145:8087/job/PipelineOne/120/consoleText
	 * @return
	 */
//	public String getJenkinsBuildingLog(String jobPath, String jobName) throws Exception {
//		return getJenkinsLog(jobPath, jobName);
//	}
	
	/**
	 * 部署暂停后手动操作继续执行调用
	 * job/zhoudu_itmp_1_45_packagedeploy/32/input/Interrupt1/proceedEmpty
	 * @param subUrl
	 * @return
	 * @throws IOException
	 */
	public void continuePipeline(String subUrl) throws IOException {
		if (!subUrl.startsWith("/")) {
			subUrl = "/" + subUrl;
		}
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		client.post(subUrl);
	}
	
	/**
	 * 停止正在Build的Jenkins
	 * http://192.168.1.145:8087/job/zhoudu_wms_DEV_44_deploy/22/stop
	 * @param jobName
	 */
	public void stopJenkinsBuilding(String jobPath, String jobName) throws Exception {
		for (int i = 0; i < 3; i++) {
			JobWithDetails job = getJenkinsJob(jobPath, jobName);
			if (job != null) {
				int lastNumber = job.getLastBuild().getNumber();//最后一次构建(可能正在构建)
				int lastCompleteNumber = job.getLastCompletedBuild().getNumber();//最后一次已完成构建
				if (lastNumber != lastCompleteNumber) {
					if (i != 0) {
						Thread.sleep(5000);
					}
					jobName = encode(jobName);
					jobPath = getValidJobPath(jobPath);
					String path = jobPath + "job/" + jobName + "/" + lastNumber + "/stop";
					JenkinsHttpConnection client = getJenkinsHttpConnection();
					client.post(path);
					Thread.sleep(1000);
				} else {
					break;
				}
			}
		}
	}
	
	/**
	 * 判断任务是不是正在构建
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
//	public boolean isJenkinsBuilding(String jobPath, String jobName) throws Exception {
//		jobName = encode(jobName);
//		boolean isBuilding = false;
//		JobWithDetails job = getJenkinsJob(jobPath, jobName);
//		int lastNumber = job.getLastBuild().getNumber();//最后一次构建(可能正在构建)
//		int lastCompleteNumber = job.getLastCompletedBuild().getNumber();//最后一次已完成构建
//		if (lastNumber != lastCompleteNumber) {
//			isBuilding = true;
//		}
//		return isBuilding;
//	}
	
	/**
	 * 判断任务是不是正在构建
	 * @param jobName
	 * @return
	 * @throws Exception
	 */
	public boolean isJenkinsBuilding(String jobPath, String jobName, int jobNumber) throws Exception {
		boolean isBuilding = false;
		JobWithDetails job = getJenkinsJob(jobPath, jobName);
		if (job != null) {
			Build build = job.getBuildByNumber(jobNumber);
			if (build != null) {
				BuildWithDetails detail = build.details();
				if (detail != null) {
					isBuilding = detail.isBuilding();
				}
				
			}
		}
		return isBuilding;
	}
	
	/**
	 * 验证Jenkins的Cron表达式
	 * http://192.168.1.145:8087/descriptorByName/hudson.triggers.TimerTrigger/checkSpec?value=H/15 * * * * 
	 * <div class=warning><img src='/static/a1a52d5a/images/none.gif' height=16 width=1>无计划因此不会运行</div>
	 * <div class=error><img src='/static/a1a52d5a/images/none.gif' height=16 width=1>Invalid input: &quot;H/5 * * *&quot;: line 1:10: expecting space, found &#039;null&#039;</div>
	 * <div class=ok><img src='/static/a1a52d5a/images/none.gif' height=16 width=1>上次运行的时间 2019年1月28日 星期一 下午04时20分23秒 CST; 下次运行的时间 2019年1月28日 星期一 下午04时25分23秒 CST.</div>
	 * @param cron
	 * @return
	 * @throws Exception
	 */
	public String validateJenkinsCron(String cron) throws Exception {
		String path = "/descriptorByName/hudson.triggers.TimerTrigger/checkSpec?value=" + cron;
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		return client.get(path);
	}
	
	/**
	 * 获取构建开始时间
	 * @param jobPath
	 * @param jobName
	 * @param jobNumber
	 * @return
	 * @throws Exception
	 */
	public Timestamp getJenkinsJobStartDate(String jobPath, String jobName, int jobNumber) throws Exception {
		Timestamp time = null;
		String timestamp = getJenkinsJobData(jobPath, jobName, jobNumber, "timestamp");
		time = new Timestamp(Long.parseLong(timestamp));
//		if (StringUtil.isNotEmpty(timestamp)) {
//			Date date = new Date(Long.parseLong(timestamp));
//			startDate = DateUtil.formatDate(date, DateUtil.fullFormat);
//		}
		return time;
	}
	/**
	 * 获取构建结束时间
	 * @param jobPath
	 * @param jobName
	 * @param jobNumber
	 * @return
	 * @throws Exception
	 */
	public Timestamp getJenkinsJobEndDate(String jobPath, String jobName, int jobNumber) throws Exception {
		Timestamp time = null;
		String timestamp = getJenkinsJobData(jobPath, jobName, jobNumber, "timestamp");
		String duration = getJenkinsJobData(jobPath, jobName, jobNumber, "duration");
		if (StringUtil.isNotEmpty(timestamp) && StringUtil.isNotEmpty(duration)) {
			long timestampValue = Long.parseLong(timestamp);
			long durationValue = Long.parseLong(duration);
			timestampValue = timestampValue + durationValue;
			time = new Timestamp(timestampValue);
//			Date date = new Date(timestampValue);
//			endDate = DateUtil.formatDate(date, DateUtil.fullFormat);
		}
		return time;
	}
	
	public String getJenkinsJobData(String jobPath, String jobName, int jobNumber, String para) throws Exception {
		jobName = encode(jobName);
		String data = "";
		if (jobNumber <= 0) {
			JobWithDetails job = getJenkinsJob(jobPath, jobName);
			jobNumber = job.getLastBuild().getNumber();//最后一次构建(可能正在构建)
		}
		jobPath = getValidJobPath(jobPath);
		jobPath = jobPath + "job/" + jobName + "/" + jobNumber;
//		writeLog(jobName, jobPath);
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		String buildJson = client.get(jobPath);
		if (StringUtil.isNotEmpty(buildJson)) {
			JSONObject obj = JSON.parseObject(buildJson);
			data = Objects.toString(obj.get(para), "");
		}
		return data;
	}
	
	/**
	 * 获取StageView列表内容
	 * http://192.168.1.145:8087/job/zhoudu_itmp_git_2_175_deploy/67/wfapi/describe
	 * @param jobPath
	 * @param jobName
	 * @param jobNumber
	 * @return
	 */
	public String getStageViewDescribeJson(String jobPath, String jobName, int jobNumber) throws Exception {
		jobName = encode(jobName);
		if (jobNumber <= 0) {
			JobWithDetails job = getJenkinsJob(jobPath, jobName);
			jobNumber = job.getLastBuild().getNumber();
		}
		jobPath = getValidJobPath(jobPath);
		jobPath = jobPath + "job/" + jobName + "/" + jobNumber + "/wfapi/describe";
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		String dataJson = "";
		try {
			dataJson = client.get(jobPath);
		} catch (Exception e) {
			logger.info("getStageViewDescribeJson:" + jobPath);
		}
		return dataJson;
	}
	
	/**
	 * 获取Jenkins的中断内容
	 * http://192.168.1.145:8087/job/zhoudu_itmp_1_45_deploy/523/wfapi/pendingInputActions
	 * @param jobPath
	 * @param jobName
	 * @param jobNumber
	 * @return
	 */
	public String getNextPendingInputAction(String jobPath, String jobName, int jobNumber) throws Exception {
		jobName = encode(jobName);
		if (jobNumber <= 0) {
			JobWithDetails job = getJenkinsJob(jobPath, jobName);
			jobNumber = job.getLastBuild().getNumber();
		}
		jobPath = getValidJobPath(jobPath);
		jobPath = jobPath + "job/" + jobName + "/" + jobNumber + "/wfapi/nextPendingInputAction";
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		String dataJson = "";
		try {
			dataJson = client.get(jobPath);
		} catch (Exception e) {
			logger.info("getStageViewDescribeJson:" + jobPath);
		}
		return dataJson;
	}
	
	/**
	 * 执行中断继续或者停止
	 * /job/wms_git_20190806_2_176_deploy/10/input/Interrupt2/proceedEmpty
	 * @param jobPath
	 * @param jobName
	 * @param jobNumber
	 * @param interruptId
	 * @param flag
	 * @throws Exception
	 */
	public void getStageViewNextPending(String jobPath, String jobName, int jobNumber, String interruptId, Integer flag) throws Exception {
		jobName = encode(jobName);
		if (jobNumber <= 0) {
			JobWithDetails job = getJenkinsJob(jobPath, jobName);
			jobNumber = job.getLastBuild().getNumber();
		}
		jobPath = getValidJobPath(jobPath);
		String methodStr = "abort";
		if (flag == 1) {
			methodStr = "proceedEmpty";
		}
		jobPath = jobPath + "job/" + jobName + "/" + jobNumber + "/input/" + interruptId + "/" + methodStr;
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		client.post(jobPath);
	}
	
	/**
	 * 获取StageView列表子页execution内容
	 * http://192.168.1.145:8087/job/zhoudu_itmp_git_2_175_deploy/67/execution/node/10/wfapi/describe
	 * @param jobPath
	 * @param jobName
	 * @param jobNumber
	 * @return
	 */
	public String getStageViewDescribeExecutionJson(String jobPath, String jobName, int jobNumber, int describeId) throws Exception {
		jobName = encode(jobName);
		if (jobNumber <= 0) {
			JobWithDetails job = getJenkinsJob(jobPath, jobName);
			jobNumber = job.getLastBuild().getNumber();
		}
		jobPath = getValidJobPath(jobPath);
		jobPath = jobPath + "job/" + jobName + "/" + jobNumber + "/execution/node/" + describeId + "/wfapi/describe";
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		String dataJson = "";
		try {
			dataJson = client.get(jobPath);
		} catch (Exception e) {
			logger.info("getStageViewDescribeExecutionJson:" + jobPath);
		}
		return dataJson;
	}
	
	/**
	 * 获取StageView列表子页execution的详细日志内容
	 * http://192.168.1.145:8087/job/zhoudu_itmp_git_2_175_deploy/67/execution/node/11/wfapi/log
	 * @param jobPath
	 * @param jobName
	 * @param jobNumber
	 * @param describeId
	 * @return
	 * @throws Exception
	 */
	public String getStageViewExecutionLogJson(String jobPath, String jobName, int jobNumber, int executionId) throws Exception {
		jobName = encode(jobName);
		if (jobNumber <= 0) {
			JobWithDetails job = getJenkinsJob(jobPath, jobName);
			jobNumber = job.getLastBuild().getNumber();
		}
		jobPath = getValidJobPath(jobPath);
		jobPath = jobPath + "job/" + jobName + "/" + jobNumber + "/execution/node/" + executionId + "/wfapi/log";
		JenkinsHttpConnection client = getJenkinsHttpConnection();
		String dataJson = "";
		try {
			dataJson = client.get(jobPath);
		} catch (Exception e) {
			logger.info("getStageViewExecutionLogJson:" + jobPath);
		}
		return dataJson;
	}

	/**
	 * 删除Jenkins的Workspace目录下无效项目数据（删除任务时不会删除项目文件）
	 * @param url Jenkins地址URL，例："http://*.*.*.*:8091/jenkins"
	 * @param workspaceDir Jenkins节点本地Workspace目录，例：“D:\jenkins\workspace”
	 * @throws Exception
	 */
	public void deleteJenkinsWorkspaceFile(String url) throws Exception {
		JenkinsHttpConnection httpConn = getJenkinsHttpConnection();
		String jenkinsJson = httpConn.get(url + "/computer/api/json");
		JSONObject obj = JSON.parseObject(jenkinsJson);
		Object computerObj = obj.get("computer");
		
		String workspaceDir = "";
		String s = "";
		/* 主要逻辑：逐个验证本地workspace下的目录（即job名称）是否在刚查询到的任务名称清单中存在 */
		File dir = new File(workspaceDir);
		for (File f : dir.listFiles()) {
			if (s.indexOf(f.getName() + '\n') == -1) {//为保证避免目录名是任务名的子串情况，加'\n'进行严格查找
				System.out.println(f.getName() + " not exists!");
				deleteFile(f);
				if (f.exists()) {
					System.out.println(f.getName() + " can 【not】 be deleted!");
				} else {
					System.out.println(f.getName() + " was deleted!");
				}
 
			}
		}
		System.out.println("Done!");
		
	}

	public void deleteFile(File file) throws Exception {
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				deleteFile(f);
			}
		}
		if (!file.delete()) {
			logger.info("deleteJenkinsWorkspaceFile file delete failed!!!");
		}
		
	}
	
	/**
	 * 获取流水线模板
	 * @param pipelineType 1为普通流水线模板，2 用于快照转releases的Jenkins模板 
	 * @return
	 */
	private String getJenkinsJobPipelineXml(int pipelineType) {
		String jenkinsJobPipelineXml = "";
		if (pipelineType == 1) {//标准流水线任务模板
			jenkinsJobPipelineXml = "<flow-definition plugin=\"workflow-job@2.25\">\r\n" + 
					"	<actions />\r\n" + 
					"	<description>自动构建</description>\r\n" + 
					"	<keepDependencies>false</keepDependencies>\r\n" + 
					"	<properties>\r\n" + 
					"		<org.jenkinsci.plugins.workflow.job.properties.DisableConcurrentBuildsJobProperty/>\r\n" + 
					"		<jenkins.model.BuildDiscarderProperty>\r\n" + 
					"			<strategy class=\"hudson.tasks.LogRotator\">\r\n" + 
					"				<daysToKeep>-1</daysToKeep>\r\n" + 
					"				<numToKeep>-1</numToKeep>\r\n" + 
					"				<artifactDaysToKeep>-1</artifactDaysToKeep>\r\n" + 
					"				<artifactNumToKeep>-1</artifactNumToKeep>\r\n" + 
					"			</strategy>\r\n" + 
					"		</jenkins.model.BuildDiscarderProperty>\r\n" + 
					"	</properties>\r\n" + 
					"	<definition\r\n" + 
					"		class=\"org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition\"\r\n" + 
					"		plugin=\"workflow-cps@2.54\">\r\n" + 
					"		<script>\r\n" + 
					"		</script>\r\n" + 
					"		<sandbox>true</sandbox>\r\n" + 
					"	</definition>\r\n" + 
					"	<triggers />\r\n" + 
					"	<quietPeriod>0</quietPeriod>\r\n" + 
					"	<disabled>false</disabled>\r\n" + 
					"</flow-definition>";
		} else if (pipelineType == 2) {//用于快照转releases的Jenkins模板 
			jenkinsJobPipelineXml = "<project>\r\n" + 
					"	<description>包件管理打标签时执行构建任务：实现将SNAPSHOT快照转换成Release生产版本上传到Nexus\r\n" + 
					"	</description>\r\n" + 
					"	<keepDependencies>false</keepDependencies>\r\n" + 
					"	<properties>\r\n" + 
					"		<jenkins.model.BuildDiscarderProperty>\r\n" + 
					"			<strategy class=\"hudson.tasks.LogRotator\">\r\n" + 
					"				<daysToKeep>-1</daysToKeep>\r\n" + 
					"				<numToKeep>2</numToKeep>\r\n" + 
					"				<artifactDaysToKeep>-1</artifactDaysToKeep>\r\n" + 
					"				<artifactNumToKeep>-1</artifactNumToKeep>\r\n" + 
					"			</strategy>\r\n" + 
					"		</jenkins.model.BuildDiscarderProperty>\r\n" + 
					"	</properties>\r\n" + 
					"	<scm class=\"hudson.scm.NullSCM\" />\r\n" + 
					"	<canRoam>true</canRoam>\r\n" + 
					"	<disabled>false</disabled>\r\n" + 
					"	<blockBuildWhenDownstreamBuilding>false\r\n" + 
					"	</blockBuildWhenDownstreamBuilding>\r\n" + 
					"	<blockBuildWhenUpstreamBuilding>false</blockBuildWhenUpstreamBuilding>\r\n" + 
					"	<triggers />\r\n" + 
					"	<concurrentBuild>false</concurrentBuild>\r\n" + 
					"	<builders>\r\n" + 
					"		<hudson.tasks.Shell>\r\n" + 
					"			<command></command>\r\n" + 
					"		</hudson.tasks.Shell>\r\n" + 
					"	</builders>\r\n" + 
					"	<publishers />\r\n" + 
					"	<buildWrappers />\r\n" + 
					"</project>";
		}
		return jenkinsJobPipelineXml;
	}

	/**
	 * runScript:http //localhost:8087/scriptText/api/json 运行一个基于groovy的脚本测试，无实际意义
	 * getComputers http://localhost:8087/computer/api/xml 获取到当前电脑的一些基本信息：比如内存、Jenkins安装目录、操作系统
	 * getComputerSet http://localhost:8087/computer/api/xml?depth=2 获取Jenkins任务列表、地址、及getComputers里面的基本信息
	 * job.updateDescription http://localhost:8087/job/itmp_maven/updateDescription 更新任务描述
	 * @param args
	 * @throws Exception
	 */
//	public static void main(String[] args) throws Exception {
	
//	String jobName = "deploy_ccic_int_uat_single_patch";
//	String jobName = "wms_git_20190806_2_176";
	
//	JenkinsUtil ju = new JenkinsUtil("http://192.168.137.215:8080/jenkins", "admin", "admin");
//	JenkinsUtil ju = new JenkinsUtil("http://192.168.1.145:8087", "admin", "admin");
//	String ss = ju.getConfigXml("ccic_int/trunk/trunk-multibranch-micro-service-build",jobName);
	
//	JenkinsHttpConnection client = ju.getJenkinsHttpConnection();
//	
//	JobWithDetails job = ju.getJenkinsJob(null, "test_wms_deploy");
//	System.out.println(job.getDisplayName());
	
//	job.build();
//	String jobPath = "";
//	String log = ju.getJenkinsLog("ccic_int/trunk/trunk-multibranch-micro-service-build", jobName);
	
	
//	JobWithDetails aaa = client.get("/job/ccic_int/job/trunk/job/trunk-multibranch-micro-service-build/job/trunk%2Ftool", JobWithDetails.class);
//
//	String log = ju.getJenkinsLog(job);
//	Map<String, String> parameterMap = new HashMap<String, String>();
//	parameterMap.put("build_version", "1.1.0B01");
//	parameterMap.put("sonar_run", "false");
//	ju.buildJenkinsJob("/job/ccic_int/job/trunk/job/trunk-multibranch-micro-service-build/", jobName, parameterMap);
//	job.build(parameterMap);
//	System.out.println(sss);
//	ju.getJenkinsJobStartDate("deploy/ccic_int_deploy", jobName, 0);
//	ju.isJenkinsBuilding("", jobName, 8);
//	ju.stopJenkinsBuilding("ccic_int/release/multibranch-micro-service-Release", jobName);
//	Element root = ju.getSAXElementByPath(JenkinsUtil.JENKINS_XML_PATH + JenkinsUtil.JENKINS_JOB_SNAPSHOTS2RELEASE_XML);
//	System.out.println(JenkinsUtil.class.getResourceAsStream("/jenkins/jenkins_job_maven.xml"));
//	System.out.println(System.getProperty("user.dir"));
//		
//	}
//	System.out.println("ok");
//} 
	
//	public static void main(String[] args) {
//		BCryptPasswordEncoder en = new BCryptPasswordEncoder();
//		String pass = en.encode("pangu123");
//		System.out.println(pass); 
//	}
	
	private static class MapEntryToQueryStringPair implements Function<Map.Entry<String, String>, String> {
        @Override
        public String apply(Map.Entry<String, String> entry) {
            Escaper escaper = UrlEscapers.urlFormParameterEscaper();
            return escaper.escape(entry.getKey()) + "=" + escaper.escape(entry.getValue());
        }
    }
}
