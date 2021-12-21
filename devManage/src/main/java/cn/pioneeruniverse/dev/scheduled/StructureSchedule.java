package cn.pioneeruniverse.dev.scheduled;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.entity.TblSystemJenkins;
import cn.pioneeruniverse.dev.entity.TblSystemScm;
import cn.pioneeruniverse.dev.entity.TblToolInfo;
import cn.pioneeruniverse.dev.notify.StructureNotify;
import cn.pioneeruniverse.dev.service.build.IJenkinsBuildService;
import cn.pioneeruniverse.dev.service.structure.IStructureService;

@Component
public class StructureSchedule {
	@Autowired
	private IStructureService iStructureService;
	@Autowired
	RedisUtils redisUtils;
	@Autowired
	private StructureNotify structureNotify;
	@Autowired
	private IJenkinsBuildService iJenkinsBuildService;
	private final static Logger log = LoggerFactory.getLogger(StructureSchedule.class);
	//private static final SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static ExecutorService threadPool;
	{
		threadPool = Executors.newCachedThreadPool();

	}

	// 手动构建
//    @Scheduled(fixedRate = 3800000)	
//	public void scheduledManualStructure() {
//		log.info("scheduled 处理自定义坏数据- print time every 1 hour :"+formate.format(new Date()));
//		// 获取所有手动构建时间超过1小时的id
//		Map<String, Object> paramMap = new HashMap<>();
//		paramMap.put("status", 1);
//		paramMap.put("build_status", 2);
//		paramMap.put("create_type", 2);
//		paramMap.put("job_type", 1);
//		List<TblSystemJenkins> jenkins = iStructureService.selectJenkinsByMap(paramMap);
//		if (jenkins.size() <= 0) {
//
//		} else {
//			for (TblSystemJenkins tblSystemJenkins : jenkins) {
//
//				iStructureService.detailErrorStructure(tblSystemJenkins,"2","1");
//
//			}
//		}
//
//	}
//    
// // 手动部署
//    @Scheduled(fixedRate = 3800000)	
//	public void scheduledDeployManualStructure() {
//		log.info("scheduled 处理自定义坏数据- print time every 1 hour :"+formate.format(new Date()));
//		// 获取所有手动构建时间超过1小时的id
//		Map<String, Object> paramMap = new HashMap<>();
//		paramMap.put("status", 1);
//		paramMap.put("build_status", 2);
//		paramMap.put("create_type", 2);
//		paramMap.put("job_type", 2);
//		List<TblSystemJenkins> jenkins = iStructureService.selectJenkinsByMap(paramMap);
//		if (jenkins.size() <= 0) {
//
//		} else {
//			for (TblSystemJenkins tblSystemJenkins : jenkins) {
//
//				iStructureService.detailErrorStructure(tblSystemJenkins,"2","1");
//
//			}
//		}
//
//	}
//	 //自动构建
//    @Scheduled(fixedRate = 3600000)	
//	public void scheduledAutoStructure(){
//		log.info("scheduled 处理自动坏数据 print time every 1 hour:{}"+formate.format(new Date()));
//		// 获取所有手动构建时间超过1小时的id
//		Map<String, Object> paramMap = new HashMap<>();
//		paramMap.put("status", 1);
//		paramMap.put("build_status", 2);
////		paramMap.put("create_type", 1);
////		paramMap.put("job_type", 1);
//		List<TblSystemScm> scms = iStructureService.selectScmByMap(paramMap);
//		
//		if (scms.size() <= 0) {
//
//		} else {
//			for (TblSystemScm scm : scms) {
//
//				iStructureService.detailAutoErrorStructure(scm,"1","1",null);
//
//			}
//		}
//	    
//	}
//    
//    //自动部署
//    @Scheduled(fixedRate = 3700000)	
//	public void scheduledAutoDeployStructure(){
//		log.info("scheduled 处理自动坏数据 print time every 1 hour:{}"+formate.format(new Date()));
//		// 获取所有手动构建时间超过1小时的id
//		Map<String, Object> paramMap = new HashMap<>();
//		paramMap.put("status", 1);
//		paramMap.put("deploy_status", 2);
////		paramMap.put("create_type", 1);
////		paramMap.put("job_type", 1);
//		List<TblSystemScm> scms = iStructureService.selectScmByMap(paramMap);
//		
//		if (scms.size() <= 0) {
//
//		} else {
//			for (TblSystemScm scm : scms) {
//
//				iStructureService.detailAutoErrorStructure(scm,"2","1",null);
//
//			}
//		}
//	    
//	}
//    
//    
    
    
    //轮询
    @Scheduled(fixedRate = 60000)	
	public void structPolling() throws Exception{
		log.info("构建轮询开始");
		List<Map<String, Object>> itmpMaps=(List<Map<String, Object>>) redisUtils.get("structCallback");	
		if(itmpMaps!=null && !itmpMaps.isEmpty()) {
			Iterator<Map<String, Object>> it=itmpMaps.iterator();
			while(it.hasNext()){
				Map<String, Object> map = it.next();
				String jobName=map.get("jobName").toString();				
				TblToolInfo tblToolInfo=iStructureService.geTblToolInfo(Long.parseLong(map.get("jenkinsToolId").toString())) ;
				TblSystemJenkins tblSystemJenkins=iStructureService.selectSystemJenkinsById(map.get("systemJenkinsId").toString());
				String jobNumber="";
				if(map.get("jobNumber")!=null){
					jobNumber=map.get("jobNumber").toString();
				}
				Boolean flag= iJenkinsBuildService.isJenkinsBuilding(tblToolInfo, tblSystemJenkins, jobName,Integer.parseInt(jobNumber));
				if(flag==true) {
					continue;				
				}else {
					List<Map<String, Object>> refreshMapsList=new ArrayList<>();
					refreshMapsList.addAll(itmpMaps);					
					refreshMapsList.remove(map);
					redisUtils.set("structCallback", refreshMapsList);
					detailStructCallBack(map,refreshMapsList);
					
				}		
			    
			}

		}	
	}
    
    //部署轮询
    @Scheduled(fixedRate = 60000)	
	public void deployPolling() throws Exception{
		log.info("部署轮询开始");
		List<Map<String, Object>> itmpMaps=(List<Map<String, Object>>) redisUtils.get("deployCallback");	
		
		
		if(itmpMaps!=null && !itmpMaps.isEmpty()) {
			Iterator<Map<String, Object>> it=itmpMaps.iterator();
			while(it.hasNext()){
				Map<String, Object> map = it.next();
				String jobName=map.get("jobName").toString();
				TblSystemJenkins tblSystemJenkins=iStructureService.selectSystemJenkinsById(map.get("systemJenkinsId").toString());
				TblToolInfo tblToolInfo=iStructureService.geTblToolInfo(Long.parseLong(map.get("jenkinsToolId").toString())) ;
				String jobNumber="";
				if(map.get("jobNumber")!=null){
					jobNumber=map.get("jobNumber").toString();
				}
                Boolean flag= iJenkinsBuildService.isJenkinsBuilding(tblToolInfo, tblSystemJenkins, jobName,Integer.parseInt(jobNumber));
				if(flag==true) {
					continue;				
				}else {
					List<Map<String, Object>> refreshMapsList=new ArrayList<>();
					refreshMapsList.addAll(itmpMaps);		
					refreshMapsList.remove(map);
					redisUtils.set("deployCallback", refreshMapsList);
					detailDeployCallBack(map,refreshMapsList);
                    
				}		
			    
			}

		}	
	}

    private void detailStructCallBack(Map<String, Object> paramMap,List<Map<String, Object>> itmpMaps) throws Exception {
		ObjectMapper json = new ObjectMapper();
		String param = "";
		try {
			param = json.writeValueAsString(paramMap);
			structureNotify.callBackManualJenkins(param);
			//itmpMaps.remove(paramMap);
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}

	}
    
    
    
    private void detailDeployCallBack(Map<String, Object> paramMap,List<Map<String, Object>> itmpMaps) throws Exception {
		ObjectMapper json = new ObjectMapper();
		String param = "";
		try {
			param = json.writeValueAsString(paramMap);
			structureNotify.callBackManualDepolyJenkins(param);
			//itmpMaps.remove(paramMap);
			
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}

	}
}
