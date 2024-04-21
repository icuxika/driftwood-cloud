package com.icuxika.framework.service.websocket.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WebSocketSessionInfo {

    private Long userId;

    private Integer clientType;

    public WebSocketSessionInfo(Long userId, Integer clientType) {
        this.userId = userId;
        this.clientType = clientType;
    }

}
