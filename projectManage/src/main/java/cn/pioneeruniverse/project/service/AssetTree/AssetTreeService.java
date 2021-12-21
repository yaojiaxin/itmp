package cn.pioneeruniverse.project.service.AssetTree;

import cn.pioneeruniverse.project.entity.TblAssetTreeTier;
import cn.pioneeruniverse.project.vo.BusinessSystemTreeVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author:liushan
 * Date: 2019/5/14 下午 3:11
 */
public interface AssetTreeService {
    /**
     * 业务树层级列表
     * @param assetTreeType
     * @return
     */
    List<TblAssetTreeTier> getAssetTreeList(Integer assetTreeType);

    void saveAssetTree(TblAssetTreeTier assetTreeTier, HttpServletRequest request) throws Exception;

    void editAssetTree(TblAssetTreeTier assetTreeTier, HttpServletRequest request);

    Map<String,Object> getTreeListByTierId(BusinessSystemTreeVo assetTreeTier);
}
