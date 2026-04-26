package com.bytedance.content.audit.entity;

import com.bytedance.content.common.enums.AuditStatus;
import com.bytedance.content.content.entity.Content;
import com.bytedance.content.admin.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_record", indexes = {
        // 按审核状态统计通过率时用
        @Index(name = "idx_audit_status", columnList = "status"),
        // 按审核员查询审核记录时用
        @Index(name = "idx_audit_reviewer_id", columnList = "reviewer_id"),
        // 按时间范围查询审核记录时用
        @Index(name = "idx_audit_created_at", columnList = "created_at")
})
@Data
public class AuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "内容不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @NotNull(message = "审核人不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @NotNull(message = "审核状态不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AuditStatus status;

    @Column(name = "comment", length = 500)
    private String comment;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

