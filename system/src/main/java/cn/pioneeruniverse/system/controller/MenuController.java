package cn.pioneeruniverse.system.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.system.entity.TblMenuButtonInfo;
import cn.pioneeruniverse.system.service.menu.IMenuService;

import javax.servlet.http.HttpServletRequest;

/**
 * Description: menu controller
 * Author:liushan
 * Date: 2018/11/13 下午 4:00
 */
@RestController
@RequestMapping("menu")
public class MenuController {

    private static Logger logger = LoggerFactory.getLogger(MenuController.class);

    @Autowired
    private IMenuService iMenuService;


    @RequestMapping(value = "getMenuEntity", method = RequestMethod.POST)
    public Map<String, Object> getMenuEntity(Long menuId) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        if (menuId == null) {
            return this.handleException(new Exception(), "查询失败,参数为空");
        }
        try {
            TblMenuButtonInfo menuButtonInfo = iMenuService.getMenuEntity(menuId);
            result.put("menuInfo", menuButtonInfo);
        } catch (Exception e) {
            return this.handleException(e, "查询失败");
        }
        return result;
    }

    @RequestMapping(value = "deleteMenu", method = RequestMethod.POST)
    public Map<String, Object> deleteMenu(@RequestBody TblMenuButtonInfo tblMenuButtonInfo, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        if (tblMenuButtonInfo == null) {
            return this.handleException(new Exception(), "删除用户菜单失败,参数为空");
        } else {
            try {
                iMenuService.deleteMenu(tblMenuButtonInfo, request);
            } catch (Exception e) {
                return this.handleException(e, "删除用户菜单失败");
            }
        }
        return result;
    }

    @RequestMapping(value = "list", method = RequestMethod.POST)
    public Map<String, Object> listData() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            List<TblMenuButtonInfo> list = iMenuService.getListMenu();
            result.put("data", list);
        } catch (Exception e) {
            return this.handleException(e, "获取用户菜单失败");
        }
        return result;
    }

    @RequestMapping(value = "insertMenu", method = RequestMethod.POST)
    public Map<String, Object> insertMenu(@RequestBody TblMenuButtonInfo menu, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        if (menu == null) {
            return this.handleException(new Exception(), "新增菜单失败,参数为空");
        } else {
            try {
                iMenuService.insertMenu(menu, request);
            } catch (Exception e) {
                return this.handleException(e, "新增菜单失败");
            }
        }
        return result;
    }

    @RequestMapping(value = "updateThisMenu", method = RequestMethod.POST)
    public Map<String, Object> updateThisMenu(@RequestBody TblMenuButtonInfo menu, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        if (menu == null) {
            return this.handleException(new Exception(), "更新菜单失败,参数为空");
        } else {
            try {
                iMenuService.updateThisMenu(menu, request);
            } catch (Exception e) {
                return this.handleException(e, "更新菜单失败");
            }
        }

        return result;
    }

    @RequestMapping(value = "updateMenu", method = RequestMethod.POST)
    public Map<String, Object> updateMenu(@RequestBody TblMenuButtonInfo menu, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        if (menu == null) {
            return this.handleException(new Exception(), "更新菜单失败,参数为空");
        } else {
            try {
                iMenuService.updateMenu(menu, request);
            } catch (Exception e) {
                return this.handleException(e, "更新菜单失败");
            }
        }

        return result;
    }


    @RequestMapping(value = "getUserMenu", method = RequestMethod.POST)
    public List<TblMenuButtonInfo> getUserMenu(Long userId) {
        try {
            return iMenuService.getUserMenu(userId);
        } catch (Exception e) {
            logger.error("查询用户菜单异常，异常原因：" + e.getMessage(), e);
            return null;
        }
    }


    @RequestMapping(value = "getAllMenu", method = RequestMethod.POST)
    public Map<String, Object> getAllMenu() {

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);

        try {
            List<TblMenuButtonInfo> list = iMenuService.getAllMenu();
            result.put("data", JSONObject.toJSONString(list));
        } catch (Exception e) {
            return this.handleException(e, "获取用户菜单失败");
        }
        return result;

    }

    @RequestMapping(value = "selectMenuById", method = RequestMethod.POST)
    public Map<String, Object> selectMenuById(Long id) {

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            TblMenuButtonInfo menu = iMenuService.selectMenuById(id);
            result.put("data", JSONObject.toJSONString(menu));
        } catch (Exception e) {
            return this.handleException(e, "获取菜单详情失败");
        }
        return result;
    }


    @RequestMapping(value = "selectMenuByParentId", method = RequestMethod.POST)
    public Map<String, Object> selectMenuByParentId(Long id) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            List<TblMenuButtonInfo> list = iMenuService.selectMenuByParentId(id);
            result.put("data", JSONObject.toJSONString(list));
        } catch (Exception e) {
            return this.handleException(e, "获取子菜单失败");
        }
        return result;
    }

    @RequestMapping(value = "getMenusWithRole", method = RequestMethod.POST)
    public Map<String, Object> getMenusWithRole() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            List<TblMenuButtonInfo> list = iMenuService.getMenusWithRole();
            result.put("data", JSONObject.toJSONString(list));
        } catch (Exception e) {
            return this.handleException(e, "获取所有菜单失败");
        }
        return result;
    }

    @RequestMapping(value = "getMenuByCode", method = RequestMethod.POST)
    public TblMenuButtonInfo getMenuByCode(String menuButtonCode) {
        return iMenuService.getMenuButtonByCode(menuButtonCode);
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
