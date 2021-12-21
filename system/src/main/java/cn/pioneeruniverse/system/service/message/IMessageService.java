package cn.pioneeruniverse.system.service.message;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import cn.pioneeruniverse.system.entity.TblMessageInfo;
import cn.pioneeruniverse.system.entity.TblMessageQueue;

public interface IMessageService {

	Map<String, Object> getAllMessage(TblMessageInfo message, Integer pageIndex, Integer pageSize);

	TblMessageInfo selectMessageById(Long id);

	void insertMessage(HttpServletRequest request, TblMessageInfo message);

	void deleteMessage(HttpServletRequest request, TblMessageInfo message);
	
	void deleteMessageList(HttpServletRequest request, String ids);

	List<TblMessageQueue> selectMessageQueueList(HttpServletRequest request, TblMessageQueue queue);

	void saveMessageData(boolean success, TblMessageQueue queue);


}
