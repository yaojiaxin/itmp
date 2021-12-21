package cn.pioneeruniverse.project.controller.projectPlan;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.project.entity.TblProjectPlanApproveUser;
import cn.pioneeruniverse.project.entity.TblProjectPlanHistory;
import cn.pioneeruniverse.project.entity.TblUserInfo;
import cn.pioneeruniverse.project.service.projectPlan.ProjectPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("plan")
public class ProjectPlanController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(ProjectPlanController.class);

    @Autowired
    private ProjectPlanService projectPlanService;


    /**
     * 主页面
     * @param projectId
     * @return
     */
    @RequestMapping(value="getProjectPlan",method=RequestMethod.POST)
    public Map<String, Object> getProjectPlan(Long projectId,HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        try {
            map = projectPlanService.getProjectPlan(projectId,request);
            map.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return super.handleException(e, "项目计划查询失败！");
        }
        return map;
    }

    /**
     * 项目第一次提交计划
     * @param projectPlanList
     * @param projectId
     * @param request
     * @return
     */
    @RequestMapping(value="insertProjectPlan",method=RequestMethod.POST)
    public Map<String, Object> insertProjectPlan(String projectPlanList,Long projectId, HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        map.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            projectPlanService.insertProjectPlan(projectId,projectPlanList, request);
            map.put("status", 1);
        } catch (Exception e) {
            return super.handleException(e, "项目计划新增失败！");
        }
        return map;
    }

    /**
     * 提交项目计划变更
     * @param projectPlanList
     * @param projectId
     * @param request
     * @return
     */
    @RequestMapping(value="updateProjectPlan",method=RequestMethod.POST)
    public Map<String, Object> updateProjectPlan(Long projectId,String projectPlanList,
                     String updateMessage,String approveUsers, HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        map.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            projectPlanService.updateProjectPlan(projectId,projectPlanList,approveUsers,updateMessage, request);
            map.put("status", 1);
        } catch (Exception e) {
            return super.handleException(e, "项目计划变更失败！");
        }
        return map;
    }

    /**
     * 重新提交项目计划变更
     * @param projectPlanList
     * @param projectId
     * @param request
     * @return
     */
    @RequestMapping(value="updateProjectPlan2",method=RequestMethod.POST)
    public Map<String, Object> updateProjectPlan2(Long projectId,String projectPlanList,Long approveRequestId,
                                     String updateMessage,String approveUsers, HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        map.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            projectPlanService.updateProjectPlan2(projectId,projectPlanList,approveUsers,
                    updateMessage,approveRequestId, request);
            map.put("status", 1);
        } catch (Exception e) {
            return super.handleException(e, "项目计划变更失败！");
        }
        return map;
    }

    /**
     * 获取所有版本号
     * @param projectId
     * @return
     */
    @RequestMapping(value="getAllPlanNumber",method=RequestMethod.POST)
    public Map<String, Object> getAllPlanNumber(Long projectId){
        Map<String,Object> map = new HashMap<>();
        try {
            List<TblProjectPlanHistory> list = projectPlanService.getAllPlanNumber(projectId);
            map.put("data",list);
            map.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return super.handleException(e, "计划版本查询失败！");
        }
        return map;
    }

    /**
     * 根据版本号获取版本信息
     * @param projectId
     * @return
     */
    @RequestMapping(value="getPlanNumberByNumber",method=RequestMethod.POST)
    public Map<String, Object> getPlanNumberByNumber(Long projectId,Integer planNumber){
        Map<String,Object> map = new HashMap<>();
        try {
            map = projectPlanService.getPlanNumberByNumber(projectId,planNumber);
            map.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return super.handleException(e, "历史版本查询失败！");
        }
        return map;
    }


    /**
     * 获取变更审批页面需要的信息
     * @param projectId
     * @param approveRequestId
     * @return
     */
    @RequestMapping(value="getPlanApproveRequest",method=RequestMethod.POST)
    public Map<String, Object> getPlanApproveRequest(Long projectId,Long approveRequestId,HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        try {
            map = projectPlanService.getPlanApproveRequest(projectId,approveRequestId,request);
            map.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return super.handleException(e, "审批信息查询失败！");
        }
        return map;
    }

    /**
     * 取消变更
     * @param projectId
     * @param approveRequestId
     * @return
     */
    @RequestMapping(value="callOffUpdate",method=RequestMethod.POST)
    public Map<String, Object> callOffUpdate(Long projectId,Long approveRequestId,HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        try {
            projectPlanService.callOffUpdate(projectId,approveRequestId,request);
            map.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return super.handleException(e, "取消变更失败！");
        }
        return map;
    }
    /**
     * 返回
     * @param projectId
     * @return
     */
    @RequestMapping(value="getProjectPlan1",method=RequestMethod.POST)
    public Map<String, Object> getProjectPlan1(Long projectId){
        Map<String,Object> map = new HashMap<>();
        try {
            map = projectPlanService.getProjectPlan1(projectId);
            map.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return super.handleException(e, "项目计划查询失败！");
        }
        return map;
    }
    /**
     * 审批人审批
     * @param approveUser
     * @return
     */
    @RequestMapping(value="approve",method=RequestMethod.POST)
    public Map<String, Object> approve(TblProjectPlanApproveUser approveUser, HttpServletRequest request){
        Map<String,Object> map = new HashMap<>();
        try {
            projectPlanService.approve(approveUser,request);
            map.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return super.handleException(e, "审批失败！");
        }
        return map;
    }

    @RequestMapping(value = "selectUserInfoVague",method= RequestMethod.POST)
    public List<TblUserInfo> selectUserInfoVague(TblUserInfo userInfo) {
        return projectPlanService.selectUserInfoVague(userInfo);
    }

}
