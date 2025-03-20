package com.icuxika.authorization.config.qrcode;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.SecurityContextRepository;

public class QRCodeAuthenticationFilterDsl extends AbstractHttpConfigurer<QRCodeAuthenticationFilterDsl, HttpSecurity> {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        SecurityContextRepository securityContextRepository = http.getSharedObject(SecurityContextRepository.class);

        QRCodeAuthenticationFilter qrCodeAuthenticationFilter = new QRCodeAuthenticationFilter(authenticationManager);
        qrCodeAuthenticationFilter.setSessionStrategy(sessionAuthenticationStrategy);
        qrCodeAuthenticationFilter.setSecurityContextRepository(securityContextRepository);
        http.addFilterBefore(qrCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    public static QRCodeAuthenticationFilterDsl qrCodeAuthenticationFilterDsl() {
        return new QRCodeAuthenticationFilterDsl();
    }
}
