package com.icuxika.authorization.config.phone;

import com.icuxika.authorization.config.common.CommonUserService;
import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.basic.transfer.auth.PhoneCodeCache;
import com.icuxika.framework.object.modules.user.feign.UserClient;
import com.icuxika.framework.object.modules.user.vo.UserAuthVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(value = "phoneUserDetailsService")
@RequiredArgsConstructor
@Slf4j
public class PhoneUserDetailsServiceImpl implements PhoneUserDetailsService, CommonUserService {

    private final UserClient userClient;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserDetails loadUserByPhone(String phone, String code) throws UsernameNotFoundException {
        PhoneCodeCache phoneCodeCache = (PhoneCodeCache) redisTemplate.opsForHash().get(SystemConstant.REDIS_OAUTH2_PHONE_CODE, phone);
        if (phoneCodeCache == null) {
            if (log.isErrorEnabled()) {
                log.error("当前手机号[{}]请求的验证码不存在或已失效", phone);
            }
            throw new UsernameNotFoundException("当前手机号请求的验证码不存在或已失效");
        }
        if (phoneCodeCache.getCreateTime() + phoneCodeCache.getTime() < System.currentTimeMillis()) {
            if (log.isErrorEnabled()) {
                log.error("当前手机号[{}]请求的验证码已过期", phone);
            }
            throw new UsernameNotFoundException("当前手机号请求的验证码已过期");
        }
        if (!phoneCodeCache.getCode().equals(code)) {
            if (log.isErrorEnabled()) {
                log.error("当前手机号[{}]对应的验证码不正确", phone);
            }
            throw new UsernameNotFoundException("当前手机号对应的验证码不正确");
        }
        ApiData<UserAuthVO> userApiData = userClient.findByPhone(phone);
        if (!userApiData.isSuccess()) {
            if (log.isErrorEnabled()) {
                log.error("根据手机号[{}]查询用户信息请求未成功", phone);
            }
            throw new UsernameNotFoundException("根据手机号查询用户信息请求未成功");
        }
        UserAuthVO user = userApiData.getData();
        if (user == null) {
            if (log.isErrorEnabled()) {
                log.error("该手机号[{}]未注册", phone);
            }
            throw new UsernameNotFoundException("该手机号未注册");
        }
        return buildUserDetails(user);
    }
}
