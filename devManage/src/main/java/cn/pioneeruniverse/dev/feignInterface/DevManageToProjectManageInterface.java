package cn.pioneeruniverse.dev.feignInterface;

import cn.pioneeruniverse.common.dto.SystemTreeVo;
import cn.pioneeruniverse.dev.feignFallback.DevManageToProjectManageFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@FeignClient(value = "projectManage", fallbackFactory = DevManageToProjectManageFallback.class)
public interface DevManageToProjectManageInterface {

    @RequestMapping(value = "/systemTree/insertFirstSystem", method = RequestMethod.POST)
    Map<String, Object> insertFirstSystem(@RequestBody SystemTreeVo systemTreeVo,
                                          @RequestParam("currentUserId") Long currentUserId);

    @RequestMapping(value = "/project/getProjectGroupByProjectGroupId", method = RequestMethod.POST)
    Map<String, Object> getProjectGroupByProjectGroupId(@RequestParam("projectGroupId") Long projectGroupId);

    @RequestMapping(value = "/systemTree/getAssetSystemTreeById", method = RequestMethod.POST)
    Map<String, Object> getAssetSystemTreeById(@RequestParam("id") Long id);
}
