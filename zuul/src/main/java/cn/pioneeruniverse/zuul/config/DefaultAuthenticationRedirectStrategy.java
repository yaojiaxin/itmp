package cn.pioneeruniverse.zuul.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 10:47 2019/8/26
 * @Modified By:
 */
public final class DefaultAuthenticationRedirectStrategy implements AuthenticationRedirectStrategy {
    public DefaultAuthenticationRedirectStrategy() {
    }

    public void redirect(HttpServletRequest request, HttpServletResponse response, String potentialRedirectUrl) throws IOException {
        response.sendRedirect(potentialRedirectUrl);
    }
}
