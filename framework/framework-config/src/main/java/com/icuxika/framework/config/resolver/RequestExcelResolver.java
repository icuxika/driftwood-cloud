package com.icuxika.framework.config.resolver;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.icuxika.framework.config.annotation.RequestExcel;
import com.icuxika.framework.config.converter.LocalDateConverter;
import com.icuxika.framework.config.converter.LocalDateTimeConverter;
import com.icuxika.framework.config.converter.LocalTimeConverter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RequestExcelResolver implements HandlerMethodArgumentResolver {

    private static final Logger L = LoggerFactory.getLogger(RequestExcelResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(RequestExcel.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Class<?> parameterClass = parameter.getParameterType();
        if (!parameterClass.isAssignableFrom(List.class)) {
            throw new IllegalArgumentException("@RequestExcel仅支持集合类型的参数");
        }
        RequestExcel requestExcel = parameter.getParameterAnnotation(RequestExcel.class);
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        InputStream inputStream;
        if (request instanceof MultipartRequest) {
            MultipartFile file = ((MultipartRequest) request).getFile(requestExcel.fileKey());
            inputStream = file.getInputStream();
        } else {
            inputStream = request.getInputStream();
        }
        Class<?> excelModelClass = ResolvableType.forMethodParameter(parameter).getGeneric(0).resolve();

        List<Object> list = new ArrayList<>();
        ReadListener<Object> readListener = new ReadListener<>() {
            @Override
            public void invoke(Object o, AnalysisContext analysisContext) {
                list.add(o);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                L.info("Excel 读取完成");
            }
        };
        EasyExcel.read(inputStream, excelModelClass, readListener)
                .registerConverter(new LocalDateConverter())
                .registerConverter(new LocalTimeConverter())
                .registerConverter(new LocalDateTimeConverter())
                .sheet().doRead();
        return list;
    }
}
