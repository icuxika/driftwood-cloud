package com.icuxika.config.phone;

import com.icuxika.config.source.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.JoseHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.util.CollectionUtils;

import java.security.Principal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PhoneAuthenticationProvider implements AuthenticationProvider {

    public static final String AUTHORIZATION_GRANT_TYPE_PHONE_VALUE = "phone";
    public static final String OAUTH2_PARAMETER_NAME_PHONE = "phone";
    public static final String OAUTH2_PARAMETER_NAME_CODE = "code";

    private static final StringKeyGenerator DEFAULT_REFRESH_TOKEN_GENERATOR = new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);
    public static final String PHONE_VERIFICATION_AUTHENTICATION_KEY = PhoneVerificationAuthenticationToken.class.getName().concat(".PRINCIPAL");

    private final AuthenticationManager authenticationManager;
    private final OAuth2AuthorizationService authorizationService;
    private final JwtEncoder jwtEncoder;
    private OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer = (context) -> {
    };
    private Supplier<String> refreshTokenGenerator = DEFAULT_REFRESH_TOKEN_GENERATOR::generateKey;
    private ProviderSettings providerSettings;

    public PhoneAuthenticationProvider(AuthenticationManager authenticationManager, OAuth2AuthorizationService authorizationService, JwtEncoder jwtEncoder) {
        this.authenticationManager = authenticationManager;
        this.authorizationService = authorizationService;
        this.jwtEncoder = jwtEncoder;
    }

    public void setJwtCustomizer(OAuth2TokenCustomizer<JwtEncodingContext> jwtCustomizer) {
        this.jwtCustomizer = jwtCustomizer;
    }

    @Autowired(required = false)
    public void setProviderSettings(ProviderSettings providerSettings) {
        this.providerSettings = providerSettings;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        PhoneAuthenticationToken phoneAuthenticationToken = (PhoneAuthenticationToken) authentication;
        OAuth2ClientAuthenticationToken clientPrincipal = getAuthenticatedClientElseThrowInvalidClient(phoneAuthenticationToken);
        RegisteredClient registeredClient = clientPrincipal.getRegisteredClient();

        if (!registeredClient.getAuthorizationGrantTypes().contains(new AuthorizationGrantType(AUTHORIZATION_GRANT_TYPE_PHONE_VALUE))) {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.UNAUTHORIZED_CLIENT);
        }

        Map<String, Object> additionalParameters = phoneAuthenticationToken.getAdditionalParameters();
        String phone = (String) additionalParameters.get(OAUTH2_PARAMETER_NAME_PHONE);
        String code = (String) additionalParameters.get(OAUTH2_PARAMETER_NAME_CODE);

        try {
            PhoneVerificationAuthenticationToken phoneVerificationAuthenticationToken = new PhoneVerificationAuthenticationToken(phone, code);
            Authentication phoneVerificationAuthentication = authenticationManager.authenticate(phoneVerificationAuthenticationToken);
            Set<String> authorizedScopes = registeredClient.getScopes();

            if (!CollectionUtils.isEmpty(phoneAuthenticationToken.getScopes())) {
                Set<String> unauthorizedScopes = phoneAuthenticationToken.getScopes().stream()
                        .filter(requestScope -> !registeredClient.getScopes().contains(requestScope))
                        .collect(Collectors.toSet());

                if (!CollectionUtils.isEmpty(unauthorizedScopes)) {
                    throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_SCOPE);
                }

                authorizedScopes = new LinkedHashSet<>(phoneAuthenticationToken.getScopes());
            }

            String issuer = this.providerSettings != null ? this.providerSettings.getIssuer() : null;

            JoseHeader.Builder headersBuilder = JwtUtils.headers();
            JwtClaimsSet.Builder claimsBuilder = JwtUtils.accessTokenClaims(
                    registeredClient, issuer, clientPrincipal.getName(), authorizedScopes
            );

            JwtEncodingContext context = JwtEncodingContext.with(headersBuilder, claimsBuilder)
                    .registeredClient(registeredClient)
                    .principal(phoneVerificationAuthentication)
                    .authorizedScopes(authorizedScopes)
                    .tokenType(OAuth2TokenType.ACCESS_TOKEN)
                    .authorizationGrantType(new AuthorizationGrantType(AUTHORIZATION_GRANT_TYPE_PHONE_VALUE))
                    .authorizationGrant(phoneAuthenticationToken)
                    .put(PHONE_VERIFICATION_AUTHENTICATION_KEY, phoneVerificationAuthentication)
                    .build();

            this.jwtCustomizer.customize(context);

            JoseHeader headers = context.getHeaders().build();
            JwtClaimsSet claims = context.getClaims().build();
            Jwt jwtAccessToken = this.jwtEncoder.encode(headers, claims);

            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    jwtAccessToken.getTokenValue(),
                    jwtAccessToken.getIssuedAt(),
                    jwtAccessToken.getExpiresAt(),
                    authorizedScopes
            );

            OAuth2RefreshToken refreshToken = null;
            if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
                refreshToken = generateRefreshToken(registeredClient.getTokenSettings().getRefreshTokenTimeToLive());
            }

            OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                    .principalName(phoneVerificationAuthentication.getName())
                    .authorizationGrantType(new AuthorizationGrantType(AUTHORIZATION_GRANT_TYPE_PHONE_VALUE))
                    .token(accessToken, metadata -> metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, jwtAccessToken.getClaims()))
                    .attribute(OAuth2Authorization.AUTHORIZED_SCOPE_ATTRIBUTE_NAME, authorizedScopes)
                    .attribute(Principal.class.getName(), phoneVerificationAuthentication);

            if (refreshToken != null) {
                authorizationBuilder.refreshToken(refreshToken);
            }

            OAuth2Authorization authorization = authorizationBuilder.build();
            this.authorizationService.save(authorization);
            Map<String, Object> tokenAdditionalParameters = new HashMap<>();
            return new OAuth2AccessTokenAuthenticationToken(registeredClient, clientPrincipal, accessToken, refreshToken, tokenAdditionalParameters);
        } catch (Exception e) {
            OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR);
            if (e instanceof BadCredentialsException) {
                error = new OAuth2Error(e.getMessage());
            }
            throw new OAuth2AuthenticationException(error, e);
        }
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return PhoneAuthenticationToken.class.isAssignableFrom(aClass);
    }

    private OAuth2ClientAuthenticationToken getAuthenticatedClientElseThrowInvalidClient(Authentication authentication) {
        OAuth2ClientAuthenticationToken clientPrincipal = null;
        if (OAuth2ClientAuthenticationToken.class.isAssignableFrom(authentication.getPrincipal().getClass())) {
            clientPrincipal = (OAuth2ClientAuthenticationToken) authentication.getPrincipal();
        }

        if (clientPrincipal != null && clientPrincipal.isAuthenticated()) {
            return clientPrincipal;
        } else {
            throw new OAuth2AuthenticationException(OAuth2ErrorCodes.INVALID_CLIENT);
        }
    }

    private OAuth2RefreshToken generateRefreshToken(Duration tokenTimeToLive) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(tokenTimeToLive);
        return new OAuth2RefreshToken(this.refreshTokenGenerator.get(), issuedAt, expiresAt);
    }
}
