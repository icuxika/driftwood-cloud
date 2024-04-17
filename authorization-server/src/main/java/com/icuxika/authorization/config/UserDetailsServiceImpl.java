package com.icuxika.authorization.config;

import com.icuxika.authorization.config.common.CommonUserService;
import com.icuxika.framework.basic.common.ApiData;
import com.icuxika.framework.object.modules.user.feign.UserClient;
import com.icuxika.framework.object.modules.user.vo.UserAuthVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service(value = "userDetailsService")
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService, CommonUserService {

    private final UserClient userClient;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        ApiData<UserAuthVO> userApiData = userClient.findByUsername(s);
        if (!userApiData.isSuccess()) {
            if (log.isErrorEnabled()) {
                log.error("根据用户名[{}]查询用户信息请求未成功", s);
            }
            throw new UsernameNotFoundException("根据用户名查询用户信息请求未成功");
        }

        UserAuthVO user = userApiData.getData();
        if (user == null) {
            if (log.isErrorEnabled()) {
                log.error("该用户名[{}]未注册", s);
            }
            throw new UsernameNotFoundException("该用户名未注册");
        }

        return buildUserDetails(user);
    }
}
