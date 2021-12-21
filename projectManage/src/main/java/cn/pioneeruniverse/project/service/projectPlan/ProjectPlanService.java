package cn.pioneeruniverse.project.service.projectPlan;

import cn.pioneeruniverse.project.entity.TblProjectPlanApproveUser;
import cn.pioneeruniverse.project.entity.TblProjectPlanHistory;
import cn.pioneeruniverse.project.entity.TblUserInfo;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public interface ProjectPlanService {

	Map<String,Object> getProjectPlan(Long projectId,HttpServletRequest request);

	Map<String,Object> getProjectPlan1(Long projectId);

	void insertProjectPlan(Long projectId,String projectPlans,HttpServletRequest request);

	void updateProjectPlan(Long projectId,String projectPlanList,String approveUsers,
						   String updateMessage, HttpServletRequest request);

	void updateProjectPlan2(Long projectId,String projectPlanList,String approveUsers,
						   String updateMessage,Long approveRequestId, HttpServletRequest request);

	List<TblProjectPlanHistory> getAllPlanNumber(Long projectId);

	Map<String,Object> getPlanNumberByNumber(Long projectId,Integer planNumber);

	void callOffUpdate(Long projectId, Long approveRequestId,HttpServletRequest request);

	Map<String,Object> getPlanApproveRequest(Long projectId,Long approveRequestId,HttpServletRequest request);

	void approve(TblProjectPlanApproveUser approveUser, HttpServletRequest request);

	List<TblUserInfo> selectUserInfoVague(TblUserInfo userInfo);
}
