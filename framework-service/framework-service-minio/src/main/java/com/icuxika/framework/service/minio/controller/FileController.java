package com.icuxika.framework.service.minio.controller;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.object.modules.file.vo.MinioFileVO;
import com.icuxika.framework.service.minio.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 小文件-上传单个文件
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
     * 小文件-上传多个文件
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

    /**
     * 获取文件下载链接
     *
     * @param id 文件id
     * @return ApiData
     */
    @GetMapping("/preSignedFileUrl/{id}")
    public ApiData<String> getPreSignedFileUrlById(@PathVariable("id") Long id) {
        String url = fileService.getPreSignedFileUrlById(id);
        return ApiData.ok(url);
    }

    /**
     * 大文件分片上传-创建分片上传请求
     *
     * @param fileName    文件名称
     * @param chunkNumber 分片数量
     * @return 分片上传地址、uploadId
     */
    @GetMapping("/createMultipartUpload")
    public ApiData<Map<String, String>> createMultipartUpload(String fileName, Integer chunkNumber) {
        Map<String, String> upload = fileService.createMultipartUpload(fileName, chunkNumber);
        return ApiData.ok(upload);
    }

    /**
     * 大文件分片上传-合并上传完成的分片文件
     *
     * @param fileName 文件名称
     * @param uploadId 分片数量
     * @return ApiData
     */
    @GetMapping("/completeMultipartUpload")
    public ApiData<Void> completeMultipartUpload(String fileName, String uploadId) {
        fileService.completeMultipartUpload(fileName, uploadId);
        return ApiData.okMsg("文件合并成功");
    }
}
