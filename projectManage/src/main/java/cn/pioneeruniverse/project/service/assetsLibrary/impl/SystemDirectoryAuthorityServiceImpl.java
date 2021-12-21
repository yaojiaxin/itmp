package cn.pioneeruniverse.project.service.assetsLibrary.impl;

import cn.pioneeruniverse.common.entity.BootStrapTablePage;
import cn.pioneeruniverse.common.utils.CollectionUtil;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.project.dao.mybatis.assetLibrary.SystemDirectoryPostAuthorityDao;
import cn.pioneeruniverse.project.dao.mybatis.assetLibrary.SystemDirectoryUserAuthorityDao;
import cn.pioneeruniverse.project.entity.TblSystemDirectoryPostAuthority;
import cn.pioneeruniverse.project.entity.TblSystemDirectoryUserAuthority;
import cn.pioneeruniverse.project.feignInterface.ProjectToSystemInterface;
import cn.pioneeruniverse.project.service.assetsLibrary.SystemDirectoryAuthorityService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 13:55 2019/12/5
 * @Modified By:
 */
@Service("SystemDirectoryAuthorityService")
public class SystemDirectoryAuthorityServiceImpl implements SystemDirectoryAuthorityService {

    @Autowired
    SystemDirectoryUserAuthorityDao systemDirectoryUserAuthorityDao;

    @Autowired
    SystemDirectoryPostAuthorityDao systemDirectoryPostAuthorityDao;

    @Autowired
    ProjectToSystemInterface projectToSystemInterface;

    @Autowired
    RedisUtils redisUtils;

    /**
     * @param bootStrapTablePage
     * @param directoryId
     * @return cn.pioneeruniverse.common.entity.BootStrapTablePage<cn.pioneeruniverse.project.entity.TblSystemDirectoryUserAuthority>
     * @Description 查询文档库权限配置人员列表
     * @MethodName getDirectoryUserAuthority
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/12/5 15:45
     */
    @Override
    @Transactional(readOnly = true)
    public BootStrapTablePage<TblSystemDirectoryUserAuthority> getDirectoryUserAuthority(BootStrapTablePage<TblSystemDirectoryUserAuthority> bootStrapTablePage, Long directoryId) {
        List<TblSystemDirectoryUserAuthority> list = systemDirectoryUserAuthorityDao.selectList(new EntityWrapper<TblSystemDirectoryUserAuthority>().allEq(new HashMap<String, Object>() {{
            put("SYSTEM_DIRECTORY_ID", directoryId);
            put("STATUS", 1);
        }}));
        if (CollectionUtil.isNotEmpty(list)) {
            List<Long> userIds = (List<Long>) CollectionUtil.collect(list, o -> ((TblSystemDirectoryUserAuthority) o).getUserId());
            List<Map<String, Object>> userInfo = projectToSystemInterface.getUserInfoByUserIds(userIds);
            bootStrapTablePage.setOtherData(userInfo);
        }
        bootStrapTablePage.setRows(list);
        bootStrapTablePage.setTotal(list.size());
        return bootStrapTablePage;
    }

    @Override
    @Transactional(readOnly = true)
    public BootStrapTablePage<TblSystemDirectoryPostAuthority> getDirectoryPostAuthority(BootStrapTablePage<TblSystemDirectoryPostAuthority> bootStrapTablePage, Long directoryId) {
        List<TblSystemDirectoryPostAuthority> list = systemDirectoryPostAuthorityDao.selectList(new EntityWrapper<TblSystemDirectoryPostAuthority>().allEq(new HashMap<String, Object>() {{
            put("SYSTEM_DIRECTORY_ID", directoryId);
            put("STATUS", 1);
        }}));
        bootStrapTablePage.setOtherData(JsonUtil.fromJson((String) redisUtils.get("TBL_PROJECT_GROUP_USER_USER_POST"), Map.class));
        bootStrapTablePage.setRows(list);
        bootStrapTablePage.setTotal(list.size());
        return bootStrapTablePage;
    }

    @Override
    @Transactional(readOnly = false)
    public void addOrUpdateDirectoryUserAuthority(List<TblSystemDirectoryUserAuthority> tblSystemDirectoryUserAuthorities, HttpServletRequest request) {
        for (TblSystemDirectoryUserAuthority tblSystemDirectoryUserAuthority : tblSystemDirectoryUserAuthorities) {
            tblSystemDirectoryUserAuthority.preInsertOrUpdate(request);
            if (tblSystemDirectoryUserAuthority.getId() != null) {
                systemDirectoryUserAuthorityDao.update(tblSystemDirectoryUserAuthority, new EntityWrapper<TblSystemDirectoryUserAuthority>().allEq(new HashMap<String, Object>() {{
                    put("ID", tblSystemDirectoryUserAuthority.getId());
                    put("SYSTEM_DIRECTORY_ID", tblSystemDirectoryUserAuthority.getSystemDirectoryId());
                    put("USER_ID", tblSystemDirectoryUserAuthority.getUserId());
                    put("STATUS", 1);
                }}));
            } else {
                systemDirectoryUserAuthorityDao.insert(tblSystemDirectoryUserAuthority);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteDirectoryUserAuthority(List<TblSystemDirectoryUserAuthority> tblSystemDirectoryUserAuthorities) {
        for (TblSystemDirectoryUserAuthority tblSystemDirectoryUserAuthority : tblSystemDirectoryUserAuthorities) {
            systemDirectoryUserAuthorityDao.delete(new EntityWrapper<TblSystemDirectoryUserAuthority>().allEq(new HashMap<String, Object>() {{
                put("ID", tblSystemDirectoryUserAuthority.getId());
                put("SYSTEM_DIRECTORY_ID", tblSystemDirectoryUserAuthority.getSystemDirectoryId());
                put("USER_ID", tblSystemDirectoryUserAuthority.getUserId());
            }}));
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void addOrUpdateDirectoryPostAuthority(List<TblSystemDirectoryPostAuthority> tblSystemDirectoryPostAuthorities, HttpServletRequest request) {
        for (TblSystemDirectoryPostAuthority tblSystemDirectoryPostAuthority : tblSystemDirectoryPostAuthorities) {
            tblSystemDirectoryPostAuthority.preInsertOrUpdate(request);
            if (tblSystemDirectoryPostAuthority.getId() != null) {
                systemDirectoryPostAuthorityDao.update(tblSystemDirectoryPostAuthority, new EntityWrapper<TblSystemDirectoryPostAuthority>().allEq(new HashMap<String, Object>() {{
                    put("ID", tblSystemDirectoryPostAuthority.getId());
                    put("SYSTEM_DIRECTORY_ID", tblSystemDirectoryPostAuthority.getSystemDirectoryId());
                    put("USER_POST", tblSystemDirectoryPostAuthority.getUserPost());
                    put("STATUS", 1);
                }}));
            } else {
                systemDirectoryPostAuthorityDao.insert(tblSystemDirectoryPostAuthority);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteDirectoryPostAuthority(List<TblSystemDirectoryPostAuthority> tblSystemDirectoryPostAuthorities) {
        for (TblSystemDirectoryPostAuthority tblSystemDirectoryPostAuthority : tblSystemDirectoryPostAuthorities) {
            systemDirectoryPostAuthorityDao.delete(new EntityWrapper<TblSystemDirectoryPostAuthority>().allEq(new HashMap<String, Object>() {{
                put("ID", tblSystemDirectoryPostAuthority.getId());
                put("SYSTEM_DIRECTORY_ID", tblSystemDirectoryPostAuthority.getSystemDirectoryId());
                put("USER_POST", tblSystemDirectoryPostAuthority.getUserPost());
            }}));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Boolean> getCurrentUserDirectoryAuthority(HttpServletRequest request, Long projectId, Long directoryId) {
        int readAuth = 0;
        int writeAuth = 0;
        Map<Integer, Integer> map = new HashMap<Integer, Integer>() {{
            put(1, 1);
            put(2, 0);
        }};
        Long userId = (Long) CommonUtil.getCurrentUser(request).get("id");
        TblSystemDirectoryUserAuthority tblSystemDirectoryUserAuthority = new TblSystemDirectoryUserAuthority();
        tblSystemDirectoryUserAuthority.setSystemDirectoryId(directoryId);
        tblSystemDirectoryUserAuthority.setUserId(userId);
        tblSystemDirectoryUserAuthority = systemDirectoryUserAuthorityDao.selectOne(tblSystemDirectoryUserAuthority);
        readAuth |= (tblSystemDirectoryUserAuthority.getReadAuth() == null ? 0 : (map.get(tblSystemDirectoryUserAuthority.getReadAuth()) == null ? 0 : map.get(tblSystemDirectoryUserAuthority.getReadAuth())));
        writeAuth |= (tblSystemDirectoryUserAuthority.getWriteAuth() == null ? 0 : (map.get(tblSystemDirectoryUserAuthority.getWriteAuth()) == null ? 0 : map.get(tblSystemDirectoryUserAuthority.getWriteAuth())));

        return null;
    }
}
