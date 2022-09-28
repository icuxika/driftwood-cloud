package com.icuxika.admin.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface FileService {
    Long uploadFile(MultipartFile file);

    void downloadFile(Long fileId, HttpServletResponse response);
}
