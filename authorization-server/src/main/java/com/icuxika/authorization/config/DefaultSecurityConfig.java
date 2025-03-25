package com.icuxika.authorization.config;

import com.icuxika.authorization.config.qrcode.QRCodeAuthenticationFilterDsl;
import com.icuxika.authorization.config.qrcode.QRCodeUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.session.Session;
import org.springframework.session.security.SpringSessionBackedSessionRegistry;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration()
@RequiredArgsConstructor
public class DefaultSecurityConfig<S extends Session> {

    private final SpringSessionBackedSessionRegistry<S> sessionRegistry;
    private final QRCodeUserDetailsService qrCodeUserDetailsService;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.
                authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/assets/**", "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(formLogin ->
                        formLogin.loginPage("/login")
                )
                .with(QRCodeAuthenticationFilterDsl.qrCodeAuthenticationFilterDsl(), qrCodeAuthenticationFilterDsl -> {
                    qrCodeAuthenticationFilterDsl.setQrCodeUserDetailsService(qrCodeUserDetailsService);
                })
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .expiredUrl("/login?expired")
                        .expiredSessionStrategy(event -> {
                            // 配置了expiredSessionStrategy则expiredUrl不再起作用
                            HttpServletRequest request = event.getRequest();
                            HttpServletResponse response = event.getResponse();
                            new HttpSessionRequestCache().saveRequest(request, response);
                            new DefaultRedirectStrategy().sendRedirect(request, response, "/login?expired");
                        })
                        .maxSessionsPreventsLogin(false)
                        .sessionRegistry(sessionRegistry)
                )
                .rememberMe(rememberMe -> rememberMe
                        .rememberMeServices(rememberMeServices())
                        .useSecureCookie(true)
                );
        return http.build();
    }

    @Bean
    public SpringSessionRememberMeServices rememberMeServices() {
        SpringSessionRememberMeServices rememberMeServices =
                new SpringSessionRememberMeServices();
        rememberMeServices.setAlwaysRemember(true);
        return rememberMeServices;
    }

}
