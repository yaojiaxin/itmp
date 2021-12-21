package cn.pioneeruniverse.dev.feignInterface;

import cn.pioneeruniverse.dev.feignFallback.TestManageToSystemFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "system", fallbackFactory = TestManageToSystemFallback.class)
public interface TestManageToSystemInterface {

    @RequestMapping(value = "/fieldTemplate/findFieldByTableName", method = RequestMethod.POST)
    Map<String,Object> findFieldByTableName(@RequestParam("tableName") String tableName);

    @RequestMapping(value = "/message/insertMessage", method = RequestMethod.POST)
    Map<String, Object> insertMessage( @RequestParam("messageJson") String messageJson);
}
