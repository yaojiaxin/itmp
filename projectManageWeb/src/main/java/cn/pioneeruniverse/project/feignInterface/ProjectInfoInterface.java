package cn.pioneeruniverse.project.feignInterface;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.pioneeruniverse.project.feignFallback.ProjectInfoFallback;

@FeignClient(value="projectManage",fallbackFactory=ProjectInfoFallback.class)
public interface ProjectInfoInterface {

	//项目详情
	@RequestMapping(value="project/selectProjectById",method=RequestMethod.POST)
	Map<String, Object> selectProjectById(@RequestParam("id")Long id);
}
