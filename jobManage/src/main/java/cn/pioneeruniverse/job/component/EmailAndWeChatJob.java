package cn.pioneeruniverse.job.component;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.pioneeruniverse.job.feignInterface.JobManageToSystemInterface;

/**
 * @DisallowConcurrentExecution 单例不并发
 * @author zhoudu
 *
 */
@Component
@DisallowConcurrentExecution
public class EmailAndWeChatJob implements Job {

	@Autowired
	private JobManageToSystemInterface systemInterface;

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		systemInterface.sendMessageJob();
	}

}
