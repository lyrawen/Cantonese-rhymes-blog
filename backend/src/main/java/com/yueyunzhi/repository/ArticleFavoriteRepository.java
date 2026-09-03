package com.yueyunzhi.repository;

import com.yueyunzhi.entity.ArticleFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ArticleFavoriteRepository extends JpaRepository<ArticleFavorite, com.yueyunzhi.entity.ArticleFavoriteId> {
    
    Optional<ArticleFavorite> findByUserIdAndArticleId(Long userId, Long articleId);
    
    void deleteByUserIdAndArticleId(Long userId, Long articleId);
    
    @Query("SELECT COUNT(af) FROM ArticleFavorite af WHERE af.articleId = :articleId")
    Long countByArticleId(@Param("articleId") Long articleId);
    
    @Query("SELECT CASE WHEN COUNT(af) > 0 THEN true ELSE false END FROM ArticleFavorite af WHERE af.userId = :userId AND af.articleId = :articleId")
    boolean existsByUserIdAndArticleId(@Param("userId") Long userId, @Param("articleId") Long articleId);
}