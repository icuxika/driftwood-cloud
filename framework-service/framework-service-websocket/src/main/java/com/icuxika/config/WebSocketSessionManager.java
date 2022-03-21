package com.icuxika.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessionManager {

    private static final Logger L = LoggerFactory.getLogger(WebSocketSessionManager.class);

    /**
     * 用户ID <-> 多个设备登录的会话信息
     */
    private static final Map<Long, List<ManageableWebSocketSession>> USER_SESSION_LIST_MAP = new ConcurrentHashMap<>();

    /**
     * 会话id <-> 用户ID
     */
    private static final Map<String, ManageableWebSocketSession> SESSION_ID_MAP = new ConcurrentHashMap<>();

    /**
     * 新的用户会话
     *
     * @param userId           用户ID
     * @param webSocketSession 会话数据
     */
    public static void openSession(WebSocketSession webSocketSession, long userId, int clientType) {
        // 判断当前是否记录了该用户的会话数据
        List<ManageableWebSocketSession> userSessionList = USER_SESSION_LIST_MAP.computeIfAbsent(userId, k -> new ArrayList<>());
        ManageableWebSocketSession manageableWebSocketSession = new ManageableWebSocketSession(webSocketSession, userId, clientType);
        SESSION_ID_MAP.put(webSocketSession.getId(), manageableWebSocketSession);
        userSessionList.add(manageableWebSocketSession);
        L.info(userId + "[" + clientType + "]" + "上线了");
        // TODO 同类型设备登录同账号是否踢掉、向自己的某个设备发送消息
    }

    /**
     * 关闭用户会话
     *
     * @param userId           用户ID
     * @param webSocketSession 被关闭的会话
     */
    public static void closeSession(long userId, WebSocketSession webSocketSession) {
        List<ManageableWebSocketSession> userSessionList = USER_SESSION_LIST_MAP.get(userId);
        if (userSessionList != null) {
            ManageableWebSocketSession manageableWebSocketSession = SESSION_ID_MAP.get(webSocketSession.getId());
            if (manageableWebSocketSession != null) {
                userSessionList.remove(manageableWebSocketSession);
                SESSION_ID_MAP.remove(webSocketSession.getId());
                L.info(userId + "下线了");
            }
        }
    }

    /**
     * 获取当前的会话集合
     *
     * @return 会话集合
     */
    public static List<ManageableWebSocketSession> getCurrentWebSocketSessionList() {
        List<ManageableWebSocketSession> result = new ArrayList<>();
        USER_SESSION_LIST_MAP.values().forEach(result::addAll);
        return result;
    }

    /**
     * 根据会话信息获取响应会话
     *
     * @param session 会话
     * @return 会话数据
     */
    public static ManageableWebSocketSession getManageableWebSocketSessionBySession(WebSocketSession session) {
        return SESSION_ID_MAP.get(session.getId());
    }

    /**
     * 向指定用户发送消息
     *
     * @param userId  用户ID
     * @param message 消息
     */
    public static void sendMessageToUser(long userId, String message) {
        List<ManageableWebSocketSession> userSessionList = USER_SESSION_LIST_MAP.get(userId);
        if (userSessionList != null && !userSessionList.isEmpty()) {
            userSessionList.forEach(manageableWebSocketSession -> manageableWebSocketSession.sendMessage(message));
        }
    }
}
