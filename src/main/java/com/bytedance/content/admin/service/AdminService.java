package com.bytedance.content.admin.service;

import com.bytedance.content.admin.dto.*;
import com.bytedance.content.common.exception.BusinessException;
import com.bytedance.content.permission.entity.Permission;
import com.bytedance.content.permission.entity.Role;
import com.bytedance.content.permission.entity.User;
import com.bytedance.content.permission.repository.PermissionRepository;
import com.bytedance.content.permission.repository.RoleRepository;
import com.bytedance.content.permission.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

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
    public UserResponse createUser(UserCreateRequest request) {
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
    public UserResponse updateUser(Long userId, UserCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        // 检查角色是否存在
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));

        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(role);

        User updatedUser = userRepository.save(user);

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
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        
        // 防止删除 admin 用户
        if ("ADMIN".equals(user.getRole().getRoleName().toString())) {
            throw new BusinessException(400, "不能删除管理员用户");
        }

        userRepository.delete(user);
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
    public PermissionResponse createPermission(String permissionName) {
        Permission permission = new Permission();
        permission.setPermissionName(permissionName);
        
        Permission savedPermission = permissionRepository.save(permission);
        
        return new PermissionResponse(
                savedPermission.getId(),
                savedPermission.getPermissionName()
        );
    }

    /**
     * 删除权限
     */
    public void deletePermission(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new BusinessException(404, "权限不存在"));
        
        permissionRepository.delete(permission);
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

