package com.bytedance.content.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionCreateRequest {

    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    @NotNull(message = "角色ID不能为空")
    private Long roleId;
}

