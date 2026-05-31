# 内容运营后台系统 (content-ops-backend)

## 项目简介

字节跳动实习项目，基于 Spring Boot 3.x 构建的内容运营管理后台。系统支持内容全生命周期管理（创建→审核→上线→下线），提供用户权限管理、内容审核、数据统计等核心功能。

## 技术栈

| 层次 | 技术 |
|---|---|
| 框架 | Spring Boot 3.x / Java 17 |
| 安全 | Spring Security + JWT |
| 持久层 | Spring Data JPA + Hibernate |
| 数据库 | MySQL 8.x (AWS RDS) / H2（测试） |
| 工具 | Lombok, BCrypt |

## 模块结构

```
content-ops-backend/
├── admin/      # 用户、角色、权限管理
├── audit/      # 内容审核流程
├── content/    # 内容 CRUD 与状态流转
├── stats/      # 数据统计
└── common/     # 公共组件（安全、异常、工具类）
```

## 环境要求

- Java 17+
- Maven 3.8+
- MySQL 8.x（或使用测试 H2 内存数据库）

## 环境变量配置

启动前必须配置以下环境变量，参考 `.env.example`：

| 变量名 | 说明 | 示例 |
|---|---|---|
| `DB_PASSWORD` | 数据库密码（必填） | `your_db_password` |
| `JWT_SECRET` | JWT 签名密钥（必填，建议32位以上） | `your_jwt_secret_key` |
| `DB_URL` | 数据库连接URL（可选，有默认值） | `jdbc:mysql://host:3306/db` |
| `DB_USERNAME` | 数据库用户名（可选，默认 admin） | `admin` |
| `JWT_EXPIRATION_MS` | Token 过期时间毫秒（可选，默认24小时） | `86400000` |

## 快速启动

**Windows PowerShell：**
```powershell
$env:DB_PASSWORD="your_password"
$env:JWT_SECRET="your_secret_key_at_least_32_chars"
./mvnw spring-boot:run
```

**Linux / macOS：**
```bash
export DB_PASSWORD=your_password
export JWT_SECRET=your_secret_key_at_least_32_chars
./mvnw spring-boot:run
```

启动后服务监听：`http://localhost:8080`

## 运行测试

```bash
./mvnw test
```

测试使用 H2 内存数据库，无需配置额外环境变量。

## API 接口总览

所有接口路径前缀：`/api/v1`

### 认证
| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/v1/auth/login` | 用户登录，返回 JWT Token |

### 用户管理（需 ADMIN 角色）
| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/v1/admin/users` | 分页查询用户 |
| POST | `/api/v1/admin/users` | 创建用户 |
| PUT | `/api/v1/admin/users/{id}` | 更新用户 |
| DELETE | `/api/v1/admin/users/{id}` | 删除用户 |

### 内容管理（需 OPERATOR 角色）
| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/v1/content` | 创建内容（初始为草稿） |
| PUT | `/api/v1/content/{id}` | 编辑内容（仅草稿可编辑） |
| DELETE | `/api/v1/content/{id}` | 删除内容 |
| POST | `/api/v1/content/{id}/submit` | 提交审核 |
| POST | `/api/v1/content/{id}/publish` | 发布上线 |
| POST | `/api/v1/content/{id}/offline` | 下线 |
| GET | `/api/v1/content` | 查询内容列表（支持分页和过滤） |

### 审核（需 REVIEWER 角色）
| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/v1/audit` | 审核内容（通过/拒绝） |

## 统一响应格式

所有接口均返回如下 JSON 结构：

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

常见错误码：`400` 参数错误 / `401` 未登录 / `403` 权限不足 / `404` 资源不存在 / `500` 服务器错误

## 内容状态流转

```
DRAFT（草稿）
  ↓ 提交审核
PENDING（待审核）
  ↓ 审核通过        ↓ 审核拒绝
APPROVED（通过）   REJECTED（拒绝）→ 修改后可重新提交
  ↓ 发布
ONLINE（已上线）
  ↓ 下线
OFFLINE（已下线）
```

