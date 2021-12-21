package cn.pioneeruniverse.dev.feignInterface;

import cn.pioneeruniverse.dev.feignFallback.TestManageWebToSystemFallBack;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 **  类说明 
* @author:tingting
* @version:2019年3月8日 下午3:22:21
 */
@FeignClient(value = "system", fallbackFactory = TestManageWebToSystemFallBack.class)
public interface TestManageWebToSystemInterface {
    
    @RequestMapping(value = "menu/getMenuByCode", method = RequestMethod.POST)
    Map<String, Object> getMenuByCode(@RequestParam("menuButtonCode") String menuButtonCode);


}
