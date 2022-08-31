package com.icuxika.authorization.config.password;

import com.icuxika.authorization.config.source.OAuth2EndpointUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PasswordAuthenticationConverter implements AuthenticationConverter {

    public PasswordAuthenticationConverter() {
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!AuthorizationGrantType.PASSWORD.getValue().equals(grantType)) {
            return null;
        } else {
            Authentication clientPrincipal = SecurityContextHolder.getContext().getAuthentication();
            MultiValueMap<String, String> parameters = OAuth2EndpointUtils.getParameters(request);
            String scope = parameters.getFirst(OAuth2ParameterNames.SCOPE);
            if (StringUtils.hasText(scope) && parameters.get(OAuth2ParameterNames.SCOPE).size() != 1) {
                OAuth2EndpointUtils.throwError(
                        OAuth2ErrorCodes.INVALID_REQUEST,
                        OAuth2ParameterNames.SCOPE,
                        OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI
                );
            }

            Set<String> requestScopes = null;
            if (StringUtils.hasText(scope)) {
                requestScopes = new HashSet<>(Arrays.asList(StringUtils.delimitedListToStringArray(scope, " ")));
            }

            String username = parameters.getFirst(OAuth2ParameterNames.USERNAME);
            if (!StringUtils.hasText(username) || parameters.get(OAuth2ParameterNames.USERNAME).size() != 1) {
                OAuth2EndpointUtils.throwError(
                        OAuth2ErrorCodes.INVALID_REQUEST,
                        OAuth2ParameterNames.USERNAME,
                        OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI
                );
            }

            String password = parameters.getFirst(OAuth2ParameterNames.PASSWORD);
            if (!StringUtils.hasText(password) || parameters.get(OAuth2ParameterNames.PASSWORD).size() != 1) {
                OAuth2EndpointUtils.throwError(
                        OAuth2ErrorCodes.INVALID_REQUEST,
                        OAuth2ParameterNames.PASSWORD,
                        OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI
                );
            }

            if (clientPrincipal == null) {
                OAuth2EndpointUtils.throwError(
                        OAuth2ErrorCodes.INVALID_REQUEST,
                        OAuth2ErrorCodes.INVALID_CLIENT,
                        OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI
                );
            }

            Map<String, Object> additionalParameters = parameters
                    .entrySet().stream()
                    .filter(e -> !e.getKey().equals(OAuth2ParameterNames.GRANT_TYPE) && !e.getKey().equals(OAuth2ParameterNames.SCOPE))
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

            return new PasswordAuthenticationToken(AuthorizationGrantType.PASSWORD, clientPrincipal, requestScopes, additionalParameters);
        }
    }
}
