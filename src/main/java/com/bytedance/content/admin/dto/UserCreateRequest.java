package com.bytedance.content.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {
    
    private String username;
    
    private String password;
    
    private Long roleId;
}

