package com.yueyunzhi.entity;

import lombok.Data;
import java.io.Serializable;

@Data
public class CommentLikeId implements Serializable {
    
    private Long userId;
    private Long commentId;
    
    public CommentLikeId() {}
    
    public CommentLikeId(Long userId, Long commentId) {
        this.userId = userId;
        this.commentId = commentId;
    }
}
