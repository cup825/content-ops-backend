-- 自动初始化测试数据（仅在开发环境使用）

-- 插入角色数据 (使用 INSERT IGNORE 避免重复插入)
INSERT IGNORE INTO role (role_name) VALUES ('ADMIN');
INSERT IGNORE INTO role (role_name) VALUES ('OPERATOR');
INSERT IGNORE INTO role (role_name) VALUES ('REVIEWER');

-- 插入用户数据 (使用 INSERT IGNORE 避免重复插入)
-- 管理员
INSERT IGNORE INTO user (username, password, role_id, created_at)
VALUES ('admin', 'admin123', 1, NOW());

-- 运营人员 (OPERATOR) - 多个
INSERT IGNORE INTO user (username, password, role_id, created_at)
VALUES ('operator1', 'operator123', 2, NOW());
INSERT IGNORE INTO user (username, password, role_id, created_at)
VALUES ('operator2', 'operator123', 2, NOW());
INSERT IGNORE INTO user (username, password, role_id, created_at)
VALUES ('operator3', 'operator123', 2, NOW());

-- 审核员 (REVIEWER) - 多个
INSERT IGNORE INTO user (username, password, role_id, created_at)
VALUES ('reviewer1', 'reviewer123', 3, NOW());
INSERT IGNORE INTO user (username, password, role_id, created_at)
VALUES ('reviewer2', 'reviewer123', 3, NOW());

-- 插入权限数据 (使用 INSERT IGNORE 避免重复插入)
INSERT IGNORE INTO permission (permission_name) VALUES ('CREATE_CONTENT');
INSERT IGNORE INTO permission (permission_name) VALUES ('REVIEW_CONTENT');
INSERT IGNORE INTO permission (permission_name) VALUES ('DELETE_CONTENT');

