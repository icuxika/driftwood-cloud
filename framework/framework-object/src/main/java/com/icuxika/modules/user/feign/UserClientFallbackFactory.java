package com.icuxika.modules.user.feign;

import com.icuxika.common.ApiData;
import com.icuxika.modules.user.vo.UserAuthVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    private static final Logger L = LoggerFactory.getLogger(UserClientFallbackFactory.class);

    @Override
    public UserClient create(Throwable cause) {

        L.error(cause.getMessage());

        return new UserClient() {
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

            @Override
            public ApiData<Void> updateUserIP(Long userId, String ip) {
                L.warn("更新用户最近登录ip地址失败");
                return ApiData.errorMsg("[updateUserIP]进入fallback");
            }
        };
    }
}
