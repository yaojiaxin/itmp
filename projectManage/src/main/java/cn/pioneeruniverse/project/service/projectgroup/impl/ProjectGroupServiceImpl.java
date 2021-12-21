package cn.pioneeruniverse.project.service.projectgroup.impl;

import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.project.entity.TblProjectGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.pioneeruniverse.project.dao.mybatis.ProjectGroupMapper;
import cn.pioneeruniverse.project.service.projectgroup.ProjectGroupService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectGroupServiceImpl implements ProjectGroupService {

    @Autowired
    private ProjectGroupMapper projectGroupMapper;

    @Override
    public List<Long> findProjectIdsByProjectGroupIds(List<Long> ids) {
        // TODO Auto-generated method stub
        return projectGroupMapper.findProjectIdsByProjectGroupIds(ids);

    }

    @Override
    public List<Long> findProjectGroupIdsByProjectId(Long id) {
        // TODO Auto-generated method stub
        return projectGroupMapper.findProjectGroupIdsByProjectId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProjectGroupTree(Long projectId) {
        return projectGroupMapper.findAllProjectGroupForZTree(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getProjectGroupTreeBySystemId(Long systemId) {
        return projectGroupMapper.findAllProjectGroupBySystemIdForZTree(systemId);
    }

    @Override
    @Transactional(readOnly = true)
    public TblProjectGroup getProjectGroupByProjectGroupId(Long projectGroupId) {
        return projectGroupMapper.selectById(projectGroupId);
    }

}
