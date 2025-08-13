# 相亲社交APP API使用指南

## 推荐注册登录流程

### 主要流程：手机验证码登录/注册 + 分步注册

这是推荐的用户注册登录流程，适合移动端使用：

#### 1. 手机验证码登录/注册（合二为一）

```bash
# 发送验证码
POST /api/auth/send-verification-code?phoneNumber=13800138000

# 手机验证码登录/注册
POST /api/auth/phone-login?phoneNumber=13800138000&verificationCode=123456
```

**响应示例：**
```json
// 新用户（开始注册流程）
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "temp_token_123_1640995200000",
    "expiresIn": 86400000,
    "message": "开始注册流程"
  }
}

// 老用户（直接登录）
{
  "success": true,
  "message": "登录成功",
  "data": {
    "token": "token_123_1640995200000",
    "refreshToken": "refresh_123_1640995200000",
    "expiresIn": 86400000,
    "userInfo": {
      "id": 123,
      "username": "user123",
      "email": "user@example.com",
      "nickname": "用户昵称",
      "avatarUrl": "avatar.jpg",
      "role": "USER",
      "isVerified": true
    },
    "message": "登录成功"
  }
}
```

#### 2. 分步注册流程

用户完成手机验证码登录后，如果是新用户，系统会创建临时用户并开始分步注册流程：

```bash
# 步骤1：选择性别
POST /api/auth/step-register
{
  "step": 1,
  "phoneNumber": "13800138000",
  "gender": "MALE"
}

# 步骤2：身高和年龄
POST /api/auth/step-register
{
  "step": 2,
  "phoneNumber": "13800138000",
  "age": 25,
  "height": 175,
  "weight": 65,
  "isWeightPrivate": false
}

# 步骤3：学历
POST /api/auth/step-register
{
  "step": 3,
  "phoneNumber": "13800138000",
  "educationLevel": "BACHELOR"
}

# 步骤4：学校
POST /api/auth/step-register
{
  "step": 4,
  "phoneNumber": "13800138000",
  "school": "北京大学"
}

# 步骤5：公司性质和工作
POST /api/auth/step-register
{
  "step": 5,
  "phoneNumber": "13800138000",
  "companyType": "PRIVATE",
  "occupation": "软件工程师",
  "incomeLevel": "TEN_TO_TWENTY"
}

# 步骤6：地理位置
POST /api/auth/step-register
{
  "step": 6,
  "phoneNumber": "13800138000",
  "currentProvince": "北京",
  "currentCity": "北京",
  "currentDistrict": "朝阳区",
  "hometownProvince": "山东",
  "hometownCity": "济南",
  "hometownDistrict": "历下区"
}

# 步骤7：买房买车情况
POST /api/auth/step-register
{
  "step": 7,
  "phoneNumber": "13800138000",
  "houseStatus": "HOUSE_WITH_LOAN",
  "carStatus": "NO_CAR"
}

# 步骤8：婚姻状况
POST /api/auth/step-register
{
  "step": 8,
  "phoneNumber": "13800138000",
  "maritalStatus": "SINGLE",
  "childrenStatus": "NO_CHILDREN"
}

# 步骤9：恋爱观念
POST /api/auth/step-register
{
  "step": 9,
  "phoneNumber": "13800138000",
  "loveAttitude": "SERIOUS_LOVE_MARRY",
  "preferredAgeMin": 22,
  "preferredAgeMax": 28,
  "marriagePlan": "WITHIN_TWO_YEARS"
}

# 步骤10：昵称和头像
POST /api/auth/step-register
{
  "step": 10,
  "phoneNumber": "13800138000",
  "nickname": "阳光男孩",
  "avatarUrl": "avatar.jpg"
}

# 步骤11：生活照
POST /api/auth/step-register
{
  "step": 11,
  "phoneNumber": "13800138000",
  "lifePhotos": "[\"photo1.jpg\",\"photo2.jpg\",\"photo3.jpg\"]"
}

# 步骤12：关于我-性格
POST /api/auth/step-register
{
  "step": 12,
  "phoneNumber": "13800138000",
  "selfIntroduction": "我是天秤座，性格热情喜欢认识新朋友，也会好好对自己喜欢的人。"
}

# 步骤13：关于我-生活
POST /api/auth/step-register
{
  "step": 13,
  "phoneNumber": "13800138000",
  "lifestyle": "我是一名外科医生，平时很忙，医院家里两点一线，吃饭只能食堂解决。"
}

# 步骤14：关于我-理想伴侣
POST /api/auth/step-register
{
  "step": 14,
  "phoneNumber": "13800138000",
  "idealPartner": "我的理想型是性格乐观开朗，温柔顾家的人。希望恋爱中不要冷战。"
}

# 步骤15：我想认识的人
POST /api/auth/step-register
{
  "step": 15,
  "phoneNumber": "13800138000",
  "userTags": "[\"LOVE_SPORTS\",\"HUMOROUS\",\"SUNNY\"]"
}

# 步骤16：实名认证（最后一步）
POST /api/auth/step-register
{
  "step": 16,
  "phoneNumber": "13800138000",
  "realName": "张三",
  "idCardNumber": "110101199001011234",
  "preferredTags": "[\"GOOD_TEMPERAMENT\",\"SUNSHINE\",\"KIND_HEARTED\"]"
}
```

#### 3. 获取注册进度

```bash
# 获取用户注册进度
GET /api/auth/registration-progress?phoneNumber=13800138000

# 获取用户状态
GET /api/auth/user-status?phoneNumber=13800138000
```

**用户状态响应示例：**
```json
{
  "success": true,
  "message": "操作成功",
  "data": {
    "exists": true,
    "registrationStep": 5,
    "isVerified": false,
    "isComplete": false,
    "userId": 123
  }
}
```

### 备用流程：传统用户名密码登录

```bash
# 用户名密码登录
POST /api/auth/login
{
  "usernameOrEmail": "testuser",
  "password": "password123"
}
```

### 完整注册流程（主要用于测试）

```bash
# 一次性完整注册
POST /api/auth/register
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "phoneNumber": "13800138000",
  // ... 其他所有字段
}
```

## 前端实现建议

### 1. 移动端流程

```javascript
// 1. 用户输入手机号，点击发送验证码
async function sendVerificationCode(phoneNumber) {
  const response = await fetch('/api/auth/send-verification-code?phoneNumber=' + phoneNumber);
  // 显示倒计时，防止重复发送
}

// 2. 用户输入验证码，点击登录/注册
async function phoneLogin(phoneNumber, verificationCode) {
  const response = await fetch('/api/auth/phone-login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: `phoneNumber=${phoneNumber}&verificationCode=${verificationCode}`
  });
  
  const result = await response.json();
  
  if (result.data.message === "开始注册流程") {
    // 新用户，跳转到分步注册页面
    startStepRegistration(phoneNumber, result.data.token);
  } else {
    // 老用户，直接进入应用
    loginSuccess(result.data);
  }
}

// 3. 分步注册
async function stepRegister(stepData) {
  const response = await fetch('/api/auth/step-register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(stepData)
  });
  
  const result = await response.json();
  
  if (stepData.step === 16) {
    // 注册完成，跳转到主页面
    registrationComplete(result.data);
  } else {
    // 继续下一步
    goToNextStep(stepData.step + 1);
  }
}
```

### 2. 注册进度管理

```javascript
// 检查用户注册进度
async function checkRegistrationProgress(phoneNumber) {
  const response = await fetch('/api/auth/user-status?phoneNumber=' + phoneNumber);
  const result = await response.json();
  
  if (result.data.exists) {
    if (result.data.isComplete) {
      // 用户已完成注册，直接登录
      return 'COMPLETE';
    } else {
      // 用户未完成注册，继续注册流程
      return 'IN_PROGRESS';
    }
  } else {
    // 新用户，开始注册
    return 'NEW_USER';
  }
}
```

## 优势总结

### 1. 用户体验优势
- **一键登录**：手机验证码登录，无需记住密码
- **智能识别**：自动识别新老用户，无需手动选择注册或登录
- **分步引导**：逐步收集信息，减少用户压力
- **进度保存**：支持中断后继续注册

### 2. 技术优势
- **统一入口**：手机验证码作为主要登录方式
- **灵活扩展**：保留传统登录作为备用方案
- **数据完整**：分步收集确保信息质量
- **状态管理**：完整的注册进度跟踪

### 3. 业务优势
- **提高转化率**：简化注册流程，提高完成率
- **数据质量**：分步收集提高信息准确性
- **移动友好**：适合移动端操作习惯
- **安全可靠**：手机验证码确保用户身份 