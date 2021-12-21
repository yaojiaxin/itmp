package cn.pioneeruniverse.job.component;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Component
public class Myjob implements Job{
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		System.out.println("execute my job");
	}


}
