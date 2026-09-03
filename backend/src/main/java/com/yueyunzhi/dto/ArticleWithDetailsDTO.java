package com.yueyunzhi.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ArticleWithDetailsDTO {
    private Long articleId;
    private Long authorId;
    private Integer categoryId;
    private String title;
    private String summary;
    private String excerpt;
    private String content;
    private String coverImage;
    private Integer status;
    private Boolean isFeatured;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer favoriteCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime publishTime;
    private String authorAvatar;
    private String authorName;
    private String category;
    private String tags;
    private LocalDateTime updatedAt;
    
    // 关联查询字段
    private String categoryName;
    private String userNickname;
    private String userAvatar;
    private String userBio;
    private Integer articleCount;
    private Integer followerCount;
    
    // 构造函数
    public ArticleWithDetailsDTO(Long articleId, Long authorId, Integer categoryId, String title, String summary, 
                               String excerpt, String content, String coverImage, Integer status, 
                               Boolean isFeatured, Integer viewCount, Integer likeCount, 
                               Integer commentCount, Integer favoriteCount, LocalDateTime createTime, 
                               LocalDateTime updateTime, LocalDateTime publishTime, String authorAvatar, 
                               String authorName, String category, String tags, LocalDateTime updatedAt, 
                               String categoryName, String userNickname, String userAvatarFromUser, 
                               String userBio, Integer articleCount, Integer followerCount) {
        this.articleId = articleId;
        this.authorId = authorId;
        this.categoryId = categoryId;
        this.title = title;
        this.summary = summary;
        this.excerpt = excerpt;
        this.content = content;
        this.coverImage = coverImage;
        this.status = status;
        this.isFeatured = isFeatured;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.favoriteCount = favoriteCount;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.publishTime = publishTime;
        this.authorAvatar = authorAvatar;
        this.authorName = authorName;
        this.category = category;
        this.tags = tags;
        this.updatedAt = updatedAt;
        this.categoryName = categoryName;
        this.userNickname = userNickname;
        this.userAvatar = userAvatarFromUser;
        this.userBio = userBio;
        this.articleCount = articleCount;
        this.followerCount = followerCount;
    }
}