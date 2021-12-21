package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.project.entity.TblQuestionInfo;

public interface TblQuestionInfoMapper {

	List<TblQuestionInfo> getQuestions(Long projectId);

	void deleteQuestion(TblQuestionInfo questionInfo);

	void insertQuestion(TblQuestionInfo tblQuestionInfo);

	TblQuestionInfo getQuestionById(Long id);

	void updateQuestion(TblQuestionInfo tblQuestionInfo);

	List<TblQuestionInfo> getQuestionByProgram(Long programId);

}
