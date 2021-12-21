package cn.pioneeruniverse.common.velocity.tag;

import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.common.utils.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 18:19 2018/12/6
 * @Modified By:
 */
public class VelocityAuthority {

    private static RedisUtils redisUtils = SpringContextHolder.getBean(RedisUtils.class);
    private static final Logger logger = LoggerFactory.getLogger(VelocityAuthority.class);

    public boolean hasPermission(String token, String permission) {
        boolean hasPermission = false;
        try {
            Object redisUser = redisUtils.get(token);
            if (redisUser != null) {
                Map redisUserMap = (Map) redisUser;
                ArrayList<String> stringPermissions = (ArrayList<String>) redisUserMap.get("stringPermissions");
                hasPermission = stringPermissions.contains(permission);
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
        }
        return hasPermission;
    }


    public boolean hasAnyPermission(String token, String... permissions) {
        boolean hasAnyPermission = false;
        try {
            Object redisUser = redisUtils.get(token);
            if (redisUser != null) {
                Map redisUserMap = (Map) redisUser;
                ArrayList<String> stringPermissions = (ArrayList<String>) redisUserMap.get("stringPermissions");
                for (String permission : permissions) {
                    if (stringPermissions.contains(permission)) {
                        hasAnyPermission = true;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
        }
        return hasAnyPermission;
    }


    public boolean hasAllPermission(String token, String... permissions) {
        boolean hasAnyPermission = false;
        try {
            Object redisUser = redisUtils.get(token);
            if (redisUser != null) {
                Map redisUserMap = (Map) redisUser;
                ArrayList<String> stringPermissions = (ArrayList<String>) redisUserMap.get("stringPermissions");
                if (stringPermissions.containsAll(Arrays.asList(permissions))) {
                    hasAnyPermission = true;
                }
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
        }
        return hasAnyPermission;
    }


    public boolean lacksPermission(String token, String permission) {
        boolean lacksPermission = false;
        try {
            Object redisUser = redisUtils.get(token);
            if (redisUser != null) {
                Map redisUserMap = (Map) redisUser;
                ArrayList<String> stringPermissions = (ArrayList<String>) redisUserMap.get("stringPermissions");
                lacksPermission = !stringPermissions.contains(permission);
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
        }
        return lacksPermission;
    }

    public boolean hasRole(String token, String role) {
        boolean hasRole = false;
        try {
            Object redisUser = redisUtils.get(token);
            if (redisUser != null) {
                Map redisUserMap = (Map) redisUser;
                ArrayList<String> roles = (ArrayList<String>) redisUserMap.get("roles");
                hasRole = roles.contains(role);
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e.getMessage()), e);
        }
        return hasRole;
    }


}
