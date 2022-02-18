package com.icuxika.config.phone;

import com.icuxika.exception.FeignFallbackException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
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
        } catch (UsernameNotFoundException | FeignFallbackException e) {
            L.error("根据手机号[" + phone + "]查询用户信息失败");
            throw new BadCredentialsException(e.getMessage());
        }
        PhoneVerificationAuthenticationToken result = new PhoneVerificationAuthenticationToken(userDetails, "123456", userDetails.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return PhoneVerificationAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
