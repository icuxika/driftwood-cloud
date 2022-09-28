package com.icuxika.admin.controller;

import com.icuxika.admin.service.FileService;
import com.icuxika.framework.basic.common.ApiData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

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
}
