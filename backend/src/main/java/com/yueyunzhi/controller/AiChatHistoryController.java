package com.yueyunzhi.controller;

import com.yueyunzhi.entity.AiChatHistory;
import com.yueyunzhi.service.AiChatHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai-chat")
public class AiChatHistoryController {
    
    @Autowired
    private AiChatHistoryService aiChatHistoryService;
    
    @GetMapping("/history/{userId}")
    public ResponseEntity<List<AiChatHistory>> getChatHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(aiChatHistoryService.getChatHistoryByUserId(userId));
    }
    
    @PostMapping
    public ResponseEntity<AiChatHistory> saveChatHistory(@RequestBody AiChatHistory chatHistory) {
        AiChatHistory saved = aiChatHistoryService.saveChatHistory(
            chatHistory.getUserId(),
            chatHistory.getQuestion(),
            chatHistory.getAnswer()
        );
        return ResponseEntity.ok(saved);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChatHistory(@PathVariable Long id) {
        aiChatHistoryService.deleteChatHistory(id);
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteAllChatHistory(@PathVariable Long userId) {
        aiChatHistoryService.deleteAllChatHistoryByUserId(userId);
        return ResponseEntity.ok().build();
    }
}