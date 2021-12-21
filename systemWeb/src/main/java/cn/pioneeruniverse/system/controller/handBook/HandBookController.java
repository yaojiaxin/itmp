package cn.pioneeruniverse.system.controller.handBook;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;

@RestController
@RequestMapping("handBook")
public class HandBookController extends BaseController {

	 @RequestMapping(value = "toHandBook")
	    public ModelAndView toHandBook(HttpServletRequest request) {
	        ModelAndView view = new ModelAndView();
	        view.addObject("token", CommonUtil.getToken(request));
	        view.setViewName("handBook/handBook");
	        return view;
	    }
}
