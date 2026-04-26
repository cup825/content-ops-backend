package com.bytedance.content.admin.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bytedance.content.common.utils.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedance.content.admin.dto.*;
import com.bytedance.content.admin.entity.Permission;
import com.bytedance.content.admin.entity.Role;
import com.bytedance.content.admin.entity.User;
import com.bytedance.content.admin.repository.PermissionRepository;
import com.bytedance.content.admin.repository.RoleRepository;
import com.bytedance.content.admin.repository.UserRepository;
import com.bytedance.content.common.exception.BusinessException;
import com.bytedance.content.common.utils.PasswordEncoderUtil;
import com.bytedance.content.content.service.OperationLogService;

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
    private PasswordEncoderUtil passwordEncoderUtil;

    @Autowired
    private PermissionCheckService permissionCheckService;

    private void checkPermission(Long operatorId) {
        // 首先尝试从安全上下文获取认证用户信息
        Long authenticatedUserId = SecurityUtil.getCurrentUserId();
        if (authenticatedUserId != null) {
            operatorId = authenticatedUserId;
        }
        
        if (operatorId == null) {
            throw new BusinessException(403, "操作权限不足");
        }
        if (!permissionCheckService.canManagePermission(operatorId)) {
            throw new BusinessException(403, "仅管理员可执行此操作");
        }
    }

    // 用户管理
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

    public PaginationResponse<UserResponse> getUsersByPage(PaginationRequest request) {
        Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortOrder()) 
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";
        
        org.springframework.data.domain.Pageable pageable = PageRequest.of(
                request.getPageNumber(),
                request.getPageSize(),
                Sort.by(direction, sortBy)
        );
        
        Page<User> page = userRepository.findAll(pageable);
        
        List<UserResponse> content = page.getContent().stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getRole().getRoleName().toString(),
                        user.getCreatedAt() != null ? user.getCreatedAt().toString() : ""
                ))
                .collect(Collectors.toList());
        
        return new PaginationResponse<>(
                content,
                page.getTotalElements(),
                request.getPage() != null ? request.getPage() : 1,
                request.getPageSize(),
                page.getTotalPages()
        );
    }

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

    public UserResponse createUser(Long operatorId, UserCreateRequest request) {
        checkPermission(operatorId);
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException(400, "用户名已存在");
        }
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));
        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoderUtil.encode(request.getPassword()));
        user.setRole(role);
        User savedUser = userRepository.save(user);
        
        operationLogService.log(operatorId != null ? operatorId : 1L, "CREATE_USER", savedUser.getId());
        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getRole().getRoleName().toString(),
                savedUser.getCreatedAt() != null ? savedUser.getCreatedAt().toString() : ""
        );
    }

    public UserResponse updateUser(Long operatorId, Long userId, UserCreateRequest request) {
        checkPermission(operatorId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));
        
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoderUtil.encode(request.getPassword()));
        user.setRole(role);
        User updatedUser = userRepository.save(user);
        
        operationLogService.log(operatorId != null ? operatorId : 1L, "UPDATE_USER", userId);
        return new UserResponse(
                updatedUser.getId(),
                updatedUser.getUsername(),
                updatedUser.getRole().getRoleName().toString(),
                updatedUser.getCreatedAt() != null ? updatedUser.getCreatedAt().toString() : ""
        );
    }

    public void deleteUser(Long operatorId, Long userId) {
        checkPermission(operatorId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        userRepository.delete(user);
        operationLogService.log(operatorId != null ? operatorId : 1L, "DELETE_USER", userId);
    }

    // 角色管理
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleResponse(
                        role.getId(),
                        role.getRoleName().toString()
                ))
                .collect(Collectors.toList());
    }

    public RoleResponse getRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));
        return new RoleResponse(
                role.getId(),
                role.getRoleName().toString()
        );
    }

    public RoleResponse updateRole(Long operatorId, Long roleId, RoleCreateRequest request) {
        checkPermission(operatorId);
        throw new BusinessException(400, "角色为系统预定义，不支持修改");
    }

    public void deleteRole(Long operatorId, Long roleId) {
        checkPermission(operatorId);
        throw new BusinessException(400, "角色为系统预定义，不支持删除");
    }

    // 权限管理
    public List<PermissionResponse> getAllPermissions() {
        return permissionRepository.findAll().stream()
                .map(permission -> new PermissionResponse(
                        permission.getId(),
                        permission.getPermissionName()
                ))
                .collect(Collectors.toList());
    }

    public PaginationResponse<PermissionResponse> getPermissionsByPage(PaginationRequest request) {
        Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortOrder())
                ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "id";

        org.springframework.data.domain.Pageable pageable = PageRequest.of(
                request.getPageNumber(),
                request.getPageSize(),
                Sort.by(direction, sortBy)
        );

        Page<Permission> page = permissionRepository.findAll(pageable);

        List<PermissionResponse> content = page.getContent().stream()
                .map(permission -> new PermissionResponse(
                        permission.getId(),
                        permission.getPermissionName()
                ))
                .collect(Collectors.toList());

        return new PaginationResponse<>(
                content,
                page.getTotalElements(),
                request.getPage() != null ? request.getPage() : 1,
                request.getPageSize(),
                page.getTotalPages()
        );
    }

    public PermissionResponse createPermission(Long operatorId, PermissionCreateRequest request) {
        checkPermission(operatorId);
        if (permissionRepository.findByPermissionName(request.getPermissionName()).isPresent()) {
            throw new BusinessException(400, "权限已存在");
        }
        
        Permission permission = new Permission();
        permission.setPermissionName(request.getPermissionName());
        Permission savedPermission = permissionRepository.save(permission);
        
        operationLogService.log(operatorId != null ? operatorId : 1L, "CREATE_PERMISSION", savedPermission.getId());
        return new PermissionResponse(
                savedPermission.getId(),
                savedPermission.getPermissionName()
        );
    }

    public void deletePermission(Long operatorId, Long permissionId) {
        checkPermission(operatorId);
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new BusinessException(404, "权限不存在"));
        permissionRepository.delete(permission);
        operationLogService.log(operatorId != null ? operatorId : 1L, "DELETE_PERMISSION", permissionId);
    }

    public Map<String, Object> getSystemStats() {
        long userCount = userRepository.count();
        long roleCount = roleRepository.count();
        long permissionCount = permissionRepository.count();
        return Map.of(
                "userCount", userCount,
                "roleCount", roleCount,
                "permissionCount", permissionCount
        );
    }
}

