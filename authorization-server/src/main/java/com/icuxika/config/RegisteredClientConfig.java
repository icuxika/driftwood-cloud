package com.icuxika.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;

import java.time.Duration;
import java.util.Arrays;

@Configuration
public class RegisteredClientConfig {

    @Bean
    public RegisteredClientRepository registeredClientRepository(JdbcTemplate jdbcTemplate) {
        JdbcRegisteredClientRepository registeredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);

        RegisteredClient authorizationCodeRegisteredClient = RegisteredClient.withId("1")
                .clientId("id_authorization_code")
                .clientSecret("secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://www.baidu.com/baidu.html")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(false).build())
                .tokenSettings(tokenSettings())
                .build();
        RegisteredClient clientCredentialsRegisteredClient = RegisteredClient.withId("2")
                .clientId("id_client_credentials")
                .clientSecret("secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .tokenSettings(tokenSettings())
                .scope("ROLE_USER")
                .build();
        RegisteredClient passwordRegisteredClient = RegisteredClient.withId("3")
                .clientId("id_password")
                .clientSecret("secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.PASSWORD)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .tokenSettings(tokenSettings())
                .build();
        RegisteredClient phoneRegisteredClient = RegisteredClient.withId("4")
                .clientId("id_phone")
                .clientSecret("secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(new AuthorizationGrantType("phone"))
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .tokenSettings(tokenSettings())
                .build();

        Arrays.asList(authorizationCodeRegisteredClient, clientCredentialsRegisteredClient, passwordRegisteredClient, phoneRegisteredClient).forEach(registeredClient -> {
            String id = registeredClient.getId();
            String clientId = registeredClient.getClientId();
            RegisteredClient existRegisteredClient = registeredClientRepository.findById(id);
            if (existRegisteredClient == null) {
                existRegisteredClient = registeredClientRepository.findByClientId(clientId);
            }
            if (existRegisteredClient == null) {
                registeredClientRepository.save(registeredClient);
            }
        });

        return registeredClientRepository;
    }

    private TokenSettings tokenSettings() {
        return TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofMinutes(10))
                .refreshTokenTimeToLive(Duration.ofMinutes(60))
                .build();
    }
}
