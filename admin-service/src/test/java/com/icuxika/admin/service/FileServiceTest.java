package com.icuxika.admin.service;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.icuxika.admin.entity.AdminFile;
import com.icuxika.admin.repository.FileRepository;
import com.icuxika.framework.oss.core.FileTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FileServiceTest {

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileTemplate fileTemplate;

    @Test
    void getObjectMetadata() {
        AdminFile adminFile = fileRepository.findById(27L).orElseThrow();
        ObjectMetadata objectMetadata = fileTemplate.getObjectMetadata(adminFile.getBucketName(), adminFile.getObjectName());
        System.out.println("objectMetadata.getContentLength(): " + objectMetadata.getContentLength());
    }
}