package com.yueyunzhi.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "article_category")
public class ArticleCategory {

    @Id
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name", nullable = false, length = 50)
    private String categoryName;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}