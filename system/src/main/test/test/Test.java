package test;

import base.BaseJunit;
import cn.pioneeruniverse.common.dto.TblUserInfoDTO;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.common.utils.SpringContextHolder;
import cn.pioneeruniverse.system.dao.mybatis.user.UserDao;
import cn.pioneeruniverse.system.entity.TblMenuButtonInfo;
import cn.pioneeruniverse.system.entity.TblUserInfo;
import cn.pioneeruniverse.system.feignInterface.RemoteClientInterface;
import cn.pioneeruniverse.system.service.menu.IMenuService;
import cn.pioneeruniverse.system.service.user.IUserService;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 11:36 2019/1/22
 * @Modified By:
 */
public class Test extends BaseJunit {

    @Autowired
    IUserService iUserService;

    @Autowired
    IMenuService iMenuService;

    @Autowired
    RemoteClientInterface remoteClientInterface;

    @Autowired
    UserDao userDao;

    @Autowired
    RedisUtils redisUtils;

    @org.junit.Test
    public void test() {
        /*Map<String, Object> map = remoteClientInterface.getProject("5uy1srs3Qzz9vxutGN7d");
        String a = "";*/
       /* RequestContext ctx = RequestContext.getCurrentContext();
        HttpSession session = ctx.getRequest().getSession();
        String id = session.getId();
        String a = "";*/
        List<TblUserInfoDTO> list = userDao.getBatchUserDetailByIds(new ArrayList<Long>() {{
            add(1L);
            add(2L);
            add(3L);
            add(4L);
        }});
        String a = "";
    }

    @org.junit.Test
    public void menuTest() {
       /* SpringContextHolder.getBean(RedisUtils.class).remove("111");
        List<TblMenuButtonInfo> menu = iMenuService.getUserMenu(1L);
        String a = "";*/
        TblUserInfoDTO userInfoDTO = new TblUserInfoDTO();
        userInfoDTO.setBirthday(new Date());
        redisUtils.set("yaojiaxinTest", userInfoDTO, 700L);
        TblUserInfoDTO userInfoDTO1 = JsonUtil.mapToObject((Map) redisUtils.get("yaojiaxinTest"), TblUserInfoDTO.class);
        String c = "";
    }

}
