package com.icuxika.config.common;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;

/**
 * 考虑是否需要抽离一些公共方法（等待Spring Authorization Server相关类代码稳定不再变更后）
 */
public abstract class AbstractCommonAuthenticationProvider implements CommonProviderService {

    protected final AuthenticationManager authenticationManager;
    protected final OAuth2AuthorizationService authorizationService;
    protected final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    protected AbstractCommonAuthenticationProvider(AuthenticationManager authenticationManager, OAuth2AuthorizationService authorizationService, OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator) {
        this.authenticationManager = authenticationManager;
        this.authorizationService = authorizationService;
        this.tokenGenerator = tokenGenerator;
    }

}
