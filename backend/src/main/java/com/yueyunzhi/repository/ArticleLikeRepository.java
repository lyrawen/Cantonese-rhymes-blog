package com.yueyunzhi.repository;

import com.yueyunzhi.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, com.yueyunzhi.entity.ArticleLikeId> {
    
    Optional<ArticleLike> findByUserIdAndArticleId(Long userId, Long articleId);
    
    void deleteByUserIdAndArticleId(Long userId, Long articleId);
    
    @Query("SELECT COUNT(al) FROM ArticleLike al WHERE al.articleId = :articleId")
    Long countByArticleId(@Param("articleId") Long articleId);
    
    @Query("SELECT CASE WHEN COUNT(al) > 0 THEN true ELSE false END FROM ArticleLike al WHERE al.userId = :userId AND al.articleId = :articleId")
    boolean existsByUserIdAndArticleId(@Param("userId") Long userId, @Param("articleId") Long articleId);
}