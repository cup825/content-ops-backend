-- ====================================================
-- 📝 测试数据生成脚本 V2（改进版）
-- 正确的状态转换流程（通过 API 调用逻辑模拟）
-- ====================================================

-- 清理现有数据（可选，根据需要取消注释）
-- DELETE FROM operation_log;
-- DELETE FROM audit_record;
-- DELETE FROM content;

-- ====================================================
-- ⚠️ 重要说明
-- ====================================================
-- 这个脚本模拟了通过 API 调用的完整业务流程
-- 每个内容的状态转换都遵循系统的业务规则：
-- 1. 创建 → DRAFT（自动）
-- 2. 提交审核 → PENDING（需要从 DRAFT 或 REJECTED）
-- 3. 审核 → APPROVED 或 REJECTED（从 PENDING）
-- 4. 发布 → ONLINE（从 APPROVED）
-- 5. 下线 → OFFLINE（从 ONLINE）

-- ====================================================
-- 第1组：DRAFT 状态（草稿）
-- 由 operator1 创建，不提交审核
-- ====================================================
INSERT INTO content (title, content, status, creator_id, created_at, updated_at)
VALUES ('Spring Boot 最佳实践', 'Spring Boot 框架的最佳实践和常见陷阱...', 'DRAFT', 2, NOW(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (2, 'CREATE_CONTENT', LAST_INSERT_ID(), NOW());

INSERT INTO content (title, content, status, creator_id, created_at, updated_at)
VALUES ('Java 并发编程指南', 'Java 并发编程的深入理解和实践指导...', 'DRAFT', 2, NOW(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (2, 'CREATE_CONTENT', LAST_INSERT_ID(), NOW());

-- ====================================================
-- 第2组：PENDING 状态（待审核）
-- 由 operator2 创建后提交审核
-- 流程：DRAFT → PENDING
-- ====================================================
INSERT INTO content (title, content, status, creator_id, created_at, updated_at)
VALUES ('数据库性能优化技巧', '关于数据库查询性能的优化建议...', 'PENDING', 3, NOW(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (3, 'CREATE_CONTENT', LAST_INSERT_ID(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (3, 'SUBMIT_REVIEW', LAST_INSERT_ID(), NOW());

-- ====================================================
-- 第3组：APPROVED 状态（审核通过）
-- 由 operator1 创建 → 提交审核 → reviewer1 审核通过
-- 流程：DRAFT → PENDING → APPROVED
-- ====================================================
INSERT INTO content (title, content, status, creator_id, created_at, updated_at)
VALUES ('微服务架构设计', '微服务架构的设计原则和实现方案...', 'APPROVED', 2, NOW(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (2, 'CREATE_CONTENT', LAST_INSERT_ID(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (2, 'SUBMIT_REVIEW', LAST_INSERT_ID(), NOW());

-- 创建审核记录
INSERT INTO audit_record (content_id, reviewer_id, status, comment, created_at)
VALUES (LAST_INSERT_ID(), 4, 'APPROVED', '内容质量不错，审核通过', NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (4, 'APPROVE_CONTENT', LAST_INSERT_ID(), NOW());

-- ====================================================
-- 第4组：REJECTED 状态（审核拒绝）
-- 由 operator3 创建 → 提交审核 → reviewer2 审核拒绝
-- 流程：DRAFT → PENDING → REJECTED
-- ====================================================
INSERT INTO content (title, content, status, creator_id, created_at, updated_at)
VALUES ('API 设计规范', 'RESTful API 的设计规范和最佳实践...', 'REJECTED', 4, NOW(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (4, 'CREATE_CONTENT', LAST_INSERT_ID(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (4, 'SUBMIT_REVIEW', LAST_INSERT_ID(), NOW());

-- 创建审核记录
INSERT INTO audit_record (content_id, reviewer_id, status, comment, created_at)
VALUES (LAST_INSERT_ID(), 5, 'REJECTED', '内容不符合规范，请补充更多技术细节', NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (5, 'REJECT_CONTENT', LAST_INSERT_ID(), NOW());

-- ====================================================
-- 第5组：ONLINE 状态（已上线）
-- 由 operator1 创建 → 提交审核 → reviewer1 审核通过 → 发布上线
-- 流程：DRAFT → PENDING → APPROVED → ONLINE
-- ====================================================
INSERT INTO content (title, content, status, creator_id, created_at, updated_at)
VALUES ('Git 工作流最佳实践', 'Git 的分支管理和工作流程规范...', 'ONLINE', 2, NOW(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (2, 'CREATE_CONTENT', LAST_INSERT_ID(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (2, 'SUBMIT_REVIEW', LAST_INSERT_ID(), NOW());

-- 创建审核记录
INSERT INTO audit_record (content_id, reviewer_id, status, comment, created_at)
VALUES (LAST_INSERT_ID(), 4, 'APPROVED', '审核通过，可以发布', NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (4, 'APPROVE_CONTENT', LAST_INSERT_ID(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (2, 'PUBLISH_CONTENT', LAST_INSERT_ID(), NOW());

-- ====================================================
-- 第6组：OFFLINE 状态（已下线）
-- 由 operator2 创建 → 审核通过 → 发布上线 → 下线
-- 流程：DRAFT → PENDING → APPROVED → ONLINE → OFFLINE
-- ====================================================
INSERT INTO content (title, content, status, creator_id, created_at, updated_at)
VALUES ('Docker 容器化指南', 'Docker 的基础使用和最佳实践...', 'OFFLINE', 3, NOW(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (3, 'CREATE_CONTENT', LAST_INSERT_ID(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (3, 'SUBMIT_REVIEW', LAST_INSERT_ID(), NOW());

-- 创建审核记录
INSERT INTO audit_record (content_id, reviewer_id, status, comment, created_at)
VALUES (LAST_INSERT_ID(), 5, 'APPROVED', '通过审核', NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (5, 'APPROVE_CONTENT', LAST_INSERT_ID(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (3, 'PUBLISH_CONTENT', LAST_INSERT_ID(), NOW());

INSERT INTO operation_log (user_id, action, target_id, created_at)
VALUES (3, 'OFFLINE_CONTENT', LAST_INSERT_ID(), NOW());

-- ====================================================
-- ✅ 验证数据完整性
-- ====================================================
SELECT '====== 用户数据 ======' AS info;
SELECT id, username, role_id FROM user ORDER BY id;

SELECT '====== 内容数据统计 ======' AS info;
SELECT status, COUNT(*) as count FROM content GROUP BY status ORDER BY status;

SELECT '====== 内容详情 ======' AS info;
SELECT id, title, status, creator_id, created_at FROM content ORDER BY id;

SELECT '====== 操作日志统计 ======' AS info;
SELECT action, COUNT(*) as count FROM operation_log GROUP BY action ORDER BY action;

SELECT '====== 审核记录 ======' AS info;
SELECT id, content_id, reviewer_id, status, comment, created_at FROM audit_record ORDER BY id;

-- ====================================================
-- 📊 期望结果
-- ====================================================
-- status      | count
-- ------------|-------
-- APPROVED    | 1
-- DRAFT       | 2
-- OFFLINE     | 1
-- ONLINE      | 1
-- PENDING     | 1
-- REJECTED    | 1
--
-- 总共 7 条内容记录，涵盖所有 6 种状态


