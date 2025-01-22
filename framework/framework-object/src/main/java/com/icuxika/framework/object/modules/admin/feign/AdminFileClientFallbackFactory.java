package com.icuxika.framework.object.modules.admin.feign;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.object.modules.admin.vo.AdminFileVO;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class AdminFileClientFallbackFactory implements FallbackFactory<AdminFileClient> {

    @Override
    public AdminFileClient create(Throwable cause) {
        if (log.isErrorEnabled()) {
            log.error(cause.getMessage());
        }
        return new AdminFileClient() {
            @Override
            public ApiData<AdminFileVO> uploadFile(MultipartFile file) {
                return ApiData.errorMsg("上传文件出错");
            }

            @Override
            public void downloadFile(Long fileId, HttpServletResponse response) {
                if (log.isErrorEnabled()) {
                    log.error("下载文件出错");
                }
            }
        };
    }
}
