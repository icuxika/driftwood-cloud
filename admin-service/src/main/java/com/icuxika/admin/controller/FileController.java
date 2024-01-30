package com.icuxika.admin.controller;

import com.icuxika.admin.service.FileService;
import com.icuxika.admin.vo.OSSSignatureVO;
import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.config.annotation.JsonClip;
import com.icuxika.framework.object.modules.user.dto.UserExcelDTO;
import com.icuxika.framework.security.annotation.Anonymous;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("uploadFile")
    public ApiData<Long> uploadFile(@RequestPart("file") MultipartFile file) {
        Long fileId = fileService.uploadFile(file);
        return ApiData.ok(fileId);
    }

    @GetMapping("/{fileId}")
    public void downloadFile(@PathVariable("fileId") Long fileId, HttpServletResponse response) {
        fileService.downloadFile(fileId, response);
    }

    @PostMapping("uploadFileWithJSON")
    public ApiData<Void> uploadFileWithJSON(@RequestPart("file") MultipartFile file, @JsonClip UserExcelDTO userExcelDTO) {
        if (file != null) {
            System.out.println("文件名称：" + file.getName());
            System.out.println("文件大小：" + file.getSize());
        }
        if (userExcelDTO != null) {
            System.out.println(userExcelDTO);
        }
        return ApiData.okMsg("上传成功");
    }

    // ------------------------------ 阿里云

    /**
     * 获取阿里云对象存储服务端签名
     */
    @Anonymous
    @GetMapping("/getAliOSSSignature")
    public ApiData<OSSSignatureVO> getAliOSSSignature() {
        return ApiData.ok(fileService.getAliOSSSignature());
    }
}
