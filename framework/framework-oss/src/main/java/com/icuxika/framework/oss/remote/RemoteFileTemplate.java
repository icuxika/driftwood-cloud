package com.icuxika.framework.oss.remote;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.icuxika.framework.oss.core.FileProperties;
import com.icuxika.framework.oss.core.FileTemplate;
import com.icuxika.framework.oss.core.FileUploadPart;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.MediaType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;

public class RemoteFileTemplate implements FileTemplate, InitializingBean {

    private FileProperties fileProperties;

    private AmazonS3 amazonS3;

    public RemoteFileTemplate(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    @Override
    public void createBucket(String bucketName) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            amazonS3.createBucket(bucketName);
        }
    }

    @Override
    public List<Bucket> getAllBuckets() {
        return amazonS3.listBuckets();
    }

    @Override
    public void removeBucket(String bucketName) {
        amazonS3.deleteBucket(bucketName);
    }

    @Override
    public void putObject(String bucketName, String objectName, InputStream inputStream, String contextType) {
        try {
            byte[] bytes = inputStream.readAllBytes();
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(bytes.length);
            objectMetadata.setContentType(contextType);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            amazonS3.putObject(bucketName, objectName, byteArrayInputStream, objectMetadata);
        } catch (IOException e) {
            throw new RuntimeException("保存文件失败");
        }
    }

    @Override
    public void putObject(String bucketName, String objectName, InputStream inputStream) {
        putObject(bucketName, objectName, inputStream, MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }

    @Override
    public S3Object getObject(String bucketName, String objectName) {
        return amazonS3.getObject(bucketName, objectName);
    }

    @Override
    public void removeObject(String bucketName, String objectName) {
        amazonS3.deleteObject(bucketName, objectName);
    }

    @Override
    public List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) {
        return new ArrayList<>(amazonS3.listObjects(bucketName, prefix).getObjectSummaries());
    }

    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String objectName) {
        return amazonS3.getObjectMetadata(bucketName, objectName);
    }

    @Override
    public InitiateMultipartUploadResult initiateMultipartUpload(String bucketName, String objectName) {
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(bucketName, objectName);
        return amazonS3.initiateMultipartUpload(initRequest);
    }

    @Override
    public UploadPartResult uploadPart(String bucketName, String objectName, InputStream inputStream, FileUploadPart fileUploadPart) {
        UploadPartRequest uploadPartRequest = new UploadPartRequest()
                .withBucketName(bucketName)
                .withKey(objectName)
                .withUploadId(fileUploadPart.getUploadId())
                .withPartNumber(fileUploadPart.getPartNumber())
                .withPartSize(fileUploadPart.getPartSize())
                .withMD5Digest(Base64.getEncoder().encodeToString(HexFormat.of().parseHex(fileUploadPart.getMd5Digest())))
                .withInputStream(inputStream);
        return amazonS3.uploadPart(uploadPartRequest);
    }

    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(String bucketName, String objectName, String uploadId, List<PartETag> partETags) {
        CompleteMultipartUploadRequest uploadRequest = new CompleteMultipartUploadRequest(bucketName, objectName, uploadId, partETags);
        return amazonS3.completeMultipartUpload(uploadRequest);
    }

    @Override
    public MultipartUploadListing listMultipartUploads(String bucketName) {
        ListMultipartUploadsRequest request = new ListMultipartUploadsRequest(bucketName);
        return amazonS3.listMultipartUploads(request);
    }

    @Override
    public void abortMultipartUpload(String bucketName, String objectName, String uploadId) {
        amazonS3.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName, objectName, uploadId));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setMaxConnections(fileProperties.getRemote().getMaxConnections());
        clientConfiguration.setProtocol(Protocol.HTTP);
        clientConfiguration.setConnectionTimeout(50000);
        clientConfiguration.setSocketTimeout(50000);

        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(fileProperties.getRemote().getEndpoint(), fileProperties.getRemote().getRegion());
        AWSCredentials awsCredentials = new BasicAWSCredentials(fileProperties.getRemote().getAccessKey(), fileProperties.getRemote().getSecretKey());
        AWSCredentialsProvider awsCredentialsProvider = new AWSStaticCredentialsProvider(awsCredentials);
        this.amazonS3 = AmazonS3Client.builder()
                .withEndpointConfiguration(endpointConfiguration)
                .withClientConfiguration(clientConfiguration)
                .withCredentials(awsCredentialsProvider)
                .disableChunkedEncoding()
                .withPathStyleAccessEnabled(fileProperties.getRemote().getPathStyleAccessEnabled())
                .build();
    }
}
