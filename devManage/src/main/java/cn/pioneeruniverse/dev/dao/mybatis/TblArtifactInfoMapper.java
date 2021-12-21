package cn.pioneeruniverse.dev.dao.mybatis;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import cn.pioneeruniverse.dev.entity.TblArtifactInfo;

public interface TblArtifactInfoMapper  extends BaseMapper<TblArtifactInfo>{

    int inserts(TblArtifactInfo record);

    TblArtifactInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblArtifactInfo record);

	List<Map<String, Object>> findArtInfo(Map<String, Object> moduleParam);

	List<TblArtifactInfo> findNewPackage(@Param("systemId") Long systemId,@Param("systemModuleId") Long systemModuleId,@Param("env") Integer env);

	List<String> findRequidsByartId(Map<String, Object> map);
}