package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import cn.pioneeruniverse.dev.entity.TblArtifactTag;

public interface TblArtifactTagMapper {

    int insert(TblArtifactTag record);

    TblArtifactTag selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblArtifactTag record);
    
    List<TblArtifactTag> selectByArtifactId(@Param("artifactId") Long artifactId);
    
    void deleteByArtifactId(Long artifactId);

    List<TblArtifactTag> selectTagByArtifactId(Long id);

    List<TblArtifactTag> selectByArtifactIdAndEnv(Map<String,Object> map);
}