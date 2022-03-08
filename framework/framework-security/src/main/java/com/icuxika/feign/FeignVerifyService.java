package com.icuxika.feign;

import com.icuxika.constant.SystemConstant;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component(value = "fvs")
public class FeignVerifyService {

    /**
     * 验证当前请求是否是feign请求
     *
     * @param request HttpServletRequest
     * @return 是否
     */
    public final boolean isFeign(HttpServletRequest request) {
        return SystemConstant.FEIGN_REQUEST_HEADER_VALUE.equals(request.getHeader(SystemConstant.FEIGN_REQUEST_HEADER_KEY));
    }

    /**
     * 验证当前请求是否是feign请求，不是的话是否具备角色
     *
     * @param request HttpServletRequest
     * @param roles   角色
     * @return 是否
     */
    public final boolean isFeignOrHasRole(HttpServletRequest request, String... roles) {
        // To be considered
        return false;
    }

    /**
     * 验证当前请求是否是feign请求，不是的话是否具备权限
     *
     * @param request     HttpServletRequest
     * @param authorities 权限
     * @return 是否
     */
    public final boolean isFeignOrHasAuthority(HttpServletRequest request, String... authorities) {
        // To be considered
        return false;
    }
}
