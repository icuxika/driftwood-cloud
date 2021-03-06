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

    public static final String ATTRIBUTE_HEADER_INFO = "websocket-session-info";

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String bearToken = request.getHeaders().getOrDefault(HttpHeaders.AUTHORIZATION, Collections.singletonList("")).get(0);
        if (!StringUtils.hasText(bearToken)) {
            return false;
        }
        L.info("Bear Token: " + bearToken);
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest httpServletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            L.info("token: " + httpServletRequest.getParameter(SystemConstant.WEBSOCKET_QUERY_PARAMS_KEY));
            L.info("Current User Id: " + SecurityUtil.getUserId());
            L.info("Current Client Type: " + SecurityUtil.getClientType());
            attributes.put(ATTRIBUTE_HEADER_INFO, new WebSocketSessionInfo(SecurityUtil.getUserId(), SecurityUtil.getClientType()));
            return true;
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        if (exception != null) {
            exception.printStackTrace();
            L.error(exception.getMessage());
        }
    }
}
