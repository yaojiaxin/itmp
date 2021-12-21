package cn.pioneeruniverse.dev.service.notice;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.dev.entity.TblNoticeInfo;

public interface INoticeService {

	Map<String, Object> getAllNotice(HttpServletRequest request, TblNoticeInfo notice, Integer pageIndex, Integer pageSize,Integer type,String programId);

	TblNoticeInfo selectNoticeById(Long id);

	void insertNotice(HttpServletRequest request, TblNoticeInfo message);

	void updateNotice(HttpServletRequest request, TblNoticeInfo message);
	
	void deleteNotice(HttpServletRequest request, TblNoticeInfo message);
	
	void deleteNoticeList(HttpServletRequest request, String ids);

	List<TblNoticeInfo> getAllValidSystemNotice(HttpServletRequest request);

	List<TblNoticeInfo> getAllValidProjectNotice(HttpServletRequest request);


}
