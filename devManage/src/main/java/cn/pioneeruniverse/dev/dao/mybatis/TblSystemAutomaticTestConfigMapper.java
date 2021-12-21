package cn.pioneeruniverse.dev.dao.mybatis;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblSystemAutomaticTestConfig;

public interface TblSystemAutomaticTestConfigMapper extends BaseMapper<TblSystemAutomaticTestConfig> {
	int deleteByPrimaryKey(Long id);

	Integer insertAutoTest(TblSystemAutomaticTestConfig record);

	int insertSelective(TblSystemAutomaticTestConfig record);

	TblSystemAutomaticTestConfig selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(TblSystemAutomaticTestConfig record);

	int updateByPrimaryKey(TblSystemAutomaticTestConfig record);
}