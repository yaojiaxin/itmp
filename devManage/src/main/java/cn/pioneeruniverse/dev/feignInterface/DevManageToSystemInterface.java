package cn.pioneeruniverse.dev.feignInterface;

import cn.pioneeruniverse.common.dto.TblUserInfoDTO;
import cn.pioneeruniverse.dev.feignFallback.DevManageToSystemFallback;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 15:52 2018/12/14
 * @Modified By:
 */
@FeignClient(value = "system", fallbackFactory = DevManageToSystemFallback.class)
public interface DevManageToSystemInterface {

    @RequestMapping(value = "/user/getCodeBaseUsersDetail", method = RequestMethod.POST)
    List<TblUserInfoDTO> getCodeBaseUsersDetail(@RequestParam("svnUserAuth") Map<String, String> svnUserAuth);

    @RequestMapping(value = "/user/getCodeBaseUserDetailByUserScmAccount", method = RequestMethod.POST)
    TblUserInfoDTO getCodeBaseUserDetailByUserScmAccount(@RequestParam("userScmAccount") String userScmAccount);

    @RequestMapping(value = "/user/getDevUserNameAndReviewersNameForCodeReivew", method = RequestMethod.POST)
    Map<String, String> getDevUserNameAndReviewersNameForCodeReivew(@RequestParam("devUserId") Long devUserId, @RequestParam("codeReviewUserIds") String codeReviewUserIds);

    @RequestMapping(value = "user/findUserById", method = RequestMethod.POST)
    Map<String, Object> findUserById(@RequestParam("userId") Long userId);

    @RequestMapping(value = "dept/selectDeptById", method = RequestMethod.POST)
    Map<String, Object> selectDeptById(@RequestParam("id") Long id);

    @RequestMapping(value = "role/findRolesByUserID", method = RequestMethod.POST)
    Map<String, Object> findRolesByUserID(Long currentUserId);

    @RequestMapping(value = "/fieldTemplate/findFieldByTableName", method = RequestMethod.POST)
    Map<String, Object> findFieldByTableName(@RequestParam("tableName") String tableName);

    @RequestMapping(value = "/user/createSvnAccountPassword", method = RequestMethod.POST)
    Map<String, String> createSvnAccountPassword(@RequestParam("userId") Long userId);

    @RequestMapping(value = "/message/insertMessage", method = RequestMethod.POST)
    Map<String, String> insertMessage(@RequestParam("messageJson") String messageJson);
    
    @RequestMapping(value = "/message/sendMessage", method = RequestMethod.POST)
    Map<String, String> sendMessage(@RequestParam("messageJson") String messageJson);

}
