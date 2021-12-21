package cn.pioneeruniverse.dev.controller;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;

import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.dev.feignInterface.defect.DefectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Description:缺陷管理前端controller
 * Author:liushan
 * Date: 2018/12/10 下午 12:02
 */
@RestController
@RequestMapping("defect")
public class DefectController extends BaseController {
	@Value("${requirement.att.url}")
	private String reqAttUrl;

    @RequestMapping(value = "toDefect",method = RequestMethod.GET)
    public ModelAndView toDefect(HttpServletRequest request){
        ModelAndView view = new ModelAndView();
        String reqFiD = request.getParameter("reqFiD");
        String reqFName = request.getParameter("reqFName");
        view.addObject("reqFiD", reqFiD);
        view.addObject("reqFName", reqFName);
        view.addObject("status", request.getParameter("status"));
        view.addObject("workTaskId", request.getParameter("workTaskId"));
        view.addObject("workTaskCode", request.getParameter("workTaskCode"));
        view.addObject("token", CommonUtil.getToken(request));
        view.addObject("userId",CommonUtil.getCurrentUserId(request));
        view.addObject("userName", CommonUtil.getCurrentUserName(request));
        view.addObject("url",Constants.TEST_MANAGE_UI_URL+request.getRequestURI());
        view.setViewName("defectManagement/defectManagement");
        return view;
    }


    @RequestMapping(value = "toCheckDefect",method = RequestMethod.GET)
    public ModelAndView toCheckDefect(HttpServletRequest request){
        ModelAndView view = new ModelAndView();
        view.addObject("token", CommonUtil.getToken(request));
        view.addObject("defectId", request.getParameter("defectId"));
        view.addObject("userId",CommonUtil.getCurrentUserId(request));
        view.addObject("userName", CommonUtil.getCurrentUserName(request));
        view.addObject("url",Constants.TEST_MANAGE_UI_URL+request.getRequestURI());
        view.addObject("reqAttUrl",reqAttUrl);
        view.setViewName("defectManagement/checkDefect");
        return view;
    }


}
