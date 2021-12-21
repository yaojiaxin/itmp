package cn.pioneeruniverse.dev.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.CommonUtils;

@RestController
@RequestMapping("notice")
public class NoticeController {
	
	@RequestMapping("toNoticeManage")
	public ModelAndView toNoticeManage(HttpServletRequest request, String projectIds, String type) {
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		Boolean flag = new CommonUtils().currentUserWithAdmin(request);
		view.addObject("manageFlag", flag);
		view.addObject("projectIds", projectIds);
		view.addObject("type", type);
		view.setViewName("notice/noticeManage");
		return view;
	}
	
	@RequestMapping("toNoticeDetail")
	public ModelAndView toNoticeDetail(HttpServletRequest request, Long noticeId) {
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("noticeId", noticeId);
		view.setViewName("notice/noticeDetail");
		return view;
	}

}
