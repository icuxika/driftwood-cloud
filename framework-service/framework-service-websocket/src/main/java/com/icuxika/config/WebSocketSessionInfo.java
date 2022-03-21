package com.icuxika.config;

public class WebSocketSessionInfo {

    private Long userId;

    private Integer clientType;

    public WebSocketSessionInfo(Long userId, Integer clientType) {
        this.userId = userId;
        this.clientType = clientType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getClientType() {
        return clientType;
    }

    public void setClientType(Integer clientType) {
        this.clientType = clientType;
    }
}
