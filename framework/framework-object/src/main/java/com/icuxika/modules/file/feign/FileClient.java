package com.icuxika.modules.file.feign;

import com.icuxika.common.ApiData;
import com.icuxika.modules.file.vo.MinioFileVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FeignClient(value = "framework-service-minio", contextId = "fileClient")
public interface FileClient {

    @PostMapping(value = "/file/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiData<MinioFileVO> uploadFile(@RequestPart("file") MultipartFile file, @RequestParam("path") String path);

    @PostMapping(value = "/file/uploadFileList", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiData<List<MinioFileVO>> uploadFileList(@RequestPart("fileList") List<MultipartFile> fileList, @RequestParam("path") String path);

    @GetMapping("/file/preSignedFileUrl/{id}")
    ApiData<String> getPreSignedFileUrlById(@PathVariable("id") Long id);

}
