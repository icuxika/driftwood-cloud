package com.icuxika.config;

import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

public class AuthorizeRequestMatcher implements RequestMatcher {

    @Override
    public boolean matches(HttpServletRequest httpServletRequest) {
        String feignRequestVerification = httpServletRequest.getHeader("FEIGN-REQUEST");
        return feignRequestVerification != null && feignRequestVerification.equals("VERIFICATION");
    }

}
