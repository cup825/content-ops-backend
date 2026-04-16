package com.bytedance.content.admin.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Entity
@Table(name = "permission")
@Data
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "权限名称不能为空")
    @Column(name = "permission_name", nullable = false, length = 100, unique = true)
    private String permissionName;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
}

