package cn.pioneeruniverse.project.controller;

import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.project.entity.*;
import cn.pioneeruniverse.project.service.personnelmanagement.PersonnelManagementService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("personnelManagement")
public class PersonnelManagementController extends BaseController {
	@Autowired
	private PersonnelManagementService personnelManagementService;

    private static Logger log = LoggerFactory.getLogger(PersonnelManagementController.class);

	@RequestMapping(value = "selectCompanyVague",method= RequestMethod.POST)
	public List<Company> selectCompanyVague(String companyName) {
		return personnelManagementService.selectCompanyVague(companyName);
	}

	@RequestMapping(value = "selectDeptVague",method= RequestMethod.POST)
	public List<TblDeptInfo> selectDeptVague(String deptName) {
		return personnelManagementService.selectDeptVague(deptName);
	}

	@RequestMapping(value = "selectProjectNameVague",method= RequestMethod.POST)
	public List<TblProjectInfo> selectProjectNameVague(String projectName) {
		return personnelManagementService.selectProjectVague(projectName,null);
	}

	@RequestMapping(value = "selectProjectCodeVague",method= RequestMethod.POST)
	public List<TblProjectInfo> selectProjectCodeVague(String projectCode) {
		return personnelManagementService.selectProjectVague(null,projectCode);
	}

	@RequestMapping(value = "selectUserInfoVague",method= RequestMethod.POST)
	public List<TblUserInfo> selectUserInfoVague(TblUserInfo userInfo) {
		return personnelManagementService.selectUserInfoVague(userInfo);
	}

	@RequestMapping(value = "getAllUserProject",method= RequestMethod.POST)
	public String getAllUserProject(String search,Integer rows,Integer page) {
        JSONObject jsonObj = new JSONObject();
		try{
            Map<String,Object> mapObj = new HashMap<>();
            if (StringUtils.isNotBlank(search)) {
                mapObj = JSONObject.parseObject(search,Map.class);
            }
            mapObj.put("pageIndex", (page - 1) * rows);
            mapObj.put("pageSize", rows);
            int count= personnelManagementService.getAllUserProjectCount(mapObj);
            List<TblUserInfo> list = personnelManagementService.getAllUserProject(mapObj);

            jsonObj.put("page", page);
            if(rows!=null) {
                jsonObj.put("total", (count+rows-1)/rows);
            }
            jsonObj.put("records", count);
            jsonObj.put("rows", list);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("mes:" + e.getMessage(), e);
		}
		return jsonObj.toJSONString();
    }

	@RequestMapping(value = "getProjectInfoByUser",method= RequestMethod.POST)
	public Map<String,Object> getProjectInfoByUser(Long userId) {
		Map<String,Object> map = new HashMap<>();
		try{
			String projectNames = personnelManagementService.getProjectInfoByUser(userId);
			map.put("projectNames",projectNames);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	@RequestMapping(value = "getAllProject",method= RequestMethod.POST)
	public Map<String,Object> getAllProject() {
		Map<String,Object> map = new HashMap<>();
		try{
			List<TblProjectInfo> tblProjectInfo = personnelManagementService.getAllProject();
			map.put("tblProjectInfo",tblProjectInfo);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	@RequestMapping(value = "getProjectGroupByProjectId",method= RequestMethod.POST)
	public Map<String,Object> getProjectGroupByProjectId(Long projectId) {
		Map<String,Object> map = new HashMap<>();
		try{
			List<TblProjectGroup> projectGroup = personnelManagementService.
					getProjectGroupByProjectId(projectId);
			map.put("projectGroup",projectGroup);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}


	@RequestMapping(value = "insertProjectGroupUser",method= RequestMethod.POST)
	public Map<String,Object> insertProjectGroupUser(String tblProjectGroupUser,HttpServletRequest request) {
		Map<String,Object> map = new HashMap<>();
		try{
			List<TblProjectGroupUser> groupUserList = JSON.parseArray(tblProjectGroupUser,TblProjectGroupUser.class);
			personnelManagementService.insertItmpProjectGroupUser(groupUserList,request);
			map.put("status",1);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}

	@RequestMapping(value = "previewProject",method= RequestMethod.POST)
	public Map<String,Object> previewProject(Long projectId) {
		Map<String,Object> map = new HashMap<>();
		try{
			List<TblProjectGroup> groupList = personnelManagementService.previewProject(projectId);
			map.put("groupList",groupList);
		} catch (Exception e) {
			map = super.handleException(e, e.getMessage());
		}
		return map;
	}
}
