package com.godx.cloud.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.*;
import lombok.extern.slf4j.Slf4j;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.io.*;

@Slf4j
@Component
@RefreshScope
public class OSSUtil {


    private static final String CodeMybatisHostPath="generate/code/mybatis";

    @Value("${bucket.endpoint}")
    private static String endpoint;

    @Value("${bucket.accessKeyId}")
    private static String accessKeyId;

    @Value("${bucket.accessKeySecret}")
    private static String accessKeySecret;

    @Value("${bucket.bucketName}")
    private static String bucketName;

    @Value("${bucket.fileHost}")
    private static String fileHost;

    @Value("${bucket.endpoint}")
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Value("${bucket.accessKeyId}")
    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    @Value("${bucket.accessKeySecret}")
    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    @Value("${bucket.bucketName}")
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    @Value("${bucket.fileHost}")
    public void setFileHost(String fileHost) {
        this.fileHost = fileHost;
    }

    /**
     * 根据本地文件路径，上传该文件至OSS
     *
     * @param localFilePath 本地文件路径 + 文件名
     * @return
     */
    public static String uploadWithLocalFile(String localFilePath,String ossFilePath){
        File file=new File(localFilePath);
        if (!file.getParentFile().exists()) {
            log.warn("不存在本地文件");
            return null;
        }
        log.info("=========>OSS文件上传开始："+localFilePath);

        if(null == file){
            return null;
        }

        OSSClient ossClient = new OSSClient(endpoint,accessKeyId,accessKeySecret);
        try {
            //容器不存在，就创建
            if(! ossClient.doesBucketExist(bucketName)){
                ossClient.createBucket(bucketName);
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.Private);
                ossClient.createBucket(createBucketRequest);
            }
            //上传文件
            PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucketName, ossFilePath, file));
            //设置权限 这里是private 不公开
            ossClient.setBucketAcl(bucketName,CannedAccessControlList.Private);
            if(null != result){
                log.info("==========>OSS文件上传成功,OSS地址："+ossFilePath);
                return ossFilePath;
            }
        }catch (OSSException oe){
            log.error(oe.getMessage());
        }catch (ClientException ce){
            log.error(ce.getMessage());
        }finally {
            //关闭
            ossClient.shutdown();
        }
        return ossFilePath;
    }

    /** todo
     * 根据文件流，上传文件至OSS
     *
     * @param localFilePath 本地文件路径 + 文件名
     * @return
     */
//    public static String uploadWithStream(OutputStream os){
//
//        // todo 定义文件上传路径
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        String dateStr = format.format(new Date());
//
//        if(null == os){
//            return null;
//        }
//
//        OSSClient ossClient = new OSSClient(endpoint,accessKeyId,accessKeySecret);
//        String fileUrl=null;
//        try {
//            //容器不存在，就创建
//            if(! ossClient.doesBucketExist(bucketName)){
//                ossClient.createBucket(bucketName);
//                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
//                createBucketRequest.setCannedACL(CannedAccessControlList.Private);
//                ossClient.createBucket(createBucketRequest);
//            }
//            //创建文件路径
//            fileUrl = fileHost+"/"+(dateStr + "/" + UUID.randomUUID().toString().replace("-","")+"-"+file.getName());
//            //上传文件
////            PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucketName, fileUrl, file));
//            //设置权限 这里是公开读
//            ossClient.setBucketAcl(bucketName,CannedAccessControlList.Private);
//            if(null != result){
//                log.info("==========>OSS文件上传成功,OSS地址："+fileUrl);
//                return fileUrl;
//            }
//        }catch (OSSException oe){
//            log.error(oe.getMessage());
//        }catch (ClientException ce){
//            log.error(ce.getMessage());
//        }finally {
//            //关闭
//            ossClient.shutdown();
//        }
//        return fileUrl;
//    }

    /**
     * 获取OSS文件InputStream，用于读取oss文件内容
     *
     * @param ossFilePath OSS文件路径 + 文件名
     * @return
     */
    public static InputStream getOSS2InputStream(String ossFilePath) {
        OSS ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        OSSObject ossObj = ossClient.getObject(bucketName, ossFilePath);
        return ossObj.getObjectContent();
    }


    /**
     * 从OSS服务器下载到本地
     * @param ossFilePath  OSS文件路径 + 文件名
     */
    public static BufferedInputStream downloadFromOss(String ossFilePath) throws IOException {
        OSS ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        OSSObject ossObject = ossClient.getObject(bucketName, ossFilePath);
        BufferedInputStream reader = new BufferedInputStream(ossObject.getObjectContent());

        return reader;
    }


}
