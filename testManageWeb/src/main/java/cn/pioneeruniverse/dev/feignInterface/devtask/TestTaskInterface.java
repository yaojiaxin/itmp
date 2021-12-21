package cn.pioneeruniverse.dev.feignInterface.devtask;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.pioneeruniverse.dev.feignFallback.testtask.TestTaskFallback;


/**
*  类说明 
* @author:tingting
* @version:2018年11月13日 下午3:35:10 
*/
@FeignClient(value="testManage",fallbackFactory=TestTaskFallback.class)
public interface TestTaskInterface {
	
	@RequestMapping(value="testtask/toAddData",method=RequestMethod.POST)
    Map<String, Object> toAddData();
	@RequestMapping(value="testtask/getOneTestTask3",method=RequestMethod.POST)
	Map<String, Object> getOneDevTask(@RequestParam("id") Long id);


}
