package cn.pioneeruniverse.project.service.datadic;

import java.util.List;

import cn.pioneeruniverse.project.entity.TblDataDic;


public interface DataDicService {

	public List<TblDataDic> getDataDicList(String termCode);
	
	
}
