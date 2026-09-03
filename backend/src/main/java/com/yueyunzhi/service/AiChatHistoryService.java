package com.yueyunzhi.service;

import com.yueyunzhi.entity.AiChatHistory;
import com.yueyunzhi.repository.AiChatHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AiChatHistoryService {
    
    @Autowired
    private AiChatHistoryRepository aiChatHistoryRepository;
    
    public List<AiChatHistory> getChatHistoryByUserId(Long userId) {
        return aiChatHistoryRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
    
    @Transactional
    public AiChatHistory saveChatHistory(Long userId, String question, String answer) {
        AiChatHistory chatHistory = new AiChatHistory();
        chatHistory.setUserId(userId);
        chatHistory.setQuestion(question);
        chatHistory.setAnswer(answer);
        return aiChatHistoryRepository.save(chatHistory);
    }
    
    @Transactional
    public void deleteChatHistory(Long id) {
        aiChatHistoryRepository.deleteById(id);
    }
    
    @Transactional
    public void deleteAllChatHistoryByUserId(Long userId) {
        aiChatHistoryRepository.deleteByUserId(userId);
    }
}