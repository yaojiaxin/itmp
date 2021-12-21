package cn.pioneeruniverse.project.controller;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.project.service.BusinessTree.BusinessTreeService;
import cn.pioneeruniverse.project.vo.BusinessSystemTreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:业务系统树维护
 * Author:liushan
 * Date: 2019/5/14 下午 1:58
 */
@RestController
@RequestMapping("business")
public class BusinessTreeController extends BaseController {

    @Autowired
    private BusinessTreeService businessTreeService;

    /**
     * 查询列表
     * @param request
     * @param response
     * @param businessSystemTreeVo
     * @return
     */
    @PostMapping(value="list")
    public JqGridPage<BusinessSystemTreeVo> list(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 BusinessSystemTreeVo businessSystemTreeVo){
        JqGridPage<BusinessSystemTreeVo> jqGridPage = null;
        try {
            jqGridPage = businessTreeService.getBusinessTreeListByTierNumber(new JqGridPage<>(request, response), businessSystemTreeVo);
        }catch (Exception e){
            logger.error("message" + ":" + e.getMessage(), e);
        }
        return jqGridPage;
    }

    @PostMapping(value="treeList")
    public JqGridPage<BusinessSystemTreeVo> treeList(HttpServletRequest request,
                                                     HttpServletResponse response,
                                                     BusinessSystemTreeVo businessSystemTreeVo){
        JqGridPage<BusinessSystemTreeVo> jqGridPage = null;
        try {
            jqGridPage = businessTreeService.getTreeList(new JqGridPage<>(request, response), businessSystemTreeVo);
        }catch (Exception e){
            logger.error("message" + ":" + e.getMessage(), e);
        }
        return jqGridPage;
    }


    /**
    *@author liushan
    *@Description 显示左边菜单，默认显示两层
    *@Date 2019/9/3
    *@Param [nodeId]
    *@return java.util.Map<java.lang.String,java.lang.Object>
    **/
    @PostMapping(value="getBusinessTreeList")
    public List<BusinessSystemTreeVo> getBusinessTreeList(Long nodeId,Integer type){
        try {
            return businessTreeService.getBusinessTreeList(nodeId,type);
        }catch (Exception e){
            handleException(e, "查询列表失败！");
        }
        return null;
    }

    /**
     * 新增条目
     * @param businessSystemTreeVo
     * @return
     */
    @PostMapping(value="insert")
    public Map<String, Object> insertBusinessTree(BusinessSystemTreeVo businessSystemTreeVo,HttpServletRequest request){
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = businessTreeService.insertBusinessTree(businessSystemTreeVo,request);
            result.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return handleException(e, "新增条目失败");
        }
        return result;
    }

    @PostMapping(value="update")
    public Map<String, Object> updateBusinessTree(BusinessSystemTreeVo businessSystemTreeVo,HttpServletRequest request){
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = businessTreeService.updateBusinessTree(businessSystemTreeVo,request);
            result.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return handleException(e, "编辑条目失败");
        }
        return result;
    }

    /**
     * 获取实体类信息
     * @param businessSystemTreeVo
     * @return
     */
    @PostMapping(value="getEntityInfo")
    public Map<String, Object> getEntityInfo(BusinessSystemTreeVo businessSystemTreeVo){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            BusinessSystemTreeVo businessTreeServiceEntityInfo = businessTreeService.getEntityInfo(businessSystemTreeVo.getId());
            result.put("entityInfo", businessTreeServiceEntityInfo);
        } catch (Exception e) {
            return handleException(e, "获取条目信息失败");
        }
        return result;
    }

    @PostMapping(value="getChildNodes")
    public Map<String, Object> getChildNodes(Long businessId){
        Map<String, Object> result = new HashMap<String, Object>();

        try {
            result  = businessTreeService.getChildNodes(businessId);
            result.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return handleException(e, "获取子节点失败");
        }
        return result;
    }

    /**
     * 移除条目
     * @param ids
     * @param request
     * @return
     */
    @PostMapping(value="remove")
    public Map<String, Object> removeBusinessTrees(Long[] ids,HttpServletRequest request){
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            businessTreeService.removeBusinessTrees(ids,request);
        } catch (Exception e) {
            return handleException(e, "编辑条目失败");
        }
        return result;
    }

    /**
     * 导入条目
     * @param file
     * @param request
     * @return
     */
    @PostMapping(value="import")
    public Map<String, Object> importBusinessTree(String bsVo,
                                                  @RequestParam("file") MultipartFile file,
                                                  HttpServletRequest request){
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = businessTreeService.importBusinessTree(bsVo,file,request);
        } catch (Exception e) {
            return handleException(e, "导入条目失败,请检查EXCEL数据格式是否正确");
        }
        return result;
    }

    /**
    *@author liushan
    *@Description 导入模板
    *@Date 2019/9/5
    *@Param [bsVo, file, request]
    *@return java.util.Map<java.lang.String,java.lang.Object>
    **/
    @PostMapping(value="importTemplet")
    public Map<String, Object> importTemplet(String bsVo,
                                              @RequestParam("file") MultipartFile file,
                                              HttpServletRequest request){
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = businessTreeService.importTemplet(bsVo,file,request);
        } catch (Exception e) {
            return handleException(e, "导入条目失败,请检查EXCEL数据格式是否正确");
        }
        return result;
    }

    @RequestMapping(value = "export")
    public void export(Integer type,BusinessSystemTreeVo businessSystemTreeVo,HttpServletResponse response,HttpServletRequest request) throws Exception{
        businessTreeService.export(type,businessSystemTreeVo,response,request) ;
    }

}
