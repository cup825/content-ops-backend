package com.bytedance.content.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditResponse {

    private Long auditId;

    private Long contentId;

    private String status;

    private String comment;
}

