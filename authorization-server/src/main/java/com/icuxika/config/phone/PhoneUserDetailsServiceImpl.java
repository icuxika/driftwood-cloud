package com.icuxika.config.phone;

import com.icuxika.common.ApiData;
import com.icuxika.config.common.CommonUserService;
import com.icuxika.exception.FeignFallbackException;
import com.icuxika.user.feign.UserClient;
import com.icuxika.user.vo.UserAuthVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(value = "phoneUserDetailsService")
public class PhoneUserDetailsServiceImpl implements PhoneUserDetailsService, CommonUserService {

    private static final Logger L = LoggerFactory.getLogger(PhoneUserDetailsServiceImpl.class);

    @Autowired
    @Qualifier("com.icuxika.user.feign.UserClient")
    private UserClient userClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public UserDetails loadUserByPhone(String phone, String code) throws UsernameNotFoundException {
        System.out.println("phone: " + phone + ", code: " + code);
        System.out.println("REDIS: " + redisTemplate.opsForValue().get(phone));
        ApiData<UserAuthVO> userApiData = userClient.findByPhone(phone);
        if (!userApiData.isSuccess()) {
            L.error("根据手机号[" + phone + "]查询用户信息请求未成功");
            throw new FeignFallbackException("根据手机号查询用户信息请求未成功");
        }
        UserAuthVO user = userApiData.getData();
        if (user == null) {
            L.info("该手机号[" + phone + "]未注册");
            throw new UsernameNotFoundException("该手机号未注册");
        }
        return buildUserDetails(user);
    }
}
