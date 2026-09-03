package com.yueyunzhi.controller;

import com.yueyunzhi.entity.Comment;
import com.yueyunzhi.service.CommentService;
import com.yueyunzhi.service.CommentLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private CommentLikeService commentLikeService;
    
    @GetMapping("/article/{articleId}")
    public ResponseEntity<Page<Comment>> getCommentsByArticle(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.getCommentsByArticle(articleId, pageable));
    }
    
    @GetMapping("/article/{articleId}/tree")
    public ResponseEntity<List<Comment>> getCommentsTree(@PathVariable Long articleId) {
        return ResponseEntity.ok(commentService.getCommentsTree(articleId));
    }
    
    @GetMapping("/replies/{parentId}")
    public ResponseEntity<List<Comment>> getReplies(@PathVariable Long parentId) {
        return ResponseEntity.ok(commentService.getReplies(parentId));
    }
    
    @GetMapping("/count/{articleId}")
    public ResponseEntity<Long> getCommentCount(@PathVariable Long articleId) {
        return ResponseEntity.ok(commentService.getCommentCount(articleId));
    }
    
    @PostMapping
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.createComment(comment));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/like")
    public ResponseEntity<Boolean> toggleLike(@RequestParam Long userId, @RequestParam Long commentId) {
        boolean isLiked = commentLikeService.toggleLike(userId, commentId);
        return ResponseEntity.ok(isLiked);
    }
    
    @GetMapping("/is-liked")
    public ResponseEntity<Boolean> isLiked(@RequestParam Long userId, @RequestParam Long commentId) {
        boolean isLiked = commentLikeService.isLiked(userId, commentId);
        return ResponseEntity.ok(isLiked);
    }
}