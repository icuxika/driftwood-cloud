package com.icuxika.framework.config.handler;

import com.alibaba.excel.EasyExcel;
import com.icuxika.framework.basic.util.DateUtil;
import com.icuxika.framework.config.annotation.ResponseExcel;
import com.icuxika.framework.config.converter.LocalDateConverter;
import com.icuxika.framework.config.converter.LocalDateTimeConverter;
import com.icuxika.framework.config.converter.LocalTimeConverter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

public class ResponseExcelHandler extends RequestResponseBodyMethodProcessor {
    public ResponseExcelHandler(List<HttpMessageConverter<?>> converters) {
        super(converters);
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return returnType.hasMethodAnnotation(ResponseExcel.class);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException, HttpMessageNotWritableException {
        ResponseExcel responseExcel = returnType.getMethodAnnotation(ResponseExcel.class);
        if (responseExcel == null) {
            super.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
            return;
        }

        String filename = responseExcel.filename();
        if (!StringUtils.hasText(filename)) {
            filename = DateUtil.getLocalDateTimeText();
        }

        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
//        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename + ".xlsx", StandardCharsets.UTF_8).build());
        headers.toSingleValueMap().forEach(response::addHeader);
        EasyExcel.write(response.getOutputStream())
                .registerConverter(new LocalDateConverter())
                .registerConverter(new LocalTimeConverter())
                .registerConverter(new LocalDateTimeConverter())
                .sheet("Sheet1").doWrite((Collection<?>) returnValue);
//        EasyExcel.write(response.getOutputStream()).sheet("Sheet1").doFill(returnValue);
    }
}
