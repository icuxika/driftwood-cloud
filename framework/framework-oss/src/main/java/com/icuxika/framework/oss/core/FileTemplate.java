package com.icuxika.framework.oss.core;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.InputStream;
import java.util.List;

public interface FileTemplate {

    String FILE_SEPARATOR = System.getProperty("file.separator");

    void createBucket(String bucketName);

    List<Bucket> getAllBuckets();

    void removeBucket(String bucketName);

    void putObject(String bucketName, String objectName, InputStream inputStream, String contextType);

    void putObject(String bucketName, String objectName, InputStream inputStream);

    S3Object getObject(String bucketName, String objectName);

    void removeObject(String bucketName, String objectName);

    List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive);
}
