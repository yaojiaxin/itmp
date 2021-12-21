package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.dev.entity.TblTestTaskLogAttachement;


public interface TblTestTaskLogAttachementMapper {
	void addLogAttachement(List<TblTestTaskLogAttachement> list);
	
	List<TblTestTaskLogAttachement> findTaskLogAttachement(List<Long> ids);
	
	List<TblTestTaskLogAttachement> findTestLogFile(Long testId);
}
