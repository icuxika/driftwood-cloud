package com.icuxika.config;

import com.icuxika.constant.SystemConstant;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class AuthorizeRequestMatcher implements RequestMatcher {

    @Override
    public boolean matches(HttpServletRequest httpServletRequest) {
        String feignRequestVerification = httpServletRequest.getHeader(SystemConstant.FEIGN_REQUEST_HEADER_KEY);
        return feignRequestVerification != null && feignRequestVerification.equals(SystemConstant.FEIGN_REQUEST_HEADER_VALUE);
    }

}
