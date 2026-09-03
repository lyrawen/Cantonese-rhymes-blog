package com.yueyunzhi.repository;

import com.yueyunzhi.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    Page<ChatMessage> findBySessionIdOrderByCreateTimeAsc(Long sessionId, Pageable pageable);

    @Query("SELECT COUNT(m) FROM ChatMessage m JOIN ChatSession s ON m.sessionId = s.sessionId " +
           "WHERE m.senderId <> :userId AND m.isRead = false " +
           "AND ((s.userA = :userId AND s.hiddenForA = false) OR (s.userB = :userId AND s.hiddenForB = false))")
    long countTotalUnreadForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM ChatMessage m WHERE m.sessionId = :sessionId " +
           "AND m.senderId <> :userId AND m.isRead = false")
    long countUnreadInSession(@Param("sessionId") Long sessionId, @Param("userId") Long userId);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.isRead = true WHERE m.sessionId = :sessionId " +
           "AND m.senderId <> :userId AND m.isRead = false")
    int markSessionReadForUser(@Param("sessionId") Long sessionId, @Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM ChatMessage m WHERE m.sessionId = :sessionId")
    void deleteBySessionId(@Param("sessionId") Long sessionId);
}
