package cn.pioneeruniverse.dev.service.structure;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.dev.entity.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.pioneeruniverse.common.sonar.bean.SonarQubeException;

public interface IStructureService {
	Map<String, Object> getSystemModule(Integer id);
	long  creOrUpdSjenkins(TblSystemJenkins tblSystemJenkins,String env,String createType,String jobType);
	//	TblSystemSonar  creOrUpdSonar(TblSystemSonar tblSystemSonar,long sonarId);
	long  insertJenkinsJobRun(TblSystemJenkinsJobRun tblSystemJenkinsJobRun);
	long  insertJenkinsModuleJobRun(TblSystemModuleJenkinsJobRun tblSystemModuleJenkinsJobRun);
	long  insertJenkinsModuleJobRunNew(TblSystemModuleJenkinsJobRun tblSystemModuleJenkinsJobRun);
	TblSystemScm selectBysystemId(long id);
	TblToolInfo geTblToolInfo(long id);
	void updateJenkinsJobRunLog(String jboname,String log,Integer status);
	TblSystemInfo geTblSystemInfo(long id);
	List<TblSystemScm> getScmByParam(Map<String, Object> map);
	List<TblSystemJenkins> getsystemJenkinsByParam(Map<String, Object> map);
	List<TblSystemModuleScm> getModuleScmByParam(Map<String, Object> map);
	TblSystemModule getTblsystemModule(long id);
	TblSystemModuleScm getSystemModuleScm(long id);
	void updateJobRun(TblSystemJenkinsJobRun tblSystemJenkinsJobRun);
	List<TblSystemJenkinsJobRun> selectLastTimeBySystemId(long id);
	List<TblSystemJenkinsJobRun> selectNowTimeBySystemId(long id);
	List<TblSystemModule> selectSystemModule(long systemId);
	List<TblSystemModuleJenkinsJobRun>	selectLastTimeByModuleId (long moduleId);
	TblSystemJenkinsJobRun	selectBuildMessageById(long jobrunId);
	List<Map<String, Object>> selectMessageBySystemId(long id,String type);
	List<Map<String, Object>> selectMessageBySystemIdAndPage(long id,Integer pageNumber ,Integer pageSize,String type);
	List<TblProjectInfo> getAllprojectByUser(HttpServletRequest request);
	List<Map<String, Object>> selectModuleBuildMessage(Map<String, Object> map);
	List<Map<String, Object>> selectModuleBuildMessageNow(Map<String, Object> map);
	List<Map<String, Object>> selectModuleBuildMessagesNow(Map<String, Object> map);
	List<TblSystemModuleScm> judge(String moduleid, Long scmId, Integer systemId);
	void updateSystemScmBuildStatus(TblSystemScm t);
	List<TblSystemScm> selectBuildingBySystemid(Long id);
	List<TblSystemScm> judgeScmBuildStatus(Long id);
	int countScmBuildStatus(Map<String, Object> map);
	List<TblSystemModuleJenkinsJobRun> selectSonarBySystemidAndDate(Map<String, Object> map);
	List<Map<String, Object>> selectModuleJobRunByjobRunId(long jobRunId);
	int countTaskBySystemId(Long id);
	void sonarDetail(String pKeysMid,Timestamp timestamp,String sonarToolId,String  Flag,long jobId,Timestamp createTime, String moduleJson)throws SonarQubeException;
	TblSystemModuleJenkinsJobRun selectByModuleRunId(long id);
	List<TblSystemModuleJenkinsJobRun> selectSonarBySystemidAndDateMincro(Map<String, Object> map);
	public List<TblSystemSonar> selectSonarByMap(Map<String, Object> columnMap);
	public String getSonarIssue(String toolId,String dateTime,String projectKey,String type,String p) ;
	public List<TblSystemSonar> getSonarByMap(Map<String, Object> columnMap);
	public Long getRandomSonarId(String env);
	List<TblSystemJenkins> selectJenkinsByMap(Map<String, Object> map);
	List<TblSystemJenkinsJobRun> selectLastTimeBySystemIdManual(Long id);
	void updateJenkins(TblSystemJenkins tblSystemJenkins);
	TblSystemSonar creOrUpdSonarManual(TblSystemSonar tblSystemSonar);
	List<TblSystemJenkins> getSystemJenkinsByMap(Map<String, Object> colMap);
	List<TblSystemModuleJenkinsJobRun> selectSonarBySystemidAndDateManual(Map<String, Object> mapParam);
	void sonarDetailManual(String jobName,String systemSonarId, Timestamp timestamp, String sonarName,String moduleJobRunId,String flag,long jobId);
	List<Map<String, Object>> selectModuleJobRunByjobRunIdManual(long parseLong);
	List<TblSystemJenkinsJobRun> selectLastTimeBySystemIdManualDeploy(Long id);
	void updateModuleJonRun(TblSystemModuleJenkinsJobRun tmjr);
	List<TblArtifactTag>  getArtifactTag(Map<String, Object> colMap);
	public Map<String, Object> creatJenkinsJob(String sysId, String systemName, String serverType, String modules,
											   String env,String sonarflag, HttpServletRequest request);
	Map<String, Object> creatJenkinsJobBatch(String env, String data, HttpServletRequest request);
	public Map<String, Object> getBuildMessageById(String jobRunId, String createType);
	public Map<String, Object> getSonarMessage(String systemId, String startDate, String endDate);
	public Map<String, Object> getSonarMessageMincro(String systemId, String startDate, String endDate);
	public Map<String, Object> buildJobManual(HttpServletRequest request, String systemJenkisId, String jsonParam);
	public Map<String, Object> getSonarMessageManual(String systemId, String startDate, String endDate);
	public Map<String, Object> getAllSystemInfo(String systemInfo, Integer pageSize, Integer pageNum,
												String scmBuildStatus,Long uid, String[] refreshIds, String[] checkIds, String[] checkModuleIds,HttpServletRequest request);
	void detailErrorStructure(TblSystemJenkins tblSystemJenkins,String jobType,String flag);
	List<TblSystemScm> selectScmByMap(Map<String, Object> paramMap);
	void detailAutoErrorStructure(TblSystemScm scm,String jobType,String flag,String jenkinsId)  throws Exception;
	Map<String, Object> getAllSystemInfoByBuilding(String ids);
	Map<String, Object> creatJenkinsJobScheduled(String sysId, String systemName, String serverType, String env,
												 HttpServletRequest request,TblSystemJenkins tblSystemJenkins);
	List<TblSystemScm> selectSystemScmByMap(Map<String, Object> param);
	public Map<String, Object> buildJobManualScheduled(HttpServletRequest request, String systemJenkisId);
	//List<TblSystemJenkins> selectCornJenkinsByMap(Map<String, Object> systemJenkinsParam);
	void insertSystemJenkins(TblSystemJenkins tblSystemJenkins);
	public TblToolInfo getEnvtool(String env,String toolType);
	TblSystemJenkins selectSystemJenkinsById(String systemJenkinsId);
	void updateSystemScm(TblSystemScm tblSystemScm);
	void updateModuleStatusBySystemId(Map<String, Object> map);
	void updateModuleScm(Map<String, Object> moduleScmid);
	void updateConfigInfo(Map<String, Object> configMap, Map<String, Object> paramMap);
	public List<Map<String, Object>> getBreakName(String createType,long systemId,String jobType);
	List<TblToolInfo> getTblToolInfo(Map<String, Object> map);
	void detailArtAutoErrorStructure(TblSystemJenkins tblSystemJenkins, String string);
	void insertModuleRunCallBack(List<Integer> moduleRunIds, long jobid, Timestamp timestamp, String string,Timestamp startTime,String moduleJson);
	void judgeJenkins(TblSystemJenkins tblSystemJenkins, String jobName);
	String getJobNameByEnv(String env, long systemId, String createType, String jobType,int flag);
	TblSystemScm getTblsystemScmById(long id);
	Map<String, Object> getEnvBySystemId(long systemId);
	public String detailByteBylog(String log,int defaultSize);
	
	Integer getJobRunLastJobNumber(TblSystemJenkinsJobRun jobRun);

	void detailAutoLog(Map<String, Object> map);

	List<Map<String, Object>> detailSprint(List<TblSprintInfo> sprintInfoList,long systemId);

    void getDeleteEnvName(String tbl_system_scm_environment_type, Map<String, Object> envMap);

    void updateModuleInfoFristCompile(List<String> moduleList);


    void detailU(Map<String,Object> map);
    /**
    *@author liushan
    *@Description 根据系统id查询系统信息
    *@Date 2019/11/20
    *@Param [systemId]
    *@return cn.pioneeruniverse.dev.entity.TblSystemInfo
    **/
	TblSystemInfo getTblSystemInfoById(Long systemId);


    String selectModulesNamesByRunId(String id);
}
