package com.icuxika.util;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public interface SecurityUtil {

    String CLAIM_USER_ID = "userId";

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
        return getClaim(jwt, CLAIM_USER_ID, Long.class);
    }

    static Long getUserId() {
        return getClaim(getJwt(), CLAIM_USER_ID, Long.class);
    }
}
