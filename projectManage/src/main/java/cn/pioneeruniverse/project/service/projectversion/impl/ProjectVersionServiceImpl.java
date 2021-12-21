package cn.pioneeruniverse.project.service.projectversion.impl;

import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.project.dao.mybatis.ProjectVersionMapper;
import cn.pioneeruniverse.project.entity.TblSystemInfo;
import cn.pioneeruniverse.project.entity.TblSystemVersion;
import cn.pioneeruniverse.project.service.projectversion.IProjectVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ProjectVersionServiceImpl implements IProjectVersionService {

    @Autowired
    private ProjectVersionMapper projectVersionMapper;

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> selectSystemByProjectId(Long projectId) {
        Map<String, Object> map = new HashMap<>();
        List<TblSystemInfo> allSystemList = projectVersionMapper.selectSystemByProjectId(projectId);
        map.put("allSystemList",allSystemList);
        return map;
    }

    /**
     * 根据条件查询所有版本
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProjectVersionByCon(Long projectId) {
        Map<String, Object> map = new HashMap<>();
        List<TblSystemVersion> systemVersions = projectVersionMapper.getProjectVersionByCon(projectId);
        map.put("rows", systemVersions);
        return map;
    }

    /**
     * 添加版本
     */
    @Override
    @Transactional(readOnly = false)
    public void insertVersion(TblSystemVersion systemVersion, HttpServletRequest request) {
        String [] systemIds = systemVersion.getSystemIds().split(",");
        for (String systemId : systemIds){
            systemVersion.setSystemId(Long.valueOf(systemId));
            List<TblSystemVersion> systemList = projectVersionMapper.getSystemVersionByVersion(systemVersion);
            if(systemList == null||systemList.size()<=0){
                CommonUtil.setBaseValue(systemVersion, request);
                projectVersionMapper.insertVersion(systemVersion);
            }
        }
    }

    /**
     * 获取修改信息
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getProjectVersionBySystemVersion(TblSystemVersion systemVersion){
        Map<String, Object> map = new HashMap<>();
        List<TblSystemInfo> allSystemList = projectVersionMapper.selectSystemByProjectId(systemVersion.getProjectId());
        List<TblSystemVersion> systemList = projectVersionMapper.getProjectVersionByVersion(systemVersion);
        map.put("systemList", systemList);
        map.put("allSystemList", allSystemList);
        return map;
    }
    /**
     * 修改版本
     */
    @Override
    @Transactional(readOnly = false)
    public void updateVersion(TblSystemVersion systemVersion, HttpServletRequest request) {

        List<TblSystemVersion> beforeUpdate = projectVersionMapper.getBeforeUpdate(systemVersion.getIds());
        systemVersion.setSystemId(Long.valueOf(4));
        projectVersionMapper.deleteVersion(systemVersion.getIds());
        String [] systemArr = systemVersion.getSystemIds().split(",");
        for(String systemId : systemArr){
            systemVersion.setSystemId(Long.valueOf(systemId));
            List<TblSystemVersion> systemList = projectVersionMapper.getSystemVersionByVersion(systemVersion);
            if(systemList == null||systemList.size()<=0){
                Integer status = getStatus(beforeUpdate,systemId);
                systemVersion.setStatus(status);
                CommonUtil.setBaseValue(systemVersion, request);
                projectVersionMapper.insertVersion(systemVersion);
            }
        }
    }
    @Override
    @Transactional(readOnly = false)
    public void closeOrOpenSystemVersion(TblSystemVersion systemVersion, HttpServletRequest request){
        String [] ids = systemVersion.getIds().split(",");
        for (String id:ids){
            TblSystemVersion systemVersion1 = new TblSystemVersion();
            systemVersion1.setId(Long.valueOf(id));
            systemVersion1.setStatus(systemVersion.getStatus());
            systemVersion1.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
            systemVersion1.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
            projectVersionMapper.updateStatusById(systemVersion1);
        }
    }

   private Integer getStatus(List<TblSystemVersion> beforeUpdate,String systemId){
        Integer status = 1;
       for (TblSystemVersion version:beforeUpdate){
           if(version.getSystemId().toString().equals(systemId)){
               status = version.getStatus();
           }
       }
       return status;
   }
}
