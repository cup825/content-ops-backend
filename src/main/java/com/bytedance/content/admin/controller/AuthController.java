package com.bytedance.content.admin.controller;

import com.bytedance.content.admin.dto.LoginRequest;
import com.bytedance.content.admin.dto.LoginResponse;
import com.bytedance.content.admin.service.AuthenticationService;
import com.bytedance.content.common.vo.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供登录接口
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * 用户登录
     * POST /api/v1/auth/login
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ApiResponse.success(authenticationService.login(loginRequest));
    }
}
