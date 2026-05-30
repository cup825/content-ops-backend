package com.bytedance.content.admin.controller;

import com.bytedance.content.admin.dto.*;
import com.bytedance.content.admin.service.PermissionManageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class PermissionController {

    @Autowired
    private PermissionManageService permissionManageService;

    /**
     * 获取系统统计信息
     */
    @GetMapping("/stats")
    public Map<String, Object> getSystemStats() {
        return permissionManageService.getSystemStats();
    }

    // ==================== 用户管理 ====================

    /**
     * 分页获取用户列表
     * GET /api/v1/admin/users?page=1&pageSize=10&sortBy=id&sortOrder=desc
     */
    @GetMapping("/users")
    public PaginationResponse<UserResponse> getUsersByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        PaginationRequest request = new PaginationRequest(page, pageSize, sortBy, sortOrder);
        return permissionManageService.getUsersByPage(request);
    }

    /**
     * 获取单个用户
     */
    @GetMapping("/users/{userId}")
    public UserResponse getUser(@PathVariable Long userId) {
        return permissionManageService.getUserById(userId);
    }

    /**
     * 创建用户（需要 ADMIN 权限，操作人从 JWT 解析）
     */
    @PostMapping("/users")
    public UserResponse createUser(@RequestBody UserCreateRequest request) {
        return permissionManageService.createUser(request);
    }

    /**
     * 更新用户（需要 ADMIN 权限，操作人从 JWT 解析）
     */
    @PutMapping("/users/{userId}")
    public UserResponse updateUser(@PathVariable Long userId,
                                   @RequestBody UserCreateRequest request) {
        return permissionManageService.updateUser(userId, request);
    }

    /**
     * 删除用户（需要 ADMIN 权限，操作人从 JWT 解析）
     */
    @DeleteMapping("/users/{userId}")
    public Map<String, String> deleteUser(@PathVariable Long userId) {
        permissionManageService.deleteUser(userId);
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

    /**
     * 获取单个角色
     */
    @GetMapping("/roles/{roleId}")
    public RoleResponse getRole(@PathVariable Long roleId) {
        return permissionManageService.getRoleById(roleId);
    }

    /**
     * 更新角色（系统预定义，暂不支持）
     */
    @PutMapping("/roles/{roleId}")
    public RoleResponse updateRole(@PathVariable Long roleId,
                                   @RequestBody RoleCreateRequest request) {
        return permissionManageService.updateRole(roleId, request);
    }

    /**
     * 删除角色（系统预定义，暂不支持）
     */
    @DeleteMapping("/roles/{roleId}")
    public Map<String, String> deleteRole(@PathVariable Long roleId) {
        permissionManageService.deleteRole(roleId);
        return Map.of("message", "角色删除成功");
    }

    // ==================== 权限管理 ====================

    /**
     * 分页获取权限列表
     * GET /api/v1/admin/permissions?page=1&pageSize=10
     */
    @GetMapping("/permissions")
    public PaginationResponse<PermissionResponse> getPermissionsByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        PaginationRequest request = new PaginationRequest(page, pageSize, sortBy, sortOrder);
        return permissionManageService.getPermissionsByPage(request);
    }

    /**
     * 获取所有权限（全量）
     */
    @GetMapping("/permissions/all")
    public List<PermissionResponse> getAllPermissions() {
        return permissionManageService.getAllPermissions();
    }

    /**
     * 创建权限（需要 ADMIN 权限，操作人从 JWT 解析）
     */
    @PostMapping("/permissions")
    public PermissionResponse createPermission(@RequestBody PermissionCreateRequest request) {
        return permissionManageService.createPermission(request);
    }

    /**
     * 删除权限（需要 ADMIN 权限，操作人从 JWT 解析）
     */
    @DeleteMapping("/permissions/{permissionId}")
    public Map<String, String> deletePermission(@PathVariable Long permissionId) {
        permissionManageService.deletePermission(permissionId);
        return Map.of("message", "权限删除成功");
    }
}

