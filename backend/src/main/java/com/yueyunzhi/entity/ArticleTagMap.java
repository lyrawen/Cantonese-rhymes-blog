package com.yueyunzhi.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "article_tag_map")
public class ArticleTagMap {

    @EmbeddedId
    private ArticleTagMapId id;
}
