package com.icuxika.exception;

public class FeignFallbackException extends RuntimeException {

    public FeignFallbackException(String message) {
        super(message);
    }

    public FeignFallbackException(String message, Throwable cause) {
        super(message, cause);
    }
}
