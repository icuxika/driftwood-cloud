package com.icuxika.framework.service.minio.service;

import com.icuxika.framework.object.modules.file.vo.MinioFileVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface FileService {

    MinioFileVO uploadFile(MultipartFile file, String path);

    List<MinioFileVO> uploadFileList(List<MultipartFile> fileList, String path);

    String getPreSignedFileUrlById(Long id);

    Map<String, String> createMultipartUpload(String fileName, Integer chunkNumber);

    void completeMultipartUpload(String fileName, String uploadId);
}
