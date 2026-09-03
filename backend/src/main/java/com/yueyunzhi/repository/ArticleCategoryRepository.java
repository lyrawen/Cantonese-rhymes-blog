package com.yueyunzhi.repository;

import com.yueyunzhi.entity.ArticleCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, Integer> {
    List<ArticleCategory> findAllByOrderBySortOrderAsc();
}