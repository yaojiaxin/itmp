package cn.pioneeruniverse.dev.controller;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.dev.feignInterface.tool.ToolInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Description: 工具信息接口
 * Author:liushan
 * Date: 2018/10/29
 * Time: 下午 5:08
 */
@RestController
@RequestMapping("development")
public class ToolController extends BaseController {

    @RequestMapping("toTool")
    public ModelAndView toSystemInformation(HttpServletRequest request){
        ModelAndView view = new ModelAndView();
        view.addObject("token", CommonUtil.getToken(request));
        view.setViewName("tool/toolInformationConfiguration");
        return view;
    }
}
