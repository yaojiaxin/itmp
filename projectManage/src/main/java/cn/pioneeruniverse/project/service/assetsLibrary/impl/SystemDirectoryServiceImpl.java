package cn.pioneeruniverse.project.service.assetsLibrary.impl;

import cn.pioneeruniverse.common.utils.*;
import cn.pioneeruniverse.project.dao.mybatis.assetLibrary.SystemDirectoryDao;
import cn.pioneeruniverse.project.dao.mybatis.assetLibrary.SystemDirectoryDocumentAttachmentDao;
import cn.pioneeruniverse.project.dao.mybatis.assetLibrary.SystemDirectoryDocumentDao;
import cn.pioneeruniverse.project.dao.mybatis.assetLibrary.SystemDirectoryDocumentHistoryDao;
import cn.pioneeruniverse.project.entity.*;
import cn.pioneeruniverse.project.feignInterface.DevTaskInterface;
import cn.pioneeruniverse.project.service.assetsLibrary.SystemDirectoryService;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 11:57 2019/11/12
 * @Modified By:
 */
@Service("systemDirectoryService")
public class SystemDirectoryServiceImpl extends ServiceImpl<SystemDirectoryDao, TblSystemDirectory> implements SystemDirectoryService {

    @Autowired
    private SystemDirectoryDao systemDirectoryDao;

    @Autowired
    private SystemDirectoryDocumentDao systemDirectoryDocumentDao;

    @Autowired
    private SystemDirectoryDocumentHistoryDao systemDirectoryDocumentHistoryDao;

    @Autowired
    private SystemDirectoryDocumentAttachmentDao systemDirectoryDocumentAttachmentDao;

    @Autowired
    private DevTaskInterface devTaskInterface;

    @Autowired
    private S3Util s3Util;

    @Value("${s3.documentBucket}")
    private String documentBucket;
    @Autowired
    private RedisUtils redisUtils;
    
    @Override
    @Transactional(readOnly = true)
    public List<TblSystemDirectory> getDirectoryTreeForDocumentLibrary(Long projectId) {
        return systemDirectoryDao.getDirectoryTreeForDocumentLibrary(projectId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getDirectoryTree(String systemIds, HttpServletRequest request) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (StringUtils.isEmpty(systemIds)) {
            String token = CommonUtil.getToken(request);
            List<Map<String, Object>> systems = devTaskInterface.getMyProjectSystems(token);
            if (CollectionUtil.isNotEmpty(systems)) {
                systemIds = StringUtils.join(CollectionUtil.collect(systems, o -> ((Map<String, Object>) o).get("id")), ",");
            }
        }
        List<TblSystemDirectoryDocument> documentTypes = systemDirectoryDocumentDao.getDocumentTypesBySystemIds(systemIds);
        if (CollectionUtil.isNotEmpty(documentTypes)) {
            Map<Long, List<TblSystemDirectoryDocument>> documentTypesGroups = documentTypes.stream().collect(Collectors.groupingBy(TblSystemDirectoryDocument::getSystemId));
            for (Map.Entry<Long, List<TblSystemDirectoryDocument>> entry : documentTypesGroups.entrySet()) {
                result.add(new LinkedHashMap<String, Object>() {{
                    put("id", "SYSTEM-" + entry.getKey());
                    put("pId", null);
                    put("name", devTaskInterface.getSystemNameById(entry.getKey()));
                }});
                for (TblSystemDirectoryDocument tblSystemDirectoryDocument : entry.getValue()) {
                    result.add(new LinkedHashMap<String, Object>() {{
                        put("id", "DOCUMENTTYPE-" + tblSystemDirectoryDocument.getDocumentType());
                        put("pId", "SYSTEM-" + entry.getKey());
                        put("name", tblSystemDirectoryDocument.getDocumentTypeName());
                        put("systemId", entry.getKey());
                        put("documentTypeId", tblSystemDirectoryDocument.getDocumentType());
                    }});
                }
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getFilesUnderDirectory(Long systemId, Long documentType) {
        Map<String, Object> result = new HashMap<>();
        List<TblSystemDirectoryDocument> documents = systemDirectoryDocumentDao.getDocumentsUnderDocumentTypesDirectory(systemId, documentType);
        if (CollectionUtil.isNotEmpty(documents)) {
            //当前集合中markdown有且仅有一个返回markdown数据
            Map<Boolean, List<TblSystemDirectoryDocument>> predicateMap = documents.stream().collect(Collectors.groupingBy(x -> ObjectUtils.equals(x.getSaveType(), 2)));
            List<TblSystemDirectoryDocument> markDownList = predicateMap.get(true) == null ? new ArrayList<>() : predicateMap.get(true);
            if (CollectionUtil.isNotEmpty(markDownList) && markDownList.size() == 1) {
                result.put("type", "markDown");
                result.put("markDownDocumentId", markDownList.get(0).getId());
            } else {
                result.put("type", "documents");
                result.put("documents", documents);
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemDirectoryDocumentAttachment> getAllAttachments(Long documentId) {
        return systemDirectoryDocumentAttachmentDao.getNumOfAttachmentsByDocumentId(documentId, null);
    }

    @Override
    @Transactional(readOnly = false)
    public TblSystemDirectory addSystemDirectory(HttpServletRequest request, TblSystemDirectory tblSystemDirectory) {
        tblSystemDirectory.preInsertOrUpdate(request);
        //TODO 文档类型 ?(继承父节点)
        systemDirectoryDao.addSystemDirectory(tblSystemDirectory);
        return tblSystemDirectory;
    }

    @Override
    @Transactional(readOnly = false)
    public void delSystemDirectory(Long directoryId) throws Exception {
        TblSystemDirectory tblSystemDirectory = systemDirectoryDao.selectById(directoryId);
        if (ObjectUtils.equals(tblSystemDirectory.getCreateType(), 1)) {
            throw new Exception("该目录为自动创建目录，不得删除！");
        }
        //获取当前目录及其子目录ID集合
        List<Long> directoryIds = systemDirectoryDao.getAllSonDirectoryIds(tblSystemDirectory);
        //判断上述集合中是否存在关联文档
        if (CollectionUtil.isNotEmpty(directoryIds)) {
            Boolean existRelatedDocuments = systemDirectoryDocumentDao.existRelatedDocuments(directoryIds);
            if (existRelatedDocuments) {
                throw new Exception("该目录下存在文档，不得删除！");
            } else {
                systemDirectoryDao.delSystemDirectoriesByIds(directoryIds);
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void updateSystemDirectoryName(HttpServletRequest request, Long directoryId, String directoryName) throws Exception {
        TblSystemDirectory tblSystemDirectory = systemDirectoryDao.selectById(directoryId);
        if (ObjectUtils.equals(tblSystemDirectory.getCreateType(), 1)) {
            throw new Exception("该目录为自动创建目录，不得修改！");
        }
        TblSystemDirectory newDirectory = new TblSystemDirectory();
        newDirectory.setDirName(directoryName);
        newDirectory.preInsertOrUpdate(request);
        systemDirectoryDao.update(newDirectory, new EntityWrapper<TblSystemDirectory>().allEq(new HashMap<String, Object>() {{
            put("ID", tblSystemDirectory.getId());
            put("PROJECT_TYPE", 2);
            put("CREATE_TYPE", 2);
            put("STATUS", 1);
        }}));
    }

    @Override
    @Transactional(readOnly = true)
    public TblSystemDirectoryDocument getDocumentById(Long id) {
        return systemDirectoryDocumentDao.selectById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TblSystemDirectoryDocumentHistory> getSystemDirectoryDocumentHistory(Long documentId) {
        return systemDirectoryDocumentHistoryDao.getSystemDirectoryHistoryByDocumentId(documentId);
    }

    @Override
    @Transactional(readOnly = false)
    public void upLoadNewDocument(MultipartFile[] files, TblSystemDirectoryDocument tblSystemDirectoryDocument, HttpServletRequest request)  {
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    try {
						this.upLoadDocument(file, tblSystemDirectoryDocument, request);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    tblSystemDirectoryDocument.preInsertOrUpdate(request);
                    tblSystemDirectoryDocument.setDocumentVersion(0);
                    //新写添加
                    systemDirectoryDocumentDao.insertDirectoryDocument(tblSystemDirectoryDocument);
                }
            }
        }
    }

    @Override
    @Transactional(readOnly = false)
    public void coverUploadOldDocument(MultipartFile[] files, TblSystemDirectoryDocument
            tblSystemDirectoryDocument, HttpServletRequest request) throws IOException {
        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    this.upLoadDocument(file, tblSystemDirectoryDocument, request);
                    tblSystemDirectoryDocument.preInsertOrUpdate(request);
                    systemDirectoryDocumentDao.updateVersionForCoverUploadDocument(tblSystemDirectoryDocument);
                }
            }
        }
    }

    @Override
    public void downLoadDocument(TblSystemDirectoryDocument tblSystemDirectoryDocument, HttpServletResponse
            response) {
        s3Util.downObject(tblSystemDirectoryDocument.getDocumentS3Bucket(),
                tblSystemDirectoryDocument.getDocumentS3Key(),
                tblSystemDirectoryDocument.getDocumentName(), response);
    }

    @Override
    public void upLoadDocument(MultipartFile file, TblSystemDirectoryDocument tblSystemDirectoryDocument, HttpServletRequest request) throws IOException {
        InputStream inputStream = file.getInputStream();
        String fileNameOld = file.getOriginalFilename();
        if (BrowserUtil.isMSBrowser(request)) {
            fileNameOld = fileNameOld.substring(fileNameOld.lastIndexOf("\\") + 1);
            tblSystemDirectoryDocument.setDocumentName(fileNameOld);
        }
        tblSystemDirectoryDocument.setDocumentS3Bucket(documentBucket);
        tblSystemDirectoryDocument.setDocumentS3Key(s3Util.putObject(documentBucket, fileNameOld, inputStream));
    }

	@Override
	public List<Map<String, Object>>  getDocumentTypes(Long directoryId) {
		List<Map<String, Object>> list=new ArrayList<>();
		TblSystemDirectory tblSystemDirectory=systemDirectoryDao.getDocumentType(directoryId);
		 Object object = redisUtils.get("DOCUMENT_TYPE1");
		 Map<String, Object> mapsource=new HashMap<String, Object>();
		 if (object != null &&!"".equals( object)) {//redis有直接从redis中取
			 mapsource = JSON.parseObject(object.toString());
		 }
		 String documentType=tblSystemDirectory.getDocumentType();
		 String[] documentTypeArr = null;
		 if(documentType!=""&&documentType!=null) {
			 documentTypeArr = documentType.split(",");
		 }
		for(int i=0;i<documentTypeArr.length;i++) {
		 for(String key:mapsource.keySet()) {
			 if(key.equals(documentTypeArr[i])) {
				 Map<String, Object> map=new HashMap<String, Object>();
				 map.put("valueCode", key);
				 map.put("valueName", mapsource.get(key).toString());
				 list.add(map);
			 }
		 }
		}
		return list;
	}

	@Override
	public List<TblSystemDirectoryDocument> getDocumentFile(Long directoryId) {
		// TODO Auto-generated method stub
		 List<TblSystemDirectoryDocument> list=systemDirectoryDocumentDao.getDirectoryDocumentsByDirectoryId(directoryId);
		return list;
	}

}
