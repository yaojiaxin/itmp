package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblProjectGroupUser;

public interface TblProjectGroupUserMapper extends BaseMapper<TblProjectGroupUser> {

	List<Long> findUserIdByPost();

	List<Long> findUserIdBySystemId(Long systemId);

}
