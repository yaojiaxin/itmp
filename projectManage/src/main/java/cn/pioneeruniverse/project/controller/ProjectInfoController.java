package cn.pioneeruniverse.project.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.project.entity.TblProjectGroup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.project.entity.TblProjectInfo;
import cn.pioneeruniverse.project.entity.TblSystemInfo;
import cn.pioneeruniverse.project.service.dept.DeptInfoService;
import cn.pioneeruniverse.project.service.projectgroup.ProjectGroupService;
import cn.pioneeruniverse.project.service.projectgroupuser.ProjectGroupUserService;
import cn.pioneeruniverse.project.service.projectinfo.ProjectInfoService;
import cn.pioneeruniverse.project.service.systeminfo.SystemInfoService;
import cn.pioneeruniverse.project.service.user.UserService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("project")
public class ProjectInfoController extends BaseController {

    private final static Logger logger = LoggerFactory.getLogger(ProjectInfoController.class);

    @Autowired
    private ProjectInfoService projectInfoService;

    @Autowired
    private DeptInfoService deptInfoService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectGroupUserService projectGroupUserService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SystemInfoService systemInfoService;

    @Autowired
    private ProjectGroupService projectGroupService;

    /**
     * ???????????????????????????????????????
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "getAllProject", method = RequestMethod.POST)
    public Map<String, Object> getAllProject(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            result = projectInfoService.getAllProject(request);
            result.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return handleException(e, "????????????????????????");
        }
        return result;
    }

    /**
     * ??????????????????and??????????????????
     *
     * @param tblProjectInfo
     * @param userName
     * @param currentPage
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "selectProjects", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> selectProjects(
            TblProjectInfo tblProjectInfo, @RequestParam(value = "userName", required = false) String userName,
            Integer page, Integer rows
    ) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            //???????????????????????????id
            if (StringUtils.isNotEmpty(userName)) {
                Long userId = userService.findIdByUserName(userName);
                List<Long> ids = projectGroupUserService.findProjectGroupIdsByUserId(userId);
                List<Long> ids1 = projectGroupService.findProjectIdsByProjectGroupIds(ids);
                tblProjectInfo.setIds(ids1);
            }
            //????????????????????????????????????redis??????????????????code
            //TBL_PROJECT_INFO_PROJECT_TYPE   TBL_PROJECT_INFO_PROJECT_STATUS
            String typeName = tblProjectInfo.getProjectTypeName();
            String statusName = tblProjectInfo.getProjectStatusName();
            if (StringUtils.isNotEmpty(typeName)) {
                String redisStr = redisUtils.get("TBL_PROJECT_INFO_PROJECT_TYPE").toString();
                JSONObject jsonObj = JSON.parseObject(redisStr);

                for (String key : jsonObj.keySet()) {
                    String value = jsonObj.get(key).toString();
                    if (value.equals(typeName)) {
                        tblProjectInfo.setProjectType(Integer.valueOf(key));
                    }
                }
            }
            if (StringUtils.isNotEmpty(statusName)) {
                String redisStr = redisUtils.get("TBL_PROJECT_INFO_PROJECT_STATUS").toString();
                JSONObject jsonObj = JSON.parseObject(redisStr);
                for (String key : jsonObj.keySet()) {
                    String value = jsonObj.get(key).toString();
                    if (value.equals(statusName)) {
                        tblProjectInfo.setProjectStatus(Integer.valueOf(key));
                    }
                }
            }
            List<TblProjectInfo> list = projectInfoService.selectProjects(tblProjectInfo, page, rows);
            List<TblProjectInfo> list2 = projectInfoService.selectProjects(tblProjectInfo, 1, Integer.MAX_VALUE);
            for (TblProjectInfo tpi : list) {
                Long deptId = tpi.getDeptId();
                String deptName = deptInfoService.selectDeptName(deptId);
                tpi.setDeptName(deptName);
                //???????????????????????????????????????code??????redis???????????????????????????
                Integer t = tpi.getProjectType();
                if (t != null) {
                    String type = t.toString();
                    String redisStr = redisUtils.get("TBL_PROJECT_INFO_PROJECT_TYPE").toString();
                    JSONObject jsonObj = JSON.parseObject(redisStr);
                    String projectTypeName = jsonObj.get(type).toString();
                    tpi.setProjectTypeName(projectTypeName);
                }
                Integer s = tpi.getProjectStatus();
                if (s != null) {
                    String status = s.toString();
                    String redisStr2 = redisUtils.get("TBL_PROJECT_INFO_PROJECT_STATUS").toString();
                    JSONObject jsonObj2 = JSON.parseObject(redisStr2);
                    String projectStatusName = jsonObj2.get(status).toString();
                    tpi.setProjectStatusName(projectStatusName);
                }
            }
//				Integer records = projectInfoService.selectCount();    //????????????
            Integer total = list2.size() / rows;                      //?????????=????????????/?????????????????????(????????????)
//				Integer page = page;                            //?????????
            map.put("page", page);
            map.put("total", total);
            map.put("records", list2.size());
            map.put("data", list);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.handleException(e, "??????????????????");
        }
        return map;
    }

    /**
     * ???redis????????????????????????????????????????????????
     *
     * @param termCode
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "selectTypeAndStatusList", method = RequestMethod.POST)
    public Map<String, Object> selectTypeList(@RequestParam("termCode") String termCode) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            ArrayList<String> list = new ArrayList<>();
            String redisStr = redisUtils.get(termCode).toString();
            JSONObject jsonObj = JSON.parseObject(redisStr);
            for (String key : jsonObj.keySet()) {
                String ValueName = jsonObj.get(key).toString();
                list.add(ValueName);
            }
//			System.out.println(list);
            map.put("data", list);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.handleException(e, "????????????????????????");
        }
        return map;
    }

    /**
     * ????????????
     *
     * @param tblProjectInfo
     * @return
     */
    @RequestMapping(value = "insertProject", method = RequestMethod.POST)
    public void insertProject(TblProjectInfo tblProjectInfo) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", Constants.ITMP_RETURN_SUCCESS);
        String deptName = tblProjectInfo.getDeptName();
        Long deptId = deptInfoService.selectDeptId(deptName);
        tblProjectInfo.setDeptId(deptId);
        //????????????????????????redis????????????????????????
        String typeName = tblProjectInfo.getProjectTypeName();
        String redisStr = redisUtils.get("TBL_PROJECT_INFO_PROJECT_TYPE").toString();
        JSONObject jsonObj = JSON.parseObject(redisStr);
        for (String key : jsonObj.keySet()) {
            String value = jsonObj.get(key).toString();
            if (value.equals(typeName)) {
                tblProjectInfo.setProjectType(Integer.valueOf(key));
            }
        }
        try {
            projectInfoService.insertProject(tblProjectInfo);
            //?????????????????????????????????id
            Long projectId = tblProjectInfo.getId();
            //??????????????????????????????
            List<String> systemNames = tblProjectInfo.getSystemName();
            //????????????????????????
            systemInfoService.updateSystemInfo(projectId, systemNames);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.handleException(e, "??????????????????");
        }
    }

    /**
     * ????????????
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "deleteProject", method = RequestMethod.POST)
    public void deleteProjectById(@RequestParam("id") Long id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            projectInfoService.deleteProjectById(id);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.handleException(e, "??????????????????");
        }
    }

    /**
     * ????????????id????????????(??????????????????????????????????????????)
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "selectProjectById", method = RequestMethod.POST)
    public Map<String, Object> selectProjectById(@RequestParam("id") Long id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            TblProjectInfo tpi = projectInfoService.selectProjectById(id);
            Long deptId = tpi.getDeptId();
            String type = tpi.getProjectType().toString();
            String status = tpi.getProjectStatus().toString();
            String deptName = deptInfoService.selectDeptName(deptId);
            tpi.setDeptName(deptName);
            //???redis?????????????????????
            String redisStr = redisUtils.get("TBL_PROJECT_INFO_PROJECT_TYPE").toString();
            JSONObject jsonObj = JSON.parseObject(redisStr);
            String typeName = jsonObj.get(type).toString();
            //???????????????
            String redisStr2 = redisUtils.get("TBL_PROJECT_INFO_PROJECT_STATUS").toString();
            JSONObject jsonObj2 = JSON.parseObject(redisStr2);
            String statusName = jsonObj2.get(status).toString();
            tpi.setProjectTypeName(typeName);
            tpi.setProjectStatusName(statusName);
            //??????????????????
            List<String> systemNames = systemInfoService.selectSystemName(id);
            tpi.setSystemName(systemNames);
//			map.put("systemNames", systemNames);
            map.put("data", tpi);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.handleException(e, "??????????????????");
        }
        return map;
    }

    /**
     * ??????(??????)??????
     *
     * @param
     * @return
     */
    @RequestMapping(value = "updateProjectById", method = RequestMethod.POST)
    public Map<String, Object> updateProject(TblProjectInfo tblProjectInfo, List<String> systemNames) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", Constants.ITMP_RETURN_SUCCESS);
//		String deptName = tblProjectInfo.getDeptName();
//		Long deptId = deptInfoService.selectDeptId(deptName);
//		tblProjectInfo.setDeptId(deptId);
        //???????????????????????????id
        Long projectId = tblProjectInfo.getId();
        try {
            projectInfoService.updateProject(tblProjectInfo);
            systemInfoService.updateSystemInfo(projectId, systemNames);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.handleException(e, "??????????????????");
        }
        return map;
    }

    /**
     * ??????????????????????????????????????????(???????????????????????????)
     *
     * @param systemName
     */
    public void updateSystemInfoBySystemName(String systemName) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            systemInfoService.updateSystemInfoBySystemName(systemName);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.handleException(e, "??????????????????????????????");
        }
    }

    /**
     * ????????????????????????????????????????????????(?????????)
     *
     * @param tblSystemInfo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "selectSystemInfo", method = RequestMethod.POST)
    public Map<String, Object> selectSystemInfo(TblSystemInfo tblSystemInfo,
                                                @RequestParam(value = "pageNumber", defaultValue = "1", required = true) Integer pageNumber,                             //
                                                @RequestParam(value = "pageSize", defaultValue = "10", required = true) Integer pageSize
    ) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            List<TblSystemInfo> list = systemInfoService.selectSystemInfo(tblSystemInfo, pageNumber, pageSize);
            List<TblSystemInfo> list2 = systemInfoService.selectSystemInfo(tblSystemInfo, 1, Integer.MAX_VALUE);
            map.put("total", list2.size());
            map.put("rows", list);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.handleException(e, "????????????????????????");
        }
        return map;
    }


    /**
     * ???????????????  ??????????????????????????????????????????????????????????????????????????????
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "selectAllDeptName", method = RequestMethod.POST)
    public Map<String, Object> selectAllDeptName() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            List<String> list = deptInfoService.selectAllDeptName();
            map.put("data", list);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            this.handleException(e, "????????????????????????");
        }
        return map;
    }


    @RequestMapping(value = "getProjectGroupTree", method = RequestMethod.POST)
    public List<Map<String, Object>> getProjectGroupTree(Long projectId) {
        return projectGroupService.getProjectGroupTree(projectId);
    }

    @RequestMapping(value = "getProjectGroupTreeBySystemId", method = RequestMethod.POST)
    public List<Map<String, Object>> getProjectGroupTreeBySystemId(Long systemId) {
        return projectGroupService.getProjectGroupTreeBySystemId(systemId);
    }

    @RequestMapping(value = "getProjectGroupByProjectGroupId", method = RequestMethod.POST)
    public TblProjectGroup getProjectGroupByProjectGroupId(Long projectGroupId) {
        return projectGroupService.getProjectGroupByProjectGroupId(projectGroupId);
    }

    @RequestMapping(value = "getAllProjectInfo", method = RequestMethod.POST)
    public List<TblProjectInfo> getAllProjectInfo() {
        return projectInfoService.getAllProjectInfo();
    }


    public Map<String, Object> handleException(Exception e, String message) {
        e.printStackTrace();
        logger.error(message + ":" + e.getMessage(), e);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", Constants.ITMP_RETURN_FAILURE);
        map.put("errorMessage", message);
        return map;
    }
}
