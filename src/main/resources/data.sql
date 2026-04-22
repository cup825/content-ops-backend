-- 自动初始化测试数据（仅在开发环境使用）

-- 插入角色数据 (使用 INSERT IGNORE 避免重复插入)
INSERT IGNORE INTO role (role_name) VALUES ('ADMIN');
INSERT IGNORE INTO role (role_name) VALUES ('OPERATOR');
INSERT IGNORE INTO role (role_name) VALUES ('REVIEWER');

-- 插入用户数据 (使用 INSERT IGNORE 避免重复插入)
-- 管理员 (密码: admin123 - BCrypt加密)
INSERT IGNORE INTO user (username, password, role_id, created_at)
VALUES ('admin', '$2a$10$slYQmyNdGzin7olVN3DONOr8htsbK55yKpxFZpf0dVVAFhE7aZZwO', 1, NOW());

-- 运营人员 (OPERATOR) - 多个 (密码: operator123 - BCrypt加密)
INSERT IGNORE INTO user (username, password, role_id, created_at)
VALUES ('operator1', '$2a$10$6cEjOaECL.a4qMBDhVvGK.F4sQ3M6YfJNGzYLQQqI5nO3KfqRQNfm', 2, NOW());
INSERT IGNORE INTO user (username, password, role_id, created_at)
VALUES ('operator2', '$2a$10$6cEjOaECL.a4qMBDhVvGK.F4sQ3M6YfJNGzYLQQqI5nO3KfqRQNfm', 2, NOW());
INSERT IGNORE INTO user (username, password, role_id, created_at)
VALUES ('operator3', '$2a$10$6cEjOaECL.a4qMBDhVvGK.F4sQ3M6YfJNGzYLQQqI5nO3KfqRQNfm', 2, NOW());

-- 审核员 (REVIEWER) - 多个 (密码: reviewer123 - BCrypt加密)
INSERT IGNORE INTO user (username, password, role_id, created_at)
VALUES ('reviewer1', '$2a$10$H4hJGkN7aMQRCBCQfXKhB.nFQBrK0t2kN9P3w2YmK.sN5B8m0w2mK', 3, NOW());
INSERT IGNORE INTO user (username, password, role_id, created_at)
VALUES ('reviewer2', '$2a$10$H4hJGkN7aMQRCBCQfXKhB.nFQBrK0t2kN9P3w2YmK.sN5B8m0w2mK', 3, NOW());

-- ====================================================
-- 插入权限数据 (根据角色职责分类)
-- role_id: 1=ADMIN, 2=OPERATOR, 3=REVIEWER
-- ====================================================

-- 管理员权限 (权限模块相关) - role_id=1
INSERT IGNORE INTO permission (permission_name, role_id) VALUES ('ADMIN_USER_MANAGE', 1);
INSERT IGNORE INTO permission (permission_name, role_id) VALUES ('ADMIN_ROLE_MANAGE', 1);
INSERT IGNORE INTO permission (permission_name, role_id) VALUES ('ADMIN_PERMISSION_MANAGE', 1);
INSERT IGNORE INTO permission (permission_name, role_id) VALUES ('ADMIN_SYSTEM_CONFIG', 1);

-- 运营人员权限 (内容模块相关) - role_id=2
INSERT IGNORE INTO permission (permission_name, role_id) VALUES ('OPERATOR_CREATE_CONTENT', 2);
INSERT IGNORE INTO permission (permission_name, role_id) VALUES ('OPERATOR_EDIT_CONTENT', 2);
INSERT IGNORE INTO permission (permission_name, role_id) VALUES ('OPERATOR_VIEW_CONTENT', 2);
INSERT IGNORE INTO permission (permission_name, role_id) VALUES ('OPERATOR_SUBMIT_REVIEW', 2);

-- 审核员权限 (审核模块相关) - role_id=3
INSERT IGNORE INTO permission (permission_name, role_id) VALUES ('REVIEWER_AUDIT_CONTENT', 3);
INSERT IGNORE INTO permission (permission_name, role_id) VALUES ('REVIEWER_VIEW_PENDING_CONTENT', 3);

