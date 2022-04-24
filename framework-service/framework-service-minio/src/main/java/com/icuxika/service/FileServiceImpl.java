package com.icuxika.service;

import com.icuxika.config.MinioProperties;
import com.icuxika.constant.SystemConstant;
import com.icuxika.exception.GlobalServiceException;
import com.icuxika.modules.file.bo.FileUploadBO;
import com.icuxika.modules.file.entity.MinioFile;
import com.icuxika.modules.file.vo.MinioFileVO;
import com.icuxika.repository.FileRepository;
import com.icuxika.util.MinioUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    private MinioProperties minioProperties;

    @Autowired
    private MinioUtil minioUtil;

    @Autowired
    private FileRepository fileRepository;

    @Override
    public MinioFileVO uploadFile(MultipartFile file, String path) {
        String fileSha256;
        try {
            fileSha256 = DigestUtils.sha256Hex(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            throw new GlobalServiceException("文件上传失败：" + e.getMessage());
        }
        String endPoint = minioProperties.getEndpoint();
        MinioFileVO minioFileVO = new MinioFileVO();

        // 根据文件的SHA-256查询是否已存在
        Optional<MinioFile> minioFileOptional = fileRepository.findByFileSha256(fileSha256);
        if (minioFileOptional.isPresent()) {
            MinioFile existFile = minioFileOptional.get();
            minioFileVO.setId(existFile.getId());
            minioFileVO.setFullPath(endPoint + existFile.getFilePath());
            return minioFileVO;
        }

        FileUploadBO fileUploadBO = minioUtil.upload(SystemConstant.MINIO_BUCKET_NAME, formatPath(path), file);
        if (fileUploadBO.getFilePath() == null) {
            throw new GlobalServiceException("上传失败");
        }
        MinioFile minioFile = new MinioFile();
        minioFile.setFileName(file.getOriginalFilename());
        minioFile.setFilePath(fileUploadBO.getFilePath());
        minioFile.setFileStoreName(fileUploadBO.getFileStoreName());
        minioFile.setFileType(0);
        minioFile.setFileSize(file.getSize());
        minioFile.setFileExtension(FilenameUtils.getExtension(file.getOriginalFilename()));
        minioFile.setFileSha256(fileSha256);
        minioFile.setCreateUserId(0L);
        minioFile.setCreateTime(LocalDateTime.now());
        minioFile.setUpdateUserId(0L);
        minioFile.setUpdateTime(LocalDateTime.now());
        fileRepository.save(minioFile);
        minioFileVO.setId(minioFile.getId());
        minioFileVO.setFullPath(endPoint + minioFile.getFilePath());
        return minioFileVO;
    }

    @Override
    public List<MinioFileVO> uploadFileList(List<MultipartFile> fileList, String path) {
        return fileList.stream().map(file -> uploadFile(file, path)).collect(Collectors.toList());
    }

    /**
     * 格式化文件路径，使之满足 ABC/
     *
     * @param path 文件路径
     * @return 文件路径
     */
    private String formatPath(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path;
    }
}
