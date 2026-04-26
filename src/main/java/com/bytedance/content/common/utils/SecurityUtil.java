package com.bytedance.content.common.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 安全工具类
 * 用于从 Spring Security 上下文中获取当前用户信息
 */
@Component
public class SecurityUtil {

    /**
     * 获取当前认证用户的 ID
     * @return 用户 ID，如果未认证则返回 null
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object details = authentication.getDetails();
            if (details instanceof Long) {
                return (Long) details;
            }
        }
        return null;
    }

    /**
     * 获取当前认证用户的用户名
     * @return 用户名，如果未认证则返回 null
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * 获取当前认证用户的角色
     * @return 角色，如果未认证则返回 null
     */
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getAuthorities().isEmpty()) {
            return authentication.getAuthorities().iterator().next().getAuthority();
        }
        return null;
    }

    /**
     * 检查当前用户是否是 ADMIN
     * @return 是否是 ADMIN
     */
    public static boolean isAdmin() {
        String role = getCurrentUserRole();
        return role != null && role.equals("ROLE_ADMIN");
    }

    /**
     * 检查当前用户是否已认证
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}

