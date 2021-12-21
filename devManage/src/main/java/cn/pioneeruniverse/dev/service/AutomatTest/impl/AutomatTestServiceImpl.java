package cn.pioneeruniverse.dev.service.AutomatTest.impl;

import cn.pioneeruniverse.common.databus.DataBusRequestHead;
import cn.pioneeruniverse.common.utils.HttpUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.dao.mybatis.TblSystemAutomaticTestResultMapper;
import cn.pioneeruniverse.dev.entity.TblSystemAutomaticTestResult;
import cn.pioneeruniverse.dev.entity.TestFirstResultInfo;
import cn.pioneeruniverse.dev.service.AutomatTest.AutomatTestService;
import cn.pioneeruniverse.dev.service.deploy.IDeployService;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
  * 向esb发送post请求工具类
  * @author tang
  *
  */
@Service
public class AutomatTestServiceImpl implements AutomatTestService {

    @Autowired
    private IDeployService iDeployService;

    @Autowired
    private TblSystemAutomaticTestResultMapper tblSystemAutomaticTestResultMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RestTemplate restTemplate;

    @Value("${esb.automat.test.url}")
    private String automatTestUrl;

    @Value("${esb.automat.uitest.url}")
    private String automatUiTestUrl;

    @Value("${system.integration.open}")
    private  Boolean integration;

    private final static Logger log = LoggerFactory.getLogger(AutomatTestServiceImpl.class);

    @Override
    public void toAutomatTest(Map<String, Object> map) {
        if(integration!=true){
            return;
        }
        log.info("请求参数--------->>>>>>"+JSON.toJSONString(map,true));
        //设置头信息
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Content-Type", "application/json;charset=UTF-8");
        headers.set("Accept", "application/json");

        //封装请求参数
        Map<String, Object> mapAll = new LinkedHashMap<>();
        String testType=map.get("testType").toString();

        Map<String, Object> requestBody = (Map<String, Object>)map.get("testConfig");
        mapAll.put("requestHead",DataBusRequestHead.getRequestHead());

        if(testType.equals("1")) {
            DataBusRequestHead.setProviderID1("apiautotest");
        }else{
            DataBusRequestHead.setProviderID1("uiautotest");
        }
        mapAll.put("requestBody",requestBody);
        //查看请求报文
        log.info("请求报文--------->>>>>>"+JSON.toJSONString(mapAll,true));
        //发送请求
        HttpEntity<Map<String, Object>> sendParam = new HttpEntity<>(mapAll, headers);

        Map<String, Object> mapResult=new HashMap<>();
        log.info("测试类型="+testType);
        if(testType.equals("1")) {//api 自动化测试
          mapResult = send(sendParam);
        }else{//ui 自动化测试
          mapResult = sendUI(sendParam);
           // String testText=sendUIhttp(JSON.toJSONString(mapAll,true));
        }


        map = putFirstMap(map,mapResult);

        //查看回调报文
        log.info("回调报文--------->>>>>>"+JSON.toJSONString(map,true));
        iDeployService.detailTestRequestNumber(map);
    }

    public Map<String, Object> send(HttpEntity<Map<String, Object>> he) {
        Map<String, Object> map = new HashMap<>();
        try{
            //发送请求
            ResponseEntity<String> result = restTemplate.exchange
                    (automatTestUrl, HttpMethod.POST, he,String.class);
            if(result.getStatusCode().is2xxSuccessful()){
                log.info("响应报文--------->>>>>>"+result.getBody());
                map = JSON.parseObject(JSON.parseObject(result.getBody()).get("responseBody").toString());
            }else{
                log.info("调用失败，状态码："+result.getStatusCodeValue()+"。");
            }
        }catch (Exception ex){
            ex.getMessage();
        }
        return map;
    }



    public Map<String, Object> sendUI(HttpEntity<Map<String, Object>> he) {
        Map<String, Object> map = new HashMap<>();
        try{
            //发送请求
            ResponseEntity<String> result = restTemplate.exchange
                    (automatUiTestUrl, HttpMethod.POST, he,String.class);
            if(result.getStatusCode().is2xxSuccessful()){
                log.info("UI响应报文--------->>>>>>"+result.getBody());
                map = JSON.parseObject(JSON.parseObject(result.getBody()).get("responseBody").toString());
            }else{
                log.info("UI调用失败，状态码："+result.getStatusCodeValue()+"。");
            }
        }catch (Exception ex){
            log.error("UI message" + ":" + ex.getMessage(), ex);

        }
        return map;
    }


    public Map<String, Object> putFirstMap(Map<String, Object> map,Map<String, Object> resultMap) {
        if(resultMap!=null) {
            Map<String, Object> testConfig = JSON.parseObject(JSON.toJSONString(map.get("testConfig")));
            List<Map<String, Object>> listMaps = (List<Map<String, Object>>) testConfig.get("subSystemInfo");
            TestFirstResultInfo testFirstResultInfo = JSON.parseObject(JSON.toJSONString(resultMap), TestFirstResultInfo.class);
            if (testFirstResultInfo.getTestRequestNumber()!=null && !testFirstResultInfo.getTestRequestNumber().equals("")) {
                redisUtils.set(testFirstResultInfo.getTestRequestNumber(), map);
            } else {
                for (TestFirstResultInfo t : testFirstResultInfo.getSubSystemInfo()) {
                    for (Map<String, Object> listMap : listMaps) {
                        if (t.getSystemCode().equals(listMap.get("subSystemCode").toString())) {
                            listMap.put("subTestRequestNumber", t.getTestRequestNumber());
                            redisUtils.set(t.getTestRequestNumber(), map);
                        }
                    }
                }
            }
            testConfig.put("subSystemInfo", listMaps);
            map.put("testConfig", testConfig);
            map.put("testRequestNumber", testFirstResultInfo.getTestRequestNumber());
        }
        return map;
    }



}
