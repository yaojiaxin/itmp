package cn.pioneeruniverse.dev.feignInterface.defect;


import cn.pioneeruniverse.dev.feignFallback.defect.DefectFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * Description:
 * Author:liushan
 * Date: 2018/12/10 下午 5:17
 */
@FeignClient(value="testManage",fallbackFactory = DefectFallback.class)
public interface DefectInterface {
   /* @RequestMapping(value="defect/getAllSelectDefect",method=RequestMethod.POST)
    Map<String,Object> getAllSelect();*/

}
