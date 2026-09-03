package com.yueyunzhi.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "article_favorite")
@IdClass(ArticleFavoriteId.class)
public class ArticleFavorite {
    
    @Id
    @Column(name = "user_id")
    private Long userId;
    
    @Id
    @Column(name = "article_id")
    private Long articleId;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    public ArticleFavorite() {
        this.createTime = LocalDateTime.now();
    }
    
    public ArticleFavorite(Long userId, Long articleId) {
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