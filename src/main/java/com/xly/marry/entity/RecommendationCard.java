package com.xly.marry.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation_cards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationCard {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // 推荐给谁（当前用户）
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    // 被推荐的人
    @Column(name = "recommended_user_id", nullable = false)
    private Long recommendedUserId;
    
    // 匹配度分数 (0-100)
    @Column(name = "match_score")
    private Integer matchScore;
    
    // 推荐原因标签
    @Column(name = "match_reasons", columnDefinition = "TEXT")
    private String matchReasons; // JSON格式存储匹配原因
    
    // 推荐状态
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CardStatus status = CardStatus.PENDING;
    
    // 用户操作
    @Column(name = "user_action")
    @Enumerated(EnumType.STRING)
    private UserAction userAction;
    
    // 是否已查看
    @Column(name = "is_viewed")
    private Boolean isViewed = false;
    
    // 查看时间
    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;
    
    // 操作时间
    @Column(name = "action_at")
    private LocalDateTime actionAt;
    
    // 创建时间
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    // 更新时间
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 关联被推荐用户信息（非持久化）
    @Transient
    private User recommendedUser;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 枚举类
    public enum CardStatus {
        PENDING("待处理"),
        LIKED("已喜欢"),
        PASSED("已跳过"),
        MATCHED("已匹配");
        
        private final String displayName;
        
        CardStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum UserAction {
        LIKE("喜欢"),
        PASS("跳过"),
        SUPER_LIKE("超级喜欢");
        
        private final String displayName;
        
        UserAction(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
} 