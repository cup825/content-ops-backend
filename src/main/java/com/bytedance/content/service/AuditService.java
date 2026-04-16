package com.bytedance.content.service;

import com.bytedance.content.dto.AuditRequest;
import com.bytedance.content.dto.AuditResponse;

public interface AuditService {

    /**
     * 审核内容（通过或驳回）
     * 
     * @param request 审核请求（包含内容ID、审核人ID、审核操作、意见）
     * @return 审核结果
     */
    AuditResponse auditContent(AuditRequest request);
}

