package com.yueyunzhi.service;

import com.yueyunzhi.entity.Article;
import com.yueyunzhi.entity.ArticleLike;
import com.yueyunzhi.entity.ArticleFavorite;
import com.yueyunzhi.entity.User;
import com.yueyunzhi.dto.ArticleWithDetailsDTO;
import com.yueyunzhi.repository.ArticleRepository;
import com.yueyunzhi.repository.ArticleTagRepository;
import com.yueyunzhi.repository.ArticleTagMapRepository;
import com.yueyunzhi.repository.UserFollowRepository;
import com.yueyunzhi.repository.ArticleLikeRepository;
import com.yueyunzhi.repository.ArticleFavoriteRepository;
import com.yueyunzhi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArticleService {
    
    @Autowired
    private ArticleRepository articleRepository;
    
    @Autowired
    private ArticleTagRepository articleTagRepository;
    
    @Autowired
    private ArticleTagMapRepository articleTagMapRepository;
    
    @Autowired
    private UserFollowRepository userFollowRepository;
    
    @Autowired
    private ArticleLikeRepository articleLikeRepository;
    
    @Autowired
    private ArticleFavoriteRepository articleFavoriteRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleCacheService articleCacheService;
    
    public Page<Article> getAllArticles(Pageable pageable) {
        return articleRepository.findByStatus(1, pageable);
    }
    
    public Page<Article> getArticlesByCategoryId(Integer categoryId, Pageable pageable) {
        return articleRepository.findByCategoryIdAndStatus(categoryId, 1, pageable);
    }
    
    public Page<Article> getFeaturedArticles(Pageable pageable) {
        return articleRepository.findByIsFeaturedAndStatus(true, 1, pageable);
    }
    
    public List<ArticleWithDetailsDTO> searchArticles(String keyword) {
        List<ArticleWithDetailsDTO> articles = articleRepository.searchArticlesWithDetails(keyword);
        // 填充标签
        for (ArticleWithDetailsDTO article : articles) {
            List<String> tagNames = articleTagRepository.findTagNamesByArticleId(article.getArticleId());
            if (tagNames != null && !tagNames.isEmpty()) {
                article.setTags(String.join(",", tagNames));
            }
        }
        return articles;
    }
    
    public ArticleWithDetailsDTO getArticleById(Long id) {
        ArticleWithDetailsDTO article = getCachedOrLoad(id);
        int viewCount = incrementViewCount(id);
        article.setViewCount(viewCount);
        overlayLiveStats(article, id, false);
        return article;
    }

    public ArticleWithDetailsDTO getArticleByIdWithoutView(Long id) {
        ArticleWithDetailsDTO article = getCachedOrLoad(id);
        overlayLiveStats(article, id, true);
        return article;
    }

    private ArticleWithDetailsDTO getCachedOrLoad(Long id) {
        return articleCacheService.get(id).orElseGet(() -> {
            ArticleWithDetailsDTO article = articleRepository.findByIdWithDetails(id);
            if (article == null) {
                throw new RuntimeException("文章不存在");
            }
            fillTags(article);
            articleCacheService.put(id, article);
            return article;
        });
    }

    private void fillTags(ArticleWithDetailsDTO article) {
        List<String> tagNames = articleTagRepository.findTagNamesByArticleId(article.getArticleId());
        if (tagNames != null && !tagNames.isEmpty()) {
            article.setTags(String.join(",", tagNames));
        }
    }

    private int incrementViewCount(Long id) {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        int current = existingArticle.getViewCount() == null ? 0 : existingArticle.getViewCount();
        int next = current + 1;
        existingArticle.setViewCount(next);
        articleRepository.save(existingArticle);
        return next;
    }

    private void overlayLiveStats(ArticleWithDetailsDTO article, Long id, boolean includeViewCount) {
        Article entity = articleRepository.findById(id).orElse(null);
        if (entity != null) {
            if (includeViewCount) {
                article.setViewCount(entity.getViewCount() == null ? 0 : entity.getViewCount());
            }
            article.setLikeCount(entity.getLikeCount());
            article.setCommentCount(entity.getCommentCount());
            article.setFavoriteCount(entity.getFavoriteCount());
        }
        Long articleCount = articleRepository.countArticlesByAuthorId(article.getAuthorId());
        Long followerCount = userFollowRepository.countFollowersByUserId(article.getAuthorId());
        article.setArticleCount(articleCount != null ? articleCount.intValue() : 0);
        article.setFollowerCount(followerCount != null ? followerCount.intValue() : 0);
    }

    public void evictArticleCache(Long id) {
        articleCacheService.evict(id);
    }

    public ArticleWithDetailsDTO getArticleByIdForAuthor(Long id) {
        ArticleWithDetailsDTO article = articleRepository.findByIdWithDetailsForAuthor(id);
        if (article == null) {
            throw new RuntimeException("文章不存在");
        }
        // 填充标签
        fillTags(article);
        // 获取作者的文章数和粉丝数
        Long articleCount = articleRepository.countArticlesByAuthorId(article.getAuthorId());
        Long followerCount = userFollowRepository.countFollowersByUserId(article.getAuthorId());
        article.setArticleCount(articleCount != null ? articleCount.intValue() : 0);
        article.setFollowerCount(followerCount != null ? followerCount.intValue() : 0);
        // 作者查看自己的文章不需要更新浏览量
        return article;
    }
    
    public List<ArticleWithDetailsDTO> getHotArticles() {
        try {
            List<ArticleWithDetailsDTO> articles = articleRepository.findHotArticlesWithDetails();
            // 填充标签
            for (ArticleWithDetailsDTO article : articles) {
                List<String> tagNames = articleTagRepository.findTagNamesByArticleId(article.getArticleId());
                if (tagNames != null && !tagNames.isEmpty()) {
                    article.setTags(String.join(",", tagNames));
                }
            }
            return articles;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
    
    public List<ArticleWithDetailsDTO> getFeaturedArticlesList() {
        try {
            List<ArticleWithDetailsDTO> articles = articleRepository.findFeaturedArticlesWithDetails();
            // 填充标签
            for (ArticleWithDetailsDTO article : articles) {
                List<String> tagNames = articleTagRepository.findTagNamesByArticleId(article.getArticleId());
                if (tagNames != null && !tagNames.isEmpty()) {
                    article.setTags(String.join(",", tagNames));
                }
            }
            return articles;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
    
    public List<ArticleWithDetailsDTO> getAllArticlesList() {
        try {
            List<ArticleWithDetailsDTO> articles = articleRepository.findAllArticlesWithDetails();
            // 填充标签
            for (ArticleWithDetailsDTO article : articles) {
                List<String> tagNames = articleTagRepository.findTagNamesByArticleId(article.getArticleId());
                if (tagNames != null && !tagNames.isEmpty()) {
                    article.setTags(String.join(",", tagNames));
                }
            }
            return articles;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
    
    public Page<Article> getArticlesByAuthor(Long authorId, Pageable pageable) {
        return articleRepository.findByAuthorIdAndStatus(authorId, 1, pageable);
    }
    
    @Transactional
    public Article createArticle(Article article) {
        Article savedArticle = articleRepository.save(article);
        
        // 处理标签关联
        if (article.getTags() != null && !article.getTags().isEmpty()) {
            String[] tagNames = article.getTags().split(",");
            for (String tagName : tagNames) {
                // 查找或创建标签
                com.yueyunzhi.entity.ArticleTag tag = articleTagRepository.findByTagName(tagName.trim())
                        .orElseGet(() -> articleTagRepository.save(new com.yueyunzhi.entity.ArticleTag(tagName.trim())));
                
                // 创建文章-标签关联
                com.yueyunzhi.entity.ArticleTagMap tagMap = new com.yueyunzhi.entity.ArticleTagMap();
                com.yueyunzhi.entity.ArticleTagMapId tagMapId = new com.yueyunzhi.entity.ArticleTagMapId();
                tagMapId.setArticleId(savedArticle.getArticleId());
                tagMapId.setTagId(tag.getTagId());
                tagMap.setId(tagMapId);
                articleTagMapRepository.save(tagMap);
            }
        }
        
        return savedArticle;
    }
    
    @Transactional
    public Article updateArticle(Long id, Article article) {
        Article existingArticle = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        existingArticle.setTitle(article.getTitle());
        existingArticle.setContent(article.getContent());
        existingArticle.setExcerpt(article.getExcerpt());
        existingArticle.setCoverImage(article.getCoverImage());
        existingArticle.setCategoryId(article.getCategoryId());
        existingArticle.setIsFeatured(article.getIsFeatured());
        Article saved = articleRepository.save(existingArticle);
        articleCacheService.evict(id);
        return saved;
    }
    
    @Transactional
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
        articleCacheService.evict(id);
    }
    
    @Transactional
    public void incrementLikes(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        if (article.getLikeCount() == null) {
            article.setLikeCount(0);
        }
        article.setLikeCount(article.getLikeCount() + 1);
        articleRepository.save(article);
        articleCacheService.evict(id);
    }
    
    @Transactional
    public boolean toggleLike(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        boolean isLiked = articleLikeRepository.existsByUserIdAndArticleId(userId, articleId);
        
        if (isLiked) {
            articleLikeRepository.deleteByUserIdAndArticleId(userId, articleId);
            article.setLikeCount(article.getLikeCount() - 1);
        } else {
            ArticleLike articleLike = new ArticleLike(userId, articleId);
            articleLikeRepository.save(articleLike);
            article.setLikeCount(article.getLikeCount() + 1);
        }
        
        articleRepository.save(article);
        articleCacheService.evict(articleId);
        return !isLiked;
    }
    
    @Transactional
    public boolean toggleFavorite(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        boolean isFavorited = articleFavoriteRepository.existsByUserIdAndArticleId(userId, articleId);
        
        if (isFavorited) {
            articleFavoriteRepository.deleteByUserIdAndArticleId(userId, articleId);
            article.setFavoriteCount(article.getFavoriteCount() - 1);
        } else {
            ArticleFavorite articleFavorite = new ArticleFavorite(userId, articleId);
            articleFavoriteRepository.save(articleFavorite);
            article.setFavoriteCount(article.getFavoriteCount() + 1);
        }
        
        articleRepository.save(article);
        articleCacheService.evict(articleId);
        return !isFavorited;
    }
    
    public boolean isLiked(Long articleId, Long userId) {
        return articleLikeRepository.existsByUserIdAndArticleId(userId, articleId);
    }
    
    public boolean isFavorited(Long articleId, Long userId) {
        return articleFavoriteRepository.existsByUserIdAndArticleId(userId, articleId);
    }
    
    public List<ArticleWithDetailsDTO> getTop10ByViewCount() {
        try {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("viewCount").descending());
            List<ArticleWithDetailsDTO> articles = articleRepository.findTop10ByViewCount(pageable);
            System.out.println("获取到的阅读量排行榜数据: " + articles.size() + " 条");
            // 填充标签
            for (ArticleWithDetailsDTO article : articles) {
                List<String> tagNames = articleTagRepository.findTagNamesByArticleId(article.getArticleId());
                if (tagNames != null && !tagNames.isEmpty()) {
                    article.setTags(String.join(",", tagNames));
                }
            }
            return articles;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
    
    public List<ArticleWithDetailsDTO> getTop10ByLikeCount() {
        try {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("likeCount").descending());
            List<ArticleWithDetailsDTO> articles = articleRepository.findTop10ByLikeCount(pageable);
            System.out.println("获取到的点赞数排行榜数据: " + articles.size() + " 条");
            // 填充标签
            for (ArticleWithDetailsDTO article : articles) {
                List<String> tagNames = articleTagRepository.findTagNamesByArticleId(article.getArticleId());
                if (tagNames != null && !tagNames.isEmpty()) {
                    article.setTags(String.join(",", tagNames));
                }
            }
            return articles;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
    
    public List<ArticleWithDetailsDTO> getTop10ByCommentCount() {
        try {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("commentCount").descending());
            List<ArticleWithDetailsDTO> articles = articleRepository.findTop10ByCommentCount(pageable);
            System.out.println("获取到的评论数排行榜数据: " + articles.size() + " 条");
            // 填充标签
            for (ArticleWithDetailsDTO article : articles) {
                List<String> tagNames = articleTagRepository.findTagNamesByArticleId(article.getArticleId());
                if (tagNames != null && !tagNames.isEmpty()) {
                    article.setTags(String.join(",", tagNames));
                }
            }
            return articles;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
    
    public List<Object[]> getCategoryViewStats() {
        try {
            return articleRepository.findCategoryViewStats();
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
    
    public List<ArticleWithDetailsDTO> getRelatedArticles(Long articleId, Integer categoryId) {
        try {
            List<Integer> tagIds = articleTagRepository.findTagIdsByArticleId(articleId);
            List<ArticleWithDetailsDTO> relatedArticles;
            
            if (tagIds != null && !tagIds.isEmpty()) {
                Pageable pageable = PageRequest.of(0, 2);
                relatedArticles = articleRepository.findRelatedArticlesByCategoryAndTags(articleId, categoryId, tagIds, pageable);
                
                if (relatedArticles == null || relatedArticles.isEmpty()) {
                    relatedArticles = articleRepository.findRelatedArticlesByCategory(articleId, categoryId, pageable);
                }
            } else {
                Pageable pageable = PageRequest.of(0, 2);
                relatedArticles = articleRepository.findRelatedArticlesByCategory(articleId, categoryId, pageable);
            }
            
            if (relatedArticles == null) {
                return java.util.Collections.emptyList();
            }
            
            for (ArticleWithDetailsDTO article : relatedArticles) {
                List<String> tagNames = articleTagRepository.findTagNamesByArticleId(article.getArticleId());
                if (tagNames != null && !tagNames.isEmpty()) {
                    article.setTags(String.join(",", tagNames));
                }
            }
            
            return relatedArticles;
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Collections.emptyList();
        }
    }
}