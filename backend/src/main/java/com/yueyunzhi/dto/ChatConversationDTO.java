package com.yueyunzhi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationDTO {
    private Long sessionId;
    private Long peerId;
    private String peerNickname;
    private String peerAvatar;
    private String lastMsg;
    private LocalDateTime lastMsgTime;
    private int unreadCount;
    /** existing_session | new_mutual */
    private String type;
    private String hint;
    private boolean canSend;
}
