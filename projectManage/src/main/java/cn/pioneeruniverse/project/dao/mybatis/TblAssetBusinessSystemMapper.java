package cn.pioneeruniverse.project.dao.mybatis;

import cn.pioneeruniverse.project.entity.TblAssetBusinessSystem;
import feign.Param;

public interface TblAssetBusinessSystemMapper {
    int deleteByPrimaryKey(Long id);

    int insert(TblAssetBusinessSystem record);

    int insertSelective(TblAssetBusinessSystem record);

    TblAssetBusinessSystem selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblAssetBusinessSystem record);

    int updateByPrimaryKey(TblAssetBusinessSystem record);

    void remove(@Param("businessSystemIds") Long[] businessSystemIds);
}