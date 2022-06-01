package com.icuxika.config.handler;

import com.icuxika.async.AsyncWrapper;
import com.icuxika.constant.SystemConstant;
import com.icuxika.modules.user.feign.UserClient;
import com.icuxika.util.IPUtil;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AccessTokenAuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 自定义AccessToken响应输出，主要代码来自 org.springframework.security.oauth2.server.authorization.web.OAuth2TokenEndpointFilter#sendAccessTokenResponse(HttpServletRequest, HttpServletResponse, Authentication)
 */
@Configuration
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserClient userClient;

    @Autowired
    private AsyncWrapper asyncWrapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2AccessTokenAuthenticationToken accessTokenAuthentication =
                (OAuth2AccessTokenAuthenticationToken) authentication;

        OAuth2AccessToken accessToken = accessTokenAuthentication.getAccessToken();

        // 存储用户当前激活AccessToken
        activateUserSession(request, accessToken);

        OAuth2RefreshToken refreshToken = accessTokenAuthentication.getRefreshToken();
        Map<String, Object> additionalParameters = accessTokenAuthentication.getAdditionalParameters();

        OAuth2AccessTokenResponse.Builder builder =
                OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
                        .tokenType(accessToken.getTokenType())
                        .scopes(accessToken.getScopes());
        if (accessToken.getIssuedAt() != null && accessToken.getExpiresAt() != null) {
            builder.expiresIn(ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()));
        }
        if (refreshToken != null) {
            builder.refreshToken(refreshToken.getTokenValue());
        }
        if (!CollectionUtils.isEmpty(additionalParameters)) {
            builder.additionalParameters(additionalParameters);
        }
        OAuth2AccessTokenResponse accessTokenResponse = builder.build();
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        new OAuth2AccessTokenResponseHttpMessageConverter().write(accessTokenResponse, null, httpResponse);
    }

    /**
     * 一次登录产生的AccessToken及由RefreshToken刷新得到的AccessToken组成的集合作为当前用户的激活会话
     * 再次登录会重置此集合
     * Resource Server 根据此集合来验证 AccessToken是否处于激活状态，来决定是否允许同设备多处登录
     * TODO:
     */
    private void activateUserSession(HttpServletRequest request, OAuth2AccessToken accessToken) {
        String grantType = request.getParameter("grant_type");
        boolean isRefreshToken = "refresh_token".equals(grantType);
        try {
            String token = accessToken.getTokenValue();
            JWTClaimsSet jwtClaimsSet = JWTParser.parse(token).getJWTClaimsSet();
            Long userId = (Long) jwtClaimsSet.getClaim(SystemConstant.OAUTH2_JWT_CLAIM_KEY_USER_ID);
            Integer clientType = ((Long) jwtClaimsSet.getClaim(SystemConstant.OAUTH2_JWT_CLAIM_KEY_CLIENT_TYPE)).intValue();
            String key = userId + ":" + clientType;
            @SuppressWarnings("unchecked")
            List<String> userTokenList = (List<String>) redisTemplate.opsForHash().get(SystemConstant.REDIS_OAUTH2_USER_SESSION, key);
            if (userTokenList == null) {
                userTokenList = new ArrayList<>();
            }
            if (!isRefreshToken) {
                userTokenList.clear();
            }
            // TODO RefreshToken时判断当前激活的AccessToken集合大小并清理旧的AccessToken，合理的情况应只存在两个有效的AccessToken
            userTokenList.add(token);
            redisTemplate.opsForHash().put(SystemConstant.REDIS_OAUTH2_USER_SESSION, key, userTokenList);
            // 异步更新用户最近登录ip地址
            String ip = Optional.ofNullable(IPUtil.getIP(request)).orElse("127.0.0.1");
            asyncWrapper.doAsync("更新用户最近登录ip地址", () -> updateUserIP(userId, ip));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新用户最近登录ip地址
     *
     * @param userId 用户id
     * @param ip     ip地址
     */
    private void updateUserIP(Long userId, String ip) {
        // 将ip地址使用Base64格式化，否则无法直接传输
        userClient.updateUserIP(userId, Base64.getEncoder().encodeToString(ip.getBytes(StandardCharsets.UTF_8)));
    }

}
