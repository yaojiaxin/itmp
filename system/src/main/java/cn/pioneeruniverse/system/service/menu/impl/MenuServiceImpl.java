package cn.pioneeruniverse.system.service.menu.impl;

import java.util.*;
import java.util.regex.Pattern;

import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.system.entity.TblRoleInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import cn.pioneeruniverse.system.dao.mybatis.menu.MenuDao;
import cn.pioneeruniverse.system.entity.TblMenuButtonInfo;
import cn.pioneeruniverse.system.service.menu.IMenuService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;


@Service("iMenuService")
public class MenuServiceImpl extends ServiceImpl<MenuDao, TblMenuButtonInfo> implements IMenuService {

    @Autowired
    private MenuDao menuDao;
    @Autowired
    private RedisUtils redisUtils;
    private static Pattern urlPattern = Pattern.compile("[/[\\w]{1,}]{1,}(\\?.+?)*");

    @Override
    @Transactional(readOnly = true)
    public List<TblMenuButtonInfo> getUserMenu(Long userId) {
        return menuDao.getUserMenu(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<TblMenuButtonInfo> getUserAllMenuButton(Long userId) {
        return menuDao.getUserAllMenuButton(userId);
    }

    @Override
    public TblMenuButtonInfo selectMenuById(Long id) {
        return menuDao.selectMenuById(id);
    }

    @Override
    public List<TblMenuButtonInfo> selectMenuByParentId(Long id) {
        return menuDao.selectMenuByParentId(id);
    }

    /**
     * 添加一级菜单 或 下级菜单
     *
     * @param menu
     * @param request
     */
    @Override
    public void insertMenu(TblMenuButtonInfo menu, HttpServletRequest request) {
        if (menu.getId() != null) {
            menu.setParentId(menu.getId());
            if (menu.getParentIds() == null) {
                menu.setParentIds(menu.getId() + ",");
            } else {
                menu.setParentIds(menu.getParentIds() + menu.getId() + ",");
            }
        }
        if (menu.getMenuOrder() == null) {
            menu.setMenuOrder(1);
        }
        menu.setCreateBy(CommonUtil.getCurrentUserId(request));
        menu.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
        menuDao.insertMenu(menu);
        this.putAllMenuUrlToRedis();
    }

    /**
     * 置为无效有效操作
     *
     * @param menu
     * @param request
     */
    @Override
    @Transactional
    public void updateMenu(TblMenuButtonInfo menu, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("validStatus", menu.getValidStatus());
        map.put("lastUpdateBy", CommonUtil.getCurrentUserId(request));
        if (menu.getParentIds() == null) {
            menu.setParentIds(menu.getId() + ",");
        } else {
            menu.setParentIds(menu.getParentIds() + menu.getId() + ",");
        }
        List<TblMenuButtonInfo> menuButtonInfos = menuDao.getMenuByParentIds(menu);
        if (menuButtonInfos != null && menuButtonInfos.size() != 0) {
            for (TblMenuButtonInfo menuButtonInfo : menuButtonInfos) {
                map.put("id", menuButtonInfo.getId());
                menuDao.updateMenu(map);
            }
        }

    }

    /**
     * 删除菜单，有下级菜单也删除
     *
     * @param tblMenuButtonInfo
     */
    @Override
    @Transactional
    public void deleteMenu(TblMenuButtonInfo tblMenuButtonInfo, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", tblMenuButtonInfo.getStatus());
        map.put("validStatus", tblMenuButtonInfo.getValidStatus());
        map.put("lastUpdateBy", CommonUtil.getCurrentUserId(request));
        if (tblMenuButtonInfo.getParentIds() == null) {
            tblMenuButtonInfo.setParentIds(tblMenuButtonInfo.getId() + ",");
        } else {
            tblMenuButtonInfo.setParentIds(tblMenuButtonInfo.getParentIds() + tblMenuButtonInfo.getId() + ",");
        }
        List<TblMenuButtonInfo> menuButtonInfos = menuDao.getMenuByParentIds(tblMenuButtonInfo);
        if (menuButtonInfos != null && menuButtonInfos.size() != 0) {
            for (TblMenuButtonInfo menuButtonInfo : menuButtonInfos) {
                map.put("id", menuButtonInfo.getId());
                menuDao.updateMenu(map);
                // 删除菜单权限关联表
                menuDao.deleteRoleMenu(map);
            }
        }

        this.putAllMenuUrlToRedis();
    }

    /**
     * 修改当前行
     *
     * @param menu
     * @param request
     */
    @Override
    @Transactional
    public void updateThisMenu(TblMenuButtonInfo menu, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", menu.getId());
        map.put("menuButtonName", menu.getMenuButtonName());
        map.put("menuButtonType", menu.getMenuButtonType());
        map.put("menuButtonCode", menu.getMenuButtonCode());
        map.put("menuOrder", menu.getMenuOrder());
        map.put("url", menu.getUrl());
        map.put("css", menu.getCss());
        map.put("validStatus", menu.getValidStatus());
        map.put("lastUpdateBy", CommonUtil.getCurrentUserId(request));
        map.put("openStatus", menu.getOpenStatus());
        menuDao.updateMenu(map);

        if (menu.getUrl() != null) {
            this.putAllMenuUrlToRedis();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TblMenuButtonInfo getMenuButtonByCode(String menuButtonCode) {
        return menuDao.getMenuButtonByCode(menuButtonCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllMenuButtonUrl() {
        return menuDao.getAllMenuButtonUrl();
    }

    /**
     * 获取菜单管理列表
     *
     * @return
     */
    @Override
    public List<TblMenuButtonInfo> getListMenu() {
        Map<String, TblMenuButtonInfo> resultMap = new LinkedHashMap<String, TblMenuButtonInfo>();
        TblMenuButtonInfo menuButtonInfo = new TblMenuButtonInfo();
        List<TblMenuButtonInfo> menuList = menuDao.getMenus();
        resultMap = menuList(menuList, resultMap);

        List<TblMenuButtonInfo> menu_list = new ArrayList<TblMenuButtonInfo>();
        for (Map.Entry<String, TblMenuButtonInfo> entry : resultMap.entrySet()) {
            menuButtonInfo.setId(entry.getValue().getId());
            menuButtonInfo.setLoaded(true);
            menuButtonInfo.setExpanded(false);
            menuButtonInfo.setMenuButtonName(entry.getValue().getMenuButtonName());
            menuButtonInfo.setParent(entry.getValue().getParentId());

            String parentIds;
            if (entry.getValue().getParentIds() == null || entry.getValue().getParentIds().equals("")) {
                parentIds = entry.getValue().getId() + ",";
                menuButtonInfo.setLevel(0);
            } else {
                parentIds = entry.getValue().getParentIds() + entry.getValue().getId() + ",";
                String[] parendIdsArr = entry.getValue().getParentIds().split(",");
                menuButtonInfo.setLevel(parendIdsArr.length);
            }

            List<TblRoleInfo> childrenList = menuDao.getChildrenMenu(parentIds);
            if (childrenList != null && childrenList.size() != 0) {
                menuButtonInfo.setLeaf(false);
            } else {
                menuButtonInfo.setLeaf(true);
            }
            menuButtonInfo.setLoaded(true);
            menuButtonInfo.setExpanded(false);
            menuButtonInfo.setId(entry.getValue().getId());
            menuButtonInfo.setCss(entry.getValue().getCss());
            menuButtonInfo.setMenu(entry.getValue().getMenuButtonName());
            menuButtonInfo.setParent(entry.getValue().getParentId());
            menuButtonInfo.setParentId(entry.getValue().getParentId());
            menuButtonInfo.setParentIds(entry.getValue().getParentIds());
            menuButtonInfo.setMenuButtonType(entry.getValue().getMenuButtonType());
            menuButtonInfo.setMenuButtonCode(entry.getValue().getMenuButtonCode());
            menuButtonInfo.setUrl(entry.getValue().getUrl());
            menuButtonInfo.setStatus(entry.getValue().getStatus());
            menuButtonInfo.setValidStatus(entry.getValue().getValidStatus());
            menuButtonInfo.setMenuOrder(entry.getValue().getMenuOrder());
            menuButtonInfo.setOpenStatus(entry.getValue().getOpenStatus());
            menu_list.add(menuButtonInfo);
            menuButtonInfo = new TblMenuButtonInfo();
        }

        return menu_list;

    }

    public Map<String, TblMenuButtonInfo> menuList(List<TblMenuButtonInfo> menuList, Map<String, TblMenuButtonInfo> resultMap) {
        Map<String, Object> map = new HashMap<>();
        if (menuList != null && menuList.size() != 0) {
            for (TblMenuButtonInfo tblMenuButtonInfo : menuList) {
                if (tblMenuButtonInfo.getParentIds() != null) {
                    map.put("parentId", tblMenuButtonInfo.getParentIds() + tblMenuButtonInfo.getId() + ",");
                } else {
                    map.put("parentId", tblMenuButtonInfo.getId() + ",");
                }
                resultMap.put("menu_" + tblMenuButtonInfo.getId(), tblMenuButtonInfo);
                List<TblMenuButtonInfo> childrenMenus = menuDao.getChildrenAllMenus(map);
                menuList(childrenMenus, resultMap);
            }
        }
        return resultMap;
    }

    @Override
    public List<TblMenuButtonInfo> getMenusWithRole() {
        return menuDao.getMenusWithRole();
    }

    @Override
    public List<TblMenuButtonInfo> getAllMenu() {
        return menuDao.getAllMenu();
    }

    @Override
    public void putAllMenuUrlToRedis() {
        Set<String> allMenuUrlCollection = new HashSet<>();
        for (String url : getAllMenuButtonUrl()) {
            if (StringUtils.isNotBlank(url) && urlPattern.matcher(url.substring(2)).matches()) {
                allMenuUrlCollection.add(url.substring(2));
            }
        }
        redisUtils.set("allMenuUrl", allMenuUrlCollection);
    }

    @Override
    public TblMenuButtonInfo getMenuEntity(Long menuId) {
        return menuDao.getMenuEntity(menuId);
    }
}
