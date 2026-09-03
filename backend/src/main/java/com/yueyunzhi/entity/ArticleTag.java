package com.yueyunzhi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "article_tags")
public class ArticleTag {
    
    @Id
    @Column(name = "tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tagId;
    
    @Column(name = "tag_name", length = 50, unique = true)
    private String tagName;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
    
    public ArticleTag() {
    }
    
    public ArticleTag(String tagName) {
        this.tagName = tagName;
        this.createTime = LocalDateTime.now();
    }
}
