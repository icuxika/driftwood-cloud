package com.icuxika.framework.object.modules.file.feign;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.object.modules.file.vo.MinioFileVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class MinioFileClientFallbackFactory implements FallbackFactory<MinioFileClient> {

    private static final Logger L = LoggerFactory.getLogger(MinioFileClientFallbackFactory.class);

    @Override
    public MinioFileClient create(Throwable cause) {

        if (L.isErrorEnabled()) {
            L.error(cause.getMessage());
        }

        return new MinioFileClient() {
            @Override
            public ApiData<MinioFileVO> uploadFile(MultipartFile file, String path) {
                return ApiData.errorMsg("上传文件出错");
            }

            @Override
            public ApiData<List<MinioFileVO>> uploadFileList(List<MultipartFile> fileList, String path) {
                return ApiData.errorMsg("上传文件出错");
            }

            @Override
            public ApiData<String> getPreSignedFileUrlById(Long id) {
                return ApiData.errorMsg("获取文件下载链接出错");
            }
        };
    }
}
