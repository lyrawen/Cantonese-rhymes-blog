package com.yueyunzhi.controller;

import com.yueyunzhi.dto.ChatConversationDTO;
import com.yueyunzhi.dto.ChatMessageDTO;
import com.yueyunzhi.entity.User;
import com.yueyunzhi.service.ChatService;
import com.yueyunzhi.service.UserService;
import com.yueyunzhi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private User requireUser(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("请先登录");
        }
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        return userService.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
    }

    @GetMapping("/conversations")
    public ResponseEntity<?> listConversations(@RequestHeader("Authorization") String token) {
        try {
            User user = requireUser(token);
            List<ChatConversationDTO> list = chatService.listConversations(user.getUserId());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/unread-total")
    public ResponseEntity<?> unreadTotal(@RequestHeader("Authorization") String token) {
        try {
            User user = requireUser(token);
            long count = chatService.getTotalUnreadCount(user.getUserId());
            return ResponseEntity.ok(Map.of("unreadTotal", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{peerId}")
    public ResponseEntity<?> chatStatus(
            @RequestHeader("Authorization") String token,
            @PathVariable Long peerId) {
        try {
            User user = requireUser(token);
            return ResponseEntity.ok(chatService.getChatStatus(user.getUserId(), peerId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/messages/{peerId}")
    public ResponseEntity<?> getMessages(
            @RequestHeader("Authorization") String token,
            @PathVariable Long peerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            User user = requireUser(token);
            chatService.markSessionRead(user.getUserId(), peerId);
            Page<ChatMessageDTO> messages = chatService.getMessages(user.getUserId(), peerId, page, size);
            Map<String, Object> body = new HashMap<>();
            body.put("content", messages.getContent());
            body.put("totalPages", messages.getTotalPages());
            body.put("totalElements", messages.getTotalElements());
            body.put("status", chatService.getChatStatus(user.getUserId(), peerId));
            return ResponseEntity.ok(body);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/messages/{peerId}/read")
    public ResponseEntity<?> markRead(
            @RequestHeader("Authorization") String token,
            @PathVariable Long peerId) {
        try {
            User user = requireUser(token);
            chatService.markSessionRead(user.getUserId(), peerId);
            return ResponseEntity.ok(Map.of("message", "已标记为已读"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/conversations/{peerId}")
    public ResponseEntity<?> deleteConversation(
            @RequestHeader("Authorization") String token,
            @PathVariable Long peerId) {
        try {
            User user = requireUser(token);
            chatService.deleteConversationForUser(user.getUserId(), peerId);
            return ResponseEntity.ok(Map.of("message", "会话已从列表中移除"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
