package cn.pioneeruniverse.project.feignFallback.requirment;

import cn.pioneeruniverse.common.dto.TblCompanyInfoDTO;
import cn.pioneeruniverse.common.dto.TblDeptInfoDTO;
import cn.pioneeruniverse.project.feignInterface.requirment.ReqManageWebToSystemInterface;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description: projectManagemWeb模块请求system模块微服务接口熔断处理
 * @Date: Created in 11:11 2018/12/18
 * @Modified By:
 */
@Component
public class ReqManageWebToSystemFallback implements FallbackFactory<ReqManageWebToSystemInterface> {
    @Override
    public ReqManageWebToSystemInterface create(Throwable throwable) {
        return new ReqManageWebToSystemInterface() {
            @Override
            public List<TblDeptInfoDTO> getAllDeptInfo() {
                return null;
            }
            @Override
            public List<TblCompanyInfoDTO> getAllCompanyInfo() {
                return null;
            }
        };
    }
}
