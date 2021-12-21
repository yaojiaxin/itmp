package cn.pioneeruniverse.dev.feignInterface;


import cn.pioneeruniverse.dev.feignFallback.TestManageToDevManageFallback;

import org.apache.ibatis.annotations.Param;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: liushan
 * @Description:
 * @Date: Created in 15:52 2019/1/17
 * @Modified By:
 */
@FeignClient(value = "devManage", fallbackFactory = TestManageToDevManageFallback.class)
public interface TestManageToDevManageInterface {

    @RequestMapping(value = "defect/syncDefect", method = RequestMethod.POST)
    Map<String, Object> syncDefect(@RequestBody String objectJson) throws Exception;

    @RequestMapping(value = "defect/syncDefectAtt", method = RequestMethod.POST)
    Map<String, Object> syncDefectAtt(@RequestBody String objectJson) throws Exception;
    
   /* @RequestMapping(value="systeminfo/findSystemIdByUserId",method = RequestMethod.POST)
    List<Long> findSystemIdByUserId(@RequestParam Long id);*/

    @RequestMapping(value = "devtask/synReqFeatureDeployStatus", method = RequestMethod.POST)
    Map<String, Object> synReqFeatureDeployStatus(@RequestParam("requirementId") Long requirementId, @RequestParam("systemId") Long systemId, @RequestParam("deployStatus") String deployStatus, @RequestParam("loginfo") String loginfo);

    @RequestMapping(value = "devtask/updateReqFeatureTimeTrace", method = RequestMethod.POST)
    Map<String, Object> updateReqFeatureTimeTrace(@RequestParam("jsonString") String jsonString);

    @RequestMapping(value = "defect/syncDefectWithFiles", method = RequestMethod.POST)
    Map<String, Object> syncDefectWithFiles(@RequestBody String json);

    @RequestMapping(value = "devtask/synReqFeaturewindow", method = RequestMethod.POST)
    Map<String, Object> synReqFeaturewindow(@RequestParam("requirementId") Long requirementId, @RequestParam("systemId") Long systemId, @RequestParam("commissioningWindowId") Long commissioningWindowId, @RequestParam("loginfo") String loginfo, @RequestParam("beforeName") String beforeName, @RequestParam("afterName") String afterName);

    @RequestMapping(value = "devtask/synReqFeatureDept", method = RequestMethod.POST)
    Map<String, Object> synReqFeatureDept(@RequestParam("requirementId") Long requirementId, @RequestParam("systemId") Long systemId, @RequestParam("deptId") Long deptId, @RequestParam("loginfo") String loginfo, @RequestParam("deptBeforeName") String deptBeforeName,
                                          @RequestParam("deptAfterName") String deptAfterName);

    @RequestMapping(value = "devtask/getDevTaskBySystemAndRequirement", method = RequestMethod.POST)
    Map<String, Object> getDevTaskBySystemAndRequirement(@RequestParam("systemId") Long systemId, @RequestParam("requirementId") Long requirementId);

    @RequestMapping(value = "/systemVersion/getSystemVersionBySystemVersionId", method = RequestMethod.POST)
    Map<String, Object> getSystemVersionBySystemVersionId(@RequestParam("systemVersionId") Long systemVersionId);
}
