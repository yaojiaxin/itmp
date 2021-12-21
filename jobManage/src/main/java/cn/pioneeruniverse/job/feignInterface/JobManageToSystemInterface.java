package cn.pioneeruniverse.job.feignInterface;

import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.pioneeruniverse.job.feignFallback.JobManageToSystemFallback;

@FeignClient(value = "system", fallbackFactory = JobManageToSystemFallback.class)
public interface JobManageToSystemInterface {

	@RequestMapping(value = "/message/sendMessageJob", method = RequestMethod.POST)
	Map<String, String> sendMessageJob();

}
