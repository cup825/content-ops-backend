package com.bytedance.content.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionCreateRequest {
    
    private String permissionName;
    
    private Long roleId;
}

