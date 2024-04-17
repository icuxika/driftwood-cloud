package com.icuxika.authorization.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration(proxyBeanMethods = false)
public class DefaultSecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        // 暂不支持通过网关访问授权地址，需要考虑授权码模式的重定向地址跳转
        // http://localhost:8901/oauth2/authorize?response_type=code&client_id=id_authorization_code
        // http://localhost:8900/auth/oauth2/authorize?response_type=code&client_id=id_authorization_code
        return http.build();
    }

}
