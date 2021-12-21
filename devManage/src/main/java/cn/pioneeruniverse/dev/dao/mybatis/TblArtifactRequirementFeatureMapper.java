package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;

import cn.pioneeruniverse.dev.entity.TblRequirementFeature;
import org.apache.ibatis.annotations.Param;

import cn.pioneeruniverse.dev.entity.TblArtifactRequirementFeature;

public interface TblArtifactRequirementFeatureMapper {

    int insert(TblArtifactRequirementFeature record);

    TblArtifactRequirementFeature selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblArtifactRequirementFeature record);
    
    List<TblRequirementFeature> selectByArtifactId(@Param("artifactId") Long artifactId);
    
} 