package cn.pioneeruniverse.dev.dao.mybatis;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblSystemAutomaticTestResult;

public interface TblSystemAutomaticTestResultMapper extends BaseMapper<TblSystemAutomaticTestResult> {
	int deleteByPrimaryKey(Long id);

	Integer insert(TblSystemAutomaticTestResult record);

	int insertSelective(TblSystemAutomaticTestResult record);

	TblSystemAutomaticTestResult selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(TblSystemAutomaticTestResult record);

	int updateByPrimaryKey(TblSystemAutomaticTestResult record);

    void insertNew(TblSystemAutomaticTestResult tblSystemAutomaticTestResult);
}