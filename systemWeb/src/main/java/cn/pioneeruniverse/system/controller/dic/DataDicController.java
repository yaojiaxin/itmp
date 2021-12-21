package cn.pioneeruniverse.system.controller.dic;


import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


@RestController
@RequestMapping("dataDic")
public class DataDicController {


    @RequestMapping(value = "toDicManage")
    public ModelAndView toDicManage(HttpServletRequest request) {
        ModelAndView view = new ModelAndView();
        view.addObject("token", CommonUtil.getToken(request));
        view.addObject("url",Constants.SYSTEM_MANAGE_UI_URL+request.getRequestURI());
        view.setViewName("systemManagement/dataDictionaryManagement");
        return view;
    }

    @RequestMapping(value = "toDataDicModal")
    public ModelAndView toDataDicModal(HttpServletRequest request) throws UnsupportedEncodingException {
        ModelAndView view = new ModelAndView();
        view.addObject("token", CommonUtil.getToken(request));
        view.addObject("type", request.getParameter("type"));
        view.addObject("termCode", URLDecoder.decode(request.getParameter("termCode"), "UTF-8"));
        view.addObject("termName", URLDecoder.decode(request.getParameter("termName"), "UTF-8"));
        view.setViewName("systemManagement/dataDictionaryEdit");
        return view;
    }
}
