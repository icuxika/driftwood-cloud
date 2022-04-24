package com.icuxika.controller;

import com.icuxika.common.ApiData;
import com.icuxika.modules.file.vo.MinioFileVO;
import com.icuxika.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传单个文件
     *
     * @param file 文件
     * @param path 文件路径
     * @return ApiData
     */
    @PostMapping("/uploadFile")
    public ApiData<MinioFileVO> uploadFile(@RequestPart("file") MultipartFile file, @RequestParam("path") String path) {
        MinioFileVO minioFileVO = fileService.uploadFile(file, path);
        return ApiData.ok(minioFileVO);
    }

    /**
     * 上传多个文件
     *
     * @param fileList 文件列表
     * @param path     文件路径
     * @return ApiData
     */
    @PostMapping("/uploadFileList")
    public ApiData<List<MinioFileVO>> uploadFileList(@RequestPart("fileList") List<MultipartFile> fileList, @RequestParam("path") String path) {
        List<MinioFileVO> minioFileVOList = fileService.uploadFileList(fileList, path);
        return ApiData.ok(minioFileVOList);
    }
}
