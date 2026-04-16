package com.bytedance.content.admin.service;

import com.bytedance.content.common.exception.BusinessException;
import com.bytedance.content.admin.dto.*;
import com.bytedance.content.admin.entity.Permission;
import com.bytedance.content.admin.entity.Role;
import com.bytedance.content.admin.entity.User;
import com.bytedance.content.admin.repository.PermissionRepository;
import com.bytedance.content.admin.repository.RoleRepository;
import com.bytedance.content.admin.repository.UserRepository;
import com.bytedance.content.content.service.OperationLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限管理服务
 * 职责：用户、角色、权限的增删改查管理，并记录操作日志
 * 
 * 权限校验说明：
 * - 所有修改操作（增删改）都需要 ADMIN 权限
 * - 越权操作会抛出 403 异常
 */
@Service
@Transactional
public class PermissionManageService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private OperationLogService operationLogService;

    @Autowired
    private PermissionCheckService permissionCheckService;

    // ====================================================
    // 权限校验方法（防止越权操作）
    // ====================================================

    /**
     * 校验操作人是否有权限执行该操作
     * @param operatorId 操作人ID
     * @param requiredRole 所需角色
     * @throws BusinessException 403 权限不足
     */
    private void checkPermission(Long operatorId, String requiredRole) {
        // 如果没有传入操作人ID，使用默认管理员ID
        if (operatorId == null) {
            operatorId = 1L; // 使用系统默认管理员
        }

        User operator = userRepository.findById(operatorId)
                .orElseThrow(() -> new BusinessException(403, "操作人不存在"));

        String userRole = operator.getRole().getRoleName().toString();
        if (!requiredRole.equals(userRole)) {
            throw new BusinessException(403, "无权限执行此操作，需要 " + requiredRole + " 角色");
        }
    }

    /**
     * 获取所有用户
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getRole().getRoleName().toString(),
                        user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""
                ))
                .collect(Collectors.toList());
    }

    /**
     * 根据 ID 获取用户
     */
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().getRoleName().toString(),
                user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""
        );
    }

    /**
     * 创建用户
     */
    public UserResponse createUser(Long operatorId, UserCreateRequest request) {
        // 权限校验：仅 ADMIN 可创建用户
        checkPermission(operatorId, "ADMIN");

        // 检查用户名是否已存在
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException(400, "用户名已存在");
        }

        // 检查角色是否存在
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // 实际应该进行加密
        user.setRole(role);

        User savedUser = userRepository.save(user);

        // 记录操作日志
        operationLogService.log(operatorId != null ? operatorId : 1L, "CREATE_USER", savedUser.getId());

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole().getRoleName().toString(),
                savedUser.getCreatedAt() != null ? savedUser.getCreatedAt().toString() : ""
        );
    }

    /**
     * 更新用户
     */
    public UserResponse updateUser(Long operatorId, Long userId, UserCreateRequest request) {
        // 权限校验：仅 ADMIN 可更新用户
        checkPermission(operatorId, "ADMIN");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        // 检查角色是否存在
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(role);

        User updatedUser = userRepository.save(user);

        // 记录操作日志
        operationLogService.log(operatorId != null ? operatorId : 1L, "UPDATE_USER", userId);

        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getRole().getRoleName().toString(),
                updatedUser.getCreatedAt() != null ? updatedUser.getCreatedAt().toString() : ""
        );
    }

    /**
     * 删除用户
     */
    public void deleteUser(Long operatorId, Long userId) {
        // 权限校验：仅 ADMIN 可删除用户
        checkPermission(operatorId, "ADMIN");

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        
        // 防止删除 admin 用户
        if ("ADMIN".equals(user.getRole().getRoleName().toString())) {
            throw new BusinessException(400, "不能删除管理员用户");
        }

        userRepository.delete(user);

        // 记录操作日志
        operationLogService.log(operatorId != null ? operatorId : 1L, "DELETE_USER", userId);
    }

    /**
     * 获取所有角色
     */
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleResponse(
                        role.getId(),
                        role.getRoleName().toString()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 获取所有权限
     */
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permission -> new PermissionResponse(
                        permission.getId(),
                        permission.getPermissionName()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 创建权限
     */
    public PermissionResponse createPermission(Long operatorId, PermissionCreateRequest request) {
        // 权限校验：仅 ADMIN 可创建权限
        checkPermission(operatorId, "ADMIN");

        // 检查权限名称是否已存在
        if (permissionRepository.existsByPermissionName(request.getPermissionName())) {
            throw new BusinessException(400, "权限名称已存在");
        }

        // 检查角色是否存在
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));

        Permission permission = new Permission();
        permission.setPermissionName(request.getPermissionName());
        permission.setRole(role);
        
        Permission savedPermission = permissionRepository.save(permission);
        
        // 记录操作日志
        operationLogService.log(operatorId != null ? operatorId : 1L, "CREATE_PERMISSION", savedPermission.getId());
        
        return new PermissionResponse(
                savedPermission.getId(),
                savedPermission.getPermissionName()
        );
    }

    /**
     * 删除权限
     */
    public void deletePermission(Long operatorId, Long permissionId) {
        // 权限校验：仅 ADMIN 可删除权限
        checkPermission(operatorId, "ADMIN");

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new BusinessException(404, "权限不存在"));
        
        permissionRepository.delete(permission);

        // 记录操作日志
        operationLogService.log(operatorId != null ? operatorId : 1L, "DELETE_PERMISSION", permissionId);
    }

    /**
     * 获取系统统计信息
     */
    public java.util.Map<String, Object> getSystemStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalRoles", roleRepository.count());
        stats.put("totalPermissions", permissionRepository.count());
        return stats;
    }
}

