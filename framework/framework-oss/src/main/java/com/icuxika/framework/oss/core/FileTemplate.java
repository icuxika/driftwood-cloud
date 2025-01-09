package com.icuxika.framework.oss.core;

import com.amazonaws.services.s3.model.*;

import java.io.InputStream;
import java.nio.file.FileSystems;
import java.util.List;

public interface FileTemplate {

    String FILE_SEPARATOR = FileSystems.getDefault().getSeparator();

    void createBucket(String bucketName);

    List<Bucket> getAllBuckets();

    void removeBucket(String bucketName);

    void putObject(String bucketName, String objectName, InputStream inputStream, String contextType);

    void putObject(String bucketName, String objectName, InputStream inputStream);

    S3Object getObject(String bucketName, String objectName);

    void removeObject(String bucketName, String objectName);

    List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive);

    ObjectMetadata getObjectMetadata(String bucketName, String objectName);

    InitiateMultipartUploadResult initiateMultipartUpload(String bucketName, String objectName);

    UploadPartResult uploadPart(String bucketName, String objectName, InputStream inputStream, FileUploadPart fileUploadPart);

    CompleteMultipartUploadResult completeMultipartUpload(String bucketName, String objectName, String uploadId, List<PartETag> partETags);

    MultipartUploadListing listMultipartUploads(String bucketName);

    void abortMultipartUpload(String bucketName, String objectName, String uploadId);
}
