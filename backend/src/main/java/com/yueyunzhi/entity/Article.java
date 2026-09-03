package com.yueyunzhi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "articles")
public class Article {
    
    @Id
    @Column(name = "article_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleId;
    
    @Column(name = "author_id", nullable = false)
    private Long authorId;
    
    @Column(name = "category_id")
    private Integer categoryId;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(name = "summary", length = 500)
    private String summary;
    
    @Column(name = "excerpt", length = 500)
    private String excerpt;
    
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "cover_image", length = 255)
    private String coverImage;
    
    @Column(name = "status")
    private Integer status;
    
    @Column(name = "is_featured")
    private Boolean isFeatured;
    
    @Column(name = "view_count")
    private Integer viewCount;
    
    @Column(name = "like_count")
    private Integer likeCount;
    
    @Column(name = "comment_count")
    private Integer commentCount;
    
    @Column(name = "favorite_count")
    private Integer favoriteCount;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    @Column(name = "publish_time")
    private LocalDateTime publishTime;
    
    @Column(name = "author_avatar", length = 200)
    private String authorAvatar;
    
    @Column(name = "author_name", length = 50)
    private String authorName;
    
    @Column(name = "category", length = 100)
    private String category;
    
    @Column(name = "tags", length = 255)
    private String tags;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 关联查询时使用的临时字段
    @Transient
    private String categoryName;
    
    @Transient
    private String userNickname;
    
    @Transient
    private String userAvatar;
}