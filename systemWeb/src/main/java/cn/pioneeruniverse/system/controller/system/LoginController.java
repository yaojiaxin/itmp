package cn.pioneeruniverse.system.controller.system;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.dto.ResultDataDTO;
import cn.pioneeruniverse.common.entity.AjaxModel;
import cn.pioneeruniverse.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.system.feignInterface.user.UserInterface;

@RestController
public class LoginController {

    Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private UserInterface userInterface;
    @Autowired
    private RedisUtils redisUtils;

    @Value("${system.name}")
    private String systemName;

    @Value("${cas.config.open}")
    private Boolean casConfigOpen;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView loginPage(HttpServletRequest request) throws Exception {
        ModelAndView view = new ModelAndView();
        request.getSession(true);//生成客户端session
        if (CommonUtil.systemTokenCheck(request)) {
            view.setViewName("redirect:" + Constants.System.INDEX_URL);
            return view;
        } else {
            view.setViewName("login");
            return view;
        }
    }

    /**
     * @param request
     * @param response
     * @return org.springframework.web.servlet.ModelAndView
     * @Description 登录验证
     * @MethodName login
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/5/10 16:45
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public AjaxModel login(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (casConfigOpen != null && casConfigOpen) {
                String currentUserAccount = request.getParameter("currentUserAccount");
                String currentUserName = request.getParameter("currentUserName");
                ResultDataDTO dataDTO = userInterface.afterCasLogin(currentUserAccount, currentUserName);
                if (StringUtils.equals(dataDTO.getResCode(), ResultDataDTO.DEFAULT_SUCCESS_CODE)) {
                    return AjaxModel.SUCCESS(dataDTO.getData().toString());
                } else {
                    return AjaxModel.FAIL(new HashMap<String, String>() {{
                        put("message", dataDTO.getResDesc());
                        put("errorCode", dataDTO.getResCode());
                    }});
                }
            } else {
                String userAccount = request.getParameter("userAccount");
                String password = request.getParameter("password");
                ResultDataDTO dataDTO = userInterface.login(userAccount, password);
                if (StringUtils.equals(dataDTO.getResCode(), ResultDataDTO.DEFAULT_SUCCESS_CODE)) {
                    CookieUtils.setCookie(response, Constants.System.TOKEN_NAME, dataDTO.getData().toString(), "/", -1, true);//会话级cookie,关闭浏览器失效
                    return AjaxModel.SUCCESS();
                } else {
                    return AjaxModel.FAIL(new HashMap<String, String>() {{
                        put("message", dataDTO.getResDesc());
                        put("errorCode", dataDTO.getResCode());
                    }});
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxModel.FAIL(new HashMap<String, String>() {{
                put("message", e.getMessage());
                put("errorCode", ResultDataDTO.DEFAULT_ABNORMAL_CODE);
            }});
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public ModelAndView logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView model = new ModelAndView();
        CommonUtil.systemTokenClean(request, response);
        request.getSession().invalidate();
        model.setViewName("redirect:" + Constants.System.DEFAULT_URL[0]);
        return model;
    }

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public ModelAndView index(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        String currentUserToken = CommonUtil.getToken(request);
        Map currentUser = (Map) redisUtils.get(currentUserToken);
        model.addObject("token", currentUserToken);
        model.addObject("user", currentUser);
        model.addObject("title", systemName);
        model.setViewName("main");
        return model;
    }

    @RequestMapping(value = "/toModifyMySvnPassword", method = RequestMethod.GET)
    public ModelAndView toModifyMySvnPassword() {
        ModelAndView model = new ModelAndView();
        model.setViewName("modifyMySvnPassword");
        return model;
    }

    @RequestMapping(value = "/toDefaultMenu", method = RequestMethod.GET)
    public ModelAndView toDefaultMenu() {
        ModelAndView model = new ModelAndView();
        model.setViewName("menu/defaultMenu");
        return model;
    }

}
