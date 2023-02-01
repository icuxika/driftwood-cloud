package com.icuxika.framework.config.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.config.annotation.JsonClip;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Constructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class JsonClipResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonClipResolver() {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_DATE_PATTERN)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_TIME_PATTERN)));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_DATE_TIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_DATE_PATTERN)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_TIME_PATTERN)));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(SystemConstant.DEFAULT_DATE_TIME_PATTERN)));
        objectMapper.registerModule(javaTimeModule);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(JsonClip.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String contentType = webRequest.getHeader("content-type");
        if (contentType == null || contentType.isEmpty()) {
            return null;
        }

        Class<?> parameterClass = parameter.getParameterType();

        if (contentType.toLowerCase().startsWith(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            JsonClip jsonClip = parameter.getParameterAnnotation(JsonClip.class);
            String[] parameterValues = webRequest.getParameterValues(jsonClip.name());
            if (parameterValues == null) {
                Constructor<?> constructor = parameterClass.getDeclaredConstructor();
                return constructor.newInstance();
            }

            StringBuilder parameterValueStringValueBuilder = new StringBuilder();
            for (String parameterValue : parameterValues) {
                parameterValueStringValueBuilder.append(parameterValue);
            }

            return objectMapper.readValue(parameterValueStringValueBuilder.toString(), parameterClass);
        } else if (contentType.toLowerCase().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
            if (httpServletRequest == null) {
                return null;
            }
            HttpInputMessage httpInputMessage = new ServletServerHttpRequest(httpServletRequest);
            EmptyBodyCheckingHttpInputMessage emptyBodyCheckingHttpInputMessage = new EmptyBodyCheckingHttpInputMessage(httpInputMessage);
            if (emptyBodyCheckingHttpInputMessage.hasBody()) {
                InputStream inputStream = emptyBodyCheckingHttpInputMessage.getBody();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, len);
                }
                String content = outputStream.toString();
                if (content.isEmpty()) {
                    Constructor<?> constructor = parameterClass.getDeclaredConstructor();
                    return constructor.newInstance();
                } else {
                    return objectMapper.readValue(content, parameterClass);
                }
            } else {
                Constructor<?> constructor = parameterClass.getDeclaredConstructor();
                return constructor.newInstance();
            }
        }
        return null;
    }

    private static class EmptyBodyCheckingHttpInputMessage implements HttpInputMessage {

        private final HttpHeaders headers;

        @Nullable
        private final InputStream body;

        public EmptyBodyCheckingHttpInputMessage(HttpInputMessage inputMessage) throws IOException {
            this.headers = inputMessage.getHeaders();
            InputStream inputStream = inputMessage.getBody();
            if (inputStream.markSupported()) {
                inputStream.mark(1);
                this.body = (inputStream.read() != -1 ? inputStream : null);
                inputStream.reset();
            } else {
                PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
                int b = pushbackInputStream.read();
                if (b == -1) {
                    this.body = null;
                } else {
                    this.body = pushbackInputStream;
                    pushbackInputStream.unread(b);
                }
            }
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.headers;
        }

        @Override
        public InputStream getBody() {
            return (this.body != null ? this.body : StreamUtils.emptyInput());
        }

        public boolean hasBody() {
            return (this.body != null);
        }
    }
}
