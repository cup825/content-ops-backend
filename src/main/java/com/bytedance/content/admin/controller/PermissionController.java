package com.bytedance.content.admin.controller;

import com.bytedance.content.admin.dto.*;
import com.bytedance.content.admin.service.PermissionManageService;
import com.bytedance.content.common.vo.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
public class PermissionController {

    @Autowired
    private PermissionManageService permissionManageService;

    @GetMapping("/stats")
    public ApiResponse<Map<String, Object>> getSystemStats() {
        return ApiResponse.success(permissionManageService.getSystemStats());
    }

    // ==================== 用户管理 ====================

    @GetMapping("/users")
    public ApiResponse<PaginationResponse<UserResponse>> getUsersByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        PaginationRequest request = new PaginationRequest(page, pageSize, sortBy, sortOrder);
        return ApiResponse.success(permissionManageService.getUsersByPage(request));
    }

    @GetMapping("/users/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long userId) {
        return ApiResponse.success(permissionManageService.getUserById(userId));
    }

    @PostMapping("/users")
    public ApiResponse<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(permissionManageService.createUser(request));
    }

    @PutMapping("/users/{userId}")
    public ApiResponse<UserResponse> updateUser(@PathVariable Long userId,
                                                @Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(permissionManageService.updateUser(userId, request));
    }

    @DeleteMapping("/users/{userId}")
    public ApiResponse<String> deleteUser(@PathVariable Long userId) {
        permissionManageService.deleteUser(userId);
        return ApiResponse.success("用户删除成功", null);
    }

    // ==================== 角色管理 ====================

    @GetMapping("/roles")
    public ApiResponse<List<RoleResponse>> getAllRoles() {
        return ApiResponse.success(permissionManageService.getAllRoles());
    }

    @GetMapping("/roles/{roleId}")
    public ApiResponse<RoleResponse> getRole(@PathVariable Long roleId) {
        return ApiResponse.success(permissionManageService.getRoleById(roleId));
    }

    @PutMapping("/roles/{roleId}")
    public ApiResponse<RoleResponse> updateRole(@PathVariable Long roleId,
                                                @RequestBody RoleCreateRequest request) {
        return ApiResponse.success(permissionManageService.updateRole(roleId, request));
    }

    @DeleteMapping("/roles/{roleId}")
    public ApiResponse<String> deleteRole(@PathVariable Long roleId) {
        permissionManageService.deleteRole(roleId);
        return ApiResponse.success("角色删除成功", null);
    }

    // ==================== 权限管理 ====================

    @GetMapping("/permissions")
    public ApiResponse<PaginationResponse<PermissionResponse>> getPermissionsByPage(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder) {
        PaginationRequest request = new PaginationRequest(page, pageSize, sortBy, sortOrder);
        return ApiResponse.success(permissionManageService.getPermissionsByPage(request));
    }

    @GetMapping("/permissions/all")
    public ApiResponse<List<PermissionResponse>> getAllPermissions() {
        return ApiResponse.success(permissionManageService.getAllPermissions());
    }

    @PostMapping("/permissions")
    public ApiResponse<PermissionResponse> createPermission(@Valid @RequestBody PermissionCreateRequest request) {
        return ApiResponse.success(permissionManageService.createPermission(request));
    }

    @DeleteMapping("/permissions/{permissionId}")
    public ApiResponse<String> deletePermission(@PathVariable Long permissionId) {
        permissionManageService.deletePermission(permissionId);
        return ApiResponse.success("权限删除成功", null);
    }
}
