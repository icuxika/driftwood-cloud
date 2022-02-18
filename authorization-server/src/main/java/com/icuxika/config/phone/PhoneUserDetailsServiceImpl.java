package com.icuxika.config.phone;

import com.icuxika.common.ApiData;
import com.icuxika.config.UserDetailsImpl;
import com.icuxika.exception.FeignFallbackException;
import com.icuxika.user.entity.User;
import com.icuxika.user.feign.UserClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Service(value = "phoneUserDetailsService")
public class PhoneUserDetailsServiceImpl implements PhoneUserDetailsService {

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
        ApiData<User> userApiData = userClient.findByPhone(phone);
        if (!userApiData.isSuccess()) {
            L.error("根据手机号[" + phone + "]查询用户信息请求未成功");
            throw new FeignFallbackException("根据手机号查询用户信息请求未成功");
        }
        User user = userApiData.getData();
        if (user == null) {
            L.info("该手机号[" + phone + "]未注册");
            throw new UsernameNotFoundException("该手机号未注册");
        }
        UserDetailsImpl userDetails = new UserDetailsImpl();
        BeanUtils.copyProperties(user, userDetails);
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"));
        userDetails.setAuthorities(authorities);
        return userDetails;
    }
}
