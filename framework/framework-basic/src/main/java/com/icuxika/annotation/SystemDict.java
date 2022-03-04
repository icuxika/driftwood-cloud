package com.icuxika.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SystemDict {

    String name();

    /**
     * 字典全局级别 系统 > 模块
     */
    long type() default 0;

    /**
     * 字典所属模块名称
     */
    String module() default "系统";
}
