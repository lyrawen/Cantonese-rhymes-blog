package com.yueyunzhi.controller;

import com.yueyunzhi.entity.User;
import com.yueyunzhi.service.UserService;
import com.yueyunzhi.service.UserFollowService;
import com.yueyunzhi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserFollowService userFollowService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 获取粉丝数最多的前三位用户（须在 /{id} 之前，避免路径冲突）
     */
    @GetMapping("/top-authors")
    public ResponseEntity<?> getTopAuthors() {
        try {
            List<User> topUsers = userService.getTop3UsersByFollowerCount();
            List<Map<String, Object>> topAuthors = topUsers.stream()
                    .map(user -> {
                        Map<String, Object> author = convertToUserResponse(user);
                        author.put("followerCount", userFollowService.getFollowerCount(user.getUserId()));
                        return author;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(topAuthors);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userService.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Map<String, Object> userResponse = convertToUserResponse(user);
            // 添加粉丝数量和关注数量
            long followerCount = userFollowService.getFollowerCount(id);
            long followingCount = userFollowService.getFollowingCount(id);
            userResponse.put("followerCount", followerCount);
            userResponse.put("followingCount", followingCount);
            return ResponseEntity.ok(userResponse);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "用户不存在");
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<?> updateUser(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody User userUpdate) {
        try {
            // 验证token并获取当前用户
            String username = jwtUtil.extractUsername(token.substring(7));
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 只能修改自己的资料
            if (!currentUser.getUserId().equals(id)) {
                return ResponseEntity.status(403).body(Map.of("error", "无权修改其他用户的资料"));
            }

            // 更新用户信息
            User updatedUser = userService.updateProfile(id, userUpdate);

            return ResponseEntity.ok(convertToUserResponse(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/follow/{followingId}")
    public ResponseEntity<?> toggleFollow(
            @PathVariable Long followingId,
            @RequestHeader("Authorization") String token) {
        try {
            // 验证token并获取当前用户
            String username = jwtUtil.extractUsername(token.substring(7));
            User currentUser = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            Long followerId = currentUser.getUserId();
            boolean isFollowing = userFollowService.toggleFollow(followerId, followingId);

            Map<String, Object> response = new HashMap<>();
            response.put("isFollowing", isFollowing);
            response.put("followerCount", userFollowService.getFollowerCount(followingId));
            response.put("followingCount", userFollowService.getFollowingCount(followerId));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id:\\d+}/followers/count")
    public ResponseEntity<?> getFollowerCount(@PathVariable Long id) {
        try {
            long followerCount = userFollowService.getFollowerCount(id);
            return ResponseEntity.ok(Map.of("followerCount", followerCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id:\\d+}/following/count")
    public ResponseEntity<?> getFollowingCount(@PathVariable Long id) {
        try {
            long followingCount = userFollowService.getFollowingCount(id);
            return ResponseEntity.ok(Map.of("followingCount", followingCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id:\\d+}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable Long id) {
        try {
            List<User> followers = userFollowService.getFollowers(id);
            List<Map<String, Object>> followerResponses = followers.stream()
                    .map(this::convertToUserResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(followerResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id:\\d+}/following")
    public ResponseEntity<?> getFollowing(@PathVariable Long id) {
        try {
            List<User> following = userFollowService.getFollowing(id);
            List<Map<String, Object>> followingResponses = following.stream()
                    .map(this::convertToUserResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(followingResponses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    private Map<String, Object> convertToUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("userId", user.getUserId());
        userResponse.put("username", user.getUsername());
        userResponse.put("email", user.getEmail());
        userResponse.put("nickname", user.getNickname());
        userResponse.put("avatar", user.getAvatar());
        userResponse.put("bio", user.getBio());
        userResponse.put("role", user.getRole());
        userResponse.put("status", user.getStatus());
        userResponse.put("createTime", user.getCreateTime());
        userResponse.put("coverPhoto", user.getCoverPhoto());
        return userResponse;
    }
}
