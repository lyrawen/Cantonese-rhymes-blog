package com.yueyunzhi.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yueyunzhi.dto.ChatMessageDTO;
import com.yueyunzhi.dto.WsChatPayload;
import com.yueyunzhi.entity.User;
import com.yueyunzhi.service.ChatService;
import com.yueyunzhi.service.TokenBlacklistService;
import com.yueyunzhi.service.UserService;
import com.yueyunzhi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatSessionRegistry chatSessionRegistry;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private ObjectMapper objectMapper;

    public static final String ATTR_USER_ID = "userId";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (!authenticate(session)) {
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }
        Long userId = (Long) session.getAttributes().get(ATTR_USER_ID);
        chatSessionRegistry.register(userId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get(ATTR_USER_ID);
        if (userId != null) {
            chatSessionRegistry.unregister(userId);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long senderId = (Long) session.getAttributes().get(ATTR_USER_ID);
        if (senderId == null) {
            return;
        }

        WsChatPayload incoming;
        try {
            incoming = objectMapper.readValue(message.getPayload(), WsChatPayload.class);
        } catch (Exception e) {
            sendError(session, "消息格式错误");
            return;
        }

        if (!"send".equals(incoming.getType())) {
            sendError(session, "不支持的消息类型");
            return;
        }

        if (incoming.getPeerId() == null || incoming.getContent() == null) {
            sendError(session, "缺少 peerId 或 content");
            return;
        }

        try {
            ChatMessageDTO saved = chatService.sendMessage(senderId, incoming.getPeerId(), incoming.getContent());

            WsChatPayload ack = new WsChatPayload();
            ack.setType("sent");
            ack.setMessage(saved);
            ack.setPeerId(incoming.getPeerId());
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(ack)));

            WebSocketSession peerSession = chatSessionRegistry.getSession(incoming.getPeerId());
            if (peerSession != null && peerSession.isOpen()) {
                WsChatPayload push = new WsChatPayload();
                push.setType("receive");
                push.setMessage(saved);
                push.setPeerId(senderId);
                peerSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(push)));
            }
        } catch (IllegalStateException e) {
            WsChatPayload err = new WsChatPayload();
            err.setType("error");
            err.setError(e.getMessage());
            err.setCanSend(false);
            err.setPeerId(incoming.getPeerId());
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(err)));
        } catch (Exception e) {
            sendError(session, e.getMessage());
        }
    }

    private void sendError(WebSocketSession session, String error) throws Exception {
        WsChatPayload payload = new WsChatPayload();
        payload.setType("error");
        payload.setError(error);
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(payload)));
    }

    public boolean authenticate(WebSocketSession session) {
        try {
            URI uri = session.getUri();
            if (uri == null || uri.getQuery() == null) {
                return false;
            }
            String token = null;
            for (String part : uri.getQuery().split("&")) {
                if (part.startsWith("token=")) {
                    token = URLDecoder.decode(part.substring(6), StandardCharsets.UTF_8);
                    break;
                }
            }
            if (token == null || token.isBlank()) {
                return false;
            }
            if (tokenBlacklistService.isBlacklisted(token) || !jwtUtil.isTokenValid(token)) {
                return false;
            }
            String username = jwtUtil.extractUsername(token);
            User user = userService.findByUsername(username).orElse(null);
            if (user == null || user.getStatus() == null || user.getStatus() != 1) {
                return false;
            }
            session.getAttributes().put(ATTR_USER_ID, user.getUserId());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
