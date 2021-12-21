package cn.pioneeruniverse.project.service.projectPlan.impl;

import java.sql.Timestamp;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.project.dao.mybatis.UserMapper;
import cn.pioneeruniverse.project.dao.mybatis.projectPlan.*;
import cn.pioneeruniverse.project.entity.*;
import cn.pioneeruniverse.project.vo.JqueryGanttVO;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.pioneeruniverse.project.service.projectPlan.ProjectPlanService;

@Service
public class ProjectPlanServiceImpl implements ProjectPlanService {
	
	@Autowired
	private ProjectPlanMapper projectPlanMapper;
	@Autowired
	private ProjectPlanHistoryMapper planHistoryMapper;
	@Autowired
	private ProjectPlanApproveRequestMapper approveRequestMapper;
	@Autowired
	private ProjectPlanApproveRequestLogMapper requestLogMapper;
	@Autowired
	private ProjectPlanApproveUserMapper approveUserMapper;
	@Autowired
	private UserMapper userMapper;

	@Override
	@Transactional(readOnly=true)
	public Map<String,Object> getProjectPlan(Long projectId,HttpServletRequest request) {
		Map<String,Object> map = new HashMap<>();
		List<JqueryGanttVO> list = new ArrayList<>();
		//获取项目计划状态
		Integer planStatus = projectPlanMapper.getProjectPlanStatus(projectId);
        Integer planNumber = 0;
		if (planStatus == null){
            planStatus = 1;
			list = projectPlanMapper.getProjectPlanTemplate();
		}else if(planStatus == 2||planStatus == 3){
			planNumber = planHistoryMapper.getMaxPlanNumber(projectId);
			TblProjectPlanApproveRequest approveRequest = approveRequestMapper.getLastPlanApproveRequest(projectId);
			list = approveRequestMapper.getPlanApproveRequestDetail(approveRequest.getId());
			map.put("approveRequestId",approveRequest.getId());
		}else if(planStatus == 4){
			planNumber = planHistoryMapper.getMaxPlanNumber(projectId);
			list = projectPlanMapper.getProjectPlan(projectId);
		}else{
            planStatus = 1;
			list = projectPlanMapper.getProjectPlanTemplate();
		}
		List<JqueryGanttVO> list1 = setList(list);
		//获取项目计划变更默认审批人
		List<TblProjectPlanApproveUserConfig> approveUserConfig =approveUserMapper.getApproveUserConfig(projectId);
        map.put("data",list1);
        map.put("planStatus",planStatus);
        map.put("planNumber",planNumber);
		map.put("approveUserConfig",approveUserConfig);
		map.put("userId",CommonUtil.getCurrentUserId(request));
		return map;
	}

	@Override
	@Transactional(readOnly=true)
	public Map<String,Object> getProjectPlan1(Long projectId) {
		Map<String,Object> map = new HashMap<>();
		Integer planNumber = planHistoryMapper.getMaxPlanNumber(projectId);
		List<JqueryGanttVO> list = projectPlanMapper.getProjectPlan(projectId);
		List<JqueryGanttVO> list1 = setList(list);
		map.put("data",list1);
        map.put("planStatus",4);
        map.put("planNumber",planNumber);
		return map;
	}

	@Override
	@Transactional(readOnly=false)
	public void insertProjectPlan(Long projectId,String projectPlanList,HttpServletRequest request) {
		projectPlanMapper.updateProjectStatus(4,projectId);

		List<TblProjectPlanInfo> projectPlans = JSON.parseArray(projectPlanList,TblProjectPlanInfo.class);
		projectPlanMapper.deleteProjectPlanByProjectId(projectId);
		for(TblProjectPlanInfo planInfo : projectPlans){
			CommonUtil.setBaseValue(planInfo,request);
			projectPlanMapper.insertProjectPlan(planInfo);
		}
		List<TblProjectPlanHistory> projectPlanHistorys = JSON.parseArray(projectPlanList,TblProjectPlanHistory.class);
		for(TblProjectPlanHistory planHistory : projectPlanHistorys){
			CommonUtil.setBaseValue(planHistory,request);
			planHistory.setProjectPlanNumber(1);
			planHistoryMapper.insertProjectPlanHistory(planHistory);
		}
	}

	@Override
	@Transactional(readOnly=false)
	public void updateProjectPlan(Long projectId, String projectPlanList, String approveUsers,
								  String updateMessage, HttpServletRequest request) {
		//修改项目计划状态为2（待审批）
		projectPlanMapper.updateProjectStatus(2,projectId);

		//添加审批申请记录
		TblProjectPlanApproveRequest approveRequest = new TblProjectPlanApproveRequest();
		approveRequest.setProjectId(projectId);
		approveRequest.setRequestUserId(CommonUtil.getCurrentUserId(request));
		approveRequest.setRequestReason(updateMessage);
		approveRequest.setApproveStatus(1);
		approveRequestMapper.insertPlanApproveRequest(approveRequest);

		//添加审批申请明细记录
		List<TblProjectPlanApproveRequestDetail> approveRequestDetails =
				JSON.parseArray(projectPlanList,TblProjectPlanApproveRequestDetail.class);
		for(TblProjectPlanApproveRequestDetail requestDetail : approveRequestDetails){
			CommonUtil.setBaseValue(requestDetail,request);
			requestDetail.setProjectPlanId(requestDetail.getId());
			requestDetail.setProjectPlanApproveRequestId(approveRequest.getId());
			approveRequestMapper.insertPlanApproveRequestDetail(requestDetail);
		}
		//添加审批申请日志
		TblProjectPlanApproveRequestLog requestLog = new TblProjectPlanApproveRequestLog();
		Long userId = CommonUtil.getCurrentUserId(request);
		requestLog.setProjectPlanApproveRequestId(approveRequest.getId());
		requestLog.setLogType("申请变更审批");
		requestLog.setLogDetail(updateMessage);
		requestLog.setUserId(userId);
		TblUserInfo userInfo = userMapper.selectUserById(userId);
		requestLog.setUserName(userInfo.getUserName());
		requestLog.setUserAccount(userInfo.getUserAccount());
		CommonUtil.setBaseValue(requestLog,request);
		requestLogMapper.insertPlanApproveRequestLog(requestLog);

		// 修改审批申请默认配置人员
		// 添加审批申请人
		approveUserMapper.deleteProjectPlanApproveUserConfig(projectId);
		List<TblProjectPlanApproveUser> approveUserList = JSON.parseArray(approveUsers,TblProjectPlanApproveUser.class);
		for(TblProjectPlanApproveUser user : approveUserList){
			user.setProjectPlanApproveRequestId(approveRequest.getId());
			user.setApproveStatus(1);
			if(user.getApproveLevel()==1){
				user.setApproveOnOff(1);
			}else {
				user.setApproveOnOff(0);
			}
			approveUserMapper.insertProjectPlanApproveUser(user);

			TblProjectPlanApproveUserConfig userConfig = new TblProjectPlanApproveUserConfig();
			userConfig.setProjectId(projectId);
			userConfig.setUserId(user.getUserId());
			userConfig.setApproveLevel(user.getApproveLevel());
			approveUserMapper.insertProjectPlanApproveUserConfig(userConfig);
		}
	}

	@Override
	@Transactional(readOnly=false)
	public void updateProjectPlan2(Long projectId, String projectPlanList, String approveUsers,
								  String updateMessage,Long approveRequestId, HttpServletRequest request) {
		TblProjectPlanApproveRequest approveRequest = new TblProjectPlanApproveRequest();
		approveRequest.setId(approveRequestId);
		approveRequest.setRequestUserId(CommonUtil.getCurrentUserId(request));
		approveRequest.setRequestReason(updateMessage);
		approveRequestMapper.updatePlanApproveRequest(approveRequest);

		approveRequestMapper.deleteApproveRequestDetailByRequestId(approveRequestId);
		List<TblProjectPlanApproveRequestDetail> approveRequestDetails =
				JSON.parseArray(projectPlanList,TblProjectPlanApproveRequestDetail.class);
		for(TblProjectPlanApproveRequestDetail requestDetail : approveRequestDetails){
			CommonUtil.setBaseValue(requestDetail,request);
			requestDetail.setProjectPlanId(requestDetail.getId());
			requestDetail.setProjectPlanApproveRequestId(approveRequestId);
			approveRequestMapper.insertPlanApproveRequestDetail(requestDetail);
		}

		TblProjectPlanApproveRequestLog requestLog = new TblProjectPlanApproveRequestLog();
		Long userId = CommonUtil.getCurrentUserId(request);
		requestLog.setProjectPlanApproveRequestId(approveRequest.getId());
		requestLog.setLogType("重新申请变更审批");
		requestLog.setLogDetail(updateMessage);
		requestLog.setUserId(userId);
		TblUserInfo userInfo = userMapper.selectUserById(userId);
		requestLog.setUserName(userInfo.getUserName());
		requestLog.setUserAccount(userInfo.getUserAccount());
		CommonUtil.setBaseValue(requestLog,request);
		requestLogMapper.insertPlanApproveRequestLog(requestLog);

		approveUserMapper.deleteProjectPlanApproveUserConfig(projectId);
		approveUserMapper.deletePlanApproveUserByRequestId(approveRequestId);
		List<TblProjectPlanApproveUser> approveUserList = JSON.parseArray(approveUsers,TblProjectPlanApproveUser.class);
		for(TblProjectPlanApproveUser user : approveUserList){
			user.setProjectPlanApproveRequestId(approveRequestId);
			user.setApproveStatus(1);
			if(user.getApproveLevel()==1){
				user.setApproveOnOff(1);
			}else {
				user.setApproveOnOff(0);
			}
			approveUserMapper.insertProjectPlanApproveUser(user);

			TblProjectPlanApproveUserConfig userConfig = new TblProjectPlanApproveUserConfig();
			userConfig.setProjectId(projectId);
			userConfig.setUserId(user.getUserId());
			userConfig.setApproveLevel(user.getApproveLevel());
			approveUserMapper.insertProjectPlanApproveUserConfig(userConfig);
		}
	}

	@Override
	@Transactional(readOnly=true)
	public List<TblProjectPlanHistory> getAllPlanNumber(Long projectId) {
		return planHistoryMapper.getAllPlanNumber(projectId);
	}

	@Override
	@Transactional(readOnly=true)
	public Map<String,Object> getPlanNumberByNumber(Long projectId,Integer planNumber) {
		Map<String,Object> map = new HashMap<>();
		List<JqueryGanttVO> list = planHistoryMapper.getPlanNumberByNumber(projectId,planNumber);
		List<JqueryGanttVO> list1 = setList(list);
		map.put("data",list1);
		return map;
	}

	@Override
	@Transactional(readOnly=false)
	public void callOffUpdate(Long projectId, Long approveRequestId,HttpServletRequest request) {
		projectPlanMapper.updateProjectStatus(4,projectId);
		approveRequestMapper.deleteApproveRequestById(approveRequestId);
		approveRequestMapper.deleteApproveRequestDetailByRequestId(approveRequestId);
		requestLogMapper.deleteApproveRequestLogByRequestId(approveRequestId);
		approveUserMapper.deletePlanApproveUserByRequestId(approveRequestId);
	}

	@Override
	@Transactional(readOnly=true)
	public Map<String, Object> getPlanApproveRequest(Long projectId,Long approveRequestId,HttpServletRequest request) {
		Map<String,Object> map = new HashMap<>();
		TblProjectPlanApproveRequest approveRequest = approveRequestMapper.getPlanApproveRequestById(approveRequestId);

		List<TblProjectPlanInfo> projectPlan = projectPlanMapper.getProjectPlanByProjectId(projectId);
		List<TblProjectPlanApproveRequestDetail> requestDetail
				= approveRequestMapper.getRequestDetailByApproveId(approveRequestId);
		List<TblProjectPlanApproveRequestLog> requestLog = requestLogMapper.getApproveRequestLog(approveRequestId);
		approveRequest.setCommitDate(requestDetail.get(0).getCreateDate());

		Long userId = CommonUtil.getCurrentUserId(request);

		TblProjectPlanApproveUser approveUser = approveUserMapper.getApproveUserButt(approveRequestId,userId);
		if(approveUser != null){
			List<TblProjectPlanApproveUser> approveUsers = approveUserMapper.getAllApproveUserDesc(approveRequestId);
			if(userId == approveUsers.get(0).getUserId()){
				map.put("succ",2);
			}else {
				map.put("succ",1);
			}
			//审批申请人员表ID
			map.put("id",approveUser.getId());
		}
		//当前操作人员ID
		map.put("userId1",userId);
		map.put("requestLog",requestLog);
		map.put("projectPlan",projectPlan);
		map.put("requestDetail",requestDetail);
		map.put("approveRequest",approveRequest);
		return map;
	}

	@Override
	@Transactional(readOnly=false)
	public void approve(TblProjectPlanApproveUser approveUser,HttpServletRequest request){
        //修改本级审批人审批状态为已审批（已审批）
        approveUser.setApproveStatus(2);
		approveUser.setApproveOnOff(0);
        approveUserMapper.updateApproveUser(approveUser);
		List<TblProjectPlanApproveUser> approveUsers = approveUserMapper.
				getAllApproveUserDesc(approveUser.getProjectPlanApproveRequestId());
		Collections.reverse(approveUsers);

		for(int i=0;i<approveUsers.size();i++){
			if(approveUser.getId()==approveUsers.get(i).getId()&&approveUsers.size()-i>1){
				approveUserMapper.updateApproveUser1(approveUsers.get(i+1).getId());
			}
		}


        //添加审批日志信息
        TblProjectPlanApproveRequestLog requestLog = new TblProjectPlanApproveRequestLog();
        requestLog.setProjectPlanApproveRequestId(approveUser.getProjectPlanApproveRequestId());
        requestLog.setLogDetail(approveUser.getApproveSuggest());
        requestLog.setUserId(approveUser.getUserId());
        TblUserInfo userInfo = userMapper.selectUserById(approveUser.getUserId());
        requestLog.setUserName(userInfo.getUserName());
        requestLog.setUserAccount(userInfo.getUserAccount());
        CommonUtil.setBaseValue(requestLog,request);

        Integer projectPlanStatus = 0;
		if(approveUser.getOperate()==1){
            projectPlanStatus = 3; //修改项目计划状态为3（审批中）
			requestLog.setLogType("同意并提交下一级审批人");
		}else if(approveUser.getOperate()==2){
            projectPlanStatus = 4; //修改项目计划状态为4（正式）
			requestLog.setLogType("同意并办结");

            //将申请变更记录的状态变为2(审批完成)
            TblProjectPlanApproveRequest approveRequest = new TblProjectPlanApproveRequest();
            approveRequest.setId(approveUser.getProjectPlanApproveRequestId());
            approveRequest.setApproveStatus(2);
            approveRequestMapper.updatePlanApproveStatus(approveRequest);

			//添加正式版本与历史版本
			insertPlan(approveUser.getProjectId(),approveUser.getProjectPlanApproveRequestId(),request);
		}else if(approveUser.getOperate()==3){
            projectPlanStatus = 4; //修改项目计划状态为4（正式）
			requestLog.setLogType("驳回项目计划变更");

			//将申请变更记录的状态变为3(驳回)
			TblProjectPlanApproveRequest approveRequest = new TblProjectPlanApproveRequest();
			approveRequest.setId(approveUser.getProjectPlanApproveRequestId());
			approveRequest.setApproveStatus(3);
			approveRequestMapper.updatePlanApproveStatus(approveRequest);
		}
        requestLogMapper.insertPlanApproveRequestLog(requestLog);
        projectPlanMapper.updateProjectStatus(projectPlanStatus,approveUser.getProjectId());
	}

	@Override
	public List<TblUserInfo> selectUserInfoVague(TblUserInfo userInfo) {
		return projectPlanMapper.selectUserInfoVague(userInfo.getUserAndAccount());
	}

	//审批完成添加正式版本与历史版本
	private void insertPlan(Long projectId,Long approveRequestId,HttpServletRequest request){
		List<TblProjectPlanApproveRequestDetail> approveRequestDetail =
				approveRequestMapper.getRequestDetailByApproveId(approveRequestId);
		String detail = JSON.toJSONString(approveRequestDetail);

		List<TblProjectPlanInfo> projectPlans = JSON.parseArray(detail,TblProjectPlanInfo.class);
		projectPlanMapper.deleteProjectPlanByProjectId(projectId);
		for(TblProjectPlanInfo planInfo : projectPlans){
			planInfo.setProjectId(projectId);
			CommonUtil.setBaseValue(planInfo,request);
			projectPlanMapper.insertProjectPlan(planInfo);
		}
		List<TblProjectPlanHistory> projectPlanHistorys = JSON.parseArray(detail,TblProjectPlanHistory.class);
		Integer number = planHistoryMapper.getMaxPlanNumber(projectId)+1;
		for(TblProjectPlanHistory planHistory : projectPlanHistorys){
			planHistory.setProjectId(projectId);
			CommonUtil.setBaseValue(planHistory,request);
			planHistory.setProjectPlanNumber(number);
			planHistoryMapper.insertProjectPlanHistory(planHistory);
		}
	}


	private List<JqueryGanttVO> setList(List<JqueryGanttVO> list){
		for(JqueryGanttVO jqueryGanttVO:list) {
			if (jqueryGanttVO.getStartIsMilestone1() != null && jqueryGanttVO.getStartIsMilestone1() == 1) {
				jqueryGanttVO.setStartIsMilestone(true);
			} else if (jqueryGanttVO.getStartIsMilestone1() != null && jqueryGanttVO.getStartIsMilestone1() == 2) {
				jqueryGanttVO.setStartIsMilestone(false);
			}
			if (jqueryGanttVO.getEndIsMilestone1() != null && jqueryGanttVO.getEndIsMilestone1() == 1) {
				jqueryGanttVO.setEndIsMilestone(true);
			} else if (jqueryGanttVO.getEndIsMilestone1() != null && jqueryGanttVO.getEndIsMilestone1() == 2) {
				jqueryGanttVO.setEndIsMilestone(false);
			}
			if(jqueryGanttVO.getStart() == null){
				jqueryGanttVO.setStart(new Timestamp(new Date().getTime()));
			}
			if(jqueryGanttVO.getEnd() == null){
				jqueryGanttVO.setEnd(new Timestamp(new Date().getTime()));
			}
		}
		return list;
	}
}
