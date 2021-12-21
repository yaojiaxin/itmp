package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.project.entity.TblProjectChangeLog;

public interface TblProjectChangeLogMapper {

	void insertLog(TblProjectChangeLog log);

	List<TblProjectChangeLog> getChangeLog(Long id);

}
