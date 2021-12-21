package cn.pioneeruniverse.dev.feignInterface;


import cn.pioneeruniverse.dev.feignFallback.DevManageToTestManageFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: liushan
 * @Description:
 * @Date: Created in 15:52 2019/1/17
 * @Modified By:
 */
@FeignClient(value = "testManage", fallbackFactory = DevManageToTestManageFallback.class)
public interface DevManageToTestManageInterface {

    @RequestMapping(value = "defect/syncDefect", method = RequestMethod.POST)
    Map<String,Object> syncDefect(@RequestBody String objectJson);

    @RequestMapping(value = "defect/syncDefectAtt", method = RequestMethod.POST)
    Map<String,Object> syncDefectAtt(@RequestBody String objectJson);
    
    @RequestMapping(value ="testtask/synReqFeatureDeployStatus",method = RequestMethod.POST)
    Map<String, Object> synReqFeatureDeployStatus( @RequestParam("requirementId") Long requirementId,@RequestParam("systemId")
            Long systemId,@RequestParam("deployStatus") String deployStatus, @RequestParam("loginfo")String loginfo);

    @RequestMapping(value ="defect/insertDefectAttachement",method = RequestMethod.POST)
    Map<String,Object> insertDefectAttachement(@RequestBody String defectAttache);

    @RequestMapping(value ="testtask/synReqFeaturewindow",method = RequestMethod.POST)
    Map<String, Object> synReqFeaturewindow(@RequestParam("requirementId")Long requirementId, @RequestParam("systemId")Long systemId, @RequestParam("commissioningWindowId")Long commissioningWindowId, @RequestParam("loginfo") String loginfo, @RequestParam("beforeName")String beforeName, @RequestParam("afterName")String afterName);
    
    @RequestMapping(value="testtask/synReqFeatureDept",method = RequestMethod.POST)
    Map<String, Object> synReqFeatureDept(@RequestParam("requirementId")Long requirementId, @RequestParam("systemId")Long systemId, @RequestParam("deptId")Integer deptId, @RequestParam("loginfo")String loginfo, @RequestParam("deptBeforeName")String deptBeforeName,
			@RequestParam("deptAfterName")String deptAfterName);


    @RequestMapping(value ="testtask/detailEnvDate",method = RequestMethod.POST)
    Map<String,Object> detailEnvDate(@RequestParam("list")String list, @RequestParam("envName")String envName, @RequestParam("timestamp")String timestamp);

    @RequestMapping(value ="testtask/test1",method = RequestMethod.POST)
    Map<String,Object> test1( @RequestParam("envName")String envName);

}
