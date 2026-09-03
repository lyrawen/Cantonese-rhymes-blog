package com.yueyunzhi.controller;

import com.yueyunzhi.entity.ArticleCategory;
import com.yueyunzhi.service.ArticleCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class ArticleCategoryController {

    @Autowired
    private ArticleCategoryService articleCategoryService;

    @GetMapping
    public ResponseEntity<List<ArticleCategory>> getAllCategories() {
        List<ArticleCategory> categories = articleCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
}