package com.bytedance.content.admin.service;

import com.bytedance.content.common.exception.BusinessException;
import com.bytedance.content.admin.entity.User;
import com.bytedance.content.admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 权限校验服务
 * 职责：提供权限校验接口，控制用户访问范围
 * 
 * 权限规划：
 * 管理员 (ADMIN)：系统配置、用户管理、角色管理、权限分配
 *   - ADMIN_USER_MANAGE / ADMIN_ROLE_MANAGE / ADMIN_PERMISSION_MANAGE / ADMIN_SYSTEM_CONFIG
 * 
 * 运营人员 (OPERATOR)：创建内容、修改内容、查看内容、提交审核
 *   - OPERATOR_CREATE_CONTENT / OPERATOR_EDIT_CONTENT / OPERATOR_VIEW_CONTENT / OPERATOR_SUBMIT_REVIEW
 * 
 * 审核员 (REVIEWER)：内容审核、查看待审核内容、执行通过/驳回
 *   - REVIEWER_AUDIT_CONTENT / REVIEWER_VIEW_PENDING_CONTENT
 */
@Service
@Transactional
public class PermissionCheckService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取用户信息并验证权限
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }

    // ====================================================
    // 权限模块权限检查（管理员）
    // ====================================================

    /**
     * 检查用户是否有用户管理权限（仅 ADMIN）
     */
    public boolean canManageUser(Long userId) {
        User user = getUserById(userId);
        return "ADMIN".equals(user.getRole().getRoleName().toString());
    }

    /**
     * 检查用户是否有角色管理权限（仅 ADMIN）
     */
    public boolean canManageRole(Long userId) {
        User user = getUserById(userId);
        return "ADMIN".equals(user.getRole().getRoleName().toString());
    }

    /**
     * 检查用户是否有权限管理权限（仅 ADMIN）
     */
    public boolean canManagePermission(Long userId) {
        User user = getUserById(userId);
        return "ADMIN".equals(user.getRole().getRoleName().toString());
    }

    // ====================================================
    // 内容模块权限检查（运营人员）
    // ====================================================

    /**
     * 检查用户是否有创建内容的权限（仅 OPERATOR）
     */
    public boolean canCreateContent(Long userId) {
        User user = getUserById(userId);
        return "OPERATOR".equals(user.getRole().getRoleName().toString());
    }

    /**
     * 检查用户是否有修改内容的权限（仅 OPERATOR 且为创建人）
     */
    public boolean canEditContent(Long userId, Long creatorId) {
        User user = getUserById(userId);
        if (!"OPERATOR".equals(user.getRole().getRoleName().toString())) {
            return false;
        }
        return userId.equals(creatorId);
    }

    /**
     * 检查用户是否有查看内容的权限（OPERATOR 可看自己的，ADMIN 可看所有）
     */
    public boolean canViewContent(Long userId) {
        User user = getUserById(userId);
        String roleName = user.getRole().getRoleName().toString();
        return "OPERATOR".equals(roleName) || "ADMIN".equals(roleName);
    }

    /**
     * 检查用户是否有提交审核的权限（仅 OPERATOR 且为创建人）
     */
    public boolean canSubmitReview(Long userId, Long creatorId) {
        User user = getUserById(userId);
        if (!"OPERATOR".equals(user.getRole().getRoleName().toString())) {
            return false;
        }
        return userId.equals(creatorId);
    }

    // ====================================================
    // 审核模块权限检查（审核员）
    // ====================================================

    /**
     * 检查用户是否有审核权限（仅 REVIEWER）
     */
    public boolean canAuditContent(Long userId) {
        User user = getUserById(userId);
        return "REVIEWER".equals(user.getRole().getRoleName().toString());
    }

    /**
     * 检查用户是否有查看待审核内容的权限（仅 REVIEWER）
     */
    public boolean canViewPendingContent(Long userId) {
        User user = getUserById(userId);
        return "REVIEWER".equals(user.getRole().getRoleName().toString());
    }

    // ====================================================
    // 内容操作辅助检查
    // ====================================================

    /**
     * 检查用户是否是内容创建人
     */
    public boolean isContentCreator(Long userId, Long creatorId) {
        return userId.equals(creatorId);
    }
}
