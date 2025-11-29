package com.xly.marry.controller;

import com.xly.marry.dto.ApiResponse;
import com.xly.marry.dto.RecommendationCardDto;
import com.xly.marry.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "*")
public class RecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    /**
     * 获取下一个推荐卡片
     */
    @GetMapping("/next")
    public ResponseEntity<ApiResponse<RecommendationCardDto>> getNextRecommendation(@RequestParam Long userId) {
        try {
            RecommendationCardDto card = recommendationService.getNextRecommendation(userId);
            if (card != null) {
                return ResponseEntity.ok(ApiResponse.success("获取推荐卡片成功", card));
            } else {
                return ResponseEntity.ok(ApiResponse.success("暂无更多推荐", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 用户操作推荐卡片（喜欢/跳过/超级喜欢）
     */
    @PostMapping("/action")
    public ResponseEntity<ApiResponse<String>> handleCardAction(
            @RequestParam Long userId,
            @RequestParam Long cardId,
            @RequestParam String action) {
        try {
            recommendationService.handleCardAction(userId, cardId, action);
            return ResponseEntity.ok(ApiResponse.success("操作成功", "操作已记录"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 标记推荐卡片为已查看
     */
    @PostMapping("/view")
    public ResponseEntity<ApiResponse<String>> markAsViewed(
            @RequestParam Long userId,
            @RequestParam Long cardId) {
        try {
            recommendationService.markAsViewed(userId, cardId);
            return ResponseEntity.ok(ApiResponse.success("标记成功", "已标记为查看"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取推荐统计信息
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRecommendationStats(@RequestParam Long userId) {
        try {
            // 这里应该调用服务获取统计数据
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCards", 100);
            stats.put("likedCards", 25);
            stats.put("passedCards", 50);
            stats.put("matchedCards", 5);
            stats.put("remainingCards", 20);
            stats.put("averageMatchScore", 75.5);
            
            return ResponseEntity.ok(ApiResponse.success("获取统计信息成功", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 生成新的推荐卡片
     */
    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<String>> generateRecommendations(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            // 这里应该调用服务生成推荐
            return ResponseEntity.ok(ApiResponse.success("生成推荐成功", "已生成" + limit + "个推荐"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取用户喜欢的推荐卡片列表
     */
    @GetMapping("/liked")
    public ResponseEntity<ApiResponse<Object>> getLikedRecommendations(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            // 这里应该调用服务获取喜欢的推荐列表
            Map<String, Object> result = new HashMap<>();
            result.put("page", page);
            result.put("size", size);
            result.put("total", 25);
            result.put("data", new Object[0]); // 实际应该是推荐卡片列表
            
            return ResponseEntity.ok(ApiResponse.success("获取喜欢的推荐成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * 获取匹配成功的推荐卡片列表
     */
    @GetMapping("/matches")
    public ResponseEntity<ApiResponse<Object>> getMatches(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            // 这里应该调用服务获取匹配列表
            Map<String, Object> result = new HashMap<>();
            result.put("page", page);
            result.put("size", size);
            result.put("total", 5);
            result.put("data", new Object[0]); // 实际应该是匹配列表
            
            return ResponseEntity.ok(ApiResponse.success("获取匹配列表成功", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
} 