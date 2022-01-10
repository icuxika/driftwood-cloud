package com.icuxika.common;

import org.springframework.http.HttpStatus;

/**
 * 接口返回数据包装类
 * 正常请求接口一般返回此类型数据，由 code 代表具体返回内容
 * 但是 401、403等请求直接返回正常Http请求返回值，不经由此类包装
 *
 * @param <T> 数据类型
 */
public class ApiData<T> {
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 数据
     */
    private T data;

    /**
     * 消息
     */
    private String msg;

    public ApiData() {
    }

    public static ApiData<Void> ok(String msg) {
        return new ApiData<>(ApiStatusCode.SUCCESS.getCode(), null, msg);
    }

    public static <T> ApiData<T> ok(T data) {
        return new ApiData<>(ApiStatusCode.SUCCESS.getCode(), data, HttpStatus.OK.getReasonPhrase());
    }

    public static <T> ApiData<T> ok(T data, String msg) {
        return new ApiData<>(HttpStatus.OK.value(), data, msg);
    }

    public static ApiData<Void> error(String msg) {
        return new ApiData<>(ApiStatusCode.FAILURE.getCode(), null, msg);
    }

    public static <T> ApiData<T> error(T data, String msg) {
        return new ApiData<>(ApiStatusCode.FAILURE.getCode(), data, msg);
    }

    public ApiData(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public Boolean isSuccess() {
        return this.getCode().equals(ApiStatusCode.SUCCESS.getCode());
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
