package com.xly.marry.util;

import com.xly.marry.entity.User;
import jakarta.servlet.http.HttpSession;

/**
 * Session 工具类
 */
public class SessionUtil {
    
    private static final String SESSION_USER_KEY = "user";
    private static final String SESSION_USER_ID_KEY = "userId";
    
    /**
     * 将用户信息存储到 Session
     */
    public static void setUser(HttpSession session, User user) {
        if (session != null && user != null) {
            session.setAttribute(SESSION_USER_KEY, user);
            session.setAttribute(SESSION_USER_ID_KEY, user.getId());
        }
    }
    
    /**
     * 从 Session 获取用户信息
     */
    public static User getUser(HttpSession session) {
        if (session != null) {
            return (User) session.getAttribute(SESSION_USER_KEY);
        }
        return null;
    }
    
    /**
     * 从 Session 获取用户 ID
     */
    public static Long getUserId(HttpSession session) {
        if (session != null) {
            return (Long) session.getAttribute(SESSION_USER_ID_KEY);
        }
        return null;
    }
    
    /**
     * 清除 Session 中的用户信息
     */
    public static void clearUser(HttpSession session) {
        if (session != null) {
            session.removeAttribute(SESSION_USER_KEY);
            session.removeAttribute(SESSION_USER_ID_KEY);
        }
    }
    
    /**
     * 检查用户是否已登录
     */
    public static boolean isLoggedIn(HttpSession session) {
        return session != null && session.getAttribute(SESSION_USER_KEY) != null;
    }
}

