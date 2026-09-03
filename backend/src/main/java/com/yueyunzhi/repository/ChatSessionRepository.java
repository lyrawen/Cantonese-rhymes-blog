package com.yueyunzhi.repository;

import com.yueyunzhi.entity.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

    Optional<ChatSession> findByUserAAndUserB(Long userA, Long userB);

    @Query("SELECT s FROM ChatSession s WHERE (s.userA = :userId OR s.userB = :userId) " +
           "AND ((s.userA = :userId AND s.hiddenForA = false) OR (s.userB = :userId AND s.hiddenForB = false)) " +
           "ORDER BY COALESCE(s.lastMsgTime, s.createTime) DESC")
    List<ChatSession> findVisibleSessionsForUser(@Param("userId") Long userId);
}
