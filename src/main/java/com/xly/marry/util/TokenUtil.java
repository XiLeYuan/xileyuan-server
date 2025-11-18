package com.xly.marry.util;

import com.xly.marry.entity.User;
import com.xly.marry.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Token 工具类
 * 用于从请求中获取 token 和用户信息
 */
@Component
public class TokenUtil {
    
    private static TokenService staticTokenService;
    
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    public void setTokenService(TokenService tokenService) {
        TokenUtil.staticTokenService = tokenService;
        this.tokenService = tokenService;
    }
    
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    
    /**
     * 从请求中获取 token
     */
    public static String getTokenFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader(TOKEN_HEADER);
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length());
        }
        // 也支持从 X-Auth-Token header 获取（App 常用）
        String token = request.getHeader("X-Auth-Token");
        if (token != null && !token.isEmpty()) {
            return token;
        }
        return null;
    }
    
    /**
     * 从请求中获取用户信息（静态方法）
     */
    public static User getUserFromRequest(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null || token.isEmpty() || staticTokenService == null) {
            return null;
        }
        return staticTokenService.validateToken(token);
    }
    
    /**
     * 从请求中获取用户信息（实例方法）
     */
    public User getUserFromRequestInstance(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token == null || token.isEmpty()) {
            return null;
        }
        return tokenService.validateToken(token);
    }
    
    /**
     * 从请求中获取用户 ID
     */
    public static Long getUserIdFromRequest(HttpServletRequest request) {
        User user = getUserFromRequest(request);
        return user != null ? user.getId() : null;
    }
}

