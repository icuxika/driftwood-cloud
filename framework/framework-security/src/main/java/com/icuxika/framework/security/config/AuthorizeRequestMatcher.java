package com.icuxika.framework.security.config;

import com.icuxika.framework.basic.constant.SystemConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class AuthorizeRequestMatcher implements RequestMatcher {

    @Override
    public boolean matches(HttpServletRequest httpServletRequest) {
        String feignRequestVerification = httpServletRequest.getHeader(SystemConstant.FEIGN_REQUEST_HEADER_KEY);
        return feignRequestVerification != null && feignRequestVerification.equals(SystemConstant.FEIGN_REQUEST_HEADER_VALUE);
    }

}
