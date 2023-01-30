package com.icuxika.framework.service.minio.util;

import com.google.common.collect.Multimap;
import com.icuxika.framework.object.modules.file.bo.FileUploadBO;
import com.icuxika.framework.service.minio.config.PartialMinioClient;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Part;
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
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
public class MinioUtil {

    private static final Logger L = LoggerFactory.getLogger(MinioUtil.class);

    @Autowired
    private PartialMinioClient minioClient;

    /**
     * 创建bucket
     *
     * @param bucketName bucket名称
     */
    private void createBucket(String bucketName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!found) {
            if (L.isInfoEnabled()) {
                L.info("创建新的bucket：" + bucketName);
            }
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        } else {
            if (L.isInfoEnabled()) {
                L.info("bucket已存在：" + bucketName);
            }
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
            if (L.isErrorEnabled()) {
                L.error("文件上传失败：" + e.getMessage());
            }
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
            if (L.isErrorEnabled()) {
                L.error("获取文件下载链接失败：" + e.getMessage());
            }
            return null;
        }
    }

    public String getPreSignedObjectUrlForUploadChunk(String bucketName, String objectName, Map<String, String> reqParams) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(30, TimeUnit.MINUTES)
                            .extraQueryParams(reqParams)
                            .build()
            );
        } catch (Exception e) {
            if (L.isErrorEnabled()) {
                L.error("获取文件下载链接失败：" + e.getMessage());
            }
            return null;
        }
    }

    /**
     * 创建分片上传请求
     *
     * @param bucketName       Name of the bucket.
     * @param region           Region name of buckets in S3 service.
     * @param objectName       Object name in the bucket.
     * @param headers          Request headers.
     * @param extraQueryParams Extra query parameters for request (Optional).
     */
    public CreateMultipartUploadResponse createMultipartUpload(String bucketName, String region, String objectName, Multimap<String, String> headers, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        return minioClient.createMultipartUpload(bucketName, region, objectName, headers, extraQueryParams);
    }

    /**
     * 完成分片上传，执行合并文件
     *
     * @param bucketName       Name of the bucket.
     * @param region           Region of the bucket.
     * @param objectName       Object name in the bucket.
     * @param uploadId         Upload ID.
     * @param parts            List of parts.
     * @param extraHeaders     Extra headers (Optional).
     * @param extraQueryParams Extra query parameters (Optional).
     */
    public ObjectWriteResponse completeMultipartUpload(String bucketName, String region, String objectName, String uploadId, Part[] parts, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        return minioClient.completeMultipartUpload(bucketName, region, objectName, uploadId, parts, extraHeaders, extraQueryParams);
    }

    /**
     * 查询分片数据
     *
     * @param bucketName       Name of the bucket.
     * @param region           Name of the bucket (Optional).
     * @param objectName       Object name in the bucket.
     * @param maxParts         Maximum parts information to fetch (Optional).
     * @param partNumberMarker Part number marker (Optional).
     * @param uploadId         Upload ID.
     * @param extraHeaders     Extra headers for request (Optional).
     * @param extraQueryParams Extra query parameters for request (Optional).
     */
    public ListPartsResponse listParts(String bucketName, String region, String objectName, Integer maxParts, Integer partNumberMarker, String uploadId, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        return minioClient.listParts(bucketName, region, objectName, maxParts, partNumberMarker, uploadId, extraHeaders, extraQueryParams);
    }
}
