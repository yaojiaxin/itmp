package cn.pioneeruniverse.dev.controller;

import cn.pioneeruniverse.common.utils.CommonUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 17:39 2018/12/7
 * @Modified By:
 */
@RestController
@RequestMapping("versioncontrol")
public class VersionController {

    @RequestMapping(value = "toSvn", method = RequestMethod.GET)
    public ModelAndView toSvn(HttpServletRequest request) {
        ModelAndView view = new ModelAndView();
        view.addObject("currentUserId", CommonUtil.getCurrentUserId(request));
        view.addObject("token", CommonUtil.getToken(request));
        view.setViewName("codeBase/codeBase");
        return view;
    }

    @RequestMapping(value = "toGitLab", method = RequestMethod.GET)
    public ModelAndView toGitLab(HttpServletRequest request) {
        ModelAndView view = new ModelAndView();
        view.addObject("token", CommonUtil.getToken(request));
        view.setViewName("codeBase/gitLabCodeBase");
        return view;
    }

    @RequestMapping(value = "getUserList", method = RequestMethod.GET)
    public ModelAndView getUserList(String existUsers, String codeBaseType, HttpServletRequest request) {
        ModelAndView view = new ModelAndView();
        view.setViewName("codeBase/codeBaseUser");
        Map currentUser = CommonUtil.getCurrentUser(request);
        if (currentUser != null && !currentUser.isEmpty()) {
            view.addObject("currentUserProjectGroupId", currentUser.get("myProjectGroupId"));
        }
        view.addObject("existUsers", existUsers);
        view.addObject("codeBaseType", codeBaseType);
        return view;
    }

    @RequestMapping(value = "getCodeSubmitConfigUser", method = RequestMethod.GET)
    public ModelAndView getCodeSubmitConfigUser(String existUsers, String existUsersName, HttpServletRequest request) throws UnsupportedEncodingException {
        ModelAndView view = new ModelAndView();
        Map currentUser = CommonUtil.getCurrentUser(request);
        if (currentUser != null && !currentUser.isEmpty()) {
            view.addObject("currentUserProjectGroupId", currentUser.get("myProjectGroupId"));
        }
        view.addObject("existUsers", existUsers);
        view.addObject("existUsersName", URLDecoder.decode(existUsersName, "UTF-8"));
        view.setViewName("codeBase/codeSubmitConfigUser");
        return view;
    }

    @RequestMapping(value = "createCodeBase", method = RequestMethod.GET)
    public ModelAndView createCodeBase(Integer scmType, Long systemId, @RequestParam(value = "treeId", required = false) String treeId) {
        ModelAndView view = new ModelAndView();
        view.addObject("scmType", scmType);
        view.addObject("systemId", systemId);
        view.addObject("treeId", treeId);
        view.setViewName("codeBase/createCodeBase");
        return view;
    }

    @RequestMapping(value = "codeSubmitConfig", method = RequestMethod.GET)
    public ModelAndView codeSubmitConfig(Integer scmType, Long systemId) {
        ModelAndView view = new ModelAndView();
        view.addObject("scmType", scmType);
        view.addObject("systemId", systemId);
        view.setViewName("codeBase/codeSubmitConfig");
        return view;
    }
}
