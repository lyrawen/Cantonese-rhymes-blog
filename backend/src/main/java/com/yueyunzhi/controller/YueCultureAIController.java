package com.yueyunzhi.controller;

import com.yueyunzhi.service.YueCultureAIService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/yue-culture")
public class YueCultureAIController {
    
    @Autowired
    private YueCultureAIService aiService;
    
    @PostMapping("/ask")
    public ResponseEntity<YueCultureAIService.QAResponse> ask(
            @RequestBody QuestionDTO question) {
        
        YueCultureAIService.QAResponse response = aiService.askQuestion(
            question.getText(), 
            question.getCategory()
        );
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/chat/stream")
    public ResponseEntity<String> chatStream(@RequestBody QuestionDTO question) {
        // 流式输出实现打字机效果
        String response = aiService.askQuestionStream(question.getText());
        return ResponseEntity.ok(response);
    }
    
    @Data
    public static class QuestionDTO {
        private String text;
        private String category; // 可选，用户可指定领域
    }
}