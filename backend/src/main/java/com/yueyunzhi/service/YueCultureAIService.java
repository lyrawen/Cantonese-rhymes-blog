package com.yueyunzhi.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class YueCultureAIService {
    
    @Value("${deepseek.api.key}")
    private String apiKey;
    
    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String apiUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 核心系统Prompt
    private static final String SYSTEM_PROMPT = """
        你是「粤韵志」文化博客的AI助手「粤韵AI」，一个精通粤文化的专家。
        
        你的知识库涵盖：
        1. 粤剧艺术（历史、名剧、名伶、唱腔、行当）
        2. 粤菜文化（名菜典故、烹饪技法、饮食习俗、老字号）
        3. 粤语方言（俚语俗语、歇后语、语法特点）
        4. 岭南民俗（传统节日、婚丧嫁娶、民间信仰）
        5. 广东音乐（曲目、乐器、代表人物）
        6. 广府建筑（镬耳屋、骑楼、西关大屋）
        7. 工艺美术（广绣、广彩、牙雕）
        8. 粤商文化（老字号、商帮历史）
        
        回答原则：
        1. 准确：不确定时明确说明
        2. 生动：多用典故、趣闻
        3. 亲切：可适当用粤语词（需解释）
        4. 完整：提供背景、细节、延伸
        
        回答结构（灵活运用）：
        - 开篇：直接回应核心问题
        - 展开：背景故事、细节知识
        - 延伸：相关话题、阅读推荐
        - 互动：询问是否需要深入了解
        
        边界处理：
        - 非粤文化问题：礼貌引导回主题
        - 争议内容：呈现不同说法
        - 超出范围：坦诚说明，建议查阅粤韵志网站
        """;
    
    /**
     * 通用问答接口
     */
    public QAResponse askQuestion(String question, String category) {
        try {
            // 根据问题类别选择场景Prompt
            String scenePrompt = getScenePrompt(question, category);
            
            // 构建消息列表
            List<Map<String, String>> messages = Arrays.asList(
                Map.of("role", "system", "content", SYSTEM_PROMPT),
                Map.of("role", "system", "content", scenePrompt),
                Map.of("role", "user", "content", question)
            );
            
            // 调用DeepSeek API
            String answer = callDeepSeekAPI(messages);
            
            // 提取可能的相关文章推荐
            List<String> relatedArticles = extractArticleRecommendations(answer);
            
            return QAResponse.builder()
                .question(question)
                .answer(answer)
                .relatedArticles(relatedArticles)
                .timestamp(new Date())
                .build();
            
        } catch (Exception e) {
            log.error("AI问答失败", e);
            return QAResponse.builder()
                .question(question)
                .answer("抱歉，AI服务暂时遇到问题。建议您直接浏览粤韵志网站的相关文章，或稍后再试。")
                .error(e.getMessage())
                .build();
        }
    }
    
    /**
     * 流式问答接口（用于打字机效果）
     */
    public String askQuestionStream(String question) {
        try {
            // 根据问题内容智能判断场景
            String scenePrompt = getScenePrompt(question, "");
            
            // 构建消息列表
            List<Map<String, String>> messages = Arrays.asList(
                Map.of("role", "system", "content", SYSTEM_PROMPT),
                Map.of("role", "system", "content", scenePrompt),
                Map.of("role", "user", "content", question)
            );
            
            // 调用DeepSeek API
            return callDeepSeekAPI(messages);
            
        } catch (Exception e) {
            log.error("AI流式问答失败", e);
            return "抱歉，AI服务暂时遇到问题。建议您直接浏览粤韵志网站的相关文章，或稍后再试。";
        }
    }
    
    /**
     * 获取场景化Prompt
     */
    private String getScenePrompt(String question, String category) {
        // 根据问题内容智能判断场景
        String lowerQuestion = question.toLowerCase();
        
        if (containsAny(lowerQuestion, "粤剧", "帝女花", "紫钗记", "牡丹亭", "任剑辉", "红线女")) {
            return getOperaPrompt();
        } else if (containsAny(lowerQuestion, "粤菜", "烧鹅", "点心", "煲仔饭", "老火汤", "镬气")) {
            return getCuisinePrompt();
        } else if (containsAny(lowerQuestion, "粤语", "白话", "俚语", "俗语", "歇后语")) {
            return getCantonesePrompt();
        } else if (containsAny(lowerQuestion, "节日", "过年", "端午", "中秋", "清明", "冬至")) {
            return getFestivalPrompt();
        } else if (containsAny(lowerQuestion, "建筑", "骑楼", "西关", "镬耳", "祠堂")) {
            return getArchitecturePrompt();
        } else {
            // 默认通用Prompt
            return getGeneralPrompt();
        }
    }
    
    /**
     * 调用DeepSeek API
     */
    private String callDeepSeekAPI(List<Map<String, String>> messages) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat");
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2000);
            requestBody.put("top_p", 0.9);
            requestBody.put("frequency_penalty", 0.3);
            requestBody.put("presence_penalty", 0.3);
            
            // 添加一些参数让回答更生动
            requestBody.put("stop", null);  // 不设停止词
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                apiUrl, request, String.class
            );
            
            JsonNode root = objectMapper.readTree(response.getBody());
            String content = root.path("choices").get(0)
                               .path("message").path("content")
                               .asText();
            
            return content;
            
        } catch (Exception e) {
            log.error("DeepSeek API调用失败", e);
            throw new RuntimeException("AI服务调用失败", e);
        }
    }
    
    /**
     * 从回答中提取推荐文章
     */
    private List<String> extractArticleRecommendations(String answer) {
        List<String> recommendations = new ArrayList<>();
        
        // 查找"粤韵志"相关的推荐
        int index = answer.indexOf("粤韵志");
        while (index != -1) {
            int endIndex = answer.indexOf("。", index);
            if (endIndex != -1 && endIndex - index < 50) {
                recommendations.add(answer.substring(index, endIndex + 1));
            }
            index = answer.indexOf("粤韵志", index + 1);
        }
        
        return recommendations;
    }
    
    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    // ==================== 场景化Prompts ====================
    
    private String getOperaPrompt() {
        return """
            【粤剧问答场景】
            
            回答要求：
            1. 介绍剧目时需包含：编剧、首演时间、剧情梗概、经典版本
            2. 提到名伶时需说明：所属行当、代表作、艺术特色
            3. 解释唱腔时需说明：梆子/二黄区别、代表人物
            
            风格特色：
            - 可引用经典唱词（附解释）
            - 可讲梨园趣闻
            - 可对比不同流派版本
            
            示例开头：
            "讲到粤剧《帝女花》，就不得不提唐涤生和任白..."
            """;
    }
    
    private String getCuisinePrompt() {
        return """
            【粤菜问答场景】
            
            回答要求：
            1. 介绍菜品时需包含：起源故事、选材讲究、烹饪精髓
            2. 提到老字号时需说明：创始年份、招牌菜、掌故
            3. 解释技法时需说明：科学原理、文化内涵
            
            风格特色：
            - 可引用美食家评价（如蔡澜、韬韬）
            - 可讲名人与美食的故事
            - 可推荐品鉴搭配
            
            示例开头：
            "讲到烧鹅，广州人第一个想到的就是'江记'..."
            """;
    }
    
    private String getCantonesePrompt() {
        return """
            【粤语问答场景】
            
            回答要求：
            1. 解释词语时需包含：本义、用法、例句、情感色彩
            2. 说明来源时需考证：古汉语留存、外来词、新造词
            3. 对比差异时需说明：广式/港式区别
            
            风格特色：
            - 可引用经典影视对白
            - 可讲词语背后的生活智慧
            - 可对比普通话表达
            
            示例开头：
            "'盏鬼'这个词真系好盏鬼！佢既意思系..."
            """;
    }
    
    private String getFestivalPrompt() {
        return """
            【岭南节庆问答场景】
            
            回答要求：
            1. 介绍节日时需包含：起源传说、传统习俗、特色食品
            2. 说明禁忌时需解释：文化心理、民间信仰
            3. 提到仪式时需描述：流程、寓意、现状
            
            风格特色：
            - 可引用广府童谣
            - 可讲儿时回忆
            - 可对比不同地区习俗
            
            示例开头：
            "广州人过年，最讲究'意头'..."
            """;
    }
    
    private String getArchitecturePrompt() {
        return """
            【广府建筑问答场景】
            
            回答要求：
            1. 介绍建筑时需包含：结构特点、功能用途、文化象征
            2. 说明工艺时需描述：材料、技法、工匠
            3. 提到老街时需讲述：历史变迁、名人故居、现状保护
            
            风格特色：
            - 可引用建筑术语并解释
            - 可讲风水讲究
            - 可推荐探访路线
            
            示例开头：
            "西关大屋最能体现广府人的居住智慧..."
            """;
    }
    
    private String getGeneralPrompt() {
        return """
            【通用粤文化问答场景】
            
            回答要求：
            1. 先确认问题属于哪个粤文化领域
            2. 提供核心信息 + 2-3个有趣细节
            3. 最后可问"您想深入了解某个方面吗？"
            
            风格特色：
            - 保持专业但亲切
            - 适当使用"犀利"、"好嘢"等粤语词（解释）
            - 可推荐粤韵志相关文章
            
            示例开头：
            "您问的这个问题好犀利！等我慢慢同你讲..."
            """;
    }
    
    // ==================== 响应类 ====================
    
    @lombok.Data
    @lombok.Builder
    public static class QAResponse {
        private String question;
        private String answer;
        private List<String> relatedArticles;
        private Date timestamp;
        private String error;
    }
}