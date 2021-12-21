package cn.pioneeruniverse.dev.jiraUtil;


import cn.pioneeruniverse.common.utils.DateUtil;
import cn.pioneeruniverse.dev.vo.JiraDevelopmentVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JiraWorkTaskUtil {

    public static final String JIRA_URL = "http://jira.ccic-net.com.cn";
    public static final String JIRA_USERNAME = "8000631482";
    public static final String JIRA_PASSWORD = "G3t269Mn";

    public final static Logger logger = LoggerFactory.getLogger(JiraWorkTaskUtil.class);

    /**
     *  自定义url语句
     * @param url
     * @return
     */
    public static Object getCustomUrl(String url){
        try {
            HttpResponse<JsonNode> response = Unirest.get(url)
                .basicAuth(JIRA_USERNAME, JIRA_PASSWORD)
                .header("Accept", "application/json")
                .asJson();
            return  response.getBody();
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * 获取仪表板
     * @param url
     * @return
     */
    public static String getDashboard(String url){
        try {
            HttpResponse<JsonNode> response = Unirest.get(url+"/rest/api/2/dashboard")
                .basicAuth(JIRA_USERNAME, JIRA_PASSWORD)
                .header("Accept", "application/json")
                .asJson();
            return  response.getBody().toString();
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }

    /**
     *  取得问题
     * @param url
     * @param issueIdOrKey
     * @return
     */
    public static String getIssue(String url,String issueIdOrKey){
        try {
            HttpResponse<JsonNode> response = Unirest.get(url+issueIdOrKey)   //  /rest/api/2/issue/{issueIdOrKey}
                    .basicAuth(JIRA_USERNAME, JIRA_PASSWORD)
                    .header("Accept", "application/json")
                    .asJson();
            return  response.getBody().toString();
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }

    /**
     * 获取附件文件
     * @param id
     * @return
     */
    public static String getAccessory(String id){
        try {
            HttpResponse<JsonNode> response = Unirest.get(JIRA_URL+"/rest/api/2/attachment/"+id)
                .basicAuth(JIRA_USERNAME, JIRA_PASSWORD)
                .header("Accept", "application/json")
                .asJson();
            JSONObject accessoryJSON = JSON.parseObject(response.getBody().toString());
            return  accessoryJSON.get("content").toString();
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }

    /**
     *  根据问题url获取附件
     * @param fields
     */
    public static Map[] getAll(String fields){
        JSONObject jsonObject = JSON.parseObject(fields);
        Object field = jsonObject.get("attachment");
        JSONArray objects = JSON.parseArray(field.toString());
        Map[] maps = new Map[objects.size()];
        for (int i =0;i<objects.size();i++){
            Map<String,Object> map = new HashMap<>();
            String id = objects.getJSONObject(i).get("id").toString();
            String fileName = objects.getJSONObject(i).get("filename").toString();
            if (id != null ){
                String accessory = JiraWorkTaskUtil.getAccessory(id);
                logger.info("id:"+id+"的附件content为："+accessory);
                map.put("fileName",fileName);
                map.put("accessory",accessory);
                maps[i] = map;
            }
        }
        return maps;
    }

    public static JiraDevelopmentVO jiraDemo(String id){
        Object customUrl = JiraWorkTaskUtil.getCustomUrl(JIRA_URL + "/rest/api/2/issue/"+ id);//获得问题
        logger.info("DataSource："+customUrl);
        Object fields = JSON.parseObject(customUrl.toString()).get("fields");
        JSONObject jsonObject = JSON.parseObject(fields.toString());

        JiraDevelopmentVO jiraDevelopmentVO = new JiraDevelopmentVO();
        jiraDevelopmentVO.setFeatureCode("jira"+id);
        //任务名称
        if (jsonObject.get("summary") != null){
            String summary = jsonObject.get("summary").toString();
            jiraDevelopmentVO.setFeatureName(summary);
        }
        //任务描述
        if (jsonObject.get("description") != null){
            String description = jsonObject.get("description").toString();
            jiraDevelopmentVO.setFeatureOverview(description);
        }
        //任务优先级
        if (jsonObject.get("priority") != null){
            JSONObject priorityJson = JSON.parseObject(jsonObject.get("priority").toString());
            String priority = priorityJson.get("id").toString();
            jiraDevelopmentVO.setRequirementFeaturePriority(Integer.valueOf(priority));
        }
        //systemReqID
        if (jsonObject.get("customfield_10326") != null){
            String systemReqID = jsonObject.get("customfield_10326").toString();
            jiraDevelopmentVO.setSystemReqID(systemReqID);
        }
        //经办人
        if(jsonObject.get("assignee") != null){
            JSONObject userJson = JSON.parseObject(jsonObject.get("assignee").toString());
            String userCode = userJson.get("key").toString();
            jiraDevelopmentVO.setUserCode(userCode);
        }
        //模块
        if (jsonObject.get("customfield_10329") != null){
            JSONObject moduleJson = JSON.parseObject(jsonObject.get("customfield_10329").toString());
            String moduleName = moduleJson.get("value").toString();
            jiraDevelopmentVO.setModuleName(moduleName);
            if(moduleJson.get("child") != null){
                Object child = moduleJson.get("child");
                JSONObject childJson = JSON.parseObject(child.toString());
                String moduleName1 = childJson.get("value").toString();
                jiraDevelopmentVO.setModuleName1(moduleName1);
            }
        }
        //项目小组
        if(jsonObject.get("customfield_10328") != null){
            JSONObject projectGroupId = JSON.parseObject(jsonObject.get("customfield_10328").toString());
            String projectGroupIdName = projectGroupId.get("value").toString();
            jiraDevelopmentVO.setProjectGroupName(projectGroupIdName);
        }
        //预计开始日期
        if (jsonObject.get("customfield_10324") != null){
            String expectStartedDate = jsonObject.get("customfield_10324").toString();
            jiraDevelopmentVO.setPlanStartDate(DateUtil.getDate(expectStartedDate,DateUtil.format));
        }
        //预计结束日期
        if (jsonObject.get("customfield_10323") != null){
            String expectFinishDate = jsonObject.get("customfield_10323").toString();
            jiraDevelopmentVO.setPlanEndDate(DateUtil.getDate(expectFinishDate,DateUtil.format));
        }
        //修复的发布版本
        if(jsonObject.get("fixVersions") != null){
            JSONArray fixVersionsArray = JSON.parseArray(jsonObject.get("fixVersions").toString());
            if(fixVersionsArray != null&&fixVersionsArray.size()>0 ){
                String fixVersions = fixVersionsArray.getJSONObject(0).get("name").toString();
                jiraDevelopmentVO.setRepairSystemVersioName(fixVersions);
            }
        }
        //工作量
        if(jsonObject.get("timetracking") != null){
            JSONObject timetrac = JSON.parseObject(jsonObject.get("timetracking").toString());
            String estimateWorkload = timetrac.get("originalEstimateSeconds").toString();       //预计工作量
            String estimateRemainWorkload = timetrac.get("remainingEstimateSeconds").toString(); //剩余工作量
            jiraDevelopmentVO.setEstimateWorkload(getEstimate(estimateWorkload));
            jiraDevelopmentVO.setEstimateRemainWorkload(getEstimate(estimateRemainWorkload));
        }
        //附件
        Map[] maps = JiraWorkTaskUtil.getAll(fields.toString());  //获取附件
        jiraDevelopmentVO.setMaps(maps);
        return jiraDevelopmentVO;
    }

    private static Double getEstimate(String estimateString){
        Integer dt = 8 * 60 * 60;
        Integer estimate = Integer.valueOf(estimateString);
        double day = new BigDecimal((float)estimate/dt).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
        return day;
    }
    /*
    public static void main(String[] args){
        Object customUrl = JiraWorkTaskUtil.getCustomUrl(JIRA_URL + "/rest/api/2/issue/29257");//获得问题
        logger.info("DataSource："+customUrl);
        Object fields = JSON.parseObject(customUrl.toString()).get("fields");
        JSONObject jsonObject = JSON.parseObject(fields.toString());

        if (jsonObject.get("customfield_10329") != null){
            JSONObject moduleJson = JSON.parseObject(jsonObject.get("customfield_10329").toString());
            String moduleName = moduleJson.get("value").toString();
            Object child = moduleJson.get("child");
            JSONObject childJson = JSON.parseObject(child.toString());
            String moduleName1 = childJson.get("value").toString();
            System.out.println(moduleName);
            System.out.println(moduleName1);
        }
        String [] version = "pkg/1.3.1B01".split("/");
        for(String s:version){
            System.out.println(s);
        }
    }*/

}
