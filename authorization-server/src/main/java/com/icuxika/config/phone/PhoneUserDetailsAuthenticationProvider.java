package com.icuxika.config.phone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 类似于 {@link org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider}
 * 用来支持根据手机号查询用户名
 */
public class PhoneUserDetailsAuthenticationProvider implements AuthenticationProvider {

    private static final Logger L = LoggerFactory.getLogger(PhoneUserDetailsAuthenticationProvider.class);

    private final PhoneUserDetailsService phoneUserDetailsService;

    public PhoneUserDetailsAuthenticationProvider(PhoneUserDetailsService phoneUserDetailsService) {
        this.phoneUserDetailsService = phoneUserDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String phone = (String) authentication.getPrincipal();
        String code = (String) authentication.getCredentials();
        UserDetails userDetails;
        try {
            userDetails = phoneUserDetailsService.loadUserByPhone(phone, code);
        } catch (UsernameNotFoundException e) {
            // Spring Security 默认返回国际化的 "Bad credentials"（密码模式中）
            throw new BadCredentialsException(e.getMessage());
        }
        // 账户状态检测
        preAuthenticationCheck(userDetails);
        PhoneVerificationAuthenticationToken result = new PhoneVerificationAuthenticationToken(userDetails, "123456", userDetails.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return PhoneVerificationAuthenticationToken.class.isAssignableFrom(aClass);
    }

    private void preAuthenticationCheck(UserDetails user) {
        if (!user.isAccountNonLocked()) {
            throw new LockedException("User account is locked");
        }
        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled");
        }
        if (!user.isAccountNonExpired()) {
            throw new AccountExpiredException("User account has expired");
        }
    }
}
