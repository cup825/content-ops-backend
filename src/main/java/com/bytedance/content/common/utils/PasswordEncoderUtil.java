package com.bytedance.content.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 密码加密工具类
 * 使用 BCrypt 算法对密码进行哈希处理
 */
@Component
public class PasswordEncoderUtil {
    
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    /**
     * 对原始密码进行加密
     * @param rawPassword 原始明文密码
     * @return 加密后的密码哈希值
     */
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    /**
     * 验证原始密码是否与加密后的密码匹配
     * @param rawPassword 原始明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}

