package com.yueyunzhi.controller;

import com.yueyunzhi.entity.ArticleTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping
    public ResponseEntity<List<String>> getAllTagNames() {
        String sql = "SELECT tag_name FROM article_tags ORDER BY tag_name";
        List<String> tagNames = jdbcTemplate.queryForList(sql, String.class);
        return ResponseEntity.ok(tagNames);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<ArticleTag>> getAllTags() {
        String sql = "SELECT * FROM article_tags ORDER BY tag_name";
        List<ArticleTag> tags = jdbcTemplate.query(sql, (rs, rowNum) -> {
            ArticleTag tag = new ArticleTag();
            tag.setTagId(rs.getInt("tag_id"));
            tag.setTagName(rs.getString("tag_name"));
            tag.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            return tag;
        });
        return ResponseEntity.ok(tags);
    }
}
