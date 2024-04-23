package com.icuxika.framework.object.modules.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileVO {

    private Long id;

    private String filepath;

    private String originalFilename;

}
