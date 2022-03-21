package com.icuxika.config;

import com.icuxika.constant.SystemConstant;
import com.icuxika.util.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger L = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);

    /**
     * 表示当前客户端类型
     */
    private static final String QUERY_KEY_CLIENT_TYPE = "clientType";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String bearToken = request.getHeaders().getOrDefault(HttpHeaders.AUTHORIZATION, Collections.singletonList("")).get(0);
        if (!StringUtils.hasText(bearToken)) {
            return false;
        }
        L.info("Bear Token: " + bearToken);
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            L.info("token: " + httpServletRequest.getParameter(SystemConstant.WEBSOCKET_QUERY_PARAMS_KEY));
            String clientType = httpServletRequest.getParameter(QUERY_KEY_CLIENT_TYPE);
            L.info("Current User Id: " + SecurityUtil.getUserId());
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }
}
