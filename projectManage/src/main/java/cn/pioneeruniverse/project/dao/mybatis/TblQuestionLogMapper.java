package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.project.entity.TblQuestionLog;

public interface TblQuestionLogMapper {

	void insertLog(TblQuestionLog tblQuestionLog);

	List<TblQuestionLog> getQuestionLog(Long id);

}
