package com.icuxika.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {

    /**
     * 业务功能名称
     */
    String name();

    /**
     * key
     */
    String key();

    /**
     * 等待加锁直到，0表示一直等待，单位毫秒
     */
    long lockUntil() default 0;

    /**
     * 超时，单位毫秒
     */
    long expireIn() default 3000;

    String error() default "服务器异常，请稍后再试！";
}
