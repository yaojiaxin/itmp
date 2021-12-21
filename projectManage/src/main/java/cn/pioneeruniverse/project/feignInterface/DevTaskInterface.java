package cn.pioneeruniverse.project.feignInterface;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.pioneeruniverse.project.feignFallback.DevTaskFallback;

import java.util.List;
import java.util.Map;

@FeignClient(value = "devManage", fallbackFactory = DevTaskFallback.class)
public interface DevTaskInterface {

    @RequestMapping(value = "/devtask/cancelStatus", method = RequestMethod.POST)
    Map<String, Object> changeCancelStatus(@RequestParam("requirementId") Long requirementId);

    @RequestMapping(value = "/devtask/cancelStatusReqFeature", method = RequestMethod.POST)
    Map<String, Object> cancelStatusReqFeature(@RequestParam("reqFeatureId") Long reqFeatureId);

    @RequestMapping(value = "/version/getCodeFilesByDevTaskId", method = RequestMethod.POST)
    Map<String, Object> getCodeFilesByDevTaskId(@RequestParam("devTaskId") Long devTaskId);

    @RequestMapping(value = "/systeminfo/getSystemNameById", method = RequestMethod.POST)
    String getSystemNameById(@RequestParam("systemId") Long systemId);

    @RequestMapping(value = "/version/getMyProjectSystems", method = RequestMethod.POST)
    List<Map<String, Object>> getMyProjectSystems(@RequestParam("token") String token);
}
