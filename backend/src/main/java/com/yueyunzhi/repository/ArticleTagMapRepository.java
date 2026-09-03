package com.yueyunzhi.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ArticleTagMapRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public void save(com.yueyunzhi.entity.ArticleTagMap tagMap) {
        String sql = "INSERT INTO article_tag_map (article_id, tag_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, tagMap.getId().getArticleId(), tagMap.getId().getTagId());
    }
}
