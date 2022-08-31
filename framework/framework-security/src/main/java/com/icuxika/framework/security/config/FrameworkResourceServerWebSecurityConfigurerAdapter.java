package com.icuxika.framework.security.config;

import com.icuxika.framework.basic.constant.SystemConstant;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.List;

public class FrameworkResourceServerWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

    /**
     * 允许匿名访问的资源
     */
    private final List<String> anonymousPathList;

    public FrameworkResourceServerWebSecurityConfigurerAdapter(List<String> anonymousPathList) {
        this.anonymousPathList = anonymousPathList;
    }

    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName(SystemConstant.OAUTH2_JWT_CLAIM_KEY_AUTHORITIES);

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests(authorizeRequests -> authorizeRequests
                        // flowable-ui -> modeler
                        .antMatchers("/actuator/**", "/druid/**", "/modeler/**", "/modeler-app/**").permitAll()
                        .antMatchers(anonymousPathList.toArray(new String[0])).permitAll()
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
