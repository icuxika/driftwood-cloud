package com.icuxika.config.common;

import org.springframework.security.authentication.*;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
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

    /**
     * 更明确的登录异常信息
     *
     * @param exception 登录异常
     * @return OAuth2AuthenticationException
     */
    protected OAuth2AuthenticationException buildOAuth2AuthenticationExceptionFromException(Exception exception) {
        OAuth2Error error = new OAuth2Error(OAuth2ErrorCodes.SERVER_ERROR);
        if (exception instanceof BadCredentialsException) {
            error = new OAuth2Error("[账户验证错误]" + exception.getMessage());
        }
        if (exception instanceof LockedException
                || exception instanceof DisabledException
                || exception instanceof AccountExpiredException
                || exception instanceof CredentialsExpiredException) {
            error = new OAuth2Error("[账户状态异常]" + exception.getMessage());
        }
        return new OAuth2AuthenticationException(error, exception);
    }
}
