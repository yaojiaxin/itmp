package cn.pioneeruniverse.project.feignInterface;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.pioneeruniverse.project.feignFallback.OamProjectInfoFallback;

@FeignClient(value="projectManage",fallbackFactory=OamProjectInfoFallback.class)
public interface OamProjectInfoInterface {

	
	//运维项目详情
	@RequestMapping(value="oamproject/selectProjectAndUserById",method=RequestMethod.GET)
	Map<String, Object> selectProjectAndUserById(@RequestParam("id")Long id,@RequestParam("type")String type);
}
