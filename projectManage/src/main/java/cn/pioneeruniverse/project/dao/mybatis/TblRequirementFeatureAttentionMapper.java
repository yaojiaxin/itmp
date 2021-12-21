package cn.pioneeruniverse.project.dao.mybatis;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.project.entity.TblRequirementFeatureAttention;


public interface TblRequirementFeatureAttentionMapper extends BaseMapper<TblRequirementFeatureAttention> {
	int deleteByPrimaryKey(Long id);

	Integer insert(TblRequirementFeatureAttention record);

	int insertSelective(TblRequirementFeatureAttention record);

	TblRequirementFeatureAttention selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(TblRequirementFeatureAttention record);

	int updateByPrimaryKey(TblRequirementFeatureAttention record);

	String getUserIdsByReqFeatureId(@Param("requirementFeatureId")Long id);
}