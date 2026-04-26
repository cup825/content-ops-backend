package com.bytedance.content.common.config;

import com.bytedance.content.common.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 * 配置 JWT 认证、授权规则和过滤器链
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF
                .csrf(csrf -> csrf.disable())
                // 使用无状态会话（不使用 session）
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置授权规则
                .authorizeHttpRequests(authz -> authz
                        // 登录端点无需认证
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        // 健康检查端点
                        .requestMatchers("/actuator/**").permitAll()
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                // 在 UsernamePasswordAuthenticationFilter 前添加 JWT 过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                // 配置异常处理
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(401);
                            response.getWriter().write("{\"error\": \"未认证，请先登录\", \"code\": 401}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(403);
                            response.getWriter().write("{\"error\": \"无权限访问此资源\", \"code\": 403}");
                        })
                );

        return http.build();
    }
}

