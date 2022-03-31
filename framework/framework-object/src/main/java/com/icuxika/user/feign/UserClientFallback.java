package com.icuxika.user.feign;

import com.icuxika.common.ApiData;
import com.icuxika.user.vo.UserAuthVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient {

    private static final Logger L = LoggerFactory.getLogger(UserClientFallback.class);

    @Override
    public ApiData<UserAuthVO> findByUsername(String username) {
        L.warn("[findByUsername]进入fallback");
        return ApiData.errorMsg("[findByUsername]进入fallback");
    }

    @Override
    public ApiData<UserAuthVO> findByPhone(String phone) {
        L.warn("[findByPhone]进入fallback");
        return ApiData.errorMsg("[findByPhone]进入fallback");
    }
}
