package com.icuxika.admin.entity;

import com.icuxika.framework.object.base.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name = "admin_file")
@Entity
public class AdminFile extends BaseEntity {

    /**
     * 文件原始名称
     */
    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false)
    private String bucketName;

    /**
     * 文件实际存储名称
     */
    @Column(nullable = false)
    private String fileName;

    /**
     * 文件实际存储名称（包含所在目录）
     */
    @Column(nullable = false)
    private String objectName;

    /**
     * 文件大小
     */
    @Column(nullable = false)
    private Long fileSize;

    /**
     * 文件扩展名
     */
    @Column()
    private String fileExtension;

    /**
     * 文件SHA-256
     */
    @Column(nullable = false)
    private String fileSha256;

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileSha256() {
        return fileSha256;
    }

    public void setFileSha256(String fileSha256) {
        this.fileSha256 = fileSha256;
    }
}
