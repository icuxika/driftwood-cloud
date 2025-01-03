package com.icuxika.framework.service.activiti.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Set;

@EnableWebSecurity
@Configuration
public class DefaultSecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/**").permitAll()
                );
        return httpSecurity.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/**");
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            Set<GrantedAuthority> authorities = Set.of(new SimpleGrantedAuthority("ROLE_ACTIVITI_USER"), new SimpleGrantedAuthority("ROLE_ACTIVITI_ADMIN"));
            return new User(username, username, true, true, true, true, authorities);
        };
    }
}
