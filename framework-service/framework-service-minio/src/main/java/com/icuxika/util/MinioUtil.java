package com.icuxika.util;

import com.icuxika.modules.file.bo.FileUploadBO;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
public class MinioUtil {

    private static final Logger L = LoggerFactory.getLogger(MinioUtil.class);

    @Autowired
    private MinioClient minioClient;

    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     */
    private void createBucket(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            L.info("创建新的bucket：" + bucketName);
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } else {
            L.info("bucket已存在：" + bucketName);
        }
    }

    /**
     * 上传文件到minio
     *
     * @param bucketName bucket名称
     * @param path       文件路径
     * @param file       文件
     * @return 文件存储路径
     */
    public FileUploadBO upload(String bucketName, String path, MultipartFile file) {
        FileUploadBO fileUploadBO = new FileUploadBO();
        if (file == null || file.isEmpty()) {
            return fileUploadBO;
        }
        String fileName;
        try {
            createBucket(bucketName);
            String originalFilename = file.getOriginalFilename();
            // 文件扩展名
            String fileExtension = FilenameUtils.getExtension(originalFilename) == null ? "" : "." + FilenameUtils.getExtension(originalFilename);
            // 文件名
            fileName = bucketName + "_" + DigestUtils.md5Hex(file.getInputStream()) + "_" + ThreadLocalRandom.current().nextInt(0, 10) + fileExtension;
            // 格式化文件路径
            path = formatPath(path);
            String objectName = path + fileName;
            minioClient.putObject(
                    PutObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            fileUploadBO.setFilePath("/" + bucketName + "/" + objectName);
            fileUploadBO.setFileStoreName(objectName);
            return fileUploadBO;
        } catch (Exception e) {
            L.error("文件上传失败：" + e.getMessage());
            return fileUploadBO;
        }
    }

    /**
     * 格式化文件路径，使之满足 ABC/
     *
     * @param path 文件路径
     * @return 文件路径
     */
    private String formatPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path;
    }

    public String getPreSignedObjectUrl(String bucketName, String objectName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(objectName)
                            // 30 分钟有消息
                            .expiry(30, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            L.error("获取文件下载链接失败：" + e.getMessage());
            return null;
        }
    }
}
