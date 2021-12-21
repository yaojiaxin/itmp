package cn.pioneeruniverse.project.dao.mybatis;

import cn.pioneeruniverse.project.entity.TblAssetSystemTree;
import cn.pioneeruniverse.project.vo.BusinessSystemTreeVo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface TblAssetSystemTreeMapper extends BaseMapper<TblAssetSystemTree> {
    int deleteByPrimaryKey(Long id);

    int insertSelective(TblAssetSystemTree record);

    TblAssetSystemTree selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(TblAssetSystemTree record);

    int updateByPrimaryKey(TblAssetSystemTree record);

    List<TblAssetSystemTree> selectListByBusId(Long businessSystemTreeId);

    List<BusinessSystemTreeVo> getSystemTreeListByTier(BusinessSystemTreeVo assetTreeTier);

    void insertBusinessSystemTreeVo(BusinessSystemTreeVo businessSystemTreeVo);

    BusinessSystemTreeVo getEntityInfo(Long id);

    List<BusinessSystemTreeVo> selectChildTier(String parentIds);

    void updateBusinessSystemTreeVo(BusinessSystemTreeVo treeVo);

    List<String> selectAllParentIds();

    Map<String,Object> getAllTierNameWithTree(@Param("parentIds") String parentIds);

    List<Object> getTierByTierId(Long assetTreeTierId);

    List<BusinessSystemTreeVo> getTiersByParentIds(String parentIds);

    List<BusinessSystemTreeVo> getSystemTreeList(BusinessSystemTreeVo businessSystemTreeVo);

    String selectParentTreeNames(String parentIds);

    BusinessSystemTreeVo getBusinessByParentIdAndName(@Param("parentId") Long parentId, @Param("name") String name);

    List<BusinessSystemTreeVo> getListByParentId(BusinessSystemTreeVo businessSystemTreeVo);

    List<BusinessSystemTreeVo> getFirstTree();

    List<BusinessSystemTreeVo> getTreeByParentId(Long nodeId);

    List<BusinessSystemTreeVo> getFirstSecondTree();

    List<BusinessSystemTreeVo> getListByParentIdWithName(BusinessSystemTreeVo businessSystemTreeVo);

    List<String> selectAllParentIdsByAssetTreeId(Long assetTreeId);

    BusinessSystemTreeVo getSystemByCode(String businessSystemTreeCode);

    /**
    *@author liushan
    *@Description 根据id查询所有的子节点
    *@Date 2019/9/17
    *@Param [id]
    *@return java.util.List<cn.pioneeruniverse.project.vo.BusinessSystemTreeVo>
    **/
    List<BusinessSystemTreeVo>  getSystemTreeById(@Param("id") Long id,@Param("likeId") String likeId);

	List<TblAssetSystemTree> getModuleList(Map<String, Object> map);
}