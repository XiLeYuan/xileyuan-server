# 相亲社交APP后端服务

这是一个基于Spring Boot 3.5.4的相亲社交APP后端服务项目，提供用户注册、登录、认证等功能，支持分步注册流程。

## 技术栈

- **Spring Boot 3.5.4** - 主框架
- **Spring Data JPA** - 数据访问层
- **Spring Security** - 安全认证
- **MySQL 8.0** - 数据库
- **JWT** - 令牌认证
- **Lombok** - 代码简化
- **Maven** - 依赖管理

## 项目结构

```
src/main/java/com/xly/marry/
├── config/                 # 配置类
│   ├── AppConfig.java
│   └── SecurityConfig.java
├── controller/            # 控制器层
│   ├── AuthController.java
│   ├── UserController.java
│   ├── TagController.java
│   └── TestController.java
├── dto/                  # 数据传输对象
│   ├── ApiResponse.java
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── StepRegisterRequest.java
├── entity/               # 实体类
│   └── User.java
├── repository/           # 数据访问层
│   └── UserRepository.java
├── security/             # 安全相关
│   ├── JwtAuthenticationFilter.java
│   └── JwtTokenProvider.java
├── service/              # 业务逻辑层
│   ├── CustomUserDetailsService.java
│   ├── SimpleUserService.java
│   └── UserService.java
└── MarryApplication.java # 启动类
```

## 数据库设计

### 用户表 (users)

| 字段名 | 类型 | 说明 | 约束 |
|--------|------|------|------|
| id | BIGINT | 主键 | AUTO_INCREMENT |
| username | VARCHAR(50) | 用户名 | UNIQUE, NOT NULL |
| email | VARCHAR(100) | 邮箱 | UNIQUE, NOT NULL |
| password | VARCHAR(255) | 密码 | NOT NULL |
| phone_number | VARCHAR(20) | 手机号 | UNIQUE |
| real_name | VARCHAR(50) | 真实姓名 | |
| id_card_number | VARCHAR(18) | 身份证号 | |
| is_real_name_verified | BOOLEAN | 是否实名认证 | DEFAULT FALSE |
| nickname | VARCHAR(50) | 昵称 | |
| avatar_url | TEXT | 头像URL | |
| gender | ENUM | 性别 | MALE/FEMALE |
| birth_date | DATETIME | 出生日期 | |
| age | INT | 年龄 | 18-100 |
| height | INT | 身高(cm) | 140-210 |
| weight | INT | 体重(kg) | 30-200 |
| is_weight_private | BOOLEAN | 体重是否保密 | DEFAULT FALSE |
| education_level | ENUM | 教育程度 | DOCTOR/MASTER/BACHELOR/COLLEGE/BELOW_COLLEGE |
| school | VARCHAR(200) | 学校 | |
| company_type | ENUM | 公司性质 | STATE_OWNED/INSTITUTION/PRIVATE/CIVIL_SERVANT/LISTED/INDIVIDUAL/FOREIGN/MILITARY/OTHER |
| occupation | VARCHAR(100) | 职业 | |
| income_level | ENUM | 收入水平 | BELOW_50K/FIVE_TO_TEN/TEN_TO_TWENTY/TWENTY_TO_THIRTY/THIRTY_TO_SIXTY/SIXTY_TO_HUNDRED/ABOVE_HUNDRED |
| current_province | VARCHAR(100) | 现居地省份 | |
| current_city | VARCHAR(100) | 现居地城市 | |
| current_district | VARCHAR(100) | 现居地区县 | |
| hometown_province | VARCHAR(100) | 家乡省份 | |
| hometown_city | VARCHAR(100) | 家乡城市 | |
| hometown_district | VARCHAR(100) | 家乡区县 | |
| house_status | ENUM | 购房情况 | NO_HOUSE/HOUSE_NO_LOAN/HOUSE_WITH_LOAN/MULTIPLE_HOUSES |
| car_status | ENUM | 购车情况 | NO_CAR/CAR_NO_LOAN/CAR_WITH_LOAN |
| marital_status | ENUM | 婚姻状况 | SINGLE/DIVORCED/WIDOWED |
| children_status | ENUM | 子女情况 | NO_CHILDREN/CHILDREN_WITH_SELF/CHILDREN_WITH_OTHER |
| love_attitude | ENUM | 恋爱观念 | WANT_MARRY_SOON/SERIOUS_LOVE_MARRY/SERIOUS_LOVE_FIRST/NOT_SURE |
| preferred_age_min | INT | 期望年龄最小值 | |
| preferred_age_max | INT | 期望年龄最大值 | |
| marriage_plan | ENUM | 结婚计划 | WITHIN_ONE_YEAR/WITHIN_TWO_YEARS/WITHIN_THREE_YEARS/WHEN_FATE_COMES |
| self_introduction | TEXT | 自我介绍 | |
| lifestyle | TEXT | 生活描述 | |
| ideal_partner | TEXT | 理想伴侣描述 | |
| hobbies | TEXT | 兴趣爱好 | |
| life_photos | TEXT | 生活照(JSON) | |
| photo_count | INT | 照片数量 | DEFAULT 0 |
| user_tags | TEXT | 用户标签(JSON) | |
| preferred_tags | TEXT | 期望伴侣标签(JSON) | |
| is_verified | BOOLEAN | 是否已验证 | DEFAULT FALSE |
| is_active | BOOLEAN | 是否激活 | DEFAULT TRUE |
| registration_step | INT | 注册步骤 | 0-16 |
| last_login_time | DATETIME | 最后登录时间 | |
| created_at | DATETIME | 创建时间 | NOT NULL |
| updated_at | DATETIME | 更新时间 | NOT NULL |
| role | ENUM | 用户角色 | USER/ADMIN/VIP |

### 验证码表 (verification_codes)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| code | VARCHAR(10) | 验证码 |
| type | ENUM | 验证类型(EMAIL/PHONE) |
| target | VARCHAR(100) | 目标(邮箱/手机号) |
| is_used | BOOLEAN | 是否已使用 |
| expires_at | DATETIME | 过期时间 |
| created_at | DATETIME | 创建时间 |

### 登录日志表 (login_logs)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| login_time | DATETIME | 登录时间 |
| ip_address | VARCHAR(45) | IP地址 |
| user_agent | TEXT | 用户代理 |
| login_status | ENUM | 登录状态(SUCCESS/FAILED) |
| failure_reason | VARCHAR(255) | 失败原因 |

### 用户偏好表 (user_preferences)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户ID |
| min_age | INT | 最小年龄 |
| max_age | INT | 最大年龄 |
| preferred_gender | ENUM | 偏好性别 |
| min_height | INT | 最小身高 |
| max_height | INT | 最大身高 |
| preferred_cities | TEXT | 偏好城市(JSON) |
| education_requirements | TEXT | 教育要求(JSON) |
| income_requirements | TEXT | 收入要求(JSON) |
| has_children_preference | ENUM | 子女偏好 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### 用户标签表 (user_tags)

| 字段名 | 类型 | 说明 |
|--------|------|------|
| id | BIGINT | 主键 |
| name | VARCHAR(100) | 标签代码 | UNIQUE |
| category | VARCHAR(50) | 标签分类 | |
| display_name | VARCHAR(100) | 显示名称 | |
| is_active | BOOLEAN | 是否激活 | DEFAULT TRUE |
| created_at | DATETIME | 创建时间 | |

## API接口

### 认证相关接口

#### 1. 用户注册（完整注册）
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "phoneNumber": "13800138000",
  "nickname": "测试用户",
  "gender": "MALE",
  "age": 25,
  "height": 175,
  "weight": 65,
  "isWeightPrivate": false,
  "educationLevel": "BACHELOR",
  "school": "北京大学",
  "companyType": "PRIVATE",
  "occupation": "软件工程师",
  "incomeLevel": "TEN_TO_TWENTY",
  "currentProvince": "北京",
  "currentCity": "北京",
  "currentDistrict": "朝阳区",
  "hometownProvince": "山东",
  "hometownCity": "济南",
  "hometownDistrict": "历下区",
  "houseStatus": "HOUSE_WITH_LOAN",
  "carStatus": "NO_CAR",
  "maritalStatus": "SINGLE",
  "childrenStatus": "NO_CHILDREN",
  "loveAttitude": "SERIOUS_LOVE_MARRY",
  "preferredAgeMin": 22,
  "preferredAgeMax": 28,
  "marriagePlan": "WITHIN_TWO_YEARS",
  "selfIntroduction": "我是一个热爱生活的人",
  "lifestyle": "我是一名外科医生，平时很忙",
  "idealPartner": "我的理想型是性格乐观开朗的人",
  "hobbies": "读书,旅行,运动",
  "lifePhotos": "[\"photo1.jpg\",\"photo2.jpg\"]",
  "userTags": "[\"LOVE_SPORTS\",\"HUMOROUS\"]",
  "preferredTags": "[\"GOOD_TEMPERAMENT\",\"SUNSHINE\"]",
  "realName": "张三",
  "idCardNumber": "110101199001011234",
  "registrationStep": 16
}
```

#### 2. 分步注册
```
POST /api/auth/step-register
Content-Type: application/json

{
  "step": 1,
  "phoneNumber": "13800138000",
  "gender": "MALE"
}
```

#### 3. 用户登录
```
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "testuser",
  "password": "password123"
}
```

#### 4. 发送验证码
```
POST /api/auth/send-verification-code?phoneNumber=13800138000
```

#### 5. 验证验证码
```
POST /api/auth/verify-code?phoneNumber=13800138000&code=123456
```

#### 6. 检查用户名是否存在
```
GET /api/auth/check-username?username=testuser
```

#### 7. 检查邮箱是否存在
```
GET /api/auth/check-email?email=test@example.com
```

#### 8. 检查手机号是否存在
```
GET /api/auth/check-phone?phoneNumber=13800138000
```

#### 9. 获取注册进度
```
GET /api/auth/registration-progress?phoneNumber=13800138000
```

### 标签相关接口

#### 1. 获取所有标签分类
```
GET /api/tags/categories
```

#### 2. 获取用户标签
```
GET /api/tags/user-tags
```

#### 3. 获取期望伴侣标签
```
GET /api/tags/preferred-tags
```

### 用户相关接口

#### 1. 获取所有用户
```
GET /api/users
Authorization: Bearer {token}
```

#### 2. 根据ID获取用户
```
GET /api/users/{id}
Authorization: Bearer {token}
```

#### 3. 获取当前用户信息
```
GET /api/users/profile
Authorization: Bearer {token}
```

#### 4. 更新用户信息
```
PUT /api/users/profile
Authorization: Bearer {token}
Content-Type: application/json

{
  "nickname": "新昵称",
  "selfIntroduction": "更新后的自我介绍"
}
```

### 测试接口

#### 1. 健康检查
```
GET /api/test/health
```

#### 2. 欢迎信息
```
GET /api/test/hello
```

## 分步注册流程

系统支持17步分步注册流程：

1. **步骤0**: 手机验证码登录
2. **步骤1**: 选择性别
3. **步骤2**: 身高和年龄（体重可选保密）
4. **步骤3**: 学历选择
5. **步骤4**: 学校输入
6. **步骤5**: 公司性质和工作信息
7. **步骤6**: 地理位置（现居地和家乡）
8. **步骤7**: 买房买车情况
9. **步骤8**: 婚姻状况和子女情况
10. **步骤9**: 恋爱观念和期望年龄
11. **步骤10**: 昵称和头像
12. **步骤11**: 生活照上传
13. **步骤12**: 关于我-性格描述
14. **步骤13**: 关于我-生活描述
15. **步骤14**: 关于我-理想伴侣
16. **步骤15**: 我想认识的人（标签选择）
17. **步骤16**: 实名认证

## 响应格式

所有API都使用统一的响应格式：

```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    
  },
  "timestamp": 1640995200000
}
```

## 安装和运行

### 1. 环境要求
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库配置
1. 创建数据库：
```sql
CREATE DATABASE marry_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/marry_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: your_username
    password: your_password
```

### 3. 运行项目
```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

### 4. 访问接口
- 服务地址：http://localhost:8080
- 健康检查：http://localhost:8080/api/test/health

## 安全特性

1. **密码加密**：使用MD5加密（生产环境建议使用BCrypt）
2. **JWT认证**：使用JWT令牌进行身份验证
3. **输入验证**：对所有用户输入进行验证
4. **CORS配置**：支持跨域请求
5. **SQL注入防护**：使用JPA参数化查询
6. **实名认证**：支持身份证验证

## 扩展功能

1. **分步注册**：支持17步分步注册流程
2. **手机/邮箱验证**：支持发送验证码验证用户身份
3. **用户标签系统**：支持用户选择标签和期望伴侣标签
4. **照片管理**：支持头像和生活照上传
5. **地理位置**：支持现居地和家乡信息
6. **资产状况**：支持购房购车情况记录
7. **恋爱观念**：支持详细的恋爱和婚姻观念记录

## 注意事项

1. 生产环境中请修改JWT密钥
2. 建议启用HTTPS
3. 定期备份数据库
4. 监控系统性能和日志
5. 实现完整的验证码发送和验证逻辑
6. 添加照片上传和存储功能
7. 实现实名认证验证逻辑 

## 技术特性

- **枚举类型**：所有选项都使用枚举，确保数据一致性
- **JSON存储**：照片URL和标签使用JSON格式存储
- **验证规则**：完整的输入验证和格式检查
- **进度跟踪**：支持注册步骤进度管理
- **临时用户**：支持分步注册过程中的临时用户管理
- **Spring Security集成**：User实体实现UserDetails接口，支持Spring Security认证 