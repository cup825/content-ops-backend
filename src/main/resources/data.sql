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

