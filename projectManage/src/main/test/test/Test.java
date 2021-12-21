package test;

import base.BaseJunit;
import cn.pioneeruniverse.common.dto.TblDataDicDTO;
import cn.pioneeruniverse.common.utils.CollectionUtil;
import cn.pioneeruniverse.common.utils.JsonUtil;
import cn.pioneeruniverse.common.utils.RedisUtils;
import cn.pioneeruniverse.common.utils.S3Util;
import cn.pioneeruniverse.project.dao.mybatis.ProjectGroupMapper;
import cn.pioneeruniverse.project.dao.mybatis.assetLibrary.*;
import cn.pioneeruniverse.project.entity.*;
import cn.pioneeruniverse.project.service.assetsLibrary.SystemDirectoryAuthorityService;
import cn.pioneeruniverse.project.service.assetsLibrary.SystemDirectoryService;
import cn.pioneeruniverse.project.service.oamproject.OamProjectService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.ByteArrayInputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: yaojiaxin [yaojiaxin@pioneerservice.cn]
 * @Description:
 * @Date: Created in 11:14 2019/1/11
 * @Modified By:
 */
public class Test extends BaseJunit {

    @Autowired
    ProjectGroupMapper projectGroupMapper;

    @Autowired
    SystemDirectoryDocumentDao systemDirectoryDocumentDao;

    @Autowired
    SystemDirectoryDao systemDirectoryDao;

    @Autowired
    SystemDirectoryDocumentAttachmentDao systemDirectoryDocumentAttachmentDao;

    @Autowired
    SystemDirectoryDocumentHistoryDao systemDirectoryDocumentHistoryDao;

    @Autowired
    OamProjectService oamProjectService;

    @Autowired
    SystemDirectoryService systemDirectoryService;

    @Autowired
    SystemDirectoryUserAuthorityDao systemDirectoryUserAuthorityDao;

    @Autowired
    S3Util s3Util;

    @Autowired
    RedisUtils redisUtils;

    @org.junit.Test
    public void test() {
        //projectGroupMapper.untyingProjectGroup(1L);
        //List<Map<String, Object>> list = projectGroupMapper.findAllProjectGroupForZTree();
        //String a = "";
        //List<TblSystemDirectoryDocument> list = systemDirectoryDocumentDao.getDirectoryDocumentsByDirectoryId(1L);
      /*  List<TblSystemDirectoryDocumentHistory> list = systemDirectoryDocumentHistoryDao.getSystemDirectoryHistoryByDocumentId(2L);
        list = (List<TblSystemDirectoryDocumentHistory>) CollectionUtil.collect(list, new Transformer() {
            int i = 1;
            @Override
            public Object transform(Object o) {
                TblSystemDirectoryDocumentHistory tblSystemDirectoryDocumentHistory = (TblSystemDirectoryDocumentHistory) o;
                int k = i % 2;
                if (k == 0) {
                    tblSystemDirectoryDocumentHistory.setStatus(2);
                } else {
                    tblSystemDirectoryDocumentHistory.setStatus(1);
                }
                i++;
                return tblSystemDirectoryDocumentHistory;
            }
        });*/

     /*   int c = 3;
        int k = 3 / 2;
        //List<TblSystemDirectory> list = systemDirectoryService.getSystemDirectoryTreeList(null, 2L);
        String u = "";
        TblSystemDirectory tblSystemDirectory = new TblSystemDirectory();
        tblSystemDirectory.setId(1L);
        tblSystemDirectory.setDirName("01-立项结项与采购1");
        systemDirectoryDao.updateSystemDirectoryName(tblSystemDirectory);*/
     /*   List<TblSystemDirectory> list = systemDirectoryService.getSystemDirectoryTreeList("1");
        String a = "";*/
     /*   TblSystemDirectory tblSystemDirectory = new TblSystemDirectory();
        tblSystemDirectory.setId(100L);
        tblSystemDirectory.setDirName("testinsert");
        tblSystemDirectory.setParentId(2L);
        tblSystemDirectory.setOrderNumber(1);
        tblSystemDirectory.setTierNumber(2);
        systemDirectoryDao.addSystemDirectory(tblSystemDirectory);*/
        //String a = "";

        //List<TblSystemDirectoryDocumentAttachment> list2 = systemDirectoryDocumentAttachmentDao.getNumOfAttachmentsByDocumentId(1L, 5);
        //List<TblSystemDirectoryDocumentHistory> list = systemDirectoryDocumentHistoryDao.getSystemDirectoryHistoryByDocumentId(1L);
        //List<TblSystemDirectoryDocument> list = systemDirectoryDocumentDao.getAllDirectoryDocuments(1L,2L);
      /*  TblSystemDirectory tblSystemDirectory = new TblSystemDirectory();
        tblSystemDirectory.setId(1L);
        Map<String, Object> map = systemDirectoryService.getSystemDirectoryDetail(tblSystemDirectory);*/
/*        List<TblSystemDirectory> list = systemDirectoryService.getSystemDirectoryListForProjectVision(173L);
        String a = "";
        /*///*//*String d = "test_1573552531306";*/
 /*       for (int i = 0; i < 30; i++) {
                int finalI = i;
            new Thread(() -> {*/
           /*     TblSystemDirectoryDocument tblSystemDirectoryDocument = new TblSystemDirectoryDocument();
                tblSystemDirectoryDocument.setSystemDirectoryId(12L);
                tblSystemDirectoryDocument.setProjectType(2);
                tblSystemDirectoryDocument.setDocumentName("yjxTest15");
                tblSystemDirectoryDocument.setDocumentVersion(1);
                tblSystemDirectoryDocument.setSaveType(1);
                tblSystemDirectoryDocument.setStatus(1);
                tblSystemDirectoryDocument.setCreateDate(new Timestamp(new Date().getTime()));
                systemDirectoryService.insertOrUpdate(tblSystemDirectoryDocument);*/
        /*    }, "thread" + i).start();
        }
        try {
            Thread.currentThread().sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/


        /*  @Override
    @Transactional(readOnly = false)
    public void insertOrUpdate(TblSystemDirectoryDocument newDocument) {
        TblSystemDirectoryDocument oldDocument = systemDirectoryDocumentDao.selectUploadedDocumentForUpdate(newDocument);
        System.out.println(new TimeStamp(new Date().getTime()) + " " + Thread.currentThread().getName() + ":" + oldDocument == null);
        if (oldDocument == null) {
            systemDirectoryDocumentDao.insertDirectoryDocument(newDocument);
            systemDirectoryDocumentHistoryDao.insertDirectoryDocumentHistory(newDocument);
        } else {
            BeanUtils.copyProperties(newDocument, oldDocument, "id", "documentVersion");
            oldDocument.setDocumentVersion(oldDocument.getDocumentVersion() + 1);
            systemDirectoryDocumentDao.updateForSameDirectoryDocument(oldDocument);
            systemDirectoryDocumentHistoryDao.insertDirectoryDocumentHistory(oldDocument);
        }
    }*/
       /* TblSystemDirectoryUserAuthority tblSystemDirectoryUserAuthority = new TblSystemDirectoryUserAuthority();
        tblSystemDirectoryUserAuthority.setId(1L);
        tblSystemDirectoryUserAuthority.setSystemDirectoryId(1L);
        tblSystemDirectoryUserAuthority.setUserId(1L);
        tblSystemDirectoryUserAuthority.setReadAuth(2);
        tblSystemDirectoryUserAuthority.setWriteAuth(2);
        systemDirectoryUserAuthorityDao.insert(tblSystemDirectoryUserAuthority);*/
      /*  systemDirectoryUserAuthorityDao.update(tblSystemDirectoryUserAuthority, new EntityWrapper<TblSystemDirectoryUserAuthority>().allEq(new HashMap<String, Object>() {{
            put("ID", tblSystemDirectoryUserAuthority.getUserId());
            put("SYSTEM_DIRECTORY_ID", tblSystemDirectoryUserAuthority.getSystemDirectoryId());
        }}));*/
/*
        List<TblSystemDirectoryUserAuthority> list = systemDirectoryUserAuthorityDao.selectList(new EntityWrapper<TblSystemDirectoryUserAuthority>().allEq(new HashMap<String, Object>() {{
            put("SYSTEM_DIRECTORY_ID", 1L);
            put("STATUS", 1);
        }}));
        List<Long> userIds = (List<Long>) CollectionUtil.collect(list, o -> ((TblSystemDirectoryUserAuthority) o).getUserId());
        String a = "";*/

        TblSystemDirectoryUserAuthority tblSystemDirectoryUserAuthority = new TblSystemDirectoryUserAuthority();
        tblSystemDirectoryUserAuthority.setSystemDirectoryId(1L);
        tblSystemDirectoryUserAuthority.setUserId(1L);
        tblSystemDirectoryUserAuthority = systemDirectoryUserAuthorityDao.selectOne(tblSystemDirectoryUserAuthority);
        String a = "";
    }

    @org.junit.Test
    public void test2() {
        //systemDirectoryService.getFilesUnderDirectory(1L, 1, 1);
        //List<Map<String, Object>> list = systemDirectoryService.getDirectoryTree("1,2,3", null);
    /*  ist<TblSystemDirectory> list = systemDirectoryDao.getDirectoryTreeForDocumentLibrary(1L);
        String a="";*/

        //TblSystemDirectory tblSystemDirectory = systemDirectoryDao.selectOne(new TblSystemDirectory())
        try {
            systemDirectoryService.updateSystemDirectoryName(null, 1L, "bbabb");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String b = "";

        //map转list
/*
        return map.entrySet().stream().filter(x -> documentTypeList.contains(x.getKey())).map(x -> {
            TblDataDicDTO tblDataDicDTO = new TblDataDicDTO();
            tblDataDicDTO.setValueCode(x.getKey());
            tblDataDicDTO.setValueName(x.getValue());
            return tblDataDicDTO;
        }).collect(Collectors.toList());*/

//map 过滤
      /*  Map<Boolean, List<TblSystemDirectoryDocument>> predicateMap = systemDirectoryDocuments.stream().collect(Collectors.groupingBy(x -> ObjectUtils.equals(x.getSaveType(), 2)));
        List<TblSystemDirectoryDocument> markDownList = predicateMap.get(true) == null ? new ArrayList<>() : predicateMap.get(true);
        List<TblSystemDirectoryDocument> otherDocList = predicateMap.get(false) == null ? new ArrayList<>() : predicateMap.get(false);
*/
    }


}
