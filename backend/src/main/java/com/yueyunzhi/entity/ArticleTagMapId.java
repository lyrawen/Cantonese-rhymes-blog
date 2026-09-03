package com.yueyunzhi.entity;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Data
@Embeddable
public class ArticleTagMapId implements Serializable {

    private Long articleId;
    private Integer tagId;
}
