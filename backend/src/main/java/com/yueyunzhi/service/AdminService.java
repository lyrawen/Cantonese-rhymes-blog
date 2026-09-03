package com.yueyunzhi.service;

import com.yueyunzhi.entity.Article;
import com.yueyunzhi.entity.ArticleCategory;
import com.yueyunzhi.entity.User;
import com.yueyunzhi.repository.ArticleCategoryRepository;
import com.yueyunzhi.repository.ArticleRepository;
import com.yueyunzhi.repository.CommentRepository;
import com.yueyunzhi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCategoryRepository articleCategoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleService articleService;

    public Map<String, Object> getOverviewStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("userCount", count("SELECT COUNT(*) FROM users"));
        stats.put("publishedArticleCount", count("SELECT COUNT(*) FROM articles WHERE status = 1"));
        stats.put("pendingArticleCount", count("SELECT COUNT(*) FROM articles WHERE status = 0"));
        stats.put("rejectedArticleCount", count("SELECT COUNT(*) FROM articles WHERE status = 2"));
        stats.put("commentCount", count("SELECT COUNT(*) FROM comments"));
        stats.put("blockedCommentCount", count("SELECT COUNT(*) FROM comments WHERE status = 0"));
        stats.put("totalViewCount", count("SELECT COALESCE(SUM(view_count), 0) FROM articles"));
        stats.put("tagCount", count("SELECT COUNT(*) FROM article_tags"));
        stats.put("aiChatCount", count("SELECT COUNT(*) FROM ai_chat_history"));
        stats.put("announcementCount", count("SELECT COUNT(*) FROM announcements"));
        stats.put("categoryStats", jdbcTemplate.queryForList(
                "SELECT ac.category_name AS name, COUNT(a.article_id) AS value " +
                "FROM article_category ac LEFT JOIN articles a ON ac.category_id = a.category_id AND a.status = 1 " +
                "GROUP BY ac.category_id, ac.category_name ORDER BY value DESC"));
        stats.put("articleStatusStats", List.of(
                Map.of("name", "已发布", "value", count("SELECT COUNT(*) FROM articles WHERE status = 1")),
                Map.of("name", "待审核", "value", count("SELECT COUNT(*) FROM articles WHERE status = 0")),
                Map.of("name", "已拒绝", "value", count("SELECT COUNT(*) FROM articles WHERE status = 2"))
        ));
        return stats;
    }

    public List<Map<String, Object>> getPendingArticles() {
        return jdbcTemplate.queryForList(
                "SELECT a.article_id AS articleId, a.title, a.summary, a.content, a.cover_image AS coverImage, " +
                "a.status, a.create_time AS createTime, a.author_id AS authorId, " +
                "COALESCE(u.nickname, u.username) AS authorName, ac.category_name AS categoryName " +
                "FROM articles a " +
                "LEFT JOIN users u ON a.author_id = u.user_id " +
                "LEFT JOIN article_category ac ON a.category_id = ac.category_id " +
                "WHERE a.status = 0 ORDER BY a.create_time DESC");
    }

    public List<Map<String, Object>> getAllArticlesAdmin() {
        return jdbcTemplate.queryForList(
                "SELECT a.article_id AS articleId, a.title, a.status, a.view_count AS viewCount, " +
                "a.like_count AS likeCount, a.create_time AS createTime, a.publish_time AS publishTime, " +
                "COALESCE(u.nickname, u.username) AS authorName, ac.category_name AS categoryName " +
                "FROM articles a " +
                "LEFT JOIN users u ON a.author_id = u.user_id " +
                "LEFT JOIN article_category ac ON a.category_id = ac.category_id " +
                "ORDER BY a.create_time DESC");
    }

    @Transactional
    public Article approveArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        article.setStatus(1);
        article.setPublishTime(LocalDateTime.now());
        Article saved = articleRepository.save(article);
        articleService.evictArticleCache(id);
        return saved;
    }

    @Transactional
    public Article rejectArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        article.setStatus(2);
        Article saved = articleRepository.save(article);
        articleService.evictArticleCache(id);
        return saved;
    }

    @Transactional
    public void deleteArticle(Long id) {
        articleService.deleteArticle(id);
    }

    public List<Map<String, Object>> getAllCommentsAdmin() {
        return jdbcTemplate.queryForList(
                "SELECT c.comment_id AS commentId, c.content, c.status, c.create_time AS createTime, " +
                "COALESCE(c.user_name, u.nickname, u.username) AS userName, u.avatar AS userAvatar, " +
                "a.title AS articleTitle, a.article_id AS articleId " +
                "FROM comments c " +
                "LEFT JOIN users u ON c.user_id = u.user_id " +
                "LEFT JOIN articles a ON c.article_id = a.article_id " +
                "ORDER BY c.create_time DESC");
    }

    @Transactional
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }

    @Transactional
    public void updateCommentStatus(Long id, int status) {
        commentRepository.findById(id).ifPresent(comment -> {
            comment.setStatus((byte) status);
            commentRepository.save(comment);
        });
    }

    public List<Map<String, Object>> getCategoriesWithCount() {
        return jdbcTemplate.queryForList(
                "SELECT ac.category_id AS categoryId, ac.category_name AS categoryName, ac.sort_order AS sortOrder, " +
                "COUNT(a.article_id) AS articleCount " +
                "FROM article_category ac " +
                "LEFT JOIN articles a ON ac.category_id = a.category_id AND a.status = 1 " +
                "GROUP BY ac.category_id, ac.category_name, ac.sort_order " +
                "ORDER BY ac.sort_order ASC, ac.category_id ASC");
    }

    @Transactional
    public ArticleCategory createCategory(String categoryName, Integer sortOrder) {
        Integer maxId = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(category_id), 0) FROM article_category", Integer.class);
        ArticleCategory category = new ArticleCategory();
        category.setCategoryId(maxId + 1);
        category.setCategoryName(categoryName);
        category.setSortOrder(sortOrder != null ? sortOrder : maxId + 1);
        category.setCreateTime(LocalDateTime.now());
        return articleCategoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Integer id) {
        Long count = count("SELECT COUNT(*) FROM articles WHERE category_id = " + id);
        if (count > 0) {
            throw new RuntimeException("该分类下还有文章，无法删除");
        }
        articleCategoryRepository.deleteById(id);
    }

    public List<Map<String, Object>> getTagsWithCount() {
        return jdbcTemplate.queryForList(
                "SELECT t.tag_id AS tagId, t.tag_name AS tagName, COUNT(m.article_id) AS articleCount " +
                "FROM article_tags t LEFT JOIN article_tag_map m ON t.tag_id = m.tag_id " +
                "GROUP BY t.tag_id, t.tag_name ORDER BY articleCount DESC, t.tag_name ASC");
    }

    @Transactional
    public void createTag(String tagName) {
        jdbcTemplate.update("INSERT INTO article_tags (tag_name, create_time) VALUES (?, NOW())", tagName.trim());
    }

    @Transactional
    public void deleteTag(Integer tagId) {
        jdbcTemplate.update("DELETE FROM article_tag_map WHERE tag_id = ?", tagId);
        jdbcTemplate.update("DELETE FROM article_tags WHERE tag_id = ?", tagId);
    }

    public List<Map<String, Object>> getAllUsersAdmin() {
        return jdbcTemplate.queryForList(
                "SELECT u.user_id AS userId, u.username, u.nickname, u.email, u.role, u.status, u.avatar, " +
                "u.create_time AS createTime, COUNT(a.article_id) AS articleCount " +
                "FROM users u LEFT JOIN articles a ON u.user_id = a.author_id AND a.status = 1 " +
                "GROUP BY u.user_id, u.username, u.nickname, u.email, u.role, u.status, u.avatar, u.create_time " +
                "ORDER BY u.create_time DESC");
    }

    @Transactional
    public User updateUserStatus(Long userId, int status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setStatus(status);
        return userRepository.save(user);
    }

    public List<Map<String, Object>> getAnnouncements() {
        return jdbcTemplate.queryForList(
                "SELECT announcement_id AS announcementId, title, content, status, create_time AS createTime " +
                "FROM announcements ORDER BY create_time DESC");
    }

    @Transactional
    public Map<String, Object> createAnnouncement(String title, String content) {
        jdbcTemplate.update(
                "INSERT INTO announcements (title, content, status, create_time) VALUES (?, ?, 1, NOW())",
                title, content);
        Integer id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        return jdbcTemplate.queryForMap(
                "SELECT announcement_id AS announcementId, title, content, status, create_time AS createTime " +
                "FROM announcements WHERE announcement_id = ?", id);
    }

    @Transactional
    public void updateAnnouncementStatus(Integer id, int status) {
        jdbcTemplate.update("UPDATE announcements SET status = ? WHERE announcement_id = ?", status, id);
    }

    @Transactional
    public void deleteAnnouncement(Integer id) {
        jdbcTemplate.update("DELETE FROM announcements WHERE announcement_id = ?", id);
    }

    public List<Map<String, Object>> getAllAiChatAdmin() {
        return jdbcTemplate.queryForList(
                "SELECT h.chat_id AS chatId, h.user_id AS userId, h.question, h.answer, h.create_time AS createTime, " +
                "COALESCE(u.nickname, u.username) AS userName " +
                "FROM ai_chat_history h LEFT JOIN users u ON h.user_id = u.user_id " +
                "ORDER BY h.create_time DESC LIMIT 100");
    }

    @Transactional
    public void deleteAiChat(Long id) {
        jdbcTemplate.update("DELETE FROM ai_chat_history WHERE chat_id = ?", id);
    }

    public List<Map<String, Object>> getTopArticles(String metric, int limit) {
        String column = switch (metric) {
            case "likes" -> "like_count";
            case "comments" -> "comment_count";
            case "favorites" -> "favorite_count";
            default -> "view_count";
        };
        return jdbcTemplate.queryForList(
                "SELECT article_id AS articleId, title, view_count AS viewCount, like_count AS likeCount, " +
                "comment_count AS commentCount, favorite_count AS favoriteCount " +
                "FROM articles WHERE status = 1 ORDER BY " + column + " DESC LIMIT ?", limit);
    }

    private long count(String sql) {
        Long result = jdbcTemplate.queryForObject(sql, Long.class);
        return result != null ? result : 0L;
    }
}
