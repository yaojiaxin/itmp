package cn.pioneeruniverse.system.service.menu;

import java.util.List;

import cn.pioneeruniverse.system.entity.TblMenuButtonInfo;

import javax.servlet.http.HttpServletRequest;

public interface IMenuService {

    List<TblMenuButtonInfo> getUserMenu(Long userId);

    List<TblMenuButtonInfo> getUserAllMenuButton(Long userId);

    TblMenuButtonInfo selectMenuById(Long id);

    List<TblMenuButtonInfo> selectMenuByParentId(Long id);

    void insertMenu(TblMenuButtonInfo menu, HttpServletRequest request);

    void updateMenu(TblMenuButtonInfo menu, HttpServletRequest request);

    List<TblMenuButtonInfo> getMenusWithRole();

    List<TblMenuButtonInfo> getAllMenu();

    List<TblMenuButtonInfo> getListMenu();

    void deleteMenu(TblMenuButtonInfo tblMenuButtonInfo, HttpServletRequest request);

    void updateThisMenu(TblMenuButtonInfo menu, HttpServletRequest request);

    /* void insertChildrenMenu(TblMenuButtonInfo menu);*/
    TblMenuButtonInfo getMenuButtonByCode(String menuButtonCode);

    List<String> getAllMenuButtonUrl();

    void putAllMenuUrlToRedis();

    TblMenuButtonInfo getMenuEntity(Long menuId);
}
