package com.bytedance.content.entity;

import com.bytedance.content.common.enums.ContentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "content")
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
    @Column(columnDefinition = "TEXT") // 对应文档里的 text 类型
    private String content;

    @NotNull(message = "状态不能为空")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentStatus status = ContentStatus.DRAFT;//默认状态为DRAFT

    @NotNull(message = "创建人不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @CreationTimestamp // 对应文档 created_at，保存时自动生成时间
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // 对应文档 updated_at，修改时自动更新时间
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}