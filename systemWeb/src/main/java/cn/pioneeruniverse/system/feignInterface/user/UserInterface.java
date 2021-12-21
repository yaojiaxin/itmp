package cn.pioneeruniverse.system.feignInterface.user;

import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.common.dto.ResultDataDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import cn.pioneeruniverse.system.feignFallback.user.UserFallback;
import cn.pioneeruniverse.system.vo.company.Company;
import cn.pioneeruniverse.system.vo.dept.TblDeptInfo;
import cn.pioneeruniverse.system.vo.user.TblUserInfo;

@FeignClient(value = "system", fallbackFactory = UserFallback.class)
public interface UserInterface {

    @RequestMapping(value = "user/getCompanyOpt", method = RequestMethod.POST)
    Map<String, Object> getCompanyOpt();

    @RequestMapping(value = "user/isBindUserHF", method = RequestMethod.POST)
    String isBindUserHF(@RequestParam("userId") Long userId, @RequestParam("cardUID") String cardUID);

    @RequestMapping(value = "user/updateBindHF", method = RequestMethod.POST)
    String updateBindHF(@RequestParam("userId") Long userId, @RequestParam("writeUID") String writeUID);

    @RequestMapping(value = "user/isBindUser", method = RequestMethod.POST)
    String isBindUser(@RequestParam("userId") Long userId, @RequestParam("cardUID") String cardUID);

    @RequestMapping(value = "user/updateBind", method = RequestMethod.POST)
    String updateBind(@RequestParam("userId") Long userId, @RequestParam("writeUID") String writeUID);


    //..............
    @RequestMapping(value = "user/saveUser", method = RequestMethod.POST)
    Map<String, Object> saveUser(@RequestParam("newUser") String newUser, @RequestParam("roleIds") String roleIds);

    @RequestMapping(value = "user/updateUser", method = RequestMethod.POST)
    Map<String, Object> updateUser(@RequestParam("newUser") String newUser, @RequestParam("roleIds") String roleIds);

    @RequestMapping(value = "user/findUserById", method = RequestMethod.POST)
    Map<String, Object> findUserById(@RequestParam("userId") Long userId);

    @RequestMapping(value = "user/getUserWithRoles", method = RequestMethod.POST)
    Map<String, Object> getUserWithRoles(@RequestParam("userAccount") String userAccount);


    @RequestMapping(value = "user/updatePassword", method = RequestMethod.POST)
    Map<String, Object> updatePassword(@RequestBody TblUserInfo user);


    @RequestMapping(value = "user/resetPassword", method = RequestMethod.POST)
    Map<String, Object> resetPassword(@RequestBody TblUserInfo user);

    /*//获取列表信息
    @RequestMapping(value="user/getAllUser",method=RequestMethod.POST)
    Map getAllUser(@RequestParam("FindUser") String FindUser, @RequestParam("page") Integer pageIndex, @RequestParam("rows") Integer pageSize);
    */
    @RequestMapping(value = "user/getCompany", method = RequestMethod.POST)
    List<Company> getCompany();

    @RequestMapping(value = "user/getDept", method = RequestMethod.POST)
    List<TblDeptInfo> getDept();

    @RequestMapping(value = "user/getPreUser", method = RequestMethod.POST)
    Map<String, Object> getPreUser(@RequestParam("userAccount") String userAccount);

    @RequestMapping(value = "user/delUser", method = RequestMethod.POST)
    Map<String, Object> delUser(@RequestParam("userIds") String userIds, @RequestParam("lastUpdateBy") Long lastUpdateBy);

    @RequestMapping(value = "user/login", method = RequestMethod.POST)
    ResultDataDTO login(@RequestParam("userAccount") String userAccount, @RequestParam("password") String password);


    @RequestMapping(value = "user/afterCasLogin", method = RequestMethod.POST)
    ResultDataDTO afterCasLogin(@RequestParam("userCode") String userCode, @RequestParam("userName") String userName);


}
