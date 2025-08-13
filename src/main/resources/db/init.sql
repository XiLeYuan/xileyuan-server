-- 创建数据库
CREATE DATABASE IF NOT EXISTS marry_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE marry_db;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    real_name VARCHAR(50),
    nickname VARCHAR(50),
    avatar_url TEXT,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    birth_date DATETIME,
    age INT CHECK (age >= 18 AND age <= 100),
    height INT CHECK (height >= 100 AND height <= 250),
    weight INT CHECK (weight >= 30 AND weight <= 200),
    education_level VARCHAR(50),
    occupation VARCHAR(100),
    income_level VARCHAR(50),
    city VARCHAR(100),
    province VARCHAR(100),
    marital_status ENUM('SINGLE', 'DIVORCED', 'WIDOWED'),
    has_children BOOLEAN,
    self_introduction TEXT,
    hobbies TEXT,
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    last_login_time DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    role ENUM('USER', 'ADMIN', 'VIP') DEFAULT 'USER',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone_number),
    INDEX idx_city (city),
    INDEX idx_age (age),
    INDEX idx_gender (gender),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建用户验证码表（用于手机/邮箱验证）
CREATE TABLE IF NOT EXISTS verification_codes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    code VARCHAR(10) NOT NULL,
    type ENUM('EMAIL', 'PHONE') NOT NULL,
    target VARCHAR(100) NOT NULL, -- 邮箱或手机号
    is_used BOOLEAN DEFAULT FALSE,
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_target (target),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建用户登录日志表
CREATE TABLE IF NOT EXISTS login_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    login_time DATETIME NOT NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    login_status ENUM('SUCCESS', 'FAILED') NOT NULL,
    failure_reason VARCHAR(255),
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_login_time (login_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建用户偏好设置表
CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    min_age INT DEFAULT 18,
    max_age INT DEFAULT 100,
    preferred_gender ENUM('MALE', 'FEMALE', 'BOTH'),
    min_height INT DEFAULT 150,
    max_height INT DEFAULT 200,
    preferred_cities TEXT, -- JSON格式存储多个城市
    education_requirements TEXT, -- JSON格式存储教育要求
    income_requirements TEXT, -- JSON格式存储收入要求
    has_children_preference ENUM('YES', 'NO', 'DONT_CARE'),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入管理员用户（密码需要在实际使用时通过BCrypt加密）
-- INSERT INTO users (username, email, password, nickname, role, created_at, updated_at) 
-- VALUES ('admin', 'admin@marry.com', '$2a$10$encrypted_password_here', '管理员', 'ADMIN', NOW(), NOW()); 