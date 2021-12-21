package cn.pioneeruniverse.project.service.projectgroup;

import cn.pioneeruniverse.project.entity.TblProjectGroup;

import java.util.List;
import java.util.Map;

public interface ProjectGroupService {

    List<Long> findProjectIdsByProjectGroupIds(List<Long> ids);

    List<Long> findProjectGroupIdsByProjectId(Long id);

    List<Map<String, Object>> getProjectGroupTree(Long projectId);

    List<Map<String, Object>> getProjectGroupTreeBySystemId(Long systemId);

    TblProjectGroup getProjectGroupByProjectGroupId(Long projectGroupId);

}
