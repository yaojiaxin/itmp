package cn.pioneeruniverse.project.feignInterface;

import cn.pioneeruniverse.project.feignFallback.TestTaskFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.Map;

@FeignClient(value = "testManage", fallbackFactory = TestTaskFallback.class)
public interface TestTaskInterface {

    @RequestMapping(value = "/testtask/cancelStatus", method = RequestMethod.POST)
    Map<String,Object> changeCancelStatus(@RequestParam("requirementId") Long requirementId);

    @RequestMapping(value = "/testtask/cancelStatusReqFeature", method = RequestMethod.POST)
    Map<String,Object> cancelStatusReqFeature(@RequestParam("reqFeatureId") Long reqFeatureId);
}
