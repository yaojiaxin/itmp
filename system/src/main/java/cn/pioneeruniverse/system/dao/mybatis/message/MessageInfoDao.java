package cn.pioneeruniverse.system.dao.mybatis.message;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import cn.pioneeruniverse.system.entity.TblMessageInfo;

public interface MessageInfoDao extends BaseMapper<TblMessageInfo>{
	int deleteByPrimaryKey(Long id);

	Integer insert(TblMessageInfo record);

	int insertSelective(TblMessageInfo record);

	TblMessageInfo selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(TblMessageInfo record);

	int updateByPrimaryKey(TblMessageInfo record);

	List<TblMessageInfo> getAllMessage(TblMessageInfo message);
}