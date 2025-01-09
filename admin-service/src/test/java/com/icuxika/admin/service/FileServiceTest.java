package com.icuxika.admin.service;

import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icuxika.admin.entity.AdminFile;
import com.icuxika.admin.repository.FileRepository;
import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.basic.util.DateUtil;
import com.icuxika.framework.oss.core.FileTemplate;
import com.icuxika.framework.oss.core.FileUploadPart;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

@SpringBootTest
class FileServiceTest {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileTemplate fileTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getObjectMetadata() {
        AdminFile adminFile = fileRepository.findById(27L).orElseThrow();
        ObjectMetadata objectMetadata = fileTemplate.getObjectMetadata(adminFile.getBucketName(), adminFile.getObjectName());
        System.out.println("objectMetadata.getContentLength(): " + objectMetadata.getContentLength());
    }

    @Test
    void initiateMultipartUpload() {
        String fileName = DateUtil.getLocalDateTimeText() + "_" + ThreadLocalRandom.current().nextInt(0, 10);
        String objectName = DateUtil.getLocalDateText() + "/" + fileName;
        InitiateMultipartUploadResult uploadResult = fileTemplate.initiateMultipartUpload(SystemConstant.MINIO_BUCKET_NAME, objectName);
        try {
            System.out.println(objectMapper.writeValueAsString(uploadResult));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void uploadPart() {
        try {
            File file = new File("C:\\Users\\icuxika\\Downloads\\nsis-3.09.zip");
            InputStream inputStream = new FileInputStream(file);
            FileUploadPart fileUploadPart = new FileUploadPart();
            fileUploadPart.setUploadId("MzM0M2JjMzAtNmIxOC00ZTIyLWExMzktYmFkZDBhMWU0NDdjLmQ5YWU4NjY4LTZmMDAtNGY2OC04OTk4LTA1N2FiY2Y3YzcxYw");
            fileUploadPart.setPartNumber(1);
            fileUploadPart.setPartSize(file.length());
            fileUploadPart.setFileOffset(0L);
            fileUploadPart.setMd5Digest("f267cf6acbd3fffa56370f5c15c5904e");
            UploadPartResult uploadPartResult = fileTemplate.uploadPart(SystemConstant.MINIO_BUCKET_NAME, "20250108/20250108180330_6", inputStream, fileUploadPart);
            System.out.println(objectMapper.writeValueAsString(uploadPartResult));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void listMultipartUploads() {
        try {
            System.out.println(objectMapper.writeValueAsString(fileTemplate.listMultipartUploads(SystemConstant.MINIO_BUCKET_NAME)));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void abortMultipartUpload() {
        fileTemplate.abortMultipartUpload(SystemConstant.MINIO_BUCKET_NAME, "20250108/20250108180330_6", "MzM0M2JjMzAtNmIxOC00ZTIyLWExMzktYmFkZDBhMWU0NDdjLmQ5YWU4NjY4LTZmMDAtNGY2OC04OTk4LTA1N2FiY2Y3YzcxYw");
    }
}