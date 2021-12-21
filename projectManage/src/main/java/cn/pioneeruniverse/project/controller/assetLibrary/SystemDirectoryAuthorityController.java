package cn.pioneeruniverse.project.controller.assetLibrary;

import cn.pioneeruniverse.common.entity.AjaxModel;
import cn.pioneeruniverse.common.entity.BootStrapTablePage;
import cn.pioneeruniverse.project.entity.TblSystemDirectoryPostAuthority;
import cn.pioneeruniverse.project.entity.TblSystemDirectoryUserAuthority;
import cn.pioneeruniverse.project.service.assetsLibrary.SystemDirectoryAuthorityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 10:26 2019/12/5
 * @Modified By:
 */
@RestController
@RequestMapping("/assetLibrary/directoryAuthority")
public class SystemDirectoryAuthorityController {

    private static final Logger logger = LoggerFactory.getLogger(SystemDirectoryAuthorityController.class);

    @Autowired
    SystemDirectoryAuthorityService systemDirectoryAuthorityService;


    @RequestMapping(value = "getUserAuthorities", method = RequestMethod.POST)
    public BootStrapTablePage<TblSystemDirectoryUserAuthority> getUserAuthorities(Long directoryId, HttpServletRequest request, HttpServletResponse response) {
        return systemDirectoryAuthorityService.getDirectoryUserAuthority(new BootStrapTablePage<TblSystemDirectoryUserAuthority>(request, response), directoryId);
    }

    @RequestMapping(value = "getPostAuthorities", method = RequestMethod.POST)
    public BootStrapTablePage<TblSystemDirectoryPostAuthority> getPostAuthorities(Long directoryId, HttpServletRequest request, HttpServletResponse response) {
        return systemDirectoryAuthorityService.getDirectoryPostAuthority(new BootStrapTablePage<TblSystemDirectoryPostAuthority>(request, response), directoryId);
    }


    @RequestMapping(value = "/addOrUpdateUserAuthority", method = RequestMethod.POST)
    public AjaxModel addOrUpdateUserAuthority(@RequestBody List<TblSystemDirectoryUserAuthority> tblSystemDirectoryUserAuthorities, HttpServletRequest request) {
        try {
            systemDirectoryAuthorityService.addOrUpdateDirectoryUserAuthority(tblSystemDirectoryUserAuthorities, request);
            return AjaxModel.SUCCESS();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "/deleteUserAuthority", method = RequestMethod.POST)
    public AjaxModel deleteUserAuthority(@RequestBody List<TblSystemDirectoryUserAuthority> tblSystemDirectoryUserAuthorities) {
        try {
            systemDirectoryAuthorityService.deleteDirectoryUserAuthority(tblSystemDirectoryUserAuthorities);
            return AjaxModel.SUCCESS();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "/addOrUpdatePostAuthority", method = RequestMethod.POST)
    public AjaxModel addOrUpdatePostAuthority(@RequestBody List<TblSystemDirectoryPostAuthority> tblSystemDirectoryPostAuthorities, HttpServletRequest request) {
        try {
            systemDirectoryAuthorityService.addOrUpdateDirectoryPostAuthority(tblSystemDirectoryPostAuthorities, request);
            return AjaxModel.SUCCESS();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "/deletePostAuthority", method = RequestMethod.POST)
    public AjaxModel deletePostAuthority(@RequestBody List<TblSystemDirectoryPostAuthority> tblSystemDirectoryPostAuthorities) {
        try {
            systemDirectoryAuthorityService.deleteDirectoryPostAuthority(tblSystemDirectoryPostAuthorities);
            return AjaxModel.SUCCESS();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxModel.FAIL(e);
        }
    }

    @RequestMapping(value = "/getCurrentUserDirectoryAuthority", method = RequestMethod.POST)
    public Map<String, Boolean> getCurrentUserDirectoryAuthority(HttpServletRequest request, Long projectId, Long directoryId) {
       return null;
    }

}
