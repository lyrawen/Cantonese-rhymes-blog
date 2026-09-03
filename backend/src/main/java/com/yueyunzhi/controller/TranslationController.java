package com.yueyunzhi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@RestController
@RequestMapping("/api/translate")
public class TranslationController {

    private static final String BAIDU_API_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";
    private static final String BAIDU_APP_ID = "yueyunzhi";

    // 密钥通过配置文件注入，不入库（见 resources/application-secret.properties）
    @Value("${baidu.translate.api-key}")
    private String baiduApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public String translate(@RequestParam("q") String text) {
        try {
            String salt = String.valueOf(new Random().nextLong());
            String sign = generateSign(text, salt);
            
            // 对文本进行URL编码
            String encodedText = URLEncoder.encode(text, "UTF-8");
            
            String url = BAIDU_API_URL + "?q=" + encodedText + "&from=zh&to=yue&appid=" + BAIDU_APP_ID + "&salt=" + salt + "&sign=" + sign;
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"翻译失败\"}";
        }
    }

    private String generateSign(String query, String salt) throws NoSuchAlgorithmException {
        String str = BAIDU_APP_ID + query + salt + baiduApiKey;
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] bytes = md.digest(str.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
