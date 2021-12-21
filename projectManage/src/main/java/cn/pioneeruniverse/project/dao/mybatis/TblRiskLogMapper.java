package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.project.entity.TblRiskLog;

public interface TblRiskLogMapper {

	void insertLog(TblRiskLog log);

	List<TblRiskLog> getRiskLog(Long id);

}
