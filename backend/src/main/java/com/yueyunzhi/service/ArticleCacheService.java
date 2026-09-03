package com.yueyunzhi.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.yueyunzhi.dto.ArticleWithDetailsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ArticleCacheService {

    private static final Logger log = LoggerFactory.getLogger(ArticleCacheService.class);
    private static final String KEY_PREFIX = "article:detail:";

    @Value("${cache.article-detail.ttl-minutes:30}")
    private long ttlMinutes;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public Optional<ArticleWithDetailsDTO> get(Long articleId) {
        try {
            String cacheKey = key(articleId);
            String json = redisTemplate.opsForValue().get(cacheKey);
            if (json == null || json.isBlank()) {
                log.info("[文章缓存] MISS articleId={} key={}", articleId, cacheKey);
                return Optional.empty();
            }
            ArticleWithDetailsDTO article = objectMapper.readValue(json, ArticleWithDetailsDTO.class);
            log.info("[文章缓存] HIT  articleId={} key={}", articleId, cacheKey);
            return Optional.of(article);
        } catch (Exception e) {
            log.warn("[文章缓存] 读取失败 articleId={}: {}", articleId, e.getMessage());
            return Optional.empty();
        }
    }

    public void put(Long articleId, ArticleWithDetailsDTO article) {
        try {
            String cacheKey = key(articleId);
            String json = objectMapper.writeValueAsString(article);
            long jitterSeconds = ThreadLocalRandom.current().nextLong(0, 301);
            Duration ttl = Duration.ofMinutes(ttlMinutes).plusSeconds(jitterSeconds);
            redisTemplate.opsForValue().set(cacheKey, json, ttl);
            log.info("[文章缓存] PUT  articleId={} key={} ttl={}s", articleId, cacheKey, ttl.getSeconds());
        } catch (JsonProcessingException e) {
            log.warn("[文章缓存] 写入失败 articleId={}: {}", articleId, e.getMessage());
        } catch (Exception e) {
            log.warn("[文章缓存] Redis 不可用 articleId={}: {}", articleId, e.getMessage());
        }
    }

    public void evict(Long articleId) {
        try {
            String cacheKey = key(articleId);
            Boolean deleted = redisTemplate.delete(cacheKey);
            log.info("[文章缓存] EVICT articleId={} key={} deleted={}", articleId, cacheKey, deleted);
        } catch (Exception e) {
            log.warn("[文章缓存] 删除失败 articleId={}: {}", articleId, e.getMessage());
        }
    }

    private String key(Long articleId) {
        return KEY_PREFIX + articleId;
    }
}
