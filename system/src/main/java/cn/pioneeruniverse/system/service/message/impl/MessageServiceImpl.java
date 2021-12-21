package cn.pioneeruniverse.system.service.message.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.StringUtil;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.system.dao.mybatis.message.MessageInfoDao;
import cn.pioneeruniverse.system.dao.mybatis.messagequeue.MessageQueueDao;
import cn.pioneeruniverse.system.dao.mybatis.messagequeue.MessageQueueHistoryDao;
import cn.pioneeruniverse.system.entity.TblMessageInfo;
import cn.pioneeruniverse.system.entity.TblMessageQueue;
import cn.pioneeruniverse.system.entity.TblMessageQueueHistory;
import cn.pioneeruniverse.system.service.message.IMessageService;


@Service("iMessageService")
public class MessageServiceImpl extends ServiceImpl<MessageInfoDao, TblMessageInfo> implements IMessageService {

    @Autowired
    private MessageInfoDao messageInfoDao;
    
    @Autowired
    private MessageQueueDao messageQueueDao;
    
    @Autowired
    private MessageQueueHistoryDao messageQueueHistoryDao;
    
	@Override
	public Map<String, Object> getAllMessage(TblMessageInfo message, Integer pageIndex, Integer pageSize) {
		Map<String, Object> result = new HashMap<String, Object>();
        if (pageIndex != null && pageSize != null) {
            PageHelper.startPage(pageIndex, pageSize);
            List<TblMessageInfo> list = messageInfoDao.getAllMessage(message);
            PageInfo<TblMessageInfo> pageInfo = new PageInfo<TblMessageInfo>(list);
            result.put("rows", list);
            result.put("records", pageInfo.getTotal());
            result.put("total", pageInfo.getPages());
            result.put("page", pageIndex < pageInfo.getPages() ? pageIndex : pageInfo.getPages());
        } else {
            result.put("rows", messageInfoDao.selectList(new EntityWrapper<TblMessageInfo>(message)));
        }

        return result;
	}

	@Override
	public TblMessageInfo selectMessageById(Long id) {
		return messageInfoDao.selectByPrimaryKey(id);
	}

	@Override
	public void insertMessage(HttpServletRequest request, TblMessageInfo message) {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		Long userId = message.getCreateBy();
		if (userId == null && request != null) {
			userId = CommonUtil.getCurrentUserId(request);
		}
		message.setCreateBy(userId);
		message.setCreateDate(stamp);
		message.setLastUpdateBy(userId);
		message.setLastUpdateDate(stamp);
		message.setStatus(1);
		messageInfoDao.insert(message);

	}

	@Override
	public void deleteMessage(HttpServletRequest request, TblMessageInfo message) {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		Long userId = CommonUtil.getCurrentUserId(request);
		message.setStatus(2);
		message.setLastUpdateDate(stamp);
		message.setLastUpdateBy(userId);
		messageInfoDao.updateByPrimaryKey(message);
	}

	@Override
	public void deleteMessageList(HttpServletRequest request, String ids) {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		Long userId = CommonUtil.getCurrentUserId(request);
		if (StringUtil.isNotEmpty(ids)) {
			String[] idArr = ids.split(",");
			for (String id : idArr) {
				if (StringUtil.isNotEmpty(id)) {
					TblMessageInfo message = new TblMessageInfo();
					message.setId(Long.parseLong(id));
					message.setStatus(2);
					message.setLastUpdateDate(stamp);
					message.setLastUpdateBy(userId);
					messageInfoDao.updateByPrimaryKeySelective(message);
				}
			}
		}
	}

	@Override
	public List<TblMessageQueue> selectMessageQueueList(HttpServletRequest request, TblMessageQueue queue) {
		EntityWrapper<TblMessageQueue> wrapper = new EntityWrapper<TblMessageQueue>();
		List<TblMessageQueue> queueList = messageQueueDao.selectList(wrapper);
		return queueList;
	}

	/**
	 * 消息队列：发送外部消息：email 微信结果处理。
	 */
	@Override
	@Transactional(readOnly = false)
	public void saveMessageData(boolean success, TblMessageQueue queue) {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		if (success) {// 发送成功则保存到历史
			TblMessageQueueHistory history = new TblMessageQueueHistory();
			history.setMessageTitle(queue.getMessageTitle());
			history.setMessageContent(queue.getMessageContent());
			history.setMessageReceiver(queue.getMessageReceiver());
			history.setCreateBy(queue.getCreateBy());
			history.setCreateDate(stamp);
			history.setStatus(1);
			messageQueueHistoryDao.insert(history);
			if (queue.getId() != null) {
				messageQueueDao.deleteByPrimaryKey(queue.getId());
			}
		} else {// 发送邮件失败,转为定时发送
			if (queue.getId() != null) {
				queue.setSendCount(queue.getSendCount() + 1);
				queue.setLastUpdateBy(1L);
				queue.setLastUpdateDate(stamp);
				messageQueueDao.updateByPrimaryKey(queue);
			} else {
				queue.setSendCount(1);
				queue.setCreateDate(stamp);
				queue.setStatus(1);
				messageQueueDao.insert(queue);
			}
		}
	}
	
}

