package com.icuxika.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Optional;

@Component(value = "defaultWebSocketHandler")
public class WebSocketHandler extends AbstractWebSocketHandler {

    @Autowired
    private StreamBridge streamBridge;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        WebSocketSessionInfo info = (WebSocketSessionInfo) session.getAttributes().get(WebSocketHandshakeInterceptor.ATTRIBUTE_HEADER_INFO);
        if (info == null) {
            session.close();
            return;
        }
        session.sendMessage(new TextMessage("hi"));
        WebSocketSessionManager.openSession(session, info.getUserId(), info.getClientType());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        streamBridge.send("websocketMessageConsumer-out-0", message.getPayload());
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        // to be considered
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) {
        Optional.ofNullable(WebSocketSessionManager.getManageableWebSocketSessionBySession(session)).ifPresent(ManageableWebSocketSession::onPong);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        WebSocketSessionInfo info = (WebSocketSessionInfo) session.getAttributes().get(WebSocketHandshakeInterceptor.ATTRIBUTE_HEADER_INFO);
        WebSocketSessionManager.closeSession(info.getUserId(), session);
    }
}
