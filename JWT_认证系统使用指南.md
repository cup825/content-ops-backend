# JWT 认证系统使用指南

## 概述

本项目已升级为完整的 JWT（JSON Web Token）认证系统，替代了不安全的 X-User-Id header 传递方案。

## 核心改进点

### 问题修复
- ✅ **取消纯靠客户端"信任"的模式** - 现在使用加密的 JWT token
- ✅ **防止 User ID 伪造** - Token 由服务器签名并在每次请求时验证
- ✅ **添加正规认证流程** - 需要用户名/密码登录才能获得 token
- ✅ **向后兼容** - 仍支持 X-User-Id header，但 JWT 优先级更高

## 使用流程

### 1. 用户登录

**请求：**
```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "admin",
    "password": "123456"
}
```

**成功响应 (200)：**
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsInVzZXJuYW1lIjoiYWRtaW4iLCJyb2xlIjoiQURNSU4iLCJpYXQiOjE3MTk0MzI0NzQsImV4cCI6MTcxOTUxODg3NH0.abc123...",
    "tokenType": "Bearer",
    "expiresIn": 86400000,
    "userId": 1,
    "username": "admin",
    "role": "ADMIN"
}
```

**失败响应 (401)：**
```json
{
    "error": "用户名或密码错误",
    "code": 401
}
```

### 2. 使用 Token 发送认证请求

所有受保护的端点都需要在请求头中提供 JWT token：

```http
GET /api/admin/users/page
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 3. Token 过期处理

- Token 默认有效期：**24 小时**（可在 `application.properties` 中配置 `app.jwtExpirationMs`）
- Token 过期后，需要重新登录获取新 token

**过期响应 (401)：**
```json
{
    "error": "JWT token 已过期",
    "code": 401
}
```

## API 端点权限配置

| 端点 | 方法 | 权限要求 | 认证方式 |
|------|------|--------|--------|
| `/api/auth/login` | POST | 无 | 无需认证 |
| `/api/admin/stats` | GET | 需要认证 | JWT |
| `/api/admin/users/page` | GET | 需要认证 | JWT |
| `/api/admin/users` | POST | ADMIN | JWT |
| `/api/admin/users/{id}` | PUT | ADMIN | JWT |
| `/api/admin/users/{id}` | DELETE | ADMIN | JWT |
| `/api/admin/permissions` | POST | ADMIN | JWT |
| `/api/admin/permissions/{id}` | DELETE | ADMIN | JWT |

## 配置说明

### application.properties

```properties
# JWT 配置
app.jwtSecret=mySecretKeyThatIsLongEnoughFor256BitHS256AlgorithmHereWithMinimumLength
app.jwtExpirationMs=86400000
```

**参数说明：**
- `app.jwtSecret` - JWT 签名密钥（至少 32 字符用于 HS256 算法）
- `app.jwtExpirationMs` - Token 过期时间（毫秒），86400000 = 24小时

## 核心类说明

### JwtTokenProvider
- **位置：** `com.bytedance.content.common.utils.JwtTokenProvider`
- **功能：** JWT token 的生成、验证和解析
- **关键方法：**
  - `generateToken(userId, username, role)` - 生成 token
  - `validateToken(token)` - 验证 token 有效性
  - `getUserIdFromToken(token)` - 提取用户 ID
  - `getRoleFromToken(token)` - 提取用户角色

### JwtAuthenticationFilter
- **位置：** `com.bytedance.content.common.security.JwtAuthenticationFilter`
- **功能：** 拦截请求，验证并解析 JWT token
- **工作流程：** 从 Authorization header 提取 token → 验证 → 设置 SecurityContext

### AuthenticationService
- **位置：** `com.bytedance.content.admin.service.AuthenticationService`
- **功能：** 处理用户登录逻辑
- **关键方法：** `login(loginRequest)` - 验证用户凭证并返回 token

### SecurityUtil
- **位置：** `com.bytedance.content.common.utils.SecurityUtil`
- **功能：** 从 Spring Security 上下文获取当前用户信息
- **关键方法：**
  - `getCurrentUserId()` - 获取当前用户 ID
  - `getCurrentUsername()` - 获取当前用户名
  - `isAdmin()` - 检查是否是 ADMIN 角色

## 集成示例

### Java 后端调用

```java
@RestController
@RequestMapping("/api/content")
public class ContentController {
    
    @PostMapping("/create")
    public CreateContentResponse createContent(@RequestBody CreateContentRequest request) {
        // 从安全上下文获取当前用户 ID
        Long currentUserId = SecurityUtil.getCurrentUserId();
        String currentUsername = SecurityUtil.getCurrentUsername();
        
        // 检查权限
        if (!SecurityUtil.isAdmin()) {
            throw new BusinessException(403, "仅管理员可创建内容");
        }
        
        return contentService.createContent(currentUserId, request);
    }
}
```

### JavaScript/前端调用

```javascript
// 1. 登录获取 token
async function login() {
    const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            username: 'admin',
            password: '123456'
        })
    });
    
    const data = await response.json();
    if (response.ok) {
        // 保存 token 到 localStorage
        localStorage.setItem('accessToken', data.accessToken);
        return data;
    } else {
        throw new Error(data.error);
    }
}

// 2. 使用 token 发送认证请求
async function getUsers() {
    const token = localStorage.getItem('accessToken');
    const response = await fetch('/api/admin/users/page', {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });
    
    if (response.status === 401) {
        // Token 过期，需要重新登录
        await login();
        return getUsers(); // 重试
    }
    
    return response.json();
}

// 3. 拦截器示例（使用 Axios）
import axios from 'axios';

const api = axios.create({
    baseURL: '/api'
});

// 请求拦截器
api.interceptors.request.use(config => {
    const token = localStorage.getItem('accessToken');
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});

// 响应拦截器
api.interceptors.response.use(
    response => response,
    error => {
        if (error.response?.status === 401) {
            // Token 过期，清除并重定向到登录页
            localStorage.removeItem('accessToken');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);

export default api;
```

## 测试命令

### 使用 cURL 测试

```bash
# 1. 登录
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}'

# 2. 使用返回的 token 进行请求
curl -X GET http://localhost:8080/api/admin/users/page \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

# 3. 创建用户
curl -X POST http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json" \
  -d '{"username":"newuser","password":"password123","roleId":2}'
```

## 安全最佳实践

1. **不要在 URL 中传递 token**
   ```javascript
   // ❌ 错误
   fetch('/api/users?token=xxx');
   
   // ✅ 正确
   fetch('/api/users', {
       headers: { 'Authorization': 'Bearer xxx' }
   });
   ```

2. **存储 token 的安全考虑**
   ```javascript
   // 选项 1: 存储在内存中（刷新页面丢失）
   let token;
   
   // 选项 2: 存储在 localStorage（XSS 脆弱，但便于跨页面访问）
   localStorage.setItem('token', token);
   
   // 选项 3: HttpOnly Cookie（最安全）
   // 需要后端设置 Set-Cookie header
   ```

3. **Token 过期处理**
   - 前端应检测 401 响应并提示用户重新登录
   - 自动刷新 token（可选）：发送旧 token 获取新 token

4. **HTTPS 部署**
   - 生产环境必须使用 HTTPS 传输 token
   - Token 包含敏感的用户信息

## 迁移指南

### 旧项目（使用 X-User-Id）迁移步骤

1. **更新依赖**
   ```bash
   mvn clean install
   ```

2. **配置环境变量**
   - 确保 `application.properties` 中的 JWT 配置正确

3. **前端代码迁移**
   - 调用 `/api/auth/login` 获取 token
   - 使用 `Authorization: Bearer` header 替代 `X-User-Id`

4. **向后兼容**
   - 系统仍支持 X-User-Id header（但 JWT 优先）
   - 建议立即切换到 JWT

## 故障排除

### 错误：Token 无效
```json
{"error": "无效的 JWT token: ...", "code": 401}
```
**原因：** token 被篡改或使用了错误的密钥
**解决：** 重新登录获取新 token

### 错误：Token 已过期
```json
{"error": "JWT token 已过期", "code": 401}
```
**原因：** token 超过了有效期
**解决：** 重新登录获取新 token

### 错误：未认证
```json
{"error": "未认证，请先登录", "code": 401}
```
**原因：** 请求中没有有效的 token
**解决：** 先调用登录接口获取 token

### 错误：权限不足
```json
{"error": "无权限访问此资源", "code": 403}
```
**原因：** 用户角色权限不足
**解决：** 使用具有相应权限的账号

## 常见问题

**Q: 如何更新 JWT 密钥？**
A: 修改 `application.properties` 中的 `app.jwtSecret`。注意：更改密钥后，所有现有的 token 将失效。

**Q: 支持刷新 token 吗？**
A: 当前版本没有实现刷新 token，用户需要重新登录。可在后续版本添加此功能。

**Q: JWT token 包含哪些信息？**
A: userId、username、role、issuedAt、expiresAt。这些信息是可见的但无法篡改。

**Q: 如何登出？**
A: 删除客户端保存的 token 即可。后端不需要维护登出列表（stateless）。

---

**文档版本：** 1.0  
**更新时间：** 2024-04-24  
**适用版本：** 0.0.1-SNAPSHOT 及以后

