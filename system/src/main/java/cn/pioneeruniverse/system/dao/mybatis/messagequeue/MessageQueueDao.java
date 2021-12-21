package cn.pioneeruniverse.system.dao.mybatis.messagequeue;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.system.entity.TblMessageQueue;

public interface MessageQueueDao extends BaseMapper<TblMessageQueue>{
    int deleteByPrimaryKey(Long id);

    Integer insert(TblMessageQueue record);

    int insertSelective(TblMessageQueue record);

    TblMessageQueue selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblMessageQueue record);

    int updateByPrimaryKey(TblMessageQueue record);
}