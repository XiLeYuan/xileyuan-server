package com.xly.marry.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xly.marry.dto.RecommendationCardDto;
import com.xly.marry.entity.RecommendationCard;
import com.xly.marry.entity.User;
import com.xly.marry.repository.RecommendationCardRepository;
import com.xly.marry.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RecommendationService {
    
    @Autowired
    private RecommendationCardRepository recommendationCardRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 获取下一个推荐卡片
     */
    public RecommendationCardDto getNextRecommendation(Long userId) {
        List<RecommendationCard> pendingCards = recommendationCardRepository
                .findByUserIdAndStatusOrderByMatchScoreDesc(userId, RecommendationCard.CardStatus.PENDING);
        
        if (pendingCards.isEmpty()) {
            return null;
        }
        
        RecommendationCard card = pendingCards.get(0);
        return convertToDto(card);
    }
    
    /**
     * 获取推荐卡片列表（分页）
     */
    public Page<RecommendationCardDto> getRecommendationList(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RecommendationCard> cards = recommendationCardRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        return cards.map(this::convertToDto);
    }
    
    /**
     * 获取用户喜欢的推荐卡片列表（分页）
     */
    public Page<RecommendationCardDto> getLikedRecommendations(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RecommendationCard> cards = recommendationCardRepository
                .findByUserIdAndUserActionOrderByActionAtDesc(userId, RecommendationCard.UserAction.LIKE, pageable);
        
        return cards.map(this::convertToDto);
    }
    
    /**
     * 获取匹配成功的推荐卡片列表（分页）
     */
    public Page<RecommendationCardDto> getMatchedRecommendations(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<RecommendationCard> matchedCards = recommendationCardRepository
                .findByUserIdAndStatusOrderByMatchScoreDesc(userId, RecommendationCard.CardStatus.MATCHED);
        
        // 手动分页处理
        int start = page * size;
        int end = Math.min(start + size, matchedCards.size());
        
        List<RecommendationCard> pageContent = matchedCards.subList(start, end);
        
        // 创建分页结果
        Page<RecommendationCard> pageResult = new org.springframework.data.domain.PageImpl<>(
            pageContent, pageable, matchedCards.size()
        );
        
        return pageResult.map(this::convertToDto);
    }
    
    /**
     * 用户操作推荐卡片
     */
    public void handleCardAction(Long userId, Long cardId, String action) {
        Optional<RecommendationCard> cardOpt = recommendationCardRepository.findById(cardId);
        if (cardOpt.isPresent()) {
            RecommendationCard card = cardOpt.get();
            card.setUserAction(RecommendationCard.UserAction.valueOf(action));
            card.setActionAt(LocalDateTime.now());
            
            // 处理匹配逻辑
            if ("LIKE".equals(action) || "SUPER_LIKE".equals(action)) {
                handleLikeAction(card);
            }
            
            recommendationCardRepository.save(card);
        }
    }
    
    /**
     * 标记卡片为已查看
     */
    public void markAsViewed(Long userId, Long cardId) {
        Optional<RecommendationCard> cardOpt = recommendationCardRepository.findById(cardId);
        if (cardOpt.isPresent()) {
            RecommendationCard card = cardOpt.get();
            card.setIsViewed(true);
            card.setViewedAt(LocalDateTime.now());
            recommendationCardRepository.save(card);
        }
    }
    
    /**
     * 获取推荐统计信息
     */
    public Map<String, Object> getRecommendationStats(Long userId) {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        stats.put("totalCards", recommendationCardRepository.countByUserId(userId));
        stats.put("likedCards", recommendationCardRepository.countLikedByUserId(userId));
        stats.put("passedCards", recommendationCardRepository.countPassedByUserId(userId));
        stats.put("matchedCards", recommendationCardRepository.countMatchedByUserId(userId));
        stats.put("remainingCards", recommendationCardRepository.countPendingByUserId(userId));
        stats.put("averageMatchScore", recommendationCardRepository.getAverageMatchScoreByUserId(userId));
        
        // 获取最后推荐时间
        List<RecommendationCard> recentCards = recommendationCardRepository
                .findRecentRecommendationsByUserId(userId, PageRequest.of(0, 1));
        if (!recentCards.isEmpty()) {
            stats.put("lastRecommendationTime", recentCards.get(0).getCreatedAt());
        }
        
        return stats;
    }
    
    /**
     * 处理喜欢操作，检查是否匹配
     */
    private void handleLikeAction(RecommendationCard card) {
        // 检查是否相互喜欢（匹配）
        Optional<RecommendationCard> reverseCard = recommendationCardRepository
                .findByUserIdAndRecommendedUserId(card.getRecommendedUserId(), card.getUserId());
        
        if (reverseCard.isPresent() && reverseCard.get().getUserAction() == RecommendationCard.UserAction.LIKE) {
            // 相互喜欢，创建匹配
            card.setStatus(RecommendationCard.CardStatus.MATCHED);
            reverseCard.get().setStatus(RecommendationCard.CardStatus.MATCHED);
            
            recommendationCardRepository.save(reverseCard.get());
        }
    }
    
    private RecommendationCardDto convertToDto(RecommendationCard card) {
        RecommendationCardDto dto = new RecommendationCardDto();
        dto.setId(card.getId());
        dto.setRecommendedUserId(card.getRecommendedUserId());
        dto.setMatchScore(card.getMatchScore());
        dto.setMatchReasons(parseJsonToList(card.getMatchReasons()));
        dto.setStatus(card.getStatus().name());
        dto.setUserAction(card.getUserAction() != null ? card.getUserAction().name() : null);
        dto.setIsViewed(card.getIsViewed());
        dto.setViewedAt(card.getViewedAt());
        dto.setActionAt(card.getActionAt());
        dto.setCreatedAt(card.getCreatedAt());
        
        // 获取被推荐用户信息
        Optional<User> userOpt = userRepository.findById(card.getRecommendedUserId());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            RecommendationCardDto.UserCardInfo userInfo = new RecommendationCardDto.UserCardInfo();
            userInfo.setId(user.getId());
            userInfo.setNickname(user.getNickname());
            userInfo.setAvatarUrl(user.getAvatarUrl());
            userInfo.setGender(user.getGender() != null ? user.getGender().name() : null);
            userInfo.setAge(user.getAge());
            userInfo.setHeight(user.getHeight());
            userInfo.setEducationLevel(user.getEducationLevel() != null ? user.getEducationLevel().name() : null);
            userInfo.setOccupation(user.getOccupation());
            userInfo.setCurrentCity(user.getCurrentCity());
            userInfo.setHometownCity(user.getHometownCity());
            userInfo.setHouseStatus(user.getHouseStatus() != null ? user.getHouseStatus().name() : null);
            userInfo.setCarStatus(user.getCarStatus() != null ? user.getCarStatus().name() : null);
            userInfo.setMaritalStatus(user.getMaritalStatus() != null ? user.getMaritalStatus().name() : null);
            userInfo.setSelfIntroduction(user.getSelfIntroduction());
            userInfo.setLifestyle(user.getLifestyle());
            userInfo.setIdealPartner(user.getIdealPartner());
            userInfo.setUserTags(parseJsonToList(user.getUserTags()));
            userInfo.setLifePhotos(parseJsonToList(user.getLifePhotos()));
            userInfo.setPhotoCount(user.getPhotoCount());
            userInfo.setIsVerified(user.getIsVerified());
            userInfo.setIsRealNameVerified(user.getIsRealNameVerified());
            
            dto.setRecommendedUser(userInfo);
        }
        
        return dto;
    }
    
    private List<String> parseJsonToList(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return new java.util.ArrayList<>();
            }
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return new java.util.ArrayList<>();
        }
    }
} 