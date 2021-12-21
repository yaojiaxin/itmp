package cn.pioneeruniverse.dev.service.structure.impl;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.sonar.bean.SonarQubeException;
import cn.pioneeruniverse.common.sonar.client.SonarQubeClientApi;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.dao.mybatis.*;
import cn.pioneeruniverse.dev.entity.*;
import cn.pioneeruniverse.dev.feignInterface.DevManageToSystemInterface;
import cn.pioneeruniverse.dev.service.AutomatTest.AutomatTestService;
import cn.pioneeruniverse.dev.service.build.IJenkinsBuildService;
import cn.pioneeruniverse.dev.service.structure.IStructureService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Service("iStructureService")
public class StructureServiceImpl implements IStructureService {
    private final static Logger log = LoggerFactory.getLogger(StructureServiceImpl.class);

    @Autowired
    private TblSystemInfoMapper tblSystemInfoMapper;
    @Autowired
    private TblSystemModuleMapper tblSystemModuleMapper;
    @Autowired
    private TblSystemJenkinsMapper tblSystemJenkinsMapper;
    @Autowired
    private TblSystemJenkinsJobRunMapper tblSystemJenkinsJobRunMapper;
    @Autowired
    private TblSystemModuleJenkinsJobRunMapper tblSystemModuleJenkinsJobRunMapper;
    @Autowired
    private TblSystemScmMapper tblSystemScmMapper;
    @Autowired
    private TblToolInfoMapper tblToolInfoMapper;
    @Autowired
    private TblSystemSonarMapper tblSystemSonarMapper;
    @Autowired
    private TblSystemModuleScmMapper tblSystemModuleScmMapper;
    @Autowired
    private TblProjectInfoMapper tblProjectInfoMapper;
    @Autowired
    private TblRequirementFeatureMapper tblRequirementFeatureMapper;
    @Autowired
    private IJenkinsBuildService iJenkinsBuildService;
    @Autowired
    RedisUtils redisUtils;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private DevManageToSystemInterface systemInterface;
    @Autowired
    private TblSystemAutomaticTestResultMapper tblSystemAutomaticTestResultMapper;
    @Autowired
    private DevManageToSystemInterface devManageToSystemInterface;
    @Autowired
    private AutomatTestService automatTestService;
    @Autowired
    private TblUserInfoMapper tblUserInfoMapper;
    @Autowired
    private TblCommissioningWindowMapper tblCommissioningWindowMapper;
    private static ExecutorService threadPool;

    {
        threadPool = Executors.newCachedThreadPool();

    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemModule(Integer id) {
        Map<String, Object> map = new HashMap<>();
        try {

            List<TblSystemModule> list = tblSystemModuleMapper.selectSystemModule((long) id);
            map.put("data", JSONObject.toJSONString(list));
        } catch (Exception e) {

            this.handleException(e);
            throw e;
        }
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public TblToolInfo getEnvtool(String env, String toolType) {

        Map<String, Object> sonarParam = new HashMap<>();
        sonarParam.put("toolType", toolType);
        sonarParam.put("environmentType", env);
        List<TblToolInfo> jenktools = tblToolInfoMapper.selectToolByEnv(sonarParam);
        // 随机取一个值
        int n = this.getRandom(jenktools.size());
        TblToolInfo tblToolInfo = jenktools.get(n);
        return tblToolInfo;
    }


    @Override
    @Transactional(readOnly = false)
    public long insertJenkinsJobRun(TblSystemJenkinsJobRun tblSystemJenkinsJobRun) {

        try {
            tblSystemJenkinsJobRunMapper.insertNew(tblSystemJenkinsJobRun);

            return tblSystemJenkinsJobRun.getId();
        } catch (Exception e) {

            this.handleException(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public long insertJenkinsModuleJobRun(TblSystemModuleJenkinsJobRun tblSystemModuleJenkinsJobRun) {
        try {
            tblSystemModuleJenkinsJobRunMapper.insertNew(tblSystemModuleJenkinsJobRun);
            return tblSystemModuleJenkinsJobRun.getId();
        } catch (Exception e) {

            this.handleException(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public long insertJenkinsModuleJobRunNew(TblSystemModuleJenkinsJobRun tblSystemModuleJenkinsJobRun) {

        try {
            tblSystemModuleJenkinsJobRunMapper.insertSelective(tblSystemModuleJenkinsJobRun);

            return tblSystemModuleJenkinsJobRun.getId();
        } catch (Exception e) {

            this.handleException(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TblSystemScm selectBysystemId(long id) {
        try {
            List<TblSystemScm> list = tblSystemScmMapper.getBySystemId(id);
            if (list != null && list.size() > 0) {

                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {

            this.handleException(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TblToolInfo geTblToolInfo(long id) {
        try {
            return tblToolInfoMapper.selectByPrimaryKey(id);
        } catch (Exception e) {
            this.handleException(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void updateJenkinsJobRunLog(String jobName, String log, Integer status) {
        try {
            TblSystemJenkinsJobRun tjr = new TblSystemJenkinsJobRun();
            List<TblSystemJenkinsJobRun> jobRuns = tblSystemJenkinsJobRunMapper.selectLastTimeByJobName(jobName);
            long id = jobRuns.get(0).getId();
            tjr.setId(id);
            tjr.setBuildLogs(log);
            tjr.setBuildStatus(status);
            tblSystemJenkinsJobRunMapper.updateByPrimaryKey(tjr);
        } catch (Exception e) {

            throw e;
        }

    }


    @Transactional(readOnly = false)
    public TblSystemSonar creOrUpdSonar(TblSystemSonar tblSystemSonar, long sonarId, String env) {

        try {
            TblSystemSonar sonarTemp = new TblSystemSonar();
            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("systemId", tblSystemSonar.getSystemId());
            mapParam.put("systemScmId", tblSystemSonar.getSystemScmId());
            mapParam.put("systemModuleId", tblSystemSonar.getSystemModuleId());
            mapParam.put("systemJenkinsId", tblSystemSonar.getSystemJenkinsId());
            List<TblSystemSonar> sonarTemps = tblSystemSonarMapper.selectByMapLimit(mapParam);
            if (sonarTemps != null && sonarTemps.size() > 0) {
                sonarTemp = sonarTemps.get(0);
                sonarTemp.setLastUpdateDate(tblSystemSonar.getCreateDate());
                sonarTemp.setLastUpdateBy(tblSystemSonar.getCreateBy());
                //判断sonar环境是否修改
                long sonarToolId = judgeSonar(sonarTemp.getToolId(), env);
                sonarTemp.setToolId(sonarToolId);
                tblSystemSonarMapper.updateById(sonarTemp);
                // 有数据
                return sonarTemp;
            } else {
                // 插入数据
                tblSystemSonar.setToolId(sonarId);
                tblSystemSonarMapper.insertNew(tblSystemSonar);
                return tblSystemSonar;
            }
        } catch (Exception e) {
            this.handleException(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TblSystemInfo geTblSystemInfo(long id) {

        try {
            return tblSystemInfoMapper.selectById(id);
        } catch (Exception e) {
            this.handleException(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemScm> getScmByParam(Map<String, Object> map) {

        try {
            return tblSystemScmMapper.selectByMap(map);
        } catch (Exception e) {
            this.handleException(e);
            throw e;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public TblSystemModule getTblsystemModule(long id) {

        return tblSystemModuleMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemModuleScm> getModuleScmByParam(Map<String, Object> map) {

        return tblSystemModuleScmMapper.getModuleScmByParam(map);
    }

    @Override
    public TblSystemModuleScm getSystemModuleScm(long id) {

        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemJenkins> getsystemJenkinsByParam(Map<String, Object> map) {
        try {
            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("systemId", map.get("systemId"));
            mapParam.put("systemScmId", map.get("systemScmId"));
            List<TblSystemJenkins> list = tblSystemJenkinsMapper.selectByParam(mapParam);
            return list;
        } catch (Exception e) {
            this.handleException(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemJenkins> getSystemJenkinsByMap(Map<String, Object> map) {

        List<TblSystemJenkins> list = tblSystemJenkinsMapper.selectByMap(map);
        return list;
    }

    @Override
    @Transactional(readOnly = false)
    public void updateJobRun(TblSystemJenkinsJobRun tblSystemJenkinsJobRun) {

        tblSystemJenkinsJobRunMapper.updateJobRun(tblSystemJenkinsJobRun);
    }


    @Override
    @Transactional(readOnly = true)
    public List<TblProjectInfo> getAllprojectByUser(HttpServletRequest request) {
        String id = String.valueOf(CommonUtil.getCurrentUserId(request));
        //Boolean flag =flagAdmin(id);
        LinkedHashMap codeMap = (LinkedHashMap) redisUtils.get(CommonUtil.getToken(request));
        List<String> roleCodes = (List<String>) codeMap.get("roles");
        if (roleCodes != null && roleCodes.contains("XITONGGUANLIYUAN")) {
            return tblProjectInfoMapper.selectAllProject();
        } else {
            return tblProjectInfoMapper.selectAllProjectByUser(Long.parseLong(id));
        }
    }

    private Boolean flagAdmin(String userId) {
        Map<String, Object> userMap = systemInterface.findUserById(Long.valueOf(userId));
        Boolean flag = false;
        if (userMap != null) {
            Map<String, Object> user = JSON.parseObject(userMap.get("data").toString());
            JSONArray array = JSON.parseArray(user.get("userRoles").toString());

            for (int i = 0; i < array.size(); i++) {
                JSONObject json = array.getJSONObject(i);
                if (json.get("id").toString().equals("1")) {
                    flag = true;
                }
            }
        }
        return flag;
    }


    @Override
    @Transactional(readOnly = false)
    public void sonarDetail(String pKeysMid, Timestamp endTime, String sonarToolId, String Flag, long jobId, Timestamp startTime,String moduleJson)
            throws SonarQubeException {
        try {
            // 获取sonartoken
            try {
                Thread.sleep(7000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            TblToolInfo sonartool = new TblToolInfo();
            sonartool.setId(Long.parseLong(sonarToolId));
            sonartool = tblToolInfoMapper.selectOne(sonartool);
            String sonarUrl = "http://" + sonartool.getIp() + ":" + sonartool.getPort() + "/";
            String token = sonartool.getSonarApiToken();
            SonarQubeClientApi sonar = new SonarQubeClientApi(sonarUrl, token);
            Map<String,Object> moduleList = (Map<String,Object>) JSONArray.parse(moduleJson);
            // 微服务
            JSONArray jArray = JSONObject.parseArray(pKeysMid);
            for (int i = 0; i < jArray.size(); i++) {
                JSONObject jsobject = jArray.getJSONObject(i);
                String projectKey = jsobject.getString("projectKey");
                String modulejobRunId = jsobject.getString("moduleRunId");

                //增加判断此项目是否正在运行
                for (int z = 0; z < 720; z++) {
                    try {
                        boolean flag = sonar.getCeApi().isProgress(projectKey, sonarUrl, "IN_PROGRESS");

                        log.info("sonar正在分析判断" + flag);
                        if (flag == true) {

                            Thread.sleep(5000);
                            log.info("sonar正在分析中");

                        } else {

                            break;
                        }
                    } catch (Exception e) {
                        try {
                            Thread.sleep(3000);
                            log.info("sonar分析接口出现问题下次循环" + z);
                        } catch (Exception e1) {

                        }
                        continue;
                    }
                }
                log.info("跳出sonar分析接口");

                String result = sonar.getMeasuresApi().searchByUrl(projectKey,
                        "alert_status,bugs,vulnerabilities,code_smells,coverage,duplicated_lines_density,complexity,tests,test_errors,test_failures,test_success_density", sonarUrl);
                String bugs = "0";
                String alert_status = "OK";
                String vulnerabilities = "0";
                String code_smells = "0";
                String duplications = "0.00";
                String coverage = "0.00";
                String complexity = "0";
                String tests = "0";
                String test_erroes = "0";
                String test_failures = "0";
                String test_success_density = "0.00";

                JSONObject jsonObject = JSONObject.parseObject(result);
                String measures = jsonObject.getString("measures");
                JSONArray jsonArray = JSONObject.parseArray(measures);
                for (int y = 0; y < jsonArray.size(); y++) {
                    JSONObject jobject = jsonArray.getJSONObject(y);
                    String metric = jobject.getString("metric");
                    String value = jobject.getString("value");

                    //新增
                    if (metric.equals("tests")) {
                        tests = value;
                        if (tests.equals("")) {
                            tests = "0";
                        }
                    }
                    if (metric.equals("complexity")) {
                        complexity = value;
                        if (complexity.equals("")) {
                            complexity = "0";
                        }
                    }
                    if (metric.equals("test_erroes")) {
                        test_erroes = value;
                        if (test_erroes.equals("")) {
                            test_erroes = "0";
                        }
                    }

                    if (metric.equals("test_failures")) {
                        test_failures = value;
                        if (test_failures.equals("")) {
                            test_failures = "0";
                        }
                    }
                    if (metric.equals("test_success_density")) {
                        test_success_density = value;
                        if (test_success_density.equals("")) {
                            test_success_density = "0.00";
                        }
                    }


                    if (metric.equals("alert_status")) {

                        alert_status = value;
                    }
                    if (metric.equals("bugs")) {
                        bugs = value;
                        if (bugs.equals("")) {
                            bugs = "0";
                        }
                    }
                    if (metric.equals("vulnerabilities")) {
                        vulnerabilities = value;
                        if (vulnerabilities.equals("")) {
                            vulnerabilities = "0";
                        }
                    }
                    if (metric.equals("code_smells")) {
                        code_smells = value;
                        if (code_smells.equals("")) {
                            code_smells = "0";
                        }
                    }
                    if (metric.equals("duplicated_lines_density")) {
                        duplications = value;
                        if (duplications.equals("")) {
                            duplications = "0.00";
                        }
                    }
                    if (metric.equals("coverage")) {
                        coverage = value;
                        if (coverage.equals("")) {
                            coverage = "0.00";
                        }
                    }

                }
                TblSystemModuleJenkinsJobRun tmjr = new TblSystemModuleJenkinsJobRun();

                tmjr.setSonarBugs(Integer.parseInt(bugs));
                tmjr.setSonarCodeSmells(Integer.parseInt(code_smells));
                tmjr.setSonarCoverage(Double.parseDouble(coverage));
                tmjr.setSonarDuplications(Double.parseDouble(duplications));
                tmjr.setSonarQualityGate(alert_status);
                tmjr.setSonarVulnerabilities(Integer.parseInt(vulnerabilities));
                tmjr.setLastUpdateDate(endTime);
                tmjr.setId(Long.parseLong(modulejobRunId));
                tmjr.setCreateDate(startTime);
                tmjr.setSonarUnitTestNumber(Integer.parseInt(tests));
                if (Flag.equals("2")) {// 定时任务
                    TblSystemModuleJenkinsJobRun moduleJobrun = tblSystemModuleJenkinsJobRunMapper
                            .selectById(modulejobRunId);
                    moduleJobrun.setSonarBugs(Integer.parseInt(bugs));
                    moduleJobrun.setSonarCodeSmells(Integer.parseInt(code_smells));
                    moduleJobrun.setSonarCoverage(Double.parseDouble(coverage));
                    moduleJobrun.setSonarDuplications(Double.parseDouble(duplications));
                    moduleJobrun.setSonarQualityGate(alert_status);
                    moduleJobrun.setSonarVulnerabilities(Integer.parseInt(vulnerabilities));
                    moduleJobrun.setId(null);
                    moduleJobrun.setSystemJenkinsJobRun(jobId);
                    detailModuleInfo(moduleJobrun,moduleList,modulejobRunId,startTime,endTime);
                    // 插入数据
                    tblSystemModuleJenkinsJobRunMapper.insertNew(moduleJobrun);
                } else {
                    // 更新module数据
                    detailModuleInfo(tmjr,moduleList,modulejobRunId,startTime,endTime);
                    tblSystemModuleJenkinsJobRunMapper.updateByPrimaryKeySelective(tmjr);
                }
            }
        } catch (NumberFormatException e) {
            this.handleException(e);
            throw e;
        }

    }

    private void detailModuleInfo(TblSystemModuleJenkinsJobRun tmjr, Map<String,Object> moduleList,String modulejobRunId,Timestamp startTime,Timestamp endTime){
        for(String key:moduleList.keySet()){
            String value = moduleList.get(key).toString();
            Map<String,Object> map = (Map<String,Object>) JSONArray.parse(value);
            Integer mjrId=Integer.parseInt(map.get("moduleRunId").toString());
            if(modulejobRunId.equals(mjrId.toString())){
                SimpleDateFormat time = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
                Integer buildStatus=Integer.parseInt(map.get("buildStatus").toString());
                tmjr.setBuildStatus(buildStatus);
                if(map.get("endDate")==null){
                    tmjr.setLastUpdateDate(endTime);
                }else {
                    tmjr.setLastUpdateDate(Timestamp.valueOf(map.get("endDate").toString()));
                }
                if(map.get("startDate")==null){
                    tmjr.setCreateDate(startTime);
                }else{
                    tmjr.setCreateDate( Timestamp.valueOf(map.get("startDate").toString()));
                }

                break;
            }


        }
    }

    @Override
    public String getSonarIssue(String toolId, String dateTime, String projectKey, String type, String p) {
        // 获取sonartoken
        try {
            TblToolInfo entity = new TblToolInfo();
            entity.setId(Long.parseLong(toolId));
            entity = tblToolInfoMapper.selectOne(entity);
            String sonarUrl = "http://" + entity.getIp() + ":" + entity.getPort() + "/";
            String token = entity.getSonarApiToken();
            SonarQubeClientApi sonar = new SonarQubeClientApi(sonarUrl, token);
            String result;
            try {
                result = sonar.getIssuesApi().searchIssueAuthor(sonarUrl, type, projectKey, p, dateTime);
            } catch (SonarQubeException e) {
                this.handleException(e);

                return "";
            }
            return result;
        } catch (NumberFormatException e) {
            this.handleException(e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int countTaskBySystemId(Long id) {

        return tblRequirementFeatureMapper.countTaskBySystemId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemModuleJenkinsJobRun> selectSonarBySystemidAndDate(Map<String, Object> map) {

        return tblSystemModuleJenkinsJobRunMapper.selectSonarBySystemidAndDate(map);
    }

    @Override
    @Transactional(readOnly = true)
    public TblSystemModuleJenkinsJobRun selectByModuleRunId(long id) {

        return tblSystemModuleJenkinsJobRunMapper.selectById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemModuleJenkinsJobRun> selectSonarBySystemidAndDateMincro(Map<String, Object> map) {

        return tblSystemModuleJenkinsJobRunMapper.selectSonarBySystemidAndDateMincro(map);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> selectModuleJobRunByjobRunId(long jobRunId) {

        return tblSystemModuleJenkinsJobRunMapper.selectModuleJobRunByjobRunId(jobRunId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemSonar> selectSonarByMap(Map<String, Object> columnMap) {

        if (columnMap.get("SYSTEM_ID") != null && columnMap.get("SYSTEM_ID").equals("")) {
            TblSystemModule tr = new TblSystemModule();

            tr.setId(Long.parseLong(columnMap.get("SYSTEM_MODULE_ID").toString()));
            tr = tblSystemModuleMapper.selectOne(tr);
            columnMap.put("SYSTEM_ID", tr.getSystemId());
        }
        return tblSystemSonarMapper.selectByMap(columnMap);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemSonar> getSonarByMap(Map<String, Object> columnMap) {
        return tblSystemSonarMapper.selectByMap(columnMap);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getRandomSonarId(String env) {
        // 随机存一个sonarid
        Map<String, Object> sonarParam = new HashMap<>();
        sonarParam.put("toolType", 3);
        sonarParam.put("environmentType", env);
        List<TblToolInfo> sonartools = tblToolInfoMapper.selectToolByEnv(sonarParam);
        // 随机取一个值
        int n = this.getRandom(sonartools.size());
        return sonartools.get(n).getId();
    }


    @Override

    public List<TblSystemJenkins> selectJenkinsByMap(Map<String, Object> map) {

        return tblSystemJenkinsMapper.selectByMap(map);
    }

    @Override
    @Transactional(readOnly = true)

    public List<TblSystemJenkinsJobRun> selectLastTimeBySystemIdManual(Long id) {

        return tblSystemJenkinsJobRunMapper.selectLastTimeBySystemIdManual(id);
    }

    @Override
    @Transactional(readOnly = false)

    public void updateJenkins(TblSystemJenkins tblSystemJenkins) {
        tblSystemJenkinsMapper.updateById(tblSystemJenkins);

    }

    @Override
    @Transactional(readOnly = false)
    public TblSystemSonar creOrUpdSonarManual(TblSystemSonar tblSystemSonar) {
        TblSystemSonar sonarParam = new TblSystemSonar();
        sonarParam.setSystemId(tblSystemSonar.getSystemId());
        sonarParam.setSystemJenkinsId(tblSystemSonar.getSystemJenkinsId());
        // sonarParam.setJobname()
        TblSystemSonar sonarFlag = tblSystemSonarMapper.selectOne(sonarParam);
        if (sonarFlag == null) {
            // 插入新数据
            tblSystemSonarMapper.insertNew(tblSystemSonar);
            return tblSystemSonar;

        } else {
            return sonarFlag;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemModuleJenkinsJobRun> selectSonarBySystemidAndDateManual(Map<String, Object> mapParam) {
        return tblSystemModuleJenkinsJobRunMapper.selectSonarBySystemidAndDateManual(mapParam);

    }

    @Override
    @Transactional(readOnly = false)
    public void sonarDetailManual(String jobName, String systemSonar, Timestamp timestamp, String sonarName,
                                  String moduleJobRunId, String flag, long jobId) {
        // 获取该sonar相关信息
        Map<String, Object> columnMap = new HashMap<>();
        try {
            if (sonarName.equals("")) {

            } else {
                columnMap.put("JENKINS_PLUGIN_NAME", sonarName);
            }
            columnMap.put("status", 1);
            columnMap.put("TOOL_TYPE", 3);
            TblToolInfo sonartool = tblToolInfoMapper.selectByMap(columnMap).get(0);
            // 获取systemsonar
            TblSystemSonar tblSystemSonar = JsonUtil.fromJson(systemSonar, TblSystemSonar.class);
            tblSystemSonar.setToolId(sonartool.getId());
            tblSystemSonarMapper.updateById(tblSystemSonar);
            // 获取sonarkey等信息
            String sonarUrl = "http://" + sonartool.getIp() + ":" + sonartool.getPort() + "/";
            String token = sonartool.getSonarApiToken();
            SonarQubeClientApi sonar = new SonarQubeClientApi(sonarUrl, token);
            // 微服务
            String projectKey = tblSystemSonar.getSonarProjectKey();
            String result = "";
            try {
                result = sonar.getMeasuresApi().searchByUrl(projectKey,
                        "alert_status,bugs,vulnerabilities,code_smells,coverage,duplicated_lines_density", sonarUrl);
            } catch (SonarQubeException e) {
                this.handleException(e);

            }
            String bugs = "0";
            String alert_status = "OK";
            String vulnerabilities = "0";
            String code_smells = "0";
            String duplications = "0.00";
            String coverage = "0.00";
            JSONObject jsonObject = JSONObject.parseObject(result);
            String measures = jsonObject.getString("measures");
            JSONArray jsonArray = JSONObject.parseArray(measures);
            for (int y = 0; y < jsonArray.size(); y++) {
                JSONObject jobject = jsonArray.getJSONObject(y);
                String metric = jobject.getString("metric");
                String value = jobject.getString("value");
                if (metric.equals("alert_status")) {

                    alert_status = value;
                }
                if (metric.equals("bugs")) {
                    bugs = value;
                    if (bugs.equals("")) {
                        bugs = "0";
                    }
                }
                if (metric.equals("vulnerabilities")) {
                    vulnerabilities = value;
                    if (vulnerabilities.equals("")) {
                        vulnerabilities = "0";
                    }
                }
                if (metric.equals("code_smells")) {
                    code_smells = value;
                    if (code_smells.equals("")) {
                        code_smells = "0";
                    }
                }
                if (metric.equals("duplicated_lines_density")) {
                    duplications = value;
                    if (duplications.equals("")) {
                        duplications = "0.00";
                    }
                }
                if (metric.equals("coverage")) {
                    coverage = value;
                    if (coverage.equals("")) {
                        coverage = "0.00";
                    }
                }

            }

            if (flag.equals("2")) {
                // 定时
                TblSystemModuleJenkinsJobRun moduleJobrun = tblSystemModuleJenkinsJobRunMapper
                        .selectById(moduleJobRunId);
                moduleJobrun.setSonarBugs(Integer.parseInt(bugs));
                moduleJobrun.setSonarCodeSmells(Integer.parseInt(code_smells));
                moduleJobrun.setSonarCoverage(Double.parseDouble(coverage));
                moduleJobrun.setSonarDuplications(Double.parseDouble(duplications));
                moduleJobrun.setSonarQualityGate(alert_status);
                moduleJobrun.setSonarVulnerabilities(Integer.parseInt(vulnerabilities));
                moduleJobrun.setLastUpdateDate(timestamp);
                moduleJobrun.setId(null);
                moduleJobrun.setSystemJenkinsJobRun(jobId);
                tblSystemModuleJenkinsJobRunMapper.insertNew(moduleJobrun);

            } else {
                // 插入数据

                TblSystemModuleJenkinsJobRun tmjr = new TblSystemModuleJenkinsJobRun();
                tmjr.setSonarBugs(Integer.parseInt(bugs));
                tmjr.setSonarCodeSmells(Integer.parseInt(code_smells));
                tmjr.setSonarCoverage(Double.parseDouble(coverage));
                tmjr.setSonarDuplications(Double.parseDouble(duplications));
                tmjr.setSonarQualityGate(alert_status);
                tmjr.setSonarVulnerabilities(Integer.parseInt(vulnerabilities));
                tmjr.setLastUpdateDate(timestamp);
                tmjr.setId(Long.parseLong(moduleJobRunId));
                tblSystemModuleJenkinsJobRunMapper.updateByPrimaryKeySelective(tmjr);
            }
        } catch (NumberFormatException e) {
            this.handleException(e);
            throw e;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> selectModuleJobRunByjobRunIdManual(long id) {

        return tblSystemModuleJenkinsJobRunMapper.selectModuleJobRunByjobRunIdManual(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemJenkinsJobRun> selectLastTimeBySystemIdManualDeploy(Long id) {

        return tblSystemJenkinsJobRunMapper.selectLastTimeBySystemIdManualDeploy(id);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateModuleJonRun(TblSystemModuleJenkinsJobRun tmjr) {
        tblSystemModuleJenkinsJobRunMapper.updateById(tmjr);

    }

    @Override
    @Transactional(readOnly = true)
    public List<TblArtifactTag> getArtifactTag(Map<String, Object> colMap) {
        List<TblArtifactTag> list = new ArrayList<>();
        return list;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> creatJenkinsJob(String sysId, String systemName, String serverType, String modules,
                                               String env, String sonarflag, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        long usrId = CommonUtil.getCurrentUserId(request);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Object redisEnvType = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
        Map<String, Object> envMap = JSON.parseObject(redisEnvType.toString());
        String envName = envMap.get(env).toString();
        TblSystemInfo tSystemInfo = tblSystemInfoMapper.getOneSystemInfo(Long.parseLong(sysId));
        Map<String, Object> map = new HashMap<>();
        List<String> modulesList = new ArrayList<>();
        String jobrun = "";
        Integer systemId = Integer.parseInt(sysId);
        // 获取主源码表id
        Map<String, Object> mapParam = new HashMap<>();
        mapParam.put("system_Id", systemId);
        mapParam.put("environment_Type", env);
        mapParam.put("status", 1);
        // 获取项目scm表数据 env与systemid确定唯一
        List<TblSystemScm> countList = this.getScmByParam(mapParam);
        if (countList == null || countList.size() == 0) {
            result.put("status", Constants.ITMP_RETURN_FAILURE);
            result.put("message", "请配置此环境下源码信息");
            return result;
        }
        TblSystemScm tblSystemScm = countList.get(0);
        // 判断此环境下是否正在构建
        if (tblSystemScm.getBuildStatus() == 2) {
            result.put("status", Constants.ITMP_RETURN_FAILURE);
            result.put("message", "此环境下正在构建");
            return result;
        }
        if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {// 微服务

            String[] alModule = modules.split(",");
            for (int i = 0; i < alModule.length; i++) {
                // 判断是否符合环境要求
                modulesList = this.Judge(alModule[i], modulesList, tblSystemScm.getId(), systemId);
            }
            // 如果一个都没有符合条件的就返回
            if (modulesList.size() == 0) {
                result.put("status", Constants.ITMP_RETURN_FAILURE);
                result.put("message", "请配置此环境下源码信息");
                return result;
            }

        }

        //勾选的module
        List<String> checkModuleList = new ArrayList<>();
        checkModuleList.addAll(modulesList);
        result.put("checkModuleList", checkModuleList);

        // 新增处理
        if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {
            modulesList = this.sortAndDetailModuleid(modulesList, tblSystemScm.getId(), systemId);
        }
        // 将此构建状态改为构建中
        TblSystemScm systemScmUpdate = tblSystemScm;
        systemScmUpdate.setId(tblSystemScm.getId());
        systemScmUpdate.setBuildStatus(2);// 2为构建中
        systemScmUpdate.setLastUpdateDate(timestamp);
        systemScmUpdate.setLastUpdateBy(usrId);
        this.updateSystemScmBuildStatus(systemScmUpdate);
        String jobname = tSystemInfo.getSystemCode() + "_" + env + "_" + String.valueOf(systemId);
        // 查询是否有sysid和env 的 TBL_SYSTEM_JENKINS记录如果没有，则新生成此记录
        TblSystemJenkins tblSystemJenkins = new TblSystemJenkins();
        tblSystemJenkins.setCreateDate(timestamp);
        tblSystemJenkins.setJobName(jobname);
        tblSystemJenkins.setRootPom("./");
        tblSystemJenkins.setSystemId((long) systemId);
        tblSystemJenkins.setSystemScmId(tblSystemScm.getId());
        tblSystemJenkins.setStatus(1);// 1正常2删除
        tblSystemJenkins.setGoalsOptions("mvn clean deploy -Dmaven.test.skip=true");
        tblSystemJenkins.setCreateBy(usrId);
        tblSystemJenkins.setJobType(1);// 1构建2部署
        tblSystemJenkins.setBuildStatus(2);// 2构建中
        tblSystemJenkins.setCreateType(1);
        long jconfId = this.creOrUpdSjenkins(tblSystemJenkins, env, "1", "1");
        TblToolInfo jenkinsTool = new TblToolInfo();
        tblSystemJenkins = tblSystemJenkinsMapper.selectById(jconfId);
        // 获取toolid
        jenkinsTool = this.geTblToolInfo(tblSystemJenkins.getToolId());
        // 调用build的接口
        long systemScmId = tblSystemScm.getId();
        List<TblSystemModuleScm> tblSystemModuleScmList = new ArrayList<>();
        List<TblSystemModuleJenkinsJobRun> moduleRunJobList = new ArrayList<>();
        TblSystemModuleJenkinsJobRun resultModuleRun = new TblSystemModuleJenkinsJobRun();
        List<TblSystemModule> tblSystemModuleList = new ArrayList<>();
        if (jconfId > 0) {
            // TBL_SYSTEM_JENKINS_JOB_RUN 记录
            TblSystemJenkinsJobRun ttr = new TblSystemJenkinsJobRun();
            ttr.setSystemJenkinsId((long) jconfId);
            ttr.setSystemId((long) systemId);
            ttr.setJobName(jobname);
            ttr.setRootPom(".");
            ttr.setBuildStatus(1);// 正常
            ttr.setStartDate(timestamp);
            ttr.setStatus(1);
            ttr.setCreateDate(timestamp);
            ttr.setEnvironmentType(Integer.parseInt(env));
            ttr.setGoalsOptions("mvn clean install -Dmaven.test.skip=true");
            ttr.setCreateBy(usrId);
            ttr.setCreateType(1);
            ttr.setJobType(1);
            try {
                int jobNumber = iJenkinsBuildService.getNextBuildNumber(jenkinsTool, tblSystemJenkins, jobname);
                result.put("jobNumber", jobNumber);
                ttr.setJobRunNumber(jobNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            long jobid = this.insertJenkinsJobRun(ttr);// jobId jenkins任务执行表id
            jobrun = jobid + "";
            if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {
                for (String id : modulesList) {
                    TblSystemModuleJenkinsJobRun tj = new TblSystemModuleJenkinsJobRun();
                    tj.setSystemJenkinsJobRun(jobid);
                    tj.setSystemModuleId((long) Integer.parseInt(id));
                    tj.setCreateDate(timestamp);
                    tj.setStatus(1);// 正常
                    tj.setCreateBy(usrId);
                    tj.setSystemId((long) systemId);
                    tj.setSystemScmId(systemScmId);
                    tj.setJobName(jobname);
                    tj.setCreateType(1);// 1是自定义
                    tj.setJobType(1);// 构建
                    tj.setBuildStatus(1);
                    long moduleRunId = this.insertJenkinsModuleJobRun(tj);
                    // 获取信息
                    TblSystemModuleJenkinsJobRun mrid = this.selectByModuleRunId(moduleRunId);
                    moduleRunJobList.add(mrid);
                    tblSystemModuleList.add(this.getTblsystemModule((long) Integer.parseInt(id)));
                    Map<String, Object> moduleParam = new HashMap<>();
                    moduleParam.put("systemId", systemId);
                    // 增加
                    moduleParam.put("systemScmId", systemScmId);
                    moduleParam.put("status", 1);
                    moduleParam.put("systemModuleId", id);
                    List<TblSystemModuleScm> scmList = this.getModuleScmByParam(moduleParam);
                    if (scmList != null && scmList.size() > 0) {
                        tblSystemModuleScmList.add(scmList.get(0));
                    }
                }

            } else {
                // 传统服务也要插入modulejobrun为了获取sonar扫描结果
                TblSystemModuleJenkinsJobRun tj = new TblSystemModuleJenkinsJobRun();
                tj.setSystemJenkinsJobRun(jobid);
                tj.setSystemScmId(systemScmId);
                tj.setSystemId((long) systemId);
                tj.setCreateDate(timestamp);
                tj.setStatus(1);// 正常
                tj.setCreateBy(usrId);
                tj.setJobName(jobname);
                tj.setCreateType(1);
                tj.setJobType(1);
                tj.setBuildStatus(1);
                Long trModuleRunId = this.insertJenkinsModuleJobRun(tj);
                resultModuleRun = this.selectByModuleRunId(trModuleRunId);
            }
        }
        TblToolInfo sourceTool = new TblToolInfo();
        // 获取svntoolinfo
        if (tblSystemScm.getToolId() != null) {
            sourceTool = this.geTblToolInfo(tblSystemScm.getToolId());
        }
        // 先判断此微服务下是否有的sonar是否有toolid
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("SYSTEM_SCM_ID", tblSystemScm.getId());
        columnMap.put("SYSTEM_ID", systemId);
        List<TblSystemSonar> lists = this.getSonarByMap(columnMap);
        Long randomSonar = new Long(0);
        if (sonarflag.equals("1")) {

            if (lists == null || lists.size() == 0) {
                // 随机选取一个toolld
                randomSonar = this.getRandomSonarId(env);
            } else {
                randomSonar = lists.get(0).getToolId();
                //判断此sonar是否修改了
                randomSonar = judgeSonar(randomSonar, env);


            }
        }
        if (sonarflag.equals("1")) {
            List<TblToolInfo> tblSonarToolList = new ArrayList<>();
            if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {// 微服务
                List<TblSystemSonar> tblSystemSonarList = new ArrayList<TblSystemSonar>();
                for (TblSystemModule tblSystemModule : tblSystemModuleList) {
                    TblSystemSonar tblSystemSonar = assembleSonar(systemId, env, tSystemInfo, tblSystemScm,
                            tblSystemModule, timestamp, usrId, randomSonar, jconfId);
                    tblSystemSonarList.add(tblSystemSonar);
                    TblToolInfo tblToolInfo = tblToolInfoMapper.selectById(tblSystemSonar.getToolId());
                    tblSonarToolList.add(tblToolInfo);
                }

                result.put("tblSonarToolList", tblSonarToolList);
                result.put("tblSystemSonarList", tblSystemSonarList);
            } else {
                TblSystemSonar tblSystemSonar = assembleSonar(systemId, env, tSystemInfo, tblSystemScm, null, timestamp,
                        usrId, randomSonar, jconfId);
                TblToolInfo tblToolInfo = tblToolInfoMapper.selectById(tblSystemSonar.getToolId());
                result.put("tblSystemSonar", tblSystemSonar);
                result.put("tblSonarTool", tblToolInfo);

            }
        }
        //增加获取此次jenkinsjob Number
        try {
            int jobNumber = iJenkinsBuildService.getNextBuildNumber(jenkinsTool, tblSystemJenkins, jobname);
            result.put("jobNumber", jobNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        result.put("resultModuleRun", resultModuleRun);
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        result.put("serverType", serverType);
        result.put("tSystemInfo", tSystemInfo);
        result.put("tblSystemScm", tblSystemScm);
        result.put("tblSystemModuleList", tblSystemModuleList);
        result.put("tblSystemModuleScmList", tblSystemModuleScmList);
        if (tblSystemModuleScmList.size() > 0) {
            List<TblToolInfo> sourceToolList = new ArrayList<>();
            //微服务
            for (TblSystemModuleScm tms : tblSystemModuleScmList) {
                sourceToolList.add(tblToolInfoMapper.selectByPrimaryKey(tms.getToolId()));
            }
            result.put("sourceToolList", sourceToolList);
        }


        result.put("tblSystemJenkins", tblSystemJenkins);
        result.put("jenkinsTool", jenkinsTool);
        result.put("sourceTool", sourceTool);
        result.put("jobrun", jobrun);
        result.put("moduleRunJobList", moduleRunJobList);
        result.put("moduleList", modulesList);
        result.put("userId", CommonUtil.getCurrentUserId(request));
        result.put("envName", envName);
        return result;
    }

    private Long judgeSonar(Long randomSonar, String env) {
        TblToolInfo tblToolInfo = tblToolInfoMapper.selectByPrimaryKey(randomSonar);
        boolean flag = judgEnv(tblToolInfo.getEnvironmentType(), env);
        if (flag) {
            return randomSonar;
        } else {
            return getRandomSonarId(env);
        }

    }

    private List<String> Judge(String moduleid, List<String> modulesList, Long scmId, Integer systemId) {
        // 判断此moduleid是否符合要求
        List<TblSystemModuleScm> moduleScmList = this.judge(moduleid, scmId, systemId);
        if (moduleScmList != null && moduleScmList.size() > 0) {
            modulesList.add(moduleid);
        }

        return modulesList;
    }

    private TblSystemSonar assembleSonar(Integer systemId, String env, TblSystemInfo tSystemInfo,
                                         TblSystemScm tblSystemScm, TblSystemModule tblSystemModule, Timestamp timestamp, long usrId,
                                         long randomSonar, long systemJenkinsId) {
        // 插入Jenkins配置表和sonar配置表
        try {
            TblSystemSonar tblSystemSonar = new TblSystemSonar();
            tblSystemSonar.setCreateDate(timestamp);
            String projectKey = "";
            if (tblSystemModule == null) {// 传统构建
                projectKey = tSystemInfo.getSystemCode() + "_" + env;
            } else {// 微服务构建
                projectKey = tSystemInfo.getSystemCode() + "_" + tblSystemModule.getModuleCode() + "_" + env;
                tblSystemSonar.setSystemModuleId(tblSystemModule.getId());

            }
            tblSystemSonar.setSonarProjectKey(projectKey);
            tblSystemSonar.setSystemJenkinsId(systemJenkinsId);
            tblSystemSonar.setSonarProjectName(projectKey);
            tblSystemSonar.setStatus(1);// 正常
            tblSystemSonar.setSonarJavaBinaries(".");
            tblSystemSonar.setSystemScmId(tblSystemScm.getSystemId());
            tblSystemSonar.setSonarSources(".");
            tblSystemSonar.setSystemScmId(tblSystemScm.getId());
            tblSystemSonar.setSystemId((long) systemId);
            tblSystemSonar.setCreateBy(usrId);
            tblSystemSonar = this.creOrUpdSonar(tblSystemSonar, randomSonar, env);
            return tblSystemSonar;
        } catch (Exception e) {
            this.handleException(e);
            throw e;
        }
    }

    @Override

    public Map<String, Object> creatJenkinsJobBatch(String env, String data, HttpServletRequest request) {
        StringBuffer error = new StringBuffer("");
        long usrId = CommonUtil.getCurrentUserId(request);
        Map<String, Object> map = new HashMap<>();
        JSONArray jsonArray = JSONObject.parseArray(data);
        for (int y = 0; y < jsonArray.size(); y++) {
            // 循环调用
            JSONObject jobject = jsonArray.getJSONObject(y);
            String sysId = jobject.getString("sysId");
            String systemName = jobject.getString("systemName");
            String serverType = jobject.getString("serverType");
            String modules = jobject.getString("modules");
            // 先判断此环境下此systemid是否在构建中，构建中直接跳出
            Map<String, Object> juMap = new HashMap<>();
            juMap.put("id", sysId);
            juMap.put("environmentType", env);
            int item = this.countScmBuildStatus(juMap);
            if (item > 0) {// 正在构建中
                continue;
            }
            Map<String, Object> searchParam = new HashMap<>();
            searchParam.put("systemId", sysId);
            searchParam.put("environmentType", env);
            // 获取主源码表id
            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("system_Id", sysId);
            mapParam.put("environment_Type", env);
            mapParam.put("status", 1);
            // 获取项目scm表数据 env与systemid确定唯一
            TblSystemInfo tblSystemInfoflag = tblSystemInfoMapper.selectById(Long.parseLong(sysId));
            List<TblSystemScm> countList = this.getScmByParam(mapParam);
            if (countList == null || countList.size() == 0) {
                error.append(tblSystemInfoflag.getSystemName() + ",");
                continue;
            }
            List<String> modulesListFlag = new ArrayList<>();
            if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {// 微服务
                if (modules.equals("")) {
                    // 全部构建
                    List<Map<String, Object>> list = tblSystemModuleMapper
                            .getSystemModuleBySystemId(Long.parseLong(sysId));
                    if (list != null && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            modulesListFlag.add(list.get(i).get("id").toString());
                        }
                    }

                } else {
                    String[] alModule = modules.split(",");
                    for (int i = 0; i < alModule.length; i++) {
                        // 判断是否符合环境要求
                        modulesListFlag = Judge(alModule[i], modulesListFlag, countList.get(0).getId(),
                                Integer.parseInt(sysId));

                    }

                }
                // 如果一个都没有符合条件的就返回
                if (modulesListFlag.size() == 0) {
                    error.append(tblSystemInfoflag.getSystemName() + ",");
                    continue;
                }
            }
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    DefaultTransactionDefinition def = new DefaultTransactionDefinition();
                    def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
                    PlatformTransactionManager txManager = webApplicationContext
                            .getBean(PlatformTransactionManager.class);
                    TransactionStatus status = txManager.getTransaction(def);

                    try {

                        Object redisEnvType = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
                        Map<String, Object> envMap = JSON.parseObject(redisEnvType.toString());
                        String envName = envMap.get(env).toString();
                        TblSystemInfo tSystemInfo = tblSystemInfoMapper.getOneSystemInfo(Long.parseLong(sysId));
                        Map<String, Object> map = new HashMap<>();
                        List<String> modulesList = new ArrayList<>();
                        String jobrun = "";
                        Integer systemId = Integer.parseInt(sysId);
                        // 获取主源码表id
                        Map<String, Object> mapParam = new HashMap<>();
                        mapParam.put("systemId", systemId);
                        mapParam.put("environmentType", env);
                        // 获取项目scm表数据 env与systemid确定唯一
                        TblSystemScm tblSystemScm = countList.get(0);
                        // 将此构建状态改为构建中
                        TblSystemScm systemScmUpdate = new TblSystemScm();
                        systemScmUpdate.setId(tblSystemScm.getId());
                        systemScmUpdate.setBuildStatus(2);// 2为构建中
                        systemScmUpdate.setLastUpdateBy(usrId);
                        systemScmUpdate.setLastUpdateDate(timestamp);
                        updateSystemScmBuildStatus(systemScmUpdate);
                        // moduleRunId 信息
                        List<TblSystemModuleJenkinsJobRun> moduleRunJobList = new ArrayList<>();
                        TblSystemModuleJenkinsJobRun resultModuleRun = new TblSystemModuleJenkinsJobRun();
                        if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {// 微服务
                            if (modules.equals("")) {
                                // 全部构建
                                List<Map<String, Object>> list = tblSystemModuleMapper
                                        .getSystemModuleBySystemId((long) systemId);
                                if (list != null && list.size() > 0) {

                                    for (int i = 0; i < list.size(); i++) {
                                        modulesList.add(list.get(i).get("id").toString());

                                    }
                                }

                            } else {

                                String[] alModule = modules.split(",");
                                for (int i = 0; i < alModule.length; i++) {
                                    // 判断是否符合环境要求
                                    modulesList = Judge(alModule[i], modulesList, tblSystemScm.getId(), systemId);

                                }

                            }

                        }
                        if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {
                            modulesList = sortAndDetailModuleid(modulesList, tblSystemScm.getId(), systemId);
                        }
                        String jobname = tSystemInfo.getSystemCode() + "_" + env + "_" + String.valueOf(systemId);
                        // 查询是否有sysid和env 的 TBL_SYSTEM_JENKINS记录如果没有，则新生成此记录
                        TblSystemJenkins tblSystemJenkins = new TblSystemJenkins();
                        tblSystemJenkins.setCreateDate(timestamp);
                        tblSystemJenkins.setJobName(jobname);
                        tblSystemJenkins.setRootPom("./");
                        tblSystemJenkins.setSystemId((long) systemId);
                        tblSystemJenkins.setSystemScmId(tblSystemScm.getId());
                        tblSystemJenkins.setStatus(1);// 1正常2删除
                        tblSystemJenkins.setCreateBy(usrId);
                        tblSystemJenkins.setJobType(1);// 1构建2部署
                        tblSystemJenkins.setBuildStatus(2);// 2构建中
                        tblSystemJenkins.setCreateType(1);
                        tblSystemJenkins.setGoalsOptions("mvn clean deploy -Dmaven.test.skip=true");
                        long jconfId = creOrUpdSjenkins(tblSystemJenkins, env, "1", "1");
                        TblToolInfo jenkinsTool = new TblToolInfo();
                        // 获取toolid
                        jenkinsTool = geTblToolInfo(tblSystemJenkins.getToolId());
                        // 调用build的接口
                        long systemScmId = tblSystemScm.getId();
                        List<TblSystemModuleScm> tblSystemModuleScmList = new ArrayList<>();
                        List<TblSystemModule> tblSystemModuleList = new ArrayList<>();
                        if (jconfId > 0) {
                            // TBL_SYSTEM_JENKINS_JOB_RUN 记录
                            TblSystemJenkinsJobRun ttr = new TblSystemJenkinsJobRun();
                            ttr.setSystemJenkinsId((long) jconfId);
                            ttr.setSystemId((long) systemId);
                            ttr.setJobName(jobname);
                            ttr.setRootPom(".");
                            ttr.setBuildStatus(1);// 正常
                            ttr.setStartDate(timestamp);
                            ttr.setStatus(1);
                            ttr.setCreateDate(timestamp);
                            ttr.setCreateBy(usrId);
                            ttr.setEnvironmentType(Integer.parseInt(env));
                            ttr.setGoalsOptions("mvn clean install -Dmaven.test.skip=true");
                            ttr.setCreateType(1);
                            ttr.setJobType(1);
                            long jobid = insertJenkinsJobRun(ttr);// jobId jenkins任务执行表id
                            jobrun = jobid + "";
                            if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {// 如果是1则插入module表
                                for (String id : modulesList) {// 需要修改
                                    TblSystemModuleJenkinsJobRun tj = new TblSystemModuleJenkinsJobRun();
                                    tj.setSystemJenkinsJobRun(jobid);
                                    tj.setSystemModuleId((long) Integer.parseInt(id));
                                    tj.setCreateDate(timestamp);
                                    tj.setStatus(1);// 正常
                                    tj.setSystemScmId(systemScmId);
                                    tj.setJobName(jobname);
                                    tj.setSystemId((long) systemId);
                                    tj.setCreateType(1);
                                    tj.setCreateBy(usrId);
                                    tj.setJobType(1);
                                    long moduleRunId = insertJenkinsModuleJobRun(tj);
                                    // 获取信息
                                    TblSystemModuleJenkinsJobRun mrid = selectByModuleRunId(moduleRunId);
                                    moduleRunJobList.add(mrid);
                                    tblSystemModuleList.add(getTblsystemModule((long) Integer.parseInt(id)));
                                    Map<String, Object> moduleParam = new HashMap<>();
                                    moduleParam.put("systemId", systemId);
                                    /* moduleParam.put("environmentType", env); */
                                    // 增加
                                    moduleParam.put("systemScmId", systemScmId);
                                    moduleParam.put("systemModuleId", id);
                                    List<TblSystemModuleScm> scmList = getModuleScmByParam(moduleParam);
                                    if (scmList != null && scmList.size() > 0) {
                                        tblSystemModuleScmList.add(scmList.get(0));
                                    }
                                }

                            } else {
                                // 传统服务也要插入modulejobrun为了获取sonar扫描结果
                                TblSystemModuleJenkinsJobRun tjs = new TblSystemModuleJenkinsJobRun();
                                tjs.setSystemJenkinsJobRun(jobid);
                                tjs.setSystemScmId(systemScmId);
                                tjs.setSystemId((long) systemId);
                                tjs.setCreateDate(timestamp);
                                tjs.setStatus(1);// 正常
                                tjs.setCreateBy(usrId);
                                tjs.setJobName(jobname);
                                tjs.setCreateType(1);
                                tjs.setJobType(1);
                                Long trModuleRunId = insertJenkinsModuleJobRun(tjs);
                                resultModuleRun = selectByModuleRunId(trModuleRunId);
                            }
                        }
                        TblToolInfo sourceTool = new TblToolInfo();
                        // 获取svntoolinfo
                        if (tblSystemScm.getToolId() != null) {
                            sourceTool = geTblToolInfo(tblSystemScm.getToolId());
                        }
                        // 先判断此微服务下是否有的sonar是否有toolid
                        Map<String, Object> columnMap = new HashMap<>();
                        columnMap.put("SYSTEM_SCM_ID", tblSystemScm.getId());
                        columnMap.put("SYSTEM_ID", systemId);
                        List<TblSystemSonar> lists = getSonarByMap(columnMap);
                        Long randomSonar = new Long(0);
                        if (lists == null || lists.size() == 0) {
                            // 随机选取一个toolld
                            randomSonar = getRandomSonarId(env);

                        } else {
                            randomSonar = lists.get(0).getToolId();
                        }
                        Map<String, Object> result = new HashMap<>();
                        result.put("tSystemInfo", tSystemInfo);
                        result.put("tblSystemScm", tblSystemScm);
                        result.put("jenkinsTool", jenkinsTool);
                        result.put("sourceTool", sourceTool);
                        result.put("jobrun", jobrun);
                        result.put("tblSystemJenkins", tblSystemJenkins);
                        if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {// 微服务
                            List<TblSystemSonar> tblSystemSonarList = new ArrayList<TblSystemSonar>();
                            for (TblSystemModule tblSystemModule : tblSystemModuleList) {
                                TblSystemSonar tblSystemSonar = assembleSonar(systemId, env, tSystemInfo,
                                        tblSystemScm, tblSystemModule, timestamp, usrId, randomSonar, jconfId);
                                tblSystemSonarList.add(tblSystemSonar);
                            }

                            result.put("tblSystemModuleList", tblSystemModuleList);
                            result.put("tblSystemModuleScmList", tblSystemModuleScmList);
                            result.put("moduleRunJobList", moduleRunJobList);
                            result.put("tblSystemSonarList", tblSystemSonarList);
                            iJenkinsBuildService.buildMicroAutoJob(result);
                        } else {
                            TblSystemSonar tblSystemSonar = assembleSonar(systemId, env, tSystemInfo, tblSystemScm,
                                    null, timestamp, usrId, randomSonar, jconfId);
                            result.put("tblSystemSonar", tblSystemSonar);
                            result.put("resultModuleRun", resultModuleRun);
                            iJenkinsBuildService.buildGeneralAutoJob(result);
                        }
                        txManager.commit(status);
                    } catch (Exception e) {
                        handleException(e);

                        txManager.rollback(status);
                    }
                }
            });
        }
        map.put("status", Constants.ITMP_RETURN_SUCCESS);
        if (!error.toString().equals("")) {
            error = error.deleteCharAt(error.length() - 1);
            error.append("没有配置环境已忽略");
            map.put("error", error.toString());
        }
        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getBuildMessageById(String jobRunId, String createType) {
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Map<String, Object> map = new HashMap<>();
        try {
            TblSystemJenkinsJobRun jorRun = this.selectBuildMessageById(Long.parseLong(jobRunId));
            String isSonar = "true";
            String text = jorRun.getBuildLogs();
            String endLog = "";
            if(text!=null) {
                try {
                    endLog = new String(text.getBytes("ISO8859-1"), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    handleException(e);
                }
            }

            map.put("content", jorRun);
            map.put("buildLogs", endLog);
            map.put("buildStatus", jorRun.getStatus());
            if (jorRun.getStartDate() != null) {
                map.put("startDate", sdf.format(jorRun.getStartDate()));
            } else {
                map.put("startDate", "");
            }
            if (jorRun.getEndDate() != null) {
                map.put("endDate", sdf.format(jorRun.getEndDate()));
            } else {
                map.put("endDate", "");
            }
            List<Map<String, Object>> list = new ArrayList<>();
            if (createType.equals(Constants.CREATE_TYPE_AUTO)) {
                // 获取此次构建sonar信息
                list = this.selectModuleJobRunByjobRunId(Long.parseLong(jobRunId));
            } else {
                // 获取此次手动构建sonar信息
                list = this.selectModuleJobRunByjobRunIdManual(Long.parseLong(jobRunId));
            }
            String ybug = "";
            String yVulnerabilities = "";
            String yCodeSmells = "";
            String yCoverage = "";
            String yduplications = "";
            String xValue = "";
            String yTests = "";
            List<Map<String, Object>> maps = new ArrayList<>();
            if (list != null && list.size() > 0) {
                for (Map<String, Object> trs : list) {
                    if (trs.get("SONAR_BUGS") == null && trs.get("SONAR_VULNERABILITIES") == null && trs.get("SONAR_CODE_SMELLS") == null) {
                        isSonar = "flase";
                    }

                    Map<String, Object> resultMap = new HashMap<>();
                    if (trs.get("SONAR_BUGS") != null) {
                        ybug = trs.get("SONAR_BUGS").toString();
                    }
                    if (trs.get("SONAR_VULNERABILITIES") != null) {
                        yVulnerabilities = trs.get("SONAR_VULNERABILITIES").toString();
                    }
                    if (trs.get("SONAR_CODE_SMELLS") != null) {
                        yCodeSmells = trs.get("SONAR_CODE_SMELLS").toString();
                    }
                    if (trs.get("SONAR_DUPLICATIONS") != null) {
                        yduplications = trs.get("SONAR_DUPLICATIONS").toString();
                    }
                    if (trs.get("SONAR_COVERAGE") != null) {
                        yCoverage = trs.get("SONAR_COVERAGE").toString();
                    }
                    if (trs.get("LAST_UPDATE_DATE") != null) {
                        xValue = sdf.format(trs.get("LAST_UPDATE_DATE"));
                    }


                    if (trs.get("SONAR_UNIT_TEST_NUMBER") != null) {
                        yTests = trs.get("SONAR_UNIT_TEST_NUMBER").toString();
                    } else {
                        yTests = "0";
                    }
                    Map<String, Object> sonarQueryMap = new HashMap<>();
                    long systemJenkinsId = jorRun.getSystemJenkinsId();
                    sonarQueryMap.put("SYSTEM_JENKINS_ID", systemJenkinsId);
                    if (createType.equals("1")) {// 自动构建

                        // 微服务
                        if (trs.get("SYSTEM_MODULE_ID") != null) {
                            TblSystemModule tModule = this
                                    .getTblsystemModule(Long.parseLong(trs.get("SYSTEM_MODULE_ID").toString()));
                            resultMap.put("moduleame", tModule.getModuleName());
                            sonarQueryMap.put("SYSTEM_MODULE_ID", trs.get("SYSTEM_MODULE_ID"));
                        }
                        sonarQueryMap.put("SYSTEM_ID", trs.get("SYSTEM_ID"));
                        sonarQueryMap.put("SYSTEM_SCM_ID", trs.get("SYSTEM_SCM_ID"));
                    } else {
                        // 手动构建

                        sonarQueryMap.put("SYSTEM_ID", trs.get("SYSTEM_ID"));


                    }
                    List<TblSystemSonar> sonarList = this.selectSonarByMap(sonarQueryMap);
                    String projectKey = "";

                    if (sonarList.size() > 0) {
                        projectKey = sonarList.get(0).getSonarProjectKey();
                        resultMap.put("toolId", sonarList.get(0).getToolId());
                    } else {
                        resultMap.put("toolId", "");
                    }
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String projectDateTime = "";
                    if (trs.get("LAST_UPDATE_DATE") != null) {
                        projectDateTime = format.format(trs.get("LAST_UPDATE_DATE"));
                        projectDateTime = projectDateTime.replace(" ", "T");
                        projectDateTime = projectDateTime + "+0800";
                    }
                    resultMap.put("projectKey", projectKey);
                    resultMap.put("projectDateTime", projectDateTime);
                    //resultMap.put("toolId", sonarList.get(0).getToolId());
                    resultMap.put("bug", ybug);
                    resultMap.put("Vulnerabilities", yVulnerabilities);
                    resultMap.put("CodeSmells", yCodeSmells);
                    resultMap.put("duplications", yduplications + "%");
                    resultMap.put("Coverage", yCoverage + "%");
                    resultMap.put("xValue", xValue);
                    resultMap.put("tests", yTests);
                    maps.add(resultMap);

                }

            }
            map.put("isSonar", isSonar);
            map.put("list", maps);
            return map;
        } catch (NumberFormatException e) {
            this.handleException(e);
            throw e;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSonarMessage(String systemId, String startDate, String endDate) {
        Map<String, Object> map = new HashMap<>();
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            // 传统服务
            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("startDate", startDate);
            mapParam.put("endDate", endDate);
            mapParam.put("systemId", systemId);
            List<TblSystemModuleJenkinsJobRun> tmjr = this.selectSonarBySystemidAndDate(mapParam);
            String ybug = "[";
            String yVulnerabilities = "[";
            String yCodeSmells = "[";
            String yCoverage = "[";
            String yduplications = "[";
            String xValue = "[";
            if (tmjr == null || tmjr.size() == 0) {
                map.put("status", "2");// 此时间段没有数据
                return map;
            }
            if (tmjr != null && tmjr.size() > 0) {
                for (TblSystemModuleJenkinsJobRun trs : tmjr) {
                    // 修改
                    if (trs.getSonarBugs() != null) {
                        ybug = ybug + trs.getSonarBugs() + ",";
                    } else {
                        ybug = ybug + "0" + ",";
                    }
                    if (trs.getSonarVulnerabilities() != null) {
                        yVulnerabilities = yVulnerabilities + trs.getSonarVulnerabilities() + ",";
                    } else {
                        yVulnerabilities = yVulnerabilities + "0" + ",";
                    }

                    if (trs.getSonarCodeSmells() != null) {
                        yCodeSmells = yCodeSmells + trs.getSonarCodeSmells() + ",";
                    } else {
                        yCodeSmells = yCodeSmells + "0" + ",";
                    }
                    if (trs.getSonarDuplications() != null) {
                        yduplications = yduplications + trs.getSonarDuplications() + "%,";
                    } else {
                        yduplications = yduplications + "0.0%" + ",";
                    }

                    if (trs.getSonarCoverage() != null) {
                        yCoverage = yCoverage + trs.getSonarCoverage() + "%,";
                    } else {
                        yCoverage = yCoverage + "0.0%" + ",";
                    }

                    if (trs.getLastUpdateDate() != null) {
                        xValue = xValue + sdf.format(trs.getLastUpdateDate()) + ",";
                    } else {
                        xValue = xValue + "0" + ",";
                    }

                }

            }
            ybug = ybug.substring(0, ybug.length() - 1) + "]";
            yVulnerabilities = yVulnerabilities.substring(0, yVulnerabilities.length() - 1) + "]";
            ;
            yCodeSmells = yCodeSmells.substring(0, yCodeSmells.length() - 1) + "]";
            ;
            yCoverage = yCoverage.substring(0, yCoverage.length() - 1) + "]";
            ;
            yduplications = yduplications.substring(0, yduplications.length() - 1) + "]";
            ;
            xValue = xValue.substring(0, xValue.length() - 1) + "]";
            ;
            map.put("Bugs", ybug);
            map.put("Vulnerabilities", yVulnerabilities);
            map.put("Code Smells", yCodeSmells);
            map.put("Coverage", yCoverage);
            map.put("Duplications", yduplications);
            map.put("xValue", xValue);
            map.put("status", "1");
            return map;
        } catch (Exception e) {
            this.handleException(e);
            throw e;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSonarMessageMincro(String systemId, String startDate, String endDate) {
        Map<String, Object> resultMap = new HashMap<>();
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        List<Map<String, Object>> list = new ArrayList<>();
        // 获取该微服务下所有子id
        try {
            List<TblSystemModule> modules = this.selectSystemModule(Long.parseLong(systemId));
            for (TblSystemModule module : modules) {
                Map<String, Object> map = new HashMap<>();
                Map<String, Object> mapParam = new HashMap<>();
                mapParam.put("startDate", startDate);
                mapParam.put("endDate", endDate);
                mapParam.put("moduleId", module.getId());
                mapParam.put("systemId", systemId);
                List<TblSystemModuleJenkinsJobRun> tmjr = this.selectSonarBySystemidAndDateMincro(mapParam);
                String yBug = "[";
                String yTests = "[";
                String yVulnerabilities = "[";
                String yCodeSmells = "[";
                String yCoverage = "[";
                String yDuplications = "[";
                String xValue = "[";


                if (tmjr == null || tmjr.size() == 0) {
                    continue;
                }
                if (tmjr != null && tmjr.size() > 0) {
                    for (TblSystemModuleJenkinsJobRun trs : tmjr) {


                        if (trs.getSonarUnitTestNumber() != null) {
                            yTests = yTests + trs.getSonarUnitTestNumber() + ",";
                        } else {
                            yTests = yTests + "0" + ",";
                        }

                        if (trs.getSonarBugs() != null) {
                            yBug = yBug + trs.getSonarBugs() + ",";
                        } else {
                            yBug = yBug + "0" + ",";
                        }
                        if (trs.getSonarVulnerabilities() != null) {
                            yVulnerabilities = yVulnerabilities + trs.getSonarVulnerabilities() + ",";
                        } else {
                            yVulnerabilities = yVulnerabilities + "0" + ",";
                        }
                        if (trs.getSonarCodeSmells() != null) {
                            yCodeSmells = yCodeSmells + trs.getSonarCodeSmells() + ",";
                        } else {
                            yCodeSmells = yCodeSmells + "0" + ",";
                        }
                        if (trs.getSonarDuplications() != null) {
                            yDuplications = yDuplications + trs.getSonarDuplications() + "%,";
                        } else {
                            yDuplications = yDuplications + "0.0%" + ",";
                        }

                        if (trs.getSonarCoverage() != null) {
                            yCoverage = yCoverage + trs.getSonarCoverage() + "%,";
                        } else {
                            yCoverage = yCoverage + "0.0%" + ",";
                        }
                        if (trs.getLastUpdateDate() != null) {
                            xValue = xValue + sdf.format(trs.getLastUpdateDate()) + ",";
                        } else {
                            xValue = xValue + "0" + ",";
                        }
                    }
                }
                yBug = yBug.substring(0, yBug.length() - 1) + "]";
                yVulnerabilities = yVulnerabilities.substring(0, yVulnerabilities.length() - 1) + "]";
                yCodeSmells = yCodeSmells.substring(0, yCodeSmells.length() - 1) + "]";
                yCoverage = yCoverage.substring(0, yCoverage.length() - 1) + "]";
                yDuplications = yDuplications.substring(0, yDuplications.length() - 1) + "]";
                xValue = xValue.substring(0, xValue.length() - 1) + "]";

                yTests = yTests.substring(0, yTests.length() - 1) + "]";
                map.put("moduleName", module.getModuleName());
                map.put("Bugs", yBug);
                map.put("Vulnerabilities", yVulnerabilities);
                map.put("Code Smells", yCodeSmells);
                map.put("Coverage", yCoverage);
                map.put("Duplications", yDuplications);
                map.put("xValue", xValue);
                map.put("tests", yTests);
                list.add(map);
            }
            resultMap.put("list", list);
            resultMap.put("status", "1");// 成功
            resultMap.put("createType", 1);
            return resultMap;
        } catch (NumberFormatException e) {
            this.handleException(e);
            throw e;
        }

    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> buildJobManual(HttpServletRequest request, String systemJenkisId, String jsonParam) {
        Map<String, Object> paramMap = new HashMap<>();
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            long usrId = CommonUtil.getCurrentUserId(request);
            Map<String, Object> map = new HashMap<>();
            map.put("id", systemJenkisId);
            List<TblSystemJenkins> list = this.selectJenkinsByMap(map);
            TblSystemJenkins tblSystemJenkins = list.get(0);
            if (tblSystemJenkins.getBuildStatus() == 2) {
                paramMap.put("status", Constants.ITMP_RETURN_FAILURE);
                paramMap.put("message", "正在构建中请稍后!");
                return paramMap;
            }
            String jobName = tblSystemJenkins.getJobName();
            tblSystemJenkins.setBuildStatus(2);// 构建中
            // 修改此状态
            this.updateJenkins(tblSystemJenkins);
            // 获取jenkinstoolinfo
            TblToolInfo jenkinsToolInfo = this.geTblToolInfo(tblSystemJenkins.getToolId());
            // 插入tbl_system_sonar
            TblSystemSonar tblSystemSonar = assembleSonarManual(timestamp, tblSystemJenkins.getSystemId(), usrId,
                    Long.parseLong(systemJenkisId));

            //保存字符串值
            String buildParameter=jsonParam;
            buildParameter= buildParameter.replaceAll("\"","");
            buildParameter=buildParameter.replaceAll("\\{","");
            buildParameter=buildParameter.replaceAll("\\}","");
            buildParameter=buildParameter.replaceAll(",",";");
            TblSystemJenkinsJobRun ttr = new TblSystemJenkinsJobRun();
            ttr.setSystemJenkinsId(Long.parseLong(systemJenkisId));
            ttr.setSystemId(tblSystemJenkins.getSystemId());
            ttr.setJobName(jobName);
            ttr.setRootPom(".");
            ttr.setBuildStatus(1);// 正常
            ttr.setStartDate(timestamp);
            ttr.setStatus(1);
            ttr.setCreateDate(timestamp);
            ttr.setJobType(1);
            ttr.setBuildParameter(buildParameter);
            ttr.setGoalsOptions("mvn clean install -Dmaven.test.skip=true");
            ttr.setCreateBy(usrId);
            ttr.setCreateType(2);// 手动

            try {
                int jobNumber = iJenkinsBuildService.getNextBuildNumber(jenkinsToolInfo, tblSystemJenkins, jobName);
                paramMap.put("jobNumber", jobNumber);
                ttr.setJobRunNumber(jobNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }

            long jobid = this.insertJenkinsJobRun(ttr);
            TblSystemModuleJenkinsJobRun tj = new TblSystemModuleJenkinsJobRun();
            tj.setSystemJenkinsJobRun(jobid);
            tj.setCreateDate(timestamp);
            tj.setStatus(1);
            tj.setCreateBy(usrId);
            tj.setSystemId(tblSystemJenkins.getSystemId());
            tj.setJobName(jobName);
            tj.setCreateType(2);
            tj.setJobType(1);
            long moduleJobRunId = this.insertJenkinsModuleJobRun(tj);
            //增加获取此次jenkinsjob Number
            paramMap.put("jsonParam", jsonParam);
            paramMap.put("jobRunId", jobid);
            paramMap.put("systemJenkinsId", systemJenkisId);
            paramMap.put("moduleJobRunId", moduleJobRunId);
            paramMap.put("systemSonarId", tblSystemSonar.getId());
            paramMap.put("jobName", jobName);
            paramMap.put("jenkinsToolInfo", jenkinsToolInfo);
            paramMap.put("tblSystemJenkins", tblSystemJenkins);
        } catch (Exception e) {
            this.handleException(e);
            throw e;
        }
        return paramMap;
    }

    private TblSystemSonar assembleSonarManual(Timestamp timestamp, long systemId, long usrId, long systemJenkinsId) {
        TblSystemSonar tblSystemSonar = new TblSystemSonar();
        tblSystemSonar.setCreateDate(timestamp);
        tblSystemSonar.setStatus(1);// 正常
        tblSystemSonar.setSystemId(systemId);
        tblSystemSonar.setCreateBy(usrId);
        tblSystemSonar.setSystemJenkinsId(systemJenkinsId);
        tblSystemSonar = this.creOrUpdSonarManual(tblSystemSonar);
        return tblSystemSonar;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getSonarMessageManual(String systemId, String startDate, String endDate) {
        try {
            Map<String, Object> resultMap = new HashMap<>();
            DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            List<Map<String, Object>> list = new ArrayList<>();
            // 获取该systemid下jobname
            Map<String, Object> colMap = new HashMap<>();
            colMap.put("status", 1);
            colMap.put("system_id", systemId);
            colMap.put("create_type", 2);
            List<TblSystemJenkins> jobNameList = this.getSystemJenkinsByMap(colMap);
            for (TblSystemJenkins tblSystemJenkins : jobNameList) {
                String jobName = tblSystemJenkins.getJobName();
                // 有可能出现删除jobname的数据查询出来 删选
                long systemJenkinsId = tblSystemJenkins.getId();
                Map<String, Object> runMap = new HashMap<>();
                runMap.put("systemId", systemId);
                runMap.put("systemJenkinsId", systemJenkinsId);
                runMap.put("jobName", jobName);
                List<TblSystemJenkinsJobRun> jobRunidsList = tblSystemJenkinsJobRunMapper.selectByMapLimit(runMap);
                List<String> jobRunids = new ArrayList<>();
                for (TblSystemJenkinsJobRun tblSystemJenkinsJobRun : jobRunidsList) {
                    // jobRunids.append(tblSystemJenkinsJobRun.getId()+",");
                    jobRunids.add(tblSystemJenkinsJobRun.getId() + "");
                }
                // 获取该jobname下sonar扫描历史
                Map<String, Object> mapParam = new HashMap<>();
                List<TblSystemModuleJenkinsJobRun> tmjr = new ArrayList<>();
                mapParam.put("startDate", startDate);
                mapParam.put("systemId", systemId);
                mapParam.put("endDate", endDate);
                mapParam.put("jobName", jobName);
                mapParam.put("jobRunids", jobRunids);// 为了区分已删除同名jobname数据
                if (jobRunids.size() > 0) {
                    tmjr = this.selectSonarBySystemidAndDateManual(mapParam);
                }
                String yBug = "[";
                String yVulnerabilities = "[";
                String yCodeSmells = "[";
                String yCoverage = "[";
                String yDuplications = "[";
                String xValue = "[";
                Map<String, Object> map = new HashMap<>();
                if (tmjr == null || tmjr.size() == 0) {
                    continue;
                }

                if (tmjr != null && tmjr.size() > 0) {
                    for (TblSystemModuleJenkinsJobRun trs : tmjr) {

                        if (trs.getSonarBugs() != null) {
                            yBug = yBug + trs.getSonarBugs() + ",";
                        } else {
                            yBug = yBug + "0" + ",";
                        }
                        if (trs.getSonarVulnerabilities() != null) {
                            yVulnerabilities = yVulnerabilities + trs.getSonarVulnerabilities() + ",";
                        } else {
                            yVulnerabilities = yVulnerabilities + "0" + ",";
                        }

                        if (trs.getSonarCodeSmells() != null) {
                            yCodeSmells = yCodeSmells + trs.getSonarCodeSmells() + ",";
                        } else {
                            yCodeSmells = yCodeSmells + "0" + ",";
                        }
                        if (trs.getSonarDuplications() != null) {
                            yDuplications = yDuplications + trs.getSonarDuplications() + "%,";
                        } else {
                            yDuplications = yDuplications + "0.0%" + ",";
                        }

                        if (trs.getSonarCoverage() != null) {
                            yCoverage = yCoverage + trs.getSonarCoverage() + "%,";
                        } else {
                            yCoverage = yCoverage + "0.0%" + ",";
                        }

                        if (trs.getLastUpdateDate() != null) {
                            xValue = xValue + sdf.format(trs.getLastUpdateDate()) + ",";
                        } else {
                            xValue = xValue + "0" + ",";
                        }
                    }
                }
                yBug = yBug.substring(0, yBug.length() - 1) + "]";
                yVulnerabilities = yVulnerabilities.substring(0, yVulnerabilities.length() - 1) + "]";
                yCodeSmells = yCodeSmells.substring(0, yCodeSmells.length() - 1) + "]";
                yCoverage = yCoverage.substring(0, yCoverage.length() - 1) + "]";
                yDuplications = yDuplications.substring(0, yDuplications.length() - 1) + "]";
                xValue = xValue.substring(0, xValue.length() - 1) + "]";
                map.put("jobName", jobName);
                map.put("Bugs", yBug);
                map.put("Vulnerabilities", yVulnerabilities);
                map.put("Code Smells", yCodeSmells);
                map.put("Coverage", yCoverage);
                map.put("Duplications", yDuplications);
                map.put("xValue", xValue);
                list.add(map);

            }
            resultMap.put("list", list);
            resultMap.put("createType", 2);
            resultMap.put("status", "1");// 成功
            return resultMap;
        } catch (Exception e) {
            this.handleException(e);
            throw e;
        }

    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllSystemInfoByBuilding(String ids) {// scmBuildStatus 1空闲 2构建中
        Object tblToolInfo = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
        Map<String, Object> envMap = JSON.parseObject(tblToolInfo.toString());
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> moduleList = new ArrayList<>();
        Map<String, Object> systeminfoMap = new HashMap<>();
        List<String> idsList = new ArrayList<>();
        String[] item = ids.split(",");
        for (int i = 0; i < item.length; i++) {
            idsList.add(item[i]);
        }
        systeminfoMap.put("ids", idsList);
        List<Map<String, Object>> sysInfoList = tblSystemInfoMapper.getAllSystemInfoByBuilding(systeminfoMap);
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        List<Map<String, Object>> endList = new ArrayList<>();
        for (Map<String, Object> sysInfomap : sysInfoList) {
            Long id = (Long) sysInfomap.get("id");
            String createType = "";
            if (sysInfomap.get("createType") == null) {
                continue;
            } else {
                createType = sysInfomap.get("createType").toString();
            }
            // 判断是否为空闲或构建中 空闲是每个环境下都没有在构建 自动
            List<TblSystemScm> judgeList = this.judgeScmBuildStatus(id);
            // 获取该sonar相关信息
            Map<String, Object> buildingMap = new HashMap<>();
            buildingMap.put("system_id", id);
            buildingMap.put("create_type", 2);
            buildingMap.put("job_type", 1);
            buildingMap.put("status", 1);
            buildingMap.put("build_status", 2);
            List<TblSystemJenkins> judgeMaualList = this.selectJenkinsByMap(buildingMap);
            // 获取开发中任务数
            int taskCount = this.countTaskBySystemId(id);
            sysInfomap.put("taskCount", taskCount);
            String parent_id = "parent_" + id + "";
            if (createType.equals("2")) {
                // 手动
                // 查询正在构建的数据
                Map<String, Object> clmap = new HashMap<>();
                clmap.put("SYSTEM_ID", id);
                clmap.put("CREATE_TYPE", 2);
                clmap.put("BUILD_STATUS", 2);// 2构建中
                clmap.put("JOB_TYPE", 1);
                List<TblSystemJenkins> list = this.selectJenkinsByMap(clmap);

                if (list != null && list.size() > 0) {
                    String nowJobName = "";
                    for (TblSystemJenkins t : list) {
                        String envName = t.getJobName();
                        nowJobName = nowJobName + envName + "正在构建,";
                    }
                    nowJobName = nowJobName.substring(0, nowJobName.length() - 1);
                    sysInfomap.put("nowStatus", "true");// 正在构建
                    sysInfomap.put("nowJobName", nowJobName);
                } else {
                    sysInfomap.put("nowStatus", "false");// 不再构建
                    sysInfomap.put("nowJobName", "");
                }
            } else {
                // 自动
                String envids = "";
                List<TblSystemScm> buildstatus = this.selectBuildingBySystemid(id);
                if (buildstatus != null && buildstatus.size() > 0) {
                    String nowEnvironmentType = "";
                    for (TblSystemScm t : buildstatus) {
                        String envName = envMap.get(t.getEnvironmentType().toString()).toString();
                        nowEnvironmentType = nowEnvironmentType + envName + "正在构建,";
                        envids = envids + t.getEnvironmentType().toString() + ",";
                    }
                    nowEnvironmentType = nowEnvironmentType.substring(0, nowEnvironmentType.length() - 1);
                    sysInfomap.put("nowStatus", "true");// 正在构建
                    sysInfomap.put("nowEnvironmentType", nowEnvironmentType);
                    sysInfomap.put("envids", envids);// 正在构建的环境
                } else {
                    sysInfomap.put("nowStatus", "false");// 不再构建
                    sysInfomap.put("nowEnvironmentType", "");
                    sysInfomap.put("envids", "");
                }
                // 获取该systemid下环境配置
                Map<String, Object> scmMap = new HashMap<>();
                scmMap.put("SYSTEM_ID", id);
                scmMap.put("status", 1);
                // 可选环境
                String choiceEnvids = "";
                List<TblSystemScm> tblSystemScms = tblSystemScmMapper.selectByMap(scmMap);
                for (TblSystemScm tblSystemScm : tblSystemScms) {
                    if (envids.indexOf(tblSystemScm.getEnvironmentType().toString()) == -1) {// 不包含
                        String envType = tblSystemScm.getEnvironmentType().toString();
                        String envName = envMap.get(envType).toString();
                        choiceEnvids = choiceEnvids + envType + ":" + envName + ",";
                    }
                }
                if (!choiceEnvids.equals("")) {
                    choiceEnvids = choiceEnvids.substring(0, choiceEnvids.length() - 1);
                }
                sysInfomap.put("choiceEnvids", choiceEnvids);// 显示环境
            }
            // 获取上次构建信息
            List<TblSystemJenkinsJobRun> jobrunlist = new ArrayList<>();
            if (createType.equals("1")) {// 自动
                jobrunlist = this.selectLastTimeBySystemId(id);
            } else {// 手动

                jobrunlist = this.selectLastTimeBySystemIdManual(id);
            }
            String lastBuildTime = "";
            String buildStatus = "";
            String status = "";
            String environmentType = "";
            String lastJobName = "";
            if (jobrunlist != null && jobrunlist.size() > 0) {
                if (jobrunlist.get(0).getBuildStatus() == null) {
                    status = "未构建";
                    buildStatus = "4"; // 未构建
                } else {
                    environmentType = jobrunlist.get(0).getEnvironmentType() + "";
                    status = jobrunlist.get(0).getBuildStatus() + "";
                    buildStatus = jobrunlist.get(0).getBuildStatus() + "";
                    if (status.equals("1")) {
                        status = "构建中";
                    } else if (status.equals("2")) {
                        status = "成功";
                    } else {
                        status = "失败";
                    }
                }
                lastJobName = jobrunlist.get(0).getJobName();
                if (jobrunlist.get(0).getEndDate() == null) {
                    lastBuildTime = "";
                } else {
                    lastBuildTime = sdf.format(jobrunlist.get(0).getEndDate());
                }
            } else {
                buildStatus = "4";
                lastBuildTime = "";
                status = "未构建";
            }
            sysInfomap.put("lastJobName", lastJobName);
            sysInfomap.put("environmentType", environmentType);
            sysInfomap.put("lastBuildTime", lastBuildTime);
            sysInfomap.put("buildStatus", buildStatus);
            sysInfomap.put("status", status);
            sysInfomap.put("level", "0");
            sysInfomap.put("expanded", "false");
            sysInfomap.put("parent", "");
            sysInfomap.put("loaded", "true");
            sysInfomap.put("key_id", parent_id);
            if (sysInfomap.get("architectureType").toString().equals(Constants.SERVER_MICRO_TYPE)) {// 1为微服务
                sysInfomap.put("isLeaf", "false");
                endList.add(sysInfomap);
                List<TblSystemModule> lists = this.selectSystemModule(id);// systemid获取子模块
                for (TblSystemModule tblSystemModule : lists) {
                    Map<String, Object> moduleData = new HashMap<>();
                    moduleData.put("level", "1");
                    moduleData.put("isLeaf", "true");
                    moduleData.put("expanded", "false");
                    moduleData.put("parent", parent_id);
                    moduleData.put("loaded", "true");
                    moduleData.put("systemCode", tblSystemModule.getModuleCode());
                    moduleData.put("id", tblSystemModule.getId() + "");
                    moduleData.put("systemName", tblSystemModule.getModuleName());
                    // 获取最新构建
                    Map<String, Object> moduleParam = new HashMap<>();
                    moduleParam.put("moduleId", tblSystemModule.getId());
                    moduleParam.put("systemId", id);
                    // 现在构建信息
                   // List<Map<String, Object>> nowModuleMessage = this.selectModuleBuildMessagesNow(moduleParam);
                    List<Map<String, Object>> nowModuleMessage = tblSystemJenkinsJobRunMapper.getModuleInfoIng(moduleParam);
                    String nowstatus = "false";
                    if (nowModuleMessage != null && nowModuleMessage.size() > 0) {
                        String nowEnvironmentType = "";
                        for (Map<String, Object> moumaps : nowModuleMessage) {
                            if (moumaps != null) {
                                if (moumaps.get("buildStatus").toString().equals("1")) {
                                    nowstatus = "true";
                                    String envName = envMap.get(moumaps.get("environmentType").toString()).toString();
                                    nowEnvironmentType = nowEnvironmentType + envName + "正在构建,";

                                }

                            }

                        }
                        if (!nowEnvironmentType.equals("")) {
                            nowEnvironmentType = nowEnvironmentType.substring(0, nowEnvironmentType.length() - 1);
                        }
                        moduleData.put("nowStatus", nowstatus);// 正在构建
                        moduleData.put("nowEnvironmentType", nowEnvironmentType);
                    } else {
                        moduleData.put("nowStatus", "false");// 不再构建
                        moduleData.put("nowEnvironmentType", "");
                    }
                    // 上次构建信息
                   // List<Map<String, Object>> moduleMessage = this.selectModuleBuildMessage(moduleParam);
                    List<Map<String, Object>> moduleMessage = tblSystemJenkinsJobRunMapper.selectModuleRunInfo(moduleParam);
                    if (moduleMessage != null && moduleMessage.size() > 0) {
                        for (Map<String, Object> mapResult : moduleMessage) {
                            if (mapResult.get("endDate") != null && !mapResult.get("endDate").toString().equals("")) {
                                moduleData.put("lastBuildTime", sdf.format(mapResult.get("endDate")));
                                moduleData.put("buildStatus", mapResult.get("buildStatus"));
                                moduleData.put("environmentType", mapResult.get("environmentType"));
                                break;
                            } else {
                                moduleData.put("lastBuildTime", "");
                                moduleData.put("buildStatus", "4");
                                moduleData.put("environmentType", "");
                            }
                        }
                    } else {
                        moduleData.put("lastBuildTime", "");
                        moduleData.put("buildStatus", "4");
                        moduleData.put("environmentType", "");
                    }
                    moduleData.put("createType", createType);
                    String child_id = "child_" + tblSystemModule.getId() + "";
                    moduleData.put("key_id", child_id);
                    moduleList.add(moduleData);
                    endList.add(moduleData);
                }

            } else {
                sysInfomap.put("isLeaf", "true");
                endList.add(sysInfomap);
            }
        }
        sysInfoList.addAll(moduleList);
        map.put("rows", endList);
        return map;
    }

    @Override
    @Transactional(readOnly = false)
    public void detailErrorStructure(TblSystemJenkins tblSystemJenkins, String jobType, String flag) {
        long systemJenkinsId = tblSystemJenkins.getId();
        long systemId = tblSystemJenkins.getSystemId();
        Map<String, Object> paramMap = new HashMap<>();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        List<TblSystemJenkinsJobRun> jobRuns = new ArrayList<>();
        paramMap.put("systemJenkinsId", systemJenkinsId);
        paramMap.put("systemId", systemId);
        paramMap.put("createType", 2);
        paramMap.put("jobType", jobType);
        if (flag.equals("1")) {//定时任务
            jobRuns = tblSystemJenkinsJobRunMapper.getErrorStructure(paramMap);
        } else {
            jobRuns = tblSystemJenkinsJobRunMapper.getErrorBreakeStructure(paramMap);
        }
        if (jobRuns.size() > 0) {
            TblSystemJenkins updateJenkins = new TblSystemJenkins();
            updateJenkins.setId(systemJenkinsId);
            if (jobType.equals(Constants.JOB_TYPE_DEPLOY)) {
                updateJenkins.setDeployStatus(1);//部署
            } else {
                updateJenkins.setBuildStatus(1);// 构建
            }
            updateJenkins.setLastUpdateDate(timestamp);
            tblSystemJenkinsMapper.updateById(updateJenkins);

            for (TblSystemJenkinsJobRun jobRun : jobRuns) {
                log.info("处理自定义坏数据:jobrunid" + jobRun.getId());
                // 更新系统JENKINS任务执行表
                TblSystemJenkinsJobRun tblSystemJenkinsJobRun = new TblSystemJenkinsJobRun();
                tblSystemJenkinsJobRun.setBuildStatus(3);// 构建失败
                tblSystemJenkinsJobRun.setEndDate(timestamp);
                tblSystemJenkinsJobRun.setLastUpdateDate(timestamp);
                if (flag.equals("1")) {
                    tblSystemJenkinsJobRun.setBuildLogs("失败");
                } else {
                    tblSystemJenkinsJobRun.setBuildLogs("强制结束");
                }
                tblSystemJenkinsJobRun.setId(jobRun.getId());
                tblSystemJenkinsJobRun.setStatus(2);
                tblSystemJenkinsJobRunMapper.updateById(tblSystemJenkinsJobRun);

                //更新testresult
                detailTestResult(jobRun.getId());


                // 更新子模块任务表
                TblSystemModuleJenkinsJobRun tblSystemModuleJenkinsJobRun = new TblSystemModuleJenkinsJobRun();
                tblSystemModuleJenkinsJobRun.setLastUpdateDate(timestamp);
                tblSystemModuleJenkinsJobRun.setSystemJenkinsJobRun(jobRun.getId());
                tblSystemModuleJenkinsJobRun.setStatus(2);
                tblSystemModuleJenkinsJobRun.setBuildStatus(3);
                tblSystemModuleJenkinsJobRunMapper.updateErrorInfo(tblSystemModuleJenkinsJobRun);

            }
        }
    }

    @Override
    public List<TblSystemScm> selectScmByMap(Map<String, Object> paramMap) {
        return tblSystemScmMapper.selectByMap(paramMap);
    }

    @Override
    public void detailAutoErrorStructure(TblSystemScm scm, String jobType, String flag, String jenkinsId) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        List<TblSystemJenkinsJobRun> jobRuns = new ArrayList<>();
        if (flag.equals("1")) {//定时任务
            paramMap.put("environmentType", scm.getEnvironmentType());
            paramMap.put("systemId", scm.getSystemId());
            paramMap.put("createType", 1);
            paramMap.put("jobType", jobType);
            jobRuns = tblSystemJenkinsJobRunMapper.getAutoErrorStructure(paramMap);
        } else {//2强制结束
            paramMap.put("environmentType", scm.getEnvironmentType());
            paramMap.put("systemId", scm.getSystemId());
            paramMap.put("createType", 1);
            paramMap.put("jobType", jobType);
            paramMap.put("systemJenkinsId", jenkinsId);
            jobRuns = tblSystemJenkinsJobRunMapper.getAutoBreakeErrorStructure(paramMap);
        }

        TblSystemScm tblSystemScm = getTblsystemScmById(scm.getId());
        tblSystemScm.setId(scm.getId());
        tblSystemScm.setLastUpdateDate(timestamp);
        if (jobType.equals("1")) {
            tblSystemScm.setBuildStatus(1);
        } else {
            tblSystemScm.setDeployStatus(1);
        }
        tblSystemScmMapper.updateById(tblSystemScm);

        if (jobRuns.size() > 0) {

            for (TblSystemJenkinsJobRun jobRun : jobRuns) {
                // 更新系统JENKINS任务执行表
                log.info("处理坏数据:jobrunid" + jobRun.getId());
                TblSystemJenkinsJobRun tblSystemJenkinsJobRun = new TblSystemJenkinsJobRun();
                tblSystemJenkinsJobRun.setBuildStatus(3);// 构建失败
                tblSystemJenkinsJobRun.setEndDate(timestamp);
                tblSystemJenkinsJobRun.setLastUpdateDate(timestamp);
                if (flag.equals("1")) {
                    tblSystemJenkinsJobRun.setBuildLogs("失败");
                } else {
                    tblSystemJenkinsJobRun.setBuildLogs("强制结束");
                }
                tblSystemJenkinsJobRun.setId(jobRun.getId());
                tblSystemJenkinsJobRun.setStatus(2);
                tblSystemJenkinsJobRunMapper.updateById(tblSystemJenkinsJobRun);
                //更新testresult
                detailTestResult(jobRun.getId());
                // 更新子模块任务表
                TblSystemModuleJenkinsJobRun tblSystemModuleJenkinsJobRun = new TblSystemModuleJenkinsJobRun();
                tblSystemModuleJenkinsJobRun.setLastUpdateDate(timestamp);
                tblSystemModuleJenkinsJobRun.setSystemJenkinsJobRun(jobRun.getId());
                tblSystemModuleJenkinsJobRun.setStatus(2);
                tblSystemModuleJenkinsJobRun.setBuildStatus(3);
                tblSystemModuleJenkinsJobRunMapper.updateErrorInfo(tblSystemModuleJenkinsJobRun);

            }

        }

    }


    @Override
    public void detailArtAutoErrorStructure(TblSystemJenkins tblSystemJenkins, String flag) {
        Map<String, Object> paramMap = new HashMap<>();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        List<TblSystemJenkinsJobRun> jobRuns = new ArrayList<>();
        tblSystemJenkins.setDeployStatus(1);
        tblSystemJenkinsMapper.updateById(tblSystemJenkins);
        paramMap.put("environmentType", tblSystemJenkins.getEnvironmentType());
        paramMap.put("systemId", tblSystemJenkins.getSystemId());
        paramMap.put("createType", tblSystemJenkins.getCreateType());
        paramMap.put("jobType", tblSystemJenkins.getJobType());
        paramMap.put("systemJenkinsId", tblSystemJenkins.getId());
        jobRuns = tblSystemJenkinsJobRunMapper.getAutoBreakeErrorStructure(paramMap);

        if (jobRuns.size() > 0) {
            for (TblSystemJenkinsJobRun jobRun : jobRuns) {
                // 更新系统JENKINS任务执行表
                log.info("处理坏数据:jobrunid" + jobRun.getId());
                TblSystemJenkinsJobRun tblSystemJenkinsJobRun = new TblSystemJenkinsJobRun();
                tblSystemJenkinsJobRun.setBuildStatus(3);// 构建失败
                tblSystemJenkinsJobRun.setEndDate(timestamp);
                tblSystemJenkinsJobRun.setLastUpdateDate(timestamp);
                tblSystemJenkinsJobRun.setStatus(2);
                if (flag.equals("1")) {
                    tblSystemJenkinsJobRun.setBuildLogs("失败");
                } else {
                    tblSystemJenkinsJobRun.setBuildLogs("强制结束");
                }
                tblSystemJenkinsJobRun.setId(jobRun.getId());

                detailTestResult(jobRun.getId());
                tblSystemJenkinsJobRunMapper.updateById(tblSystemJenkinsJobRun);
                // 更新子模块任务表
                TblSystemModuleJenkinsJobRun tblSystemModuleJenkinsJobRun = new TblSystemModuleJenkinsJobRun();
                tblSystemModuleJenkinsJobRun.setLastUpdateDate(timestamp);
                tblSystemModuleJenkinsJobRun.setSystemJenkinsJobRun(jobRun.getId());
                tblSystemModuleJenkinsJobRun.setStatus(2);
                tblSystemModuleJenkinsJobRunMapper.updateErrorInfo(tblSystemModuleJenkinsJobRun);

            }

        }

    }


    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAllSystemInfo(String systemInfo, Integer pageSize, Integer pageNum,
                                                String scmBuildStatus, Long uid, String[] refreshIds, String[] checkIds, String[] checkModuleIds, HttpServletRequest request) {// scmBuildStatus 1空闲 2构建中

        Object tblToolInfo = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
        Map<String, Object> envMap = JSON.parseObject(tblToolInfo.toString());
        getDeleteEnvName("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE", envMap);
        Map<String, Object> map = new HashMap<>();
        TblSystemInfo tblSystemInfo = new TblSystemInfo();
        if (StringUtils.isNotBlank(systemInfo)) {
            tblSystemInfo = JSONObject.parseObject(systemInfo, TblSystemInfo.class);
        }
        List<Map<String, Object>> moduleList = new ArrayList<>();
        Map<String, Object> systeminfoMap = new HashMap<>();
        int start = (pageNum - 1) * pageSize;
        systeminfoMap.put("start", start);
        systeminfoMap.put("pageSize", pageSize);
        systeminfoMap.put("systemInfo", tblSystemInfo);
        //Boolean flag =flagAdmin(String.valueOf(uid));
        LinkedHashMap codeMap = (LinkedHashMap) redisUtils.get(CommonUtil.getToken(request));
        List<String> roleCodes = (List<String>) codeMap.get("roles");


        if (roleCodes != null && roleCodes.contains("XITONGGUANLIYUAN")) {
        } else {
            systeminfoMap.put("uid", uid);
        }
        List<Map<String, Object>> sysInfoList = tblSystemInfoMapper.getAllSystemInfoByBuild(systeminfoMap);
        start = 0;
        systeminfoMap.put("start", start);
        systeminfoMap.put("pageSize", Integer.MAX_VALUE);
        List<Map<String, Object>> size = tblSystemInfoMapper.getAllSystemInfoByBuild(systeminfoMap);
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        List<Map<String, Object>> endList = new ArrayList<>();
        for (Map<String, Object> sysInfomap : sysInfoList) {
            Long id = (Long) sysInfomap.get("id");
            TblSystemInfo tblSystemInfoEnvs = tblSystemInfoMapper.selectById(id);

            String createType = "";
            if (sysInfomap.get("createType") == null) {
                continue;
            } else {
                createType = sysInfomap.get("createType").toString();
            }
            // 判断是否为空闲或构建中 空闲是每个环境下都没有在构建 自动
            List<TblSystemScm> judgeList = this.judgeScmBuildStatus(id);
            // 获取该sonar相关信息
            Map<String, Object> buildingMap = new HashMap<>();
            buildingMap.put("system_id", id);
            buildingMap.put("create_type", 2);
            buildingMap.put("job_type", 1);
            buildingMap.put("status", 1);
            buildingMap.put("build_status", 2);
            List<TblSystemJenkins> judgeMaualList = this.selectJenkinsByMap(buildingMap);
            if (scmBuildStatus != null && !scmBuildStatus.equals("")) {
                if (scmBuildStatus.equals("1")) {// 查询空闲状态system
                    if (createType.equals("1")) {
                        if (judgeList == null || judgeList.size() == 0) {// 空闲

                        } else {// 不是空闲
                            continue; // 跳出本次循环
                        }

                    } else {

                        if (judgeMaualList == null || judgeMaualList.size() == 0) {// 空闲

                        } else {// 不是空闲
                            continue; // 跳出本次循环
                        }

                    }

                } else {
                    if (createType.equals("1")) {
                        if (judgeList != null && judgeList.size() > 0) {// 构建中

                        } else {// 空闲
                            continue; // 跳出本次循环
                        }
                    } else {
                        if (judgeMaualList != null && judgeMaualList.size() > 0) {// 构建中

                        } else {// 空闲
                            continue; // 跳出本次循环
                        }
                    }

                }

            }

            // 获取开发中任务数
//			int taskCount = this.countTaskBySystemId(id);
//			sysInfomap.put("taskCount", taskCount);
            String parent_id = "parent_" + id + "";
            if (createType.equals("2")) {
                // 手动
                Map<String, Object> clmap = new HashMap<>();
                clmap.put("SYSTEM_ID", id);
                clmap.put("CREATE_TYPE", 2);
                clmap.put("BUILD_STATUS", 2);// 2构建中
                clmap.put("JOB_TYPE", 1);
                List<TblSystemJenkins> list = this.selectJenkinsByMap(clmap);
                if (list != null && list.size() > 0) {
                    String nowJobName = "";
                    for (TblSystemJenkins t : list) {
                        String envName = t.getJobName();
                        nowJobName = nowJobName + envName + "正在构建,";
                    }
                    nowJobName = nowJobName.substring(0, nowJobName.length() - 1);
                    sysInfomap.put("nowStatus", "true");// 正在构建
                    sysInfomap.put("nowJobName", nowJobName);
                } else {
                    sysInfomap.put("nowStatus", "false");// 不再构建
                    sysInfomap.put("nowJobName", "");
                }
            } else {
                // 自动
                String envids = "";
                List<TblSystemScm> buildstatus = this.selectBuildingBySystemid(id);
                if (buildstatus != null && buildstatus.size() > 0) {
                    String nowEnvironmentType = "";
                    for (TblSystemScm t : buildstatus) {
                        String envName = envMap.get(t.getEnvironmentType().toString()).toString();
                        nowEnvironmentType = nowEnvironmentType + envName + "正在构建,";
                        envids = envids + t.getEnvironmentType().toString() + ",";

                    }
                    nowEnvironmentType = nowEnvironmentType.substring(0, nowEnvironmentType.length() - 1);
                    sysInfomap.put("nowStatus", "true");// 正在构建
                    sysInfomap.put("nowEnvironmentType", nowEnvironmentType);
                    sysInfomap.put("envids", envids);// 正在构建的环境
                } else {
                    sysInfomap.put("nowStatus", "false");// 不再构建
                    sysInfomap.put("nowEnvironmentType", "");
                    sysInfomap.put("envids", "");
                }
                // 获取该systemid下环境配置
                Map<String, Object> scmMap = new HashMap<>();
                scmMap.put("SYSTEM_ID", id);
                scmMap.put("status", 1);
                // 可选环境
                String choiceEnvids = "";
                List<TblSystemScm> tblSystemScms = tblSystemScmMapper.selectByMap(scmMap);
                //非空
                tblSystemScms = detailSystemSccms(tblSystemScms);
                for (TblSystemScm tblSystemScm : tblSystemScms) {
                    if (envids.indexOf(tblSystemScm.getEnvironmentType().toString()) == -1) {// 不包含
                        String envType = tblSystemScm.getEnvironmentType().toString();
                        if (judgeDeleteEnv("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE", envType)) {

                        } else {

                            String envName = envMap.get(envType).toString();
                            //判断此环境是否在system 环境中
                            if (tblSystemInfoEnvs.getEnvironmentType() != null && !tblSystemInfoEnvs.getEnvironmentType().equals("")) {
                                boolean systemEnvsFlag = judegeSystemEnvs(tblSystemInfoEnvs.getEnvironmentType(), envType);
                                if (systemEnvsFlag) {
                                    choiceEnvids = choiceEnvids + envType + ":" + envName + ",";
                                }
                            }
                        }
                    }

                }
                if (!choiceEnvids.equals("")) {
                    choiceEnvids = choiceEnvids.substring(0, choiceEnvids.length() - 1);
                }
                sysInfomap.put("choiceEnvids", choiceEnvids);// 显示环境
            }

            // 获取上次构建信息
            List<TblSystemJenkinsJobRun> jobrunlist = new ArrayList<>();
            if (createType.equals("1")) {// 自动
                jobrunlist = this.selectLastTimeBySystemId(id);
            } else {// 手动
                jobrunlist = this.selectLastTimeBySystemIdManual(id);
            }
            String lastBuildTime = "";
            String buildStatus = "";
            String status = "";
            String environmentType = "";
            String lastJobName = "";
            if (jobrunlist != null && jobrunlist.size() > 0) {
                if (jobrunlist.get(0).getBuildStatus() == null) {
                    status = "未构建";
                    buildStatus = "4"; // 未构建
                } else {
                    environmentType = jobrunlist.get(0).getEnvironmentType() + "";
                    status = jobrunlist.get(0).getBuildStatus() + "";
                    buildStatus = jobrunlist.get(0).getBuildStatus() + "";
                    if (status.equals("1")) {
                        status = "构建中";
                    } else if (status.equals("2")) {
                        status = "成功";
                    } else {
                        status = "失败";
                    }
                }
                lastJobName = jobrunlist.get(0).getJobName();
                if (jobrunlist.get(0).getEndDate() == null) {
                    lastBuildTime = "";
                } else {
                    lastBuildTime = sdf.format(jobrunlist.get(0).getEndDate());
                }

            } else {
                buildStatus = "4";
                lastBuildTime = "";
                status = "未构建";
            }
            sysInfomap.put("lastJobName", lastJobName);
            sysInfomap.put("environmentType", environmentType);
            sysInfomap.put("lastBuildTime", lastBuildTime);
            sysInfomap.put("buildStatus", buildStatus);
            sysInfomap.put("status", status);
            sysInfomap.put("level", "0");
            if (refreshIds != null && Arrays.asList(refreshIds).contains(String.valueOf(id))) {
                sysInfomap.put("expanded", "true");
            } else {
                sysInfomap.put("expanded", "false");
            }

            if (checkIds != null && Arrays.asList(checkIds).contains(String.valueOf(id))) {
                sysInfomap.put("check", "true");
            } else {
                sysInfomap.put("check", "false");
            }

            sysInfomap.put("parent", "");
            sysInfomap.put("systemId", id);
            sysInfomap.put("loaded", "true");
            sysInfomap.put("key_id", parent_id);
            if (sysInfomap.get("architectureType").toString().equals(Constants.SERVER_MICRO_TYPE)
            ) {// 1为微服务
                sysInfomap.put("isLeaf", "false");
                endList.add(sysInfomap);
                List<TblSystemModule> lists = this.selectSystemModule(id);// systemid获取子模块
                for (TblSystemModule tblSystemModule : lists) {
                    Map<String, Object> moduleData = new HashMap<>();
                    if (checkModuleIds != null && Arrays.asList(checkModuleIds).contains(String.valueOf(tblSystemModule.getId()))) {
                        moduleData.put("check", "true");
                    } else {
                        moduleData.put("check", "false");
                    }

                    moduleData.put("level", "1");
                    moduleData.put("systemId", id);
                    moduleData.put("isLeaf", "true");
                    moduleData.put("expanded", "false");
                    moduleData.put("parent", parent_id);
                    moduleData.put("loaded", "true");
                    moduleData.put("systemCode", tblSystemModule.getModuleCode());
                    moduleData.put("id", tblSystemModule.getId() + "");
                    moduleData.put("systemName", tblSystemModule.getModuleName());
                    // 获取最新构建
                    Map<String, Object> moduleParam = new HashMap<>();
                    moduleParam.put("moduleId", tblSystemModule.getId());
                    moduleParam.put("systemId", id);
                    // 现在构建信息
//                    List<Map<String, Object>> nowModuleMessage = this.selectModuleBuildMessagesNow(moduleParam);
                    List<Map<String, Object>> nowModuleMessage = tblSystemJenkinsJobRunMapper.getModuleInfoIng(moduleParam);
                    String nowstatus = "false";
                    if (nowModuleMessage != null && nowModuleMessage.size() > 0) {
                        String nowEnvironmentType = "";
                        for (Map<String, Object> moumaps : nowModuleMessage) {
                            if (moumaps != null) {
                                if (moumaps.get("buildStatus").toString().equals("1")) {
                                    nowstatus = "true";
                                    String envName = envMap.get(moumaps.get("environmentType").toString()).toString();
                                    nowEnvironmentType = nowEnvironmentType + envName + "正在构建,";
                                }
                            }

                        }
                        if (!nowEnvironmentType.equals("")) {
                            nowEnvironmentType = nowEnvironmentType.substring(0, nowEnvironmentType.length() - 1);
                        }
                        moduleData.put("nowStatus", nowstatus);// 不再构建
                        moduleData.put("nowEnvironmentType", nowEnvironmentType);
                    } else {
                        moduleData.put("nowStatus", "false");// 不再构建
                        moduleData.put("nowEnvironmentType", "");
                    }

                    // 上次构建信息
            //        List<Map<String, Object>> moduleMessage = this.selectModuleBuildMessage(moduleParam);
                    List<Map<String, Object>> moduleMessage = tblSystemJenkinsJobRunMapper.selectModuleRunInfo(moduleParam);
                    if (moduleMessage != null && moduleMessage.size() > 0) {
                        // 没有数据
                        for (Map<String, Object> mapResult : moduleMessage) {
                            if (mapResult.get("endDate") != null && !mapResult.get("endDate").toString().equals("")) {
                                moduleData.put("lastBuildTime", sdf.format(mapResult.get("endDate")));
                                moduleData.put("buildStatus", mapResult.get("buildStatus"));
                                moduleData.put("environmentType", mapResult.get("environmentType"));
                                break;
                            } else {
                                moduleData.put("lastBuildTime", "");
                                moduleData.put("buildStatus", "4");
                                moduleData.put("environmentType", "");
                            }

                        }

                    } else {
                        moduleData.put("lastBuildTime", "");
                        moduleData.put("buildStatus", "4");
                        moduleData.put("environmentType", "");
                    }

                    moduleData.put("createType", createType);
                    String child_id = "child_" + tblSystemModule.getId() + "";
                    moduleData.put("key_id", child_id);
                    moduleList.add(moduleData);
                    endList.add(moduleData);

                }

            } else {
                sysInfomap.put("isLeaf", "true");
                endList.add(sysInfomap);
            }

        }

        sysInfoList.addAll(moduleList);
        map.put("rows", endList);
        map.put("total", size.size());
        return map;
    }

    private String getEnvName(String envType, String termCode) {
        Map<String, Object> param = new HashMap<>();
        param.put("envType", envType);
        param.put("termCode", termCode);
        List<Map<String, Object>> lists = tblSystemJenkinsJobRunMapper.getEnvName(param);
        if (lists.size() > 0) {
            return lists.get(0).get("VALUE_NAME").toString();
        } else {
            return "";
        }
    }

    @Override
    public void getDeleteEnvName(String termCode, Map<String, Object> envMap) {
        Map<String, Object> param = new HashMap<>();
        param.put("status", "2");
        param.put("termCode", termCode);
        List<Map<String, Object>> lists = tblSystemJenkinsJobRunMapper.getDeleteEnvName(param);


        for (Map<String, Object> map : lists) {
            String valueName = map.get("VALUE_NAME").toString();
            String valueCode = map.get("VALUE_CODE").toString();
            envMap.put(valueCode, valueName);
        }

    }

    @Override
    public void updateModuleInfoFristCompile(List<String> moduleList) {

        for (String id : moduleList) {
            TblSystemModule tblSystemModule = tblSystemModuleMapper.selectById(id);
            tblSystemModule.setFirstCompileFlag(2);
            tblSystemModuleMapper.updateById(tblSystemModule);
        }
    }

    @Override
    @Async
    public void detailU(Map<String, Object> map) {
        System.out.println("+++++" + JsonUtil.toJson(map) + "------" + Thread.currentThread().getName());
        try {
            Thread.sleep(8000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    *@author liushan
    *@Description 根据系统id查询系统信息
    *@Date 2019/11/20
    *@Param [systemId]
    *@return cn.pioneeruniverse.dev.entity.TblSystemInfo
    **/
    @Override
    public TblSystemInfo getTblSystemInfoById(Long systemId) {
        try {
            return  tblSystemInfoMapper.getTblSystemInfoById(systemId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String selectModulesNamesByRunId(String id) {


        return tblSystemModuleJenkinsJobRunMapper.selectModulesNamesByRunId(Long.parseLong(id));

    }


    //排除环境为空的情况
    private List<TblSystemScm> detailSystemSccms(List<TblSystemScm> tblSystemScms) {
        List<TblSystemScm> tblSystemScmsNew = new ArrayList<>();
        for (TblSystemScm tblSystemScm : tblSystemScms) {
            if (tblSystemScm.getEnvironmentType() != null && !tblSystemScm.getEnvironmentType().equals("")) {

                tblSystemScmsNew.add(tblSystemScm);
            }
        }
        return tblSystemScmsNew;

    }

    private boolean judegeSystemEnvs(String environmentType, String env) {
        boolean flag = false;
        String[] systemEnvs = environmentType.split(",");
        for (String envFlag : systemEnvs) {
            if (envFlag.equals(env)) {
                flag = true;
            }
        }
        return flag;
    }

    @Transactional(readOnly = false)
    public Map<String, Object> creatJenkinsJobScheduled(String sysId, String systemName, String serverType, String env,
                                                        HttpServletRequest request, TblSystemJenkins tblSystemJenkins) {
        Map<String, Object> result = new HashMap<>();
        long usrId = CommonUtil.getCurrentUserId(request);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Object redisEnvType = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
        Map<String, Object> envMap = JSON.parseObject(redisEnvType.toString());
        String envName = envMap.get(env).toString();
        TblSystemInfo tSystemInfo = tblSystemInfoMapper.getOneSystemInfo(Long.parseLong(sysId));
        Map<String, Object> map = new HashMap<>();
        List<String> modulesList = new ArrayList<>();
        String jobrun = "";
        Integer systemId = Integer.parseInt(sysId);
        // 获取主源码表id
        Map<String, Object> mapParam = new HashMap<>();
        mapParam.put("system_Id", systemId);
        mapParam.put("environment_Type", env);
        mapParam.put("status", 1);
        // 获取项目scm表数据 env与systemid确定唯一
        List<TblSystemScm> countList = this.getScmByParam(mapParam);
        if (countList == null || countList.size() == 0) {
            result.put("status", Constants.ITMP_RETURN_FAILURE);
            result.put("message", "请配置此环境下源码信息");
            return result;
        }
        TblSystemScm tblSystemScm = countList.get(0);
        String modules = "";
        // 获取该系统下所有module
        List<TblSystemModule> allmoduleList = this.selectSystemModule(Long.parseLong(sysId));
        if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {// 微服务

            for (TblSystemModule tblSystemModule : allmoduleList) {
                modules = modules + tblSystemModule.getId() + ",";
            }
            modules = modules.substring(0, modules.length() - 1);

            String[] alModule = modules.split(",");
            for (int i = 0; i < alModule.length; i++) {
                // 判断是否符合环境要求
                modulesList = this.Judge(alModule[i], modulesList, tblSystemScm.getId(), systemId);
            }
        }
        // 新增
        if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {
            modulesList = this.sortAndDetailModuleid(modulesList, tblSystemScm.getId(), systemId);
        }
        String jobname = tblSystemJenkins.getCronJobName();
        long jconfId = tblSystemJenkins.getId();
        // tblSystemJenkins=tblSystemJenkinsMapper.selectById(jconfId);
        TblToolInfo jenkinsTool = new TblToolInfo();
        // 获取toolid
        jenkinsTool = this.geTblToolInfo(tblSystemJenkins.getToolId());
        // 调用build的接口
        long systemScmId = tblSystemScm.getId();
        List<TblSystemModuleScm> tblSystemModuleScmList = new ArrayList<>();
        List<TblSystemModuleJenkinsJobRun> moduleRunJobList = new ArrayList<>();
        TblSystemModuleJenkinsJobRun resultModuleRun = new TblSystemModuleJenkinsJobRun();
        List<TblSystemModule> tblSystemModuleList = new ArrayList<>();
        if (jconfId > 0) {
            // TBL_SYSTEM_JENKINS_JOB_RUN 记录
            TblSystemJenkinsJobRun ttr = new TblSystemJenkinsJobRun();
            ttr.setSystemJenkinsId((long) jconfId);
            ttr.setSystemId((long) systemId);
            ttr.setJobName(jobname);
            ttr.setRootPom(".");
            ttr.setBuildStatus(1);// 自动构建默认成功
            ttr.setStartDate(timestamp);
            ttr.setStatus(1);
            ttr.setCreateDate(timestamp);
            ttr.setEnvironmentType(Integer.parseInt(env));
            ttr.setGoalsOptions("mvn clean install -Dmaven.test.skip=true");
            ttr.setCreateBy(usrId);
            ttr.setCreateType(1);
            ttr.setJobType(1);
            try {
                int jobNumber = iJenkinsBuildService.getNextBuildNumber(jenkinsTool, tblSystemJenkins, jobname);
                result.put("jobNumber", jobNumber);
                ttr.setJobRunNumber(jobNumber);
            } catch (Exception e) {
                e.printStackTrace();
            }
            long jobid = this.insertJenkinsJobRun(ttr);// jobId jenkins任务执行表id
            jobrun = jobid + "";
            if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {// 如果是1则插入module表
                for (String id : modulesList) {// 需要修改
                    TblSystemModuleJenkinsJobRun tj = new TblSystemModuleJenkinsJobRun();
                    tj.setSystemJenkinsJobRun(jobid);
                    tj.setSystemModuleId((long) Integer.parseInt(id));
                    tj.setCreateDate(timestamp);
                    tj.setStatus(1);// 正常
                    tj.setCreateBy(usrId);
                    tj.setSystemId((long) systemId);
                    tj.setSystemScmId(systemScmId);
                    tj.setJobName(jobname);
                    tj.setCreateType(1);// 1是自定义
                    tj.setJobType(1);// 构建

                    long moduleRunId = this.insertJenkinsModuleJobRun(tj);
                    // 获取信息
                    TblSystemModuleJenkinsJobRun mrid = this.selectByModuleRunId(moduleRunId);
                    moduleRunJobList.add(mrid);
                    tblSystemModuleList.add(this.getTblsystemModule((long) Integer.parseInt(id)));
                    Map<String, Object> moduleParam = new HashMap<>();
                    moduleParam.put("systemId", systemId);
                    // 增加
                    moduleParam.put("systemScmId", systemScmId);
                    moduleParam.put("systemModuleId", id);
                    moduleParam.put("status", 1);
                    List<TblSystemModuleScm> scmList = this.getModuleScmByParam(moduleParam);
                    if (scmList != null && scmList.size() > 0) {
                        tblSystemModuleScmList.add(scmList.get(0));
                    }

                }

            } else {
                // 传统服务也要插入modulejobrun为了获取sonar扫描结果
                TblSystemModuleJenkinsJobRun tj = new TblSystemModuleJenkinsJobRun();
                tj.setSystemJenkinsJobRun(jobid);
                tj.setSystemScmId(systemScmId);
                tj.setSystemId((long) systemId);
                tj.setCreateDate(timestamp);
                tj.setStatus(1);// 正常
                tj.setCreateBy(usrId);
                tj.setJobName(jobname);
                tj.setCreateType(1);
                tj.setJobType(1);
                Long trModuleRunId = this.insertJenkinsModuleJobRun(tj);
                resultModuleRun = this.selectByModuleRunId(trModuleRunId);
            }

        }
        TblToolInfo sourceTool = new TblToolInfo();
        // 获取svntoolinfo
        if (tblSystemScm.getToolId() != null) {
            sourceTool = this.geTblToolInfo(tblSystemScm.getToolId());
        }

        // 先判断此微服务下是否有的sonar是否有toolid
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("SYSTEM_SCM_ID", tblSystemScm.getId());
        columnMap.put("SYSTEM_ID", systemId);
        List<TblSystemSonar> lists = this.getSonarByMap(columnMap);
        Long randomSonar = new Long(0);
        if (lists == null || lists.size() == 0) {
            // 随机选取一个toolld
            randomSonar = this.getRandomSonarId(env);

        } else {
            randomSonar = lists.get(0).getToolId();
        }

        if (serverType.equals(Constants.SERVER_MICRO_TYPE)) {// 微服务
            List<TblSystemSonar> tblSystemSonarList = new ArrayList<TblSystemSonar>();

            for (TblSystemModule tblSystemModule : tblSystemModuleList) {

                TblSystemSonar tblSystemSonar = assembleSonar(systemId, env, tSystemInfo, tblSystemScm,
                        tblSystemModule, timestamp, usrId, randomSonar, jconfId);
                tblSystemSonarList.add(tblSystemSonar);
            }

            result.put("tblSystemSonarList", tblSystemSonarList);
        } else {
            TblSystemSonar tblSystemSonar = assembleSonar(systemId, env, tSystemInfo, tblSystemScm, null, timestamp,
                    usrId, randomSonar, jconfId);
            result.put("tblSystemSonar", tblSystemSonar);
            result.put("resultModuleRun", resultModuleRun);
        }
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        result.put("serverType", serverType);
        result.put("tSystemInfo", tSystemInfo);
        result.put("tblSystemScm", tblSystemScm);
        result.put("tblSystemModuleList", tblSystemModuleList);
        result.put("tblSystemModuleScmList", tblSystemModuleScmList);
        if (tblSystemModuleScmList.size() > 0) {
            List<TblToolInfo> sourceToolList = new ArrayList<>();
            //微服务
            for (TblSystemModuleScm tms : tblSystemModuleScmList) {
                sourceToolList.add(tblToolInfoMapper.selectByPrimaryKey(tms.getToolId()));
            }
            result.put("sourceToolList", sourceToolList);
        }


        result.put("tblSystemJenkins", tblSystemJenkins);
        result.put("jenkinsTool", jenkinsTool);
        result.put("sourceTool", sourceTool);
        result.put("jobrun", jobrun);
        result.put("moduleRunJobList", moduleRunJobList);
        result.put("envName", envName);
        result.put("userId", CommonUtil.getCurrentUserId(request));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemScm> selectSystemScmByMap(Map<String, Object> param) {

        return tblSystemScmMapper.selectByMap(param);
    }

    @Transactional(readOnly = false)
    @Override
    public Map<String, Object> buildJobManualScheduled(HttpServletRequest request, String systemJenkisId) {

        Map<String, Object> paramMap = new HashMap<>();
        try {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            long usrId = CommonUtil.getCurrentUserId(request);
            Map<String, Object> map = new HashMap<>();
            map.put("id", systemJenkisId);
            List<TblSystemJenkins> list = this.selectJenkinsByMap(map);
            TblSystemJenkins tblSystemJenkins = list.get(0);
            String jobName = tblSystemJenkins.getJobName();
            // tblSystemJenkins.setBuildStatus(1);// 构建中
            // 修改此状态
            this.updateJenkins(tblSystemJenkins);
            // 获取jenkinstoolinfo
            TblToolInfo jenkinsToolInfo = this.geTblToolInfo(tblSystemJenkins.getToolId());
            // 插入tbl_system_sonar
            TblSystemSonar tblSystemSonar = assembleSonarManual(timestamp, tblSystemJenkins.getSystemId(), usrId,
                    Long.parseLong(systemJenkisId));
            TblSystemJenkinsJobRun ttr = new TblSystemJenkinsJobRun();
            ttr.setSystemJenkinsId(Long.parseLong(systemJenkisId));
            ttr.setSystemId(tblSystemJenkins.getSystemId());
            ttr.setJobName(jobName);
            ttr.setRootPom(".");
            ttr.setBuildStatus(1);
            ttr.setStartDate(timestamp);
            ttr.setStatus(1);
            ttr.setCreateDate(timestamp);
            ttr.setJobType(1);

            ttr.setGoalsOptions("mvn clean install -Dmaven.test.skip=true");
            ttr.setCreateBy(usrId);
            ttr.setCreateType(2);// 手动
            long jobid = this.insertJenkinsJobRun(ttr);
            TblSystemModuleJenkinsJobRun tj = new TblSystemModuleJenkinsJobRun();
            tj.setSystemJenkinsJobRun(jobid);
            tj.setCreateDate(timestamp);
            tj.setStatus(1);
            tj.setCreateBy(usrId);
            tj.setSystemId(tblSystemJenkins.getSystemId());
            tj.setJobName(jobName);
            tj.setCreateType(2);
            tj.setJobType(1);
            long moduleJobRunId = this.insertJenkinsModuleJobRun(tj);
            // paramMap.put("jsonParam", jsonParam);
            paramMap.put("jobRunId", jobid);
            paramMap.put("systemJenkinsId", systemJenkisId);
            paramMap.put("moduleJobRunId", moduleJobRunId);
            paramMap.put("systemSonarId", tblSystemSonar.getId());
            paramMap.put("jobName", jobName);
            paramMap.put("jenkinsToolInfo", jenkinsToolInfo);
        } catch (Exception e) {
            this.handleException(e);
            throw e;

        }
        return paramMap;
    }

    @Override
    public void insertSystemJenkins(TblSystemJenkins tblSystemJenkins) {
        tblSystemJenkinsMapper.insertNew(tblSystemJenkins);

    }

    @Override
    public TblSystemJenkins selectSystemJenkinsById(String systemJenkinsId) {
        return tblSystemJenkinsMapper.selectById(systemJenkinsId);
    }

    @Override
    public void updateSystemScm(TblSystemScm tblSystemScm) {
        tblSystemScmMapper.updateById(tblSystemScm);

    }

    @Override
    public void updateModuleStatusBySystemId(Map<String, Object> map) {
        tblSystemModuleScmMapper.updateModuleStatusBySystemId(map);

    }

    @Override
    public void updateModuleScm(Map<String, Object> moduleScmid) {
        tblSystemModuleScmMapper.updateModuleScm(moduleScmid);

    }

    @Override
    public void updateConfigInfo(Map<String, Object> configMap, Map<String, Object> paramMap) {
        String jobConfiguration = "";
        if (configMap.get("jobConfiguration") == null) {

        } else {
            jobConfiguration = configMap.get("jobConfiguration").toString();
        }
        String jobRunId = paramMap.get("jobRunId").toString();
        TblSystemJenkinsJobRun tblSystemJenkinsJobRun = tblSystemJenkinsJobRunMapper
                .selectById(Long.parseLong(jobRunId));
        tblSystemJenkinsJobRun.setJobConfiguration(jobConfiguration);
        tblSystemJenkinsJobRunMapper.updateById(tblSystemJenkinsJobRun);

    }

    @Override
    public List<Map<String, Object>> getBreakName(String createType, long systemId, String jobType) {
        Object redisEnvType = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
        Map<String, Object> envMap = JSON.parseObject(redisEnvType.toString());
        List<Map<String, Object>> resultList = new ArrayList<>();
        TblSystemInfo tblSystemInfo = tblSystemInfoMapper.selectById(systemId);
        Integer deployType = tblSystemInfo.getDeployType();
        if ((createType.equals(Constants.CREATE_TYPE_AUTO) && jobType.equals(Constants.JOB_TYPE_DEPLOY) && deployType == 1) || (createType.equals(Constants.CREATE_TYPE_AUTO) && jobType.equals(Constants.JOB_TYPE_BUILD))) {// 自动构建和自动源码部署
            Map<String, Object> param = new HashMap<>();
            List<TblSystemScm> list = new ArrayList<>();
            param.put("status", 1);
            param.put("SYSTEM_ID", systemId);
            if (jobType.equals("2")) {
                //param.put("DEPLOY_STATUS", 2);
                list = tblSystemScmMapper.selectBreakName(systemId);
            } else {
                param.put("BUILD_STATUS", 2);
                list = tblSystemScmMapper.selectByMap(param);
            }

            //处理环境为空的情况
            list = detailSystemSccms(list);

            for (TblSystemScm scm : list) {
                Map<String, Object> result = new HashMap<>();
                String envName = envMap.get(scm.getEnvironmentType().toString()).toString();
                result.put("envName", envName);
                TblSystemInfo tSystemInfo = tblSystemInfoMapper.selectById(systemId);
                String jobName = "";
                if (jobType.equals("1")) {// 自动构建
                    jobName = tSystemInfo.getSystemCode() + "_" + scm.getEnvironmentType().toString() + "_" + String.valueOf(systemId);
                } else {// 自动源码部署

                    jobName = tSystemInfo.getSystemCode() + "_" + scm.getEnvironmentType().toString() + "_" + String.valueOf(systemId) + "_deploy";
                }
                // 获取此jenkinsid
                Map<String, Object> jenkinsParam = new HashMap<>();
                jenkinsParam.put("system_id", systemId);
                jenkinsParam.put("CREATE_TYPE", createType);
                jenkinsParam.put("JOB_TYPE", jobType);
                jenkinsParam.put("SYSTEM_SCM_ID", scm.getId());
                List<TblSystemJenkins> tblSystemJenkinsList = tblSystemJenkinsMapper.selectByMap(jenkinsParam);
                if (tblSystemJenkinsList != null && tblSystemJenkinsList.size() > 0) {
                    result.put("toolId", tblSystemJenkinsList.get(0).getToolId());
                    result.put("jenkinsId", tblSystemJenkinsList.get(0).getId());
                } else {
                    continue;
                }
                result.put("jobName", jobName);
                result.put("createType", "1");//自动
                resultList.add(result);
            }

        } else {


            Map<String, Object> param = new HashMap<>();
            param.put("status", 1);
            param.put("system_Id", systemId);
            param.put("job_Type", jobType);
            param.put("create_Type", createType);
            List<TblSystemJenkins> list = new ArrayList<>();
            if (createType.equals("1") && deployType == 2) {//自动制品部署
                Map<String, Object> packagPparam = new HashMap<>();
                packagPparam.put("status", 1);
                packagPparam.put("systemId", systemId);
                packagPparam.put("jobType", jobType);
                packagPparam.put("createType", createType);
                list = tblSystemJenkinsMapper.selectBreakName(packagPparam);
            } else if (jobType.equals("2")) {
                param.put("DEPLOY_STATUS", 2);
                list = tblSystemJenkinsMapper.selectByMap(param);
            } else {
                param.put("BUILD_STATUS", 2);
                list = tblSystemJenkinsMapper.selectByMap(param);
            }


            for (TblSystemJenkins jenkins : list) {
                Map<String, Object> result = new HashMap<>();
                String jobName = jenkins.getJobName();
                result.put("jobName", jobName);
                result.put("toolId", jenkins.getToolId());
                result.put("jenkinsId", jenkins.getId());
                if (createType.equals("1") && deployType == 2) {
                    result.put("createType", "1");//自动制品部署
                } else {
                    result.put("createType", "2");//手动
                }
                resultList.add(result);

            }

        }

        return resultList;

    }

    private List<String> sortAndDetailModuleid(List<String> moduleId, long scmId, long systemId) {
        // 查询出必须jar包
        Map<String, Object> param = new HashMap<>();
        param.put("system_id", systemId);
        param.put("status", 1);
        param.put("BUILD_DEPENDENCY", 1);
        List<TblSystemModule> moduleList = tblSystemModuleMapper.selectByMap(param);
        for (TblSystemModule tm : moduleList) {
            if (!moduleId.contains(String.valueOf(tm.getId()))) {
                //新增判断此module是否配置
                List<TblSystemModuleScm> moduleScms = judge(String.valueOf(tm.getId()), scmId, Integer.parseInt(String.valueOf(systemId)));
                if (moduleScms != null && moduleScms.size() > 0) {
                    moduleId.add(String.valueOf(tm.getId()));
                }
            }

        }
        Map<String, Object> moduleParam = new HashMap<>();
        moduleParam.put("ids", moduleId);
        List<TblSystemModule> endList = new ArrayList<>();
        if (moduleId.size() > 0) {
            endList = tblSystemModuleMapper.sortModule(moduleParam);
        }

        List<String> result = new ArrayList<>();
        for (TblSystemModule tm : endList) {
            result.add(String.valueOf(tm.getId()));
        }

        return result;

    }

    @Override
    public List<TblToolInfo> getTblToolInfo(Map<String, Object> map) {

        return tblToolInfoMapper.selectByMap(map);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> selectMessageBySystemId(long id, String type) {
        Map<String, Object> mapParam = new HashMap<>();
        mapParam.put("systemId", id);
        mapParam.put("createType", type);
        List<Map<String, Object>> maps = tblSystemJenkinsJobRunMapper.selectMessageBySystemId(mapParam);
        return maps;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemJenkinsJobRun> selectLastTimeBySystemId(long id) {
        return tblSystemJenkinsJobRunMapper.selectLastTimeBySystemId(id);

    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemModule> selectSystemModule(long systemId) {
        return tblSystemModuleMapper.selectSystemModule(systemId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemModuleJenkinsJobRun> selectLastTimeByModuleId(long moduleId) {
        return tblSystemModuleJenkinsJobRunMapper.selectLastTimeByModuleId(moduleId);
    }

    @Override
    @Transactional(readOnly = true)
    public TblSystemJenkinsJobRun selectBuildMessageById(long jobrunId) {

        return tblSystemJenkinsJobRunMapper.selectById(jobrunId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> selectModuleBuildMessage(Map<String, Object> map) {
        return tblSystemJenkinsJobRunMapper.selectModuleBuildMessage(map);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> selectModuleBuildMessageNow(Map<String, Object> map) {
        return tblSystemJenkinsJobRunMapper.selectModuleBuildMessageNow(map);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> selectModuleBuildMessagesNow(Map<String, Object> map) {
        return tblSystemJenkinsJobRunMapper.selectModuleBuildMessagesNow(map);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> selectMessageBySystemIdAndPage(long id, Integer pageNumber, Integer pageSize,
                                                                    String type) {
        Map<String, Object> mapParam = new HashMap<>();
        int start = (pageNumber - 1) * pageSize;
        mapParam.put("start", start);
        mapParam.put("pageSize", pageSize);
        mapParam.put("systemId", id);
        mapParam.put("createType", type);
        List<Map<String, Object>> maps = tblSystemJenkinsJobRunMapper.selectMessageBySystemIdAndPage(mapParam);
        return maps;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemJenkinsJobRun> selectNowTimeBySystemId(long id) {
        return tblSystemJenkinsJobRunMapper.selectNowTimeBySystemId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemModuleScm> judge(String moduleid, Long scmId, Integer systemId) {
        Map<String, Object> map = new HashMap<>();
        map.put("systemId", systemId);
        map.put("systemScmId", scmId);
        map.put("systemModuleId", moduleid);
        map.put("status", 1);
        return tblSystemModuleScmMapper.judge(map);
    }

    @Override
    @Transactional(readOnly = false)
    public void updateSystemScmBuildStatus(TblSystemScm t) {
        tblSystemScmMapper.updateByPrimaryKeySelective(t);

    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemScm> selectBuildingBySystemid(Long id) {
        return tblSystemScmMapper.selectBuildingBySystemid(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemScm> judgeScmBuildStatus(Long id) {
        return tblSystemScmMapper.judgeScmBuildStatus(id);
    }

    @Override
    @Transactional(readOnly = true)
    public int countScmBuildStatus(Map<String, Object> map) {
        return tblSystemScmMapper.countScmBuildStatus(map);
    }

    public void handleException(Exception e) {
        e.printStackTrace();
        log.error(e.getMessage(), e);

    }

    private int getRandom(int size) {
        SecureRandom random = new SecureRandom();
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        int n = random.nextInt(size);
        return n;
    }


    public long creOrUpdSjenkins(TblSystemJenkins tblSystemJenkins, String env, String createType, String jobType) {
        // 查询是否有此systemid 数据
        try {
            Map<String, Object> mapParam = new HashMap<>();
            mapParam.put("system_Id", tblSystemJenkins.getSystemId());
            mapParam.put("system_Scm_Id", tblSystemJenkins.getSystemScmId());
            mapParam.put("status", 1);
            mapParam.put("job_Type", jobType);
            mapParam.put("CREATE_TYPE", createType);
            List<TblSystemJenkins> list = tblSystemJenkinsMapper.selectByMap(mapParam);
            if (list != null && list.size() > 0) {
                TblSystemJenkins tblSystemJenkinsTemp = list.get(0);
                tblSystemJenkins.setId(tblSystemJenkinsTemp.getId());
                String jobNameFlag = "";
                jobNameFlag = tblSystemJenkinsTemp.getJobName();
                if (jobNameFlag == null || jobNameFlag.equals("")) {// 没有构建或定时过数据
                    List<TblToolInfo> jenktools = getJenkinsByEnv(env);
                    // 随机取一个值
                    int n = this.getRandom(jenktools.size());
                    TblToolInfo tblToolInfo = jenktools.get(n);
                    tblSystemJenkins.setToolId(tblToolInfo.getId());
                    tblSystemJenkinsMapper.updateById(tblSystemJenkins);
                    return tblSystemJenkins.getId();
                } else {
                    //判断此toolid是否更改环境
                    TblToolInfo netJenkinsInfo = tblToolInfoMapper.selectByPrimaryKey(tblSystemJenkinsTemp.getToolId());
                    if (netJenkinsInfo.getEnvironmentType() != null &&
                            netJenkinsInfo.getStatus() == 1 && judgEnv(netJenkinsInfo.getEnvironmentType(), env)) {

                    } else {
                        //获取新的jenkins
                        List<TblToolInfo> jenktools = getJenkinsByEnv(env);
                        int n = this.getRandom(jenktools.size());
                        TblToolInfo tblToolInfo = jenktools.get(n);
                        tblSystemJenkinsTemp.setToolId(tblToolInfo.getId());
                    }
                    tblSystemJenkinsTemp.setLastUpdateDate(tblSystemJenkins.getCreateDate());
                    tblSystemJenkinsTemp.setLastUpdateBy(tblSystemJenkins.getCreateBy());
                    tblSystemJenkinsTemp.setJobName(tblSystemJenkins.getJobName());
                    tblSystemJenkinsMapper.updateById(tblSystemJenkinsTemp);
                    return tblSystemJenkinsTemp.getId();
                }

            } else {

                List<TblToolInfo> jenktools = getJenkinsByEnv(env);
                // 随机取一个值
                int n = this.getRandom(jenktools.size());
                TblToolInfo tblToolInfo = jenktools.get(n);
                tblSystemJenkins.setToolId(tblToolInfo.getId());
                tblSystemJenkinsMapper.insertNew(tblSystemJenkins);
                return tblSystemJenkins.getId();

            }
        } catch (Exception e) {

            this.handleException(e);
            throw e;
        }

    }


    private List<TblToolInfo> getJenkinsByEnv(String env) {
        Map<String, Object> jenkinsParam = new HashMap<>();
        jenkinsParam.put("toolType", 4);
        jenkinsParam.put("environmentType", env);
        jenkinsParam.put("status", 1);
        List<TblToolInfo> jenktools = tblToolInfoMapper.selectToolByEnv(jenkinsParam);
        return jenktools;
    }



    @Override
    public void insertModuleRunCallBack(List<Integer> moduleRunIds, long jobid, Timestamp endTime, String flag, Timestamp startTime,String moduleJson) {

        Map<String,Object> moduleList = (Map<String,Object>) JSONArray.parse(moduleJson);
        for(String key:moduleList.keySet()){
            String value = moduleList.get(key).toString();
            Map<String,Object> map = (Map<String,Object>) JSONArray.parse(value);
            SimpleDateFormat time = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
            Integer modulejobRunId=Integer.parseInt(map.get("moduleRunId").toString());
            Integer buildStatus=Integer.parseInt(map.get("buildStatus").toString());
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
            if (flag.equals("2")) {
                // 插入数据
                moduleJobrun.setSystemJenkinsJobRun(jobid);
                moduleJobrun.setId(null);
                tblSystemModuleJenkinsJobRunMapper.insertNew(moduleJobrun);
            } else {

                tblSystemModuleJenkinsJobRunMapper.updateByPrimaryKeySelective(moduleJobrun);
            }

        }



//
//
//        for (Integer modulejobRunId : moduleRunIds) {
//
//            TblSystemModuleJenkinsJobRun moduleJobrun = tblSystemModuleJenkinsJobRunMapper
//                    .selectById(modulejobRunId);
//            moduleJobrun.setLastUpdateDate(endTime);
//            moduleJobrun.setCreateDate(startTime);
//            if (flag.equals("2")) {
//                // 插入数据
//                moduleJobrun.setSystemJenkinsJobRun(jobid);
//                moduleJobrun.setId(null);
//                tblSystemModuleJenkinsJobRunMapper.insertNew(moduleJobrun);
//            } else {
//
//                tblSystemModuleJenkinsJobRunMapper.updateByPrimaryKeySelective(moduleJobrun);
//            }
//
//        }


    }


    private boolean judgEnv(String envs, String env) {
        boolean flag = false;
        if (envs != null) {
            String[] envFlags = envs.split(",");
            for (String envFlag : envFlags) {
                if (envFlag.equals(env)) {
                    flag = true;
                    break;
                }
            }
        }
        return flag;
    }

    @Override
    public void judgeJenkins(TblSystemJenkins tblSystemJenkins, String jobName) {
        TblSystemScm tblSystemScm = tblSystemScmMapper.selectById(tblSystemJenkins.getSystemScmId());
        String env = String.valueOf(tblSystemScm.getEnvironmentType());
        List<TblToolInfo> jenktools = getJenkinsByEnv(env);
        Long toolId = tblSystemJenkins.getToolId();
        TblToolInfo jenkinsTool = tblToolInfoMapper.selectByPrimaryKey(toolId);
        if (jenkinsTool.getStatus() == 1) {
            //判断此jenkins是否还在此环境中
            if (!judgEnv(jenkinsTool.getEnvironmentType(), env)) {
                //jenkins已经改变
                int n = this.getRandom(jenktools.size());
                TblToolInfo tblToolInfo = jenktools.get(n);
                tblSystemJenkins.setToolId(tblToolInfo.getId());
                try {
                    //将之前的定时任务销毁
                    iJenkinsBuildService.deleteJob(jenkinsTool, jobName);
                } catch (Exception e) {
                    //不做任何处理
                }


            }


        } else {
            int n = this.getRandom(jenktools.size());
            TblToolInfo tblToolInfo = jenktools.get(n);
            tblSystemJenkins.setToolId(tblToolInfo.getId());
            //将之前的定时任务销毁
            try {
                //将之前的定时任务销毁
                iJenkinsBuildService.deleteJob(jenkinsTool, jobName);
            } catch (Exception e) {
                //不做任何处理
            }
        }

    }

    @Override
    /**
     * @param flag flag0 0是制品部署 1是其他
     */
    public String getJobNameByEnv(String envName, long systemId, String createType, String jobType, int flag) {
        String env = "";
        Object redisEnvType = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
        Map<String, Object> envMap = JSON.parseObject(redisEnvType.toString());
        for (Map.Entry<String, Object> m : envMap.entrySet()) {
            if (m.getValue().toString().equals(envName)) {
                env = m.getKey();
                break;
            }

        }


        String jobName = "";
        TblSystemInfo tSystemInfo = tblSystemInfoMapper.selectById(systemId);
        if (createType.equals(Constants.CREATE_TYPE_AUTO) && jobType.equals(Constants.JOB_TYPE_BUILD)) {// 自动构建
            jobName = tSystemInfo.getSystemCode() + "_" + env + "_" + String.valueOf(systemId);
        } else if (createType.equals(Constants.CREATE_TYPE_AUTO) && jobType.equals(Constants.JOB_TYPE_DEPLOY) && flag == 1) {// 自动源码部署

            jobName = tSystemInfo.getSystemCode() + "_" + env + "_" + String.valueOf(systemId) + "_deploy";
        } else {
            jobName = tSystemInfo.getSystemCode() + "_" + env + "_" + String.valueOf(systemId) + "_packagedeploy";

        }
        return jobName;
    }

    @Override
    public TblSystemScm getTblsystemScmById(long id) {
        return tblSystemScmMapper.selectById(id);
    }

    @Override
    public Map<String, Object> getEnvBySystemId(long systemId) {
        TblSystemInfo tblSystemInfo = tblSystemInfoMapper.selectById(systemId);
        String systemEnvs = tblSystemInfo.getEnvironmentType();
        String[] systemEnvsArray = systemEnvs.split(",");
        Object redisEnvType = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
        Map<String, Object> envMap = JSON.parseObject(redisEnvType.toString());
        Map<String, Object> resultMap = new HashMap<>();
        for (String key : envMap.keySet()) {
            for (String envId : systemEnvsArray) {
                if (key.equals(envId)) {
                    resultMap.put(key, envMap.get(key));
                }
            }
        }

        return resultMap;

    }

    @Override
    public String detailByteBylog(String log, int defaultSize) {
        int size = 0;
        try {
            byte[] bytes = log.getBytes("UTF-8");
            size = bytes.length;
            if (size > defaultSize) {

                int tempLen = new String(bytes, 0, defaultSize, "UTF-8").length();
                log = log.substring(log.length() - tempLen, log.length());

                log = "日志过长已截取........\n" + log;

                //log = log.substring(1, log.length());
                //detailByteBylog(log,defaultSize);
            }
        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        try {
            int size2 = log.getBytes("UTF-8").length;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return log;


    }
    
	public Integer getJobRunLastJobNumber(TblSystemJenkinsJobRun jobRun) {
		EntityWrapper<TblSystemJenkinsJobRun> wrapper = new EntityWrapper<TblSystemJenkinsJobRun>(jobRun);
		wrapper.setSqlSelect("JOB_RUN_NUMBER as jobRunNumber");
		wrapper.eq("STATUS", 1);
		wrapper.isNotNull("JOB_RUN_NUMBER");
		wrapper.orderBy("START_DATE", false);
		wrapper.last("limit 1");
		List<TblSystemJenkinsJobRun> list = tblSystemJenkinsJobRunMapper.selectList(wrapper);
		Integer lastJobNumber = 0;
		if (list != null && list.size() > 0) {
			lastJobNumber = list.get(0).getJobRunNumber();
		}
    	return lastJobNumber;
    }


    public void detailAutoLogTest(Map<String, Object> map) {
        String resultLog = "";
        String jobRunId = map.get("jobRunId").toString();
        String log = map.get("log").toString();
        String status = map.get("status").toString();

        TblSystemJenkinsJobRun tblSystemJenkinsJobRun = tblSystemJenkinsJobRunMapper.selectById(jobRunId);
        String buildlogs = tblSystemJenkinsJobRun.getBuildLogs();
        String info = "";
        try {
            info = new String("\n自动化平台返回日志信息如下:\n".getBytes("UTF-8"), "ISO8859-1");
            log = new String(log.getBytes("UTF-8"), "ISO8859-1");
            buildlogs = buildlogs + info + log;
            resultLog = new String(buildlogs.getBytes("ISO8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        tblSystemJenkinsJobRun.setBuildStatus(Integer.parseInt(status));

        //增加邮件
        tblSystemJenkinsJobRun.setBuildLogs(resultLog);
        tblSystemJenkinsJobRunMapper.updateById(tblSystemJenkinsJobRun);

    }


    @Override
    public void detailAutoLog(Map<String, Object> map) {

        log.info("已进入日志处理+++++++++++++");
        String autoTest = "false";
        Map<String, Object> autoMap = new HashMap<>();
        if (map.get("autoMap") != null) {
            autoMap = (Map<String, Object>) map.get("autoMap");
            autoTest = autoMap.get("autoTest").toString();
        }

        //	List<Map<String,Object>>   testConfig= (List<Map<String, Object>>) autoMap.get("testConfig");
        String resultLog = "";
        String jobRunId = map.get("jobRunId").toString();
        String loginfo = map.get("log").toString();
        String status = map.get("status").toString();

        TblSystemJenkinsJobRun tblSystemJenkinsJobRun = tblSystemJenkinsJobRunMapper.selectById(jobRunId);
        String buildlogs = tblSystemJenkinsJobRun.getBuildLogs();
        String info = "";
        try {

            info = new String("\n自动化平台返回日志信息如下:\n".getBytes("UTF-8"), "ISO8859-1");
            log.info("info+++"+info);
            loginfo = new String(loginfo.getBytes("UTF-8"), "ISO8859-1");
            buildlogs = buildlogs + info + loginfo;
            resultLog = new String(buildlogs.getBytes("ISO8859-1"), "UTF-8");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        if (autoTest.equals("true") && status.equals("2")) { //自动化运维失败不调用测试反之调用
            Map<String, Object> mapPost = autoMap;
            List<Map<String, Object>> testConfig = (List<Map<String, Object>>) mapPost.get("testConfig");
            List<Map<String, Object>> uiTestConfig = (List<Map<String, Object>>) mapPost.get("uiTestConfig");
            if (testConfig.size() > 0) {
                for (Map<String, Object> resultMap : testConfig) {
                    mapPost.put("testConfig", resultMap);
                    mapPost.put("testType", "1");
                    //调用测试接口
                    Map<String, Object> paramMap = new HashMap<String, Object>() {{
                        putAll(mapPost);
                    }};
                    threadPool.submit(new StructureServiceImpl.MyRun(paramMap));
                }
            }


            if (uiTestConfig.size() > 0) {
                for (Map<String, Object> resultMap : uiTestConfig) {
                    mapPost.put("testConfig", resultMap);
                    mapPost.put("testType", "2");
                    //调用测试接口
                    Map<String, Object> paramMap = new HashMap<String, Object>() {{
                        putAll(mapPost);
                    }};
                    threadPool.submit(new StructureServiceImpl.MyRun(paramMap));
                }
            }

        } else {//直接完成
            log.info("直接完成");
            tblSystemJenkinsJobRun.setBuildStatus(Integer.parseInt(status));
            //查询此jobrun的模块jobrun

            List<Map<String,Object>>  moduleJenkinsJobRuns= tblSystemModuleJenkinsJobRunMapper.selectModuleJobRunByjobRunId(Long.parseLong(jobRunId));
            for(Map<String,Object> moduleJobRun:moduleJenkinsJobRuns){
                long id=Long.parseLong(moduleJobRun.get("ID").toString());
                TblSystemModuleJenkinsJobRun tblSystemModuleJenkinsJobRun= tblSystemModuleJenkinsJobRunMapper.selectById(id);
                tblSystemModuleJenkinsJobRun.setBuildStatus(Integer.parseInt(status));
                tblSystemModuleJenkinsJobRunMapper.updateById(tblSystemModuleJenkinsJobRun);

            }
            sendMessage(tblSystemJenkinsJobRun, autoMap, "2");
        }
        tblSystemJenkinsJobRun.setBuildLogs(resultLog);
        tblSystemJenkinsJobRunMapper.updateById(tblSystemJenkinsJobRun);
        log.info("结束");

    }

    class MyRun implements Runnable {
        private Map<String, Object> data;

        public MyRun(Map<String, Object> data) {
            this.data = data;
        }

        @Override
        public void run() {
            automatTestService.toAutomatTest(this.data);
        }
    }


    @Override
    public List<Map<String, Object>> detailSprint(List<TblSprintInfo> sprintInfoList, long systemId) {
        List<Map<String, Object>> list = new ArrayList<>();

        Collections.sort(sprintInfoList, new Comparator<TblSprintInfo>() {

            @Override
            public int compare(TblSprintInfo o1, TblSprintInfo o2) {
                return o1.getSprintEndDate().compareTo(o2.getSprintEndDate());
            }
        });


        Date nowDate = new Date();
        TblSystemInfo tblSystemInfo = tblSystemInfoMapper.selectById(systemId);
        int i = 0;
        for (TblSprintInfo tblSprintInfo : sprintInfoList) {

            Map<String, Object> map = new HashMap<>();
            Date startDate = tblSprintInfo.getSprintStartDate();
            Date endDate = tblSprintInfo.getSprintEndDate();
            if (tblSystemInfo.getDevelopmentMode() != null && tblSystemInfo.getDevelopmentMode() == 1) {
                if (isEffectiveDate(nowDate, startDate, endDate)) {
                    if (i < 2) {
                        i = i + 1;
                        map.put("default", "true");
                    }

                }
            }
            map.put("id", tblSprintInfo.getId());
            map.put("sprintName", tblSprintInfo.getSprintName());
            list.add(map);

        }
        return list;
    }


    public boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        nowTime = date.getTime();

        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);


        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean judgeDeleteEnv(String termCode, String envType) {
        boolean flag = false;
        Map<String, Object> param = new HashMap<>();
        param.put("status", "2");
        param.put("termCode", termCode);
        List<Map<String, Object>> lists = tblSystemJenkinsJobRunMapper.getDeleteEnvName(param);
        for (Map<String, Object> map : lists) {

            String valueCode = map.get("VALUE_CODE").toString();
            if (valueCode.equals(envType)) {
                flag = true;

            }
        }
        return flag;
    }


    //处理result值
    private void detailTestResult(long jobRunId) {
        Map<String, Object> param = new HashMap<>();
        param.put("SYSTEM_JENKINS_JOB_RUN", jobRunId);
        List<TblSystemAutomaticTestResult> list = tblSystemAutomaticTestResultMapper.selectByMap(param);
        for (TblSystemAutomaticTestResult t : list) {
            t.setStatus(2);
            tblSystemAutomaticTestResultMapper.updateById(t);
        }
        log.info("更新测试结果状态为废弃已完成");
    }



    private void sendMessage(TblSystemJenkinsJobRun tblSystemJenkinsJobRun,Map<String,Object> backMap ,String jobType){
        log.info("进入发送消息++++");
        /* 改造项目和系统关系 liushan  */
        TblSystemInfo tblSystemInfo = getTblSystemInfoById(tblSystemJenkinsJobRun.getSystemId());
        String messageTitle="";
        String messageContent="";
        if(tblSystemInfo.getProjectIds() != null && !tblSystemInfo.getProjectIds().equals("") && backMap.get("userId")!=null && backMap.get("envName")!=null ) {
            String creatUserId = backMap.get("userId").toString();
            String envName = backMap.get("envName").toString();
            Map<String, Object> map = new HashMap<>();
            String stauts = "";

            if(jobType.equals("1")){
                messageTitle="["+tblSystemInfo.getSystemName()+"]" + "-" +  (tblSystemJenkinsJobRun.getBuildStatus() == 2 ? "构建成功" : "构建失败");
                messageContent=tblSystemInfo.getSystemName()+"-"+envName+"-"+ (tblSystemJenkinsJobRun.getBuildStatus() == 2 ? "构建成功" : "构建失败")+ "开始时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tblSystemJenkinsJobRun.getStartDate()) + "-结束时间" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tblSystemJenkinsJobRun.getEndDate());

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
            devManageToSystemInterface.insertMessage(JSON.toJSONString(map));
            //发送系统内消息
            if(backMap.get("reqFeatureqIdsEmail")!=null && !backMap.get("reqFeatureqIdsEmail").toString().equals("")){
                if(tblSystemJenkinsJobRun.getBuildStatus()==2){
                    messageTitle = "[" + tblSystemInfo.getSystemName() + "]-" + tblSystemInfo.getSystemName() + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tblSystemJenkinsJobRun.getEndDate()) + "投产成功提醒";
                }else {

                    messageTitle = "[" + tblSystemInfo.getSystemName() + "]-" + tblSystemInfo.getSystemName() + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tblSystemJenkinsJobRun.getEndDate()) + "投产失败提醒";
                }
                messageContent="开发任务";
                String reqFeatureqIds=backMap.get("reqFeatureqIdsEmail").toString();
                String windowName="";
                for (String id:reqFeatureqIds.split(",")){
                  TblRequirementFeature tblRequirementFeature=  tblRequirementFeatureMapper.selectById(Long.parseLong(id));
                  if(tblRequirementFeature.getCommissioningWindowId()!=null) {

                      TblCommissioningWindow tblCommissioningWindow = tblCommissioningWindowMapper.selectById(tblRequirementFeature.getCommissioningWindowId());
                      windowName=tblCommissioningWindow.getWindowName();
                  }
                  messageContent=messageContent+tblRequirementFeature.getFeatureCode()+"|"+tblRequirementFeature.getFeatureName()+",";

                }
                if(tblSystemJenkinsJobRun.getBuildStatus()==2){
                    messageContent=messageContent+"已在"+windowName+"投产窗口成功上线";

                }else{
                    messageContent=messageContent+"已在"+windowName+"投产窗口上线失败";
                }

                //发送邮件邮箱
                Map<String, Object> emWeMap = new HashMap<>();
                emWeMap.put("sendMethod","3");
                emWeMap.put("messageContent",messageContent);
                emWeMap.put("messageTitle",messageTitle);
                //查询此次 项目管理岗、开发管理岗、配置管理员
                Map<String,Object> param=new HashMap<>();
                param.put("roleName","项目管理岗");
                param.put("userIds", Arrays.asList(userIds.split(",")));
                param.put("projectIds", Arrays.asList(tblSystemInfo.getProjectIds().split(",")));
                List<String> pList= tblUserInfoMapper.findRoleByUserIds(param);
                param.put("roleName","需求管理岗");
                List<String> reqList= tblUserInfoMapper.findRoleByUserIds(param);
                pList.add(creatUserId);
                pList.addAll(reqList);
                HashSet h = new HashSet(pList);
                pList.clear();
                pList.addAll(h);
                emWeMap.put("messageReceiver",String.join(",",pList));
                devManageToSystemInterface.sendMessage(JSON.toJSONString(emWeMap));
            }





        }




    }


}
