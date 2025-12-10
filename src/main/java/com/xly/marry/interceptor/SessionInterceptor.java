package com.xly.marry.interceptor;

import com.xly.marry.entity.User;
import com.xly.marry.util.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Token 拦截器，用于检查用户登录状态（适合移动 App）
 */
@Component
public class SessionInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取 token
        String token = TokenUtil.getTokenFromRequest(request);
        
        if (token == null || token.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"未提供认证 token，请先登录\"}");
            return false;
        }
        
        // 验证 token 并获取用户信息
        User user = TokenUtil.getUserFromRequest(request);
        
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"token 无效或已过期，请重新登录\"}");
            return false;
        }
        
        // 将用户信息存储到 request 属性中，方便后续使用
        request.setAttribute("currentUser", user);
        request.setAttribute("currentUserId", user.getId());
        
        return true;
    }
}

