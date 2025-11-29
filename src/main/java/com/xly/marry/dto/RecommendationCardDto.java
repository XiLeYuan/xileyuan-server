package com.xly.marry.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationCardDto {
    
    private Long id;
    private Long recommendedUserId;
    private Integer matchScore;
    private List<String> matchReasons;
    private String status;
    private String userAction;
    private Boolean isViewed;
    private LocalDateTime viewedAt;
    private LocalDateTime actionAt;
    private LocalDateTime createdAt;
    
    // 被推荐用户的详细信息
    private UserCardInfo recommendedUser;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCardInfo {
        private Long id;
        private String nickname;
        private String avatarUrl;
        private String gender;
        private Integer age;
        private Integer height;
        private String educationLevel;
        private String occupation;
        private String currentCity;
        private String hometownCity;
        private String houseStatus;
        private String carStatus;
        private String maritalStatus;
        private String selfIntroduction;
        private String lifestyle;
        private String idealPartner;
        private List<String> userTags;
        private List<String> lifePhotos;
        private Integer photoCount;
        private Boolean isVerified;
        private Boolean isRealNameVerified;
    }
}

// 用户操作请求DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
class CardActionRequest {
    private Long cardId;
    private String action; // LIKE, PASS, SUPER_LIKE
    private String message; // 可选的消息
}

// 推荐偏好设置DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
class RecommendationPreferenceDto {
    private Integer minAge;
    private Integer maxAge;
    private String preferredGender;
    private Integer minHeight;
    private Integer maxHeight;
    private List<String> preferredCities;
    private List<String> educationRequirements;
    private List<String> incomeRequirements;
    private String hasChildrenPreference;
    private Integer maxDistance; // 最大距离（公里）
    private Boolean onlyVerifiedUsers; // 只看实名认证用户
}

// 推荐统计DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
class RecommendationStatsDto {
    private Integer totalCards;
    private Integer likedCards;
    private Integer passedCards;
    private Integer matchedCards;
    private Integer remainingCards;
    private Double averageMatchScore;
    private LocalDateTime lastRecommendationTime;
} 