package com.bytedance.content.content.entity;

import com.bytedance.content.common.enums.ContentStatus;
import com.bytedance.content.admin.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "content", indexes = {
        // status 字段：按状态查询/统计时用（如 countByStatus）
        @Index(name = "idx_content_status", columnList = "status"),
        // creator_id 字段：按创建者筛选内容时用
        @Index(name = "idx_content_creator_id", columnList = "creator_id"),
        // created_at 字段：按时间范围统计发布量时用
        @Index(name = "idx_content_created_at", columnList = "created_at"),
        // 组合索引：同时按状态+时间查询时，比两个单独索引更快
        @Index(name = "idx_content_status_created_at", columnList = "status, created_at")
})
@Data
@ToString(exclude = {"content"})
public class Content {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "标题不能为空")
    @Column(length = 255)
    private String title;

    @NotBlank(message = "内容不能为空")
    @Column(columnDefinition = "TEXT")
    private String content;

    @NotNull(message = "状态不能为空")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus status = ContentStatus.DRAFT;

    @NotNull(message = "创建人不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}

