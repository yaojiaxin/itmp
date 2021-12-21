package cn.pioneeruniverse.project.feignInterface;

import cn.pioneeruniverse.project.feignFallback.DevTaskFallback;
import cn.pioneeruniverse.project.feignFallback.ProjectToSystemFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "system", fallbackFactory = ProjectToSystemFallback.class)
public interface ProjectToSystemInterface {

    @RequestMapping(value = "/message/insertMessage", method = RequestMethod.POST)
    Map<String, String> insertMessage(@RequestParam("messageJson") String messageJson);

    @RequestMapping(value = "/fieldTemplate/findFieldByTableName", method = RequestMethod.POST)
    Map<String, Object> findFieldByTableName(@RequestParam("tableName") String tableName);

    @RequestMapping(value = "/message/sendMessage", method = RequestMethod.POST)
    Map<String, String> sendMessage(@RequestParam("messageJson") String messageJson);

    @RequestMapping(value = "/user/getUserInfoByUserIds", method = RequestMethod.POST)
    List<Map<String, Object>> getUserInfoByUserIds(@RequestParam("userIds") List<Long> userIds);


}
