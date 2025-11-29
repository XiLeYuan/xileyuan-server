package com.xly.marry.repository;

import com.xly.marry.entity.RecommendationCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationCardRepository extends JpaRepository<RecommendationCard, Long> {
    
    // 查找用户的所有推荐卡片
    Page<RecommendationCard> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    // 查找用户未处理的推荐卡片
    List<RecommendationCard> findByUserIdAndStatusOrderByMatchScoreDesc(Long userId, RecommendationCard.CardStatus status);
    
    // 查找用户已喜欢的推荐卡片
    Page<RecommendationCard> findByUserIdAndUserActionOrderByActionAtDesc(Long userId, RecommendationCard.UserAction userAction, Pageable pageable);
    
    // 查找用户已查看的推荐卡片
    Page<RecommendationCard> findByUserIdAndIsViewedTrueOrderByViewedAtDesc(Long userId, Pageable pageable);
    
    // 查找用户未查看的推荐卡片
    List<RecommendationCard> findByUserIdAndIsViewedFalseOrderByCreatedAtAsc(Long userId);
    
    // 查找特定推荐卡片
    Optional<RecommendationCard> findByUserIdAndRecommendedUserId(Long userId, Long recommendedUserId);
    
    // 检查是否已经推荐过某个用户
    boolean existsByUserIdAndRecommendedUserId(Long userId, Long recommendedUserId);
    
    // 统计用户推荐卡片数量
    @Query("SELECT COUNT(rc) FROM RecommendationCard rc WHERE rc.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    // 统计用户已喜欢的推荐卡片数量
    @Query("SELECT COUNT(rc) FROM RecommendationCard rc WHERE rc.userId = :userId AND rc.userAction = 'LIKE'")
    long countLikedByUserId(@Param("userId") Long userId);
    
    // 统计用户已跳过的推荐卡片数量
    @Query("SELECT COUNT(rc) FROM RecommendationCard rc WHERE rc.userId = :userId AND rc.userAction = 'PASS'")
    long countPassedByUserId(@Param("userId") Long userId);
    
    // 统计用户已匹配的推荐卡片数量
    @Query("SELECT COUNT(rc) FROM RecommendationCard rc WHERE rc.userId = :userId AND rc.status = 'MATCHED'")
    long countMatchedByUserId(@Param("userId") Long userId);
    
    // 统计用户未处理的推荐卡片数量
    @Query("SELECT COUNT(rc) FROM RecommendationCard rc WHERE rc.userId = :userId AND rc.status = 'PENDING'")
    long countPendingByUserId(@Param("userId") Long userId);
    
    // 计算平均匹配分数
    @Query("SELECT AVG(rc.matchScore) FROM RecommendationCard rc WHERE rc.userId = :userId")
    Double getAverageMatchScoreByUserId(@Param("userId") Long userId);
    
    // 查找最近推荐的卡片
    @Query("SELECT rc FROM RecommendationCard rc WHERE rc.userId = :userId ORDER BY rc.createdAt DESC")
    List<RecommendationCard> findRecentRecommendationsByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 查找高匹配度的推荐卡片
    @Query("SELECT rc FROM RecommendationCard rc WHERE rc.userId = :userId AND rc.matchScore >= :minScore ORDER BY rc.matchScore DESC")
    List<RecommendationCard> findHighMatchScoreRecommendations(@Param("userId") Long userId, @Param("minScore") Integer minScore);
    
    // 查找特定时间范围内的推荐卡片
    @Query("SELECT rc FROM RecommendationCard rc WHERE rc.userId = :userId AND rc.createdAt BETWEEN :startTime AND :endTime")
    List<RecommendationCard> findByUserIdAndCreatedAtBetween(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 删除用户的过期推荐卡片（超过30天未处理的）
    @Query("DELETE FROM RecommendationCard rc WHERE rc.userId = :userId AND rc.status = 'PENDING' AND rc.createdAt < :expireTime")
    void deleteExpiredRecommendations(@Param("userId") Long userId, @Param("expireTime") LocalDateTime expireTime);
} 