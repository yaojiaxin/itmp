package cn.pioneeruniverse.project.service.question;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.project.entity.TblQuestionInfo;
import cn.pioneeruniverse.project.entity.TblQuestionLog;

public interface QuestionService {

	List<TblQuestionInfo> getQuestions(Long projectId, HttpServletRequest request);

	void deleteQuestion(Long id, HttpServletRequest request);

	void insertQuestion(TblQuestionInfo tblQuestionInfo, HttpServletRequest request);

	TblQuestionInfo getQuestionById(Long id);

	List<TblQuestionLog> getQuestionLog(Long id);

	void updateQuestion(TblQuestionInfo tblQuestionInfo, HttpServletRequest request);

	List<TblQuestionInfo> getQuestionByProgram(Long programId, HttpServletRequest request);

}
