package com.bytedance.content.common.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 临时工具类：生成 BCrypt 密码值
 * 用法：直接运行 main 方法，复制输出的 SQL 到数据库执行
 * 用完可以删除此文件
 */
public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String admin123  = encoder.encode("admin123");
        String op123     = encoder.encode("op@123");
        String re123     = encoder.encode("re@123");

        System.out.println("-- 复制以下 SQL 到数据库执行 --");
        System.out.println("UPDATE user SET password = '" + admin123 + "' WHERE username = 'admin';");
        System.out.println("UPDATE user SET password = '" + op123    + "' WHERE username = 'operator1';");
        System.out.println("UPDATE user SET password = '" + op123    + "' WHERE username = 'operator2';");
        System.out.println("UPDATE user SET password = '" + op123    + "' WHERE username = 'operator3';");
        System.out.println("UPDATE user SET password = '" + re123    + "' WHERE username = 'reviewer1';");
        System.out.println("UPDATE user SET password = '" + re123    + "' WHERE username = 'reviewer2';");
    }
}

