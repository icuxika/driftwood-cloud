package com.icuxika.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icuxika.annotation.JsonClip;
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

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.lang.reflect.Constructor;

public class JsonClipResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    public JsonClipResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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
            String parameterName = parameter.getParameterName();
            if (parameterName == null) {
                return null;
            }
            String[] parameterValues = webRequest.getParameterValues(parameterName);
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
