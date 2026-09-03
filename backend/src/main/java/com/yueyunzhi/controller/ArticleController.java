package com.yueyunzhi.controller;

import com.yueyunzhi.entity.Article;
import com.yueyunzhi.dto.ArticleWithDetailsDTO;
import com.yueyunzhi.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    
    @Autowired
    private ArticleService articleService;
    
    @GetMapping
    public ResponseEntity<Page<Article>> getAllArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return ResponseEntity.ok(articleService.getAllArticles(pageable));
    }
    
    @GetMapping("/featured")
    public ResponseEntity<List<ArticleWithDetailsDTO>> getFeaturedArticles() {
        return ResponseEntity.ok(articleService.getFeaturedArticlesList());
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<ArticleWithDetailsDTO>> getAllArticlesList() {
        return ResponseEntity.ok(articleService.getAllArticlesList());
    }
    
    @GetMapping("/hot")
    public ResponseEntity<List<ArticleWithDetailsDTO>> getHotArticles() {
        return ResponseEntity.ok(articleService.getHotArticles());
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<Article>> getArticlesByCategoryId(
            @PathVariable Integer categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return ResponseEntity.ok(articleService.getArticlesByCategoryId(categoryId, pageable));
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<ArticleWithDetailsDTO>> searchArticles(
            @RequestParam String keyword) {
        return ResponseEntity.ok(articleService.searchArticles(keyword));
    }
    
    @GetMapping("/author/{authorId}")
    public ResponseEntity<Page<Article>> getArticlesByAuthor(
            @PathVariable Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return ResponseEntity.ok(articleService.getArticlesByAuthor(authorId, pageable));
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<Article>> getArticlesByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
        return ResponseEntity.ok(articleService.getArticlesByAuthor(userId, pageable));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ArticleWithDetailsDTO> getArticleById(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @GetMapping("/{id}/author")
    public ResponseEntity<ArticleWithDetailsDTO> getArticleByIdForAuthor(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleByIdForAuthor(id));
    }
    
    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        return ResponseEntity.ok(articleService.createArticle(article));
    }
    
    @PostMapping("/draft")
    public ResponseEntity<Article> saveDraft(@RequestBody Article article) {
        return ResponseEntity.ok(articleService.createArticle(article));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody Article article) {
        return ResponseEntity.ok(articleService.updateArticle(id, article));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/like")
    public ResponseEntity<ArticleWithDetailsDTO> likeArticle(@PathVariable Long id) {
        articleService.incrementLikes(id);
        return ResponseEntity.ok(articleService.getArticleByIdWithoutView(id));
    }
    
    @PostMapping("/{id}/toggle-like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long id, @RequestParam Long userId) {
        boolean isLiked = articleService.toggleLike(id, userId);
        ArticleWithDetailsDTO article = articleService.getArticleByIdWithoutView(id);
        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("likeCount", article.getLikeCount());
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/{id}/toggle-favorite")
    public ResponseEntity<Map<String, Object>> toggleFavorite(@PathVariable Long id, @RequestParam Long userId) {
        boolean isFavorited = articleService.toggleFavorite(id, userId);
        ArticleWithDetailsDTO article = articleService.getArticleByIdWithoutView(id);
        Map<String, Object> result = new HashMap<>();
        result.put("isFavorited", isFavorited);
        result.put("favoriteCount", article.getFavoriteCount());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{id}/like-status")
    public ResponseEntity<Map<String, Boolean>> getLikeStatus(@PathVariable Long id, @RequestParam Long userId) {
        boolean isLiked = articleService.isLiked(id, userId);
        boolean isFavorited = articleService.isFavorited(id, userId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("isFavorited", isFavorited);
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/ranking/views")
    public ResponseEntity<List<ArticleWithDetailsDTO>> getViewsRanking() {
        return ResponseEntity.ok(articleService.getTop10ByViewCount());
    }
    
    @GetMapping("/ranking/likes")
    public ResponseEntity<List<ArticleWithDetailsDTO>> getLikesRanking() {
        return ResponseEntity.ok(articleService.getTop10ByLikeCount());
    }
    
    @GetMapping("/ranking/comments")
    public ResponseEntity<List<ArticleWithDetailsDTO>> getCommentsRanking() {
        return ResponseEntity.ok(articleService.getTop10ByCommentCount());
    }
    
    @GetMapping("/ranking/category-stats")
    public ResponseEntity<List<Object[]>> getCategoryStats() {
        return ResponseEntity.ok(articleService.getCategoryViewStats());
    }
    
    @GetMapping("/{id}/related")
    public ResponseEntity<List<ArticleWithDetailsDTO>> getRelatedArticles(@PathVariable Long id) {
        ArticleWithDetailsDTO article = articleService.getArticleByIdWithoutView(id);
        if (article == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(articleService.getRelatedArticles(id, article.getCategoryId()));
    }
}