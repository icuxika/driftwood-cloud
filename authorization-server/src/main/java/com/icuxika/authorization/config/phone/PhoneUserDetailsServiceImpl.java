package com.icuxika.authorization.config.phone;

import com.icuxika.authorization.config.common.CommonUserService;
import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.basic.constant.SystemConstant;
import com.icuxika.framework.basic.transfer.auth.PhoneCodeCache;
import com.icuxika.framework.object.modules.user.feign.UserClient;
import com.icuxika.framework.object.modules.user.vo.UserAuthVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(value = "phoneUserDetailsService")
public class PhoneUserDetailsServiceImpl implements PhoneUserDetailsService, CommonUserService {

    private static final Logger L = LoggerFactory.getLogger(PhoneUserDetailsServiceImpl.class);

    @Autowired
    private UserClient userClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public UserDetails loadUserByPhone(String phone, String code) throws UsernameNotFoundException {
        PhoneCodeCache phoneCodeCache = (PhoneCodeCache) redisTemplate.opsForHash().get(SystemConstant.REDIS_OAUTH2_PHONE_CODE, phone);
        if (phoneCodeCache == null) {
            if (L.isErrorEnabled()) {
                L.error("当前手机号[" + phone + "]请求的验证码不存在或已失效");
            }
            throw new UsernameNotFoundException("当前手机号请求的验证码不存在或已失效");
        }
        if (phoneCodeCache.getCreateTime() + phoneCodeCache.getTime() < System.currentTimeMillis()) {
            if (L.isErrorEnabled()) {
                L.error("当前手机号[" + phone + "]请求的验证码已过期");
            }
            throw new UsernameNotFoundException("当前手机号请求的验证码已过期");
        }
        if (!phoneCodeCache.getCode().equals(code)) {
            if (L.isErrorEnabled()) {
                L.error("当前手机号[" + phone + "]对应的验证码不正确");
            }
            throw new UsernameNotFoundException("当前手机号对应的验证码不正确");
        }
        ApiData<UserAuthVO> userApiData = userClient.findByPhone(phone);
        if (!userApiData.isSuccess()) {
            if (L.isErrorEnabled()) {
                L.error("根据手机号[" + phone + "]查询用户信息请求未成功");
            }
            throw new UsernameNotFoundException("根据手机号查询用户信息请求未成功");
        }
        UserAuthVO user = userApiData.getData();
        if (user == null) {
            if (L.isErrorEnabled()) {
                L.error("该手机号[" + phone + "]未注册");
            }
            throw new UsernameNotFoundException("该手机号未注册");
        }
        return buildUserDetails(user);
    }
}
