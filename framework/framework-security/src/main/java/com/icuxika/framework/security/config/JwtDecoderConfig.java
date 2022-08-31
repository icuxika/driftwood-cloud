package com.icuxika.framework.security.config;

import com.icuxika.framework.basic.constant.SystemConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;

import java.time.Duration;
import java.util.List;

@Configuration
public class JwtDecoderConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    /**
     * 重新登录时使上次登录产生一系列AccessToken（以及由RefreshToken刷新产生）失效
     */
    @Value("${abandon-last-token:true}")
    private Boolean abandonLastToken;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final Logger L = LoggerFactory.getLogger(JwtDecoderConfig.class);

    @Bean
    JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuerUri);

        OAuth2TokenValidator<Jwt> withClockSkew = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(Duration.ofSeconds(60)),
                new JwtIssuerValidator(issuerUri),
                new SessionValidator()
        );

        jwtDecoder.setJwtValidator(withClockSkew);

        return jwtDecoder;
    }

    /**
     * 为实现同一用户再次登录时，使上一次进行登录操作时产生的AccessToken（包括使用RefreshToken刷新得到的新token）失效
     * 说明：
     * 1、对于一次登录操作产生AccessToken以及使用RefreshToken刷新得到的token，两个AccessToken都属于有效的凭证，但应该有合适的过期时间设置
     * 2、第二次登录操作后，上一次登录操作产生的token应该被标记为禁用
     * 3、不同设备之间不影响
     * <p>
     * [REDIS_OAUTH2_USER_SESSION]:
     * userId:clientType -> AccessToken / [AccessToken]
     */
    private class SessionValidator implements OAuth2TokenValidator<Jwt> {

        OAuth2Error error = new OAuth2Error("login_expired", "Login expired", null);

        @Override
        public OAuth2TokenValidatorResult validate(Jwt token) {
            if (!abandonLastToken) {
                if (L.isDebugEnabled()) {
                    L.info("用户会话当前支持多处登录");
                }
                return OAuth2TokenValidatorResult.success();
            }

            String currentToken = token.getTokenValue();
            Long userId = token.getClaim(SystemConstant.OAUTH2_JWT_CLAIM_KEY_USER_ID);
            Integer clientType = ((Long) token.getClaim(SystemConstant.OAUTH2_JWT_CLAIM_KEY_CLIENT_TYPE)).intValue();
            String key = userId + ":" + clientType;
            @SuppressWarnings("unchecked")
            List<String> activeTokenList = (List<String>) redisTemplate.opsForHash().get(SystemConstant.REDIS_OAUTH2_USER_SESSION, key);
            // 判断当前token是否处于激活列表
            if (activeTokenList == null || !activeTokenList.contains(currentToken)) {
                return OAuth2TokenValidatorResult.failure(error);
            }
            return OAuth2TokenValidatorResult.success();
        }
    }
}
