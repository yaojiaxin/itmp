package cn.pioneeruniverse.dev.service.workTask;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.dev.entity.TblTestTaskAttachement;
import cn.pioneeruniverse.dev.entity.TblTestTaskLog;
import cn.pioneeruniverse.dev.entity.TblTestTaskLogAttachement;
import cn.pioneeruniverse.dev.entity.TblTestTaskRemark;
import cn.pioneeruniverse.dev.entity.TblTestTaskRemarkAttachement;

public interface workTaskRemark {
	void addTaskRemark(String remark,Long Userid,String userName,String UserAccount,List<TblTestTaskRemarkAttachement> files);
	
	List<TblTestTaskRemark> selectRemark(String testTaskId);
	List<TblTestTaskRemarkAttachement> findTaskRemarkAttchement(List<Long> ids);
	List<TblTestTaskLog> findLogList(Long id);
	List<TblTestTaskLogAttachement> findLogAttachement(List<Long> ids);
	//查询所有日志
	List<TblTestTaskLogAttachement> findTestLogFile(Long testId);
	
	List<TblTestTaskAttachement> findAttachement(Long testId);
	
	 void updateNo(List<TblTestTaskAttachement> list, HttpServletRequest request);
}
