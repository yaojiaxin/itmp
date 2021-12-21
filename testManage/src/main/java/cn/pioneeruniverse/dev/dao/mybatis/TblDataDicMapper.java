package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;
import java.util.Map;

public interface TblDataDicMapper {

	List<Long> findDploys(Map<String, Object> map);

}
