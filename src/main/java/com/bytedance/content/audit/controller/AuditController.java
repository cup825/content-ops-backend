package com.bytedance.content.audit.controller;

import com.bytedance.content.audit.dto.AuditRequest;
import com.bytedance.content.audit.dto.AuditResponse;
import com.bytedance.content.audit.service.AuditService;
import com.bytedance.content.common.vo.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    /**
     * 审核内容（审核人从 JWT 解析）
     * POST /api/v1/audit
     * Body: { "contentId": 1, "action": "APPROVED", "comment": "..." }
     */
    @PostMapping
    public ApiResponse<AuditResponse> auditContent(@Valid @RequestBody AuditRequest request) {
        return ApiResponse.success(auditService.auditContent(request));
    }
}
