package com.icuxika.authorization.config.qrcode;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

@RequiredArgsConstructor
public class QRCodeAuthenticationProvider implements AuthenticationProvider {

    private final QRCodeUserDetailsService qrCodeUserDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserDetails userDetails = qrCodeUserDetailsService.loadUserByQRCodeId(authentication.getCredentials().toString());
        UsernamePasswordAuthenticationToken result = UsernamePasswordAuthenticationToken.authenticated(userDetails, authentication.getCredentials(), userDetails.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return QRCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
