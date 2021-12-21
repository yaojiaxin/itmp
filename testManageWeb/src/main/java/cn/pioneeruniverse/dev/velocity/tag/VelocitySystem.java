package cn.pioneeruniverse.dev.velocity.tag;

import cn.pioneeruniverse.common.dto.TblCompanyInfoDTO;
import cn.pioneeruniverse.common.dto.TblDataDicDTO;
import cn.pioneeruniverse.common.dto.TblDeptInfoDTO;
import cn.pioneeruniverse.common.utils.SpringContextHolder;
import cn.pioneeruniverse.dev.entity.TblDeptInfo;
import cn.pioneeruniverse.dev.feignInterface.TestManageWebToSystemInterface;
import cn.pioneeruniverse.dev.feignInterface.devtask.UserInterface;

import java.util.List;
import java.util.Map;

/**
 *  类说明 
* @author:tingting
* @version:2019年3月8日 下午3:22:21
 * 
 */
public class VelocitySystem {

    private static TestManageWebToSystemInterface testManageWebToSystemInterface = SpringContextHolder.getBean(TestManageWebToSystemInterface.class);

    public Map<String, Object> getMenuByCode(String code) {
        return testManageWebToSystemInterface.getMenuByCode(code);
    }

}
