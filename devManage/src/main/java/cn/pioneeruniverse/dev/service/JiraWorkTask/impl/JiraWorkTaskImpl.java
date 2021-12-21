package cn.pioneeruniverse.dev.service.JiraWorkTask.impl;

import cn.pioneeruniverse.common.utils.CommonUtil;
import cn.pioneeruniverse.common.utils.S3Util;
import cn.pioneeruniverse.dev.dao.mybatis.*;
import cn.pioneeruniverse.dev.entity.TblRequirementFeature;
import cn.pioneeruniverse.dev.entity.TblRequirementFeatureAttachement;
import cn.pioneeruniverse.dev.entity.TblSystemInfo;
import cn.pioneeruniverse.dev.jiraUtil.JiraWorkTaskUtil;
import cn.pioneeruniverse.dev.service.JiraWorkTask.IJiraWorkTaskService;
import cn.pioneeruniverse.dev.vo.JiraDevelopmentVO;
import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

@Service
public class JiraWorkTaskImpl implements IJiraWorkTaskService{

    @Autowired
    private S3Util s3Util;
    @Autowired
    private JiraWorkTaskMapper jiraWorkTaskMapper;
    @Autowired
    private TblUserInfoMapper userMapper;
    @Autowired
    private TblSystemInfoMapper systemInfoMapper;
    @Autowired
    private TblRequirementFeatureMapper featureMapper;
    @Autowired
    private TblRequirementFeatureAttachementMapper attachementMapper;



    public final static Logger logger = LoggerFactory.getLogger(JiraWorkTaskImpl.class);
    /**
     *  jira数据增加
     * @param jiraDevelopmentVO
     * @return
     */
    @Override
    public void synJiraWorkTask(JiraDevelopmentVO jiraDevelopmentVO,HttpServletRequest request) {
        getJiraDevelopmentVO(jiraDevelopmentVO);
        CommonUtil.setBaseValue(jiraDevelopmentVO,request);
        TblRequirementFeature feature = featureMapper.getFeatureByCode(jiraDevelopmentVO.getFeatureCode());
        if(feature!=null){
            jiraDevelopmentVO.setId(feature.getId());
            jiraWorkTaskMapper.updateJiraWorkTask(jiraDevelopmentVO);
        }else{
            jiraWorkTaskMapper.insertJiraWorkTask(jiraDevelopmentVO);
        }
        insertAttr(jiraDevelopmentVO);
    }

    private JiraDevelopmentVO getJiraDevelopmentVO(JiraDevelopmentVO jiraDevelopmentVO){
        Map<String,Object>  fieldTemplate = new HashMap<>();
        Map [] extendedFields = new Map[1];
        Map<String,String> extendedField = new HashMap<>();
        extendedField.put("required","true");
        extendedField.put("fieldName","systemReqID");
        extendedField.put("labelName","systemReqID");
        extendedField.put("valueName",jiraDevelopmentVO.getSystemReqID());
        extendedFields[0] = extendedField;
        fieldTemplate.put("field",extendedFields);
        jiraDevelopmentVO.setFieldTemplate(JSON.toJSONString(fieldTemplate));
        jiraDevelopmentVO.setManageUserId(getUserId(jiraDevelopmentVO.getUserCode()));
        jiraDevelopmentVO.setExecuteUserId(getUserId(jiraDevelopmentVO.getUserCode()));
        jiraDevelopmentVO.setSystemId(getSystemId(jiraDevelopmentVO.getSystemCode()));
        jiraDevelopmentVO.setAssetSystemTreeId(getAssetSystemTreeId(jiraDevelopmentVO.getSystemCode(),
                jiraDevelopmentVO.getModuleName(),jiraDevelopmentVO.getModuleName1()));
        jiraDevelopmentVO.setExecuteProjectGroupId(getExecuteProjectGroupId(jiraDevelopmentVO.getProjectGroupName()));
        jiraDevelopmentVO.setRepairSystemVersionId(getSystemVersion(jiraDevelopmentVO.getSystemId(),
                jiraDevelopmentVO.getRepairSystemVersioName()));
        jiraDevelopmentVO.setCreateType(1);
        jiraDevelopmentVO.setRequirementFeatureStatus("01");
        return jiraDevelopmentVO;
    }

    private Long getUserId(String userCode) {
        Long userId = userMapper.findIdByUserAccount(userCode);
        if(userId!=null&&userId.longValue()>0) {
            return userId;
        }else {
            return null;
        }
    }

    private Long getSystemId(String systemCode) {
        TblSystemInfo systeInfo = systemInfoMapper.getSystemByCode(systemCode);
        if(systeInfo!=null) {
            return systeInfo.getId();
        }else {
            return null;
        }
    }

    private Long getAssetSystemTreeId(String systemCode,String module,String module1){
        Long id = jiraWorkTaskMapper.findAssetSystemTreeID(systemCode,module);
        if(module1!=null&&!"".equals(module1)){
            id = jiraWorkTaskMapper.findAssetSystemTreeID1(id.toString(),module1);
        }
        return  id;
    }

    private Long getExecuteProjectGroupId(String projectGroupName){
        Long id = jiraWorkTaskMapper.findProjectGroupID(projectGroupName);
        if(id!=null&&id.longValue()>0){
            return id;
        }else {
            return null;
        }
    }
    private Long getSystemVersion(Long systemId, String versionAndFlag) {
        Long userId = null;
        String [] version = versionAndFlag.split("/");
        if(version.length==2){
            userId = jiraWorkTaskMapper.getSystemVersionID(systemId,version[0],version[1]);
        }
        return userId;
    }

    public void insertAttr(JiraDevelopmentVO jiraDevelopmentVO) {
        try {
            for(Map<String,Object> map:jiraDevelopmentVO.getMaps()){
                String fileName = map.get("fileName").toString();
                TblRequirementFeatureAttachement atta = new TblRequirementFeatureAttachement();
                String extension = fileName
                        .substring(fileName.lastIndexOf(".") + 1);// 后缀名

                HttpURLConnection httpURLConnection = downloadFile(map.get("accessory").toString());
                // 文件大小
                Long fileLengthLong = httpURLConnection.getContentLengthLong();
                // 控制台打印文件大小
                logger.info("您要下载的文件大小为:" + fileLengthLong / (1024) + "KB");
                // 建立链接从请求中获取数据
                InputStream is = httpURLConnection.getInputStream();
                String keyname = s3Util.putObject1(s3Util.getDevTaskBucket(), fileName, is,fileLengthLong);

                atta.setRequirementFeatureId(jiraDevelopmentVO.getId());
                atta.setFileS3Bucket(s3Util.getDevTaskBucket());
                atta.setFileS3Key(keyname);
                atta.setFileNameOld(fileName);
                atta.setFileType(extension);
                TblRequirementFeatureAttachement fAtta = attachementMapper.
                        getReqFeatureAttByfileNameOld(atta.getRequirementFeatureId(),fileName);
                if (fAtta == null) {
                    attachementMapper.insertAtt(atta);
                }else{
                    attachementMapper.updateByPrimaryKey(atta);
                }

            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("文件下载失败！");
        }
    }

    /**
     * 说明：根据指定URL将文件下载到指定目标位置
     * @param urlPath 下载路径
     * @return 返回下载文件
     */
    public HttpURLConnection downloadFile(String urlPath) {
        HttpURLConnection httpURLConnection = null;
        try {
            //统一资源
            URL url = new URL(urlPath);
            // 连接类的父类，抽象类
            URLConnection urlConnection = url.openConnection();
            // http的连接类
            httpURLConnection = (HttpURLConnection) urlConnection;
            //设置超时
            httpURLConnection.setConnectTimeout(1000*5);
            httpURLConnection.setDoInput(true);

            //设置请求方式，默认是GET
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
            httpURLConnection.setRequestProperty("Upgrade-Insecure-Requests", "1");
            httpURLConnection.setRequestProperty("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/77.0.3865.120 Safari/537.36");

            String user = JiraWorkTaskUtil.JIRA_USERNAME;
            String pwd = JiraWorkTaskUtil.JIRA_PASSWORD;
            String auth = user+":"+pwd;
            //对其进行加密
            byte[] rel = Base64.encodeBase64(auth.getBytes());
            String res = new String(rel);

            //设置认证属性
            httpURLConnection.setRequestProperty("Authorization","Basic " + res);
            httpURLConnection.connect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("文件下载失败！");
        }
        return httpURLConnection;
    }
}
