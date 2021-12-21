package cn.pioneeruniverse.dev.entity;

import com.baomidou.mybatisplus.annotations.TableName;

import cn.pioneeruniverse.common.entity.BaseEntity;

@TableName("tbl_dev_task_attention")
public class TblDevTaskAttention extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private Long devTaskId;

	private Long userId;

	public Long getDevTaskId() {
		return devTaskId;
	}

	public void setDevTaskId(Long devTaskId) {
		this.devTaskId = devTaskId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}