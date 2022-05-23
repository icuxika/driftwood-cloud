package com.icuxika.modules.file.feign;

import com.icuxika.common.ApiData;
import com.icuxika.modules.file.vo.MinioFileVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(value = "framework-service-minio", contextId = "fileClient")
public interface FileClient {

    @PostMapping("/file/uploadFile")
    ApiData<MinioFileVO> uploadFile(@RequestPart("file") MultipartFile file, @RequestParam("path") String path);

    @PostMapping("/file/uploadFileList")
    ApiData<List<MinioFileVO>> uploadFileList(@RequestPart("fileList") List<MultipartFile> fileList, @RequestParam("path") String path);

    @GetMapping("/file/preSignedFileUrl/{id}")
    ApiData<String> getPreSignedFileUrlById(@PathVariable("id") Long id);

}
