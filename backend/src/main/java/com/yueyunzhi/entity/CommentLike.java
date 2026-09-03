package com.yueyunzhi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comment_like")
@IdClass(CommentLikeId.class)
public class CommentLike {
    
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    @Id
    @Column(name = "comment_id")
    private Long commentId;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
}
