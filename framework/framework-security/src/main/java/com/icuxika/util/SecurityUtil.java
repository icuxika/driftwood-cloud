package com.icuxika.util;

import com.icuxika.constant.SystemConstant;
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

    static Long getUserId(Jwt jwt) {
        return getClaim(jwt, SystemConstant.OAUTH2_JWT_CLAIM_KEY_USER_ID, Long.class);
    }

    static Long getUserId() {
        return getClaim(getJwt(), SystemConstant.OAUTH2_JWT_CLAIM_KEY_USER_ID, Long.class);
    }
}
