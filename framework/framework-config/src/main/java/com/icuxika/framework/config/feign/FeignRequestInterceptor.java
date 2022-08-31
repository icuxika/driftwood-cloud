package com.icuxika.framework.config.feign;

import com.icuxika.framework.basic.constant.SystemConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        requestTemplate.header(SystemConstant.FEIGN_REQUEST_HEADER_KEY, SystemConstant.FEIGN_REQUEST_HEADER_VALUE);
    }
}
