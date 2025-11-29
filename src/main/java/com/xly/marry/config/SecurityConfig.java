package com.xly.marry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import com.xly.marry.interceptor.SessionInterceptor;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SessionInterceptor sessionInterceptor() {
        return new SessionInterceptor();
    }
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")  // 允许 Authorization 和 X-Auth-Token header
                .allowCredentials(false)  // App 使用 token，不需要 Cookie
                .maxAge(3600);
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sessionInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                    "/api/auth/login",
                    "/api/auth/register",
                    "/api/auth/phoneLogin",
                    // stepRegister 需要 token，不排除
                    "/api/auth/send-verification-code",
                    "/api/auth/verify-code",
                    "/api/auth/check-username",
                    "/api/auth/check-email",
                    "/api/auth/check-phone",
                    "/api/auth/registration-progress",
                    "/api/auth/user-status",
                    "/api/payment/alipay/notify",  // 支付宝回调不需要token
                    "/api/payment/wechat/notify",  // 微信回调不需要token
                    "/api/test/**"
                );
    }
} 