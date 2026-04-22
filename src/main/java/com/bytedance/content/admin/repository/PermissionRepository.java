package com.bytedance.content.admin.repository;

import com.bytedance.content.admin.entity.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    /**
     * 检查权限名称是否已存在
     */
    boolean existsByPermissionName(String permissionName);

    /**
     * 根据权限名称查找权限
     */
    Optional<Permission> findByPermissionName(String permissionName);

    /**
     * 按权限名称模糊查询（支持分页）
     */
    Page<Permission> findByPermissionNameContaining(String keyword, Pageable pageable);
}

