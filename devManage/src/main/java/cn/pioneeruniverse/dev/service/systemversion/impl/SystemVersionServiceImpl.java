package cn.pioneeruniverse.dev.service.systemversion.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.dev.dao.mybatis.TblSystemVersionMapper;
import cn.pioneeruniverse.dev.entity.TblSystemVersion;
import cn.pioneeruniverse.dev.service.systemversion.ISystemVersionService;

@Service
@Transactional(readOnly = true)
public class SystemVersionServiceImpl implements ISystemVersionService {

    @Autowired
    private TblSystemVersionMapper systemVersionMapper;


    /**
     * 添加版本
     */
    @Override
    @Transactional(readOnly = false)
    public Integer insertVersion(TblSystemVersion systemVersion,HttpServletRequest request) {
        Integer succ = 0;
        List<TblSystemVersion> systemList = systemVersionMapper.getSystemVersionByVersion(systemVersion);
        if(systemList==null||systemList.size()<=0){
            CommonUtil.setBaseValue(systemVersion, request);
            systemVersionMapper.insertVersion(systemVersion);
        }else{
            succ = 1;
        }
        return succ;
    }

    @Override
    @Transactional(readOnly = true)
    public TblSystemVersion getSystemVersionBySystemVersionId(Long systemVersionId) {
        return systemVersionMapper.selectByPrimaryKey(systemVersionId);
    }

    /**
     * 修改版本
     */
    @Override
    @Transactional(readOnly = false)
    public Integer updateVersion(TblSystemVersion systemVersion, HttpServletRequest request) {
        Integer succ = 0;
        TblSystemVersion systemVersion1 = systemVersionMapper.selectByPrimaryKey(systemVersion.getId());
        if(systemVersion.getGroupFlag().equals(systemVersion1.getGroupFlag())&&
                systemVersion.getVersion().equals(systemVersion1.getVersion())){

        }else {
            systemVersion.setSystemId(systemVersion1.getSystemId());
            List<TblSystemVersion> systemList = systemVersionMapper.getSystemVersionByVersion(systemVersion);
            if(systemList==null||systemList.size()<=0){
                systemVersion.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
                systemVersion.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
                systemVersionMapper.updateByPrimaryKeySelective(systemVersion);
            }else{
                succ = 1;
            }
        }
        return succ;
    }
    /**
     * 关闭或者开启版本
     */
    @Override
    @Transactional(readOnly = false)
    public Integer closeOrOpenSystemVersion (TblSystemVersion systemVersion, HttpServletRequest request){
        systemVersion.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
        systemVersion.setLastUpdateDate(new Timestamp(System.currentTimeMillis()));
        return systemVersionMapper.updateByPrimaryKeySelective(systemVersion);
    }
    /**
     * 根据条件查询所有版本
     */
    @Override
    public Map<String, Object> getSystemVersionByCon(Long systemId, Integer status) {
        Map<String, Object> map = new HashMap<>();
        TblSystemVersion systemVersion = new TblSystemVersion();
        systemVersion.setSystemId(systemId);
        systemVersion.setStatus(status);
        List<TblSystemVersion> systemVersions = systemVersionMapper.getSystemVersionByCon(systemVersion);
        map.put("rows", systemVersions);
        return map;
    }


}
