package com.xly.marry.service;

import com.xly.marry.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Token 管理服务
 * 用于生成、存储和验证 token（适合移动 App）
 */
@Service
public class TokenService {
    
    // Token 存储：token -> User
    private final Map<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();
    
    // 用户 Token 映射：userId -> token（一个用户只能有一个有效 token）
    private final Map<Long, String> userTokenMap = new ConcurrentHashMap<>();
    
    @Value("${app.token.expiration:86400000}")
    private long tokenExpirationMs; // 默认 24 小时
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    /**
     * Token 信息
     */
    private static class TokenInfo {
        private final User user;
        private final long expireTime;
        
        public TokenInfo(User user, long expireTime) {
            this.user = user;
            this.expireTime = expireTime;
        }
        
        public User getUser() {
            return user;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }
    
    public TokenService() {
        // 启动定时清理过期 token
        scheduler.scheduleAtFixedRate(this::cleanExpiredTokens, 1, 1, TimeUnit.HOURS);
    }
    
    /**
     * 生成并存储 token
     */
    public String generateToken(User user) {
        // 如果用户已有 token，先清除旧的
        String oldToken = userTokenMap.get(user.getId());
        if (oldToken != null) {
            tokenStore.remove(oldToken);
        }
        
        // 生成新 token
        String token = UUID.randomUUID().toString().replace("-", "") + "_" + System.currentTimeMillis();
        long expireTime = System.currentTimeMillis() + tokenExpirationMs;
        
        TokenInfo tokenInfo = new TokenInfo(user, expireTime);
        tokenStore.put(token, tokenInfo);
        userTokenMap.put(user.getId(), token);
        
        return token;
    }
    
    /**
     * 验证 token 并返回用户信息
     */
    public User validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        
        TokenInfo tokenInfo = tokenStore.get(token);
        if (tokenInfo == null) {
            return null;
        }
        
        if (tokenInfo.isExpired()) {
            // Token 已过期，清除
            tokenStore.remove(token);
            if (tokenInfo.getUser() != null) {
                userTokenMap.remove(tokenInfo.getUser().getId());
            }
            return null;
        }
        
        return tokenInfo.getUser();
    }
    
    /**
     * 清除 token
     */
    public void removeToken(String token) {
        TokenInfo tokenInfo = tokenStore.remove(token);
        if (tokenInfo != null && tokenInfo.getUser() != null) {
            userTokenMap.remove(tokenInfo.getUser().getId());
        }
    }
    
    /**
     * 清除用户的所有 token
     */
    public void removeUserToken(Long userId) {
        String token = userTokenMap.remove(userId);
        if (token != null) {
            tokenStore.remove(token);
        }
    }
    
    /**
     * 刷新 token（延长过期时间）
     */
    public String refreshToken(String token) {
        TokenInfo tokenInfo = tokenStore.get(token);
        if (tokenInfo == null || tokenInfo.isExpired()) {
            return null;
        }
        
        User user = tokenInfo.getUser();
        // 生成新 token
        return generateToken(user);
    }
    
    /**
     * 清理过期 token
     */
    private void cleanExpiredTokens() {
        tokenStore.entrySet().removeIf(entry -> {
            if (entry.getValue().isExpired()) {
                User user = entry.getValue().getUser();
                if (user != null) {
                    userTokenMap.remove(user.getId());
                }
                return true;
            }
            return false;
        });
    }
}

