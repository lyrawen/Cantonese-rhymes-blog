package com.yueyunzhi.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/speech")
public class SpeechController {

    private static final String BAIDU_SPEECH_API_URL = "https://tsn.baidu.com/text2audio";
    private static final String BAIDU_TOKEN_API_URL = "https://aip.baidubce.com/oauth/2.0/token";

    // 密钥通过配置文件注入，不入库（见 resources/application-secret.properties）
    @Value("${baidu.speech.api-key}")
    private String baiduApiKey;

    @Value("${baidu.speech.secret-key}")
    private String baiduSecretKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private String accessToken = null;
    private long tokenExpireTime = 0;

    @PostMapping("/synthesize")
    public ResponseEntity<byte[]> synthesize(@RequestBody SpeechRequest request) {
        try {
            // 获取文本和语言
            String text = request.getText();
            String lang = request.getLang() != null ? request.getLang() : "zh";
            
            // 限制文本长度，避免百度API的URL过长
            if (text.length() > 500) {
                text = text.substring(0, 500) + "...";
            }
            
            // 对文本进行URL编码
            String encodedText = URLEncoder.encode(text, "UTF-8");
            
            // 获取access_token
            String token = getAccessToken();
            
            if (token == null) {
                return ResponseEntity.status(500).body("Failed to get access token".getBytes());
            }
            
            // 构建请求URL，使用粤语语音人（20101为粤语男声）
            String url = BAIDU_SPEECH_API_URL + "?tex=" + encodedText + "&lan=" + lang + "&cuid=yueyunzhi&ctp=1&tok=" + token + "&per=20101&spd=5&pit=5&vol=5&aue=3";
            
            // 调用API获取语音数据
            byte[] audioData = restTemplate.getForObject(url, byte[].class);
            
            if (audioData == null || audioData.length == 0) {
                return ResponseEntity.status(500).body("Failed to synthesize speech".getBytes());
            }
            
            // 设置响应头，指定内容类型为音频
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentLength(audioData.length);
            
            return ResponseEntity.ok().headers(headers).body(audioData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(e.getMessage().getBytes());
        }
    }

    // 语音请求类
    private static class SpeechRequest {
        private String text;
        private String lang;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }
    }

    private synchronized String getAccessToken() throws IOException {
        // 检查token是否有效
        if (accessToken == null || System.currentTimeMillis() > tokenExpireTime) {
            // 构建token请求URL
            String url = BAIDU_TOKEN_API_URL + "?grant_type=client_credentials&client_id=" + baiduApiKey + "&client_secret=" + baiduSecretKey;
            
            // 调用API获取token
            TokenResponse response = restTemplate.getForObject(url, TokenResponse.class);
            
            if (response != null) {
                accessToken = response.getAccess_token();
                // 设置过期时间（提前10分钟）
                tokenExpireTime = System.currentTimeMillis() + (response.getExpires_in() - 600) * 1000;
                System.out.println("Got access token: " + accessToken);
            } else {
                System.err.println("Failed to get access token");
            }
        }
        return accessToken;
    }

    // Token响应类
    private static class TokenResponse {
        private String access_token;
        private int expires_in;

        public String getAccess_token() {
            return access_token;
        }

        public void setAccess_token(String access_token) {
            this.access_token = access_token;
        }

        public int getExpires_in() {
            return expires_in;
        }

        public void setExpires_in(int expires_in) {
            this.expires_in = expires_in;
        }
    }
}
