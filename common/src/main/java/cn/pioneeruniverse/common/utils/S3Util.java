package cn.pioneeruniverse.common.utils;

import java.io.*;
import java.net.URLEncoder;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

@Component("S3Util")
@Lazy
public class S3Util {

    private static Logger log = LoggerFactory.getLogger(S3Util.class);
    /**
     * @param args
     */

    @Value("${s3.accessKeyID}")
    private String accessKey;
    @Value("${s3.secretAccessKey}")
    private String secretKey;
    @Value("${s3.endpoint}")
    private String endpoint;

    @Value("${s3.projectBucket}")
    private String projectBucket;
    @Value("${s3.requirementBucket}")
    private String requirementBucket;
    @Value("${s3.devTaskBucket}")
    private String devTaskBucket;
    @Value("${s3.testTaskBucket}")
    private String testTaskBucket;
    @Value("${s3.defectBucket}")
    private String defectBucket;

    public String getProjectBucket() {
        return projectBucket;
    }

    public String getRequirementBucket() {
        return requirementBucket;
    }

    public String getDevTaskBucket() {
        return devTaskBucket;
    }

    public String getTestTaskBucket() {
        return testTaskBucket;
    }

    public String getDefectBucket() {
        return defectBucket;
    }

    /**
     * ???????????????bucket
     *
     * @param bucketName ?????????
     * @param fileName   ?????????
     * @return keyName ??????????????????key???
     */
    public String putObject(String bucketName, String fileName, InputStream inputstream) {
        System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");
        String keyName = "";
        AWSCredentials credentials = null;
        try {
            keyName = fileName + "_" + new Date().getTime();
            credentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, "")).build();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(((FileInputStream) inputstream).getChannel().size());
            s3.putObject(bucketName, keyName, inputstream, metadata);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return keyName;
    }

    public String putObject1(String bucketName, String fileName, InputStream inputstream,Long contentLength) {
        System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");
        String keyName = "";
        AWSCredentials credentials = null;
        try {
            keyName = fileName + "_" + new Date().getTime();
            credentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, "")).build();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(contentLength);
            s3.putObject(bucketName, keyName, inputstream, metadata);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return keyName;
    }
    /**
     * ???bucket????????????
     *
     * @param bucketName ?????????
     * @param keyName    ????????????key
     * @param fileName   ?????????
     */
    public void downObject(String bucketName, String keyName, String fileName, HttpServletResponse response) {
        System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");
        AWSCredentials credentials = null;
        try {
            credentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, "")).build();

            S3Object object = s3.getObject(new GetObjectRequest(bucketName, keyName));
            S3ObjectInputStream s3is = object.getObjectContent();
            String fileName1 = URLEncoder.encode(fileName, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName1);
            //1.????????????ContentType?????????????????????????????????????????????????????????    
            response.setContentType("multipart/form-data");
            OutputStream os = response.getOutputStream();
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = s3is.read(read_buf)) != -1) {
                os.write(read_buf, 0, read_len);
                os.flush();
            }
            s3is.close();
            os.close();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * ???bucket????????????
     *
     * @param bucketName ?????????
     * @param keyName    ????????????key
     */
    public String getStringByS3(String bucketName, String keyName) {
        String markdown="";
        System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");
        AWSCredentials credentials = null;
        try {
            credentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, "")).build();
            S3Object object = s3.getObject(new GetObjectRequest(bucketName, keyName));
            S3ObjectInputStream s3is = object.getObjectContent();
            InputStreamReader reader = new InputStreamReader(s3is);
            StringWriter writer = new StringWriter();
            try {
                //???????????????????????????
                char[] buffer = new char[1024];
                int n = 0;
                while (-1 != (n = reader.read(buffer))) {
                    writer.write(buffer, 0, n);
                }
            } catch (Exception e) {
                e.printStackTrace();

            } finally {
                if (reader != null)
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
             markdown=writer.toString();
             s3is.close();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return markdown;
    }

    /**



    /**
     * ????????????
     *
     * @param bucketName
     * @param keyName  ???????????????
     */
    public void deleteObject(String bucketName, String keyName) {
        System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");
        AWSCredentials credentials = null;
        try {
            credentials = new BasicAWSCredentials(accessKey, secretKey);
            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, "")).build();

            s3.deleteObject(bucketName, keyName);
        } catch (AmazonServiceException e) {
            log.error(e.getMessage(), e);
            System.exit(1);
        }
    }
}