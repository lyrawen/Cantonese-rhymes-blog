package com.yueyunzhi.controller;

import com.yueyunzhi.entity.User;
import com.yueyunzhi.service.UserService;
import com.yueyunzhi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Value("${file.upload-path:D:/yueyunzhi/uploads/}")
    private String uploadPath;

    /**
     * 上传用户头像
     */
    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) {
        try {
            // 验证token并获取用户名
            String username = jwtUtil.extractUsername(token.substring(7));
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 验证文件类型
            if (!isValidImageFile(file)) {
                return ResponseEntity.badRequest().body(Map.of("error", "请上传有效的图片文件(jpg, jpeg, png, gif)"));
            }

            // 创建上传目录
            String avatarDir = uploadPath + "avatars/";
            Path uploadDir = Paths.get(avatarDir);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = "user_" + user.getUserId() + "_avatar" + extension;

            // 保存文件
            Path filePath = uploadDir.resolve(newFilename);
            file.transferTo(filePath.toFile());

            // 更新用户头像URL
            String avatarUrl = "/uploads/avatars/" + newFilename;
            user.setAvatar(avatarUrl);
            userService.updateProfile(user.getUserId(), user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("avatarUrl", avatarUrl);
            response.put("message", "头像上传成功");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "上传失败: " + e.getMessage()));
        }
    }

    /**
     * 上传用户封面背景
     */
    @PostMapping("/cover")
    public ResponseEntity<?> uploadCover(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) {
        try {
            // 验证token并获取用户名
            String username = jwtUtil.extractUsername(token.substring(7));
            User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));

            // 验证文件类型
            if (!isValidImageFile(file)) {
                return ResponseEntity.badRequest().body(Map.of("error", "请上传有效的图片文件(jpg, jpeg, png, gif)"));
            }

            // 创建上传目录
            String coverDir = uploadPath + "profile_covers/";
            Path uploadDir = Paths.get(coverDir);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = "user_" + user.getUserId() + "_cover" + extension;

            // 保存文件
            Path filePath = uploadDir.resolve(newFilename);
            file.transferTo(filePath.toFile());

            // 更新用户封面URL
            String coverUrl = "/uploads/profile_covers/" + newFilename;
            user.setCoverPhoto(coverUrl);
            userService.updateProfile(user.getUserId(), user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("coverUrl", coverUrl);
            response.put("message", "封面上传成功");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "上传失败: " + e.getMessage()));
        }
    }

    /**
     * 上传文章封面
     */
    @PostMapping("/article-cover")
    public ResponseEntity<?> uploadArticleCover(
            @RequestHeader("Authorization") String token,
            @RequestParam("file") MultipartFile file) {
        try {
            // 验证token
            jwtUtil.extractUsername(token.substring(7));

            // 验证文件类型
            if (!isValidImageFile(file)) {
                return ResponseEntity.badRequest().body(Map.of("error", "请上传有效的图片文件(jpg, jpeg, png, gif)"));
            }

            // 创建上传目录
            String coverDir = uploadPath + "article_covers/";
            Path uploadDir = Paths.get(coverDir);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String newFilename = "article_cover_" + UUID.randomUUID() + extension;

            // 保存文件
            Path filePath = uploadDir.resolve(newFilename);
            file.transferTo(filePath.toFile());

            // 返回封面URL
            String coverUrl = "/uploads/article_covers/" + newFilename;

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("coverUrl", coverUrl);
            response.put("message", "封面上传成功");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "上传失败: " + e.getMessage()));
        }
    }

    /**
     * 验证是否为有效的图片文件
     */
    private boolean isValidImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            return false;
        }
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        return contentType.startsWith("image/");
    }
}