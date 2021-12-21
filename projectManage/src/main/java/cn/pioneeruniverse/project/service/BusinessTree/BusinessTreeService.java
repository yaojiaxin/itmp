package cn.pioneeruniverse.project.service.BusinessTree;

import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.project.vo.BusinessSystemTreeVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author:liushan
 * Date: 2019/5/14 下午 2:02
 */
public interface BusinessTreeService {
    JqGridPage<BusinessSystemTreeVo> getBusinessTreeListByTierNumber(JqGridPage<BusinessSystemTreeVo> baseEntityJqGridPage, BusinessSystemTreeVo businessSystemTreeVo);

    JqGridPage<BusinessSystemTreeVo> getTreeList(JqGridPage<BusinessSystemTreeVo> baseEntityJqGridPage, BusinessSystemTreeVo businessSystemTreeVo);

    /**
     *@author liushan
     *@Description 显示左边菜单，默认显示两层
     *@Date 2019/9/3
     *@Param [nodeId]
     *@return java.util.Map<java.lang.String,java.lang.Object>
     **/
    List<BusinessSystemTreeVo> getBusinessTreeList(Long nodeId, Integer type);

    Map<String, Object> updateBusinessTree(BusinessSystemTreeVo businessSystemTreeVo, HttpServletRequest request) throws Exception;

    Map<String, Object> insertBusinessTree(BusinessSystemTreeVo assetBusinessTree, HttpServletRequest request) throws Exception;

    BusinessSystemTreeVo getEntityInfo(Long id);

    Map<String, Object> getChildNodes(Long businessId);

    void removeBusinessTrees(Long[] ids, HttpServletRequest request) throws Exception;

    Map<String, Object> importBusinessTree(String bsVo, MultipartFile file, HttpServletRequest request) throws Exception;

    /**
     *@author liushan
     *@Description 导入模板
     *@Date 2019/9/5
     *@Param [bsVo, file, request]
     *@return java.util.Map<java.lang.String,java.lang.Object>
     **/
    Map<String,Object> importTemplet(String bsVo, MultipartFile file, HttpServletRequest request) throws Exception;

    Long getNextAssetTreeId(Long assetTreeTierId, Long assetTreeType) throws Exception;

    void export(Integer type, BusinessSystemTreeVo businessSystemTreeVo, HttpServletResponse response, HttpServletRequest request) throws Exception;

    /**
    *@author liushan
    *@Description 导入当前节点的子节点
    *@Date 2019/9/6
    *@Param [sbVo, assetTreeType, currentUserId, type, file]
    *@return java.util.Map<java.lang.String,java.lang.Object>
    **/
    Map<String, Object> importTempletWithBS(String sbVo, Integer assetTreeType, Long currentUserId, int type, MultipartFile file) throws Exception;

    /**
    *@author liushan
    *@Description 导入当前所有层级的子节点
    *@Date 2019/9/6
    *@Param [bsVo, assetTreeType, currentUserId, type, file]
    *@return java.util.Map<java.lang.String,java.lang.Object>
    **/
    Map<String,Object> importTemplet(String bsVo, Integer assetTreeType, Long currentUserId, int type, MultipartFile file) throws Exception;
}
