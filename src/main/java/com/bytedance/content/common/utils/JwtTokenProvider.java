package com.bytedance.content.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT Token 生成和验证工具类
 * 使用 JJWT 库管理 JWT token 的生成、验证和解析
 */
@Component
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationMs}")
    private long jwtExpirationMs;

    /**
     * 生成 JWT token
     * @param userId 用户 ID
     * @param username 用户名
     * @param role 角色
     * @return JWT token 字符串
     */
    public String generateToken(Long userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);
        return createToken(claims, username);
    }

    /**
     * 从 token 中获取用户 ID
     * @param token JWT token
     * @return 用户 ID
     */
    public Long getUserIdFromToken(String token) {
        return ((Number) getClaimsFromToken(token).get("userId")).longValue();
    }

    /**
     * 从 token 中获取用户名
     * @param token JWT token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 从 token 中获取角色
     * @param token JWT token
     * @return 角色
     */
    public String getRoleFromToken(String token) {
        return (String) getClaimsFromToken(token).get("role");
    }

    /**
     * 验证 token 是否有效
     * @param token JWT token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new RuntimeException("无效的 JWT token: " + ex.getMessage());
        } catch (ExpiredJwtException ex) {
            throw new RuntimeException("JWT token 已过期");
        } catch (SignatureException ex) {
            throw new RuntimeException("JWT 签名验证失败");
        }
    }

    /**
     * 从 token 中获取所有声明
     * @param token JWT token
     * @return Claims 对象
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 创建 JWT token
     * @param claims 声明
     * @param subject 主题（通常是用户名）
     * @return JWT token 字符串
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), Jwts.SIG.HS256)
                .compact();
    }
}

