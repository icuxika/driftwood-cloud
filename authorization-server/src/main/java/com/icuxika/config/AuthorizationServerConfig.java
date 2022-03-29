package com.icuxika.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icuxika.config.jackson2.LongMixin;
import com.icuxika.config.jackson2.PhoneVerificationAuthenticationTokenMixin;
import com.icuxika.config.jackson2.UserDetailsImplMixin;
import com.icuxika.config.jose.Jwks;
import com.icuxika.config.password.PasswordAuthenticationConverter;
import com.icuxika.config.password.PasswordAuthenticationProvider;
import com.icuxika.config.password.PasswordAuthenticationToken;
import com.icuxika.config.phone.*;
import com.icuxika.constant.ClientType;
import com.icuxika.constant.SystemConstant;
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
import org.springframework.security.jackson2.CoreJackson2Module;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.*;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2RefreshTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.oauth2.server.authorization.web.authentication.DelegatingAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2AuthorizationCodeAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2ClientCredentialsAuthenticationConverter;
import org.springframework.security.oauth2.server.authorization.web.authentication.OAuth2RefreshTokenAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private PhoneUserDetailsService phoneUserDetailsService;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfigurer<HttpSecurity> authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer<>();
        http.apply(authorizationServerConfigurer.tokenEndpoint(oAuth2TokenEndpointConfigurer ->
                oAuth2TokenEndpointConfigurer
                        .accessTokenRequestConverter(new DelegatingAuthenticationConverter(
                                        Arrays.asList(
                                                new OAuth2AuthorizationCodeAuthenticationConverter(),
                                                new OAuth2RefreshTokenAuthenticationConverter(),
                                                new OAuth2ClientCredentialsAuthenticationConverter(),
                                                new PasswordAuthenticationConverter(),
                                                new PhoneAuthenticationConverter()
                                        )
                                )
                        )
                        .accessTokenResponseHandler(authenticationSuccessHandler)));

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        http
                .requestMatcher(endpointsMatcher)
                .authorizeRequests(authorizeRequests -> authorizeRequests.anyRequest().authenticated())
                .csrf(csrf -> csrf.ignoringRequestMatchers(endpointsMatcher))
                .apply(authorizationServerConfigurer);

        SecurityFilterChain securityFilterChain = http.formLogin(Customizer.withDefaults()).build();
        // 添加自定义验证模式
        addCustomProviders(http);
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
        objectMapper.addMixIn(Long.class, LongMixin.class);
        objectMapper.addMixIn(PhoneVerificationAuthenticationToken.class, PhoneVerificationAuthenticationTokenMixin.class);
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

    /**
     * 自定义token携带信息
     */
    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> buildCustomizer() {
        return context -> {
            AbstractAuthenticationToken token = null;

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof OAuth2ClientAuthenticationToken) {
                token = (AbstractAuthenticationToken) authentication;
            }

            if (token != null) {
                if (token.isAuthenticated() && OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
                    AuthorizationGrantType authorizationGrantType = context.getAuthorizationGrantType();

                    Authentication userPasswordAuthentication = context.getPrincipal();

                    Integer clientType = 0;

                    if (authorizationGrantType == AuthorizationGrantType.AUTHORIZATION_CODE) {
                        OAuth2AuthorizationCodeAuthenticationToken oAuth2AuthorizationCodeAuthenticationToken = context.getAuthorizationGrant();
                        clientType = getClientType(oAuth2AuthorizationCodeAuthenticationToken.getAdditionalParameters());
                    }

                    if (authorizationGrantType == AuthorizationGrantType.REFRESH_TOKEN) {
                        OAuth2RefreshTokenAuthenticationToken oAuth2RefreshTokenAuthenticationToken = context.getAuthorizationGrant();
                        clientType = getClientType(oAuth2RefreshTokenAuthenticationToken.getAdditionalParameters());
                    }

                    if (authorizationGrantType == AuthorizationGrantType.PASSWORD) {
                        PasswordAuthenticationToken passwordAuthenticationToken = context.getAuthorizationGrant();
                        clientType = getClientType(passwordAuthenticationToken.getAdditionalParameters());
                    }

                    if (authorizationGrantType.equals(new AuthorizationGrantType(PhoneAuthenticationProvider.AUTHORIZATION_GRANT_TYPE_PHONE_VALUE))) {
                        PhoneAuthenticationToken phoneAuthenticationToken = context.getAuthorizationGrant();
                        clientType = getClientType(phoneAuthenticationToken.getAdditionalParameters());
                    }

                    UserDetailsImpl userDetails = null;
                    if (userPasswordAuthentication instanceof UsernamePasswordAuthenticationToken) {
                        userDetails = (UserDetailsImpl) userPasswordAuthentication.getPrincipal();
                    }
                    if (userPasswordAuthentication instanceof PhoneVerificationAuthenticationToken) {
                        userDetails = (UserDetailsImpl) userPasswordAuthentication.getPrincipal();
                    }
                    if (userDetails != null) {
                        Set<String> authorities = userDetails.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toSet());
                        JwtClaimsSet.Builder builder = context.getClaims();
                        builder.claim(SystemConstant.OAUTH2_JWT_CLAIM_KEY_AUTHORITIES, authorities);
                        builder.claim(SystemConstant.OAUTH2_JWT_CLAIM_KEY_USER_ID, userDetails.getId());
                        builder.claim(SystemConstant.OAUTH2_JWT_CLAIM_KEY_CLIENT_TYPE, clientType);
                    }
                }
            }
        };
    }

    /**
     * 添加自定义认证方式
     */
    private void addCustomProviders(HttpSecurity http) {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        OAuth2AuthorizationService authorizationService = http.getSharedObject(OAuth2AuthorizationService.class);
        @SuppressWarnings("unchecked")
        OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator = http.getSharedObject(OAuth2TokenGenerator.class);
        // 密码模式
        addPasswordAuthenticationProvider(http, authenticationManager, authorizationService, tokenGenerator);
        // 短信模式
        addPhoneAuthenticationProvider(http, authenticationManager, authorizationService, tokenGenerator);
    }

    /**
     * 密码模式
     */
    private void addPasswordAuthenticationProvider(HttpSecurity http, AuthenticationManager authenticationManager, OAuth2AuthorizationService authorizationService, OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        PasswordAuthenticationProvider passwordAuthenticationProvider = new PasswordAuthenticationProvider(authenticationManager, authorizationService, tokenGenerator);
        http.authenticationProvider(passwordAuthenticationProvider);
    }

    /**
     * 短信模式
     */
    private void addPhoneAuthenticationProvider(HttpSecurity http, AuthenticationManager authenticationManager, OAuth2AuthorizationService authorizationService, OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        PhoneAuthenticationProvider phoneAuthenticationProvider = new PhoneAuthenticationProvider(authenticationManager, authorizationService, tokenGenerator);
        http.authenticationProvider(phoneAuthenticationProvider);

        PhoneUserDetailsAuthenticationProvider phoneUserDetailsAuthenticationProvider = new PhoneUserDetailsAuthenticationProvider(phoneUserDetailsService);
        http.authenticationProvider(phoneUserDetailsAuthenticationProvider);
    }

    /**
     * 判断请求中是否携带了设备类型信息，没有的话，默认为{@link ClientType#HTML}
     */
    private Integer getClientType(Map<String, Object> additionalParameters) {
        Integer clientType = ClientType.HTML;
        if (additionalParameters != null && additionalParameters.containsKey("client_type")) {
            Object value = additionalParameters.get("client_type");
            // 此处value为String类型，放入claim中后又自动转换为Long类型
            return Integer.valueOf(String.valueOf(value));
        }
        return clientType;
    }

}
