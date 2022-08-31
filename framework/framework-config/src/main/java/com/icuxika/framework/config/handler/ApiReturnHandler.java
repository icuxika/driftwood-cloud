package com.icuxika.framework.config.handler;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.basic.common.ApiStatusCode;
import com.icuxika.framework.config.annotation.ApiReturn;
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

    public ApiReturnHandler(List<HttpMessageConverter<?>> converters) {
        super(converters);
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
            result.setCode(ApiStatusCode.SUCCESS.getCode());
            result.setData(returnValue);
            result.setMsg(ApiStatusCode.SUCCESS.getMsg());
            super.handleReturnValue(result, returnType, mavContainer, webRequest);
        }
    }
}
