package com.icuxika.common;

/**
 * 系统业务状态码定义
 */
public enum ApiStatusCode {

    /**
     * 代表一个通用操作成功
     */
    SUCCESS(10000, "操作成功"),

    /**
     * 代表一个通用的操作失败，但无详细表示
     */
    FAILURE(20000, "操作失败");

    private final Integer code;

    private final String msg;

    ApiStatusCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
