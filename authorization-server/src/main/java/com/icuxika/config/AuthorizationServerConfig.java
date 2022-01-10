package com.icuxika.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icuxika.config.jackson2.UserDetailsImplMixin;
import com.icuxika.config.jose.Jwks;
import com.icuxika.config.password.PasswordAuthenticationConverter;
import com.icuxika.config.password.PasswordAuthenticationProvider;
import com.icuxika.config.phone.PhoneAuthenticationConverter;
import com.icuxika.config.phone.PhoneAuthenticationProvider;
import com.icuxika.config.phone.PhoneUserDetailsAuthenticationProvider;
import com.icuxika.user.feign.UserService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

    @Autowired
    private UserService userService;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer<>();
        http.apply(authorizationServerConfigurer.tokenEndpoint(oAuth2TokenEndpointConfigurer ->
                oAuth2TokenEndpointConfigurer.accessTokenRequestConverter(new DelegatingAuthenticationConverter(
                                Arrays.asList(
                                        new OAuth2AuthorizationCodeAuthenticationConverter(),
                                        new OAuth2RefreshTokenAuthenticationConverter(),
                                        new OAuth2ClientCredentialsAuthenticationConverter(),
                                        new PasswordAuthenticationConverter(),
                                        new PhoneAuthenticationConverter()
                                )
                        )
                )));

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        http
                .requestMatcher(endpointsMatcher)
                .authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .apply(authorizationServerConfigurer);

        SecurityFilterChain securityFilterChain = http.formLogin(Customizer.withDefaults()).build();
        // 密码模式
        addPasswordAuthenticationProvider(http);
        // 短信模式
        addPhoneAuthenticationProvider(http);
        return securityFilterChain;
    }

    @Bean
    public OAuth2AuthorizationService authorizationService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        JdbcOAuth2AuthorizationService service = new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository);
        JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper rowMapper = new JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper(registeredClientRepository);
        ObjectMapper objectMapper = new ObjectMapper();
        ClassLoader classLoader = JdbcOAuth2AuthorizationService.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        objectMapper.registerModules(securityModules);
        objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        objectMapper.registerModule(new CoreJackson2Module());
        objectMapper.addMixIn(UserDetailsImpl.class, UserDetailsImplMixin.class);
        rowMapper.setObjectMapper(objectMapper);
        service.setAuthorizationRowMapper(rowMapper);
        return service;
    }

    @Bean
    public OAuth2AuthorizationConsentService authorizationConsentService(JdbcTemplate jdbcTemplate, RegisteredClientRepository registeredClientRepository) {
        return new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder().issuer("http://driftwood-cloud:8900/auth").build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> buildCustomizer() {
        return context -> new JwtCustomizerImpl().customize(context);
    }

    /**
     * 密码模式
     */
    private void addPasswordAuthenticationProvider(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        ProviderSettings providerSettings = http.getSharedObject(ProviderSettings.class);
        OAuth2AuthorizationService authorizationService = http.getSharedObject(OAuth2AuthorizationService.class);
        JwtEncoder jwtEncoder = http.getSharedObject(JwtEncoder.class);
        OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer = context -> new JwtCustomizerImpl().customize(context);

        PasswordAuthenticationProvider passwordAuthenticationProvider = new PasswordAuthenticationProvider(authenticationManager, authorizationService, jwtEncoder);
        passwordAuthenticationProvider.setJwtCustomizer(jwtCustomizer);
        passwordAuthenticationProvider.setProviderSettings(providerSettings);
        http.authenticationProvider(passwordAuthenticationProvider);
    }

    /**
     * 短信模式
     */
    private void addPhoneAuthenticationProvider(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        ProviderSettings providerSettings = http.getSharedObject(ProviderSettings.class);
        OAuth2AuthorizationService authorizationService = http.getSharedObject(OAuth2AuthorizationService.class);
        JwtEncoder jwtEncoder = http.getSharedObject(JwtEncoder.class);
        OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer = context -> new JwtCustomizerImpl().customize(context);

        PhoneAuthenticationProvider phoneAuthenticationProvider = new PhoneAuthenticationProvider(authenticationManager, authorizationService, jwtEncoder);
        phoneAuthenticationProvider.setJwtCustomizer(jwtCustomizer);
        phoneAuthenticationProvider.setProviderSettings(providerSettings);
        http.authenticationProvider(phoneAuthenticationProvider);

        PhoneUserDetailsAuthenticationProvider phoneUserDetailsAuthenticationProvider = new PhoneUserDetailsAuthenticationProvider(userService);
        http.authenticationProvider(phoneUserDetailsAuthenticationProvider);
    }

    private static class JwtCustomizerImpl {
        private void customize(JwtEncodingContext context) {
            AbstractAuthenticationToken token = null;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof OAuth2ClientAuthenticationToken) {
                token = (AbstractAuthenticationToken) authentication;
            }

            if (token != null) {
                if (token.isAuthenticated() && OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                    Authentication userPasswordAuthentication = null;
                    AuthorizationGrantType authorizationGrantType = context.getAuthorizationGrantType();
                    if (authorizationGrantType == AuthorizationGrantType.AUTHORIZATION_CODE) {
                        userPasswordAuthentication = context.getPrincipal();
                    }

                    if (authorizationGrantType == AuthorizationGrantType.PASSWORD) {
                        userPasswordAuthentication = context.get(PasswordAuthenticationProvider.USERNAME_PASSWORD_AUTHENTICATION_KEY);
                    }

                    if (userPasswordAuthentication instanceof UsernamePasswordAuthenticationToken) {
                        UserDetails userDetails = (UserDetails) userPasswordAuthentication.getPrincipal();
                        Set<String> authorities = userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet());
                        JwtClaimsSet.Builder builder = context.getClaims();
                        builder.claim(OAuth2ParameterNames.SCOPE, authorities);
                    }

                    if (authorizationGrantType.equals(new AuthorizationGrantType(PhoneAuthenticationProvider.AUTHORIZATION_GRANT_TYPE_PHONE_VALUE))) {
                        Authentication phoneVerificationAuthentication = context.get(PhoneAuthenticationProvider.PHONE_VERIFICATION_AUTHENTICATION_KEY);
                        UserDetails userDetails = (UserDetails) phoneVerificationAuthentication.getPrincipal();
                        Set<String> authorities = userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet());
                        JwtClaimsSet.Builder builder = context.getClaims();
                        builder.claim(OAuth2ParameterNames.SCOPE, authorities);
                    }
                }
            }
        }
    }

}
