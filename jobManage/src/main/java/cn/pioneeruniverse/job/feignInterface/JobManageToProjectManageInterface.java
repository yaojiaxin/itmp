package cn.pioneeruniverse.job.feignInterface;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.pioneeruniverse.job.feignFallback.JobManageToProjectManageFallback;

@FeignClient(value = "projectManage", fallbackFactory = JobManageToProjectManageFallback.class)
public interface JobManageToProjectManageInterface {

	@RequestMapping(value = "/requirementFeature/executeFeatureToHistoryJob", method = RequestMethod.POST)
	Map<String, Object> executeFeatureToHistoryJob(@RequestBody String parameterJson);

}
