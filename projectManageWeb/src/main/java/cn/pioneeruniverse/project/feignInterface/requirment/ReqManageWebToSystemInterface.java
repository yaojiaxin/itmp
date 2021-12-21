package cn.pioneeruniverse.project.feignInterface.requirment;

import cn.pioneeruniverse.common.dto.TblCompanyInfoDTO;
import cn.pioneeruniverse.common.dto.TblDeptInfoDTO;
import cn.pioneeruniverse.project.feignFallback.requirment.ReqManageWebToSystemFallback;
import cn.pioneeruniverse.project.feignInterface.requirment.ReqManageWebToSystemInterface;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description: projectManagemWeb模块请求system模块微服务接口
 * @Date: Created in 11:06 2018/12/18
 * @Modified By:
 */
@FeignClient(value = "system", fallbackFactory = ReqManageWebToSystemFallback.class)
public interface ReqManageWebToSystemInterface {

    /**
     * @param
     * @return java.util.List<cn.pioneeruniverse.common.dto.TblDeptInfoDTO>
     * @Description 获取全部部门
     * @MethodName getDeptList
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2018/12/18 11:17
     */
	@RequestMapping(value = "dept/getAllDeptInfo", method = RequestMethod.POST)
    List<TblDeptInfoDTO> getAllDeptInfo();

    @RequestMapping(value = "company/getAllCompanyInfo", method = RequestMethod.POST)
    List<TblCompanyInfoDTO> getAllCompanyInfo();


}
