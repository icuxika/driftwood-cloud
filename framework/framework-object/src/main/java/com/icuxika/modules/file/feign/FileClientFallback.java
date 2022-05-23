package com.icuxika.modules.file.feign;

import com.icuxika.common.ApiData;
import com.icuxika.modules.file.vo.MinioFileVO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
public class FileClientFallback implements FileClient {
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
}
