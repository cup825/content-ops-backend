package com.bytedance.content.admin.controller;

import com.bytedance.content.admin.dto.*;
import com.bytedance.content.admin.service.PermissionManageService;
import com.bytedance.content.admin.service.PermissionCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class PermissionController {

    @Autowired
    private PermissionManageService permissionManageService;

    @Autowired
    private PermissionCheckService permissionCheckService;

    /**
     * 获取系统统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getSystemStats() {
        return permissionManageService.getSystemStats();
    }

    // ==================== 用户管理 ====================

    /**
     * 获取所有用户
     */
    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return permissionManageService.getAllUsers();
    }


    /**
     * 获取单个用户
     */
    @GetMapping("/users/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        return permissionManageService.getUserById(userId);
    }

    /**
     * 创建用户（需要 ADMIN 权限）
     */
    @PostMapping("/users")
    public UserResponse createUser(@RequestHeader(value = "X-User-Id", required = false) Long operatorId,
                                   @RequestBody UserCreateRequest request) {
        return permissionManageService.createUser(operatorId, request);
    }

    /**
     * 更新用户（需要 ADMIN 权限）
     */
    @PutMapping("/users/{userId}")
    public UserResponse updateUser(@PathVariable Long userId,
                                   @RequestHeader(value = "X-User-Id", required = false) Long operatorId,
                                   @RequestBody UserCreateRequest request) {
        return permissionManageService.updateUser(operatorId, userId, request);
    }

    /**
     * 删除用户（需要 ADMIN 权限）
     */
    @DeleteMapping("/users/{userId}")
    public Map<String, String> deleteUser(@PathVariable Long userId,
                                          @RequestHeader(value = "X-User-Id", required = false) Long operatorId) {
        permissionManageService.deleteUser(operatorId, userId);
        return Map.of("message", "用户删除成功");
    }

    // ==================== 角色管理 ====================

    /**
     * 获取所有角色
     */
    @GetMapping("/roles")
    public List<RoleResponse> getAllRoles() {
        return permissionManageService.getAllRoles();
    }


    // ==================== 权限管理 ====================

    /**
     * 获取所有权限
     */
    @GetMapping("/permissions")
    public List<PermissionResponse> getAllPermissions() {
        return permissionManageService.getAllPermissions();
    }


    /**
     * 创建权限（需要 ADMIN 权限）
     * 请求体: {"permissionName": "PERMISSION_NAME", "roleId": 1}
     */
    @PostMapping("/permissions")
    public PermissionResponse createPermission(@RequestHeader(value = "X-User-Id", required = false) Long operatorId,
                                               @RequestBody PermissionCreateRequest request) {
        return permissionManageService.createPermission(operatorId, request);
    }

    /**
     * 删除权限（需要 ADMIN 权限）
     */
    @DeleteMapping("/permissions/{permissionId}")
    public Map<String, String> deletePermission(@PathVariable Long permissionId,
                                                @RequestHeader(value = "X-User-Id", required = false) Long operatorId) {
        permissionManageService.deletePermission(operatorId, permissionId);
        return Map.of("message", "权限删除成功");
    }
}

