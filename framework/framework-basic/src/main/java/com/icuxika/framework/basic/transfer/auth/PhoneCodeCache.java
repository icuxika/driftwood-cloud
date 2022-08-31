package com.icuxika.framework.basic.transfer.auth;

import java.io.Serializable;

public class PhoneCodeCache implements Serializable {

    /**
     * 验证码
     */
    private String code;

    /**
     * 有效时长
     */
    private Long time;

    /**
     * 创建时间
     */
    private final Long createTime = System.currentTimeMillis();

    public PhoneCodeCache() {
    }

    public PhoneCodeCache(String code, Long time) {
        this.code = code;
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getCreateTime() {
        return createTime;
    }
}
