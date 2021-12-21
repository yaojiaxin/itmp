package cn.pioneeruniverse.dev.service.deploy;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.dev.entity.*;

public interface IDeployService {
	public Map<String, Object> getAllManualSystemInfo(String systemInfo, Integer pageSize, Integer pageNum,
													  String scmBuildStatus,Long uid, String[] refreshIds, String[] checkIds, String[] checkModuleIds,HttpServletRequest request);
	public Map<String, Object> getDeployJobName(String systemId,HttpServletRequest request) ;
	public Map<String, Object> getJobNameParam(String systemJenkisId) ;
	public Map<String, Object> buildJobManualDeploy(HttpServletRequest request, String systemJenkisId,String jsonParam);
	public Map<String, Object> buildJobAutoDeploy(HttpServletRequest request, String sysId,String serverType,String env,String systemName,String modules );
	public List<Map<String, Object>> selectMessageBySystemIdAndPage(long id, Integer pageNumber, Integer pageSize,
																	String type,Integer jobType);
	public Map<String, Object> getDeployMessageById(String jobRunId, String createType);
	public Map<String, Object> buildJobAutoDeployScheduled(HttpServletRequest request, String sysId, String serverType,
														   String env, String systemName,TblSystemJenkins tblSystemJenkins);
	public Map<String, Object> buildJobAutoDeployBatch(String env, String data, HttpServletRequest request);
	public Map<String, Object> getSonarMessageMincro(String systemId, String startDate, String endDate);
	public Map<String, Object> getSonarMessage(String systemId, String startDate, String endDate);
	public Map<String, Object> buildManualdeployScheduled(HttpServletRequest request, String systemJenkisId);
	public List<TblSystemModule> getModuleInfo(String systemId);
	public List<Map<String,Object>> getArtifactInfo(String systemId, String moduleId, String env);

	public Map<String, Object> buildPackageDeploy(HttpServletRequest request, String sysId,
												  String env, String[] artifactids);

	Map<String, Object> pushData(TblSystemInfo system,Long [] featureIds,
								 VersionInfo versionInfo,String systemPackageName,List<Map<String,Object>> subSystemList);

	void updateCommissioningResultData(Map<String, Object> data);
	public List<TblCommissioningWindow> getWindowsByartId(String artifactids);

	List<Map<String ,Object>> getWindowsBySystemId(long systemId,String env);

	List<TblCommissioningWindow> getWindowsLimit(long systemId);

	List<DevTaskVo> getReqFeaByartId(String artifactids);

	List<Map<String,Object>>  getTestConfig(long systemId, List<String> moduleIds, String envType,String testType);

	public void detailTestRequestNumber(Map<String,Object> map);
	public void callBackAutoTest(Map<String, Object> map);

    String goContinueLog(String url, String toolId);

    String judgeFeatureIds(Long[] featureIds);

    void detailEnvDate(String reqFeatureqIds, String envType, Timestamp timestamp);

    List<TblSystemDeployeUserConfig> getDeployUsers(Long systemId);

	void addDeployUser(TblSystemDeployeUserConfig tblSystemDeployeUserConfig, HttpServletRequest request);

	void updateDeployUser(TblSystemDeployeUserConfig tblSystemDeployeUserConfig, HttpServletRequest request);

	void deleteDeployUser(Long id,HttpServletRequest reques);

	List<Map<String,Object>>  getEnvsBySystemId(long systemId);
}
