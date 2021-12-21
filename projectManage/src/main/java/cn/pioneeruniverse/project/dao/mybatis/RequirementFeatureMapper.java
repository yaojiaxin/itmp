package cn.pioneeruniverse.project.dao.mybatis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.pioneeruniverse.project.entity.TblRequirementFeature;
import com.baomidou.mybatisplus.mapper.BaseMapper;

public interface RequirementFeatureMapper extends BaseMapper<TblRequirementFeature> {
	List<TblRequirementFeature> findFeatureByRequirementId(Long id);
	
    int deleteByPrimaryKey(Long id);
    
    TblRequirementFeature getFeatureByCode(String code);

    int insertFeature(TblRequirementFeature record);
    
    int updateFeatureById(TblRequirementFeature record);

    int updateStatusById(TblRequirementFeature record);
    
    List<TblRequirementFeature> getAllFeature(String featureName);

	List<TblRequirementFeature> selectFeatures(HashMap<String, Object> map);

	void relationRequirement(HashMap<String, Object> map);

    List<Map<String, Object>> getReqidByreqTaskId(Map<String, Object> reqTaskMap);

    List<Map<String, Object>> getDevTaskByreqTaskId(long requireId);

    List<TblRequirementFeature> selectReqFeatureById(long requireId);

    List<Map<String, Object>> getFeatureByReId(long requireId);

	List<TblRequirementFeature> getFeatureToHistoryList(Map<String, String> map);

	TblRequirementFeature getReqFeatureById(Long id);

	List<TblRequirementFeature> getRequirementId(Map<String, Object> map);

	List<TblRequirementFeature> getRequirementFeatures(Map<String, Object> map2);


    /*int insertSelective(TblRequirementFeature record);

    TblRequirementFeature selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblRequirementFeature record);*/
    
}