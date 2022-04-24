package com.icuxika.config;

import com.icuxika.common.ApiData;
import com.icuxika.config.common.CommonUserService;
import com.icuxika.modules.user.feign.UserClient;
import com.icuxika.modules.user.vo.UserAuthVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService, CommonUserService {

    @Autowired
    @Qualifier("com.icuxika.modules.user.feign.UserClient")
    private UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        ApiData<UserAuthVO> userApiData = userClient.findByUsername(s);
        if (!userApiData.isSuccess()) {
            throw new UsernameNotFoundException("系统异常");
        }

        UserAuthVO user = userApiData.getData();
        if (user == null) {
            throw new UsernameNotFoundException("用户名为空");
        }

        return buildUserDetails(user);
    }
}
