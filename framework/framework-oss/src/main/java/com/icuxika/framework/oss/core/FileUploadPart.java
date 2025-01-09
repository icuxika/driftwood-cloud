package com.icuxika.framework.oss.core;

import lombok.Data;

@Data
public class FileUploadPart {
    private String uploadId;
    private Integer partNumber;
    private Long partSize;
    private Long fileOffset;
    private String md5Digest;
}
