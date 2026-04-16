package com.bytedance.content.permission.service;

import com.bytedance.content.common.exception.BusinessException;
import com.bytedance.content.permission.entity.User;
import com.bytedance.content.permission.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PermissionService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取用户信息并验证权限
     */
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }

    /**
     * 检查用户是否有创建内容的权限（OPERATOR 或 ADMIN）
     */
    public boolean canCreateContent(Long userId) {
        User user = getUserById(userId);
        String roleName = user.getRole().getRoleName().toString();
        return "ADMIN".equals(roleName) || "OPERATOR".equals(roleName);
    }

    /**
     * 检查用户是否有审核权限（REVIEWER 或 ADMIN）
     */
    public boolean canAuditContent(Long userId) {
        User user = getUserById(userId);
        String roleName = user.getRole().getRoleName().toString();
        return "ADMIN".equals(roleName) || "REVIEWER".equals(roleName);
    }

    /**
     * 检查用户是否是内容创建人
     */
    public boolean isContentCreator(Long userId, Long creatorId) {
        return userId.equals(creatorId);
    }
}

