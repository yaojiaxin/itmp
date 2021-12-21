package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import cn.pioneeruniverse.project.entity.TblDataDic;



public interface DataDicDao extends BaseMapper<TblDataDic> {
	
	List<TblDataDic> getDataDicList(String termCode);
}
