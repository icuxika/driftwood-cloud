package com.icuxika.framework.object.modules.user.feign;

import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.object.modules.user.vo.UserAuthVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {

        if (log.isErrorEnabled()) {
            log.error(cause.getMessage());
        }

        return new UserClient() {
            @Override
            public ApiData<UserAuthVO> findByUsername(String username) {
                if (log.isWarnEnabled()) {
                    log.warn("[findByUsername]进入fallback");
                }
                return ApiData.errorMsg("[findByUsername]进入fallback");
            }

            @Override
            public ApiData<UserAuthVO> findByPhone(String phone) {
                if (log.isWarnEnabled()) {
                    log.warn("[findByPhone]进入fallback");
                }
                return ApiData.errorMsg("[findByPhone]进入fallback");
            }

            @Override
            public ApiData<UserAuthVO> findByOpenid(String openid, Integer type) {
                if (log.isWarnEnabled()) {
                    log.warn("[findByOpenid]进入fallback");
                }
                return ApiData.errorMsg("[findByOpenid]进入fallback");
            }

            @Override
            public ApiData<Boolean> findThirdBindByOpenid(String openid, Integer type) {
                if (log.isWarnEnabled()) {
                    log.warn("[findThirdBindByOpenid]进入fallback");
                }
                return ApiData.errorMsg("[findThirdBindByOpenid]进入fallback");
            }

            @Override
            public ApiData<Void> updateUserIP(Long userId, String ip) {
                if (log.isWarnEnabled()) {
                    log.warn("更新用户最近登录ip地址失败");
                }
                return ApiData.errorMsg("[updateUserIP]进入fallback");
            }
        };
    }
}
