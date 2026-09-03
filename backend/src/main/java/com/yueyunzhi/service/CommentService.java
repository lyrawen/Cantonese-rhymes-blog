package com.yueyunzhi.service;

import com.yueyunzhi.entity.Comment;
import com.yueyunzhi.entity.User;
import com.yueyunzhi.repository.CommentRepository;
import com.yueyunzhi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public Page<Comment> getCommentsByArticle(Long articleId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByArticleIdAndStatusOrderByCreateTimeDesc(articleId, (byte) 1, pageable);
        // 为每条评论填充用户信息
        comments.getContent().forEach(this::fillUserInfo);
        return comments;
    }
    
    public List<Comment> getReplies(Long parentId) {
        List<Comment> replies = commentRepository.findByParentIdAndStatusOrderByCreateTimeAsc(parentId, (byte) 1);
        // 为每条回复填充用户信息
        replies.forEach(this::fillUserInfo);
        return replies;
    }
    
    public List<Comment> getCommentsTree(Long articleId) {
        List<Comment> comments = commentRepository.findByArticleIdAndParentIdIsNullAndStatusOrderByCreateTimeAsc(articleId, (byte) 1);
        // 为每条评论填充用户信息
        comments.forEach(this::fillUserInfo);
        System.out.println("获取一级评论，文章ID: " + articleId + ", 评论数量: " + comments.size());
        for (Comment comment : comments) {
            System.out.println("一级评论 ID: " + comment.getId() + ", parent_id: " + comment.getParentId() + ", content: " + comment.getContent());
        }
        return comments;
    }
    
    private void fillUserInfo(Comment comment) {
        if (comment.getUserId() != null) {
            User user = userRepository.findById(comment.getUserId()).orElse(null);
            if (user != null) {
                // 如果数据库中没有存储用户名和头像，则从用户表获取
                if (comment.getUserName() == null || comment.getUserName().isEmpty()) {
                    comment.setUserName(user.getNickname() != null ? user.getNickname() : user.getUsername());
                }
                if (comment.getUserAvatar() == null || comment.getUserAvatar().isEmpty()) {
                    comment.setUserAvatar(user.getAvatar());
                }
            }
        }
    }
    
    @Transactional
    public Comment createComment(Comment comment) {
        User user = userRepository.findById(comment.getUserId()).orElse(null);
        if (user != null) {
            comment.setUserName(user.getUsername());
            comment.setUserAvatar(user.getAvatar());
        }
        
        // 如果是回复评论，设置被回复用户的信息
        if (comment.getReplyToUserId() != null) {
            User replyToUser = userRepository.findById(comment.getReplyToUserId()).orElse(null);
            if (replyToUser != null) {
                comment.setReplyToUserName(replyToUser.getNickname() != null ? replyToUser.getNickname() : replyToUser.getUsername());
            }
        }
        
        Comment savedComment = commentRepository.save(comment);
        return savedComment;
    }
    
    @Transactional
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
    
    public long getCommentCount(Long articleId) {
        return commentRepository.countByArticleId(articleId);
    }
}