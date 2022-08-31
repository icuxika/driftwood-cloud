package com.icuxika.gateway.factory;

import com.icuxika.framework.basic.constant.SystemConstant;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;

/**
 * 通过WebSocket请求参数来构造用于OAuth2 Resource Server的Bear Token验证
 */
@Component
public class WebSocketGatewayFilterFactory extends AbstractGatewayFilterFactory<AbstractGatewayFilterFactory.NameConfig> {

    public WebSocketGatewayFilterFactory() {
        super(NameConfig.class);
    }

    @Override
    public GatewayFilter apply(NameConfig config) {
        return (exchange, chain) -> {
            String token = exchange.getRequest().getQueryParams().getOrDefault(SystemConstant.WEBSOCKET_QUERY_PARAMS_KEY, Collections.singletonList("")).get(0);
            if (!StringUtils.hasText(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            ServerHttpRequest request = exchange.getRequest().mutate()
                    .headers(httpHeaders -> httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + token)).build();
            return chain.filter(exchange.mutate().request(request).build());
        };
    }
}
