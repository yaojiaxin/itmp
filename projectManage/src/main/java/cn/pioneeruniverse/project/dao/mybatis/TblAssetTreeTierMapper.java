package cn.pioneeruniverse.project.dao.mybatis;

import cn.pioneeruniverse.project.entity.TblAssetTreeTier;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TblAssetTreeTierMapper extends BaseMapper<TblAssetTreeTier> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblAssetTreeTier record);

    TblAssetTreeTier selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblAssetTreeTier record);

    int updateByPrimaryKey(TblAssetTreeTier record);

    List<TblAssetTreeTier> getAssetTreeList(Integer assetTreeType);

    List<TblAssetTreeTier> getTierNumbersByTreeType(Long assetTreeType);

    TblAssetTreeTier getAssetTree(@Param("assetTreeType") int assetTreeType, @Param("tierNumber") int tierNumber);
}