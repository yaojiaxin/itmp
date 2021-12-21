package cn.pioneeruniverse.dev.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.dev.entity.TblSystemVersion;
import cn.pioneeruniverse.dev.service.systemversion.ISystemVersionService;

@RestController
@RequestMapping("systemVersion")
public class SystemVersionController extends BaseController {

    @Autowired
    private ISystemVersionService systemVersionService;

    /**
     * 根据条件查询所有版本
     *
     * @param systemId
     * @return
     */
    @RequestMapping("getSystemVersionByCon")
    public Map<String, Object> getSystemVersionByCon(Long systemId,Integer status) {
        Map<String, Object> map = new HashMap<>();
        try {
            map = systemVersionService.getSystemVersionByCon(systemId, status);
            map.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            map = super.handleException(e, e.getMessage());
        }
        return map;
    }

    /**
     * 添加
     *
     * @param systemVersion
     * @param request
     * @return
     */
    @RequestMapping("addSystemVersion")
    public Map<String, Object> addSystemVersion(TblSystemVersion systemVersion,HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer succ = systemVersionService.insertVersion(systemVersion,request);
            if(succ==0){
                map.put("status", Constants.ITMP_RETURN_SUCCESS);
            }else{
                map.put("status", Constants.ITMP_RETURN_FAILURE);
                map.put("errorMessage", "已存在相同的版本标签");
            }
        } catch (Exception e) {
            map = super.handleException(e, e.getMessage());
        }
        return map;
    }

    /**
     * 修改
     *
     * @param systemVersion
     * @param request
     * @return
     */
    @RequestMapping("updateSystemVersion")
    public Map<String, Object> updateSystemVersion(TblSystemVersion systemVersion, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            Integer succ = systemVersionService.updateVersion(systemVersion, request);
            if(succ==0){
                map.put("status", Constants.ITMP_RETURN_SUCCESS);
            }else{
                map.put("status", Constants.ITMP_RETURN_FAILURE);
                map.put("errorMessage", "已存在相同的版本标签");
            }
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
            Integer succ = systemVersionService.closeOrOpenSystemVersion(systemVersion, request);
            if(succ>0){
                map.put("status", Constants.ITMP_RETURN_SUCCESS);
            }else{
                map.put("status", Constants.ITMP_RETURN_FAILURE);
                map.put("errorMessage", "操作失败");
            }
        } catch (Exception e) {
            map = super.handleException(e, e.getMessage());
        }
        return map;
    }

    @RequestMapping(value = "getSystemVersionBySystemVersionId", method = RequestMethod.POST)
    public TblSystemVersion getSystemVersionBySystemVersionId(Long systemVersionId) {
        return systemVersionService.getSystemVersionBySystemVersionId(systemVersionId);
    }
}
