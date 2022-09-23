package com.icuxika.framework.oss.remote;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.icuxika.framework.oss.core.FileProperties;
import com.icuxika.framework.oss.core.FileTemplate;

import java.io.InputStream;
import java.util.List;

public class RemoteFileTemplate implements FileTemplate {

    private FileProperties fileProperties;

    public RemoteFileTemplate(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    @Override
    public void createBucket(String bucketName) {

    }

    @Override
    public List<Bucket> getAllBuckets() {
        return null;
    }

    @Override
    public void removeBucket(String bucketName) {

    }

    @Override
    public void putObject(String bucketName, String objectName, InputStream inputStream, String contextType) {

    }

    @Override
    public void putObject(String bucketName, String objectName, InputStream inputStream) {

    }

    @Override
    public S3Object getObject(String bucketName, String objectName) {
        return null;
    }

    @Override
    public void removeObject(String bucketName, String objectName) {

    }

    @Override
    public List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) {
        return null;
    }
}
