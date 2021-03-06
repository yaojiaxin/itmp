package cn.pioneeruniverse.common.bean;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CollectionUtil;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.CookieUtils;
import com.ccic.cas.client.util.CasUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 14:35 2019/5/20
 * @Modified By:
 */
public class SessionTimeOutFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(SessionTimeOutFilter.class);

    private List<String> excludeUrls = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        String systemNotInterceptUrl = filterConfig.getInitParameter("systemNotInterceptUrl");
        String systemOuterInterfaceUrl = filterConfig.getInitParameter("systemOuterInterfaceUrl");
        if (StringUtils.isNotEmpty(systemNotInterceptUrl)) {
            excludeUrls.addAll(Arrays.asList(systemNotInterceptUrl.split("\\s+")));
        }
        if (StringUtils.isNotEmpty(systemOuterInterfaceUrl)) {
            excludeUrls.addAll(Arrays.asList(systemOuterInterfaceUrl.split("\\s+")));
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if (!CommonUtil.handleExcludeURL(this.excludeUrls, request.getServletPath())) {
            HttpSession session = request.getSession(false);
            if (session == null) {
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                response.setStatus(999);
                response.setCharacterEncoding("utf-8");
                response.setContentType("text/html; charset=UTF-8");
                PrintWriter writer = response.getWriter();
                String logoutUrl = CommonUtil.urlReplaceWithOutParam(request.getRequestURL().toString(), Constants.System.LOGOUT_URL);
                response.setHeader("logoutUrl", logoutUrl);
                writer.write("<script type = \"text/javascript\"> window.parent.location.href = \"" + logoutUrl + "\";</script>");
                writer.flush();
                writer.close();
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {
    }
}
