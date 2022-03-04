package com.icuxika.exception;

import com.icuxika.common.ApiStatusCode;

/**
 * 全局业务异常
 */
public class GlobalServiceException extends RuntimeException {

    private Integer statusCode = ApiStatusCode.FAILURE.getCode();

    public GlobalServiceException(String message) {
        super(message);
    }

    public GlobalServiceException(String message, Integer statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public Integer getStatusCode() {
        return statusCode;
    }
}
