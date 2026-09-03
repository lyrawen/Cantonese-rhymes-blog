package com.yueyunzhi.service;

import com.yueyunzhi.entity.User;
import com.yueyunzhi.repository.UserRepository;
import com.yueyunzhi.repository.UserFollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFollowRepository userFollowRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("用户名已被注册");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置默认头像
        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar("/avatars/default_avatar.png");
        }
        
        // 设置默认背景图片
        if (user.getCoverPhoto() == null || user.getCoverPhoto().isEmpty()) {
            user.setCoverPhoto("/uploads/profile_covers/default_cover.png");
        }
        
        // 设置创建时间
        user.setCreateTime(LocalDateTime.now());
        
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        Optional<User> user = userRepository.findByUsername(usernameOrEmail);
        if (user.isPresent()) {
            return user;
        }
        return userRepository.findByEmail(usernameOrEmail);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User updateProfile(Long userId, User user) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 更新基本信息
        if (user.getNickname() != null) {
            existingUser.setNickname(user.getNickname());
        }
        if (user.getBio() != null) {
            existingUser.setBio(user.getBio());
        }
        if (user.getAvatar() != null) {
            existingUser.setAvatar(user.getAvatar());
        }
        if (user.getCoverPhoto() != null) {
            existingUser.setCoverPhoto(user.getCoverPhoto());
        }
        
        // 更新密码（如果提供了新密码）
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        
        return userRepository.save(existingUser);
    }
    
    /**
     * 更新用户头像
     */
    public User updateAvatar(Long userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setAvatar(avatarUrl);
        return userRepository.save(user);
    }
    
    /**
     * 更新用户封面背景
     */
    public User updateCoverPhoto(Long userId, String coverPhotoUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setCoverPhoto(coverPhotoUrl);
        return userRepository.save(user);
    }
    
    /**
     * 获取粉丝数最多的前三位用户
     */
    public List<User> getTop3UsersByFollowerCount() {
        List<Long> ids = userFollowRepository.findTop3UserIdsByFollowerCount();
        if (ids.isEmpty()) {
            return List.of();
        }
        Map<Long, User> userMap = userRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(User::getUserId, u -> u));
        List<User> ordered = new ArrayList<>();
        for (Long id : ids) {
            User user = userMap.get(id);
            if (user != null) {
                ordered.add(user);
            }
        }
        return ordered;
    }
}