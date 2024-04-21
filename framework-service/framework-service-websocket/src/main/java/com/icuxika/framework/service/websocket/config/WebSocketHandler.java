package com.icuxika.framework.service.websocket.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.util.Optional;

@Component(value = "defaultWebSocketHandler")
@RequiredArgsConstructor
@Slf4j
public class WebSocketHandler extends AbstractWebSocketHandler {

    private final StreamBridge streamBridge;

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
        WebSocketSessionInfo info = (WebSocketSessionInfo) session.getAttributes().get(WebSocketHandshakeInterceptor.ATTRIBUTE_HEADER_INFO);
        Message<SimpleMessage> msg = new GenericMessage<>(new SimpleMessage(info.getUserId(), info.getClientType(), message.getPayload()));
        streamBridge.send("websocketMessageConsumer-out-0", msg);
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
