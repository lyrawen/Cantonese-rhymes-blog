package com.yueyunzhi.repository;

import com.yueyunzhi.entity.AiChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AiChatHistoryRepository extends JpaRepository<AiChatHistory, Long> {
    
    List<AiChatHistory> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    void deleteByUserId(Long userId);
}