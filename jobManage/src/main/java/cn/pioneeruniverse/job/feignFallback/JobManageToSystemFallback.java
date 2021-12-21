package cn.pioneeruniverse.job.feignFallback;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cn.pioneeruniverse.job.feignInterface.JobManageToSystemInterface;
import feign.hystrix.FallbackFactory;

@Component
public class JobManageToSystemFallback implements FallbackFactory<JobManageToSystemInterface> {

	private static final Logger logger = LoggerFactory.getLogger(JobManageToSystemFallback.class);

	@Override
	public JobManageToSystemInterface create(Throwable throwable) {
		return new JobManageToSystemInterface() {
			@Override
			public Map<String, String> sendMessageJob() {
				logger.error(throwable.getMessage(), throwable.getCause());
				return null;
			}

		};
	}
}
