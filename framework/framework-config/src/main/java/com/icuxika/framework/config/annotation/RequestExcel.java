package com.icuxika.framework.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestExcel {

    /**
     * 文件参数标识，相当于 @RequestPart("file") 的 file
     */
    String fileKey() default "file";
}
