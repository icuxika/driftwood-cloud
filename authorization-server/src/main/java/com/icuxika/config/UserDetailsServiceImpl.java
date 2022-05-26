package com.icuxika.config;

import com.icuxika.common.ApiData;
import com.icuxika.config.common.CommonUserService;
import com.icuxika.modules.user.feign.UserClient;
import com.icuxika.modules.user.vo.UserAuthVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService, CommonUserService {

    private static final Logger L = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        ApiData<UserAuthVO> userApiData = userClient.findByUsername(s);
        if (!userApiData.isSuccess()) {
            L.error("根据用户名[" + s + "]查询用户信息请求未成功");
            throw new UsernameNotFoundException("根据用户名查询用户信息请求未成功");
        }

        UserAuthVO user = userApiData.getData();
        if (user == null) {
            L.error("该用户名[" + s + "]未注册");
            throw new UsernameNotFoundException("该用户名未注册");
        }

        return buildUserDetails(user);
    }
}
