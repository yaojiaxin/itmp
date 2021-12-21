package cn.pioneeruniverse.zuul.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 14:44 2019/8/21
 * @Modified By:
 */
public interface AuthenticationRedirectStrategy {
    void redirect(HttpServletRequest var1, HttpServletResponse var2, String var3) throws IOException;
}
