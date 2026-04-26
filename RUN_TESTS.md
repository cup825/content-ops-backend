# 快速测试脚本

## 运行所有分页测试

### Windows PowerShell
```powershell
# 进入项目目录
cd D:\_JAVA\_byteDance\content-ops-backend

# 清理并运行所有测试
mvn clean test

# 或运行特定的测试类
mvn test -Dtest=PermissionManageServicePaginationTest
mvn test -Dtest=PermissionControllerPaginationTest
```

### Mac/Linux
```bash
# 进入项目目录
cd D:/_JAVA/_byteDance/content-ops-backend

# 清理并运行所有测试
mvn clean test

# 或运行特定测试
mvn test -Dtest=PermissionManageServicePaginationTest
mvn test -Dtest=PermissionControllerPaginationTest
```

---

## 预期输出

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.bytedance.content.admin.service.PermissionManageServicePaginationTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.345 s - in com.bytedance.content.admin.service.PermissionManageServicePaginationTest
[INFO] 
[INFO] Running com.bytedance.content.admin.controller.PermissionControllerPaginationTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 3.456 s - in com.bytedance.content.admin.controller.PermissionControllerPaginationTest
[INFO] 
[INFO] -------------------------------------------------------
[INFO] Tests run: 20, Failures: 0, Errors: 0, Skipped: 0
[INFO] -------------------------------------------------------
[INFO] BUILD SUCCESS
```

---

## 在 IDE 中运行单个测试

### IntelliJ IDEA
1. 打开 `PermissionManageServicePaginationTest.java`
2. 右键点击类名或方法名
3. 选择 "Run 'TestClassName'" 或 "Run 'testMethodName()'"
4. 查看 "Run" 窗口的结果

### VS Code（需要 Java Test Runner 扩展）
1. 打开测试文件
2. 点击方法名上方的 "Run Test" 代码操作
3. 查看测试结果

---

## 解决常见编译问题

### 问题1：找不到测试类
```bash
# 方案：重新编译
mvn clean compile
```

### 问题2：依赖版本冲突
```bash
# 方案：更新依赖
mvn dependency:resolve
mvn clean install
```

### 问题3：测试数据库连接失败
```bash
# 确保 H2 数据库依赖已安装
mvn dependency:tree | grep h2
# 应该看到 h2 依赖
```

---

## 检查测试覆盖率

```bash
# 生成 JaCoCo 覆盖率报告
mvn clean test jacoco:report

# 报告位置
target/site/jacoco/index.html
```

打开 `target/site/jacoco/index.html` 可以看到详细的覆盖率数据。

