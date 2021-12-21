package test;

import base.BaseJunit;
import cn.pioneeruniverse.dev.dao.mybatis.TblDevTaskMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblRequirementFeatureMapper;
import cn.pioneeruniverse.dev.dao.mybatis.TblSystemScmMapper;
import cn.pioneeruniverse.dev.entity.TblSystemInfo;
import cn.pioneeruniverse.dev.entity.TblSystemScm;
import cn.pioneeruniverse.dev.service.devtask.DevTaskService;
import cn.pioneeruniverse.dev.service.systeminfo.ISystemInfoService;
import cn.pioneeruniverse.dev.service.worktask.WorkTaskService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 12:07 2019/1/17
 * @Modified By:
 */
public class Test extends BaseJunit {
    @Autowired
    private TblSystemScmMapper tblSystemScmMapper;

    @Autowired
    private WorkTaskService workTaskService;

    @Autowired
    private DevTaskService devTaskService;

    @Autowired
    private TblRequirementFeatureMapper tblRequirementFeatureMapper;

    @Autowired
    private TblDevTaskMapper tblDevTaskMapper;

    @Autowired
    private ISystemInfoService systemInfoService;

    @org.junit.Test
    public void test() {
        // Long id = tblRequirementFeatureMapper.getRequirementFeatureIdByDevTaskId(1L);
        //String a = "";
        TblSystemInfo systemInfo = systemInfoService.findById(1L);
        String b = "";




    }

}
