package cn.pioneeruniverse.project.controller;

import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.controller.BaseController;
import cn.pioneeruniverse.common.dto.SystemTreeVo;
import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.project.entity.TblAssetSystemTree;
import cn.pioneeruniverse.project.service.SystemTree.SystemTreeService;
import cn.pioneeruniverse.project.vo.BusinessSystemTreeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 系统树维护
 * Author:liushan
 * Date: 2019/5/14 下午 2:11
 */
@RestController
@RequestMapping("systemTree")
public class SystemTreeController extends BaseController {

    @Autowired
    private SystemTreeService systemTreeService;

    /**
     * 查询列表
     *
     * @param request
     * @param response
     * @param businessSystemTreeVo
     * @return
     */
    @PostMapping(value = "list")
    public JqGridPage<BusinessSystemTreeVo> list(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 BusinessSystemTreeVo businessSystemTreeVo) {
        JqGridPage<BusinessSystemTreeVo> jqGridPage = null;
        try {
            jqGridPage = systemTreeService.getSystemTreeListByTier(new JqGridPage<>(request, response), businessSystemTreeVo);
        } catch (Exception e) {
            logger.error("message" + ":" + e.getMessage(), e);
            return (JqGridPage<BusinessSystemTreeVo>) super.handleException(e, "查询列表失败！");
        }
        return jqGridPage;
    }

    @PostMapping(value = "treeList")
    public JqGridPage<BusinessSystemTreeVo> treeList(HttpServletRequest request,
                                                     HttpServletResponse response,
                                                     BusinessSystemTreeVo businessSystemTreeVo) {
        JqGridPage<BusinessSystemTreeVo> jqGridPage = null;
        try {
            jqGridPage = systemTreeService.getTreeList(new JqGridPage<>(request, response), businessSystemTreeVo);
        } catch (Exception e) {
            logger.error("message" + ":" + e.getMessage(), e);
        }
        return jqGridPage;
    }


    /**
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author liushan
     * @Description 显示左边菜单，默认显示两层
     * @Date 2019/9/3
     * @Param [nodeId]
     **/
    @PostMapping(value = "getBusinessTreeList")
    public List<BusinessSystemTreeVo> getSystemTreeList(Long nodeId, Integer type) {
        try {
            return systemTreeService.getSystemTreeList(nodeId, type);
        } catch (Exception e) {
            handleException(e, "查询列表失败！");
        }
        return null;
    }

    /**
     * @return java.util.List<cn.pioneeruniverse.project.vo.BusinessSystemTreeVo>
     * @author liushan
     * @Description 根据系统编号查询，该系统的所有节点
     * @Date 2019/9/17
     * @Param [businessSystemTreeVo]
     **/
    @PostMapping(value = "getSystemTreeByCode")
    public Map<String, Object> getSystemTreeByCode(String systemCode) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            result.put("data", systemTreeService.getSystemTreeByCode(systemCode));
        } catch (Exception e) {
            return handleException(e, "查询失败！");
        }
        return result;
    }

    @PostMapping(value = "getAssetSystemTreeBySystemCode")
    public List<BusinessSystemTreeVo> getAssetSystemTreeBySystemCode(String systemCode) throws Exception {
        return systemTreeService.getSystemTreeByCode(systemCode);
    }

    @PostMapping(value = "getAssetSystemTreeById")
    public BusinessSystemTreeVo getAssetSystemTreeById(Long id) {
        BusinessSystemTreeVo vo = new BusinessSystemTreeVo();
        vo.setId(id);
        return systemTreeService.getEntityInfo(vo);
    }

    /**
     * 新增条目
     *
     * @param businessSystemTreeVo
     * @return
     */
    @PostMapping(value = "insert")
    public Map<String, Object> insertSystemTree(BusinessSystemTreeVo businessSystemTreeVo, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = systemTreeService.insertSystemTree(businessSystemTreeVo, request);
            result.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return handleException(e, "新增条目失败");
        }
        return result;
    }

    @PostMapping(value = "update")
    public Map<String, Object> updateSystemTree(BusinessSystemTreeVo businessSystemTreeVo, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = systemTreeService.updateSystemTree(businessSystemTreeVo, request);
            result.put("status", Constants.ITMP_RETURN_SUCCESS);
        } catch (Exception e) {
            return handleException(e, "编辑条目失败");
        }
        return result;
    }

    /**
     * 获取实体类信息
     *
     * @param businessSystemTreeVo
     * @return
     */
    @PostMapping(value = "getEntityInfo")
    public Map<String, Object> getEntityInfo(BusinessSystemTreeVo businessSystemTreeVo) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            BusinessSystemTreeVo businessTreeServiceEntityInfo = systemTreeService.getEntityInfo(businessSystemTreeVo);
            result.put("entityInfo", businessTreeServiceEntityInfo);
        } catch (Exception e) {
            return handleException(e, "新增条目失败");
        }
        return result;
    }

    /**
     * 移除条目
     *
     * @param ids
     * @param request
     * @return
     */
    @PostMapping(value = "remove")
    public Map<String, Object> removeSystemTrees(Long[] ids, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", Constants.ITMP_RETURN_SUCCESS);
        try {
            systemTreeService.removeSystemTrees(ids, request);
        } catch (Exception e) {
            return handleException(e, "编辑条目失败");
        }
        return result;
    }

    /**
     * 导入条目
     *
     * @param file
     * @param request
     * @return
     */
    @PostMapping(value = "import")
    public Map<String, Object> importSystemTree(String bsVo,
                                                @RequestParam("file") MultipartFile file,
                                                HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = systemTreeService.importSystemTree(bsVo, file, request);
        } catch (Exception e) {
            return handleException(e, "导入条目失败,请检查EXCEL数据格式是否正确");
        }
        return result;
    }

    /**
     * @return java.util.Map<java.lang.String,java.lang.Object>
     * @author liushan
     * @Description 导入模板
     * @Date 2019/9/5
     * @Param [bsVo, file, request]
     **/
    @PostMapping(value = "importTemplet")
    public Map<String, Object> importTemplet(String bsVo,
                                             @RequestParam("file") MultipartFile file,
                                             HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            result = systemTreeService.importTemplet(bsVo, file, request);
        } catch (Exception e) {
            return handleException(e, "导入条目失败,请检查EXCEL数据格式是否正确");
        }
        return result;
    }

    @RequestMapping(value = "export")
    public void export(Integer type, BusinessSystemTreeVo businessSystemTreeVo, HttpServletResponse response, HttpServletRequest request) throws Exception {
        systemTreeService.export(type, businessSystemTreeVo, response, request);
    }


    @PostMapping(value = "insertFirstSystem")
    public Map<String, Object> insertFirstSystem(@RequestBody SystemTreeVo systemTreeVo, Long currentUserId) {
        Map<String, Object> result = new HashMap<String, Object>();
        try {
            systemTreeService.insertFirstSystem(systemTreeVo, currentUserId);
        } catch (Exception e) {
            return handleException(e, "同步第一层系统信息失败！");
        }
        return result;
    }

    //模块弹窗
    @RequestMapping(value = "moduleTable", method = RequestMethod.POST)
    public Map<String, Object> moduleTable(TblAssetSystemTree systemTree, Integer pageNumber, Integer pageSize, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            List<TblAssetSystemTree> list = systemTreeService.getModuleList(systemTree, request, pageNumber, pageSize);
            List<TblAssetSystemTree> list2 = systemTreeService.getModuleList(systemTree, request, 1, Integer.MAX_VALUE);
            map.put("rows", list);
            map.put("total", list2.size());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            map.put("status", "fail");
        }
        return map;
    }

}
