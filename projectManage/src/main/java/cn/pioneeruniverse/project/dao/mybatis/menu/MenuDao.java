package cn.pioneeruniverse.project.dao.mybatis.menu;

import cn.pioneeruniverse.project.entity.TblMenuButtonInfo;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

public interface MenuDao extends BaseMapper<TblMenuButtonInfo> {

    List<TblMenuButtonInfo> getMenusWithTYPE(Map<String, Object> map);

    List<TblMenuButtonInfo> getChildrenMenus(Map<String, Object> map);

    List<TblMenuButtonInfo> findRoleMenu(String parentIds);

    List<TblMenuButtonInfo> getButton(Map<String, Object> map);

    TblMenuButtonInfo getButtonByRid(Map<String, Object> map);

    List<TblMenuButtonInfo> selectMenuByParentId(Long id);
}
