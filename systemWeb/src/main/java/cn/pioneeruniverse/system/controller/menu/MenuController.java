package cn.pioneeruniverse.system.controller.menu;

import java.util.HashMap;
import java.util.Map;

import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.system.feignInterface.menu.MenuInterface;
import cn.pioneeruniverse.system.vo.menu.TblMenuButtonInfo;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("menu")
public class MenuController extends BaseController {

    @RequestMapping(value = "toMenuManage")
    public ModelAndView toUserManage(HttpServletRequest request) {
        ModelAndView view = new ModelAndView();
        view.addObject("token", CommonUtil.getToken(request));
        view.setViewName("systemManagement/menuManagement");
        return view;
    }
}
