package com.icuxika.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icuxika.annotation.ApiReturn;
import com.icuxika.common.ApiData;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.io.IOException;
import java.util.List;

public class ApiReturnHandler extends RequestResponseBodyMethodProcessor {

    private final ObjectMapper objectMapper;

    public ApiReturnHandler(List<HttpMessageConverter<?>> converters, ObjectMapper objectMapper) {
        super(converters);
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return super.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        ApiReturn apiReturn = returnType.getMethodAnnotation(ApiReturn.class);
        if (apiReturn != null) {
            if (apiReturn.disable()) {
                super.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
                return;
            }
        }

        if (returnValue instanceof ApiData) {
            super.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else {
            ApiData<Object> result = new ApiData<>();
            result.setCode(200);
            result.setData(returnValue);
            super.handleReturnValue(result, returnType, mavContainer, webRequest);
        }
    }
}
