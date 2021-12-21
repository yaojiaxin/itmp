package cn.pioneeruniverse.system.feignFallback;

import cn.pioneeruniverse.common.dto.ResultDataDTO;
import cn.pioneeruniverse.system.feignInterface.SystemToDevManageInterface;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 18:07 2019/4/29
 * @Modified By:
 */
@Component
public class SystemToDevManageFallback implements FallbackFactory<SystemToDevManageInterface> {

    private static final Logger logger = LoggerFactory.getLogger(SystemToDevManageFallback.class);

    @Override
    public SystemToDevManageInterface create(Throwable throwable) {
        return new SystemToDevManageInterface() {
            @Override
            public ResultDataDTO modifySvnPassword(Long currentUserId, String userScmAccount, String userScmPassword, String entryptUserScmPassword) {
                return ResultDataDTO.ABNORMAL("修改SVN密码配置文件接口异常，异常原因：" + throwable.getMessage());
            }
        };
    }
}
