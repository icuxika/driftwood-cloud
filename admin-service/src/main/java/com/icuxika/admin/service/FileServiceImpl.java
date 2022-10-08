package com.icuxika.admin.service;

import com.amazonaws.services.s3.model.S3Object;
import com.icuxika.admin.entity.AdminFile;
import com.icuxika.admin.repository.FileRepository;
import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.basic.exception.GlobalServiceException;
import com.icuxika.framework.config.util.FileUtil;
import com.icuxika.framework.oss.core.FileTemplate;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FileServiceImpl implements FileService {

    private static final String ADMIN_FILE_PATH_PREFIX = "admin" + File.separator;

    @Autowired
    private FileTemplate fileTemplate;

    @Autowired
    private FileRepository fileRepository;

    @Override
    public Long uploadFile(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            // 计算文件的SHA-256
            String fileSha256 = DigestUtils.sha256Hex(file.getInputStream());
            String originalFilename = file.getOriginalFilename();
            // 文件扩展名
            String fileExtension = FilenameUtils.getExtension(originalFilename) == null ? "" : "." + FilenameUtils.getExtension(originalFilename);
            // 文件名
            String fileName = SystemConstant.MINIO_BUCKET_NAME + "_" + DigestUtils.md5Hex(file.getInputStream()) + "_" + ThreadLocalRandom.current().nextInt(0, 10) + fileExtension;
            String objectName = ADMIN_FILE_PATH_PREFIX + fileName;
            fileTemplate.putObject(SystemConstant.MINIO_BUCKET_NAME, objectName, inputStream);

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
}
