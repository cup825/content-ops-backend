package com.bytedance.content.admin.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.bytedance.content.common.utils.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * <p>
 * 权限校验说明：
 * - 所有修改操作（增删改）都需要 ADMIN 权限
 * - 越权操作会抛出 403 异常
 */
@Service
@Transactional
public class PermissionManageService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionManageService.class);

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

    //检查是否具有管理权限，没有则抛出异常
    private void checkPermission() {
        Long operatorId = SecurityUtil.getCurrentUserId();
        if (operatorId == null) {
            throw new BusinessException(403, "操作权限不足");
        }
        if (!permissionCheckService.canManagePermission(operatorId)) {
            throw new BusinessException(403, "仅管理员可执行此操作");
        }
    }

    //不需要传入用户ID，从JWT安全上下文获取当前用户ID，如果未登录则抛出异常
    private Long requireCurrentUserId() {
        Long userId = SecurityUtil.getCurrentUserId();
        if (userId == null) throw new BusinessException(401, "请先登录");
        return userId;
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

    //分页查询用户
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

    // 获取单个用户由id
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

    //创建新用户，用户名存在或角色不存在时抛出异常
    public UserResponse createUser(UserCreateRequest request) {
        checkPermission();
        Long operatorId = requireCurrentUserId();
        logger.info("创建用户：operatorId={}, username={}", operatorId, request.getUsername());
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BusinessException(400, "用户名已存在");
        }
        Role role = roleRepository.findById(request.getRoleId()) // 和上面，一个是查到了报错，一个是查不到报错
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoderUtil.encode(request.getPassword()));//保存加密后密码
        user.setRole(role);
        User savedUser = userRepository.save(user);

        operationLogService.log(operatorId, "CREATE_USER", savedUser.getId());
        logger.info("用户创建成功：userId={}", savedUser.getId());
        return new UserResponse(savedUser.getId(), savedUser.getUsername(),
                savedUser.getRole().getRoleName().toString(),
                savedUser.getCreatedAt() != null ? savedUser.getCreatedAt().toString() : ""
        );
    }

    public UserResponse updateUser(Long userId, UserCreateRequest request) {
        checkPermission();
        Long operatorId = requireCurrentUserId();
        logger.info("更新用户：operatorId={}, targetUserId={}", operatorId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new BusinessException(404, "角色不存在"));

        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoderUtil.encode(request.getPassword()));
        user.setRole(role);
        User updatedUser = userRepository.save(user);

        operationLogService.log(operatorId, "UPDATE_USER", userId);
        return new UserResponse(updatedUser.getId(), updatedUser.getUsername(),
                updatedUser.getRole().getRoleName().toString(),
                updatedUser.getCreatedAt() != null ? updatedUser.getCreatedAt().toString() : "");
    }

    public void deleteUser(Long userId) {
        checkPermission();
        Long operatorId = requireCurrentUserId();
        logger.info("删除用户：operatorId={}, targetUserId={}", operatorId, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
        userRepository.delete(user);
        operationLogService.log(operatorId, "DELETE_USER", userId);
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

    // 不支持修改/删除角色
    public RoleResponse updateRole(Long roleId, RoleCreateRequest request) {
        checkPermission();
        throw new BusinessException(400, "角色为系统预定义，不支持修改");
    }

    public void deleteRole(Long roleId) {
        checkPermission();
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

    // 分页查询权限
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

    // 创建权限
    public PermissionResponse createPermission(PermissionCreateRequest request) {
        checkPermission();
        Long operatorId = requireCurrentUserId();
        logger.info("创建权限：operatorId={}, permissionName={}", operatorId, request.getPermissionName());
        if (permissionRepository.findByPermissionName(request.getPermissionName()).isPresent()) {
            throw new BusinessException(400, "权限已存在");
        }

        Permission permission = new Permission();
        permission.setPermissionName(request.getPermissionName());
        Permission savedPermission = permissionRepository.save(permission);

        operationLogService.log(operatorId, "CREATE_PERMISSION", savedPermission.getId());
        return new PermissionResponse(savedPermission.getId(), savedPermission.getPermissionName());
    }

    public void deletePermission(Long permissionId) {
        checkPermission();
        Long operatorId = requireCurrentUserId();
        logger.info("删除权限：operatorId={}, permissionId={}", operatorId, permissionId);
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new BusinessException(404, "权限不存在"));
        permissionRepository.delete(permission);
        operationLogService.log(operatorId, "DELETE_PERMISSION", permissionId);
    }

    //获取系统统计数据，包括用户总数、角色总数、权限总数
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

