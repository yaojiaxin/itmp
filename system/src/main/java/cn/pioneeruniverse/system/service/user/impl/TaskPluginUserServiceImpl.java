package cn.pioneeruniverse.system.service.user.impl;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.*;
import cn.pioneeruniverse.system.dao.mybatis.user.UserDao;
import cn.pioneeruniverse.system.entity.TblUserInfo;
import cn.pioneeruniverse.system.service.user.ITaskPluginUserService;
import cn.pioneeruniverse.system.service.user.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 15:06 2019/1/4
 * @Modified By:
 */
@Service("iTaskPluginUserService")
public class TaskPluginUserServiceImpl implements ITaskPluginUserService {

    private static final Logger logger = LoggerFactory.getLogger(TaskPluginUserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private IUserService iUserService;

    @Value("${task.plugin.esb.service.addr}")
    private String taskPluginEsbServiceAddr;

    @Value("${task.plugin.esb.login.check}")
    private Boolean taskPluginEsbLoginCheck;

    @Value("${task.plugin.esb.login.consumer.id}")
    private String taskPluginEsbLoginConsumerId;

    @Override
    public Object afterLoginCheck(TblUserInfo user, String password, String loginIp) {
        //将用户ip地址填写至redis中
        redisUtils.setForHash(Constants.TaskPlugin.REDIS_SUFFIX + user.getUserAccount(), "loginIp", loginIp);
        //生成当前已验证登录成功用户token
        String token = JWTTokenUtils.createJavaWebToken(new HashMap<String, Object>() {{
            put("userAccount", user.getUserAccount());
            put("userName", user.getUserName());
            put("userPassword", password);
        }});
        Map<String, Object> dataMap = new HashMap<String, Object>() {{
            put("id", user.getId());
            put("userName", user.getUserAccount());
            put("realName", user.getUserName());
            put("userToken", token);
        }};
        return dataMap;
    }

    @Override
    public void logout(String username) {
        //清空redis中loginIp
        redisUtils.delForHash(Constants.TaskPlugin.REDIS_SUFFIX + username, "loginIp");
    }

    @Override
    @Transactional(readOnly = true)
    public TblUserInfo checkLogin(String userAccount, String password) throws LoginException {
        Map<String, String> requestHead = new HashMap<String, String>() {{
            put("seqNo", "");
            put("consumerSeqNo", "");
            put("consumerID", taskPluginEsbLoginConsumerId);
            put("providerID", "");
            put("classCode", "");
            put("riskCode", "");
            put("regionCode", "");
            put("version", "");
        }};
        Map<String, String> requestBody = new HashMap<String, String>() {{
            put("usercode", userAccount);
            put("password", password);
            put("authtype", "passwordAuth");
            put("ip", "");
            put("mac", "");
        }};
        Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>() {{
            put("requestHead", requestHead);
            put("requestBody", requestBody);
        }};
        String resultJson = HttpUtil.doPost(taskPluginEsbServiceAddr, JsonUtil.toJson(map), "UTF-8");
        Map<String, Map<String, String>> result = JsonUtil.fromJson(resultJson, HashMap.class);
        if (result == null || result.isEmpty()) {
            logger.error("插件登录验证接口返回结果为空");
            throw new LoginException("验证未通过");
        }
        Map<String, String> responseHead = result.get("responseHead");
        if (!StringUtils.equals(responseHead.get("status"), "0")) {
            logger.error("插件登录验证接口返回失败，status:" + responseHead.get("status") + ";esbMessage:" + requestHead.get("esbMessage") + ";appMessage:" + requestHead.get("appMessage"));
            throw new LoginException("验证未通过");
        } else {
            return userDao.getUserAcc(userAccount);
        }
    }

    @Override
    public Object loginStatusCheck(String username, String userToken, String loginIp) throws LoginException {
        TblUserInfo user;
        String password = JWTTokenUtils.parserJavaWebToken(userToken).get("userPassword").toString();
        if (taskPluginEsbLoginCheck) {
            user = this.checkLogin(username, password);
        } else {
            user = iUserService.checkLogin(username, password);
        }
        //校验ip地址与redis中是否依然匹配
        String loginedIp = (String) redisUtils.getForHash(Constants.TaskPlugin.REDIS_SUFFIX + user.getUserAccount(), "loginIp");
        if (StringUtils.isEmpty(loginedIp) || !StringUtils.equals(loginIp, loginedIp)) {
            throw new LoginException("ip地址不匹配");
        }
        return this.afterLoginCheck(user, password, loginIp);
    }

}

