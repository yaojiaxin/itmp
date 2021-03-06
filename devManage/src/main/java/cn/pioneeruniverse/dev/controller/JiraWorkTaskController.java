package cn.pioneeruniverse.dev.controller;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.dev.jiraUtil.JiraWorkTaskUtil;
import cn.pioneeruniverse.dev.service.JiraWorkTask.IJiraWorkTaskService;
import cn.pioneeruniverse.dev.vo.JiraDevelopmentVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("jiraWorkTask")
public class JiraWorkTaskController extends BaseController {

    @Autowired
    private IJiraWorkTaskService jiraWorkTaskService;

    /**
     *  jira数据增加
     * @return
     */
    @RequestMapping("jiraWorkTaskMethod")
    public Map<String,Object> insertJiraWorkTask(HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        try{
            String customUrlTotal = JiraWorkTaskUtil.getCustomUrl(JiraWorkTaskUtil.JIRA_URL + "/rest/api/2/search?maxResults=1&startAt=0").toString();//获得所有问题
            logger.info("total:"+JSON.parseObject(customUrlTotal).get("total"));  //总数
            Integer total = Integer.valueOf(JSON.parseObject(customUrlTotal).get("total").toString());
            JiraDevelopmentVO jiraDevelopmentVO = JiraWorkTaskUtil.jiraDemo("29257");
            jiraDevelopmentVO.setSystemCode("JARASYN");
            jiraWorkTaskService.synJiraWorkTask(jiraDevelopmentVO,request);
            /*for(int j=0;j<total;j = j + 1000){
                String customUr = JiraWorkTaskUtil.getCustomUrl(JiraWorkTaskUtil.JIRA_URL + "/rest/api/2/search?maxResults=1000&startAt="+j).toString();//获得所有问题
                JSONArray issues = JSON.parseArray(JSON.parseObject(customUr).get("issues").toString());
                for (int i =0 ;i<issues.size();i++){
                    String id = issues.getJSONObject(i).get("id").toString();   //当前问题id
                    String fields = issues.getJSONObject(i).get("fields").toString();//获取当前问题
                    JSONObject jsonObject = JSON.parseObject(JSON.parseObject(fields).get("issuetype").toString());
                    String type = jsonObject.get("name").toString();   //获取问题中的类型
                    if (type.equals("Development")){   //如果类型是Development则存储该数据
                        JiraDevelopmentVO jiraDevelopmentVO = JiraWorkTaskUtil.jiraDemo(id);
                        jiraDevelopmentVO.setSystemCode("JARASYN");
                        jiraWorkTaskService.synJiraWorkTask(jiraDevelopmentVO,request);
                    }
                }
            }*/
            map.put("status", Constants.ITMP_RETURN_SUCCESS);
        }catch (Exception e){
            return super.handleException(e, "获取数据失败！");
        }
        return map;
    }

}
