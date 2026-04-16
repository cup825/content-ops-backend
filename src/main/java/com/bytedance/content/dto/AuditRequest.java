package com.bytedance.content.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuditRequest {

    @NotNull(message = "内容ID不能为空")
    private Long contentId;

    @NotNull(message = "审核人ID不能为空")
    private Long reviewerId;

    @NotNull(message = "审核操作不能为空")
    private String action;  // APPROVED 或 REJECTED

    private String comment;  // 审核意见
}

