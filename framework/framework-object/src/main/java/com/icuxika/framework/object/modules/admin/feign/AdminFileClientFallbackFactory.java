package com.icuxika.framework.object.modules.admin.feign;

import com.icuxika.framework.basic.common.ApiData;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class AdminFileClientFallbackFactory implements FallbackFactory<AdminFileClient> {

    private static final Logger L = LoggerFactory.getLogger(AdminFileClientFallbackFactory.class);

    @Override
    public AdminFileClient create(Throwable cause) {
        if (L.isErrorEnabled()) {
            L.error(cause.getMessage());
        }
        return new AdminFileClient() {
            @Override
            public ApiData<Long> uploadFile(MultipartFile file) {
                return ApiData.errorMsg("上传文件出错");
            }

            @Override
            public void downloadFile(Long fileId, HttpServletResponse response) {
                if (L.isErrorEnabled()) {
                    L.error("下载文件出错");
                }
            }
        };
    }
}
