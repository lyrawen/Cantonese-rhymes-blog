package com.yueyunzhi.service;

import com.yueyunzhi.dto.ChatConversationDTO;
import com.yueyunzhi.dto.ChatMessageDTO;
import com.yueyunzhi.entity.ChatMessage;
import com.yueyunzhi.entity.ChatSession;
import com.yueyunzhi.entity.User;
import com.yueyunzhi.repository.ChatMessageRepository;
import com.yueyunzhi.repository.ChatSessionRepository;
import com.yueyunzhi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final String NEW_MUTUAL_HINT = "你们已经相互关注啦，一起来交流粤文化吧";

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserFollowService userFollowService;

    @Autowired
    private UserRepository userRepository;

    public long[] normalizePair(Long userId1, Long userId2) {
        long a = Math.min(userId1, userId2);
        long b = Math.max(userId1, userId2);
        return new long[]{a, b};
    }

    public Optional<ChatSession> findSession(Long userId1, Long userId2) {
        long[] pair = normalizePair(userId1, userId2);
        return chatSessionRepository.findByUserAAndUserB(pair[0], pair[1]);
    }

    @Transactional
    public ChatSession getOrCreateSession(Long userId1, Long userId2) {
        return findSession(userId1, userId2).orElseGet(() -> {
            long[] pair = normalizePair(userId1, userId2);
            ChatSession session = new ChatSession();
            session.setUserA(pair[0]);
            session.setUserB(pair[1]);
            session.setHiddenForA(false);
            session.setHiddenForB(false);
            return chatSessionRepository.save(session);
        });
    }

    public Long getPeerId(ChatSession session, Long currentUserId) {
        return session.getUserA().equals(currentUserId) ? session.getUserB() : session.getUserA();
    }

    public boolean isHiddenForUser(ChatSession session, Long userId) {
        if (session.getUserA().equals(userId)) {
            return Boolean.TRUE.equals(session.getHiddenForA());
        }
        if (session.getUserB().equals(userId)) {
            return Boolean.TRUE.equals(session.getHiddenForB());
        }
        return true;
    }

    @Transactional
    public void unhideSessionForUser(ChatSession session, Long userId) {
        if (session.getUserA().equals(userId)) {
            session.setHiddenForA(false);
        } else if (session.getUserB().equals(userId)) {
            session.setHiddenForB(false);
        }
        chatSessionRepository.save(session);
    }

    public List<ChatConversationDTO> listConversations(Long currentUserId) {
        List<Long> mutualIds = userFollowService.findMutualFriendIds(currentUserId);
        Map<Long, User> userMap = loadUsers(mutualIds);

        List<ChatSession> sessions = chatSessionRepository.findVisibleSessionsForUser(currentUserId);
        Set<Long> sessionPeerIds = new HashSet<>();
        List<ChatConversationDTO> result = new ArrayList<>();

        for (ChatSession session : sessions) {
            Long peerId = getPeerId(session, currentUserId);
            sessionPeerIds.add(peerId);
            User peer = userMap.get(peerId);
            if (peer == null) {
                peer = userRepository.findById(peerId).orElse(null);
            }
            if (peer == null) {
                continue;
            }
            boolean mutual = userFollowService.isMutualFollow(currentUserId, peerId);
            int unread = (int) chatMessageRepository.countUnreadInSession(session.getSessionId(), currentUserId);
            result.add(buildConversation(session, peer, unread, "existing_session", null, mutual));
        }

        for (Long peerId : mutualIds) {
            if (sessionPeerIds.contains(peerId)) {
                continue;
            }
            User peer = userMap.get(peerId);
            if (peer == null) {
                continue;
            }
            ChatConversationDTO dto = new ChatConversationDTO();
            dto.setSessionId(null);
            dto.setPeerId(peerId);
            dto.setPeerNickname(peer.getNickname() != null ? peer.getNickname() : peer.getUsername());
            dto.setPeerAvatar(peer.getAvatar());
            dto.setLastMsg(null);
            dto.setLastMsgTime(null);
            dto.setUnreadCount(0);
            dto.setType("new_mutual");
            dto.setHint(NEW_MUTUAL_HINT);
            dto.setCanSend(true);
            result.add(dto);
        }

        result.sort((a, b) -> {
            if ("new_mutual".equals(a.getType()) && !"new_mutual".equals(b.getType())) {
                return 1;
            }
            if (!"new_mutual".equals(a.getType()) && "new_mutual".equals(b.getType())) {
                return -1;
            }
            LocalDateTime ta = a.getLastMsgTime();
            LocalDateTime tb = b.getLastMsgTime();
            if (ta == null && tb == null) {
                return 0;
            }
            if (ta == null) {
                return 1;
            }
            if (tb == null) {
                return -1;
            }
            return tb.compareTo(ta);
        });

        return result;
    }

    private ChatConversationDTO buildConversation(ChatSession session, User peer, int unread,
                                                  String type, String hint, boolean canSend) {
        ChatConversationDTO dto = new ChatConversationDTO();
        dto.setSessionId(session.getSessionId());
        dto.setPeerId(peer.getUserId());
        dto.setPeerNickname(peer.getNickname() != null ? peer.getNickname() : peer.getUsername());
        dto.setPeerAvatar(peer.getAvatar());
        dto.setLastMsg(session.getLastMsg());
        dto.setLastMsgTime(session.getLastMsgTime());
        dto.setUnreadCount(unread);
        dto.setType(type);
        dto.setHint(hint);
        dto.setCanSend(canSend);
        return dto;
    }

    private Map<Long, User> loadUsers(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        return userRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(User::getUserId, u -> u));
    }

    public long getTotalUnreadCount(Long userId) {
        return chatMessageRepository.countTotalUnreadForUser(userId);
    }

    public Page<ChatMessageDTO> getMessages(Long currentUserId, Long peerId, int page, int size) {
        if (currentUserId.equals(peerId)) {
            throw new IllegalArgumentException("不能与自己聊天");
        }
        Optional<ChatSession> sessionOpt = findSession(currentUserId, peerId);
        if (sessionOpt.isEmpty()) {
            return Page.empty();
        }
        ChatSession session = sessionOpt.get();
        if (isHiddenForUser(session, currentUserId)) {
            unhideSessionForUser(session, currentUserId);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessage> messages = chatMessageRepository.findBySessionIdOrderByCreateTimeAsc(
                session.getSessionId(), pageable);

        return messages.map(m -> toMessageDto(m, currentUserId));
    }

    @Transactional
    public void markSessionRead(Long currentUserId, Long peerId) {
        findSession(currentUserId, peerId).ifPresent(session ->
                chatMessageRepository.markSessionReadForUser(session.getSessionId(), currentUserId));
    }

    @Transactional
    public ChatMessageDTO sendMessage(Long senderId, Long peerId, String content) {
        if (senderId.equals(peerId)) {
            throw new IllegalArgumentException("不能给自己发私信");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("消息内容不能为空");
        }
        if (content.length() > 1000) {
            throw new IllegalArgumentException("消息内容不能超过1000字");
        }
        if (!userFollowService.isMutualFollow(senderId, peerId)) {
            throw new IllegalStateException("仅互相关注的用户可以发送私信");
        }

        ChatSession session = getOrCreateSession(senderId, peerId);
        unhideSessionForUser(session, senderId);
        unhideSessionForUser(session, peerId);

        ChatMessage message = new ChatMessage();
        message.setSessionId(session.getSessionId());
        message.setSenderId(senderId);
        message.setContent(content.trim());
        message.setIsRead(false);
        message.setCreateTime(LocalDateTime.now());
        message = chatMessageRepository.save(message);

        String preview = content.trim();
        if (preview.length() > 100) {
            preview = preview.substring(0, 100);
        }
        session.setLastMsg(preview);
        session.setLastMsgTime(message.getCreateTime());
        chatSessionRepository.save(session);

        return toMessageDto(message, senderId);
    }

    @Transactional
    public void deleteConversationForUser(Long currentUserId, Long peerId) {
        Optional<ChatSession> sessionOpt = findSession(currentUserId, peerId);
        if (sessionOpt.isEmpty()) {
            return;
        }
        ChatSession session = sessionOpt.get();
        if (session.getUserA().equals(currentUserId)) {
            session.setHiddenForA(true);
        } else {
            session.setHiddenForB(true);
        }
        chatSessionRepository.save(session);
    }

    public Map<String, Object> getChatStatus(Long currentUserId, Long peerId) {
        Map<String, Object> status = new HashMap<>();
        if (currentUserId.equals(peerId)) {
            status.put("canSend", false);
            status.put("mutualFollow", false);
            status.put("reason", "不能给自己发私信");
            return status;
        }
        boolean mutual = userFollowService.isMutualFollow(currentUserId, peerId);
        status.put("mutualFollow", mutual);
        status.put("canSend", mutual);
        if (!mutual) {
            Optional<ChatSession> session = findSession(currentUserId, peerId);
            status.put("hasHistory", session.isPresent());
            status.put("reason", "需要双方互相关注才能发送新消息");
        }
        return status;
    }

    private ChatMessageDTO toMessageDto(ChatMessage message, Long currentUserId) {
        ChatMessageDTO dto = new ChatMessageDTO();
        dto.setMessageId(message.getMessageId());
        dto.setSessionId(message.getSessionId());
        dto.setSenderId(message.getSenderId());
        dto.setContent(message.getContent());
        dto.setIsRead(message.getIsRead());
        dto.setCreateTime(message.getCreateTime());
        dto.setMine(message.getSenderId().equals(currentUserId));
        return dto;
    }
}
