package com.bytedance.content.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录响应，包含 JWT token
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
    /**
     * JWT 访问 token
     */
    private String accessToken;
    
    /**
     * token 类型（通常为 Bearer）
     */
    private String tokenType;
    
    /**
     * 过期时间（毫秒）
     */
    private long expiresIn;
    
    /**
     * 用户 ID
     */
    private Long userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 角色
     */
    private String role;
}

