package cn.pioneeruniverse.project.velocity.tag;

import cn.pioneeruniverse.common.dto.TblCompanyInfoDTO;
import cn.pioneeruniverse.common.dto.TblDeptInfoDTO;
import cn.pioneeruniverse.common.utils.SpringContextHolder;
import cn.pioneeruniverse.project.feignInterface.requirment.ReqManageWebToSystemInterface;

import java.util.List;

public class VelocitySystem {

    private static ReqManageWebToSystemInterface reqManageWebToSystemInterface = SpringContextHolder.getBean(ReqManageWebToSystemInterface.class);

    public List<TblDeptInfoDTO> getDept() {
        return reqManageWebToSystemInterface.getAllDeptInfo();
    }

    public List<TblCompanyInfoDTO> getCompany() {
        return reqManageWebToSystemInterface.getAllCompanyInfo();
    }

}
