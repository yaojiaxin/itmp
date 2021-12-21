package cn.pioneeruniverse.system.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

import java.util.List;

@TableName("tbl_message_info")
public class TblMessageInfo extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private List<Object> systemIdList;

	private Integer messageType;

	private Integer messageSource;

	private String messageTitle;

	private String messageContent;

	private String messageUrl;

	private Long menuButtonId;

	private Integer messageReceiverScope;

	private String messageReceiver;

	private Long systemId;  //系统id

	private Long projectId; 	//项目id

	@TableField(exist = false)
	private Long currentUserId;//当前登录用户

	public Integer getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(Integer messageSource) {
		this.messageSource = messageSource;
	}

	public String getMessageTitle() {
		return messageTitle;
	}

	public void setMessageTitle(String messageTitle) {
		this.messageTitle = messageTitle == null ? null : messageTitle.trim();
	}

	public String getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(String messageContent) {
		this.messageContent = messageContent == null ? null : messageContent.trim();
	}

	public String getMessageUrl() {
		return messageUrl;
	}

	public void setMessageUrl(String messageUrl) {
		this.messageUrl = messageUrl == null ? null : messageUrl.trim();
	}

	public Long getMenuButtonId() {
		return menuButtonId;
	}

	public void setMenuButtonId(Long menuButtonId) {
		this.menuButtonId = menuButtonId;
	}

	public Integer getMessageReceiverScope() {
		return messageReceiverScope;
	}

	public void setMessageReceiverScope(Integer messageReceiverScope) {
		this.messageReceiverScope = messageReceiverScope;
	}

	public String getMessageReceiver() {
		return messageReceiver;
	}

	public void setMessageReceiver(String messageReceiver) {
		this.messageReceiver = messageReceiver == null ? null : messageReceiver.trim();
	}

	public Long getCurrentUserId() {
		return currentUserId;
	}

	public void setCurrentUserId(Long currentUserId) {
		this.currentUserId = currentUserId;
	}

	public Long getSystemId() {
		return systemId;
	}

	public void setSystemId(Long systemId) {
		this.systemId = systemId;
	}

	public List<Object> getSystemIdList() {
		return systemIdList;
	}

	public void setSystemIdList(List<Object> systemIdList) {
		this.systemIdList = systemIdList;
	}

	public Integer getMessageType() {
		return messageType;
	}

	public void setMessageType(Integer messageType) {
		this.messageType = messageType;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	@Override
	public String toString() {
		return "TblMessageInfo{" +
				"systemIdList=" + systemIdList +
				", messageType=" + messageType +
				", messageSource=" + messageSource +
				", messageTitle='" + messageTitle + '\'' +
				", messageContent='" + messageContent + '\'' +
				", messageUrl='" + messageUrl + '\'' +
				", menuButtonId=" + menuButtonId +
				", messageReceiverScope=" + messageReceiverScope +
				", messageReceiver='" + messageReceiver + '\'' +
				", systemId=" + systemId +
				", projectId=" + projectId +
				", currentUserId=" + currentUserId +
				'}';
	}
}