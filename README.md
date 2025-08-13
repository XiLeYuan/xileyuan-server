# 相亲社交APP后端服务

这是一个基于Spring Boot 3.5.4的相亲社交APP后端服务项目，提供用户注册、登录、认证等功能。

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
│   └── SecurityConfig.java
├── controller/            # 控制器层
│   └── AuthController.java
├── dto/                  # 数据传输对象
│   ├── ApiResponse.java
│   ├── AuthResponse.java
│   ├── LoginRequest.java
│   └── RegisterRequest.java
├── entity/               # 实体类
│   └── User.java
├── repository/           # 数据访问层
│   └── UserRepository.java
├── security/             # 安全相关
│   ├── JwtAuthenticationFilter.java
│   └── JwtTokenProvider.java
├── service/              # 业务逻辑层
│   ├── CustomUserDetailsService.java
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
| nickname | VARCHAR(50) | 昵称 | |
| avatar_url | TEXT | 头像URL | |
| gender | ENUM | 性别 | MALE/FEMALE/OTHER |
| birth_date | DATETIME | 出生日期 | |
| age | INT | 年龄 | 18-100 |
| height | INT | 身高(cm) | 100-250 |
| weight | INT | 体重(kg) | 30-200 |
| education_level | VARCHAR(50) | 教育程度 | |
| occupation | VARCHAR(100) | 职业 | |
| income_level | VARCHAR(50) | 收入水平 | |
| city | VARCHAR(100) | 城市 | |
| province | VARCHAR(100) | 省份 | |
| marital_status | ENUM | 婚姻状况 | SINGLE/DIVORCED/WIDOWED |
| has_children | BOOLEAN | 是否有子女 | |
| self_introduction | TEXT | 自我介绍 | |
| hobbies | TEXT | 兴趣爱好 | |
| is_verified | BOOLEAN | 是否已验证 | DEFAULT FALSE |
| is_active | BOOLEAN | 是否激活 | DEFAULT TRUE |
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

## API接口

### 认证相关接口

#### 1. 用户注册
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
  "educationLevel": "本科",
  "occupation": "软件工程师",
  "incomeLevel": "10-20万",
  "city": "北京",
  "province": "北京",
  "maritalStatus": "SINGLE",
  "hasChildren": false,
  "selfIntroduction": "我是一个热爱生活的人",
  "hobbies": "读书,旅行,运动"
}
```

#### 2. 用户登录
```
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "testuser",
  "password": "password123"
}
```

#### 3. 检查用户名是否存在
```
GET /api/auth/check-username?username=testuser
```

#### 4. 检查邮箱是否存在
```
GET /api/auth/check-email?email=test@example.com
```

## 响应格式

所有API都使用统一的响应格式：

```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    // 具体数据
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
- API文档：http://localhost:8080/api/auth/register

## 安全特性

1. **密码加密**：使用BCrypt算法加密用户密码
2. **JWT认证**：使用JWT令牌进行身份验证
3. **输入验证**：对所有用户输入进行验证
4. **CORS配置**：支持跨域请求
5. **SQL注入防护**：使用JPA参数化查询

## 扩展功能

1. **手机/邮箱验证**：支持发送验证码验证用户身份
2. **用户偏好设置**：支持用户设置匹配偏好
3. **登录日志**：记录用户登录行为
4. **用户角色管理**：支持不同用户角色和权限

## 注意事项

1. 生产环境中请修改JWT密钥
2. 建议启用HTTPS
3. 定期备份数据库
4. 监控系统性能和日志 