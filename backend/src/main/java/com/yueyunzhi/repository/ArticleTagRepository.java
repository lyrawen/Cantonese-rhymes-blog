package com.yueyunzhi.repository;

import com.yueyunzhi.entity.ArticleTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class ArticleTagRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    public List<String> findTagNamesByArticleId(Long articleId) {
        String sql = "SELECT at.tag_name FROM article_tags at JOIN article_tag_map atm ON at.tag_id = atm.tag_id WHERE atm.article_id = ?";
        return jdbcTemplate.queryForList(sql, String.class, articleId);
    }
    
    public List<Integer> findTagIdsByArticleId(Long articleId) {
        String sql = "SELECT at.tag_id FROM article_tags at JOIN article_tag_map atm ON at.tag_id = atm.tag_id WHERE atm.article_id = ?";
        return jdbcTemplate.queryForList(sql, Integer.class, articleId);
    }
    
    public Optional<ArticleTag> findByTagName(String tagName) {
        String sql = "SELECT * FROM article_tags WHERE tag_name = ?";
        try {
            ArticleTag tag = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(rs.getInt("tag_id"));
                articleTag.setTagName(rs.getString("tag_name"));
                articleTag.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
                return articleTag;
            }, tagName);
            return Optional.ofNullable(tag);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public ArticleTag save(ArticleTag tag) {
        if (tag.getTagId() == null) {
            String sql = "INSERT INTO article_tags (tag_name, create_time) VALUES (?, NOW())";
            jdbcTemplate.update(sql, tag.getTagName());
            
            String idSql = "SELECT LAST_INSERT_ID()";
            Integer tagId = jdbcTemplate.queryForObject(idSql, Integer.class);
            tag.setTagId(tagId);
        }
        return tag;
    }
}
