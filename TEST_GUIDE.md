# 分页API单元测试指南

## 📋 测试文件清单

### 1. Service 层单元测试
**文件**：`src/test/java/com/bytedance/content/admin/service/PermissionManageServicePaginationTest.java`

**测试范围**：
- ✅ 用户分页查询（第一页、第二页）
- ✅ 排序功能（升序、降序）
- ✅ 分页参数验证
- ✅ 默认值处理
- ✅ 数据映射完整性

**包含的测试方法**（共10个）：
```
1. testGetUsersByPage_FirstPage()           - 第一页查询
2. testGetUsersByPage_SecondPage()          - 第二页查询
3. testGetUsersByPage_WithSorting()         - 排序功能
4. testGetUsersByPage_DefaultPageSize()     - 默认pageSize
5. testGetUsersByPage_PageOutOfRange()      - 超范围处理
6. testPaginationRequest_GetPageNumber()    - 页码转换
7. testPaginationRequest_GetPageSize()      - pageSize处理
8. testPaginationRequest_SortOrder()        - 排序方向处理
9. testGetAllUsers_NoPageination()          - 全量查询
10. testUserResponse_DataMapping()          - 数据映射
```

### 2. Controller 层集成测试
**文件**：`src/test/java/com/bytedance/content/admin/controller/PermissionControllerPaginationTest.java`

**测试范围**：
- ✅ API 端点可达性
- ✅ HTTP 状态码（200, 400, 404）
- ✅ 响应格式验证
- ✅ 分页参数传递
- ✅ 角色管理API

**包含的测试方法**（共10个）：
```
1. testGetUsersByPage_Success()             - 成功获取分页
2. testGetUsersByPage_WithSorting()         - 带排序参数
3. testGetUsersByPage_SecondPage()          - 第二页请求
4. testGetUsersByPage_DefaultValues()       - 默认参数
5. testGetPermissionsByPage_Success()       - 权限分页
6. testGetRoleById_Success()                - 获取单个角色
7. testGetRoleById_NotFound()               - 角色不存在
8. testUpdateRole_NotSupported()            - 禁止修改角色
9. testDeleteRole_NotSupported()            - 禁止删除角色
10. testGetUsersByPage_ResponseFormat()     - 响应格式验证
```

### 3. 测试配置
**文件**：`src/test/resources/application-test.properties`

**配置说明**：
- 使用 H2 内存数据库（不影响真实数据库）
- 自动创建/删除测试表（ddl-auto: create-drop）
- 禁用SQL日志输出（提高测试速度）

---

## 🚀 运行测试

### 方法1：IDE中运行（推荐）

#### 运行单个测试类
1. 打开 `PermissionManageServicePaginationTest.java`
2. 右键点击类名 → `Run 'PermissionManageServicePaginationTest'`
3. 查看测试结果

#### 运行单个测试方法
1. 打开测试类
2. 右键点击方法名 → `Run 'testGetUsersByPage_FirstPage'`

#### 运行所有测试
1. 右键点击 `src/test` 目录
2. 选择 `Run Tests`

### 方法2：Maven命令运行

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=PermissionManageServicePaginationTest

# 运行特定测试方法
mvn test -Dtest=PermissionManageServicePaginationTest#testGetUsersByPage_FirstPage

# 运行并生成测试报告
mvn test -DreportFormat=plain
```

### 方法3：Gradle命令运行（如果使用Gradle）

```bash
# 运行所有测试
gradle test

# 运行特定测试类
gradle test --tests PermissionManageServicePaginationTest
```

---

## ✅ 预期测试结果

### 成功标志
```
✓ PermissionManageServicePaginationTest ............ PASSED (10 tests)
✓ PermissionControllerPaginationTest ............... PASSED (10 tests)
✓ Total Tests: 20
✓ Passed: 20
✗ Failed: 0
✓ Coverage: 分页相关代码 85%+
```

---

## 📊 测试覆盖范围

### Service 层覆盖
```
PermissionManageService.java
├── getUsersByPage()                 ✅ 覆盖
├── getPermissionsByPage()           ✅ 覆盖
├── getAllUsers()                    ✅ 覆盖
├── getAllPermissions()              ✅ 覆盖
└── getRoleById()                    ✅ 覆盖
```

### Controller 层覆盖
```
PermissionController.java
├── GET /api/admin/users/page        ✅ 覆盖
├── GET /api/admin/permissions/page  ✅ 覆盖
├── GET /api/admin/roles/{id}        ✅ 覆盖
├── PUT /api/admin/roles/{id}        ✅ 覆盖
└── DELETE /api/admin/roles/{id}     ✅ 覆盖
```

### DTO 层覆盖
```
PaginationRequest.java
├── getPageNumber()                  ✅ 覆盖
├── getPageSize()                    ✅ 覆盖
├── getSortOrder()                   ✅ 覆盖
└── 字段验证                         ✅ 覆盖
```

---

## 🔍 测试详解

### 1️⃣ Service 层测试示例

```java
@Test
public void testGetUsersByPage_FirstPage() {
    // 1. 构造请求参数
    PaginationRequest request = new PaginationRequest(1, 5, "id", "asc");
    
    // 2. 调用方法
    PaginationResponse<UserResponse> response = 
        permissionManageService.getUsersByPage(request);
    
    // 3. 验证结果
    assertEquals(5, response.getContent().size());      // 第一页有5条
    assertEquals(10, response.getTotal());              // 总共10条
    assertEquals(1, response.getPage());                // 第1页
    assertEquals(2, response.getTotalPages());          // 共2页
}
```

**验证点**：
- ✓ 返回正确数量的数据
- ✓ 分页信息正确（total, totalPages）
- ✓ 数据排序正确

### 2️⃣ Controller 层测试示例

```java
@Test
public void testGetUsersByPage_Success() throws Exception {
    // 1. 发送HTTP请求
    MvcResult result = mockMvc.perform(
        get("/api/admin/users/page")
            .param("page", "1")
            .param("pageSize", "5")
    )
    .andExpect(status().isOk())      // 验证HTTP 200
    .andReturn();
    
    // 2. 验证响应体格式
    String content = result.getResponse().getContentAsString();
    assertTrue(content.contains("\"total\""));
    assertTrue(content.contains("\"page\""));
}
```

**验证点**：
- ✓ HTTP 状态码是 200
- ✓ 响应包含必需字段
- ✓ JSON格式正确

---

## 🐛 常见问题排查

### Q1: 测试运行失败 - "Unable to start nested embedded Tomcat"
**原因**：端口被占用或依赖冲突
**解决**：
```bash
# 清理并重新编译
mvn clean compile
mvn test
```

### Q2: 测试无法找到 H2 数据库驱动
**原因**：H2 依赖未导入（通常由 spring-boot-starter-test 提供）
**解决**：确保 pom.xml 包含 `spring-boot-starter-test`

### Q3: 测试数据库操作失败
**原因**：用户表不存在（未自动创建）
**解决**：
```properties
# 在 application-test.properties 中设置
spring.jpa.hibernate.ddl-auto=create-drop  # 自动创建表
```

### Q4: 某个测试时而成功时而失败（间歇性失败）
**原因**：测试间有数据污染，setUp() 未完全清理
**解决**：
```java
@BeforeEach
public void setUp() {
    // 必须完整清理所有表
    userRepository.deleteAll();
    roleRepository.deleteAll();
}
```

---

## 📈 代码覆盖率检查

### 使用 JaCoCo 插件生成覆盖率报告

1. **在 pom.xml 中添加依赖**：
```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
</plugin>
```

2. **运行测试并生成报告**：
```bash
mvn clean test jacoco:report
```

3. **查看覆盖率报告**：
```
target/site/jacoco/index.html
```

---

## ✨ 总结

- ✅ **20个单元测试** 覆盖分页功能
- ✅ **10个 Service 层测试** 验证业务逻辑
- ✅ **10个 Controller 层测试** 验证API端点
- ✅ **完整的测试配置** 使用 H2 内存数据库
- ✅ **清晰的测试文档** 便于维护和扩展

通过这些测试，可以确保分页功能的正确性和稳定性！

