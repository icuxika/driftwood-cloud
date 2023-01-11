package com.icuxika.framework.object.modules.admin.feign;

import com.icuxika.framework.basic.common.ApiData;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "admin-service", contextId = "adminFileClient")
public interface AdminFileClient {
    @PostMapping("/file/uploadFile")
    ApiData<Long> uploadFile(@RequestPart("file") MultipartFile file);

    @GetMapping("/file/{fileId}")
    void downloadFile(@PathVariable("fileId") Long fileId, HttpServletResponse response);
}
