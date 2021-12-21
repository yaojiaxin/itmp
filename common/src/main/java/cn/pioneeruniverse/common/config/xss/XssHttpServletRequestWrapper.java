package cn.pioneeruniverse.common.config.xss;

import cn.pioneeruniverse.common.utils.JsoupUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 18:10 2019/5/15
 * @Modified By:
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    HttpServletRequest orgRequest = null;

    public XssHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        orgRequest = request;
    }

    /**
     * @param name
     * @return java.lang.String
     * @Description 覆盖getParameter方法，将参数名和参数值都做xss过滤。
     * 如果需要获得原始的值，则通过super.getParameterValues(name)来获取
     * @MethodName getParameter
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/5/15 18:15
     */
    @Override
    public String getParameter(String name) {
        name = JsoupUtils.cleanForXssHttpServletRequest(name);
        String value = super.getParameter(name);
        if (StringUtils.isNotBlank(value)) {
            value = JsoupUtils.cleanForXssHttpServletRequest(value);
        }
        return value;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] arr = super.getParameterValues(name);
        if (arr != null) {
            for (int i = 0; i < arr.length; i++) {
                arr[i] = JsoupUtils.cleanForXssHttpServletRequest(arr[i]);
            }
        }
        return arr;
    }

    @Override
    public String getHeader(String name) {
        name = JsoupUtils.cleanForXssHttpServletRequest(name);
        String value = super.getHeader(name);
        if (StringUtils.isNotBlank(value)) {
            value = JsoupUtils.cleanForXssHttpServletRequest(value);
        }
        return value;
    }

    public HttpServletRequest getOrgRequest() {
        return orgRequest;
    }

    public static HttpServletRequest getOrgRequest(HttpServletRequest req) {
        if (req instanceof XssHttpServletRequestWrapper) {
            return ((XssHttpServletRequestWrapper) req).getOrgRequest();
        }
        return req;
    }


}
