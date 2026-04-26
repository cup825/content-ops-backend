package com.bytedance.content.admin.controller;

import com.bytedance.content.admin.dto.*;
import com.bytedance.content.admin.service.PermissionManageService;
import com.bytedance.content.admin.service.PermissionCheckService;
import com.bytedance.content.common.utils.SecurityUtil;
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
     * 分页获取用户列表（支持排序）
     * 
     * 示例：GET /api/admin/users/page?page=1&pageSize=10&sortBy=id&sortOrder=desc
     */
    @GetMapping("/users/page")
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
     * 
     * 认证方式：
     * 1. 优先使用 JWT token（Authorization: Bearer <token>）
     * 2. 向后兼容 X-User-Id header
     */
    @DeleteMapping("/users/{userId}")
    public Map<String, String> deleteUser(@PathVariable Long userId,
                                          @RequestHeader(value = "X-User-Id", required = false) Long operatorId) {
        Long authenticatedUserId = SecurityUtil.getCurrentUserId();
        if (authenticatedUserId != null) {
            operatorId = authenticatedUserId;
        }
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

    /**
     * 获取单个角色
     */
    @GetMapping("/roles/{roleId}")
    public RoleResponse getRole(@PathVariable Long roleId) {
        return permissionManageService.getRoleById(roleId);
    }

    /**
     * 更新角色（需要 ADMIN 权限）
     * 注意：系统预定义角色，暂不支持修改
     */
    @PutMapping("/roles/{roleId}")
    public RoleResponse updateRole(@PathVariable Long roleId,
                                  @RequestHeader(value = "X-User-Id", required = false) Long operatorId,
                                  @RequestBody RoleCreateRequest request) {
        Long authenticatedUserId = SecurityUtil.getCurrentUserId();
        if (authenticatedUserId != null) {
            operatorId = authenticatedUserId;
        }
        return permissionManageService.updateRole(operatorId, roleId, request);
    }

    /**
     * 删除角色（需要 ADMIN 权限）
     * 注意：系统预定义角色，暂不支持删除
     */
    @DeleteMapping("/roles/{roleId}")
    public Map<String, String> deleteRole(@PathVariable Long roleId,
                                         @RequestHeader(value = "X-User-Id", required = false) Long operatorId) {
        Long authenticatedUserId = SecurityUtil.getCurrentUserId();
        if (authenticatedUserId != null) {
            operatorId = authenticatedUserId;
        }
        permissionManageService.deleteRole(operatorId, roleId);
        return Map.of("message", "角色删除成功");
    }


    // ==================== 权限管理 ====================

    /**
     * 分页获取权限列表（支持排序）
     * 
     * 示例：GET /api/admin/permissions/page?page=1&pageSize=10&sortBy=id&sortOrder=asc
     */
    @GetMapping("/permissions/page")
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
        Long authenticatedUserId = SecurityUtil.getCurrentUserId();
        if (authenticatedUserId != null) {
            operatorId = authenticatedUserId;
        }
        return permissionManageService.createPermission(operatorId, request);
    }

    /**
     * 删除权限（需要 ADMIN 权限）
     */
    @DeleteMapping("/permissions/{permissionId}")
    public Map<String, String> deletePermission(@PathVariable Long permissionId,
                                                @RequestHeader(value = "X-User-Id", required = false) Long operatorId) {
        Long authenticatedUserId = SecurityUtil.getCurrentUserId();
        if (authenticatedUserId != null) {
            operatorId = authenticatedUserId;
        }
        permissionManageService.deletePermission(operatorId, permissionId);
        return Map.of("message", "权限删除成功");
    }
}

