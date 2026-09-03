package com.yueyunzhi.dto;

import lombok.Data;

@Data
public class WsChatPayload {
    private String type;
    private Long peerId;
    private String content;
    private ChatMessageDTO message;
    private String error;
    private Boolean canSend;
}
