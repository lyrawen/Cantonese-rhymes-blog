package com.yueyunzhi.repository;

import com.yueyunzhi.entity.Article;
import com.yueyunzhi.dto.ArticleWithDetailsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    
    Page<Article> findByStatus(Integer status, Pageable pageable);
    
    Page<Article> findByCategoryIdAndStatus(Integer categoryId, Integer status, Pageable pageable);
    
    Page<Article> findByIsFeaturedAndStatus(Boolean isFeatured, Integer status, Pageable pageable);
    
    Page<Article> findByAuthorIdAndStatus(Long authorId, Integer status, Pageable pageable);
    
    @Query("SELECT a FROM Article a WHERE a.status = 1 AND (a.title LIKE %:keyword% OR a.content LIKE %:keyword%) ORDER BY a.createTime DESC")
    Page<Article> searchArticles(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT new com.yueyunzhi.dto.ArticleWithDetailsDTO(a.articleId, a.authorId, a.categoryId, a.title, a.summary, a.excerpt, a.content, a.coverImage, a.status, a.isFeatured, a.viewCount, a.likeCount, a.commentCount, a.favoriteCount, a.createTime, a.updateTime, a.publishTime, a.authorAvatar, a.authorName, a.category, a.tags, a.updatedAt, ac.categoryName, u.nickname, u.avatar, u.bio, 0, 0) " +
                 "FROM Article a " +
                 "LEFT JOIN User u ON a.authorId = u.userId " +
                 "LEFT JOIN ArticleCategory ac ON a.categoryId = ac.categoryId " +
                 "WHERE a.status = 1 AND (a.title LIKE %:keyword% OR a.content LIKE %:keyword%) " +
                 "ORDER BY a.createTime DESC")
    List<ArticleWithDetailsDTO> searchArticlesWithDetails(@Param("keyword") String keyword);
    
    List<Article> findTop10ByStatusOrderByViewCountDesc(Integer status);
    
    List<Article> findTop6ByStatusAndIsFeaturedOrderByCreateTimeDesc(Integer status, Boolean isFeatured);
    
    @Query("SELECT new com.yueyunzhi.dto.ArticleWithDetailsDTO(a.articleId, a.authorId, a.categoryId, a.title, a.summary, a.excerpt, a.content, a.coverImage, a.status, a.isFeatured, a.viewCount, a.likeCount, a.commentCount, a.favoriteCount, a.createTime, a.updateTime, a.publishTime, a.authorAvatar, a.authorName, a.category, a.tags, a.updatedAt, ac.categoryName, u.nickname, u.avatar, u.bio, 0, 0) " +
                 "FROM Article a " +
                 "LEFT JOIN User u ON a.authorId = u.userId " +
                 "LEFT JOIN ArticleCategory ac ON a.categoryId = ac.categoryId " +
                 "WHERE a.status = 1 " +
                 "ORDER BY a.viewCount DESC")
    List<ArticleWithDetailsDTO> findHotArticlesWithDetails();
    
    @Query("SELECT new com.yueyunzhi.dto.ArticleWithDetailsDTO(a.articleId, a.authorId, a.categoryId, a.title, a.summary, a.excerpt, a.content, a.coverImage, a.status, a.isFeatured, a.viewCount, a.likeCount, a.commentCount, a.favoriteCount, a.createTime, a.updateTime, a.publishTime, a.authorAvatar, a.authorName, a.category, a.tags, a.updatedAt, ac.categoryName, u.nickname, u.avatar, u.bio, 0, 0) " +
                 "FROM Article a " +
                 "LEFT JOIN User u ON a.authorId = u.userId " +
                 "LEFT JOIN ArticleCategory ac ON a.categoryId = ac.categoryId " +
                 "WHERE a.status = 1 AND a.isFeatured = true " +
                 "ORDER BY a.createTime DESC")
    List<ArticleWithDetailsDTO> findFeaturedArticlesWithDetails();
    
    @Query("SELECT new com.yueyunzhi.dto.ArticleWithDetailsDTO(a.articleId, a.authorId, a.categoryId, a.title, a.summary, a.excerpt, a.content, a.coverImage, a.status, a.isFeatured, a.viewCount, a.likeCount, a.commentCount, a.favoriteCount, a.createTime, a.updateTime, a.publishTime, a.authorAvatar, a.authorName, a.category, a.tags, a.updatedAt, ac.categoryName, u.nickname, u.avatar, u.bio, 0, 0) " +
                 "FROM Article a " +
                 "LEFT JOIN User u ON a.authorId = u.userId " +
                 "LEFT JOIN ArticleCategory ac ON a.categoryId = ac.categoryId " +
                 "WHERE a.articleId = :id AND a.status = 1")
    ArticleWithDetailsDTO findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT new com.yueyunzhi.dto.ArticleWithDetailsDTO(a.articleId, a.authorId, a.categoryId, a.title, a.summary, a.excerpt, a.content, a.coverImage, a.status, a.isFeatured, a.viewCount, a.likeCount, a.commentCount, a.favoriteCount, a.createTime, a.updateTime, a.publishTime, a.authorAvatar, a.authorName, a.category, a.tags, a.updatedAt, ac.categoryName, u.nickname, u.avatar, u.bio, 0, 0) " +
                 "FROM Article a " +
                 "LEFT JOIN User u ON a.authorId = u.userId " +
                 "LEFT JOIN ArticleCategory ac ON a.categoryId = ac.categoryId " +
                 "WHERE a.articleId = :id")
    ArticleWithDetailsDTO findByIdWithDetailsForAuthor(@Param("id") Long id);
    
    @Query("SELECT COUNT(a) FROM Article a WHERE a.authorId = :authorId")
    Long countArticlesByAuthorId(@Param("authorId") Long authorId);
    
    @Query("SELECT new com.yueyunzhi.dto.ArticleWithDetailsDTO(a.articleId, a.authorId, a.categoryId, a.title, a.summary, a.excerpt, a.content, a.coverImage, a.status, a.isFeatured, a.viewCount, a.likeCount, a.commentCount, a.favoriteCount, a.createTime, a.updateTime, a.publishTime, a.authorAvatar, a.authorName, a.category, a.tags, a.updatedAt, ac.categoryName, u.nickname, u.avatar, u.bio, 0, 0) " +
                 "FROM Article a " +
                 "LEFT JOIN User u ON a.authorId = u.userId " +
                 "LEFT JOIN ArticleCategory ac ON a.categoryId = ac.categoryId " +
                 "WHERE a.status = 1 " +
                 "ORDER BY a.createTime DESC")
    List<ArticleWithDetailsDTO> findAllArticlesWithDetails();
    
    @Query("SELECT new com.yueyunzhi.dto.ArticleWithDetailsDTO(a.articleId, a.authorId, a.categoryId, a.title, a.summary, a.excerpt, a.content, a.coverImage, a.status, a.isFeatured, a.viewCount, a.likeCount, a.commentCount, a.favoriteCount, a.createTime, a.updateTime, a.publishTime, a.authorAvatar, a.authorName, a.category, a.tags, a.updatedAt, ac.categoryName, u.nickname, u.avatar, u.bio, 0, 0) " +
                 "FROM Article a " +
                 "LEFT JOIN User u ON a.authorId = u.userId " +
                 "LEFT JOIN ArticleCategory ac ON a.categoryId = ac.categoryId " +
                 "WHERE a.status = 1")
    List<ArticleWithDetailsDTO> findTop10ByViewCount(Pageable pageable);
    
    @Query("SELECT new com.yueyunzhi.dto.ArticleWithDetailsDTO(a.articleId, a.authorId, a.categoryId, a.title, a.summary, a.excerpt, a.content, a.coverImage, a.status, a.isFeatured, a.viewCount, a.likeCount, a.commentCount, a.favoriteCount, a.createTime, a.updateTime, a.publishTime, a.authorAvatar, a.authorName, a.category, a.tags, a.updatedAt, ac.categoryName, u.nickname, u.avatar, u.bio, 0, 0) " +
                 "FROM Article a " +
                 "LEFT JOIN User u ON a.authorId = u.userId " +
                 "LEFT JOIN ArticleCategory ac ON a.categoryId = ac.categoryId " +
                 "WHERE a.status = 1")
    List<ArticleWithDetailsDTO> findTop10ByLikeCount(Pageable pageable);
    
    @Query("SELECT new com.yueyunzhi.dto.ArticleWithDetailsDTO(a.articleId, a.authorId, a.categoryId, a.title, a.summary, a.excerpt, a.content, a.coverImage, a.status, a.isFeatured, a.viewCount, a.likeCount, a.commentCount, a.favoriteCount, a.createTime, a.updateTime, a.publishTime, a.authorAvatar, a.authorName, a.category, a.tags, a.updatedAt, ac.categoryName, u.nickname, u.avatar, u.bio, 0, 0) " +
                 "FROM Article a " +
                 "LEFT JOIN User u ON a.authorId = u.userId " +
                 "LEFT JOIN ArticleCategory ac ON a.categoryId = ac.categoryId " +
                 "WHERE a.status = 1")
    List<ArticleWithDetailsDTO> findTop10ByCommentCount(Pageable pageable);
    
    @Query("SELECT ac.categoryName, SUM(a.viewCount) as totalViews " +
                 "FROM Article a " +
                 "LEFT JOIN ArticleCategory ac ON a.categoryId = ac.categoryId " +
                 "WHERE a.status = 1 " +
                 "GROUP BY ac.categoryName " +
                 "ORDER BY totalViews DESC")
    List<Object[]> findCategoryViewStats();
    
    @Query("SELECT new com.yueyunzhi.dto.ArticleWithDetailsDTO(a.articleId, a.authorId, a.categoryId, a.title, a.summary, a.excerpt, a.content, a.coverImage, a.status, a.isFeatured, a.viewCount, a.likeCount, a.commentCount, a.favoriteCount, a.createTime, a.updateTime, a.publishTime, a.authorAvatar, a.authorName, a.category, a.tags, a.updatedAt, ac.categoryName, u.nickname, u.avatar, u.bio, 0, 0) " +
                 "FROM Article a " +
                 "LEFT JOIN User u ON a.authorId = u.userId " +
                 "LEFT JOIN ArticleCategory ac ON a.categoryId = ac.categoryId " +
                 "WHERE a.status = 1 AND a.articleId != :articleId AND a.categoryId = :categoryId " +
                 "AND EXISTS (SELECT 1 FROM ArticleTagMap atm WHERE atm.id.articleId = a.articleId AND atm.id.tagId IN :tagIds) " +
                 "ORDER BY a.viewCount DESC")
    List<ArticleWithDetailsDTO> findRelatedArticlesByCategoryAndTags(@Param("articleId") Long articleId, @Param("categoryId") Integer categoryId, @Param("tagIds") List<Integer> tagIds, Pageable pageable);
    
    @Query("SELECT new com.yueyunzhi.dto.ArticleWithDetailsDTO(a.articleId, a.authorId, a.categoryId, a.title, a.summary, a.excerpt, a.content, a.coverImage, a.status, a.isFeatured, a.viewCount, a.likeCount, a.commentCount, a.favoriteCount, a.createTime, a.updateTime, a.publishTime, a.authorAvatar, a.authorName, a.category, a.tags, a.updatedAt, ac.categoryName, u.nickname, u.avatar, u.bio, 0, 0) " +
                 "FROM Article a " +
                 "LEFT JOIN User u ON a.authorId = u.userId " +
                 "LEFT JOIN ArticleCategory ac ON a.categoryId = ac.categoryId " +
                 "WHERE a.status = 1 AND a.articleId != :articleId AND a.categoryId = :categoryId " +
                 "ORDER BY a.viewCount DESC")
    List<ArticleWithDetailsDTO> findRelatedArticlesByCategory(@Param("articleId") Long articleId, @Param("categoryId") Integer categoryId, Pageable pageable);
}
