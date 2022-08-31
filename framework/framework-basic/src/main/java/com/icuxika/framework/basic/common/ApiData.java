package com.icuxika.framework.basic.common;

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

    public static <T> ApiData<T> ok(T data) {
        return ok(data, ApiStatusCode.SUCCESS.getMsg());
    }

    public static <T> ApiData<T> ok(T data, String msg) {
        return ok(ApiStatusCode.SUCCESS.getCode(), data, msg);
    }

    public static <T> ApiData<T> okMsg(String msg) {
        return error(ApiStatusCode.SUCCESS.getCode(), null, msg);
    }

    public static <T> ApiData<T> ok(Integer code, T data, String msg) {
        return new ApiData<>(code, data, msg);
    }

    public static <T> ApiData<T> error(Integer code) {
        return error(code, null, ApiStatusCode.FAILURE.getMsg());
    }

    public static <T> ApiData<T> error(ApiStatusCode apiStatusCode) {
        return error(apiStatusCode.getCode(), null, apiStatusCode.getMsg());
    }

    public static <T> ApiData<T> error(Integer code, String msg) {
        return error(code, null, msg);
    }

    public static <T> ApiData<T> error(T data, String msg) {
        return error(ApiStatusCode.FAILURE.getCode(), data, msg);
    }

    public static <T> ApiData<T> errorMsg(String msg) {
        return error(ApiStatusCode.FAILURE.getCode(), null, msg);
    }

    /**
     * error情况的data一般只给全局异常返回使用
     */
    public static <T> ApiData<T> error(Integer code, T data, String msg) {
        return new ApiData<>(code, data, msg);
    }


    public ApiData(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    /**
     * 业务状态码 [10000, 20000) 代表请求成功
     */
    public Boolean isSuccess() {
        return this.getCode().compareTo(ApiStatusCode.SUCCESS.getCode()) >= 0
                && this.getCode().compareTo(ApiStatusCode.FAILURE.getCode()) < 0;
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
