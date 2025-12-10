-- 创建数据库
CREATE DATABASE IF NOT EXISTS marry_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE marry_db;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    -- 基础认证信息
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20) UNIQUE,
    
    -- 实名认证信息
    real_name VARCHAR(50),
    id_card_number VARCHAR(18),
    is_real_name_verified BOOLEAN DEFAULT FALSE,
    
    -- 基本信息
    nickname VARCHAR(50),
    avatar_url TEXT,
    gender ENUM('MALE', 'FEMALE'),
    birth_date DATETIME,
    age INT CHECK (age >= 18 AND age <= 100),
    height INT CHECK (height >= 140 AND height <= 210),
    weight INT CHECK (weight >= 30 AND weight <= 200),
    is_weight_private BOOLEAN DEFAULT FALSE,
    
    -- 教育信息
    education_level ENUM('DOCTOR', 'MASTER', 'BACHELOR', 'COLLEGE', 'BELOW_COLLEGE'),
    school VARCHAR(200),
    
    -- 职业信息
    company_type ENUM('STATE_OWNED', 'INSTITUTION', 'PRIVATE', 'CIVIL_SERVANT', 'LISTED', 'INDIVIDUAL', 'FOREIGN', 'MILITARY', 'OTHER'),
    occupation VARCHAR(100),
    income_level ENUM('BELOW_50K', 'FIVE_TO_TEN', 'TEN_TO_TWENTY', 'TWENTY_TO_THIRTY', 'THIRTY_TO_SIXTY', 'SIXTY_TO_HUNDRED', 'ABOVE_HUNDRED'),
    
    -- 地理位置
    current_province VARCHAR(100),
    current_city VARCHAR(100),
    current_district VARCHAR(100),
    hometown_province VARCHAR(100),
    hometown_city VARCHAR(100),
    hometown_district VARCHAR(100),
    
    -- 资产状况
    house_status ENUM('NO_HOUSE', 'HOUSE_NO_LOAN', 'HOUSE_WITH_LOAN', 'MULTIPLE_HOUSES'),
    car_status ENUM('NO_CAR', 'CAR_NO_LOAN', 'CAR_WITH_LOAN'),
    
    -- 婚姻状况
    marital_status ENUM('SINGLE', 'DIVORCED', 'WIDOWED'),
    children_status ENUM('NO_CHILDREN', 'CHILDREN_WITH_SELF', 'CHILDREN_WITH_OTHER'),
    
    -- 恋爱观念
    love_attitude ENUM('WANT_MARRY_SOON', 'SERIOUS_LOVE_MARRY', 'SERIOUS_LOVE_FIRST', 'NOT_SURE'),
    preferred_age_min INT,
    preferred_age_max INT,
    marriage_plan ENUM('WITHIN_ONE_YEAR', 'WITHIN_TWO_YEARS', 'WITHIN_THREE_YEARS', 'WHEN_FATE_COMES'),
    
    -- 个人介绍
    self_introduction TEXT,
    lifestyle TEXT,
    ideal_partner TEXT,
    hobbies TEXT,
    
    -- 照片信息
    life_photos TEXT, -- JSON格式存储多张照片URL
    photo_count INT DEFAULT 0,
    
    -- 标签信息
    user_tags TEXT, -- JSON格式存储用户选择的标签
    preferred_tags TEXT, -- JSON格式存储期望伴侣的标签
    
    -- 系统状态
    is_verified BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    registration_step INT DEFAULT 0, -- 注册步骤，0-16
    last_login_time DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    role ENUM('USER', 'ADMIN', 'VIP') DEFAULT 'USER',
    
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone_number),
    INDEX idx_current_city (current_city),
    INDEX idx_age (age),
    INDEX idx_gender (gender),
    INDEX idx_education_level (education_level),
    INDEX idx_income_level (income_level),
    INDEX idx_marital_status (marital_status),
    INDEX idx_registration_step (registration_step),
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

-- 推荐卡片表
CREATE TABLE IF NOT EXISTS recommendation_cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '推荐给谁（当前用户）',
    recommended_user_id BIGINT NOT NULL COMMENT '被推荐的人',
    match_score INT COMMENT '匹配度分数 (0-100)',
    match_reasons TEXT COMMENT '推荐原因标签（JSON格式）',
    status ENUM('PENDING', 'LIKED', 'PASSED', 'MATCHED') DEFAULT 'PENDING' COMMENT '推荐状态',
    user_action ENUM('LIKE', 'PASS', 'SUPER_LIKE') COMMENT '用户操作',
    is_viewed BOOLEAN DEFAULT FALSE COMMENT '是否已查看',
    viewed_at DATETIME COMMENT '查看时间',
    action_at DATETIME COMMENT '操作时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    INDEX idx_user_id (user_id),
    INDEX idx_recommended_user_id (recommended_user_id),
    INDEX idx_user_status (user_id, status),
    INDEX idx_user_action (user_id, user_action),
    INDEX idx_created_at (created_at),
    UNIQUE KEY uk_user_recommended (user_id, recommended_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='推荐卡片表';

-- 用户偏好表
CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    min_age INT COMMENT '最小年龄',
    max_age INT COMMENT '最大年龄',
    preferred_gender ENUM('MALE', 'FEMALE') COMMENT '偏好性别',
    min_height INT COMMENT '最小身高',
    max_height INT COMMENT '最大身高',
    preferred_cities TEXT COMMENT '偏好城市（JSON格式）',
    education_requirements TEXT COMMENT '教育要求（JSON格式）',
    income_requirements TEXT COMMENT '收入要求（JSON格式）',
    has_children_preference ENUM('YES', 'NO', 'DONT_CARE') COMMENT '子女偏好',
    max_distance INT COMMENT '最大距离（公里）',
    only_verified_users BOOLEAN DEFAULT FALSE COMMENT '只看实名认证用户',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    UNIQUE KEY uk_user_id (user_id),
    INDEX idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户偏好表';

-- 创建用户标签表
CREATE TABLE IF NOT EXISTS user_tags (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    category VARCHAR(50), -- 标签分类：性格、爱好、职业等
    display_name VARCHAR(100) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at DATETIME NOT NULL,
    
    INDEX idx_category (category),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入默认标签数据
INSERT INTO user_tags (name, category, display_name, created_at) VALUES
-- 性格标签
('PERSISTENT_HOBBY', 'PERSONALITY', '有坚持很久的爱好', NOW()),
('LOVE_SPORTS', 'PERSONALITY', '爱运动', NOW()),
('HUMOROUS', 'PERSONALITY', '有幽默感', NOW()),
('SUNNY', 'PERSONALITY', '阳光开朗', NOW()),
('EASYGOING', 'PERSONALITY', '性格随和', NOW()),
('FAMILY_ORIENTED', 'PERSONALITY', '享受家庭时光', NOW()),
('CAREER_PLANNED', 'PERSONALITY', '对职业有规划', NOW()),
('MATURE_RATIONAL', 'PERSONALITY', '成熟理智', NOW()),
('HAPPY_DAILY', 'PERSONALITY', '每天都很开心', NOW()),
('ROMANTIC', 'PERSONALITY', '感性浪漫', NOW()),
('INDEPENDENT', 'PERSONALITY', '有自己的主见和想法', NOW()),
('CAREFUL', 'PERSONALITY', '比较细心', NOW()),
('GOOD_FIGURE', 'APPEARANCE', '身材匀称', NOW()),
('GOOD_LOOKING', 'APPEARANCE', '有颜值', NOW()),
('GOOD_DRESSING', 'APPEARANCE', '衣品好', NOW()),
('SINCERE', 'PERSONALITY', '真诚重感情', NOW()),
('LOGICAL', 'PERSONALITY', '逻辑思维强', NOW()),
('PROGRESSIVE', 'PERSONALITY', '有追求能一起进步', NOW()),
('IMAGINATIVE', 'PERSONALITY', '想象力丰富', NOW()),
('KIND', 'PERSONALITY', '善良有爱心', NOW()),
('GOOD_HABITS', 'LIFESTYLE', '生活习惯好', NOW()),
('STABLE_LIFE', 'LIFESTYLE', '想过安稳生活', NOW()),
('ECONOMIC_ABILITY', 'ECONOMIC', '有一定经济能力', NOW()),
('LIFE_PURSUIT', 'LIFESTYLE', '对生活有追求', NOW()),
('GOOD_EDUCATION', 'EDUCATION', '有良好学历背景', NOW()),
('ENJOY_LIFE', 'LIFESTYLE', '会享受生活', NOW()),
('COMMON_HOBBIES', 'LIFESTYLE', '有共同爱好', NOW()),
('GOOD_COMMUNICATION', 'PERSONALITY', '善于沟通，会换位思考', NOW()),
('LOVE_OVER_MATERIAL', 'VALUES', '相比物质更看重爱情', NOW()),
('GOOD_CONVERSATION', 'PERSONALITY', '聊得来', NOW()),
('LESS_OVERTIME', 'WORK', '加班少', NOW()),

-- 期望伴侣特征标签
('GOOD_TEMPERAMENT', 'APPEARANCE', '气质好', NOW()),
('FREE_SPIRITED', 'PERSONALITY', '自由随性', NOW()),
('OUTGOING', 'PERSONALITY', '外向开朗', NOW()),
('EMOTIONALLY_STABLE', 'PERSONALITY', '情绪稳定', NOW()),
('KIND_HEARTED', 'PERSONALITY', '善良', NOW()),
('HIGH_LIFE_QUALITY', 'LIFESTYLE', '生活品质高', NOW()),
('OPTIMISTIC', 'PERSONALITY', '乐观', NOW()),
('TALL_GIRL', 'APPEARANCE', '高个子女生', NOW()),
('PROGRAMMER', 'PROFESSION', '程序员', NOW()),
('SUNSHINE', 'PERSONALITY', '阳光', NOW()),
('HEALTHY_LIFE', 'LIFESTYLE', '健康生活', NOW()),
('LIFE_ATMOSPHERE', 'LIFESTYLE', '有生活气息', NOW()),
('SELF_DISCIPLINED', 'PERSONALITY', '自律', NOW()),
('STABLE_FAMILY', 'LIFESTYLE', '稳定顾家', NOW()),
('INTERNET_INDUSTRY', 'PROFESSION', '互联网行业', NOW()),
('INTELLIGENT', 'PERSONALITY', '聪明', NOW()),
('RATIONAL', 'PERSONALITY', '理性', NOW()),
('SMILEY', 'PERSONALITY', '爱笑', NOW()),
('URBAN_WHITE_COLLAR', 'PROFESSION', '都市白领', NOW()),
('LOVE_SPORTS_DAILY', 'LIFESTYLE', '平时爱运动', NOW()),
('RICH_DAILY_LIFE', 'LIFESTYLE', '日常生活丰富', NOW()),
('LOVE_TRAVEL', 'LIFESTYLE', '喜欢旅行', NOW()),
('GOOD_COOKING', 'LIFESTYLE', '厨艺好', NOW()),
('LIFE_SENTIMENT', 'LIFESTYLE', '有生活情调', NOW()),
('SIMPLE_CIRCLE', 'LIFESTYLE', '圈子简单', NOW()),
('OUTDOOR_SPORTS', 'LIFESTYLE', '户外运动', NOW()),
('INNOCENT', 'PERSONALITY', '单纯', NOW()),
('SYSTEM_WORKER', 'PROFESSION', '体制内', NOW()),
('TOP_UNIVERSITY', 'EDUCATION', '名校毕业', NOW()),
('HIGH_EDUCATION', 'EDUCATION', '高学历', NOW()),
('LOVE_READING', 'LIFESTYLE', '爱读书', NOW()),
('FOOD_DRINK', 'LIFESTYLE', '吃吃喝喝', NOW()),
('BROAD_VISION', 'PERSONALITY', '视野开阔', NOW()),
('INTERESTING', 'PERSONALITY', '有趣', NOW()),
('HUMOROUS_PERSON', 'PERSONALITY', '有幽默感', NOW()),
('SUCCESSFUL_CAREER', 'PROFESSION', '事业有成', NOW()),
('STRONG_WORK_ABILITY', 'PROFESSION', '工作能力强', NOW()),
('SYSTEM_INTERNAL', 'PROFESSION', '体制内', NOW()),
('LONG_TERM_HOBBY', 'LIFESTYLE', '有长期爱好', NOW());

-- 插入管理员用户（密码需要在实际使用时通过BCrypt加密）
-- INSERT INTO users (username, email, password, nickname, role, created_at, updated_at) 
-- VALUES ('admin', 'admin@marry.com', '$2a$10$encrypted_password_here', '管理员', 'ADMIN', NOW(), NOW());