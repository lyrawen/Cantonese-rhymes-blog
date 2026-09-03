package com.yueyunzhi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    /**
     * 逗号分隔的允许跨域来源，如 http://localhost:4000,https://yueyunzhi.com
     */
    private String allowedOrigins = "http://localhost:4000";

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedOriginList() {
        if (allowedOrigins == null || allowedOrigins.isBlank()) {
            return List.of();
        }
        return Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public String[] getAllowedOriginArray() {
        return getAllowedOriginList().toArray(new String[0]);
    }
}
