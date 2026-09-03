package com.yueyunzhi.repository;

import com.yueyunzhi.entity.CommentLike;
import com.yueyunzhi.entity.CommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
    
    boolean existsByUserIdAndCommentId(Long userId, Long commentId);
    
    void deleteByUserIdAndCommentId(Long userId, Long commentId);
}
