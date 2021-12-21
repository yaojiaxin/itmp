package cn.pioneeruniverse.dev.service.dbconfig.impl;

import cn.pioneeruniverse.common.entity.BaseEntity;
import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.CommonUtils;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.dev.dao.mybatis.TblSystemDbConfigMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblSystemInfoMapper;
import cn.pioneeruniverse.dev.entity.TblSystemDbConfig;
import cn.pioneeruniverse.dev.entity.TblSystemDbConfigVo;
import cn.pioneeruniverse.dev.entity.TblSystemInfo;
import cn.pioneeruniverse.dev.entity.TblSystemModule;
import cn.pioneeruniverse.dev.service.dbconfig.IDbConfigService;
import cn.pioneeruniverse.dev.service.structure.IStructureService;
import cn.pioneeruniverse.dev.service.systeminfo.ISystemInfoService;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Service("iDbConfigService")
public class DbConfigServiceImpl implements IDbConfigService {
    @Autowired
    private TblSystemInfoMapper tblSystemInfoMapper;
    @Autowired
    private IStructureService structureService;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private TblSystemDbConfigMapper tblSystemDbConfigMapper;
    @Override
    public List<TblSystemInfo> getSystemByUserId(HttpServletRequest request) {
        Long userId = CommonUtil.getCurrentUserId(request);
        LinkedHashMap codeMap = (LinkedHashMap) redisUtils.get(CommonUtil.getToken(request));
        List<String> roleCodes = (List<String>) codeMap.get("roles");
        if (roleCodes!=null && roleCodes.contains("XITONGGUANLIYUAN")) {//当前登录用户的角色包含系统管理员  查询所有
            Map<String, Object> map = new HashMap<>();
            map.put("status", 1);
            return tblSystemInfoMapper.selectByMap(map);
        }else {
            return tblSystemInfoMapper.getSystemsByUserId(userId);
        }

    }

    @Override
    public Map<String, Object> getEnvAndModuleBySystemId(long systemId) {
        Map<String, Object> result=new HashMap<>();
        List<Map<String, Object>> list = new ArrayList<>();
        Object tblToolInfo = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
        Map<String, Object> envMap = JSON.parseObject(tblToolInfo.toString());
        List<TblSystemModule> modules = structureService.selectSystemModule(systemId);
        String env = tblSystemInfoMapper.getEnvBySystemId(systemId);
        if(env!=null) {
            for (String envType : env.split(",")) {
                Map<String, Object> map = new HashMap<>();
                map.put("envVaule", envType);
                map.put("envName", envMap.get(envType));
                list.add(map);
            }
        }
        result.put("envList",list);
        result.put("modules",modules);
        return result;

    }

    @Override
    public void addDbConfig(TblSystemDbConfig tblSystemDbConfig) {
        tblSystemDbConfigMapper.insertSelective(tblSystemDbConfig);
    }

    @Override
    public JqGridPage<TblSystemDbConfigVo> findDbConfigListPage(JqGridPage<TblSystemDbConfigVo> jqGridPage, TblSystemDbConfigVo tblSystemDbConfigVo,HttpServletRequest request) {


        try {
         Object redisEnvType = redisUtils.get("TBL_SYSTEM_SCM_ENVIRONMENT_TYPE");
         Map<String, Object> envMap = JSON.parseObject(redisEnvType.toString());
        jqGridPage.filtersAttrToEntityField(tblSystemDbConfigVo);
         List<TblSystemDbConfigVo> lists=new ArrayList<>();
        PageHelper.startPage(jqGridPage.getJqGridPrmNames().getPage(), jqGridPage.getJqGridPrmNames().getRows());

        if(new CommonUtils().currentUserWithAdmin(request)==true) {
           lists = tblSystemDbConfigMapper.findDbConfigListPage(tblSystemDbConfigVo);
        }else{
            tblSystemDbConfigVo.setUserId(CommonUtil.getCurrentUserId(request));
            lists = tblSystemDbConfigMapper.findDbConfigListPageByUserId(tblSystemDbConfigVo);
        }
        for(TblSystemDbConfigVo tblSystemDbConfig:lists){
            if(tblSystemDbConfig.getEnvironmentType()!=null){
                tblSystemDbConfig.setEnvironmentTypeName(envMap.get(tblSystemDbConfig.getEnvironmentType().toString()).toString());
            }
        }
        PageInfo<TblSystemDbConfigVo> pageInfo = new PageInfo<>(lists);
        jqGridPage.processDataForResponse(pageInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jqGridPage;
    }

    @Override
    public void deleteDbConfig(long id) {
        TblSystemDbConfig tblSystemDbConfig=  tblSystemDbConfigMapper.selectById(id);
        tblSystemDbConfig.setStatus(2);
        tblSystemDbConfigMapper.updateById(tblSystemDbConfig);
    }

    @Override
    public void updateDbConfig(TblSystemDbConfig tblSystemDbConfig) {
        tblSystemDbConfigMapper.updateById(tblSystemDbConfig);
    }


}
