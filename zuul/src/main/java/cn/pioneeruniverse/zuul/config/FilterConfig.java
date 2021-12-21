package cn.pioneeruniverse.zuul.config;


import cn.pioneeruniverse.common.bean.SessionTimeOutFilter;
import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.JsonUtil;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.Hashtable;
import java.util.Map;


/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 10:37 2019/1/16
 * @Modified By:
 */
@Configuration
public class FilterConfig {

    @Value("${cas.server.url.prefix}")
    private String casServerUrlPrefix;

    @Value("${cas.server.login.url}")
    private String casServerLoginUrl;

    @Value("${my.system.server.name}")
    private String mySystemServerName;

    @Value("${cas.include.url.patterns}")
    private String casIncludeUrlPatterns;

    @Value("${cas.exclude.url.patterns}")
    private String casExcludeUrlPatterns;

    @Value("${system.not.intercept.url}")
    private String systemNotInterceptUrl;

    @Value("${system.outer.interface.url}")
    private String systemOuterInterfaceUrl;

    @Value("${cas.config.open}")
    private Boolean casConfigOpen;

    @Bean
    @Order(0)
    public FilterRegistrationBean systemLogoutRedirectToCasLogoutFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new SystemLogoutRedirectToCasLogoutFilter());
        registrationBean.addUrlPatterns(Constants.System.LOGOUT_URL);
        registrationBean.setEnabled(casConfigOpen);
        return registrationBean;
    }

    @Bean
    @ConditionalOnProperty(prefix = "cas.config", name = "open", havingValue = "false")
    @Order(1)
    public FilterRegistrationBean sessionTimeOutFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setEnabled(true);
        Map<String, String> initParameters = Maps.newHashMap();
        filterRegistrationBean.setFilter(new SessionTimeOutFilter());
        initParameters.put("systemNotInterceptUrl", systemNotInterceptUrl);
        initParameters.put("systemOuterInterfaceUrl", systemOuterInterfaceUrl);
        filterRegistrationBean.setInitParameters(initParameters);
        return filterRegistrationBean;
    }

    @Bean
    @Order(2)
    public FilterRegistrationBean systemCasFilter() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new MyCasFilter());
        Hashtable<String, String> authFilterConfigParams = new Hashtable<String, String>() {{
            put("casServerLoginUrl", casServerLoginUrl);
            put("serverName", mySystemServerName);
            put("authenticationRedirectStrategy", "cn.pioneeruniverse.zuul.config.SystemAuthenticationRedirectStrategy");
        }};
        Hashtable<String, String> validFilterConfigParams = new Hashtable<String, String>() {{
            put("casServerUrlPrefix", casServerUrlPrefix);
            put("serverName", mySystemServerName);
        }};
        filterRegistrationBean.addInitParameter("casIncludeUrlPatterns", casIncludeUrlPatterns);
        filterRegistrationBean.addInitParameter("casExcludeUrlPatterns", casExcludeUrlPatterns);
        filterRegistrationBean.addInitParameter("authFilterConfigParamsJson", JsonUtil.toJson(authFilterConfigParams));
        filterRegistrationBean.addInitParameter("validFilterConfigParamsJson", JsonUtil.toJson(validFilterConfigParams));
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setEnabled(casConfigOpen);
        return filterRegistrationBean;
    }

    @Bean
    @Order(3)
    public FilterRegistrationBean afterCasLoginSuccessFilter() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new AfterCasLoginSuccessFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.addInitParameter("casExcludeUrlPatterns", casExcludeUrlPatterns);
        registrationBean.setEnabled(casConfigOpen);
        return registrationBean;
    }

    @Bean
    @Order(4)
    public FilterRegistrationBean redirectToSystemIndex() {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new RedirectToSystemIndexFilter());
        registrationBean.addUrlPatterns(Constants.System.DEFAULT_URL);
        registrationBean.setEnabled(casConfigOpen);
        return registrationBean;
    }
}
