package com.yueyunzhi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "article_like")
@IdClass(ArticleLikeId.class)
public class ArticleLike {
    
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    @Id
    @Column(name = "article_id")
    private Long articleId;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    public ArticleLike() {
        this.createTime = LocalDateTime.now();
    }
    
    public ArticleLike(Long userId, Long articleId) {
        this.userId = userId;
        this.articleId = articleId;
        this.createTime = LocalDateTime.now();
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getArticleId() {
        return articleId;
    }
    
    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
    
    public LocalDateTime getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}