package com.yueyunzhi.service;

import com.yueyunzhi.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);
    private static final String KEY_PREFIX = "auth:blacklist:";

    /** Redis 不可用时单机内存降级（仅当前进程有效） */
    private final ConcurrentHashMap<String, Long> localBlacklist = new ConcurrentHashMap<>();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    public void blacklist(String token) {
        String hashed = hashToken(token);
        long ttlSeconds = jwtUtil.getRemainingSeconds(token);
        if (ttlSeconds <= 0) {
            return;
        }
        long expireAt = System.currentTimeMillis() + ttlSeconds * 1000;
        localBlacklist.put(hashed, expireAt);

        try {
            redisTemplate.opsForValue().set(KEY_PREFIX + hashed, "1", Duration.ofSeconds(ttlSeconds));
        } catch (Exception e) {
            log.warn("Redis 不可用，已使用内存黑名单降级: {}", e.getMessage());
        }
    }

    public boolean isBlacklisted(String token) {
        String hashed = hashToken(token);
        Long expireAt = localBlacklist.get(hashed);
        if (expireAt != null) {
            if (System.currentTimeMillis() < expireAt) {
                return true;
            }
            localBlacklist.remove(hashed);
        }

        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(KEY_PREFIX + hashed));
        } catch (Exception e) {
            log.warn("Redis 黑名单查询失败，仅使用内存黑名单: {}", e.getMessage());
            return false;
        }
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
