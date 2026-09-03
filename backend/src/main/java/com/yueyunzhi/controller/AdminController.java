package com.yueyunzhi.controller;

import com.yueyunzhi.entity.Article;
import com.yueyunzhi.entity.ArticleCategory;
import com.yueyunzhi.entity.User;
import com.yueyunzhi.service.AdminService;
import com.yueyunzhi.service.UserService;
import com.yueyunzhi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    private void requireAdmin(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new RuntimeException("未登录");
        }
        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userService.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!"admin".equals(user.getRole())) {
            throw new RuntimeException("无权访问管理后台");
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            return ResponseEntity.ok(adminService.getOverviewStats());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/articles/pending")
    public ResponseEntity<?> getPendingArticles(@RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            return ResponseEntity.ok(adminService.getPendingArticles());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/articles")
    public ResponseEntity<?> getAllArticles(@RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            return ResponseEntity.ok(adminService.getAllArticlesAdmin());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/articles/{id}/approve")
    public ResponseEntity<?> approveArticle(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            return ResponseEntity.ok(adminService.approveArticle(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/articles/{id}/reject")
    public ResponseEntity<?> rejectArticle(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            return ResponseEntity.ok(adminService.rejectArticle(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity<?> deleteArticle(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            adminService.deleteArticle(id);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/comments")
    public ResponseEntity<?> getComments(@RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            return ResponseEntity.ok(adminService.getAllCommentsAdmin());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            adminService.deleteComment(id);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/comments/{id}/status")
    public ResponseEntity<?> updateCommentStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body,
            @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            adminService.updateCommentStatus(id, body.getOrDefault("status", 1));
            return ResponseEntity.ok(Map.of("message", "更新成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/categories")
    public ResponseEntity<?> getCategories(@RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            return ResponseEntity.ok(adminService.getCategoriesWithCount());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/categories")
    public ResponseEntity<?> createCategory(
            @RequestBody Map<String, Object> body,
            @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            String name = (String) body.get("categoryName");
            Integer sortOrder = body.get("sortOrder") != null ? ((Number) body.get("sortOrder")).intValue() : null;
            ArticleCategory category = adminService.createCategory(name, sortOrder);
            return ResponseEntity.ok(category);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            adminService.deleteCategory(id);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/tags")
    public ResponseEntity<?> getTags(@RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            return ResponseEntity.ok(adminService.getTagsWithCount());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/tags")
    public ResponseEntity<?> createTag(@RequestBody Map<String, String> body, @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            adminService.createTag(body.get("tagName"));
            return ResponseEntity.ok(Map.of("message", "添加成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/tags/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            adminService.deleteTag(id);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> getUsers(@RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            return ResponseEntity.ok(adminService.getAllUsersAdmin());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<?> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body,
            @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            User user = adminService.updateUserStatus(id, body.getOrDefault("status", 1));
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/announcements")
    public ResponseEntity<?> getAnnouncements(@RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            return ResponseEntity.ok(adminService.getAnnouncements());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/announcements")
    public ResponseEntity<?> createAnnouncement(
            @RequestBody Map<String, String> body,
            @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            return ResponseEntity.ok(adminService.createAnnouncement(body.get("title"), body.get("content")));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/announcements/{id}/status")
    public ResponseEntity<?> updateAnnouncementStatus(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> body,
            @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            adminService.updateAnnouncementStatus(id, body.getOrDefault("status", 0));
            return ResponseEntity.ok(Map.of("message", "更新成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/announcements/{id}")
    public ResponseEntity<?> deleteAnnouncement(@PathVariable Integer id, @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            adminService.deleteAnnouncement(id);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ai-chat")
    public ResponseEntity<?> getAiChat(@RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            return ResponseEntity.ok(adminService.getAllAiChatAdmin());
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/ai-chat/{id}")
    public ResponseEntity<?> deleteAiChat(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            adminService.deleteAiChat(id);
            return ResponseEntity.ok(Map.of("message", "删除成功"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/ranking/{metric}")
    public ResponseEntity<?> getRanking(@PathVariable String metric, @RequestHeader("Authorization") String token) {
        try {
            requireAdmin(token);
            return ResponseEntity.ok(adminService.getTopArticles(metric, 10));
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(Map.of("error", e.getMessage()));
        }
    }
}
