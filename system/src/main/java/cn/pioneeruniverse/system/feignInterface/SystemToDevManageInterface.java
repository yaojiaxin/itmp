package cn.pioneeruniverse.system.feignInterface;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 18:06 2019/4/29
 * @Modified By:
 */

import cn.pioneeruniverse.common.dto.ResultDataDTO;
import cn.pioneeruniverse.system.feignFallback.SystemToDevManageFallback;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "devManage", fallbackFactory = SystemToDevManageFallback.class)
public interface SystemToDevManageInterface {

    @RequestMapping(value = "version/modifySvnPassword", method = RequestMethod.POST)
    ResultDataDTO modifySvnPassword(@RequestParam("currentUserId") Long currentUserId, @RequestParam("userScmAccount") String userScmAccount,
                                    @RequestParam("userScmPassword") String userScmPassword, @RequestParam("entryptUserScmPassword") String entryptUserScmPassword);
}
