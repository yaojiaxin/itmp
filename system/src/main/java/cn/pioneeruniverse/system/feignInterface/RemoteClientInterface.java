package cn.pioneeruniverse.system.feignInterface;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 16:16 2019/6/28
 * @Modified By:
 */
@FeignClient(name = "remoteSApi", url = "http://10.1.12.37:82/")
public interface RemoteClientInterface {
    @RequestMapping(value = "/system/user/findUserById", method = RequestMethod.POST)
    Map<String, Object> findUserById(@RequestParam("userId") Long userId);

    @RequestMapping(value = "/api/v4/projects/6", method = RequestMethod.GET)
    Map<String, Object> getProject(@RequestParam("private_token") String private_token);
}
