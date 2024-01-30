package com.icuxika.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class SCGConfiguration {

    @Bean
    public BlockRequestHandler blockRequestHandler() {
        return (serverWebExchange, throwable) ->
                ServerResponse.status(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(BodyInserters.fromValue("""
                                {"code":20000,"data":null,"msg":"SCS Sentinel block","success":false}
                                """));
    }

}
