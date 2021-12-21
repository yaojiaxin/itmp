package cn.pioneeruniverse.job.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;

import cn.pioneeruniverse.job.model.Result;
import cn.pioneeruniverse.job.model.TaskInfo;
import cn.pioneeruniverse.job.service.ITaskService;

@RestController
public class TaskController {

	@Autowired
	private ITaskService iTaskService;
	
	private static final Logger log = LoggerFactory.getLogger(TaskController.class);
	
	@GetMapping("getJob")
	public Result getJob() {
		try {
			List<TaskInfo> list = iTaskService.queryJobList();
			return Result.success(list);
		}catch(Exception e) {
			log.error("查询任务失败：{}",e);
			return Result.fail();
		}
	}
	
	/**
	 * 可通过页面执行：
	 * http://localhost:8080/job/addJob?taskJson={"jobName":"putFeatureToHistory","className":"cn.pioneeruniverse.job.component.RequirementFeatureToHistoryJob","cronExpression":"0 45 9 * * ?","jobDescription":"Test222"}
	 * http://localhost:8080/job/startJob?jobName=putFeatureToHistory
	 * @param taskJson
	 * @return
	 */
	@GetMapping("addJob")
	public Result addJob(String taskJson) {
		try {
			TaskInfo task = JSON.parseObject(taskJson, TaskInfo.class);
			iTaskService.addJob(task);
			return Result.success();
		}catch(Exception e) {
			log.error("添加任务失败：{}",e);
			return Result.fail();
		}
	}
	
	@GetMapping("simpleTriggerJob")
	public Result simpleTriggerJob(String taskJson) {
		try {
			TaskInfo task = JSON.parseObject(taskJson, TaskInfo.class);
			iTaskService.setSimpleTriggerJob(task);
			return Result.success();
		}catch(Exception e) {
			log.error("触发任务失败：{}",e);
			return Result.fail();
		}
	}
	@GetMapping("editJob")
	public Result editJob(String taskJson) {
		try {
			TaskInfo task = JSON.parseObject(taskJson, TaskInfo.class);
			iTaskService.editJob(task);
			return Result.success();
		}catch(Exception e) {
			log.error("编辑任务失败：{}",e);
			return Result.fail();
		}
	}
	
	
	@GetMapping("startJob")
	public Result startJob(String taskJson) {
		try {
			TaskInfo task = JSON.parseObject(taskJson, TaskInfo.class);
			iTaskService.startJob(task);
			return Result.success();
		}catch(Exception e) {
			log.error("开始任务失败：{}",e);
			return Result.fail();
		}
	}
	
	@GetMapping("deleteJob")
	public Result deleteJob(String taskJson) {
		try {
			TaskInfo task = JSON.parseObject(taskJson, TaskInfo.class);
			iTaskService.deleteJob(task);
			return Result.success();
		}catch(Exception e) {
			log.error("添加任务失败：{}",e);
			return Result.fail();
		}
	}
	
	@GetMapping("checkExists")
	public Result checkExists(String taskJson) {
		try {
			TaskInfo task = JSON.parseObject(taskJson, TaskInfo.class);
			iTaskService.checkExists(task);
			return Result.success();
		}catch(Exception e) {
			log.error("开始任务失败：{}",e);
			return Result.fail();
		}
	}
	
	@GetMapping("pauseJob")
	public Result pauseJob(String taskJson) {
		try {
			TaskInfo task = JSON.parseObject(taskJson, TaskInfo.class);
			iTaskService.pauseJob(task);
			return Result.success();
		}catch(Exception e) {
			log.error("删除任务失败：{}",e);
			return Result.fail();
		}
	}
	
	@GetMapping("resumeJob")
	public Result resumeJob(String taskJson) {
		try {
			TaskInfo task = JSON.parseObject(taskJson, TaskInfo.class);
			iTaskService.resumeJob(task);
			return Result.success();
		}catch(Exception e) {
			log.error("恢复任务失败：{}",e);
			return Result.fail();
		}
	}
	
	/*
	 * public static void main(String args[]) { 
	 * TaskInfo task = new TaskInfo();
	 * task.setCreateTime("2019-11-18 15:39:00");
	 * task.setCronExpression("0 0/5 0 * * ?");
	 *  task.setEndDate("2019-11-18");
	 * task.setJobDescription(""); 
	 * task.setJobGroup("group1");
	 * task.setJobName("cn.pioneerservice.job.component.Myjob");
	 * task.setMilliSeconds("100000"); 
	 * task.setRepeatCount("2");
	 * System.out.println(JSONObject.toJSONString(task));
	 * 
	 * }
	 */
	
}
