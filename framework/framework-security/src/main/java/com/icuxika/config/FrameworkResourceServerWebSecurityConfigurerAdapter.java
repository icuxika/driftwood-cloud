package com.icuxika.config;

import com.icuxika.converter.PasswordJwtAuthenticationConverter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

public class FrameworkResourceServerWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    JwtAuthenticationConverter jwtAuthenticationConverter() {
        PasswordJwtAuthenticationConverter passwordJwtAuthenticationConverter = new PasswordJwtAuthenticationConverter();
        passwordJwtAuthenticationConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(passwordJwtAuthenticationConverter);
        return jwtAuthenticationConverter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        // flowable-ui -> modeler
                        .antMatchers("/actuator/**", "/druid/**", "/modeler/**", "/modeler-app/**").permitAll()
                        .requestMatchers(new AuthorizeRequestMatcher()).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer
                        .authenticationEntryPoint(new AuthenticationEntryPointImpl())
                        .accessDeniedHandler(new AccessDeniedHandlerImpl())
                        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );
    }
}
