package cn.pioneeruniverse.project.dao.mybatis;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.project.entity.TblRequirementFeatureHistory;

public interface RequirementFeatureHistoryMapper extends BaseMapper<TblRequirementFeatureHistory> {
	int deleteByPrimaryKey(Long id);

	Integer insert(TblRequirementFeatureHistory record);

	int insertSelective(TblRequirementFeatureHistory record);

	TblRequirementFeatureHistory selectByPrimaryKey(Long id);

	int updateByPrimaryKeySelective(TblRequirementFeatureHistory record);

	int updateByPrimaryKey(TblRequirementFeatureHistory record);

	List<TblRequirementFeatureHistory> getFeatureHistoryWorkloadList(Map<String, String> map);
}