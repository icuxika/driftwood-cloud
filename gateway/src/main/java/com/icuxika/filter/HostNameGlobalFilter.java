package com.icuxika.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class HostNameGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("进入过滤器");
        System.out.println("HostName: " + exchange.getRequest().getRemoteAddress().getHostName());
        System.out.println("URI：" + exchange.getRequest().getURI());
        Mono<Void> result = chain.filter(exchange);
        return result;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
