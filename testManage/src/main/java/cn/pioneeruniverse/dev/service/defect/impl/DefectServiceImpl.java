package cn.pioneeruniverse.dev.service.defect.impl;

import cn.pioneeruniverse.common.annotion.DataSource;
import cn.pioneeruniverse.common.bean.PropertyInfo;
import cn.pioneeruniverse.common.bean.ReflectFieledType;
import cn.pioneeruniverse.common.constants.Constants;
import cn.pioneeruniverse.common.constants.DicConstants;
import cn.pioneeruniverse.common.dto.TblAttachementInfoDTO;
import cn.pioneeruniverse.common.entity.JqGridPage;
import cn.pioneeruniverse.common.utils.*;
import cn.pioneeruniverse.common.velocity.tag.VelocityDataDict;
import cn.pioneeruniverse.dev.dao.mybatis.*;
import cn.pioneeruniverse.dev.dto.AssetSystemTreeDTO;
import cn.pioneeruniverse.dev.entity.*;

import cn.pioneeruniverse.dev.feignInterface.TestManageToDevManageInterface;
import cn.pioneeruniverse.dev.feignInterface.TestManageToProjectManageInterface;
import cn.pioneeruniverse.dev.feignInterface.TestManageToSystemInterface;
import cn.pioneeruniverse.dev.service.defect.DefectService;
import cn.pioneeruniverse.dev.vo.DefectInfoVo;
import cn.pioneeruniverse.dev.vo.SynDefectInfo;
import cn.pioneeruniverse.dev.yiranUtil.DefectExport;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Description:????????????service???
 * Author:liushan
 * Date: 2018/12/10 ?????? 1:45
 */
@Transactional(readOnly = true)
@Service("defectService")
public class DefectServiceImpl implements DefectService {

    public final static Logger logger = LoggerFactory.getLogger(DefectServiceImpl.class);

    private static final String LOG_TYPE_1 = "????????????";
    private static final String LOG_TYPE_2 = "????????????";
    private static final String LOG_TYPE_3 = "????????????";
    @Autowired
    private TblUserInfoMapper tblUserInfoMapper;
    @Autowired
    private TblDefectInfoMapper defectInfoMapper;
    @Autowired
    private TblSystemInfoMapper systemInfoMapper;
    @Autowired
    private TblDefectLogMapper defectLogMapper;
    @Autowired
    private TblDefectLogAttachementMapper logAttachementMapper;
    @Autowired
    private TblDefectRemarkMapper defectRemarkMapper;
    @Autowired
    private TblDefectRemarkAttachementMapper remarkAttachementMapper;
    @Autowired
    private TblDefectAttachementMapper attachementMapper;
    @Autowired
    private TblTestTaskMapper tblTestTaskMapper;
    @Autowired
    private TblRequirementFeatureMapper requirementFeatureMapper;
    @Autowired
    private TblCommissioningWindowMapper windowMapper;
    @Autowired
    private TestManageToDevManageInterface toDevManageInterface;
    @Autowired
    private TblTestSetCaseExecuteMapper tblTestSetCaseExecuteMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private S3Util s3Util;

    @Autowired
    private TestManageToProjectManageInterface testManageToProjectManageInterface;

    @Autowired
    private TestManageToDevManageInterface testManageToDevManageInterface;

    @Autowired
    private TestManageToSystemInterface testManageToSystemInterface;

    @Value("${itmp.devTest.integration}")
    private Boolean integration;

    @Value("${itmp.dev.url}")
    private String devTestUrl;

    @Value("${itsm.severityLevel}")
    private String itsmSeverityLevel;

    @Value("${itsm.defectType}")
    private String itsmDefectType;

    @Override
    public JqGridPage<DefectInfoVo> findDefectListPage(JqGridPage<DefectInfoVo> page, DefectInfoVo defectInfo) throws Exception {
        DefectInfoVo defectInfoForExport = page.filtersToEntityField(defectInfo);

        int rows = page.getJqGridPrmNames().getRows();
        int pageNum = page.getJqGridPrmNames().getPage();
        List<TblDefectInfo> list = defectInfoMapper.findDefectList(defectInfoForExport, (pageNum - 1) * rows, rows);
        int count = defectInfoMapper.countFindDefectList(defectInfoForExport);

        for (TblDefectInfo tblDefectInfo : list) {
            Set<Long> set = new HashSet<>();
            Long defectId = tblDefectInfo.getId();
            Long systemId = tblDefectInfo.getSystemId();
            List<Long> userIds1 = defectInfoMapper.findUserIdBySystemId(systemId);
            Long userId2 = defectInfoMapper.findUserIdByDefectId(defectId);
            Long systemId2 = defectInfoMapper.findSystemIdByDefectId(defectId);//??????????????????????????????
            List<Long> userIds3 = defectInfoMapper.findUserIdBySystemId(systemId2);
            set.addAll(userIds1);
            set.add(userId2);
            set.addAll(userIds3);
            tblDefectInfo.setUserSet(set);
        }

        PageInfo<TblDefectInfo> pageInfo = new PageInfo<>(list);
        pageInfo.setPages((count + rows - 1) / rows);
        pageInfo.setTotal(count);
        pageInfo.setPageNum(pageNum);
        page.processDataForResponse(pageInfo);
        return page;
    }

    public TblDefectInfo getDefectEntity(Long defectId) {
        TblDefectInfo defectInfo = defectInfoMapper.getDefectEntity(defectId);
        //?????????????????????
        if (defectInfo != null && defectInfo.getProjectGroupId() != null) {
            Map<String, Object> projectGroup = testManageToProjectManageInterface.getProjectGroupByProjectGroupId(defectInfo.getProjectGroupId());
            if (projectGroup != null && !projectGroup.isEmpty()) {
                defectInfo.setProjectGroupName((String) projectGroup.get("projectGroupName"));
            }
        }
        //???????????????????????????????????????
        if (defectInfo != null && defectInfo.getAssetSystemTreeId() != null) {
            Map<String, Object> assetSystemTree = testManageToProjectManageInterface.getAssetSystemTreeById(defectInfo.getAssetSystemTreeId());
            if (assetSystemTree != null && !assetSystemTree.isEmpty()) {
                defectInfo.setAssetSystemTreeName((String)assetSystemTree.get("businessSystemTreeName"));
            }
        }
        //???????????????????????????
        if (defectInfo != null && defectInfo.getDetectedSystemVersionId() != null) {
            Map<String, Object> detectedSystemVersion = testManageToDevManageInterface.getSystemVersionBySystemVersionId(defectInfo.getDetectedSystemVersionId());
            if (detectedSystemVersion != null && !detectedSystemVersion.isEmpty()) {
                defectInfo.setDetectedSystemVersionName((String) detectedSystemVersion.get("version"));
            }
        }
        //?????????????????????????????????
        if (defectInfo != null && defectInfo.getRepairSystemVersionId() != null) {
            Map<String, Object> repairSystemVersion = testManageToDevManageInterface.getSystemVersionBySystemVersionId(defectInfo.getRepairSystemVersionId());
            if (repairSystemVersion != null && !repairSystemVersion.isEmpty()) {
                defectInfo.setRepairSystemVersionName((String) repairSystemVersion.get("version"));
            }
        }
        return defectInfo;
    }

    @Override
    public Map<String, Object> getAllProject(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        List<TblProjectInfo> projects = new ArrayList<>();
        if (new CommonUtils().currentUserWithAdmin(request) == false) {
            projects = defectInfoMapper.getAllProjectByCurrentUserId(CommonUtil.getCurrentUserId(request));
        } else {
            projects = defectInfoMapper.getAllProject();
        }
        map.put("projects", projects);
        return map;
    }

    /**
     * ??????????????????
     *
     * @param key
     * @return
     */
    public Map<String, Object> getDicKey(String key) {
        Object object = redisUtils.get(key);
        Map<String, Object> map = JSON.parseObject(object.toString());
        return map;
    }

    /**
     * ?????????????????????
     *
     * @param defectId
     * @return
     */
    @Override
    public TblDefectInfo getDefectById(Long defectId) {
        return defectInfoMapper.getDefectById(defectId);
    }

    /**
     * ??????????????? ?????????????????????????????? 1???,????????????????????????????????? 2???
     *
     * @param tblDefectInfo
     * @param request
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Map<String, Object> insertDefect(TblDefectInfo tblDefectInfo, MultipartFile[] files, HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap<>();
        tblDefectInfo.setSubmitUserId(CommonUtil.getCurrentUserId(request));
        tblDefectInfo.setSubmitDate(DateUtil.getDate(new Date().toString(), "yyyy-mm-dd"));
        tblDefectInfo = (TblDefectInfo) CommonUtil.setBaseValue(tblDefectInfo, request);

        tblDefectInfo.setDefectCode(getDefectCode());
        defectInfoMapper.insertDefect(tblDefectInfo);

        Long defectId = tblDefectInfo.getId();

        // ????????????
        TblDefectLog defectLog = new TblDefectLog(defectId, LOG_TYPE_1, request);
        defectLogMapper.insertDefectLog(defectLog);

        Map<String, Object> sysncMap = new HashMap<>();
        Map<String, Object> defectMap = new HashMap<>();

        defectMap.put("log", JSON.toJSON(defectLog));
        defectMap.put("defect", JSON.toJSON(tblDefectInfo));
        defectMap.put("opt", "insert");

        map.put("defectId", defectId);
        map.put("logId", defectLog.getId());

        Map<String, Object> filesMap = this.updateFiles(files, defectId, defectLog.getId(), request);
        filesMap.put("opt", "insert");

        sysncMap.put("defectMap", defectMap);
        sysncMap.put("filesMap", filesMap);
        String url = devTestUrl + "devManage/defect/syncDefectWithFiles";
        this.syncDefectWithFiles(url, sysncMap);

        //?????????????????????????????????????????????
        try{
            this.sendAddMessage(tblDefectInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
//        if (tblDefectInfo.getDefectStatus() == 2){
//            try{
//                this.sendAddMessage(tblDefectInfo);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }


        return map;
    }


    /**
     * ????????????
     *
     * @param tblDefectInfo
     * @param request
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Map<String, Object> updateDefect(MultipartFile[] files, TblDefectInfo tblDefectInfo, HttpServletRequest request, String removeAttIds) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> sysncMap = new HashMap<>();
        Map<String, Object> defectMap = new HashMap<>();
        Long defectId = tblDefectInfo.getId();

        TblDefectInfo oldDefect = defectInfoMapper.getDefectByIdForLog(defectId);
        tblDefectInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
        tblDefectInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
        defectInfoMapper.updateDefect(tblDefectInfo);

        TblDefectInfo newDefect = defectInfoMapper.getDefectByIdForLog(defectId);
        //newDefect.setFieldTemplate(tblDefectInfo.getFieldTemplate());

        //????????????????????????????????????????????????
        try{
            this.sendAddMessage(tblDefectInfo);
        }catch (Exception e){
            e.printStackTrace();
        }
//        if (tblDefectInfo.getDefectStatus() == 10){
//            try{
//                this.sendAddMessage(tblDefectInfo);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }



        // ??????
        TblDefectLog defectLog = new TblDefectLog(defectId, LOG_TYPE_3, CommonUtils.updateFieledsReflect(oldDefect, newDefect, null),request);
        defectLogMapper.insertDefectLog(defectLog);

        defectMap.put("log", JSON.toJSON(defectLog));
        defectMap.put("defect", JSON.toJSON(newDefect));
        defectMap.put("opt", "update");

        map.put("defectId", defectId);
        map.put("logId", defectLog.getId());

        Map<String, Object> filesMap = this.updateFiles(files, defectId, defectLog.getId(), request);
        filesMap.put("opt", "insert");

        sysncMap.put("defectMap", defectMap);
        sysncMap.put("filesMap", filesMap);
        String url = devTestUrl + "devManage/defect/syncDefectWithFiles";
        this.syncDefectWithFiles(url, sysncMap);
        if (removeAttIds != null && !removeAttIds.equals("")) {
            String[] str = removeAttIds.split(",");
            Long[] ids = (Long[]) ConvertUtils.convert(str, Long.class);
            this.removeAtts(ids, defectId, defectLog.getId(), request);
        }
        //???????????????????????????????????????????????????
        if (!ObjectUtils.equals(oldDefect.getDefectStatus(), newDefect.getDefectStatus())) {
            sendMessageForDefectStatusUpdate(newDefect);
        }
        return map;
    }



    /**
     *  ??????????????????
     * @param tblDefectInfo
     */
    private void sendAddMessage(TblDefectInfo tblDefectInfo){
        logger.info("???????????????"+tblDefectInfo);
        Map<String,Object> map=new HashMap<>();
        map.put("messageTitle","?????????????????????");
        map.put("messageContent",tblDefectInfo.getDefectCode()+"|"+tblDefectInfo.getDefectSummary());
        map.put("messageReceiverScope",2);
        //???????????? 3-- ????????????
        map.put("messageSource",3);
        map.put("systemId",tblDefectInfo.getSystemId());
        map.put("messageReceiver",tblDefectInfo.getTestUserId());
        logger.info("???????????????"+map);
        testManageToSystemInterface.insertMessage(JSON.toJSONString(map));

    }




    /**
     * ????????????????????????
     * ?????????????????????????????????????????????????????????
     *
     * @param tblDefectInfo
     * @param defectRemark
     * @param request
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Long updateDefectwithTBC(TblDefectInfo tblDefectInfo, TblDefectRemark defectRemark, HttpServletRequest request) throws Exception {

        TblDefectInfo oldDefect = defectInfoMapper.getDefectByIdForLog(tblDefectInfo.getId());
        tblDefectInfo.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
        tblDefectInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
        TblDefectInfo newDefect = new TblDefectInfo();
        // ???????????????:????????????????????????????????????
        String logType = LOG_TYPE_2;
        if (tblDefectInfo.getDefectStatus() != null) {
            if (tblDefectInfo.getDefectStatus().intValue() == 2 && tblDefectInfo.getAssignUserId() != null) {
                logType = LOG_TYPE_3;
                defectInfoMapper.updateDefectAssignUser(tblDefectInfo);
                newDefect = defectInfoMapper.getDefectByIdForLog(tblDefectInfo.getId());
            } else {
                // ???????????????????????????????????????????????????
                if (tblDefectInfo.getDefectStatus().intValue() == 3 && tblDefectInfo.getRejectReason() != null) {
                    defectInfoMapper.updateDefectRejectReason(tblDefectInfo);
                }

                // ??????????????????????????????????????????????????????
                if (tblDefectInfo.getDefectStatus().intValue() == 4) {
                    defectInfoMapper.updateDefectStatus(tblDefectInfo);
                }

                // ????????????????????? ????????????????????? ???????????????
                if (tblDefectInfo.getDefectStatus().intValue() == 5 && tblDefectInfo.getSolveStatus() != null) {
                    defectInfoMapper.updateDefectSolveStatus(tblDefectInfo);
                }
                newDefect = defectInfoMapper.getDefectByIdForLog(tblDefectInfo.getId());
            }
        }

        if (StringUtils.isBlank(defectRemark.getDefectRemark())) {
            defectRemark.setDefectRemark("?????????????????????");
        }
        Map<String, Object> remarkMap = new HashMap<>();
        remarkMap.put("remark", defectRemark.getDefectRemark());
        TblDefectLog defectLog = new TblDefectLog(tblDefectInfo.getId(), logType, CommonUtils.updateFieledsReflect(oldDefect, newDefect, remarkMap), request);
        defectLogMapper.insertDefectLog(defectLog);

        Map<String, Object> sysncMap = new HashMap<>();
        sysncMap.put("log", JSON.toJSON(defectLog));
        sysncMap.put("defect", JSON.toJSON(newDefect));
        String devUrl = devTestUrl + "devManage/defect/syncDefect";
        this.syncOpt(devUrl, "update", sysncMap);

        // ???????????????????????????????????????????????????,??????????????????
        if (ObjectUtils.equals(tblDefectInfo.getDefectStatus(), 3) && tblDefectInfo.getRejectReason() != null) {
            sendMessageForDefectStatusUpdate(oldDefect);
        }
        // ??????????????????????????????????????????????????????,??????????????????
        if (ObjectUtils.equals(tblDefectInfo.getDefectStatus(), 4)) {
            sendMessageForDefectStatusUpdate(oldDefect);
        }
        // ????????????????????? ????????????????????? ????????????????????????????????????????????????
        if (ObjectUtils.equals(tblDefectInfo.getDefectStatus(), 5) && tblDefectInfo.getSolveStatus() != null) {
            sendMessageForDefectStatusUpdate(oldDefect);
        }
        return defectLog.getId();
    }

    /**
     * ???????????????
     *
     * @param tblDefectInfo
     * @param defectRemark
     * @param request
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Long updateDefectStatus(TblDefectInfo tblDefectInfo, TblDefectRemark defectRemark, HttpServletRequest request) throws Exception {

        TblDefectInfo oldDefect = defectInfoMapper.getDefectByIdForLog(tblDefectInfo.getId());

        // ????????????????????????????????????????????????????????? ?????????????????????????????????
        defectInfoMapper.updateDefectStatus(tblDefectInfo);
        TblDefectInfo newDefect = defectInfoMapper.getDefectByIdForLog(tblDefectInfo.getId());

        if (StringUtils.isBlank(defectRemark.getDefectRemark())) {
            defectRemark.setDefectRemark("?????????????????????");
        }
        Map<String, Object> remarkMap = new HashMap<>();
        remarkMap.put("remark", defectRemark.getDefectRemark());
        TblDefectLog defectLog = new TblDefectLog(tblDefectInfo.getId(), LOG_TYPE_2, CommonUtils.updateFieledsReflect(oldDefect, newDefect, remarkMap), request);
        defectLogMapper.insertDefectLog(defectLog);

        Map<String, Object> sysncMap = new HashMap<>();
        sysncMap.put("log", JSON.toJSON(defectLog));
        sysncMap.put("defect", JSON.toJSON(newDefect));
        String devUrl = devTestUrl + "devManage/defect/syncDefect";
        this.syncOpt(devUrl, "update", sysncMap);

        sendMessageForDefectStatusUpdate(newDefect);
        return defectLog.getId();
    }

    /**
     * ??????????????????
     *
     * @param id
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void removeDefect(Long id, HttpServletRequest request) throws Exception {
        this.removeDefectById(id);
        String url = devTestUrl + "devManage/defect/syncDefect";
        TblDefectInfo tblDefectInfo = new TblDefectInfo();
        tblDefectInfo.setId(id);

        Map<String, Object> sysncMap = new HashMap<>();
        sysncMap.put("defect", JSON.toJSON(tblDefectInfo));
        this.syncOpt(url, "remove", sysncMap);
    }

    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void deleteDefect(Long id, HttpServletRequest request) throws Exception {
        Integer rows = defectInfoMapper.deleteDefectById(id);
        if (rows > 0) {
            String url = devTestUrl + "devManage/defect/syncDefect";
            TblDefectInfo tblDefectInfo = new TblDefectInfo();
            tblDefectInfo.setId(id);
            Map<String, Object> sysncMap = new HashMap<>();
            sysncMap.put("defect", JSON.toJSON(tblDefectInfo));
            this.syncOpt(url, "delete", sysncMap);
            redisUtils.remove(Constants.TMP_TEST_DEFECT_CODE);
            logger.warn("??????????????????:" + id + "???????????????????????????" + rows);
        }
    }

    /**
     * ?????????????????? ?????????
     *
     * @param id
     */
    public void removeDefectById(Long id) throws Exception {
        defectInfoMapper.removeDefect(id);
        attachementMapper.removeAttachements(id);
        // ?????????????????????id
        Long[] remarkId = defectRemarkMapper.findRemarkByDefectId(id);
        if (remarkId != null && remarkId.length > 0) {
            remarkAttachementMapper.removeRemarkAttachements(remarkId);
            defectRemarkMapper.removeDefectRemark(remarkId);
        }
    }

    /**
     * ?????????????????????????????????????????????
     */
    @Override
    @DataSource(name = "tmpDataSource")
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void updateTmpDefectByWorkId(TblDefectInfo tblDefectInfo) throws Exception {
        defectInfoMapper.updateDefectByWorkId(tblDefectInfo);
        if (integration == true) {
            SpringContextHolder.getBean(DefectService.class).updateItmpDefectByWorkId(tblDefectInfo);
        }
    }

    @Override
    @DataSource(name = "itmpDataSource")
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void updateItmpDefectByWorkId(TblDefectInfo tblDefectInfo) throws Exception {
        defectInfoMapper.updateDefectByWorkId(tblDefectInfo);
    }


    /**
     * ????????????
     *
     * @param files
     * @param defectId
     * @param logId
     * @param request
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public Map<String, Object> updateFiles(MultipartFile[] files, Long defectId, Long logId, HttpServletRequest request) throws Exception {
        Map<String, Object> syscMap = new HashMap<>();
        if (files.length > 0 && files != null) {
            List<TblDefectAttachement> list = new ArrayList<TblDefectAttachement>();
            List<TblDefectLogAttachement> logList = new ArrayList<TblDefectLogAttachement>();
            for (MultipartFile file : files) {
                if (!Objects.isNull(file) || !file.isEmpty()) {
                    TblAttachementInfoDTO attachementInfoDTO = updateFile(file);
                    TblDefectAttachement defectAttachement = new TblDefectAttachement();
                    defectAttachement = (TblDefectAttachement) CommonUtil.setBaseValue(defectAttachement, request);
                    defectAttachement.setDefectId(defectId);
                    defectAttachement.setFileType(attachementInfoDTO.getFileType());
                    defectAttachement.setFileS3Key(attachementInfoDTO.getFileS3Key());
                    defectAttachement.setFileS3Bucket(s3Util.getDefectBucket());
                    defectAttachement.setFilePath(attachementInfoDTO.getFilePath());
                    defectAttachement.setFileNameOld(attachementInfoDTO.getFileNameOld());
                    attachementMapper.insertDefectAttachement(defectAttachement);

                    TblDefectLogAttachement logAttachement = insertLogAttachement(attachementInfoDTO, logId, request);
                    list.add(defectAttachement);
                    logList.add(logAttachement);
                }
            }

            syscMap.put("defectAttachement", list);
            syscMap.put("logAttachement", logList);
            /*String url = devTestUrl + "devManage/defect/syncDefectAtt";
            this.syncOpt(url, "insert", syscMap);*/
        }

        return syscMap;
    }

    /**
     * ????????????????????????
     *
     * @param files
     * @param logId
     * @param request
     * @return
     */
    @Override
    @Transactional(readOnly = false)
    public String updateRemarkLogFiles(MultipartFile[] files, Long logId, HttpServletRequest request) throws Exception {
        if (files.length > 0 && files != null) {
            Map<String, Object> syscMap = new HashMap<>();
            List<TblDefectLogAttachement> logList = new ArrayList<TblDefectLogAttachement>();
            for (MultipartFile file : files) {
                if (Objects.isNull(file) || file.isEmpty()) {
                    return "NULL";
                }
                TblAttachementInfoDTO attachementInfoDTO = updateFile(file);

                TblDefectLogAttachement logAttachement = insertLogAttachement(attachementInfoDTO, logId, request);
                logList.add(logAttachement);
            }
            syscMap.put("logAttachement", logList);
            String url = devTestUrl + "devManage/defect/syncDefectAtt";
            this.syncOpt(url, "insert", syscMap);
        }
        return "SUCCESS";
    }

    /**
     * ??????????????????
     *
     * @param attachementInfoDTO
     * @param logId
     */
    private TblDefectLogAttachement insertLogAttachement(TblAttachementInfoDTO attachementInfoDTO, Long logId, HttpServletRequest request) throws Exception {
        TblDefectLogAttachement logAttachement = new TblDefectLogAttachement();
        logAttachement.setDefectLogId(logId);
        logAttachement.setFileType(attachementInfoDTO.getFileType());
        logAttachement.setFileS3Key(attachementInfoDTO.getFileS3Key());
        logAttachement.setFileS3Bucket(s3Util.getDefectBucket());
        logAttachement.setFilePath(attachementInfoDTO.getFilePath());
        logAttachement.setFileNameOld(attachementInfoDTO.getFileNameOld());
        logAttachement = (TblDefectLogAttachement) CommonUtil.setBaseValue(logAttachement, request);
        logAttachementMapper.insertSelective(logAttachement);
        return logAttachement;
    }

    /**
     * ??????????????????
     *
     * @param file
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = false)
    public TblAttachementInfoDTO updateFile(MultipartFile file) throws Exception {
        TblAttachementInfoDTO attachementInfoDTO = new TblAttachementInfoDTO();
        InputStream inputStream = file.getInputStream();
        String fileName = file.getOriginalFilename();
        fileName = fileName.substring(file.getOriginalFilename().lastIndexOf("\\") + 1);
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);//?????????
        String oldFileName = fileName.substring(0, fileName.lastIndexOf("."));
        Random random = new Random();
		String i = String.valueOf(random.nextInt());
        String keyname = s3Util.putObject(s3Util.getDefectBucket(), i, inputStream);
        attachementInfoDTO.setFileS3Key(keyname);
        attachementInfoDTO.setFilePath(attachementInfoDTO.getFilePath() + fileName);
        attachementInfoDTO.setFileNameOld(fileName);
        attachementInfoDTO.setFileType(fileType);
        return attachementInfoDTO;
    }

    /**
     * ??????????????????????????????
     *
     * @param path
     * @return
     */
    @Override
    public TblDefectAttachement getDefectAttByUrl(String path) throws Exception {
        return attachementMapper.getDefectAttByUrl(path);
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param defectId
     * @return
     */
    @Override
    public List<TblDefectAttachement> findAttListByDefectId(Long defectId) throws Exception {
        return attachementMapper.findAttListByDefectId(defectId);
    }

    /**
     * ??????id??????????????????
     *
     * @param defectId
     * @return
     */
    @Override
    public List<TblDefectLog> getDefectLogsById(Long defectId) throws Exception {
        return defectLogMapper.getDefectLogsById(defectId);
    }

    /**
     * ???????????????????????????
     *
     * @param defectId
     * @return
     */
    @Override
    public Map<String, Object> getDefectRecentLogById(Long defectId) throws Exception {
        Map<String, Object> result = new HashMap<>();
        // ?????????????????????????????????????????????id
        Long logId = defectLogMapper.getDefectMaxLogId(defectId);
        TblDefectInfo defectInfo = defectInfoMapper.getDefectById(defectId);
        // ?????????????????????????????????????????????
        if (defectInfo.getTestTaskId() != null) {
            TblRequirementFeature feature = requirementFeatureMapper.getFeatureByTestTaskId(defectInfo.getTestTaskId());
            result.put("feature", feature);
        }

        result.put("data", defectLogMapper.getDefectRecentLogById(logId));
        return result;
    }

    /**
     * ????????????
     *
     * @param
     * @param defectId
     * @param request
     */
    @Override
    @Transactional(readOnly = false, rollbackFor = Exception.class)
    public void removeAtts(Long[] ids, Long defectId, Long logId, HttpServletRequest request) throws Exception {
        if (ids != null && ids.length != 0) {

            Map<String, Object> syscMap = new HashMap<>();
            List<TblDefectAttachement> list = new ArrayList<TblDefectAttachement>();
            List<TblDefectLogAttachement> logList = new ArrayList<TblDefectLogAttachement>();

            for (int i = 0, len = ids.length; i < len; i++) {
                TblDefectAttachement defectAttachement = new TblDefectAttachement();
                defectAttachement.setLastUpdateDate(new Timestamp(new Date().getTime()));
                defectAttachement.setLastUpdateBy(CommonUtil.getCurrentUserId(request));
                defectAttachement.setId(ids[i]);
                attachementMapper.removeAttachement(defectAttachement);
                list.add(defectAttachement);

                // ?????????????????????????????????
                defectAttachement = attachementMapper.getAttById(ids[i]);
                TblDefectLogAttachement logAttachement = new TblDefectLogAttachement();
                logAttachement = (TblDefectLogAttachement) CommonUtil.setBaseValue(logAttachement, request);
                logAttachement.setDefectLogId(logId);
                logAttachement.setFileNameOld(defectAttachement.getFileNameOld());
                logAttachement.setFilePath(defectAttachement.getFilePath());
                logAttachement.setFileS3Bucket(defectAttachement.getFileS3Bucket());
                logAttachement.setFileS3Key(defectAttachement.getFileS3Key());
                logAttachement.setFileType(defectAttachement.getFileType());
                logAttachement.setStatus(2);
                logAttachementMapper.insertSelective(logAttachement);

                logList.add(logAttachement);
            }

            syscMap.put("defectAttachement", list);
            syscMap.put("logAttachement", logList);

            String url = devTestUrl + "devManage/defect/syncDefectAtt";
            this.syncOpt(url, "remove", syscMap);
        }
    }


    /**
     * ???????????????????????????
     */
    @Override
    public Map<String, Object> getAllSystemInfo(TblSystemInfo systemInfo, Integer pageNumber, Integer pageSize) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map = PageWithBootStrap.setPageNumberSize(map, pageNumber, pageSize);
        map.put("systemInfo", systemInfo);
        map.put("uid", null);
        List<Map<String, Object>> list = systemInfoMapper.getAllSystemInfoModal(map);
        int count = systemInfoMapper.countGetAllSystemInfo(map);
        Map<String, Object> result = new HashMap<>();
        result.put("rows", list);
        result.put("total", count);
        return result;
    }

    /**
     * ???????????????????????????
     */
    public Map<String, Object> getAllRequirement(TblRequirementInfo requirementInfo, Integer pageNumber, Integer pageSize) throws Exception {

        Map<String, Object> map = new HashMap<>();
        int start = (pageNumber - 1) * pageSize;
        map.put("start", start);
        map.put("pageSize", pageSize);
        map.put("requirement", requirementInfo);
        List<Map<String, Object>> list = defectInfoMapper.getAllRequirement(map);
        int count = defectInfoMapper.countGetAllRequirement(requirementInfo);

        Map<String, Object> result = new HashMap<>();
        result.put("rows", list);
        result.put("total", count);
        return result;
    }

    /**
     * ?????????????????????????????????
     *
     * @param testTask
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public Map<String, Object> getAllTestTask(TblTestTask testTask, Integer pageNumber, Integer pageSize, HttpServletRequest request) throws Exception {
        Map<String, Object> map = new HashMap<>();
        Long currentUserId = CommonUtil.getCurrentUserId(request);
        map = PageWithBootStrap.setPageNumberSize(map, pageNumber, pageSize);
        testTask.setId(currentUserId);

        /*if(testTask.getWorkTaskStatus() != null && !testTask.getWorkTaskStatus().equals("")){
            String[] devStatus = testTask.getWorkTaskStatus().split(",");
            if(devStatus != null && devStatus.length != 0) {
                List<Integer> array =new ArrayList();
                for(int i=0;i < devStatus.length;i++) {
                    array.add(Integer.parseInt(devStatus[i]));
                }
                testTask.setTestStatusList(array);
            }
        }*/

        map.put("test", testTask);

        List<TblTestTask> list = new ArrayList<>();
        int total = 0;

        Boolean flag = new CommonUtils().currentUserWithAdmin(request);
        if (flag == true) {
            list = tblTestTaskMapper.getTestTaskAll(map);
            total = tblTestTaskMapper.getTestTaskAllTotal(map);
        } else {
            list = tblTestTaskMapper.getTestTask(map);
            total = tblTestTaskMapper.getAllTestTaskTotal(map);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("rows", list);
        result.put("total", total);
        return result;
    }

    /**
     * ?????????????????????????????????
     *
     * @param window
     * @param pageNumber
     * @param pageSize
     * @return
     */
    @Override
    public Map<String, Object> getAllComWindow(TblCommissioningWindow window, Integer pageNumber, Integer pageSize) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map = PageWithBootStrap.setPageNumberSize(map, pageNumber, pageSize);
        map.put("win", window);

        List<TblCommissioningWindow> list = windowMapper.getAllComWindow(map);
        int total = windowMapper.getAllComWindowTotal(map);
        Map<String, Object> result = new HashMap<>();
        result.put("rows", list);
        result.put("total", total);
        return result;
    }


    /**
     * ???????????????????????????????????????
     * defectTable ??????????????????????????????
     *
     * @param defectInfoVo
     * @param response
     * @param request
     * @throws Exception
     */
    @Override
    public void export(JqGridPage<DefectInfoVo> tJqGridPage, DefectInfoVo defectInfoVo, HttpServletResponse response, HttpServletRequest request,TblCustomFieldTemplate tblCustomFieldTemplate,List<AssetSystemTreeDTO> assetSystemTreeDTO,List<AssetSystemTreeDTO> assetSystemTreeDTOList,List<AssetSystemTreeDTO> dataDicAll) throws Exception {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        String sheetName = "????????????";
        String filename = "????????????" + DateUtil.getDateString(new Timestamp(new Date().getTime()), "yyyyMMddHHmmss") + ".xlsx";
        Font font = workbook.createFont();
        font.setFontName("????????????"); //????????????
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD); //??????
        font.setItalic(false); //??????????????????
        font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//????????????

        CellStyle headStyle = workbook.createCellStyle();
        headStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER); // ????????????
        headStyle.setFont(font);
        CellStyle valueStyle = workbook.createCellStyle();

        String[] titleValues = DefectExport.ArrayString(tblCustomFieldTemplate);
        int indexValue = 30;
        if (tblCustomFieldTemplate != null){
            JSONObject jsonObject = JSON.parseObject(tblCustomFieldTemplate.getCustomField());
            Object field = jsonObject.get("field");
            JSONArray objects = JSON.parseArray(field.toString());
            if (objects.size()>0){
                for (int j=0;j<objects.size();j++){
                    if (objects.getJSONObject(j).get("status").equals("1")){
                        titleValues[indexValue] =objects.getJSONObject(j).get("label")+"(??????????????????"+objects.getJSONObject(j).get("fieldName")+")";
                        indexValue = indexValue+1;
                    }
                }
            }
        }
        DefectInfoVo defectInfoForExport = tJqGridPage.filtersToEntityField(defectInfoVo);
        List<TblDefectInfo> list = defectInfoMapper.findDefectList(defectInfoForExport, null, null);

        //???????????????
        for (int i =0 ;i<list.size();i++){
            for (int j =0;j<assetSystemTreeDTO.size();j++){
                //???????????????
                if (list.get(i).getAssetSystemTreeName() != null && list.get(i).getAssetSystemTreeName().equals(assetSystemTreeDTO.get(j).getID().toString())){
                    list.get(i).setAssetSystemTreeName(assetSystemTreeDTO.get(j).getSystemTreeName());
                }
            }
        }


        //???????????????????????????????????????????????????
        for (int i =0 ;i<list.size();i++){
            for (int j =0;j<assetSystemTreeDTOList.size();j++){
                //???????????????????????????
                if ( list.get(i).getDetectedSystemVersionName() != null && list.get(i).getDetectedSystemVersionName().equals(assetSystemTreeDTOList.get(j).getID().toString())){
                    list.get(i).setDetectedSystemVersionName(assetSystemTreeDTOList.get(j).getVersion());
                }
                //???????????????????????????
                if (list.get(i).getRepairSystemVersionName() != null && list.get(i).getRepairSystemVersionName().equals(assetSystemTreeDTOList.get(j).getID().toString())){
                    list.get(i).setRepairSystemVersionName(assetSystemTreeDTOList.get(j).getVersion());
                }
            }
        }
        String[][] value = new String[0][100];
        if (list != null && list.size() > 0) {
            value = new String[list.size()][];
            for (int i = 0, len = list.size(); i < len; i++) {
                value[i] = new String[titleValues.length];
                value[i][0] = list.get(i).getDefectCode();
                value[i][1] = list.get(i).getDefectSummary();
                value[i][2] = list.get(i).getDefectStatus() != null ? getDicValue(DicConstants.TEST_DEFECT_STATUS, list.get(i).getDefectStatus()) : "";
                value[i][3] = list.get(i).getDefectSource() != null ? getDicValue(DicConstants.TEST_DEFECT_SOURCE, list.get(i).getDefectSource()) : "";
                value[i][4] = list.get(i).getSeverityLevel() != null ? getDicValue(DicConstants.TEST_SEVERITY_LEVEL, list.get(i).getSeverityLevel()) : "";
                value[i][5] = list.get(i).getEmergencyLevel() != null ? getDicValue(DicConstants.TEST_EMERGENCY_LEVEL, list.get(i).getEmergencyLevel()) : "";
                value[i][6] = list.get(i).getDefectType() != null ? getDicValue(DicConstants.TEST_DEFECT_TYPE, list.get(i).getDefectType()) : "";
                value[i][7] = list.get(i).getRepairRound() != null ? list.get(i).getRepairRound().toString() : "";
                value[i][8] = list.get(i).getWindowName();
                value[i][9] = list.get(i).getAssignUserName();
                value[i][10] = list.get(i).getTestUserName();
                value[i][11] = list.get(i).getSubmitUserName();
                value[i][12] = list.get(i).getDevelopUserName();
                value[i][13] = DateUtil.getDateString(list.get(i).getSubmitDate(), "yyyy???MM???dd???");
                value[i][14] = list.get(i).getSystemName();
                value[i][15] = list.get(i).getRequirementCode();
                value[i][16] = list.get(i).getTestTaskName();
                String featureName = requirementFeatureMapper.getFeatureName(list.get(i).getTestTaskId());
                value[i][17] = featureName != null ? featureName : "";
                //????????????????????????
                value[i][18] = list.get(i).getTestTaskCode() == null ? "" : list.get(i).getTestTaskCode();
                //????????????????????????
                value[i][19] = list.get(i).getFeatureCode() == null ? "" : list.get(i).getFeatureCode();
                //????????????
                for (int j =0; j<dataDicAll.size();j++){
                    if (list.get(i).getRejectReason() != null && list.get(i).getRejectReason().toString().equals(dataDicAll.get(j).getValueCode())){
                        value[i][20] = dataDicAll.get(j).getValueName() == null ? "" : dataDicAll.get(j).getValueName();
                    }
                }
                //????????????
                value[i][21] = list.get(i).getProjectGroupName() == null ? "" : list.get(i).getProjectGroupName();
                //????????????
                value[i][22] = list.get(i).getCloseTime() == null ? "" : list.get(i).getCloseTime().toString();
                //??????
                value[i][23] = list.get(i).getAssetSystemTreeName() == null ? "" : list.get(i).getAssetSystemTreeName();
                //?????????????????????
                value[i][24] = list.get(i).getDetectedSystemVersionName() == null ? "" : list.get(i).getDetectedSystemVersionName();
                //?????????????????????
                value[i][25] = list.get(i).getRepairSystemVersionName() == null ? "" : list.get(i).getRepairSystemVersionName();
                //??????????????????
                value[i][26] = DateUtil.getDateString(list.get(i).getExpectRepairDate(), "yyyy???MM???dd???");
                //???????????????
                value[i][27] = list.get(i).getEstimateWorkload() == null ? "" : list.get(i).getEstimateWorkload().toString();
                //??????????????????
                value[i][28] = DefectExport.conversion(list.get(i).getRootCauseAnalysis());
                //????????????
                value[i][29] = DefectExport.conversion(list.get(i).getDefectOverview());

                //????????????
                if (list.get(i).getFieldTemplate() != null){
                    JSONObject jsonObject = JSON.parseObject(list.get(i).getFieldTemplate());
                    Object field = jsonObject.get("field");
                    JSONArray objects = JSON.parseArray(field.toString());
                    if (objects.size()>0){
                        for (int j=0;j<objects.size();j++){
                            for (int k =0;k<titleValues.length;k++) {
                                if (titleValues[k] != null && titleValues[k].contains(objects.getJSONObject(j).get("fieldName").toString())) {
                                    value[i][k] = objects.getJSONObject(j).get("valueName").toString();
                                }
                            }
                        }
                    }
                }

            }
        }

        ExcelUtil.export(titleValues, sheetName, filename, value, workbook, 0, headStyle, valueStyle, request, response);
    }


    private String getDicValue(String dicKey, Integer dicTableKey) {
        Map<String, Object> dic = getDicKey(dicKey);
        return (String) dic.get(dicTableKey.toString());
    }

    private String getDefectCode() throws Exception {
        String featureCode = "";
        Integer codeInt = 0;
        Object object = redisUtils.get(Constants.TMP_TEST_DEFECT_CODE);
        if (object != null && !"".equals(object)) {
            // redis????????????redis??????
            String code = object.toString();
            if (!StringUtils.isBlank(code)) {
                codeInt = Integer.parseInt(code) + 1;
            }
        } else {
            // redis????????????????????????????????????????????????
            String maxDefectNo = this.selectMaxDefectCode();
            codeInt = 1;
            if (StringUtils.isNotBlank(maxDefectNo)) {
                codeInt = Integer.valueOf(maxDefectNo.substring(Constants.TMP_TEST_DEFECT_CODE.length())) + 1;
            }
        }
        featureCode = Constants.TMP_TEST_DEFECT_CODE + String.format("%08d", codeInt);
        redisUtils.set(Constants.TMP_TEST_DEFECT_CODE, String.format("%08d", codeInt));
        return featureCode;
    }

    private String selectMaxDefectCode() throws Exception {
        return defectInfoMapper.selectMaxDefectCode();
    }


    public Map<String, Object> handleException(Exception e, String message) {
        logger.error(message + ":" + e.getMessage(), e);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", Constants.ITMP_RETURN_FAILURE);
        map.put("errorMessage", message);
        return map;
    }

    private void syncOpt(String url, String opt, Object object) throws Exception {
        Gson gson = new Gson();
        Map<String, Object> syncMap = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>();
        syncMap.put("opt", opt);
        syncMap.put("value", gson.toJson(object));
        String json = gson.toJson(syncMap);
        logger.info("itmp.dev.url:" + url);
        if (integration == false) {
            String result = HttpUtil.doPost(url, json, 6000, 6000, null);
            resultMap = JSON.parseObject(result);
        } else if (integration == true) {
            String urlOpt = url.substring(url.lastIndexOf("/") + 1);
            if (urlOpt.equals("syncDefect")) {
                resultMap = toDevManageInterface.syncDefect(json);
            } else if (urlOpt.equals("syncDefectAtt")) {
                resultMap = toDevManageInterface.syncDefectAtt(json);
            } else if (urlOpt.equals("syncDefectWithFiles")) {
                resultMap = toDevManageInterface.syncDefectWithFiles(json);
            }
        }
        if (resultMap.get("status") != null && resultMap.get("status").equals("2")) {
            logger.error((String) resultMap.get("errorMessage"));
            throw new Exception((String) resultMap.get("errorMessage"));
        }
    }

    private void syncDefectWithFiles(String url, Object object) throws Exception {
        Gson gson = new Gson();
        Map<String, Object> syncMap = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>();
        syncMap.put("value", gson.toJson(object));
        String json = gson.toJson(syncMap);
        logger.info("itmp.dev.url:" + url);

        if (integration == false) {
            String result = HttpUtil.doPost(url, json, 6000, 6000, null);
            resultMap = JSON.parseObject(result);
        } else if (integration == true) {
            resultMap = toDevManageInterface.syncDefectWithFiles(json);
        }
        if (resultMap.get("status") != null && resultMap.get("status").equals("2")) {
            logger.error((String) resultMap.get("errorMessage"));
            throw new Exception((String) resultMap.get("errorMessage"));
        }
    }

    //????????????????????????
    @Override
    public List<TblProjectInfo> getProject(Long systemId) {
        return defectInfoMapper.getProject(systemId);
    }

    //????????????
    @Override
    @DataSource(name = "tmpDataSource")
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void addDefectRemark(TblDefectRemark tblDefectRemark, List<TblDefectRemarkAttachement> files, HttpServletRequest request) {
        // TODO Auto-generated method stub
        tblDefectRemark.setCreateBy(CommonUtil.getCurrentUserId(request));
        tblDefectRemark.setUserId(CommonUtil.getCurrentUserId(request));
        tblDefectRemark.setUserName(CommonUtil.getCurrentUserName(request));
        tblDefectRemark.setUserAccount(CommonUtil.getCurrentUserAccount(request));
        tblDefectRemark.setCreateDate(DateUtil.getCurrentTimestamp());
        tblDefectRemark.setStatus(1);
        defectRemarkMapper.insertDefectRemark(tblDefectRemark);
        Long id = tblDefectRemark.getId();
        if (null != files && files.size() > 0) {
            for (int i = 0; i < files.size(); i++) {
                files.get(i).setCreateBy(CommonUtil.getCurrentUserId(request));
                files.get(i).setDefectRemarkId(id);
                files.get(i).setCreateDate(DateUtil.getCurrentTimestamp());
            }
            remarkAttachementMapper.addRemarkAttachement(files);
        }
        SpringContextHolder.getBean(DefectService.class).addItmpDefectRemark(tblDefectRemark, files, request);
    }

    //????????????
    @Override
    @DataSource(name = "itmpDataSource")
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void addItmpDefectRemark(TblDefectRemark tblDefectRemark, List<TblDefectRemarkAttachement> files,
                                    HttpServletRequest request) {
        // TODO Auto-generated method stub
        tblDefectRemark.setCreateBy(CommonUtil.getCurrentUserId(request));
        tblDefectRemark.setUserId(CommonUtil.getCurrentUserId(request));
        tblDefectRemark.setUserName(CommonUtil.getCurrentUserName(request));
        tblDefectRemark.setUserAccount(CommonUtil.getCurrentUserAccount(request));
        tblDefectRemark.setCreateDate(DateUtil.getCurrentTimestamp());
        tblDefectRemark.setStatus(1);
        defectRemarkMapper.insertDefectRemark(tblDefectRemark);
        Long id = tblDefectRemark.getId();
        if (null != files && files.size() > 0) {
            for (int i = 0; i < files.size(); i++) {
                files.get(i).setCreateBy(CommonUtil.getCurrentUserId(request));
                files.get(i).setDefectRemarkId(id);
                files.get(i).setCreateDate(DateUtil.getCurrentTimestamp());
            }
            remarkAttachementMapper.addRemarkAttachement(files);
        }
    }

    //????????????
    @Override
    @Transactional(readOnly = true)
    public List<TblDefectRemark> getRemarkByDefectId(Long defectId) {
        // TODO Auto-generated method stub
        List<TblDefectRemark> list = defectRemarkMapper.getRemarkByDefectId(defectId);
        return list;
    }

    //??????????????????
    @Override
    @Transactional(readOnly = true)
    public List<TblDefectRemarkAttachement> getRemarkAttsByDefectId(Long defectId) {
        // TODO Auto-generated method stub
        return remarkAttachementMapper.getRemarkAttsByDefectId(defectId);
    }

    /**
     * @param tblDefectInfo
     * @return void
     * @Description ???????????????????????????????????????
     * @MethodName sendMessageForDefectStatusUpdate
     * @author yaojiaxin [yaojiaxin@pioneerservice.cn]
     * @Date 2019/9/29 14:43
     */
    private void sendMessageForDefectStatusUpdate(TblDefectInfo tblDefectInfo) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("messageTitle", "???????????????????????????");
        map.put("messageContent", tblDefectInfo.getDefectCode() + "|" + tblDefectInfo.getDefectSummary());
        map.put("messageReceiverScope", 2);
        map.put("messageReceiver", tblDefectInfo.getAssignUserId());
        Map<String, Object> result = testManageToSystemInterface.insertMessage(JSON.toJSONString(map));
        if (result != null && !result.isEmpty()) {
            if (StringUtils.equals(result.get("status").toString(), Constants.ITMP_RETURN_FAILURE)) {
                throw new Exception(result.get("errorMessage").toString());
            }
        }
    }

    //??????????????????
    @Override
    @DataSource(name = "tmpDataSource")
    @Transactional(readOnly = false, rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void insertTmpDefect(String defectData, HttpServletRequest request, HttpServletResponse response)  throws Exception {
        Integer type = 0;
        String data = JSON.toJSONString(jsonToMap(defectData).get("requestBody"));
        SynDefectInfo synDefectInfo = JSONObject.parseObject(data,SynDefectInfo.class);
        TblDefectInfo tblDefectInfo = SynDefectInfoToTblDefectInfo(synDefectInfo);
        TblDefectInfo tblDefectInfo1 = defectInfoMapper.getDefectByDefectCode(tblDefectInfo.getDefectCode());
        if(tblDefectInfo1==null){
            tblDefectInfo.setStatus(1);
            tblDefectInfo.setDefectStatus(2);
            tblDefectInfo.setCreateBy(Long.valueOf(1));
            tblDefectInfo.setCreateDate(new Timestamp(new Date().getTime()));
            tblDefectInfo.setLastUpdateBy(Long.valueOf(1));
            tblDefectInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
            defectInfoMapper.insertSynDefect(tblDefectInfo);
            type=1;
        }else{
            tblDefectInfo.setId(tblDefectInfo1.getId());
            tblDefectInfo.setLastUpdateBy(Long.valueOf(1));
            tblDefectInfo.setLastUpdateDate(new Timestamp(new Date().getTime()));
            defectInfoMapper.updateSynDefect(tblDefectInfo);
            type=2;
        }
        SpringContextHolder.getBean(DefectService.class).insertItmpDefect(tblDefectInfo,type);
    }

    //??????????????????
    @Override
    @DataSource(name = "itmpDataSource")
    @Transactional(readOnly=false,rollbackFor=Exception.class,propagation=Propagation.REQUIRES_NEW)
    public void insertItmpDefect(TblDefectInfo tblDefectInfo,Integer type) throws Exception{
        if(type==1){
            defectInfoMapper.insertSynDefect(tblDefectInfo);
        }else{
            defectInfoMapper.updateSynDefect(tblDefectInfo);
        }
    }

    public TblDefectInfo SynDefectInfoToTblDefectInfo(SynDefectInfo synDefectInfo) {
        TblDefectInfo tblDefectInfo = new TblDefectInfo();
        if(synDefectInfo!=null) {
            tblDefectInfo.setDefectCode(synDefectInfo.getProblemNumber());
            tblDefectInfo.setDefectSummary(synDefectInfo.getProblemTitle());
            tblDefectInfo.setDefectOverview(synDefectInfo.getDetailedDesc());

            Map<String, String> itsmSeverityLevelMap = JSONObject.parseObject(itsmSeverityLevel,Map.class);
            String severityLevel = itsmSeverityLevelMap.get(synDefectInfo.getProblemLevel());
            tblDefectInfo.setSeverityLevel(Integer.valueOf(severityLevel));

            Map<String,String> itsmDefectTypeMap = JSONObject.parseObject(itsmDefectType,Map.class);
            String defectType = itsmDefectTypeMap.get(synDefectInfo.getProblemType());
            tblDefectInfo.setDefectType(Integer.valueOf(defectType));

            tblDefectInfo.setSystemId(getSystemInfoId(synDefectInfo.getSystemCode()));
            tblDefectInfo.setSubmitUserId(getUserInfoId(synDefectInfo.getReporterCode()));
            tblDefectInfo.setSubmitDate(synDefectInfo.getReportTime());
            tblDefectInfo.setCheckTime(synDefectInfo.getAcknowledgeTime());
            tblDefectInfo.setAssignUserId(getUserInfoId(synDefectInfo.getLine3Code()));
            tblDefectInfo.setDevelopUserId(getUserInfoId(synDefectInfo.getLine4Code()));
            tblDefectInfo.setProblenUrl(synDefectInfo.getProblemUrl());
            tblDefectInfo.setCreateType(2);
        }
        return tblDefectInfo;
    }
    private Long getSystemInfoId(String systemCode) {
        Long systemId = systemInfoMapper.findSystemIdBySystemCode(systemCode);
        if(systemId!=null&&systemId.longValue()>0) {
            return systemId;
        }else {
            return null;
        }
    }
    private Long getUserInfoId(String userCode) {
        Long userId = tblUserInfoMapper.findIdByUserAccount(userCode);
        if(userId!=null&&userId.longValue()>0) {
            return userId;
        }else {
            return null;
        }
    }

    //??????tbl_custom_field_template????????????custom_form='tbl_defect_info'?????????
    @Override
    @DataSource(name = "itmpDataSource")
    public TblCustomFieldTemplate selectTblCustomFieldTemplateByTblDefectInfo() {
        return defectInfoMapper.selectTblCustomFieldTemplateByTblDefectInfo();
    }

    /**
     * ????????????????????????????????????
     */
    @Override
    @DataSource(name = "itmpDataSource")
    public List<AssetSystemTreeDTO> selectAssetSystemTreeAll() {
        return defectInfoMapper.selectAssetSystemTreeAll();
    }

    /**
     * ?????????????????????????????????
     * @return
     */
    @Override
    @DataSource(name = "itmpDataSource")
    public List<AssetSystemTreeDTO> selectSystemVersionAll() {
        return defectInfoMapper.selectSystemVersionAll();
    }

    public static Map<String, Object> jsonToMap(String str) {
        Map<String, Object> mapTypes = JSON.parseObject(str);
        return mapTypes;
    }

    /**
     *  ?????????????????????????????????
     * @return
     */
    @Override
    @DataSource(name = "itmpDataSource")
    public List<AssetSystemTreeDTO> selectDataDicAll() {
        return defectInfoMapper.selectDataDicAll();
    }
}