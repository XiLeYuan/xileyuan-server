package com.xly.marry.controller;

import com.xly.marry.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class TagController {
    
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<Map<String, List<Map<String, String>>>>> getTagCategories() {
        Map<String, List<Map<String, String>>> categories = new HashMap<>();
        
        // 用户标签（我想认识的人）
        List<Map<String, String>> userTags = Arrays.asList(
            createTag("PERSISTENT_HOBBY", "有坚持很久的爱好"),
            createTag("LOVE_SPORTS", "爱运动"),
            createTag("HUMOROUS", "有幽默感"),
            createTag("SUNNY", "阳光开朗"),
            createTag("EASYGOING", "性格随和"),
            createTag("FAMILY_ORIENTED", "享受家庭时光"),
            createTag("CAREER_PLANNED", "对职业有规划"),
            createTag("MATURE_RATIONAL", "成熟理智"),
            createTag("HAPPY_DAILY", "每天都很开心"),
            createTag("ROMANTIC", "感性浪漫"),
            createTag("INDEPENDENT", "有自己的主见和想法"),
            createTag("CAREFUL", "比较细心"),
            createTag("GOOD_FIGURE", "身材匀称"),
            createTag("GOOD_LOOKING", "有颜值"),
            createTag("GOOD_DRESSING", "衣品好"),
            createTag("SINCERE", "真诚重感情"),
            createTag("LOGICAL", "逻辑思维强"),
            createTag("PROGRESSIVE", "有追求能一起进步"),
            createTag("IMAGINATIVE", "想象力丰富"),
            createTag("KIND", "善良有爱心"),
            createTag("GOOD_HABITS", "生活习惯好"),
            createTag("STABLE_LIFE", "想过安稳生活"),
            createTag("ECONOMIC_ABILITY", "有一定经济能力"),
            createTag("LIFE_PURSUIT", "对生活有追求"),
            createTag("GOOD_EDUCATION", "有良好学历背景"),
            createTag("ENJOY_LIFE", "会享受生活"),
            createTag("COMMON_HOBBIES", "有共同爱好"),
            createTag("GOOD_COMMUNICATION", "善于沟通，会换位思考"),
            createTag("LOVE_OVER_MATERIAL", "相比物质更看重爱情"),
            createTag("GOOD_CONVERSATION", "聊得来"),
            createTag("LESS_OVERTIME", "加班少")
        );
        
        // 期望伴侣标签（你希望她有哪些特征）
        List<Map<String, String>> preferredTags = Arrays.asList(
            createTag("GOOD_TEMPERAMENT", "气质好"),
            createTag("FREE_SPIRITED", "自由随性"),
            createTag("OUTGOING", "外向开朗"),
            createTag("EMOTIONALLY_STABLE", "情绪稳定"),
            createTag("KIND_HEARTED", "善良"),
            createTag("HIGH_LIFE_QUALITY", "生活品质高"),
            createTag("OPTIMISTIC", "乐观"),
            createTag("TALL_GIRL", "高个子女生"),
            createTag("PROGRAMMER", "程序员"),
            createTag("SUNSHINE", "阳光"),
            createTag("HEALTHY_LIFE", "健康生活"),
            createTag("LIFE_ATMOSPHERE", "有生活气息"),
            createTag("SELF_DISCIPLINED", "自律"),
            createTag("STABLE_FAMILY", "稳定顾家"),
            createTag("INTERNET_INDUSTRY", "互联网行业"),
            createTag("INTELLIGENT", "聪明"),
            createTag("RATIONAL", "理性"),
            createTag("SMILEY", "爱笑"),
            createTag("URBAN_WHITE_COLLAR", "都市白领"),
            createTag("LOVE_SPORTS_DAILY", "平时爱运动"),
            createTag("RICH_DAILY_LIFE", "日常生活丰富"),
            createTag("LOVE_TRAVEL", "喜欢旅行"),
            createTag("GOOD_COOKING", "厨艺好"),
            createTag("LIFE_SENTIMENT", "有生活情调"),
            createTag("SIMPLE_CIRCLE", "圈子简单"),
            createTag("OUTDOOR_SPORTS", "户外运动"),
            createTag("INNOCENT", "单纯"),
            createTag("SYSTEM_WORKER", "体制内"),
            createTag("TOP_UNIVERSITY", "名校毕业"),
            createTag("HIGH_EDUCATION", "高学历"),
            createTag("LOVE_READING", "爱读书"),
            createTag("FOOD_DRINK", "吃吃喝喝"),
            createTag("BROAD_VISION", "视野开阔"),
            createTag("INTERESTING", "有趣"),
            createTag("HUMOROUS_PERSON", "有幽默感"),
            createTag("SUCCESSFUL_CAREER", "事业有成"),
            createTag("STRONG_WORK_ABILITY", "工作能力强"),
            createTag("SYSTEM_INTERNAL", "体制内"),
            createTag("LONG_TERM_HOBBY", "有长期爱好")
        );
        
        categories.put("userTags", userTags);
        categories.put("preferredTags", preferredTags);
        
        return ResponseEntity.ok(ApiResponse.success("获取标签分类成功", categories));
    }
    
    @GetMapping("/user-tags")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getUserTags() {
        List<Map<String, String>> userTags = Arrays.asList(
            createTag("PERSISTENT_HOBBY", "有坚持很久的爱好"),
            createTag("LOVE_SPORTS", "爱运动"),
            createTag("HUMOROUS", "有幽默感"),
            createTag("SUNNY", "阳光开朗"),
            createTag("EASYGOING", "性格随和"),
            createTag("FAMILY_ORIENTED", "享受家庭时光"),
            createTag("CAREER_PLANNED", "对职业有规划"),
            createTag("MATURE_RATIONAL", "成熟理智"),
            createTag("HAPPY_DAILY", "每天都很开心"),
            createTag("ROMANTIC", "感性浪漫"),
            createTag("INDEPENDENT", "有自己的主见和想法"),
            createTag("CAREFUL", "比较细心"),
            createTag("GOOD_FIGURE", "身材匀称"),
            createTag("GOOD_LOOKING", "有颜值"),
            createTag("GOOD_DRESSING", "衣品好"),
            createTag("SINCERE", "真诚重感情"),
            createTag("LOGICAL", "逻辑思维强"),
            createTag("PROGRESSIVE", "有追求能一起进步"),
            createTag("IMAGINATIVE", "想象力丰富"),
            createTag("KIND", "善良有爱心"),
            createTag("GOOD_HABITS", "生活习惯好"),
            createTag("STABLE_LIFE", "想过安稳生活"),
            createTag("ECONOMIC_ABILITY", "有一定经济能力"),
            createTag("LIFE_PURSUIT", "对生活有追求"),
            createTag("GOOD_EDUCATION", "有良好学历背景"),
            createTag("ENJOY_LIFE", "会享受生活"),
            createTag("COMMON_HOBBIES", "有共同爱好"),
            createTag("GOOD_COMMUNICATION", "善于沟通，会换位思考"),
            createTag("LOVE_OVER_MATERIAL", "相比物质更看重爱情"),
            createTag("GOOD_CONVERSATION", "聊得来"),
            createTag("LESS_OVERTIME", "加班少")
        );
        
        return ResponseEntity.ok(ApiResponse.success("获取用户标签成功", userTags));
    }
    
    @GetMapping("/preferred-tags")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getPreferredTags() {
        List<Map<String, String>> preferredTags = Arrays.asList(
            createTag("GOOD_TEMPERAMENT", "气质好"),
            createTag("FREE_SPIRITED", "自由随性"),
            createTag("OUTGOING", "外向开朗"),
            createTag("EMOTIONALLY_STABLE", "情绪稳定"),
            createTag("KIND_HEARTED", "善良"),
            createTag("HIGH_LIFE_QUALITY", "生活品质高"),
            createTag("OPTIMISTIC", "乐观"),
            createTag("TALL_GIRL", "高个子女生"),
            createTag("PROGRAMMER", "程序员"),
            createTag("SUNSHINE", "阳光"),
            createTag("HEALTHY_LIFE", "健康生活"),
            createTag("LIFE_ATMOSPHERE", "有生活气息"),
            createTag("SELF_DISCIPLINED", "自律"),
            createTag("STABLE_FAMILY", "稳定顾家"),
            createTag("INTERNET_INDUSTRY", "互联网行业"),
            createTag("INTELLIGENT", "聪明"),
            createTag("RATIONAL", "理性"),
            createTag("SMILEY", "爱笑"),
            createTag("URBAN_WHITE_COLLAR", "都市白领"),
            createTag("LOVE_SPORTS_DAILY", "平时爱运动"),
            createTag("RICH_DAILY_LIFE", "日常生活丰富"),
            createTag("LOVE_TRAVEL", "喜欢旅行"),
            createTag("GOOD_COOKING", "厨艺好"),
            createTag("LIFE_SENTIMENT", "有生活情调"),
            createTag("SIMPLE_CIRCLE", "圈子简单"),
            createTag("OUTDOOR_SPORTS", "户外运动"),
            createTag("INNOCENT", "单纯"),
            createTag("SYSTEM_WORKER", "体制内"),
            createTag("TOP_UNIVERSITY", "名校毕业"),
            createTag("HIGH_EDUCATION", "高学历"),
            createTag("LOVE_READING", "爱读书"),
            createTag("FOOD_DRINK", "吃吃喝喝"),
            createTag("BROAD_VISION", "视野开阔"),
            createTag("INTERESTING", "有趣"),
            createTag("HUMOROUS_PERSON", "有幽默感"),
            createTag("SUCCESSFUL_CAREER", "事业有成"),
            createTag("STRONG_WORK_ABILITY", "工作能力强"),
            createTag("SYSTEM_INTERNAL", "体制内"),
            createTag("LONG_TERM_HOBBY", "有长期爱好")
        );
        
        return ResponseEntity.ok(ApiResponse.success("获取期望伴侣标签成功", preferredTags));
    }
    
    private Map<String, String> createTag(String code, String name) {
        Map<String, String> tag = new HashMap<>();
        tag.put("code", code);
        tag.put("name", name);
        return tag;
    }
} 