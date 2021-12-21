package cn.pioneeruniverse.dev.controller;

import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.entity.TestFirstResultInfo;
import cn.pioneeruniverse.dev.service.deploy.IDeployService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "automatTest")
public class AutomatTestController {

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private IDeployService iDeployService;

    private final static Logger log = LoggerFactory.getLogger(AutomatTestController.class);
    @RequestMapping(value = "automatedTestingResult", method = RequestMethod.POST)
    public void testResult(@RequestBody String publishResult,HttpServletResponse response) {
        Map<String, Object> head = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        log.info("自动化测试平台返回结果" + publishResult);
        try {
            if (!StringUtils.isBlank(publishResult)) {
                Map<String, Object> data = JSON
                        .parseObject(JSON.parseObject(publishResult).get("requestBody").toString());

                log.info("测试结果报文--------->>>>>>"+JSON.toJSONString(data,true));
                Map<String, Object> antoMap = putSecondMap(data);
                log.info("回调报文--------->>>>>>"+JSON.toJSONString(antoMap,true));
                iDeployService.callBackAutoTest(antoMap);
                removeRedis(antoMap);

                map.put("consumerSeqNo", "itmgr");
                map.put("status", 0);
                map.put("seqNo", "");
                map.put("providerSeqNo", "");
                map.put("esbCode", "");
                map.put("esbMessage", "");
                map.put("appCode", "0");
                map.put("appMessage", "");
                head.put("responseHead", map);

                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(head));
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("自动化测试平台返回信息处理失败" + ":" + e.getMessage(), e);
        }
    }

    public Map<String, Object> putSecondMap(Map<String, Object> resultMap) {
        TestFirstResultInfo testFirstResultInfo=JSON.parseObject(JSON.toJSONString(resultMap), TestFirstResultInfo.class);
        Map<String, Object> map = new HashMap<>();
        if(testFirstResultInfo.getTestRequestNumber()!=null && !testFirstResultInfo.getTestRequestNumber().equals("")){
            map=(Map<String, Object>)redisUtils.get(testFirstResultInfo.getTestRequestNumber());
            map.put("testType",testFirstResultInfo.getTestType());
            map.put("testRequestNumber",testFirstResultInfo.getTestRequestNumber());
            map.put("successNumber",testFirstResultInfo.getSuccessNumber());
            map.put("failedNumber",testFirstResultInfo.getFailedNumber());
            map.put("testResult",testFirstResultInfo.getTestResult());
            map.put("testResultDetailUrl",testFirstResultInfo.getTestResultDetailUrl());
        }else{
            TestFirstResultInfo t=testFirstResultInfo.getSubSystemInfo().get(0);
            map=(Map<String, Object>)redisUtils.get(t.getSubTestRequestNumber());
            Map<String, Object> testConfig = JSON.parseObject(JSON.toJSONString(map.get("testConfig")));
            List<Map<String, Object>> listMaps = (List<Map<String, Object>>)testConfig.get("subSystemInfo");

            for (Map<String, Object> listMap:listMaps){
                if(t.getSubSystemCode().equals(listMap.get("subSystemCode").toString())){
                    listMap.put("subTestRequestNumber",t.getSubTestRequestNumber());
                    listMap.put("subSuccessNumber",t.getSubSuccessNumber());
                    listMap.put("subFailedNumber",t.getSubFailedNumber());
                    listMap.put("subTestResult",t.getSubTestResult());
                    listMap.put("subTestResultDetailUrl",t.getSubTestResultDetailUrl());
                }
            }
            testConfig.put("subSystemInfo",listMaps);
            map.put("testConfig",testConfig);
            map.put("testType",testFirstResultInfo.getTestType());
            map.put("testRequestNumber",testFirstResultInfo.getTestRequestNumber());
            map.put("successNumber",testFirstResultInfo.getSuccessNumber());
            map.put("failedNumber",testFirstResultInfo.getFailedNumber());
            map.put("testResult",testFirstResultInfo.getTestResult());
            map.put("testResultDetailUrl",testFirstResultInfo.getTestResultDetailUrl());
        }
        return map;
    }

    public void removeRedis(Map<String, Object> map) {
        if(!map.get("testRequestNumber").toString().equals("")){
            redisUtils.remove(map.get("testRequestNumber").toString());
        }else{
            Map<String, Object> testConfig = JSON.parseObject(map.get("testConfig").toString());
            List<Map<String, Object>> listMaps = (List<Map<String, Object>>)testConfig.get("subSystemInfo");
            for (Map<String, Object> listMap:listMaps){
                if(listMap.get("subTestRequestNumber")!=null&&!listMap.get("subTestRequestNumber").toString().equals("")){
                    redisUtils.remove(listMap.get("subTestRequestNumber").toString());
                }
            }

        }
    }
    //获取redist信息
    /*@RequestMapping(value = "getRedisConfig", method = RequestMethod.GET)
    public HashMap<Object,Object> getRedisConfig(){
        Set<String> keys = redisUtils.getKeys("");

        HashMap<Object,Object> map = new HashMap<>();
        int i=0;
        for (String key:keys){
            //redisUtils.remove(key);
            System.out.println(i+">>"+redisUtils.getType(key)+":      "+key);
            Object value = redisUtils.get(key);
            map.put(key,value);
            i++;
        }
        return map;
    }*/
}
