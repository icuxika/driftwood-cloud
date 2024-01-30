package com.icuxika.admin.service;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.amazonaws.services.s3.model.S3Object;
import com.icuxika.admin.entity.AdminFile;
import com.icuxika.admin.repository.FileRepository;
import com.icuxika.admin.vo.OSSSignatureVO;
import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.basic.exception.GlobalServiceException;
import com.icuxika.framework.basic.util.DateUtil;
import com.icuxika.framework.config.util.FileUtil;
import com.icuxika.framework.oss.core.FileTemplate;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileTemplate fileTemplate;

    @Autowired
    private FileRepository fileRepository;

    @Override
    public Long uploadFile(MultipartFile file) {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                InputStream inputStream = file.getInputStream()
        ) {
            final byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            try (
                    InputStream sha256HexStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                    InputStream md5HexStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                    InputStream fileStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ) {
                // 计算文件的SHA-256
                String fileSha256 = DigestUtils.sha256Hex(sha256HexStream);
                String originalFilename = file.getOriginalFilename();
                // 文件扩展名
                String fileExtension = FilenameUtils.getExtension(originalFilename) == null ? "" : "." + FilenameUtils.getExtension(originalFilename);
                // 文件名
                String fileName = DateUtil.getLocalDateTimeText() + "_" + DigestUtils.md5Hex(md5HexStream) + "_" + ThreadLocalRandom.current().nextInt(0, 10) + fileExtension;
                String objectName = DateUtil.getLocalDateText() + File.separator + fileName;
                fileTemplate.createBucket(SystemConstant.MINIO_BUCKET_NAME);
                fileTemplate.putObject(SystemConstant.MINIO_BUCKET_NAME, objectName, fileStream);

                AdminFile adminFile = new AdminFile();
                adminFile.setOriginalFilename(originalFilename);
                adminFile.setBucketName(SystemConstant.MINIO_BUCKET_NAME);
                adminFile.setFileName(fileName);
                adminFile.setObjectName(objectName);
                adminFile.setFileSize(file.getSize());
                adminFile.setFileExtension(fileExtension);
                adminFile.setFileSha256(fileSha256);
                fileRepository.save(adminFile);
                return adminFile.getId();
            }
        } catch (IOException e) {
            throw new GlobalServiceException("文件上传失败：" + e.getMessage());
        }
    }

    @Override
    public void downloadFile(Long fileId, HttpServletResponse response) {
        AdminFile adminFile = fileRepository.findById(fileId).orElseThrow(() -> new GlobalServiceException("文件信息不存在"));
        try (S3Object s3Object = fileTemplate.getObject(SystemConstant.MINIO_BUCKET_NAME, adminFile.getObjectName())) {
            FileUtil.responseFile(response, adminFile.getOriginalFilename(), outputStream -> {
                try {
                    StreamUtils.copy(s3Object.getObjectContent(), outputStream);
                } catch (IOException e) {
                    throw new GlobalServiceException("文件写出失败：" + e.getMessage());
                }
            });
        } catch (IOException e) {
            throw new GlobalServiceException("文件下载失败：" + e.getMessage());
        }
    }

    @Override
    public OSSSignatureVO getAliOSSSignature() {
        OSSSignatureVO ossSignatureVO = new OSSSignatureVO();
        try {
            EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();
            String endpoint = "oss-cn-zhangjiakou.aliyuncs.com";
            // 填写Host名称，格式为https://bucketname.endpoint。
            String host = "https://driftwood-cloud.oss-cn-zhangjiakou.aliyuncs.com";
            // 设置上传回调URL，即回调服务器地址，用于处理应用服务器与OSS之间的通信。OSS会在文件上传完成后，把文件上传信息通过此回调URL发送给应用服务器。
            String callbackUrl = "https://192.168.0.0:8888";
            // 设置上传到OSS文件的前缀，可置空此项。置空后，文件将上传至Bucket的根目录下。
            String dir = "test/";

            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);

            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConditions = new PolicyConditions();
            policyConditions.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConditions.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConditions);
            byte[] binaryData = postPolicy.getBytes(StandardCharsets.UTF_8);
            String accessId = credentialsProvider.getCredentials().getAccessKeyId();
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);
            ossSignatureVO.setAccessId(accessId);
            ossSignatureVO.setPolicy(encodedPolicy);
            ossSignatureVO.setSignature(postSignature);
            ossSignatureVO.setDir(dir);
            ossSignatureVO.setHost(host);
            ossSignatureVO.setExpire(LocalDateTime.ofInstant(Instant.ofEpochMilli(expireEndTime), ZoneId.systemDefault()));

            JSONObject jasonCallback = new JSONObject();
            jasonCallback.put("callbackUrl", callbackUrl);
            jasonCallback.put("callbackBody",
                    "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
            jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
            String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());
            ossSignatureVO.setCallback(base64CallbackBody);
        } catch (Exception e) {
            throw new GlobalServiceException("获取对象存储服务端签名失败" + e.getMessage());
        }
        return ossSignatureVO;
    }
}
