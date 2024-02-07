package com.icuxika.admin.service;

import com.icuxika.admin.vo.OSSSignatureVO;
import com.icuxika.framework.object.modules.admin.vo.AdminFileVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    AdminFileVO uploadFile(MultipartFile file);

    void downloadFile(Long fileId, HttpServletResponse response);

    OSSSignatureVO getAliOSSSignature();
}
