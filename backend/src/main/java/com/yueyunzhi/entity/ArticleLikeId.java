package com.yueyunzhi.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ArticleLikeId implements Serializable {
    
    private Long userId;
    private Long articleId;
    
    public ArticleLikeId() {
    }
    
    public ArticleLikeId(Long userId, Long articleId) {
        this.userId = userId;
        this.articleId = articleId;
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleLikeId that = (ArticleLikeId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(articleId, that.articleId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, articleId);
    }
}