package com.bytedance.content.audit.controller;

import com.bytedance.content.audit.dto.AuditRequest;
import com.bytedance.content.audit.dto.AuditResponse;
import com.bytedance.content.audit.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    /**
     * 审核内容（审核人从 JWT 解析，无需传 reviewerId）
     * POST /api/v1/audit
     * Body: { "contentId": 1, "action": "APPROVED", "comment": "..." }
     */
    @PostMapping
    public AuditResponse auditContent(@RequestBody AuditRequest request) {
        return auditService.auditContent(request);
    }
}
