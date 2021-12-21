package cn.pioneeruniverse.common.utils;

import cn.pioneeruniverse.common.dto.TblAttachementInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Description: 上传文件
 * Author:liushan
 * Date: 2019/2/25 下午 2:39
 */
public class UploadFileUtil {

    public final static Logger logger = LoggerFactory.getLogger(UploadFileUtil.class);

    /**
     * 上传单个文件
     *
     * @param file 本地文件
     * @param bucketName 上传文件的桶名
     * @return TblAttachementInfoDTO 上传文件存储数据
     * @throws Exception
     */
    public static TblAttachementInfoDTO updateFile(File file,S3Util s3Util,String bucketName) throws Exception {
        TblAttachementInfoDTO attachementInfoDTO = new TblAttachementInfoDTO();
        try {
            InputStream inputStream = new FileInputStream(file);
            String fileName = file.getName();
            String oldFileName = fileName.substring(0, fileName.lastIndexOf("."));
            fileName = fileName.substring(file.getName().lastIndexOf(File.separator) + 1);
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);//后缀名
            String keyname = s3Util.putObject(bucketName, oldFileName, inputStream);
            attachementInfoDTO.setFileS3Key(keyname);
            attachementInfoDTO.setFileS3Bucket(bucketName);
            attachementInfoDTO.setFilePath(attachementInfoDTO.getFilePath() + bucketName +File.separator+fileName);
            attachementInfoDTO.setFileNameOld(fileName);
            attachementInfoDTO.setFileType(fileType);
            attachementInfoDTO.setCreateBy(1L);
            attachementInfoDTO.setCreateDate(new Timestamp(new Date().getTime()));
            attachementInfoDTO.setLastUpdateBy(1L);
            attachementInfoDTO.setLastUpdateDate(new Timestamp(new Date().getTime()));
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
        return attachementInfoDTO;
    }
}
