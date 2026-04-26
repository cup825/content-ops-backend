package com.bytedance.content.admin.controller;

import com.bytedance.content.admin.dto.LoginRequest;
import com.bytedance.content.admin.dto.LoginResponse;
import com.bytedance.content.admin.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供登录接口
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * 用户登录
     * 
     * 请求示例：
     * POST /api/auth/login
     * {
     *     "username": "admin",
     *     "password": "123456"
     * }
     * 
     * 响应示例：
     * {
     *     "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
     *     "tokenType": "Bearer",
     *     "expiresIn": 86400000,
     *     "userId": 1,
     *     "username": "admin",
     *     "role": "ADMIN"
     * }
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return authenticationService.login(loginRequest);
    }
}

