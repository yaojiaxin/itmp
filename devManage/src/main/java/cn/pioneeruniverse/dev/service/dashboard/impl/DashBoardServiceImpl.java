package cn.pioneeruniverse.dev.service.dashboard.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.common.utils.DateUtil;
import cn.pioneeruniverse.dev.dao.mybatis.*;
import cn.pioneeruniverse.dev.dao.mybatis.projectPlan.PlanApproveRequestMapper;
import cn.pioneeruniverse.dev.entity.*;
import cn.pioneeruniverse.dev.entity.projectPlan.PlanApproveRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.pioneeruniverse.dev.service.dashboard.DashBoardService;

@Transactional(readOnly = true)
@Service("DashBoardServiceImpl")
public class DashBoardServiceImpl implements DashBoardService {


	@Autowired
	private TblProjectInfoMapper projectInfoMapper; //项目mapper
	@Autowired
	private TblSystemInfoMapper systemMapper; //系统Mapper
	@Autowired
	private TblSystemJenkinsJobRunMapper sjrMapper; //系统构建mapper
	@Autowired
	private TblSystemModuleJenkinsJobRunMapper smjrMapper; //子系统JENKINSmapper
	@Autowired
	private TblRequirementFeatureMapper requirementFeatureMapper; //开发任务mapper
	@Autowired
	private TblDevTaskMapper devTaskMapper;	//工作任务mapper
	@Autowired
	private TblDevTaskScmHistoryMapper devTaskScmHistoryMapper; //源码提交mapper
	@Autowired
	private TblCommissioningWindowMapper commissioningWindowMapper;	//投产窗口mapper
	@Autowired
	private TblDefectInfoMapper defectMapper; //缺陷Mapper	
	@Autowired
	private TblRequirementFeatureTimeTraceMapper timeTraceMapper; //开发任务时间追踪Mapper
	@Autowired
	private TblSprintInfoMapper sprintInfoMapper; //冲刺表Mapper
	@Autowired
	private TblSystemModuleMapper systemModuleMapper; //系统模块表Mapper

	@Autowired
	private PlanApproveRequestMapper approveRequestMapper; //审批申请表Mapper

	private static String format = "yyyy-MM-dd";

	//个人工作台
	@Override
	public Map<String, Object> getUserDesk(Long userId) {
		Map<String, Object> map = new HashMap<>();
		List<DevTaskVo> featureList = requirementFeatureMapper.findFeatureByManageUserId(userId);
		List<TblDevTask> devTaskList = devTaskMapper.findDevTaskByDevUser(userId);
		List<TblDefectInfo> defectList=defectMapper.findDefectByAssignUserId(userId);
//		List<TblSystemJenkinsJobRun> sjrList = sjrMapper.findJenkinsJobRunByUser(userId);//已废弃
		map.put("featureList", featureList);
		map.put("devTaskList", devTaskList);
		map.put("defectList", defectList);
//		map.put("sjrList", sjrList);

		List<PlanApproveRequest> approveRequests = approveRequestMapper.getApproveByUserId(userId);
		List<PlanApproveRequest> approveRequests1 = approveRequestMapper.getRequestUserIdByUserId(userId);
		map.put("approveRequests",approveRequests);
		map.put("approveRequests1",approveRequests1);
		return map;
	}
	//获取所有项目
	@Override
	public Map<String, Object> getAllProject(Long id) {		
		Map<String, Object> map = new HashMap<>();
		List<TblProjectInfo> projectList = projectInfoMapper.findProjectByUser(id);			
		map.put("projectList", projectList);
		return map;
	}
	
	//获取投产窗口关联开发任务状态
	@Override
	public Map<String, Object> getFratureStatus(Long windowId,Long systemId) {
		Map<String, Object> map = new HashMap<>();
		List<TblRequirementFeature> featureList=requirementFeatureMapper.findFratureStatusByWindow(windowId,systemId);
		map.put("featureList", featureList);
		return map;
	}


	//DashBoard
	@Override
	public Map<String, Object> getDashBoard(Long systemId,String startDate,String endDate) {
		System.out.println("开始时间>>>"+DateUtil.formatDate(new Date(),DateUtil.fullFormat));
		Map<String, Object> map = new HashMap<>();
		TblSystemInfo system=systemMapper.getOneSystemInfo(systemId);
		if(system!=null&&system.getDevelopmentMode()!=null&&system.getDevelopmentMode()==1){
			List<TblRequirementFeature> featureList=requirementFeatureMapper.findFratureStatusBySystemId(systemId);
			List<TblSprintInfo> sprintInfo=sprintInfoMapper.findSprintBySystemIdDate(systemId);
			map.put("featureList", featureList);
			map.put("sprintInfo", sprintInfo);
		}else{
			List<TblCommissioningWindow> windowList=commissioningWindowMapper.findWindowBySystemId(systemId);
			map.put("windowsList", windowList); //投产窗口
		}

		//提交代码相关
		TblDevTaskScmHistory scmCount1= devTaskScmHistoryMapper.findScmCountBySystemId(systemId,(long)0);
		TblDevTaskScmHistory scmCount7= devTaskScmHistoryMapper.findScmCountBySystemId(systemId,(long)6);
		TblDevTaskScmHistory scmCount14= devTaskScmHistoryMapper.findScmCountBySystemId(systemId,(long)13);
		List<TblDevTaskScmHistory> ScmCountList7= devTaskScmHistoryMapper.find7DayScmCountBySystemId(systemId);

		//构建相关
		List<TblSystemJenkinsJobRun> jenkinsCountList = sjrMapper.find14DayJenkinsCountBySystemId(systemId);
		List<TblSystemJenkinsJobRun> jenkinsMinuteList = sjrMapper.find14DayJenkinsMinuteBySystemId(systemId);
		List<Map<String,Object>> top5JenkinsList = sjrMapper.findTop5JenkinsBySystemId(systemId);
		for (Map<String, Object> maps : top5JenkinsList) {	
			Long succ;
			maps.put("jenkinsId", maps.get("ID").toString());
			if (maps.get("createType").toString().equals("2")) {
				succ=sjrMapper.findTop1CustomJenkinsBySystemId(maps);				
			}else {
				succ=sjrMapper.findTop1AutomaticJenkinsBySystemId(maps);
			}
			if(succ.toString().equals("1")) {				
				maps.put("manualFrist", "true");
			}else {
				maps.put("manualFrist", "false");
			}
		}
		TblSystemJenkinsJobRun jenkinsCount1 = sjrMapper.findJenkinsCountBySystemId(systemId,(long)0);
		TblSystemJenkinsJobRun jenkinsCount7 = sjrMapper.findJenkinsCountBySystemId(systemId,(long)6);
		TblSystemJenkinsJobRun jenkinsCount14 = sjrMapper.findJenkinsCountBySystemId(systemId,(long)13);
		List<TblSystemJenkinsJobRun> jenkinsList7 = sjrMapper.find7DayJenkinsBySysyemId(systemId);
		//代码扫描
		List<TblSystemModule> systemModuleList= systemModuleMapper.selectSystemModule(systemId);
		List<TblSystemModuleJenkinsJobRun> moduleCount=smjrMapper.findNewModuleCountBySystemId(systemId,null);
		List<TblSystemModuleJenkinsJobRun> moduleTrend=smjrMapper.find7DayModuleTrendBySystemId(systemId,null);
		if(systemModuleList==null||systemModuleList.size()==0){
			map.put("moduleCount", moduleCount);	// 最近一次代码扫描问题总览
			map.put("moduleTrend", moduleTrend);	// 最近7日代码扫描问题趋势
			map.put("systemModuleList", systemModuleList);	// 查询子系统
		}else {
			map.put("systemModuleList", systemModuleList);	// 查询子系统
		}
		//开发任务时间点追踪
		List<TblRequirementFeatureTimeTrace> timeTraceList = getList(systemId,startDate,endDate);
        Map<Object,Object> timeTraceList1 = getList1(systemId,startDate,endDate);

		map.put("scmCount1", scmCount1);				//今日代码提交情况
		map.put("scmCount7", scmCount7);				//最近7日代码提交情况
		map.put("scmCount14", scmCount14);				//最近14日代码提交情况
		map.put("ScmCountList7", ScmCountList7);		//最近7日代码提交次数
		map.put("jenkinsCountList", jenkinsCountList);	//最近14日构建次数
		map.put("jenkinsMinuteList", jenkinsMinuteList);//最近14日平均构建耗时
		map.put("top5JenkinsList", top5JenkinsList);	//最新构建记录
		map.put("jenkinsCount1", jenkinsCount1);		//今日构建次数
		map.put("jenkinsCount7", jenkinsCount7);		//最近7日构建次数
		map.put("jenkinsCount14", jenkinsCount14);		//最近14日构建次数
		map.put("jenkinsList7", jenkinsList7);			//最近7日构建情况		

		//开发任务时间点追踪
		map.put("timeTraceList", timeTraceList);	//最近N天累积流图
		map.put("timeTraceList1", timeTraceList1);	//TTM分布图
		System.out.println("统计完成时间>>>"+DateUtil.formatDate(new Date(),DateUtil.fullFormat));
		return map;
	}
	@Override
	public Map<String, Object> getTheCumulative(Long systemId, String startDate, String endDate) {
		Map<String, Object> map = new HashMap<>();
		List<TblRequirementFeatureTimeTrace> timeTraceList = getList(systemId,startDate,endDate);
		Map<Object,Object> timeTraceList1 = getList1(systemId,startDate,endDate);

		map.put("timeTraceList", timeTraceList);	//最近N天累积流图
		map.put("timeTraceList1", timeTraceList1);	//Tim分布图
		return map;
	}

	@Override
	public Map<String,Object> valueStreamMapping(Long timeTraceId) {
		Map<String, Object> map = new HashMap<>();
		Map[] maps =new Map[7];
		List<Date> timeTrace= timeTraceMapper.findDateById(timeTraceId);
		List<Date> dates=getBetweenDate1(timeTrace.get(0),timeTrace.get(7));
		Collections.reverse(dates);
		for (int i=0;i<maps.length;i++){
			Map<String, Object> map1 = new HashMap<>();
			map1.put("startDate",timeTrace.get(i));
			map1.put("endDate",timeTrace.get(i+1));
			map1.put("status",i);
			maps[i]=map1;
		}
		map.put("dates",dates);
		map.put("maps",maps);
		return map;
	}

	@Override
	public Map<String, Object> getSonarByModuleId(Long systemModuleId) {
		Map<String, Object> map = new HashMap<>();
		List<TblSystemModuleJenkinsJobRun> moduleCount=smjrMapper.findNewModuleCountBySystemId(null,systemModuleId);
		List<TblSystemModuleJenkinsJobRun> moduleTrend=smjrMapper.find7DayModuleTrendBySystemId(null,systemModuleId);
		map.put("moduleCount", moduleCount);	// 最近一次代码扫描问题总览
		map.put("moduleTrend", moduleTrend);	// 最近7日代码扫描问题趋势
		return map;
	}

	public List<TblRequirementFeatureTimeTrace> getList(Long systemId,String startTime,String endTime){
		List<TblRequirementFeatureTimeTrace> list = new ArrayList<>();
		List<TblRequirementFeatureTimeTraceVo> timeTraceList = timeTraceMapper.selectBySystemId1(systemId);
    	List<Date> dateList= getBetweenDate(startTime,endTime);
    	for(Date date:dateList ) {
            TblRequirementFeatureTimeTrace trace1 = new TblRequirementFeatureTimeTrace();
            int onLine=0,stayOnline=0,inVerification=0,waitValid=0,achieve=0,process=0,ready=0;
    		for(TblRequirementFeatureTimeTraceVo trace:timeTraceList ) {
        		if(contrastDate(date,trace.getRequirementFeatureProdCompleteTime())){
                    onLine=onLine+1; continue;
        		}else if(contrastDate(date,trace.getRequirementFeatureTestCompleteTime())) {
                    stayOnline=stayOnline+1; continue;
        		}else if(contrastDate(date,trace.getRequirementFeatureTestingTime())) {
                    inVerification=inVerification+1; continue;
        		}else if(contrastDate(date,trace.getRequirementFeatureFirstTestDeployTime())) {
                    waitValid=waitValid+1; continue;
        		}else if(contrastDate(date,trace.getRequirementFeatureDevCompleteTime())) {
                    achieve=achieve+1; continue;
        		}else if(contrastDate(date,trace.getCodeFirstCommitTime())) {
                    process=process+1; continue;
        		}else if(contrastDate(date,trace.getRequirementFeatureCreateTime())) {
                    ready=ready+1; continue;
        		}      		
        	}
            trace1.setOnLine(onLine);
            trace1.setStayOnline(stayOnline);
            trace1.setInVerification(inVerification);
            trace1.setWaitValid(waitValid);
            trace1.setAchieve(achieve);
            trace1.setProcess(process);
            trace1.setReady(ready);
			trace1.setCliskDate(date);
    		list.add(trace1);
    	}
    	Collections.reverse(list);
		return list;   	
    }

	public Map<Object,Object> getList1(Long systemId,String startTime,String endTime){
		List<TblRequirementFeatureTimeTrace> timeTraceList = timeTraceMapper.selectBySystemId2(systemId);
		List<Date> dateList = getBetweenDate(startTime,endTime);
		Collections.reverse(dateList);
        Map<Object,Object> map = new LinkedHashMap<>();
        for(Date date:dateList ) {
            List<TblRequirementFeatureTimeTrace> list = new ArrayList<>();
            for (TblRequirementFeatureTimeTrace trace : timeTraceList) {
                TblRequirementFeatureTimeTrace trace1 = new TblRequirementFeatureTimeTrace();
                if (date.getTime() == trace.getProdCompleteTime().getTime()) {
                    trace1.setId(trace.getId());
                    trace1.setRequirementFeatureId(trace.getRequirementFeatureId());
                    trace1.setRequirementFeatureProdCompleteTime(trace.getProdCompleteTime());
                    trace1.setTaskDays(getTaskDays(trace.getRequirementFeatureCreateTime(), trace.getRequirementFeatureProdCompleteTime()));
                    list.add(trace1);
                }
            }
            map.put(date.getTime(),list);
        }
		return map;
	}

    public static List<Date> getBetweenDate(String startTime, String endTime){ 
    	SimpleDateFormat sdf = new SimpleDateFormat(format);
        // 声明保存日期集合
        List<Date> list = new ArrayList<>();
        try {
        	// 转化成日期类型
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);
            //用Calendar 进行日期比较判断
            Calendar calendar = Calendar.getInstance();
            while (startDate.getTime()<=endDate.getTime()){
                // 把日期添加到集合
                list.add(endDate);
                // 设置日期
                calendar.setTime(endDate);
                //把日期增加一天
                calendar.add(Calendar.DATE, -1);
                // 获取增加后的日期
                endDate=calendar.getTime();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

	public static List<Date> getBetweenDate1(Date startDate, Date endDate){
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		// 声明保存日期集合
		List<Date> list = new ArrayList<>();
		try {
			startDate=sdf.parse(sdf.format(startDate));
			endDate=sdf.parse(sdf.format(endDate));
			//用Calendar 进行日期比较判断
			Calendar calendar1 = Calendar.getInstance();
			calendar1.setTime(startDate);
			calendar1.add(Calendar.DATE, -5);
			Date date1 = calendar1.getTime();

			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(endDate);
			calendar2.add(Calendar.DATE, 5);
			Date date2 = calendar2.getTime();

			Calendar calendar = Calendar.getInstance();
			while (date1.getTime()<=date2.getTime()){
				// 把日期添加到集合
				list.add(date2);
				// 设置日期
				calendar.setTime(date2);
				//把日期增加一天
				calendar.add(Calendar.DATE, -1);
				// 获取增加后的日期
				date2=calendar.getTime();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

    public static Double getTaskDays(Date startDate,Date endDate){ 
    	long nh = 1000 * 60 * 60;//每小时毫秒数
    	double taskDay=0.0;
    	if(startDate!=null&&endDate!=null) {
    		Long diff =endDate.getTime()-startDate.getTime();   		
    		double hour = new BigDecimal((float)diff/nh).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();    		
    		taskDay=new BigDecimal((float)hour/24).setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    	}
    	return taskDay;
    }

	public static boolean contrastDate(Date date1, Date date2) {
		if(date2==null){
			return false;
		}else{
			if(date1.getTime()>=date2.getTime()){
                return true;
            }else {
                return false;
            }
		}
	}
}
