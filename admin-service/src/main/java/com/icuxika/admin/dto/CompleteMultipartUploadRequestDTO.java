package com.icuxika.admin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CompleteMultipartUploadRequestDTO {

    private String originalFilename;
    private String objectName;
    private String uploadId;
    private List<PartETagDTO> partETags;

}
