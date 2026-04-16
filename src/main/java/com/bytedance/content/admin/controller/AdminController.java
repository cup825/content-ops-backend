package com.bytedance.content.admin.controller;

import com.bytedance.content.admin.dto.*;
import com.bytedance.content.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    /**
     * 获取系统统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getSystemStats() {
        return adminService.getSystemStats();
    }

    // ==================== 用户管理 ====================

    /**
     * 获取所有用户
     */
    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return adminService.getAllUsers();
    }

    /**
     * 获取单个用户
     */
    @GetMapping("/users/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        return adminService.getUserById(userId);
    }

    /**
     * 创建用户
     */
    @PostMapping("/users")
    public UserResponse createUser(@RequestBody UserCreateRequest request) {
        return adminService.createUser(request);
    }

    /**
     * 更新用户
     */
    @PutMapping("/users/{userId}")
    public UserResponse updateUser(@PathVariable Long userId, @RequestBody UserCreateRequest request) {
        return adminService.updateUser(userId, request);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/users/{userId}")
    public Map<String, String> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return Map.of("message", "用户删除成功");
    }

    // ==================== 角色管理 ====================

    /**
     * 获取所有角色
     */
    @GetMapping("/roles")
    public List<RoleResponse> getAllRoles() {
        return adminService.getAllRoles();
    }

    // ==================== 权限管理 ====================

    /**
     * 获取所有权限
     */
    @GetMapping("/permissions")
    public List<PermissionResponse> getAllPermissions() {
        return adminService.getAllPermissions();
    }

    /**
     * 创建权限
     */
    @PostMapping("/permissions")
    public PermissionResponse createPermission(@RequestParam String permissionName) {
        return adminService.createPermission(permissionName);
    }

    /**
     * 删除权限
     */
    @DeleteMapping("/permissions/{permissionId}")
    public Map<String, String> deletePermission(@PathVariable Long permissionId) {
        adminService.deletePermission(permissionId);
        return Map.of("message", "权限删除成功");
    }
}

