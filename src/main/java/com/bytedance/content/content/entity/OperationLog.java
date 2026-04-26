package com.bytedance.content.content.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "operation_log", indexes = {
        // 按操作人查询日志时用
        @Index(name = "idx_log_user_id", columnList = "user_id"),
        // 按操作类型查询日志时用
        @Index(name = "idx_log_action", columnList = "action"),
        // 按时间查询日志时用
        @Index(name = "idx_log_created_at", columnList = "created_at")
})
@Data
public class OperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "操作类型不能为空")
    @Column(name = "action", nullable = false, length = 50)
    private String action;

    @NotNull(message = "目标ID不能为空")
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

