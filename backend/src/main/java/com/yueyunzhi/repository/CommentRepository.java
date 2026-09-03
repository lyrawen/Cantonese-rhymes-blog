package com.yueyunzhi.repository;

import com.yueyunzhi.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    Page<Comment> findByArticleIdAndStatusOrderByCreateTimeDesc(Long articleId, Byte status, Pageable pageable);
    
    List<Comment> findByParentIdAndStatusOrderByCreateTimeAsc(Long parentId, Byte status);
    
    List<Comment> findByArticleIdAndParentIdIsNullAndStatusOrderByCreateTimeAsc(Long articleId, Byte status);
    
    long countByArticleId(Long articleId);
}