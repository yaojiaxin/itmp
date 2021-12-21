package cn.pioneeruniverse.zuul.config;

import cn.pioneeruniverse.common.utils.CollectionUtil;
import cn.pioneeruniverse.common.utils.JsonUtil;
import com.ccic.cas.client.filter.CasFilter;
import com.ccic.cas.client.filter.CheckAsUserFilter;
import com.ccic.cas.client.filter.SingleSignOutFilter;
import com.ccic.cas.client.filter.UpdateTicketLastUsedFilter;
import com.ccic.cas.client.filter.support.CasValidFilter;
import com.ccic.cas.client.filter.support.FilterConfigWrapper;
import com.ccic.cas.client.filter.support.RequestMatcher;
import com.ccic.cas.client.filter.support.UrlPatternRequestMatcher;
import com.ccic.cas.client.util.CasUtils;
import org.apache.commons.lang3.StringUtils;
import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.FilterConfig;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 9:55 2019/8/21
 * @Modified By:
 */
public class MyCasFilter extends CasFilter {

    private static final Logger log = LoggerFactory.getLogger(MyCasFilter.class);

    protected Hashtable<String, String> authFilterConfigParams;

    protected Hashtable<String, String> validFilterConfigParams;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.initFilterInitParameters(filterConfig);
        UrlPatternRequestMatcher casRequestMatcher = new UrlPatternRequestMatcher();
        if (StringUtils.isNotEmpty(this.casIncludeUrlPatterns)) {
            List<String> casIncludeUrls = Arrays.asList(this.casIncludeUrlPatterns.split("\\s+"));
            if (CollectionUtil.isNotEmpty(casIncludeUrls)) {
                for (String casIncludeUrl : casIncludeUrls) {
                    if (StringUtils.isNotEmpty(casIncludeUrl)) {
                        casRequestMatcher.getIncludeUrlPatterns().add(casIncludeUrl);
                    }
                }
            }
        }
        if (StringUtils.isNotEmpty(this.casExcludeUrlPatterns)) {
            List<String> casExcludeUrls = Arrays.asList(this.casExcludeUrlPatterns.split("\\s+"));
            if (CollectionUtil.isNotEmpty(casExcludeUrls)) {
                for (String casExcludeUrl : casExcludeUrls) {
                    if (StringUtils.isNotEmpty(casExcludeUrl)) {
                        casRequestMatcher.getExcludeUrlPatterns().add(casExcludeUrl);
                    }
                }
            }
        }
        UpdateTicketLastUsedFilter updateTicketLastUsedFilter = new UpdateTicketLastUsedFilter();
        updateTicketLastUsedFilter.init(filterConfig);
        this.addChainedFilter(new RequestMatcher() {
            public boolean matches(ServletRequest request) {
                return request instanceof HttpServletRequest ? CasUtils.isCasLoggedIn((HttpServletRequest) request) : false;
            }
        }, updateTicketLastUsedFilter);
        List<Filter> casAuthFilters = new ArrayList(5);
        SingleSignOutFilter signOutFilter = new SingleSignOutFilter();
        signOutFilter.init(filterConfig);
        casAuthFilters.add(signOutFilter);
        CheckAsUserFilter checkAsUserFilter = new CheckAsUserFilter();
        checkAsUserFilter.init(filterConfig);
        casAuthFilters.add(checkAsUserFilter);
        MyCasAuthenticationFilter authFilter = new MyCasAuthenticationFilter();
        this.authFilterConfigParams.put("encodeServiceUrl", "false");
        FilterConfigWrapper authFilterConfig = new FilterConfigWrapper(filterConfig, this.authFilterConfigParams);
        authFilter.init(authFilterConfig);
        casAuthFilters.add(authFilter);
        CasValidFilter validFilter = new CasValidFilter();//重写cas的Cas20ProxyReceivingTicketValidationFilter
        this.validFilterConfigParams.put("hostnameVerifier", "org.jasig.cas.client.ssl.AnyHostnameVerifier");
        this.validFilterConfigParams.put("encoding", "UTF-8");
        FilterConfigWrapper validFilterConfig = new FilterConfigWrapper(filterConfig, this.validFilterConfigParams);
        validFilter.init(validFilterConfig);
        casAuthFilters.add(validFilter);
        if (StringUtils.isNotEmpty(this.initFilterClass)) {
            try {
                Class<?> fc = Class.forName(this.initFilterClass);
                Filter initFilter = (Filter) fc.newInstance();
                initFilter.init(filterConfig);
                casAuthFilters.add(initFilter);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new ServletException(e);
            }
        }
        this.addChainedFilters(casRequestMatcher, casAuthFilters);
        this.setInitChainedFilters(false);
    }

    private void initFilterInitParameters(FilterConfig filterConfig) {
        this.extractInitParameters(filterConfig);
        this.authFilterConfigParams = JsonUtil.fromJson(filterConfig.getInitParameter("authFilterConfigParamsJson"), Hashtable.class);
        this.validFilterConfigParams = JsonUtil.fromJson(filterConfig.getInitParameter("validFilterConfigParamsJson"), Hashtable.class);
    }
}
