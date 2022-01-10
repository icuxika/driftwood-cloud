package com.icuxika.config.phone;

import com.icuxika.config.source.OAuth2EndpointUtils;
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

public class PhoneAuthenticationConverter implements AuthenticationConverter {

    public PhoneAuthenticationConverter() {
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        String grantType = request.getParameter(OAuth2ParameterNames.GRANT_TYPE);
        if (!PhoneAuthenticationProvider.AUTHORIZATION_GRANT_TYPE_PHONE_VALUE.equals(grantType)) {
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

            String phone = parameters.getFirst(PhoneAuthenticationProvider.OAUTH2_PARAMETER_NAME_PHONE);
            if (!StringUtils.hasText(phone) || parameters.get(PhoneAuthenticationProvider.OAUTH2_PARAMETER_NAME_PHONE).size() != 1) {
                OAuth2EndpointUtils.throwError(
                        OAuth2ErrorCodes.INVALID_REQUEST,
                        PhoneAuthenticationProvider.OAUTH2_PARAMETER_NAME_PHONE,
                        OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI
                );
            }

            String code = parameters.getFirst(PhoneAuthenticationProvider.OAUTH2_PARAMETER_NAME_CODE);
            if (!StringUtils.hasText(code) || parameters.get(PhoneAuthenticationProvider.OAUTH2_PARAMETER_NAME_CODE).size() != 1) {
                OAuth2EndpointUtils.throwError(
                        OAuth2ErrorCodes.INVALID_REQUEST,
                        PhoneAuthenticationProvider.OAUTH2_PARAMETER_NAME_CODE,
                        OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI
                );
            }

            if (clientPrincipal == null) {
                OAuth2EndpointUtils.throwError(
                        OAuth2ErrorCodes.INVALID_REQUEST,
                        PhoneAuthenticationProvider.AUTHORIZATION_GRANT_TYPE_PHONE_VALUE,
                        OAuth2EndpointUtils.ACCESS_TOKEN_REQUEST_ERROR_URI
                );
            }

            Map<String, Object> additionalParameters = parameters
                    .entrySet().stream()
                    .filter(e -> !e.getKey().equals(OAuth2ParameterNames.GRANT_TYPE) && !e.getKey().equals(OAuth2ParameterNames.SCOPE))
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));

            return new PhoneAuthenticationToken(new AuthorizationGrantType(PhoneAuthenticationProvider.AUTHORIZATION_GRANT_TYPE_PHONE_VALUE), clientPrincipal, requestScopes, additionalParameters);
        }
    }
}
