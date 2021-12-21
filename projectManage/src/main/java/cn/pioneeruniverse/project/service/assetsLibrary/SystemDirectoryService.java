package cn.pioneeruniverse.project.service.assetsLibrary;

import cn.pioneeruniverse.project.entity.TblSystemDirectory;
import cn.pioneeruniverse.project.entity.TblSystemDirectoryDocument;
import cn.pioneeruniverse.project.entity.TblSystemDirectoryDocumentAttachment;
import cn.pioneeruniverse.project.entity.TblSystemDirectoryDocumentHistory;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 11:53 2019/11/12
 * @Modified By:
 */
public interface SystemDirectoryService {

    List<TblSystemDirectory> getDirectoryTreeForDocumentLibrary(Long projectId);
    
    List<Map<String, Object>>  getDocumentTypes(Long directoryId);

    List<Map<String, Object>> getDirectoryTree(String systemIds, HttpServletRequest request);

    Map<String, Object> getFilesUnderDirectory(Long systemId, Long documentType);

    List<TblSystemDirectoryDocumentAttachment> getAllAttachments(Long documentId);

    TblSystemDirectory addSystemDirectory(HttpServletRequest request, TblSystemDirectory tblSystemDirectory);

    List<TblSystemDirectoryDocumentHistory> getSystemDirectoryDocumentHistory(Long documentId);

    void delSystemDirectory(Long directoryId) throws Exception;

    void updateSystemDirectoryName(HttpServletRequest request, Long directoryId, String directoryName) throws Exception;

    TblSystemDirectoryDocument getDocumentById(Long id);

    void downLoadDocument(TblSystemDirectoryDocument tblSystemDirectoryDocument, HttpServletResponse response);

    void upLoadNewDocument(MultipartFile[] files, TblSystemDirectoryDocument tblSystemDirectoryDocument, HttpServletRequest request) ;

    void coverUploadOldDocument(MultipartFile[] files, TblSystemDirectoryDocument tblSystemDirectoryDocument, HttpServletRequest request) throws IOException;

    void upLoadDocument(MultipartFile file, TblSystemDirectoryDocument tblSystemDirectoryDocument, HttpServletRequest request) throws IOException;
   
    List<TblSystemDirectoryDocument> getDocumentFile(Long directoryId);
}
