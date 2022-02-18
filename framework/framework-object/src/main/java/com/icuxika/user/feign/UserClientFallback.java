package com.icuxika.user.feign;

import com.icuxika.common.ApiData;
import com.icuxika.user.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    private static final Logger L = LoggerFactory.getLogger(UserClientFallback.class);

    @Override
    public ApiData<User> findByUsername(String username) {
        L.warn("[findByUsername]进入fallback");
        return ApiData.error("[findByUsername]进入fallback");
    }

    @Override
    public ApiData<User> findByPhone(String phone) {
        L.warn("[findByPhone]进入fallback");
        return ApiData.error("[findByPhone]进入fallback");
    }
}
