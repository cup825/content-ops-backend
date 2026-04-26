package com.bytedance.content.admin.service;

import com.bytedance.content.admin.dto.LoginRequest;
import com.bytedance.content.admin.dto.LoginResponse;
import com.bytedance.content.admin.entity.User;
import com.bytedance.content.admin.repository.UserRepository;
import com.bytedance.content.common.exception.BusinessException;
import com.bytedance.content.common.utils.JwtTokenProvider;
import com.bytedance.content.common.utils.PasswordEncoderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 认证服务
 * 负责用户登录和 JWT token 生成
 */
@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${app.jwtExpirationMs}")
    private long jwtExpirationMs;

    /**
     * 用户登录
     * @param loginRequest 登录请求（包含用户名和密码）
     * @return 登录响应（包含 JWT token）
     * @throws BusinessException 用户不存在或密码错误
     */
    public LoginResponse login(LoginRequest loginRequest) {
        // 查询用户
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BusinessException(401, "用户名或密码错误"));

        // 验证密码
        if (!passwordEncoderUtil.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 生成 JWT token
        String token = jwtTokenProvider.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole().getRoleName().toString()
        );

        // 构建响应
        return new LoginResponse(
                token,
                "Bearer",
                jwtExpirationMs,
                user.getId(),
                user.getUsername(),
                user.getRole().getRoleName().toString()
        );
    }
}

