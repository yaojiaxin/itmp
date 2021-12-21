package cn.pioneeruniverse.project.controller.assetLibrary;

import cn.pioneeruniverse.common.entity.AjaxModel;
import cn.pioneeruniverse.project.entity.TblSystemDirectory;
import cn.pioneeruniverse.project.entity.TblSystemDirectoryDocument;
import cn.pioneeruniverse.project.service.assetsLibrary.SystemDirectoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 17:33 2019/12/26
 * @Modified By:
 */
@RestController
@RequestMapping("documentLibrary")
public class DocumentLibraryController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentLibraryController.class);

    @Autowired
    private SystemDirectoryService systemDirectoryService;

    /**
     * @param projectId
     * @return java.util.List<cn.pioneeruniverse.project.entity.TblSystemDirectory>
     * @Description 获取文档库文档树目录
     * @MethodName getDirectoryTree
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/12/27 11:37
     */
    @RequestMapping(value = "getDirectoryTree", method = RequestMethod.POST)
    public List<TblSystemDirectory> getDirectoryTree(@RequestParam("projectId") Long projectId) {
        return systemDirectoryService.getDirectoryTreeForDocumentLibrary(projectId);
    }

    /**
     * @param request
     * @param tblSystemDirectory
     * @return cn.pioneeruniverse.common.entity.AjaxModel
     * @Description 新增文档目录
     * @MethodName addDirectory
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/12/27 11:38
     */
    @RequestMapping(value = "addDirectory", method = RequestMethod.POST)
    public AjaxModel addDirectory(HttpServletRequest request, TblSystemDirectory tblSystemDirectory) {
        try {
            systemDirectoryService.addSystemDirectory(request, tblSystemDirectory);
            return AjaxModel.SUCCESS();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxModel.FAIL(e);
        }
    }

    /**
     * @param directoryId
     * @return cn.pioneeruniverse.common.entity.AjaxModel
     * @Description 删除文档目录
     * @MethodName delDirectory
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/12/27 16:05
     */
    @RequestMapping(value = "delDirectory", method = RequestMethod.POST)
    public AjaxModel delDirectory(@RequestParam("directoryId") Long directoryId) {
        try {
            systemDirectoryService.delSystemDirectory(directoryId);
            return AjaxModel.SUCCESS();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxModel.FAIL(e);
        }
    }

    /**
     * @param request
     * @param directoryId
     * @param directoryName
     * @return cn.pioneeruniverse.common.entity.AjaxModel
     * @Description 更新文档目录名
     * @MethodName updateDirectoryName
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/12/27 16:30
     */
    @RequestMapping(value = "updateDirectoryName", method = RequestMethod.POST)
    public AjaxModel updateDirectoryName(HttpServletRequest request, @RequestParam("directoryId") Long directoryId, @RequestParam("directoryName") String directoryName) {
        try {
            systemDirectoryService.updateSystemDirectoryName(request, directoryId, directoryName);
            return AjaxModel.SUCCESS();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxModel.FAIL(e);
        }
    }

    /**
     * @param response
     * @param tblSystemDirectoryDocument
     * @return void
     * @Description 下载文档
     * @MethodName downloadDocument
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/12/27 17:39
     */
    @RequestMapping(value = "downloadDocument", method = RequestMethod.GET)
    public void downloadDocument(HttpServletResponse response, TblSystemDirectoryDocument tblSystemDirectoryDocument) {
        try {
            systemDirectoryService.downLoadDocument(systemDirectoryService.getDocumentById(tblSystemDirectoryDocument.getId()), response);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * @param files
     * @param tblSystemDirectoryDocument
     * @param request
     * @return void
     * @Description 上传新文档
     * @MethodName uploadDocument
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/12/31 10:58
     */
    @RequestMapping(value = "uploadNewDocument", method = RequestMethod.POST)
    public AjaxModel uploadDocument(@RequestParam("file") MultipartFile[] file, TblSystemDirectoryDocument tblSystemDirectoryDocument, HttpServletRequest request) {
        try {
            systemDirectoryService.upLoadNewDocument(file, tblSystemDirectoryDocument, request);
            return AjaxModel.SUCCESS();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxModel.FAIL(e);
        }
    }


    /**
     * @param files
     * @param tblSystemDirectoryDocument
     * @param request
     * @return cn.pioneeruniverse.common.entity.AjaxModel
     * @Description 覆盖上传文档
     * @MethodName coverUploadOldDocument
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/12/31 11:01
     */
    @RequestMapping(value = "coverUploadOldDocument", method = RequestMethod.POST)
    public AjaxModel coverUploadOldDocument(@RequestParam("documentFiles") MultipartFile[] files, TblSystemDirectoryDocument tblSystemDirectoryDocument, HttpServletRequest request) {
        try {
            systemDirectoryService.coverUploadOldDocument(files, tblSystemDirectoryDocument, request);
            //TODO 插入历史记录
            return AjaxModel.SUCCESS();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxModel.FAIL(e);
        }
    }
    @RequestMapping(value = "getDocumentTypes", method = RequestMethod.POST)
    public  Map<String, Object>   getDocumentTypes(@RequestParam("directoryId") Long directoryId) {
        Map<String, Object> map=new HashMap<String, Object>();
        map.put("data", systemDirectoryService.getDocumentTypes(directoryId));
    	return map;
    }
    
    @RequestMapping(value = "getDocumentFile", method = RequestMethod.POST)
    public  Map<String, Object>   getDocumentFile(@RequestParam("id") Long id) {
        Map<String, Object> map=new HashMap<String, Object>();
        map.put("data", systemDirectoryService.getDocumentFile(id));
        map.put("type", "documents");
    	return map;
    }

}
