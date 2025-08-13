# 相亲社交APP后端 - 快速启动指南

## 🚀 快速开始

### 1. 环境准备
确保你的系统已安装：
- JDK 17+
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库设置
```sql
-- 创建数据库
CREATE DATABASE marry_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE marry_db;
```

### 3. 配置文件
修改 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/marry_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: your_username  # 替换为你的MySQL用户名
    password: your_password  # 替换为你的MySQL密码
```

### 4. 启动项目
```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run
```

### 5. 测试API
项目启动后，访问以下地址测试：

#### 健康检查
```bash
curl http://localhost:8080/api/test/health
```

#### 用户注册
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
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
  }'
```

#### 用户登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "usernameOrEmail": "testuser",
    "password": "password123"
  }'
```

## 📋 功能特性

### ✅ 已实现功能
- [x] 用户注册
- [x] 用户登录
- [x] 用户名/邮箱唯一性检查
- [x] 密码加密存储
- [x] 用户信息管理
- [x] 数据库自动建表
- [x] CORS跨域支持
- [x] 统一API响应格式

### 🔄 待完善功能
- [ ] JWT令牌认证
- [ ] Spring Security集成
- [ ] 手机/邮箱验证码
- [ ] 用户头像上传
- [ ] 用户匹配算法
- [ ] 聊天功能
- [ ] 用户偏好设置

## 🗄️ 数据库表结构

项目启动后会自动创建以下表：
- `users` - 用户基本信息表
- `verification_codes` - 验证码表
- `login_logs` - 登录日志表
- `user_preferences` - 用户偏好表

## 🔧 常见问题

### Q: 启动时数据库连接失败？
A: 检查MySQL服务是否启动，以及配置文件中的数据库连接信息是否正确。

### Q: 端口8080被占用？
A: 修改 `application.yml` 中的 `server.port` 配置。

### Q: 如何查看SQL日志？
A: 在 `application.yml` 中设置 `spring.jpa.show-sql: true`。

## 📝 API文档

详细的API文档请参考 `README.md` 文件。

## 🛠️ 开发建议

1. **生产环境部署前**：
   - 修改JWT密钥
   - 启用HTTPS
   - 配置数据库连接池
   - 设置日志级别

2. **安全性增强**：
   - 使用BCrypt替代MD5加密
   - 添加请求频率限制
   - 实现完整的JWT认证
   - 添加SQL注入防护

3. **性能优化**：
   - 添加Redis缓存
   - 优化数据库查询
   - 实现分页查询
   - 添加数据库索引

## 📞 技术支持

如有问题，请查看项目文档或提交Issue。 