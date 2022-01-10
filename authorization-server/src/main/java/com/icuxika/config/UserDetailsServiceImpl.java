package com.icuxika.config;

import com.icuxika.common.ApiData;
import com.icuxika.user.entity.User;
import com.icuxika.user.feign.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;

@Service(value = "userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        ApiData<User> userApiData = userService.findByUsername(s);
        if (!userApiData.isSuccess()) {
            throw new UsernameNotFoundException("系统异常");
        }

        User user = userApiData.getData();
        if (user == null) {
            throw new UsernameNotFoundException("用户名为空");
        }

        UserDetailsImpl userDetails = new UserDetailsImpl();
        BeanUtils.copyProperties(user, userDetails);
        Collection<? extends GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"));
        userDetails.setAuthorities(authorities);
        return userDetails;
    }
}
