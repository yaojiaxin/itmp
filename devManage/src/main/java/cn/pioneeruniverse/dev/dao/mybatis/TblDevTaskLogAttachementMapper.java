package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.dev.entity.TblDevTaskLogAttachement;


public interface TblDevTaskLogAttachementMapper {
	void addLogAttachement(List<TblDevTaskLogAttachement> list);
	
	List<TblDevTaskLogAttachement> findTaskLogAttachement(List<Long> ids);
	
	List<TblDevTaskLogAttachement> findTestLogFile(Long devId);
}
