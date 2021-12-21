package cn.pioneeruniverse.dev.feignInterface;

import cn.pioneeruniverse.dev.feignFallback.TestManageWebToDevManageFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 14:50 2019/9/24
 * @Modified By:
 */
@FeignClient(value = "devManage", fallbackFactory = TestManageWebToDevManageFallback.class)
public interface TestManageWebToDevManageInterface {

    @RequestMapping(value = "systemVersion/getSystemVersionsBySystemId", method = RequestMethod.POST)
    List<Map<String, Object>> getSystemVersionsBySystemId(@RequestParam("systemId") Long systemId);

}
