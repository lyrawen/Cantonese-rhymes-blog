package com.yueyunzhi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_session")
public class ChatSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;

    @Column(name = "user_a", nullable = false)
    private Long userA;

    @Column(name = "user_b", nullable = false)
    private Long userB;

    @Column(name = "last_msg", length = 100)
    private String lastMsg;

    @Column(name = "last_msg_time")
    private LocalDateTime lastMsgTime;

    @Column(name = "hidden_for_a", nullable = false)
    private Boolean hiddenForA = false;

    @Column(name = "hidden_for_b", nullable = false)
    private Boolean hiddenForB = false;

    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
}
