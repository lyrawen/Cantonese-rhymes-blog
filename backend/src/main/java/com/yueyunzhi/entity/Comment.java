package com.yueyunzhi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    
    @Column(name = "article_id", nullable = false)
    private Long articleId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "user_name", length = 100)
    private String userName;
    
    @Column(name = "user_avatar", length = 200)
    private String userAvatar;
    
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(name = "reply_to_user_id")
    private Long replyToUserId;
    
    @Column(name = "reply_to_user_name", length = 100)
    private String replyToUserName;
    
    @Column(name = "status", nullable = false)
    private Byte status = 1;
    
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}