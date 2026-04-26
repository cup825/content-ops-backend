package com.bytedance.content.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
}

