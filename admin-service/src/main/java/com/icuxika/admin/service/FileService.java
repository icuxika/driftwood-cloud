package com.icuxika.admin.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    Long uploadFile(MultipartFile file);

    void downloadFile(Long fileId, HttpServletResponse response);
}
