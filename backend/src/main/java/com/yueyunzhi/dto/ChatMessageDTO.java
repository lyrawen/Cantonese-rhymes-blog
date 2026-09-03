package com.yueyunzhi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {
    private Long messageId;
    private Long sessionId;
    private Long senderId;
    private String content;
    private Boolean isRead;
    private LocalDateTime createTime;
    private boolean mine;
}
