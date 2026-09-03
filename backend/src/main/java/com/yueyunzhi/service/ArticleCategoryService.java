package com.yueyunzhi.service;

import com.yueyunzhi.entity.ArticleCategory;
import com.yueyunzhi.repository.ArticleCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ArticleCategoryService {

    @Autowired
    private ArticleCategoryRepository articleCategoryRepository;

    public List<ArticleCategory> getAllCategories() {
        return articleCategoryRepository.findAllByOrderBySortOrderAsc();
    }
}