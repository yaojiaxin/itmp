package cn.pioneeruniverse.project.dao.mybatis;

import cn.pioneeruniverse.project.entity.TblAssetBusinessTree;
import cn.pioneeruniverse.project.vo.BusinessSystemTreeVo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface TblAssetBusinessTreeMapper extends BaseMapper<TblAssetBusinessTree> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblAssetBusinessTree record);

    TblAssetBusinessTree selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblAssetBusinessTree record);

    int updateByPrimaryKey(TblAssetBusinessTree record);

    void insertBusinessSystemTreeVo(BusinessSystemTreeVo assetBusinessTree);

    List<BusinessSystemTreeVo> getBusinessTreeListByTierNumber(BusinessSystemTreeVo businessSystemTreeVo);

    BusinessSystemTreeVo getEntityInfo(Long id);

    void updateBusinessSystemTreeVo(BusinessSystemTreeVo businessSystemTreeVo);

    List<BusinessSystemTreeVo> selectChildTier(String parentIds);

    List<BusinessSystemTreeVo> getBusinessTreeListWithSystem(BusinessSystemTreeVo businessSystemTreeVo);

    List<BusinessSystemTreeVo> getTiersByParentIds(String parentIds);

    Map<String,Object> getAllTierNameWithTree(@Param("parentIds") String parentIds);

    List<String> selectAllParentIds();

    String selectParentTreeNames(String parentIds);

    List<Object> getTierByTierId(Long assetTreeTierId);

    List<BusinessSystemTreeVo> getBusinessTreeFirstListWithSystem(BusinessSystemTreeVo businessSystemTreeVo);

    /**
    *@author liushan
    *@Description 固定获取业务树第一二层
    *@Date 2019/9/3
    *@Param []
    *@return java.util.List<cn.pioneeruniverse.project.vo.BusinessSystemTreeVo>
    **/
    List<BusinessSystemTreeVo> getFirstSecondTree();

    /**
    *@author liushan
    *@Description 获取子节点
    *@Date 2019/9/3
    *@Param []
    *@return java.util.List<cn.pioneeruniverse.project.vo.BusinessSystemTreeVo>
    **/
    List<BusinessSystemTreeVo> getTreeByParentId(@Param("parentFrontId") Long parentFrontId);

    List<BusinessSystemTreeVo> getListByParentId(BusinessSystemTreeVo vo);

    List<BusinessSystemTreeVo> getFirstTree();

    List<BusinessSystemTreeVo> getListByParentIdWithName(BusinessSystemTreeVo businessSystemTreeVo);

    BusinessSystemTreeVo getBusinessByParentIdAndName(@Param("parentId") Long parentId, @Param("name") String name);

    List<String> selectAllParentIdsByAssetTreeId(Long assetTreeId);
}