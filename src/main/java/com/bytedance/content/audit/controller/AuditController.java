package com.bytedance.content.audit.controller;

import com.bytedance.content.audit.dto.AuditRequest;
import com.bytedance.content.audit.dto.AuditResponse;
import com.bytedance.content.audit.service.AuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/audit")
public class AuditController {

    @Autowired
    private AuditService auditService;

    /**
     * 审核内容
     */
    @PostMapping("/audit")
    public AuditResponse auditContent(@RequestBody AuditRequest request) {
        return auditService.auditContent(request);
    }
}

