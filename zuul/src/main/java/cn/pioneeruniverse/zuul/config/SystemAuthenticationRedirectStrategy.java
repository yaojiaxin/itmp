package cn.pioneeruniverse.zuul.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 17:57 2019/8/17
 * @Modified By:
 */
public final class SystemAuthenticationRedirectStrategy implements AuthenticationRedirectStrategy {
    public SystemAuthenticationRedirectStrategy() {

    }

    @Override
    public void redirect(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String s) throws IOException {
        httpServletResponse.setStatus(999);
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setContentType("text/html; charset=UTF-8");
        PrintWriter writer = httpServletResponse.getWriter();
        httpServletRequest.getSession().invalidate();
        httpServletResponse.setHeader("logoutUrl", s);//返回ajax
        writer.write("<script type = \"text/javascript\"> window.parent.location.href = \"" + s + "\";</script>");
        writer.flush();
        writer.close();
    }
}
