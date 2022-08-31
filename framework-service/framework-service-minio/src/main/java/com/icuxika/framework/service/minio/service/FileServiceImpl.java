package com.icuxika.framework.service.minio.service;

import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.basic.exception.GlobalServiceException;
import com.icuxika.framework.object.modules.file.bo.FileUploadBO;
import com.icuxika.framework.object.modules.file.entity.MinioFile;
import com.icuxika.framework.object.modules.file.vo.MinioFileVO;
import com.icuxika.framework.service.minio.config.MinioProperties;
import com.icuxika.framework.service.minio.repository.FileRepository;
import com.icuxika.framework.service.minio.util.MinioUtil;
import io.minio.CreateMultipartUploadResponse;
import io.minio.ListPartsResponse;
import io.minio.ObjectWriteArgs;
import io.minio.messages.Part;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        FileUploadBO fileUploadBO = minioUtil.upload(SystemConstant.MINIO_BUCKET_NAME, path, file);
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

    @Override
    public String getPreSignedFileUrlById(Long id) {
        Optional<MinioFile> minioFileOptional = fileRepository.findById(id);
        if (minioFileOptional.isPresent()) {
            MinioFile minioFile = minioFileOptional.get();
            if (minioFile.getFileStoreName() != null) {
                return minioUtil.getPreSignedObjectUrl(SystemConstant.MINIO_BUCKET_NAME, minioFile.getFileStoreName());
            }
        }
        return null;
    }

    @Override
    public Map<String, String> createMultipartUpload(String fileName, Integer chunkNumber) {
        Map<String, String> result = new HashMap<>();
        try {
            CreateMultipartUploadResponse response = minioUtil.createMultipartUpload(SystemConstant.MINIO_BUCKET_NAME, null, fileName, null, null);
            String uploadId = response.result().uploadId();
            result.put("uploadId", uploadId);

            Map<String, String> reqParams = new HashMap<>();
            reqParams.put("uploadId", uploadId);
            for (int i = 1; i <= chunkNumber; i++) {
                reqParams.put("partNumber", String.valueOf(i));
                String url = minioUtil.getPreSignedObjectUrlForUploadChunk(SystemConstant.MINIO_BUCKET_NAME, fileName, reqParams);
                result.put("chunk_" + (i - 1), url);
            }
        } catch (Exception e) {
            throw new GlobalServiceException("创建文件分片上传请求失败：" + e.getMessage());
        }
        return result;
    }

    @Override
    public void completeMultipartUpload(String fileName, String uploadId) {
        try {
            ListPartsResponse response = minioUtil.listParts(SystemConstant.MINIO_BUCKET_NAME, null, fileName, ObjectWriteArgs.MAX_MULTIPART_COUNT, 0, uploadId, null, null);
            List<Part> partList = response.result().partList();
            Part[] parts = new Part[partList.size()];
            for (int i = 0; i < partList.size(); i++) {
                parts[i] = new Part(i + 1, partList.get(i).etag());
            }
            minioUtil.completeMultipartUpload(SystemConstant.MINIO_BUCKET_NAME, null, fileName, uploadId, parts, null, null);
        } catch (Exception e) {
            throw new GlobalServiceException("合并分片文件失败：" + e.getMessage());
        }
    }
}
