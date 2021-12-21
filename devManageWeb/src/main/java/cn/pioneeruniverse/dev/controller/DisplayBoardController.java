package cn.pioneeruniverse.dev.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;

/**
*  看板前台controller 
* @author:tingting
* @version:2019年3月20日 上午11:45:16 
*/

@RestController
@RequestMapping("displayboard")
public class DisplayBoardController extends BaseController{
	@Value("${requirement.att.url}")
	private String reqAttUrl;
	
	@RequestMapping("toDisPlayBoard")
	public ModelAndView toDisPlayBoard(HttpServletRequest request, Long id) {
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("reqAttUrl",reqAttUrl);
		view.setViewName("displayBoard/displayBoard");
		view.addObject("id", id);
		return view;
	}

}
