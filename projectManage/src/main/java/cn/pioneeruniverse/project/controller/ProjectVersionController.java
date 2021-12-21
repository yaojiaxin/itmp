package cn.pioneeruniverse.project.controller;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.project.entity.TblSystemInfo;
import cn.pioneeruniverse.project.entity.TblSystemVersion;
import cn.pioneeruniverse.project.service.projectversion.IProjectVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("projectVersion")
public class ProjectVersionController extends BaseController {

    @Autowired
    private IProjectVersionService projectVersionService;

    /**
     * 根据项目查询所有系统
     * @param projectId
     * @return
     */
    @RequestMapping("getSystemByProjectId")
    public Map<String, Object> getSystemByProjectId(Long projectId) {
        Map<String, Object> map = new HashMap<>();
        try {
            map = projectVersionService.selectSystemByProjectId(projectId);
            map.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            map = super.handleException(e, e.getMessage());
        }
        return map;
    }

    /**
     * 根据项目查询所有版本
     * @param projectId
     * @return
     */
    @RequestMapping("getProjectVersionByCon")
    public Map<String, Object> getProjectVersionByCon(Long projectId) {
        Map<String, Object> map = new HashMap<>();
        try {
            map = projectVersionService.getProjectVersionByCon(projectId);
            map.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            map = super.handleException(e, e.getMessage());
        }
        return map;
    }

    /**
     * 新增版本
     *
     * @param systemVersion
     * @param request
     * @return
     */
    @RequestMapping("addProjectVersion")
    public Map<String, Object> addProjectVersion(TblSystemVersion systemVersion,HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            projectVersionService.insertVersion(systemVersion,request);
            map.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            map = super.handleException(e, e.getMessage());
        }
        return map;
    }

    /**
     * 获取编辑的数据修改
     * @param systemVersion
     * @return
     */
    @RequestMapping("toUpdateProjectVersion")
    public Map<String, Object> toUpdateProjectVersion(TblSystemVersion systemVersion) {
        Map<String, Object> map = new HashMap<>();
        try {
            map = projectVersionService.getProjectVersionBySystemVersion(systemVersion);
            map.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            map = super.handleException(e, e.getMessage());
        }
        return map;
    }
    /**
     * 编辑提交
     *
     * @param systemVersion
     * @param request
     * @return
     */
    @RequestMapping("updateProjectVersion")
    public Map<String, Object> updateProjectVersion(TblSystemVersion systemVersion, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            projectVersionService.updateVersion(systemVersion, request);
            map.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            map = super.handleException(e, e.getMessage());
        }
        return map;
    }

    /**
     * 关闭或者开启版本
     *
     * @param systemVersion
     * @param request
     * @return
     */
    @RequestMapping("closeOrOpenSystemVersion")
    public Map<String, Object> closeOrOpenSystemVersion(TblSystemVersion systemVersion, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
             projectVersionService.closeOrOpenSystemVersion(systemVersion, request);
             map.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            map = super.handleException(e, e.getMessage());
        }
        return map;
    }
}
