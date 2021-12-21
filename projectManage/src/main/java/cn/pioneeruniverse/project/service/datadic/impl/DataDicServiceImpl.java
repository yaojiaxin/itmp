package cn.pioneeruniverse.project.service.datadic.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.project.dao.mybatis.DataDicDao;
import cn.pioneeruniverse.project.entity.TblDataDic;
import cn.pioneeruniverse.project.service.datadic.DataDicService;

@Service("dateDicService")
public class DataDicServiceImpl implements DataDicService {

	@Autowired
	private DataDicDao dateDicDao;
	@Autowired
	public RedisUtils redisUtils;
	@Override
	public List<TblDataDic> getDataDicList(String termCode) {	
		return dateDicDao.getDataDicList(termCode);
	}

}
