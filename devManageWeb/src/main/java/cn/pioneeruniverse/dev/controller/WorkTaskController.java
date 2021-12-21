package cn.pioneeruniverse.dev.controller;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.ExcelUtil;

@RestController
@RequestMapping("worktask")
public class WorkTaskController extends BaseController{
	
	@RequestMapping("toWorktask")
	public ModelAndView  toConstruction(HttpServletRequest request){
		ModelAndView view = new ModelAndView();
		view.addObject("token", CommonUtil.getToken(request));
		view.addObject("userId", CommonUtil.getCurrentUserId(request));
		view.addObject("userName", CommonUtil.getCurrentUserName(request));
		String devTaskStatus = request.getParameter("devTaskStatus");
		view.addObject("devTaskStatus", devTaskStatus);
		view.addObject("url",Constants.DEV_MANAGE_UI_URL+request.getRequestURI());
		view.setViewName("worktask/worktaskManage");
		return view;
	}
	
	

}
