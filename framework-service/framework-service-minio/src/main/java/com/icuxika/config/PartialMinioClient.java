package com.icuxika.config;

import com.google.common.collect.Multimap;
import io.minio.CreateMultipartUploadResponse;
import io.minio.ListPartsResponse;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.errors.*;
import io.minio.messages.Part;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 大文件分片上传：
 * 1、前端访问文件服务，请求上传文件，后端返回签名数据及uploadId
 * 2、前端分片文件，携带签名数据及uploadId并发上传分片数据
 * 3、分片上传完成后，访问合并文件接口，后台负责合并文件
 */
public class PartialMinioClient extends MinioClient {

    protected PartialMinioClient(MinioClient client) {
        super(client);
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
    @Override
    public CreateMultipartUploadResponse createMultipartUpload(String bucketName, String region, String objectName, Multimap<String, String> headers, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        return super.createMultipartUpload(bucketName, region, objectName, headers, extraQueryParams);
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
    @Override
    public ObjectWriteResponse completeMultipartUpload(String bucketName, String region, String objectName, String uploadId, Part[] parts, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        return super.completeMultipartUpload(bucketName, region, objectName, uploadId, parts, extraHeaders, extraQueryParams);
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
    @Override
    public ListPartsResponse listParts(String bucketName, String region, String objectName, Integer maxParts, Integer partNumberMarker, String uploadId, Multimap<String, String> extraHeaders, Multimap<String, String> extraQueryParams) throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        return super.listParts(bucketName, region, objectName, maxParts, partNumberMarker, uploadId, extraHeaders, extraQueryParams);
    }
}
