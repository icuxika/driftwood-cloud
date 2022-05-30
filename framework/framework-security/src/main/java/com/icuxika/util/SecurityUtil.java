package com.icuxika.util;

import com.icuxika.constant.SystemConstant;
import com.icuxika.exception.GlobalServiceException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public interface SecurityUtil {

    static Jwt getJwt() {
        return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    static <T> T getClaim(Jwt jwt, String claim, Class<T> clazz) {
        T value = null;
        if (jwt.hasClaim(claim)) {
            value = jwt.getClaim(claim);
        }
        return value;
    }

    /**
     * 获取登录用户的id
     *
     * @return user id
     */
    static Long getUserId() {
        return getClaim(getJwt(), SystemConstant.OAUTH2_JWT_CLAIM_KEY_USER_ID, Long.class);
    }

    /**
     * 获取登录用户的设备类型
     *
     * @return user id
     */
    static Integer getClientType() {
        return getClaim(getJwt(), SystemConstant.OAUTH2_JWT_CLAIM_KEY_CLIENT_TYPE, Integer.class);
    }

    /**
     * 获取租户id
     *
     * @return tenant id
     */
    static String getTenantId() {
//        return getClaim(getJwt(), "tenantId", String.class);
        throw new GlobalServiceException("尚不支持");
    }
}
