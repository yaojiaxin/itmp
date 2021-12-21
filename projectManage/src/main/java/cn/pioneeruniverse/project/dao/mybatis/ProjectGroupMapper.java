package cn.pioneeruniverse.project.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.project.entity.TblProjectGroup;
import org.apache.ibatis.annotations.Param;

public interface ProjectGroupMapper {

    List<Long> findProjectIdsByProjectGroupIds(List<Long> ids);

    List<Long> findProjectGroupIdsByProjectId(Long id);

    List<TblProjectGroup> selectProjectGroups(Long id);

    void untyingProjectGroup(Long ud);

    void updateProjectGroup(HashMap<String, Object> map2);

    List<TblProjectGroup> getChildrenProjectGroup(Map<String, Object> map);

    List<TblProjectGroup> getChildren(String parentIds);

    List<TblProjectGroup> selectParentProjectGroups(Long id);

    void deletePeojectGroup(HashMap<String, Object> map);

    void editProjectGroup(TblProjectGroup tblProjectGroup);

    String selectParentIds(Long parent);

    void saveProjectGroup(TblProjectGroup tblProjectGroup);

    List<Map<String, Object>> findAllProjectGroupForZTree(@Param("projectId") Long projectId);

    List<Map<String, Object>> findAllProjectGroupBySystemIdForZTree(@Param("systemId") Long systemId);

    List<TblProjectGroup> findChildProjectGroups(Long projectGroupId);

    void saveTmpProjectGroup(TblProjectGroup tblProjectGroup);

    TblProjectGroup selectById(Long id);

}
