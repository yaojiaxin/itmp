package cn.pioneeruniverse.system.service.uifavorite.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.system.dao.mybatis.uifavorite.UifavoriteDao;
import cn.pioneeruniverse.system.entity.UiFavorite;
import cn.pioneeruniverse.system.service.uifavorite.UiFavoriteService;

/**
*  类说明 
* @author:tingting
* @version:2019年1月28日 上午9:42:13 
*/
@Service
@Transactional(readOnly = true)
public class UifavoriteServiceImpl implements UiFavoriteService{
	@Autowired
	private UifavoriteDao uifavoriteDao;
	@Autowired
	private RedisUtils redisUtils;

	@Override
	@Transactional(readOnly = false)
	public void addAndUpdate(UiFavorite uiFavorite, HttpServletRequest request) {
		Long uid = CommonUtil.getCurrentUserId(request);
		UiFavorite uiFavorite2 = uifavoriteDao.findByMenuUrlAndUserId(uiFavorite);
		if (uiFavorite2!=null) {
			uiFavorite.setLastUpdateBy(uid);
			uiFavorite.setLastUpdateDate(new Timestamp(new Date().getTime()));
			uifavoriteDao.update(uiFavorite);
		}else {
			uiFavorite.setCreateBy(uid);
			uiFavorite.setCreateDate(new Timestamp(new Date().getTime()));
			uifavoriteDao.insert(uiFavorite);
		}
		//取出redis是否已经包含当前登录uid的list 如果有往list里添加收藏信息
		Map<String, Object> object = (Map<String, Object>) redisUtils.get("TBL_UI_FAVORIITE");
		if (object!=null && object.size()>0) {
			if (!object.containsKey(uid.toString())) {
				List<Map<String, Object>> list = new ArrayList<>();
				Map<String, Object> infoMap = new HashMap<>();
				infoMap.put(uiFavorite.getMenuUrl(), uiFavorite.getFavoriteContent());
				list.add(infoMap);
				object.put(uid.toString(), list);
			}else {
				List<Map<String, Object>> list = new ArrayList<>();
				list =  (List<Map<String, Object>>)object.get(uid.toString());
				if (list!=null&&list.size()>0) {
					Boolean flag = false;
					for (Map<String, Object> infomap : list) {
						 flag = false;
						if (infomap.containsKey(uiFavorite.getMenuUrl())) {
							flag = true;
							infomap.put(uiFavorite.getMenuUrl(), uiFavorite.getFavoriteContent());
						}
					}
					if(!flag) {
						Map<String, Object> infomap2 = new HashMap<>();
						infomap2.put(uiFavorite.getMenuUrl(), uiFavorite.getFavoriteContent());
						list.add(infomap2);
					}
					object.put(uid.toString(), list);
				}else {
					Map<String, Object> infomap = new HashMap<>();
					infomap.put(uiFavorite.getMenuUrl(), uiFavorite.getFavoriteContent());
					list.add(infomap);
					object.put(uid.toString(), list);
				}
			}
		}else {
			object = new HashMap<>();
			List<Map<String, Object>> list = new ArrayList<>();
			Map<String, Object> infoMap = new HashMap<>();
			infoMap.put(uiFavorite.getMenuUrl(), uiFavorite.getFavoriteContent());
			list.add(infoMap);
			object.put(uid.toString(), list);
		}
		redisUtils.set("TBL_UI_FAVORIITE",object);
		
	}

}
