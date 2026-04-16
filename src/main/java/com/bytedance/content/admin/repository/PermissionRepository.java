package com.bytedance.content.admin.repository;

import com.bytedance.content.admin.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    
    /**
     * 检查权限名称是否已存在
     */
    boolean existsByPermissionName(String permissionName);
}

