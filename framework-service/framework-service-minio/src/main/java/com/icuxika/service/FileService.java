package com.icuxika.service;

import com.icuxika.modules.file.vo.MinioFileVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    MinioFileVO uploadFile(MultipartFile file, String path);

    List<MinioFileVO> uploadFileList(List<MultipartFile> fileList, String path);
}
