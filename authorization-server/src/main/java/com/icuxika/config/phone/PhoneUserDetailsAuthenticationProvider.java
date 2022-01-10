package com.icuxika.config.phone;

import com.icuxika.common.ApiData;
import com.icuxika.config.UserDetailsImpl;
import com.icuxika.user.entity.User;
import com.icuxika.user.feign.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collection;

public class PhoneUserDetailsAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    public PhoneUserDetailsAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String phone = (String) authentication.getPrincipal();
        ApiData<User> userApiData = userService.findByPhone(phone);
        if (!userApiData.isSuccess()) {
            throw new RuntimeException();
        }
        User user = userApiData.getData();
        if (user == null) {
            throw new BadCredentialsException("");
        }
        UserDetailsImpl userDetails = new UserDetailsImpl();
        BeanUtils.copyProperties(user, userDetails);

        Collection<? extends GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"), new SimpleGrantedAuthority("ROLE_ADMIN"));
        userDetails.setAuthorities(authorities);
        PhoneVerificationAuthenticationToken result = new PhoneVerificationAuthenticationToken(userDetails, "123456", userDetails.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return PhoneVerificationAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
