package com.bytedance.content.entity;

import com.bytedance.content.common.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "role")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "角色名称不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", nullable = false)
    private UserRole roleName;
}

