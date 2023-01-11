package com.icuxika.framework.config.feign;

import com.icuxika.framework.basic.constant.SystemConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    private static final String PROXY_HEADER_HOST = "host";
    private static final String PROXY_HEADER_X_REAL_IP = "x-real-ip";
    private static final String PROXY_HEADER_X_FORWARDED_FOR = "x-forwarded-for";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // @Async 运行的代码无法获取 RequestContextHolder.getRequestAttributes()
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            if (StringUtils.hasText(request.getHeader(PROXY_HEADER_HOST))) {
                requestTemplate.header(PROXY_HEADER_HOST, request.getHeader(PROXY_HEADER_HOST));
            }
            if (StringUtils.hasText(request.getHeader(PROXY_HEADER_X_REAL_IP))) {
                requestTemplate.header(PROXY_HEADER_X_REAL_IP, request.getHeader(PROXY_HEADER_X_REAL_IP));
            }
            if (StringUtils.hasText(request.getHeader(PROXY_HEADER_X_FORWARDED_FOR))) {
                requestTemplate.header(PROXY_HEADER_X_FORWARDED_FOR, request.getHeader(PROXY_HEADER_X_FORWARDED_FOR));
            }
        }
        requestTemplate.header(SystemConstant.FEIGN_REQUEST_HEADER_KEY, SystemConstant.FEIGN_REQUEST_HEADER_VALUE);
    }
}
