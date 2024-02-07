package com.icuxika.framework.object.modules.admin.feign;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.object.modules.admin.vo.AdminFileVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "admin-service", contextId = "adminFileClient", fallbackFactory = AdminFileClientFallbackFactory.class)
public interface AdminFileClient {
    @PostMapping(value = "/file/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ApiData<AdminFileVO> uploadFile(@RequestPart("file") MultipartFile file);

    @GetMapping("/file/{fileId}")
    void downloadFile(@PathVariable("fileId") Long fileId, HttpServletResponse response);
}
