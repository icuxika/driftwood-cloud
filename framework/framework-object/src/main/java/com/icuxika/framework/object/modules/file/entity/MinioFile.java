package com.icuxika.framework.object.modules.file.entity;

import com.icuxika.framework.object.base.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name = "minio_file")
@Entity
public class MinioFile extends BaseEntity {

    /**
     * 文件名
     */
    @Column(nullable = false)
    private String fileName;

    /**
     * 文件路径
     */
    @Column(nullable = false)
    private String filePath;

    /**
     * 文件实际存储名称
     */
    @Column()
    private String fileStoreName;

    /**
     * 文件类型
     */
    @Column(nullable = false)
    private Integer fileType;

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileStoreName() {
        return fileStoreName;
    }

    public void setFileStoreName(String fileStoreName) {
        this.fileStoreName = fileStoreName;
    }

    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
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
