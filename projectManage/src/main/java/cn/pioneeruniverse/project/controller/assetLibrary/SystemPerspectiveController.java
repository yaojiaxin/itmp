package cn.pioneeruniverse.project.controller.assetLibrary;

import cn.pioneeruniverse.common.entity.AjaxModel;
import cn.pioneeruniverse.project.service.assetsLibrary.SystemDirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description: 系统视角
 * @Date: Created in 10:41 2019/11/12
 * @Modified By:
 */
@RestController
@RequestMapping("systemPerspective")
public class SystemPerspectiveController {

    private static final Logger logger = LoggerFactory.getLogger(SystemPerspectiveController.class);

    @Autowired
    private SystemDirectoryService systemDirectoryService;

    /**
     * @param systemIds 系统Id
     * @param request
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Description 获取系统视角文档树
     * @MethodName getSystemDocumentTree
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/12/26 10:02
     */
    @RequestMapping(value = "getDirectoryTree", method = RequestMethod.POST)
    public List<Map<String, Object>> getDirectoryTree(String systemIds, HttpServletRequest request) {
        return systemDirectoryService.getDirectoryTree(systemIds, request);
    }

    /**
     * @param systemId     关联系统Id
     * @param documentType 文档类型表主键
     * @return cn.pioneeruniverse.common.entity.AjaxModel
     * @Description 获取系统视角文档树某一目录下的文档
     * @MethodName getFilesUnderDirectory
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/12/26 10:11
     */
    @RequestMapping(value = "getFilesUnderDirectory", method = RequestMethod.POST)
    public AjaxModel getFilesUnderDirectory(@RequestParam("systemId") Long systemId, @RequestParam("documentType") Long documentType) {
        try {
            return AjaxModel.SUCCESS(systemDirectoryService.getFilesUnderDirectory(systemId, documentType));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxModel.FAIL(e);
        }
    }

    /**
     * @param documentId 文档id
     * @return cn.pioneeruniverse.common.entity.AjaxModel
     * @Description 获取文档历史
     * @MethodName getDocumentHistory
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/12/26 17:30
     */
    @RequestMapping(value = "getDocumentHistory", method = RequestMethod.POST)
    public AjaxModel getDocumentHistory(@RequestParam Long documentId) {
        try {
            return AjaxModel.SUCCESS(systemDirectoryService.getSystemDirectoryDocumentHistory(documentId));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxModel.FAIL(e);
        }
    }

}
