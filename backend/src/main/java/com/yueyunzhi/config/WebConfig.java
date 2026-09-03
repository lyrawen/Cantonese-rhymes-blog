package com.yueyunzhi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:D:/粤韵志/backend/uploads/");
        
        // 配置头像的访问路径 - 使用classpath协议
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("classpath:/avatars/");
        
        // 配置封面照片的访问路径
        registry.addResourceHandler("/profile_covers/**")
                .addResourceLocations("file:D:/粤韵志/backend/uploads/profile_covers/");
    }
}
