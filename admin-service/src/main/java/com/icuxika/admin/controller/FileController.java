package com.icuxika.admin.controller;

import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.UploadPartResult;
import com.icuxika.admin.dto.CompleteMultipartUploadRequestDTO;
import com.icuxika.admin.service.FileService;
import com.icuxika.admin.vo.OSSSignatureVO;
import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.basic.util.DateUtil;
import com.icuxika.framework.config.annotation.JsonClip;
import com.icuxika.framework.object.modules.admin.vo.AdminFileVO;
import com.icuxika.framework.object.modules.admin.vo.FileVO;
import com.icuxika.framework.object.modules.user.dto.UserExcelDTO;
import com.icuxika.framework.oss.core.FileTemplate;
import com.icuxika.framework.oss.core.FileUploadPart;
import com.icuxika.framework.security.annotation.Anonymous;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final FileTemplate fileTemplate;

    @PostMapping("uploadFile")
    public ApiData<AdminFileVO> uploadFile(@RequestPart("file") MultipartFile file) {
        AdminFileVO adminFileVO = fileService.uploadFile(file);
        return ApiData.ok(adminFileVO);
    }

    @GetMapping("/{fileId}")
    public void downloadFile(@PathVariable("fileId") Long fileId, HttpServletResponse response) {
        fileService.downloadFile(fileId, response);
    }

    @GetMapping("getFilePath/{fileId}")
    public ApiData<FileVO> getFilePath(@PathVariable("fileId") Long fileId) {
        return ApiData.ok(fileService.getFilePath(fileId));
    }

    @PostMapping("uploadFileWithJSON")
    public ApiData<Void> uploadFileWithJSON(@RequestPart("file") MultipartFile file, @JsonClip UserExcelDTO userExcelDTO) {
        if (file != null) {
            System.out.println("文件名称：" + file.getName());
            System.out.println("文件大小：" + file.getSize());
        }
        if (userExcelDTO != null) {
            System.out.println(userExcelDTO);
        }
        return ApiData.okMsg("上传成功");
    }

    /**
     * <a href="https://docs.aws.amazon.com/zh_cn/AmazonS3/latest/userguide/mpu-upload-object.html">使用分段上传操作上传对象</a>
     */
    @PostMapping("initiateMultipartUpload")
    public ApiData<InitiateMultipartUploadResult> initiateMultipartUpload(@RequestPart("fileName") String fileName) {
        String fileExtension = FilenameUtils.getExtension(fileName) == null ? "" : "." + FilenameUtils.getExtension(fileName);
        String objectName = DateUtil.getLocalDateText() + "/" + DateUtil.getLocalDateTimeText() + "_" + UUID.randomUUID() + "_" + ThreadLocalRandom.current().nextInt(0, 10) + fileExtension;
        InitiateMultipartUploadResult uploadResult = fileTemplate.initiateMultipartUpload(SystemConstant.MINIO_BUCKET_NAME, objectName);
        return ApiData.ok(uploadResult);
    }

    @PostMapping("uploadPart")
    public ApiData<UploadPartResult> uploadPart(@RequestPart("file") MultipartFile file, @RequestParam("objectName") String objectName, @RequestParam("uploadId") String uploadId, @RequestParam("partNumber") Integer partNumber, @RequestParam("partSize") Long partSize, @RequestParam("fileOffset") Long fileOffset, @RequestParam("md5Digest") String md5Digest) {
        FileUploadPart fileUploadPart = new FileUploadPart();
        fileUploadPart.setUploadId(uploadId);
        fileUploadPart.setPartNumber(partNumber);
        fileUploadPart.setPartSize(partSize);
        fileUploadPart.setFileOffset(fileOffset);
        fileUploadPart.setMd5Digest(md5Digest);

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             InputStream inputStream = file.getInputStream()) {
            final byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > -1) {
                byteArrayOutputStream.write(buffer, 0, len);
            }
            return ApiData.ok(fileTemplate.uploadPart(SystemConstant.MINIO_BUCKET_NAME, objectName, new ByteArrayInputStream(byteArrayOutputStream.toByteArray()), fileUploadPart));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("completeMultipartUpload")
    public ApiData<AdminFileVO> completeMultipartUpload(@RequestBody CompleteMultipartUploadRequestDTO completeMultipartUploadRequestDTO) {
        AdminFileVO adminFileVO = fileService.completeMultipartUpload(completeMultipartUploadRequestDTO);
        return ApiData.ok(adminFileVO);
    }

    /**
     * 获取阿里云对象存储服务端签名
     */
    @Anonymous
    @GetMapping("/getAliOSSSignature")
    public ApiData<OSSSignatureVO> getAliOSSSignature() {
        return ApiData.ok(fileService.getAliOSSSignature());
    }
}
