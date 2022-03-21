package com.icuxika.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * 对一个会话进行管理，检测是否在线、网络是否依旧正常等
 */
public class ManageableWebSocketSession {

    private static final Logger L = LoggerFactory.getLogger(ManageableWebSocketSession.class);

    private final WebSocketSession webSocketSession;

    /**
     * 用户ID
     */
    private final Long userId;

    /**
     * 设备类型
     */
    private final Integer clientType;

    private long pingCount = 0L;

    private long pongCount = 0;

    public ManageableWebSocketSession(WebSocketSession webSocketSession, Long userId, Integer clientType) {
        this.webSocketSession = webSocketSession;
        this.userId = userId;
        this.clientType = clientType;
    }

    /**
     * 向该会话发送消息
     *
     * @param message 消息
     */
    public void sendMessage(String message) {
        try {
            webSocketSession.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendPing() {
        if (!webSocketSession.isOpen()) return false;
        try {
            webSocketSession.sendMessage(new PingMessage());
            pingCount++;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return offline();
    }

    public void onPong() {
        pongCount++;
    }

    /**
     * 可能下线了
     *
     * @return 是否
     */
    public boolean offline() {
        if (L.isTraceEnabled()) {
            L.trace("[" + userId + "]" + "ping: " + pingCount + ", pong: " + pongCount);
        }
        return pingCount - pongCount < 3;
    }

    public WebSocketSession getWebSocketSession() {
        return webSocketSession;
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getClientType() {
        return clientType;
    }
}
