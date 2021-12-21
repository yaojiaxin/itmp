package cn.pioneeruniverse.system.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.system.entity.UiFavorite;
import cn.pioneeruniverse.system.service.uifavorite.UiFavoriteService;

/**
*  界面收藏Controller 
* @author:tingting
* @version:2019年1月25日 下午5:01:19 
*/
@RestController
@RequestMapping("uifavorite")
public class UiFavoriteController extends BaseController{
	@Autowired
	private UiFavoriteService uiFavoriteService;
	@Autowired
	private RedisUtils redisUtils;
	
	@RequestMapping("addAndUpdate")
	public Map<String, Object> addAndUpdate(HttpServletRequest request,UiFavorite uiFavorite){
		Map<String, Object> result = new HashMap<>();
		try {
			Long uid = CommonUtil.getCurrentUserId(request);
			uiFavorite.setUserId(Integer.parseInt(uid.toString()));
			uiFavoriteService.addAndUpdate(uiFavorite,request);
			result.put("status", "success");
		} catch (Exception e) {
			return super.handleException(e, "页面收藏失败！");
		}
		return result;
	}
	
	@RequestMapping("getFavoriteContent")
	public Map<String, Object> getFavoriteContent(HttpServletRequest request,String menuUrl){
		Map<String, Object> map = new HashMap<>();
			try {
				Long uid = CommonUtil.getCurrentUserId(request);
				Map<String, Object> object = (Map<String, Object>) redisUtils.get("TBL_UI_FAVORIITE");
				if (object!=null) {
					List<Map<String, Object>>  list =  (List<Map<String, Object>>) object.get(uid.toString());
					if (list!=null && list.size()>0) {
						for (Map<String, Object> innerMap : list) {
							if (innerMap.containsKey(menuUrl)) {
								String favoriteContent = (String) innerMap.get(menuUrl);
								if (!StringUtils.isBlank(favoriteContent)) {
									map.put("favoriteContent", favoriteContent);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				return  super.handleException(e, "获取页面收藏失败！");
			}
		return map;
	}	
}
