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
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private FileTemplate fileTemplate;

    @Autowired
    private FileRepository fileRepository;

    @Override
    public Long uploadFile(MultipartFile file) {
        try (
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                InputStream inputStream = file.getInputStream()
        ) {
            final byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            try (
                    InputStream sha256HexStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                    InputStream md5HexStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
                    InputStream fileStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ) {
                // 计算文件的SHA-256
                String fileSha256 = DigestUtils.sha256Hex(sha256HexStream);
                String originalFilename = file.getOriginalFilename();
                // 文件扩展名
                String fileExtension = FilenameUtils.getExtension(originalFilename) == null ? "" : "." + FilenameUtils.getExtension(originalFilename);
                // 文件名
                String fileName = SystemConstant.MINIO_BUCKET_NAME + "_" + DigestUtils.md5Hex(md5HexStream) + "_" + ThreadLocalRandom.current().nextInt(0, 10) + fileExtension;
                String objectName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + File.separator + fileName;
                fileTemplate.putObject(SystemConstant.MINIO_BUCKET_NAME, objectName, fileStream);

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
            }
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
