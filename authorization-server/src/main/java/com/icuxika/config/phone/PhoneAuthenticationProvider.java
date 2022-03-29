package com.icuxika.config.phone;

import com.icuxika.config.common.AbstractCommonAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2ClientAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.context.ProviderContextHolder;
import org.springframework.security.oauth2.server.authorization.token.DefaultOAuth2TokenContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.util.CollectionUtils;

import java.security.Principal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PhoneAuthenticationProvider extends AbstractCommonAuthenticationProvider implements AuthenticationProvider {

    public static final String AUTHORIZATION_GRANT_TYPE_PHONE_VALUE = "phone";
    public static final String OAUTH2_PARAMETER_NAME_PHONE = "phone";
    public static final String OAUTH2_PARAMETER_NAME_CODE = "code";

    public PhoneAuthenticationProvider(AuthenticationManager authenticationManager, OAuth2AuthorizationService authorizationService, OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        super(authenticationManager, authorizationService, tokenGenerator);
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

            OAuth2Authorization.Builder authorizationBuilder = OAuth2Authorization.withRegisteredClient(registeredClient)
                    .authorizationGrantType(new AuthorizationGrantType(AUTHORIZATION_GRANT_TYPE_PHONE_VALUE))
                    .principalName(phoneVerificationAuthentication.getName())
                    .attribute(OAuth2Authorization.AUTHORIZED_SCOPE_ATTRIBUTE_NAME, authorizedScopes)
                    .attribute(Principal.class.getName(), phoneVerificationAuthentication);

            DefaultOAuth2TokenContext.Builder tokenContextBuilder = DefaultOAuth2TokenContext.builder()
                    .registeredClient(registeredClient)
                    .principal(phoneVerificationAuthentication)
                    .providerContext(ProviderContextHolder.getProviderContext())
                    .authorizedScopes(authorizedScopes)
                    .authorizationGrantType(new AuthorizationGrantType(AUTHORIZATION_GRANT_TYPE_PHONE_VALUE))
                    .authorizationGrant(phoneAuthenticationToken);

            // ---- Access Token ----
            OAuth2TokenContext tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.ACCESS_TOKEN).build();
            OAuth2Token generatedAccessToken = this.tokenGenerator.generate(tokenContext);

            if (generatedAccessToken == null) {
                OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                        "The token generator failed to generate the access token.", "");
                throw new OAuth2AuthenticationException(error);
            }

            OAuth2AccessToken accessToken = new OAuth2AccessToken(
                    OAuth2AccessToken.TokenType.BEARER,
                    generatedAccessToken.getTokenValue(),
                    generatedAccessToken.getIssuedAt(),
                    generatedAccessToken.getExpiresAt(),
                    tokenContext.getAuthorizedScopes()
            );

            if (generatedAccessToken instanceof ClaimAccessor) {
                authorizationBuilder.token(accessToken, metadata ->
                        metadata.put(OAuth2Authorization.Token.CLAIMS_METADATA_NAME, ((ClaimAccessor) generatedAccessToken).getClaims()));
            } else {
                authorizationBuilder.accessToken(accessToken);
            }

            // ---- Refresh Token ----
            OAuth2RefreshToken refreshToken = null;
            if (registeredClient.getAuthorizationGrantTypes().contains(AuthorizationGrantType.REFRESH_TOKEN)) {
                tokenContext = tokenContextBuilder.tokenType(OAuth2TokenType.REFRESH_TOKEN).build();
                OAuth2Token generatedRefreshToken = this.tokenGenerator.generate(tokenContext);
                if (!(generatedRefreshToken instanceof OAuth2RefreshToken)) {
                    OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR,
                            "The token generator failed to generate the refresh token.", "");
                    throw new OAuth2AuthenticationException(error);
                }
                refreshToken = (OAuth2RefreshToken) generatedRefreshToken;
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

}
