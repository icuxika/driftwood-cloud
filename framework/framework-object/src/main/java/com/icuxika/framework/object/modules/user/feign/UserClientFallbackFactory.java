package com.icuxika.framework.object.modules.user.feign;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.object.modules.user.vo.UserAuthVO;
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
            public ApiData<UserAuthVO> findByOpenid(String openid, Integer type) {
                L.warn("[findByOpenid]进入fallback");
                return ApiData.errorMsg("[findByOpenid]进入fallback");
            }

            @Override
            public ApiData<Boolean> findThirdBindByOpenid(String openid, Integer type) {
                L.warn("[findThirdBindByOpenid]进入fallback");
                return ApiData.errorMsg("[findThirdBindByOpenid]进入fallback");
            }

            @Override
            public ApiData<Void> updateUserIP(Long userId, String ip) {
                L.warn("更新用户最近登录ip地址失败");
                return ApiData.errorMsg("[updateUserIP]进入fallback");
            }
        };
    }
}
