package com.yueyunzhi.service;

import com.yueyunzhi.entity.CommentLike;
import com.yueyunzhi.entity.CommentLikeId;
import com.yueyunzhi.repository.CommentLikeRepository;
import com.yueyunzhi.entity.Comment;
import com.yueyunzhi.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CommentLikeService {
    
    @Autowired
    private CommentLikeRepository commentLikeRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Transactional
    public boolean toggleLike(Long userId, Long commentId) {
        // 检查是否已点赞
        boolean alreadyLiked = commentLikeRepository.existsByUserIdAndCommentId(userId, commentId);
        
        if (alreadyLiked) {
            // 取消点赞
            commentLikeRepository.deleteByUserIdAndCommentId(userId, commentId);
            // 减少点赞数
            Comment comment = commentRepository.findById(commentId).orElse(null);
            if (comment != null && comment.getLikeCount() > 0) {
                comment.setLikeCount(comment.getLikeCount() - 1);
                commentRepository.save(comment);
            }
            return false; // 已取消点赞
        } else {
            // 新增点赞
            CommentLike commentLike = new CommentLike();
            commentLike.setUserId(userId);
            commentLike.setCommentId(commentId);
            commentLike.setCreateTime(LocalDateTime.now());
            commentLikeRepository.save(commentLike);
            // 增加点赞数
            Comment comment = commentRepository.findById(commentId).orElse(null);
            if (comment != null) {
                if (comment.getLikeCount() == null) {
                    comment.setLikeCount(0);
                }
                comment.setLikeCount(comment.getLikeCount() + 1);
                commentRepository.save(comment);
            }
            return true; // 已点赞
        }
    }
    
    public boolean isLiked(Long userId, Long commentId) {
        return commentLikeRepository.existsByUserIdAndCommentId(userId, commentId);
    }
}
