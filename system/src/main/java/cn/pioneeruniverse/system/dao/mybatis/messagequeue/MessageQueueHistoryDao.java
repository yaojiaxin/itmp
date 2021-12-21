package cn.pioneeruniverse.system.dao.mybatis.messagequeue;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.system.entity.TblMessageQueueHistory;

public interface MessageQueueHistoryDao extends BaseMapper<TblMessageQueueHistory>{
    int deleteByPrimaryKey(Long id);

    Integer insert(TblMessageQueueHistory record);

    int insertSelective(TblMessageQueueHistory record);

    TblMessageQueueHistory selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblMessageQueueHistory record);

    int updateByPrimaryKey(TblMessageQueueHistory record);
}