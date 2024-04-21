package com.icuxika.framework.service.websocket.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

/**
 * 对一个会话进行管理，检测是否在线、网络是否依旧正常等
 */
@Slf4j
public class ManageableWebSocketSession {

    @Getter
    private final WebSocketSession webSocketSession;

    /**
     * 用户ID
     */
    @Getter
    private final Long userId;

    /**
     * 设备类型
     */
    @Getter
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
            log.error("向[{}]发送消息时遇到错误", userId, e);
        }
    }

    public boolean sendPing() {
        if (!webSocketSession.isOpen()) return false;
        try {
            webSocketSession.sendMessage(new PingMessage());
            pingCount++;
        } catch (IOException e) {
            log.error("向[{}]发送ping时遇到错误", userId, e);
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
        if (log.isTraceEnabled()) {
            log.trace("[{}]ping: {}, pong: {}", userId, pingCount, pongCount);
        }
        return pingCount - pongCount < 3;
    }

}
